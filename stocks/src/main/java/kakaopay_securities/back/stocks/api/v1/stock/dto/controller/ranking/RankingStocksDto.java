package kakaopay_securities.back.stocks.api.v1.stock.dto.controller.ranking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import kakaopay_securities.back.stocks.api.v1.stock.dto.service.ranking.ServiceRankingStocksDto;
import kakaopay_securities.back.stocks.api.v1.stock.dto.vo.StockRankVo;
import kakaopay_securities.back.stocks.common.extend.PageableDto;
import lombok.*;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class RankingStocksDto {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(name = "RankingStocks Request", title="RankingStocks Request")
    public static class Request extends PageableDto.Request {

        @Schema(title="필터",
                description = "1: 인기, 2: 전일대비(상승/하락), 3: 거래량",
                defaultValue = "1"
        )
        private Integer filter = 1;

        @Schema(title="정렬 방향",
                description = "0: 오름차순, 1: 내림차순",
                defaultValue = "0"
        )
        private Integer direction = 0;

        public ServiceRankingStocksDto.Request toServiceDto() throws InvocationTargetException, IllegalAccessException {
            ServiceRankingStocksDto.Request serReqDto = new ServiceRankingStocksDto.Request();
            BeanUtils.copyProperties(serReqDto, this);
            return serReqDto;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    @Schema(name = "RankingStocks Response", title="RankingStocks Response")
    public static class Response extends PageableDto.Response {
        @Schema(title="종목 순위")
        private List<StockRankVo> data;

        public Response(ServiceRankingStocksDto.Response serReqDto) throws InvocationTargetException, IllegalAccessException {
            super(serReqDto.getPage(), serReqDto.getTotalRecords());
            BeanUtils.copyProperties(this, serReqDto);
        }
    }
}
