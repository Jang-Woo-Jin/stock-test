package securities.back.stocks.api.v1.stock.dto.controller.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import securities.back.stocks.api.v1.stock.dto.service.test.ServiceUpdateFieldValueDto;

import java.lang.reflect.InvocationTargetException;

public class TestDto {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(name = "Test Request", title="Test Request")
    public static class Request {
        @Schema(title="iInput1")
        private int iInput1;

        @Schema(title="iInput2")
        private int iInput2;

        @Schema(title="sInput1")
        private String sInput1;

        @Schema(title="sInput2")
        private String sInput2;
    }
}
