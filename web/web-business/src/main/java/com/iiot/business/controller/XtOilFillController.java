package com.iiot.business.controller;

import com.iiot.business.bo.XtOilFillBoExport;
import com.iiot.business.domain.XtOilFill;
import com.iiot.business.service.IXtOilFillService;
import com.iiot.business.vo.OilFillDeptVo;
import com.iiot.common.annotation.Log;
import com.iiot.common.core.controller.BaseController;
import com.iiot.common.core.domain.AjaxResult;
import com.iiot.common.core.domain.RestResp;
import com.iiot.common.core.page.TableDataInfo;
import com.iiot.common.enums.BusinessType;
import com.iiot.common.utils.poi.ExcelUtil;
import com.iiot.system.service.ISysDeptService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 机油加注Controller
 *
 * @author desom
 * @date 2020-05-16
 */
@Controller
@RequestMapping("/business/oilFill")
public class XtOilFillController extends BaseController {
    private String prefix = "business/oilFill";


    @Autowired
    private ISysDeptService sysDeptService;


    @Autowired
    private IXtOilFillService xtOilFillService;


    @RequiresPermissions("business:oilFill:view")
    @GetMapping()
    public String oilFill(ModelMap map) {
        //取身份信息
        return prefix + "/oilFill";
    }


    /**
     * 查询机油加注列表
     */
    @RequiresPermissions("business:oilFill:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(XtOilFillBoExport xtOilFillBoExport) {
        List<XtOilFill> list = null;
        if (xtOilFillBoExport.getDeptId() != null && xtOilFillBoExport.getDeptId() != 0L) {
            startPage();
            list = xtOilFillService.selectXtOilFillByDevCodes(xtOilFillBoExport.getDeptId(), xtOilFillBoExport.getStartTime(), xtOilFillBoExport.getEndTime());
        } else {
            startPage();
            list = xtOilFillService.selectXtOilFillList(xtOilFillBoExport);
        }
        return getDataTable(list);
    }

    @RequiresPermissions("business:oilFill:list")
    @PostMapping("/listByDevCode")
    @ResponseBody
    public TableDataInfo listByDevCode(XtOilFillBoExport xtOilFillBoExport) {
        List<OilFillDeptVo> list = null;
        if (xtOilFillBoExport.getDeptId() != null && xtOilFillBoExport.getDeptId() != 0L) {
            startPage();
            list = xtOilFillService.selectXtOilFillByDevCodesAll(xtOilFillBoExport.getDeptId(), xtOilFillBoExport.getDevCode(), xtOilFillBoExport.getStartTime(), xtOilFillBoExport.getEndTime());
        } else {
            startPage();
            list = xtOilFillService.selectXtOilFillListAll(xtOilFillBoExport);
        }
        return getDataTable(list);
    }

