package com.iiot.business.service.impl;


import com.iiot.business.bo.XtDeviceBoExport;
import com.iiot.business.domain.XtDevice;
import com.iiot.business.domain.XtOilDrum;
import com.iiot.business.mapper.XtDeviceMapper;
import com.iiot.business.service.IXtDeviceService;
import com.iiot.business.service.IXtOilDrumService;
import com.iiot.business.vo.DevicesVo;
import com.iiot.common.annotation.DataScope;
import com.iiot.common.core.text.Convert;
import com.iiot.common.utils.DateUtils;
import com.iiot.framework.util.ShiroUtils;
import com.iiot.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 设备字段Service业务层处理
 *
 * @author desom
 * @date 2020-05-15
 */
@Service
public class XtDeviceServiceImpl implements IXtDeviceService {

    @Autowired
    private XtDeviceMapper xtDeviceMapper;

    @Autowired
    private IXtOilDrumService xtOilDrumService;


    /**
     * 查询设备字段
     *
     * @param id 设备字段ID
     * @return 设备字段
     */
    @Override
    public XtDevice selectXtDeviceById(Long id) {
        return xtDeviceMapper.selectXtDeviceById(id);
    }

    /**
     * 查询设备字段列表
     *
     * @param xtDevice 设备字段
     * @return 设备字段
     */
    @Override
    @DataScope(deptAlias = "a")
    public List<XtDevice> selectXtDeviceList(XtDeviceBoExport xtDevice) {
        return xtDeviceMapper.selectXtDeviceList(xtDevice);
    }

    /**
     * 新增设备字段
     *
     * @param xtDevice 设备字段
     * @return 结果
     */
    @Override
    public int insertXtDevice(XtDevice xtDevice) {
        xtDevice.setCreateTime(DateUtils.getNowDate());
        return xtDeviceMapper.insertXtDevice(xtDevice);
    }

    /**
     * 修改设备字段
     *
     * @param xtDevice 设备字段
     * @return 结果
     */
    @Override
    public int updateXtDevice(XtDevice xtDevice) {
        xtDevice.setUpdateTime(DateUtils.getNowDate());
        return xtDeviceMapper.updateXtDevice(xtDevice);
    }

    /**
     * 删除设备字段对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXtDeviceByIds(String ids) {
        return xtDeviceMapper.deleteXtDeviceByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除设备字段信息
     *
     * @param id 设备字段ID
     * @return 结果
     */
    @Override
    public int deleteXtDeviceById(Long id) {
        return xtDeviceMapper.deleteXtDeviceById(id);
    }

    /**
     * 根据部门编号查询所有设备
     *
     * @param roleId 设备
     * @param size   显示数
     * @return 结果
     */
    @Override
    public List<XtDevice> selectXtDeviceByDeptId(Long roleId, Integer size) {
        //  设置分页规则
        SysUser currentUser = ShiroUtils.getSysUser();
        if (currentUser != null) {
            // 如果是超级管理员，则不过滤数据
            if (currentUser.isAdmin() || roleId == 1) {
                return xtDeviceMapper.selectXtDeviceByDeptId(null, size);
            }
        }
        return xtDeviceMapper.selectXtDeviceByDeptId(roleId, size);
    }

    /**
     * 更换油桶进行绑定
     *
     * @param xtDevice 设备
     * @return 返回受影响的行数
     */
    @Override
    public int updateXtDeviceById(XtDevice xtDevice) {
        xtDevice.setUpdateTime(new Date());
        return xtDeviceMapper.updateXtDeviceById(xtDevice);
    }


    /**
     * 根据分组id统计所在分组下的个数
     *
     * @param devGroupId 分组id
     * @return
     */
    @Override
    public int selectCountByGroupId(Long devGroupId) {
        return xtDeviceMapper.selectCountByGroupId(devGroupId);
    }


    /**
     * 根据设备信息查询绑定的油桶信息
     *
     * @param xtDevices 设备信息
     * @return
     */
    @Override
    public List<DevicesVo> selectXtDevicesVo(List<XtDevice> xtDevices) {
        List<DevicesVo> devicesVos = new ArrayList<>();
        //返回对象
        for (XtDevice xtDevice : xtDevices) {
            XtOilDrum xtOilDrum = xtOilDrumService.selectXtOilDrumByOilCode(xtDevice.getOilCode());
            DevicesVo devicesVo = new DevicesVo();
            devicesVo.setId(xtDevice.getId());
            devicesVo.setDeptId(xtDevice.getDeptId());
            devicesVo.setDevName(xtDevice.getDevName());
            devicesVo.setDevCode(xtDevice.getDevCode());
            devicesVo.setDevDesc(xtDevice.getDevDesc());
            devicesVo.setStatus(xtDevice.getStatus());
            devicesVo.setOilCode(xtDevice.getOilCode());
            if (xtOilDrum != null) {
                devicesVo.setTankCapacity(xtOilDrum.getTankCapacity());
                devicesVo.setRemainAmount(xtOilDrum.getRemainAmount());
            }
            devicesVos.add(devicesVo);
        }
        return devicesVos;
    }

    /**
     * 根据设备状态查询出设备总数
     *
     * @param status
     * @return
     */
    @Override
    public int selectDevicesByStatus(String status) {
        return xtDeviceMapper.selectDevicesByStatus(status);
    }


    /**
     * 查询设备字段列表
     *
     * @param xtDevice 设备字段
     * @return 设备字段集合
     */
    @Override
    public List<XtDevice> selectXtDeviceListAll(XtDevice xtDevice) {
        return xtDeviceMapper.selectXtDeviceListAll(xtDevice);
    }

    /**
     * 查询设备字段
     *
     * @param devCode 设备编号
     * @return 设备字段
     */
    @Override
    public XtDevice selectXtDeviceByDevCode(String devCode) {
        return xtDeviceMapper.selectXtDeviceByDevCode(devCode);
    }
}
