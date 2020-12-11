package com.iiot.util.chinese2english;

import java.util.HashSet;
import java.util.Set;

public class TerminalAlarmType {
	
	
	public static Set<String> getAllTerminalType(){
		Set<String> set = new HashSet<String>(); 
		set.add("GPSAntennaState");
		set.add("lowVoltage");
		set.add("powerDown");
		set.add("bcarIsAlarm");
		set.add("isIllegalRemove");
		set.add("overSpeed");
		set.add("shockAlarm");
		set.add("collideAlarm");
		set.add("inAreaAlarm");
		set.add("illegalMove");
		set.add("outAreaAlarm");
		set.add("acarBts");
		set.add("bcarBts");
		set.add("emergencyAlarm");
		set.add("pseudoLbs");
		set.add("illegalStart");//非法点火
		return set;
	}
	
	public static boolean isTerminalAlarm(String alarmType) {
		Set<String> set = new HashSet<String>();
		set.add("emergencyAlarm");
		set.add("overSpeed");
		set.add("powerDown");
		set.add("lowVoltage");
		set.add("bcarIsAlarm");
		set.add("isIllegalRemove");
		set.add("shockAlarm");
		set.add("collideAlarm");
		set.add("illegalMove");
		set.add("acarBts");
		set.add("bcarBts");
		set.add("pseudoLbs");
		set.add("outAreaAlarm");
		set.add("inAreaAlarm");
		set.add("GPSAntennaState");
		set.add("illegalStart");//非法点火
		boolean contains = set.contains(alarmType);
		return contains;
	}
	

}
