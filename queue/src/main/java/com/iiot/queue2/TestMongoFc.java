package com.iiot.queue2;

import com.iiot.util.PropertiesUtilLocal;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TestMongoFc {
    static Logger logger = Logger.getLogger(TestMongoFc.class);
    static MongoFC fc = new MongoFC(1000, 1000000, PropertiesUtilLocal.getConfigAsInt("fileCachedSizeInKB"));

    static List<Thread> threadList = new LinkedList<>();

    static class WT implements Runnable {

        @Override
        public void run() {
            long t1, t2;
            t1 = System.currentTimeMillis();

            t2 = System.currentTimeMillis();
            logger.info("new t:" + (t2 - t1));

            int dateSize = PropertiesUtilLocal.getConfigAsInt("dateSize");
            byte[] data = new byte[dateSize];
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (i % 256);
            }

            int writeCount = PropertiesUtilLocal.getConfigAsInt("writeCount");

            for (int j = 0; j < 100000; j++) {
                t1 = System.currentTimeMillis();
                for (int i = 0; i < writeCount; i++) {
                    fc.push("" + i, data);
                }
                t2 = System.currentTimeMillis();
                logger.info("push:" + (t2 - t1) + ",count:" + writeCount);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    logger.error(e.getLocalizedMessage());

                }
            }

        }
    }

    static class RT implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                MongoFC.MongoFcReader reader = fc.getReader();
                if (reader == null) {
                    try {
                        Thread.sleep(1000 * 3);
                    } catch (InterruptedException e) {
                        logger.error(e.getLocalizedMessage());

                    }
                    continue;
                }
                long t1, t2;
                t1 = System.currentTimeMillis();

                int devCount = 0;
                int readCount = 0;
                while (true) {
                    Map<String, List<byte[]>> map;
                    try {
                        map = reader.fetch();
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                    if (map == null) {
                        break;
                    } else {
                        devCount += map.size();
                        Iterator<Entry<String, List<byte[]>>> iter = map.entrySet().iterator();
                        while (iter.hasNext()) {
                            readCount += iter.next().getValue().size();
                        }
                    }
                }

                t2 = System.currentTimeMillis();
                logger.info("read:" + (t2 - t1) + ",devCount:" + devCount + ",readCount:" + readCount);

                try {
                    Thread.sleep(1000 * 3);
                } catch (InterruptedException e) {
                    logger.error(e.getLocalizedMessage());

                }
            }
            // reader = fc.getLastReader();
            // if (reader == null) {
            // try {
            // Thread.sleep(1000);
            // } catch (InterruptedException e) {
            // logger.error(e.getLocalizedMessage());
            //
            // }
            // continue;
            // }
            // while (true) {
            // Map<String, List<byte[]>> map;
            // try {
            // map = reader.fetch();
            // } catch (IOException e) {
            // e.printStackTrace();
            // break;
            // }
            // if (map == null) {
            // break;
            // } else {
            // devCount += map.size();
            // Iterator<Entry<String, List<byte[]>>> iter =
            // map.entrySet().iterator();
            // while (iter.hasNext()) {
            // readCount += iter.next().getValue().size();
            // }
            // }
            // }
        }
    }

    static public void run() {

//		for (int i = 0; i < 4; i++) {
//			Thread t = new Thread(new WT());
//			t.start();
//			threadList.add(t);
//		}

        for (int i = 0; i < 1; i++) {
            Thread t = new Thread(new RT());
            t.start();
            threadList.add(t);
        }

        logger.info("end");
        // try {
        // Thread.sleep(1000 * 1000);
        // } catch (InterruptedException e) {
        //
        // }
        // fc.shutdown();
    }

    static public void shutdown() {
        for (Thread t : threadList) {
            t.stop();
        }
        MongoFC.MongoFcReader reader = fc.getLastReader();
        try {
            while (null != reader.fetch()) ;
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());

        }
    }
}
