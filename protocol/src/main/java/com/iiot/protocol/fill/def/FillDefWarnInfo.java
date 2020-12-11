package com.iiot.protocol.fill.def;

public class FillDefWarnInfo {
	private int serialNumber; // 报警消息流水号
	private long warnType; // 报警类型

	public int getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	public long getWarnType() {
		return warnType;
	}

	public void setWarnType(long warnType) {
		this.warnType = warnType;
	}

}
