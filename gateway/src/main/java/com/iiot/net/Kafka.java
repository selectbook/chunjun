package com.iiot.net;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.iiot.util.ExceptionUtil;
import com.iiot.util.PropertiesUtil;
import org.apache.log4j.Logger;


public class Kafka implements Runnable {
	static Logger logger = Logger.getLogger(Kafka.class);
	// static DONS st_ons_pos = null;
	// 运行标识
	boolean isRunning = true;
	// 信号量，由于控制线程暂停时间
	Semaphore semp = new Semaphore(1);
	// 每次从queue中取出data的个数
	int count = 100;

	KafkaPut kafkaPut;

	List<Thread> threadList = new LinkedList<Thread>();

	// 开始方法
	public boolean start() {

		kafkaPut = new KafkaPut();
		kafkaPut.start();
		int threadCount = PropertiesUtil.getConfigAsInt("onsThreadCount");
		if (threadCount <= 0) {
			threadCount = 10;
		}
		// 开启线程
		for (int i = 0; i < threadCount; i++) {
			Thread thread = new Thread(this);
			thread.start();
			threadList.add(thread);
		}
		return true;
	}

	public void stop() {
		this.isRunning = false;
		for (Thread t : threadList) {
			t.interrupt();
		}
		kafkaPut.stop();
	}

	public boolean doRun() {
		kafkaPut.send();
		return true;
	}

	public void run() {
		while (isRunning) {
			// 正常情况下。doRun()返回的是true，就是出于忙碌状态，就休息短点时间
			int sleepTime = 0;
			// 当dorun()返回false的话，就是出现特殊情况，休息时间久一点
			if (!doRun()) {
				sleepTime = 2000;
			}
			if (sleepTime > 0) {
				try {
					if (semp.tryAcquire(sleepTime, TimeUnit.MILLISECONDS)) {
						semp.release();
					}
				} catch (InterruptedException e) {
					logger.error("semp.tryAcquire(xx, yy) fail: " + ExceptionUtil.getStackStr(e));
				}
			}
		}
	}
}
