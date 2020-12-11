package com.iiot.queue;

import java.io.File;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.iiot.file.FileDB;
import com.iiot.util.ExceptionUtil;
import org.apache.log4j.Logger;

/**
 * 
 * @ClassName: DataQueue
 * @Description: 持久化队列
 *
 */

public class DataQueue {
	// 存储数据的MAP
	TagData _tagData = null;
	// 数据总量
	long _allTagLength = 0;
	// 初始化参数
	QueueParam _param = null;
	// // SQLITE存储器
	// SqliteDB sqliteDB = null;
	// 文件是否有数据？ 取文件数据后取不出来置false，往文件写数据置为true
	// 首次认为有数据
	// 已经不能用了，因为内部实现的原因，不到60秒是不会读的，即使有数据
	// 而且之前的实现可能有bug，某一个文件不能读就标识了所有的文件没有数据，这不合理，所以先去掉这个判断
	// 在没有数据的时候每次都读一下文件会有一些IO操作，但应该问题不大
	// boolean isFileNotEmpty = true;
	// ThreadLocal<Map<String, TagData>> threadLocal = new
	// ThreadLocal<Map<String, TagData>>();
	FileDB _fileDB = null;

	Set<String> _fileSet = new HashSet<String>();

	// //运行状态
	// boolean isRunning = true;
	// // 信息量
	// Semaphore semp = new Semaphore(1);
	Logger logger = Logger.getLogger(DataQueue.class);

	public DataQueue() {
		// 生成
		_tagData = new TagData();
	}

	// 初始化
	public void Start(QueueParam param) throws Exception {
		// 校验参数
		if (param.getName() == null || param.getName().equals("")) {
			throw new Exception("参数错");
		}
		// // 名字首字母
		// if (!Character.isLetter(param.getName().charAt(0))) {
		// throw new Exception("参数错");
		// }
		// 保存参数
		this._param = param;
		_fileDB = new FileDB(param.name);

		// 对文件操作初始化
		// sqliteDB = new SqliteDB();
		// if (!sqliteDB.Start(param.getName())) {
		// throw new Exception("存储文件初始化失败");
		// }
		// 把文件中的表名都放到KEY中，但不读入数据
		// InitTagsFromDB();

	}

	/**
	 * 
	 * @Title: getQueueSize @Description: 获取某一个tag的队列长度 @param @param
	 *         name @param @return @return int 返回类型 @throws
	 */
	public int getQueueSize() {
		synchronized (_tagData) {
			if (_tagData == null) {
				return 0;
			}
			return _tagData.getQueueData().size();
		}
	}

	/**
	 * 
	 * @throws SQLException
	 * @Title: SaveData
	 * @Description: 保存数据，中间会有Queue到object的转化
	 * @param @param
	 *            map
	 * @return void 返回类型 @throws
	 */
	private void SaveData(TagData tagData, long readBytes) throws SQLException {
		// 增加，如果不存在
		List<byte[]> objList = new LinkedList<byte[]>();
		// objMap.putIfAbsent(key, objList);
		// 对于每个VALUE都需要转化
		Queue<QData> queue = tagData.getQueueData();
		Iterator<QData> queueIter = queue.iterator();
		while (queueIter.hasNext()) {
			QData data = queueIter.next();
			byte[] bData = data.getData();
			objList.add(bData);
		}
		if (readBytes > 0) {
			try {
				_fileDB.write(objList, readBytes);
			} catch (Exception e) {
				logger.error(
						"fileDB.write(objList, allTagLength,allTagLength:)" + _allTagLength + e.getLocalizedMessage());
			}
		}
		// // 文件认为是非空
		// synchronized (tagData) {
		// isFileNotEmpty = true;
		// }

	}

	public void Stop() throws Exception {
		// 设置为退出，给出信号
		// isRunning = false;
		// semp.release();
		
		synchronized(_tagData){
			// 保存数据
			SaveData(_tagData, _allTagLength);
		}
		
		// sqliteDB.Stop();
		// sqliteDB = null;
	}

