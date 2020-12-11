package com.iiot.queue2;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.List;


public class App {
    static Logger logger = Logger.getLogger(DataQueue2.class);

    static DataQueue2 dq = new DataQueue2(new DataQueue2.DataQueue2Param("nn", 10, 6));

    static int dataSize = 1024;

    public static void main(String[] args) {
        // 设置LOGGER
        PropertyConfigurator.configure("conf/log4j.properties");
        logger.info("begin");

        // TestMongoFc.run();
        //
        // Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        //
        // @Override
        // public void run() {
        // TestMongoFc.shutdown();
        // }
        // }));

        // int wc = 1;
        // int rc = 4;
        // for (int i = 0; i < wc; i++) {
        // Thread t = new Thread(new WT());
        // t.start();
        // }
        //
        // for (int i = 0; i < rc; i++) {
        // Thread t = new Thread(new RT());
        // t.start();
        // }

        // long t1, t2;
        // t1 = System.currentTimeMillis();
        // dq.shutdown();
        // t2 = System.currentTimeMillis();
        // logger.info("shutdown:" + (t2 - t1));

        DataQueue2 dq11 = new DataQueue2(new DataQueue2.DataQueue2Param("nn\\abc", 10, 6));
        DataQueue2 dq12 = new DataQueue2(new DataQueue2.DataQueue2Param("nn\\abc2", 10, 6));

        DataQueue2 dq21 = new DataQueue2(new DataQueue2.DataQueue2Param("nn\\abcd", 10, 6));
        DataQueue2 dq22 = new DataQueue2(new DataQueue2.DataQueue2Param("nn\\abcd2", 10, 6));

        byte[] d1 = new byte[10240];
        dq11.push(d1);
        dq11.pop(10);

//		List<DataQueue2> ddList = new LinkedList<>();
//		for (int k = 0; k < 100; k++) {
//			DataQueue2 dd = new DataQueue2(new DataQueue2Param("nn\\abcd" + k, 2, 1));
//			for (int i = 0; i < 10000; i++) {
//				dd.push(d1);
//			}
//			ddList.add(dd);
//		}
//
//		for (DataQueue2 dd : ddList) {
//			List<byte[]> ll;
//			do {
//				ll = dd.pop(100);
//
//			} while (ll != null && !ll.isEmpty());
//		}
//		for (DataQueue2 dd : ddList) {
//			dd.shutdown();
//			
//		}
//		ddList.clear();


        d1 = new byte[1024];
        dq12.push(d1);
        dq12.pop(10);

        d1 = new byte[1024];
        dq21.push(d1);
        dq21.pop(10);

        d1 = new byte[1024];
        dq22.push(d1);
        //dq22.pop(10);

        dq11.shutdown();
        dq12.shutdown();
        dq21.shutdown();
        dq22.shutdown();

        logger.info("end");
        try {
            Thread.sleep(1000 * 1000);
        } catch (InterruptedException e) {
            logger.error(e.getLocalizedMessage());


        }


    }

    static class WT implements Runnable {

        @Override
        public void run() {

            byte b[] = new byte[dataSize];
            for (int i = 0; i < b.length; i++) {
                b[i] = (byte) (i % 256);
            }
            long t1 = System.currentTimeMillis();
            int count = 1024 * 1024 * 1024;
            // for (int i = 0; i < count; i++) {
            while (true) {
                dq.push(b);
                // try {
                // Thread.sleep(100);
                // } catch (InterruptedException e) {
                // logger.error(e.getLocalizedMessage());
                //
                // }
            }
            // long t2 = System.currentTimeMillis();
            // logger.info("push:" + (t2 - t1));
        }

    }

    static class RT implements Runnable {

        @Override
        public void run() {
            long t1, t2;

            long tpop1 = System.currentTimeMillis();
            int popCount = 100000;
            int sumT = 0;
            int sumC = 0;
            int sumB = 0;
            // for (int i = 0; i < popCount; i++) {
            while (true) {
                t1 = System.currentTimeMillis();
                int smallC = 4000;
                List<byte[]> aa = dq.pop(smallC);
                if (aa == null || aa.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error(e.getLocalizedMessage());

                    }
                    continue;
                }
                for (byte[] bb : aa) {
                    sumB += bb.length;
                    if (bb.length != dataSize) {
                        logger.error("bb.length != dataSize");
                    }
                }

                // t2 = System.currentTimeMillis();
                // if (i % 100 == 0) {
                // logger.info("pop:" + i + "," + sumT + ",avg:" + (sumC * 1.0 /
                // (sumT / 1000.0)) / 1024 / 1024
                // + ",avg2:" + (sumB * 1.0 / (sumT / 1000.0)) / 1024 / 1024);
                // sumT = 0;
                // sumB = 0;
                // } else {
                // sumT += (t2 - t1);
                // sumC += smallC * dataSize;
                // }
            }
            // long tpop2 = System.currentTimeMillis();
            // logger.info("allpop:" + (tpop2 - tpop1));
        }

    }

}
