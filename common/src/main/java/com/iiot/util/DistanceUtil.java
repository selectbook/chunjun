package com.iiot.util;

/**
 *
 *
 *  根据经纬度求两个点之间的距离，单位是米
 *
 */

public class DistanceUtil {
    
	    private static final double EARTH_RADIUS = 6378.137; // 地球半径，单位km
 
	    private static double rad(double d)
	    {
	       return d * Math.PI / 180.0;
	    }

	    public static double GetDistance(double lat1, double lng1, double lat2, double lng2)
	    {
	       double radLat1 = rad(lat1);
	       double radLat2 = rad(lat2);
	       double a = radLat1 - radLat2;
	       double b = rad(lng1) - rad(lng2);
	       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + 
	        Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
	       s = s * EARTH_RADIUS*1000;
	       s = Math.round(s * 10000) / 10000;
	       return s;
	    }

	    
	    public static void main(String[] args) {
       
	    	double lat1 = 22.31004;
	    	double lng1 = 114.02560;
	    	double lat2 = 22.32004;
	    	double lng2 = 114.01560;
	    	
	    	double getDistance = GetDistance(lat1, lng1,lat2,lng2);
	    	
			System.out.println(getDistance);
			
		}
	    
}
