package com.iiot.business.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备
 */
@Data
@ApiModel(value = "小程序设备")
public class DevicesVo {


    @ApiModelProperty(name = "id", value = "设备id", dataType = "Long", example = "1")
    private Long id;

    @ApiModelProperty(name = "deptId", value = "部门编号", dataType = "Long", example = "103")
    private Long deptId;

    @ApiModelProperty(name = "devName", value = "设备名称", dataType = "String", example = "天猫加注    机1")
    private String devName;

    @ApiModelProperty(name = "devDesc", value = "识别描述", dataType = "String", example = "WIFI通用款")
    private String devDesc;

    @ApiModelProperty(name = "devCode", value = "设备编号", dataType = "String", example = "XT365-000012")
    private String devCode;

    @ApiModelProperty(name = "status", value = "设备状态（0初始值 1正常 2离线 3故障）", dataType = "String", example = "1")
    private String status;

    @ApiModelProperty(name = "oilCode", value = "油桶编号", dataType = "String", example = "28671")
    private String oilCode;

    @ApiModelProperty(name = "tankCapacity", value = "油桶容量", dataType = "Double", example = "1012.00")
    private Double tankCapacity;

    @ApiModelProperty(name = "remainAmount", value = "油桶余量", dataType = "Double", example = "200.00")
    private Double remainAmount;

}
