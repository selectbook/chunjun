package com.iiot.common.core.domain;

public enum ApiCode {

    SUCCESS(0, "操作成功"),

    UNAUTHORIZED(401, "非法访问"),

    NOT_PERMISSION(403, "没有权限"),

    NOT_FOUND(404, "你请求的路径不存在"),

    TOKEN_FAIL(-1, "Token失效"),

    PARAM_FORMAT_ERROR(10, "参数格式错误"),

    PARAM_EXC(11, "参数校验异常"),

    FORM_VARIABLE_MISSING(20, "表单变量缺失"),

    DB_ERROR(30, "数据库错误"),

    DB_LOOKUP_FAILED(40, "数据库查找失败"),

    INTERNAL_SERVLET_ERROR(50, "内部SERVLET错误"),

    FAIL(55, "操作错误"),

    LOGIN_EXC(60, "登录异常");

    private final int code;
    private final String msg;

    ApiCode(final int code, final String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ApiCode getApiCode(int code) {
        ApiCode[] ecs = ApiCode.values();
        for (ApiCode ec : ecs) {
            if (ec.getCode() == code) {
                return ec;
            }
        }
        return SUCCESS;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
