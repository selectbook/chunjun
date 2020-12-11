package com.iiot.cmd;

/**
 * @author Administrator 命令下发信息交流类
 */
public class ComInfo {

	private String uuid; // UUID号
	private String vip; // vip
	private String commandMsg; // 命令
	private String status; // 下发状态
	private ParamMsg paramMsg; // 参数信息
	private String devid; // 终端号

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public String getCommandMsg() {
		return commandMsg;
	}

	public void setCommandMsg(String commandMsg) {
		this.commandMsg = commandMsg;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ParamMsg getParamMsg() {
		return paramMsg;
	}

	public void setParamMsg(ParamMsg paramMsg) {
		this.paramMsg = paramMsg;
	}

	public String getDevid() {
		return devid;
	}

	public void setDevid(String devid) {
		this.devid = devid;
	}

	public ComInfo(String uuid, String vip, String commandMsg, String status, ParamMsg paramMsg, String devid) {
		super();
		this.uuid = uuid;
		this.vip = vip;
		this.commandMsg = commandMsg;
		this.status = status;
		this.paramMsg = paramMsg;
		this.devid = devid;
	}

	public ComInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "ComInfo [uuid=" + uuid + ", vip=" + vip + ", commandMsg=" + commandMsg + ", status=" + status
				+ ", paramMsg=" + paramMsg + ", devid=" + devid + "]";
	}
	
	
	

}
