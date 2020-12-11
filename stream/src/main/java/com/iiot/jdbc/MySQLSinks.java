package com.iiot.jdbc;

import com.iiot.bean.InSufficient;
import com.iiot.commCommon.Fill;
import com.iiot.util.DBUtils;
import com.iiot.util.DBUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

public class MySQLSinks extends RichSinkFunction<List<InSufficient>> {
    private PreparedStatement ps;
    private Connection connection;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        //获取数据库连接，准备写入数据库
        connection = DBUtils.getConnection();
        String sql = "insert into xt_report_alarm(dev_code, type, time_begin, create_time, remain_amount) values (?, ?, ?, ?, ?); ";
        ps = connection.prepareStatement(sql);
    }

    @Override
    public void close() throws Exception {
        super.close();
        //关闭并释放资源
        if(connection != null) {
            connection.close();
        }

        if(ps != null) {
            ps.close();
        }
    }

    @Override
    public void invoke(List<InSufficient> inSufficients, Context context) throws Exception {
        for(InSufficient inSufficient : inSufficients) {
            ps.setString(1, inSufficient.getDev_code());
            ps.setString(2, inSufficient.getAlarmType());
            ps.setTimestamp(3, new Timestamp(inSufficient.getTimeBegin()));
            ps.setTimestamp(4, new Timestamp(inSufficient.getCreateTime()));
            ps.setDouble(5, inSufficient.getRemainAmount());
            ps.addBatch();
        }

        //一次性写入
        int[] count = ps.executeBatch();
        System.out.println("成功写入Mysql数量：" + count.length);

    }


}
