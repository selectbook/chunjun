package com.iiot.business.service.impl;

import com.iiot.business.bo.XtOilFillBoExport;
import com.iiot.business.domain.XtOilFill;
import com.iiot.business.mapper.XtOilFillMapper;
import com.iiot.business.service.IXtOilFillService;
import com.iiot.business.vo.OilFillDeptVo;
import com.iiot.common.annotation.DataScope;
import com.iiot.common.core.text.Convert;
import com.iiot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 机油加注Service业务层处理
 *
 * @author desom
 * @date 2020-05-16
 */
@Service
public class XtOilFillServiceImpl implements IXtOilFillService {
    @Autowired
    private XtOilFillMapper xtOilFillMapper;

    /**
     * 查询机油加注
     *
     * @param id 机油加注ID
     * @return 机油加注
     */
    @Override
    public XtOilFill selectXtOilFillById(Long id) {
        return xtOilFillMapper.selectXtOilFillById(id);
    }

    /**
     * 查询机油加注列表
     *
     * @param xtOilFillBoExport 机油加注
     * @return 机油加注
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<XtOilFill> selectXtOilFillList(XtOilFillBoExport xtOilFillBoExport) {
        return xtOilFillMapper.selectXtOilFillList(xtOilFillBoExport);
    }

    /**
     * 新增机油加注
     *
     * @param xtOilFill 机油加注
     * @return 结果
     */
    @Override
    public int insertXtOilFill(XtOilFill xtOilFill) {
        xtOilFill.setCreateTime(DateUtils.getNowDate());
        return xtOilFillMapper.insertXtOilFill(xtOilFill);
    }

    /**
     * 修改机油加注
     *
     * @param xtOilFill 机油加注
     * @return 结果
     */
    @Override
    public int updateXtOilFill(XtOilFill xtOilFill) {
        return xtOilFillMapper.updateXtOilFill(xtOilFill);
    }

    /**
     * 删除机油加注对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXtOilFillByIds(String ids) {
        return xtOilFillMapper.deleteXtOilFillByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除机油加注信息
     *
     * @param id 机油加注ID
     * @return 结果
     */
    @Override
    public int deleteXtOilFillById(Long id) {
        return xtOilFillMapper.deleteXtOilFillById(id);
    }

    /**
     * 根据设备编号分组查询列表
     *
     * @param startTime 起始时间 格式：yyyy-MM-dd HH:mm:ss
     * @param overTime  截止时间 格式：yyyy-MM-dd HH:mm:ss
     * @return 结果
     */
    @Override
    public List<XtOilFill> selectListGroupByDevCode(String startTime, String overTime) {
        return xtOilFillMapper.selectListGroupByDevCode(startTime, overTime);
    }

    /**
     * 查询加注记录
     *
     * @param devCode 油桶编号
     * @return
     */
    @Override
    public List<XtOilFill> selectXtOilFillByDevCode(String devCode) {
        return xtOilFillMapper.selectXtOilFillByDevCode(devCode);
    }

    /**
     * 根据多个设备编号查询加注记录
     *
     * @param deptId
     * @return
     */
    @Override
    public List<XtOilFill> selectXtOilFillByDevCodes(Long deptId, String startTime, String endTime) {
        return xtOilFillMapper.selectXtOilFillByDevCodes(deptId, startTime, endTime);
    }

    /**
     * 根据多个设备编号查询加注记录
     *
     * @param deptId
     * @return
     */
    @Override
    public List<OilFillDeptVo> selectXtOilFillByDevCodesAll(Long deptId, String devCode, String startTime, String endTime) {
        return xtOilFillMapper.selectXtOilFillByDevCodesAll(deptId, devCode, startTime, endTime);
    }


    /**
     * 查询机油加注列表
     *
     * @param xtOilFillBoExport 机油加注
     * @return 机油加注集合
     */
    public List<OilFillDeptVo> selectXtOilFillListAll(XtOilFillBoExport xtOilFillBoExport) {
        return xtOilFillMapper.selectXtOilFillListAll(xtOilFillBoExport);
    }

    /**
     * 根据区域和时间查询加注总量
     *
     * @param area
     * @param timeType
     * @return
     */
    public Double selectFillDataByTimeAndArea(String area, String timeType) {
        return xtOilFillMapper.selectFillDataByTimeAndArea(area, timeType);
    }

    /**
     * 根据区域获得加注总量 （区域润滑油用量）
     *
     * @param area
     * @return
     */
    public Double selectFillDataByArea(String area) {
        return xtOilFillMapper.selectFillDataByArea(area);
    }

    /**
     * 油品用量
     *
     * @param area
     * @param timeType
     * @return
     */
    public List<Map<String, Object>> fillDataByOilName(String area, String timeType) {
        return xtOilFillMapper.fillDataByOilName(area, timeType);
    }
}
