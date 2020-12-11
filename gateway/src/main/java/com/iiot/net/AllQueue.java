package com.iiot.net;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.iiot.commCommon.World;
import com.iiot.queue2.DataQueue2;
import com.iiot.util.ConstantUtil;
import com.iiot.util.YunDataSerialize;
import org.apache.log4j.Logger;


public class AllQueue {
	static Logger logger = Logger.getLogger(AllQueue.class);
	// 装载加注数据的queue，用于kafka
	private static DataQueue2 onsQueue;

	// 装载加注数据的queue，用于REDIS
	private static DataQueue2 redisQueue;

	static YunDataSerialize yds; // 序列化

	public static void pushObj(World world) throws Exception {
		byte[] array = yds.WorldToArray(world);
		if (array != null) {
				onsQueue.push(array);
		}
	}

	/* ========================= 缓存数据 ========================= */
	public static void onsPushObj(World world) throws Exception {
		byte[] array = yds.WorldToArray(world);
		if (array != null) {
			onsQueue.push(array);
		}
	}



	public static void redisPushObj(World world) throws Exception {
		byte[] array = yds.WorldToArray(world);
		if (array != null) {
			redisQueue.push(array);
		}
	}
	


	/* ========================= 初始化QUEUE ========================= */
	public static void onsStart() throws Exception {
		if (yds == null) {
			yds = new YunDataSerialize();
		}

		if (onsQueue == null) {
			onsQueue = new DataQueue2(
					new DataQueue2.DataQueue2Param("onsQueue", ConstantUtil.ROCKETMQ_MAX_MEM, ConstantUtil.ROCKETMQ_SUB_MEM));
		}
	}


	public static void redisStart() throws Exception {
		if (yds == null) {
			yds = new YunDataSerialize();
		}

		if (redisQueue == null) {
			redisQueue = new DataQueue2(
					new DataQueue2.DataQueue2Param("redisQueue", ConstantUtil.REDIS_MAX_MEM, ConstantUtil.REDIS_SUB_MEM));
		}
	}



	/* ========================= 关闭QUEUE ========================= */
	public static void onsStop() throws Exception {
		onsQueue.shutdown();
	}

	public static void redisStop() throws Exception {
		redisQueue.shutdown();
	}

	/* ========================= 取数据 ========================= */
	public static Queue<World> onsPop(int count) throws Exception {
		if (onsQueue == null) {
			return null;
		}
		List<byte[]> data = onsQueue.pop(count);
		Queue<World> retQueue = new LinkedList<World>();
		if (data != null && !data.isEmpty()) {
			for (byte[] b : data) {
				// byte数组转World对象
				World world = yds.WorldFromArray(b);
				if (world != null) {
					retQueue.add(world);
				}
			}
		}
		return retQueue;
	}


	public static Queue<World> redisPop(int count) throws Exception {
		List<byte[]> data = redisQueue.pop(count);
		Queue<World> retQueue = new LinkedList<World>();
		if (data != null && !data.isEmpty()) {
			for (byte[] b : data) {
				// byte数组转World对象
				World world = yds.WorldFromArray(b);
				if (world != null) {
					retQueue.add(world);
				}
			}
		}
		return retQueue;
	}


	/**
	 * 获取REDIS缓存字节数
	 * @return
	 */
	public static long getRedisCachedCount() {
		if (redisQueue != null) {
			return redisQueue.getQueueBytesInMem();
		} else {
			return 0;
		}
	}

	/**
	 * 获取kafka缓存字节数
	 * @return
	 */
	public static long getRocketMQCachedCount() {
		if (onsQueue != null) {
			return onsQueue.getQueueBytesInMem();
		} else {
			return 0;
		}
	}


}
