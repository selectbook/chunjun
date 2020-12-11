package com.iiot.util;

import com.iiot.commCommon.*;
import com.iiot.common.bytes.Conv;
import org.apache.log4j.Logger;
import java.util.*;

/**
 * 数据序列化
 */
public class YunDataSerialize {

	static Logger logger = Logger.getLogger(YunDataSerialize.class);

	final static int STATE_BIT_EMERGENCYALARM = 0; // 紧急报警(1：报警/0：正常)
	final static int STATE_BIT_OVERSPEED = 1; // 超速报警(1：报警/0：正常)
	final static int STATE_BIT_POWERDOWN = 2; // 终端主电源掉电(1：报警/0：正常)
	final static int STATE_BIT_LOWVOLTAGE = 3; // 终端主电源欠压(1：报警/0：正常)
	final static int STATE_BIT_GPSANTENNASTATE = 4; // GPS天线状态(0：正常/1：开路)
	final static int STATE_BIT_BCARISALARM = 5; // BCAR是否报警(1：报警/0：正常)
	final static int STATE_BIT_ISILLEGALREMOVE = 6; // 非法拆除报警(1：报警/0：正常)
	final static int STATE_BIT_SHOCKALARM = 7; // 震动报警(1：报警/0：正常)
	final static int STATE_BIT_ILLEGALMOVE = 8; // 位移报警(1：报警/0：正常)
	final static int STATE_BIT_ACARBTS = 9; // ACAR蓝牙状态(1：故障/0：正常)
	final static int STATE_BIT_BCARBTS = 10; // BCAR蓝牙状态(1：故障/0：正常)
	final static int STATE_BIT_PSEUDOLBS = 11; // 伪基站报警(1：报警/0：正常)
	final static int STATE_BIT_OUTAREAALARM = 12; // 出围栏报警(1：报警/0：正常)
	final static int STATE_BIT_INAREAALARM = 13; // 进围栏报警(1：报警/0：正常)
	final static int STATE_BIT_BCARSTATE = 14; // BCAR状态(1：检测中.../0：正常)
	final static int STATE_BIT_BTISON_1 = 15; // 蓝牙功能是否开启(bit15-16 01：开启 10：关闭 00：保留 11：保留)
	final static int STATE_BIT_BTISON_2 = 16;
	final static int STATE_BIT_OILISNORMAL = 17; // 油路是否正常(0：正常 1：断开)
	final static int STATE_BIT_PSEUDOLBSSTATE = 18; // 是否检测到伪基站(0：否/1：是)
	final static int STATE_BIT_FATIGUEDRIVE = 19; // 是否疲劳驾驶(0：否/1：是)
	final static int STATE_BIT_ISCHARGE_1 = 20; // 是否接电源充电(bit20-21 01：已接 10：未接  00：保留 11：保留)
	final static int STATE_BIT_ISCHARGE_2 = 21;
	final static int STATE_BIT_ISARM_1 = 22; // 是否设防(bit22-23 01：设防 10：撤防 00：保留 11：保留)
	final static int STATE_BIT_ISARM_2 = 23;
	final static int STATE_BIT_ISREALTIME = 24; // 是否是实时数据(0：实时/1：补传)
	final static int STATE_BIT_COLLIDEALARM = 25; // 碰撞报警(0：正常/1：报警)
	final static int STATE_BIT_ILLEGALSTART = 26; // 非法点火报警(0：正常/1：报警)
	final static int STATE_BIT_POWERSTATE_1 = 27; // 电源状态(01：开启 10：关闭 00：保留 11：保留)
	final static int STATE_BIT_POWERSTATE_2 = 28;
	final static int STATE_BIT_BRAKESTATE_1 = 29; // 刹车状态(01：刹车 10：未刹车 00：保留 11：保留)
	final static int STATE_BIT_BRAKESTATE_2 = 30;
	final static int STATE_BIT_SEARCHSTATE_1 = 31; // 寻车状态(01：寻车 10：未寻车 00：保留 11：保留)
	final static int STATE_BIT_SEARCHSTATE_2 = 32;
	final static int STATE_BIT_ALARMVOICE_1 = 33; // 报警语音状态(01：开启 10：关闭 00：保留 11：保留)
	final static int STATE_BIT_ALARMVOICE_2 = 34;
	final static int STATE_BIT_SOFTSWITCH_1 = 35; // 软开关状态(01：开启 10：关闭 00：保留 11：保留)
	final static int STATE_BIT_SOFTSWITCH_2 = 36;
	final static int STATE_BIT_WAYBILLMODEL_1 = 37; // 运单模式(01：开启 10：关闭 00：保留 11：保留)
	final static int STATE_BIT_WAYBILLMODEL_2 = 38;
	final static int STATE_BIT_GPSSTATE = 39; // GSP状态(1：开启  0：关闭)

	/**
	 * World对象转数组
	 * @param world
	 * @return
	 */
	public byte[] WorldToArray(World world) {
		if (world == null) {
			return null;
		}

		String protocolType = world.getProtocolType(); // 协议类型
		int seqId = (int) world.getSequenceId(); // 流水号
		String vip = world.getId(); // 设备VIP 数据格式127.0.0.1/0000014566668888
		String dataType = world.getObjType(); // 数据类型
		Pos pos = null; // Pos数据
		Object objPos = world.getObj();
		if (objPos instanceof Pos) {
			pos = (Pos) objPos;
		}

		// 协议类型标识1字节，1：A6 2：JT 3：V3
		int protocolTypeFlag = getProtocolTypeFlag(protocolType);

		// 流水号数组2字节
		byte[] seqArray = Conv.short2bytes(seqId);

		// VIP数组8字节
		byte[] vipArray = vipToArray(vip);

		// 数据类型1字节，1：pos(位置数据) 2：alarm(报警数据) 3：supplement(盲区数据) 4：namedPos(点名应答数据)
		int dataTypeFlag = getDataTypeFlag(dataType);

		// Pos数据数组
		int posArrayLen = 0;
		byte[] posArray = PosToArray(pos);
		if (posArray != null) {
			posArrayLen = posArray.length;
		}

		// 组序列化结果包，长度：1 + 2 + 8 + 1 + posArrayLen
		int len = 1 + 2 + 8 + 1 + posArrayLen;
		int offset = 0;
		byte[] retArr = new byte[len];

		// 协议类型
		retArr[offset] = (byte) protocolTypeFlag;
		offset += 1;

		// 流水号
		System.arraycopy(seqArray, 0, retArr, offset, 2);
		offset += 2;

		// VIP
		System.arraycopy(vipArray, 0, retArr, offset, 8);
		offset += 8;

		// 数据类型
		retArr[offset] = (byte) dataTypeFlag;
		offset += 1;

		// Pos数据
		if (posArray != null) {
			System.arraycopy(posArray, 0, retArr, offset, posArrayLen);
		}

		return retArr;
	}

	/**
	 * 数组转World对象
	 * @param array
	 * @return
	 */
	public World WorldFromArray(byte[] array) {
		if (array == null) {
			return null;
		}

		int offset = 0;
		int dataLen = array.length;

		// 协议类型，1字节
		String protocolType = "";
		if (offset + 1 <= dataLen) {
			int protocolTypeFlag = array[offset];
			if (protocolTypeFlag == 1) {
				protocolType = "A6";
			} else if (protocolTypeFlag == 2) {
				protocolType = "JT";
			} else if (protocolTypeFlag == 3) {
				protocolType = "V3";
			} else if (protocolTypeFlag == 4) {
				protocolType = null;
			} else if (protocolTypeFlag == 5) {

			} else {
				protocolType = "A6";
			}
		}
		offset += 1;

		// 流水号，2字节
		int seqId = 0;
		if (offset + 2 <= dataLen) {
			seqId = Conv.getShortNetOrder(array, offset);
		}
		offset += 2;

		// VIP，8字节
		String vip = "";
		if (offset + 8 <= dataLen) {
			vip = vipFromArray(array, offset, 8, protocolType);
		}
		offset += 8;

		// 数据类型，1字节
		String dataType = "";
		if (offset + 1 <= dataLen) {
			int dataTypeFlag = array[offset];
			if (dataTypeFlag == 1) {
				dataType = "pos";
			} else if (dataTypeFlag == 2) {
				dataType = "alarm";
			} else if (dataTypeFlag == 3) {
				dataType = "supplement";
			} else if (dataTypeFlag == 4) {
				dataType = "namedPos";
			} else if (dataTypeFlag == 5) {
				dataType = "ackPacket";
			} else if (dataTypeFlag == 6) {
				dataType = null;
			} else if (dataTypeFlag == 7) {

			} else {
				dataType = "pos";
			}
		}
		offset += 1;

		// Pos，N字节
		Pos pos = PosFromArray(array, offset, dataLen);

		// 封装数据
		World world = new World();
		world.setProtocolType(protocolType);
		world.setSequenceId(seqId);
		world.setId(vip);
		world.setObjType(dataType);
		world.setObj(pos);
		return world;
	}

