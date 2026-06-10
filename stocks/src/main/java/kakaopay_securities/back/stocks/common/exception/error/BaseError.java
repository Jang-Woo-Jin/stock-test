package kakaopay_securities.back.stocks.common.exception.error;

import org.springframework.http.HttpStatus;

public interface BaseError {
	HttpStatus getStatus();
	String getCode();
	String getMsg();
}
