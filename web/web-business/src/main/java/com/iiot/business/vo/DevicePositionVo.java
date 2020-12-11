package com.iiot.business.vo;

import lombok.Data;
import java.util.Date;

@Data
public class DevicePositionVo {


    /**
     * 编号
     */
    private Long id;

    /**
     * 设备编号
     */
    private String devCode;

    /**
     * 设备IP地址
     */
    private String ipaddr;

    /**
     * 定位纬度
     */
    private String latitude;

    /**
     * 定位经度
     */
    private String longitude;

    /**
     * 定位时间
     */
    private Date gpsTime;

    /**
     * 所在省份
     */
    private String province;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 详细地址
     */
    private String address;


    /**
     * 部门id
     */
    private Long deptId;
}
