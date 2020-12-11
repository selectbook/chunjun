package com.iiot.business.mapper;

import com.iiot.business.domain.XtDevicePosition;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

/**
 * 设备定位字段Mapper接口
 *
 * @author desom
 * @date 2020-05-16
 */
public interface XtDevicePositionMapper {
    /**
     * 查询设备定位字段
     *
     * @param id 设备定位字段ID
     * @return 设备定位字段
     */
    public XtDevicePosition selectXtDevicePositionById(Long id);

    /**
     * 查询设备定位字段列表
     *
     * @param xtDevicePosition 设备定位字段
     * @return 设备定位字段集合
     */
    public List<XtDevicePosition> selectXtDevicePositionList(XtDevicePosition xtDevicePosition);

    /**
     * 新增设备定位字段
     *
     * @param xtDevicePosition 设备定位字段
     * @return 结果
     */
    public int insertXtDevicePosition(XtDevicePosition xtDevicePosition);

    /**
     * 修改设备定位字段
     *
     * @param xtDevicePosition 设备定位字段
     * @return 结果
     */
    public int updateXtDevicePosition(XtDevicePosition xtDevicePosition);

    /**
     * 删除设备定位字段
     *
     * @param id 设备定位字段ID
     * @return 结果
     */
    public int deleteXtDevicePositionById(Long id);

    /**
     * 批量删除设备定位字段
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXtDevicePositionByIds(String[] ids);

    /**
     * 根据设备编号和定位时间统计设备定位
     *
     * @param devCode 设备编号
     * @param gpsTime GPS定位时间
     * @return 结果
     */
    int selectPositionCount(@Param("devCode") String devCode,
                            @Param("gpsTime") Date gpsTime);

    /**
     * 批量新增设备定位字段
     *
     * @param devicePositionList 设备定位字段列表
     * @return 结果
     */
    int insertDevicePositionBatch(@Param("devicePositionList") List<XtDevicePosition> devicePositionList);

    /**
     *  根据部门查询加注记录
     * @param deptId
     * @return
     */
    public List<XtDevicePosition> selectPositionByDevCodes(Long deptId);

    /**
     * 分组后获取最新的定位
     * @return
     */
    public List<XtDevicePosition> selectXtDevicePositionGroupByCode(@Param("deptId") Long deptId,@Param("deviceCode")String devCode);

    /**
     * 分组后回获取最新的定位
     * @return
     */
    public List<XtDevicePosition> selectXtDevicePositionGroupAll(XtDevicePosition xtDevicePosition);
}
