package com.iiot.commCommon;

import java.io.Serializable;
import java.sql.Timestamp;


public class Fill implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id; // 数据库中的终端编号
    private String devId; // 设备号
    private Float settingAmount; // 设定加注量
    private Float currentAmount; // 当前加注量
    private Float totalAmount; // 总加注量
    private Float tankCapacity; // 油桶容量
    private Float leftTankAmount; // 油桶余量
    private Timestamp registerTime; // 网关时间
    private Timestamp addTime; // 终端时间
    private char devStatus; // 终端状态（0初始值 1正常 2离线 3故障）
    private String ip; // 设备ip
    private Float realTotalAmount; // 真实加注总量


    public Fill() {
    }

    public Fill(Integer id, String devId, Float settingAmount, Float currentAmount, Float totalAmount, Float tankCapacity, Float leftTankAmount, Timestamp registerTime, Timestamp addTime, char devStatus, String ip, Float realTotalAmount) {
        this.id = id;
        this.devId = devId;
        this.settingAmount = settingAmount;
        this.currentAmount = currentAmount;
        this.totalAmount = totalAmount;
        this.tankCapacity = tankCapacity;
        this.leftTankAmount = leftTankAmount;
        this.registerTime = registerTime;
        this.addTime = addTime;
        this.devStatus = devStatus;
        this.ip = ip;
        this.realTotalAmount = realTotalAmount;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getSettingAmount() {
        return settingAmount;
    }

    public void setSettingAmount(Float settingAmount) {
        this.settingAmount = settingAmount;
    }

    public Float getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(Float currentAmount) {
        this.currentAmount = currentAmount;
    }

    public Float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Float getTankCapacity() {
        return tankCapacity;
    }

    public void setTankCapacity(Float tankCapacity) {
        this.tankCapacity = tankCapacity;
    }

    public Float getLeftTankAmount() {
        return leftTankAmount;
    }

    public void setLeftTankAmount(Float leftTankAmount) {
        this.leftTankAmount = leftTankAmount;
    }

    public Timestamp getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Timestamp registerTime) {
        this.registerTime = registerTime;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    public char getIfOffline() {
        return devStatus;
    }

    public void setIfOffline(char ifOffline) {
        this.devStatus = ifOffline;
    }

    public String getIp() {
        return ip;
    }

    public char getDevStatus() {
        return devStatus;
    }

    public void setDevStatus(char devStatus) {
        this.devStatus = devStatus;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Float getRealTotalAmount() {
        return realTotalAmount;
    }

    public void setRealTotalAmount(Float realTotalAmount) {
        this.realTotalAmount = realTotalAmount;
    }

    @Override
    public String toString() {
        return "Fill{" +
                "id=" + id +
                ", devId='" + devId + '\'' +
                ", settingAmount=" + settingAmount +
                ", currentAmount=" + currentAmount +
                ", totalAmount=" + totalAmount +
                ", tankCapacity=" + tankCapacity +
                ", leftTankAmount=" + leftTankAmount +
                ", registerTime=" + registerTime +
                ", addTime=" + addTime +
                ", devStatus=" + devStatus +
                ", ip='" + ip + '\'' +
                ", realTotalAmount=" + realTotalAmount +
                '}';
    }
}
