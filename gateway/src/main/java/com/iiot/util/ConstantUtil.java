package com.iiot.util;



public class ConstantUtil {
	public static final int MONGO_FLAG = PropertiesUtil.getConfigAsInt("mongoFlag"); // 数据是否写MONGO标识 0：不写 1：写
	public static final int REDIS_FLAG = PropertiesUtil.getConfigAsInt("redisFlag"); // 数据是否写REDIS标识 0：不写 1：写
	public static final int ROCKETMQ_FLAG = PropertiesUtil.getConfigAsInt("rocketMqFlag"); // 数据是否写RocketMq标识 0：不写 1：写
	public static final int FIRST_CACHED_FLAG = PropertiesUtil.getConfigAsInt("firstCachedFlag"); // 数据是否写一级缓存标识 0：不写 1：写
	public static final int MAX_MEM = PropertiesUtil.getConfigAsInt("maxMemInMB"); // 设定的最大缓存，单位MB
	public static final int SUB_MEM = PropertiesUtil.getConfigAsInt("subMemInMB"); // 缓存超过设定的最大值，往文件写多少数据，单位MB	
	public static final int MONGO_MAX_MEM = PropertiesUtil.getConfigAsInt("mongoMaxMemInMB"); // 设定的最大缓存，单位MB
	public static final int MONGO_SUB_MEM = PropertiesUtil.getConfigAsInt("mongoSubMemInMB"); // 缓存超过设定的最大值，往文件写多少数据，单位MB
	public static final int REDIS_MAX_MEM = PropertiesUtil.getConfigAsInt("redisMaxMemInMB"); // 设定的最大缓存，单位MB
	public static final int REDIS_SUB_MEM = PropertiesUtil.getConfigAsInt("redisSubMemInMB"); // 缓存超过设定的最大值，往文件写多少数据，单位MB
//	public static final int ROCKETMQ_MAX_MEM = PropertiesUtil.getConfigAsInt("rocketMqMaxMemInMB"); // 设定的最大缓存，单位MB
	public static final int ROCKETMQ_MAX_MEM = 512; // 设定的最大缓存，单位MB
//	public static final int ROCKETMQ_SUB_MEM = PropertiesUtil.getConfigAsInt("rocketMqSubMemInMB"); // 缓存超过设定的最大值，往文件写多少数据，单位MB
	public static final int ROCKETMQ_SUB_MEM = 120; // 缓存超过设定的最大值，往文件写多少数据，单位MB
	public static final int HDFS_MAX_MEM = 200; // 设定的最大缓存，单位MB
	public static final int HDFS_SUB_MEM = 20; // 缓存超过设定的最大值，往文件写多少数据，单位MB
}
