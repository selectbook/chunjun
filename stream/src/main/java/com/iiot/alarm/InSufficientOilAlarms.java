package com.iiot.alarm;

import com.alibaba.fastjson.JSONObject;
import com.iiot.bean.InSufficient;
import com.iiot.commCommon.Fill;
import com.iiot.jdbc.MySQLSinks;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.time.Time;
import java.util.List;

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;

import java.util.Properties;

public class InSufficientOilAlarms {
    public static void main(String[] args) throws Exception{
        //构建流执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //kafka
        Properties prop = new Properties();
        prop.put("bootstrap.servers", "cdh1.macro.com:9092,cdh2.macro.com:9092,cdh3.macro.com:9092");
//        prop.put("zookeeper.connect", "localhost:2181");
        prop.put("group.id", "fill6");
        prop.put("key.serializer", "org.apache.kafka.common.serialization.StringDeserializer");
        prop.put("value.serializer", "org.apache.kafka.common.serialization.StringDeserializer");
        prop.put("auto.offset.reset", "earliest");

        DataStreamSource<String> stream = env
                .addSource(new FlinkKafkaConsumer010<String>(
                        "fill",
                        new SimpleStringSchema(), prop)).
                //单线程打印，控制台不乱序，不影响结果
                setParallelism(1);

        //从kafka里读取数据，转换成Person对象
        DataStream<Fill> dataStream = stream.map(value ->
                JSONObject.parseObject(value, Fill.class)
        );

        SingleOutputStreamOperator<InSufficient> result = dataStream.map(new MapFunction<Fill, InSufficient>() {
                           @Override
                           public InSufficient map(Fill fill) throws Exception {
                               InSufficient inSufficient = new InSufficient();
                               Float leftTankAmount = fill.getLeftTankAmount();
                               Float tankCapacity = fill.getTankCapacity();
                               String devCode = fill.getDevId();
                               long timeBegin = fill.getAddTime().getTime();
                               System.out.println("devCode:-------------------------------------------------" + devCode);
                               String alarmType = "";
                               if ((leftTankAmount / tankCapacity) < 0.1 ) {
                                   alarmType = "inSufficientOil";
                                   inSufficient.setDev_code(devCode);
                                   inSufficient.setCreateTime(System.currentTimeMillis());
                                   inSufficient.setTimeBegin(timeBegin);
                                   inSufficient.setAlarmType(alarmType);
                                   inSufficient.setRemainAmount(leftTankAmount);
                               }
                               return inSufficient;
                           }
                       }

        );


        //收集5秒钟的总数
        result.timeWindowAll(Time.seconds(5L)).
                apply(new AllWindowFunction<InSufficient, List<InSufficient>, TimeWindow>() {

                    @Override
                    public void apply(TimeWindow timeWindow, Iterable<InSufficient> iterable, Collector<List<InSufficient>> out) throws Exception {
                        List<InSufficient> inSufficients = Lists.newArrayList(iterable);

                        if(inSufficients.size() > 0) {
                            System.out.println("5秒的总共收到的条数：" + inSufficients.size());
                            out.collect(inSufficients);
                        }

                    }
                })
                //sink 到数据库
                .addSink(new MySQLSinks());
        //打印到控制台
        //.print();

        env.execute("kafka 消费任务开始");
    }
}
