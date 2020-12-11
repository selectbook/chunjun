package com.iiot.protocol.fill;

import com.alibaba.fastjson.serializer.ByteArraySerializer;
import com.iiot.common.bytes.Conv;
import com.iiot.common.bytes.HexStr;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import java.util.List;


/**
 * 做包的封装、解析、校验
 * 
 * @author
 *
 */
public class FillPacket {

	private static Logger log = Logger.getLogger(FillPacket.class);
	private static final byte[] esc_bytes = { 0x7e, 0x7d };
	private static final byte ESCAPE = 0x7d;
	private static final byte ESC_XOR = 0x7C;

	private static int sequenceId = 0;

	/**
	 * 组包
	 * 
	 * @param head
	 *            消息头
	 * @param body
	 *            消息体
	 * @return
	 */
	public static byte[] getPacket(FillHead head, byte[] body) {

		// 组消息头
		byte[] headData = getHeadData(head);

		// 校验位
		byte[] headAndBody = ArrayUtils.addAll(headData, body);
		log.info(HexStr.toStr(headAndBody));
		int xor = FillPacketUtil.checkXorSecondVersion(headAndBody, 0, headAndBody.length);
		byte realXor = (byte)(xor & 0xff);
//		byte[] xorBytes = new byte[4];
//		Conv.setIntNetOrder(xorBytes, 0, xor);

		// 组包
		int offset = 0;

		int pack_len;
		// 计算包的长度
		if (body == null) {
			pack_len = 1 + headData.length;
		} else {
			pack_len = 1 + headData.length + body.length;
		}

		byte[] esc_data = new byte[pack_len];

		// 消息头
		for (byte headDate : headData) {
			esc_data[offset++] = headDate;
		}

		// 消息体
		if (body != null) {
			for (byte bodyData : body) {
				esc_data[offset++] = bodyData;
			}
		}

		// 校验位
//		for (byte xorData : xorBytes) {
//			esc_data[offset++] = xorData;
//		}
		esc_data[offset++] = realXor;
		return esc_data;
	}

	/**
	 * 解析包
	 * 
	 * @param esc_data
	 *            反转义好的数据
	 * @param data_offset
	 *            数据起始索引
	 * @param head
	 *            消息头信息
	 * @param info
	 *            分包项信息
	 */
	public static void fromPacket(byte[] esc_data, int data_offset, FillHead head, FillPacketInfo info) {
		int offset = data_offset;
		if (esc_data != null) {

			// 序列号
			byte serialNum = esc_data[offset++];

			head.setSerialNum(serialNum);
			// 加注设备ID
			byte[] devId = new byte[12];
			System.arraycopy(esc_data, offset, devId, 0, 12);
			head.setDevId(devId);
			offset += 12;
			// 命令类型
			byte cmdType = esc_data[offset++];
			head.setCmdType(cmdType);
			// 消息包封装项
			// 数据包个数
			int packNum = esc_data[offset++];
			info.setPacketTotal(packNum);
			head.setPackNum((byte)packNum);
			// 数据包ID
			byte packId = esc_data[offset++];
			info.setPacketNo(packId);

			// 消息体起始索引
			int startIndex = offset - 1;

			// 消息体
			int dataLen = esc_data.length - 19 - 1;
			byte[] body = FillPacketUtil.getBody(esc_data, startIndex, dataLen);
			offset += dataLen;
			head.setBody(body);

			// 校验码
			int xor = FillPacketUtil.checkXorSecondVersion(esc_data, 0, esc_data.length - 1);
			head.setXor(xor);
		}
	}

