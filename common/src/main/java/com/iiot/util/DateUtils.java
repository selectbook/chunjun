package com.iiot.util;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/4/16.
 */
public class DateUtils {

    public final static String TIME_STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final static SimpleDateFormat sdf = new SimpleDateFormat(TIME_STANDARD_FORMAT);

    /**
     * 转化格式
     *
     * @return
     */
    public static String dateToStr(String format, Date date) {
        if (StringUtils.isEmpty(format) || date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);

    }

    /**
     * 获得时间转化类
     * @param str 转化的字符串
     * @return
     */
    public static SimpleDateFormat getDateFormat(String str){
        if(StringUtils.isEmpty(str)){
            return sdf;
        }
        return new SimpleDateFormat(str);
    }

    /**
     * 获得时间转化类
     * @return
     */
    public static SimpleDateFormat getDateFormat(){
        return getDateFormat(null);
    }

    /**
     * 字符串转化为date
     * @param format
     * @param date
     * @return
     * @throws Exception
     */
    public static Date strToDate(String format, String date){
        if (StringUtils.isEmpty(format) || date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 字符串转化为date
     * @param date
     * @return
     * @throws Exception
     */
    public static Date strToDate(String date){
        return strToDate(TIME_STANDARD_FORMAT, date);
    }

    /**
     * 方法重载
     * @param date
     * @return
     */
    public static String dateToStr(Date date) {
        return dateToStr(TIME_STANDARD_FORMAT, date);
    }

    /*
	 * 把秒数转为页面需要的 天，小时，
	 *
	 */
    public static String separatedFormatByTime(Date startTime, Date endTime) {
        if(startTime == null || endTime == null){
            return "";
        }
        String result = "";
        long diff = endTime.getTime() - startTime.getTime();
        diff = diff / 1000;
        long day = 0;
        long hour = 0;
        long min = 0;
        day = diff / (24 * 60 * 60);
        hour = (diff / (60 * 60) - day * 24);
        min = ((diff / 60) - day * 24 * 60 - hour * 60);
        if (day > 0) {
            result = day + "天" + hour + "小时" + min + "分";
        } else if (hour > 0) {
            result = hour + "小时" + min + "分";
        } else if (min > 0) {
            result = min + "分";
        }
        return result;
    }

    /**
     * 计算时间
     * @param startTime
     * @param endTime
     * @return
     */
    public static long calculationTime(Date startTime, Date endTime) {
        if(startTime == null || endTime == null){
            return 0;
        }
        return (endTime.getTime() - startTime.getTime());
    }

    /**
     * 计算间隔时间返回多少秒
     * @param startTime
     * @param endTime
     * @return
     */
    public static int calculationTimeBySecond(Date startTime, Date endTime) {
        return (int) (calculationTime(startTime,endTime) / 1000);
    }

    /*
	 * 把秒数转为页面需要的 天，小时，
	 *
	 */
    public static String separatedFormatByTime(long time) {
        String result = "";
        if(time == 0){
            return result;
        }
        long diff = time / 1000;
        long day = 0;
        long hour = 0;
        long min = 0;
        day = diff / (24 * 60 * 60);
        hour = (diff / (60 * 60) - day * 24);
        min = ((diff / 60) - day * 24 * 60 - hour * 60);
        if (day > 0) {
            result = day + "天" + hour + "小时" + min + "分";
        } else if (hour > 0) {
            result = hour + "小时" + min + "分";
        } else if (min > 0) {
            result = min + "分";
        }
        return result;
    }
    
    /**
     * 精确到秒
     * @param time
     * @return
     */
    public static String separatedFormatByTime2Sec(long time) {
        String result = "";
        if(time == 0){
            return result;
        }
        long diff = time / 1000;
        long day = 0;
        long hour = 0;
        long min = 0;
        day = diff / (24 * 60 * 60);
        hour = (diff / (60 * 60) - day * 24);
        min = ((diff / 60) - day * 24 * 60 - hour * 60);
        if (day > 0) {
            result = day + "天" + hour + "小时" + min + "分";
        } else if (hour > 0) {
            result = hour + "小时" + min + "分";
        } else if (min > 0) {
            result = min + "分";
        }
        return result;
    }
    
    public static String getDistanceTimeL(long oldTime, long newTime) {

		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		long diff;

		if (oldTime < newTime) {
			diff = newTime - oldTime;
		} else {
			diff = oldTime - newTime;
		}
		day = diff / (24 * 60 * 60 * 1000);
		hour = (diff / (60 * 60 * 1000) - day * 24);
		min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
		sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		return day + "天" + hour + "小时" + min + "分"+sec+"秒";
	}


    /**
     * 查询几天后的日期
     * @param date
     * @param days
     * @return
     */
    public static Date dateByDays(Date date, int days) {
        // 加上新加的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 不是直接用月，而是用日*31
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    /**
     * 得到几天前的集合
     * @param date
     * @param day
     * @return
     */
    public static List<String> getDateAfter(Date date, int day) {
        List<String> list = new LinkedList<>();
        for (int i = 0; i < day; i++) {
            Date days = dateByDays(date, -i);
            list.add(dateToStr("yyyyMMdd",days));
        }
        return list;
    }

    public static List<String> getDateAfter(Date date, int day, String str) {
        List<String> list = new LinkedList<>();
        for (int i = 0; i < day; i++) {
            Date days = dateByDays(date, -i);
            list.add(dateToStr(str, days));
        }
        return list;
    }

    /**
     * 2个时间相隔多少天
     * @param beginTime
     * @param endTime
     * @return 相隔天数的集合
     */
    public static List<String> getDateAfter(Date beginTime, Date endTime) {
        int between_days = (int) ((endTime.getTime() - beginTime.getTime()) / (1000 * 3600 * 24));
        return getDateAfter(endTime, between_days + 1, "yyyy-MM-dd");
    }

    /**
     * 2个时间相隔多少天
     * @param begin
     * @param end
     * @return 相隔天数的集合
     */
    public static List<String> getDateAfter(String begin, String end) {
        Date beginTime = strToDate("yyyy-MM-dd", begin);
        Date endTime = strToDate("yyyy-MM-dd", end);
        return getDateAfter(beginTime, endTime);
    }

    /**
     * 添加指定月份方法
     * @param date
     * @param month
     * @return
     */
    public static Date addMonthTime(Date date, int month) {
        if(date == null || month == 0){
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 不是直接用月，而是用日*31
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }


    /**
     * 获得下个小时的间隔时间
     * @param date
     * @return
     */
    private static long getHourNextInterval(Date date) {
        if (date == null) {
            return 0;
        }
        String str = DateUtils.dateToStr("mm:ss", date);
        String[] arr = str.split(":");
        int min = 59 - Integer.parseInt(arr[0]);
        int second = 60 - Integer.parseInt(arr[1]);
        return (min * 60 + second);
    }

    /**
     * 根据日期和间隔算出中间相隔的时间段(比较特殊的算法)
     * @param date
     * @param time
     * @return
     */
    public static Map<String, Long> getDateStrList(Date date, long time) {
        Map<String, Long> map = new HashMap<>();
        if (date == null || time == 0) {
            return map;
        }
        //获得小时
        String str = DateUtils.dateToStr("HH", date);
        int strInt = Integer.parseInt(str);
        long hourInterval = getHourNextInterval(date);
        if (hourInterval >= time) {
            map.put(strInt + "", time);
        } else {
            map.put(strInt + "", hourInterval);
            long timeInterval = time - hourInterval;
            int except = (int) (timeInterval / (60 * 60));
            int remainder = (int) (timeInterval % (60 * 60));
            for (int i = 1; i < except + 1; i++) {
                String key = dateHourOperation(strInt, i) + "";
                if(map.containsKey(key)){
                    long value = map.get(key);
                    value = value + (60*60);
                    map.put(key, value);
                }else {
                    map.put(key, (60*60L));
                }
            }
            String lastKey = dateHourOperation(strInt, except + 1) + "";
            map.put(lastKey, (long) remainder);
        }
        return map;
    }

    /**
     * 小时加减运算
     * @param strInt
     * @param interval
     * @return
     */
    private static int dateHourOperation(int strInt, int interval) {
        int i = strInt + interval;
        if (i < 24) {
            return i;
        } else {
            int except = i / 24;
            return (i - 24 * except);
        }
    }

    /**
     * 时间显示格式
     * @param ctime
     * @return
     */
    public static String showTime(Date ctime) {
        String r = "";
        if (ctime == null){
            return r;
        }
        long nowtimelong = System.currentTimeMillis();
        long ctimelong = ctime.getTime();
        long result = Math.abs(nowtimelong - ctimelong);
        if(result < 60000){// 一分钟内
            long seconds = result / 1000;
            if(seconds == 0){
                r = "刚刚";
            }else{
                r = seconds + "秒前";
            }
        }else if (result >= 60000 && result < 3600000){// 一小时内
            long seconds = result / 60000;
            r = seconds + "分钟前";
        }else if (result >= 3600000 && result < 86400000){// 一天内
            long seconds = result / 3600000;
            r = seconds + "小时前";
        }else if (result >= 86400000 && result < 1702967296){// 三十天内
            long seconds = result / 86400000;
            r = seconds + "天前";
        } else {// 日期格式
            r = dateToStr("MM-dd",ctime);
        }
        return r;
    }

    /**
     * 电动车显示日期方法
     * @param startDate
     * @return
     */
    public static String showTimeByECar(Date startDate) {
        String result = "";
        //获取当地时区
        int offSet = Calendar.getInstance().getTimeZone().getRawOffset();
        long today = (System.currentTimeMillis() + offSet) / 86400000;
        long start = (startDate.getTime() + offSet) / 86400000;
        int interval = (int) (start - today);
        String dateStr = dateToStr("HH:mm:ss", startDate);
        if (interval == -2) {
            result = "前天 " + dateStr;
        } else if (interval == -1) {
            result = "昨天 " + dateStr;
        } else if (interval == 0) {
            result = "今天 " + dateStr;
        } else {
            result = dateToStr(startDate);
        }
        return result;
    }

    /**
     * 电动车显示日期方法
     * @param ctime
     * @return
     */
    public static String showTimeByECar(String ctime) {
        String r = "";
        if (StringUtils.isEmpty(ctime)){
            return r;
        }
        Date ctimeDate = strToDate(ctime);
        return showTimeByECar(ctimeDate);
    }


    /**
     * 时间显示格式
     * @param ctime
     * @return
     */
    public static String showTime(String ctime) {

        String r = "";
        if (ctime == null){
            return r;
        }
        Date ctimeDate = strToDate(ctime);
        return showTime(ctimeDate);
    }

    /**
     * 现在离凌晨还有多长时间(秒)
     * @return
     */
    public static long toMorningTime(){
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now();
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        LocalDateTime tomorrowMidnight = todayMidnight.plusDays(1);
        return TimeUnit.NANOSECONDS.toSeconds(Duration.between(LocalDateTime.now(), tomorrowMidnight).toNanos());
    }


}
