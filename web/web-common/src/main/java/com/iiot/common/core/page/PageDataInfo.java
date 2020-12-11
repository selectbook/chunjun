package com.iiot.common.core.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.iiot.common.core.domain.ApiCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象
 */
@ApiModel(value = "响应数据", description = "响应信息")
public class PageDataInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息状态码
     */
    @ApiModelProperty(value = "响应码")
    private int code;

    @ApiModelProperty(value = "响应信息")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String msg;

    /**
     * 总记录数
     */
    @ApiModelProperty(value = "总记录数")
    private Long total;

    /**
     * 列表数据
     */
    @ApiModelProperty(value = "列表数据")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T rows;

    public PageDataInfo() {
        super();
    }

    public PageDataInfo(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public PageDataInfo(int code, String msg, Long total, List<?> rows) {
        super();
        this.code = code;
        this.msg = msg;
        this.total = total;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public T getRows() {
        return rows;
    }

    public void setRows(T rows) {
        this.rows = rows;
    }

    public static PageDataInfo success() {
        PageDataInfo resultData = new PageDataInfo();
        resultData.setCode(ApiCode.SUCCESS.getCode());
        resultData.setMsg(ApiCode.SUCCESS.getMsg());
        return resultData;
    }

    public static PageDataInfo success(Object rows) {
        PageDataInfo resultData = new PageDataInfo();
        resultData.setCode(ApiCode.SUCCESS.getCode());
        resultData.setMsg(ApiCode.SUCCESS.getMsg());
        resultData.setRows(rows);
        return resultData;
    }

    public static PageDataInfo error() {
        return error(ApiCode.FAIL.getCode(), ApiCode.FAIL.getMsg());
    }

    public static PageDataInfo error(String msg) {
        return error(ApiCode.FAIL.getCode(), msg);
    }

    public static PageDataInfo error(int code, String msg) {
        PageDataInfo resultData = new PageDataInfo();
        resultData.setCode(code);
        resultData.setMsg(msg);
        return resultData;
    }
}
