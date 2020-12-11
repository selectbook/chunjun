package com.iiot.protocol.fill;

import com.iiot.common.bytes.Conv;
import com.iiot.common.bytes.HexStr;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class FillHead {

	private static Logger log = Logger.getLogger(FillHead.class);

	static public final byte HEAD_FLAG = 0x55;
	static public final byte TAIL_FLAG = 0x3a;


	// 数据长度 除检验码的长度
	private int len;

	// 序列号
	private byte serialNum;

	// 加注设备ID
	private byte[] devId;

	// 命令类型
	private  byte cmdType;

	// 数据包个数
	private byte packNum;

	// 包体（消息头后一字节到校验码前一字节）
	private byte[] body;

	// 校验码
	private int xor;


	// 从一个byte转为head
	public FillHead(byte[] array) throws Exception {
		super();
		if (array[0] != FillHead.HEAD_FLAG || array[array.length - 1] != FillHead.TAIL_FLAG) {
			throw new Exception("头不对");
		}
		int offset = 0;
		// 数据长度(头标识、数据头、数据体)
		len = Conv.getShortNetOrder(array, offset);
		offset += 2;
		if (len - 1 != array.length) {
			throw new Exception("长度不对");
		}
		// serialNum
		serialNum = array[offset++];
		// devId
		devId = new byte[12];
		System.arraycopy(array, offset, devId, 0, 12);
		offset += 12;
		// cmdType
		cmdType = array[offset];
		// packNum
		packNum = array[offset];

		// 其它的是body，除去Xor，尾
		int bodyLen = len - offset - 4;
		if (bodyLen > 0) {
			body = new byte[bodyLen];
			System.arraycopy(array, offset, body, 0, bodyLen);
			offset += bodyLen;
		}
		// xor
		xor = array[offset];
		offset += 1;

		if (offset != array.length) {
			// 未解析完全
			log.warn("解包数据未用完:" + HexStr.toStr(array));
		}

	}

	public FillHead() {
		super();
	}


	public FillHead(byte cmdType, byte[] devId) {
		super();
		this.cmdType = cmdType;
		this.devId = devId;
	}

	
	public FillHead(byte cmdType, byte[] devId, byte serialNum) {
		super();
		this.cmdType = cmdType;
		this.devId = devId;
		this.serialNum = serialNum;
	}


	public FillHead(byte cmdType, byte[] devId, byte serialNum, byte xor) {
		super();
		this.cmdType = cmdType;
		this.devId = devId;
		this.serialNum = serialNum;
		this.xor = xor;
	}

	public FillHead(short len, byte serialNum, byte[] devId, byte cmdType, byte packNum) {
		this.len = len;
		this.serialNum = serialNum;
		this.devId = devId;
		this.cmdType = cmdType;
		this.packNum = packNum;
	}

	public FillHead(short len, byte serialNum, byte[] devId, byte cmdType, byte packNum, byte[] body, byte xor) {
		this.len = len;
		this.serialNum = serialNum;
		this.devId = devId;
		this.cmdType = cmdType;
		this.packNum = packNum;
		this.body = body;
		this.xor = xor;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public int getXor() {
		return xor;
	}

	public void setXor(int xor) {
		this.xor = xor;
	}

	public static byte getHeadFlag() {
		return HEAD_FLAG;
	}

	public static byte getTailFlag() {
		return TAIL_FLAG;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public byte getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(byte serialNum) {
		this.serialNum = serialNum;
	}

	public byte[] getDevId() {
		return devId;
	}

	public void setDevId(byte[] devId) {
		this.devId = devId;
	}

	public byte getCmdType() {
		return cmdType;
	}

	public void setCmdType(byte cmdType) {
		this.cmdType = cmdType;
	}

	public byte getPackNum() {
		return packNum;
	}

	public void setPackNum(byte packNum) {
		this.packNum = packNum;
	}

	/**
	 * 十六进制字符串转字节数组
	 * 
	 * @param src
	 * @return
	 */
	private static byte[] hexStringToByte(String src) {
		if (src == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(src);
		if (sb.length() > 12) {
			log.info("src.length() > 12: " + sb.toString());
			return null;
		}

		byte[] array = new byte[6];

		// 不是双数,前面加个0
		if (sb.length() % 2 != 0) {
			sb.insert(0, "0");
		}

		int len = sb.length();
		for (int i = 0; i < len; i += 2) {
			byte b = 0;
			char char1 = sb.charAt(len - 2 - i);
			char char2 = sb.charAt(len - 1 - i);

			// 从后面开始计算
			b = (byte) ((byte) ((char1 - '0') << 4) | (char2 - '0'));
			array[6 - 1 - i / 2] = b;
		}
		return array;
	}

	@Override
	public String toString() {
		return "FillHead{" +
				"len=" + len +
				", serialNum=" + serialNum +
				", devId=" + Arrays.toString(devId) +
				", cmdType=" + cmdType +
				", packNum=" + packNum +
				", body=" + Arrays.toString(body) +
				", xor=" + xor +
				'}';
	}
}
