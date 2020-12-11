package com.iiot.business.bo;


import com.iiot.common.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XtCustomerCarBo {

    /**
     * id
     */
    private Long id;

    /**
     * 联系人
     */
    @Excel(name = "联系人")
    private String contactPerson;

    /**
     * 公司名称
     */
    @Excel(name = "公司名称")
    private String companyName;

    /**
     * 电话
     */
    @Excel(name = "电话")
    private String phone;

    /**
     * 地址
     */
    @Excel(name = "地址")
    private String address;

    /**
     * 客户名称
     */
    @Excel(name = "客户名称")
    private String customerName;


    /**
     * 品牌
     */
    @Excel(name = "品牌")
    private String brandSeries;

    /**
     * 设备编号
     */
    @Excel(name = "设备编号")
    private String devCode;

    /**
     * 客户id
     */
    @Excel(name = "客户id")
    private Long customerId;

    /**
     * 车牌号
     */
    @Excel(name = "车牌号")
    private String licensePlate;

    /** 行驶公里 */
    @Excel(name = "行驶公里")
    private String carKilometre;



}
