package com.iiot.common.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * REST 返回数据封装
 */
@Data
@ApiModel(value = "响应数据", description = "响应信息")
public class RestResp<T> {

    @ApiModelProperty(value = "响应码")
    private int code;

    @ApiModelProperty(value = "响应信息")
    private String msg;

    @ApiModelProperty(value = "响应数据")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public RestResp() {
        this.code = ApiCode.SUCCESS.getCode();
        this.msg = ApiCode.SUCCESS.getMsg();
    }

    public static RestResp success() {
        return new RestResp();
    }

    public static RestResp success(Object data) {
        RestResp restResp = new RestResp();
        restResp.setData(data);
        return restResp;
    }

    public static RestResp success(String msg) {
        RestResp restResp = new RestResp();
        restResp.setMsg(msg);
        return restResp;
    }

    public static RestResp success(String msg, Object data) {
        RestResp restResp = new RestResp();
        restResp.setMsg(msg);
        restResp.setData(data);
        return restResp;
    }

    public static RestResp success(Map<String, Object> map) {
        RestResp restResp = new RestResp();
        restResp.setData(map);
        return restResp;
    }

    public static RestResp success(String msg, Map<String, Object> map) {
        RestResp restResp = new RestResp();
        restResp.setMsg(msg);
        restResp.setData(map);
        return restResp;
    }

    public static RestResp object(int code, String msg, Object data) {
        RestResp restResp = new RestResp();
        restResp.setCode(code);
        restResp.setMsg(msg);
        restResp.setData(data);
        return restResp;
    }

    public static RestResp error() {
        return error(ApiCode.FAIL.getCode(), ApiCode.FAIL.getMsg());
    }

    public static RestResp error(String msg) {
        return error(ApiCode.FAIL.getCode(), msg);
    }

    public static RestResp error(int code, String msg) {
        RestResp restResp = new RestResp();
        restResp.setCode(code);
        restResp.setMsg(msg);
        return restResp;
    }
}
