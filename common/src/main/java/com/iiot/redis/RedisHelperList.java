package com.iiot.redis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.iiot.jedis.JedisCluster;
import com.iiot.util.JedisClusterCRC16;

/**
 * 
* @ClassName: RedisHelper
* @Description: REDIS相关的工具类
*
 */
public class RedisHelperList {
	/**
	 * 
	* @Title: mget
	* @Description: 
	* @param @param jc
	* @param @param keys
	* @param @return
	* @return List<String>    返回类型 
	* @throws
	 */
	static public List<String> mget(JedisCluster jc,String ...keys){
		List<String> retList = null;
		Map<Integer,List<String>> slotMap = new HashMap<Integer,List<String>>();
		for(final String key : keys){
			int slot = JedisClusterCRC16.getSlot(key);
			List<String> list = slotMap.get(slot);
			if(list == null){
				slotMap.put(new Integer(slot), new LinkedList<String>(){
					{
						add(key);
					}
				});
			}else{
				list.add(key);
			}
		}
		//对于每一个都 调用mget
		Iterator<Entry<Integer,List<String>>> iter = slotMap.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Integer,List<String>> entry = iter.next();
			String keyArray[] = new String[entry.getValue().size()];
			entry.getValue().toArray(keyArray);
			List<String> valueList = jc.mget(keyArray);
			if(retList == null){
				retList = valueList;
			}else{
				retList.addAll(valueList);
			}
		}		
		
		return retList;
	}
}
