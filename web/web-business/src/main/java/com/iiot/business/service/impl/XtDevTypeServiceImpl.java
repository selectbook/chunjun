package com.iiot.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iiot.business.mapper.XtDevTypeMapper;
import com.iiot.business.domain.XtDevType;
import com.iiot.business.service.IXtDevTypeService;
import com.iiot.common.core.text.Convert;

/**
 * 类型Service业务层处理
 * 
 * @author desom
 * @date 2020-06-30
 */
@Service
public class XtDevTypeServiceImpl implements IXtDevTypeService 
{
    @Autowired
    private XtDevTypeMapper xtDevTypeMapper;

    /**
     * 查询类型
     * 
     * @param devTypeId 类型ID
     * @return 类型
     */
    @Override
    public XtDevType selectXtDevTypeById(Integer devTypeId)
    {
        return xtDevTypeMapper.selectXtDevTypeById(devTypeId);
    }

    /**
     * 查询类型列表
     * 
     * @param xtDevType 类型
     * @return 类型
     */
    @Override
    public List<XtDevType> selectXtDevTypeList(XtDevType xtDevType)
    {
        return xtDevTypeMapper.selectXtDevTypeList(xtDevType);
    }

    /**
     * 新增类型
     * 
     * @param xtDevType 类型
     * @return 结果
     */
    @Override
    public int insertXtDevType(XtDevType xtDevType)
    {
        return xtDevTypeMapper.insertXtDevType(xtDevType);
    }

    /**
     * 修改类型
     * 
     * @param xtDevType 类型
     * @return 结果
     */
    @Override
    public int updateXtDevType(XtDevType xtDevType)
    {
        return xtDevTypeMapper.updateXtDevType(xtDevType);
    }

    /**
     * 删除类型对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXtDevTypeByIds(String ids)
    {
        return xtDevTypeMapper.deleteXtDevTypeByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除类型信息
     * 
     * @param devTypeId 类型ID
     * @return 结果
     */
    @Override
    public int deleteXtDevTypeById(Integer devTypeId)
    {
        return xtDevTypeMapper.deleteXtDevTypeById(devTypeId);
    }
}
