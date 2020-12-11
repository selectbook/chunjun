package com.iiot.util.chinese2english;

/**
 * @author created by chengcong
 * @date 2016年8月30日--下午5:36:27
 * @description:提供报警类型在字母与中文之间的转换
 *
 */
public class AlarmTypeChange {

	// 把报警类型字母类型换成对应的中文
	public static String alarmTypeEnToCh(String type) {
		switch (type) {
		case AlarmTypeEn2Ch.INROADALARM:
			type = "进路线报警";
			break;
		case AlarmTypeEn2Ch.OUTROADALARM:
			type = "出路线报警";
			break;
		case AlarmTypeEn2Ch.ROADOVERSPEEDALARM:
			type = "分段限速报警";
			break;
		case AlarmTypeEn2Ch.MAXOVERSPEEDALARM:
			type = "道路限速";
			break;
		case AlarmTypeEn2Ch.OUTAREAALARM:
			type = "终端出区域报警";
			break;
		case AlarmTypeEn2Ch.OUTREGIONALARM:
			type = "出区域报警";
			break;
		case AlarmTypeEn2Ch.OUTCIRCLEALARM:
			type = "圆形区域报警";
			break;
		case AlarmTypeEn2Ch.INAREAALARM:
			type = "终端进区域报警";
			break;
		case AlarmTypeEn2Ch.INREGIONALARM:
			type = "进区域报警";
			break;
		case AlarmTypeEn2Ch.AREAOVERSPEEDALARM:
			type = "区域超速报警";
			break;
		case AlarmTypeEn2Ch.LOWVOLTAGE:
			type = "终端主电源欠压";
			break;
		case AlarmTypeEn2Ch.POWERDOWN:
			type = "终端主电源掉电";
			break;
		case AlarmTypeEn2Ch.GPSANTENNASTATE:
			type = "GPS天线开路";
			break;
		case AlarmTypeEn2Ch.BCARISALARM:
			type = "Bcar拆除报警";
			break;
		case AlarmTypeEn2Ch.ISILLEGALREMOVE:
			type = "防拆除报警";
			break;
		case AlarmTypeEn2Ch.NEARPOINT:
			type = "近点报警";
			break;
		case AlarmTypeEn2Ch.STAYED:
			type = "停留报警";
			break;
		case AlarmTypeEn2Ch.STAYEDL:
			type = "超长停留报警";
			break;
		case AlarmTypeEn2Ch.OVERSPEED:
			type = "超速报警";
			break;
		case AlarmTypeEn2Ch.EMERGENCYALARM:
			type = "紧急报警";
			break;
		case AlarmTypeEn2Ch.SHOCKALARM:
			type = "震动报警";
			break;
		case AlarmTypeEn2Ch.COLLIDEALARM:
			type = "碰撞报警";
			break;
		case AlarmTypeEn2Ch.ILLEGALMOVE:
			type = "位移报警";
			break;
		case AlarmTypeEn2Ch.ACARBTS:
			type = "ACAR蓝牙报警";
			break;
		case AlarmTypeEn2Ch.BCARBTS:
			type = "BCAR蓝牙报警";
			break;
		case AlarmTypeEn2Ch.OUTADMINAREAALARM:
			type = "超出区域报警";
			break;
		case AlarmTypeEn2Ch.PSEUDOLBS:
			type = "伪基站报警";
			break;
		case AlarmTypeEn2Ch.ABNORMALSTAYALARM:
			type = "异常停留报警";
			break;
		case AlarmTypeEn2Ch.ILLEGALSTART:
			type = "非法点火报警";
			break;
		case AlarmTypeEn2Ch.TWOCHARGEALARM:
			type = "二押点报警";
			break;
		default:
			break;
		}
		return type;
	}

