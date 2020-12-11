package com.iiot.business.bo;


import com.iiot.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XtOilFillBoExport extends BaseEntity {

    private static final long serialVersionUID = 1L;




    /**
     * 部门编号
     */
    private Long deptId;

    /**
     * 编号
     */
    private Long id;

    /**
     * 终端编号
     */
    private String devCode;

    /**
     * 工作模式（1基本模式 2汽修店模式 3油厂模式）
     */
    private String workMod;

    /**
     * 设定加注量
     */
    private Double settingAmount;

    /**
     * 当前加注量
     */
    private Double currentAmount;

    /**
     * 实际加注总量
     */
    private Double realTotalAmount;

    /**
     * 总加注量
     */
    private Double totalAmount;

    /**
     * 油桶容量
     */
    private Double tankCapacity;

    /**
     * 油桶余量
     */
    private Double remainAmount;

    /**
     * 终端状态（0初始值 1正常 2离线 3故障）
     */
    private String devStatus;

    /**
     * 终端IP地址
     */
    private String ipaddr;

    /**
     * 终端时间
     */
    private Date localTime;


    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

}