	/**
	 * 先校验,再解析
	 * 
	 * @param data
	 *            一包数据
	 * @param data_offset
	 *            起始所以
	 * @param data_len
	 *            数据长度
	 * @param head
	 *            消息头信息
	 * @param info
	 *            分包项信息
	 * @return
	 */
	public static boolean checkPacket(byte[] data, int data_offset, int data_len, FillHead head, FillPacketInfo info) {
		if (data == null) {
			// log.info("JTPacket checkPacket param null");
			return false;
		}

		// 校验包头、包尾
		if (data[data_offset++] != 0x55 || data[data_offset++] != 0x3a) {
			// log.info("JTPacket checkPacket parket error");
			return false;
		}

		// 反转义数据，返回一个byte数组，不包含包头、包尾
//		byte[] esc_pack = FillPacketUtil.escapeReverseData(data, data_offset, data_len);
//		if (esc_pack == null) {
//			// log.info("JTPacket checkPacket parket escape reverse error");
//			return false;
//		}

		// 至少13个字节 消息头(19)+校验位(1)
		int len = data.length;
		if (len < 20) {
			// log.info("JTPacket checkPacket parket len too short");
			return false;
		}

		// 消息头长度，固定为19个字节
		int headLen = 19;
		// 消息长度
		int dataLen = Conv.getShortNetOrder(data, data_offset);
		data_offset += 2;
		head.setLen(dataLen);

		// 校验
//		int xor = esc_pack[len - 1] & 0xFF;
//		int xorC = FillPacketUtil.CheckXor(data, len - 4, 4);
//		if (xor != xorC) {
//			// log.info("JTPacket checkPacket parket xor error");
//			return false;
//		}

		// 数据长度 消息长度 + 校验
		int pack_len = dataLen + 1;

		// 长度校验
		if (len != pack_len) {
			return false;
		}

		// 解析包
		fromPacket(data, data_offset, head, info);
		return true;
	}

	/**
	 * 组消息头数据
	 * 
	 * @param head
	 * @return
	 */
	public static byte[] getHeadData(FillHead head) {
		// 消息头数据，不考虑数据加密和分包
		byte[] headData = new byte[19];
		int offset = 0;

		headData[offset++] = 0x55;
		headData[offset++] =0x3a;

		// 长度
		int len = head.getLen();
		Conv.setShortNetOrder(headData, offset, len);
		offset += 2;

		// 序列号
		byte serialNum = head.getSerialNum();
		headData[offset++] = serialNum;

		// 加注设备ID
		byte[] devId = head.getDevId();
		System.arraycopy(devId, 0, headData, offset, 12);
		offset += 12;

		// 命令类型
		byte cmdType = head.getCmdType();
		headData[offset++] = cmdType;

		// 数据包个数
		byte packNum = head.getPackNum();
		headData[offset++] = packNum;

		return headData;
	}

	/**
	 * 校验
	 *
	 * @param data
	 * @param data_offset
	 * @return
	 */
	public static boolean getXorResult(byte[] data, int data_offset) {

		if (data == null) {
			log.error("Param is null");
			return false;
		}

		int offset = data_offset;

		// 包头
		offset++; // 0
		offset++; // 1

		// 包长
		int len = Conv.getShortNetOrder(data, offset);
		offset += 2; // 4

		// 序列号
		offset++; // 5

		// 加注设备ID
		offset += 12;

		// 命令类型
		offset++;

		// 数据包个数
		offset++;

		// 数据包
		int bodyLen = len - 1 - 1 - 2 - 1 - 12 - 1 - 1;
		offset += bodyLen;

		// 做校验,从包头开始到校验前每一个字节的异或
		int rightXor = FillPacketUtil.checkXorSecondVersion(data, data_offset, len);


		// 校验码
		byte xor = data[offset++];
		int xorInt = Byte.toUnsignedInt(xor);

		if (xorInt != rightXor) {
			log.error("A6Packet xor err");
			return false;
		}
		return true;
	}

	/**
	 * 从前向后找包头
	 *
	 * @param array
	 * @param offset
	 * @return
	 */
	public static int getPosHead(byte[] array, int offset) {
		int posHead = -1;
		if (array == null) {
			return posHead;
		}
		for (int i = offset; i < array.length - 1; i++) {
			if (array[i] == 0x55 && array[i + 1] == 0x3a) {
				posHead = i;
				break;
			}
		}
		return posHead;
	}

