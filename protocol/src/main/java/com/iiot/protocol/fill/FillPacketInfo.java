package com.iiot.protocol.fill;

public class FillPacketInfo {
	// 消息总包数
	private int packetTotal;

	// 包序号
	private int packetNo;

	public int getPacketTotal() {
		return packetTotal;
	}

	public void setPacketTotal(int packetTotal) {
		this.packetTotal = packetTotal;
	}

	public int getPacketNo() {
		return packetNo;
	}

	public void setPacketNo(int packetNo) {
		this.packetNo = packetNo;
	}

}
