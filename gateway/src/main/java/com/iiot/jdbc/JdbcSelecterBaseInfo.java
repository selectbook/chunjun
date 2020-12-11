package com.iiot.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iiot.commCommon.Devices;
import com.iiot.commCommon.Fill;
import com.iiot.source.Drum;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;


public class JdbcSelecterBaseInfo {

	Logger logger = Logger.getLogger(JdbcSelecterBaseInfo.class);

	// 获取dbpool
	DBPool dbpool = new DBPool();
	// 数据库连接
	Connection connection = null;

	public void start(String dbUrlBase, String dbUser, String dbPsw) throws Exception {
		// 开启dbpool
		dbpool.start(dbUrlBase, dbUser, dbPsw);
	}

	public void stop() {
		// 释放数据库的dbpool
		if (connection != null) {
			try {
				dbpool.freeConn(connection);
			} catch (SQLException e) {
				logger.error("dbpool.freeConn(connection) fail:" + e.getLocalizedMessage());
			}
		}
	}

	/**
	 * 查询加注机的基础资料
	 */
	public List<Devices> SelectFillDevInfo() {
		// 装返回结果
		List<Devices> list = new ArrayList<Devices>();
		try {
			// 查询配置表中所有参数
//			String sql = "select id, dev_code as devId, setting_amount as settingAmount, " +
//					"current_amount as currentAmount, total_amount as totalAmount, tank_capacity as tankCapacity, " +
//					"remain_amount as remainAmount, dev_status as devStatus, ipaddr as ip from xt_oil_fill";

			String sql = "select id, dev_code, status from xt_device";

			// 开启连接
			connection = dbpool.getConn();
			PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			List<Map<String, Object>> values = handleResultSettoMapList(rs);
			list = transferMapListToBeanList(Devices.class, values);
		} catch (Exception e) {
			logger.error("SelectFillDevInfo  fail:" + e.getLocalizedMessage());
		} finally {
			// 释放数据库的dbpool
			if (connection != null) {
				try {
					dbpool.freeConn(connection);
				} catch (SQLException e) {
					logger.error("dbpool.freeConn(connection) fail:" + e.getLocalizedMessage());
				}
			}
		}
		return list;
	}


	/**
	 * 根据终端编号获得加注机基本信息
	 * 
	 * @return
	 */
	public Devices SelectFillDdevInfoByDevId(String devId) {
		// 装返回结果
		List<Devices> list = new ArrayList<Devices>();
		ResultSet rs = null;
		Devices devices = new Devices();
		try {
			// 查询配置表中所有参数
			String sql = "select id, dev_code from xt_device where dev_code = ?";
			// 开启连接
			connection = dbpool.getConn();
			PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(sql);
			pstmt.setString(1, devId);
			rs = pstmt.executeQuery();
			ResultSetMetaData md = rs.getMetaData();// 获取键名
			int columnCount = md.getColumnCount();// 获取行的数量
			while (rs.next()) {
				 int id = rs.getInt(1);
				 String dev_code = rs.getString(2);
				 devices.setId(id);
				 devices.setDev_code(dev_code);
			}
//			List<Map<String, Object>> values = handleResultSettoMapList(rs);
//			list = transferMapListToBeanList(Devices.class, values);
		} catch (Exception e) {
			logger.error("SelectFillDdevInfoByDevId  fail:" + e.getLocalizedMessage());
		} finally {
			// 释放数据库的dbpool
			if (connection != null) {
				try {
					dbpool.freeConn(connection);
				} catch (SQLException e) {
					logger.error("dbpool.freeConn(connection) fail:" + e.getLocalizedMessage());
				}
			}
		}
		return devices;
	}

	/**
	 * 根据设备编号获得油桶编号
	 *
	 * @return
	 */
	public String selectOilCodeByDevCode(String dev_code) {
		ResultSet rs = null;
		String oil_code = null;
		try {
			// 查询配置表中所有参数
			String sql = "select oil_code from xt_device where dev_code = ?";
			// 开启连接
			connection = dbpool.getConn();
			PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(sql);
			pstmt.setString(1, dev_code);

			rs = pstmt.executeQuery();
			ResultSetMetaData md = rs.getMetaData();// 获取键名
			int columnCount = md.getColumnCount();// 获取行的数量
			while (rs.next()) {
				oil_code = rs.getString(1);
			}
		} catch (Exception e) {
			logger.error("SelectOilCodeByDevCode  fail:" + e.getLocalizedMessage());
		} finally {
			// 释放数据库的dbpool
			if (connection != null) {
				try {
					dbpool.freeConn(connection);
				} catch (SQLException e) {
					logger.error("dbpool.freeConn(connection) fail:" + e.getLocalizedMessage());
				}
			}
		}
		return oil_code;
	}


