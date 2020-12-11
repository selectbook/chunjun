package com.iiot.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class DBPool {
	
	DataSource dataSource = null;
	
	Logger logger = Logger.getLogger(DBPool.class);
	//mysql驱动
	public void start(String url,String user,String psw) throws Exception{
		start("com.mysql.jdbc.Driver",url,user,psw);
	}
	
	//sqlServer驱动
	public void startSqlServer(String url,String user,String psw) throws Exception{
		start("com.microsoft.sqlserver.jdbc.SQLServerDriver",url,user,psw);
	}
	
	public void start(String url,String user,String psw,int initialSize,int maxActive,int minIdle) throws Exception{
		start("com.microsoft.sqlserver.jdbc.SQLServerDriver",url,user,psw,initialSize,maxActive,minIdle);
	}
	
	public void start(String driver,String url,String user,String psw) throws Exception{
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
		try {
			dataSource = DruidDataSourceFactory.createDataSource(prop);
		} catch (Exception e) {
			logger.error("dbpool:"+e.getLocalizedMessage());
			throw e;
		}
	}
	
	public void start(String driver,String url,String user,String psw,int initialSize,int maxActive,int minIdle) throws Exception{
		
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
		if(initialSize==0){
			initialSize=15;
		}
		if(maxActive==0){
			maxActive=20;
		}
		if(minIdle==0){
			minIdle=10;
		}
		
		prop.setProperty("initialSize",""+initialSize);
		prop.setProperty("maxActive", ""+maxActive);
		prop.setProperty("minIdle", ""+minIdle);
		
		try {
			dataSource = DruidDataSourceFactory.createDataSource(prop);
		} catch (Exception e) {
			logger.error("dbpool:"+e.getLocalizedMessage());
			throw e;
		}
	}
	
	
	public Connection getConn() throws SQLException{
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			logger.error("getConn:"+e.getLocalizedMessage());
			throw e;
		}
	}
	public void freeConn(Connection conn) throws SQLException {
		conn.close();
	}
}
