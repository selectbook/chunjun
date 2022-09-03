package com.iiot;

import com.iiot.framework.shiro.service.SysPasswordService;

/**
 * @Author: SelectBook
 * @Date: 2022/9/3 23:12
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(new SysPasswordService().encryptPassword("admin", "Le@123456", "2d13b8"));
    }
}
