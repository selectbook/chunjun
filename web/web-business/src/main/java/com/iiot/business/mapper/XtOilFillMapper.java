package com.iiot.business.mapper;

import com.iiot.business.bo.XtOilFillBoExport;
import com.iiot.business.domain.XtOilFill;
import com.iiot.business.vo.OilFillDeptVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 机油加注Mapper接口
 *
 * @author desom
 * @date 2020-05-16
 */
public interface XtOilFillMapper {
    /**
     * 查询机油加注
     *
     * @param id 机油加注ID
     * @return 机油加注
     */
    public XtOilFill selectXtOilFillById(Long id);

    /**
     * 查询机油加注列表
     *
     * @param xtOilFillBoExport 机油加注
     * @return 机油加注集合
     */
    public List<XtOilFill> selectXtOilFillList(XtOilFillBoExport xtOilFillBoExport);

    /**
     * 新增机油加注
     *
     * @param xtOilFill 机油加注
     * @return 结果
     */
    public int insertXtOilFill(XtOilFill xtOilFill);

    /**
     * 修改机油加注
     *
     * @param xtOilFill 机油加注
     * @return 结果
     */
    public int updateXtOilFill(XtOilFill xtOilFill);

    /**
     * 删除机油加注
     *
     * @param id 机油加注ID
     * @return 结果
     */
    public int deleteXtOilFillById(Long id);

    /**
     * 批量删除机油加注
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXtOilFillByIds(String[] ids);

    /**
     * 根据设备编号分组查询列表
     *
     * @param startTime 起始时间 格式：yyyy-MM-dd HH:mm:ss
     * @param overTime  截止时间 格式：yyyy-MM-dd HH:mm:ss
     * @return 结果
     */
    List<XtOilFill> selectListGroupByDevCode(@Param("startTime") String startTime,
                                             @Param("overTime") String overTime);


    /**
     * 查询加注记录
     *
     * @param devCode 油桶编号
     * @return
     */
    public List<XtOilFill> selectXtOilFillByDevCode(String devCode);


    /**
     * 根据多个设备编号查询加注记录
     *
     * @param deptId
     * @return
     */
    public List<XtOilFill> selectXtOilFillByDevCodes(@Param("deptId") Long deptId, @Param("startTime") String startTime, @Param("endTime") String endTime);


    /**
     * 根据多个设备编号查询加注记录
     *
     * @param deptId
     * @return
     */
    public List<OilFillDeptVo> selectXtOilFillByDevCodesAll(@Param("deptId") Long deptId, @Param("devCode") String devCode, @Param("startTime") String startTime, @Param("endTime") String endTime);


    /**
     * 查询机油加注列表
     *
     * @param xtOilFillBoExport 机油加注
     * @return 机油加注集合
     */
    public List<OilFillDeptVo> selectXtOilFillListAll(XtOilFillBoExport xtOilFillBoExport);


    /**
     * 根据区域和时间查询加注总量
     *
     * @param area
     * @param timeType
     * @return
     */
    public Double selectFillDataByTimeAndArea(@Param("area") String area, @Param("timeType") String timeType);

    /**
     * 根据区域获得加注总量 （区域润滑油用量）
     *
     * @param area
     * @return
     */
    public Double selectFillDataByArea(@Param("area") String area);

    /**
     * 油品用量
     *
     * @param area
     * @param timeType
     * @return
     */
    public List<Map<String, Object>> fillDataByOilName(@Param("area") String area, @Param("timeType") String timeType);

}
