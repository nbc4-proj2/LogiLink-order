package order_service.order.domain.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class OrderUpdateReq {

    @NotBlank(message = "도착지 주소는 필수입니다.")
    @Size(max = 255)
    private String destinationAddress;

    @NotNull(message = "상품 아이디는 필수입니다.")
    private UUID productId;

    @NotNull(message = "상품 수량은 필수입니다.")
    @Min(value = 0, message = "재고 수량은 0개 이상이어야 합니다.")
    private Long productQuantity;

    @NotBlank(message = "요청사항은 필수입니다.")
    private String memo;

}
