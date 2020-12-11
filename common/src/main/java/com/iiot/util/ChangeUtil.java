package com.iiot.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

public class ChangeUtil {
	static Logger logger = Logger.getLogger(ChangeUtil.class);

	/**
	 * 将对象转换成byte数组
	 */
	public static byte[] ObjectToByte(Object obj) {
		ByteArrayOutputStream baot = new ByteArrayOutputStream();
		ObjectOutputStream oot;
		byte[] array = null;
		try {
			oot = new ObjectOutputStream(baot);
			oot.writeObject(obj);
			array = baot.toByteArray();
		} catch (IOException e) {
			logger.error("ObjectToByte fail: " + ExceptionUtil.getStackStr(e));
		}
		return array;
	}

	/**
	 * 将byte数组转换成对象
	 */
	public static Object byteToObject(byte[] array) {
		ByteArrayInputStream is = new ByteArrayInputStream(array);
		Object obj = null;
		try {
			ObjectInputStream is2 = new ObjectInputStream(is);
			obj = is2.readObject();
		} catch (Exception e) {
			logger.error("byteToObject fail: " + ExceptionUtil.getStackStr(e));
		}
		return obj;
	}
}
