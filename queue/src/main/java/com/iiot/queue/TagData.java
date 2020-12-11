package com.iiot.queue;

import java.util.LinkedList;
import java.util.Queue;

public class TagData {
	//队列中的所有数据量
	long tagLen;
	//数据队列
	Queue<QData> queueData;
	
	public TagData(){
		tagLen = 0;
		queueData = new LinkedList<QData>();
	}
	
	public void AddDataQueue(TagData newData){
		tagLen += newData.getTagLen();
		queueData.addAll(newData.queueData);
	}
	
	public void AddData(QData data){
		tagLen += data.getData().length;
		queueData.add(data);
	}
	
	public long getTagLen() {
		return tagLen;
	}
	public void setTagLen(long tagLen) {
		this.tagLen = tagLen;
	}
	public Queue<QData> getQueueData() {
		return queueData;
	}
	public void setQueueData(Queue<QData> queueData) {
		this.queueData = queueData;
	}
	
	
}
