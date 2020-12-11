package com.iiot.business.vo;

import lombok.Data;

import java.util.Date;

/**
 * 百度地图云麻点
 */
@Data
public class CsvPositionVo {

    private String devCode;

    private String address;

    private String longitude;

    private String latitude;

    private String ipaddr;

    private Date gpsTime;

}
