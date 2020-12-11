package com.iiot.source;

import com.iiot.commCommon.Funs;
import com.iiot.commCommon.ProtocolBase;
import com.iiot.commCommon.TransformUpResult;
import com.iiot.commCommon.World;
import com.iiot.common.bytes.HexStr;
import com.iiot.protocol.fill.FillCmdIssued;
import com.iiot.protocol.fill.FillDataPacket;
import com.iiot.protocol.fill.FillPacketUtil;
import io.netty.buffer.ByteBuf;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Fill implements ProtocolBase {

	Funs funs = null;
	static Logger log = Logger.getLogger(Fill.class);

	// 开始
	public void Start(Funs funs) throws Exception {
		this.funs = funs;
	}

	// 停止
	public void Stop() throws Exception {

	}

	// 分包
	public void Packet(ByteBuf in, List<Object> out) throws Exception {
		// int readableBytes()方法返回ByteBuf有多少字节可读
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
		
		// log.info("整包数据：" + HexStr.toStr(data));

		int lastOkIndex = FillDataPacket.analysisPacket(OKList, NGList, data);

		// 这里要读取最后正确位置之前的数据(也就是将ByteBuf的读指针偏移到最后正确位置处)
		// 否则会导致ByteBuf数据逐渐变大
		in.readBytes(lastOkIndex).release();

		// 将完整包放入out
		out.addAll(OKList);

		// 将错误包放入out
		// out.addAll(NGList);

		int unparseLen = len - lastOkIndex;

		// 找包头
		int posHead = FillPacketUtil.getPosHead(data, lastOkIndex);
		if (posHead < 0 && lastOkIndex < len) {
			// 没有包头,全部数据丢掉
			byte[] arr = new byte[unparseLen];
			System.arraycopy(data, lastOkIndex, arr, 0, unparseLen);
			NGList.add(arr);
			// log.info("未找到包头数据：" + HexStr.toStr(arr));
			in.readBytes(in.readableBytes()).release();
			return;
		}

		if (len - posHead < 15) {
			return;
		}

		if (unparseLen < 2048) {
			return;
		} else {
			// 数据太多,全部数据丢掉
			byte[] arr = new byte[unparseLen];
			System.arraycopy(data, posHead, arr, 0, unparseLen);
			NGList.add(arr);
			in.readBytes(in.readableBytes()).release();
			return;
		}
	}

	// 上发
	public TransformUpResult TransformUp(byte[] array, String pro) throws Exception {
		TransformUpResult result = FillParser.parserData(array, funs, pro);
		return result;
	}

	// 下发
	public Map<String, Object> TransformDown(World world) throws Exception {
		Map<String, Object> issued = null;
		if (world != null) {
			issued = FillCmdIssued.parserIssuedData(world);
		}
		return issued;
	}

	// 判断命令下发是否立即响应
	// 对于部标协议来说，TCP通信命令下发后，要收到上传数据后再响应
	public boolean isResponseNow() throws Exception {
		return false;
	}

	@Override
	public boolean isCachedTerminalId() throws Exception {
		return false;
	}

	public static void main(String[] args) {
		byte[] array = HexStr.toArray("553A003a0058543336352D3030303030300a06000440a00000010440a00000020440a00000030443480000040443430000060707e4050a000000603D8F1C");
		Fill fill = new Fill();
		try {
			TransformUpResult result = fill.TransformUp(array, "tcp");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
