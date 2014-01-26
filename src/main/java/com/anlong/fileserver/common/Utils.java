package com.anlong.fileserver.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Utils {
	/** 判断是否为空 */
	public static boolean isNull(Object obj1) {
		if (Utils.isNotNull(obj1)) {
			return false;
		} else {
			return true;
		}
	}

	/** 判断是否非空 */
	public static boolean isNotNull(Object obj1) {
		if (null == obj1 || "".equals(obj1.toString()) || "null".equals(obj1.toString())) {
			return false;
		} else {
			return true;
		}
	}

	/** equals重写 */
	public static boolean equals(String s1, String s2) {
		if (Utils.isNotNull(s1) && Utils.isNotNull(s2) && s1.equals(s2)) {
			return true;
		} else {
			return false;
		}
	}

	/** equals重写 */
	public static boolean equals(Object obj1, Object obj2) {
		if (Utils.isNotNull(obj1) && Utils.isNotNull(obj2) && obj1.toString().equals(obj2.toString())) {
			return true;
		} else {
			return false;
		}
	}



	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 日期格式转换方法, 将Date转为String
	 * @param date
	 * @return String
	 */
	public static String getDateFormat(Date date) {
		return sdf.format(date);
	}

	/**
	 * 日期格式转换方法, 将String转为Date
	 * @param String
	 * @return Date
	 */
	public static Date getDateFormat(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 拼装状态报告字符串
	 * @param oldState
	 * @param clintState
	 * @param mobileState
	 * @return
	 */
	public static int updateStateReport(int oldState, int clintState, int mobileState) {
		String s_oldState = String.valueOf(oldState);
		String oldClintState = null;
		String oldMobileState = null;
		try {
			if (clintState != 99 && mobileState != 9) {
				oldClintState = String.valueOf(clintState);
				oldMobileState = String.valueOf(mobileState);
			} else if (clintState == 99 && mobileState != 9) {
				oldClintState = s_oldState.substring(0, s_oldState.length() - 1);
				oldMobileState = String.valueOf(mobileState);
			} else if (clintState != 99 && mobileState == 9) {
				oldClintState = String.valueOf(clintState);
				oldMobileState = s_oldState.substring(s_oldState.length() - 1);
			}
			return Integer.parseInt(oldClintState + oldMobileState);
		} catch (Exception e) {
			return oldState;
		}
	}
	
	/**
	 * 编码
	 * @param s
	 * @return
	 */
	public static String toStringHex(String s) {
		if ("0x".equals(s.substring(0, 2))) {
			s = s.substring(2);
		}
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
			}
		}
		try {
			s = new String(baKeyword, "gbk");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}
	
	/** 创建发送流水号 */
	public static String getSerialNum(Integer userId){
		StringBuffer serialNum = new StringBuffer();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = format.format(new Date());
		serialNum.append(dateString).append(userId);
		Random r = new Random();
		for(int i=0;i<4;i++){
			serialNum.append(r.nextInt(10));
		}
		return serialNum.toString();
	}
	
	/**
	 * 获取项目绝对路径
	 * @return
	 */
	public static String getWebRoot() {
		String realUrl = Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String newUrl = "";
		if (realUrl.contains("/WEB-INF/")) {
			newUrl = realUrl.substring(0, realUrl.lastIndexOf("WEB-INF/"));
		}
		realUrl = newUrl.replace("%20", " ");
		return realUrl;
	}
	
	/** MD5编码 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString();
	}

}
