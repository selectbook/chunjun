package com.iiot.business.domain;

import com.iiot.common.annotation.Excel;
import com.iiot.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 车型机油保养字段对象 xt_car_upkeep
 *
 * @author desom
 * @date 2020-05-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XtCarUpkeep extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private Long id;

    /**
     * 首字母
     */
    @Excel(name = "首字母")
    private String firstLetter;

    /**
     * 品牌
     */
    @Excel(name = "品牌")
    private String brand;

    /**
     * 车系
     */
    @Excel(name = "车系")
    private String series;

    /**
     * 车系id
     */
    @Excel(name = "车系id")
    private Long seriesId;

    /**
     * 厂商
     */
    @Excel(name = "厂商")
    private String vendor;

    /**
     * 年款
     */
    @Excel(name = "年款")
    private String annual;

    /**
     * 年款id
     */
    @Excel(name = "年款id")
    private Long annualId;

    /**
     * 车型
     */
    @Excel(name = "车型")
    private String models;

    /**
     * 车型id
     */
    @Excel(name = "车型id")
    private Long modelsId;

    /**
     * 用量
     */
    @Excel(name = "用量")
    private String dosage;

    /**
     * 建议
     */
    @Excel(name = "建议")
    private String advice;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

}
