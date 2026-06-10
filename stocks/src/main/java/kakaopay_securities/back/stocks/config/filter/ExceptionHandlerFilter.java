package kakaopay_securities.back.stocks.config.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakaopay_securities.back.stocks.common.exception.ApiException;
import kakaopay_securities.back.stocks.common.exception.dto.ExceptionResponseDto;
import kakaopay_securities.back.stocks.common.exception.error.BaseError;
import kakaopay_securities.back.stocks.common.exception.error.CommonError;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

	public ExceptionHandlerFilter() {}

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (ApiException e) { // controller 밖에서 api exception 이 발생할 때 catch (로그인 과정 등)
			BaseError error =  e.getError();

			ExceptionResponseDto exceptionResponseDto = ExceptionResponseDto.builder()
					.errorCode(error.getCode())
					.errorMsg(e.getMessage())
					.errorData(e.getData())
					.build();
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(error.getStatus().value());
			response.getWriter().write(convertObjectToJson(exceptionResponseDto));
		} catch (RuntimeException e) { // controller 밖에서 runtime exception 이 발생할 때 catch
			CommonError error = CommonError.INTERNAL_SERVER_ERROR;

			ExceptionResponseDto exceptionResponseDto = ExceptionResponseDto.builder()
					.errorCode(error.getCode())
					.errorMsg(error.getMsg())
					.errorData(e.toString())
					.build();
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(error.getStatus().value());
			response.getWriter().write(convertObjectToJson(exceptionResponseDto));
		}
	}
	
	private String convertObjectToJson(Object object) throws JsonProcessingException {
		if(object == null)
			return null;
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(object);
	}
}