package com.iiot.business.service;

import java.util.List;
import com.iiot.business.domain.XtReportAlarm;

/**
 * 警告Service接口
 * 
 * @author desom
 * @date 2020-07-14
 */
public interface IXtReportAlarmService 
{
    /**
     * 查询警告
     * 
     * @param id 警告ID
     * @return 警告
     */
    public XtReportAlarm selectXtReportAlarmById(Long id);

    /**
     * 查询警告列表
     * 
     * @param xtReportAlarm 警告
     * @return 警告集合
     */
    public List<XtReportAlarm> selectXtReportAlarmList(XtReportAlarm xtReportAlarm);

    /**
     * 新增警告
     * 
     * @param xtReportAlarm 警告
     * @return 结果
     */
    public int insertXtReportAlarm(XtReportAlarm xtReportAlarm);

    /**
     * 修改警告
     * 
     * @param xtReportAlarm 警告
     * @return 结果
     */
    public int updateXtReportAlarm(XtReportAlarm xtReportAlarm);

    /**
     * 批量删除警告
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXtReportAlarmByIds(String ids);

    /**
     * 删除警告信息
     * 
     * @param id 警告ID
     * @return 结果
     */
    public int deleteXtReportAlarmById(Long id);


    /**
     *
     * @param xtReportAlarm
     * @return
     */
    public XtReportAlarm selectAlarmDrum(XtReportAlarm xtReportAlarm);
}
