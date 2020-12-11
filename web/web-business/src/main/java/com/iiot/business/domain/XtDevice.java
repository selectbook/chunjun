package com.iiot.business.domain;

import com.iiot.common.annotation.Excel;
import com.iiot.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 设备字段对象 xt_device
 *
 * @author desom
 * @date 2020-05-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XtDevice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private Long id;

    /**
     * 部门id
     */
    @Excel(name = "部门id")
    private Long deptId;

    /**
     * 设备名称
     */
    @Excel(name = "设备名称")
    private String devName;

    /**
     * 设备描述
     */
    @Excel(name = "设备描述")
    private String devDesc;

    /**
     * 设备类型
     */
    @Excel(name = "设备类型")
    private String devType;

    /**
     * 设备类型id
     */
    @Excel(name = "设备类型id")
    private Long devTypeId;

    /**
     * 设备分组
     */
    @Excel(name = "设备分组")
    private String devGroup;

    /**
     * 设备分组id
     */
    @Excel(name = "设备分组id")
    private Long devGroupId;

    /**
     * 设备编号
     */
    @Excel(name = "设备编号")
    private String devCode;

    /**
     * 设备状态（0初始值 1正常 2离线 3故障）
     */
    @Excel(name = "设备状态", readConverterExp = "0=初始值,1=正常,2=离线,3=故障")
    private String status;

    /**
     * 油桶编号
     */
    @Excel(name = "油桶编号")
    private String oilCode;

    /**
     * 绑定部门id
     */
    @Excel(name = "绑定部门id")
    private Long bindDeptId;

    /**
     * 绑定时间
     */
    @Excel(name = "绑定时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date bindTime;

    /**
     * 注册状态（0未注册 1已注册）
     */
    @Excel(name = "注册状态", readConverterExp = "0=未注册,1=已注册")
    private String register;

    /**
     * 注册时间
     */
    @Excel(name = "注册时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date registerTime;

    /**
     * 工作模式（1基本模式 2汽修店模式 3油厂模式）
     */
    @Excel(name = "工作模式", readConverterExp = "1=基本模式,2=汽修店模式,3=油厂模式")
    private String workMod;

    /**
     * 所在省份
     */
    @Excel(name = "所在省份")
    private String province;

    /**
     * 所在城市
     */
    @Excel(name = "所在城市")
    private String city;

    /**
     * 详细地址
     */
    @Excel(name = "详细地址")
    private String address;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;



}
