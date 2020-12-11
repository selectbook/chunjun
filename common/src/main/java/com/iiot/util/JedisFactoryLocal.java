package com.iiot.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import com.iiot.jedis.HostAndPort;
import com.iiot.jedis.Jedis;
import com.iiot.jedis.JedisCluster;
import com.iiot.jedis.JedisPool;
import com.iiot.jedis.JedisPoolConfig;

public class JedisFactoryLocal {
	static JedisCluster jedisCluster;
	static Jedis jc;
	static  JedisPool pool=null;
	static Logger logger = Logger.getLogger(JedisFactoryLocal.class);
	protected static final int DEFAULT_TIMEOUT = 2000;
	protected static final int DEFAULT_MAX_REDIRECTIONS = 5;

	private static void CreateJedis() {
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			int maxIdle = Integer.parseInt(PropertiesUtilLocal.getConfig("maxIdle"));
			config.setMaxIdle(maxIdle);
			int maxTotal = Integer.parseInt(PropertiesUtilLocal.getConfig("maxTotal"));
			config.setMaxTotal(maxTotal);
			int maxWaitMillis = Integer.parseInt(PropertiesUtilLocal.getConfig("maxWaitMillis"));
			config.setMaxWaitMillis(maxWaitMillis);
			// config.setTestOnBorrow(true);
			String hosts = PropertiesUtilLocal.getConfig("host");
			String[] hostArr = hosts.split(";");
			int port = Integer.parseInt(PropertiesUtilLocal.getConfig("redisport"));
			Set<HostAndPort> nodes = new HashSet<HostAndPort>();
			for (String host : hostArr) {
				nodes.add(new HostAndPort(host, port));
			}
			String redisPsw = PropertiesUtilLocal.getConfig("redisPsw");
			logger.info("redis配置：nodes: " + JSON.toJSONString(nodes) + ", maxIdle: " + maxIdle + ", maxTotal: "
					+ maxTotal + ", maxWaitMillis: " + maxWaitMillis);
			//没有密码就不使用密码
			if (redisPsw == null || redisPsw.equals("")) {
				logger.info("REDIS不使用密码");
				jedisCluster = new JedisCluster(nodes, config);
			} else {
				logger.info("REDIS使用密码");
				jedisCluster = new JedisCluster(nodes, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT, DEFAULT_MAX_REDIRECTIONS, redisPsw,
						config);
			}

		} catch (Exception e) {
			logger.error("添加REDIS组失败: " + ExceptionUtil.getStackStr(e));
		}
	}

	public static JedisCluster getJedisCluster() {
		if (null == jedisCluster) {
			CreateJedis();
		}
		return jedisCluster;
	}
	
	public static JedisPool getJedisPool(){
		 //创建Jedis链接池  
		if(pool !=null){
			return pool;
		}else{
			String ip = PropertiesUtilLocal.getConfig("ip");
			int port = Integer.parseInt(PropertiesUtilLocal.getConfig("redisport"));
			pool=new JedisPool(ip,port);
		}
		return pool;
	}
	
	
	public static void closeJedisPool(){
		if(pool==null){
			return;
		}
		pool.close(); 
	}
	
	
	public static Jedis getJedis() {
		pool=getJedisPool();
		if(pool==null){
			logger.info("JedisPool is null");
			return null;
		}else{
			return pool.getResource();
		}
	}
	
}
