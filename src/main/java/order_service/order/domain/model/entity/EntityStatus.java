package order_service.order.domain.model.entity;

public enum EntityStatus {
    ACTIVE("정상"),
    INACTIVE("비활성화");

    private final String status;

    EntityStatus(String status) {
        this.status = status;
    }
}
