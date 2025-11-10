package order_service.order.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OrderErrorCode implements ErrorCode{
    ORDER_NOT_FOUND("ORDER0001", "주문을 찾을 수 없습니다.",HttpStatus.NOT_FOUND);



    private final String code;
    private final String message;
    private final HttpStatus status;

    OrderErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
