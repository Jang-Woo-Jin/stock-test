package kakaopay_securities.back.stocks.common.exception;

import kakaopay_securities.back.stocks.common.exception.error.BaseError;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
	private BaseError error;
	private Object data;
	
	public ApiException(BaseError invalidFaStatus) {
		super(invalidFaStatus.getMsg());
		this.error = invalidFaStatus;
	}
	
	public ApiException(BaseError error, String msg) {
		super(msg);
		this.error = error;
	}
	
	public ApiException(BaseError error, Object data) {
		super(error.getMsg());
		this.error = error;
		this.data = data;
	}
	
	public ApiException(BaseError error, String msg, Object data) {
		super(msg);
		this.error = error;
		this.data = data;
	}
}
