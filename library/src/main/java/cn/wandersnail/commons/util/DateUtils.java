package cn.wandersnail.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    /**
     * 是否是今天
     *
     * @param time 时间戳
     */
    public static boolean isToday(long time) {
        return isSame(Calendar.DATE, System.currentTimeMillis(), time);
    }

    /**
     * 是否是今天
     *
     * @param date 日期
     */
    public static boolean isToday(Date date) {
        return isSame(Calendar.DATE, new Date(), date);
    }

    /**
     * 是否是昨天
     *
     * @param time 时间戳
     */
    public static boolean isYesterday(long time) {
        long day = getDay(System.currentTimeMillis(), -1);
        return isSame(Calendar.DATE, day, time);
    }

    /**
     * 是否是昨天
     *
     * @param date 日期
     */
    public static boolean isYesterday(Date date) {
        Date day = getDay(new Date(), -1);
        return isSame(Calendar.DATE, day, date);
    }

    /**
     * 判断两个日期是否在同一类型的范围内
     *
     * @param field 给定的日历字段。Calendar.DATE, Calendar.MONTH, Calendar.YEAR
     * @param date1 日期
     * @param date2 日期
     */
    public static boolean isSame(int field, long date1, long date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(date2);
        return isSame(field, cal1, cal2);
    }

    /**
     * 判断两个日期是否在同一类型的范围内
     *
     * @param field 给定的日历字段。Calendar.DATE, Calendar.MONTH, Calendar.YEAR
     * @param date1 日期
     * @param date2 日期
     */
    public static boolean isSame(int field, Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSame(field, cal1, cal2);
    }

    /**
     * 判断两个日期是否在同一类型的范围内
     *
     * @param field 给定的日历字段。Calendar.DATE, Calendar.MONTH, Calendar.YEAR
     * @param date1 日期
     * @param date2 日期
     */
    public static boolean isSame(int field, Calendar date1, Calendar date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        switch (field) {
            case Calendar.DATE:
                return date1.get(Calendar.ERA) == date2.get(Calendar.ERA) &&
                        date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                        date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR);
            case Calendar.MONTH:
                return date1.get(Calendar.ERA) == date2.get(Calendar.ERA) &&
                        date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                        date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH);
            case Calendar.YEAR:
                return date1.get(Calendar.ERA) == date2.get(Calendar.ERA) &&
                        date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR);
            default:
                return false;
        }
    }

    /**
     * 获取基于当前日期偏移后的日期
     *
     * @param date   当前日期
     * @param offset 偏移的天数，负数向前偏移，正数向后偏移
     */
    public static Date getDay(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, offset);
        return calendar.getTime();
    }

    /**
     * 获取基于当前日期偏移后的日期
     *
     * @param date   当前日期
     * @param offset 偏移的天数，负数向前偏移，正数向后偏移
     */
    public static long getDay(long date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.add(Calendar.DATE, offset);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取基于当前月份的偏移值
     */
    public static Date getMonth(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, offset);
        return calendar.getTime();
    }

    /**
     * 获取前一天日期
     */
    public static Date getPreviousDay(Date date) {
        return getDay(date, -1);
    }

    /**
     * 获取前一天日期
     */
    public static long getPreviousDay(long date) {
        return getDay(date, -1);
    }

    /**
     * 获取后一天日期
     */
    public static Date getNextDay(Date date) {
        return getDay(date, 1);
    }

    /**
     * 获取后一天日期
     */
    public static long getNextDay(long date) {
        return getDay(date, 1);
    }

    /**
     * 返回指定格式日期
     */
    public static String formatDate(Date date, String pattern, Locale locale) {
        return new SimpleDateFormat(pattern, locale).format(date);
    }

    /**
     * 返回指定格式日期
     */
    public static String formatDate(long time, String pattern, Locale locale) {
        return new SimpleDateFormat(pattern, locale).format(time);
    }

    /**
     * 返回指定格式日期
     */
    public static String formatDate(Date date, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }

    /**
     * 返回指定格式日期
     */
    public static String formatDate(long time, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(time);
    }

    /**
     * 获取当前日期对应是星期几
     *
     * @param date 当前日期
     * @return 与Calendar的星期字段相同，1表示星期天，以此类推
     */
    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取指定日期的当天毫秒数
     */
    public static long getMillisInDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY) * 3600000 + c.get(Calendar.MINUTE) * 60000 +
                c.get(Calendar.SECOND) * 1000 + c.get(Calendar.MILLISECOND);
    }

    /**
     * 将字符串日期解析成Date对象
     */
    public static Date parseStringDate(String date, String pattern, Locale locale) {
        try {
            return new SimpleDateFormat(pattern, locale).parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期格式不对");
        }
    }

    /**
     * 将字符串日期解析成Date对象
     */
    public static Date parseStringDate(String date, String pattern) {
        try {
            return new SimpleDateFormat(pattern, Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期格式不对");
        }
    }

    /**
     * 两个日期相差的天数(B-A)
     */
    public static int daysBetween(Date dateA, Date dateB) {
        Calendar a = getStartOfDay(dateA);
        Calendar b = getStartOfDay(dateB);
        return (int) ((b.getTimeInMillis() - a.getTimeInMillis()) / (24 * 3600000L));
    }

    /**
     * 两个日期相差的天数(B-A)
     */
    public static int daysBetween(long dateA, long dateB) {
        dateA = getStartOfDay(dateA);
        dateB = getStartOfDay(dateB);
        return (int) ((dateB - dateA) / (24 * 3600000L));
    }

    /**
     * 将此日期时间设置成0点整
     */
    public static Calendar getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 将此日期时间设置成0点整
     */
    public static long getStartOfDay(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 根据条件获取相应最大值，如此日期的月有多少天，Calendar.DATE
     */
    public static int getActualMaximum(Date date, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(field);
    }

    /**
     * 获取日期是当月的第几天
     */
    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE);
    }

    /**
     * 获取日期是当年的第几月，1月为0
     */
    public static int getMonthOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    /**
     * 获取日期所在月份的的第一天
     */
    public static Date getStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * 获取日期所在月份的的最后一天
     */
    public static Date getEndOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        return calendar.getTime();
    }

    /**
     * 获取日期所在年的最后一天
     */
    public static Date getEndOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        return calendar.getTime();
    }

    /**
     * 获取日期所在年的最后一天
     */
    public static Date getStartOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }
}
