package com.iiot.common.utils;

import com.iiot.common.constant.Constants;

/**
 * Token工具类
 */
public class TokenUtils {

    /**
     * 设置Token缓存
     *
     * @param key   参数键
     * @param value 字典数据列表
     */
    public static void setTokenCache(String key, String value) {
        CacheUtils.put(getCacheName(), getCacheKey(key), value);
    }

    /**
     * 获取Token缓存
     *
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public static String getTokenCache(String key) {
        Object cacheObj = CacheUtils.get(getCacheName(), getCacheKey(key));
        return cacheObj == null ? null : cacheObj.toString();
    }

    /**
     * 清空Token缓存
     */
    public static void clearTokenCache() {
        CacheUtils.removeAll(getCacheName());
    }

    /**
     * 获取cache name
     *
     * @return 缓存名
     */
    public static String getCacheName() {
        return Constants.TOKEN_CACHE;
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey) {
        return Constants.TOKEN_KEY + configKey;
    }
}
