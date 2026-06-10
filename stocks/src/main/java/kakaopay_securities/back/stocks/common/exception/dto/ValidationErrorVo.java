package kakaopay_securities.back.stocks.common.exception.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidationErrorVo {
	private String field;
	private String message;
	private Object input;
}
