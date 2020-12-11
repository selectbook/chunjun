package com.iiot.framework.handler;

import com.iiot.common.annotation.LoginUser;
import com.iiot.framework.interceptor.AuthenticationInterceptor;
import com.iiot.system.domain.SysUser;
import com.iiot.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 有@LoginUser注解的方法参数，注入当前登录用户
 */
public class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private ISysUserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(SysUser.class) && parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
                                  NativeWebRequest request, WebDataBinderFactory factory) {
        //获取用户名username
        Object username = request.getAttribute(AuthenticationInterceptor.USERNAME, RequestAttributes.SCOPE_REQUEST);
        if (username == null) {
            return null;
        }

        //获取用户信息
        return userService.selectUserByLoginName(String.valueOf(username));
    }
}