	/**
	 * VIP反序列化
	 * @param array
	 * @param offset
	 * @param vipLen
	 * @param protocolType
	 * @return
	 */
	private String vipFromArray(byte[] array, int offset, int vipLen, String protocolType) {
		int vipFlag = array[offset];
		if (vipFlag == 0x0A) {
			return null;
		}

		if (vipFlag == 0x0B) {
			return "";
		}

		StringBuffer buf = new StringBuffer();
		String vip = "";
		if ("V3".equals(protocolType)) {
			String vipStr = String.valueOf(Conv.getLongNetOrder(array, offset));
			int len = vipStr.length();
			switch (len) {
			case 16:
				buf.append(vipStr);
				break;
			case 15:
				buf.append("0");
				buf.append(vipStr);
				break;
			case 14:
				buf.append("00");
				buf.append(vipStr);
				break;
			case 13:
				buf.append("000");
				buf.append(vipStr);
				break;
			case 12:
				buf.append("0000");
				buf.append(vipStr);
				break;
			case 11:
				buf.append("00000");
				buf.append(vipStr);
				break;
			default:
				break;
			}

			vip = buf.toString();
		} else {
			offset += 4; // 8字节的后4字节
			byte[] iArr = new byte[4]; // IP数据数组
			System.arraycopy(array, offset, iArr, 0, 4);
			for (byte b : iArr) {
				int tmp = Byte.toUnsignedInt(b);
				if (buf.length() > 0 && !buf.toString().endsWith(".")) {
					buf.append(".");
				}

				buf.append(tmp);
			}
			vip = buf.toString();
		}

		return vip;
	}

	/**
	 * Pos对象转数组
	 * @param pos
	 * @return
	 */
	public byte[] PosToArray(Pos pos) {
		if (pos == null) {
			return null;
		}

		// 固定数据长度，可能会有扩展数据，先把固定的数据序列化。
		int fixLen = 8 + 8 + 8 + 8 + 8 + 8 + 8 + 1 + 1 + 8;
		int offset = 0;
		byte[] data = new byte[fixLen];
		// 终端时间(8字节)
		Date devTime = pos.getDevTime();
		if (devTime != null) {
			Conv.setLongNetOrder(data, offset, devTime.getTime());
		} else {
			Conv.setLongNetOrder(data, offset, -1);
		}
		offset += 8;

		// 平台接收时间(8字节)
		Date recvTime = pos.getRecvTime();
		if (recvTime != null) {
			Conv.setLongNetOrder(data, offset, recvTime.getTime());
		} else {
			Conv.setLongNetOrder(data, offset, -1);
		}
		offset += 8;

		// 经度(8字节)
		Double lon = pos.getLon();
		if (lon != null) {
			Conv.setLongNetOrder(data, offset, Double.doubleToLongBits(lon));
		} else {
			Conv.setLongNetOrder(data, offset, Double.doubleToLongBits(-1));
		}
		offset += 8;

		// 纬度(8字节)
		Double lat = pos.getLat();
		if (lat != null) {
			Conv.setLongNetOrder(data, offset, Double.doubleToLongBits(lat));
		} else {
			Conv.setLongNetOrder(data, offset, Double.doubleToLongBits(-1));
		}
		offset += 8;

		// 速度(8字节)
		Double speed = pos.getSpeed();
		if (speed != null) {
			Conv.setLongNetOrder(data, offset, Double.doubleToLongBits(speed));
		} else {
			Conv.setLongNetOrder(data, offset, Double.doubleToLongBits(-1));
		}
		offset += 8;

		// 方向(8字节)
		Double direct = pos.getDirect();
		if (direct != null) {
			Conv.setLongNetOrder(data, offset, Double.doubleToLongBits(direct));
		} else {
			Conv.setLongNetOrder(data, offset, Double.doubleToLongBits(-1));
		}
		offset += 8;

		// 里程(8字节)
		Double mileage = pos.getMileage();
		if (mileage != null) {
			Conv.setLongNetOrder(data, offset, Double.doubleToLongBits(mileage));
		} else { // 无里程设备
			Conv.setLongNetOrder(data, offset, Double.doubleToLongBits(-1));
		}
		offset += 8;

		// ACC (1：关/0：开/2：不解析ACC)
		int accFlag = 2;
		Integer isAcc = pos.getIsAcc();
		if (isAcc != null) {
			accFlag = isAcc;
		}
		data[offset] = (byte) accFlag;
		offset += 1;

		// 定位状态 (0：不定位，1：GPS, 2：wifi, 3：多基站，4：单基站)
		int isPosFlag = 0;
		Integer isPos = pos.getIsPos();
		if (isPos != null) {
			isPosFlag = isPos;
		}
		data[offset] = (byte) isPosFlag;
		offset += 1;

		// 报警状态位(8字节)，格式化状态位
		long state = getPosState(pos);
		Conv.setLongNetOrder(data, offset, state);
		offset += 8;

		// 扩展数据
		Map<String, Object> extend = pos.getExtend();
		byte[] extendArray = extendToArray(extend);
		int extendLen = 0;
		if (extendArray != null) {
			extendLen = extendArray.length;
		}

		int index = 0;
		int len = fixLen + extendLen;
		byte[] ret = new byte[len];
		System.arraycopy(data, 0, ret, index, fixLen);
		index += fixLen;

		if (extendArray != null) {
			System.arraycopy(extendArray, 0, ret, index, extendLen);
		}

		return ret;
	}

