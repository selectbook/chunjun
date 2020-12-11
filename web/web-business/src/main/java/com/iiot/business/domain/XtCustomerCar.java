package com.iiot.business.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.iiot.common.annotation.Excel;
import com.iiot.common.core.domain.BaseEntity;

/**
 * 客户车型对象 xt_customer_car
 * 
 * @author desom
 * @date 2020-07-26
 */
public class XtCustomerCar extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 品牌 */
    @Excel(name = "品牌")
    private String brandSeries;

    /** 设备编号 */
    @Excel(name = "设备编号")
    private String devCode;

    /** 客户id */
    @Excel(name = "客户id")
    private Long customerId;

    /** 车牌号 */
    @Excel(name = "车牌号")
    private String licensePlate;

    /** 当前加注量 */
    @Excel(name = "当前加注量")
    private Double currentAmount;

    /** 总加注量 */
    @Excel(name = "总加注量")
    private Double totalAmount;

    /** 设定量 */
    @Excel(name = "设定量")
    private Double settingAmount;

    /** 行驶公里 */
    @Excel(name = "行驶公里")
    private String carKilometre;

    /** 实定量 */
    @Excel(name = "实定量")
    private Double reallizeAmout;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setBrandSeries(String brandSeries) 
    {
        this.brandSeries = brandSeries;
    }

    public String getBrandSeries() 
    {
        return brandSeries;
    }
    public void setDevCode(String devCode) 
    {
        this.devCode = devCode;
    }

    public String getDevCode() 
    {
        return devCode;
    }
    public void setCustomerId(Long customerId) 
    {
        this.customerId = customerId;
    }

    public Long getCustomerId() 
    {
        return customerId;
    }
    public void setLicensePlate(String licensePlate) 
    {
        this.licensePlate = licensePlate;
    }

    public String getLicensePlate() 
    {
        return licensePlate;
    }
    public void setCurrentAmount(Double currentAmount) 
    {
        this.currentAmount = currentAmount;
    }

    public Double getCurrentAmount() 
    {
        return currentAmount;
    }
    public void setTotalAmount(Double totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public Double getTotalAmount() 
    {
        return totalAmount;
    }
    public void setSettingAmount(Double settingAmount) 
    {
        this.settingAmount = settingAmount;
    }

    public Double getSettingAmount() 
    {
        return settingAmount;
    }
    public void setCarKilometre(String carKilometre) 
    {
        this.carKilometre = carKilometre;
    }

    public String getCarKilometre() 
    {
        return carKilometre;
    }
    public void setReallizeAmout(Double reallizeAmout) 
    {
        this.reallizeAmout = reallizeAmout;
    }

    public Double getReallizeAmout() 
    {
        return reallizeAmout;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("brandSeries", getBrandSeries())
            .append("devCode", getDevCode())
            .append("customerId", getCustomerId())
            .append("licensePlate", getLicensePlate())
            .append("currentAmount", getCurrentAmount())
            .append("totalAmount", getTotalAmount())
            .append("settingAmount", getSettingAmount())
            .append("carKilometre", getCarKilometre())
            .append("reallizeAmout", getReallizeAmout())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
