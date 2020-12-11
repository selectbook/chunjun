package com.iiot.util;

import org.apache.log4j.Logger;

public class VipCreateUsedForJtAndBsj {

	static Logger logger = Logger.getLogger(VipCreateUsedForJtAndBsj.class);

	/**
	 * 将终端手机号转成VIP
	 * @param sSim
	 * @return
	 */
	public static String IntegerMobileIPAddress(String sSim) {
		if (sSim == null || "".equals(sSim)) {
			return null;
		}

		if (sSim.length() > 11) {
			sSim = getLastCharacter(sSim, 11);
		}

		try {
			int[] sTemp = new int[4];
			String[] sIp = new String[4];
			int iHigt;
			if (sSim.length() == 11) {
				sTemp[0] = Integer.parseInt(sSim.substring(3, 5));
				sTemp[1] = Integer.parseInt(sSim.substring(5, 7));
				sTemp[2] = Integer.parseInt(sSim.substring(7, 9));
				sTemp[3] = Integer.parseInt(sSim.substring(9, 11));
				iHigt = Integer.parseInt(sSim.substring(1, 3)) - 30;
			} else if (sSim.length() == 10) {
				sTemp[0] = Integer.parseInt(sSim.substring(2, 4));
				sTemp[1] = Integer.parseInt(sSim.substring(4, 6));
				sTemp[2] = Integer.parseInt(sSim.substring(6, 8));
				sTemp[3] = Integer.parseInt(sSim.substring(8, 10));
				iHigt = Integer.parseInt(sSim.substring(0, 2)) - 30;
			} else if (sSim.length() == 9) {
				sTemp[0] = Integer.parseInt(sSim.substring(1, 3));
				sTemp[1] = Integer.parseInt(sSim.substring(3, 5));
				sTemp[2] = Integer.parseInt(sSim.substring(5, 7));
				sTemp[3] = Integer.parseInt(sSim.substring(7, 9));
				iHigt = Integer.parseInt(sSim.substring(0, 1));
			} else if (sSim.length() < 9) {
				switch (sSim.length()) {
				case 8:
					sSim = "140" + sSim;
					break;
				case 7:
					sSim = "1400" + sSim;
					break;
				case 6:
					sSim = "14000" + sSim;
					break;
				case 5:
					sSim = "140000" + sSim;
					break;
				case 4:
					sSim = "1400000" + sSim;
					break;
				case 3:
					sSim = "14000000" + sSim;
					break;
				case 2:
					sSim = "140000000" + sSim;
					break;
				case 1:
					sSim = "1400000000" + sSim;
					break;
				}
				sTemp[0] = Integer.parseInt(sSim.substring(3, 5));
				sTemp[1] = Integer.parseInt(sSim.substring(5, 7));
				sTemp[2] = Integer.parseInt(sSim.substring(7, 9));
				sTemp[3] = Integer.parseInt(sSim.substring(9, 11));
				iHigt = Integer.parseInt(sSim.substring(1, 3)) - 30;
			} else {
				return null;
			}

			if ((iHigt & 0x8) != 0)
				sIp[0] = String.valueOf((sTemp[0] | 128));
			else
				sIp[0] = String.valueOf(sTemp[0]);
			if ((iHigt & 0x4) != 0)
				sIp[1] = String.valueOf(sTemp[1] | 128);
			else
				sIp[1] = String.valueOf(sTemp[1]);
			if ((iHigt & 0x2) != 0)
				sIp[2] = String.valueOf(sTemp[2] | 128);
			else
				sIp[2] = String.valueOf(sTemp[2]);
			if ((iHigt & 0x1) != 0)
				sIp[3] = String.valueOf(sTemp[3] | 128);
			else
				sIp[3] = String.valueOf(sTemp[3]);
			return sIp[0] + "." + sIp[1] + "." + sIp[2] + "." + sIp[3];
		} catch (Exception ex) {
			logger.error("设备号转VIP异常：" + ExceptionUtil.getStackStr(ex));
			return null;
		}
	}

	/**
	 * 获取一个字符串最后 n 位字符
	 * @param str 字符串
	 * @param n 多少位
	 * @return
	 */
	public static String getLastCharacter(String str, int n) {

		if (str == null || "".equals(str)) {
			logger.info("Param is null OR empty");
			return null;
		}

		int len = str.length();
		String devid;
		if (len >= n) {
			devid = str.substring(len - n);
		} else {
			StringBuffer sb = new StringBuffer(str);
			int index = n - len;
			for (int i = 0; i < index; i++) {
				sb.insert(0, "0");
			}

			devid = sb.toString();
		}

		return devid;
	}

}
