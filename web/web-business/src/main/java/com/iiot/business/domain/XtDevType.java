package com.iiot.business.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.iiot.common.annotation.Excel;
import com.iiot.common.core.domain.BaseEntity;

/**
 * 类型对象 xt_dev_type
 * 
 * @author desom
 * @date 2020-06-30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XtDevType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 类型编号 */
    private Integer devTypeId;

    /** 类型名称 */
    @Excel(name = "类型名称")
    private String devType;


}
