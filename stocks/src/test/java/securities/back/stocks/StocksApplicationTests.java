package securities.back.stocks;

import securities.back.stocks.api.v1.stock.dto.controller.ranking.RankingStocksDto;
import securities.back.stocks.api.v1.stock.dto.controller.test.UpdateFieldValueDto;
import securities.back.stocks.api.v1.stock.dto.vo.StockRankVo;
import securities.back.stocks.common.constant.StockConstant;
import securities.back.stocks.common.util.DateUtil;
import securities.back.stocks.common.util.StockUtil;
import securities.back.stocks.domain.dao.PriceDao;
import securities.back.stocks.domain.dao.StockDao;
import securities.back.stocks.domain.model.Price;
import securities.back.stocks.domain.model.Stock;
import securities.back.stocks.domain.model.multiple_key.PriceId;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestMethodOrder(MethodOrdered.OrderAnnotation.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StocksApplicationTests {
    @LocalServerPort
    private int port;

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private StockDao stockDao;
    @Autowired private PriceDao priceDao;

    /*
     * 테스트용 값 세팅
     * - 관련된 모든 값을 0으로 초기화
     * - 아래 6개 종목의 값을 순서대로 세팅
     * - 기본값 세팅 후 조회 시
     *   - 인기순(조회수 내림차순, 6개) - 105560, 028260, 035420, 005935, 005930, 035720
     *   - 상승종목 순(3개) - 035720, 005930, 005935
     *   - 하락종목 순(3개) - 105560, 028260, 035420
     *   - 거래량 순(6개) - 035720, 105560, 028260, 035420, 005930, 005935
     * */
    final int testStockCount = 6;
    final List<String> stockCodeList = Arrays.asList("035720",  "005930", "005935", "035420", "028260", "105560");
    final List<Integer> viewCountList = Arrays.asList(20, 30, 40, 50, 60, 70);
    final List<Integer> currentPriceList = Arrays.asList(100000, 80000, 60000, 40000, 20000, 10000);
    final List<Integer> previousPriceList = Arrays.asList(80000, 70000, 55000, 41000, 22000, 13000);
    final List<Integer> volumeList = Arrays.asList(1000000, 500000, 400000, 600000, 700000, 800000);

    final List<String> answerViewCount = Arrays.asList("105560", "028260", "035420", "005935", "005930", "035720");
    final List<String> answerBullMarket = Arrays.asList("035720", "005930", "005935");
    final List<String> answerBearMarket = Arrays.asList("105560", "028260", "035420");
    final List<String> answerVolume = Arrays.asList("035720", "105560", "028260", "035420", "005930", "005935");
    
    @BeforeAll
    public static void setup(@Autowired TestRestTemplate restTemplate) {
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @BeforeEach
    void resetTestValue() {
        // 전체 종목
        List<Stock> stockList = stockDao.findAll();
        List<Price> priceList = priceDao.findAll();

        // 테스트 코드를 위해 값을 모두 0으로 초기화 및 테스트 종목들을 따로 저장
        for(Stock item: stockList) item.setViewCount(0);

        for(Price item: priceList) {
            item.setPrice(0);
            item.setPreviousPrice(0);
            item.setDiffPrice(0);
            item.setHighPrice(0);
            item.setLowPrice(0);
            item.setStartPrice(0);
            item.setUpperLimitPrice(0);
            item.setLowerLimitPrice(0);
            item.setVolume(0);
        }

        stockDao.saveAll(stockList);
        priceDao.saveAll(priceList);

        log.debug("reset stock and price data");
    }

    private void setStockData() {
        List<Stock> testStocks = new ArrayList<>();
        for(int i = 0; i < testStockCount; i++) {
            Optional<Stock> optionalStock = stockDao.findById(stockCodeList.get(i));
            if(optionalStock.isPresent()) {
                Stock stock = optionalStock.get();
                stock.setViewCount(viewCountList.get(i));
                testStocks.add(stock);
            }
        }
        stockDao.saveAll(testStocks); // 테스트 데이터 저장
        log.debug("Set stock test data");
    }

    private void setPriceData() {
        String today = DateUtil.getToday();
        List<Price> priceList = new ArrayList<>();
        for(int i = 0; i < testStockCount; i++) {
            Optional<Price> optionalPrice = priceDao.findById(new PriceId(today, stockCodeList.get(i)));
            if(optionalPrice.isEmpty()) continue;

            Price priceItem = optionalPrice.get();
            int currentPrice = currentPriceList.get(i);
            int previousPrice = previousPriceList.get(i);

            priceItem.setPrice(currentPrice);
            priceItem.setPreviousPrice(previousPrice);
            priceItem.setDiffPrice(currentPrice - previousPrice);
            priceItem.setHighPrice(currentPrice);
            priceItem.setLowPrice(currentPrice);
            priceItem.setStartPrice(currentPrice);
            priceItem.setUpperLimitPrice(StockUtil.getUpperLimitPrice(currentPrice));
            priceItem.setLowerLimitPrice(StockUtil.getLowerLimitPrice(currentPrice));
            priceItem.setVolume(volumeList.get(i));

            priceList.add(priceItem);
        }

        priceDao.saveAll(priceList);
        log.debug("Set price test data");
    }
    
    @Test /* 인기(조회수) 랭킹 테스트 */
    void testViewCountRank() {
        log.debug("start testViewCountRank");

        int filter = StockConstant.RankingCategory.VIEW_COUNT;
        int direction = StockConstant.Direction.DESC;
        int page = 0;
        int size = 6;

        setStockData();

        RankingStocksDto.Request reqDto = new RankingStocksDto.Request(filter, direction);
        reqDto.setPageData(page, size);

        String url = "http://localhost:"+ port + "/api/v1/ranking/stocks";

        // API 호출
        ResponseEntity<RankingStocksDto.Response> responseEntity = restTemplate.postForEntity(url, reqDto, RankingStocksDto.Response.class);

        // API 결과 검증
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<String> responseCodeList = new ArrayList<>();
        for(StockRankVo item: responseEntity.getBody().getData())
            responseCodeList.add(item.getStock().getCode());

        assertThat(responseCodeList.size()).isEqualTo(size);
        assertThat(responseCodeList).isEqualTo(answerViewCount);

        log.debug("end testViewCountRank");
    }

    @Test /* 상승장 랭킹 테스트 */
    void testBullMarketRank() {
        log.debug("start testBullMarketRank");

        int filter = StockConstant.RankingCategory.DIFF_PRICE;
        int direction = StockConstant.Direction.DESC;
        int page = 0;
        int size = 3;

        setPriceData();

        RankingStocksDto.Request reqDto = new RankingStocksDto.Request(filter, direction);
        reqDto.setPageData(page, size);

        String url = "http://localhost:"+ port + "/api/v1/ranking/stocks";

        // API 호출
        ResponseEntity<RankingStocksDto.Response> responseEntity = restTemplate.postForEntity(url, reqDto, RankingStocksDto.Response.class);

        // API 결과 검증
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<String> responseCodeList = new ArrayList<>();
        for(StockRankVo item: responseEntity.getBody().getData())
            responseCodeList.add(item.getStock().getCode());

        assertThat(responseCodeList.size()).isEqualTo(size);
        assertThat(responseCodeList).isEqualTo(answerBullMarket);

        log.debug("end testBullMarketRank");
    }

    @Test /* 하락장 랭킹 테스트 */
    void testBearMarketRank() {
        log.debug("start testBearMarketRank");

        int filter = StockConstant.RankingCategory.DIFF_PRICE;
        int direction = StockConstant.Direction.ASC;
        int page = 0;
        int size = 3;

        setPriceData();

        RankingStocksDto.Request reqDto = new RankingStocksDto.Request(filter, direction);
        reqDto.setPageData(page, size);

        String url = "http://localhost:"+ port + "/api/v1/ranking/stocks";

        // API 호출
        ResponseEntity<RankingStocksDto.Response> responseEntity = restTemplate.postForEntity(url, reqDto, RankingStocksDto.Response.class);

        // API 결과 검증
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<String> responseCodeList = new ArrayList<>();
        for(StockRankVo item: responseEntity.getBody().getData())
            responseCodeList.add(item.getStock().getCode());

        assertThat(responseCodeList.size()).isEqualTo(size);
        assertThat(responseCodeList).isEqualTo(answerBearMarket);

        log.debug("end testBearMarketRank");
    }

    @Test /* 거래량 랭킹 테스트 */
    void testVolumeRank() {
        log.debug("start testVolumeRank");

        int filter = StockConstant.RankingCategory.VOLUME;
        int direction = StockConstant.Direction.DESC;
        int page = 0;
        int size = 6;

        setPriceData();

        RankingStocksDto.Request reqDto = new RankingStocksDto.Request(filter, direction);
        reqDto.setPageData(page, size);

        String url = "http://localhost:"+ port + "/api/v1/ranking/stocks";

        // API 호출
        ResponseEntity<RankingStocksDto.Response> responseEntity = restTemplate.postForEntity(url, reqDto, RankingStocksDto.Response.class);

        // API 결과 검증
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<String> responseCodeList = new ArrayList<>();
        for(StockRankVo item: responseEntity.getBody().getData())
            responseCodeList.add(item.getStock().getCode());

        assertThat(responseCodeList.size()).isEqualTo(size);
        assertThat(responseCodeList).isEqualTo(answerVolume);

        log.debug("end testVolumeRank");
    }
    
    @Test /* 인기(조회수) 변화 테스트 */
    void testChangeViewCount() {
        log.debug("start testChangeValue");

        setStockData();

        String url = "http://localhost:"+ port + "/api/v1/test/filed-value";

        String stockCode = "035720";
        Optional<Stock> optionalStock = stockDao.findById(stockCode);
        if(optionalStock.isEmpty()) {
            fail("Invalid stockCode: " + stockCode);
        }
        
        int updateValue = 700;
        UpdateFieldValueDto.Request reqDto =
                new UpdateFieldValueDto.Request(stockCode, StockConstant.FieldName.VIEW_COUNT_FIELD, updateValue);

        // API 호출
        HttpEntity<UpdateFieldValueDto.Request> requestEntity = new HttpEntity<>(reqDto);
        ResponseEntity<Void> responseEntity = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Void.class);

        // API 결과 검증
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        optionalStock = stockDao.findById(stockCode);
        if(optionalStock.isPresent())
            assertThat(optionalStock.get().getViewCount()).isEqualTo(updateValue);

        log.debug("end testChangeValue");
    }

    @Test /* 가격 변화 테스트 */
    void testChangePrice() {
        log.debug("start testChangeValue");

        setPriceData();

        String url = "http://localhost:"+ port + "/api/v1/test/filed-value";

        String today = DateUtil.getToday();
        String stockCode = "035720";

        Optional<Price> optionalPrice = priceDao.findById(new PriceId(today, stockCode));
        if(optionalPrice.isEmpty()) {
            fail("Invalid date,stockCode: " + today + "," + stockCode);
        }
        
        // 현재가 = 시가 = 고가 = 저가 = 100000, 전일종가: 80000
        // 현재가를 110000원으로 변경
        int highUpdateValue = 110000;
        UpdateFieldValueDto.Request reqDto =
                new UpdateFieldValueDto.Request(stockCode, StockConstant.FieldName.PRICE_FIELD, highUpdateValue);

        // API 호출
        HttpEntity<UpdateFieldValueDto.Request> requestEntity = new HttpEntity<>(reqDto);
        ResponseEntity<Void> responseEntity = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Void.class);

        // API 결과 검증
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        optionalPrice = priceDao.findById(new PriceId(today, stockCode));
        if (optionalPrice.isPresent()) {
            Price priceItem = optionalPrice.get();
            assertThat(priceItem.getPrice()).isEqualTo(highUpdateValue);
            assertThat(priceItem.getDiffPrice()).isEqualTo(30000);
            assertThat(priceItem.getHighPrice()).isEqualTo(highUpdateValue);
            assertThat(priceItem.getLowPrice()).isNotEqualTo(highUpdateValue);
        }

        // 현재가를 75000원으로 변경
        int lowUpdateValue = 75000;
        reqDto = new UpdateFieldValueDto.Request(stockCode, StockConstant.FieldName.PRICE_FIELD, lowUpdateValue);

        // API 호출
        requestEntity = new HttpEntity<>(reqDto);
        responseEntity = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Void.class);

        // API 결과 검증
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        optionalPrice = priceDao.findById(new PriceId(today, stockCode));
        if (optionalPrice.isPresent()) {
            Price priceItem = optionalPrice.get();
            assertThat(priceItem.getPrice()).isEqualTo(lowUpdateValue);
            assertThat(priceItem.getDiffPrice()).isEqualTo(-5000);
            assertThat(priceItem.getHighPrice()).isNotEqualTo(lowUpdateValue);
            assertThat(priceItem.getLowPrice()).isEqualTo(lowUpdateValue);
        }

        log.debug("end testChangeValue");
    }

    @Test /* 가격 변화 테스트 */
    void testChangeVolume() {
        log.debug("start testChangeVolume");

        setPriceData();

        String url = "http://localhost:"+ port + "/api/v1/test/filed-value";

        String today = DateUtil.getToday();
        String stockCode = "035720";

        Optional<Price> optionalPrice = priceDao.findById(new PriceId(today, stockCode));
        if(optionalPrice.isEmpty()) {
            fail("Invalid date,stockCode: " + today + "," + stockCode);
        }

        int updateValue = 30000000;
        UpdateFieldValueDto.Request reqDto =
                new UpdateFieldValueDto.Request(stockCode, StockConstant.FieldName.VOLUME_FIELD, updateValue);

        // API 호출
        HttpEntity<UpdateFieldValueDto.Request> requestEntity = new HttpEntity<>(reqDto);
        ResponseEntity<Void> responseEntity = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Void.class);

        // API 결과 검증
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        optionalPrice = priceDao.findById(new PriceId(today, stockCode));
        if(optionalPrice.isPresent()) {
            assertThat(optionalPrice.get().getVolume()).isEqualTo(updateValue);
        }
        log.debug("end testChangeValue");
    }
}
