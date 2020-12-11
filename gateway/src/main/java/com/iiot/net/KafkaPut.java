package com.iiot.net;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import com.iiot.commCommon.FillData;
import com.iiot.commCommon.World;
import com.iiot.common.ptest.NumSave;
import com.iiot.util.ExceptionUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;


public class KafkaPut {

    Properties kafkaProps;
    int count = 100;
    static Logger logger = Logger.getLogger(KafkaPut.class);

    KafkaProducer<String, byte[]> producer;

    public void start() {
        kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "cdh1.macro.com:9092,cdh2.macro.com:9092,cdh3.macro.com:9092");
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        producer = new KafkaProducer<>(kafkaProps);
    }

    public void stop() {
        producer.close();
    }

    public void send() {
        // 装载结构体的的集合
        List<World> worldList = new ArrayList<World>();
        try {
            // 从queue中取出数据
            Queue<World> data = AllQueue.onsPop(count);
            if (data == null || data.isEmpty()) {
                return;
            }

            worldList.addAll(data);
        } catch (Exception e) {
            logger.error("AllQueue.onsPop(count) fail：" + ExceptionUtil.getStackStr(e));
            return;
        }

        int writeNum = 0;
        int size = worldList.size();
        String topic = "fill";
        for (; writeNum < size; writeNum++) {
            long start = System.currentTimeMillis(); // 开始时间
            try {
                // 从集合中获取对象
                World world = worldList.get(writeNum);
                if (world == null) {
                    continue;
                }

                FillData fillData = (FillData) world.getObj();
                String devId = world.getId();
                com.iiot.commCommon.Fill baseFill = new com.iiot.commCommon.Fill();

				baseFill = new com.iiot.commCommon.Fill();
				baseFill.setDevId(devId);

				SimpleDateFormat sdfAddTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat sdfRegisterTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date now = new Date();

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


                ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(topic,
                        JSON.toJSONString(baseFill).getBytes());
                producer.send(record);

            } catch (Exception e) {
                if (e instanceof NullPointerException) {
                    logger.error("未写入kafka：" + (size - writeNum) + "," + ExceptionUtil.getStackStr(e));
                } else {
                    logger.error("未写入kafka NULL：" + (size - writeNum) + "," + ExceptionUtil.getStackStr(e));
                }
                NumSave.add("OnsFail：", size - writeNum);
                // 如果出现异常，将没有写入kafka的数据，重写push到queue中
                for (int i = writeNum; i < size; i++) {
                    try {
                        AllQueue.onsPushObj(worldList.get(i));
                    } catch (Exception e1) {
                        logger.error("数据写入AllQueue中失败: " + ExceptionUtil.getStackStr(e));
                        return;
                    }
                }
                return;
            }
            long end = System.currentTimeMillis(); // 结束时间
            long diff = end - start; // 处理延时
            NumSave.add("kafkaDelay", diff); // 总延时
        }
        NumSave.add("OnsOK", writeNum);
    }

}
