package com.iiot.protocol.fill;

import com.iiot.common.bytes.Conv;
import com.iiot.common.bytes.HexStr;
import org.apache.log4j.Logger;

import java.util.zip.CRC32;

/**
 * JT工具方法
 * 
 * @author
 *
 */
public class FillPacketUtil {

	private static Logger log = Logger.getLogger(FillPacketUtil.class);

	private static final byte ESCAPE = 0x7d;

	/**
	 * 校验结果
	 * @param data 未反转义数据，完整包
	 * @param offset 消息ID所对应的索引
	 * @return
	 */
	public static boolean getCheckResult(byte[] data, int offset, int pac_len) {
		if (data == null) {
			return false;
		}

		// 反转义数据
		byte[] esc = escapeReverseData(data, offset, pac_len);

		int len = esc.length;
		int xorC = CheckXor(esc, 0, len - 1);
		int xor = Byte.toUnsignedInt(esc[len - 1]);
		if (xorC != xor) {
			log.info("JTPacket xor error");
			return false;
		}

		return true;
	}

	/**
	 * 校验码：从消息头开始到校验前一字节的所有字节异或
	 * @param data 反转义的数据，去掉包头、包尾
	 * @param offset 消息ID所对应的索引
	 * @param data_len 校验长度
	 * @return
	 */
	public static int CheckXor(byte[] data, int offset, int data_len) {
		int n = 0;
		if (data == null) {
			return n;
		}
		for (int i = offset; i < offset + data_len; i++) {
			n ^= data[i];
		}
		return n & 0xFF;
	}

	/**
	 * CRC32校验 从消息头开始到校验前一个字节的所有字节
	 *
	 * @param data
	 * @param offset
	 * @param length
	 * @return
	 */
	public static long crc32(byte[] data, int offset, int length) {
		byte[] realArray = new byte[length];
		System.arraycopy(data, offset, realArray, 0, length);
		System.out.println(HexStr.toStr(realArray));
		CRC32 crc32 = new CRC32();
		crc32.reset();//Resets CRC-32 to initial value.
		crc32.update(realArray, offset, length);//将数据丢入CRC32解码器
		return crc32.getValue();//获取CRC32 的值  默认返回值类型为long 用于保证返回值是一个正数
	}

	/**
	 * 一个字节 异或
	 * @param data
	 * @param offset
	 * @param dataLen
	 * @return
	 */
	public static int checkXorSecondVersion (byte[] data, int offset, int dataLen) {
		byte n = 0;
		if (data == null) {
			return n;
		}
		for (int i = offset; i < offset + dataLen; i++) {
			n ^= data[i];
		}
		return Byte.toUnsignedInt(n);
	}



