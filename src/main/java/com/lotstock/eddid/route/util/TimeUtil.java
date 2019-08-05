package com.lotstock.eddid.route.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 
 * 
 * @author 熊伟
 */
public abstract class TimeUtil {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat();

	private static Date tempDate = new Date();

	/**
	 * @return 当前时间对象
	 */
	public static Date now() {
		return new Date(Calendar.getInstance().getTimeInMillis());
	}

	/**
	 * @return 当前时间对象
	 */
	public static Timestamp nowTime() {
		return new Timestamp(Calendar.getInstance().getTimeInMillis());
	}

	public static Date parseDateFormat(String strDate, String pattern) throws ParseException {
		Date date = null;
		dateFormat = new SimpleDateFormat();
		dateFormat.applyPattern(pattern);
		date = dateFormat.parse(strDate);
		return date;
	}

	public static Date parseDateFormat(String strDate) throws ParseException {
		String pattern = "yyyy-MM-dd";
		return parseDateFormat(strDate, pattern);
	}

	public static Date parseTimeFormat(String strDate) throws ParseException {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		return parseDateFormat(strDate, pattern);
	}

	/**
	 * @return 当前的日期对象
	 */
	public static Date today() {
		Date t = now();
		getDate(t);
		return t;
	}

	/**
	 * 创建一个时间对象，以传入时间为基准变动其中的某个域
	 * 
	 * @param t
	 *            基准时间
	 * @param f
	 *            变化域
	 * @param ci
	 *            变化量
	 * 
	 * @return 变动后的新建的时间对象
	 */
	public static Date createConversionCal(Date t, int f, int ci) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(t);
		cal.set(f, cal.get(f) + ci);
		return new Date(cal.getTimeInMillis());
	}

	/**
	 * 创建一个时间对象，以传入时间为基准变动数日
	 * 
	 * @param t
	 *            基准时间
	 * @param ci
	 *            日期变动量
	 * 
	 * @return 变动后的新建的时间对象
	 */
	public static Date createConversionDate(Date t, int ci) {
		return createConversionCal(t, Calendar.DATE, ci);
	}

	/**
	 * @param t
	 *            基准时间
	 * @param f
	 *            变化域
	 * @param si
	 *            设置的时间
	 */
	public static void setFiled(Date t, int f, int si) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(t);
		cal.set(f, si);
		t.setTime(cal.getTimeInMillis());
	}

	/**
	 * 将日期对象格式化为日期标准格式的字符串
	 * 
	 * @param date
	 *            待格式化的日期对象
	 * @return
	 */
	public static String formatDate(Date date) {
		dateFormat = new SimpleDateFormat();
		dateFormat.applyPattern("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	/**
	 * 将日期对象格式化为日期、时间标准格式的字符串
	 * 
	 * @param date
	 *            待格式化的日期对象
	 * @return 格式化为日期、时间格式的字符串
	 */
	public static String formatDateTime(Date date) {
		dateFormat = new SimpleDateFormat();
		dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	/**
	 * 将日期对象格式化为特定格式的字符串
	 * 
	 * @param date
	 *            待格式化的日期对象
	 * @param pattern
	 *            格式化标准
	 * @return
	 */
	public static String formatDate(Date date, String pattern) {
		dateFormat = new SimpleDateFormat();
		dateFormat.applyPattern(pattern);
		return dateFormat.format(date);
	}

	/**
	 * 将长整型数据作为日期信息格式化为日期格式的字符串
	 * 
	 * @param d
	 *            待格式化的长整型
	 * @return
	 */
	public static String formatDate(long d) {
		tempDate.setTime(d);
		return formatDate(tempDate);
	}

	/**
	 * 将长整型数据作为日期信息格式化为日期、时间格式的字符串
	 * 
	 * @param t
	 *            待格式化的长整型
	 * @return
	 */
	public static String formatDateTime(long t) {
		tempDate.setTime(t);
		return formatDateTime(tempDate);
	}

	/**
	 * 将长整型数据作为日期信息格式化为特定标准的字符串
	 * 
	 * @param t
	 *            待格式化的长整型
	 * @param pattern
	 *            格式化标准
	 * @return
	 */
	public static String formatDate(long t, String pattern) {
		tempDate.setTime(t);
		return formatDate(tempDate, pattern);
	}

	/**
	 * @return 日期格式化对象
	 */
	public static DateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * 将时间戳对象中的时间信息移出，仅保留日期信息
	 * 
	 * @param t
	 *            待变更的时间戳对象
	 */
	public static void getDate(Date t) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(t);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		t.setTime(cal.getTimeInMillis());
	}

	/**
	 * 根据用户生日计算年龄
	 */
	public static int getAgeByBirthday(Date birthday) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date());
		int theYear = calendar.get(Calendar.YEAR);
		calendar.setTime(birthday);
		int birthYear = calendar.get(Calendar.YEAR);
		int year = theYear - birthYear;
		if (year < 0) {
			return 0;
		}
		return year;
	}

	/**
	 * 根据用户生日计算年龄（包括年月日）
	 */
	public static String getAllAgeByBirthday(Date birthday) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date());
		int theYear = calendar.get(Calendar.YEAR);
		int theMonth = calendar.get(Calendar.MONTH) + 1;
		int theDay = calendar.get(Calendar.DATE);

		calendar.setTime(birthday);

		int birthYear = calendar.get(Calendar.YEAR);
		int birthMonth = calendar.get(Calendar.MONTH) + 1;
		int birthDay = calendar.get(Calendar.DATE);

		int year = theYear - birthYear;
		int month = theMonth - birthMonth;
		int day = theDay - birthDay;
		int week = day % 7;
		String str = year + "岁" + month + "月" + week + "周" + day;
		return str;
	}

	/**
	 * 两个日期相差秒数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getTimeDelta(Date date1, Date date2) {
		long timeDelta = (date1.getTime() - date2.getTime()) / 1000;// 单位是秒
		int secondsDelta = timeDelta > 0 ? (int) timeDelta : (int) Math.abs(timeDelta);
		return secondsDelta;
	}

	public static String formatSecond(Integer second) {
		String html = "0秒";
		if (second != null) {
			String format;
			Object[] array;
			Integer hours = (int) (second / (60 * 60));
			Integer minutes = (int) (second / 60 - hours * 60);
			Integer seconds = (int) (second - minutes * 60 - hours * 60 * 60);
			if (hours > 0) {
				format = "%1$,d小时%2$,d分%3$,d秒";
				array = new Object[] { hours, minutes, seconds };
			} else if (minutes > 0) {
				format = "%1$,d分%2$,d秒";
				array = new Object[] { minutes, seconds };
			} else {
				format = "%1$,d秒";
				array = new Object[] { seconds };
			}
			html = String.format(format, array);
		}
		return html;
	}

	/**
	 * 两个日期相差月数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getMonthSpace(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(date1);
		c2.setTime(date2);
		return c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);

	}

	/**
	 * 两个日期相差天数
	 * 
	 * @param smdate
	 *            较小的时间
	 * @param bdate
	 *            较大的时间
	 * @return 相差天数
	 * @throws ParseException
	 */
	public static Integer getDaySpace(Date smdate, Date bdate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			smdate = sdf.parse(sdf.format(smdate));
			bdate = sdf.parse(sdf.format(bdate));
			Calendar cal = Calendar.getInstance();
			cal.setTime(smdate);
			long time1 = cal.getTimeInMillis();
			cal.setTime(bdate);
			long time2 = cal.getTimeInMillis();
			long between_days = (time2 - time1) / (1000 * 3600 * 24);
			return Integer.parseInt(String.valueOf(between_days));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	/**
	 * 计算两个日期之间相差的天数
	 * 
	 * @param smdate
	 *            较小的时间
	 * @param bdate
	 *            较大的时间
	 * @return 相差天数
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	public static String timeDiff(String startTime, String endTime) {
		// 按照传入的格式生成一个simpledateformate对象
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long nh = 1000 * 60 * 60;// 一小时的毫秒数
		long nm = 1000 * 60;// 一分钟的毫秒数
		//long ns = 1000;// 一秒钟的毫秒数
		long diff;
		long day = 0;
		long hour = 0;
		long min = 0;
		//long sec = 0;
		// 获得两个时间的毫秒时间差异
		try {
			diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
			day = diff / nd;// 计算差多少天
			hour = diff % nd / nh + day * 24;// 计算差多少小时
			min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
			//sec = diff % nd % nh % nm / ns;// 计算差多少秒
			// 输出结果
			/*
			 * System.out.println( "时间相差：" + day + "天" + (hour - day * 24) +
			 * "小时" + (min - day * 24 * 60) + "分钟" + sec + "秒。");
			 * System.out.println("hour=" + hour + ",min=" + min);
			 */
			if (day > 0) {
				return day + "天";
			} else if (hour > 0) {
				return hour + "小时";
			} else if (min >= 0) {
				return min == 0 ? "1分钟" : min + "分钟";
			}

		} catch (ParseException e) {
		}
		return "1分钟";
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String queryDateZh(String dateStr) throws ParseException {
		Date date=TimeUtil.parseDateFormat(dateStr);
		SimpleDateFormat df = new SimpleDateFormat("M月d日");
		return df.format(date);
	}
	
	public static void main(String arg[]){

		String days="2019-04-12 00:00:00";
		try {
			int day = getDaySpace(new Date(),parseDateFormat(days));
			System.out.println("相差天数days："+day);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
