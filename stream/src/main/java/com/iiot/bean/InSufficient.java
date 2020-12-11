package com.iiot.bean;

public class InSufficient {

    private Long id;
    private String dev_code;
    private String alarmType;
    private Long timeBegin;
    private Long createTime;
    private Float remainAmount;

    public InSufficient() {

    }

    public InSufficient(String dev_code, String alarmType, Long timeBegin, Long createTime, Float remainAmount) {
        this.dev_code = dev_code;
        this.alarmType = alarmType;
        this.timeBegin = timeBegin;
        this.createTime = createTime;
        this.remainAmount = remainAmount;
    }

    public String getDev_code() {
        return dev_code;
    }

    public void setDev_code(String dev_code) {
        this.dev_code = dev_code;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public Long getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin(Long timeBegin) {
        this.timeBegin = timeBegin;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Float getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(Float remainAmount) {
        this.remainAmount = remainAmount;
    }

    @Override
    public String toString() {
        return "InSufficient{" +
                "dev_code='" + dev_code + '\'' +
                ", alarmType='" + alarmType + '\'' +
                ", timeBegin=" + timeBegin +
                ", createTime=" + createTime +
                ", remainAmount=" + remainAmount +
                '}';
    }
}
