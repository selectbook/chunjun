package com.iiot.business.controller;

import java.util.List;

import com.iiot.business.service.IXtDeviceService;
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
import com.iiot.business.domain.XtGroup;
import com.iiot.business.service.IXtGroupService;
import com.iiot.common.core.controller.BaseController;
import com.iiot.common.core.domain.AjaxResult;
import com.iiot.common.utils.poi.ExcelUtil;
import com.iiot.common.core.page.TableDataInfo;

/**
 * 分组Controller
 *
 * @author desom
 * @date 2020-06-24
 */
@Controller
@RequestMapping("/business/group")
public class XtGroupController extends BaseController {

    private String prefix = "business/group";

    @Autowired
    private IXtGroupService xtGroupService;


    @Autowired
    private IXtDeviceService xtDeviceService;

    @RequiresPermissions("business:group:view")
    @GetMapping()
    public String group() {
        return prefix + "/group";
    }


    /**
     * 查询分组列表
     */
    @RequiresPermissions("business:group:list")
    @GetMapping("/list")
    @ResponseBody
    public TableDataInfo list(XtGroup xtGroup) {
        startPage();
        List<XtGroup> list = xtGroupService.selectXtGroupList(xtGroup);
        return getDataTable(list);
    }

    /**
     * 导出分组列表
     */
    @RequiresPermissions("business:group:export")
    @Log(title = "分组", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(XtGroup xtGroup) {
        List<XtGroup> list = xtGroupService.selectXtGroupList(xtGroup);
        ExcelUtil<XtGroup> util = new ExcelUtil<XtGroup>(XtGroup.class);
        return util.exportExcel(list, "group");
    }

    /**
     * 新增分组
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存分组
     */
    @RequiresPermissions("business:group:add")
    @Log(title = "分组", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(XtGroup xtGroup) {
        xtGroup.getDevGroup();
        return toAjax(xtGroupService.insertXtGroup(xtGroup));
    }

    /**
     * 修改分组
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        XtGroup xtGroup = xtGroupService.selectXtGroupById(id);
        mmap.put("xtGroup", xtGroup);
        return prefix + "/edit";
    }

    /**
     * 修改保存分组
     */
    @RequiresPermissions("business:group:edit")
    @Log(title = "分组", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(XtGroup xtGroup) {
        return toAjax(xtGroupService.updateXtGroup(xtGroup));
    }

    /**
     * 删除分组
     */
    @RequiresPermissions("business:group:remove")
    @Log(title = "分组", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(xtGroupService.deleteXtGroupByIds(ids));
    }


    /**
     * 根据分组id推荐分组下的加注机的个数
     *
     * @param devGroupId
     * @return
     */
    @GetMapping("/countGroup")
    @ResponseBody
    public AjaxResult countGroup(Long devGroupId) {
        return AjaxResult.success(xtDeviceService.selectCountByGroupId(devGroupId));
    }

}
