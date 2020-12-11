package com.iiot.jdbc;

import java.util.Properties;

import javax.sql.DataSource;
import java.sql.Connection;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class JdbcTemplate {
	
	
	public static Connection getJdbcTemplate(String driver,String url,String user,String psw)throws Exception{
		    Properties prop = new Properties();
			//prop.setProperty("driverClassName", "com.mysql.jdbc.Driver");
			prop.setProperty("driverClassName", driver);
			//prop.setProperty("url", "jdbc:mysql://rm-uf6qy3i80z8051xm7.mysql.rds.aliyuncs.com:3306/bsj");
			//prop.setProperty("username", "bsj");
			//prop.setProperty("password", "Bsj20160101asdf");
			prop.setProperty("url", url);
			prop.setProperty("username", user);
			prop.setProperty("password", psw);
			prop.setProperty("poolPreparedStatements", "true");
			
			//默认池的数量为8
			prop.setProperty("initialSize", "8");
			prop.setProperty("maxActive", "20");
			prop.setProperty("minIdle", "10");
			
			DataSource dataSource = DruidDataSourceFactory.createDataSource(prop);
			Connection conn = dataSource.getConnection();
			return conn;
	}
	


}
