package com.iiot.queue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

/**
 * 
* @ClassName: Utils
* @Description: 实用类
* @date 2016年5月9日 下午7:20:44
*
 */
public class Utils {
	static Logger logger = Logger.getLogger(Utils.class);

	/**
	 * 将对象转换成byte数组
	 * @throws IOException 
	 */
	public static byte[] ObjectToByte(Object obj) throws IOException {
		ByteArrayOutputStream baot = new ByteArrayOutputStream();
		ObjectOutputStream oot;
		byte[] array = null;
		// try {
		oot = new ObjectOutputStream(baot);
		oot.writeObject(obj);
		array = baot.toByteArray();
		// } catch (IOException e) {
		// logger.error("ObjectToByte fail"+e.getLocalizedMessage());
		// System.out.println("zheer");
		// }
		return array;
	}

	/**
	 * 将byte数组转换成对象
	 * @throws Exception 
	 * @throws ClassNotFoundException 
	 */
	public static Object byteToObject(byte[] array) throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(array);
		Object obj = null;
		// try {
		ObjectInputStream is2 = new ObjectInputStream(is);
		obj = is2.readObject();
		// } catch (Exception e) {
		// logger.error("byteToObject fail"+ e.getLocalizedMessage());
		// }
		return obj;
	}
	
	/** 
	 * 删除单个文件 
	 * @param   sPath    被删除文件的文件名 
	 * @return 单个文件删除成功返回true，否则返回false 
	 */  
	public static boolean deleteFile(String sPath) {  
		boolean flag = false;  
	    File file = new File(sPath);  
	    // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	}  
	public static byte[] int2byte(int res) {
		byte[] targets = new byte[4];

		targets[0] = (byte) (res & 0xff);// 最低位
		targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
		targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
		targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
		return targets;
	}
}
