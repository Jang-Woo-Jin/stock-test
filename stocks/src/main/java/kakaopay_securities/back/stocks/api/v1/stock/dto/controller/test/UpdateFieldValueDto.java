package kakaopay_securities.back.stocks.api.v1.stock.dto.controller.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kakaopay_securities.back.stocks.api.v1.stock.dto.service.test.ServiceUpdateFieldValueDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

public class UpdateFieldValueDto {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(name = "UpdateFieldValue Request", title="UpdateFieldValue Request")
    public static class Request {
        @Schema(title="종목코드")
        @NotNull
        private String stockCode;

        @Schema(title="필드 명",
                description = "viewCount: 조회수, price: 현재가, volume: 거래량",
                defaultValue = "viewCount"
        )
        @NotNull
        private String fieldName = "viewCount";

        @Schema(title="필드 값",
                defaultValue = "0"
        )
        @NotNull
        private Integer fieldValue = 0;

        public ServiceUpdateFieldValueDto.Request toServiceDto() throws InvocationTargetException, IllegalAccessException {
            ServiceUpdateFieldValueDto.Request serReqDto = new ServiceUpdateFieldValueDto.Request();
            BeanUtils.copyProperties(serReqDto, this);
            return serReqDto;
        }
    }
}