	/**
	 * 扩展数据转数组
	 * @param extend
	 * @return
	 */
	private byte[] extendToArray(Map<String, Object> extend) {
		if (extend == null) {
			return null;
		}

		// 数据上报类型数组
		byte[] upTypeArray = upTypeToArray(extend);
		int upTypeArrayLen = 0;
		if (upTypeArray != null) {
			upTypeArrayLen = upTypeArray.length;
		}

		// 电压等级数组
		byte[] volGradeArray = volGradeToArray(extend);
		int volGradeArrayLen = 0;
		if (volGradeArray != null) {
			volGradeArrayLen = volGradeArray.length;
		}

		// 数据类型标识，心跳 OR 位置数据
		byte[] dataTypeArray = dataTypeToArray(extend);
		int dataTypeArrayLen = 0;
		if (dataTypeArray != null) {
			dataTypeArrayLen = dataTypeArray.length;
		}

		// 电量数组 ID(1字节) + 长度(1字节) + 电量(8字节)
		byte[] powerArray = powerToArray(extend);
		int powerArrayLen = 0;
		if (powerArray != null) {
			powerArrayLen = powerArray.length;
		}

		// 单基站
		byte[] sbsArray = singleBaseStationToArray(extend);
		int sbsArrayLen = 0;
		if (sbsArray != null) {
			sbsArrayLen = sbsArray.length;
		}

		// 多基站
		byte[] mbsArray = multipleBaseStationToArray(extend);
		int mbsArrayLen = 0;
		if (mbsArray != null) {
			mbsArrayLen = mbsArray.length;
		}

		// WIFI
		byte[] wifiArray = wifiToArray(extend);
		int wifiArrayLen = 0;
		if (wifiArray != null) {
			wifiArrayLen = wifiArray.length;
		}

		// 原始数据
		byte[] sourceDataArray = sourceDataToArray(extend);
		int sourceDataArrayLen = 0;
		if (sourceDataArray != null) {
			sourceDataArrayLen = sourceDataArray.length;
		}

		// 电压值
		byte[] voltageArray = voltageToArray(extend);
		int voltageArrayLen = 0;
		if (voltageArray != null) {
			voltageArrayLen = voltageArray.length;
		}

		// channelId
		byte[] channelIdArr = channelIdToArray(extend);
		int channelIdArrLen = 0;
		if (channelIdArr != null) {
			channelIdArrLen = channelIdArr.length;
		}

		// ip
		byte[] ipArr = ipToArray(extend);
		int ipArrLen = 0;
		if (ipArr != null) {
			ipArrLen = ipArr.length;
		}

		// ICCID
		byte[] iccidArr = iccidToArray(extend);
		int iccidArrLen = 0;
		if (iccidArr != null) {
			iccidArrLen = iccidArr.length;
		}

		int offset = 0;
		int len = upTypeArrayLen + volGradeArrayLen + dataTypeArrayLen + powerArrayLen + sbsArrayLen + mbsArrayLen
				+ wifiArrayLen + sourceDataArrayLen + voltageArrayLen + channelIdArrLen + ipArrLen + iccidArrLen;
		byte[] ret = new byte[len];
		if (upTypeArray != null) {
			System.arraycopy(upTypeArray, 0, ret, offset, upTypeArrayLen); // 数据上报类型
		}
		offset += upTypeArrayLen;

		if (volGradeArray != null) {
			System.arraycopy(volGradeArray, 0, ret, offset, volGradeArrayLen); // 电压等级
		}
		offset += volGradeArrayLen;

		if (dataTypeArray != null) {
			System.arraycopy(dataTypeArray, 0, ret, offset, dataTypeArrayLen); // 数据类型
		}
		offset += dataTypeArrayLen;

		if (powerArray != null) {
			System.arraycopy(powerArray, 0, ret, offset, powerArrayLen); // 电量
		}
		offset += powerArrayLen;

		if (sbsArray != null) {
			System.arraycopy(sbsArray, 0, ret, offset, sbsArrayLen); // 单基站
		}
		offset += sbsArrayLen;

		if (mbsArray != null) {
			System.arraycopy(mbsArray, 0, ret, offset, mbsArrayLen); // 多基站
		}
		offset += mbsArrayLen;

		if (wifiArray != null) {
			System.arraycopy(wifiArray, 0, ret, offset, wifiArrayLen); // WIFI数据
		}
		offset += wifiArrayLen;

		if (sourceDataArray != null) {
			System.arraycopy(sourceDataArray, 0, ret, offset, sourceDataArrayLen); // 原始数据
		}
		offset += sourceDataArrayLen;

		if (voltageArray != null) {
			System.arraycopy(voltageArray, 0, ret, offset, voltageArrayLen); // 电压值
		}
		offset += voltageArrayLen;

		if (channelIdArr != null) {
			System.arraycopy(channelIdArr, 0, ret, offset, channelIdArrLen); // channelId
		}
		offset += channelIdArrLen;

		if (ipArr != null) {
			System.arraycopy(ipArr, 0, ret, offset, ipArrLen); // IP
		}
		offset += ipArrLen;

		if (iccidArr != null) {
			System.arraycopy(iccidArr, 0, ret, offset, iccidArrLen); // ICCID
		}
		offset += iccidArrLen;
		return ret;
	}

	/**
	 * ICCID序列化
	 * @param extend
	 * @return
	 */
	private byte[] iccidToArray(Map<String, Object> extend) {
		// ICCID数组 ID(1字节) + 长度(2字节) + ICCID内容(n字节)
		byte[] ret = null;
		String iccid = null;
		Object obj = extend.get("iccid");
		if (obj instanceof String) {
			iccid = (String) obj;
			byte[] arr = iccid.getBytes();
			int arrLen = arr.length;
			byte[] lenArr = Conv.short2bytes(arrLen);

			int offset = 0;
			ret = new byte[1 + 2 + arrLen];
			ret[offset++] = 0x5B;

			System.arraycopy(lenArr, 0, ret, offset, 2);
			offset += 2;

			System.arraycopy(arr, 0, ret, offset, arrLen);
			offset += arrLen;
		}

		return ret;
	}

	/**
	 * IP序列化
	 * @param extend
	 * @return
	 */
	private byte[] ipToArray(Map<String, Object> extend) {
		// IP数组 ID(1字节) + 长度(2字节) + IP内容(4字节)
		byte[] ret = null;
		String ip = null;
		Object obj = extend.get("ip");
		if (obj instanceof String) {
			ip = (String) obj;
			if (ip != null && !"".equals(ip)) {
				String[] ipStrArr = ip.split("\\.");
				int len = ipStrArr.length;
				byte[] ipArr = new byte[4];
				for (int i = 0; i < len; i++) {
					ipArr[i] = (byte) Integer.parseInt(ipStrArr[i]);
				}
				ret = new byte[7];
				int offset = 0;
				ret[offset++] = 0x4B;
				ret[offset++] = 0x00;
				ret[offset++] = 0x04;
				System.arraycopy(ipArr, 0, ret, offset, 4);
				offset += 4;
			}
		}
		return ret;
	}

	/**
	 * channelId序列化
	 * @param extend
	 * @return
	 */
	private byte[] channelIdToArray(Map<String, Object> extend) {
		// channelId数组 ID(1字节) + 长度(2字节) + ID内容(N字节)
		byte[] ret = null;
		String idStr = null;
		Object obj = extend.get("cId");
		if (obj instanceof String) {
			idStr = (String) obj;
			if (idStr != null) {
				byte[] idArr = idStr.getBytes();
				int idArrLen = idArr.length;
				byte[] lenArr = Conv.short2bytes(idArrLen);
				ret = new byte[1 + 2 + idArrLen];
				int offset = 0;
				ret[offset++] = 0x3B;
				System.arraycopy(lenArr, 0, ret, offset, 2);
				offset += 2;

				System.arraycopy(idArr, 0, ret, offset, idArrLen);
				offset += idArrLen;
			}
		}

		return ret;
	}

	/**
	 * 电压值序列化
	 * @param extend
	 * @return
	 */
	private byte[] voltageToArray(Map<String, Object> extend) {
		// 电压值数组 ID(1字节) + 长度(2字节) + 电压值(4字节)
		byte[] ret = null;
		int voltage = 0;
		Object obj = extend.get("voltage");
		if (obj instanceof Double) {
			voltage = (int) (((double) obj) * 1000);
			byte[] vArr = Conv.int2bytes(voltage);
			ret = new byte[7];
			int offset = 0;
			ret[offset++] = 0x1B;
			ret[offset++] = 0x00;
			ret[offset++] = 0x04;
			System.arraycopy(vArr, 0, ret, offset, 4);
		}

		return ret;
	}

	/**
	 * 数据上报类型序列化
	 * @param extend
	 * @return
	 */
	private byte[] upTypeToArray(Map<String, Object> extend) {
		// 数据上报类型数组 ID(1字节) + 长度(2字节) + 上报类型(1字节)
		byte[] upTypeArray = null;
		int upType = -1; // 数据上报类型
		Object obj = extend.get("upType");
		if (obj instanceof Integer) {
			upType = (Integer) obj;
			upTypeArray = new byte[4];
			upTypeArray[0] = 0x0D;
			upTypeArray[1] = 0x00;
			upTypeArray[2] = 0x01;
			upTypeArray[3] = (byte) upType;
		}
		return upTypeArray;
	}

	/**
	 * 电压等级序列化
	 * @param extend
	 * @return
	 */
	private byte[] volGradeToArray(Map<String, Object> extend) {
		// 电压等级数组 ID(1字节) + 长度(2字节) + 电压等级(1字节)
		byte[] volGradeArray = null;
		int grade = -1; // 电压等级
		Object obj = extend.get("volGrade");
		if (obj instanceof Integer) {
			grade = (Integer) obj;
			volGradeArray = new byte[4];
			volGradeArray[0] = 0x1D;
			volGradeArray[1] = 0x00;
			volGradeArray[2] = 0x01;
			volGradeArray[3] = (byte) grade;
		}

		return volGradeArray;
	}

	/**
	 * 数据类型序列化
	 * @param extend
	 * @return
	 */
	private byte[] dataTypeToArray(Map<String, Object> extend) {
		// 数据类型标识，心跳 OR 位置数据 ID(1字节) + 长度(2字节) + 数据类型(1字节)
		byte[] dataTypeArray = null;
		int dataType = -1;
		Object obj = extend.get("dataType");
		if (obj instanceof Integer) {
			dataType = (Integer) obj;
			dataTypeArray = new byte[4];
			dataTypeArray[0] = 0x2D;
			dataTypeArray[1] = 0x00;
			dataTypeArray[2] = 0x01;
			dataTypeArray[3] = (byte) dataType;
		}

		return dataTypeArray;
	}

