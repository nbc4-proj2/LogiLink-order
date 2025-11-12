package order_service.order.domain.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import order_service.order.common.exception.AppException;
import order_service.order.common.exception.OrderErrorCode;
import order_service.order.domain.model.dto.request.OrderCreateReq;
import order_service.order.domain.model.dto.request.OrderUpdateReq;
import order_service.order.domain.model.dto.response.OrderRes;
import order_service.order.domain.model.dto.response.ProductRes;
import order_service.order.domain.model.entity.EntityStatus;
import order_service.order.domain.model.entity.Order;
import order_service.order.domain.model.entity.OrderStatus;
import order_service.order.domain.model.repository.OrderRepository;
import order_service.order.domain.service.client.DeliveryClient;
import order_service.order.domain.service.client.ProductClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final DeliveryClient deliveryClient;

    //=====주문 생성(MASTER, HUB, COMPANY)=====//
    @Override
    @Transactional
    public OrderRes createOrder(OrderCreateReq requestDto, Long userId, String userRole, UUID companyId, UUID hubId ) {

        // 권한 검증(COMPANY_MANAGER만 주문 생성 가능)
        if (!"COMPANY_MANAGER".equals(userRole)) {
            throw new AppException(OrderErrorCode.UNAUTHORIZED_ACTION);
        }

        // 상품 정보 조회(product-service 호출)
        ProductRes product = productClient.getProduct(requestDto.getProductId());
        if (product == null || !"ACTIVE".equals(product.getStatus())) {
            throw new AppException(OrderErrorCode.PRODUCT_NOT_FOUND);
        }
        // 회사 검증(company_manager는 본인 회사 상품 주문은 할 수 없음)
        if (product.getCompanyId().equals(companyId)) {
            throw new AppException(OrderErrorCode.SELF_ORDER_NOT_ALLOWED);
        }
        // 재고 확인(참고용 체크)
        if (product.getProductQuantity() < requestDto.getProductQuantity()) {
            // 재고 부족 시 주문 거절 상태로 저장
            Order rejectedOrder = Order.createRejected(
                    userId,
                    companyId,
                    requestDto.getDestinationAddress(),
                    hubId,
                    product.getProductId(),
                    product.getProductName(),
                    product.getProductPrice(),
                    product.getProductQuantity()
            );
            orderRepository.save(rejectedOrder);
            return OrderRes.from(rejectedOrder);
        }

        // 주문 생성(아직 ORDER_PENDING 상태)
        Order order = Order.createOrder(
                userId,
                companyId,
                requestDto.getDestinationAddress(),
                product.getHubId(),
                product.getProductId(),
                product.getProductName(),
                product.getProductPrice(),
                requestDto.getProductQuantity(),
                requestDto.getMemo()
        );
        orderRepository.save(order);

        return OrderRes.from(order);
    }

    // 주문 수락(재고 감소+배송 생성)
    @Override
    @Transactional
    public OrderRes acceptOrder(UUID orderId, Long userId, String userRole, UUID companyId, UUID hubId) {

        Order order = orderRepository.findByOrderIdAndStatus(orderId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new AppException(OrderErrorCode.ORDER_NOT_FOUND));

        // 권한 검증
        if(!"MASTER".equals(userRole) && !"HUB_MANAGER".equals(userRole)){
            throw new AppException(OrderErrorCode.UNAUTHORIZED_ACTION);
        }

        // 주문 상태 확인
        if(!order.getOrderStatus().isPending()){
            throw new AppException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        // 재고 감소
        try{
            // 재고 차감 API 호출
            productClient.decreaseStock(order.getProductId(), order.getProductQuantity());

            // 재고 차감 성공
            order.accept();

            // 배송 생성(delivery-service 호출)
            deliveryClient.createDelivery(
                    DeliveryClient.DeliveryCreateRequest.builder()
                            .originHubId(order.getHubId())
                            .destinationId(order.getCompanyId())
                            .destinationAddress(order.getDestinationAddress())
                            .orderId(order.getOrderId())
                            .build()
            );
            return OrderRes.from(order);

        } catch (FeignException.BadRequest ex) {
            // BadRequest = 재고 부족
            order.reject(); // ORDER_REJECTED
            return OrderRes.from(order);
        }
    }

    //=====주문 수정(MASTER, HUB)=====//
    @Override
    @Transactional
    public OrderRes updateOrder(UUID orderId, OrderUpdateReq requestDto, Long userId, String userRole, UUID companyId, UUID hubId) {

        Order order = orderRepository.findByOrderIdAndStatus(orderId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new AppException(OrderErrorCode.ORDER_NOT_FOUND));

        // 권한 검증
        if(!"MASTER".equals(userRole) && !"HUB_MANAGER".equals(userRole)){
            throw new AppException(OrderErrorCode.UNAUTHORIZED_ACTION);
        }

        // 상태 확인
        if(!order.getOrderStatus().isPending()){
            throw new AppException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        // 상품 재조회
        ProductRes product = productClient.getProduct(requestDto.getProductId());
        if (product == null || !"ACTIVE".equals(product.getStatus())) {
            throw new AppException(OrderErrorCode.PRODUCT_NOT_FOUND);
        }

        // 재고 확인
        if (product.getProductQuantity() < requestDto.getProductQuantity()) {
            throw new AppException(OrderErrorCode.INSUFFICIENT_STOCK);
        }

        // 주문 수정
        order.updateOrder(
                companyId,
                hubId,
                product.getProductId(),
                product.getProductName(),
                requestDto.getDestinationAddress(),
                product.getProductPrice(),
                product.getProductQuantity(),
                requestDto.getMemo()
        );

        return OrderRes.from(order);
    }

    //=====주문 삭제(MASTER, HUB)=====//
    @Override
    @Transactional
    public void deleteOrder(UUID orderId, Long userId, String userRole, UUID companyId, UUID hubId){

        Order order = orderRepository.findByOrderIdAndStatus(orderId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new AppException(OrderErrorCode.ORDER_NOT_FOUND));

        // 권한 설정
        validationDeletePermission(order, userRole, companyId, hubId);

        // 주문 상태 확인
        if("COMPANY_MANAGER".equals(userRole) && order.getOrderStatus() == OrderStatus.ORDER_SUCCEEDED){
            throw new AppException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        order.delete();
    }

    //=====주문 단건 조회(각자 ALL)=====//
    @Override
    @Transactional(readOnly = true)
    public OrderRes getOrder(UUID orderId, Long userId, String userRole, UUID companyId, UUID hubId) {
        Order order = orderRepository.findByOrderIdAndStatus(orderId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new AppException(OrderErrorCode.ORDER_NOT_FOUND));
        validationReadPermission(order, userRole, companyId, hubId);
        return OrderRes.from(order);
    }

    //=====주문 목록 조회(각자 ALL)=====//
    @Override
    @Transactional(readOnly = true)
    public Page<OrderRes> getOrderPage(Pageable pageable, Long userId, String userRole, UUID companyId, UUID hubId) {

        Page<Order> OrderPage = findOrderByRole(pageable, userRole, companyId, hubId);
        return OrderPage.map(OrderRes::from);
    }

    private void validationDeletePermission(Order order, String userRole, UUID companyId, UUID hubId) {
        switch(userRole){
            case "MASTER":
                return;

            case "HUB_MANAGER":
                if(!order.getHubId().equals(hubId)){
                    throw new AppException(OrderErrorCode.UNAUTHORIZED_ACTION);
                }
                return;

            case "COMPANY_MANAGER":
                if(!order.getCompanyId().equals(companyId)){
                    throw new AppException(OrderErrorCode.UNAUTHORIZED_ACTION);
                }
                return;
            default:
                throw new AppException(OrderErrorCode.UNAUTHORIZED_ACTION);
        }
    }

    private void validationReadPermission(Order order, String userRole, UUID companyId, UUID hubId) {
        switch(userRole){
            case "MASTER":
                return;
            case "HUB_MANAGER":
                if(hubId == null || !hubId.equals(order.getHubId())){
                    throw new AppException(OrderErrorCode.UNAUTHORIZED_ACTION);
                }
                return;
            case "COMPANY_MANAGER":
                if(companyId == null || !companyId.equals(order.getCompanyId())){
                    throw new AppException(OrderErrorCode.UNAUTHORIZED_ACTION);
                }
                return;
            default:
                throw new AppException(OrderErrorCode.UNAUTHORIZED_ACTION);
        }
    }

    private Page<Order> findOrderByRole(Pageable pageable, String userRole, UUID companyId, UUID hubId) {
        switch(userRole){
            case "MASTER":
                return orderRepository.findAllByStatus(EntityStatus.ACTIVE, pageable);
            case "HUB_MANAGER":
                if(hubId == null){
                    throw new AppException(OrderErrorCode.UNAUTHORIZED_ACTION);
                }
                return orderRepository.findAllByHubIdAndStatus(hubId, EntityStatus.ACTIVE, pageable);
            case "COMPANY_MANAGER":
                if(companyId == null){
                    throw new AppException(OrderErrorCode.UNAUTHORIZED_ACTION);
                }
                return orderRepository.findAllByCompanyIdAndStatus(companyId, EntityStatus.ACTIVE, pageable);
            default:
                throw new AppException(OrderErrorCode.UNAUTHORIZED_ACTION);
        }
    }

}
