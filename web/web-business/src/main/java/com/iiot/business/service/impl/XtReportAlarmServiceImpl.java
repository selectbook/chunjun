package com.iiot.business.service.impl;

import java.util.List;

import com.iiot.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iiot.business.mapper.XtReportAlarmMapper;
import com.iiot.business.domain.XtReportAlarm;
import com.iiot.business.service.IXtReportAlarmService;
import com.iiot.common.core.text.Convert;

/**
 * 警告Service业务层处理
 *
 * @author desom
 * @date 2020-07-14
 */
@Service
public class XtReportAlarmServiceImpl implements IXtReportAlarmService {
    @Autowired
    private XtReportAlarmMapper xtReportAlarmMapper;

    /**
     * 查询警告
     *
     * @param id 警告ID
     * @return 警告
     */
    @Override
    public XtReportAlarm selectXtReportAlarmById(Long id) {
        return xtReportAlarmMapper.selectXtReportAlarmById(id);
    }

    /**
     * 查询警告列表
     *
     * @param xtReportAlarm 警告
     * @return 警告
     */
    @Override
    public List<XtReportAlarm> selectXtReportAlarmList(XtReportAlarm xtReportAlarm) {
        return xtReportAlarmMapper.selectXtReportAlarmList(xtReportAlarm);
    }

    /**
     * 新增警告
     *
     * @param xtReportAlarm 警告
     * @return 结果
     */
    @Override
    public int insertXtReportAlarm(XtReportAlarm xtReportAlarm) {
        xtReportAlarm.setCreateTime(DateUtils.getNowDate());
        return xtReportAlarmMapper.insertXtReportAlarm(xtReportAlarm);
    }

    /**
     * 修改警告
     *
     * @param xtReportAlarm 警告
     * @return 结果
     */
    @Override
    public int updateXtReportAlarm(XtReportAlarm xtReportAlarm) {
        xtReportAlarm.setUpdateTime(DateUtils.getNowDate());
        return xtReportAlarmMapper.updateXtReportAlarm(xtReportAlarm);
    }

    /**
     * 删除警告对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXtReportAlarmByIds(String ids) {
        return xtReportAlarmMapper.deleteXtReportAlarmByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除警告信息
     *
     * @param id 警告ID
     * @return 结果
     */
    @Override
    public int deleteXtReportAlarmById(Long id) {
        return xtReportAlarmMapper.deleteXtReportAlarmById(id);
    }


    /**
     * @param xtReportAlarm
     * @return
     */
    public XtReportAlarm selectAlarmDrum(XtReportAlarm xtReportAlarm) {
        return xtReportAlarmMapper.selectAlarmDrum(xtReportAlarm);
    }
}
