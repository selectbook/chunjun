package com.iiot.jdbc;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.List;

import com.iiot.commCommon.Devices;
import com.iiot.commCommon.Fill;
import com.iiot.source.Drum;
import com.iiot.util.ExceptionUtil;
import com.iiot.util.PropertiesUtilLocal;
import org.apache.log4j.Logger;

public class JdbcBaseInfo {

	static Logger logger = Logger.getLogger(JdbcBaseInfo.class);
	private static JdbcSelecterBaseInfo jdbcSelecter = null; // JDBC连接

	/**
	 * 开始
	 * 
	 * @throws Exception
	 * 
	 */
	public static void start() {
		// 开启
		jdbcSelecter = new JdbcSelecterBaseInfo();
		String dbUrlBase = PropertiesUtilLocal.getConfig("dbUrlBase");
		String dbUser = PropertiesUtilLocal.getConfig("dbUserBase");
		String dbPsw = PropertiesUtilLocal.getConfig("dbPswBase");
		try {
			jdbcSelecter.start(dbUrlBase, dbUser, dbPsw);
		} catch (Exception e) {
			logger.error(
					"获取基础资料 jdbcSelecter.start fail:" + e.getLocalizedMessage() + "," + ExceptionUtil.getStackStr(e));
		}
	}

	public static List<Devices> getFillDevInfo() {
		List<Devices> selectFillDevInfo = null;
		if (jdbcSelecter == null) {
			start();
		}
		if (jdbcSelecter != null) {
            selectFillDevInfo = jdbcSelecter.SelectFillDevInfo();
		}
		return selectFillDevInfo;
	}

	public static Devices getFillInfoByDevID(String devId) {
		Devices selectFillDevInfo = null;
		if (jdbcSelecter == null) {
			start();
		}
		if (jdbcSelecter != null) {
            selectFillDevInfo = jdbcSelecter.SelectFillDdevInfoByDevId(devId);
		}
		return selectFillDevInfo;
	}

	/**
	 * 插入加注数据
	 * @param fill
	 */
	public static void insertDataIntoFill(Fill fill) {
		if (jdbcSelecter == null) {
			start();
		}
		if (jdbcSelecter != null) {
			jdbcSelecter.insertFill(fill);
		}
	}

	public static void updateDataIntoFill(Fill fill) {
		if (jdbcSelecter == null) {
			start();
		}
		if (jdbcSelecter != null) {
			jdbcSelecter.updateFill(fill);
		}
	}


	/**
	 * 更新油桶数据
	 * @param remainAmount
	 * @param oilCode
	 */
	public static void updateDrum(float remainAmount, String oilCode) {
		if (jdbcSelecter == null) {
			start();
		}
		if (jdbcSelecter != null) {
			jdbcSelecter.updateDrum(remainAmount, oilCode);
		}
	}


	/**
	 * 根据设备编号获得油桶编号
	 * @param dev_code
	 * @return
	 */
	public static String selectOilCodeByDevCode(String dev_code) {
		String oilCode = null;
		if (jdbcSelecter == null) {
			start();
		}
		if (jdbcSelecter != null) {
			oilCode = jdbcSelecter.selectOilCodeByDevCode(dev_code);
		}
		return oilCode;
	}

}