	/**
	 * 电量序列化
	 * @param extend
	 * @return
	 */
	private byte[] powerToArray(Map<String, Object> extend) {
		// 电量数组 ID(1字节) + 长度(2字节) + 电量(8字节)
		Object obj = extend.get("power");
		byte[] powerArray = null;
		if (obj != null) {
			int index = 0;
			Power power = (Power) obj;
			int totalPower = power.getTotalPower(); // 总电量
			int surplusPower = power.getSurplusPower(); // 剩余电量
			byte[] tArr = Conv.int2bytes(totalPower);
			byte[] sArr = Conv.int2bytes(surplusPower);
			powerArray = new byte[11];
			powerArray[index] = 0x3D; // ID
			index += 1;

			byte[] lArr = Conv.short2bytes(8);
			System.arraycopy(lArr, 0, powerArray, index, 2); // 长度
			index += 2;

			System.arraycopy(tArr, 0, powerArray, index, 4); // 总电量
			index += 4;

			System.arraycopy(sArr, 0, powerArray, index, 4); // 剩余电量		
		}

		return powerArray;
	}

	/**
	 * 单基站序列化
	 * @param extend
	 * @return
	 */
	private byte[] singleBaseStationToArray(Map<String, Object> extend) {
		// 单基站
		SingleBaseStation sbs = null; // 单基站数据
		byte[] sbsArray = null;
		Object obj = extend.get("sbs");
		if (obj instanceof SingleBaseStation) {
			sbs = (SingleBaseStation) obj;
			int mcc = sbs.getMcc();
			int mnc = sbs.getMnc();
			int lac = sbs.getLac();
			int cel = sbs.getCel();
			byte[] mccArr = Conv.short2bytes(mcc);
			byte[] lacArr = Conv.int2bytes(lac);
			byte[] celArr = Conv.int2bytes(cel);

			// 单基站字符串
			byte[] sbsStrArray = null;
			String sbsStr = null;
			Object sbsStrObj = extend.get("sinLbs");
			if (sbsStrObj instanceof String) {
				sbsStr = (String) sbsStrObj;
				sbsStrArray = sbsStr.getBytes();
			}
			int sbsStrArrayLen = 0;
			if (sbsStrArray != null) {
				sbsStrArrayLen = sbsStrArray.length;
			}

			// 数据内容长度数组，包括基站数据长度 + 基站数据字符串长度
			byte[] lArr = Conv.short2bytes(11 + sbsStrArrayLen);
			int index = 0;
			int sbsArrayLen = 1 + 2 + 11 + sbsStrArrayLen;
			sbsArray = new byte[sbsArrayLen];
			sbsArray[index] = 0x4D; // ID
			index += 1;

			System.arraycopy(lArr, 0, sbsArray, index, 2); // 长度
			index += 2;

			System.arraycopy(mccArr, 0, sbsArray, index, 2); // 国号
			index += 2;

			sbsArray[index] = (byte) mnc; // 运营商号
			index += 1;

			System.arraycopy(lacArr, 0, sbsArray, index, 4); // 区号
			index += 4;

			System.arraycopy(celArr, 0, sbsArray, index, 4); // 塔号
			index += 4;

			if (sbsStrArray != null) {
				System.arraycopy(sbsStrArray, 0, sbsArray, index, sbsStrArrayLen); // 塔号
			}
		}

		return sbsArray;
	}

	/**
	 * 多基站序列化
	 * @param extend
	 * @return
	 */
	private byte[] multipleBaseStationToArray(Map<String, Object> extend) {
		byte[] mbsArray = null;
		MultipleBaseStation mbs = null; // 多基站数据
		Object mbsObj = extend.get("mbs");
		byte[] arr = null;
		if (mbsObj instanceof MultipleBaseStation) {
			mbs = (MultipleBaseStation) mbsObj;
			int mcc = mbs.getMcc(); // 国号
			int mnc = mbs.getMnc(); // 运营商号
			List<BaseStationInfo> info = mbs.getInfo();
			if (info == null || info.isEmpty()) {
				return null;
			}

			int index = 0;
			int size = info.size();
			int mulLen = 2 + 1 + 1 + 10 * size; // 数组长度
			arr = new byte[mulLen];
			byte[] mccArray = Conv.short2bytes(mcc);
			System.arraycopy(mccArray, 0, arr, index, 2); // 国号
			index += 2;

			arr[index] = (byte) mnc; // 运营商
			index += 1;

			arr[index] = (byte) size; // 基站组数
			index += 1;

			for (BaseStationInfo bsi : info) {
				if (bsi != null) {
					int lac = bsi.getLac();
					int cel = bsi.getCel();
					int sig = bsi.getSig();
					byte[] lacArr = Conv.int2bytes(lac);
					byte[] celArr = Conv.int2bytes(cel);
					byte[] sigArr = Conv.short2bytes(sig);
					System.arraycopy(lacArr, 0, arr, index, 4); // 区号
					index += 4;

					System.arraycopy(celArr, 0, arr, index, 4); // 塔号
					index += 4;

					System.arraycopy(sigArr, 0, arr, index, 2); // 信号强度
					index += 2;
				}
			}

			String mulStr = null;
			byte[] mulStrArray = null;
			Object mulStrObj = extend.get("mulLbs");
			if (mulStrObj instanceof String) {
				mulStr = (String) mulStrObj;
				mulStrArray = mulStr.getBytes();
			}

			int mulStrArrayLen = 0;
			if (mulStrArray != null) {
				mulStrArrayLen = mulStrArray.length;
			}

			int offset = 0;
			int len = 1 + 2 + mulLen + mulStrArrayLen; // 多基站序列化长度
			mbsArray = new byte[len];
			mbsArray[offset] = 0x5D; // ID
			offset += 1;

			int cLen = mulLen + mulStrArrayLen;
			byte[] lArr = Conv.short2bytes(cLen); // 数据内容长度数组
			System.arraycopy(lArr, 0, mbsArray, offset, 2); // 长度
			offset += 2;

			System.arraycopy(arr, 0, mbsArray, offset, mulLen); // 多基站对象
			offset += mulLen;

			if (mulStrArray != null) {
				System.arraycopy(mulStrArray, 0, mbsArray, offset, mulStrArrayLen); // 多基站字符串
			}
		}

		return mbsArray;
	}

	/**
	 * wifi序列化
	 * @param extend
	 * @return
	 */
	private byte[] wifiToArray(Map<String, Object> extend) {
		byte[] wifiArray = null;
		List<WifiInfo> wifiList = null; // WIFI
		Object wifiObj = extend.get("wi");
		if (wifiObj instanceof List) {
			wifiList = (List<WifiInfo>) wifiObj;
			int size = 0;
			int wLen = 0;
			byte[] arr = null;
			if (wifiList != null && !wifiList.isEmpty()) {
				size = wifiList.size();
				wLen = 1 + 21 * size;

				int index = 0;
				arr = new byte[wLen];
				arr[index] = (byte) size; // wifi组数
				index += 1;

				for (WifiInfo wifi : wifiList) {
					int macIndex = 0;
					int sigIndex = 0;
					if (wifi != null) {
						String mac = wifi.getMac();
						int sig = wifi.getSig();
						byte[] macArray = new byte[17];
						char[] cMac = mac.toCharArray();
						for (char c : cMac) {
							macArray[macIndex++] = (byte) c;
						}

						byte[] sigArray = new byte[3];
						char[] cSig = String.valueOf(sig).toCharArray();
						for (char c : cSig) {
							sigArray[sigIndex++] = (byte) c;
						}

						System.arraycopy(macArray, 0, arr, index, 17); // MAC地址
						index += 17;

						arr[index] = 0x2C;
						index += 1;

						System.arraycopy(sigArray, 0, arr, index, 3); // 信号值
						index += 3;
					}
				}
			} else {
				return null;
			}

			// WIFI字符串
			String wifiStr = null;
			Object wifiStrObj = extend.get("wf");
			byte[] wifiStrArray = null;
			if (wifiStrObj instanceof String) {
				wifiStr = (String) wifiStrObj;
				wifiStrArray = wifiStr.getBytes();
			}
			int wifiStrArrayLen = 0;
			if (wifiStrArray != null) {
				wifiStrArrayLen = wifiStrArray.length;
			}

			int offset = 0;
			int len = 1 + 2 + wLen + wifiStrArrayLen;
			wifiArray = new byte[len];
			wifiArray[offset] = 0x6D; // ID
			offset += 1;

			int cLen = wLen + wifiStrArrayLen;
			byte[] lArr = Conv.short2bytes(cLen);
			System.arraycopy(lArr, 0, wifiArray, offset, 2); // 数据内容长度
			offset += 2;

			System.arraycopy(arr, 0, wifiArray, offset, wLen); // WIFI对象
			offset += wLen;

			if (wifiStrArray != null) {
				System.arraycopy(wifiStrArray, 0, wifiArray, offset, wifiStrArrayLen); // WIFI对象
			}
		}

		return wifiArray;
	}

