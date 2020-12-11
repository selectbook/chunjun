package com.iiot.algorithm;

import org.apache.log4j.Logger;

import Jama.Matrix;

/**
 * 
* @ClassName: MaxLikelihood
* @Description: 最大似然估计，算法参考 http://www.doc88.com/p-5035849404649.html
*
 */
public class MaxLikelihood {
	static Logger logger = Logger.getLogger(MaxLikelihood.class);
	
	public static XY DoMaxLikelihood(XY xy[],double dis[]){
		int len = xy.length;
		if(dis.length != len){
			logger.warn("xy dis length not equal");			
			return null;
		}
		//组合矩阵
		Matrix L = new Matrix(len,1);
		Matrix A = new Matrix(len,2);
		
		double xn = xy[len - 1].x;
		double yn = xy[len - 1].y;
		double dn = dis[len - 1];
		
		for(int i = 0;i<len - 1;i++){
			double xi = xy[i].x;
			double yi = xy[i].y;
        	double di = dis[i];
        	
        	L.set(i, 0, Math.pow(xi,2) - Math.pow(xn,2) + Math.pow(yi,2) - Math.pow(yn,2) + Math.pow(dn,2) - Math.pow(di,2));
        }
		
		
		for(int i = 0;i<len - 1;i++){
			double xi = xy[i].x;
			double yi = xy[i].y;
        	
        	A.set(i, 0,2 * (xi- xn));
        	A.set(i, 1,2 * (yi- yn));
		}
		
		Matrix result = null;
		try{
			//（（A的转置 * A）的逆）* （A的转置） * L
			result = A.transpose().times(A).inverse().times(A.transpose()).times(L);
		}catch(Exception e){
			logger.warn("matrix call error:"+e.getLocalizedMessage());
			return null;
		}
		XY ret = new XY(result.get(0, 0), result.get(1, 0));
		return ret;
	}
}
