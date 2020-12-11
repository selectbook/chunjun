package com.iiot.business.controller;

import java.util.List;

import com.iiot.business.bo.XtCustomerCarBo;
import com.iiot.business.domain.XtCustomerCar;
import com.iiot.business.service.IXtCustomerCarService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
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
import com.iiot.business.domain.XtCustomer;
import com.iiot.business.service.IXtCustomerService;
import com.iiot.common.core.controller.BaseController;
import com.iiot.common.core.domain.AjaxResult;
import com.iiot.common.utils.poi.ExcelUtil;
import com.iiot.common.core.page.TableDataInfo;

/**
 * 客户Controller
 *
 * @author desom
 * @date 2020-07-26
 */
@Controller
@RequestMapping("/business/customer")
public class XtCustomerController extends BaseController {
    private String prefix = "business/customer";

    @Autowired
    private IXtCustomerService xtCustomerService;

    @Autowired
    private IXtCustomerCarService xtCustomerCarService;

    @RequiresPermissions("business:customer:empower")
    @GetMapping("/empower")
    public String empower() {
        return prefix + "/empower";
    }


    @RequiresPermissions("business:customer:view")
    @GetMapping()
    public String customer() {
        return prefix + "/customer";
    }

    /**
     * 查询客户列表
     */
    @RequiresPermissions("business:customer:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(XtCustomer xtCustomer) {
        startPage();
        List<XtCustomer> list = xtCustomerService.selectXtCustomerList(xtCustomer);
        return getDataTable(list);
    }

    /**
     * 导出客户列表
     */
    @RequiresPermissions("business:customer:export")
    @Log(title = "客户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(XtCustomer xtCustomer) {
        List<XtCustomer> list = xtCustomerService.selectXtCustomerList(xtCustomer);
        ExcelUtil<XtCustomer> util = new ExcelUtil<XtCustomer>(XtCustomer.class);
        return util.exportExcel(list, "customer");
    }

    /**
     * 新增客户
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存客户
     */
    @RequiresPermissions("business:customer:add")
    @Log(title = "客户", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(XtCustomerCarBo xtCustomerCarBo) {
        // 添加客户
        XtCustomer xtCustomer = new XtCustomer();
        BeanUtils.copyProperties(xtCustomerCarBo, xtCustomer);
        xtCustomerService.insertXtCustomer(xtCustomer);
        // 添加客户的车型
        XtCustomerCar xtCustomerCar = new XtCustomerCar();
        BeanUtils.copyProperties(xtCustomerCarBo, xtCustomerCar);
        xtCustomerCar.setCustomerId(Long.valueOf(xtCustomer.getId()));
        xtCustomerCarService.insertXtCustomerCar(xtCustomerCar);
        return AjaxResult.success();
    }

    /**
     * 修改客户
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        // 查询客户
        XtCustomer xtCustomer = xtCustomerService.selectXtCustomerById(id);
        // 查询车型
        XtCustomerCar xtCustomerCar = xtCustomerCarService.selectXtCustomerCarByCustomerId(id);
        XtCustomerCarBo xtCustomerCarBo = new XtCustomerCarBo();
        BeanUtils.copyProperties(xtCustomer, xtCustomerCarBo);
        BeanUtils.copyProperties(xtCustomerCar, xtCustomerCarBo);
        xtCustomerCarBo.setId(id);
        mmap.put("xtCustomer", xtCustomerCarBo);
        return prefix + "/edit";
    }

    /**
     * 修改保存客户
     */
    @RequiresPermissions("business:customer:edit")
    @Log(title = "客户", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(XtCustomerCarBo xtCustomerCarBo) {
        // 修改客户
        XtCustomer xtCustomer = new XtCustomer();
        BeanUtils.copyProperties(xtCustomerCarBo, xtCustomer);

        // 添加客户的车型
        XtCustomerCar xtCustomerCar = new XtCustomerCar();
        BeanUtils.copyProperties(xtCustomerCarBo, xtCustomerCar);
        xtCustomerCar.setCustomerId(Long.valueOf(xtCustomerCarBo.getId()));
        xtCustomerCarService.updateXtCustomerCarByCustomerId(xtCustomerCar);
        return toAjax(xtCustomerService.updateXtCustomer(xtCustomer));
    }

    /**
     * 删除客户
     */
    @RequiresPermissions("business:customer:remove")
    @Log(title = "客户", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        // 删除车型
        xtCustomerCarService.deleteXtCustomerCarByCustomerIds(ids);
        // 删除客户
        return toAjax(xtCustomerService.deleteXtCustomerByIds(ids));
    }
}