	/**
	 * 原始数据序列化
	 * @param extend
	 * @return
	 */
	private byte[] sourceDataToArray(Map<String, Object> extend) {
		String sourceDataStr = null;
		Object srcDataObj = extend.get("sd");
		if (srcDataObj instanceof String) {
			sourceDataStr = (String) srcDataObj;
		}

		if (sourceDataStr == null) {
			return null;
		}

		byte[] arr = sourceDataStr.getBytes();
		int arrLen = 0;
		if (arr != null) {
			arrLen = arr.length;
		}

		int offset = 0;
		int retLen = 1 + 2 + arrLen;
		byte[] ret = new byte[retLen];
		ret[offset] = 0x0B; // ID
		offset += 1;

		byte[] lArr = Conv.short2bytes(arrLen);
		System.arraycopy(lArr, 0, ret, offset, 2); // 数据内容长度
		offset += 2;

		if (arr != null) {
			System.arraycopy(arr, 0, ret, offset, arrLen);
		}

		return ret;
	}

	/**
	 * 数组转Pos对象
	 * @param array
	 * @return
	 */
	public Pos PosFromArray(byte[] array, int data_offset, int dataLen) {
		if (array == null) {
			return null;
		}

		int offset = data_offset;
		if (offset + 8 > dataLen) {
			return null;
		}

		Pos pos = new Pos();

		// 终端发送时间(8字节)
		if (offset + 8 <= dataLen) {
			long devMil = Conv.getLongNetOrder(array, offset);
			if (devMil != -1) {
				pos.setDevTime(new Date(devMil));
			}
		}
		offset += 8;

		// 平台接收时间(8字节)
		if (offset + 8 <= dataLen) {
			long recvMil = Conv.getLongNetOrder(array, offset);
			if (recvMil != -1) {
				pos.setRecvTime(new Date(recvMil));
			}
		}
		offset += 8;

		// 经度(8字节)
		if (offset + 8 <= dataLen) {
			long lon = Conv.getLongNetOrder(array, offset);
			if (lon != -1) {
				pos.setLon(Double.longBitsToDouble(lon));
			}
		}
		offset += 8;

		// 纬度(8字节)
		if (offset + 8 <= dataLen) {
			long lat = Conv.getLongNetOrder(array, offset);
			if (lat != -1) {
				pos.setLat(Double.longBitsToDouble(lat));
			}
		}
		offset += 8;

		// 速度(8字节)
		if (offset + 8 <= dataLen) {
			long speed = Conv.getLongNetOrder(array, offset);
			if (speed != -1) {
				pos.setSpeed(Double.longBitsToDouble(speed));
			}
		}
		offset += 8;

		// 方向(8字节)
		if (offset + 8 <= dataLen) {
			long direct = Conv.getLongNetOrder(array, offset);
			if (direct != -1) {
				pos.setDirect(Double.longBitsToDouble(direct));
			}
		}
		offset += 8;

		// 里程(8字节)
		if (offset + 8 <= dataLen) {
			long mileage = Conv.getLongNetOrder(array, offset);
			if (mileage != -1) {
				pos.setMileage(Double.longBitsToDouble(mileage));
			}
		}
		offset += 8;

		// ACC状态
		if (offset + 1 <= dataLen) {
			int accFlag = Byte.toUnsignedInt(array[offset]);
			if (accFlag == 0 || accFlag == 1) {
				pos.setIsAcc(accFlag);
			}
		}
		offset += 1;

		// 定位状态
		if (offset + 1 <= dataLen) {
			int isPosFlag = Byte.toUnsignedInt(array[offset]);
			pos.setIsPos(isPosFlag);
		}
		offset += 1;

		// 状态位(8字节)，格式化状态位
		long state = 0;
		if (offset + 8 <= dataLen) {
			state = Conv.getLongNetOrder(array, offset);
		}
		offset += 8;
		Map<String, Object> extend = getExtendFromPosState(state);

		// 扩展数据
		Map<String, Object> retMap = extendFromArray(array, offset, dataLen);
		if (retMap != null) {
			extend.putAll(retMap);
		}
		pos.setExtend(extend);
		return pos;
	}

	/**
	 * 扩展数据反序列化[包括单基站、多基站、WIFI数据]
	 * @param array
	 * @param data_offset
	 * @param dataLen
	 * @return
	 */
	private Map<String, Object> extendFromArray(byte[] array, int data_offset, int dataLen) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		int offset = data_offset;
		final int MAX_LOOP_COUNT = 100;
		int loopCount = 0;
		while (offset + 3 < dataLen) {
			// 避免死循环
			if (loopCount++ > MAX_LOOP_COUNT) {
				break;
			}

			// ID
			int msgId = Byte.toUnsignedInt(array[offset]);
			offset += 1;

			// 数据长度
			int len = Conv.getShortNetOrder(array, offset);
			offset += 2;

			// 判断是否越界
			if (offset + len > dataLen) {
				break;
			}

			int parserBytes = doParser(array, msgId, len, offset, retMap);
			if (len != parserBytes) {
				logger.info("Extend data error or unparser: " + msgId);
			}

			offset += len;

		}

