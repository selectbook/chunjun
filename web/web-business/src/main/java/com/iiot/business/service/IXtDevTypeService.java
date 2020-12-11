package com.iiot.business.service;

import java.util.List;
import com.iiot.business.domain.XtDevType;

/**
 * 类型Service接口
 * 
 * @author desom
 * @date 2020-06-30
 */
public interface IXtDevTypeService 
{
    /**
     * 查询类型
     * 
     * @param devTypeId 类型ID
     * @return 类型
     */
    public XtDevType selectXtDevTypeById(Integer devTypeId);

    /**
     * 查询类型列表
     * 
     * @param xtDevType 类型
     * @return 类型集合
     */
    public List<XtDevType> selectXtDevTypeList(XtDevType xtDevType);

    /**
     * 新增类型
     * 
     * @param xtDevType 类型
     * @return 结果
     */
    public int insertXtDevType(XtDevType xtDevType);

    /**
     * 修改类型
     * 
     * @param xtDevType 类型
     * @return 结果
     */
    public int updateXtDevType(XtDevType xtDevType);

    /**
     * 批量删除类型
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXtDevTypeByIds(String ids);

    /**
     * 删除类型信息
     * 
     * @param devTypeId 类型ID
     * @return 结果
     */
    public int deleteXtDevTypeById(Integer devTypeId);
}
