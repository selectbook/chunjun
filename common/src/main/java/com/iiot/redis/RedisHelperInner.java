package com.iiot.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.iiot.jedis.HostAndPort;
import com.iiot.jedis.Jedis;
import com.iiot.jedis.JedisCluster;
import com.iiot.jedis.JedisPool;
import com.iiot.jedis.Pipeline;

public class RedisHelperInner {
	static Logger logger = Logger.getLogger(RedisHelperInner.class);
	/**
	 * 
	* @ClassName: SlotKeys
	* @Description:
	*
	 */
	static class SlotKeys {
		// 低高点
		int low;
		int high;
		List<String> keys;

		SlotKeys() {
			keys = new LinkedList<>();
		}
	}

	static class KeysJedis<T> {
		// 低高点
		List<T> keys;
		JedisPool pool;

		// KeysJedis() {
		// keys = new LinkedList<>();
		// }

		public KeysJedis(List<T> keys, JedisPool pool) {
			super();
			this.keys = keys;
			this.pool = pool;
		}

		public KeysJedis(JedisPool pool) {
			super();
			keys = new LinkedList<>();
			this.pool = pool;
		}

	}

	static class Slots {
		// 低高点
		Long low;
		Long high;

		JedisPool pool;

		public Slots(Long low, Long high, JedisPool pool) {
			super();
			this.low = low;
			this.high = high;
			this.pool = pool;
		}
	}

	/**
	 * 
	* @Title: getSlotsList
	* @Description: 得到服务器的slot分布
	* @param @param jc
	* @param @return
	* @return List<Slots>    返回类型 
	* @throws
	 */
	static List<Slots> getSlotsList(JedisCluster jc) {
		List<Slots> slotsList = new LinkedList<>();
		Map<String, JedisPool> poolMap = jc.getClusterNodes();
		for (Entry<String, JedisPool> entry : poolMap.entrySet()) {
			JedisPool pool = entry.getValue();
			// String name = pool.getResource().clientGetname();
			// String name2 = pool.getResource().getClient().getHost();
			// List<String> ss = pool.getResource().clusterGetKeysInSlot(1, 10);
			// String cn = pool.getResource().clusterInfo();
			Jedis jedis = pool.getResource();
			try {
				List<Object> slots = jedis.clusterSlots();

				for (Object slot : slots) {
					ArrayList<Object> e = (ArrayList<Object>) slot;
					Long low = (Long) e.get(0);
					Long high = (Long) e.get(1);
					// 得到节点的信息
					ArrayList<Object> hostInfo = (ArrayList<Object>) e.get(2);
					String ip = new String((byte[]) hostInfo.get(0));
					Long port = (Long) hostInfo.get(1);
					String uuid = new String((byte[]) hostInfo.get(2));
					// 从ip端口得到这个slots对应的jedispool
					JedisPool thisPool = poolMap.get(ip + ":" + port);
					if (thisPool == null) {
						// 未找到，应该到不了这里
					}
					slotsList.add(new Slots(low, high, thisPool));
				}
			} finally {
				jedis.close();
			}
			// 只需要得到一个就知道了
			// break;
		}
		return slotsList;
	}

	/**
	 * @throws Exception 
	 * 
	* @Title: mgetPipelined
	* @Description: 用pipelined的方式批量获取数据
	* 如果使用mget有问题，当key在不同的slot（即使这些slots分配到同一个节点）会报错key不在同一个slot中不能执行
	* @param @param jc
	* @param @param keys
	* @param @return
	* @return Map<String,String>    返回类型 
	* @throws
	 */
	static public Map<String, String> mgetPipelined(Pipeline pl, List<String> keys) throws Exception {
		// 批量执行
		for (String key : keys) {
			pl.get(key);
		}
		List<Object> retList = pl.syncAndReturnAll();

		Map<String, String> retMap = new HashMap<>();
		int index = 0;
		for (String key : keys) {
			// 数组取应该是很快的
			Object obj = retList.get(index++);
			if (obj != null && !(obj instanceof String)) {
				throw new Exception("mgetPipelined !(obj instanceof String:" + key);
			}

			if (obj != null) {
				retMap.put(key, (String) obj);
			}

		}
		return retMap;
	}

	static public void msetPipelined(Pipeline pl, List<Entry<String, String>> keys) throws Exception {
		// 批量执行
		for (Entry<String, String> key : keys) {
			if (key.getKey() != null && key.getValue() != null) {
				pl.set(key.getKey(), key.getValue());
			}
		}
		pl.sync();
	}

	static public void mhsetPipelined(Pipeline pl, List<Entry<String, HKeyValue>> keys) throws Exception {
		// 批量执行
		for (Entry<String, HKeyValue> key : keys) {
			HKeyValue hvalue = key.getValue();
			if (key.getKey() != null && hvalue.getSkey() != null && hvalue.getValue() != null) {
				pl.hset(key.getKey(), hvalue.getSkey(), hvalue.getValue());
			}
		}
		pl.sync();
	}

