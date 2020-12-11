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
import com.iiot.business.domain.XtCustomerPutFill;
import com.iiot.business.service.IXtCustomerPutFillService;
import com.iiot.common.core.controller.BaseController;
import com.iiot.common.core.domain.AjaxResult;
import com.iiot.common.utils.poi.ExcelUtil;
import com.iiot.common.core.page.TableDataInfo;

/**
 * 客户打印小票Controller
 * 
 * @author desom
 * @date 2020-07-26
 */
@Controller
@RequestMapping("/business/fill")
public class XtCustomerPutFillController extends BaseController
{
    private String prefix = "business/fill";

    @Autowired
    private IXtCustomerPutFillService xtCustomerPutFillService;

    @RequiresPermissions("business:fill:view")
    @GetMapping()
    public String fill()
    {
        return prefix + "/fill";
    }

    /**
     * 查询客户打印小票列表
     */
    @RequiresPermissions("business:fill:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(XtCustomerPutFill xtCustomerPutFill)
    {
        startPage();
        List<XtCustomerPutFill> list = xtCustomerPutFillService.selectXtCustomerPutFillList(xtCustomerPutFill);
        return getDataTable(list);
    }

    /**
     * 导出客户打印小票列表
     */
    @RequiresPermissions("business:fill:export")
    @Log(title = "客户打印小票", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(XtCustomerPutFill xtCustomerPutFill)
    {
        List<XtCustomerPutFill> list = xtCustomerPutFillService.selectXtCustomerPutFillList(xtCustomerPutFill);
        ExcelUtil<XtCustomerPutFill> util = new ExcelUtil<XtCustomerPutFill>(XtCustomerPutFill.class);
        return util.exportExcel(list, "fill");
    }

    /**
     * 新增客户打印小票
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存客户打印小票
     */
    @RequiresPermissions("business:fill:add")
    @Log(title = "客户打印小票", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(XtCustomerPutFill xtCustomerPutFill)
    {
        return toAjax(xtCustomerPutFillService.insertXtCustomerPutFill(xtCustomerPutFill));
    }

    /**
     * 修改客户打印小票
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        XtCustomerPutFill xtCustomerPutFill = xtCustomerPutFillService.selectXtCustomerPutFillById(id);
        mmap.put("xtCustomerPutFill", xtCustomerPutFill);
        return prefix + "/edit";
    }

    /**
     * 修改保存客户打印小票
     */
    @RequiresPermissions("business:fill:edit")
    @Log(title = "客户打印小票", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(XtCustomerPutFill xtCustomerPutFill)
    {
        return toAjax(xtCustomerPutFillService.updateXtCustomerPutFill(xtCustomerPutFill));
    }

    /**
     * 删除客户打印小票
     */
    @RequiresPermissions("business:fill:remove")
    @Log(title = "客户打印小票", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(xtCustomerPutFillService.deleteXtCustomerPutFillByIds(ids));
    }
}
