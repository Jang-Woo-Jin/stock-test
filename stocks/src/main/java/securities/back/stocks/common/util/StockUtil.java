package securities.back.stocks.common.util;

import securities.back.stocks.common.constant.StockConstant;

public class StockUtil {
    public static int getUpperLimitPrice(int price) {
        int price30 = applyTikPrice(price * 30 / 100);
        return applyTikPrice(price + price30);
    }

    public static int getLowerLimitPrice(int price) {
        int price30 = applyTikPrice(price * 30 / 100);
        return applyTikPrice(price - price30);
    }

    public static int applyTikPrice(int price) {
        for(int i = StockConstant.TikUnit.TIK_COUNT - 1; i >= 0 ; i--) {
            if(price > StockConstant.TikUnit.TIK_SIZE_LIST.get(i)) {
                int ticPrice = StockConstant.TikUnit.TIK_PRICE_LIST.get(i);
                return price / ticPrice * ticPrice;
            }
        }

        return price;
    }
}
