package com.iiot.business.vo;


import com.iiot.common.annotation.Excel;
import com.iiot.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;



@AllArgsConstructor
@NoArgsConstructor
public class OilFillDeptVo extends BaseEntity {


    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private Long id;

    /**
     * 终端编号
     */
    @Excel(name = "终端编号")
    private String devCode;

    /**
     * 工作模式（1基本模式 2汽修店模式 3油厂模式）
     */
    @Excel(name = "工作模式", readConverterExp = "1=基本模式,2=汽修店模式,3=油厂模式")
    private String workMod;

    /**
     * 设定加注量
     */
    @Excel(name = "设定加注量")
    private Double settingAmount;

    /**
     * 当前加注量
     */
    @Excel(name = "当前加注量")
    private Double currentAmount;

    /**
     * 实际加注总量
     */
    @Excel(name = "实际加注总量")
    private Double realTotalAmount;

    /**
     * 总加注量
     */
    @Excel(name = "总加注量")
    private Double totalAmount;

    /**
     * 油桶容量
     */
    @Excel(name = "油桶容量")
    private Double tankCapacity;

    /**
     * 油桶余量
     */
    @Excel(name = "油桶余量")
    private Double remainAmount;

    /**
     * 终端状态（0初始值 1正常 2离线 3故障）
     */
    @Excel(name = "终端状态", readConverterExp = "0=初始值,1=正常,2=离线,3=故障")
    private String devStatus;

    /**
     * 终端IP地址
     */
    @Excel(name = "终端IP地址")
    private String ipaddr;

    /**
     * 终端时间
     */

    /**
     * 终端时间
     */
    @Excel(name = "终端时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date localTime;


    /**
     * 部门编号
     */
    private Long deptId;


    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;


    /**
     * 注册地
     */
    @Excel(name = "注册地")
    private String registerAddress;

    /**
     * 定位地
     */
    @Excel(name = "最新定位地")
    private String positionAddress;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDevCode() {
        return devCode;
    }

    public void setDevCode(String devCode) {
        this.devCode = devCode;
    }

    public String getWorkMod() {
        return workMod;
    }

    public void setWorkMod(String workMod) {
        this.workMod = workMod;
    }


    public Double getSettingAmount() {
        return settingAmount;
    }

    public void setSettingAmount(Double settingAmount) {
        BigDecimal bigDecimal = new BigDecimal(settingAmount);
        this.settingAmount = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(Double currentAmount) {
        BigDecimal bigDecimal = new BigDecimal(currentAmount);
        this.currentAmount = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getRealTotalAmount() {
        return realTotalAmount;
    }

    public void setRealTotalAmount(Double realTotalAmount) {
        BigDecimal bigDecimal = new BigDecimal(realTotalAmount);
        this.realTotalAmount = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        BigDecimal bigDecimal = new BigDecimal(totalAmount);
        this.totalAmount = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getTankCapacity() {
        return tankCapacity;
    }

    public void setTankCapacity(Double tankCapacity) {
        BigDecimal bigDecimal = new BigDecimal(tankCapacity);
        this.tankCapacity =  bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(Double remainAmount) {
        BigDecimal bigDecimal = new BigDecimal(remainAmount);
        this.remainAmount = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public String getDevStatus() {
        return devStatus;
    }

    public void setDevStatus(String devStatus) {
        this.devStatus = devStatus;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public Date getLocalTime() {
        return localTime;
    }

    public void setLocalTime(Date localTime) {
        this.localTime = localTime;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public String getPositionAddress() {
        return positionAddress;
    }

    public void setPositionAddress(String positionAddress) {
        this.positionAddress = positionAddress;
    }
}
