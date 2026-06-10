package kakaopay_securities.back.stocks.api.v1.stock.service;


import kakaopay_securities.back.stocks.api.v1.stock.dto.service.test.ServiceUpdateFieldValueDto;
import kakaopay_securities.back.stocks.common.constant.StockConstant;
import kakaopay_securities.back.stocks.common.exception.ApiException;
import kakaopay_securities.back.stocks.common.exception.error.CommonError;
import kakaopay_securities.back.stocks.common.exception.error.ServiceError;
import kakaopay_securities.back.stocks.common.util.DateUtil;
import kakaopay_securities.back.stocks.common.util.StockUtil;
import kakaopay_securities.back.stocks.domain.dao.PriceDao;
import kakaopay_securities.back.stocks.domain.dao.StockDao;
import kakaopay_securities.back.stocks.domain.model.Price;
import kakaopay_securities.back.stocks.domain.model.Stock;
import kakaopay_securities.back.stocks.domain.model.multiple_key.PriceId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {
    private final StockDao stockDao;
    private final PriceDao priceDao;

    public void updateFieldValue(ServiceUpdateFieldValueDto.Request serReqDto) throws ApiException {
        Optional<Stock> optionalStock = stockDao.findById(serReqDto.getStockCode());
        if(optionalStock.isEmpty()){
            log.error("INVALID_PARAMETER - stockCode: " + serReqDto.getStockCode());
            throw new ApiException(CommonError.INVALID_PARAMETER, "올바르지 않은 종목코드입니다.");
        }

        Stock stockItem = optionalStock.get();
        if(StockConstant.UpdateFieldCategory.list.contains(serReqDto.getFieldName())) {
            if(StockConstant.FieldName.VIEW_COUNT_FIELD.equals(serReqDto.getFieldName())) {
                stockItem.setViewCount(serReqDto.getFieldValue());
                stockDao.save(stockItem);
            }
            else {
                Optional<Price> optionalPrice = priceDao.findById(new PriceId(DateUtil.getToday(), stockItem.getCode()));
                if(optionalPrice.isPresent()) {
                    Price priceItem = optionalPrice.get();
                    if(StockConstant.FieldName.PRICE_FIELD.equals(serReqDto.getFieldName())) {
                        int currentPrice = serReqDto.getFieldValue();
                        if(priceItem.getLowerLimitPrice() > currentPrice ||
                                priceItem.getUpperLimitPrice() < currentPrice) {
                            log.error("OVER_LIMIT_PRICE - stockCode: " + stockItem.getCode() + ", price: " + currentPrice);
                            throw new ApiException(ServiceError.OVER_LIMIT_PRICE, "가격이 상한가/하한가를 벗어났습니다.");
                        }

                        priceItem.setPrice(currentPrice);
                        priceItem.setDiffPrice(currentPrice - priceItem.getPreviousPrice());
                        if(priceItem.getHighPrice() < currentPrice) priceItem.setHighPrice(currentPrice);
                        if(priceItem.getLowPrice() > currentPrice) priceItem.setLowPrice(currentPrice);
                    }
                    else if(StockConstant.FieldName.VOLUME_FIELD.equals(serReqDto.getFieldName())) {
                        priceItem.setVolume(serReqDto.getFieldValue());
                    }
                    else {
                        log.error("INTERNAL_SERVER_ERROR - fieldName: " + serReqDto.getFieldName());
                        throw new ApiException(CommonError.INTERNAL_SERVER_ERROR, "정의되지 않은 필드명입니다.");
                    }
                    priceDao.save(priceItem);
                }
            }
        }
        else {
            log.error("INVALID_PARAMETER - fieldName: " + serReqDto.getFieldName());
            throw new ApiException(CommonError.INVALID_PARAMETER, "올바르지 않은 필드명입니다.");
        }
    }

    public void updateFieldValueRandom() {
        Random random = new Random();

        //update viewCount by Random
        List<Stock> stockList = stockDao.findAll();
        for(Stock item: stockList) {
            item.setViewCount(random.nextInt(500));
        }
        stockDao.saveAll(stockList);

        //update price and volume by Random
        List<Price> priceList = priceDao.findAll();
        for(Price item: priceList) {
            int newTikPrice = StockUtil.applyTikPrice(random.nextInt(300000));
            int newPreviousPrice = newTikPrice +
                    (int) Math.round(newTikPrice * ((random.nextInt(15) * (random.nextBoolean() ? 1.0:-1.0)) / 100));
            int newPreviousTikPrice = StockUtil.applyTikPrice(newPreviousPrice);

            item.setPrice(newTikPrice);
            item.setPreviousPrice(newPreviousTikPrice);
            item.setDiffPrice(newTikPrice - newPreviousTikPrice);
            item.setStartPrice(newTikPrice);
            item.setHighPrice(newTikPrice);
            item.setLowPrice(newTikPrice);
            item.setUpperLimitPrice(StockUtil.getUpperLimitPrice(newTikPrice));
            item.setLowerLimitPrice(StockUtil.getUpperLimitPrice(newTikPrice));
            item.setVolume(random.nextInt(10000000));
        }
        priceDao.saveAll(priceList);
    }
}
