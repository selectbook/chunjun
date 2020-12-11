package com.iiot.util;

import com.alibaba.fastjson.JSON;
import com.iiot.common.http.Http;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 
* @ClassName: ExceptionUtil
* @Description: 异常的工具类
* @date 2016年8月9日 下午4:08:16
*
 */
public class ExceptionUtil {

	/**
	 * 
	* @Title: getStackStr
	* @Description: 得到异常的栈信息
	* @param @param throwable
	* @param @return
	* @return String    返回类型 
	* @throws
	 */
	public static String getStackStr(Throwable throwable) {
		StringWriter buffer = new StringWriter();
		PrintWriter out = new PrintWriter(buffer);

		throwable.printStackTrace(out);
		out.flush();
		String result = buffer.toString();
		String sendUrl = PropertiesUtilLocal.getConfig("monitorUrl");
		if(!StringUtils.isEmpty(sendUrl)){
			sendExceptionMonitor(sendUrl, throwable.toString(), result);
		}
		return result;
	}

	public static void sendExceptionMonitor(String sendUrl, String title, String content) {
		Map<String, String> sendMap = new HashMap<>();
		sendMap.put("title", title);
		sendMap.put("content", content);
//		String address = "http://192.168.230.241:8088";
		String address = sendUrl;
		String json = JSON.toJSONString(sendMap);
		String serviceName = PropertiesUtilLocal.getConfig("serviceName");
		String url = address + "/monitor/addBatchMonitorWeb.do?json=" +
				json + "&name=" + serviceName + "&type=3";
		String result = Http.HttpGet(url);
		System.err.println("发送成功："+ result);
	}
}
