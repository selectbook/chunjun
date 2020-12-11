package com.iiot.util;

import org.apache.commons.lang.StringUtils;

/**
 * Created by Administrator on 2017/5/2.
 */
public class SQLUtil {

    /**
     * like方法的字符串转义
     * @param str
     * @return
     */
    public static String parameterEscapingBySql(String str){
        if(StringUtils.isEmpty(str)){
            return null;
        }
        //转义
        String result = str.replaceAll("%", "/%").replaceAll("_", "/_").replace("\'","''");
        return result;
    }
    
    /**
     * 转义处理
     * @param str
     * @return
     */
    public static String parameterEscapingForSql(String str){
        if(StringUtils.isEmpty(str)){
            return null;
        }
        //转义
        String result =  //JAVA中“\”是转义字符“\”就要用“\\”两个连续的转义字符表示;Mysql中反斜线(‘\’)字符要用“\\”转义
        		         //Mysql中 “\"”表示一个双引号(“"”)符;JAVA字符串里面的\"表示字符“"”;三个斜杠前两表示对“\”进行转义
        		         str.replaceAll("\"","\\\"")        		         
        		         //str.replaceAll("\"","\"\"")//在字符串内用‘"’引用的‘"’可以写成‘""’        		         
        		         //Mysql中 “\'”表示一个单引号(“'”)符。java字符串中也如此
        		         //.replaceAll("\'","\\\'")
        		         .replaceAll("\'","''")//字符串内用‘'’引用的‘'’可以写成‘''’
        		         //mysql中“%”用“\%”进行转义，否则“%”将解释为一个通配符
        		         .replaceAll("%", "\\%")
        		         //Mysql中 “\_”表示一个“_”符。
        		         .replaceAll("_", "\\_");
        
        return result;
    }
    
 
    public static void main(String[] args) {
    	String escapeSql = parameterEscapingForSql("aaaa\"");
    	System.out.println(escapeSql);
    	
    	String str = "aaaa\"";
    	String str1=str.replaceAll("\"","\\\"");
    	System.out.println(str1);
    	String str2 = "aaaa\'";
    	String str3=str2.replaceAll("\"","\\\'");
    	System.out.println(str3);
    	
    	String str4 = "aaaa%'";
    	String str5=str4.replaceAll("%", "\\%");
    	System.out.println(str5);
    	
	}

}
