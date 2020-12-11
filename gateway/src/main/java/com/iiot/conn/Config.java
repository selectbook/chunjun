package com.iiot.conn;

import com.iiot.util.PropertiesUtilLocal;
import org.apache.log4j.Logger;

public class Config {
	
	Logger logger = Logger.getLogger(Config.class);
	
	
	//数据源的IP和端口
	private String ip;
	private int port;
	
	public Config() {
		ip = PropertiesUtilLocal.getConfig("iiot_ip");
		logger.info("ip:"+ip);
		port = Integer.parseInt(PropertiesUtilLocal.getConfig("iiot_port"));
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}


}
