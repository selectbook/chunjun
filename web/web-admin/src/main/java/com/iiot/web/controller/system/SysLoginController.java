package com.iiot.web.controller.system;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.iiot.common.core.controller.BaseController;
import com.iiot.common.core.domain.AjaxResult;
import com.iiot.common.utils.ServletUtils;
import com.iiot.common.utils.StringUtils;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
@Controller
public class SysLoginController extends BaseController
{
    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response)
    {
        // 如果是Ajax请求，返回Json字符串。
        if (ServletUtils.isAjaxRequest(request))
        {
            return ServletUtils.renderString(response, "{\"code\":\"1\",\"msg\":\"未登录或登录超时。请重新登录\"}");
        }

        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public AjaxResult ajaxLogin(String username, String password, Boolean rememberMe)
    {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
        Subject subject = SecurityUtils.getSubject();
        try
        {
            subject.login(token);
            return success();
        }
        catch (AuthenticationException e)
        {
            String msg = "用户或密码错误";
            if (StringUtils.isNotEmpty(e.getMessage()))
            {
                msg = e.getMessage();
            }
            return error(msg);
        }
    }

    @GetMapping("/unauth")
    public String unauth()
    {
        return "error/unauth";
    }

    /**
     * 报表
     *
     * @author ruoyi
     */
    @Controller
    @RequestMapping("/demo/report")
    public static class DemoReportController
    {
        private String prefix = "demo/report";

        /**
         * 百度ECharts
         */
        @GetMapping("/echarts")
        public String echarts()
        {
            return prefix + "/echarts";
        }

        /**
         * 图表插件
         */
        @GetMapping("/peity")
        public String peity()
        {
            return prefix + "/peity";
        }

        /**
         * 线状图插件
         */
        @GetMapping("/sparkline")
        public String sparkline()
        {
            return prefix + "/sparkline";
        }

        /**
         * 图表组合
         */
        @GetMapping("/metrics")
        public String metrics()
        {
            return prefix + "/metrics";
        }
    }
}