	/**
	 * 插入加注数据
	 * @param fill
	 */
	public  void insertFill(Fill fill) {
		try {
			connection = dbpool.getConn();
			String sql = "INSERT INTO  xt_oil_fill (dev_code,setting_amount,current_amount,real_total_amount,total_amount,tank_capacity,remain_amount,ipaddr,local_time,create_time) VALUES(?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pstmt = connection.prepareStatement(sql);

			/**
			 * 调用实体StuInfo类,获取需要插入的各个字段的值
			 * 注意参数占位的位置
			 * 通过set方法设置参数的位置
			 * 通过get方法取参数的值
			 */
			pstmt.setString(1, fill.getDevId());
			pstmt.setFloat(2,fill.getSettingAmount());
			pstmt.setFloat(3, fill.getCurrentAmount());
			pstmt.setFloat(4, fill.getRealTotalAmount());
			pstmt.setFloat(5, fill.getTotalAmount());
			pstmt.setFloat(6, fill.getTankCapacity());
			pstmt.setFloat(7, fill.getLeftTankAmount());
			pstmt.setString(8, fill.getIp());
			pstmt.setTimestamp(9, fill.getAddTime());
			pstmt.setTimestamp(10,fill.getRegisterTime());
			int res = pstmt.executeUpdate();			//执行sql语句
			if(res>0){
				logger.info("加注数据插入成功");
			}
		} catch (SQLException e) {
			logger.error("insertFill  fail:" + e.getLocalizedMessage());
		}finally {
			// 释放数据库的dbpool
			if (connection != null) {
				try {
					dbpool.freeConn(connection);
				} catch (SQLException e) {
					logger.error("dbpool.freeConn(connection) fail:" + e.getLocalizedMessage());
				}
			}
		}
	}


	/**
	 * 更新加注机数据
	 * @param fill
	 */
	public  void updateFill(Fill fill) {
		try {
			connection = dbpool.getConn();
			String sql = "UPDATE xt_oil_fill set setting_amount=?,current_amount=?,total_amount=?,tank_capacity=?,remain_amount=?,ipaddr=?,local_time=?,create_time=? where dev_code=?";
			PreparedStatement pstmt = connection.prepareStatement(sql);

			/**
			 * 调用实体StuInfo类,获取需要插入的各个字段的值
			 * 注意参数占位的位置
			 * 通过set方法设置参数的位置
			 * 通过get方法取参数的值
			 */
			pstmt.setFloat(1,fill.getSettingAmount());
			pstmt.setFloat(2, fill.getCurrentAmount());
			pstmt.setFloat(3, fill.getTotalAmount());
			pstmt.setFloat(4, fill.getTankCapacity());
			pstmt.setFloat(5, fill.getLeftTankAmount());
			pstmt.setString(6, fill.getIp());
			pstmt.setTimestamp(7, fill.getAddTime());
			pstmt.setTimestamp(8, fill.getRegisterTime());
//			pstmt.setObject(7, fill.getDevStatus());
			pstmt.setString(9, fill.getDevId());

			int res = pstmt.executeUpdate();			//执行sql语句
			if(res>0){
				logger.info("加注数据更新成功");
			}
		} catch (SQLException e) {
			logger.error("updateFill  fail:" + e.getLocalizedMessage());
		}finally {
			// 释放数据库的dbpool
			if (connection != null) {
				try {
					dbpool.freeConn(connection);
				} catch (SQLException e) {
					logger.error("dbpool.freeConn(connection) fail:" + e.getLocalizedMessage());
				}
			}
		}
	}


	/**
	 * 更新油桶数据（目前主要是油桶余量，用于计算库存）
	 * @param remainAmount
	 * @param oilCode
	 */
	public  void updateDrum(float remainAmount, String oilCode) {
		try {
			connection = dbpool.getConn();
			String sql = "UPDATE xt_oil_drum set remain_amount=? where oil_code=? ";
			PreparedStatement pstmt = connection.prepareStatement(sql);


			pstmt.setFloat(1, remainAmount);
			pstmt.setString(2, oilCode);

			int res = pstmt.executeUpdate();			//执行sql语句
			if(res>0){
				logger.info("油桶数据更新成功");
			}
		} catch (SQLException e) {
			logger.error("updateDrum  fail:" + e.getLocalizedMessage());
		}finally {
			// 释放数据库的dbpool
			if (connection != null) {
				try {
					dbpool.freeConn(connection);
				} catch (SQLException e) {
					logger.error("dbpool.freeConn(connection) fail:" + e.getLocalizedMessage());
				}
			}
		}
	}


	/**
	 * 处理结果集，得到 Map的一个List，其中 一个 Map对象对应一条记录
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private static List<Map<String, Object>> handleResultSettoMapList(ResultSet resultSet) throws SQLException {
		// .准备一个 List<Map<String, Object>>:
		// 键：存放列的别名， 值：存放列的值。其中一个Map 对象对应着一条记录
		List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
		ResultSetMetaData rsmd = resultSet.getMetaData();
		Map<String, Object> map = null;

		// 处理 ResultSet， 使用 while 循环
		while (resultSet.next()) {
			map = new HashMap<String, Object>();
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				String columLabel = rsmd.getColumnLabel(i + 1);
				Object value = resultSet.getObject(i + 1);
				map.put(columLabel, value);
			}
			// .把一条记录的一个 Map 对象放入 准备的List中
			values.add(map);
		}
		return values;

	}

	private static <T> List<T> transferMapListToBeanList(Class<T> clazz, List<Map<String, Object>> values)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		// . 判断 List 是否为空集合，若不为空
		// 则遍历List，得到一个一个的Map对象，再把一个 Map对象转换为一个 Class参数对应的 Object对象
		List<T> result = new ArrayList<T>();
		T bean = null;
		if (values.size() > 0) {
			for (Map<String, Object> m : values) {
				bean = clazz.newInstance();
				for (Map.Entry<String, Object> entry : m.entrySet()) {
					String propertyName = entry.getKey();
					Object value = entry.getValue();
					BeanUtils.setProperty(bean, propertyName, value);
				}
				// . 把 Object 对象放入到 list 中
				result.add(bean);
			}
		}
		return result;
	}

}
