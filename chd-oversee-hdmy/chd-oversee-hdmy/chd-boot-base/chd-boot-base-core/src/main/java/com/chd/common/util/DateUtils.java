package com.chd.common.util;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.chd.common.constant.SymbolConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 类描述：时间操作定义类
 *
 * @Author: 张代浩
 * @Date:2012-12-8 12:15:03
 * @Version 1.0
 */
@Slf4j
public class DateUtils extends PropertyEditorSupport {

    public static String FORMAT_DATETIME="yyyy-MM-dd HH:mm:ss";
    public static String FORMAT_DATE="yyyy-MM-dd";
    public static  String[] MONTH_SHORT_NAME_ARRAY=new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public static ThreadLocal<SimpleDateFormat> date_sdf = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(FORMAT_DATE);
        }
    };
    public static ThreadLocal<SimpleDateFormat> yyyyMMdd = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd");
        }
    };
    public static ThreadLocal<SimpleDateFormat> date_sdf_wz = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy年MM月dd日");
        }
    };
    public static ThreadLocal<SimpleDateFormat> time_sdf = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
    };
    public static ThreadLocal<SimpleDateFormat> yyyymmddhhmmss = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss");
        }
    };
    public static ThreadLocal<SimpleDateFormat> short_time_sdf = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm");
        }
    };
    public static ThreadLocal<SimpleDateFormat> datetimeFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(FORMAT_DATETIME);
        }
    };

    /**
     * 以毫秒表示的时间
     */
    private static final long DAY_IN_MILLIS = 24 * 3600 * 1000;
    private static final long HOUR_IN_MILLIS = 3600 * 1000;
    private static final long MINUTE_IN_MILLIS = 60 * 1000;
    private static final long SECOND_IN_MILLIS = 1000;

    /**
     * 指定模式的时间格式
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSdFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * 当前日历，这里用中国时间表示
     *
     * @return 以当地时区表示的系统当前日历
     */
    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }

    /**
     * 指定毫秒数表示的日历
     *
     * @param millis 毫秒数
     * @return 指定毫秒数表示的日历
     */
    public static Calendar getCalendar(long millis) {
        Calendar cal = Calendar.getInstance();
        // --------------------cal.setTimeInMillis(millis);
        cal.setTime(new Date(millis));
        return cal;
    }

    // ////////////////////////////////////////////////////////////////////////////
    // getDate
    // 各种方式获取的Date
    // ////////////////////////////////////////////////////////////////////////////

    /**
     * 当前日期
     *
     * @return 系统当前时间
     */
    public static Date getDate() {
        return new Date();
    }

    /**
     * 指定毫秒数表示的日期
     *
     * @param millis 毫秒数
     * @return 指定毫秒数表示的日期
     */
    public static Date getDate(long millis) {
        return new Date(millis);
    }

    /**
     * 时间戳转换为字符串
     *
     * @param time
     * @return
     */
    public static String timestamptoStr(Timestamp time) {
        Date date = null;
        if (null != time) {
            date = new Date(time.getTime());
        }
        return date2Str(date_sdf.get());
    }

    /**
     * 字符串转换时间戳
     *
     * @param str
     * @return
     */
    public static Timestamp str2Timestamp(String str) {
        Date date = str2Date(str, date_sdf.get());
        return new Timestamp(date.getTime());
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @param sdf
     * @return
     */
    public static Date str2Date(String str, SimpleDateFormat sdf) {
        if (null == str || "".equals(str)) {
            return null;
        }
        Date date = null;
        try {
            date = sdf.parse(str);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期转换为字符串
     *
     * @param dateSdf 日期格式
     * @return 字符串
     */
    public static String date2Str(SimpleDateFormat dateSdf) {
        synchronized (dateSdf) {
            Date date = getDate();
            if (null == date) {
                return null;
            }
            return dateSdf.format(date);
        }
    }

    /**
     * 格式化时间
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateformat(String date, String format) {
        SimpleDateFormat sformat = new SimpleDateFormat(format);
        Date nowDate = null;
        try {
            nowDate = sformat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sformat.format(nowDate);
    }

    /**
     * 日期转换为字符串
     *
     * @param date     日期
     * @param dateSdf 日期格式
     * @return 字符串
     */
    public static String date2Str(Date date, SimpleDateFormat dateSdf) {
        synchronized (dateSdf) {
            if (null == date) {
                return null;
            }
            return dateSdf.format(date);
        }
    }

    /**
     * 日期转换为字符串
     *
     * @param format 日期格式
     * @return 字符串
     */
    public static String getDate(String format) {
        Date date = new Date();
        if (null == date) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 指定毫秒数的时间戳
     *
     * @param millis 毫秒数
     * @return 指定毫秒数的时间戳
     */
    public static Timestamp getTimestamp(long millis) {
        return new Timestamp(millis);
    }

    /**
     * 以字符形式表示的时间戳
     *
     * @param time 毫秒数
     * @return 以字符形式表示的时间戳
     */
    public static Timestamp getTimestamp(String time) {
        return new Timestamp(Long.parseLong(time));
    }

    /**
     * 系统当前的时间戳
     *
     * @return 系统当前的时间戳
     */
    public static Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 当前时间，格式 yyyy-MM-dd HH:mm:ss
     *
     * @return 当前时间的标准形式字符串
     */
    public static String now() {
        return datetimeFormat.get().format(getCalendar().getTime());
    }

    /**
     * 指定日期的时间戳
     *
     * @param date 指定日期
     * @return 指定日期的时间戳
     */
    public static Timestamp getTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    /**
     * 指定日历的时间戳
     *
     * @param cal 指定日历
     * @return 指定日历的时间戳
     */
    public static Timestamp getCalendarTimestamp(Calendar cal) {
        // ---------------------return new Timestamp(cal.getTimeInMillis());
        return new Timestamp(cal.getTime().getTime());
    }

    public static Timestamp gettimestamp() {
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(dt);
        java.sql.Timestamp buydate = java.sql.Timestamp.valueOf(nowTime);
        return buydate;
    }

    // ////////////////////////////////////////////////////////////////////////////
    // getMillis
    // 各种方式获取的Millis
    // ////////////////////////////////////////////////////////////////////////////

    /**
     * 系统时间的毫秒数
     *
     * @return 系统时间的毫秒数
     */
    public static long getMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 指定日历的毫秒数
     *
     * @param cal 指定日历
     * @return 指定日历的毫秒数
     */
    public static long getMillis(Calendar cal) {
        // --------------------return cal.getTimeInMillis();
        return cal.getTime().getTime();
    }

    /**
     * 指定日期的毫秒数
     *
     * @param date 指定日期
     * @return 指定日期的毫秒数
     */
    public static long getMillis(Date date) {
        return date.getTime();
    }

    /**
     * 指定时间戳的毫秒数
     *
     * @param ts 指定时间戳
     * @return 指定时间戳的毫秒数
     */
    public static long getMillis(Timestamp ts) {
        return ts.getTime();
    }

    // ////////////////////////////////////////////////////////////////////////////
    // formatDate
    // 将日期按照一定的格式转化为字符串
    // ////////////////////////////////////////////////////////////////////////////

    /**
     * 默认方式表示的系统当前日期，具体格式：年-月-日
     *
     * @return 默认日期按“年-月-日“格式显示
     */
    public static String formatDate() {
        return date_sdf.get().format(getCalendar().getTime());
    }

    /**
     * 默认方式表示的系统当前日期，具体格式：yyyy-MM-dd HH:mm:ss
     *
     * @return 默认日期按“yyyy-MM-dd HH:mm:ss“格式显示
     */
    public static String formatDateTime() {
        return datetimeFormat.get().format(getCalendar().getTime());
    }

    /**
     * 获取时间字符串
     */
    public static String getDataString(SimpleDateFormat formatstr) {
        synchronized (formatstr) {
            return formatstr.format(getCalendar().getTime());
        }
    }

    /**
     * 指定日期的默认显示，具体格式：年-月-日
     *
     * @param cal 指定的日期
     * @return 指定日期按“年-月-日“格式显示
     */
    public static String formatDate(Calendar cal) {
        return date_sdf.get().format(cal.getTime());
    }

    /**
     * 指定日期的默认显示，具体格式：年-月-日
     *
     * @param date 指定的日期
     * @return 指定日期按“年-月-日“格式显示
     */
    public static String formatDate(Date date) {
        return date_sdf.get().format(date);
    }

    /**
     * 指定毫秒数表示日期的默认显示，具体格式：年-月-日
     *
     * @param millis 指定的毫秒数
     * @return 指定毫秒数表示日期按“年-月-日“格式显示
     */
    public static String formatDate(long millis) {
        return date_sdf.get().format(new Date(millis));
    }

    /**
     * 默认日期按指定格式显示
     *
     * @param pattern 指定的格式
     * @return 默认日期按指定格式显示
     */
    public static String formatDate(String pattern) {
        return getSdFormat(pattern).format(getCalendar().getTime());
    }

    /**
     * 指定日期按指定格式显示
     *
     * @param cal     指定的日期
     * @param pattern 指定的格式
     * @return 指定日期按指定格式显示
     */
    public static String formatDate(Calendar cal, String pattern) {
        return getSdFormat(pattern).format(cal.getTime());
    }

    /**
     * 指定日期按指定格式显示
     *
     * @param date    指定的日期
     * @param pattern 指定的格式
     * @return 指定日期按指定格式显示
     */
    public static String formatDate(Date date, String pattern) {
        return getSdFormat(pattern).format(date);
    }

    // ////////////////////////////////////////////////////////////////////////////
    // formatTime
    // 将日期按照一定的格式转化为字符串
    // ////////////////////////////////////////////////////////////////////////////

    /**
     * 默认方式表示的系统当前日期，具体格式：年-月-日 时：分
     *
     * @return 默认日期按“年-月-日 时：分“格式显示
     */
    public static String formatTime() {
        return time_sdf.get().format(getCalendar().getTime());
    }

    /**
     * 指定毫秒数表示日期的默认显示，具体格式：年-月-日 时：分
     *
     * @param millis 指定的毫秒数
     * @return 指定毫秒数表示日期按“年-月-日 时：分“格式显示
     */
    public static String formatTime(long millis) {
        return time_sdf.get().format(new Date(millis));
    }

    /**
     * 指定日期的默认显示，具体格式：年-月-日 时：分
     *
     * @param cal 指定的日期
     * @return 指定日期按“年-月-日 时：分“格式显示
     */
    public static String formatTime(Calendar cal) {
        return time_sdf.get().format(cal.getTime());
    }

    /**
     * 指定日期的默认显示，具体格式：年-月-日 时：分
     *
     * @param date 指定的日期
     * @return 指定日期按“年-月-日 时：分“格式显示
     */
    public static String formatTime(Date date) {
        return time_sdf.get().format(date);
    }

    // ////////////////////////////////////////////////////////////////////////////
    // formatShortTime
    // 将日期按照一定的格式转化为字符串
    // ////////////////////////////////////////////////////////////////////////////

    /**
     * 默认方式表示的系统当前日期，具体格式：时：分
     *
     * @return 默认日期按“时：分“格式显示
     */
    public static String formatShortTime() {
        return short_time_sdf.get().format(getCalendar().getTime());
    }

    /**
     * 指定毫秒数表示日期的默认显示，具体格式：时：分
     *
     * @param millis 指定的毫秒数
     * @return 指定毫秒数表示日期按“时：分“格式显示
     */
    public static String formatShortTime(long millis) {
        return short_time_sdf.get().format(new Date(millis));
    }

    /**
     * 指定日期的默认显示，具体格式：时：分
     *
     * @param cal 指定的日期
     * @return 指定日期按“时：分“格式显示
     */
    public static String formatShortTime(Calendar cal) {
        return short_time_sdf.get().format(cal.getTime());
    }

    /**
     * 指定日期的默认显示，具体格式：时：分
     *
     * @param date 指定的日期
     * @return 指定日期按“时：分“格式显示
     */
    public static String formatShortTime(Date date) {
        return short_time_sdf.get().format(date);
    }

    // ////////////////////////////////////////////////////////////////////////////
    // parseDate
    // parseCalendar
    // parseTimestamp
    // 将字符串按照一定的格式转化为日期或时间
    // ////////////////////////////////////////////////////////////////////////////

    /**
     * 根据指定的格式将字符串转换成Date 如输入：2003-11-19 11:20:20将按照这个转成时间
     *
     * @param src     将要转换的原始字符窜
     * @param pattern 转换的匹配格式
     * @return 如果转换成功则返回转换后的日期
     * @throws ParseException
     */
    public static Date parseDate(String src, String pattern)  {
        try {
            return getSdFormat(pattern).parse(src);
        }catch ( ParseException ex){
            throw new IllegalArgumentException(ex);
        }

    }



    public static Date textParseDate(String src, String pattern) throws ParseException {
        if(StringUtils.isBlank(src)){
            return null;
        }
        String source=src.trim();
        int len=source.length();
        int index=0;
        String yearStr=null;
        String monthStr=null;
        String dayStr=null;
        String hourStr=null;
        String minuteStr=null;
        String secondsStr=null;

        StringBuilder dateSB=new StringBuilder();
        while(index<len){
            char c=source.charAt(index);
            if(Character.isDigit(c)){
                dateSB.append(c);
            }else{
                if(c == ':'){
                    if(dateSB.length()==2){
                        if(hourStr==null){
                            hourStr=dateSB.toString();
                        }else if(minuteStr==null){
                            minuteStr=dateSB.toString();
                        }else if(secondsStr==null){
                            secondsStr=dateSB.toString();
                        }
                    }
                }else {
                    if(Character.isUpperCase(c) && (index+2)<len && Character.isLowerCase(source.charAt(index+1)) && Character.isLowerCase(source.charAt(index+2))){
                        char[] monthCharArray=new char[3];
                        monthCharArray[0]=c;
                        monthCharArray[1]=source.charAt(index+1);
                        monthCharArray[2]=source.charAt(index+2);
                        String monthName=String.valueOf(monthCharArray);
                        if(StringUtils.containsAny(monthName,MONTH_SHORT_NAME_ARRAY)){
                            for(int i=0;i<12;i++){
                                if(MONTH_SHORT_NAME_ARRAY[i].equals(monthName)){
                                    monthStr=i<10?("0"+i):(""+i);
                                    index+=2;
                                    break;
                                }
                            }
                        }
                    }else {
                        if (dateSB.length() == 4) {
                            yearStr = dateSB.toString();
                        } else if (dateSB.length() == 2) {
                            if (monthStr == null) {
                                monthStr = dateSB.toString();
                            } else if (dayStr == null) {
                                dayStr = dateSB.toString();
                            } else if (hourStr != null && minuteStr != null && secondsStr == null) {
                                secondsStr = dateSB.toString();
                            }
                        } else if (dateSB.length() == 1) {
                            if (monthStr == null) {
                                monthStr = "0" + dateSB.toString();
                            } else if (dayStr == null) {
                                dayStr = "0" + dateSB.toString();
                            }
                        }
                    }
                }
                dateSB=new StringBuilder();
            }
            index++;
        }

        try {
            if(dateSB.length()==4 && yearStr==null){
                yearStr=dateSB.toString();
            }else if(dateSB.length()==2){
                if(yearStr!=null && monthStr==null && dayStr==null){
                    monthStr=dateSB.toString();
                }else if(yearStr!=null && monthStr!=null && dayStr==null){
                    dayStr=dateSB.toString();
                }else if(hourStr!=null && minuteStr==null && secondsStr==null){
                    minuteStr=dateSB.toString();
                }else if(hourStr!=null && minuteStr!=null && secondsStr==null){
                    secondsStr=dateSB.toString();
                }
            }
            Date result=null;
            if(yearStr!=null && monthStr!=null && dayStr!=null) {
                if(hourStr!=null && minuteStr!=null && secondsStr!=null){
                    result= getSdFormat(FORMAT_DATETIME).parse(String.format("%s-%s-%s %s:%s:%s", yearStr, monthStr, dayStr,hourStr,minuteStr,secondsStr));
                }else {
                    result= getSdFormat(FORMAT_DATE).parse(String.format("%s-%s-%s", yearStr, monthStr, dayStr));
                }
            }else if(yearStr!=null && monthStr!=null){
                if(hourStr!=null && minuteStr!=null && secondsStr!=null){
                    result= getSdFormat(FORMAT_DATETIME).parse(String.format("%s-%s-01 %s:%s:%s", yearStr, monthStr,hourStr,minuteStr,secondsStr));
                }else {
                    result= getSdFormat(FORMAT_DATE).parse(String.format("%s-%s-01", yearStr, monthStr));
                }
            }else if(yearStr!=null){
                if(hourStr!=null && minuteStr!=null && secondsStr!=null){
                    result= getSdFormat(FORMAT_DATETIME).parse(String.format("%s-01-01 %s:%s:%s", yearStr,hourStr,minuteStr,secondsStr));
                }else {
                    result= getSdFormat(FORMAT_DATE).parse(String.format("%s-01-01", yearStr));
                }
            }
            if(result!=null){
               String date_string= formatDate(result,pattern);
               return DateUtils.parseDate(date_string,pattern);
            }
            return result;
        }catch (ParseException ex){
            log.error("时间转换异常:"+src,ex);
            return null;
        }

    }

    /**
     * 根据指定的格式将字符串转换成Date 如输入：2003-11-19 11:20:20将按照这个转成时间
     *
     * @param src     将要转换的原始字符窜
     * @param pattern 转换的匹配格式
     * @return 如果转换成功则返回转换后的日期
     * @throws ParseException
     */
    public static Calendar parseCalendar(String src, String pattern) throws ParseException {

        Date date = parseDate(src, pattern);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static String formatAddDate(String src, String pattern, int amount) throws ParseException {
        Calendar cal;
        cal = parseCalendar(src, pattern);
        cal.add(Calendar.DATE, amount);
        return formatDate(cal);
    }

    /**
     * 根据指定的格式将字符串转换成Date 如输入：2003-11-19 11:20:20将按照这个转成时间
     *
     * @param src     将要转换的原始字符窜
     * @param pattern 转换的匹配格式
     * @return 如果转换成功则返回转换后的时间戳
     * @throws ParseException
     */
    public static Timestamp parseTimestamp(String src, String pattern) throws ParseException {
        Date date = parseDate(src, pattern);
        return new Timestamp(date.getTime());
    }

    // ////////////////////////////////////////////////////////////////////////////
    // dateDiff
    // 计算两个日期之间的差值
    // ////////////////////////////////////////////////////////////////////////////

    /**
     * 计算两个时间之间的差值，根据标志的不同而不同
     *
     * @param flag   计算标志，表示按照年/月/日/时/分/秒等计算
     * @param calSrc 减数
     * @param calDes 被减数
     * @return 两个日期之间的差值
     */
    public static int dateDiff(char flag, Calendar calSrc, Calendar calDes) {

        long millisDiff = getMillis(calSrc) - getMillis(calDes);
        char year = 'y';
        char day = 'd';
        char hour = 'h';
        char minute = 'm';
        char second = 's';

        if (flag == year) {
            return (calSrc.get(Calendar.YEAR) - calDes.get(Calendar.YEAR));
        }

        if (flag == day) {
            return (int) (millisDiff / DAY_IN_MILLIS);
        }

        if (flag == hour) {
            return (int) (millisDiff / HOUR_IN_MILLIS);
        }

        if (flag == minute) {
            return (int) (millisDiff / MINUTE_IN_MILLIS);
        }

        if (flag == second) {
            return (int) (millisDiff / SECOND_IN_MILLIS);
        }

        return 0;
    }

    public static Long getCurrentTimestamp() {
        return Long.valueOf(DateUtils.yyyymmddhhmmss.get().format(new Date()));
    }

    /**
     * String类型 转换为Date, 如果参数长度为10 转换格式”yyyy-MM-dd“ 如果参数长度为19 转换格式”yyyy-MM-dd
     * HH:mm:ss“ * @param text String类型的时间值
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtil.isNotEmpty(text)) {
            try {
                int length10 = 10;
                int length19 = 19;
                if (text.indexOf(SymbolConstant.COLON) == -1 && text.length() == length10) {
                    setValue(DateUtils.date_sdf.get().parse(text));
                } else if (text.indexOf(SymbolConstant.COLON) > 0 && text.length() == length19) {
                    setValue(DateUtils.datetimeFormat.get().parse(text));
                } else {
                    throw new IllegalArgumentException("Could not parse date, date format is error ");
                }
            } catch (ParseException ex) {
                IllegalArgumentException iae = new IllegalArgumentException("Could not parse date: " + ex.getMessage());
                iae.initCause(ex);
                throw iae;
            }
        } else {
            setValue(null);
        }
    }

    public static int getYear() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(getDate());
        return calendar.get(Calendar.YEAR);
    }

}