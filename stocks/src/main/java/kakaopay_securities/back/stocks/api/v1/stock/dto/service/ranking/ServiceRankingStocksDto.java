package kakaopay_securities.back.stocks.api.v1.stock.dto.service.ranking;

import kakaopay_securities.back.stocks.api.v1.stock.dto.vo.StockRankVo;
import kakaopay_securities.back.stocks.common.extend.PageableDto;
import lombok.*;

import java.util.List;

public class ServiceRankingStocksDto {
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Request extends PageableDto.Request {
        private int filter;
        private int direction = 0;
    }

    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class Response extends PageableDto.Response{
        private List<StockRankVo> data;

        @Builder
        public Response(int curPage, long totalRecords, List<StockRankVo> data) {
            super(curPage, totalRecords);
            this.data = data;
        }
    }
}
