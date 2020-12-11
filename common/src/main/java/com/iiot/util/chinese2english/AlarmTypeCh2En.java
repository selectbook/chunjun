/**
 * 
 */
package com.iiot.util.chinese2english;

/**
*@author  created by chengcong
*@date 2016年8月30日--下午3:50:54
*@description:报警类型中文转字母的常量类
*
*/
public class AlarmTypeCh2En {
	
	 public static final String INROADALARM="进路线报警";//inRoadAlarm
	 public static final String OUTROADALARM="出路线报警";//outRoadAlarm
	 public static final String ROADOVERSPEEDALARM="分段限速报警";//roadOverSpeedAlarm
	 public static final String MAXOVERSPEEDALARM="道路限速";//maxOverSpeedAlarm，这是抓路后判断是否报警
	 public static final String OUTAREAALARM="终端出区域报警";//outAreaAlarm（这是终端上来的）
	 public static final String INAREAALARM="终端进区域报警";//inAreaAlarm 进入区域报警（这是终端上来的）
	 public static final String OUTREGIONALARM="出区域报警";//outRegionAlarm （这是自己划定的区域）
	 public static final String OUTCIRCLEALARM="圆形区域报警";//outCircleAlarm （出圆形区域报警）
	 public static final String INREGIONALARM="进区域报警";//inRegionAlarm 进入区域报警（这是自己划定的区域）
	 public static final String AREAOVERSPEEDALARM="区域超速报警";//areaOverSpeedAlarm
	 public static final String LOWVOLTAGE="终端主电源欠压";//JT, V3电源欠压   lowVoltage
	 public static final String POWERDOWN="终端主电源掉电";//JT, V3低电压  powerDown
	 public static final String GPSANTENNASTATE="GPS天线开路";//GPS天线开路  GPSAntennaState
	 public static final String BCARISALARM="Bcar拆除报警";//Bcar拆除报警 bcarIsAlarm
	 public static final String ISILLEGALREMOVE="防拆除报警";//防拆除报警  isIllegalRemove
	 public static final String NEARPOINT="近点报警";//近点报警  nearPoint
	 public static final String OVERSPEED="超速报警";//超速报警  overSpeed
	 public static final String STAYED="停留报警";   //stayed
	 public static final String STAYEDL="超长停留报警";//stayedL
	 public static final String SHOCKALARM="震动报警";//shockAlarm
	 public static final String COLLIDEALARM="碰撞报警";//collideAlarm
	 public static final String ILLEGALMOVE="位移报警";//illegalMove
	 public static final String ACARBTS="ACAR蓝牙报警";//acarBts
	 public static final String BCARBTS="BCAR蓝牙报警";//bcarBts
	 public static final String EMERGENCYALARM="紧急报警";//emergencyAlarm
	 public static final String ACCELERATEALARM="急加速报警";//accelerateAlarm    这个功能还没做，先在这里和前台约定字段
	 public static final String SUDDENBRAKEALARM="急减速报警";//suddenBrakeAlarm   这个功能还没做，先在这里和前台约定字段
	 public static final String SHARPTURNALARM="急转弯报警";//sharpTurnAlarm  这个功能还没做，先在这里和前台约定字段
	 public static final String OUTADMINAREAALARM="超出区域报警";//outAdminAreaAlarm  这个功能还没做，先在这里和前台约定字段
	 public static final String PSEUDOLBS="伪基站报警";//pseudoLbs  伪基站报警(1：报警/0：正常)
	 public static final String ABNORMALSTAYALARM="异常停留报警";//abnormalStayAlarm  异常停留报警(1：报警/0：正常)
	 public static final String ILLEGALSTART="非法点火报警";//illegalStart  非法点火报警(1：报警/0：正常)
	 public static final String TWOCHARGEALARM="二押点报警";//twoChargeAlarm  非法点火报警(1：报警/0：正常)

	 
//	  * 添加一个报警，注意校对哪些地方：
//	  * ① com.iiot.boyun.util.AlarmTypeCh2En
//	  * ② com.iiot.boyun.util.AlarmTypeEn2Ch
//	  * ③ com.iiot.boyun.util.AlarmTypeChange
//	  * ④ Bolt对应关系，协议对应关系
//	  * ⑤ 气泡是否显示的判断 com.iiot.boyun.util.AlarmOrNot，这个现在不用了
//	  * ⑥ com.iiot.boyun.main.module.screen.report.Alarm 报表
//	  * ⑦ 报警报表前台传入英文字符需要添加对应字段
//	  * ⑧ 极光推送掩码校对
	 
	 
}

