package com.iiot.commCommon;

import java.io.Serializable;

/**
 * 单基站实体类
 * @author
 *
 */
public class SingleBaseStation implements Serializable {

	private static final long serialVersionUID = 9186748182954629137L;

	private int mcc; // 国号
	private int mnc; // 运营商码
	private int lac; // 区号
	private int cel; // 塔号
	private int sig; // 信号强度

	public SingleBaseStation() {
		super();
	}

	public SingleBaseStation(int mcc, int mnc, int lac, int cel, int sig) {
		super();
		this.mcc = mcc;
		this.mnc = mnc;
		this.lac = lac;
		this.cel = cel;
		this.sig = sig;
	}

	public int getMcc() {
		return mcc;
	}

	public void setMcc(int mcc) {
		this.mcc = mcc;
	}

	public int getMnc() {
		return mnc;
	}

	public void setMnc(int mnc) {
		this.mnc = mnc;
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
		return "SingleBaseStation [mcc=" + mcc + ", mnc=" + mnc + ", lac=" + lac + ", cel=" + cel + ", sig=" + sig
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cel;
		result = prime * result + lac;
		result = prime * result + mcc;
		result = prime * result + mnc;
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
		SingleBaseStation other = (SingleBaseStation) obj;
		if (cel != other.cel)
			return false;
		if (lac != other.lac)
			return false;
		if (mcc != other.mcc)
			return false;
		if (mnc != other.mnc)
			return false;
		if (sig != other.sig)
			return false;
		return true;
	}

}