	/**
	 * 
	 * @Title: Push @Description: 数据先放到本线程中 @param @param tag @param @param
	 *         data @param @return @param @throws Exception @return int
	 *         返回类型 @throws
	 */
	public int Push(QData data) throws Exception {
		return PushInternel(data);
		// 大批量地写好像是会更慢，好像是在List.addall上做了内存复制
		// Map<String, TagData> localMap = threadLocal.get();
		// if (localMap == null) {
		// localMap = new HashMap<String, TagData>();
		// threadLocal.set(localMap);
		// }
		//
		// // 校验参数
		// if (tag == null || data == null) {
		// throw new Exception("tag == null || data == null");
		// }
		// // 检查本线程的数据量
		// TagData localTagData = localMap.get(tag);
		// if (localTagData == null) {
		// localMap.put(tag, new TagData());
		// return -1;
		// }
		// Queue<QData> localQueue = localTagData.getQueueData();
		// //TODO 如果时间过长，那么也需要写缓存
		// //TODO 在程序退出时，需要从线程缓存中得到数据并写入
		// if (localQueue.size() > 10000) {
		// // 批量写到真正的缓存中
		// PushBulkInternal(tag,localTagData);
		// }else{
		// localTagData.AddData(data);
		// }
		//
		// return -1;
	}

	private int PushBulkInternal(TagData newTagData) throws Exception {

		boolean isWrite = false;

		int queueSize = 0;
		synchronized (_tagData) {
			// 没有这个tag，则增加
			if (_tagData == null) {
				_tagData = new TagData();
				_tagData.AddDataQueue(newTagData);
				// 增加总量
				_allTagLength += newTagData.getTagLen();
				// 放入MAP
				// dataMap.put(tag, tagData);

				// 只有一条
				queueSize = 1;
				// 数据不会满的，直接返回就行了
				return queueSize;
			}
			// 放入内存
			_tagData.AddDataQueue(newTagData);
			// 增加总量
			_allTagLength += newTagData.getTagLen();
			// 内存数据会不会太高
			if (_allTagLength > _param.highLevel) {
				isWrite = true;
			}
			queueSize = _tagData.getQueueData().size();
		}

		// 把数据放入内存
		if (isWrite) {
			WriteToFile();
		}
		return queueSize;
	}

	/**
	 * 
	 * @Title: Push @Description: 放入数据，返回插入数据后队列的长度 @param @param
	 *         tag @param @param data @param @return @param @throws
	 *         Exception @return int 返回类型 不一定返回queue中的长度了 @throws
	 */
	private int PushInternel(QData data) throws Exception {

		boolean isWrite = false;

		int queueSize = 0;
		synchronized (_tagData) {
			// TagData tagData = dataMap.get(tag);
			// 没有这个tag，则增加
			if (_tagData == null) {
				_tagData = new TagData();
				_tagData.AddData(data);
				// 增加总量
				_allTagLength += data.getData().length;
				// 放入MAP
				// dataMap.put(tag, tagData);

				// 只有一条
				queueSize = 1;
				// 数据不会满的，直接返回就行了
				return queueSize;
			}
			// 放入内存
			_tagData.AddData(data);
			// 增加总量
			_allTagLength += data.getData().length;
			// 内存数据会不会太高
			if (_allTagLength > _param.highLevel) {
				isWrite = true;
			}
			queueSize = _tagData.getQueueData().size();
		}

		// 把数据放入内存
		if (isWrite) {
			WriteToFile();
		}
		return queueSize;
	}

