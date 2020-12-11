package com.iiot.business.service.impl;

import com.iiot.business.domain.XtDevicePosition;
import com.iiot.business.domain.XtOilFill;
import com.iiot.business.mapper.XtDevicePositionMapper;
import com.iiot.business.service.IXtDevicePositionService;
import com.iiot.business.service.IXtOilFillService;
import com.iiot.business.vo.CsvPositionVo;
import com.iiot.business.vo.IpLocaltionVo;
import com.iiot.common.annotation.DataScope;
import com.iiot.common.core.text.Convert;
import com.iiot.common.json.JSON;
import com.iiot.common.utils.AddressUtils;
import com.iiot.common.utils.DateUtils;
import com.iiot.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 设备定位字段Service业务层处理
 *
 * @author desom
 * @date 2020-05-16
 */
@Service
public class XtDevicePositionServiceImpl implements IXtDevicePositionService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private XtDevicePositionMapper xtDevicePositionMapper;

    @Autowired
    private IXtOilFillService xtOilFillService;

    /**
     * 查询设备定位字段
     *
     * @param id 设备定位字段ID
     * @return 设备定位字段
     */
    @Override
    public XtDevicePosition selectXtDevicePositionById(Long id) {
        return xtDevicePositionMapper.selectXtDevicePositionById(id);
    }

    /**
     * 查询设备定位字段列表
     *
     * @param xtDevicePosition 设备定位字段
     * @return 设备定位字段
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<XtDevicePosition> selectXtDevicePositionList(XtDevicePosition xtDevicePosition) {
        return xtDevicePositionMapper.selectXtDevicePositionList(xtDevicePosition);
    }

    /**
     * 新增设备定位字段
     *
     * @param xtDevicePosition 设备定位字段
     * @return 结果
     */
    @Override
    public int insertXtDevicePosition(XtDevicePosition xtDevicePosition) {
        xtDevicePosition.setCreateTime(DateUtils.getNowDate());
        return xtDevicePositionMapper.insertXtDevicePosition(xtDevicePosition);
    }

    /**
     * 修改设备定位字段
     *
     * @param xtDevicePosition 设备定位字段
     * @return 结果
     */
    @Override
    public int updateXtDevicePosition(XtDevicePosition xtDevicePosition) {
        xtDevicePosition.setUpdateTime(DateUtils.getNowDate());
        return xtDevicePositionMapper.updateXtDevicePosition(xtDevicePosition);
    }

    /**
     * 删除设备定位字段对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXtDevicePositionByIds(String ids) {
        return xtDevicePositionMapper.deleteXtDevicePositionByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除设备定位字段信息
     *
     * @param id 设备定位字段ID
     * @return 结果
     */
    @Override
    public int deleteXtDevicePositionById(Long id) {
        return xtDevicePositionMapper.deleteXtDevicePositionById(id);
    }

    /**
     * 根据设备编号和定位时间统计设备定位
     *
     * @param devCode 设备编号
     * @param gpsTime GPS定位时间
     * @return 结果
     */
    @Override
    public int selectPositionCount(String devCode, Date gpsTime) {
        return xtDevicePositionMapper.selectPositionCount(devCode, gpsTime);
    }

    /**
     * 根据IP获取定位信息
     *
     * @param ip ip
     * @return 结果
     */
    @Override
    public IpLocaltionVo getRealAddressByIP(String ip) {
        String rspStr = AddressUtils.getRealAddressByIP1(ip);
        if (StringUtils.isEmpty(rspStr)) {
            return null;
        }

        try {
            IpLocaltionVo ipLocaltionVo = JSON.unmarshal(rspStr, IpLocaltionVo.class);
            return ipLocaltionVo;
        } catch (Exception e) {
            logger.error("解析地理位置异常 {}", ip);
        }

        return null;
    }

    /**
     * 批量新增设备定位字段
     *
     * @param devicePositionList 设备定位字段列表
     * @return 结果
     */
    @Override
    public int insertDevicePositionBatch(List<XtDevicePosition> devicePositionList) {
        return xtDevicePositionMapper.insertDevicePositionBatch(devicePositionList);
    }

    /**
     * 获取定位信息
     *
     * @param startTime 起始时间 格式：yyyy-MM-dd HH
     * @param overTime  截止时间 格式：yyyy-MM-dd HH
     */
    // 这里进行标注为异步任务，在执行此方法的时候，会单独开启线程来执行(并指定线程池的名字)
    @Async("threadPoolTaskExecutor")
    @Override
    public void getPositionInfo(String startTime, String overTime) {
        // 查询加注表分组最新设备数据
        List<XtOilFill> oilFillList = xtOilFillService.selectListGroupByDevCode(startTime, overTime);
        if (oilFillList == null || oilFillList.size() == 0) return;

        List<XtDevicePosition> devicePositionList = new ArrayList<>();
        for (XtOilFill oilFill : oilFillList) {
            // 判断 devCode ipaddr gpsTime 不能为空
            if (StringUtils.isEmpty(oilFill.getDevCode()) || StringUtils.isEmpty(oilFill.getIpaddr())
                    || StringUtils.isNull(oilFill.getCreateTime())) continue;

            // 查询加注这条数据是否记录在设备定位表，避免重复
            int result = this.selectPositionCount(oilFill.getDevCode(), oilFill.getCreateTime());
            if (result > 0) continue;

            XtDevicePosition devicePosition = new XtDevicePosition();
            devicePosition.setDeviceCode(oilFill.getDevCode());
            devicePosition.setIpaddr(oilFill.getIpaddr());
            devicePosition.setGpsTime(oilFill.getCreateTime());
            devicePosition.setDelFlag("0");
            devicePosition.setCreateTime(DateUtils.getNowDate());

            // IP获取定位信息
            IpLocaltionVo ipLocaltion = this.getRealAddressByIP(oilFill.getIpaddr());
            // IP获取定位信息成功
            if (ipLocaltion != null && ipLocaltion.getCode() == 100) {
                devicePosition.setProvince(ipLocaltion.getResult().getProvince());
                devicePosition.setCity(ipLocaltion.getResult().getCity());
                devicePosition.setAddress(ipLocaltion.getResult().getDistrict());
                devicePosition.setLatitude(String.valueOf(ipLocaltion.getResult().getLat()));
                devicePosition.setLongitude(String.valueOf(ipLocaltion.getResult().getLng()));
            }
            devicePositionList.add(devicePosition);
        }

        // 批量新增设备定位
        if (devicePositionList.size() > 0) this.insertDevicePositionBatch(devicePositionList);
    }


    /**
     * 根据多个设备编号查询加注记录
     *
     * @param deptId 设备集合
     * @return
     */
    @Override
    public List<XtDevicePosition> selectPositionByDevCodes(Long deptId) {
        return xtDevicePositionMapper.selectPositionByDevCodes(deptId);
    }


    /**
     * 分组后回获取最新的定位
     * @return
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<CsvPositionVo> selectXtDevicePositionGroupAll(XtDevicePosition xtDevicePosition){
        List<XtDevicePosition> xtDevicePositions = xtDevicePositionMapper.selectXtDevicePositionGroupAll(xtDevicePosition);
        List<CsvPositionVo> csvPositionVoList = new ArrayList<>();
        for (XtDevicePosition devicePosition : xtDevicePositions) {
            CsvPositionVo csvPositionVo = new CsvPositionVo();
            BeanUtils.copyProperties(devicePosition, csvPositionVo);
            csvPositionVo.setAddress(devicePosition.getProvince() + devicePosition.getCity() + devicePosition.getAddress());
            csvPositionVo.setDevCode(devicePosition.getDeviceCode());
            csvPositionVoList.add(csvPositionVo);
        }

        return csvPositionVoList;
    }

    /**
     * 分组后获取最新的定位
     *
     * @return
     */
    @Override
    public List<CsvPositionVo> selectXtDevicePositionGroupByCode(Long deptId,String devCode) {
        List<XtDevicePosition> xtDevicePositions = xtDevicePositionMapper.selectXtDevicePositionGroupByCode(deptId,devCode);
        List<CsvPositionVo> csvPositionVoList = new ArrayList<>();
        for (XtDevicePosition xtDevicePosition : xtDevicePositions) {
            CsvPositionVo csvPositionVo = new CsvPositionVo();
            BeanUtils.copyProperties(xtDevicePosition, csvPositionVo);
            csvPositionVo.setAddress(xtDevicePosition.getProvince() + xtDevicePosition.getCity() + xtDevicePosition.getAddress());
            csvPositionVo.setDevCode(xtDevicePosition.getDeviceCode());
            csvPositionVoList.add(csvPositionVo);
        }
        return csvPositionVoList;
    }
}
