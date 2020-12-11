package com.iiot.business.api;


import com.iiot.business.domain.XtCarUpkeep;
import com.iiot.business.domain.XtDevice;
import com.iiot.business.domain.XtOilDrum;
import com.iiot.business.domain.XtOilFill;
import com.iiot.business.service.IXtCarUpkeepService;
import com.iiot.business.service.IXtDeviceService;
import com.iiot.business.service.IXtOilDrumService;
import com.iiot.business.service.IXtOilFillService;
import com.iiot.business.vo.DevicesVo;
import com.iiot.business.vo.LoginVo;
import com.iiot.business.vo.SwapDrumVo;
import com.iiot.common.annotation.IgnoreAuth;
import com.iiot.common.core.domain.ApiCode;
import com.iiot.common.core.domain.RestResp;
import com.iiot.common.utils.DateUtils;
import com.iiot.common.utils.StringUtils;
import com.iiot.common.utils.TokenUtils;
import com.iiot.system.domain.*;
import com.iiot.system.service.ISysDeptService;
import com.iiot.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 账号授权
 */
@Api(tags = "01.账号授权")
@RestController
@RequestMapping("/api/auth")
public class AuthApiController extends BaseApiController {


    @Autowired
    private ISysUserService userService;


    @Autowired
    private IXtDeviceService xtDeviceService;


    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private IXtCarUpkeepService xtCarUpkeepService;


    @Autowired
    private IXtOilFillService oilFillService;

    @Autowired
    private IXtOilDrumService xtOilDrumService;


    /**
     * 用户登陆
     *
     * @param login
     * @return
     * @IgnoreAuth
     */
    @ApiOperation(value = "用户登录")
    @IgnoreAuth
    @PostMapping("login")
    public RestResp login(@RequestBody LoginVo login) {
        //IgnoreAuth不认证token
        if (StringUtils.isEmpty(login.getUsername())) {
            return RestResp.error(ApiCode.PARAM_EXC.getCode(), "账号不能为空");
        }

        if (StringUtils.isEmpty(login.getPassword())) {
            return RestResp.error(ApiCode.PARAM_EXC.getCode(), "密码不能为空");
        }

        SysUser user = userService.selectUserByLoginName(login.getUsername());
        if (StringUtils.isNull(user)) {
            return RestResp.error(ApiCode.PARAM_EXC.getCode(), "账号不存在！");
        }

        //判断密码
        String sycpassword = new Md5Hash(login.getUsername() + login.getPassword() + user.getSalt()).toHex();
        if (!user.getPassword().equals(sycpassword)) {
            return RestResp.error(ApiCode.PARAM_EXC.getCode(), "账号或密码错误！");
        }

        //更新登录
        user.setLoginIp(getClientIp());
        user.setLoginDate(DateUtils.getNowDate());
        userService.updateUser(user);

        // 生成token
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        // 更新缓存
        TokenUtils.setTokenCache(token, login.getUsername());
        //封装返回
        Map<String, Object> retMap = new HashMap<>(2);
        retMap.put("token", token);
        retMap.put("username", login.getUsername());
        retMap.put("userId", user.getUserId());
        return RestResp.success("登录成功！", retMap);
    }

    /**
     * 小程序首页设备列表
     *
     * @param userId
     * @param size
     * @return
     */
    //@IgnoreAuth
    @ApiOperation(value = "小程序设备列表", response = RestResp.class)
    @PostMapping("/devices")
    @ResponseBody
    public RestResp devices(Long userId, Integer size) {

        if (userId == null) {
            return RestResp.error(ApiCode.PARAM_EXC.getCode(), "用户编号不能为空");
        }

        //查询用户信息
        SysUser sysUser = userService.selectUserById(userId);
        if (sysUser == null) {
            return RestResp.error(ApiCode.PARAM_EXC.getCode(), "没有找到相应的用户");
        }


        //查询出部门信息
        SysDept sysDept = sysDeptService.selectDeptByDeptId(sysUser.getDeptId());
        if (sysDept == null) {
            return RestResp.error(ApiCode.PARAM_EXC.getCode(), "没有找到相应的部门");
        }

        //部门名称集合
        List<String> deptNames = sysDeptService.selectDeptByDeptIds(sysDept);

        // 获取当前的用户
        List<XtDevice> xtDevices = xtDeviceService.selectXtDeviceByDeptId(sysUser.getDeptId(), size);

        //判断设备
        if (xtDevices == null || xtDevices.size() < 0) {
            return RestResp.error(ApiCode.PARAM_EXC.getCode(), "没有找到属于你的设备");
        }

        //返回对象
        List<DevicesVo> devicesVos = xtDeviceService.selectXtDevicesVo(xtDevices);

        //封装返回
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("devices", devicesVos);
        retMap.put("deptNames", deptNames);
        return RestResp.success(retMap);
    }


