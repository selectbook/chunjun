package com.iiot.business.service;

import com.iiot.business.domain.XtDevicePosition;
import com.iiot.business.vo.CsvPositionVo;
import com.iiot.business.vo.IpLocaltionVo;

import java.util.Date;
import java.util.List;

/**
 * 设备定位字段Service接口
 *
 * @author desom
 * @date 2020-05-16
 */
public interface IXtDevicePositionService {
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
     * 批量删除设备定位字段
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXtDevicePositionByIds(String ids);

    /**
     * 删除设备定位字段信息
     *
     * @param id 设备定位字段ID
     * @return 结果
     */
    public int deleteXtDevicePositionById(Long id);

    /**
     * 根据设备编号和定位时间统计设备定位
     *
     * @param devCode 设备编号
     * @param gpsTime GPS定位时间
     * @return 结果
     */
    int selectPositionCount(String devCode, Date gpsTime);

    /**
     * 根据IP获取定位信息
     *
     * @param ip ip
     * @return 结果
     */
    IpLocaltionVo getRealAddressByIP(String ip);

    /**
     * 批量新增设备定位字段
     *
     * @param devicePositionList 设备定位字段列表
     * @return 结果
     */
    int insertDevicePositionBatch(List<XtDevicePosition> devicePositionList);

    /**
     * 获取定位信息
     *
     * @param startTime 起始时间 格式：yyyy-MM-dd HH:mm:ss
     * @param overTime  截止时间 格式：yyyy-MM-dd HH:mm:ss
     */
    public void getPositionInfo(String startTime, String overTime);


    /**
     *  根据部门查询加注记录
     * @param roleId
     * @return
     */
    public List<XtDevicePosition> selectPositionByDevCodes(Long roleId);

    /**
     * 分组后获取最新的定位
     * @return
     */
    public List<CsvPositionVo> selectXtDevicePositionGroupByCode(Long deptId,String devCode);


    /**
     * 分组后回获取最新的定位
     * @return
     */
    public List<CsvPositionVo> selectXtDevicePositionGroupAll(XtDevicePosition xtDevicePosition);
}
