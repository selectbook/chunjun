package com.iiot.business.controller;

import java.util.List;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.iiot.common.annotation.Log;
import com.iiot.common.enums.BusinessType;
import com.iiot.business.domain.XtDevType;
import com.iiot.business.service.IXtDevTypeService;
import com.iiot.common.core.controller.BaseController;
import com.iiot.common.core.domain.AjaxResult;
import com.iiot.common.utils.poi.ExcelUtil;
import com.iiot.common.core.page.TableDataInfo;

/**
 * 类型Controller
 * 
 * @author desom
 * @date 2020-06-30
 */
@Controller
@RequestMapping("/business/type")
public class XtDevTypeController extends BaseController
{
    private String prefix = "business/type";

    @Autowired
    private IXtDevTypeService xtDevTypeService;

    @RequiresPermissions("business:type:view")
    @GetMapping()
    public String type()
    {
        return prefix + "/type";
    }

    /**
     * 查询类型列表
     */
    @RequiresPermissions("business:type:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(XtDevType xtDevType)
    {
        startPage();
        List<XtDevType> list = xtDevTypeService.selectXtDevTypeList(xtDevType);
        return getDataTable(list);
    }

    /**
     * 导出类型列表
     */
    @RequiresPermissions("business:type:export")
    @Log(title = "类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(XtDevType xtDevType)
    {
        List<XtDevType> list = xtDevTypeService.selectXtDevTypeList(xtDevType);
        ExcelUtil<XtDevType> util = new ExcelUtil<XtDevType>(XtDevType.class);
        return util.exportExcel(list, "type");
    }

    /**
     * 新增类型
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存类型
     */
    @RequiresPermissions("business:type:add")
    @Log(title = "类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(XtDevType xtDevType)
    {
        return toAjax(xtDevTypeService.insertXtDevType(xtDevType));
    }

    /**
     * 修改类型
     */
    @GetMapping("/edit/{devTypeId}")
    public String edit(@PathVariable("devTypeId") Integer devTypeId, ModelMap mmap)
    {
        XtDevType xtDevType = xtDevTypeService.selectXtDevTypeById(devTypeId);
        mmap.put("xtDevType", xtDevType);
        return prefix + "/edit";
    }

    /**
     * 修改保存类型
     */
    @RequiresPermissions("business:type:edit")
    @Log(title = "类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(XtDevType xtDevType)
    {
        return toAjax(xtDevTypeService.updateXtDevType(xtDevType));
    }

    /**
     * 删除类型
     */
    @RequiresPermissions("business:type:remove")
    @Log(title = "类型", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(xtDevTypeService.deleteXtDevTypeByIds(ids));
    }
}
