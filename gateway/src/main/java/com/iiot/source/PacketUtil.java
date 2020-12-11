package com.iiot.source;

import com.iiot.common.bytes.Conv;
import com.iiot.common.bytes.HexStr;
import org.apache.log4j.Logger;

import java.util.zip.CRC32;


/**
 * JT工具方法
 *
 * @author
 */
public class PacketUtil {

    private static Logger log = Logger.getLogger(PacketUtil.class);

    private static final byte[] ESC_BYTES = {0x7E, 0x7D};
    private static final byte ESCAPE = 0x7D;
    private static final byte ESC_XOR = 0x7C;

    /**
     * 获取校验结果和校验值
     *
     * @param data 反转义数据，完整包
     *             //	 * @param offset 起始索引
     *             //	 * @param pac_len 数据长度
     * @return
     */
    public static int[] getXorAndResult(byte[] data) {
        // 第一个元素为校验结果，0：校验失败，1：校验成功
        // 第二个元素为校验值
        int[] array = new int[2];
        if (data == null) {
            return null;
        }
        int offset = 0;
        int len = data.length;

        int xorC = CheckXor(data, offset + 1, len - 3);
        int xor = 0;
        if (len >= 2) {
            xor = Byte.toUnsignedInt(data[len - 2]);
        }
        if (xorC != xor) {
            array[0] = 0;
            array[1] = xorC;
        } else {
            array[0] = 1;
        }

        return array;
    }

    /**
     * CS接收过来的JT数据存在校验错误和长度不对的数据
     * 因此对数据进行判断，若存在这样的错误就解析数据
     * 重新组包，true：需要解析数据 false：不需要解析数据
     *
     * @param data 反转义数据，完整包
     *             //	 * @param offset 起始索引
     *             //	 * @param pac_len 数据长度
     * @return
     */
    public static boolean isParserData(byte[] data) {
        if (data == null) {
            return false;
        }

        int offset = 0;
        int len = data.length;

        // 校验是否通过
        int xorC = CheckXor(data, offset + 1, len - 3);
        int xor = 0;
        if (len >= 2) {
            xor = Byte.toUnsignedInt(data[len - 2]);
        }
        if (xorC != xor) {
            return true;
        } else {
            // 长度校验是否通过
            int prop = 0;
            if (offset + 5 <= len) {
                prop = Conv.getShortNetOrder(data, offset + 3);
            }

            int body_len = prop & 0x3FF; // 消息体长度
            int pac_item = (prop & 0x2000) != 0 ? 4 : 0; // 是否有消息包封装项

            // 数据长度 包头 + 消息头 + 消息体 + 校验 + 包尾
            int pac_len = 1 + 12 + pac_item + body_len + 1 + 1;
            if (pac_len != len) {
                return true;
            }
        }

        return false;
    }

    /**
     * 校验码：从消息头开始到校验前一字节的所有字节异或
     *
     * @param data     反转义的数据，去掉包头、包尾
     * @param offset   消息ID所对应的索引
     * @param data_len 校验的数据长度
     * @return
     */
    public static int CheckXor(byte[] data, int offset, int data_len) {
        int n = 0;
        for (int i = offset; i < offset + data_len; i++) {
            n ^= data[i];
        }
        return n & 0xFF;
    }


    /**
     * CRC32校验 从消息头开始到校验前一个字节的所有字节
     *
     * @param data
     * @param offset
     * @param length
     * @return
     */
    public static long crc32(byte[] data, int offset, int length) {
        byte[] realArray = new byte[length];
        System.arraycopy(data, offset, realArray, 0, length);
        System.out.println(HexStr.toStr(realArray));
        CRC32 crc32 = new CRC32();
        crc32.reset();//Resets CRC-32 to initial value.
        crc32.update(realArray, offset, length);//将数据丢入CRC32解码器
        return crc32.getValue();//获取CRC32 的值  默认返回值类型为long 用于保证返回值是一个正数
    }