	public static String alarmTypeRemovePathName(String type) {
		if (type == null) {
			return "";
		} else if (type.contains(AlarmTypeCh2En.INROADALARM)) {
			type = AlarmTypeCh2En.INROADALARM;
		} else if (type.contains(AlarmTypeCh2En.OUTROADALARM)) {
			type = AlarmTypeCh2En.OUTROADALARM;
		} else if (type.contains(AlarmTypeCh2En.ROADOVERSPEEDALARM)) {
			type = AlarmTypeCh2En.ROADOVERSPEEDALARM;
		} else if (type.contains(AlarmTypeCh2En.MAXOVERSPEEDALARM)) {
			type = AlarmTypeCh2En.MAXOVERSPEEDALARM;
		} else if (type.contains(AlarmTypeCh2En.OUTAREAALARM)) {
			type = AlarmTypeCh2En.OUTAREAALARM;
		} else if (type.contains(AlarmTypeCh2En.INAREAALARM)) {
			type = AlarmTypeCh2En.INAREAALARM;
		} else if (type.contains(AlarmTypeCh2En.OUTREGIONALARM)
				 &&!type.contains(AlarmTypeCh2En.OUTADMINAREAALARM)
				 &&!type.contains(AlarmTypeCh2En.OUTAREAALARM)
				 ){
			type = AlarmTypeCh2En.OUTREGIONALARM;
		} else if (type.contains(AlarmTypeCh2En.INREGIONALARM)
				&&!type.contains(AlarmTypeCh2En.INAREAALARM)
				){
			type = AlarmTypeCh2En.INREGIONALARM;
		} else if (type.contains(AlarmTypeCh2En.AREAOVERSPEEDALARM)) {
			type = AlarmTypeCh2En.AREAOVERSPEEDALARM;
		} else if (type.contains(AlarmTypeCh2En.LOWVOLTAGE)) {
			type = AlarmTypeCh2En.LOWVOLTAGE;
		} else if (type.contains(AlarmTypeCh2En.POWERDOWN)) {
			type = AlarmTypeCh2En.POWERDOWN;
		} else if (type.contains(AlarmTypeCh2En.GPSANTENNASTATE)) {
			type = AlarmTypeCh2En.GPSANTENNASTATE;
		} else if (type.contains(AlarmTypeCh2En.ISILLEGALREMOVE)) {
			type = AlarmTypeCh2En.ISILLEGALREMOVE;
		} else if (type.contains(AlarmTypeCh2En.NEARPOINT)) {
			type = AlarmTypeCh2En.NEARPOINT;
		} else if (type.contains(AlarmTypeCh2En.NEARPOINT)) {
			type = AlarmTypeCh2En.NEARPOINT;
		} else if (type.contains(AlarmTypeCh2En.OVERSPEED)
				&&!type.contains(AlarmTypeCh2En.AREAOVERSPEEDALARM)
				) {
			type = AlarmTypeCh2En.OVERSPEED;
		} else if (type.contains(AlarmTypeCh2En.STAYED)
				  &&!type.contains(AlarmTypeCh2En.ABNORMALSTAYALARM)
				  &&!type.contains(AlarmTypeCh2En.STAYEDL)
				  ){
			type = AlarmTypeCh2En.STAYED;
		} else if (type.contains(AlarmTypeCh2En.STAYEDL)) {
			type = AlarmTypeCh2En.STAYEDL;
		} else if (type.contains(AlarmTypeCh2En.SHOCKALARM)) {
			type = AlarmTypeCh2En.SHOCKALARM;
		} else if (type.contains(AlarmTypeCh2En.COLLIDEALARM)) {
			type = AlarmTypeCh2En.COLLIDEALARM;
		} else if (type.contains(AlarmTypeCh2En.ILLEGALMOVE)) {
			type = AlarmTypeCh2En.ILLEGALMOVE;
		} else if (type.contains(AlarmTypeCh2En.ACARBTS)) {
			type = AlarmTypeCh2En.ACARBTS;
		} else if (type.contains(AlarmTypeCh2En.BCARBTS)) {
			type = AlarmTypeCh2En.BCARBTS;
		} else if (type.contains(AlarmTypeCh2En.EMERGENCYALARM)) {
			type = AlarmTypeCh2En.EMERGENCYALARM;
		} else if (type.contains(AlarmTypeCh2En.ACCELERATEALARM)) {
			type = AlarmTypeCh2En.ACCELERATEALARM;
		} else if (type.contains(AlarmTypeCh2En.SUDDENBRAKEALARM)) {
			type = AlarmTypeCh2En.SUDDENBRAKEALARM;
		} else if (type.contains(AlarmTypeCh2En.SHARPTURNALARM)) {
			type = AlarmTypeCh2En.SHARPTURNALARM;
		} else if (type.contains(AlarmTypeCh2En.OUTADMINAREAALARM)) {
			type = AlarmTypeCh2En.OUTADMINAREAALARM;
		} else if (type.contains(AlarmTypeCh2En.PSEUDOLBS)) {
			type = AlarmTypeCh2En.PSEUDOLBS;
		} else if (type.contains(AlarmTypeCh2En.PSEUDOLBS)) {
			type = AlarmTypeCh2En.PSEUDOLBS;
		} else if (type.contains(AlarmTypeCh2En.ABNORMALSTAYALARM)) {
			type = AlarmTypeCh2En.ABNORMALSTAYALARM;
		} else if (type.contains(AlarmTypeCh2En.ILLEGALSTART)) {
			type = AlarmTypeCh2En.ILLEGALSTART;
		} else if (type.contains(AlarmTypeCh2En.TWOCHARGEALARM)) {
			type = AlarmTypeCh2En.TWOCHARGEALARM;
		}
		return type;
	}
	
