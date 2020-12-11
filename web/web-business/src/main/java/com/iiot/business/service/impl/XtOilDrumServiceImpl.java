package com.iiot.business.service.impl;

import com.iiot.business.bo.XtOilDrumBoExport;
import com.iiot.business.domain.XtOilDrum;
import com.iiot.business.mapper.XtOilDrumMapper;
import com.iiot.business.service.IXtOilDrumService;
import com.iiot.common.annotation.DataScope;
import com.iiot.common.core.text.Convert;
import com.iiot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 油桶字段Service业务层处理
 *
 * @author desom
 * @date 2020-05-15
 */
@Service
public class XtOilDrumServiceImpl implements IXtOilDrumService {
    @Autowired
    private XtOilDrumMapper xtOilDrumMapper;


    /**
     * 查询油桶字段
     *
     * @param id 油桶字段ID
     * @return 油桶字段
     */
    @Override
    public XtOilDrum selectXtOilDrumById(Long id) {
        return xtOilDrumMapper.selectXtOilDrumById(id);
    }

    /**
     * 查询油桶字段列表
     *
     * @param xtOilDrumBOExport 油桶字段
     * @return 油桶字段
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<XtOilDrum> selectXtOilDrumList(XtOilDrumBoExport xtOilDrumBOExport) {
        return xtOilDrumMapper.selectXtOilDrumList(xtOilDrumBOExport);
    }

    /**
     * 新增油桶字段
     *
     * @param xtOilDrum 油桶字段
     * @return 结果
     */
    @Override
    public int insertXtOilDrum(XtOilDrum xtOilDrum) {
        xtOilDrum.setCreateTime(DateUtils.getNowDate());
        return xtOilDrumMapper.insertXtOilDrum(xtOilDrum);
    }

    /**
     * 修改油桶字段
     *
     * @param xtOilDrum 油桶字段
     * @return 结果
     */
    @Override
    public int updateXtOilDrum(XtOilDrum xtOilDrum) {
        xtOilDrum.setUpdateTime(DateUtils.getNowDate());
        return xtOilDrumMapper.updateXtOilDrum(xtOilDrum);
    }

    /**
     * 删除油桶字段对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXtOilDrumByIds(String ids) {
        return xtOilDrumMapper.deleteXtOilDrumByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除油桶字段信息
     *
     * @param id 油桶字段ID
     * @return 结果
     */
    @Override
    public int deleteXtOilDrumById(Long id) {
        return xtOilDrumMapper.deleteXtOilDrumById(id);
    }


    /**
     * 根据油桶编号查询绑定的油桶
     *
     * @param oilCode 油桶编号
     * @return
     */
    @Override
    public XtOilDrum selectXtOilDrumByOilCode(String oilCode) {
        return xtOilDrumMapper.selectXtOilDrumByOilCode(oilCode);
    }

    /**
     * 根据润滑油库存类型（used rest）获得库存量
     *
     * @param type
     * @return
     */
    public Integer selectDrumStock(String type) {
        return xtOilDrumMapper.selectDrumStock(type);
    }


    /**
     * 查询油桶字段列表
     *
     * @param xtOilDrum 油桶字段
     * @return 油桶字段集合
     */
    public List<XtOilDrum> selectXtOilDrumListAll(XtOilDrum xtOilDrum) {
        return xtOilDrumMapper.selectXtOilDrumListAll(xtOilDrum);
    }


    /**
     * 修改油桶字段
     *
     * @param xtOilDrum 油桶字段
     * @return 结果
     */
    public int updateXtOilDrumByOilCode(XtOilDrum xtOilDrum) {
        return xtOilDrumMapper.updateXtOilDrumByOilCode(xtOilDrum);
    }


    @Override
    public int selectCountDrumByVendor(String vendor) {
        return xtOilDrumMapper.selectCountDrumByVendor(vendor);
    }


    /**
     * 根据部门ID获得油桶信息
     *
     * @param deptId
     * @return
     */
    @Override
    public List<XtOilDrum> selectXtOilDrumByDeptId(Long deptId) {
        return xtOilDrumMapper.selectXtOilDrumByDeptId(deptId);
    }

    /**
     * 根据部门id获得油桶品牌
     *
     * @param deptId
     * @return
     */
    @Override
    public List<String> selectXtDrumVendorByDeptId(Long deptId) {
        return xtOilDrumMapper.selectXtDrumVendorByDeptId(deptId);
    }


    /**
     * 获取所有品牌
     *
     * @return
     */
    public List<String> selectXtDrumVendors() {
        return selectXtDrumVendors();
    }


    /**
     * 查询报警数据
     *
     * @return
     */
    @Override
    public XtOilDrum selectAlarmDrum(XtOilDrum xtOilDrum) {
        return xtOilDrumMapper.selectAlarmDrum(xtOilDrum);
    }
}