    /**
     * 获取数据中的一部分
     *
     * @param data   源数据
     * @param offset 起始索引
     * @param len    获取元素个数
     * @return
     */
    public static byte[] getBody(byte[] data, int offset, int len) {

        byte[] body = new byte[len];
        for (int i = 0; i < len; i++) {
            body[i] = data[offset + i];
        }
        return body;
    }

    /**
     * 计算消息体长度
     *
     * @param prop 消息体属性
     * @return
     */
    public static int getDataLen(int prop) {

        // 消息体长度
        int packet_len = prop & 0x3FF;
        return packet_len;
    }

    /**
     * 计算消息头长度
     *
     * @param prop 消息体属性
     * @return
     */
    public static int getHeadLen(int prop) {

        // 消息包封装项
        int pack_item = (prop & 0x2000) != 0 ? 4 : 0;

        // 消息头长度
        int headLen = 12 + pack_item;
        return headLen;

    }

    /**
     * 数据反转义
     *
     * @param data    整包数据
     * @param offset  包头位置
     * @param pac_len 数据长度
     * @return
     */
    public static byte[] escapeReverseData(byte[] data, int offset, int pac_len) {
        int index = offset + 1; // 去掉包头
        int len = offset + pac_len - 1; // 去掉包尾
        int data_offset = 0;
        byte[] array = new byte[len]; // 存储反转义好的数据
        int count = 0; // 计数器
        // 反转义
        for (int i = index; i < len; i++) {
            if (data[i] == ESCAPE) {
                // 0x7d后面接0x02
                if (data[i + 1] == 0x02) {
                    array[data_offset++] = 0x7e;
                    i += 1;
                } else if (data[i + 1] == 0x01) { // 0x7d后面接0x01
                    array[data_offset++] = 0x7d;
                    i += 1;
                } else { // 0x7d后面即不是0x01，也不是0x02。不进行反转义
                    array[data_offset++] = data[i];
                }
            } else {
                array[data_offset++] = data[i];
            }
            count += 1;
        }
        byte[] esc_pack = new byte[count];
        System.arraycopy(array, 0, esc_pack, 0, count);
        return esc_pack;
    }

