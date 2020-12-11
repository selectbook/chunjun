package com.iiot.protocol.fill.def;

/**
 * 
* @ClassName: JTDefRegister
* @Description: 终端注册类
* @author hujingbang 
* @date 2017年6月21日 上午9:56:33
*
 */
public class FillDefRegister {
	//省域ID
	int provinceId;
	// 市县域ID
	int citicountyId;
	// 制造商ID
	byte[] manufacturerId;
	// 终端型号
	byte[] terminalModelType;
	// 终端ID
	byte[] terminalId;
	// 车牌颜色
	byte vehicleColor;
	// 车牌
	String vehicleId;

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public int getCiticountyId() {
		return citicountyId;
	}

	public void setCiticountyId(int citicountyId) {
		this.citicountyId = citicountyId;
	}

	public byte[] getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(byte[] manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public byte[] getTerminalModelType() {
		return terminalModelType;
	}

	public void setTerminalModelType(byte[] terminalModelType) {
		this.terminalModelType = terminalModelType;
	}

	public byte[] getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(byte[] terminalId) {
		this.terminalId = terminalId;
	}

	public byte getVehicleColor() {
		return vehicleColor;
	}

	public void setVehicleColor(byte vehicleColor) {
		this.vehicleColor = vehicleColor;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}
	
	
}
