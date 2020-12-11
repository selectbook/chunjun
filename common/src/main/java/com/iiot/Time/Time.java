package com.iiot.Time;

import java.util.Calendar;
import java.util.Date;

import com.iiot.common.bytes.Conv;

/**
 * 
* @ClassName: Time
* @Description: 时间相关
* @date 2016年8月3日 下午1:30:30
*
 */
public class Time {
	static public byte[] toBcdTime(Date time){
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		int year = cal.get(Calendar.YEAR) - 1900 - 100;
		int month =  cal.get(Calendar.MONTH) + 1;
		int day =  cal.get(Calendar.DAY_OF_MONTH);
		int hour =  cal.get(Calendar.HOUR_OF_DAY);
		int min =  cal.get(Calendar.MINUTE);
		int sec =  cal.get(Calendar.SECOND);
		byte bcd[] = new byte[6];
		bcd[0] = Conv.dec2bcd((byte) (year & 0xff));
		bcd[1] = Conv.dec2bcd((byte) (month & 0xff));
		bcd[2] = Conv.dec2bcd((byte) (day & 0xff));
		bcd[3] = Conv.dec2bcd((byte) (hour & 0xff));
		bcd[4] = Conv.dec2bcd((byte) (min & 0xff));
		bcd[5] = Conv.dec2bcd((byte) (sec & 0xff));
		return bcd;
	}
}
