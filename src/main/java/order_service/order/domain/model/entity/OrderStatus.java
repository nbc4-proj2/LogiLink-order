package order_service.order.domain.model.entity;

public enum OrderStatus {

    ORDER_PENDING("주문 대기"),
    ORDER_SUCCEEDED("주문 성공"),
    ORDER_CANCELLED("주문 취소"),
    ORDER_REJECTED("주문 거절");

    private final String orderStatus;

    OrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    // 현재 주문이 대기 상태인지 확인
    public boolean isPending(){
        return this == ORDER_PENDING;
    }
    // 주문 상세 변경이 가능한지 확인
    public boolean canModifyDetails(){
        return this == ORDER_PENDING;
    }
    // 주문 취소 가능한지 확인
    public boolean canCancel(){
        return this == ORDER_PENDING;
    }

}
