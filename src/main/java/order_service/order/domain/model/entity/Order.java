package order_service.order.domain.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import order_service.order.common.BaseTimeEntity;
import order_service.order.common.exception.AppException;
import order_service.order.common.exception.OrderErrorCode;

import java.util.ArrayList;
import java.util.List;
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
    private Long totalPrice;

    @Column(nullable = false)
    private Long totalQuantity;

    @Column(nullable = false)
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EntityStatus status = EntityStatus.ACTIVE;

    // 유저 서비스의 유저 ID
    @Column(nullable = false)
    private Long userId;

    // 업체 서비스의 업체 ID
    @Column(nullable = false)
    private UUID companyId;

    // 주문 상세 엔티티 참조
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrderDetail> orderDetails = new ArrayList<>();

    public static Order createOrder(Long userId, String memo, UUID companyId){
        Order order = new Order();
        order.userId = userId;
        order.companyId = companyId;
        order.memo = memo;
        order.orderStatus = OrderStatus.ORDER_PENDING;
        order.totalPrice = 0L;
        order.totalQuantity = 0L;
        return order;
    }

    // ORDER_PENDING일 때 주문에 상세를 추가
    public void addDetail(OrderDetail detail){
        ensureModifiable();
        orderDetails.add(detail);
        detail.setOrder(this);
        recalcTotals();
    }

    // ORDER_PENDING일 때만 주문에서 상세를 삭제
    public void removeDetail(OrderDetail detail){
        ensureModifiable();
        orderDetails.remove(detail);
        detail.setOrder(null);
        recalcTotals();
    }

    private void ensureModifiable() {
        if (!orderStatus.canModifyDetails()) {
            throw new AppException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
    }

    public void recalcTotals() {
        long quantity = 0L;
        long price = 0L;
        for (OrderDetail d : orderDetails) {
            if (d.isActive()) {
                quantity += d.getProductQuantity();
                price += d.getProductPrice() * d.getProductQuantity();
            }
        }
        this.totalQuantity = quantity;
        this.totalPrice = price;
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
