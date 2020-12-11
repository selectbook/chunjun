package com.iiot.jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.iiot.commCommon.Devices;
import com.iiot.commCommon.Fill;
import com.iiot.util.ExceptionUtil;
import com.iiot.util.PropertiesUtilLocal;
import org.apache.log4j.Logger;


public class BaseInfoCached extends Thread {

	static Logger logger = Logger.getLogger(BaseInfoCached.class);

	// key为终端编号
	public static Map<String, Devices> terNoInfoMap = new HashMap<String, Devices>();
	// 所有加注机基本信息
	public static List<Devices> CachedInfoList = new ArrayList<>();
	public boolean isRunning = true;
	public static final String path = PropertiesUtilLocal.getConfig("IIOTCloudFile");
	private static ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock(); // 读写锁

	@SuppressWarnings("unchecked")
	public void doStart() {
		// 先判断缓存文件存不存在，如果存在，就先将文件读出来赋值给缓存，再执行run()方法
//		File file = new File(path);
		// 判断文件夹是否存在,如果不存在则创建文件夹
		ObjectInputStream ois = null;
//		if (file.exists()) {
//			try {
//				FileInputStream fis = new FileInputStream(path);
//				ois = new ObjectInputStream(fis);
//				Object obj = ois.readObject();
//				if (obj instanceof Map) {
//					terNoInfoMap = (Map<String, Fill>) obj;
//				}
//				Object obj1 = ois.readObject();
//				if (obj1 instanceof List) {
//					CachedInfoList = (List<Fill>)obj1;
//				}
//
//			} catch (Exception e) {
//				logger.error("get CachedMap fail: " + ExceptionUtil.getStackStr(e));
//			} finally {
//				if (ois != null) {
//					try {
//						ois.close();
//					} catch (IOException e) {
//						logger.error(ExceptionUtil.getStackStr(e));
//					}
//				}
//			}
//		}
		this.start();
		try {
			JdbcBaseInfo.start();
			 doRun();
		} catch (Exception e) {
			logger.error("BaseInfoCached doRun fail: " + ExceptionUtil.getStackStr(e));
		}

	}

	public void Stop() {
		this.interrupt();
		isRunning = false;
	}

	private static void doRun() throws Exception {
		// key为终端编号
		Map<String, Devices> devTerMap = new HashMap<String, Devices>();
		// 查询数据库，获取加注机基础信息
		List<Devices> devicesList = JdbcBaseInfo.getFillDevInfo();
		if (devicesList != null && !devicesList.isEmpty()) {
			for (Devices deviceInfo : devicesList) {
				// 终端编号
				String devId = deviceInfo.getDev_code();
				if (devId != null) {
					devTerMap.put(devId, deviceInfo);
				}


			}
		} else {
			return;
		}

		if (devTerMap.isEmpty()) {
			logger.warn("JDBC查不出加注机基础数据");
			return;
		}

		logger.warn("JDBC查出的加注机总数:" + devTerMap.size());

//		FileOutputStream fos = new FileOutputStream(path);
//		ObjectOutputStream ois = new ObjectOutputStream(fos);
//		try {
//			ois.writeObject(devTerMap);
//		} finally {
//			ois.close();
//		}

		rwlock.writeLock().lock();
		try {
			terNoInfoMap = devTerMap;
			CachedInfoList = devicesList;
		} finally {
			rwlock.writeLock().unlock();
		}
	}

	public void run() {
		logger.info("线程-终端资料更新-开启: " + Thread.currentThread().getName() + ", " + Thread.currentThread().getId());
		// 默认1小时一次更新一次基础数据
		int secs = 60 * 60;
		while (isRunning) {
			int thisSecs = secs;
			try {
				doRun();
			} catch (Exception e) {
				logger.error("终端资料更新失败: " + ExceptionUtil.getStackStr(e));
				// 下次将在1分钟后更新
				thisSecs = 60;
			}
			try {
				Thread.sleep(1000 * thisSecs);
			} catch (InterruptedException e) {
				logger.error(ExceptionUtil.getStackStr(e));
				return;
			}
		}
	}
	// 获得所有加注机基本信息
	public static List<Devices> getFillDevInfo() {
		rwlock.readLock().lock();
		try {
			return CachedInfoList;
		} finally {
			rwlock.readLock().unlock();
		}
	}

	// 根据终端编号获取到加注机的基础信息
	public static Devices getFillDevInfo(String devId) {
		rwlock.readLock().lock();
		try {
			return terNoInfoMap.get(devId);
		} finally {
			rwlock.readLock().unlock();
		}
	}


	// 普通修改加注机信息，添加新的终端时，调用方法
//	public static void modifyFillDevInfo(Fill fill) {
//		rwlock.writeLock().lock();
//		try {
//			terNoInfoMap.put(fill.getDevId(), fill);
//		} finally {
//			rwlock.writeLock().unlock();
//		}
//	}

	// 删除时调用方法
//	public static void delFillDevInfo(String devId) {
//		rwlock.writeLock().lock();
//		try {
//			terNoInfoMap.remove(devId);
//		} finally {
//			rwlock.writeLock().unlock();
//		}
//	}


	// 获取所有加注机信息,key为终端编号
	public static Map<String, Devices> getTerNoMapFillinfo() {
		if (terNoInfoMap == null || terNoInfoMap.isEmpty()) {
			try {
				doRun();
			} catch (Exception e) {
				logger.error("doRun() fail:" + ExceptionUtil.getStackStr(e));
			}
		}
		return terNoInfoMap;
	}


}
