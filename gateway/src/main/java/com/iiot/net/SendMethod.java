package com.iiot.net;

import io.netty.channel.ChannelHandlerContext;

public class SendMethod {
	/**
	 * 
	 * @param pack 发送的数据包
	 * @return
	 */
	public static boolean send(byte[] pack){

		boolean iiotConnected = ProcIIOTServer.procIIOIIsConnected();


		if(!iiotConnected){
			return false;
		} else {
			ServerIIOT.getInst().send(pack);
		}

		return true;
		
	}

}
