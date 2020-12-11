package com.iiot.net;

import com.iiot.common.ptest.NumSave;
import com.iiot.util.ExceptionUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author huangleping
 * @ClassName: ConnTimeout
 * @Description: 检测死连接
 * @date 2017年5月15日 下午8:48:41
 */
public class ConnTimeout {
    Logger logger = Logger.getLogger(ConnTimeout.class);

    HashMap<ChannelHandlerContext, ConnState> connStateMap = new HashMap<>();
    Thread thread;
    volatile boolean isRunning = true;

    private ConnTimeout() {

    }

    // 单例
    static ConnTimeout SINGLE = new ConnTimeout();

    static public ConnTimeout getInstance() {
        return SINGLE;
    }

    public void start() {
        // 开启线程检测
        thread = new Thread(new Runnable() {
            private void doRun() {
                long now = System.currentTimeMillis();
                synchronized (connStateMap) {
                    Iterator<Entry<ChannelHandlerContext, ConnState>> iter = connStateMap.entrySet().iterator();
                    while (iter.hasNext()) {
                        Entry<ChannelHandlerContext, ConnState> entry = iter.next();
                        ChannelHandlerContext ctx = entry.getKey();
                        ConnState state = entry.getValue();
                        long activeMill = 0;
                        if (state != null) {
                            activeMill = state.getTimeActive();
                        }

                        if (now - activeMill > 10 * 60 * 1000) {
                            logger.info("连接超时，主动断开：" + ctx);
                            if (ctx != null) {
                                ctx.close();
                            }

                            NumSave.add("connClosedZ", 1);
                            iter.remove();
                        }
                    }
                }
            }

            @Override
            public void run() {
                while (isRunning) {
                    doRun();
                    try {
                        Thread.sleep(1000 * 30);
                    } catch (InterruptedException e) {
                        logger.error("检测连接超时线程退出：" + ExceptionUtil.getStackStr(e));
                        return;
                    }
                }
            }
        });

        thread.start();
    }

    public void stop() {
        isRunning = false;
        thread.interrupt();

    }

    // 有连接上线
    public void onActive(ChannelHandlerContext ctx) {
        synchronized (connStateMap) {
            long now = System.currentTimeMillis();
            ConnState state = new ConnState(now, now);
            connStateMap.put(ctx, state);
        }
    }

    // 有连接断开
    public void onInactive(ChannelHandlerContext ctx) {
        synchronized (connStateMap) {
            connStateMap.remove(ctx);
        }
    }

    // 连接收到数据
    public void onConnRecv(ChannelHandlerContext ctx) {
        synchronized (connStateMap) {
            ConnState state = connStateMap.get(ctx);
            if (state != null) {
                state.setTimeActive(System.currentTimeMillis());
            } else {
                logger.info("未找到连接状态：" + ctx);
            }
        }
    }

    /**
     * @author huangleping
     * @ClassName: ConnState
     * @Description: 连接状态
     * @date 2017年5月15日 下午8:51:55
     */
    class ConnState {
        long timeCreate;
        long timeActive;

        public ConnState() {
            super();
            timeCreate = System.currentTimeMillis();
        }

        public ConnState(long timeCreate, long timeActive) {
            super();
            this.timeCreate = timeCreate;
            this.timeActive = timeActive;
        }

        public long getTimeCreate() {
            return timeCreate;
        }

        public void setTimeCreate(long timeCreate) {
            this.timeCreate = timeCreate;
        }

        public long getTimeActive() {
            return timeActive;
        }

        public void setTimeActive(long timeActive) {
            this.timeActive = timeActive;
        }
    }
}
