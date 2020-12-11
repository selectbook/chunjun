package com.iiot.business.controller;

import com.iiot.business.bo.XtDeviceBoExport;
import com.iiot.business.domain.XtDevType;
import com.iiot.business.domain.XtDevice;
import com.iiot.business.domain.XtGroup;
import com.iiot.business.domain.XtOilDrum;
import com.iiot.business.service.IXtDevTypeService;
import com.iiot.business.service.IXtDeviceService;
import com.iiot.business.service.IXtGroupService;
import com.iiot.business.service.IXtOilDrumService;
import com.iiot.business.vo.DeviceExportVo;
import com.iiot.common.annotation.Log;
import com.iiot.common.core.controller.BaseController;
import com.iiot.common.core.domain.AjaxResult;
import com.iiot.common.core.domain.RestResp;
import com.iiot.common.core.page.TableDataInfo;
import com.iiot.common.enums.BusinessType;
import com.iiot.common.utils.poi.ExcelUtil;
import com.iiot.framework.util.ShiroUtils;
import com.iiot.system.domain.SysDept;
import com.iiot.system.domain.SysUser;
import com.iiot.system.service.ISysDeptService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 设备字段Controller
 *
 * @author desom
 * @date 2020-05-15
 */
@Controller
@RequestMapping("/business/device")
public class XtDeviceController extends BaseController {

    private String prefix = "business/device";

    @Autowired
    private IXtDeviceService xtDeviceService;

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private IXtGroupService groupService;

    @Autowired
    private IXtDevTypeService devTypeService;

    @Autowired
    private IXtOilDrumService oilDrumService;


    @RequiresPermissions("business:device:view")
    @GetMapping()
    public String device() {
        return prefix + "/device";
    }

    /**
     * 查询设备字段列表
     */
    @RequiresPermissions("business:device:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(XtDeviceBoExport xtDevice) {
        startPage();
        Long deptId = xtDevice.getDeptId();
        System.out.println("zt:" + xtDevice.getStatus());
        List<XtDevice> list = xtDeviceService.selectXtDeviceList(xtDevice);
        return getDataTable(list);
    }


    /**
     * 查询设备字段列表
     */
    @RequiresPermissions("business:device:list")
    @PostMapping("/listType")
    @ResponseBody
    public TableDataInfo listType(@RequestBody XtDevice xtDevice) {
        System.out.println("zt:" + xtDevice.getStatus());
        XtDeviceBoExport xtDeviceBoExport = new XtDeviceBoExport();
        BeanUtils.copyProperties(xtDevice, xtDeviceBoExport);
        List<XtDevice> list = xtDeviceService.selectXtDeviceList(xtDeviceBoExport);
        return getDataTable(list);
    }


