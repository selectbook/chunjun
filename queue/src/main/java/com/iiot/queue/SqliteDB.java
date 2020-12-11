package com.iiot.queue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * 
 * @ClassName: SqliteDB
 * @Description: 在SQLITE中对文件做存储
 *
 */
public class SqliteDB {
	Connection _conn;
	Logger logger = Logger.getLogger(SqliteDB.class);
	// 不同的数据库名称，不同的文件句柄。名称应该是persistCached实例的名称
	Set<String> _tableSet;
	// 文件目录
	String fileDir = "queueFile";
	// 文件名
	String _name;

	SqliteDB() {

	}

	/**
	 * @throws SQLException
	 * 
	 * @Title: Start @Description: 开始 @param @param name @param @return
	 *         设定文件 @return boolean 返回类型 @throws
	 */
	/**
	 * 
	* @Title: Start
	* @Description: 这里用一句话描述这个方法的作用
	* @param @param name
	* @param @return
	* @param @throws SQLException
	* @return boolean    返回类型 
	* @throws
	 */
	public boolean Start(String name) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			logger.error(e.getLocalizedMessage());
		}
		_name = name;
		// 设置SQLITE的多线程环境
		// 创建文件夹
		new File("./" + fileDir).mkdir();
		checkDBFile(name);
		_conn = DriverManager.getConnection("jdbc:sqlite:" + getDBName(name) + ".db");

		// 读入所有的表
		_tableSet = ReadTables();

		return true;
	}

	/**
	 * @throws SQLException 
	 * 
	* @Title: checkDBFile
	* @Description: 检查SQLITE文件是否完整
	* @param 
	* @return void    返回类型 
	* @throws
	 */
	private void checkDBFile(String name) throws SQLException {

		_conn = DriverManager.getConnection("jdbc:sqlite:" + getDBName(name) + ".db");

		_conn.close();

	}

	/**
	 * 
	* @Title: getTableSetCopy
	* @Description: 得到所有的表的副本
	* @param @return
	* @return Set<String>    返回类型 
	* @throws
	 */
	public Set<String> getTableSetCopy() {
		return _tableSet;
	}

	/**
	 * 
	 * @Title: getDBName @Description: 以 name为文件的一部分，加上一部分其它固定的名字 @param @param
	 * tag @param @return 设定文件 @return String 返回类型 @throws
	 */
	private String getDBName(String name) {
		return "./" + fileDir + "/QueueDB-" + name;
	}

	public boolean Stop() {
		_tableSet.clear();
		try {
			_conn.close();
		} catch (SQLException e) {
			logger.error(e.getLocalizedMessage());

		}
		_conn = null;
		return true;
	}

	/**
	 * @throws SQLException
	 * 
	 * @Title: Push @Description: 数据推入缓存 @param @param tag 数据的tag @param @param
	 *         obj 数据对象 @return void 返回类型 @throws
	 */
	public void Push(Map<String, List<Object>> dataMap) throws SQLException {
		// 对于每个数据
		Iterator<Map.Entry<String, List<Object>>> entries = dataMap.entrySet().iterator();
		while (entries.hasNext()) {

			Map.Entry<String, List<Object>> entry = entries.next();
			// 以tag作为表名
			String tableName = entry.getKey();
			boolean isTableExit = false;
			synchronized (_tableSet) {
				isTableExit = _tableSet.contains(tableName);
			}
			// 表不存在，则创建表
			if (!isTableExit) {
				CreateTable(tableName);
				// 往表列表中增加
				synchronized (_tableSet) {
					_tableSet.add(tableName);
				}
			}
			// 转换数据
			List<Object> objList = entry.getValue();

			// 防止在多线程中重复一个事务
			synchronized (_conn) {
				// Statement stat = _conn.createStatement();
				boolean isOK = false;
				// 开启快速写入模式
				// isOK = stat.execute("PRAGMA synchronous = ON;");
				// 开启一个事务，写得更快
				// isOK = stat.execute("BEGIN TRANSACTION");
				// 事务是否成功？
				boolean isSuccess = false;
				try {
					_conn.setAutoCommit(false);
					// 使用PREPARE方式
					PreparedStatement pstmt = _conn.prepareStatement("insert into " + tableName + "('data') values(?)");
					// 对于每个数据，插入数据

					for (Object obj : objList) {
						// 第几个参数，从1开始
						int index = 1;
						// 把对象转化为2进制
						byte[] objBytes;
						try {
							objBytes = Utils.ObjectToByte(obj);
						} catch (IOException e) {
							logger.error("ObjectToByte fail" + e.getLocalizedMessage());
							continue;
						}
						// pstmt.setBlob(index, new
						// ByteArrayInputStream(objBytes));
						pstmt.setBytes(index, objBytes);
						index++;
						pstmt.addBatch();

						// pstmt.execute();
					}

					// 执行
					pstmt.executeBatch();
					// _conn.setAutoCommit(true);

					// 执行到最后事务应该是可以提交的
					isSuccess = true;
				} catch (Exception e) {
					logger.error("Queue Push fail:" + e.getLocalizedMessage());
				} finally {
					// 是否执行到最后决定要提交还是回滚
					if (isSuccess) {
						_conn.commit();
					} else {
						_conn.rollback();
					}

				}
			}
		}

	}

	/**
	 * 
	 * @throws SQLException 
	 * @Title: Pop @Description: 从缓存中取出数据 
	 * @param @param tag 
	 * @param 
	 * @param maxCount 最多返回的数据条数 @param @return 设定文件 @return Object[]
	 *         返回类型 @throws
	 */
	public List<Object> Pop(String tag, int maxCount) throws SQLException {
		// 检查是否存在这个表
		boolean isExistTable = false;
		synchronized (_tableSet) {
			if (_tableSet.contains(tag)) {
				isExistTable = true;
			}
		}
		if (!isExistTable) {
			return null;
		}
		List<Object> objList = new LinkedList<Object>();
		// 防止在多线程中重复一个事务
		synchronized (_conn) {
			_conn.setAutoCommit(false);

			// 最大最小行号
			int rowidMax = 0, rowidMin = 0;
			Statement stat = _conn.createStatement();
			String tableName = tag;
			ResultSet rs = stat.executeQuery("select rowid,data from " + tableName + " limit " + maxCount);
			while (rs.next()) {
				// Blob blob = rs.getBlob(1);
				// byte[] data = blob.getBytes(0, (int) blob.length());
				int rowid = rs.getInt(1);
				if (rowidMax < rowid) {
					rowidMax = rowid;
				}
				if (rowidMin > rowid) {
					rowidMin = rowid;
				}
				byte[] data = rs.getBytes(2);
				// 转为Object
				Object obj;
				try {
					obj = Utils.byteToObject(data);
				} catch (Exception e) {
					logger.error("byteToObject fail:" + e.getLocalizedMessage());
					continue;
				}
				objList.add(obj);
			}
			// 查询数据后删除数据
			stat.execute("delete from " + tableName + " where rowid >= " + rowidMin + " and rowid <= " + rowidMax);
			_conn.commit();
		}
		return objList;
	}

	/**
	 * 
	 * @throws SQLException 
	 * @Title: ReadTables @Description: 读文件内所有的表名 @param @throws SQLException
	 *         设定文件 @return void 返回类型 @throws
	 */
	private Set<String> ReadTables() throws SQLException {
		Set<String> tableNameSet = new HashSet<String>();

		try {
			Statement stat = _conn.createStatement();
			ResultSet rs = stat.executeQuery("select name from sqlite_master where type='table'");
			while (rs.next()) {
				String tableName = rs.getString("name");
				tableNameSet.add(tableName);
			}
		} catch (SQLException e) {
			logger.error(e.getLocalizedMessage());

			// 关闭
			try {
				_conn.close();
			} catch (SQLException e1) {
				logger.error(e1.getLocalizedMessage());
			}
			// 删除这个文件，重新打开
			File file = new File(getDBName(_name) + ".db");
			boolean idDel = file.delete();
			if (idDel) {
				_conn = DriverManager.getConnection("jdbc:sqlite:" + getDBName(_name) + ".db");
				//再来一次
				ReadTables();
			}else{
				throw e;
			}
		}
		return tableNameSet;
	}

	/**
	 * @throws SQLException
	 * 
	 * @Title: CreateTable @Description: 表不存在时创建这个表 @param @param tableName
	 *         设定文件 @return void 返回类型 @throws
	 */
	private void CreateTable(String tableName) throws SQLException {
		Statement stat = _conn.createStatement();
		stat.executeUpdate("create table " + tableName + "(data blob)");

	}
}
