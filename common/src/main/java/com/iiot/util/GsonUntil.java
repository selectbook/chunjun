package com.iiot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;

/**
 * 单列模式
 */
public class GsonUntil<T> {

	private static Logger logger = Logger.getLogger(GsonUntil.class);

	private static Gson gson = new Gson();

	private static GsonBuilder gsonBuilder = new GsonBuilder();

	private GsonUntil() {
		
	}

	public static Gson getGson() {
		return gson;
	}

	/**
	 * 日期转化
	 * @param str
	 * @return
	 */
	public static Gson dateFormat(String str) {
		if (StringUtils.isEmpty(str)) {
			return gson;
		}
		return gsonBuilder.setDateFormat(str).create();
	}

	/**
	 * 对象转化
	 * @param str
	 * @param c
	 * @param <T>
	 * @return
	 */
	public static <T> T genericFormat(String str, Class c) {
		if (StringUtils.isEmpty(str) || c == null) {
			return null;
		}
		return (T) gson.fromJson(str, c);
	}

	/**
	 * 处理集合
	 * @param str
	 * @param type
	 * @param <T>
     * @return
     */
	public static <T> T genericFormat(String str, Type type) {
		if (StringUtils.isEmpty(str) || type == null) {
			return null;
		}
		try {
			return (T) gson.fromJson(str, type);
		}catch (Exception e){
			logger.info(str + "解析错误");
			return null;
		}
	}

	/**
	 * 转JSON
	 * @param t
	 * @return
	 */
	public static String toJson(Object t) {
		if (t == null) {
			return "";
		}
		return gson.toJson(t);
	}

}
