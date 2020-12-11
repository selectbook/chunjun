package com.iiot.util;

import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/4/18.
 */
public class NumberUtils {

    /**
     * 数字保留2位小数
     * @param d
     * @return
     */
    public static String ReservedDecimal(double d){
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(d);
    }

    public static boolean numCheck(String str){
        if(StringUtils.isEmpty(str)){
            return false;
        }
        Pattern p = Pattern.compile("^[0-9]*$");
        // 只能输入数字
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
