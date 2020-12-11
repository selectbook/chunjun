package com.catelf.demo;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class WindowWordCountJava {
    public static void main(String[] args) throws Exception {
        //flink提供的工具类，获取传递的参数
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String hostname = parameterTool.get("hostname");
        int port = parameterTool.getInt("port");
        //步骤一：获取执行环境
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        //步骤二：获取数据源
        DataStream<String> dataStream = env.socketTextStream(hostname, port);
        //步骤三：执行逻辑操作
        DataStream<WordCount> wordAndOneStream = dataStream.flatMap(new FlatMapFunction<String, WordCount>() {
            public void flatMap(String line, Collector<WordCount> out) {
                String[] fields = line.split(",");
                for (String word : fields) {
                    out.collect(new WordCount(word, 1L));
                }
            }
        });
        DataStream<WordCount> resultStream = wordAndOneStream.keyBy("word")
                .sum("count");
        //步骤四：结果打印
        resultStream.print();
        //步骤五：任务启动
        env.execute("WindowWordCountJava");
    }

    public static class WordCount {
        public String word;
        public long count;

        //记得要有这个空构建
        public WordCount() {
        }

        public WordCount(String word, long count) {
            this.word = word;
            this.count = count;
        }

        @Override
        public String toString() {
            return "WordCount{" +
                    "word='" + word + '\'' +
                    ", count=" + count +
                    '}';
        }
    }
}
