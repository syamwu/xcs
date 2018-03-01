package com.xcs.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private static String fromat = "yyyy-MM-dd";
    private static SimpleDateFormat sdf = new SimpleDateFormat(fromat);

    /**
     * 判断是否是当天
     * 
     * @param dateStr
     * @return
     */
    public static boolean isEqualToday(String dateStr) {
        return dateStr.equals(sdf.format(new Date()));
    }

    /**
     * 是否当天，格式需要为2017-7-20开头
     * 
     * @param dateStr
     * @return
     */
    public static boolean isToday(String dateStr) {
        try {
            int result = sdf.parse(sdf.format(new Date())).compareTo(sdf.parse(dateStr));
            if (result == 0) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 测试两天是否是同一天
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDate(String date1, String date2) {
        try {
            int result = sdf.parse(date2).compareTo(sdf.parse(date1));
            if (result == 0) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 求两个日期相差天数
     * 
     * @param d1
     * @param d2
     * @return long
     */
    public static int getDaysBetween(Calendar d1, Calendar d2) {
        if (d1.after(d2)) {
            java.util.Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }
        int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
        int y2 = d2.get(Calendar.YEAR);
        if (d1.get(Calendar.YEAR) != y2) {
            d1 = (Calendar) d1.clone();
            do {
                // 得到当年的实际天数
                days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
                d1.add(Calendar.YEAR, 1);
            } while (d1.get(Calendar.YEAR) != y2);
        }
        return days;
    }

    /**
     * 判断checkDate是否在beginDate和endDate之间(可以相等)
     * 
     * @param checkDate
     * @param beginDate
     * @param endDate
     * @return
     */
    public static boolean isBetweenTwoDay(String checkDate, String beginDate, String endDate) {
        return isBetweenTwoDay(checkDate, beginDate, endDate, fromat);
    }

    /**
     * 判断checkDate是否在beginDate和endDate之间(可以相等)，format为指定时间格式进行对比
     * 
     * @param checkDate
     * @param beginDate
     * @param endDate
     * @param format
     * @return
     */
    public static boolean isBetweenTwoDay(String checkDate, String beginDate, String endDate, String format) {
        SimpleDateFormat daf = new SimpleDateFormat(format);
        try {
            int beginCompare = daf.parse(checkDate).compareTo(daf.parse(beginDate));
            if (beginCompare != 1 && beginCompare != 0) {
                return false;
            }
            int endCompare = daf.parse(checkDate).compareTo(daf.parse(endDate));
            if (endCompare != -1 && beginCompare != 0) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;

    }

//    public static int getDaysBetween(String dateStr1, String dateStr2) {
//        Date date1 = null, date2 = null;
//
//        try {
//            date1 = org.apache.commons.lang3.time.DateUtils.parseDate(dateStr1, "yyyy-MM-dd");
//            date2 = org.apache.commons.lang3.time.DateUtils.parseDate(dateStr2, "yyyy-MM-dd");
//        } catch (ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return getDaysBetween(date1, date2);
//    }

//    public static int getDaysBetween(Date date1, Date date2) {
//        Calendar c1 = org.apache.commons.lang3.time.DateUtils.toCalendar(date1);
//        Calendar c2 = org.apache.commons.lang3.time.DateUtils.toCalendar(date2);
//        return getDaysBetween(c1, c2);
//    }

    public static String addDays(String dateStr, int count) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = df.parse(dateStr);
            return df.format(new Date(date.getTime() + count * 24 * 60 * 60 * 1000));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
}
