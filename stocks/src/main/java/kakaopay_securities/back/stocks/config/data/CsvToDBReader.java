package kakaopay_securities.back.stocks.config.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.PostConstruct;
import kakaopay_securities.back.stocks.common.util.DateUtil;
import kakaopay_securities.back.stocks.common.util.StockUtil;
import kakaopay_securities.back.stocks.domain.dao.PriceDao;
import kakaopay_securities.back.stocks.domain.dao.StockDao;
import kakaopay_securities.back.stocks.domain.model.Price;
import kakaopay_securities.back.stocks.domain.model.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsvToDBReader {
    private final PriceDao priceDao;
    private final StockDao stockDao;

    //application 실행 시 db 데이터 구축
    @PostConstruct
    public void AddressInit() {
        if (!dataAlreadyLoaded(stockDao)) {
            loadDataFromCSV("sampleData.csv");
        }
    }

    private boolean dataAlreadyLoaded(JpaRepository repository) {
        return repository.count() > 0;
    }

    @Transactional
    public void loadDataFromCSV(String fileName) {
        try {
            // fileName 기준으로 파일을 가져온 후, FileReader 변환 -> 이걸 다시 CSVReader로 변환
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(
                    Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
            FileReader reader = new FileReader(file);
            CSVReader csvReader = new CSVReader(reader);

            csvReader.readNext(); // 한 줄은 column name
            String[] nextRecord;

            String today = DateUtil.getToday();
            Random random = new Random();

            List<Stock> stockList = new ArrayList<>();
            List<Price> priceList = new ArrayList<>();
            while ((nextRecord = csvReader.readNext()) != null) {
                String code = nextRecord[1];
                String name = nextRecord[2];

                // 조회수는 0~500 의 랜덤 값
                int viewCount = random.nextInt(500);

                Stock stockItem = new Stock(code, name, viewCount);
                stockList.add(stockItem);

                int price = Integer.parseInt(nextRecord[3]);

                // 전일종가는 현재가에서 -15% ~ + 15%의 임의값으로 계산
                int previousPrice = price + (int) Math.round(price * ((random.nextInt(15) * (random.nextBoolean() ? 1.0:-1.0)) / 100));
                int previousTikPrice = StockUtil.applyTikPrice(previousPrice);

                Price priceItem = new Price(today, stockItem,
                                            price, // 시가
                                            previousTikPrice, // 전일종가
                                            price - previousTikPrice, // 전일 대비
                                            price, // 시가
                                            price, // 고가
                                            price, // 저가
                                            StockUtil.getUpperLimitPrice(price), // 상한가
                                            StockUtil.getLowerLimitPrice(price), // 하한가
                                            random.nextInt(10000000) // 거래량
                                    );
                priceList.add(priceItem);
            }

            stockDao.saveAll(stockList);
            priceDao.saveAll(priceList);

            csvReader.close();
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
