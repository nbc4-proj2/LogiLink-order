package order_service.order.domain.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import order_service.order.common.BaseResponse;
import order_service.order.common.PageableUtils;
import order_service.order.domain.model.dto.request.OrderCreateReq;
import order_service.order.domain.model.dto.request.OrderUpdateReq;
import order_service.order.domain.model.dto.response.OrderRes;
import order_service.order.domain.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    public BaseResponse<OrderRes> createOrder(
            @RequestBody @Valid OrderCreateReq requestDto,
            @RequestHeader(value = "USER_ID") Long userId,
            @RequestHeader(value = "USER_ROLE") String userRole,
            @RequestHeader(value = "COMPANY_ID") UUID companyId,
            @RequestHeader(value = "HUB_ID") UUID hubId){
        OrderRes responseDto = orderService.createOrder(requestDto, userId, userRole, companyId, hubId);
        return BaseResponse.success(responseDto);
    }

    // 주문 수정
    @PutMapping("/{orderId}")
    public BaseResponse<OrderRes> upadateOrder(
            @PathVariable UUID orderId,
            @RequestBody @Valid OrderUpdateReq requestDto,
            @RequestHeader(value = "USER_ID") Long userId,
            @RequestHeader(value = "USER_ROLE") String userRole,
            @RequestHeader(value = "COMPANY_ID") UUID companyId,
            @RequestHeader(value = "HUB_ID") UUID hubId){
        OrderRes responseDto = orderService.updateOrder(orderId, requestDto, userId, userRole, companyId, hubId);
        return BaseResponse.success(responseDto);
    }

    // 주문 논리 삭제
    @DeleteMapping("/{orderId}")
    public BaseResponse<Void> deleteOrder(
            @PathVariable UUID orderId,
            @RequestHeader(value = "USER_ID") Long userId,
            @RequestHeader(value = "USER_ROLE") String userRole,
            @RequestHeader(value = "COMPANY_ID") UUID companyId,
            @RequestHeader(value = "HUB_ID") UUID hubId){
        orderService.deleteOrder(orderId, userId, userRole, companyId, hubId);
        return BaseResponse.success(null);
    }

    // 주문 단건 조회
    @GetMapping("/{orderId}")
    public BaseResponse<OrderRes> getOrder(
            @PathVariable UUID orderId,
            @RequestHeader(value = "USER_ID") Long userId,
            @RequestHeader(value = "USER_ROLE") String userRole,
            @RequestHeader(value = "COMPANY_ID") UUID companyId,
            @RequestHeader(value = "HUB_ID") UUID hubId){
        return BaseResponse.success(orderService.getOrder(orderId, userId, userRole, companyId, hubId));
    }

    // 주문 목록 조회
    @GetMapping
    public BaseResponse<Page<OrderRes>> getOrderPage(
            Pageable pageable,
            @RequestHeader(value = "USER_ID") Long userId,
            @RequestHeader(value = "USER_ROLE") String userRole,
            @RequestHeader(value = "COMPANY_ID") UUID companyId,
            @RequestHeader(value = "HUB_ID") UUID hubId){
        Pageable p = PageableUtils.enforce(pageable);
        Page<OrderRes> result = orderService.getOrderPage(p, userId, userRole, companyId, hubId);
        return BaseResponse.success(result);
    }

//    // 주문 취소
//    @PatchMapping("/{orderId}/cancel")
//    public BaseResponse<OrderRes> cancelOrder(
//            @PathVariable UUID orderId,
//            @RequestHeader("USER_ID") Long userId,
//            @RequestHeader("USER_ROLE") String userRole){
//        OrderRes responseDto = orderService.cancelOrder(orderId, userId,userRole);
//        return BaseResponse.success(responseDto);
//    }

    // 주문 수락
    @PatchMapping("/{orderId}/accept")
    public BaseResponse<OrderRes> acceptOrder(
            @PathVariable UUID orderId,
            @RequestHeader("USER_ID") Long userId,
            @RequestHeader("USER_ROLE") String userRole,
            @RequestHeader(value = "COMPANY_ID") UUID companyId,
            @RequestHeader(value = "HUB_ID") UUID hubId){
        OrderRes responseDto = orderService.acceptOrder(orderId, userId, userRole, companyId, hubId);
        return BaseResponse.success(responseDto);
    }

//    // 주문 거절
//    @PatchMapping("/{orderId}/reject")
//    public BaseResponse<OrderRes> rejectOrder(
//            @PathVariable UUID orderId,
//            @RequestHeader("USER_ID") Long userId,
//            @RequestHeader("USER_ROLE") String userRole,
//            @RequestHeader(value = "COMPANY_ID") UUID companyId,
//            @RequestHeader(value = "HUB_ID") UUID hubId){
//        OrderRes responseDto = orderService.rejectOrder(orderId, userId, userRole, companyId, hubId);
//        return BaseResponse.success(responseDto);
//    }
}
