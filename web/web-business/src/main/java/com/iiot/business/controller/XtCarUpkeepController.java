package com.iiot.business.controller;

import com.iiot.business.domain.XtCarUpkeep;
import com.iiot.business.service.IXtCarUpkeepService;
import com.iiot.common.annotation.Log;
import com.iiot.common.core.controller.BaseController;
import com.iiot.common.core.domain.AjaxResult;
import com.iiot.common.core.page.TableDataInfo;
import com.iiot.common.enums.BusinessType;
import com.iiot.common.utils.poi.ExcelUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车型机油保养字段Controller
 *
 * @author desom
 * @date 2020-05-15
 */
@Controller
@RequestMapping("/business/carUpkeep")
public class XtCarUpkeepController extends BaseController {
    private String prefix = "business/carUpkeep";

    @Autowired
    private IXtCarUpkeepService xtCarUpkeepService;

    @RequiresPermissions("business:carUpkeep:view")
    @GetMapping()
    public String carUpkeep() {
        return prefix + "/carUpkeep";
    }

    /**
     * 查询车型机油保养字段列表
     */
    @RequiresPermissions("business:carUpkeep:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(XtCarUpkeep xtCarUpkeep) {
        startPage();
        List<XtCarUpkeep> list = xtCarUpkeepService.selectXtCarUpkeepList(xtCarUpkeep);
        return getDataTable(list);
    }


    /**
     * 导出车型机油保养字段列表
     */
    @RequiresPermissions("business:carUpkeep:export")
    @Log(title = "车型机油保养字段", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(XtCarUpkeep xtCarUpkeep) {
        List<XtCarUpkeep> list = xtCarUpkeepService.selectXtCarUpkeepList(xtCarUpkeep);
        ExcelUtil<XtCarUpkeep> util = new ExcelUtil<XtCarUpkeep>(XtCarUpkeep.class);
        return util.exportExcel(list, "carUpkeep");
    }

    /**
     * 新增车型机油保养字段
     */
    @GetMapping("/add")
    public String add(ModelMap modelMap) {
        //查询所有车的品牌
        List<XtCarUpkeep> xtCarUpkeeps = xtCarUpkeepService.selectXtCarUpkeepGroup();
        modelMap.put("xtCarUpkeeps", xtCarUpkeeps);
        return prefix + "/add";
    }

    /**
     * 新增保存车型机油保养字段
     */
    @RequiresPermissions("business:carUpkeep:add")
    @Log(title = "车型机油保养字段", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(XtCarUpkeep xtCarUpkeep) {
        return toAjax(xtCarUpkeepService.insertXtCarUpkeep(xtCarUpkeep));
    }

    /**
     * 修改车型机油保养字段
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        //查询所有车的品牌
        List<XtCarUpkeep> xtCarUpkeeps = xtCarUpkeepService.selectXtCarUpkeepGroup();
        XtCarUpkeep xtCarUpkeep = xtCarUpkeepService.selectXtCarUpkeepById(id);
        mmap.put("xtCarUpkeep", xtCarUpkeep);
        mmap.put("xtCarUpkeeps", xtCarUpkeeps);
        return prefix + "/edit";
    }

    /**
     * 修改保存车型机油保养字段
     */
    @RequiresPermissions("business:carUpkeep:edit")
    @Log(title = "车型机油保养字段", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(XtCarUpkeep xtCarUpkeep) {
        return toAjax(xtCarUpkeepService.updateXtCarUpkeep(xtCarUpkeep));
    }

    /**
     * 删除车型机油保养字段
     */
    @RequiresPermissions("business:carUpkeep:remove")
    @Log(title = "车型机油保养字段", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(xtCarUpkeepService.deleteXtCarUpkeepByIds(ids));
    }

    /**
     * 下拉框按品牌查询
     * @param brand
     * @return
     */
    @RequiresPermissions("business:carUpkeep:selectCarOption")
    @PostMapping("selectOptionXtCarUpKeepGroupByBrand")
    @ResponseBody
    public AjaxResult selectOptionXtCarUpKeepGroupByBrand(String brand) {
        return AjaxResult.success(xtCarUpkeepService.selectOptionXtCarUpKeepGroupByBrand(brand));
    }


    /**
     * 下拉框按车系id查询
     * @param seriesId
     * @return
     */
    @RequiresPermissions("business:carUpkeep:selectCarOption")
    @PostMapping("selectOptionXtCarUpKeepGroupBySeriesId")
    @ResponseBody
    public AjaxResult selectOptionXtCarUpKeepGroupBySeriesId(Long seriesId) {
        return AjaxResult.success(xtCarUpkeepService.selectOptionXtCarUpKeepGroupBySeriesId(seriesId));
    }


    /**
     * 下拉框按年款id查询
     *
     * @param annualId
     * @return
     */
    @RequiresPermissions("business:carUpkeep:selectCarOption")
    @PostMapping("selectOptionXtCarUpKeepGroupByAnnualId")
    @ResponseBody
    public AjaxResult selectOptionXtCarUpKeepGroupByAnnualId(Long annualId) {
        return AjaxResult.success(xtCarUpkeepService.selectOptionXtCarUpKeepGroupByAnnualId(annualId));
    }
}