		return retMap;
	}

	/**
	 * 反序列化各个扩展数据
	 * @param data 数据
	 * @param cmd 数据ID
	 * @param len 数据内容长度
	 * @param data_offset 数据内容开始索引
	 * @param map 缓存数据的MAP
	 * @return
	 */
	private int doParser(byte[] data, int cmd, int len, int data_offset, Map<String, Object> map) {
		int parserBytes = 0;
		int offset = data_offset;
		switch (cmd) {
		case 0x0D: // GPS数据上报类型
			if (len != 1) {
				logger.error("Command(0x0D) Data length error:len != 1 len=" + len);
				break;
			}

			int upType = Byte.toUnsignedInt(data[offset]);
			map.put("upType", upType);
			parserBytes += len;
			break;

		case 0x1D: // 电压等级
			if (len != 1) {
				logger.error("Command(0x1D) Data length error:len != 1 len=" + len);
				break;
			}

			int volGrade = Byte.toUnsignedInt(data[offset]);
			map.put("volGrade", volGrade);
			parserBytes += len;
			break;

		case 0x2D: // 数据类型标识
			if (len != 1) {
				logger.error("Command(0x1D) Data length error:len != 1 len=" + len);
				break;
			}

			int dataType = Byte.toUnsignedInt(data[offset]);
			map.put("dataType", dataType);
			parserBytes += len;
			break;

		case 0x3D: // 电量
			if (len != 8) {
				logger.error("Command(0x1D) Data length error:len != 1 len=" + len);
				break;
			}

			int totalPower = (int) Conv.getIntNetOrder(data, offset); // 总电量
			offset += 4;

			int surplusPower = (int) Conv.getIntNetOrder(data, offset); // 剩余电量
			Power power = new Power();
			power.setTotalPower(totalPower);
			power.setSurplusPower(surplusPower);
			map.put("power", power);
			parserBytes += len;
			break;

		case 0x4D: // 单基站
			Map<String, Object> sbsMap = doSingleBaseStation(data, offset, len);
			if (sbsMap != null) {
				map.putAll(sbsMap);
			}
			parserBytes += len;
			break;

		case 0x5D: // 多基站
			Map<String, Object> mulMap = doMultipleBaseStation(data, offset, len);
			if (mulMap != null) {
				map.putAll(mulMap);
			}
			parserBytes += len;
			break;

		case 0x6D: // WIFI
			Map<String, Object> wifiMap = doWifiData(data, offset, len);
			if (wifiMap != null) {
				map.putAll(wifiMap);
			}
			parserBytes += len;
			break;

		case 0x0B: // 原始数据
			byte[] sourceDataArray = new byte[len];
			System.arraycopy(data, offset, sourceDataArray, 0, len);
			String sourceData = new String(sourceDataArray);
			if (sourceData != null) {
				map.put("sd", sourceData);
			}
			parserBytes += len;
			break;

		case 0x1B: // 电压值
			if (len != 4) {
				logger.error("Command(0x1B) Data length error:len != 4 len=" + len);
				break;
			}

			double voltage = Conv.getIntNetOrder(data, offset) / 1000.0;
			map.put("voltage", voltage);
			parserBytes += len;
			break;

		case 0x3B: // channelId
			byte[] channelIdArr = new byte[len];
			System.arraycopy(data, offset, channelIdArr, 0, len);
			String channelIdStr = new String(channelIdArr);
			if (channelIdStr != null && !"".equals(channelIdStr)) {
				map.put("cId", channelIdStr);
			}
			parserBytes += len;
			break;

		case 0x4B: // ip
			if (len != 4) {
				logger.error("Command(0x4B) Data length error:len != 4 len=" + len);
				break;
			}

			String ipStr = Conv.getVip(data, offset, 4);
			if (ipStr != null) {
				map.put("ip", ipStr);
			}
			parserBytes += len;
			break;

		case 0x5B: // iccid
			byte[] iccidArr = new byte[len];
			System.arraycopy(data, offset, iccidArr, 0, len);
			String iccid = new String(iccidArr);
			if (iccid != null && !"".equals(iccid)) {
				map.put("iccid", iccid);
			}
			parserBytes += len;
			break;
		default:
			parserBytes += len;
			break;
		}

		return parserBytes;
	}

	/**
	 * 解WIFI数据
	 * @param data 数据
	 * @param data_offset WIFI内容开始索引
	 * @param len 数据内容长度
	 * @return
	 */
	private Map<String, Object> doWifiData(byte[] data, int data_offset, int len) {
		int offset = data_offset;
		Map<String, Object> retMap = new HashMap<String, Object>();
		int count = Byte.toUnsignedInt(data[offset]);
		offset += 1;

		List<WifiInfo> list = new LinkedList<WifiInfo>();
		for (int i = 0; i < count; i++) {
			int subLen = 21;
			int index = 0;
			byte[] subArr = new byte[subLen];
			System.arraycopy(data, offset, subArr, 0, subLen);
			offset += subLen;

			String wifi = wifiDataToString(subArr, 0, subLen);
			String[] array = wifi.split(",");
			int arrayLen = 0;
			if (array != null) {
				arrayLen = array.length;
			}

			String mac = "";
			int sig = 0;
			if (array != null && index + 1 <= arrayLen) {
				mac = array[0];
			}
			index += 1;

			if (array != null && index + 1 <= arrayLen) {
				try {
					sig = Integer.parseInt(array[1]);
				} catch (Exception e) {
					logger.error("数据转换异常：" + ExceptionUtil.getStackStr(e) + "，数据：" + wifi);
				}
			}

			WifiInfo info = new WifiInfo(mac, sig);
			list.add(info);
		}

		retMap.put("wi", list);

		// WIFI字符串
		int wifiLen = len - (1 + 21 * count);
		byte[] wifiArr = new byte[wifiLen];
		System.arraycopy(data, offset, wifiArr, 0, wifiLen);
		String wf = new String(wifiArr);
		retMap.put("wf", wf);
		return retMap;
	}

	/**
	 * WIFI数据转字符串
	 * @param data WIFI数据
	 * @param index 索引
	 * @return
	 */
	private String wifiDataToString(byte[] data, int index, int dataLen) {
		StringBuffer buf = new StringBuffer();
		char temp;
		for (int i = index; i < index + dataLen; i++) {
			temp = (char) data[i];
			buf.append(temp);
		}

		return buf.toString();
	}

	/**
	 * 解多基站
	 * @param data 数据
	 * @param data_offset 多基站内容开始索引
	 * @param len 数据内容长度
	 * @return
	 */
	private Map<String, Object> doMultipleBaseStation(byte[] data, int data_offset, int len) {
		int offset = data_offset;
		Map<String, Object> retMap = new HashMap<String, Object>();
		int mcc = Conv.getShortNetOrder(data, offset); // 国号
		offset += 2;

		int mnc = Byte.toUnsignedInt(data[offset]); // 运营商号
		offset += 1;

		int count = Byte.toUnsignedInt(data[offset]); // 多基站组数
		offset += 1;

		List<BaseStationInfo> infoList = new LinkedList<BaseStationInfo>();
		for (int i = 0; i < count; i++) {
			int lac = (int) Conv.getIntNetOrder(data, offset); // 区号
			offset += 4;

			int cel = (int) Conv.getIntNetOrder(data, offset); // 塔号
			offset += 4;

			int sig = Conv.getShortNetOrder(data, offset); // 信号强度
			offset += 2;

			BaseStationInfo info = new BaseStationInfo(lac, cel, sig);
			infoList.add(info);
		}

		MultipleBaseStation multipleBaseStation = new MultipleBaseStation(mcc, mnc, infoList);
		retMap.put("mbs", multipleBaseStation);

		// 多基站字符串
		int mulLen = len - (2 + 1 + 1 + 10 * count);
		byte[] mulArr = new byte[mulLen];
		System.arraycopy(data, offset, mulArr, 0, mulLen);
		String mulLbs = new String(mulArr);
		retMap.put("mulLbs", mulLbs);
		return retMap;
	}

	/**
	 * 解单基站
	 * @param data 数据
	 * @param data_offset 单基站内容开始索引
	 * @param len 数据内容长度
	 * @return
	 */
	private Map<String, Object> doSingleBaseStation(byte[] data, int data_offset, int len) {
		int offset = data_offset;
		Map<String, Object> retMap = new HashMap<String, Object>();
		int mcc = Conv.getShortNetOrder(data, offset); // 国号
		offset += 2;

		int mnc = Byte.toUnsignedInt(data[offset]); // 运营商号
		offset += 1;

		int lac = (int) Conv.getIntNetOrder(data, offset); // 区号
		offset += 4;

		int cel = (int) Conv.getIntNetOrder(data, offset); // 塔号
		offset += 4;

		SingleBaseStation singleBaseStation = new SingleBaseStation(mcc, mnc, lac, cel, 0);
		retMap.put("sbs", singleBaseStation);

		// 单基站字符串
		int sbsLen = len - 11;
		byte[] sbsArr = new byte[sbsLen];
		System.arraycopy(data, offset, sbsArr, 0, sbsLen);
		String sinLbs = new String(sbsArr);
		retMap.put("sinLbs", sinLbs);
		return retMap;
	}

	/**
	 * 获取数据类型标识
	 * @param str
	 * @return
	 */
	private int getDataTypeFlag(String str) {
		int ret = 0;
		if (str == null) {
			return 6;
		}

		if ("".equals(str)) {
			return 7;
		}

		switch (str) {
		case "pos":
			ret = 1;
			break;

		case "alarm":
			ret = 2;
			break;

		case "supplement":
			ret = 3;
			break;

		case "namedPos":
			ret = 4;
			break;

		case "ackPacket":
			ret = 5;
			break;

		default:
			ret = 1;
			break;
		}

		return ret;
	}

	/**
	 * 获取协议类型标识
	 * @param str
	 * @return
	 */
	private int getProtocolTypeFlag(String str) {
		int ret = 0;
		if (str == null) {
			return 4;
		}

		if ("".equals(str)) {
			return 5;
		}

		str = str.toUpperCase();
		switch (str) {
		case "A6":
			ret = 1;
			break;

		case "JT":
			ret = 2;
			break;

		case "V3":
			ret = 3;
			break;

		default:
			ret = 1;
			break;
		}

		return ret;
	}

	/**
	 * VIP序列化
	 * @param vip
	 * @return
	 */
	private byte[] vipToArray(String vip) {
		int index = 8;
		byte[] arr = new byte[8];
		if (vip == null) {
			arr[0] = 0x0A;
			return arr;
		}

		if ("".equals(vip)) {
			arr[0] = 0x0B;
			return arr;
		}

		try {
			if (vip.indexOf(".") > 0) {
				String[] ipStrArray = vip.split("\\.");
				int len = ipStrArray.length;
				for (int i = len - 1; i >= 0; i--) {
					int tmp = Integer.parseUnsignedInt(ipStrArray[i], 10);
					arr[--index] = (byte) tmp;
				}
			} else {
				Conv.setLongNetOrder(arr, 0, Long.parseLong(vip));
			}
		} catch (Exception e) {
			logger.error("序列化VIP异常：" + ExceptionUtil.getStackStr(e));
			arr[0] = 0x0B; // 出现异常，VIP设置为空
		}

		return arr;
	}

	/**
	 * 状态序列化
	 * @param pos
	 * @return
	 */
	private long getPosState(Pos pos) {
		long state = 0L;
		Map<String, Object> extend = pos.getExtend();
		if (extend != null) {
			Object obj = extend.get("emergencyAlarm");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_EMERGENCYALARM);
			}

			obj = extend.get("overSpeed");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_OVERSPEED);
			}

			obj = extend.get("powerDown");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_POWERDOWN);
			}

			obj = extend.get("lowVoltage");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_LOWVOLTAGE);
			}

			obj = extend.get("GPSAntennaState");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_GPSANTENNASTATE);
			}

			obj = extend.get("bcarIsAlarm");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_BCARISALARM);
			}

			obj = extend.get("isIllegalRemove");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_ISILLEGALREMOVE);
			}

			obj = extend.get("shockAlarm");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_SHOCKALARM);
			}

			obj = extend.get("illegalMove");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_ILLEGALMOVE);
			}

			obj = extend.get("acarBts");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_ACARBTS);
			}

			obj = extend.get("bcarBts");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_BCARBTS);
			}

			obj = extend.get("pseudoLbs");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_PSEUDOLBS);
			}

			obj = extend.get("outAreaAlarm");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_OUTAREAALARM);
			}

			obj = extend.get("inAreaAlarm");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_INAREAALARM);
			}

			obj = extend.get("bcarState");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_BCARSTATE);
			}

			obj = extend.get("btIsOn"); // 0：开启 1：关闭
			int btIsOnFlag = -1;
			if (obj instanceof Integer) {
				btIsOnFlag = (Integer) obj;
			}

			// Bit15-16 01：开启 10：关闭 00：保留 11：保留
			if (btIsOnFlag == 0) {
				state |= (0x01L << STATE_BIT_BTISON_2);
			} else if (btIsOnFlag == 1) {
				state |= (0x01L << STATE_BIT_BTISON_1);
			}

			obj = extend.get("oilIsNormal");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_OILISNORMAL);
			}

			obj = extend.get("pseudoLbsState");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_PSEUDOLBSSTATE);
			}

			obj = extend.get("fatigueDrive");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_FATIGUEDRIVE);
			}

			obj = extend.get("isCharge");
			int isChargeFlag = -1; // 是否接电源充电(1：已接/0：未接)
			if (obj instanceof Integer) {
				isChargeFlag = (Integer) obj;
			}

			// 是否接电源充电 bit20-21 (01：已接/10：未接/00：保留/11：保留
			if (isChargeFlag == 0) {
				state |= (0x01L << STATE_BIT_ISCHARGE_1);
			} else if (isChargeFlag == 1) {
				state |= (0x01L << STATE_BIT_ISCHARGE_2);
			}

			obj = extend.get("isArm");
			int isArmFlag = -1; // 设防还是撤防 1：设防，0：撤防
			if (obj instanceof Integer) {
				isArmFlag = (Integer) obj;
			}

			// 是否设防 bit22-23 (01：设防 10：撤防 00：保留 11：保留)
			if (isArmFlag == 0) {
				state |= (0x01L << STATE_BIT_ISARM_1);
			} else if (isArmFlag == 1) {
				state |= (0x01L << STATE_BIT_ISARM_2);
			}

			obj = extend.get("isRealTime");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_ISREALTIME);
			}

			obj = extend.get("collideAlarm");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_COLLIDEALARM);
			}

			obj = extend.get("illegalStart");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_ILLEGALSTART);
			}

			// 电源状态 1：开启 0：关闭
			obj = extend.get("powerState");
			int powerStateFlag = -1;
			if (obj instanceof Integer) {
				powerStateFlag = (Integer) obj;
			}

			// Bit27-28 电源状态(01：开启 10：关闭 00：保留 11：保留)
			if (powerStateFlag == 0) {
				state |= (0x01L << STATE_BIT_POWERSTATE_1);
			} else if (powerStateFlag == 1) {
				state |= (0x01L << STATE_BIT_POWERSTATE_2);
			}

			// 刹车状态 1：未刹车 0：刹车
			obj = extend.get("brakeState");
			int brakeStateFlag = -1;
			if (obj instanceof Integer) {
				brakeStateFlag = (Integer) obj;
			}

			// Bit29-30 刹车状态(01：刹车 10：未刹车 00：保留 11：保留)
			if (brakeStateFlag == 0) {
				state |= (0x01L << STATE_BIT_BRAKESTATE_2);
			} else if (brakeStateFlag == 1) {
				state |= (0x01L << STATE_BIT_BRAKESTATE_1);
			}

			// 寻车状态 1：未寻车 0：寻车
			obj = extend.get("searchState");
			int searchStateFlag = -1;
			if (obj instanceof Integer) {
				searchStateFlag = (Integer) obj;
			}

			// Bit31-32 寻车状态(01：寻车 10：未寻车 00：保留 11：保留)
			if (searchStateFlag == 0) {
				state |= (0x01L << STATE_BIT_SEARCHSTATE_2);
			} else if (searchStateFlag == 1) {
				state |= (0x01L << STATE_BIT_SEARCHSTATE_1);
			}

			// 报警语音状态 1：开启 0：关闭
			obj = extend.get("alarmVoice");
			int alarmVoiceFlag = -1;
			if (obj instanceof Integer) {
				alarmVoiceFlag = (Integer) obj;
			}

			// Bit33-34 报警语音状态(01：开启 10：关闭 00：保留 11：保留)
			if (alarmVoiceFlag == 0) {
				state |= (0x01L << STATE_BIT_ALARMVOICE_1);
			} else if (alarmVoiceFlag == 1) {
				state |= (0x01L << STATE_BIT_ALARMVOICE_2);
			}

			// 软开关状态 1：软开关-关  0：软开关-开
			obj = extend.get("softSwitch");
			int softSwitchFlag = -1;
			if (obj instanceof Integer) {
				softSwitchFlag = (Integer) obj;
			}

			// Bit35-36：软开关状态(01：开启 10：关闭 00：保留 11：保留)
			if (softSwitchFlag == 0) {
				state |= (0x01L << STATE_BIT_SOFTSWITCH_2);
			} else if (softSwitchFlag == 1) {
				state |= (0x01L << STATE_BIT_SOFTSWITCH_1);
			}

			// 运单模式 1：运单模式-关  0：运单模式-开
			obj = extend.get("wayBillModel");
			int wayBillModelFlag = -1;
			if (obj instanceof Integer) {
				wayBillModelFlag = (Integer) obj;
			}

			// Bit37-38：运单模式(01：开启 10：关闭 00：保留 11：保留)
			if (wayBillModelFlag == 0) {
				state |= (0x01L << STATE_BIT_WAYBILLMODEL_2);
			} else if (wayBillModelFlag == 1) {
				state |= (0x01L << STATE_BIT_WAYBILLMODEL_1);
			}

			// bit39：GSP状态
			obj = extend.get("gpsState");
			if (obj instanceof Integer && (Integer) obj == 1) {
				state |= (0x01L << STATE_BIT_GPSSTATE);
			}
		}

		return state;
	}

	/**
	 * 状态反序列化
	 * @param state
	 * @return
	 */
	private Map<String, Object> getExtendFromPosState(long state) {
		Map<String, Object> extend = new HashMap<String, Object>();
		if ((state & (0x01L << STATE_BIT_INAREAALARM)) != 0) {
			extend.put("inAreaAlarm", 1);
		}

		if ((state & (0x01L << STATE_BIT_OUTAREAALARM)) != 0) {
			extend.put("outAreaAlarm", 1);
		}

		if ((state & (0x01L << STATE_BIT_PSEUDOLBS)) != 0) {
			extend.put("pseudoLbs", 1);
		}

		if ((state & (0x01L << STATE_BIT_BCARBTS)) != 0) {
			extend.put("bcarBts", 1);
		}

		if ((state & (0x01L << STATE_BIT_ACARBTS)) != 0) {
			extend.put("acarBts", 1);
		}

		if ((state & (0x01L << STATE_BIT_ILLEGALMOVE)) != 0) {
			extend.put("illegalMove", 1);
		}

		if ((state & (0x01L << STATE_BIT_SHOCKALARM)) != 0) {
			extend.put("shockAlarm", 1);
		}

		if ((state & (0x01L << STATE_BIT_ISILLEGALREMOVE)) != 0) {
			extend.put("isIllegalRemove", 1);
		}

		if ((state & (0x01L << STATE_BIT_BCARISALARM)) != 0) {
			extend.put("bcarIsAlarm", 1);
		}

		if ((state & (0x01L << STATE_BIT_GPSANTENNASTATE)) != 0) {
			extend.put("GPSAntennaState", 1);
		}

		if ((state & (0x01L << STATE_BIT_LOWVOLTAGE)) != 0) {
			extend.put("lowVoltage", 1);
		}

		if ((state & (0x01L << STATE_BIT_POWERDOWN)) != 0) {
			extend.put("powerDown", 1);
		}

		if ((state & (0x01L << STATE_BIT_OVERSPEED)) != 0) {
			extend.put("overSpeed", 1);
		}

		if ((state & (0x01L << STATE_BIT_EMERGENCYALARM)) != 0) {
			extend.put("emergencyAlarm", 1);
		}

		if ((state & (0x01L << STATE_BIT_BCARSTATE)) != 0) {
			extend.put("bcarState", 1);
		}

		int state_15 = -1;
		int state_16 = -1;
		if ((state & (0x01L << STATE_BIT_BTISON_1)) != 0) {
			state_15 = 1;
		} else {
			state_15 = 0;
		}

		if ((state & (0x01L << STATE_BIT_BTISON_2)) != 0) {
			state_16 = 1;
		} else {
			state_16 = 0;
		}

		// 0：开启 1：关闭
		// Bit15-16 01：开启 10：关闭 00：保留 11：保留
		if (state_15 == 0 && state_16 == 1) {
			extend.put("btIsOn", 0);
		} else if (state_15 == 1 && state_16 == 0) {
			extend.put("btIsOn", 1);
		}

		if ((state & (0x01L << STATE_BIT_OILISNORMAL)) != 0) {
			extend.put("oilIsNormal", 1);
		}

		if ((state & (0x01L << STATE_BIT_PSEUDOLBSSTATE)) != 0) {
			extend.put("pseudoLbsState", 1);
		}

		if ((state & (0x01L << STATE_BIT_FATIGUEDRIVE)) != 0) {
			extend.put("fatigueDrive", 1);
		}

		int state_20 = -1;
		int state_21 = -1;
		if ((state & (0x01L << STATE_BIT_ISCHARGE_1)) != 0) {
			state_20 = 1;
		} else {
			state_20 = 0;
		}

		if ((state & (0x01L << STATE_BIT_ISCHARGE_2)) != 0) {
			state_21 = 1;
		} else {
			state_21 = 0;
		}

		// 是否接电源充电(1：已接/0：未接)
		// 是否接电源充电 bit20-21 (01：已接/10：未接/00：保留/11：保留
		if (state_20 == 0 && state_21 == 1) {
			extend.put("isCharge", 1);
		} else if (state_20 == 1 && state_21 == 0) {
			extend.put("isCharge", 0);
		}

		int state_22 = -1;
		int state_23 = -1;
		if ((state & (0x01L << STATE_BIT_ISARM_1)) != 0) {
			state_22 = 1;
		} else {
			state_22 = 0;
		}

		if ((state & (0x01L << STATE_BIT_ISARM_2)) != 0) {
			state_23 = 1;
		} else {
			state_23 = 0;
		}

		// 设防还是撤防 1：设防，0：撤防
		// 是否设防 bit22-23 (01：设防 10：撤防 00：保留 11：保留)
		if (state_22 == 0 && state_23 == 1) {
			extend.put("isArm", 1);
		} else if (state_22 == 1 && state_23 == 0) {
			extend.put("isArm", 0);
		}

		if ((state & (0x01L << STATE_BIT_ISREALTIME)) != 0) {
			extend.put("isRealTime", 1);
		}

		if ((state & (0x01L << STATE_BIT_COLLIDEALARM)) != 0) {
			extend.put("collideAlarm", 1);
		}

		if ((state & (0x01L << STATE_BIT_ILLEGALSTART)) != 0) {
			extend.put("illegalStart", 1);
		}

		int state_27 = -1;
		int state_28 = -1;
		if ((state & (0x01L << STATE_BIT_POWERSTATE_1)) != 0) {
			state_27 = 1;
		} else {
			state_27 = 0;
		}

		if ((state & (0x01L << STATE_BIT_POWERSTATE_2)) != 0) {
			state_28 = 1;
		} else {
			state_28 = 0;
		}

		// 电源开启状态 1：开启 0：关闭
		// Bit27-28 电源状态(01：开启 10：关闭 00：保留 11：保留)
		if (state_27 == 0 && state_28 == 1) {
			extend.put("powerState", 1);
		} else if (state_27 == 1 && state_28 == 0) {
			extend.put("powerState", 0);
		}

		int state_29 = -1;
		int state_30 = -1;
		if ((state & (0x01L << STATE_BIT_BRAKESTATE_1)) != 0) {
			state_29 = 1;
		} else {
			state_29 = 0;
		}

		if ((state & (0x01L << STATE_BIT_BRAKESTATE_2)) != 0) {
			state_30 = 1;
		} else {
			state_30 = 0;
		}

		// 刹车状态 1：未刹车 0：刹车
		// Bit29-30 刹车状态(01：刹车 10：未刹车 00：保留 11：保留)
		if (state_29 == 0 && state_30 == 1) {
			extend.put("brakeState", 0);
		} else if (state_29 == 1 && state_30 == 0) {
			extend.put("brakeState", 1);
		}

		int state_31 = -1;
		int state_32 = -1;
		if ((state & (0x01L << STATE_BIT_SEARCHSTATE_1)) != 0) {
			state_31 = 1;
		} else {
			state_31 = 0;
		}

		if ((state & (0x01L << STATE_BIT_SEARCHSTATE_2)) != 0) {
			state_32 = 1;
		} else {
			state_32 = 0;
		}

		// 寻车状态 1：未寻车 0：寻车
		// Bit31-32 寻车状态(01：寻车 10：未寻车 00：保留 11：保留)
		if (state_31 == 0 && state_32 == 1) {
			extend.put("searchState", 0);
		} else if (state_31 == 1 && state_32 == 0) {
			extend.put("searchState", 1);
		}

		int state_33 = -1;
		int state_34 = -1;
		if ((state & (0x01L << STATE_BIT_ALARMVOICE_1)) != 0) {
			state_33 = 1;
		} else {
			state_33 = 0;
		}

		if ((state & (0x01L << STATE_BIT_ALARMVOICE_2)) != 0) {
			state_34 = 1;
		} else {
			state_34 = 0;
		}

		// 报警语音状态 1：开启 0：关闭
		// Bit33-34 报警语音状态(01：开启 10：关闭 00：保留 11：保留)
		if (state_33 == 0 && state_34 == 1) {
			extend.put("alarmVoice", 1);
		} else if (state_33 == 1 && state_34 == 0) {
			extend.put("alarmVoice", 0);
		}

		int state_35 = -1;
		int state_36 = -1;
		if ((state & (0x01L << STATE_BIT_SOFTSWITCH_1)) != 0) {
			state_35 = 1;
		} else {
			state_35 = 0;
		}

		if ((state & (0x01L << STATE_BIT_SOFTSWITCH_2)) != 0) {
			state_36 = 1;
		} else {
			state_36 = 0;
		}

		// 软开关状态 1：软开关-关  0：软开关-开
		// Bit35-36：软开关状态(01：开启 10：关闭 00：保留 11：保留)
		if (state_35 == 0 && state_36 == 1) {
			extend.put("softSwitch", 0);
		} else if (state_35 == 1 && state_36 == 0) {
			extend.put("softSwitch", 1);
		}

		int state_37 = -1;
		int state_38 = -1;
		if ((state & (0x01L << STATE_BIT_WAYBILLMODEL_1)) != 0) {
			state_37 = 1;
		} else {
			state_37 = 0;
		}

		if ((state & (0x01L << STATE_BIT_WAYBILLMODEL_2)) != 0) {
			state_38 = 1;
		} else {
			state_38 = 0;
		}

		// 运单模式 1：运单模式-关  0：运单模式-开
		// Bit37-38：运单模式(01：开启 10：关闭 00：保留 11：保留)
		if (state_37 == 0 && state_38 == 1) {
			extend.put("wayBillModel", 0);
		} else if (state_37 == 1 && state_38 == 0) {
			extend.put("wayBillModel", 1);
		}

		if ((state & (0x01L << STATE_BIT_GPSSTATE)) != 0) {
			extend.put("gpsState", 1);
		}

		return extend;
	}

}
