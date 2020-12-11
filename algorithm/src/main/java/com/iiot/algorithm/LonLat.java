package com.iiot.algorithm;

/**
 * 
* @ClassName: LonLat
* @Description: 经纬度相关的计算
*
 */
public class LonLat {
	private static final double EARTH_RADIUS = 6378.137;// 地球半径

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 
	* @Title: getDis
	* @Description: 计算两点经纬度距离
	* @param @param xy1
	* @param @param xy2
	* @param @return
	* @return double    返回类型 ，返回米
	* @throws
	 */
	public static double getDis(XY xy1, XY xy2) {
		double radLat1 = rad(xy1.getLat());
		double radLat2 = rad(xy2.getLat());
		double a = rad(xy1.getLat()) - rad(xy2.getLat());
		double b = rad(xy1.getLon()) - rad(xy2.getLon());
		double s = 2 * Math.asin(Math.sqrt(
				Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		//换算成米
		s = s * EARTH_RADIUS * 1000;
		//s = Math.round(s * 10000) / 10000;
		return s ;
	}
}
