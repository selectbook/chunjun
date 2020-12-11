package com.iiot.business.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.iiot.common.annotation.Excel;
import com.iiot.common.core.domain.BaseEntity;

/**
 * 分组对象 xt_group
 * 
 * @author desom
 * @date 2020-06-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XtGroup extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long id;

    /** 分组名称 */
    @Excel(name = "分组名称")
    private String devGroup;


}
