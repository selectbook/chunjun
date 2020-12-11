package com.iiot.commCommon;

import java.io.Serializable;

//通用协议的总体结构体
public class World implements Serializable {
	private static final long serialVersionUID = 1L;
	// 网关的ID，一般在第一次使用时生成的UUID，生成后记录这个ID，以后每次都用这个ID
	// String gateway;

	// 终端的协议类型，值有：JT,A6,DB44,…这里的类型只区分到大类，而像A5,A5C,A5E只能依赖于录入车辆和终端的数据才能解析出来
	String protocolType;

	// 流水号，在2929协议中没有，但是在部标上面有；流水号对命令下发和回复有很大的作用
	long sequenceId;

	// 终端ID，2929协议中是伪IP，数据库中使用伪IP对应车辆ID，数据库中的终端ID对通信实际没有太大的用处
	String id;

	// 2929数据类型 "namedPos"表示点名位置数据 "ackPacket"表示车载终端确认包数据
	// JT数据类型 "namedPos"表示点名位置数据 "commonAnswer"表示终端通用应答
	String objType;

	// 数据对象
	Object obj;

	public World() {
		super();
	}

	public World(String objType) {
		super();
		this.objType = objType;
	}

	public World(String protocolType, long sequenceId, String id, String objType, Object obj) {
		super();
		this.protocolType = protocolType;
		this.sequenceId = sequenceId;
		this.id = id;
		this.objType = objType;
		this.obj = obj;
	}

	public String getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}

	public long getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(long sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((obj == null) ? 0 : obj.hashCode());
		result = prime * result + ((objType == null) ? 0 : objType.hashCode());
		result = prime * result + ((protocolType == null) ? 0 : protocolType.hashCode());
		result = prime * result + (int) (sequenceId ^ (sequenceId >>> 32));
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
		World other = (World) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (this.obj == null) {
			if (other.obj != null)
				return false;
		} else if (!this.obj.equals(other.obj))
			return false;
		if (objType == null) {
			if (other.objType != null)
				return false;
		} else if (!objType.equals(other.objType))
			return false;
		if (protocolType == null) {
			if (other.protocolType != null)
				return false;
		} else if (!protocolType.equals(other.protocolType))
			return false;
		if (sequenceId != other.sequenceId)
			return false;
		return true;
	}

}
