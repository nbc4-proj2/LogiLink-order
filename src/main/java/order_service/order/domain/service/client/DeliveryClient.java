package order_service.order.domain.service.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "deliveryClient", url = "http://host.docker.internal:19091")
public interface DeliveryClient {

    @PostMapping("/api/v1/deliveries/{orderId}")
    void createDelivery(@PathVariable UUID orderId, @RequestHeader("X-User-Role") String userRole, @RequestBody DeliveryCreateRequest request);

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class DeliveryCreateRequest {
        private UUID originHubId;
        private UUID destinationId;
        private String destinationAddress;
    }
}