    /**
     * 导出机油加注列表
     */
    @RequiresPermissions("business:oilFill:export")
    @Log(title = "机油加注", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(XtOilFillBoExport xtOilFillBoExport) {
        List<OilFillDeptVo> list = null;
        if (xtOilFillBoExport.getDeptId() != null && xtOilFillBoExport.getDeptId() != 0L) {
            //如果没有部门
            list = xtOilFillService.selectXtOilFillByDevCodesAll(xtOilFillBoExport.getDeptId(), xtOilFillBoExport.getDevCode(), xtOilFillBoExport.getStartTime(), xtOilFillBoExport.getEndTime());
        } else {
            //如果有部门
            list = xtOilFillService.selectXtOilFillListAll(xtOilFillBoExport);
        }
        ExcelUtil<OilFillDeptVo> util = new ExcelUtil<OilFillDeptVo>(OilFillDeptVo.class);
        return util.exportExcel(list, "oilFill");
    }

    /**
     * 新增机油加注
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存机油加注
     */
    @RequiresPermissions("business:oilFill:add")
    @Log(title = "机油加注", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(XtOilFill xtOilFill) {
        return toAjax(xtOilFillService.insertXtOilFill(xtOilFill));
    }

    /**
     * 修改机油加注
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        XtOilFill xtOilFill = xtOilFillService.selectXtOilFillById(id);
        mmap.put("xtOilFill", xtOilFill);
        return prefix + "/edit";
    }

    /**
     * 修改保存机油加注
     */
    @RequiresPermissions("business:oilFill:edit")
    @Log(title = "机油加注", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(XtOilFill xtOilFill) {
        return toAjax(xtOilFillService.updateXtOilFill(xtOilFill));
    }

    /**
     * 删除机油加注
     */
    @RequiresPermissions("business:oilFill:remove")
    @Log(title = "机油加注", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(xtOilFillService.deleteXtOilFillByIds(ids));
    }

    /**
     * 根据区域和时间获得加注总量
     *
     * @param area
     * @param timeType
     * @return
     */
    @RequiresPermissions("business:oilFill:amountByTimeAndArea")
    @GetMapping("/amountByTimeAndArea")
    @ResponseBody
    public RestResp amountByTimeAndArea(String area, String timeType) {
        Map<String, Object> retMap = new HashMap<>();

        Double amount = 0.0;
        if (area != null && timeType != null) {
            amount = xtOilFillService.selectFillDataByTimeAndArea(area, timeType);
        }
        if (amount == null) {
            retMap.put("amountByTimeAndArea", 0);
        } else {
            retMap.put("amountByTimeAndArea", amount);
        }

        return RestResp.success(retMap);
    }


    /**
     * 根据区域获得加注总量
     *
     * @return
     */
    @RequiresPermissions("business:oilFill:amountByArea")
    @ApiOperation(value = "润滑油用量", response = RestResp.class)
    @GetMapping("/amountByArea")
    @ResponseBody
    public RestResp amountByArea() {
        Map<String, Object> retMap = new HashMap<>();
        Double southAmount = xtOilFillService.selectFillDataByArea("south");
        Double eastAmount = xtOilFillService.selectFillDataByArea("east");
        Double northAmount = xtOilFillService.selectFillDataByArea("north");
        Double southwestAmount = xtOilFillService.selectFillDataByArea("southwest");
        Double northwestAmount = xtOilFillService.selectFillDataByArea("northwest");

        if (southAmount == null) {
            retMap.put("southAmount", 0);
        } else {
            retMap.put("southAmount", southAmount);
        }
        if (eastAmount == null) {
            retMap.put("eastAmount", 0);
        } else {
            retMap.put("eastAmount", eastAmount);
        }
        if (northAmount == null) {
            retMap.put("northAmount", 0);
        } else {
            retMap.put("northAmount", northAmount);
        }
        if (southwestAmount == null) {
            retMap.put("southwestAmount", 0);
        } else {
            retMap.put("southwestAmount", southwestAmount);
        }
        if (northwestAmount == null) {
            retMap.put("northwestAmount", 0);
        } else {
            retMap.put("northwestAmount", northwestAmount);
        }
        return RestResp.success(retMap);
    }


    /**
     * 油品用量
     *
     * @return
     */
    @RequiresPermissions("business:oilFill:fillDataByOilName")
    @ApiOperation(value = "油品用量", response = RestResp.class)
    @GetMapping("/fillDataByOilName")
    @ResponseBody
    public RestResp fillDataByOilName(String timeType) {
        Map<String, Object> retMap = new HashMap<>();

        // 取身份信息
//        SysUser user = ShiroUtils.getSysUser();
//        if (user == null) {
//            return RestResp.error(ApiCode.PARAM_EXC.getCode(), "用户不存在");
//        }
        //查询出部门信息
//        SysDept sysDept = sysDeptService.selectDeptByDeptId(user.getDeptId());
//        if (sysDept == null ) {
//            return RestResp.error(ApiCode.PARAM_EXC.getCode(), "没有找到相应的部门");
//        }

        // 获得部门下的油桶信息
//        List<XtOilDrum> oilDrumList = xtOilDrumService.selectXtOilDrumByDeptId(sysDept.getDeptId());

        // 获得油桶的品牌信息
//        List<String> vendorsList = xtOilDrumService.selectXtDrumVendorByDeptId(sysDept.getDeptId());

//        List<String> vendorsList =  xtOilDrumService.selectXtDrumVendors();
//        for (int i = 0; i < oilDrumList.size(); i++) {
//            XtOilDrum oilDrum = oilDrumList.get(i);
//            vendorsList.add(oilDrum.getVendor());
//        }

        // 获得各品牌下油品的加注总量
        Map<String, Map> amountMap = new HashMap<>();

        List<String> vendorsList = new LinkedList<>();
        vendorsList.add("天猫");
        for (int i = 0; i < vendorsList.size(); i++) {
            String vendor = vendorsList.get(i);
            Map<String, Double> amountByAreaMap = new HashMap<>();

            amountByAreaMap.put("south", 0.0);
            amountByAreaMap.put("east", 0.0);
            amountByAreaMap.put("north", 0.0);
            amountByAreaMap.put("southwest", 0.0);
            amountByAreaMap.put("northwest", 0.0);
            amountMap.put(vendor, amountByAreaMap);
        }

        // 各油品华南的用量
        List<Map<String, Object>> listSouthData = null;
        // 各油品华东的用量
        List<Map<String, Object>> listEastData = null;
        // 各油品华北的用量
        List<Map<String, Object>> listNorthData = null;
        // 各油品southwest的用量
        List<Map<String, Object>> listSouthWestData = null;

        if (timeType != null) {
            listSouthData = xtOilFillService.fillDataByOilName("south", timeType);
            listEastData = xtOilFillService.fillDataByOilName("east", timeType);
            listNorthData = xtOilFillService.fillDataByOilName("north", timeType);
            listSouthWestData = xtOilFillService.fillDataByOilName("southwest", timeType);
        }

        return RestResp.success(amountMap);

    }

}
