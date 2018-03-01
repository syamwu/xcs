package com.test.springboot.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    
    public static Format format3Math = new DecimalFormat("000");
    
    /**
     * 返回今天日期，格式为:20170706
     * 
     * @return
     */
    public static String getDate() {
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        String formatDate = format.format(new Date());
        return formatDate;
    }
    
    /**
     * 返回今天日期，格式为:20170706
     * 
     * @return
     */
    public static String getDate(Date date) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        String formatDate = format.format(date);
        return formatDate;
    }
    
    /**
     * 返回今天现在小时，24小时制
     * 
     * @return
     */
    public static Integer getHour() {
        DateFormat format = new SimpleDateFormat("HH");
        String formatDate = format.format(new Date());
        return Integer.parseInt(formatDate);
    }
    
    /**
     * 返回今天现在小时，24小时制
     * 
     * @return
     */
    public static Integer getHour(Date date) {
        DateFormat format = new SimpleDateFormat("HH");
        String formatDate = format.format(date);
        return Integer.parseInt(formatDate);
    }
    
    /**
     * 返回现在的分钟
     * 
     * @return
     */
    public static Integer getMin() {
        DateFormat format = new SimpleDateFormat("mm");
        String formatDate = format.format(new Date());
        return Integer.parseInt(formatDate);
    }
    
    /**
     * 返回现在的分钟
     * 
     * @return
     */
    public static Integer getMin(Date date) {
        DateFormat format = new SimpleDateFormat("mm");
        String formatDate = format.format(date);
        return Integer.parseInt(formatDate);
    }
    
    /**
     * 获取时间,格式2017070710
     * @return
     */
    public static String getDateHour(){
        DateFormat format = new SimpleDateFormat("yyyyMMddHH");
        String formatDate = format.format(new Date());
        return formatDate;
    }
    
    /**
     * 获取时间,格式201707071020
     * @return
     */
    public static String getDateMin(){
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        String formatDate = format.format(new Date());
        return formatDate;
    }
    
}