	// 放入数据
	public Queue<QData> Pop(int count) throws Exception {
		// TODO 当某个tag已经很长时间没有数据，那么删除这个tag
		// 看数据量够不够，不够就尝试从文件中读取
		boolean isNeedReadFile = false;
		synchronized (_tagData) {
			// TagData tagData = dataMap.get(tag);
			if (_tagData == null) {
				return null;
			}
			if (_tagData.getQueueData().size() < count) {
				isNeedReadFile = true;
			}
		}

		// 检查是否需要读文件，若是则读
		if (isNeedReadFile) {
			CheckFile(count);
		}

		Queue<QData> queueReturn = null;
		synchronized (_tagData) {
			// 没有任何数据
			if (_allTagLength <= 0) {
				return null;
			}
			// TagData tagData = dataMap.get(tag);
			Queue<QData> queue = _tagData.getQueueData();
			if (queue == null) {
				return queueReturn;
			}
			// 如果外部给的缓存不够大，那么设置数据大小，外部再重新分配更大的内存获取一次
			if (queue.size() <= count) {
				// 直接给引用
				queueReturn = queue;
				// 设置新的对象
				_tagData.setQueueData(new LinkedList<QData>());

				// 减少总量
				_allTagLength -= _tagData.getTagLen();
				// 减少内存量
				_tagData.setTagLen(0);

			} else {
				queueReturn = new LinkedList<QData>();
				for (int i = 0; i < count; i++) {
					QData qd = queue.poll();
					queueReturn.add(qd);
					// 减少总量
					_allTagLength -= qd.getData().length;
					// 减少内存量
					_tagData.tagLen -= qd.getData().length;
				}
			}

			// // 校验内部数据量
			// if (!CheckDataLen()) {
			// throw new Exception("CheckDataLen fail");
			// }

		}
		return queueReturn;
	}

	/**
	 * 
	 * @Title: CheckDataLen @Description: 检查数据总量 @param @return @return boolean
	 *         返回类型 @throws
	 */
	boolean CheckDataLen() {
		// synchronized (dataMap) {
		// // 总量
		// long allBytes = 0;
		// Iterator<Map.Entry<String, TagData>> iter =
		// dataMap.entrySet().iterator();
		// while (iter.hasNext()) {
		// Map.Entry<String, TagData> entry = iter.next();
		// String key = entry.getKey();
		// TagData tagData = entry.getValue();
		// Iterator<QData> qdIter = tagData.getQueueData().iterator();
		// // 这个tag的量
		// long tagBytes = 0;
		// while (qdIter.hasNext()) {
		// QData qd = qdIter.next();
		// allBytes += qd.getData().length;
		// tagBytes += qd.getData().length;
		// }
		// if (tagBytes != tagData.getTagLen()) {
		// return false;
		// }
		// }
		// if (allBytes != allTagLength) {
		// return false;
		// }
		// }
		return true;
	}

	// 是否写内存？

	// 检查是否需要读文件，若是则读
	private void CheckFile(int maxCount) {
		// synchronized (tagData) {
		// // 文件是否为空？
		// if (!isFileNotEmpty) {
		// return;
		// }
		// }
		// 读文件
		List<byte[]> objList = null;
		try {
			String file = "./" + _param.getName() + "/";
			File f = new File(file);
			String[] fileNames = f.list();
			// 如果没有文件就return；
			if (fileNames == null || fileNames.length == 0) {
				return;
			}
			// 找到一个未使用的
			String fileNameSelected = null;
			synchronized (_fileSet) {
				for (String fileName : fileNames) {
					if (_fileSet.contains(fileName)) {
						continue;
					} else {
						File timeFile = new File(fileName);
						if (System.currentTimeMillis() - timeFile.lastModified() > 10000) {
							fileNameSelected = fileName;
							_fileSet.add(fileNameSelected);
							break;
						} else {
							continue;
						}
					}
				}
			}
			if (fileNameSelected != null) {
				try {
					file = file + fileNameSelected;
					objList = _fileDB.read(maxCount, file);
				} finally {
					//保存可以remove
					synchronized (_fileSet) {
						_fileSet.remove(fileNameSelected);
					}
				}
			}
		} catch (Exception e) {
			logger.error("fileDB.read:" + ExceptionUtil.getStackStr(e));
			return;
		}
		// 没有读到数据，返回
		if (objList == null) {
			return;
		}
		synchronized (_tagData) {
			// // 是否有数据
			// if (objList == null) {
			// isFileNotEmpty = false;
			// return;
			// }
			// 合并数据
			// TagData tagData = dataMap.get(tag);
			if (_tagData == null) {
				logger.error("CheckFile tagData == null");
				return;
			}
			Queue<QData> queueList = _tagData.getQueueData();
			for (byte[] obj : objList) {
				QData qd = new QData(obj);
				queueList.add(qd);
				// 修正数据量
				_tagData.tagLen += qd.data.length;
				_allTagLength += qd.data.length;
			}
		}
		if (!CheckDataLen()) {
			// logger.error("CheckDataLen fail");
		}
	}

