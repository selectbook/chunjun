package com.iiot.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * 
 * 判断是否是V3协议类型
 *
 */
public class FindProtocolType{

	private static final Set<String> terTypeSet = new HashSet<String>(Arrays.asList("A5E-3","KM-01","KM-02","GT02D"));
	
    //根据终端类型，判断是否是V3协议
	public static boolean getProtocolType(String TerminalType){
		boolean contains = terTypeSet.contains(TerminalType);
		return contains;
	}
	
	
	public static void main(String[] args) {
		boolean protocolType = getProtocolType("KM-02");
		System.out.println(protocolType);
	}
	
	
	public static String getProtocol(String terminalType){
		
		String protocolType="";
		
		if(terminalType==null){
			return null;
		}else{
			terminalType=terminalType.toUpperCase();
		}
		
		switch (terminalType) {
		  case "A5E-3":
		  case "KM-01":
		  case "KM-02":
		  case "GT02D":
		  protocolType="V3";
		  break;
		  case "M6":
		  case "M11":
		  case "GPRS-部标":
		  case "MINI":
		  case "BCAR":
		  protocolType="JT";
		  break;
		  default:
			    protocolType="A6";
				break;  
		}
		
		return protocolType;
		
	}
	

}
