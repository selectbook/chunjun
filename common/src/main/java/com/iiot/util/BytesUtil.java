package com.iiot.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.iiot.common.bytes.HexStr;
import com.iiot.util.ExceptionUtil;

/**
 * 加密数据操作
 * @author liy
 */
public class BytesUtil {

	static Logger logger = Logger.getLogger(BytesUtil.class);

	/**
	 * 获取需要加密的数组
	 * @param groupId
	 * @param vehicleId
	 * @return
	 */
	public static byte[] getEncryptArray(int groupId, long vehicleId) {
		// 车组ID4字节，车辆ID8字节
		byte[] array = new byte[12];
		int offset = 0;
		for (int i = 3; i >= 0; i--) {
			array[offset++] = (byte) ((groupId & (0xff << i * 8)) >> i * 8);
		}
		for (int j = 7; j >= 0; j--) {
			array[offset++] = (byte) ((vehicleId & (0xff << j * 8)) >> j * 8);
		}

		return array;
	}

	/**
	 * 解密加密后的数组
	 * @param array
	 * @return
	 */
	public static Map<String, Long> getRawData(byte[] array) {
		if (array == null) {
			return null;
		}

		Map<String, Long> map = new HashMap<String, Long>();
		int len = array.length;
		long groupId = 0;
		long vehicleId = 0;
		if (len == 12) {
			for (int i = 0, j = 3; i < 4 && j >= 0; i++, j--) {
				groupId |= Byte.toUnsignedInt(array[i]) << j * 8;
			}

			for (int i = 4, j = 7; i < 12 && j >= 0; i++, j--) {
				vehicleId |= Byte.toUnsignedInt(array[i]) << j * 8;
			}
		}

		map.put("groupId", groupId);
		map.put("vehicleId", vehicleId);
		return map;
	}

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44,
	 * 0xEF,0xD9}
	 * @param src0
	 * @param src1
	 * @return
	 */
	private static byte uniteBytes(String src0, String src1) {
		byte b0 = Byte.decode("0x" + src0).byteValue();
		b0 = (byte) (b0 << 4);
		byte b1 = Byte.decode("0x" + src1).byteValue();
		byte ret = (byte) (b0 | b1);
		return ret;
	}

	static public byte[] toArray(String src) {
		if (src == null) {
			return null;
		}
		int m = 0, n = 0;
		int l = src.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
		}
		return ret;
	}

	public static void main(String[] args) {
		byte[] b = getEncryptArray(6165, 153786);
		try {
			byte[] ret = DesUtil.encrypt(b);
			String retStr = HexStr.toStr(ret);
			byte[] arr = toArray(retStr);
			byte[] src = DesUtil.decrypt(arr);
			Map<String, Long> map = getRawData(src);
			System.out.println("车组ID：" + map.get("groupId") + " / " + "车辆ID：" + map.get("vehicleId"));
		} catch (Exception e) {
			logger.error("DES加密|DES解密失败：" + ExceptionUtil.getStackStr(e));
		}
	}
}
