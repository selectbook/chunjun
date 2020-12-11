package com.iiot.business.vo;

import lombok.Data;

@Data
public class analysisVo {

    /**
     * 时间类型 日 月 年
     */
    private String timeType;

    /**
     * 地区类型 华北 华东 华南 西南 西北
     */
    private String area;

    /**
     * 壳牌 长城 统一 昆仑 美孚
     */
    private String brand;
}
