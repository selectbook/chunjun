package com.iiot.net;

import com.iiot.source.Packet29;
import com.iiot.util.ExceptionUtil;
import com.iiot.util.PropertiesUtilLocal;
import org.apache.log4j.Logger;

public class TcpServerIIOT {
	Logger logger = Logger.getLogger(TcpServerIIOT.class);

	private static TcpServerIIOT ts = new TcpServerIIOT();

	private TcpServerIIOT() {

	}

	public static TcpServerIIOT getTcpServer() {
		return ts;
	}

	public void start() {
		// 读配置
//		String ip = "127.0.0.1";
//		int port = 3333;
//		String ip = PropertiesUtilLocal.getConfig("ServerIIOTIP");
//		int port = PropertiesUtilLocal.getConfigAsInt("ServerIIOTPort");


		String ip 	= "0.0.0.0";
		int port = 4505;


		boolean initState = false;
		try {
			initState = ServerIIOT.getInst().start(ip, port, Packet29.class, ProcIIOTServer.class);
		} catch (Exception e) {
			logger.error("IIOT服务端启动异常：" + ip + ":" + port + "/:" + ExceptionUtil.getStackStr(e));
		}
		// 初始化连接
		if (!initState) {
			logger.error("IIOT服务端启动失败：" + ip + ":" + port);
			return;
		}
	}

	public void stop() {
		// 关闭
		ServerIIOT.getInst().stop();

	}

}
