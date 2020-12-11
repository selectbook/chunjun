package com.iiot.protocol.fill.def;

public class FillDefDriverInfo {
	byte driverNameLength; // 驾驶员姓名长度
	String driverName; // 驾驶员姓名
	String driverId; // 驾驶员身份证编号
	String licence; // 从业资格证编码
	byte[] orgNameLength; // 发证机关名称长度
	String orgName; // 发证机关名称

	public byte getDriverNameLength() {
		return driverNameLength;
	}

	public void setDriverNameLength(byte driverNameLength) {
		this.driverNameLength = driverNameLength;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public byte[] getOrgNameLength() {
		return orgNameLength;
	}

	public void setOrgNameLength(byte[] orgNameLength) {
		this.orgNameLength = orgNameLength;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getLicence() {
		return licence;
	}

	public void setLicence(String licence) {
		this.licence = licence;
	}
	
	

}
