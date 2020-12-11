package com.iiot.net;

import com.alibaba.fastjson.JSON;
import com.iiot.commCommon.FillData;
import com.iiot.commCommon.ProtocolBase;
import com.iiot.commCommon.TransformUpResult;
import com.iiot.commCommon.World;
import com.iiot.common.bytes.HexStr;
import com.iiot.common.ptest.NumSave;
import com.iiot.jdbc.JdbcBaseInfo;
import com.iiot.source.Fill;
import com.iiot.util.ExceptionUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ProcIIOTServer extends SimpleChannelInboundHandler<Object> {
    private static Logger logger = Logger.getLogger(ProcIIOTServer.class);
    static final String C_TYPE = "TCP"; // 通信协议
    ProtocolBase protocol; // 解析协议
    private static int posCount = 0; // 收到的数据量
    private static Fill fill;
    private static com.iiot.commCommon.Fill baseFill;
    private static Properties kafkaProps;
    private static KafkaProducer<String, byte[]> producer;
    private static String topic = "fill";

    // Proc是否连接,默认是连接不正常
    private static boolean proc809ITOTIsConnected = false;

    public ProcIIOTServer() {

    }

    public ProcIIOTServer(ProtocolBase protocol) {
        this.protocol = protocol;
    }

    @SuppressWarnings("unchecked")
    static TransformUpResult doProc(ProtocolBase protocol, byte[] array, ChannelHandlerContext ctx, String tcpUdp)
            throws Exception {
        // 计算处理时间
        long start = System.currentTimeMillis(); // 数据处理开始时间
        // 获得ip
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = insocket.getAddress().getHostAddress();
        // 将接收到的数据给解析成通用体和下发数据
        TransformUpResult res = null;
        try {
            fill = new Fill();
            res = fill.TransformUp(array, tcpUdp);
//			res = protocol.TransformUp(array, tcpUdp);
        } catch (Exception e) {
            // logger.info("数据解析发生异常: " + HexStr.toStr(array) + "<===>" +
            // ExceptionUtil.getStackStr(e));
            NumSave.add("Exception", 1);
            logger.error("解析发生错误" + e.getLocalizedMessage());
            return null;
        }

        NumSave.add("parserCount", 1); // 解析数据包数
        long end = System.currentTimeMillis(); // 数据处理结束时间
        long diff = end - start; // 处理延时
        NumSave.add("parserDelay", diff); // 解析数据延时

        if (res == null) {
            // logger.info("数据解析失败: " + HexStr.toStr(array));
            NumSave.add("Error", 1);
            return null;
        }

        // 取出结构体
        World world = res.getWorld();
        byte[] result = res.getReplyArray();
        JedisPoolConfig config;
        //
        if (world != null) {
            String objType = world.getObjType() == null ? "" :
                    world.getObjType().toUpperCase();
            switch (objType) {
				case "ONLINE":
					logger.info("终端上线开机应答包：" + HexStr.toStr(result));
//					SendMethod.send(result);
                    ServerIIOT.send(ctx, result);
					break;
				case "DEVHEARTBEAT":
					logger.info("终端心跳应答包：" + HexStr.toStr(result));
//					SendMethod.send(result);
                    ServerIIOT.send(ctx, result);
					break;
				case "STARTFILL":
					logger.info("终端开始加注应答包：" + HexStr.toStr(result));
//					SendMethod.send(result);
                    ServerIIOT.send(ctx, result);
					break;
				case "FINISHFILL":
					logger.info("终端加注完成应答：" + HexStr.toStr(result));
                    SimpleDateFormat sdfAddTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat sdfRegisterTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date now = new Date();
//					SendMethod.send(result);
                    ServerIIOT.send(ctx, result);

                    FillData fillData = (FillData) world.getObj();
                    String devId = world.getId();

                    // 将最后一次加注信息（是不是也应该包括位置信息）存入redis
                    // 当前只是需要 当前加注量 这个值，用来计算一台设备的加注总量，因为设备会定时清空总加注量
                    config = new JedisPoolConfig();
                    // 设置最大连接数
                    config.setMaxTotal(30);
                    config.setMaxIdle(10);
                    // 测试redis
                    JedisPool jedisPool = new JedisPool(config, "192.168.0.208", 6379);
                    // 生产redis
//                    JedisPool jedisPool = new JedisPool(config, "127.0.0.1", 6379);

                    Jedis jedis = null;
                    float fillAmountFromRedis = 0;
                    try {
                        jedis = jedisPool.getResource();
                        jedis.auth("iiot123456");
                        if (jedis != null && devId != null) {
                            String json = jedis.get("fill" + devId);
                            com.iiot.commCommon.Fill fill = JSON.parseObject(json, com.iiot.commCommon.Fill.class);
                            if (fill != null) {
                                fillAmountFromRedis = fill.getRealTotalAmount();
                            }
                        }
                    } catch (Exception e) {
                        logger.error("获取redis最后加注数据失败. " + ExceptionUtil.getStackStr(e));
                    }

                    baseFill = new com.iiot.commCommon.Fill();
                    baseFill.setDevId(devId);
                    baseFill.setIp(ip);
                    float currentAmount = fillData.getCurrentAmount();
                    baseFill.setCurrentAmount(currentAmount);
                    float leftTankAmount = fillData.getLeftTankAmount();
                    baseFill.setLeftTankAmount(leftTankAmount);
//                    baseFill.setRegisterTime(new java.sql.Date(fillData.getDevTime().getTime()));
                    baseFill.setSettingAmount(fillData.getSettingAmount());
                    float totalAmount = fillData.getTotalAmount();
                    baseFill.setTotalAmount(fillData.getTotalAmount());
                    baseFill.setTankCapacity(fillData.getTankCapacity());
                    baseFill.setAddTime(Timestamp.valueOf(sdfAddTime.format(fillData.getDevTime()))); // 终端时间
                    baseFill.setRegisterTime(Timestamp.valueOf(sdfRegisterTime.format(now))); // 网关时间，当前时间
                    baseFill.setIfOffline(new Character('1'));
                    float realTotalAmount = 0;
                    if (fillAmountFromRedis == 0) {
                        realTotalAmount = fillAmountFromRedis + totalAmount;
                    } else {
                        realTotalAmount = fillAmountFromRedis + currentAmount;
                    }

                    baseFill.setRealTotalAmount(realTotalAmount);



                    // 将加注总量存入redis
                    try {
                        if (jedis != null && baseFill !=null) {
                            jedis.set("fill" + devId, JSON.toJSONString(baseFill));
                        } else {
                            jedis = jedisPool.getResource();
                            jedis.set("fill" + devId, JSON.toJSONString(baseFill));
                        }
                    } catch (Exception e) {
                        logger.error("往REDIS中存入加注信息失败: " + ExceptionUtil.getStackStr(e));
                    } finally {
                        // 释放资源
                        if(jedis != null){
                            jedis.close();

                        }
                        // 释放连接池
                        if(jedisPool != null){
                            jedisPool.close();
                        }
                    }


                    // 将统计结构放入queue,目前是放kafka queue，之后redis,数据库操作都可以放进去
//                    AllQueue.pushObj(world);
                    try {
                        kafkaProps = new Properties();
                        kafkaProps.put("bootstrap.servers", "cdh1.macro.com:9092,cdh2.macro.com:9092,cdh3.macro.com:9092");
                        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
                        producer = new KafkaProducer<>(kafkaProps);

                        ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(topic,
                                JSON.toJSONString(baseFill).getBytes());
                        producer.send(record);
                    } catch (Exception e) {
                        logger.error("未写入kafka" + ExceptionUtil.getStackStr(e));
                    }


                    // 插入加注数据到Mysql数据库
                    JdbcBaseInfo.insertDataIntoFill(baseFill);
                    // 更新油桶余量
                    String oildCode = JdbcBaseInfo.selectOilCodeByDevCode(devId);
                    JdbcBaseInfo.updateDrum(leftTankAmount, oildCode);

					break;

				default:
//					SendMethod.send(result);
                    ServerIIOT.send(ctx, result);
			}
        }
        return res;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        NumSave.add("packetTcp", 1);

        byte[] array = null;
        try {
            array = (byte[]) msg;
            posCount++;
            logger.info(HexStr.toStr(array));

        } catch (ClassCastException e) {
            logger.error("Data IIOT conversion error: " + ExceptionUtil.getStackStr(e));
            return;
        }

        // 解析
        TransformUpResult res = doProc(protocol, array, ctx, C_TYPE);

    }

    // netty未处理的异常都会在这里
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!cause.getLocalizedMessage().equals("Connection IIOT reset by peer")) {
            logger.error("Proc IIOT exceptionCaught: " + cause.getLocalizedMessage()
                    + ExceptionUtil.getStackStr(cause));
        }
    }

    // 新的客户端连接上来了
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("IIOT active");
        proc809ITOTIsConnected = true;
        NumSave.add("conn", 1);
        NumSave.add("connSum", 1);
        // 增加到连接管理
        ConnTimeout.getInstance().onActive(ctx);
    }

    // 客户端连接关闭
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        proc809ITOTIsConnected = false;
        System.out.println("IIOT inActive");
        NumSave.add("conn", -1);
        NumSave.add("connClosed", 1);
        // 从连接管理中删除
        ConnTimeout.getInstance().onInactive(ctx);
    }

    public static boolean procIIOIIsConnected() {
        return proc809ITOTIsConnected;
    }

}
