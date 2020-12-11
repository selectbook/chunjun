package com.iiot.commCommon;

import java.io.Serializable;

/**
 * 电池电压量实体类
 * 
 * @author
 *
 */
public class Power implements Serializable {

	private static final long serialVersionUID = 440892359560815296L;
	int totalPower; // 总电量
	int surplusPower; // 剩余电量

	public Power() {
		super();
	}

	public Power(int totalPower, int surplusPower) {
		super();
		this.totalPower = totalPower;
		this.surplusPower = surplusPower;
	}

	public int getTotalPower() {
		return totalPower;
	}

	public void setTotalPower(int totalPower) {
		this.totalPower = totalPower;
	}

	public int getSurplusPower() {
		return surplusPower;
	}

	public void setSurplusPower(int surplusPower) {
		this.surplusPower = surplusPower;
	}

	@Override
	public String toString() {
		return "Power [totalPower=" + totalPower + ", surplusPower=" + surplusPower + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + surplusPower;
		result = prime * result + totalPower;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Power other = (Power) obj;
		if (surplusPower != other.surplusPower)
			return false;
		if (totalPower != other.totalPower)
			return false;
		return true;
	}

}
