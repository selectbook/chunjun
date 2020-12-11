package com.iiot.business.task;

import com.iiot.business.service.IXtDevicePositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 设备定位任务调度
 */
@Component("devPosTask")
public class DevPosTask {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IXtDevicePositionService devicePositionService;

    /**
     * 获取定位信息
     */
    public void getPosInfo() {
        //String startTime = DateUtils.dateTimeNow("yyyy-MM-dd HH");
        devicePositionService.getPositionInfo(null, null);
    }
}
