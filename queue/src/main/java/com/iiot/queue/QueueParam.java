package com.iiot.queue;

/**
 * 
* @ClassName: QueueParam
* @Description: 持久化队列的参数
* @date 2016年5月6日 下午2:44:08
*
 */
public class QueueParam {
	//每一个实例具有唯一的名称，由外部负责保证，建议使用UUID（固定死）
	String name;
	//高低水平位，单位字节
	long highLevel;
	long lowLevel;
	public QueueParam(String name){
		this.name = name;
		//从原来的500调整到100，这样减少GC的可能性
		highLevel = 1024 *1024 * 20;
		lowLevel = 1024 *1024 * 10;
	}
	public QueueParam(String name,long highLevel,long lowLevel){
		this.name = name;
		this.highLevel = highLevel;
		this.lowLevel = lowLevel;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getHighLevel() {
		return highLevel;
	}
	public void setHighLevel(long highLevel) {
		this.highLevel = highLevel;
	}
	public long getLowLevel() {
		return lowLevel;
	}
	public void setLowLevel(long lowLevel) {
		this.lowLevel = lowLevel;
	}
	
	
}
