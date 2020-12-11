package com.iiot.cached;

/**
 * 
* @ClassName: MongoCached
* @Description: 用于mongodb第一缓存，缓存车辆在一段时间内的数据，减少Mongodb的磁盘（由于数据移动带来的）写入
* 				减少缓存放在内存带来的内存紧张的问题
*               大致思路：1，把数据放在磁盘中，而内存只存储索引；一般在写的过程中不需要读文件
*                        2，把一个临时文件（可能达到几十个g）按2k（可配置）划分小块
*                        3，一个终端可以有多个小块
*                        4，每个小块有头和偏移信息（内存）
*                        5，最后拿出时按内存去遍历
* @date 2017年6月26日 下午2:40:47
*
 */
public class MongoCached {
	
}
