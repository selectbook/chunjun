package com.iiot.redis;

public class HKeyValue {
	String skey;
	String value;

	public HKeyValue(String skey, String value) {
		super();
		this.skey = skey;
		this.value = value;
	}

	public String getSkey() {
		return skey;
	}

	public void setSkey(String skey) {
		this.skey = skey;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}