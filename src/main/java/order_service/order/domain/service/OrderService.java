package order_service.order.domain.service;

import order_service.order.domain.model.dto.request.OrderCreateReq;
import order_service.order.domain.model.dto.request.OrderUpdateReq;
import order_service.order.domain.model.dto.response.OrderRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    // 주문 생성
    OrderRes createOrder(OrderCreateReq requestDto, Long userId, String userRole, UUID companyId, UUID hubId);

    // 주문 수락
    OrderRes acceptOrder(UUID orderId, Long userId, String userRole, UUID companyId, UUID hubId);

//    // 주문 거절
//    OrderRes rejectOrder(UUID orderId, Long userId, String userRole, UUID companyId, UUID hubId);

    // 주문 수정
    OrderRes updateOrder(UUID orderId, OrderUpdateReq requestDto, Long userId, String userRole, UUID companyId, UUID hubId);

    // 주문 논리 삭제
    void deleteOrder(UUID orderId, Long userId, String userRole, UUID companyId, UUID hubId);

    // 주문 단건 조회
    OrderRes getOrder(UUID orderId, Long userId, String userRole, UUID companyId, UUID hubId);

    // 주문 목록 조회
    Page<OrderRes> getOrderPage(Pageable pageable, Long userId, String userRole, UUID companyId, UUID hubId);

//    // 주문 취소
//    OrderRes cancelOrder(UUID orderId, Long userId, String userRole);
}
