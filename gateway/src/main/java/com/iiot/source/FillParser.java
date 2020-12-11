package com.iiot.source;

import com.iiot.commCommon.*;
import com.iiot.commCommon.Fill;
import com.iiot.common.bytes.Conv;
import com.iiot.jdbc.BaseInfoCached;
import com.iiot.jdbc.JdbcBaseInfo;
import com.iiot.protocol.fill.FillAck;
import com.iiot.protocol.fill.FillHead;
import com.iiot.protocol.fill.FillPacket;
import com.iiot.protocol.fill.FillPacketInfo;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析数据，将数据解析为通用结构体
 *
 * @author
 */
public class FillParser {

    private static Logger log = Logger.getLogger(FillParser.class);

    // 用于809，是否把状态和报警放入扩展Map中
    public static boolean cachedFlag = false;

    /**
     * 解析上发数据
     *
     * @param array
     * @param funs
     * @param pro
     * @return
     * @throws Exception
     */
    public static final TransformUpResult parserData(byte[] array, Funs funs, String pro) throws Exception {
        if (array == null) {
            log.debug("Parser parserData mothod param null");
            return null;
        }

        FillHead head = new FillHead();
        FillPacketInfo info = new FillPacketInfo();

        // 平台应答数据
        byte[] reply = null;

        // 数据长度
        int packet_len = array.length;

        // 校验数据,解析包
        if (!FillPacket.checkPacket(array, 0, packet_len, head, info)) {
            // log.debug("Parser parserData packet xor error");
            return null;
        }

        // String strBody = HexStr.toStr(head.getBody());

        //
        byte[] devId_array = head.getDevId();
        String devIdStr = new String(devId_array);

        World world = null;

        // 数据上发的转换结果 包括上发数据、平台应答数据
        TransformUpResult transformUpResult = null;


        // 获取消息ID
        int cmdType = head.getCmdType();

        // 默认给通用应答，且默认结果OK
        reply = FillAck.packetDownCommon(head.getDevId(), head.getSerialNum() + 1, cmdType);
        switch (cmdType) {
            case 0x01: // 开始加注
                log.info("收到开始加注数据包");
                world = doParseStartFill(head, devIdStr);
                transformUpResult = new TransformUpResult();
                if (world != null) {
                    transformUpResult.setWorld(world);
                }
                reply = FillAck.packetDownStartFill(head.getDevId(), head.getSerialNum() + 1, cmdType);
                transformUpResult.setReplyArray(reply);
                break;

            case 0x0a: // 加注完成
                log.info("收到加注完成数据包");
                world = doParseFinishFill(head, devIdStr);
                transformUpResult = new TransformUpResult();
                if (world != null) {
                    transformUpResult.setWorld(world);
                }
                reply = FillAck.packetDownFillSuccess(head.getDevId(), head.getSerialNum() + 1, cmdType);
                transformUpResult.setReplyArray(reply);
                break;

            case 0x03:
                // 开机上线事件, 判断该设备是否存在与数据库中，如果不存在
                log.info("收到开机上线事件数据包");
                world = doParseOnline(head, devIdStr);
                transformUpResult = new TransformUpResult();
                if (world != null) {
                    transformUpResult.setWorld(world);
                }
//                Devices baseInfo = BaseInfoCached.getFillDevInfo(devIdStr);
                Devices devices = JdbcBaseInfo.getFillInfoByDevID(devIdStr);
                if (devices == null) {
                    // 数据库中没有录入该终端ID,应答注册失败
                    reply = FillAck.packetDownRegisterFail(head.getDevId(), head.getSerialNum() + 1, cmdType);
                    transformUpResult.setReplyArray(reply);

                } else {
                    // 如果数据库中有该终端ID，应答注册成功
                    reply = FillAck.packetDownRegisterSuccess(head.getDevId(), head.getSerialNum() + 1, cmdType);
                    transformUpResult.setReplyArray(reply);


                }
                break;

            case 0x06: // 设备心跳上传命令
                log.info("收到设备心跳数据包");
                world = doParseHeartBeat(head, devIdStr);
                transformUpResult = new TransformUpResult();
                if (world != null) {
                    transformUpResult.setWorld(world);
                }
                reply = FillAck.packetDownHeartBeat(head.getDevId(), head.getSerialNum() + 1, cmdType);
                transformUpResult.setReplyArray(reply);
                break;

            default:
                // log.info("Unprocessed messages order: " + msg_id);
                world = new World();
                world.setId(devIdStr); // 终端号
                world.setSequenceId(head.getSerialNum()); // 流水号
                transformUpResult = new TransformUpResult();
                transformUpResult.setWorld(world);
                transformUpResult.setReplyArray(reply);
                break;
        }
        return transformUpResult;
    }

