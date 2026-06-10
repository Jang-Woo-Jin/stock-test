package kakaopay_securities.back.stocks.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import kakaopay_securities.back.stocks.common.exception.ApiException;
import kakaopay_securities.back.stocks.common.exception.dto.ExceptionResponseDto;
import kakaopay_securities.back.stocks.common.exception.dto.ValidationErrorVo;
import kakaopay_securities.back.stocks.common.exception.error.BaseError;
import kakaopay_securities.back.stocks.common.exception.error.CommonError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ExceptionResponseDto> handleApiException(HttpServletRequest request, final ApiException e) {
		BaseError error =  e.getError();

		ExceptionResponseDto exceptionResponseDto = ExceptionResponseDto.builder()
													.errorCode(error.getCode())
													.errorMsg(e.getMessage())
													.errorData(e.getData())
													.build();
		
		return new ResponseEntity<ExceptionResponseDto>(exceptionResponseDto, error.getStatus());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		return this.handleFieldErrors(ex.getBindingResult().getFieldErrors());
	}

	private ResponseEntity<Object> handleFieldErrors(List<FieldError> fieldErrorList){
		CommonError error = CommonError.INVALID_PARAMETER;
		
		ArrayList<ValidationErrorVo> errorData = new ArrayList<ValidationErrorVo>();
		for(FieldError fieldError : fieldErrorList){
			ValidationErrorVo validationErrorVo = ValidationErrorVo.builder()
					.field(fieldError.getField())
					.message(fieldError.getDefaultMessage())
					.input(fieldError.getRejectedValue())
					.build();

			errorData.add(validationErrorVo);
		}

		Object exceptionResponseDto = ExceptionResponseDto.builder()
				.errorCode(error.getCode())
				.errorMsg(error.getMsg())
				.errorData(errorData)
				.build();
		
		return new ResponseEntity<Object>(exceptionResponseDto, error.getStatus());
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponseDto> handler(HttpServletRequest request, final Exception e) {
		CommonError error = CommonError.INTERNAL_SERVER_ERROR;
		
		ExceptionResponseDto exceptionResponseDto = ExceptionResponseDto.builder()
													.errorCode(error.getCode())
													.errorMsg(error.getMsg())
													.errorData(e.getMessage())
													.build();
		
		return new ResponseEntity<ExceptionResponseDto>(exceptionResponseDto, error.getStatus());
	}
}