package securities.back.stocks.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter yyyymmddFormater = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String getToday() {
        LocalDateTime now = LocalDateTime.now();

        return now.format(yyyymmddFormater);
    }
}
