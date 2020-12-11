package com.iiot.queue;

import java.io.Serializable;

public class QData implements Serializable{
	
	private static final long serialVersionUID = 187910590988370807L;
	
	byte []data;

	public QData(byte []data){
		this.data = data;
	}
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public String toString(){
		//return Arrays.toString(data);
		return UtilsByte.bcd2Str(data);
	}
	
}
