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
@Table(name="p_order_details")
public class OrderDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Long productPrice;

    @Column(nullable = false)
    private Long productQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EntityStatus status = EntityStatus.ACTIVE;

    public static OrderDetail createOrderDetail(UUID productId, String productName, Long productPrice, Long productQuantity){
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.productId = productId;
        orderDetail.productName = productName;
        orderDetail.productPrice = productPrice;
        orderDetail.productQuantity = productQuantity;
        orderDetail.status = EntityStatus.ACTIVE;
        return orderDetail;
    }

    // 수량 수정 시 상품 수량 재계산
    public void updateQuantity(Long newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new AppException(OrderErrorCode.INVALID_ORDER_QUANTITY);
        }
        this.productQuantity = newQuantity;
        // 상위 합계 갱신
        if (this.order != null) {
            this.order.recalcTotals();
        }
    }

    void setOrder(Order order) {
        this.order = order;
    }

    // 주문 상세 상태 확인
    public boolean isActive() {
        return this.status == EntityStatus.ACTIVE;
    }

    // 주문 상세 비활성화
    public void delete() {
        this.status = EntityStatus.INACTIVE;
    }
}
