package com.iiot.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.iiot.queue.DataQueue;
import com.iiot.queue.QData;
import com.iiot.queue.QueueParam;
import com.iiot.algorithm.Md5;
import com.iiot.util.ChangeUtil;
import com.iiot.util.ExceptionUtil;

/**
 * 
 * @ClassName: JdbcWriter
 * @Description: 批量写入数据 功能： 1，批量写入数据，加速数据写入速度 2，允许通过配置参数来调优写入速度 3，不保存数据入库的时序性
 *               4，一般情况下每秒4K以上的速度
 *
 */
public class JdbcWriter {
	// DataQueue qd;
	Logger logger = Logger.getLogger(JdbcWriter.class);
	List<Thread> threadList = new LinkedList<Thread>();

	DBPool dbpool = new DBPool();
	volatile boolean isRunning = true;
	Map<String, DataQueue> qdMap = new HashMap<String, DataQueue>();

	String dbName;
	String dbUrl;
	String dbUser;
	String dbPsw;
	int threadCount;
	int batchCount;
	long highLevel;
	long lowLevel;
	int initialSize = 8;
	int maxActive = 10;
	int minIdle = 5;

	// 文件数据保存路径
	String savePath;

	public void start(String dbName, String dbUrl, String dbUser, String dbPsw, int threadCount, int batchCount,
			String savePath, long highLevel, long lowLevel, int initialSize, int maxActive, int minIdle)
			throws Exception {
		start("com.mysql.jdbc.Driver", dbName, dbUrl, dbUser, dbPsw, threadCount, batchCount, savePath, highLevel,
				lowLevel, initialSize, maxActive, minIdle);
	}

	public void start(String dbName, String dbUrl, String dbUser, String dbPsw, int threadCount, int batchCount,
			String savePath, long highLevel, long lowLevel) throws Exception {
		start("com.mysql.jdbc.Driver", dbName, dbUrl, dbUser, dbPsw, threadCount, batchCount, savePath, highLevel,
				lowLevel, initialSize, maxActive, minIdle);
	}
	
	

	public void startSqlServer(String dbName, String dbUrl, String dbUser, String dbPsw, int threadCount, int batchCount,
			String savePath, long highLevel, long lowLevel,int initialSize,int maxActive,int minIdle) throws Exception {
		start("com.microsoft.sqlserver.jdbc.SQLServerDriver", dbName, dbUrl, dbUser, dbPsw, threadCount, batchCount, savePath, highLevel,
				lowLevel,initialSize, maxActive,minIdle);
	}
	
	public void startSqlServer(String dbName, String dbUrl, String dbUser, String dbPsw, int threadCount, int batchCount,
			String savePath, long highLevel, long lowLevel) throws Exception {
		start("com.microsoft.sqlserver.jdbc.SQLServerDriver", dbName, dbUrl, dbUser, dbPsw, threadCount, batchCount, savePath, highLevel,
				lowLevel,initialSize, maxActive,minIdle);
	}

	/**
	 * @Title: start
	 * @Description: 初始化
	 * @param @param dbName 数据库名，如果有多个实例，那它必须是唯一的
	 * @param @param dbUrl 数据的URL，如：jdbc:mysql://rds93oo8s3xzq3cqf116.mysql.rds.aliyuncs.com:3306/ bsj?" +  "user=bsj&password=Bsj20160101asdf&useUnicode=true&characterEncoding=UTF8
	 * @param @param threadCount 写DB的线程数
	 * @param @param batchCount 每次写的数量
	 * @param savePath 数据持久化保存的目录，如C:\xx或/root/abc 不需要以\结尾
	 * @param highLevel,lowLevel 数据持久化的高低水平位
	 * @return void 返回类型
	 * @throws
	 */
	public void start(String driver, String dbName, String dbUrl, String dbUser, String dbPsw, int threadCount,
			int batchCount, String savePath, long highLevel, long lowLevel, int initialSize, int maxActive, int minIdle)
			throws Exception {
		this.dbName = dbName;
		this.dbUrl = dbUrl;
		this.dbUser = dbUser;
		this.dbPsw = dbPsw;
		this.threadCount = threadCount;
		this.batchCount = batchCount;
		this.highLevel = highLevel;
		this.lowLevel = lowLevel;
		this.initialSize = initialSize;
		this.maxActive = maxActive;
		this.minIdle = minIdle;
		// 保证以/结束
		if (!savePath.endsWith("/") && !savePath.endsWith("\\")) {
			savePath += "/";
		}
		this.savePath = savePath;

//		qd = new DataQueue();
//		try {
//			qd.Start(new QueueParam("./jdbc" + dbName));
//		} catch (Exception e) {
//			logger.error("JdbcWriter Start:" + ExceptionUtil.getStackStr(e));
//			throw e;
//		}
		dbpool.start(driver, dbUrl, dbUser, dbPsw, initialSize, maxActive, minIdle);
		for (int i = 0; i < threadCount; i++) {
			Thread t = new Thread(new ThreadWrite(batchCount));
			t.start();
			threadList.add(t);
		}

	}