    /**
     * 解析开始加注数据包
     * @param head
     * @param devIdStr
     * @return
     */
    private static World doParseStartFill(FillHead head, String devIdStr) {
        byte[] bodyData = head.getBody();
        if (bodyData == null || bodyData.length < 15) {
            return null;
        }
        // 数据包个数
        int packNum = head.getPackNum();
        int offset = 0;
        int len = bodyData.length;
        // 数据包ID
        byte packId = bodyData[offset++];

        // 用于存放扩展数据
        Map<String, Object> map = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 包数据长度
        int packlen = bodyData[offset++];
        // 设定量
        float settingAmount = Conv.getFloat(bodyData, offset);
        offset += 4;

        // 包ID 时间戳是0x06
        byte packId2 = bodyData[offset++];
        // 包长度
        byte packlen2 = bodyData[offset++];
        // 终端时间
        int year = Conv.getShortNetOrder(bodyData, offset);
        offset += 2;
        int month = bodyData[offset++];
        int date = bodyData[offset++];
        int hour = bodyData[offset++];
        int minute = bodyData[offset++];
        int second = bodyData[offset++];

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, date, hour, minute, second);
        Date devTime = calendar.getTime();

        FillData fillData = new FillData();
        fillData.setDevTime(devTime);
        fillData.setSettingAmount(settingAmount);

        // 封装World数据
        World world = new World();
        world.setObjType("StartFill"); // 数据类型

