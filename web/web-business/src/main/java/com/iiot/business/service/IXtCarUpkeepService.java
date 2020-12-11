package com.iiot.business.service;

import com.iiot.business.domain.XtCarUpkeep;

import java.util.List;

/**
 * 车型机油保养字段Service接口
 *
 * @author desom
 * @date 2020-05-15
 */
public interface IXtCarUpkeepService {
    /**
     * 查询车型机油保养字段
     *
     * @param id 车型机油保养字段ID
     * @return 车型机油保养字段
     */
    public XtCarUpkeep selectXtCarUpkeepById(Long id);

    /**
     * 查询车型机油保养字段列表
     *
     * @param xtCarUpkeep 车型机油保养字段
     * @return 车型机油保养字段集合
     */
    public List<XtCarUpkeep> selectXtCarUpkeepList(XtCarUpkeep xtCarUpkeep);

    /**
     * 新增车型机油保养字段
     *
     * @param xtCarUpkeep 车型机油保养字段
     * @return 结果
     */
    public int insertXtCarUpkeep(XtCarUpkeep xtCarUpkeep);

    /**
     * 修改车型机油保养字段
     *
     * @param xtCarUpkeep 车型机油保养字段
     * @return 结果
     */
    public int updateXtCarUpkeep(XtCarUpkeep xtCarUpkeep);

    /**
     * 批量删除车型机油保养字段
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXtCarUpkeepByIds(String ids);

    /**
     * 删除车型机油保养字段信息
     *
     * @param id 车型机油保养字段ID
     * @return 结果
     */
    public int deleteXtCarUpkeepById(Long id);


    /**
     * 查询分组后的车
     *
     * @return
     */
    public List<XtCarUpkeep> selectXtCarUpkeepGroup();


    /**
     * 下拉框按品牌查询
     * @param brand
     * @return
     */
    public List<XtCarUpkeep> selectOptionXtCarUpKeepGroupByBrand(String brand);

    /**
     * 下拉框按车系id查询
     * @param seriesId
     * @return
     */
    public List<XtCarUpkeep> selectOptionXtCarUpKeepGroupBySeriesId(Long seriesId);


    /**
     * 下拉框按年款id查询
     * @param annualId
     * @return
     */
    public List<XtCarUpkeep> selectOptionXtCarUpKeepGroupByAnnualId(Long annualId);
}
