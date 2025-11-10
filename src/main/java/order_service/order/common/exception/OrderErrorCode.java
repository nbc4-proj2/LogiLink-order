package order_service.order.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OrderErrorCode implements ErrorCode{
    INVALID_ORDER_STATUS("ORDER0001", "주문 대기 상태가 아닙니다.", HttpStatus.BAD_REQUEST ),
    INVALID_ORDER_QUANTITY("ORDER0002", "수량은 1 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND("ORDER0003", "주문을 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    ORDER_DETAIL_NOT_FOUND("ORDER0004", "해당 주문 상세를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);




    private final String code;
    private final String message;
    private final HttpStatus status;

    OrderErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
