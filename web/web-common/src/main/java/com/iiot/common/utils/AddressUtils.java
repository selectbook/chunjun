package com.iiot.common.utils;

import com.iiot.common.config.Global;
import com.iiot.common.json.JSON;
import com.iiot.common.json.JSONObject;
import com.iiot.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取地址类
 *
 * @author ruoyi
 */
public class AddressUtils {
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    public static final String IP_URL = "http://ip.taobao.com/service/getIpInfo.php";

    public static String getRealAddressByIP(String ip) {
        String address = "XX XX";

        // 内网不查询
        if (IpUtils.internalIp(ip)) {
            return "内网IP";
        }
        if (Global.isAddressEnabled()) {
            String rspStr = HttpUtils.sendPost(IP_URL, "ip=" + ip);
            if (StringUtils.isEmpty(rspStr)) {
                log.error("获取地理位置异常 {}", ip);
                return address;
            }
            JSONObject obj;
            try {
                obj = JSON.unmarshal(rspStr, JSONObject.class);
                JSONObject data = obj.getObj("data");
                String region = data.getStr("region");
                String city = data.getStr("city");
                address = region + " " + city;
            } catch (Exception e) {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return address;
    }

    private static final String IP_URL1 = "https://ips.market.alicloudapi.com/iplocaltion";
    private static final String APP_CODE = "APPCODE 4b10a44593cb4f478c2b871a4abb9628";

    /**
     * 阿里云API市场 全球IP归属地查询
     * {
     *     "code": 100,
     *     "message": "success",
     *     "ip": "119.123.47.233",
     *     "result": {
     *         "nation": "中国",
     *         "en_short": "CN",
     *         "en_name": "China",
     *         "province": "广东省",
     *         "city": "深圳市",
     *         "district": "宝安区",
     *         "adcode": 440306,
     *         "lat": 22.55329,
     *         "lng": 113.88308
     *     }
     * }
     */
    public static String getRealAddressByIP1(String ip){
        // 内网不查询
        if (IpUtils.internalIp(ip)) {
            log.error("获取地理位置异常 内网不查询");
            return null;
        }

        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization",APP_CODE);

        String param = "ip=" + ip;
        String rspStr = HttpUtils.sendGet(IP_URL1, headers, param);
        if (StringUtils.isEmpty(rspStr)) {
            log.error("获取地理位置异常 {}", ip);
            return null;
        }
        return rspStr;
    }
}
