package com.iiot.business.domain;

import com.iiot.common.annotation.Excel;
import com.iiot.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 看板对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XtKanban extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 区域加注总量
     */
    @Excel(name = "区域加注总量")
    private Double totalAmountOfRegion;

    /**
     * 剩余库存油桶
     */
    @Excel(name = "剩余库存油桶")
    private Integer restTankStock;

    /**
     * 消耗库存油桶
     */
    @Excel(name = "消耗库存油桶")
    private Integer usedTankStock;


}