	// 电动车报警中文转换
	public static String alarmTypeKM06EnToCh(String type) {
		switch (type) {
		case AlarmTypeEn2Ch.OUTCIRCLEALARM:
			type = "电子围栏报警";
			break;
		case AlarmTypeEn2Ch.LOWVOLTAGE:
			type = "低电压报警";
			break;
		case AlarmTypeEn2Ch.POWERDOWN:
			type = "剪线报警";
			break;
		case AlarmTypeEn2Ch.SHOCKALARM:
			type = "震动报警";
			break;
		case AlarmTypeEn2Ch.ILLEGALMOVE:
			type = "轮动报警";
			break;
		case AlarmTypeEn2Ch.PSEUDOLBS:
			type = "伪基站报警";
			break;
		case AlarmTypeEn2Ch.ILLEGALSTART:
			type = "非法点火报警";
			break;
		default:
			break;
		}
		return type;
	}

	// 把报警的中文类型改为字母类型
	public static String alarmTypeChToEn(String type) {
		switch (type) {
		case AlarmTypeCh2En.INROADALARM:
			type = "inRoadAlarm";
			break;
		case AlarmTypeCh2En.OUTROADALARM:
			type = "outRoadAlarm";
			break;
		case AlarmTypeCh2En.ROADOVERSPEEDALARM:
			type = "roadOverSpeedAlarm";
			break;
		case AlarmTypeCh2En.MAXOVERSPEEDALARM:
			type = "maxOverSpeedAlarm";
			break;
		case AlarmTypeCh2En.OUTAREAALARM:
			type = "outAreaAlarm";
			break;
		case AlarmTypeCh2En.INAREAALARM:
			type = "inAreaAlarm";
			break;
		case AlarmTypeCh2En.OUTREGIONALARM:
			type = "outRegionAlarm";
			break;
		case AlarmTypeCh2En.INREGIONALARM:
			type = "inRegionAlarm";
			break;
		case AlarmTypeCh2En.OUTCIRCLEALARM:
			type = "outCircleAlarm";
			break;
		case AlarmTypeCh2En.AREAOVERSPEEDALARM:
			type = "areaOverSpeedAlarm";
			break;
		case AlarmTypeCh2En.LOWVOLTAGE:
			type = "lowVoltage";
			break;
		case AlarmTypeCh2En.POWERDOWN:
			type = "powerDown";
			break;
		case AlarmTypeCh2En.GPSANTENNASTATE:
			type = "GPSAntennaState";
			break;
		case AlarmTypeCh2En.BCARISALARM:
			type = "bcarIsAlarm";
			break;
		case AlarmTypeCh2En.ISILLEGALREMOVE:
			type = "isIllegalRemove";
			break;
		case AlarmTypeCh2En.NEARPOINT:
			type = "nearPoint";
			break;
		case AlarmTypeCh2En.STAYED:
			type = "stayed";
			break;
		case AlarmTypeCh2En.STAYEDL:
			type = "stayedL";
			break;
		case AlarmTypeCh2En.OVERSPEED:
			type = "overSpeed";
			break;
		case AlarmTypeCh2En.EMERGENCYALARM:
			type = "emergencyAlarm";
			break;
		case AlarmTypeCh2En.SHOCKALARM:
			type = "shockAlarm";
			break;
		case AlarmTypeCh2En.COLLIDEALARM:
			type = "collideAlarm";
			break;
		case AlarmTypeCh2En.ILLEGALMOVE:
			type = "illegalMove";
			break;
		case AlarmTypeCh2En.ACARBTS:
			type = "acarBts";
			break;
		case AlarmTypeCh2En.BCARBTS:
			type = "bcarBts";
			break;
		case AlarmTypeCh2En.OUTADMINAREAALARM:
			type = "outAdminAreaAlarm";
			break;
		case AlarmTypeCh2En.PSEUDOLBS:
			type = "pseudoLbs";
			break;
		case AlarmTypeCh2En.ABNORMALSTAYALARM:
			type = "abnormalStayAlarm";
			break;
		case AlarmTypeCh2En.ILLEGALSTART:
			type = "illegalStart";
			break;
		case AlarmTypeCh2En.TWOCHARGEALARM:
			type = "twoChargeAlarm";
			break;
		default:
			break;
		}

		return type;
	}

	/**
	 * 检查是否是进出区域报警
	 * 
	 * @param type
	 * @return
	 */
	public static boolean checkInOutAlarm(String type) {
		boolean result = false;
		// 如果该报警是出或者进区域报警
		if (AlarmTypeEn2Ch.OUTREGIONALARM.equals(type) || AlarmTypeEn2Ch.INREGIONALARM.equals(type)) {
			return true;
		}
		return result;
	}

}
