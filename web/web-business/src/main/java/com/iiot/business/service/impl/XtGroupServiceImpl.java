package com.iiot.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iiot.business.mapper.XtGroupMapper;
import com.iiot.business.domain.XtGroup;
import com.iiot.business.service.IXtGroupService;
import com.iiot.common.core.text.Convert;

/**
 * 分组Service业务层处理
 * 
 * @author desom
 * @date 2020-06-24
 */
@Service
public class XtGroupServiceImpl implements IXtGroupService 
{
    @Autowired
    private XtGroupMapper xtGroupMapper;

    /**
     * 查询分组
     * 
     * @param id 分组ID
     * @return 分组
     */
    @Override
    public XtGroup selectXtGroupById(Long id)
    {
        return xtGroupMapper.selectXtGroupById(id);
    }

    /**
     * 查询分组列表
     * 
     * @param xtGroup 分组
     * @return 分组
     */
    @Override
    public List<XtGroup> selectXtGroupList(XtGroup xtGroup)
    {
        return xtGroupMapper.selectXtGroupList(xtGroup);
    }

    /**
     * 新增分组
     * 
     * @param xtGroup 分组
     * @return 结果
     */
    @Override
    public int insertXtGroup(XtGroup xtGroup)
    {
        return xtGroupMapper.insertXtGroup(xtGroup);
    }

    /**
     * 修改分组
     * 
     * @param xtGroup 分组
     * @return 结果
     */
    @Override
    public int updateXtGroup(XtGroup xtGroup)
    {
        return xtGroupMapper.updateXtGroup(xtGroup);
    }

    /**
     * 删除分组对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXtGroupByIds(String ids)
    {
        return xtGroupMapper.deleteXtGroupByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除分组信息
     * 
     * @param id 分组ID
     * @return 结果
     */
    @Override
    public int deleteXtGroupById(Long id)
    {
        return xtGroupMapper.deleteXtGroupById(id);
    }


}
