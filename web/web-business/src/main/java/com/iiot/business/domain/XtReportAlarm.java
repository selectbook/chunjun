package com.iiot.business.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.iiot.common.annotation.Excel;
import com.iiot.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 警告对象 xt_report_alarm
 * 
 * @author desom
 * @date 2020-07-14
 */
public class XtReportAlarm extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 设备号 */
    @Excel(name = "设备号")
    private String devCode;

    /** 报警类型 (insufficientOilAlarm) */
    @Excel(name = "报警类型 (insufficientOilAlarm)")
    private String type;

    /** $column.columnComment */
    @Excel(name = "报警类型 (insufficientOilAlarm)", width = 30, dateFormat = "yyyy-MM-dd")
    private Date timeBegin;

    /** 油桶余量 */
    @Excel(name = "油桶余量")
    private Long remainAmount;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDevCode(String devCode) 
    {
        this.devCode = devCode;
    }

    public String getDevCode() 
    {
        return devCode;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setTimeBegin(Date timeBegin) 
    {
        this.timeBegin = timeBegin;
    }

    public Date getTimeBegin() 
    {
        return timeBegin;
    }
    public void setRemainAmount(Long remainAmount) 
    {
        this.remainAmount = remainAmount;
    }

    public Long getRemainAmount() 
    {
        return remainAmount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("devCode", getDevCode())
            .append("type", getType())
            .append("timeBegin", getTimeBegin())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remainAmount", getRemainAmount())
            .toString();
    }
}
