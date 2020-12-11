package com.iiot.cached;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;


import java.util.concurrent.atomic.*;


/**
 * 
* @ClassName: PersistentCached
* @Description: 持久化缓存
* @date 2016年6月25日 上午10:12:32
*
 */
public class PersistentCached<K, V> {

	/**
	 * 
	* <p>Title: </p>
	* <p>Description: </p>
	* @param name 用于持久化时的文件名，要用固定的，不能随机产生（如UUID）
	 */
	public PersistentCached(String name) {
		cachedName = name;
	}

	// 读写锁
	private ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();

	// 泛型MAP
	private Map<K, V> saveMap = new HashMap<K, V>();

	// 一般的KV
	private Map<String, Object> genMap = new HashMap<String, Object>();

	// 0为未初始化，1为正在读，2为完成初始化
	volatile private AtomicInteger state = new AtomicInteger(0);

	// 名字
	private String cachedName = null;

	private Logger logger = Logger.getLogger(PersistentCached.class);

	/**
	 * 
	* @Title: ReadFile
	* @Description: 读文件内容，返回String(json)
	* @param @param fileName
	* @param @return
	* @return String    返回类型 
	* @throws
	 */
	private String ReadFile(String fileName) {
		// 得到文件长度
		File file = new File(fileName);
		long fileLen = file.length();

		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e1) {
			logger.error(e1.getLocalizedMessage());
			return null;
		}
		try {
			// 读到一个vector
			// Vector<char> aa = new Vector<Byte>(1024);
			Vector<Byte> fileBuff = new Vector<Byte>(1024);
			int readLen = 0;
			char[] fileContent = new char[(int) fileLen];
			int offset = 0;
			do {
				try {
					char[] tmp = new char[1024];
					readLen = fr.read(tmp);
					if (readLen > 0) {
						// 加上去
						System.arraycopy(tmp, 0, fileContent, offset, readLen);
						offset += readLen;
					}
				} catch (IOException e) {
					logger.error(e.getLocalizedMessage());
					return null;
				}
			} while (readLen > 0);
			// 读完数据
			String jsonStr = new String(fileContent);
			return jsonStr;
		} finally {
			try {
				fr.close();
			} catch (IOException e) {
				logger.error("ReadFile close:" + e.getLocalizedMessage());
			}
		}
	}

	/**
	 * 
	* @Title: ReadFromFile
	* @Description: 从文件中读数据到内存
	* @param 
	* @return void    返回类型 
	* @throws
	 */
	private void ReadFromFile() {
		// 转为JSON，有优化空间

		// 文件
		String fileNameMap = "PERSISTENT-CACHED-MAP-" + cachedName + ".sv";
		String fileNameKV = "PERSISTENT-CACHED-KV-" + cachedName + ".sv";

		String jsonStr = ReadFile(fileNameMap);
		// 把JSON转为对象
		Map<K, V> map = (Map<K, V>) JSON.parse(jsonStr);
		jsonStr = ReadFile(fileNameKV);
		Map<String, Object> map2 = (Map<String, Object>) JSON.parse(jsonStr);
		// 替换
		rwlock.writeLock().lock();
		// 如果没有文件，就给一个空的
		try {
			if (map != null) {
				saveMap = map;
			}

			if (map2 != null) {
				genMap = map2;
			}
		} finally {
			rwlock.writeLock().unlock();
		}

	}

	/**
	 * 
	* @Title: checkInit
	* @Description: 所有的方法使用前都需要检查初始化状态
	* @param 
	* @return void    返回类型 
	* @throws
	 */
	private void checkInit() {

		// 是否已经成功
		if (state.compareAndSet(2, 2)) {
			// OK，过
		} else if (state.compareAndSet(1, 1)) {
			// 正在初始化，等初始化完成
			do {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

				}
			} while (state.compareAndSet(1, 1));
		} else if (state.compareAndSet(0, 1)) {
			// 为0，未初始化，那么读文件
			ReadFromFile();
			// 读文件完成，设置为2
			state.set(2);
		} else {
			// 正常，应该是比较为2的时候值为1，而比较 为1的时候值为2了。
		}

	}

	// 把MAP写入到文件
	public void WriteMap() {
		// 写入文件
		String fileNameMap = "PERSISTENT-CACHED-MAP-" + cachedName + ".sv";

		String jsonStr = null;
		rwlock.readLock().lock();
		try {
			jsonStr = JSON.toJSONString(saveMap);
		} finally {
			rwlock.readLock().unlock();
		}

		try {
			FileWriter fw = new FileWriter(fileNameMap);
			fw.write(jsonStr);
			fw.close();
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			return;
		}
	}

	// 把KEY-VALUE写入到文件
	public void WriteKV() {
		// 写入文件
		String fileNameKV = "PERSISTENT-CACHED-KV-" + cachedName + ".sv";

		String jsonStr = null;
		rwlock.readLock().lock();
		try {
			jsonStr = JSON.toJSONString(genMap);
		} finally {
			rwlock.readLock().unlock();
		}

		try {
			FileWriter fw = new FileWriter(fileNameKV);
			fw.write(jsonStr);
			fw.close();
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			return;
		}
	}

	/**
	 * 
	* @Title: SetMap
	* @Description: 替换整个MAP
	* @param @param map
	* @return void    返回类型 
	* @throws
	 */
	public void SetMap(Map<K, V> map) {
		checkInit();

		rwlock.writeLock().lock();
		try {
			saveMap = map;
		} finally {
			rwlock.writeLock().unlock();
		}
		WriteMap();
	}

	/**
	 * 
	* @Title: AppendMap
	* @Description: 附加内容
	* @param @param map
	* @return void    返回类型 
	* @throws
	 */
	public void AppendMap(Map<K, V> map) {
		checkInit();

		rwlock.writeLock().lock();
		try {
			saveMap.putAll(map);
		} finally {
			rwlock.writeLock().unlock();
		}
		WriteMap();
	}

	/**
	 * 
	* @Title: AppendKV
	* @Description: 增加一个KEY,VALUE
	* @param @param k
	* @param @param v
	* @return void    返回类型 
	* @throws
	 */
	public void AppendMapKV(K key, V value) {
		checkInit();

		rwlock.writeLock().lock();
		try {
			saveMap.put(key, value);
		} finally {
			rwlock.writeLock().unlock();
		}
		WriteMap();
	}

	/**
	 * 
	* @Title: GetValue
	* @Description: 获取数据
	* @param @param key
	* @param @return
	* @return V    返回类型 
	* @throws
	 */
	public V GetMapValue(K key) {
		checkInit();

		rwlock.readLock().lock();
		try {
			return saveMap.get(key);
		} finally {
			rwlock.readLock().unlock();
		}
	}

	// 简单的KV
	public void SetKV(String key, Object obj) {
		rwlock.writeLock().lock();
		try {
			genMap.put(key, obj);
		} finally {
			rwlock.writeLock().unlock();
		}
		WriteKV();
	}

	public Object GetKV(String key) {
		rwlock.readLock().lock();
		try {
			return genMap.get(key);
		} finally {
			rwlock.readLock().unlock();
		}
	}
}
