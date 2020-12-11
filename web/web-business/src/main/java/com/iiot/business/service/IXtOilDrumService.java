package com.iiot.business.service;

import com.iiot.business.bo.XtOilDrumBoExport;
import com.iiot.business.domain.XtOilDrum;

import java.util.List;

/**
 * 油桶字段Service接口
 *
 * @author desom
 * @date 2020-05-15
 */
public interface IXtOilDrumService {
    /**
     * 查询油桶字段
     *
     * @param id 油桶字段ID
     * @return 油桶字段
     */
    public XtOilDrum selectXtOilDrumById(Long id);

    /**
     * 查询油桶字段列表
     *
     * @param xtOilDrumBOExport 油桶字段
     * @return 油桶字段集合
     */
    public List<XtOilDrum> selectXtOilDrumList(XtOilDrumBoExport xtOilDrumBOExport);

    /**
     * 新增油桶字段
     *
     * @param xtOilDrum 油桶字段
     * @return 结果
     */
    public int insertXtOilDrum(XtOilDrum xtOilDrum);

    /**
     * 修改油桶字段
     *
     * @param xtOilDrum 油桶字段
     * @return 结果
     */
    public int updateXtOilDrum(XtOilDrum xtOilDrum);

    /**
     * 批量删除油桶字段
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXtOilDrumByIds(String ids);

    /**
     * 删除油桶字段信息
     *
     * @param id 油桶字段ID
     * @return 结果
     */
    public int deleteXtOilDrumById(Long id);


    /**
     * 根据油桶编号查询绑定的油桶
     * @param oilCode 油桶编号
     * @return
     */
    public XtOilDrum selectXtOilDrumByOilCode(String oilCode);

    /**
     * 根据润滑油库存类型（used rest）获得库存量
     * @param type
     * @return
     */
    public Integer selectDrumStock(String type);


    /**
     * 查询油桶字段列表
     *
     * @param xtOilDrum 油桶字段
     * @return 油桶字段集合
     */
    public List<XtOilDrum> selectXtOilDrumListAll(XtOilDrum xtOilDrum);


    /**
     * 修改油桶字段
     *
     * @param xtOilDrum 油桶字段
     * @return 结果
     */
    public int updateXtOilDrumByOilCode(XtOilDrum xtOilDrum);


    /**
     * 根据品牌名称查询出油桶个数
     * @param vendor
     * @return
     */
    public int selectCountDrumByVendor(String vendor);

    /**
     * 根据部门ID获得油桶信息
     * @param deptId
     * @return
     */
    public List<XtOilDrum> selectXtOilDrumByDeptId(Long deptId);
    /**
     * 根据部门id获得油桶品牌
     * @param deptId
     * @return
     */
    public List<String> selectXtDrumVendorByDeptId(Long deptId);

    /**
     * 获取所有品牌
     * @return
     */
    public List<String> selectXtDrumVendors();


    /**
     * 查询报警数据
     *
     * @return
     */
    public XtOilDrum selectAlarmDrum(XtOilDrum xtOilDrum);
}
