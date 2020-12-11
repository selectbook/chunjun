package com.iiot.redis;

import java.io.Serializable;

/**
 * 当redis当掉之后，将执行出现的异常的redis命令存储起来，生成对象，存在缓存中， 使用线程定时扫描，缓存中有数据就会持续执行redis命令
 * 
 * @author Administrator
 *
 */
public class RedisInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6292479585061977208L;
	// redis命令类型(如hset，或者hdel)
	private String type;
	// redis的大key
	private String mainKey;
	// redis的小key
	private String mapKey;
	// value(hdel这个值为空字符串)
	private String value = "";
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMainKey() {
		return mainKey;
	}
	public void setMainKey(String mainKey) {
		this.mainKey = mainKey;
	}
	public String getMapKey() {
		return mapKey;
	}
	public void setMapKey(String mapKey) {
		this.mapKey = mapKey;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public RedisInfo(String type, String mainKey, String mapKey, String value) {
		super();
		this.type = type;
		this.mainKey = mainKey;
		this.mapKey = mapKey;
		this.value = value;
	}
	public RedisInfo(String type, String mainKey, String mapKey) {
		super();
		this.type = type;
		this.mainKey = mainKey;
		this.mapKey = mapKey;
	}
	public RedisInfo(){
		super();
	}
}
