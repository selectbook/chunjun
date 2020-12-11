package com.catelf.demo;


import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WordCount {
    public static void main(String[] args) throws Exception {
        //步骤一：初始化程序入口:打开本地页面
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        //步骤二：数据的输入
        DataStreamSource<String> dataStraem = env.socketTextStream("localhost",
                8888);
        //步骤三：数据的处理
        SingleOutputStreamOperator<Tuple2<String, Integer>> result =
                dataStraem.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
                    public void flatMap(String line, Collector<Tuple2<String, Integer>>
                            out) throws Exception {
                        String[] fields = line.split(",");
                        for (String word : fields) {
                            out.collect(new Tuple2<String, Integer>(word, 1));
                        }
                    }
                }).keyBy(0)
                        .sum(1);
        //步骤四：结果输出
        result.print();
        //步骤五：启动程序
        env.execute("word count...");
    }
}