    /**
     * 转义数据
     *
     * @param data     要转义的数据(包含包头、包尾)
     * @param offset   起始所以
     * @param data_len 数据长度
     *                 //	 * @param esc_bytes { 0x7e, 0x7d }
     *                 //	 * @param escape 0x7d
     *                 //	 * @param esc_xor 0x7C
     * @return
     */
    public static byte[] escapeData(byte[] data, int offset, int data_len) {
        if (data == null) {
            return null;
        }
        int newOffset = offset + 1; // 去掉包头
        int len = offset + data_len - 1; // 去掉包尾
        int index = 0; // 索引
        byte[] array = new byte[data_len * 2]; // 此数组用于存放转义的数据
        int count = 0; // 计数器
        for (int i = newOffset; i < len; i++) {
            boolean isFound = false;
            for (byte x : ESC_BYTES) {
                if (x == data[i]) {
                    array[index] = ESCAPE;
                    array[index + 1] = (byte) (x ^ ESC_XOR); // 要么为0x01，要么为0x02
                    index += 2;
                    count += 1;
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                array[index] = data[i];
                index += 1;
            }
        }

        // 组包
        int escIndex = 0;
        byte[] esc_data = new byte[data_len + count];
        int escLen = esc_data.length;
        esc_data[escIndex++] = 0x7E; // 包头
        System.arraycopy(array, 0, esc_data, escIndex, escLen - 2);
        escIndex += escLen - 2;
        esc_data[escIndex] = 0x7E; // 包尾
        return esc_data;
    }

    /**
     * 判断是否需要转义数据的条件是数据中是否包含7D或者7E
     *
     * @param data 一包数据
     * @return true：转义数据，false：不转义数据
     */
    public static boolean isNeedEscape(byte[] data) {
        if (data == null) {
            return false;
        }

        // 从1开始取值，是去掉包头。
        // 数组的长度减1，是去掉包尾。
        boolean flag = false; // 默认不需要转义数据
        int len = data.length;
        for (int i = 1; i < len - 1; i++) {
            if (data[i] == 0x7D || data[i] == 0x7E) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     * 找包头
     *
     * @param data   要分包的数据
     * @param offset 给定的起始索引
     * @return
     */
    public static int getPosHead(byte[] data, int offset) {
        int posHead = -1;
        if (data == null) {
            return posHead;
        }
        for (int i = offset; i < data.length; i++) {
            byte tmp = data[i];
            if (tmp == (byte) 0x7e) {
                posHead = i;
                break;
            }
        }
        return posHead;
    }

    /**
     * 找包尾
     *
     * @param data   要分包的数据
     * @param offset 通过消息头计算所得到的包尾索引
     * @return
     */
    public static int getPosTail(byte[] data, int offset) {
        int posTail = -1;
        if (data == null) {
            return posTail;
        }
        for (int i = offset; i < data.length; i++) {
            byte tmp = data[i];
            if (tmp == (byte) 0x7e) {
                posTail = i;
                break;
            }
        }
        return posTail;
    }

    /**
     * 统计一包数据转义多少次
     *
     * @param data    整包数据
     * @param offset  起始索引
     * @param pac_len 整包数据长度
     * @return
     */
    public static int escapeCount(byte[] data, int offset, int pac_len) {
        if (data == null) {
            return 0;
        }
        int index = offset + 1; // 去掉包头
        int len = offset + pac_len - 1; // 去掉包尾
        int count = 0; // 计数器
        for (int i = index; i < len; i++) { // 反转义
            if (data[i] == ESCAPE) {
                if (i != len - 1) { // 防越界
                    if (data[i + 1] == 0x01 || data[i + 1] == 0x02) { // 0x7d后面接0x01或者0x02
                        count += 1;
                    }
                }
            }
        }

        return count;
    }

    /**
     * 校验结果
     *
     * @param data   未反转义数据，完整包
     * @param offset 消息ID所对应的索引
     * @return
     */
    public static boolean getCheckResult(byte[] data, int offset, int pac_len) {
        if (data == null) {
            return false;
        }

        // 反转义数据
        byte[] esc = escapeReverseData(data, offset, pac_len);

        int len = esc.length;
        int xorC = CheckXor(esc, 0, len - 1);
        int xor = Byte.toUnsignedInt(esc[len - 1]);
        if (xorC != xor) {
            log.info("JTPacket xor error");
            return false;
        }

        return true;
    }

    public static void main(String[] args) {

        byte[] array = HexStr.toArray("553a00250058543233332d3030303030300104010440a00000020440800000030445136800");
        byte[] allArray = HexStr.toArray("553a00250058543233332d3030303030300103010440a00000020440800000030445136800EE191752");

//		CRC32 crc32 = new CRC32();
//		crc32.reset();//Resets CRC-32 to initial value.
//		crc32.update(array, 0, array.length);//将数据丢入CRC32解码器
//		int value = (int) crc32.getValue();//获取CRC32 的值  默认返回值类型为long 用于保证返回值是一个正数
//        System.out.println(array.length);
        long checkXor = PacketUtil.crc32(array, 0, array.length);
//        long checkXor2 = Conv.IIOTCheckXor(array, 0, array.length);
        System.out.println(checkXor);
//        System.out.println(checkXor2);

//        long xor = Conv.getIntNetOrder(allArray, 37);
//        long xor1 = Conv.IIOTCheckXor(allArray, 0, 37);
        long xor2 = PacketUtil.crc32(allArray, 0, 37);
//        System.out.println(xor);
//        System.out.println(xor1);
        System.out.println(xor2);

    }
}
