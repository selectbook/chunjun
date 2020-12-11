package com.iiot.business.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.iiot.business.bo.XtOilDrumBoExport;
import com.iiot.business.domain.XtDevice;
import com.iiot.business.domain.XtOilDrum;
import com.iiot.business.domain.XtReportAlarm;
import com.iiot.business.service.IXtDeviceService;
import com.iiot.business.service.IXtOilDrumService;
import com.iiot.business.service.IXtReportAlarmService;
import com.iiot.business.vo.AlarmDrumVo;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 油桶字段Controller
 *
 * @author desom
 * @date 2020-05-15
 */
@Controller
@RequestMapping("/business/oilDrum")
public class XtOilDrumController extends BaseController {
    private String prefix = "business/oilDrum";

    @Autowired
    private IXtOilDrumService xtOilDrumService;

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private IXtDeviceService xtDeviceService;

    @Autowired
    private IXtReportAlarmService xtReportAlarmService;


    @RequiresPermissions("business:oilDrum:view")
    @GetMapping()
    public String oilDrum() {
        return prefix + "/oilDrum";
    }

    /**
     * 查询油桶字段列表
     */
    @RequiresPermissions("business:oilDrum:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(XtOilDrumBoExport xtOilDrumBOExport) {
        startPage();
        List<AlarmDrumVo> alarmDrumVos = new ArrayList<>();
        List<XtOilDrum> list = xtOilDrumService.selectXtOilDrumList(xtOilDrumBOExport);
        for (XtOilDrum xtOilDrum : list) {
            XtReportAlarm xtReportAlarm = new XtReportAlarm();
            xtReportAlarm.setDevCode(xtOilDrum.getBindDevCode());
            XtReportAlarm xtReportAlarms = xtReportAlarmService.selectAlarmDrum(xtReportAlarm);
            AlarmDrumVo alarmDrumVo = new AlarmDrumVo();
            BeanUtils.copyProperties(xtOilDrum, alarmDrumVo);
            if (xtReportAlarms != null) {
                alarmDrumVo.setDrumStatus(1);
            }
            alarmDrumVos.add(alarmDrumVo);
        }

        return getDataTable(alarmDrumVos);
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
     * 导出油桶字段列表
     */
    @RequiresPermissions("business:oilDrum:export")
    @Log(title = "油桶字段", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(XtOilDrumBoExport xtOilDrumBOExport) {
        List<XtOilDrum> list = xtOilDrumService.selectXtOilDrumList(xtOilDrumBOExport);
        ExcelUtil<XtOilDrum> util = new ExcelUtil<XtOilDrum>(XtOilDrum.class);
        return util.exportExcel(list, "oilDrum");
    }

    /**
     * 新增油桶字段
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
        map.put("sysDepts", sysDepts);
        return prefix + "/add";
    }

    /**
     * 新增保存油桶字段
     */
    @RequiresPermissions("business:oilDrum:add")
    @Log(title = "油桶字段", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(XtOilDrum xtOilDrum) {
        return toAjax(xtOilDrumService.insertXtOilDrum(xtOilDrum));
    }

    /**
     * 修改油桶字段
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap map) {
        XtOilDrum xtOilDrum = xtOilDrumService.selectXtOilDrumById(id);
        //取身份信息
        SysUser user = ShiroUtils.getSysUser();
        SysDept sysDept = new SysDept();
        if (user.isAdmin()) {
            sysDept.setDeptId(100L);
        } else {
            sysDept.setDeptId(user.getDeptId());
        }
        List<SysDept> sysDepts = sysDeptService.selectDeptListAll(sysDept);
        map.put("sysDepts", sysDepts);
        map.put("xtOilDrum", xtOilDrum);
        return prefix + "/edit";
    }

    /**
     * 查看二微码
     */
    @GetMapping("qrCode/{id}/{oilCode}")
    public String qrCode(@PathVariable("id") Long id, @PathVariable("oilCode") String oilCode, ModelMap modelMap) {
        modelMap.put("id", id);
        modelMap.put("oilCode" + ".png", oilCode);
        return prefix + "/qrCode";
    }

    /**
     * 修改保存油桶字段
     */
    @RequiresPermissions("business:oilDrum:edit")
    @Log(title = "油桶字段", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(XtOilDrum xtOilDrum) {
        //查询出设备绑定的设备
        XtDevice xtDevice = xtDeviceService.selectXtDeviceByDevCode(xtOilDrum.getBindDevCode());
        if (xtDevice != null) {
            //如果不为空说明他们以前有绑定关系,如果有绑定关系就改变设备表的绑定id
            XtDevice xtDeviceUpdate = new XtDevice();
            xtDeviceUpdate.setId(xtDevice.getId());
            xtDeviceUpdate.setOilCode(xtOilDrum.getOilCode());
            xtDeviceService.updateXtDevice(xtDeviceUpdate);
        }
        return toAjax(xtOilDrumService.updateXtOilDrum(xtOilDrum));
    }

    /**
     * 删除油桶字段
     */
    @RequiresPermissions("business:oilDrum:remove")
    @Log(title = "油桶字段", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(xtOilDrumService.deleteXtOilDrumByIds(ids));
    }

    /**
     * 下载二微码
     */
    @RequiresPermissions("business:oilDrum:QrCode")
    @GetMapping("uploadQrCode/{id}")
    @ResponseBody
    public void uploadQrCode(@PathVariable("id") Long id, HttpServletResponse response) {
        this.createQrCode(id, response);
    }

    /**
     * 创建二微码
     *
     * @param id
     * @param response
     */

    /**
     * 剩余和消耗库存量
     *
     * @return
     */
    @RequiresPermissions("business:oilDrum:drumStock")
    @ApiOperation(value = "润滑油库存", response = RestResp.class)
    @GetMapping("/drumStock")
    @ResponseBody
    public RestResp selectDrumStock() {
        Map<String, Object> retMap = new HashMap<>();
        Integer rest = xtOilDrumService.selectDrumStock("rest");
        Integer used = xtOilDrumService.selectDrumStock("used");
        if (rest == null) {
            retMap.put("rest", 0);
        } else {
            retMap.put("rest", rest);
        }
        if (used == null) {
            retMap.put("used", 0);
        } else {
            retMap.put("used", used);
        }
        return RestResp.success(retMap);
    }

    private void createQrCode(Long id, HttpServletResponse response) {
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        XtOilDrum xtOilDrums = xtOilDrumService.selectXtOilDrumById(id);
        OutputStream stream = null;
        if (xtOilDrums != null) {
            try {
                stream = response.getOutputStream();
                Map<EncodeHintType, Object> hints = new HashMap<>();
                //编码
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                //边框距
                hints.put(EncodeHintType.MARGIN, 0);
                //写出
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                //创建二微码
                BitMatrix bm = qrCodeWriter.encode(xtOilDrums.getOilCode(), BarcodeFormat.QR_CODE, 200, 200, hints);
                //创建路径,并以字节流返回给前端
                MatrixToImageWriter.writeToStream(bm, "png", stream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 根据输入的oilCode查询是否有重复的数据
     *
     * @param oilCode
     * @return
     */
    @GetMapping("/selectOilDrumByOilCode/{oilCode}")
    @ResponseBody
    public AjaxResult selectOilDrumByOilCode(@PathVariable("oilCode") String oilCode) {
        XtOilDrum xtOilDrum = new XtOilDrum();
        xtOilDrum.setOilCode(oilCode);
        List<XtOilDrum> xtOilDrums = xtOilDrumService.selectXtOilDrumListAll(xtOilDrum);
        if (xtOilDrums != null && xtOilDrums.size() > 0) {
            return AjaxResult.success(false);
        } else {
            return AjaxResult.success(true);
        }
    }


    /**
     * 根据品牌名称查询出油桶个数
     *
     * @param vendor
     * @return
     */
    @GetMapping("/selectDrumByVendor/{vendor}")
    @ResponseBody
    public AjaxResult selectDrumByVendor(@PathVariable("vendor") String vendor) {
        int i = xtOilDrumService.selectCountDrumByVendor(vendor);
        return AjaxResult.success(i);
    }


    /**
     * 查询报警数据
     *
     * @return
     */
    @GetMapping("/selectAlarmDrum")
    @ResponseBody
    public AjaxResult selectAlarmDrum(XtOilDrum xtOilDrum) {
        XtOilDrum xtOilDrumResult = xtOilDrumService.selectAlarmDrum(xtOilDrum);
        return AjaxResult.success(xtOilDrumResult);
    }
}
