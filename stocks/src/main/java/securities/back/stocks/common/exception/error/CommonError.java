package securities.back.stocks.common.exception.error;


import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum CommonError implements BaseError{
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "예상치 못한 오류가 발생했습니다."),
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "E002", "잘못된 입력입니다."),
	RESOURCE_NOT_FOUND(HttpStatus.BAD_REQUEST, "E003", "데이터가 존재하지 않습니다."),
	RESOURCE_CONFLICT(HttpStatus.CONFLICT, "E004", "중복 데이터가 존재합니다.")
	;
	
	private final HttpStatus status;
	private final String code;
	private String msg;
	
	CommonError(HttpStatus status, String code) {
		this.status = status;
		this.code = code;
	}
	
	CommonError(HttpStatus status, String code, String msg) {
		this.status = status;
		this.code = code;
		this.msg = msg;
	}
}
