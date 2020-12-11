package com.iiot.commCommon;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//通用位置数据
public class Pos implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	private Date devTime; // 终端发送时间
	private Date recvTime; // 平台接收时间
	private Double lon; // 经度
	private Double lat; // 纬度
	private Double speed; // 速度
	private Double direct; // 方向
	private Double mileage; // 里程
	private Integer isAcc; // ACC状态
	private Integer isPos; // 是否定位
	Map<String, Object> extend;// 扩展

	public Pos cloneMe() {
		try {
			Pos newPos = (Pos) super.clone();
			Map<String, Object> newExtend = new HashMap<>();
			newExtend.putAll(extend);
			newPos.setExtend(newExtend);
			return newPos;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public Pos() {
		super();
		//为每个值初始化，不然在序列化时会出错
		devTime = new Date();
		recvTime = new Date();
		lon = 0.0; // 经度
		lat = 0.0;
		speed = 0.0; // 速度
		direct = 0.0; // 方向
		mileage = 0.0; // 里程
		isAcc = 0; // ACC状态
		isPos = 0; // 是否定位
		extend = new HashMap<>();
	}

	public Pos(Date devTime, Date recvTime, Double lon, Double lat, Double speed, Double direct, Double mileage,
               int isAcc, int isPos, Map<String, Object> extend) {
		super();
		this.devTime = devTime;
		this.recvTime = recvTime;
		this.lon = lon;
		this.lat = lat;
		this.speed = speed;
		this.direct = direct;
		this.mileage = mileage;
		this.isAcc = isAcc;
		this.isPos = isPos;
		this.extend = extend;
	}

	public Date getDevTime() {
		return devTime;
	}

	public void setDevTime(Date devTime) {
		this.devTime = devTime;
	}

	public Date getRecvTime() {
		return recvTime;
	}

	public void setRecvTime(Date recvTime) {
		this.recvTime = recvTime;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Double getDirect() {
		return direct;
	}

	public void setDirect(Double direct) {
		this.direct = direct;
	}

	public Double getMileage() {
		return mileage;
	}

	public void setMileage(Double mileage) {
		this.mileage = mileage;
	}

	public Integer getIsAcc() {
		return isAcc;
	}

	public void setIsAcc(Integer isAcc) {
		this.isAcc = isAcc;
	}

	public Integer getIsPos() {
		return isPos;
	}

	public void setIsPos(Integer isPos) {
		this.isPos = isPos;
	}

	public Map<String, Object> getExtend() {
		return extend;
	}

	public void setExtend(Map<String, Object> extend) {
		this.extend = extend;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "Pos [devTime=" + devTime + ", recvTime=" + recvTime + ", lon=" + lon + ", lat=" + lat + ", speed="
				+ speed + ", direct=" + direct + ", mileage=" + mileage + ", isAcc=" + isAcc + ", isPos=" + isPos
				+ ", extend=" + extend + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((devTime == null) ? 0 : devTime.hashCode());
		result = prime * result + ((direct == null) ? 0 : direct.hashCode());
		result = prime * result + ((extend == null) ? 0 : extend.hashCode());
		result = prime * result + ((isAcc == null) ? 0 : isAcc.hashCode());
		result = prime * result + ((isPos == null) ? 0 : isPos.hashCode());
		result = prime * result + ((lat == null) ? 0 : lat.hashCode());
		result = prime * result + ((lon == null) ? 0 : lon.hashCode());
		result = prime * result + ((mileage == null) ? 0 : mileage.hashCode());
		result = prime * result + ((recvTime == null) ? 0 : recvTime.hashCode());
		result = prime * result + ((speed == null) ? 0 : speed.hashCode());
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
		Pos other = (Pos) obj;
		if (devTime == null) {
			if (other.devTime != null)
				return false;
		} else if (!devTime.equals(other.devTime))
			return false;
		if (direct == null) {
			if (other.direct != null)
				return false;
		} else if (!direct.equals(other.direct))
			return false;
		if (extend == null) {
			if (other.extend != null)
				return false;
		} else if (!extend.equals(other.extend))
			return false;
		if (isAcc == null) {
			if (other.isAcc != null)
				return false;
		} else if (!isAcc.equals(other.isAcc))
			return false;
		if (isPos == null) {
			if (other.isPos != null)
				return false;
		} else if (!isPos.equals(other.isPos))
			return false;
		if (lat == null) {
			if (other.lat != null)
				return false;
		} else if (!lat.equals(other.lat))
			return false;
		if (lon == null) {
			if (other.lon != null)
				return false;
		} else if (!lon.equals(other.lon))
			return false;
		if (mileage == null) {
			if (other.mileage != null)
				return false;
		} else if (!mileage.equals(other.mileage))
			return false;
		if (recvTime == null) {
			if (other.recvTime != null)
				return false;
		} else if (!recvTime.equals(other.recvTime))
			return false;
		if (speed == null) {
			if (other.speed != null)
				return false;
		} else if (!speed.equals(other.speed))
			return false;
		return true;
	}

}
