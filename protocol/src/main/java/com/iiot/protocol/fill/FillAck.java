package com.iiot.protocol.fill;

import com.iiot.common.bytes.Conv;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * 平台应答
 * 
 * @author
 *
 */
public class FillAck {
	private static Logger log = Logger.getLogger(FillAck.class);

	/**
	 * 平台通用应答下发包
	 * 第一版：默认应答为成功，命令类型为0x04，数据包为0
	 * @param devId 设备号
	 * @param serialNum 序列号
	 * @param cmdType 消息类型
	 * @return
	 */
	public static byte[] packetDownCommon(byte[] devId, int serialNum, int cmdType) {
		// 组应答消息体
//		byte[] body = new byte[19];
//		int offset = 0;
//
//		// 应答结果
//		body[offset] = 0x00; // 4
		byte[] body = null;
		return FillPacket.getPacket(new FillHead((short)19, (byte)serialNum, devId, (byte)0x04, (byte)0), body);
	}

	/**
	 * 开始加注回复
	 * @param devId
	 * @param serialNum
	 * @param cmdType
	 * @return
	 */
	public static byte[] packetDownStartFill(byte[] devId, int serialNum, int cmdType) {
		// 组应答消息体
//		byte[] body = new byte[19];
//		int offset = 0;
//
//		// 应答结果
//		body[offset] = 0x00; // 4
		byte[] body = null;
		return FillPacket.getPacket(new FillHead((short)19, (byte)serialNum, devId, (byte)0x0c, (byte)0), body);
	}

	/**
	 * 加注完成成功应答
	 * @param devId
	 * @param serialNum
	 * @param cmdType
	 * @return
	 */
	public static byte[] packetDownFillSuccess(byte[] devId, int serialNum, int cmdType) {
		// 组应答消息体
//		byte[] body = new byte[19];
//		int offset = 0;
//
//		// 应答结果
//		body[offset] = 0x00; // 4
		byte[] body = null;
		return FillPacket.getPacket(new FillHead((short)19, (byte)serialNum, devId, (byte)0x04, (byte)0), body);
	}

	/**
	 * 加注完成失败应答
	 * @param devId
	 * @param serialNum
	 * @param cmdType
	 * @return
	 */
	public static byte[] packetDownFillFail(byte[] devId, int serialNum, int cmdType) {
		// 组应答消息体
//		byte[] body = new byte[19];
//		int offset = 0;
//
//		// 应答结果
//		body[offset] = 0x00; // 4
		byte[] body = null;
		return FillPacket.getPacket(new FillHead((short)19, (byte)serialNum, devId, (byte)0x05, (byte)0), body);
	}



	/**
	 * 系统回复接收数据错误应答（除设备加注完成应答错误）
	 * @param devId
	 * @param serialNum
	 * @param cmdType
	 * @return
	 */
	public static byte[] packetDownError(byte[] devId, int serialNum, int cmdType) {
		// 组应答消息体
//		byte[] body = new byte[19];
//		int offset = 0;
//
//		// 应答结果
//		body[offset] = 0x00; // 4

		byte[] body = null;
		return FillPacket.getPacket(new FillHead((short)19, (byte)serialNum, devId, (byte)0x05, (byte)0), body);
	}

	/**
	 * 终端心跳应答包，命令类型也为0x0b,加上时间戳
	 * @param devId
	 * @param serialNum
	 * @param cmdType
	 * @return
	 */
	public static byte[] packetDownHeartBeat(byte[] devId, int serialNum, int cmdType) {
		// 组应答消息体
		byte[] body = new byte[9];
		int offset = 0;
		// 包ID
		body[offset++] = 0x06;
		// 包长度
		body[offset++] = 0x07;
		// 时间戳
		Date date = new Date();
		int year = date.getYear() + 1900;
		int month = date.getMonth() + 1;
		int day = date.getDate();
		int hour = date.getHours();
		int minutes = date.getMinutes();
		int seconds = date.getMinutes();
		// 日月年
		Conv.setShortNetOrder(body, offset, year);
		offset += 2;
		body[offset++] = (byte) month;
		body[offset++] = (byte) day;
		// 时分秒
		body[offset++] = (byte) hour;
		body[offset++] = (byte) minutes;
		body[offset++] = (byte) seconds;

		// 数据体长度 数据头19 + 数据体9
		short len = 19 + 9;
		return FillPacket.getPacket(new FillHead(len, (byte)serialNum, devId, (byte)0x0b, (byte)1), body);
	}


	/**
	 * 终端注册成功， 只要数据库中有该设备信息，就给注册成功应答
	 * @param devId
	 * @param serialNum
	 * @param cmdType
	 * @return
	 */
	public static byte[] packetDownRegisterSuccess(byte[] devId, int serialNum, int cmdType) {
		// 组应答消息体
//		byte[] body = new byte[19];
//		int offset = 0;
//
//		// 应答结果
//		body[offset] = 0x00; // 4

		byte[] body = null;
		return FillPacket.getPacket(new FillHead((short)19, (byte)serialNum, devId, (byte)0x08, (byte)0), body);
	}


	/**
	 * 终端注册失败 失败的情况：数据库中没有录入该设备信息
	 * @param devId
	 * @param serialNum
	 * @param cmdType
	 * @return
	 */
	public static byte[] packetDownRegisterFail(byte[] devId, int serialNum, int cmdType) {
		// 组应答消息体
//		byte[] body = new byte[19];
//		int offset = 0;
//
//		// 应答结果
//		body[offset] = 0x00; // 4

		byte[] body = null;
		return FillPacket.getPacket(new FillHead((short)19, (byte)serialNum, devId, (byte)0x09, (byte)0), body);
	}


	/**
	 * 下发符带字符信
	 * @param devId
	 * @param serialNum
	 * @param cmdType
	 * @param information
	 * @return
	 */
	public static byte[] packetDownInformation(byte[] devId, int serialNum, int cmdType, String information) {
		// 组应答消息体
//		byte[] body = new byte[19];
//		int offset = 0;
//
//		// 应答结果
//		body[offset] = 0x00; // 4

		byte[] body = null;
		return FillPacket.getPacket(new FillHead((short)19, (byte)serialNum, devId, (byte)0x09, (byte)0), body);
	}

}
