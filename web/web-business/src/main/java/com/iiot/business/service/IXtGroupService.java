package com.iiot.business.service;

import java.util.List;
import com.iiot.business.domain.XtGroup;

/**
 * 分组Service接口
 * 
 * @author desom
 * @date 2020-06-24
 */
public interface IXtGroupService 
{
    /**
     * 查询分组
     * 
     * @param id 分组ID
     * @return 分组
     */
    public XtGroup selectXtGroupById(Long id);

    /**
     * 查询分组列表
     * 
     * @param xtGroup 分组
     * @return 分组集合
     */
    public List<XtGroup> selectXtGroupList(XtGroup xtGroup);

    /**
     * 新增分组
     * 
     * @param xtGroup 分组
     * @return 结果
     */
    public int insertXtGroup(XtGroup xtGroup);

    /**
     * 修改分组
     * 
     * @param xtGroup 分组
     * @return 结果
     */
    public int updateXtGroup(XtGroup xtGroup);

    /**
     * 批量删除分组
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXtGroupByIds(String ids);

    /**
     * 删除分组信息
     * 
     * @param id 分组ID
     * @return 结果
     */
    public int deleteXtGroupById(Long id);




}
