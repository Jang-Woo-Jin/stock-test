package kakaopay_securities.back.stocks.common.exception.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "ExceptionResponseDto", title="Exception Response")
public class ExceptionResponseDto {
	@Schema(title = "에러 코드")
	private String errorCode;
	@Schema(title = "에러 메세지")
	private String errorMsg;
	@Schema(title = "에러 데이터", nullable = true)
	private Object errorData;
}
