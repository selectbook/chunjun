package com.iiot.algorithm;


public class Amap {
    // 地球半径
    private static final double EARTH_RADIUS = 6378245;
    //??
    private static final double ee = 0.00669342162296594323;

    /**
     * @param @param  amap
     * @param @return
     * @return XY    返回类型
     * @throws
     * @Title: Amp2G84
     * @Description: 把高德偏移经纬度转为84坐标系
     */
    public static XY Amp2G84(XY amap) {
        XY ret = getAmapOffset(amap);
        if (ret == null) {
            return ret;
        }
        //减去
        ret.setX(amap.getX() - ret.getX());
        ret.setY(amap.getY() - ret.getY());
        return ret;
    }

    /**
     * @param @param  g84
     * @param @return
     * @return XY    返回类型
     * @throws
     * @Title: G842Amp
     * @Description: 84坐标转高德
     */
    public static XY G842Amp(XY g84) {
        XY ret = getAmapOffset(g84);
        if (ret == null) {
            return ret;
        }
        //减去
        ret.setX(g84.getX() + ret.getX());
        ret.setY(g84.getY() + ret.getY());
        return ret;
    }

    /**
     * @param @param  amap
     * @param @return
     * @return XY    返回类型
     * @throws
     * @Title: getAmapOffset
     * @Description: 得到高德偏移
     */
    private static XY getAmapOffset(XY amap) {
        double lng = amap.x;
        double lat = amap.y;
        if (isOutOfChina(lat, lng)) {
            return null;
        }
        double dLat = TransformLat(lng - 105.0, lat - 35.0);
        double dLng = TransformLng(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((EARTH_RADIUS * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
        dLng = (dLng * 180.0) / (EARTH_RADIUS / sqrtMagic * Math.cos(radLat) * Math.PI);
        return new XY(dLng, dLat);
    }

    /**
     * @param @param  lat
     * @param @param  lng
     * @param @return
     * @return boolean    返回类型
     * @throws
     * @Title: isOutOfChina
     * @Description: 中国之外
     */
    static boolean isOutOfChina(double lat, double lng) {
        if (lng < 72.004 || lng > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    static double TransformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }


    static double TransformLng(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }
}
