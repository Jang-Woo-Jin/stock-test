package securities.back.stocks.api.v1.stock.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import securities.back.stocks.domain.model.Stock;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StockRankVo {
    @Schema(title="종목")
    private Stock stock;
    @Schema(title="현재가")
    private int price;
    @Schema(title="전일 종가")
    private int previousPrice;
    @Schema(title="전일 대비 가격")
    private int diffPrice;
    @Schema(title="거래량")
    private long volume;
    @Schema(title = "전일비")
    public Double getDiffRatio(){
        return Math.round(((this.diffPrice * 1.0) / this.previousPrice) * 100 * 100) / 100.0;
    }
}
