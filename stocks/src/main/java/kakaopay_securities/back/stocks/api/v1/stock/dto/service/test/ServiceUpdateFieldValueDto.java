package kakaopay_securities.back.stocks.api.v1.stock.dto.service.test;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ServiceUpdateFieldValueDto {
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Request {
        private String stockCode;
        private String fieldName;
        private Integer fieldValue;
    }
}
