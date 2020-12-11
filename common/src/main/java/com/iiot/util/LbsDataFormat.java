package com.iiot.util;

import org.apache.log4j.Logger;

public class LbsDataFormat {

	static Logger logger = Logger.getLogger(LbsDataFormat.class);

	/**
	 * 单基站数据格式化
	 * @param lbs 单基站数据
	 * @return
	 */
	public static String format(String lbs) {
		// 460,0,10343,4743
		// 460;0;1;9384,4463,0
		if (lbs == null || "".equals(lbs)) {
			return null;
		}

		if (lbs.indexOf(";") > 0) {
			return lbs;
		} else {
			String[] arr = lbs.split(",");
			int len = arr.length;
			int index = 0;

			// MCC
			int mcc = 460;
			if (index + 1 <= len) {
				mcc = Integer.parseInt(arr[index]);
			}
			index += 1;

			// MNC
			int mnc = 0;
			if (index + 1 <= len) {
				mnc = Integer.parseInt(arr[index]);
			}
			index += 1;

			// LAC
			int lac = 0;
			if (index + 1 <= len) {
				lac = Integer.parseInt(arr[index]);
			}
			index += 1;

			// CELLID
			int cel = 0;
			if (index + 1 <= len) {
				cel = Integer.parseInt(arr[index]);
			}

			return mcc + ";" + mnc + ";" + 1 + ";" + lac + "," + cel + "," + 0;
		}
	}
	
	
	public static void main(String[] args) {
		String ret = format("460;0;1;9384,4463,0");
		System.out.println(ret);
	}

}
