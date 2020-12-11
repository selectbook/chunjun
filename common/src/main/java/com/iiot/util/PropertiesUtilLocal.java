package com.iiot.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * 
* @ClassName: PropertiesUtil
* @Description: 带缓存的配置读取工具 ,这是从本地配置文件中获取配置信息
*
 */
public class PropertiesUtilLocal {
	static Map<String, Object> configMap = new HashMap<String, Object>();
	static Logger logger = Logger.getLogger(PropertiesUtilLocal.class);

	public static int getConfigAsInt(String key) {
		String str = getConfig(key);
		int ret = 0;
		if (str != null && !"".equals(str)) {
			try {
				ret = Integer.parseInt(str);
			} catch (NumberFormatException e) {
				return 0;
			}
			return ret;
		}
		return 0;
	}

	public static Long getConfigAsLong(String key) {
		String str = getConfig(key);
		Long ret = null;
		if (str != null && !"".equals(str)) {
			try {
				ret = Long.parseLong(str);
			} catch (NumberFormatException e) {
				return null;
			}
			return ret;
		}
		return null;
	}

	public static String getConfig(String key) {
		synchronized (configMap) {
			String value = null;
			// 得到数据
			Object obj = configMap.get(key);
			if (obj == null) {
				// 得不到，从文件读
				value = getConfigInternal(key);
				// 写缓存，即使数据是NULL
				configMap.put(key, value);

			} else {
				value = (String) obj;
			}
			return value;
		}
	}

	private static String getConfigInternal(String key) {
		Properties property = new Properties();
		String path = new PropertiesUtilLocal().getClass().getClassLoader().getResource("conf/config.properties").getPath();
		InputStream inputStream = null;
		InputStreamReader reader = null;
		try {
			inputStream = new FileInputStream(path);
			reader = new InputStreamReader(inputStream, "utf-8");
		} catch (Exception e) {
			inputStream = PropertiesUtilLocal.class.getClassLoader().getResourceAsStream("conf/config.properties");
			if (inputStream != null) {
				try {
					reader = new InputStreamReader(inputStream, "utf-8");
				} catch (UnsupportedEncodingException e1) {
					logger.error("new InputStreamReader(inputStream,'utf-8') fail" + e.getLocalizedMessage());
				}
			}
			if (inputStream == null) {
				return null;
			}
		}
		try {
			property.load(reader);

			String value = property.getProperty(key);
			return value;
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());

			return null;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e1) {
				logger.error(e1.getLocalizedMessage());

			}
		}
	}


	public static void getConfigInternal() {
		String path = new PropertiesUtilLocal().getClass().getClassLoader().getResource("./conf/log4j.properties").getPath();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(path);
		} catch (Exception e) {
			inputStream = PropertiesUtilLocal.class.getClassLoader().getResourceAsStream("./conf/log4j.properties");
			if (inputStream == null) {
				return;
			}
		}
		PropertyConfigurator.configure(inputStream);
	}

	public static void main(String[] args) {

	}
}