	/**
	 * 判断数据是否有消息包封装项
	 * @param prop 消息体属性
	 * @return
	 */
	public static boolean isPkt(int prop) {

		if ((prop & 0x2000) != 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 找包头
	 * @param data 要分包的数据
	 * @param offset 给定的起始索引
	 * @return
	 */
	public static int getPosHead(byte[] data, int offset) {
		int posHead = -1;
		if (data == null) {
			return posHead;
		}
		for (int i = offset; i < data.length; i++) {
			byte tmp = data[i];
			if (tmp == (byte) 0x7e) {
				posHead = i;
				break;
			}
		}
		return posHead;
	}

	/**
	 * 找包尾
	 * @param data 要分包的数据
	 * @param offset 通过消息头计算所得到的包尾索引
	 * @return
	 */
	public static int getPosTail(byte[] data, int offset) {
		int posTail = -1;
		if (data == null) {
			return posTail;
		}
		for (int i = offset; i < data.length; i++) {
			byte tmp = data[i];
			if (tmp == (byte) 0x7e) {
				posTail = i;
				break;
			}
		}
		return posTail;
	}

	/**
	 * 计算包长
	 *
	 * @param array
	 * @param offset
	 * @return
	 */
	private static int getDataLen(byte[] array, int offset) {
		if (array == null) {
			return 0;
		}
		// 包头
		offset += 2;

		// 包长
		int packet_len = Conv.getShortNetOrder(array, offset);
		return packet_len;
	}

	/**
	 * 计算消息头长度
	 * @param prop 消息体属性
	 * @return
	 */
	public static int getHeadLen(int prop) {

		// 消息包封装项
		int pack_item = (prop & 0x2000) != 0 ? 4 : 0;

		// 消息头长度
		int headLen = 12 + pack_item;
		return headLen;

	}

	/**
	 * 数据反转义
	 * @param data 整包数据
	 * @param offset 包头位置
	 * @param pac_len 数据长度
	 * @return
	 */
	public static byte[] escapeReverseData(byte[] data, int offset, int pac_len) {
		int index = offset + 1; // 去掉包头
		int len = offset + pac_len - 1; // 去掉包尾
		int data_offset = 0;
		byte[] array = new byte[len]; // 存储反转义好的数据
		int count = 0; // 计数器		
		for (int i = index; i < len; i++) { // 反转义
			if (data[i] == ESCAPE) {
				if (i != len - 1) { // 防越界
					if (data[i + 1] == 0x02) { // 0x7d后面接0x02
						array[data_offset++] = 0x7e;
						i += 1;
					} else if (data[i + 1] == 0x01) { // 0x7d后面接0x01
						array[data_offset++] = 0x7d;
						i += 1;
					} else { // 0x7d后面即不是0x01，也不是0x02。不进行反转义
						array[data_offset++] = data[i];
					}
				} else {
					array[data_offset++] = data[i];
				}
			} else {
				array[data_offset++] = data[i];
			}

			count += 1;
		}

		byte[] esc_pack = new byte[count];
		System.arraycopy(array, 0, esc_pack, 0, count);
		return esc_pack;
	}

	/**
	 * 统计一包数据转义多少次
	 * @param data 整包数据
	 * @param offset 起始索引
	 * @param pac_len 整包数据长度
	 * @return
	 */
	public static int escapeCount(byte[] data, int offset, int pac_len) {
		if (data == null) {
			return 0;
		}
		int index = offset + 1; // 去掉包头
		int len = offset + pac_len - 1; // 去掉包尾
		int count = 0; // 计数器
		for (int i = index; i < len; i++) { // 反转义
			if (data[i] == ESCAPE) {
				if (i != len - 1) { // 防越界
					if (data[i + 1] == 0x01 || data[i + 1] == 0x02) { // 0x7d后面接0x01或者0x02
						count += 1;
					}
				}
			}
		}

		return count;
	}

	/**
	 * 获取数据中的一部分
	 * @param data 源数据
	 * @param offset 起始索引
	 * @param len 获取元素个数
	 * @return
	 */
	public static byte[] getBody(byte[] data, int offset, int len) {
		if (data == null) {
			return null;
		}
		byte[] body = new byte[len];
		int dataLen = data.length;
		for (int i = 0; i < len; i++) {
			if (offset + i < dataLen) {
				body[i] = data[offset + i];
			}
		}
		return body;
	}

	/**
	 * 获取报警标志
	 * @param data 消息体数据
	 * @param offset 起始索引
	 * @return
	 */
	public static long getAlarmFlag(byte[] data, int offset) {
		long high_24 = (Byte.toUnsignedLong(data[offset])) << 24;
		long high_16 = (Byte.toUnsignedLong(data[offset + 1])) << 16;
		long high_8 = (Byte.toUnsignedLong(data[offset + 2])) << 8;
		long low_8 = (Byte.toUnsignedLong(data[offset + 3]));
		long alarmFlag = high_24 + high_16 + high_8 + low_8;
		return alarmFlag;
	}

	/**
	 * 获取车辆状态
	 * @param data 消息体数据
	 * @param offset 起始索引
	 * @return
	 */
	public static long getVehicleStatus(byte[] data, int offset) {
		long high_24 = (Byte.toUnsignedLong(data[offset])) << 24;
		long high_16 = (Byte.toUnsignedLong(data[offset + 1])) << 16;
		long high_8 = (Byte.toUnsignedLong(data[offset + 2])) << 8;
		long low_8 = (Byte.toUnsignedLong(data[offset + 3]));
		long status = high_24 + high_16 + high_8 + low_8;
		return status;
	}

	/**
	 * 计算纬度，纬度占4字节
	 * @param data 消息体数据
	 * @param offset 纬度所对应的起始索引
	 * @return
	 */
	public static double getLat(byte[] data, int offset) {
		long high_24 = (Byte.toUnsignedLong(data[offset])) << 24;
		long high_16 = (Byte.toUnsignedLong(data[offset + 1])) << 16;
		long high_8 = (Byte.toUnsignedLong(data[offset + 2])) << 8;
		long low_8 = (Byte.toUnsignedLong(data[offset + 3]));
		long lat = high_24 + high_16 + high_8 + low_8;
		return lat / 1000000.0;
	}

	/**
	 * 计算经度，经度占4字节
	 * @param data 消息体数据
	 * @param offset 经度所对应的起始索引
	 * @return
	 */
	public static double getLon(byte[] data, int offset) {
		long high_24 = (Byte.toUnsignedLong(data[offset])) << 24;
		long high_16 = (Byte.toUnsignedLong(data[offset + 1])) << 16;
		long high_8 = (Byte.toUnsignedLong(data[offset + 2])) << 8;
		long low_8 = (Byte.toUnsignedLong(data[offset + 3]));
		long lon = high_24 + high_16 + high_8 + low_8;
		return lon / 1000000.0;
	}

	/**
	 * 计算海拔高度，占2字节
	 * @param data 消息体数据
	 * @param offset 高程(海拔)所对应的起始索引
	 * @return
	 */
	public static double getAltitude(byte[] data, int offset) {
		int high_8 = (Byte.toUnsignedInt(data[offset])) << 8;
		int low_8 = Byte.toUnsignedInt(data[offset + 1]);
		int altitude = high_8 + low_8;
		return altitude / 1.0;
	}

	/**
	 * 计算速度，占2字节
	 * @param data 消息体数据
	 * @param offset 速度所对应的起始索引
	 * @return
	 */
	public static double getSpeed(byte[] data, int offset) {
		int high_8 = (Byte.toUnsignedInt(data[offset])) << 8;
		int low_8 = Byte.toUnsignedInt(data[offset + 1]);
		int speed = high_8 + low_8;
		return speed / 10.0;
	}

	/**
	 * 计算方向，占2字节
	 * @param data 消息体数据
	 * @param offset 方向所对应的起始索引
	 * @return
	 */
	public static double getDirect(byte[] data, int offset) {
		int high_8 = (Byte.toUnsignedInt(data[offset])) << 8;
		int low_8 = Byte.toUnsignedInt(data[offset + 1]);
		int direct = high_8 + low_8;
		return direct / 1.0;
	}

	/**
	 * 计算里程，占4字节
	 * @param data  消息体数据
	 * @param offset 里程所对应的起始索引
	 * @return
	 */
	public static double getMileage(byte[] data, int offset) {
		long high_24 = (Byte.toUnsignedLong(data[offset])) << 24;
		long high_16 = (Byte.toUnsignedLong(data[offset + 1])) << 16;
		long high_8 = (Byte.toUnsignedLong(data[offset + 2])) << 8;
		long low_8 = (Byte.toUnsignedLong(data[offset + 3]));
		long mileage = high_24 + high_16 + high_8 + low_8;
		return mileage / 10.0;
	}

	/**
	 * 计算油量，占2字节
	 * @param data 消息体数据
	 * @param offset 油量所对应的起始索引
	 * @return
	 */
	public static double getOil(byte[] data, int offset) {
		int high_8 = (Byte.toUnsignedInt(data[offset])) << 8;
		int low_8 = Byte.toUnsignedInt(data[offset + 1]);
		int oil = high_8 + low_8;
		return oil / 10.0;
	}

	/**
	 * 计算行驶记录功能获取的速度，占2字节
	 * @param data 消息体数据
	 * @param offset 行驶记录速度所对应的起始索引
	 * @return
	 */
	public static double getRecordSpeed(byte[] data, int offset) {
		int high_8 = (Byte.toUnsignedInt(data[offset])) << 8;
		int low_8 = Byte.toUnsignedInt(data[offset + 1]);
		int recordSpeed = high_8 + low_8;
		return recordSpeed / 10.0;
	}

	/**
	 * 转义数据
	 * @param data 要转义的数据
	 * @param offset 起始所以
	 * @param data_len 数据长度
	 * @param esc_bytes { 0x7e, 0x7d }
	 * @param escape 0x7d
	 * @param esc_xor 0x7C
	 * @return
	 */
	public static byte[] escapeData(byte[] data, int offset, int data_len, byte[] esc_bytes, byte escape,
			byte esc_xor) {
		
		if (data == null) {
			return null;
		}

		// 此数组用于存放转义的数据
		byte[] array = new byte[data_len * 2];

		// 索引
		int index = 0;

		// 计数器
		int count = 0;
		for (int i = offset; i < data_len; i++) {
			boolean isFound = false;
			for (byte b : esc_bytes) {
				if (b == data[i]) {
					array[index] = escape;

					// 要么为0x01，要么为0x02
					array[index + 1] = (byte) (b ^ esc_xor);
					index += 2;
					count += 1;
					isFound = true;
					break;
				}
			}
			if (!isFound) {
				array[index] = data[i];
				index += 1;
			}
		}
		byte[] esc_data = new byte[data_len + count];
		System.arraycopy(array, 0, esc_data, 0, esc_data.length);
		return esc_data;
	}
	
	
	/**
	 * 开锁次数，占4字节
	 * @param data  消息体数据
	 * @param offset 开锁次数所对应的起始索引
	 * @return
	 */
	public static long getNumberOfUnlocks(byte[] data, int offset) {
		long high_24 = (Byte.toUnsignedLong(data[offset])) << 24;
		long high_16 = (Byte.toUnsignedLong(data[offset + 1])) << 16;
		long high_8 = (Byte.toUnsignedLong(data[offset + 2])) << 8;
		long low_8 = (Byte.toUnsignedLong(data[offset + 3]));
		long count = high_24 + high_16 + high_8 + low_8;
		return count;
	}
	
	
	/**
	 * 锁状态，占2字节
	 * @param data  消息体数据
	 * @param offset 锁状态所对应的起始索引
	 * @return
	 */
	public static int getLockState(byte[] data, int offset) {
		int high_8 = (Byte.toUnsignedInt(data[offset])) << 8;
		int low_8 = (Byte.toUnsignedInt(data[offset + 1]));
		int state = high_8 + low_8;
		return state;
	}
	
	/**
	 * GPRS版本号，占4字节
	 * @param data  消息体数据
	 * @param offset GPRS版本号所对应的起始索引
	 * @return
	 */
	public static long getGPRSVersionNumber(byte[] data, int offset) {
		long high_24 = (Byte.toUnsignedLong(data[offset])) << 24;
		long high_16 = (Byte.toUnsignedLong(data[offset + 1])) << 16;
		long high_8 = (Byte.toUnsignedLong(data[offset + 2])) << 8;
		long low_8 = (Byte.toUnsignedLong(data[offset + 3]));
		long version = high_24 + high_16 + high_8 + low_8;
		return version;
	}
	
	/**
	 * BLE版本号，占2字节
	 * @param data  消息体数据
	 * @param offset BLE版本号所对应的起始索引
	 * @return
	 */
	public static int getBLEVersionNumber(byte[] data, int offset) {
		int high_8 = (Byte.toUnsignedInt(data[offset])) << 8;
		int low_8 = (Byte.toUnsignedInt(data[offset + 1]));
		int version = high_8 + low_8;
		return version;
	}
	
	/**
	 * 最后一次充电结束与现在间隔时间，占2字节
	 * @param data  消息体数据
	 * @param offset 对应的起始索引
	 * @return
	 */
	public static int getChargeInterval(byte[] data, int offset) {
		int high_8 = (Byte.toUnsignedInt(data[offset])) << 8;
		int low_8 = (Byte.toUnsignedInt(data[offset + 1]));
		int interval = high_8 + low_8;
		return interval;
	}
	
	/**
	 * 计算MCC、MNC、LAC，占2字节
	 * @param data  消息体数据
	 * @param offset MCC、MNC、LAC所对应的起始索引
	 * @return
	 */
	public static int getMccMncLac(byte[] data, int offset) {
		int high_8 = (Byte.toUnsignedInt(data[offset])) << 8;
		int low_8 = (Byte.toUnsignedInt(data[offset + 1]));
		int obj = high_8 + low_8;
		return obj;
	}
	
	/**
	 * cell蜂窝ID，占4字节
	 * @param data  消息体数据
	 * @param offset 蜂窝ID所对应的起始索引
	 * @return
	 */
	public static long getCellId(byte[] data, int offset) {
		long high_24 = (Byte.toUnsignedLong(data[offset])) << 24;
		long high_16 = (Byte.toUnsignedLong(data[offset + 1])) << 16;
		long high_8 = (Byte.toUnsignedLong(data[offset + 2])) << 8;
		long low_8 = (Byte.toUnsignedLong(data[offset + 3]));
		long id = high_24 + high_16 + high_8 + low_8;
		return id;
	}

	public static boolean isNeedEscape(byte[] jtData) {
		// TODO Auto-generated method stub
		return false;
	}

	public static int crc32Self(byte[] data, int offset,int length){
		byte i;
		int crc = 0xffffffff;        // Initial value
		length += offset;
		for(int j=offset;j<length;j++) {
			crc ^= data[j];
			for (i = 0; i < 8; ++i)
			{
				if ((crc & 1) != 0)
					crc = (crc >> 1) ^ 0xEDB88320;// 0xEDB88320= reverse 0x04C11DB7
				else
					crc = (crc >> 1);
			}
		}
		return ~crc;
	}



	public static void main(String[] args) {
		byte[] array = HexStr.toArray("553A001C0058543336352D3030303030300301060707e4050a000000");
//		long xor = crc32(array, 0, 28);
//		System.out.print(xor);

		System.out.println(array.length);
//		int xor = CheckXor(array, 0, array.length);
//		System.out.println(xor);
		int xor = checkXorSecondVersion(array, 0, array.length);
//		System.out.println();
//		System.out.println((byte)xor);

	}
}