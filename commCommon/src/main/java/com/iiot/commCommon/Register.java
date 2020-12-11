package com.iiot.commCommon;

//各终端网关第一次使用时生成的UUID，生成后记录这个ID，以后每次都用这个ID
//每次启动时都需要做一次终端网关注册
public class Register {
	String gateway; // 网关的ID，一般在第一次使用时生成的UUID，生成后记录这个ID，以后每次都用这个ID
	String ip; // 命令下发的IP(Thrift)
	String port; // 命令下发的port(Thrift)

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

}
