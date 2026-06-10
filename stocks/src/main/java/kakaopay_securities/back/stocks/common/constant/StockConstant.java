package kakaopay_securities.back.stocks.common.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StockConstant {
    public static class FieldName {
        public static final String VIEW_COUNT_FIELD = "stock_viewCount"; // 인기
        public static final String DIFF_PRICE_FIELD = "diffPrice"; // 전일 대비(상승/하락)
        public static final String VOLUME_FIELD = "volume"; // 거래량
        public static final String PRICE_FIELD = "price"; // 현재가
    }

    public static class RankingCategory {
        public static final Integer VIEW_COUNT = 1;
        public static final Integer DIFF_PRICE = 2;
        public static final Integer VOLUME = 3;

        public static final List<Integer> list = Arrays.asList(VIEW_COUNT, DIFF_PRICE, VOLUME);

        public static final HashMap<Integer, String> FIELD_MAP = new HashMap<>();
        static {
            FIELD_MAP.put(VIEW_COUNT, FieldName.VIEW_COUNT_FIELD);
            FIELD_MAP.put(DIFF_PRICE, FieldName.DIFF_PRICE_FIELD);
            FIELD_MAP.put(VOLUME, FieldName.VOLUME_FIELD);
        }
    }

    public static class Direction {
        public static final Integer ASC = 0; // 오름차순
        public static final Integer DESC = 1; // 내림차순
    }

    public static class UpdateFieldCategory {
        public static final List<String> list =
                Arrays.asList(FieldName.VIEW_COUNT_FIELD, FieldName.PRICE_FIELD, FieldName.VOLUME_FIELD);
    }

    public static class TikUnit {
        public static final List<Integer> TIK_SIZE_LIST = Arrays.asList(0, 2000, 5000, 20000, 50000, 200000, 500000);
        public static final List<Integer> TIK_PRICE_LIST = Arrays.asList(1, 5, 10, 50, 100, 500, 1000);

        public static final Integer TIK_COUNT = TIK_SIZE_LIST.size();
    }
}
