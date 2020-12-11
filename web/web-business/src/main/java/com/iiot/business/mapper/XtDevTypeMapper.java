package com.iiot.business.mapper;

import java.util.List;
import com.iiot.business.domain.XtDevType;

/**
 * 类型Mapper接口
 * 
 * @author desom
 * @date 2020-06-30
 */
public interface XtDevTypeMapper 
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
     * 删除类型
     * 
     * @param devTypeId 类型ID
     * @return 结果
     */
    public int deleteXtDevTypeById(Integer devTypeId);

    /**
     * 批量删除类型
     * 
     * @param devTypeIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteXtDevTypeByIds(String[] devTypeIds);
}
