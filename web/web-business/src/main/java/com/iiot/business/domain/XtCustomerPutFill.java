package com.iiot.business.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.iiot.common.annotation.Excel;
import com.iiot.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 客户打印小票对象 xt_customer_put_fill
 * 
 * @author desom
 * @date 2020-07-26
 */
public class XtCustomerPutFill extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 打印时间 */
    @Excel(name = "打印时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date putDate;

    /** 实定量 */
    @Excel(name = "实定量")
    private Double reallizeAmout;

    /** 设定量 */
    @Excel(name = "设定量")
    private Double settingAmount;

    /** 总加注量 */
    @Excel(name = "总加注量")
    private Double totalAmount;

    /** 当前加注量 */
    @Excel(name = "当前加注量")
    private Double currentAmount;

    /** 车牌号 */
    @Excel(name = "车牌号")
    private String licensePlate;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPutDate(Date putDate) 
    {
        this.putDate = putDate;
    }

    public Date getPutDate() 
    {
        return putDate;
    }
    public void setReallizeAmout(Double reallizeAmout) 
    {
        this.reallizeAmout = reallizeAmout;
    }

    public Double getReallizeAmout() 
    {
        return reallizeAmout;
    }
    public void setSettingAmount(Double settingAmount) 
    {
        this.settingAmount = settingAmount;
    }

    public Double getSettingAmount() 
    {
        return settingAmount;
    }
    public void setTotalAmount(Double totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public Double getTotalAmount() 
    {
        return totalAmount;
    }
    public void setCurrentAmount(Double currentAmount) 
    {
        this.currentAmount = currentAmount;
    }

    public Double getCurrentAmount() 
    {
        return currentAmount;
    }
    public void setLicensePlate(String licensePlate) 
    {
        this.licensePlate = licensePlate;
    }

    public String getLicensePlate() 
    {
        return licensePlate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("putDate", getPutDate())
            .append("reallizeAmout", getReallizeAmout())
            .append("settingAmount", getSettingAmount())
            .append("totalAmount", getTotalAmount())
            .append("currentAmount", getCurrentAmount())
            .append("licensePlate", getLicensePlate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
