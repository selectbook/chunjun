package com.iiot.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;


public class JdbcGetTable {

	Logger logger = Logger.getLogger(JdbcGetTable.class);
	
	DBPool dbpool = new DBPool();
	
	private Connection conn=null;
	
	String dbName;
	String dbUrl;
	String dbUser;
	String dbPsw;

	public void start(String dbUrl, String dbUser, String dbPsw) throws Exception {
		start("com.microsoft.sqlserver.jdbc.SQLServerDriver",dbUrl,dbUser,dbPsw);
	}
	
	public void start(String driver,String dbUrl, String dbUser, String dbPsw) throws Exception {
		this.dbUrl = dbUrl;
		this.dbUser = dbUser;
		this.dbPsw = dbPsw;
		dbpool.start(driver,dbUrl, dbUser, dbPsw);
	}
	
	
	public Connection getConnection(){
		try {
			conn = dbpool.getConn();
		} catch (SQLException e) {
			logger.error("dbpool.getConn() fail："+e.getStackTrace());
		}
		return conn;
	}
	
	public void stop(){
		try {
			dbpool.freeConn(conn);
		} catch (SQLException e) {
			logger.error("dbpool.freeConn(conn) fail："+e.getStackTrace());
		}
	}
	
}
