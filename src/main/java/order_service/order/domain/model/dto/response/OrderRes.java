package order_service.order.domain.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import order_service.order.domain.model.entity.EntityStatus;
import order_service.order.domain.model.entity.Order;
import order_service.order.domain.model.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class OrderRes {
    private UUID orderId;
    private Long userId;
    private UUID companyId;
    private String destinationAddress;
    private UUID productId;
    private OrderStatus orderStatus;
    private UUID hubId;
    private String productName;
    private Long productPrice;
    private Long productQuantity;
    private Long totalPrice;
    private String memo;
    private EntityStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public static OrderRes from(Order order) {
        return OrderRes.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .companyId(order.getCompanyId())
                .destinationAddress(order.getDestinationAddress())
                .hubId(order.getHubId())
                .productId(order.getProductId())
                .productName(order.getProductName())
                .productPrice(order.getProductPrice())
                .productQuantity(order.getProductQuantity())
                .totalPrice(order.getTotalPrice())
                .memo(order.getMemo())
                .orderStatus(order.getOrderStatus())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updateAt(order.getUpdatedAt())
                .build();
    }
}
