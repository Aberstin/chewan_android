package com.gongpingjia.carplay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.duohuo.dhroid.adapter.ValueFix;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class CarPlayValueFix implements ValueFix {

	static Map<String, DisplayImageOptions> imageOptions;

	public static DisplayImageOptions optionsDefault, headOptions,
			carLogoOptions, carBigLogoOptions, bigPicOptions;
	static {

		imageOptions = new HashMap<String, DisplayImageOptions>();
		optionsDefault = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.color.img_color)
				.showImageOnFail(R.drawable.img_loading_faild)
				.showImageForEmptyUri(R.color.img_color).cacheInMemory(true)
				.cacheOnDisk(true).resetViewBeforeLoading(true)
				.considerExifParams(false).bitmapConfig(Bitmap.Config.RGB_565)
				.build();
		imageOptions.put("default", optionsDefault);

		headOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.head_icon)
				.showImageForEmptyUri(R.drawable.head_icon)
				.showImageOnFail(R.drawable.head_icon).cacheInMemory(true)
				.cacheOnDisk(true).resetViewBeforeLoading(true).build();
		imageOptions.put("head", headOptions);

		carLogoOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).resetViewBeforeLoading(true)
				.considerExifParams(false).bitmapConfig(Bitmap.Config.RGB_565)
				.build();
		imageOptions.put("carlogo", carLogoOptions);

		carBigLogoOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.car_default)
				.showImageForEmptyUri(R.drawable.car_default)
				.showImageOnFail(R.drawable.car_default).cacheInMemory(true)
				.cacheOnDisk(true).resetViewBeforeLoading(true)
				.considerExifParams(false).bitmapConfig(Bitmap.Config.RGB_565)
				.build();
		imageOptions.put("carbiglogo", carBigLogoOptions);

		bigPicOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).resetViewBeforeLoading(true)
				.considerExifParams(false).bitmapConfig(Bitmap.Config.RGB_565)
				.build();
		imageOptions.put("big_pic", bigPicOptions);

	}

	@Override
	public Object fix(Object o, String type) {
		if (o == null)
			return null;
		if ("time".equals(type)) {
			return getStandardTime(Long.parseLong(o.toString()),
					"yyyy-MM-dd HH:mm");
		} else if ("neartime".equals(type)) {
			return converTime(Long.parseLong(o.toString()));
		}
		return o;
	}

	public Object imgDef(String type) {
		return null;
	}

	/**
	 * 
	 * @param type
	 *            header表示头像 grid中的大图,hd高清,saytop说详情页顶部
	 * @return
	 */

	public static String converTime(long timestamp) {
		long currentSeconds = System.currentTimeMillis();
		long timeGap = (currentSeconds - timestamp) / 1000;// 与现在时间相差秒�?
		Date currentDate = new Date(currentSeconds);
		Date agoDate = new Date(timestamp);
		String timeStr = null;

		Calendar aCalendar = Calendar.getInstance();

		aCalendar.setTime(currentDate);
		int currentDays = aCalendar.get(Calendar.DAY_OF_YEAR);
		aCalendar.setTime(agoDate);
		int agoDays = aCalendar.get(Calendar.DAY_OF_YEAR);
		// if(currentYear>agoYear) {
		// if()
		// }

		if (currentDays - agoDays >= 2) {// 1天以�?
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date(timestamp);
			timeStr = sdf.format(date);
		} else if (currentDays - agoDays == 1) {// 1小时-24小时
			timeStr = "昨天";
		} else if (timeGap > 60 * 60) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			Date date = new Date(timestamp);
			timeStr = sdf.format(date);
		} else if (timeGap > 60) {// //1分钟-59分钟
			timeStr = timeGap / 60 + "分钟前";
		} else {// 1秒钟-59秒钟
			timeStr = "刚刚";
		}
		return timeStr;
	}

	@SuppressLint("SimpleDateFormat")
	public static String getStandardTime(long timestamp, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = new Date(timestamp);
		sdf.format(date);
		return sdf.format(date);
	}

	public static String delHTMLTag(String htmlStr) {
		// String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>";
		// //定义script的正则表达式
		// String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>";
		// //定义style的正则表达式
		// String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式
		//
		// Pattern
		// p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
		// Matcher m_script=p_script.matcher(htmlStr);
		// htmlStr=m_script.replaceAll(""); //过滤script标签
		//
		// Pattern
		// p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
		// Matcher m_style=p_style.matcher(htmlStr);
		// htmlStr=m_style.replaceAll(""); //过滤style标签
		//
		// Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
		// Matcher m_html=p_html.matcher(htmlStr);
		// htmlStr=m_html.replaceAll(""); //过滤html标签

		if (htmlStr == null || htmlStr.trim().equals("")) {
			return "";
		}
		// 去掉�?��html元素,
		String str = htmlStr.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(
				"<[^>]*>", "");
		str = str.replaceAll("[(/>)<]", "");
		return str.replace("&nbsp", ""); // 返回文本字符�?
	}

	@Override
	public DisplayImageOptions imageOptions(String type) {
		if (type != null) {
			return imageOptions.get(type);

		}
		return imageOptions.get("default");
	}

}
