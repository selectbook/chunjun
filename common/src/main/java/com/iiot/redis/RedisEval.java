package com.iiot.redis;

import java.util.LinkedList;
import java.util.List;

/**
 * 
* @ClassName: RedisEval
* @Description: redis执行eval的参数
*
 */
public class RedisEval {
	//虽然允许有多个KEY，但只是按第一个key来区分不同的slot
	List<String> keys;
	//脚本的参数
	List<String> param;
	
	
	public RedisEval() {
		super();
		keys = new LinkedList<>();
		param = new LinkedList<>();
	}
	public List<String> getKeys() {
		return keys;
	}
	public void setKeys(List<String> keys) {
		this.keys = keys;
	}
	public List<String> getParam() {
		return param;
	}
	public void setParam(List<String> param) {
		this.param = param;
	}
	
	
}