	// 把内存的数据写入文件
	private void WriteToFile() {
		// 为每个TAG至少保留max{10条数据,lowLevel/tag数}

		// 要写文件的数据
		TagData writeData = new TagData();
		// 已读取的总量
		long readBytes = 0;
		synchronized (_tagData) {
			// 要写的数据量，希望把内存量降到高和低水平的一半，这样比较合理
			long writeBytes = _allTagLength - (_param.highLevel - _param.lowLevel) / 2;

			// 不用写？
			if (writeBytes <= 0) {
				logger.error("writeBytes <= 0");
				return;
			}
			// 最低保留的数据量
			long lowReserve = _param.lowLevel;
			// 最低的数据条数
			long lowCount = 10;

			// Iterator<Map.Entry<String, TagData>> iter =
			// dataMap.entrySet().iterator();
			// while (iter.hasNext()) {
			// Map.Entry<String, TagData> entry = iter.next();
			// String key = entry.getKey();
			// TagData tagData = entry.getValue();
			// 如果数据量不足，跳过这个tag
			if (_tagData.getTagLen() <= lowReserve || _tagData.getQueueData().size() <= lowCount) {
				return;
			}
			// 把数据移出来，移到最低水平
			// TagData newTagData = new TagData();
			// writeMap.putIfAbsent(key, newTagData);
			while (_tagData.getTagLen() > lowReserve && _tagData.getQueueData().size() > lowCount) {
				// 取出
				QData qd = _tagData.getQueueData().poll();
				// 修正数据量
				_tagData.tagLen -= qd.data.length;
				// 修正数据总量
				_allTagLength -= qd.data.length;
				// 放入新的队列
				writeData.getQueueData().add(qd);
				writeData.tagLen += qd.data.length;
				// 调整已读取的总量
				readBytes += qd.data.length;

				// 总量已够？
				if (readBytes >= writeBytes) {
					break;
				}
			}
		}
		// 保存到文件
		try {
			SaveData(writeData, readBytes);
		} catch (SQLException e) {
			logger.error("SaveData(writeMap):" + e.getLocalizedMessage());
			// TODO 把数据写回内存？
			return;
		}
	}

	// 是否是合法的TAG
	private boolean IsValidTag(String tag) {
		if (tag == null) {
			return false;
		}
		// 首字为字母
		return Character.isAlphabetic(tag.charAt(0));
	}

	// 得到所有的TAG
	public Set<String> QueryAllTag() {
		//
		// synchronized (dataMap) {
		// return dataMap.keySet();
		// }
		// TODO
		return null;
	}

	// 把文件中的实例和表都放到缓存，但暂时不读入数据
	// private void InitTagsFromDB() {
	// Set<String> tableSet = sqliteDB.getTableSetCopy();
	// // 插入到MAP中
	// synchronized (dataMap) {
	// for (String tag : tableSet) {
	// dataMap.put(tag, new TagData());
	// }
	// }
	// }
}
