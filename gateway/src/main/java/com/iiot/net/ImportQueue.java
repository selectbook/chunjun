package com.iiot.net;

import com.iiot.queue.DataQueue;
import com.iiot.queue.QData;
import com.iiot.queue.QueueParam;
import com.iiot.util.ExceptionUtil;
import org.apache.log4j.Logger;

import java.util.Queue;


public class ImportQueue {

	static Logger logger = Logger.getLogger(ImportQueue.class);

	// 装载加注数据的Queue，用于加注数据。
	private static DataQueue posDataQueue = new DataQueue();

	/**
	 * 缓存数据
	 * @param obj
	 * @throws Exception
	 */
	public static void pushObj(byte[] obj) throws Exception {
		QData data = new QData(obj);
		posDataQueue.Push(data);
	}

	/**
	 * 00获取数据
	 * @p000000000aram count
	 * @return
	 * @throws Exception
	 */
	public static Queue<QData> posPop(int count) throws Exception {
		Queue<QData> data = posDataQueue.Pop(count);
		return data;
	}

	/**
	 * 启动
	 * @throws Exception
	 */
	public static void start() throws Exception {
		posDataQueue.Start(new QueueParam("fillDataQueue"));
	}

	/**
	 * 关闭
	 * @throws Exception
	 */
	public static void stop(){
		try {
			posDataQueue.Stop();
		} catch (Exception e) {
		    System.err.println("posDataQueue.Stop() fail:"+ ExceptionUtil.getStackStr(e));
		}
	}
}
