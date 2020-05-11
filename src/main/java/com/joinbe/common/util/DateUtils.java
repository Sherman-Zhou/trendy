package com.joinbe.common.util;

import org.apache.commons.lang3.StringUtils;

import java.time.*;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author 01000114
 */
public class DateUtils {

    public static final String PATTERN_YEAR = "yyyy";
    public static final String PATTERN_YEARMOTH = "yyyy-MM";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_SDATE = "yyyyMMdd";
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_DATEALLTIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATEALLTIMEOTHER = "yyyyMMddHHmmss";
    public static final String PATTERN_MILLISECOND = "yyyyMMddHHmmssSSS";

    public static final String END_DATE_TIME =" 23:59:59";
    /**
     * 日期字符串转日期对象
     *
     * @param dateStr 日期字符串
     * @param format  日期格式
     * @return 日期对象
     */
    public static Date parseDate(String dateStr, String format) {
        try {
            Date date;
            DateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(dateStr);
            return date;
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static String formatDate(Date date, String format){
        return date == null? null: new SimpleDateFormat(format) .format(date);
    }


    public static String formatDate(Instant date, String format){
        return date == null? null: new SimpleDateFormat(format).format(Date.from(date));
    }
    /**
     * 日期对象转日期字符串
     *
     * @param date   日期对象
     * @param format 日期格式
     * @return 日期字符串
     */
    public static String parseString(Date date, String format) {
        if (null == date) {
            return null;
        } else {
            if (StringUtils.isEmpty(format)) {
                format = PATTERN_DATEALLTIME;
            }

            try {
                String dateStr;
                DateFormat dateFormat = new SimpleDateFormat(format);
                dateStr = dateFormat.format(date);
                return dateStr;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static Date getNow() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /**
     * 判断目标日期（YYYY-MM-DD）是否为过去日
     *
     * @return 结果
     */
    public static boolean isPastDate(Date targetDate){
        if (targetDate == null){
            return false;
        }
        // 获取当前年月日
        String strToday = DateUtils.parseString(DateUtils.getNow(), DateUtils.PATTERN_DATE);
        Date today = DateUtils.parseDate(strToday, DateUtils.PATTERN_DATE);

        // 检验日期
        String strTargetDate = DateUtils.parseString(targetDate, DateUtils.PATTERN_DATE);
        Date parseTargetDate = DateUtils.parseDate(strTargetDate, DateUtils.PATTERN_DATE);

        if (parseTargetDate != null && today != null){
            return parseTargetDate.before(today);
        } else {
            return false;
        }
    }
}