        world.setSequenceId(head.getSerialNum()); // 流水号
        world.setId(devIdStr); // 终端号
        world.setObj(fillData);
        return world;
    }

    /**
     * 解析加注完成数据包
     * @param head
     * @param devIdStr
     * @return
     */
    private static World doParseFinishFill(FillHead head, String devIdStr) {
        byte[] bodyData = head.getBody();
        if (bodyData == null || bodyData.length < 15) {
            return null;
        }
        // 数据包个数
        int packNum = head.getPackNum();
        int offset = 0;
        int len = bodyData.length;
        // 数据包ID
        byte packId = bodyData[offset++];

        // 用于存放扩展数据
        Map<String, Object> mapFinish = new HashMap<String, Object>();
        SimpleDateFormat sdfFinish = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 包数据长度
        int settingAmountPacklen = bodyData[offset++];
        // 设定量
        float settingAmount1 = Conv.getFloat(bodyData, offset);
        offset += settingAmountPacklen;

        // 包id
        byte currentAmountId = bodyData[offset++];
        // 包长度
        int currentAmountLen = bodyData[offset++];
        // 当前加注量
        float currentAmount = Conv.getFloat(bodyData, offset);
        offset += currentAmountLen;
        // 总加注量
        byte totalAmountId = bodyData[offset++];
        int totalAmountLen = bodyData[offset++];
        float totalAmount = Conv.getFloat(bodyData, offset);
        offset += totalAmountLen;
        // 油桶总量
        byte tankCapacityId = bodyData[offset++];
        int tankCapacityLen = bodyData[offset++];
        float tankCapacity = Conv.getFloat(bodyData, offset);
        offset += tankCapacityLen;
        // 油桶余量
        byte leftTankAmountId = bodyData[offset++];
        int leftTankAmountLen = bodyData[offset++];
        float leftTankAmount = Conv.getFloat(bodyData, offset);
        offset += leftTankAmountLen;

        // 包ID 时间戳是0x06
        byte packId2 = bodyData[offset++];
        // 包长度
        byte packlen2 = bodyData[offset++];
        // 终端时间
        int year = Conv.getShortNetOrder(bodyData, offset);
        offset += 2;
        int month = bodyData[offset++];
        int date = bodyData[offset++];
        int hour = bodyData[offset++];
        int minute = bodyData[offset++];
        int second = bodyData[offset++];

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, date, hour, minute, second);
        Date devTime = calendar.getTime();

        FillData fillData = new FillData();
        fillData.setDevTime(devTime);
        fillData.setSettingAmount(settingAmount1);
        fillData.setCurrentAmount(currentAmount);
        fillData.setTotalAmount(totalAmount);
        fillData.setTankCapacity(tankCapacity);
        fillData.setLeftTankAmount(leftTankAmount);

        // 封装World数据
        World world = new World();
        world.setObjType("finishFill"); // 数据类型
        world.setSequenceId(head.getSerialNum()); // 流水号
        world.setId(devIdStr); // 终端号
        world.setObj(fillData);
        return world;
    }

    /**
     * 解析设备心跳上传命令
     *
     * @param head  FillHead
     * @param devId 终端编号
     * @return World
     */
    private static World doParseHeartBeat(FillHead head, String devId) {
        if (head == null) {
            return null;
        }

        // 用于存放扩展数据
        Map<String, Object> map = new HashMap<String, Object>();

        // 获取消息体数据
        byte[] bodyData = head.getBody();
        if (bodyData == null || bodyData.length < 9) {
            return null;
        }

        // 数据包个数
        int packNum = head.getPackNum();

        int offset = 0;
        int len = bodyData.length;

        // 数据包ID
        int packId = bodyData[offset++];
        // 数据长度 7
        int packLen = bodyData[offset++];
        // 数据体 一个时间戳
        int year = Conv.getShortNetOrder(bodyData, offset);
        offset += 2;
        int month = bodyData[offset++];
        int date = bodyData[offset++];
        int hour = bodyData[offset++];
        int minute = bodyData[offset++];
        int second = bodyData[offset++];

        Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, date, hour, minute, second);
		Date devTime = calendar.getTime();
		map.put("devTime", devTime);

        // 封装World数据
        World world = new World();
        world.setObjType("devHeartbeat"); // 数据类型
        world.setSequenceId(head.getSerialNum()); // 流水号
        world.setId(devId); // 终端号
        world.setObj(map);
        return world;
    }

    /**
     * 设备开机上线
     *
     * @param head  FillHead
     * @param devId 终端编号
     * @return World
     */
    private static World doParseOnline(FillHead head, String devId) {
        if (head == null) {
            return null;
        }

        // 用于存放扩展数据
        Map<String, Object> map = new HashMap<String, Object>();

        // 获取消息体数据
        byte[] bodyData = head.getBody();
        if (bodyData == null || bodyData.length < 9) {
            return null;
        }

        // 数据包个数
        int packNum = head.getPackNum();

        int offset = 0;
        int len = bodyData.length;

        // 数据包ID
        int packId = bodyData[offset++];
        // 数据长度 7
        int packLen = bodyData[offset++];
        // 数据体 一个时间戳
        int year = Conv.getShortNetOrder(bodyData, offset);
        offset += 2;
        int month = bodyData[offset++];
        int date = bodyData[offset++];
        int hour = bodyData[offset++];
        int minute = bodyData[offset++];
        int second = bodyData[offset++];

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, date, hour, minute, second);
        Date devTime = calendar.getTime();
        map.put("devTime", devTime);


        // 封装World数据
        World world = new World();
        world.setObjType("online"); // 数据类型
        world.setSequenceId(head.getSerialNum()); // 流水号
        world.setId(devId); // 终端号
        world.setObj(map);
        return world;
    }


}
