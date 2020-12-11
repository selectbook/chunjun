package com.iiot.commCommon;

import java.io.Serializable;

public class WifiInfo implements Serializable {

	private static final long serialVersionUID = -4026549371894365994L;

	private String mac; // MAC地址
	private int sig; // 信号值

	public WifiInfo() {
		super();
	}

	public WifiInfo(String mac, int sig) {
		super();
		this.mac = mac;
		this.sig = sig;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public int getSig() {
		return sig;
	}

	public void setSig(int sig) {
		this.sig = sig;
	}

	@Override
	public String toString() {
		return "WifiInfo [mac=" + mac + ", sig=" + sig + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mac == null) ? 0 : mac.hashCode());
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
		WifiInfo other = (WifiInfo) obj;
		if (mac == null) {
			if (other.mac != null)
				return false;
		} else if (!mac.equals(other.mac))
			return false;
		if (sig != other.sig)
			return false;
		return true;
	}

}