	/**
	 * 从后向前找包尾
	 *
	 * @param array
	 * @param len
	 * @return
	 */
	public static int getPosTail(byte[] array, int len) {
		int posTail = -1;
		if (array == null) {
			return posTail;
		}
		for (int i = len - 1; i >= 0; i--) {
			if (array[i] == 0x0D) {
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
	 * 判断包尾是否正确
	 *
	 * @param array
	 * @param posTail
	 * @return
	 */
	private static boolean tailIsCorrect(byte[] array, int posTail) {
		// 包尾不正确
		if (array == null) {
			return false;
		}

		int len = array.length;
		if (posTail <= len - 1) {
			if (array[posTail] != 0x0D) {
				// log.error("A6Packet tail err：" + HexStr.toStr(array));
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}

	}

	/**
	 * 从数据的开始的位置找到所有正确的完整包、错误包
	 *
	 * @param OKList 用于存放正确包
	 * @param NGList 用于存放错误包
	 * @param data   终端上发数据
	 * @return
	 */
	public static int analysisPacket(List<Object> OKList, List<Object> NGList, byte[] data) {

		// 一次可以处理一个包，也可以一次处理多个包（给Out多次add）
		// int readableBytes()方法返回ByteBuf有多少字节可读
		int len = data.length;

		// 对长度有最小要求
		// 至少11字节：头（2）+ 包长（2）+ 序列号 （1）+ 加注设备ID（12）+ 命令类型（1）+ 数据包个数（1）+ 数据包（0）+ 校验码（1）
		final int PACK_MIN_LEN = 20;

		if (len < PACK_MIN_LEN) {
			return 0;
		}

		// 最后一个成功组包的位置
		int lastOkIndex = 0;

		int offset = 0;
		while (offset < len) {
			// 找包头
			int oldOffset = offset;
			int posHead = getPosHead(data, offset);
			if (posHead < 0) {
				// 没有包头，速度太慢，不要记录
				// log.info("posHead < 0 " + HexStr.toStr(data));
				return lastOkIndex;
			}

			if (len - posHead < 20) {
				return lastOkIndex;
			}

			// 将包头前面的数据放入NGList
			if (posHead >= oldOffset && posHead != lastOkIndex) {
				byte[] arr = new byte[posHead - lastOkIndex];
				System.arraycopy(data, oldOffset, arr, 0, posHead - lastOkIndex);
				NGList.add(arr);
				// log.info("包头前面的数据：" + HexStr.toStr(arr) + "<==>" + "整包数据：" + HexStr.toStr(data));
				lastOkIndex = posHead;
			}

			offset = posHead + 2;

			// 包长
			int packet_len = getDataLen(data, posHead);

			// 数据长度 = 包长+检验码（1位）
			int data_len = packet_len + 1;

			// 找包尾
			int posTail = data_len + posHead - 1;

			// 计算的数据长度是否大于余下的数据长度(是否有包尾)
			if (data_len > len - posHead) {
				// log.info("没有包尾: " + "数据长度=" + data_len + " > " + "剩余数据长度=" + (len - posHead));
				continue;
			}

			if (posTail >= len) {
				continue;
			}

			// 判断包尾是否正确
//            boolean flag = tailIsCorrect(data, posTail);

			// 包尾不正确,从当前包头后一个字节开始找包头
//            if (!flag) {
//                continue;
//            }

			// 组包
			int pac_len = posTail - posHead + 1;

//            int pac_len = data_len;
			// 校验
			if (!getXorResult(data, posHead)) {
				continue;
			}

			// 成功，把数据放进OKList
			byte[] array = new byte[pac_len];
			System.arraycopy(data, posHead, array, 0, pac_len);
			OKList.add(array);

			// 对于多个包来说，找完一个包还要接着找下一个包。
			// 重新开始找包头
			offset = posTail + 1;
			lastOkIndex = offset;
		}
		return lastOkIndex;
	}



}
