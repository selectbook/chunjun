package com.iiot.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.log4j.Logger;

import com.iiot.util.ExceptionUtil;

/**
 * 加密/解密
 * @author 
 */
public class DesUtil {

	static Logger logger = Logger.getLogger(DesUtil.class);

	static String passWord = "79854964841679113134989448933666";

	/**
	 * 加密
	 * 车辆ID（8字节）与 车组ID（4字节）组成12字节，做DES算法加密
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data) throws Exception {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(passWord.getBytes());

			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secureKey = keyFactory.generateSecret(desKey);

			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES");

			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, secureKey, random);

			// 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(data);

		} catch (Exception e) {
			logger.error("DES 加密失败：" + ExceptionUtil.getStackStr(e));
			throw e;
		}
	}

	/**
	 * 解密
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data) throws Exception {
		try {
			// DES算法要求有一个可信任的随机数源
			SecureRandom random = new SecureRandom();

			// 创建一个DESKeySpec对象
			DESKeySpec desKey = new DESKeySpec(passWord.getBytes());

			// 创建一个密匙工厂
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

			// 将DESKeySpec对象转换成SecretKey对象
			SecretKey secureKey = keyFactory.generateSecret(desKey);

			// Cipher对象实际完成解密操作
			Cipher cipher = Cipher.getInstance("DES");

			// 用密匙初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, secureKey, random);

			// 真正开始解密操作		 
			return cipher.doFinal(data);
			
		} catch (Exception e) {
			logger.error("DES 解密失败：" + ExceptionUtil.getStackStr(e));
			throw e;
		}
	}	
}
