package com.iiot.framework.interceptor;

import com.iiot.common.annotation.IgnoreAuth;
import com.iiot.common.core.domain.ApiCode;
import com.iiot.common.exception.ApiException;
import com.iiot.common.utils.StringUtils;
import com.iiot.common.utils.TokenUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限(Token)验证,拦截器
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    public static final String USERNAME = "username";
    public static final String TOKEN_KEY = "token";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        IgnoreAuth annotation;
        if (handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(IgnoreAuth.class);
        } else {
            return true;
        }

        //如果有@IgnoreAuth注解，则不验证token
        if (annotation != null) {
            return true;
        }

        //获取用户凭证
        String token = request.getHeader(TOKEN_KEY);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(TOKEN_KEY);
        }

        //凭证为空
        if (StringUtils.isBlank(token)) {
            throw new ApiException(TOKEN_KEY + "不能为空，请重新登录", ApiCode.LOGIN_EXC.getCode());
        }

        String username = TokenUtils.getTokenCache(token);
        if (StringUtils.isEmpty(username)) {
            throw new ApiException(TOKEN_KEY + "失效，请重新登录", ApiCode.TOKEN_FAIL.getCode());
        }

        //设置username到request里，后续根据username，获取用户信息
        request.setAttribute(USERNAME, username);
        return true;
    }
}
