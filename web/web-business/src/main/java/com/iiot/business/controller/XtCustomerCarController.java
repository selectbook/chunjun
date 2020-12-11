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
import com.iiot.business.domain.XtCustomerCar;
import com.iiot.business.service.IXtCustomerCarService;
import com.iiot.common.core.controller.BaseController;
import com.iiot.common.core.domain.AjaxResult;
import com.iiot.common.utils.poi.ExcelUtil;
import com.iiot.common.core.page.TableDataInfo;

/**
 * 客户车型Controller
 * 
 * @author desom
 * @date 2020-07-26
 */
@Controller
@RequestMapping("/business/car")
public class XtCustomerCarController extends BaseController
{
    private String prefix = "business/car";

    @Autowired
    private IXtCustomerCarService xtCustomerCarService;

    @RequiresPermissions("business:car:view")
    @GetMapping()
    public String car()
    {
        return prefix + "/car";
    }

    /**
     * 查询客户车型列表
     */
    @RequiresPermissions("business:car:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(XtCustomerCar xtCustomerCar)
    {
        startPage();
        List<XtCustomerCar> list = xtCustomerCarService.selectXtCustomerCarList(xtCustomerCar);
        return getDataTable(list);
    }

    /**
     * 导出客户车型列表
     */
    @RequiresPermissions("business:car:export")
    @Log(title = "客户车型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(XtCustomerCar xtCustomerCar)
    {
        List<XtCustomerCar> list = xtCustomerCarService.selectXtCustomerCarList(xtCustomerCar);
        ExcelUtil<XtCustomerCar> util = new ExcelUtil<XtCustomerCar>(XtCustomerCar.class);
        return util.exportExcel(list, "car");
    }

    /**
     * 新增客户车型
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存客户车型
     */
    @RequiresPermissions("business:car:add")
    @Log(title = "客户车型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(XtCustomerCar xtCustomerCar)
    {
        return toAjax(xtCustomerCarService.insertXtCustomerCar(xtCustomerCar));
    }

    /**
     * 修改客户车型
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        XtCustomerCar xtCustomerCar = xtCustomerCarService.selectXtCustomerCarById(id);
        mmap.put("xtCustomerCar", xtCustomerCar);
        return prefix + "/edit";
    }

    /**
     * 修改保存客户车型
     */
    @RequiresPermissions("business:car:edit")
    @Log(title = "客户车型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(XtCustomerCar xtCustomerCar)
    {
        return toAjax(xtCustomerCarService.updateXtCustomerCar(xtCustomerCar));
    }

    /**
     * 删除客户车型
     */
    @RequiresPermissions("business:car:remove")
    @Log(title = "客户车型", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(xtCustomerCarService.deleteXtCustomerCarByIds(ids));
    }
}