    /**
     * 汽车列表
     *
     * @return
     */
    @ApiOperation(value = "汽车保养列表", response = RestResp.class)
    @PostMapping("/cars")
    @ResponseBody
    public RestResp cars() {
        List<XtCarUpkeep> xtCarUpkeeps = xtCarUpkeepService.selectXtCarUpkeepGroup();
        //封装返回
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("xtCarUpkeeps", xtCarUpkeeps);
        return RestResp.success(retMap);
    }


    /**
     * 汽车保养机油推荐
     *
     * @param carId
     * @return
     */
    @ApiOperation(value = "汽车保养机油推荐", response = RestResp.class)
    @PostMapping("/engineRecommend")
    @ResponseBody
    public RestResp engineRecommend(Long carId) {
        if (carId == null || carId == 0) {
            return RestResp.error(ApiCode.PARAM_EXC.getCode(), "汽车编号不能为空a");
        }
        XtCarUpkeep xtCarUpkeep = xtCarUpkeepService.selectXtCarUpkeepById(carId);
        //封装返回
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("xtCarUpkeep", xtCarUpkeep);
        return RestResp.success(retMap);
    }

    /**
     * 解绑油箱
     *
     * @param swapDrumVo
     * @return
     */
    @ApiOperation(value = "解绑油桶", response = RestResp.class)
    @PostMapping("unBind")
    public RestResp unBind(@RequestBody SwapDrumVo swapDrumVo) {
        //解绑设备表
        XtDevice xtDevice = new XtDevice();
        xtDevice.setOilCode("");
        xtDevice.setDevCode(swapDrumVo.getDevCode());
        xtDeviceService.updateXtDeviceById(xtDevice);
        XtOilDrum xtOilDrum = xtOilDrumService.selectXtOilDrumByOilCode(swapDrumVo.getOilCode());

        if (xtOilDrum == null) {
            return RestResp.error("对不起,没有没有找到绑定的油桶");
        }

        //解绑油桶表
        xtOilDrum.setBindStatus("0");
        xtOilDrum.setBindDevCode("");
        int resultCode = xtOilDrumService.updateXtOilDrum(xtOilDrum);
        if (resultCode < 1) {
            return RestResp.success("油桶解绑失败");
        }
        return RestResp.success("油桶解绑成功");
    }

    /**
     * 更换油箱
     *
     * @param swapDrumVo
     * @return
     */
    @ApiOperation(value = "更换油桶", response = RestResp.class)
    @PostMapping("swapDrum")
    public RestResp swapDrum(@RequestBody SwapDrumVo swapDrumVo) {
        XtDevice xtDevice = new XtDevice();
        //复制属性
        BeanUtils.copyProperties(swapDrumVo, xtDevice);
        //更换油桶
        xtDevice.setBindTime(new Date());
        int resultCode = xtDeviceService.updateXtDeviceById(xtDevice);
        //改变油桶表
        XtOilDrum xtOilDrum = xtOilDrumService.selectXtOilDrumByOilCode(swapDrumVo.getOilCode());
        if (xtOilDrum == null) {
            return RestResp.error("对不起,没有没有找到油桶");
        }
        //绑定时间
        xtOilDrum.setBindTime(new Date());
        //绑定设备号
        xtOilDrum.setBindDevCode(swapDrumVo.getDevCode());
        //绑定状态
        xtOilDrum.setBindStatus("1");
        xtOilDrumService.updateXtOilDrum(xtOilDrum);
        if (resultCode < 1) {
            return RestResp.error("对不起,没有绑定油桶成功");
        }

        return RestResp.success("油桶绑定成功");
    }


    /**
     * 加注记录
     *
     * @param devCode
     * @return
     */
    @ApiOperation(value = "加注记录", response = RestResp.class)
    @PostMapping("record")
    @ResponseBody
    public RestResp record(String devCode) {
        List<XtOilFill> xtOilFills = oilFillService.selectXtOilFillByDevCode(devCode);
        //封装返回
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("xtOilFills", xtOilFills);
        return RestResp.success(retMap);
    }


}
