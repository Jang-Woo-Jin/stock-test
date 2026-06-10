package securities.back.stocks.common.exception.error;


import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ServiceError implements BaseError{
	OVER_LIMIT_PRICE(HttpStatus.BAD_REQUEST, "S001", "가격이 상한가/하한가를 벗어났습니다.")
	;

	private final HttpStatus status;
	private final String code;
	private String msg;
	
	ServiceError(HttpStatus status, String code) {
		this.status = status;
		this.code = code;
	}
	
	ServiceError(HttpStatus status, String code, String msg) {
		this.status = status;
		this.code = code;
		this.msg = msg;
	}
}