	//mget版本
	static public Map<String,Map<String,String>> mhgetMultiPipelined(Pipeline pl, List<Entry<String, List<String>>> keys) throws Exception {
		// 批量执行
		for (Entry<String, List<String>> key : keys) {
			List<String> hvalue = key.getValue();
			String[] smallKeys = new String[hvalue.size()];
			hvalue.toArray(smallKeys);
			pl.hmget(key.getKey(),smallKeys);
		}
		Map<String,Map<String,String>> retMap= new HashMap<String,Map<String,String>>();
		int index = 0;
		List<Object> retObj = pl.syncAndReturnAll();
		for (Entry<String, List<String>> key : keys) {
			Object obj = retObj.get(index++);
			if(obj instanceof List){
				List<String> valueStrs = (List<String>)obj;
				List<String> smallKeys = key.getValue();
				//key和值的个数肯定是一致的
				if(valueStrs.size() != smallKeys.size()){
					logger.error("retStrs.size() != smallKeys.size()");
					throw new Exception("retStrs.size() != smallKeys.size()");
				}
				//组成一个map
				Map<String,String> valueMap = new HashMap<>();
				Iterator<String> iterValue = valueStrs.iterator();
				Iterator<String> iterKey = smallKeys.iterator();
				while(iterValue.hasNext() && iterKey.hasNext()){
					valueMap.put(iterKey.next(), iterValue.next());
				}
				retMap.put(key.getKey(), valueMap);
			}
		}
		return retMap;
	}
	static public void mhsetMultiPipelined(Pipeline pl, List<Entry<String, List<HKeyValue>>> keys) throws Exception {
		// 批量执行
		for (Entry<String, List<HKeyValue>> key : keys) {
			List<HKeyValue> hvalue = key.getValue();
			// 转为Map<String,String>
			Map<String, String> skeyValue = new HashMap<>();
			for (HKeyValue kv : hvalue) {
				if (kv.getSkey() != null && kv.getValue() != null) {
					skeyValue.put(kv.getSkey(), kv.getValue());
				}
			}
			pl.hmset(key.getKey(), skeyValue);
			// pl.hset(key.getKey(), hvalue.getSkey(),hvalue.getValue());
		}
		pl.sync();
	}

	// key只能维持10分钟
	static public void mhsetMultiPipelined10Min(Pipeline pl, List<Entry<String, List<HKeyValue>>> keys)
			throws Exception {
		// 批量执行
		for (Entry<String, List<HKeyValue>> key : keys) {
			List<HKeyValue> hvalue = key.getValue();
			// 转为Map<String,String>
			Map<String, String> skeyValue = new HashMap<>();
			for (HKeyValue kv : hvalue) {
				if (kv.getSkey() != null && kv.getValue() != null) {
					skeyValue.put(kv.getSkey(), kv.getValue());
				}
			}
			pl.hmset(key.getKey(), skeyValue);
			// key维持3天
			pl.expire(key.getKey(), 3 * 24 * 60 * 60);
			// pl.hset(key.getKey(), hvalue.getSkey(),hvalue.getValue());
		}
		pl.sync();
	}

	/**
	 * 
	* @Title: mhgetPipelined
	* @Description: 批量hget
	* @param @param pl
	* @param @param keys 大key和小key
	* @param @return
	* @param @throws Exception
	* @return Map<String,HKeyValue>    返回类型 
	* @throws
	 */
	static public Map<String, HKeyValue> mhgetPipelined(Pipeline pl, List<Entry<String, String>> keys)
			throws Exception {
		// 批量执行
		for (Entry<String, String> key : keys) {
			String hvalue = key.getValue();
			pl.hget(key.getKey(), hvalue);
		}
		List<Object> objList = pl.syncAndReturnAll();
		Map<String, HKeyValue> retMap = new HashMap<>();
		int index = 0;
		for (Entry<String, String> entry : keys) {
			Object obj = objList.get(index++);
			HKeyValue hvalue = new HKeyValue(entry.getKey(), (String) obj);
			retMap.put(entry.getKey(), hvalue);
		}
		return retMap;
	}

	/**
	 * 
	* @Title: mhgetAllPipelined
	* @Description: 根据大key获取
	* @param @param pl
	* @param @param keys
	* @param @return
	* @param @throws Exception
	* @return Map<String,HKeyValue>    返回类型 
	* @throws
	 */
	static public Map<String, Map<String, String>> mhgetAllPipelined(Pipeline pl, List<String> keys) throws Exception {
		// 批量执行
		for (String key : keys) {
			pl.hgetAll(key);
		}
		List<Object> objList = pl.syncAndReturnAll();
		Map<String, Map<String, String>> retMap = new HashMap<>();
		int index = 0;
		for (String key : keys) {
			Object obj = objList.get(index++);
			Map<String, String> kvMap = (Map<String, String>) obj;
			retMap.put(key, kvMap);
		}
		return retMap;
	}

	// 使用脚本sha执行，会对相同的key查询的值进行合并
	static public Map<String, Object> evalPipelined(Pipeline pl, String evalsha, List<RedisEval> keys)
			throws Exception {
		// 批量执行
		for (RedisEval key : keys) {
			pl.evalsha(evalsha, key.getKeys(), key.getParam());
		}
		List<Object> objList = pl.syncAndReturnAll();
		if (!(objList instanceof ArrayList)) {
			throw new Exception("evalPipelined内部实现问题，返回的不是ArrayList");
		}
		Map<String, Object> retMap = new HashMap<>();
		// 转换为map
		int index = 0;
		for (RedisEval eval : keys) {
			retMap.put(eval.getKeys().get(0), objList.get(index++));
		}
		return retMap;
	}
	
	    //使用脚本sha执行,对每个查询的key,都有一个返回值  
		static public Set<Object> evalUniquePipelined(Pipeline pl, String evalsha, List<RedisEval> keys)
				throws Exception {
			// 批量执行
			for (RedisEval key : keys) {
				pl.evalsha(evalsha, key.getKeys(), key.getParam());
			}
			List<Object> objList = pl.syncAndReturnAll();
			if (!(objList instanceof ArrayList)) {
				throw new Exception("evalPipelined内部实现问题，返回的不是ArrayList");
			}
			Set<Object> retSet = new HashSet<>();
			retSet.addAll(objList);
			return retSet;
		}
	
}
