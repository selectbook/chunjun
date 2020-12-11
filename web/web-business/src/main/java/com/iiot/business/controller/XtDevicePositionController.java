package com.iiot.business.controller;

import com.iiot.business.domain.XtDevicePosition;
import com.iiot.business.service.IXtDevicePositionService;
import com.iiot.business.vo.CsvPositionVo;
import com.iiot.business.vo.DevicePositionVo;
import com.iiot.common.annotation.Log;
import com.iiot.common.core.controller.BaseController;
import com.iiot.common.core.domain.AjaxResult;
import com.iiot.common.enums.BusinessType;
import com.iiot.common.utils.poi.ExcelUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 设备定位字段Controller
 *
 * @author desom
 * @date 2020-05-16
 */
@Controller
@RequestMapping("/business/position")
public class XtDevicePositionController extends BaseController {
    private String prefix = "business/position";

    @Autowired
    private IXtDevicePositionService xtDevicePositionService;




    @RequiresPermissions("business:position:view")
    @GetMapping()
    public String position() {
        return prefix + "/position";
    }




    @GetMapping("/baidu")
    public String baidu() {
        return prefix + "/point";
    }

    /**
     * //     * 查询设备定位字段列表
     * //
     */
//    @RequiresPermissions("business:position:list")
//    @PostMapping("/list")
//    @ResponseBody
//    public TableDataInfo list(DevicePositionVo devicePositionVo) {
//        List<XtDevicePosition> list = null;
//        if (devicePositionVo.getDeptId() != null && devicePositionVo.getDeptId() != 0L) {
//                startPage();
//                list = xtDevicePositionService.selectPositionByDevCodes(devicePositionVo.getDeptId());
//        } else {
//            XtDevicePosition xtDevicePosition = new XtDevicePosition();
//            BeanUtils.copyProperties(devicePositionVo, xtDevicePosition);
//            xtDevicePosition.setDeviceCode(devicePositionVo.getDevCode());
//            startPage();
//            list = xtDevicePositionService.selectXtDevicePositionList(xtDevicePosition);
//        }
//        return getDataTable(list);
//    }


    /**
     * 导出设备定位字段列表
     */
    @RequiresPermissions("business:position:export")
    @Log(title = "设备定位字段", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(XtDevicePosition xtDevicePosition) {
        List<XtDevicePosition> list = xtDevicePositionService.selectXtDevicePositionList(xtDevicePosition);
        ExcelUtil<XtDevicePosition> util = new ExcelUtil<XtDevicePosition>(XtDevicePosition.class);
        return util.exportExcel(list, "position");
    }

    /**
     * 新增设备定位字段
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存设备定位字段
     */
    @RequiresPermissions("business:position:add")
    @Log(title = "设备定位字段", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(XtDevicePosition xtDevicePosition) {
        return toAjax(xtDevicePositionService.insertXtDevicePosition(xtDevicePosition));
    }

    /**
     * 修改设备定位字段
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        XtDevicePosition xtDevicePosition = xtDevicePositionService.selectXtDevicePositionById(id);
        mmap.put("xtDevicePosition", xtDevicePosition);
        return prefix + "/edit";
    }

    /**
     * 修改保存设备定位字段
     */
    @RequiresPermissions("business:position:edit")
    @Log(title = "设备定位字段", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(XtDevicePosition xtDevicePosition) {
        return toAjax(xtDevicePositionService.updateXtDevicePosition(xtDevicePosition));
    }

    /**
     * 删除设备定位字段
     */
    @RequiresPermissions("business:position:remove")
    @Log(title = "设备定位字段", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(xtDevicePositionService.deleteXtDevicePositionByIds(ids));
    }


    //TODO 占时写在这里,后续一个定时任务,定时导入到百度地图的云麻点
    @RequiresPermissions("business:position:list")
    @PostMapping("/mapList")
    @ResponseBody
    public AjaxResult mapList(@RequestBody DevicePositionVo devicePositionVo) {
        List<CsvPositionVo> xtDevicePositions = null;
        if (devicePositionVo.getDeptId() == null || devicePositionVo.getDeptId() == 0) {
            XtDevicePosition xtDevicePosition = new XtDevicePosition();
            BeanUtils.copyProperties(devicePositionVo, xtDevicePosition);
            xtDevicePosition.setDeviceCode(devicePositionVo.getDevCode());
            xtDevicePositions = xtDevicePositionService.selectXtDevicePositionGroupAll(xtDevicePosition);
        } else {
            xtDevicePositions = xtDevicePositionService.selectXtDevicePositionGroupByCode(devicePositionVo.getDeptId(),devicePositionVo.getDevCode());
        }
        return AjaxResult.success(xtDevicePositions);
    }
}
