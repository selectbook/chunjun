package com.iiot.business.mapper;

import com.iiot.business.bo.XtDeviceBoExport;
import com.iiot.business.domain.XtDevice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备字段Mapper接口
 *
 * @author desom
 * @date 2020-05-15
 */
public interface XtDeviceMapper {
    /**
     * 查询设备字段
     *
     * @param id 设备字段ID
     * @return 设备字段
     */
    public XtDevice selectXtDeviceById(Long id);

    /**
     * 查询设备字段列表
     *
     * @param xtDeviceBoExport 设备字段
     * @return 设备字段集合
     */
    public List<XtDevice> selectXtDeviceList(XtDeviceBoExport xtDeviceBoExport);

    /**
     * 新增设备字段
     *
     * @param xtDevice 设备字段
     * @return 结果
     */
    public int insertXtDevice(XtDevice xtDevice);

    /**
     * 修改设备字段
     *
     * @param xtDevice 设备字段
     * @return 结果
     */
    public int updateXtDevice(XtDevice xtDevice);

    /**
     * 删除设备字段
     *
     * @param id 设备字段ID
     * @return 结果
     */
    public int deleteXtDeviceById(Long id);

    /**
     * 批量删除设备字段
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXtDeviceByIds(String[] ids);


    /**
     * 根据部门编号查询所有设备
     * @param deptId 设备
     * @param size   显示数
     * @return 结果
     */
    public List<XtDevice> selectXtDeviceByDeptId(@Param("deptId") Long deptId,@Param("size") Integer size);


    /**
     * 更换油桶进行绑定
     * @param xtDevice 设备
     * @return 返回受影响的行数
     */
    public int updateXtDeviceById(XtDevice xtDevice);



    /**
     * 根据分组id统计所在分组下的个数
     * @param devGroupId 分组id
     * @return
     */
    public int selectCountByGroupId(Long devGroupId);

    /**
     * 根据设备状态查询出设备总数
     * @param status
     * @return
     */
    public int selectDevicesByStatus(String status);


    /**
     * 查询设备字段列表
     *
     * @param xtDevice 设备字段
     * @return 设备字段集合
     */
    public List<XtDevice> selectXtDeviceListAll(XtDevice xtDevice);

    /**
     * 查询设备字段
     *
     * @param devCode 设备编号
     * @return 设备字段
     */
    public XtDevice selectXtDeviceByDevCode(String devCode);


}
