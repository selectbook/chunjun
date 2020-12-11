package com.iiot.business.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "油桶绑定")
public class SwapDrumVo {

    @ApiModelProperty(name = "devCode", value = "设备id", required = true, dataType = "String", example = "XT365-000011")
    private String devCode;


    @ApiModelProperty(name = "oilCode", value = "油桶编号", required = true, dataType = "String", example = "28671")
    private String oilCode;


}
