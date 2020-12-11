package com.iiot.commCommon;

public interface Funs {
	// 根据VIP查询终端的类型
	String getTerminalTypeInDB(String vip);
	
	// 根据VIP获取自行车信息
	String getBicycleMacAddrByVip(String vip);
}