    /**
     * 导出设备字段列表
     */
    @RequiresPermissions("business:device:export")
    @Log(title = "设备字段", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(XtDeviceBoExport xtDevice) {
        List<DeviceExportVo> exportVos = new ArrayList<>();
        List<XtDevice> list = xtDeviceService.selectXtDeviceList(xtDevice);
        for (XtDevice device : list) {
            DeviceExportVo deviceExportVo = new DeviceExportVo();
            BeanUtils.copyProperties(device, deviceExportVo);
            if (device.getDeptId() != null) {
                deviceExportVo.setDeptName(sysDeptService.selectDeptById(device.getDeptId()).getDeptName());
            }
            exportVos.add(deviceExportVo);

        }
        ExcelUtil<DeviceExportVo> util = new ExcelUtil<DeviceExportVo>(DeviceExportVo.class);
        return util.exportExcel(exportVos, "device");
    }

    /**
     * 新增设备字段
     */
    @GetMapping("/add")
    public String add(ModelMap map) {
        //取身份信息
        SysUser user = ShiroUtils.getSysUser();
        SysDept sysDept = new SysDept();
        if (user.isAdmin()) {
            sysDept.setDeptId(100L);
        } else {
            sysDept.setDeptId(user.getDeptId());
        }
        List<SysDept> sysDepts = sysDeptService.selectDeptListAll(sysDept);
        //查询设备列表
        List<XtGroup> xtGroups = groupService.selectXtGroupList(null);
        List<XtDevType> devTypes = devTypeService.selectXtDevTypeList(null);
        map.put("sysDepts", sysDepts);
        map.put("xtGroups", xtGroups);
        map.put("devTypes", devTypes);
        return prefix + "/add";
    }


    /**
     * 添加和修改下拉框联动
     */
    @RequiresPermissions("business:oilDrum:selectDeptDrop")
    @PostMapping("/selectDeptDrop")
    @ResponseBody
    public AjaxResult selectDeptDrop(Long parentId) {
        SysDept sysDept = new SysDept();
        sysDept.setParentId(parentId);
        List<SysDept> sysDepts = sysDeptService.selectDepts(sysDept);
        return AjaxResult.success(sysDepts);
    }

    /**
     * 新增保存设备字段
     */
    @RequiresPermissions("business:device:add")
    @Log(title = "设备字段", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(XtDevice xtDevice) {
        //改变绑定的油桶
        this.UpdateOliCode(xtDevice);
        return toAjax(xtDeviceService.insertXtDevice(xtDevice));
    }

    /**
     * 修改设备字段
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        //取身份信息
        SysUser user = ShiroUtils.getSysUser();
        SysDept sysDept = new SysDept();
        if (user.isAdmin()) {
            sysDept.setDeptId(100L);
        } else {
            sysDept.setDeptId(user.getDeptId());
        }
        List<SysDept> sysDepts = sysDeptService.selectDeptListAll(sysDept);
        //查询设备列表
        List<XtGroup> xtGroups = groupService.selectXtGroupList(null);
        List<XtDevType> devTypes = devTypeService.selectXtDevTypeList(null);
        XtDevice xtDevice = xtDeviceService.selectXtDeviceById(id);
        mmap.put("sysDepts", sysDepts);
        mmap.put("xtGroups", xtGroups);
        mmap.put("devTypes", devTypes);
        mmap.put("xtDevice", xtDevice);
        return prefix + "/edit";
    }

    /**
     * 修改保存设备字段
     */
    @RequiresPermissions("business:device:edit")
    @Log(title = "设备字段", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(XtDevice xtDevice) {
        //改变绑定的油桶
        this.UpdateOliCode(xtDevice);
        return toAjax(xtDeviceService.updateXtDevice(xtDevice));
    }

    /**
     * 删除设备字段
     */
    @RequiresPermissions("business:device:remove")
    @Log(title = "设备字段", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(xtDeviceService.deleteXtDeviceByIds(ids));
    }

    /**
     * 三种状态下（在线 离线 故障）的设备总数
     *
     * @return
     */
    @RequiresPermissions("business:device:amountByStatus")
    @ApiOperation(value = "设备状态", response = RestResp.class)
    @GetMapping("/amountByStatus")
    @ResponseBody
    public RestResp deviceAmountByStatus() {
        Map<String, Object> retMap = new HashMap<>();
        Integer onlineDevices = xtDeviceService.selectDevicesByStatus("1");
        Integer offlineDevices = xtDeviceService.selectDevicesByStatus("2");
        Integer breakdownDevices = xtDeviceService.selectDevicesByStatus("3");
        if (onlineDevices == null) {
            retMap.put("onlineDevices", 0);
        } else {
            retMap.put("onlineDevices", onlineDevices);
        }
        if (offlineDevices == null) {
            retMap.put("offlineDevices", 0);
        } else {
            retMap.put("offlineDevices", offlineDevices);
        }
        if (breakdownDevices == null) {
            retMap.put("breakdownDevices", 0);
        } else {
            retMap.put("breakdownDevices", breakdownDevices);
        }
        return RestResp.success(retMap);
    }

    /**
     * 根据输入的devCode查询是否有重复的数据
     *
     * @param devCode
     * @return
     */
    @GetMapping("/selectDeviceByDevCode/{devCode}")
    @ResponseBody
    public AjaxResult selectDeviceByDevCode(@PathVariable("devCode") String devCode) {
        XtDevice xtDevice = new XtDevice();
        xtDevice.setDevCode(devCode);
        List<XtDevice> xtDevices = xtDeviceService.selectXtDeviceListAll(xtDevice);
        if (xtDevices != null && xtDevices.size() > 0) {
            return AjaxResult.success(false);
        } else {
            return AjaxResult.success(true);
        }
    }

    /**
     * 根据输入的oilCode查询是否有重复的数据
     *
     * @param oilCode
     * @return
     */
    @GetMapping("/selectDeviceByOilCode/{oilCode}")
    @ResponseBody
    public AjaxResult selectDeviceByOilCode(@PathVariable("oilCode") String oilCode) {
        XtDevice xtDevice = new XtDevice();
        xtDevice.setOilCode(oilCode);
        List<XtDevice> xtDevices = xtDeviceService.selectXtDeviceListAll(xtDevice);
        if (xtDevices != null && xtDevices.size() > 0) {
            return AjaxResult.success(false);
        } else {
            return AjaxResult.success(true);
        }
    }


    /**
     * 改变绑定的油桶
     *
     * @param xtDevice
     */
    private void UpdateOliCode(XtDevice xtDevice) {
        //改变绑定的油桶
        XtOilDrum xtOilDrum = new XtOilDrum();
        xtOilDrum.setOilCode(xtDevice.getOilCode());
        xtOilDrum.setBindTime(new Date());
        xtOilDrum.setBindDevCode(xtDevice.getDevCode());
        xtOilDrum.setBindStatus("1");
        if (xtDevice.getOilCode() != null && xtDevice.getOilCode() != "" && !xtDevice.getOilCode().equals("")) {
            oilDrumService.updateXtOilDrumByOilCode(xtOilDrum);
        }
    }

}
