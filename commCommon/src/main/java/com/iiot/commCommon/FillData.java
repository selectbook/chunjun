package com.iiot.commCommon;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FillData implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    private Date devTime; // 终端发送时间

    private Float settingAmount; // 设定加注量
    private Float currentAmount; // 当前加注量
    private Float totalAmount; // 总加注量
    private Float tankCapacity; // 油桶容量
    private Float leftTankAmount; // 油桶余量
    Map<String, Object> extend;// 扩展

    public FillData cloneMe() {
        try {
            FillData newFillData = (FillData) super.clone();
            Map<String, Object> newExtend = new HashMap<>();
            newExtend.putAll(extend);
            newFillData.setExtend(newExtend);
            return newFillData;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public FillData() {
        super();
        // 为每个值初始化,不然在序列化时会出错
        devTime = new Date();
        settingAmount = 0.0f;
        currentAmount = 0.0f;
        totalAmount = 0.0f;
        tankCapacity = 0.0f;
        leftTankAmount = 0.0f;
        extend = new HashMap<>();
    }

    public FillData(Date devTime, Float settingAmount, Float currentAmount, Float totalAmount, Float tankCapacity, Float leftTankAmount, Map<String, Object> extend) {
        this.devTime = devTime;
        this.settingAmount = settingAmount;
        this.currentAmount = currentAmount;
        this.totalAmount = totalAmount;
        this.tankCapacity = tankCapacity;
        this.leftTankAmount = leftTankAmount;
        this.extend = extend;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Date getDevTime() {
        return devTime;
    }

    public void setDevTime(Date devTime) {
        this.devTime = devTime;
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

    public Map<String, Object> getExtend() {
        return extend;
    }

    public void setExtend(Map<String, Object> extend) {
        this.extend = extend;
    }

}