	/** 
	 * @Title: stop 
	 * @Description: 关闭程序时停止 
	 * @param 
	 * @return void 返回类型 
	 * @throws
	 */
	public void stop() {
		isRunning = false;
		for (Thread t : threadList) {
			t.interrupt();
		}

		synchronized (qdMap) {
			Iterator<Entry<String, DataQueue>> iter = qdMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, DataQueue> entry = iter.next();
				try {
					entry.getValue().Stop();
				} catch (Exception e) {
					logger.error(ExceptionUtil.getStackStr(e));
				}
			}
		}
	}

	/**
	 * @Title: pushWrite 
	 * @Description: 写入一条数据 
	 * @param @param sql 写数据的SQL，如：insert into test1(a,b,c) values(?,?,?)，用?号代替实际的参数 
	 * @param @param params 多个参数，按?的顺序代替 
	 * @return void 返回类型 
	 * @throws
	 */
	public void pushWrite(String sql, Object... params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("s", sql);
		map.put("p", params);
		// QData d = new QData(JSON.toJSONString(map).getBytes());
		QData d = new QData(ChangeUtil.ObjectToByte(map));

		DataQueue dq = null;
		synchronized (qdMap) {
			dq = qdMap.get(sql);
			if (dq == null) {
				// 新启动一个Q
				dq = new DataQueue();
				// 目录由根目录+SQL的MD5

				String path = savePath + Md5.MD5(sql.getBytes());
				QueueParam qp = new QueueParam(path);
				qp.setHighLevel(highLevel);
				qp.setLowLevel(lowLevel);
				dq.Start(qp);
				qdMap.put(sql, dq);
			}
		}

		if (dq != null) {
			try {
				dq.Push(d);
			} catch (Exception e) {
				logger.error("pushWrite: " + ExceptionUtil.getStackStr(e));
				throw e;
			}
		}
	}

	class ThreadWrite implements Runnable {
		int batchCount;

		public ThreadWrite(int batchCount) {
			this.batchCount = batchCount;
		}

		private boolean doRun() {
			Queue<QData> qList = null;
			// 复制出所有的dataQ
			Collection<DataQueue> qdList = new LinkedList<DataQueue>();
			synchronized (qdMap) {
				qdList.addAll(qdMap.values());
			}

			long readCount = 0;
			Iterator<DataQueue> iter = qdList.iterator();
			while (iter.hasNext()) {
				try {
					qList = iter.next().Pop(batchCount);
					if (qList == null) {
						continue;
					}
					readCount += qList.size();
				} catch (Exception e) {
					logger.error("Pop: " + ExceptionUtil.getStackStr(e));
					continue;
				}
				if (!doWrite(qList)) {
					// TODO
					return false;
				}
			}
			if (readCount <= 0) {
				return false;
			}

			return true;
		}

		public boolean doWrite(Queue<QData> qList) {
			if (qList == null || qList.isEmpty()) {
				return false;
			}
			Connection conn = null;
			try {
				conn = dbpool.getConn();
			} catch (SQLException e) {
				logger.error(e.getLocalizedMessage());
				return false;
			}
			logger.debug("jdbcw:" + qList.size());
			try {
				// 先把第一条读出来
				QData data = qList.peek();
				// HashMap<String, Object> map = JSON.parseObject(new
				// String(data.getData()),
				// new HashMap<String, Object>().getClass());
				HashMap<String, Object> map = (HashMap<String, Object>) ChangeUtil.byteToObject(data.getData());
				Object objS = map.get("s");
				if (objS == null || !(objS instanceof String)) {
					logger.error("objS error:" + new String(data.getData()));
					return false;
				}
				String sql = (String) objS;
//				Object objP = map.get("p");
//				if (objP == null || !(objP instanceof List)) {
//					logger.error("objP error:" + new String(data.getData()));
//					return false;
//				}
//				Object params[] = (Object[]) objP;
//				JSONArray jsonArray = (JSONArray) objP;

				PreparedStatement p = null;
				try {
					p = conn.prepareStatement(sql);
				} catch (SQLException e) {
					logger.error("sql不对：" + sql + "," + ExceptionUtil.getStackStr(e));
					return false;
				}
				
				for (QData d : qList) {
					// HashMap<String, Object> map2 = JSON.parseObject(new
					// String(d.getData()), new HashMap<String,
					// Object>().getClass());
					HashMap<String, Object> map2 = (HashMap<String, Object>) ChangeUtil.byteToObject(d.getData());
					Object objP2 = map2.get("p");
					if (objP2 == null || !(objP2 instanceof Object[])) {
						logger.error("objP error: " + new String(data.getData()));
						return false;
					}
					Object params2[] = (Object[]) objP2;
					int index = 1;
					for (Object o : params2) {
						try {
							p.setObject(index++, o);
						} catch (SQLException e) {
							logger.error("参数不对：" + sql + "," + new String(d.getData()) + ExceptionUtil.getStackStr(e));
							break;
						}
					}
					try {
						p.addBatch();
					} catch (SQLException e) {
						logger.error("addBatch fail: " + sql + "," + ExceptionUtil.getStackStr(e));
						continue;
					}
				}
				try {
					p.executeBatch();
				} catch (SQLException e) {
					logger.error("executeBatch fail: " + sql + "," + ExceptionUtil.getStackStr(e));
					return false;
				}
			} finally {
				try {
					dbpool.freeConn(conn);
				} catch (SQLException e) {
					logger.error("free fail: " + ExceptionUtil.getStackStr(e));
					return false;
				}
			}
			return true;
		}

		public void run() {
			while (isRunning) {
				int waitTime = 0;
				try {
					if (!doRun()) {
						waitTime = 100;
					}
				} catch (Exception e) {
					logger.error("writedb fail: " + ExceptionUtil.getStackStr(e));
					waitTime = 100;
				}
				if (waitTime > 0) {
					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e) {
						logger.error(ExceptionUtil.getStackStr(e));
						return;
					}
				}
			}
		}
	}
}
