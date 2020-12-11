package com.iiot.business.service.impl;

import com.iiot.business.domain.XtCarUpkeep;
import com.iiot.business.mapper.XtCarUpkeepMapper;
import com.iiot.business.service.IXtCarUpkeepService;
import com.iiot.common.core.text.Convert;
import com.iiot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 车型机油保养字段Service业务层处理
 *
 * @author desom
 * @date 2020-05-15
 */
@Service
public class XtCarUpkeepServiceImpl implements IXtCarUpkeepService {
    @Autowired
    private XtCarUpkeepMapper xtCarUpkeepMapper;

    /**
     * 查询车型机油保养字段
     *
     * @param id 车型机油保养字段ID
     * @return 车型机油保养字段
     */
    @Override
    public XtCarUpkeep selectXtCarUpkeepById(Long id) {
        return xtCarUpkeepMapper.selectXtCarUpkeepById(id);
    }

    /**
     * 查询车型机油保养字段列表
     *
     * @param xtCarUpkeep 车型机油保养字段
     * @return 车型机油保养字段
     */
    @Override
    public List<XtCarUpkeep> selectXtCarUpkeepList(XtCarUpkeep xtCarUpkeep) {
        return xtCarUpkeepMapper.selectXtCarUpkeepList(xtCarUpkeep);
    }

    /**
     * 新增车型机油保养字段
     *
     * @param xtCarUpkeep 车型机油保养字段
     * @return 结果
     */
    @Override
    public int insertXtCarUpkeep(XtCarUpkeep xtCarUpkeep) {
        xtCarUpkeep.setCreateTime(DateUtils.getNowDate());
        return xtCarUpkeepMapper.insertXtCarUpkeep(xtCarUpkeep);
    }

    /**
     * 修改车型机油保养字段
     *
     * @param xtCarUpkeep 车型机油保养字段
     * @return 结果
     */
    @Override
    public int updateXtCarUpkeep(XtCarUpkeep xtCarUpkeep) {
        xtCarUpkeep.setUpdateTime(DateUtils.getNowDate());
        return xtCarUpkeepMapper.updateXtCarUpkeep(xtCarUpkeep);
    }

    /**
     * 删除车型机油保养字段对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXtCarUpkeepByIds(String ids) {
        return xtCarUpkeepMapper.deleteXtCarUpkeepByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除车型机油保养字段信息
     *
     * @param id 车型机油保养字段ID
     * @return 结果
     */
    @Override
    public int deleteXtCarUpkeepById(Long id) {
        return xtCarUpkeepMapper.deleteXtCarUpkeepById(id);
    }


    /**
     * 查询分组后的车
     *
     * @return
     */
    @Override
    public List<XtCarUpkeep> selectXtCarUpkeepGroup() {
        return xtCarUpkeepMapper.selectXtCarUpkeepGroup();
    }

    /**
     * 下拉框按品牌查询
     *
     * @param brand
     * @return
     */
    @Override
    public List<XtCarUpkeep> selectOptionXtCarUpKeepGroupByBrand(String brand) {
        return xtCarUpkeepMapper.selectOptionXtCarUpKeepGroupByBrand(brand);
    }

    /**
     * 下拉框按车系id查询
     *
     * @param seriesId
     * @return
     */
    @Override
    public List<XtCarUpkeep> selectOptionXtCarUpKeepGroupBySeriesId(Long seriesId) {
        return xtCarUpkeepMapper.selectOptionXtCarUpKeepGroupBySeriesId(seriesId);
    }

    /**
     * 下拉框按年款id查询
     *
     * @param annualId
     * @return
     */
    public List<XtCarUpkeep> selectOptionXtCarUpKeepGroupByAnnualId(Long annualId) {
        return xtCarUpkeepMapper.selectOptionXtCarUpKeepGroupByAnnualId(annualId);
    }
}
