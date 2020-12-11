package com.iiot.source;

import java.util.ArrayList;
import java.util.List;

import com.iiot.common.bytes.HexStr;
import com.iiot.protocol.fill.FillPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class Packet29 extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// 有多少字节可读
		int len = in.readableBytes();

		// 转为数组
		byte[] data = new byte[len];

		// ByteBuf getBytes(in.readerIndex(),data)
		// 将这个缓冲区的数据转移到指定的目的地从指定的绝对索引开始
		// 这个方法不会修改buffer读索引（readerIndex）或者写索引（writerIndex）
		// int readerIndex()方法返回buffer的读索引
		in.getBytes(in.readerIndex(), data);

		// 存放正确包的集合
		List<Object> OKList = new ArrayList<Object>();

		// 存放错误包的集合
		List<Object> NGList = new ArrayList<Object>();

		System.out.println(HexStr.toStr(data));

		int lastOkIndex = FillPacket.analysisPacket(OKList, NGList, data);

		// 这里要最后正确位置之前的数据读取(也就是将ByteBuf的读指针偏移到最后正确位置处)
		// 否则会导致ByteBuf数据逐渐变大
		in.readBytes(lastOkIndex);
//		System.out.println("lastOkIndex: " + lastOkIndex);
//		System.out.println("OKList: " + OKList.size());
//		System.out.println("NGList: " + NGList.size());

		// 将完整包放入out
		out.addAll(OKList);

		// 将错误包放入out
		// out.addAll(NGList);

		// 最后未解析的数据
		int unparseLen = len - lastOkIndex;

		// 找包头
		int posHead = FillPacket.getPosHead(data, lastOkIndex);
		if (posHead < 0 && lastOkIndex < len) {
			byte[] arr = new byte[unparseLen];
			System.arraycopy(data, lastOkIndex, arr, 0, unparseLen);
			NGList.add(arr);
			in.readBytes(in.readableBytes());
			return;
		}

		if (len - posHead < 20) {
			return;
		}

		if (unparseLen <= 1024 * 64) {
			return;
		} else {
			// 数据太多,全部数据丢掉
			byte[] arr = new byte[unparseLen];
			System.arraycopy(data, posHead, arr, 0, unparseLen);
			NGList.add(arr);
			in.readBytes(in.readableBytes());
			return;
		}
	}

}
