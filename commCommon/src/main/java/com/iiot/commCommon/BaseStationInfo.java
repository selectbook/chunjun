package com.iiot.commCommon;

import java.io.Serializable;

/**
 * 基站数据信息
 * @author
 *
 */
public class BaseStationInfo implements Serializable {

	private static final long serialVersionUID = -2478464247409534627L;

	int lac; // 区号
	int cel; // 塔号
	int sig; // 信号强度

	public BaseStationInfo() {
		super();
	}

	public BaseStationInfo(int lac, int cel, int sig) {
		super();
		this.lac = lac;
		this.cel = cel;
		this.sig = sig;
	}

	public int getLac() {
		return lac;
	}

	public void setLac(int lac) {
		this.lac = lac;
	}

	public int getCel() {
		return cel;
	}

	public void setCel(int cel) {
		this.cel = cel;
	}

	public int getSig() {
		return sig;
	}

	public void setSig(int sig) {
		this.sig = sig;
	}

	@Override
	public String toString() {
		return "BaseStationInfo [lac=" + lac + ", cel=" + cel + ", sig=" + sig + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cel;
		result = prime * result + lac;
		result = prime * result + sig;
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
		BaseStationInfo other = (BaseStationInfo) obj;
		if (cel != other.cel)
			return false;
		if (lac != other.lac)
			return false;
		if (sig != other.sig)
			return false;
		return true;
	}

}
