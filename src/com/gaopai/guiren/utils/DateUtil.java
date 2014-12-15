/*
 * 
 */
package com.gaopai.guiren.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.R;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.text.format.Time;

// TODO: Auto-generated Javadoc
/**
 * 描述：日期处理类.
 * 
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {

	/** 时间日期格式化到年月日时分秒. */
	public static String dateFormatYMDHMS = "yyyy-MM-dd HH:mm:ss";

	/** 时间日期格式化到年月日. */
	public static String dateFormatYMD = "yyyy-MM-dd";

	/** 时间日期格式化到年月. */
	public static String dateFormatYM = "yyyy-MM";

	/** 时间日期格式化到年月日时分. */
	public static String dateFormatYMDHM = "yyyy-MM-dd HH:mm";

	/** 时间日期格式化到年月日时分. */
	public static String dateFormatMDHM = "MM-dd HH:mm";

	/** 时间日期格式化到月日. */
	public static String dateFormatMD = "MM/dd";

	/** 时分秒. */
	public static String dateFormatHMS = "HH:mm:ss";

	/** 时分. */
	public static String dateFormatHM = "HH:mm";

	/**
	 * 描述：String类型的日期时间转化为Date类型.
	 * 
	 * @param strDate
	 *            String形式的日期时间
	 * @param format
	 *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return Date Date类型日期时间
	 */
	public static Date getDateByFormat(String strDate, String format) {
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = mSimpleDateFormat.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 描述：获取偏移之后的Date.
	 * 
	 * @param date
	 *            日期时间
	 * @param calendarField
	 *            Calendar属性，对应offset的值，
	 *            如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
	 * @param offset
	 *            偏移(值大于0,表示+,值小于0,表示－)
	 * @return Date 偏移之后的日期时间
	 */
	public Date getDateByOffset(Date date, int calendarField, int offset) {
		Calendar c = new GregorianCalendar();
		try {
			c.setTime(date);
			c.add(calendarField, offset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c.getTime();
	}

	/**
	 * 描述：获取指定日期时间的字符串(可偏移).
	 * 
	 * @param strDate
	 *            String形式的日期时间
	 * @param format
	 *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @param calendarField
	 *            Calendar属性，对应offset的值，
	 *            如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
	 * @param offset
	 *            偏移(值大于0,表示+,值小于0,表示－)
	 * @return String String类型的日期时间
	 */
	public static String getStringByOffset(String strDate, String format, int calendarField, int offset) {
		String mDateTime = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			c.setTime(mSimpleDateFormat.parse(strDate));
			c.add(calendarField, offset);
			mDateTime = mSimpleDateFormat.format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return mDateTime;
	}

	/**
	 * 描述：Date类型转化为String类型(可偏移).
	 * 
	 * @param date
	 *            the date
	 * @param format
	 *            the format
	 * @param calendarField
	 *            the calendar field
	 * @param offset
	 *            the offset
	 * @return String String类型日期时间
	 */
	public static String getStringByOffset(Date date, String format, int calendarField, int offset) {
		String strDate = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			c.setTime(date);
			c.add(calendarField, offset);
			strDate = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}

	/**
	 * 描述：Date类型转化为String类型.
	 * 
	 * @param date
	 *            the date
	 * @param format
	 *            the format
	 * @return String String类型日期时间
	 */
	public static String getStringByFormat(Date date, String format) {
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
		String strDate = null;
		try {
			strDate = mSimpleDateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}

	/**
	 * 描述：获取指定日期时间的字符串,用于导出想要的格式.
	 * 
	 * @param strDate
	 *            String形式的日期时间，必须为yyyy-MM-dd HH:mm:ss格式
	 * @param format
	 *            输出格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return String 转换后的String类型的日期时间
	 */
	public static String getStringByFormat(String strDate, String format) {
		String mDateTime = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(dateFormatYMDHMS);
			c.setTime(mSimpleDateFormat.parse(strDate));
			SimpleDateFormat mSimpleDateFormat2 = new SimpleDateFormat(format);
			mDateTime = mSimpleDateFormat2.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mDateTime;
	}

	public static String getStringByFormat(String strDate, String format1, String format2) {
		String mDateTime = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format1);
			c.setTime(mSimpleDateFormat.parse(strDate));
			SimpleDateFormat mSimpleDateFormat2 = new SimpleDateFormat(format2);
			mDateTime = mSimpleDateFormat2.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mDateTime;
	}

	/**
	 * 描述：获取milliseconds表示的日期时间的字符串.
	 * 
	 * @param milliseconds
	 *            the milliseconds
	 * @param format
	 *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return String 日期时间字符串
	 */
	public static String getStringByFormatMilli(long milliseconds, String format) {
		String thisDateTime = null;
		try {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			thisDateTime = mSimpleDateFormat.format(milliseconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return thisDateTime;
	}

	/**
	 * 描述：获取时间戳表示的日期时间的字符串.
	 * 
	 * @param milliseconds
	 *            the milliseconds
	 * @param format
	 *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return String 日期时间字符串
	 */
	public static String getStringByFormat(long milliseconds, String format) {
		String thisDateTime = null;
		try {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			thisDateTime = mSimpleDateFormat.format(new Date(milliseconds * 1000L));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return thisDateTime;
	}

	/**
	 * 描述：获取表示当前日期时间的字符串.
	 * 
	 * @param format
	 *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return String String类型的当前日期时间
	 */
	public static String getCurrentDate(String format) {
		String curDateTime = null;
		try {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			Calendar c = new GregorianCalendar();
			curDateTime = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return curDateTime;

	}

	/**
	 * 描述：获取表示当前日期时间的字符串(可偏移).
	 * 
	 * @param format
	 *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @param calendarField
	 *            Calendar属性，对应offset的值，
	 *            如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
	 * @param offset
	 *            偏移(值大于0,表示+,值小于0,表示－)
	 * @return String String类型的日期时间
	 */
	public static String getCurrentDateByOffset(String format, int calendarField, int offset) {
		String mDateTime = null;
		try {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			Calendar c = new GregorianCalendar();
			c.add(calendarField, offset);
			mDateTime = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mDateTime;

	}

	/**
	 * 描述：计算两个日期所差的天数.
	 * 
	 * @param date1
	 *            第一个时间的毫秒表示
	 * @param date2
	 *            第二个时间的毫秒表示
	 * @return int 所差的天数
	 */
	public static int getOffectDay(long date1, long date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(date2);
		// 先判断是否同年
		int y1 = calendar1.get(Calendar.YEAR);
		int y2 = calendar2.get(Calendar.YEAR);
		int d1 = calendar1.get(Calendar.DAY_OF_YEAR);
		int d2 = calendar2.get(Calendar.DAY_OF_YEAR);
		int maxDays = 0;
		int day = 0;
		if (y1 - y2 > 0) {
			maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_YEAR);
			day = d1 - d2 + maxDays;
		} else if (y1 - y2 < 0) {
			maxDays = calendar1.getActualMaximum(Calendar.DAY_OF_YEAR);
			day = d1 - d2 - maxDays;
		} else {
			day = d1 - d2;
		}
		return day;
	}

	/**
	 * 描述：计算两个日期所差的小时数.
	 * 
	 * @param date1
	 *            第一个时间的毫秒表示
	 * @param date2
	 *            第二个时间的毫秒表示
	 * @return int 所差的小时数
	 */
	public static int getOffectHour(long date1, long date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(date2);
		int h1 = calendar1.get(Calendar.HOUR_OF_DAY);
		int h2 = calendar2.get(Calendar.HOUR_OF_DAY);
		int h = 0;
		int day = getOffectDay(date1, date2);
		h = h1 - h2 + day * 24;
		return h;
	}

	/**
	 * 描述：计算两个日期所差的分钟数.
	 * 
	 * @param date1
	 *            第一个时间的毫秒表示
	 * @param date2
	 *            第二个时间的毫秒表示
	 * @return int 所差的分钟数
	 */
	public static int getOffectMinutes(long date1, long date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(date2);
		int m1 = calendar1.get(Calendar.MINUTE);
		int m2 = calendar2.get(Calendar.MINUTE);
		int h = getOffectHour(date1, date2);
		int m = 0;
		m = m1 - m2 + h * 60;
		return m;
	}

	/**
	 * 描述：获取本周一.
	 * 
	 * @param format
	 *            the format
	 * @return String String类型日期时间
	 */
	public static String getFirstDayOfWeek(String format) {
		return getDayOfWeek(format, Calendar.MONDAY);
	}

	/**
	 * 描述：获取本周日.
	 * 
	 * @param format
	 *            the format
	 * @return String String类型日期时间
	 */
	public static String getLastDayOfWeek(String format) {
		return getDayOfWeek(format, Calendar.SUNDAY);
	}

	/**
	 * 描述：获取本周的某一天.
	 * 
	 * @param format
	 *            the format
	 * @param calendarField
	 *            the calendar field
	 * @return String String类型日期时间
	 */
	private static String getDayOfWeek(String format, int calendarField) {
		String strDate = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			int week = c.get(Calendar.DAY_OF_WEEK);
			if (week == calendarField) {
				strDate = mSimpleDateFormat.format(c.getTime());
			} else {
				int offectDay = calendarField - week;
				if (calendarField == Calendar.SUNDAY) {
					offectDay = 7 - Math.abs(offectDay);
				}
				c.add(Calendar.DATE, offectDay);
				strDate = mSimpleDateFormat.format(c.getTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}

	/**
	 * 描述：获取本月第一天.
	 * 
	 * @param format
	 *            the format
	 * @return String String类型日期时间
	 */
	public static String getFirstDayOfMonth(String format) {
		String strDate = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			// 当前月的第一天
			c.set(Calendar.DAY_OF_MONTH, 1);
			strDate = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;

	}

	/**
	 * 描述：获取本月最后一天.
	 * 
	 * @param format
	 *            the format
	 * @return String String类型日期时间
	 */
	public static String getLastDayOfMonth(String format) {
		String strDate = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			// 当前月的最后一天
			c.set(Calendar.DATE, 1);
			c.roll(Calendar.DATE, -1);
			strDate = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}

	/**
	 * 描述：获取表示当前日期的0点时间毫秒数.
	 * 
	 * @return the first time of day
	 */
	public static long getFirstTimeOfDay() {
		Date date = null;
		try {
			String currentDate = getCurrentDate(dateFormatYMD);
			date = getDateByFormat(currentDate + " 00:00:00", dateFormatYMDHMS);
			return date.getTime();
		} catch (Exception e) {
		}
		return -1;
	}

	/**
	 * 描述：获取表示当前日期24点时间毫秒数.
	 * 
	 * @return the last time of day
	 */
	public static long getLastTimeOfDay() {
		Date date = null;
		try {
			String currentDate = getCurrentDate(dateFormatYMD);
			date = getDateByFormat(currentDate + " 24:00:00", dateFormatYMDHMS);
			return date.getTime();
		} catch (Exception e) {
		}
		return -1;
	}

	/**
	 * 描述：判断是否是闰年()
	 * <p>
	 * (year能被4整除 并且 不能被100整除) 或者 year能被400整除,则该年为闰年.
	 * 
	 * @param year
	 *            年代（如2012）
	 * @return boolean 是否为闰年
	 */
	public static boolean isLeapYear(int year) {
		if ((year % 4 == 0 && year % 400 != 0) || year % 400 == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 描述：根据时间返回格式化后的时间的描述. 小于1小时显示多少分钟前 大于1小时显示今天＋实际日期，大于今天全部显示实际时间
	 * 
	 * @param strDate
	 *            the str date
	 * @param outFormat
	 *            the out format
	 * @return the string
	 */
	public static String formatDateStr2Desc(String strDate, String outFormat) {

		DateFormat df = new SimpleDateFormat(dateFormatYMDHMS);
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		try {
			c2.setTime(df.parse(strDate));
			c1.setTime(new Date());
			int d = getOffectDay(c1.getTimeInMillis(), c2.getTimeInMillis());
			if (d == 0) {
				int h = getOffectHour(c1.getTimeInMillis(), c2.getTimeInMillis());
				if (h > 0) {
					return "今天" + getStringByFormat(strDate, dateFormatHM);
					// return h + "小时前";
				} else if (h < 0) {
					// return Math.abs(h) + "小时后";
				} else if (h == 0) {
					int m = getOffectMinutes(c1.getTimeInMillis(), c2.getTimeInMillis());
					if (m > 0) {
						return m + "分钟前";
					} else if (m < 0) {
						// return Math.abs(m) + "分钟后";
					} else {
						return "刚刚";
					}
				}

			} else if (d > 0) {
				if (d == 1) {
					// return "昨天"+getStringByFormat(strDate,outFormat);
				} else if (d == 2) {
					// return "前天"+getStringByFormat(strDate,outFormat);
				}
			} else if (d < 0) {
				if (d == -1) {
					// return "明天"+getStringByFormat(strDate,outFormat);
				} else if (d == -2) {
					// return "后天"+getStringByFormat(strDate,outFormat);
				} else {
					// return Math.abs(d) +
					// "天后"+getStringByFormat(strDate,outFormat);
				}
			}

			String out = getStringByFormat(strDate, outFormat);
			if (!StringUtils.isEmpty(out)) {
				return out;
			}
		} catch (Exception e) {
		}

		return strDate;
	}

	public static String formatDateStr3Desc(String strDate, String outFormat) {

		try {
			DateFormat df = new SimpleDateFormat(outFormat);
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			Date date = df.parse(strDate);
			c2.setTime(date);
			c1.setTime(new Date());
			int d = getOffectDay(c1.getTimeInMillis(), c2.getTimeInMillis());
			int y2 = c2.get(Calendar.YEAR);
			int y1 = c1.get(Calendar.YEAR);
			if (d == 0) {
				return getStringByFormat(date, dateFormatHM);
			} else if (y1 == y2) {
				return getStringByFormat(date, "MM-dd");
			} else {
				return getStringByFormat(date, dateFormatYMD);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 取指定日期为星期几.
	 * 
	 * @param strDate
	 *            指定日期
	 * @param inFormat
	 *            指定日期格式
	 * @return String 星期几
	 */
	public static String getWeekNumber(String strDate, String inFormat) {
		String week = "星期日";
		Calendar calendar = new GregorianCalendar();
		DateFormat df = new SimpleDateFormat(inFormat);
		try {
			calendar.setTime(df.parse(strDate));
		} catch (Exception e) {
			return "错误";
		}
		int intTemp = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		switch (intTemp) {
		case 0:
			week = "星期日";
			break;
		case 1:
			week = "星期一";
			break;
		case 2:
			week = "星期二";
			break;
		case 3:
			week = "星期三";
			break;
		case 4:
			week = "星期四";
			break;
		case 5:
			week = "星期五";
			break;
		case 6:
			week = "星期六";
			break;
		}
		return week;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		System.out.println(formatDateStr2Desc("2012-3-2 12:2:20", "MM月dd日  HH:mm"));
	}

	/**
	 * 
	 * @param date是为则默认今天日期
	 *            、可自行设置“2013-06-03”格式的日期
	 * 
	 * @return 返回1是星期日、2是星期一、3是星期二、4是星期三、5是星期四、6是星期五、7是星期六
	 */
	public static int getDayofweek(String date) {
		Calendar cal = Calendar.getInstance();
		if (date.equals("")) {
			cal.setTime(new Date(System.currentTimeMillis()));
		} else {
			cal.setTime(new Date(getDateByStr2(date).getTime()));
		}
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 格式化时间yyyy-MM-dd字符串为Date
	 * 
	 * @param dd
	 * @return
	 */
	public static Date getDateByStr2(String dd) {

		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try {
			date = sd.parse(dd);
		} catch (ParseException e) {
			date = null;
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 比较当前时间
	 * 
	 * @param date
	 *            时间
	 * @return ture 大于当前时间
	 */
	public static boolean compare2NowDate(String date) {
		Date d1 = getDateByStr2(date);
		Date d2 = new Date();

		if (d1.getTime() > d2.getTime()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 比较两个时间（hh:mm:ss）大小
	 * 
	 * @param date1
	 * @param date2
	 * @return true 前者大 false 后者大
	 */
	public static boolean compare2Date(String date1, String date2) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = sdf.parse(date1 + ":00");
			d2 = sdf.parse(date2 + ":00");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (d1.getTime() > d2.getTime()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取当前年份
	 * 
	 * @return
	 */
	public static int getNowYear() {
		Time time = new Time("GMT+8");
		time.setToNow();
		int year = time.year;
		return year;
	}

	public static String getNowDay() {
		Time time = new Time("GMT+8");
		time.setToNow();
		int day = time.monthDay;
		return MyUtils.addZero_2(day);
	}

	public static int getNowMonth() {
		Time time = new Time("GMT+8");
		time.setToNow();
		int month = time.month;
		return month;
	}

	// Jan 一月
	// Feb二月
	// Mar三月
	// Apr 四月
	// May 五月
	// June 六月
	// July 七月
	// Aug八月
	// Sep 九月
	// Oct 十月
	// Nov 十一月
	// Dec 十二月
	private static final String[] engMonths = { "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月",
			"12月" };

	public static String getNowMonthOnEng() {
		Time time = new Time("GMT+8");
		time.setToNow();
		int month = time.month;
		return engMonths[month];
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String friendly_time(String sdate) {
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(dateFormatYMDHM);
		Date time = null;
		try {
			time = mSimpleDateFormat.parse(sdate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (time == null) {
			return "unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		SimpleDateFormat dateFormater2 = new SimpleDateFormat(dateFormatYMD);
		String curDate = dateFormater2.format(cal.getTime());
		String paramDate = dateFormater2.format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 10) {
			ftime = days + "天前";
		} else if (days > 10) {
			ftime = "10天前";
		}
		return ftime;
	}

	/**
	 * @param 要转换的秒数
	 * 
	 * @return
	 */
	public static String formatDuring(long mss) {
		long hours = (mss % (60 * 60 * 24)) / (60 * 60);
		long minutes = (mss % (60 * 60)) / (60);
		long seconds = mss % 60;
		StringBuffer sb = new StringBuffer();
		if (hours > 0) {
			sb.append(hours + "小时");
		}
		sb.append(minutes + "分" + seconds + "秒");
		return sb.toString();
	}

	// 上午11:09
	public static String getReadableTime(int hour, int minute) {
		if (hour <= 12) {
			return "上午" + formatToTwoDigits(hour) + ":" + formatToTwoDigits(minute);
		} else {
			return "下午" + formatToTwoDigits(hour % 12) + ":" + formatToTwoDigits(minute);
		}
	}

	public static String formatToTwoDigits(int time) {
		if (time < 10) {
			return "0" + time;
		}
		return String.valueOf(time);
	}

	public static long convertStringToSeconds(String birthDate) {
		return convertStringToMilliSeconds(birthDate) / 1000;
	}

	// get millisecond of 2012-12-12 12:12
	public static long convertStringToMilliSeconds(String birthDate) {
		long time = 0;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = formatter.parse(birthDate);
			time = date.getTime();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	// 11月12日12:12~14:24
	public static String getCreatTimeFromSeconds(long start, long end) {
		String strDate = "";
		SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日HH:mm");
		Date startDate = new Date(start * 1000);
		strDate = formatter.format(startDate);

		formatter = new SimpleDateFormat("HH:mm");
		Date endDate = new Date(end * 1000);
		return strDate + "~" + formatter.format(endDate);
	}

	// 距离会议开始
	// 会议已开始
	// 会议已结束
	public static String getMeetingDiffStrFromSeconds(long start, long end) {
		Calendar currCalendar = Calendar.getInstance();
		long startDiff = (currCalendar.getTimeInMillis() / 1000 - start);
		long endDiff = (currCalendar.getTimeInMillis() / 1000 - end);
		if (startDiff > 0 && endDiff < 0) {
			return "会议正在进行中";
		}
		if (endDiff > 0) {
			return "会议已结束";
		}
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTimeInMillis(start * 1000);
		return "离会议开始还剩" +timeDifference(startCalendar);
	}

	/**
	 * 判断时间与当前时间的差距， 给予字符提示.
	 * 
	 * @param calendar
	 * @return 作者:fighter <br />
	 *         创建时间:2013-4-9<br />
	 *         修改时间:<br />
	 */
	public static String timeDifference(Calendar calendar) {
		String info = "";
		Calendar currCalendar = Calendar.getInstance();
		long second = (currCalendar.getTimeInMillis() - calendar.getTimeInMillis()) / 1000;
		int index = 0;
		if (second < (60 * 60)) {
			index = 60;
		} else if (second < (24 * 60 * 60)) {
			index = 60 * 60;
		} else if (second < (30 * (24 * 60 * 60))) {
			index = (24 * 60 * 60);
		}
		info = second(second, index);

		return info;
	}

	private static String second(long second, int index) {
		String info = "";
		if (index == 60) {
			info = DamiApp.getInstance().getString(R.string.minutes);
		} else if (index == (60 * 60)) {
			info = DamiApp.getInstance().getString(R.string.hour);
		} else if (index == (24 * 60 * 60)) {
			info = DamiApp.getInstance().getString(R.string.day);
		} else {
			return DamiApp.getInstance().getString(R.string.long_ago);
		}
		int num = (int) (second / index);
		return Math.abs(num) + info;
	}

	// private static String second(long second, int index, int num) {
	// String info = "";
	// if (index == 60) {
	// info = DamiApp.getInstance().getString(R.string.minutes);
	// } else if (index == (60 * 60)) {
	// info = DamiApp.getInstance().getString(R.string.hour);
	// } else if (index == (24 * 60 * 60)) {
	// info = DamiApp.getInstance().getString(R.string.day);
	// } else {
	// return DamiApp.getInstance().getString(R.string.long_ago);
	// }
	//
	//
	// if (second < index * num) {
	// return num + info;
	// } else {
	// return second(second, index, ++num);
	// }
	// }

	public static String getTime(long currTime) {

		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTimeInMillis(currTime);
		String str = timeDifference(calendar);
		Date date = calendar.getTime();
		SimpleDateFormat format = null;
		if (str.endsWith(DamiApp.getInstance().getString(R.string.minutes))
				|| str.endsWith(DamiApp.getInstance().getString(R.string.hour))) {
			format = new SimpleDateFormat("HH:mm:ss");
		} else if (str.endsWith(DamiApp.getInstance().getString(R.string.day))
				|| str.endsWith(DamiApp.getInstance().getString(R.string.long_ago))) {
			format = new SimpleDateFormat("yyyy-MM-dd");
		}
		return format.format(date);
	}

	public static String getBirthday(long birth) {
		String strDate = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(birth);// 获取当前时间
		strDate = formatter.format(curDate);

		return strDate;
	}

	/**
	 * 出生日期转换为年龄
	 * 
	 * @param brithDate
	 * @return 作者:fighter <br />
	 *         创建时间:2013-3-26<br />
	 *         修改时间:<br />
	 */
	public static int dateToAge(String brithDate) {
		if (TextUtils.isEmpty(brithDate)) {
			return 0;
		}
		int age = 0;
		try {
			Calendar cal = Calendar.getInstance();
			String birth = brithDate;
			String now = (cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DATE));
			Date d1 = new Date(birth); // 出生日期d1
			Date d2 = new Date(now); // 当前日期d2
			long i = (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24);
			int g = (int) i;
			age = g / 365;
		} catch (IllegalArgumentException e) {
		}

		return age;
	}

	public static String timeDifference(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);

		return timeDifference(calendar);
	}

	public static String timeOnlie(String online) {
		try {
			long mtime = Long.parseLong(online) * 1000;
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			calendar.setTimeInMillis(mtime);
			String str = timeOnlie(calendar);
			Date date = calendar.getTime();
			SimpleDateFormat format = null;
			if (str.endsWith(DamiApp.getInstance().getString(R.string.minutes))
					|| str.endsWith(DamiApp.getInstance().getString(R.string.hour))) {
				format = new SimpleDateFormat("HH:mm:ss");
			} else if (str.endsWith(DamiApp.getInstance().getString(R.string.day))
					|| str.endsWith(DamiApp.getInstance().getString(R.string.long_ago))) {
				format = new SimpleDateFormat("MM-dd HH:mm:ss");
			}

			return format.format(date);
		} catch (Exception e) {
			return "";
		}

	}

	// 3小时前
	public static String getCreateTime(long online) {
		try {
			String timeStr = "";
			long mtime = online;
			if ((online + "").length() <= 10) {
				mtime = online * 1000;
			}
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			calendar.setTimeInMillis(mtime);
			String str = timeOnlie(calendar);
			if (str.endsWith(DamiApp.getInstance().getString(R.string.minutes))
					|| str.endsWith(DamiApp.getInstance().getString(R.string.hour))) {
				timeStr = str + DamiApp.getInstance().getString(R.string.before);
			} else if (str.endsWith(DamiApp.getInstance().getString(R.string.day))
					|| str.endsWith(DamiApp.getInstance().getString(R.string.long_ago))) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date date = calendar.getTime();
				timeStr = format.format(date);
			}

			return timeStr;
		} catch (Exception e) {
			return "";
		}
	}

	public static String getHumanReadTime(long online) {
		try {
			String timeStr = "";
			long mtime = online;
			if ((online + "").length() <= 10) {
				mtime = online * 1000;
			}
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			calendar.setTimeInMillis(mtime);
			String str = timeOnlie(calendar);
			if (str.endsWith(DamiApp.getInstance().getString(R.string.minutes))
					|| str.endsWith(DamiApp.getInstance().getString(R.string.hour))) {
				timeStr = str + DamiApp.getInstance().getString(R.string.before);
			} else if (str.endsWith(DamiApp.getInstance().getString(R.string.day))
					|| str.endsWith(DamiApp.getInstance().getString(R.string.long_ago))) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date date = calendar.getTime();
				timeStr = format.format(date);
			}

			return timeStr;
		} catch (Exception e) {
			return "";
		}
	}

	public static String getSecondTime(long online) {
		try {
			String timeStr = "";
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			calendar.setTimeInMillis(online);
			String str = timeOnlie(calendar);
			if (str.endsWith(DamiApp.getInstance().getString(R.string.second))
					|| str.endsWith(DamiApp.getInstance().getString(R.string.minutes))
					|| str.endsWith(DamiApp.getInstance().getString(R.string.hour))) {
				timeStr = str + DamiApp.getInstance().getString(R.string.before);
			} else if (str.endsWith(DamiApp.getInstance().getString(R.string.day))
					|| str.endsWith(DamiApp.getInstance().getString(R.string.long_ago))) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date date = calendar.getTime();
				timeStr = format.format(date);
			}

			return timeStr;
		} catch (Exception e) {
			return "";
		}

	}

	public static String timeOnlie(Calendar calendar) {
		long cTime = calendar.getTimeInMillis();
		calendar.setTimeInMillis(cTime/* + TIME */);
		String info = "";
		Calendar currCalendar = Calendar.getInstance();
		long second = (currCalendar.getTimeInMillis() - calendar.getTimeInMillis()) / 1000;
		int index = 0;
		if (second < 0) {
			second = 0;
		}

		if (second < 60) {
			index = 1;
		} else if (second < (60 * 60)) {
			index = 60;
		} else if (second < (24 * 60 * 60)) {
			index = 60 * 60;
		} else if (second < (30 * (24 * 60 * 60))) {
			index = (24 * 60 * 60);
		}
		info = secondOnlie(second, index, 1);

		return info;
	}

	private static String secondOnlie(long second, int index, int num) {
		String info = "";
		if (index == 1) {
			info = DamiApp.getInstance().getString(R.string.second);
		} else if (index == 60) {
			info = DamiApp.getInstance().getString(R.string.minutes);
		} else if (index == (60 * 60)) {
			info = DamiApp.getInstance().getString(R.string.hour);
			;
		} else if (index == (24 * 60 * 60)) {
			info = DamiApp.getInstance().getString(R.string.day);
		} else {
			return DamiApp.getInstance().getString(R.string.long_ago);
		}
		if (second < 60) {
			return second + info;
		}
		if (second < index * num) {
			return num + info;
		} else {
			return secondOnlie(second, index, ++num);
		}
	}

	public static String timeDifference(String currTime) {
		Calendar calendar = Calendar.getInstance();
		try {
			long curr = Long.parseLong(currTime);
			calendar.setTimeInMillis(curr);
		} catch (Exception e) {
		}

		return timeDifference(calendar);
	}

	/**
	 * 获取几天以前的秒数
	 * 
	 * @param day
	 * @return 作者:fighter <br />
	 *         创建时间:2013-6-7<br />
	 *         修改时间:<br />
	 */
	public static String dayBefore(float day) {
		// Calendar calendar = Calendar.getInstance();
		// calendar.add(Calendar.DAY_OF_WEEK, 0 - day);
		// return (calendar.getTimeInMillis() / 1000) + "";
		long time = (long) (60 * 60 * 24 * day);

		return time + "";

	}

	public static Calendar getCalendar(String brithDate) {
		Calendar calendar = Calendar.getInstance();
		if (TextUtils.isEmpty(brithDate)) {
			return calendar;
		}

		String birth = brithDate;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = formatter.parse(birth);
			; // 出生日期d1
			calendar.setTime(date);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return calendar;
	}

	public static String showTimedate(int year, int month, int day, int hour, int minute) {
		Calendar calendar = Calendar.getInstance();
		int cYear = calendar.get(Calendar.YEAR);
		int cMonth = calendar.get(Calendar.MONTH);
		int cDay = calendar.get(Calendar.DAY_OF_MONTH);
		int cHour = calendar.get(Calendar.HOUR_OF_DAY);
		int cMinute = calendar.get(Calendar.MINUTE);

		if (year < cYear) {
			return "";
		}

		if (year == cYear && month < cMonth) {
			return "";
		}

		if (year == cYear && month == cMonth && day < cDay) {
			return "";
		}

		if (year == cYear && month == cMonth && day == cDay && hour < cHour) {
			return "";
		}

		if (year == cYear && month == cMonth && day == cDay && hour == cHour && minute < cMinute) {
			return "";
		}

		int trueMonth = (month + 1);
		String sMonth = trueMonth > 9 ? (trueMonth + "") : ("0" + trueMonth);
		String sDay = day > 9 ? (day + "") : ("0" + day);
		String sHour = hour > 9 ? (hour + "") : ("0" + hour);
		String sMinute = minute > 9 ? (minute + "") : ("0" + minute);
		String date = year + "-" + sMonth + "-" + sDay + " " + sHour + ":" + sMinute;
		return date;
	}

	public static String showPromptdate(int year, int month, int day, int hour, int minute) {

		int trueMonth = (month + 1);
		String sMonth = trueMonth > 9 ? (trueMonth + "") : ("0" + trueMonth);
		String sDay = day > 9 ? (day + "") : ("0" + day);
		String sHour = hour > 9 ? (hour + "") : ("0" + hour);
		String sMinute = minute > 9 ? (minute + "") : ("0" + minute);
		String date = year + "-" + sMonth + "-" + sDay + " " + sHour + ":" + sMinute;
		return date;
	}

	public static String getTime(String currTime) {
		long time = 0;
		try {
			time = Long.parseLong(currTime);
		} catch (Exception e) {
			time = System.currentTimeMillis();
		}

		return getTime(time);
	}

	public static long getTimeStamp(String brithDate) {
		long time = 0;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = formatter.parse(brithDate);
			time = date.getTime();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}
}
