package com.snail.commons.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    /**
     * 判断两个日期是否在同一类型的范围内
     * @param field 给定的日历字段。Calendar.DATE, Calendar.MONTH, Calendar.YEAR
     * @param date1 日期
     * @param date2 日期
     */
    @JvmStatic 
    fun isSame(field: Int, date1: Long, date2: Long): Boolean {
        val cal1 = Calendar.getInstance()
        cal1.timeInMillis = date1
        val cal2 = Calendar.getInstance()
        cal2.timeInMillis = date2
        return isSame(field, cal1, cal2)
    }

    /**
     * 判断两个日期是否在同一类型的范围内
     * @param field 给定的日历字段。Calendar.DATE, Calendar.MONTH, Calendar.YEAR
     * @param date1 日期
     * @param date2 日期
     */
    @JvmStatic 
    fun isSame(field: Int, date1: Date?, date2: Date?): Boolean {
        if (date1 == null || date2 == null) {
            return false
        }
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        return isSame(field, cal1, cal2)
    }

    /**
     * 判断两个日期是否在同一类型的范围内
     * @param field 给定的日历字段。Calendar.DATE, Calendar.MONTH, Calendar.YEAR
     * @param date1 日期
     * @param date2 日期
     */
    @JvmStatic 
    fun isSame(field: Int, date1: Calendar?, date2: Calendar?): Boolean {
        if (date1 == null || date2 == null) {
            return false
        }
        return when (field) {
            Calendar.DATE -> date1.get(Calendar.ERA) == date2.get(Calendar.ERA) && date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                    date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR)
            Calendar.MONTH -> date1.get(Calendar.ERA) == date2.get(Calendar.ERA) && date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                    date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
            Calendar.YEAR -> date1.get(Calendar.ERA) == date2.get(Calendar.ERA) && date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
            else -> false
        }
    }

    /**
     * 获取基于当前日期偏移后的日期
     *
     * @param date   当前日期
     * @param offset 偏移的天数，负数向前偏移，正数向后偏移
     */
    @JvmStatic 
    fun getDay(date: Date, offset: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, offset)
        return calendar.time
    }

    /**
     * 获取基于当前日期偏移后的日期
     *
     * @param date   当前日期
     * @param offset 偏移的天数，负数向前偏移，正数向后偏移
     */
    @JvmStatic 
    fun getDay(date: Long, offset: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        calendar.add(Calendar.DATE, offset)
        return calendar.timeInMillis
    }

    /**
     * 获取基于当前月份的偏移值
     */
    @JvmStatic 
    fun getMonth(date: Date, offset: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, offset)
        return calendar.time
    }

    /**
     * 获取前一天日期
     */
    @JvmStatic 
    fun getPreviousDay(date: Date): Date {
        return getDay(date, -1)
    }

    /**
     * 获取前一天日期
     */
    @JvmStatic 
    fun getPreviousDay(date: Long): Long {
        return getDay(date, -1)
    }

    /**
     * 获取后一天日期
     */
    @JvmStatic 
    fun getNextDay(date: Date): Date {
        return getDay(date, 1)
    }

    /**
     * 获取后一天日期
     */
    @JvmStatic 
    fun getNextDay(date: Long): Long {
        return getDay(date, 1)
    }

    /**
     * 返回指定格式日期
     */
    @JvmStatic 
    fun formatDate(date: Date, pattern: String): String {
        return SimpleDateFormat(pattern).format(date)
    }

    /**
     * 返回指定格式日期
     */
    @JvmStatic 
    fun formatDate(time: Long, pattern: String): String {
        return SimpleDateFormat(pattern).format(time)
    }

    /**
     * 获取当前日期对应是星期几
     *
     * @param date 当前日期
     * @return 与Calendar的星期字段相同，1表示星期天，以此类推
     */
    @JvmStatic 
    fun getDayOfWeek(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    /**
     * 获取指定日期的当天毫秒数
     */
    @JvmStatic 
    fun getMillisInDay(date: Date): Long {
        val c = Calendar.getInstance()
        c.time = date
        return (c.get(Calendar.HOUR_OF_DAY) * 3600000 + c.get(Calendar.MINUTE) * 60000 + c.get(Calendar.SECOND) * 1000 + c.get(Calendar.MILLISECOND)).toLong()
    }

    /**
     * 将字符串日期解析成Date对象
     * @return 解析成功返回Date对象，否则返回null
     */
    @JvmOverloads
    @JvmStatic 
    fun parseStringDate(date: String, pattern: String, locale: Locale = Locale.getDefault()): Date? {
        return try {
            SimpleDateFormat(pattern, locale).parse(date)
        } catch (e: ParseException) {
            null
        }

    }

    /**
     * 两个日期相差的天数(B-A)
     */
    @JvmStatic 
    fun daysBetween(dateA: Date, dateB: Date): Int {
        val a = getStartOfDay(dateA)
        val b = getStartOfDay(dateB)
        return ((b.timeInMillis - a.timeInMillis) / (24 * 3600000L)).toInt()
    }

    /**
     * 两个日期相差的天数(B-A)
     */
    @JvmStatic 
    fun daysBetween(dateA: Long, dateB: Long): Int {
        var dA = dateA
        var dB = dateB
        dA = getStartOfDay(dA)
        dB = getStartOfDay(dB)
        return ((dB - dA) / (24 * 3600000L)).toInt()
    }

    /**
     * 将此日期时间设置成0点整
     */
    @JvmStatic 
    fun getStartOfDay(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    /**
     * 将此日期时间设置成0点整
     */
    @JvmStatic 
    fun getStartOfDay(date: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date(date)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * 根据条件获取相应最大值，如此日期的月有多少天，Calendar.DATE
     */
    @JvmStatic 
    fun getActualMaximum(date: Date, field: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.getActualMaximum(field)
    }

    /**
     * 获取日期是当月的第几天
     */
    @JvmStatic 
    fun getDayOfMonth(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DATE)
    }

    /**
     * 获取日期是当年的第几月，1月为0
     */
    @JvmStatic 
    fun getMonthOfYear(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.MONTH)
    }

    /**
     * 获取日期所在月份的的第一天
     */
    @JvmStatic 
    fun getStartOfMonth(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DATE, 1)
        return calendar.time
    }

    /**
     * 获取日期所在月份的的最后一天
     */
    @JvmStatic 
    fun getEndOfMonth(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE))
        return calendar.time
    }

    /**
     * 获取日期所在年的最后一天
     */
    @JvmStatic 
    fun getEndOfYear(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.MONTH, 11)
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE))
        return calendar.time
    }

    /**
     * 获取日期所在年的最后一天
     */
    @JvmStatic 
    fun getStartOfYear(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.MONTH, 0)
        calendar.set(Calendar.DATE, 1)
        return calendar.time
    }
}
