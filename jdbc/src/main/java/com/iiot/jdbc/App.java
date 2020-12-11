package com.iiot.jdbc;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
	static Logger logger = Logger.getLogger(App.class);
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        JdbcWriter writer = new JdbcWriter();
        JdbcWriter writer2 = new JdbcWriter();
		try {
			writer.start("com.mysql.jdbc.Driver","x", "jdbc:mysql://rds93oo8s3xzq3cqf116o.mysql.rds.aliyuncs.com:3306/bsj", "bsj",
					"Bsj20160101asdf", 4, 500,"C:\\123\\",1024 *1024 * 300,1024 *1024 *50,8,10,5);
		
			writer2.start("com.mysql.jdbc.Driver","x", "jdbc:mysql://rds93oo8s3xzq3cqf116o.mysql.rds.aliyuncs.com:3306/bsj", "bsj",
					"Bsj20160101asdf", 4, 500,"C:\\1234\\",1024 *1024 * 300,1024 *1024 *50,8,10,5);
		
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());

		}
		
		try {
			writer.pushWrite("insert into test1(a,b,c) values(?,?,?)", 1, "2", new Date(115, 1, 2),new byte[1024]);
			writer2.pushWrite("insert into test1(a,b,c) values(?,?,?)", 2, "2X", new Date(115, 1, 2),new byte[1024]);
		} catch (Exception e1) {
			logger.error(e1.getLocalizedMessage());
			
			
		}
		
		

//		for (int i = 0; i < 1000000; i++) {
//			try {
//				writer.pushWrite("insert into test1(a,b,c) values(?,?,?)", 1, "2", new Date(115, 1, 2),new byte[1024]);
//			} catch (Exception e) {
//				logger.error(e.getLocalizedMessage());
//
//			}
//		}
		System.out.println("done");
		try {
			Thread.sleep(1000 * 300);
		} catch (InterruptedException e) {
			logger.error(e.getLocalizedMessage());

		}
		writer.stop();
    }
}
