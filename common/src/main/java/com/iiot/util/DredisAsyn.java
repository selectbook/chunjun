package com.iiot.util;

import java.util.function.Consumer;

import org.apache.log4j.Logger;

import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.cluster.RedisClusterClient;
import com.lambdaworks.redis.cluster.api.StatefulRedisClusterConnection;
import com.lambdaworks.redis.cluster.api.async.RedisAdvancedClusterAsyncCommands;

/**
 * <p>Title: Redis异步查询</p>
 */
public class DredisAsyn {

	static Logger logger = Logger.getLogger(DredisAsyn.class);
	static StatefulRedisClusterConnection<String, String> connection = null;

	private static void createAsyncConn() {
		String hosts = PropertiesUtil.getConfig("host");
		int port = PropertiesUtil.getConfigAsInt("redisport");
		String password = PropertiesUtil.getConfig("redisPsw");
		if (hosts != null) {
			String[] hostArr = hosts.split(";");
			String host = "";
			if (hostArr.length > 0) {
				host = hostArr[0];
			}
			RedisURI redisUri = null;
			if (password == null || "".equals(password)) {
				redisUri = RedisURI.Builder.redis(host).withPort(port).build();
			} else {
				redisUri = RedisURI.Builder.redis(host).withPort(port).withPassword(password).build();
			}
			RedisClusterClient client = RedisClusterClient.create(redisUri);
			connection = client.connect();
		}
	}

	public static StatefulRedisClusterConnection<String, String> getAsyncConn() {
		if (connection == null) {
			createAsyncConn();
		}

		return connection;
	}

	private static void start() {
		for (int i = 1; i <= 10; i++) {
			RedisAdvancedClusterAsyncCommands<String, String> asyncCmd = connection.async();
			asyncCmd.get("x").thenAccept(new Consumer<String>() {
				@Override
				public void accept(String s) {
					System.out.println("@@@值：" + s + "，###线程ID：" + Thread.currentThread().getId());
				}
			});

			System.out.println("@@@");
		}
	}

	public static void main(String[] args) {
		getAsyncConn();
		start();
		try {
			Thread.sleep(1000 * 5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
