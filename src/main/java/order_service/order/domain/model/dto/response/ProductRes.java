package order_service.order.domain.model.dto.response;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ProductRes {
    private UUID productId;
    private String productName;
    private String productDescription;
    private Long productPrice;
    private Long productQuantity;
    private String status;
    private UUID hubId;
    private UUID companyId;
}
