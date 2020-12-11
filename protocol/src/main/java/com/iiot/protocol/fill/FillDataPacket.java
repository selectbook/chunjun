package com.iiot.protocol.fill;

import java.util.List;

import com.iiot.common.bytes.Conv;
import org.apache.log4j.Logger;
/**
 * 分包
 * 
 * @author
 *
 */
public class FillDataPacket {

	private static Logger log = Logger.getLogger(FillDataPacket.class);

	/**
	 * 从数据的开始的位置找到所有正确的完整包、错误包 并判断最后未处理的数据是不处理还是全部丢掉
//	 * @param in 字节流缓冲区
	 * @param OKList 用于放正确包
	 * @param NGList 用于放错误包
	 * @param data 从缓冲区读取的数据
	 * @return
	 */
	public static int analysisPacket(List<Object> OKList, List<Object> NGList, byte[] data) {

		// 一次可以处理一个包，也可以一次处理多个包（给List多次add）
		int len = data.length;

		// 对长度有最小要求
		// 至少15字节：标识位（1）+ 消息头（12）+ 校验（1）+ 包尾（1）
		final int PACK_MIN_LEN = 15;

		if (len < PACK_MIN_LEN) {
			return 0;
		}

		// 最后未解析数据的索引
		int lastOkIndex = 0;

		int offset = 0;
		while (offset < len) {
			// 找包头
			int oldOffset = offset;
			int posHead = FillPacketUtil.getPosHead(data, offset);
			if (posHead < 0) {
				// 没有包头,全部数据丢掉
				// NGList.add(in.readBytes(in.readableBytes()));
				return lastOkIndex;
			}

			// 将包头前面的数据放入NGList
			if (posHead >= oldOffset && posHead != lastOkIndex) {
				byte[] arr = new byte[posHead - lastOkIndex];
				System.arraycopy(data, lastOkIndex, arr, 0, posHead - lastOkIndex);
				NGList.add(arr);
				// log.info("包头前面的数据：" + HexStr.toStr(arr) + "<==>" + "整包数据：" + HexStr.toStr(data));
				lastOkIndex = posHead;
			}

			if (len - posHead < 15) {
				return lastOkIndex;
			}

			offset = posHead + 1;

			// 对部分数据做转义，计算包尾位置(理论位置，不一定是，因为数据没有反转义)
			// 只校验10字节，得到的数组不包括包头，数据首字节为消息ID(2字节)
			// 计算包尾位置 包头 + 消息头 + 消息体 + 校验
			byte[] esc = FillPacketUtil.escapeReverseData(data, posHead, 10);
			int prop = Conv.getShortNetOrder(esc, 2); // 消息体属性
			int body_len = prop & 0x3FF; // 消息体长度
			int pac_item = (prop & 0x2000) != 0 ? 4 : 0; // 消息包封装项
			int pos_tail = 1 + 12 + pac_item + body_len + 1;

			// 计算的包尾位置大于数据的长度，重新找包头
			if (pos_tail > len) {
				continue;
			}

			// 找包尾,对于有多包的情况，应对pos_tail加posHead处理
			// 如不加posHead，可能找到的包尾就是上一次找到的包尾，这样一来
			// 就出现了一包数据的包尾位置在包头的位置之前(例如：两包粘包)
			int posTail = FillPacketUtil.getPosTail(data, pos_tail + posHead);

			// 找不到包尾
			if (posTail < 0) {
				continue;
			}

			// 组包
			int pac_len = posTail - posHead + 1;

			// 最小长度校验
			if (pac_len < 15) {
				continue;
			}

			// 数据长度校验 
			// 包头(1字节) + 消息头(12+N字节) + 消息体(N字节) + 校验(1字节) + 包尾(1字节) + 转义次数
			// 这里需要注意一包数据转义了多少次，dataLen要加上转义的次数。
			int escCount = FillPacketUtil.escapeCount(data, posHead, pac_len);
			int dataLen = 1 + 12 + pac_item + body_len + 1 + 1 + escCount;
			if (pac_len != dataLen) {
				continue;
			}

			// 校验			
			if (!FillPacketUtil.getCheckResult(data, posHead, pac_len)) {
				// log.info("分包数据：" + HexStr.toStr(data, posHead, pac_len) + "<===>" + "原始数据：" + HexStr.toStr(data));
				continue;
			}

			// 成功，把数据放进List
			byte[] array = new byte[pac_len];
			System.arraycopy(data, posHead, array, 0, pac_len);
			OKList.add(array);

			// 对于多个包来说，找完一个包还要接着找下一个包。
			// 重新开始找包头
			offset = posTail + 1;

			// 未处理的数据
			lastOkIndex = offset;
		}
		return lastOkIndex;
	}
}
