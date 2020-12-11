package com.iiot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class regexUtil {
	
	public static boolean isConSpeCharacters(String string){ 
		//中文、英文、数字包括下划线
//		String regEx="^[\u4E00-\u9FA5A-Za-z0-9\\)\\(\\s_-]+$";
		//不允许出现字符' " % &
		String regEx = "[^'\"%&]*";
	    Pattern p = Pattern.compile(regEx);
		// 这里单独一个特殊字符不能通过，如果是字母数字加上特殊字符，则可通过验证
		Matcher m = p.matcher(string);
		if (m.matches()) {
			return true;
		}else{
			return false;
		}
	} 
	
	public static void main(String[] args) {
		String str = "123";
		boolean conSpeCharacters = isConSpeCharacters(str);
		System.out.println(conSpeCharacters);
	}

	
	
}
