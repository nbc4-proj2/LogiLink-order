package order_service.order.domain.service.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @PostMapping("/api/v1/deliveries")
    void createDelivery(@RequestBody DeliveryCreateRequest request);

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class DeliveryCreateRequest {
        private UUID orderId;
        private UUID originHubId;
        private UUID destinationId;
        private String destinationAddress;
    }
}
