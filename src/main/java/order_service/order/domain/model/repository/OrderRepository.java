package order_service.order.domain.model.repository;

import order_service.order.domain.model.entity.EntityStatus;
import order_service.order.domain.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order,Long> {

    Optional<Order> findByOrderIdAndStatus(UUID orderId, EntityStatus status);

    Page<Order> findAllByStatus(EntityStatus status, Pageable pageable);

    Page<Order> findAllByHubIdAndStatus(UUID hubId, EntityStatus status, Pageable pageable);

    Page<Order> findAllByCompanyIdAndStatus(UUID companyId, EntityStatus status, Pageable pageable);
}
