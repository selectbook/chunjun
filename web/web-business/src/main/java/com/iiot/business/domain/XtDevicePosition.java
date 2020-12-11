package com.iiot.business.domain;

import com.iiot.common.annotation.Excel;
import com.iiot.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 设备定位字段对象 xt_device_position
 *
 * @author desom
 * @date 2020-05-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XtDevicePosition extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private Long id;

    /**
     * 设备编号
     */
    @Excel(name = "设备编号")
    private String deviceCode;

    /**
     * 设备IP地址
     */
    @Excel(name = "设备IP地址")
    private String ipaddr;

    /**
     * 定位纬度
     */
    @Excel(name = "定位纬度")
    private String latitude;

    /**
     * 定位经度
     */
    @Excel(name = "定位经度")
    private String longitude;

    /**
     * 定位时间
     */
    @Excel(name = "定位时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date gpsTime;

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
