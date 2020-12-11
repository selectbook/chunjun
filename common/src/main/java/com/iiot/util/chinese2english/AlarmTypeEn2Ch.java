package com.iiot.util.chinese2english;

import java.util.ArrayList;
import java.util.List;

//报警类型字母转中文
public class AlarmTypeEn2Ch {
	/**
	  * 各种报警的类型集合，由于报警信息存在redis中，存储结构为("G"+groupId,"报警类型"+车辆id,报警信息对象的json字符串)
	  * 由于小key不明确，所以就通过这个list，组合成各种小key。
	  * 
	  * 注意项：后期添加新的报警类型，也在list的get方法中把该中报警类型添加到list中.修改了报警类型名称，也需要在下面修改一下
	  */

	public List<String> list;

	public static final String INROADALARM = "inRoadAlarm";//inRoadAlarm   进入道路报警
	public static final String OUTROADALARM = "outRoadAlarm";//outRoadAlarm   偏离道路报警
	public static final String ROADOVERSPEEDALARM = "roadOverSpeedAlarm";//roadOverSpeed  道路限速报警
	public static final String MAXOVERSPEEDALARM = "maxOverSpeedAlarm";//maxOverSpeedAlarm  道路限速
	public static final String OUTAREAALARM = "outAreaAlarm";//outAreaAlarm     终端出区域报警
	public static final String INAREAALARM = "inAreaAlarm";//inAreaAlarm      终端进区域报警
	public static final String OUTREGIONALARM = "outRegionAlarm";//outAreaAlarm     出区域报警
	public static final String OUTCIRCLEALARM = "outCircleAlarm";//outCircleAlarm     出区域报警
	public static final String INREGIONALARM = "inRegionAlarm";//inAreaAlarm      进区域报警
	public static final String AREAOVERSPEEDALARM = "areaOverSpeedAlarm";//areaOverSpeed  区域限速报警
	public static final String LOWVOLTAGE = "lowVoltage";//jt,v3   终端主电源欠压  
	public static final String POWERDOWN = "powerDown";//jt,v3    终端主电源掉电  
	public static final String GPSANTENNASTATE = "GPSAntennaState";//天线开路
	public static final String BCARISALARM = "bcarIsAlarm";//Bcar拆除报警  
	public static final String ISILLEGALREMOVE = "isIllegalRemove";//非法拆除报警
	public static final String NEARPOINT = "nearPoint";//近点报警
	public static final String OVERSPEED = "overSpeed";//超速报警
	public static final String STAYED = "stayed";//停留报警
	public static final String STAYEDL = "stayedL";//超长停留报警
	public static final String SHOCKALARM = "shockAlarm";//震动报警
	public static final String COLLIDEALARM = "collideAlarm";//碰撞报警
	public static final String ILLEGALMOVE = "illegalMove";//"位移报警"
	public static final String ACARBTS = "acarBts";//ACAR蓝牙报警
	public static final String BCARBTS = "bcarBts";//BCAR蓝牙报警
	public static final String EMERGENCYALARM = "emergencyAlarm";//紧急报警  
	public static final String OUTADMINAREAALARM = "outAdminAreaAlarm";//超出区域报警  （ 出行政区域报警 ）
	public static final String PSEUDOLBS = "pseudoLbs";//伪基站报警(1：报警/0：正常)
	public static final String ABNORMALSTAYALARM = "abnormalStayAlarm";//异常停留报警(1：报警/0：正常)
	public static final String ILLEGALSTART = "illegalStart";// 非法点火报警(1：报警/0：正常)
	public static final String TWOCHARGEALARM = "twoChargeAlarm";// 二押点报警(1：报警/0：正常)

	public List<String> getList() {
		list = new ArrayList<String>();
		list.add("inRoadAlarm");
		list.add("abnormalStayAlarm");
		list.add("outRoadAlarm");
		list.add("roadOverSpeedAlarm");
		list.add("maxOverSpeedAlarm");
		list.add("outAreaAlarm");
		list.add("inAreaAlarm");
		list.add("outRegionAlarm");
		list.add("outCircleAlarm");
		list.add("inRegionAlarm");
		list.add("areaOverSpeedAlarm");
		list.add("lowVoltage");
		list.add("powerDown");
		list.add("GPSAntennaState");
		list.add("bcarIsAlarm");
		list.add("isIllegalRemove");
		list.add("nearPoint");
		list.add("overSpeed");
		list.add("stayed");
		list.add("stayedL");
		list.add("shockAlarm");
		list.add("collideAlarm");
		list.add("illegalMove");
		list.add("acarBts");
		list.add("bcarBts");
		list.add("emergencyAlarm");
		list.add("outAdminAreaAlarm");
		list.add("pseudoLbs");
		list.add("illegalStart");
		list.add("twoChargeAlarm");
		return list;
	}


	public List<String> getNotHandleList() {
		List<String> resultList = new ArrayList<>();
		resultList.add("maxOverSpeedAlarm");
		resultList.add("areaOverSpeedAlarm");
		resultList.add("lowVoltage");
		resultList.add("powerDown");
		resultList.add("GPSAntennaState");
		resultList.add("bcarIsAlarm");
		resultList.add("isIllegalRemove");
		resultList.add("nearPoint");
		resultList.add("overSpeed");
		resultList.add("stayed");
		resultList.add("stayedL");
		resultList.add("shockAlarm");
		resultList.add("collideAlarm");
		resultList.add("illegalMove");
		resultList.add("acarBts");
		resultList.add("bcarBts");
		resultList.add("emergencyAlarm");
		resultList.add("pseudoLbs");
		resultList.add("illegalStart");
		return resultList;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

}
