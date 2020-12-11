package com.iiot.file;


import com.iiot.common.bytes.Conv;

public class FileContent {
	static String fixStr = "FDB";
	// 固定尾（FDB 3字节）+内容长度（4字节）+数据XOR校验（1）
	byte[] fix;
	long contentLen;
	byte xor;

	public static  int getNeedLen() {
		return fixStr.length() + 4 + 1;
	}
	
	

	// 序列化
	public byte[] toArray() {
		int len = getNeedLen();
		byte array[] = new byte[len];
		int offset = 0;
		System.arraycopy(fix, 0, array, offset, fix.length);
		offset += fix.length;

		// 高位在前，低位在后

		Conv.setIntNetOrder(array, offset, contentLen);
		offset += 4;
		array[offset++] = xor;
		return array;
	}

	public void fromArray(byte array[]) throws Exception {
		int len = getNeedLen();
		if (len != array.length) {
			throw new Exception("长度不等");
		}
		int offset = 0;
		for (int i = 0; i < fix.length; i++) {
			if (fix[i] != array[offset + i]) {
				throw new Exception("头不对");
			}
		}
		offset += fix.length;

		// 高位在前，低位在后
		contentLen = Conv.getIntNetOrder(array, offset);
//		if (this.contentLen != contentLen) {
//			throw new Exception("内容长度不对");
//		}

		xor = array[offset++];

	}
	//获取文件内容的长度
	public static int getContentLen(byte[]byt){
		int num=(int) Conv.getIntNetOrder(byt,0);
		return num;
	}
	public FileContent() {
		fix = fixStr.getBytes();

	}



	public long getContentLen() {
		return contentLen;
	}



	public void setContentLen(long contentLen) {
		this.contentLen = contentLen;
	}



	public byte getXor() {
		return xor;
	}



	public void setXor(byte xor) {
		this.xor = xor;
	}

	

}
