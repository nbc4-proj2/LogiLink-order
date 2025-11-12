package order_service.order.domain.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import order_service.order.common.BaseTimeEntity;
import order_service.order.common.exception.AppException;
import order_service.order.common.exception.OrderErrorCode;

import java.util.UUID;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(force = true)
@Table(name="p_orders")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Long productPrice;

    @Column(nullable = false)
    private Long productQuantity;

    @Column(nullable = false)
    private Long totalPrice;

    @Column(nullable = false)
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EntityStatus status = EntityStatus.ACTIVE;

    @Column(nullable = false)
    private String destinationAddress;

    // 유저 서비스의 유저 ID
    @Column(nullable = false)
    private Long userId;

    // 허브 서비스의 허브 ID
    @Column(nullable = false)
    private UUID hubId;

    // 업체 서비스의 업체 ID
    @Column(nullable = false)
    private UUID companyId;

    public static Order createOrder(Long userId, UUID companyId, String destinationAddress, UUID hubId, UUID productId,
                               String productName, Long productPrice, Long productQuantity, String memo) {
        Order order = new Order();
        order.userId = userId;
        order.companyId = companyId;
        order.destinationAddress = destinationAddress;
        order.hubId = hubId;
        order.productId = productId;
        order.productName = productName;
        order.productPrice = productPrice;
        order.productQuantity = productQuantity;
        order.totalPrice = productPrice * productQuantity;
        order.memo = memo;
        order.orderStatus = OrderStatus.ORDER_PENDING;
        return order;
    }

    public void updateOrder(UUID companyId, UUID hubId, UUID productId, String destinationAddress, String productName,
                            Long productPrice, Long productQuantity, String memo){
        this.companyId = companyId;
        this.destinationAddress = destinationAddress;
        this.hubId = hubId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.memo = memo;
    }

    public static Order createRejected(
            Long userId,
            UUID companyId,
            String destinationAddress,
            UUID hubId,
            UUID productId,
            String productName,
            Long productPrice,
            Long productQuantity
    ) {
        return Order.builder()
                .userId(userId)
                .companyId(companyId)
                .destinationAddress(destinationAddress)
                .hubId(hubId)
                .productId(productId)
                .productName(productName)
                .productPrice(productPrice)
                .productQuantity(productQuantity)
                .totalPrice(productPrice * productQuantity)
                .orderStatus(OrderStatus.ORDER_REJECTED)
                .status(EntityStatus.ACTIVE)
                .build();
    }

    //총 가격 계산
    public Long getTotalPrice() {
        return this.productPrice * this.productQuantity;
    }

    // 주문 수락 조건은 ORDER_PENDING
    public void accept(){
        if(!orderStatus.isPending()){
            throw new AppException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
        this.orderStatus = OrderStatus.ORDER_SUCCEEDED;
    }

    // 주문 취소 조건은 ORDER_PENDING
    public void cancel(){
        if(!orderStatus.canCancel()){
            throw new AppException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
        this.orderStatus = OrderStatus.ORDER_CANCELLED;
    }

    // 주문 거절 조건은 ORDER_PENDING
    public void reject(){
        if(!orderStatus.isPending()){
            throw new AppException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
        this.orderStatus = OrderStatus.ORDER_REJECTED;
    }

    // 주문 상태 확인
    public boolean isActive() {
        return this.status == EntityStatus.ACTIVE;
    }

    // 주문 비활성화
    public void delete() {
        this.status = EntityStatus.INACTIVE;
    }
}
