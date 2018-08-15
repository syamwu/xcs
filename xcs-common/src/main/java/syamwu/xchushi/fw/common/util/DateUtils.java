package syamwu.xchushi.fw.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String dateToString(Date date) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateformat.format(date);
        String strDate = dateformat.format(date);
        return strDate;
    }
}
