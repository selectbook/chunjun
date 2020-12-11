package com.iiot.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import com.iiot.cloud.JedisClusterCat;
import com.iiot.jedis.HostAndPort;
import com.iiot.jedis.JedisPoolConfig;

public class JedisBSJFactory {
	static JedisClusterCat jc;
	static Logger logger = Logger.getLogger(JedisBSJFactory.class);
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
			//多节点并行执行数目
			int redisParallelNum = Integer.parseInt(PropertiesUtilLocal.getConfig("redisParallelNum"));
			// config.setTestOnBorrow(true);
			Set<HostAndPort> nodes = new HashSet<HostAndPort>();
			String hostports = PropertiesUtilLocal.getConfig("hostports");
            if(hostports !=null){
            	String[] singleHostPort = hostports.split(";");
            	for (String hostandport : singleHostPort) {
            		if(hostandport !=null&&!hostandport.equals("")){
            			String[] split = hostandport.split(":");
                		String host =split[0];
                		String portStr = split[1];
                		int port = Integer.parseInt(portStr.trim());
        				nodes.add(new HostAndPort(host, port));
            		}
    			}
            }else{
            	String hosts = PropertiesUtilLocal.getConfig("host");
    			String[] hostArr = hosts.split(";");
    			int port = Integer.parseInt(PropertiesUtilLocal.getConfig("redisport"));
    			for (String host : hostArr) {
    				nodes.add(new HostAndPort(host, port));
    			}
            }
			
			String redisPsw = PropertiesUtilLocal.getConfig("redisPsw");
			logger.info("redis配置：nodes: " + JSON.toJSONString(nodes) + ", maxIdle: " + maxIdle + ", maxTotal: "
					+ maxTotal + ", maxWaitMillis: " + maxWaitMillis+"/redisPsw:"+redisPsw);
			//没有密码就不使用密码
			if (redisPsw == null || redisPsw.equals("")) {
				logger.info("REDIS不使用密码");
				jc = new JedisClusterCat(nodes, config,redisParallelNum);
			} else {
				logger.info("REDIS使用密码:");
				jc = new JedisClusterCat(nodes, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT, DEFAULT_MAX_REDIRECTIONS, redisPsw,
						config,redisParallelNum);
			}

		} catch (Exception e) {
			logger.error("添加REDIS组失败: " + ExceptionUtil.getStackStr(e));
		}
	}

	public static JedisClusterCat getJedis() {
		if (null == jc) {
			CreateJedis();
		}
		return jc;
	}
}
