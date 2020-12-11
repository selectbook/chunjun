package com.iiot.algorithm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        XY xy = Amap.Amp2G84(new XY(114.086494,22.547089));
        xy = null;
        // for(int i = -90;i <= 90;i+=10){
        // double dis = LonLat.getDis(new XY(0, i), new XY(1,i));
        // System.out.println(""+i+" "+dis);
        // }
        // for(double i = 22;i <= 40;i+=0.1){
        // for(double j = 110;j<114;j+=0.1){
        // XY xy = new XY(j,i);
        // XY amap = Amap.G842Amp(xy);
        // XY xy2 = Amap.Amp2G84(amap);
        // double dis = LonLat.getDis(xy, xy2);
        // System.out.println(""+i+","+j+" "+dis);
        // }
        // }


//		for (int x = 0; x < 10; x++) {
//			long t1 = System.currentTimeMillis();
//			for (int i = 0; i < 100000; i++) {
//				XY xy = new XY(100, 8);
//				XY amap = Amap.G842Amp(xy);
//				//XY xy2 = Amap.Amp2G84(amap);
//				//double dis = LonLat.getDis(xy, xy2);
//			}
//			long t2 = System.currentTimeMillis();
//			System.out.println("x:" + (t2 - t1));
//		}

        assertTrue(true);
    }

}
