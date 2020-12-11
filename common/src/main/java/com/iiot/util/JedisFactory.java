package com.iiot.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import com.iiot.jedis.HostAndPort;
import com.iiot.jedis.JedisCluster;
import com.iiot.jedis.JedisPoolConfig;

public class JedisFactory {
	static JedisCluster jc;
	static Logger logger = Logger.getLogger(JedisFactory.class);
	protected static final int DEFAULT_TIMEOUT = 2000;
	protected static final int DEFAULT_MAX_REDIRECTIONS = 5;

	private static void CreateJedis() {
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			int maxIdle = Integer.parseInt(PropertiesUtil.getConfig("maxIdle"));
			config.setMaxIdle(maxIdle);
			int maxTotal = Integer.parseInt(PropertiesUtil.getConfig("maxTotal"));
			config.setMaxTotal(maxTotal);
			int maxWaitMillis = Integer.parseInt(PropertiesUtil.getConfig("maxWaitMillis"));
			config.setMaxWaitMillis(maxWaitMillis);
			// config.setTestOnBorrow(true);
			String hosts = PropertiesUtil.getConfig("host");
			String[] hostArr = hosts.split(";");
			int port = Integer.parseInt(PropertiesUtil.getConfig("redisport"));
			Set<HostAndPort> nodes = new HashSet<HostAndPort>();
			for (String host : hostArr) {
				nodes.add(new HostAndPort(host, port));
			}
			String redisPsw = PropertiesUtil.getConfig("redisPsw");
			logger.info("redis配置：nodes: " + JSON.toJSONString(nodes) + ", maxIdle: " + maxIdle + ", maxTotal: "
					+ maxTotal + ", maxWaitMillis: " + maxWaitMillis);
			//没有密码就不使用密码
			if (redisPsw == null || redisPsw.equals("")) {
				logger.info("REDIS不使用密码");
				jc = new JedisCluster(nodes, config);
			} else {
				logger.info("REDIS使用密码");
				jc = new JedisCluster(nodes, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT, DEFAULT_MAX_REDIRECTIONS, redisPsw,
						config);
			}

		} catch (Exception e) {
			logger.error("添加REDIS组失败: " + ExceptionUtil.getStackStr(e));
		}
	}

	public static JedisCluster getJedis() {
		if (null == jc) {
			CreateJedis();
		}
		return jc;
	}
}
