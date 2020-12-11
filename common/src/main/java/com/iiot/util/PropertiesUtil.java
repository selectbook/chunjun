package com.iiot.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iiot.common.bytes.HexStr;
import com.iiot.common.http.HttpPersistent;

/**
 * 
* @ClassName: PropertiesUtil
* @Description: 带缓存的配置读取工具 ,这是从mysql配置服务中获取配置信息
*
 */
public class PropertiesUtil {
	static Map<String, Object> configMap = new HashMap<String, Object>();
	static Logger logger = Logger.getLogger(PropertiesUtil.class);

	static HttpPersistent h = new HttpPersistent();
	static {
		h.start(1, 1);
	}
	

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
			if (configMap.isEmpty()) {
				try {
					//把取数据放在锁中，其它获取配置的线程全部阻塞
					getConfigFromMysql();
				} catch (Exception e) {
					logger.error("getConfigFromMysql:"+e.getLocalizedMessage());
					return null;
				}
			}
			String value = null;
			// 得到数据
			Object obj = configMap.get(key);
			if(obj instanceof String){
				return (String)obj;
			}
			
			return null;
		}
	}


	public static void getConfigFromMysql() throws Exception {

		// 从配置文件获取token
		String token = PropertiesUtilLocal.getConfig("token");
		// 从配置文件获取访问IP
		String serviceIP = PropertiesUtilLocal.getConfig("serviceIP");
		
		if (token==null || serviceIP==null) {
			logger.error("token 或 serviceIP不能为空");
			return;
		}
		

		// url例子:http://localhost:8080/ServiceConfig/GetSerConfProperties.json?token=df08554a-97d2-4c48-a5c0-a8a02e840f88
		// 其余参数
		String suffix = "/ServiceConfig/GetSerConfProperties.json?token=";
		String url = "http://" + serviceIP + suffix + token;
		// 返回参数
		String inputLine = null;
		try {
			// 设置5秒则超时
			byte[] ret = h.doHttpGet(url, 5);
			inputLine = new String(ret, "utf-8");
			int flag = JSON.parseObject(inputLine, JSONObject.class).getIntValue("flag");
			// 调用接口成功
			if (flag == 1) {
				// 获取配置参数对象信息
				byte[] bytes = JSON.parseObject(inputLine, JSONObject.class).getBytes("obj");
				// 解密
				byte[] decrypt = DesUtil.decrypt(bytes);
				logger.error("X1:"+HexStr.toStr(decrypt));
				// 转回对象
				Object byteToObject = ChangeUtil.byteToObject(decrypt);
				if (byteToObject != null) {
					// 这里知道是map类型，直接忽略警告
					@SuppressWarnings("unchecked")
					Map<String, Object> confMap = (Map<String, Object>) byteToObject;
					logger.error("X:"+JSON.toJSONString(confMap));
					// 写缓存，即使数据是NULL
					configMap.putAll(confMap);

					// value = (String) configMap.get(key);
				}

			}

		} catch (Exception e) {
			logger.error("调用服务接口失败：" + e.getLocalizedMessage());
			throw e;
		} finally {
			// 关闭连接池
			// stop();
		}

		// return value;

	}

	public static void main(String[] args) {
		// String path = getConfig("file");
		// String host1 = getConfig("host1");
		// System.out.println(path + host1);
	}
}
