package com.iiot.commCommon;

import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBuf;

//各个终端协议需要实现的接口
public interface ProtocolBase {
	// 开始
	// 解析模块中，可以根据终端VIP得到
	public void Start(Funs funs) throws Exception;

	// 停止
	public void Stop() throws Exception;

	// 分包
	public void Packet(ByteBuf in, List<Object> out) throws Exception;

	// 数据转换， 上发
	public TransformUpResult TransformUp(byte[] array, String pro) throws Exception;

	// 数据转换， 下发
	public Map<String, Object> TransformDown(World world) throws Exception;

	// 判断命令下发是否立即响应
	// 对于A6协议来说，TCP通信命令下发后，直接响应
	// 对于部标协议来说，TCP通信命令下发后，要收到上传数据后再响应
	public boolean isResponseNow() throws Exception;

	// 对于V3协议，判断是否要缓存终端ID
	// V3协议数据包中只有登录信息包带有终端ID，其它包不带终端ID
	public boolean isCachedTerminalId() throws Exception;

}
