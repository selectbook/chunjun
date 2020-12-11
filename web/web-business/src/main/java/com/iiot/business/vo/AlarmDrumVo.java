package com.iiot.business.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmDrumVo {


    /**
     * 编号
     */
    private Long id;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 油桶编号
     */
    private String oilCode;

    /**
     * 油品名称
     */
    private String oilName;

    /**
     * 品牌厂商
     */
    private String vendor;


    /**
     * 设备号
     */
    private String bindDevCode;
    /**
     * 油桶容量(l)
     */
    private Double tankCapacity;

    /**
     * 油桶余量(l)
     */
    private Double remainAmount;

    /**
     * 绑定部门id
     */
    private Long bindDeptId;

    /**
     * 绑定时间
     */
    private Date bindTime;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 0 非绑定状态 1 绑定状态
     */
    private String bindStatus;


    private Integer drumStatus;
}
