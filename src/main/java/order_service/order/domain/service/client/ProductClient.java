package order_service.order.domain.service.client;

import order_service.order.common.BaseResponse;
import order_service.order.domain.model.dto.response.ProductRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "product-service", url = "http://host.docker.internal:19091")
public interface ProductClient {
    //상품 조회 (주문 시 검증용)
    @GetMapping("/api/v1/products/by-prd/{prdId}")
    BaseResponse<ProductRes> getProduct(@PathVariable Long prdId);

    //재고 감소 (주문 성공 시 호출)
    @PutMapping("/api/v1/products/{productId}/decrease-stock")
    void decreaseStock(@PathVariable UUID productId, @RequestParam Long amount);
}
