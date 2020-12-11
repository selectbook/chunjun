package com.iiot.commCommon;

import java.io.Serializable;
import java.util.List;

/**
 * 多基站实体类
 * 
 * @author
 *
 */
public class MultipleBaseStation implements Serializable {

	private static final long serialVersionUID = -8628959794609950679L;

	int mcc; // 国号
	int mnc; // 运营商码
	List<BaseStationInfo> info; // 基站数据信息

	public MultipleBaseStation() {
		super();
	}

	public MultipleBaseStation(int mcc, int mnc, List<BaseStationInfo> info) {
		super();
		this.mcc = mcc;
		this.mnc = mnc;
		this.info = info;
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

	public List<BaseStationInfo> getInfo() {
		return info;
	}

	public void setInfo(List<BaseStationInfo> info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "MultipleBaseStation [mcc=" + mcc + ", mnc=" + mnc + ", info=" + info + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + mcc;
		result = prime * result + mnc;
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
		MultipleBaseStation other = (MultipleBaseStation) obj;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (mcc != other.mcc)
			return false;
		if (mnc != other.mnc)
			return false;
		return true;
	}

}
