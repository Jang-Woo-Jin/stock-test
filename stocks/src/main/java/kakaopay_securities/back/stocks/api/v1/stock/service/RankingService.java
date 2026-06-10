package kakaopay_securities.back.stocks.api.v1.stock.service;

import kakaopay_securities.back.stocks.api.v1.stock.dto.service.ranking.ServiceRankingStocksDto;
import kakaopay_securities.back.stocks.api.v1.stock.dto.vo.StockRankVo;
import kakaopay_securities.back.stocks.common.constant.StockConstant;
import kakaopay_securities.back.stocks.common.exception.ApiException;
import kakaopay_securities.back.stocks.common.exception.error.CommonError;
import kakaopay_securities.back.stocks.domain.dao.PriceDao;
import kakaopay_securities.back.stocks.domain.model.Price;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {
    private final PriceDao priceDao;

    public ServiceRankingStocksDto.Response getStockRanking(ServiceRankingStocksDto.Request serReqDto) throws ApiException, InvocationTargetException, IllegalAccessException {
        if (StockConstant.RankingCategory.list.contains(serReqDto.getFilter())) {
            String filterStr = StockConstant.RankingCategory.FIELD_MAP.get(serReqDto.getFilter());
            Pageable page;
            if (StockConstant.Direction.ASC.equals(serReqDto.getDirection()))
                page = serReqDto.getPageRequestWithSort(Sort.by(Sort.Direction.ASC, filterStr));
            else
                page = serReqDto.getPageRequestWithSort(Sort.by(Sort.Direction.DESC, filterStr));

            Page<Price> pricePage = priceDao.findAll(page);

            List<StockRankVo> stockRankList = new ArrayList<>();
            for(Price item: pricePage.getContent()) {
                StockRankVo stockRank = new StockRankVo();
                BeanUtils.copyProperties(stockRank, item);
                stockRankList.add(stockRank);
            }

            ServiceRankingStocksDto.Response resDto = new ServiceRankingStocksDto.Response(
                    page.getPageNumber(), pricePage.getTotalElements(), stockRankList);
            return resDto;
        }
        else {
            log.error("INVALID_PARAMETER - filter: " + serReqDto.getFilter());
            throw new ApiException(CommonError.INVALID_PARAMETER, "올바르지 않은 필터입니다.");
        }
    }
}
