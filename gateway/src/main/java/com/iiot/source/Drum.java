package com.iiot.source;

import java.sql.Timestamp;

public class Drum {

    private static final long serialVersionUID = 1L;

    private Integer id; // 数据库中的油桶编号
    private String bindDevCode; // 绑定的设备号
    private String oilCode; // 油桶编号
    private String oilName; // 油品名称
    private String vendor; // 品牌厂商
    private Float tankCapacity; // 油桶容量
    private Float remainAmount; // 油桶余量
    private char bindStatus; // 0 非绑定状态 1 绑定状态

    public Drum() {

    }

    public Drum(Integer id, String bindDevCode, String oilCode, String oilName, String vendor, Float tankCapacity, Float remainAmount, char bindStatus) {
        this.id = id;
        this.bindDevCode = bindDevCode;
        this.oilCode = oilCode;
        this.oilName = oilName;
        this.vendor = vendor;
        this.tankCapacity = tankCapacity;
        this.remainAmount = remainAmount;
        this.bindStatus = bindStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBindDevCode() {
        return bindDevCode;
    }

    public void setBindDevCode(String bindDevCode) {
        this.bindDevCode = bindDevCode;
    }

    public String getOilCode() {
        return oilCode;
    }

    public void setOilCode(String oilCode) {
        this.oilCode = oilCode;
    }

    public String getOilName() {
        return oilName;
    }

    public void setOilName(String oilName) {
        this.oilName = oilName;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Float getTankCapacity() {
        return tankCapacity;
    }

    public void setTankCapacity(Float tankCapacity) {
        this.tankCapacity = tankCapacity;
    }

    public Float getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(Float remainAmount) {
        this.remainAmount = remainAmount;
    }

    public char getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(char bindStatus) {
        this.bindStatus = bindStatus;
    }

    @Override
    public String toString() {
        return "Drum{" +
                "bindDevCode='" + bindDevCode + '\'' +
                ", oilCode='" + oilCode + '\'' +
                ", oilName='" + oilName + '\'' +
                ", vendor='" + vendor + '\'' +
                ", tankCapacity=" + tankCapacity +
                ", remainAmount=" + remainAmount +
                ", bindStatus=" + bindStatus +
                '}';
    }
}
