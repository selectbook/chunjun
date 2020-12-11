package com.iiot.protocol.fill;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iiot.cmd.ComInfo;
import com.iiot.cmd.ParamMsg;
import com.iiot.commCommon.World;
import com.iiot.common.bytes.Conv;
import com.iiot.common.bytes.HexStr;
import com.iiot.util.ExceptionUtil;
import org.apache.log4j.Logger;


/**
 * 用于解析平台主动下发的数据
 * 
 * @author
 *
 */
public class FillCmdIssued {

	private static Logger log = Logger.getLogger(FillCmdIssued.class);

	public static final Map<String, Object> parserIssuedData(World world) {
		// 下发数据、平台下发的主命令、流水号放入map
		// 对于JT来说，数据有流水号
		if (world == null) {
			log.info("Param is null");
			return null;
		}

		ComInfo info = null;
		Object obj = world.getObj();
		if (obj instanceof ComInfo) {
			info = (ComInfo) obj;
		}

		if (info == null) {
			log.error("Data error");
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>();

		// 命令信息
		String cmdMsg = info.getCommandMsg().toUpperCase();

		// 数据
		ParamMsg data = null;
		List<Object> list = null;

		// 终端手机号
		String id = info.getDevid();
		byte[] devid = hexStringToByte(id);

		// 消息体
		byte[] body = null;

		// 数据包
		byte[] issued = null;

		// 下发指令
		int cmd;

		// 流水号
		int seqId = 0;

		switch (cmdMsg) {
		// 清除紧急报警
		case "CANCELSOSALARM":
			log.info("###清除紧急报警.");
			cmd = 0x8203;
			
			// 报警流水号(2字节)：需要人工确认的报警消息流水号，0表示该报警类型所有消息
			// 人工确认报警类型(4字节)：bit0：1 确认紧急报警􄆖􂎸􁚟􂍱􂉤
			int cancelAlarmOffset = 0;
			body = new byte[6];
			body[cancelAlarmOffset++]=0x00;
			body[cancelAlarmOffset++]=0x00;
			body[cancelAlarmOffset++]=0x00;
			body[cancelAlarmOffset++]=0x00;
			body[cancelAlarmOffset++]=0x00;
			body[cancelAlarmOffset++]=0x01;	
			
			issued = FillPacket.getPacket(new FillHead((byte) cmd, devid), body);

			// 获取流水号
			// 包头(1字节) + 消息ID(2字节) + 消息体属性(2字节) + 终端手机号(6字节) + 消息流水号(2字节)
			if (issued != null && issued.length >= 13) {
				seqId = Conv.getShortNetOrder(issued, 11);
			}
			map.put("data", issued);
			map.put("cmd", cmd);
			map.put("sn", seqId);
			break;

		default:
			// log.info("Untreated Command:" + cmdMsg);
			break;
		}
		return map;
	}

	/**
	 * 组调度下文消息体数据
	 * 
	 * @param disInfo
	 * @return
	 */
	private static byte[] getDispatchInfo(String disInfo) {
		if (disInfo == null) {
			return null;
		}

		// 内容
		int len = disInfo.length();
		int lastIndex = disInfo.lastIndexOf("@");
		String content = "";
		if (lastIndex >= 0) {
			content = disInfo.substring(0, lastIndex);
		}

		// 显示方式 1：紧急 4：显示器显示 8：TTS播读 16：广告屏显示
		// 默认显示方式：紧急
		int displayFlag = 1;
		if (lastIndex + 1 <= len) {
			try {
				displayFlag = Integer.parseInt(disInfo.substring(lastIndex + 1));
			} catch (Exception e) {
				log.error("数据转换异常：" + ExceptionUtil.getStackStr(e));
			}
		}

		byte[] contentArr = null;
		try {
			contentArr = content.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			log.error("编码集错误: " + ExceptionUtil.getStackStr(e));
			return null;
		}

		// 对数据进行判断
		int cLen = 0;
		if (contentArr != null) {
			cLen = contentArr.length;
		}
		if (contentArr != null && cLen > 1023) {
			log.error("数据太长: " + cLen);
			return null;
		}

		// 组消息体
		byte[] body = null;
		if (contentArr != null) {
			int offset = 0;
			body = new byte[cLen + 1];
			body[offset] = (byte) displayFlag;
			offset += 1;
			System.arraycopy(contentArr, 0, body, offset, cLen);
		}

		return body;
	}

	/**
	 * 组调度下文消息体数据，用于A5M设备设置回传间隔
	 * @param disInfo 内容
	 * @param disStyle 显示方式
	 * @return
	 */
	private static byte[] a5mDispatchInfo(String disInfo, int disStyle) {
		if (disInfo == null) {
			return null;
		}

		byte[] contentArr = null;
		try {
			contentArr = disInfo.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			log.error("编码集错误: " + ExceptionUtil.getStackStr(e));
			return null;
		}

		// 对数据进行判断
		int cLen = 0;
		if (contentArr != null) {
			cLen = contentArr.length;
		}

		if (cLen > 1023) {
			log.error("调度文本数据太长: " + cLen);
			return null;
		}

		// 组消息体
		byte[] body = null;
		if (contentArr != null) {
			int offset = 0;
			body = new byte[cLen + 1];
			body[offset] = (byte) disStyle;
			offset += 1;
			System.arraycopy(contentArr, 0, body, offset, cLen);
		}

		return body;
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

}
