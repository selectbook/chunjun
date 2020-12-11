package com.iiot.commCommon;

import java.io.Serializable;

//数据上发的转换结果
public class TransformUpResult implements Serializable{
	
	private static final long serialVersionUID = -2772406285464220443L;

	// 统一结构
	World world;

	// 下发的回复数据
	byte[] replyArray;

	public TransformUpResult() {
		super();
	}

	public TransformUpResult(World world, byte[] replyArray) {
		super();
		this.world = world;
		this.replyArray = replyArray;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public byte[] getReplyArray() {
		return replyArray;
	}

	public void setReplyArray(byte[] replyArray) {
		this.replyArray = replyArray;
	}

}
