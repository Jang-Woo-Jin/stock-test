package kakaopay_securities.back.stocks.api.v1.stock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kakaopay_securities.back.stocks.api.v1.stock.dto.controller.ranking.RankingStocksDto;
import kakaopay_securities.back.stocks.api.v1.stock.service.RankingService;
import kakaopay_securities.back.stocks.api.v1.stock.dto.service.ranking.ServiceRankingStocksDto;
import kakaopay_securities.back.stocks.common.exception.dto.ExceptionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Ranking", description = "랭킹조회 API")
@RequestMapping(value = "/api/v1/ranking")
public class RankingController {
    private final RankingService rankingService;

    @PostMapping(value = "/stocks")
    @Operation(summary = "종목 순위 조회 API",
            description =   "인기(조회순) - filter: 1, direction: 1\n\n" +
                            "상승 - filter: 2, direction: 1\n\n" +
                            "하락 - filter: 2, direction: 0\n\n" +
                            "거래량 - filter: 3, direction: 1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "랭킹조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "E002: 올바르지 않은 필터값",
                    content = @Content(schema = @Schema(implementation = ExceptionResponseDto.class)))
    })
    public ResponseEntity<RankingStocksDto.Response> stocks(
            @Valid@RequestBody RankingStocksDto.Request reqDto) throws Exception {

        ServiceRankingStocksDto.Response serResDto = rankingService.getStockRanking(reqDto.toServiceDto());
        RankingStocksDto.Response resDto = new RankingStocksDto.Response(serResDto);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
