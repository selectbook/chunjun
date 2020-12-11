package com.iiot.business.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "用户登录")
public class LoginVo {

    /**
     * 账号
     */
    @ApiModelProperty(name = "username", value = "账号", required = true, dataType = "String", example = "admin")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(name = "password", value = "密码", required = true, dataType = "String", example = "admin123")
    private String password;
}
