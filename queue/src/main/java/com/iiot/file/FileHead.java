package com.iiot.file;


import com.iiot.common.bytes.Conv;

public class FileHead {
    static String fixStr = "BSJKJ";
    //固定字节（BSJKJ 5)+组件版本（2字节） +数据个数（4）+数据尾偏移[整个文件]（4）
    byte[] fix;
    int version;
    //数据个数（4）
    long count;
    //数据尾偏移[整个文件]
    long fileOffset;

    //内容的长度，方便外部预读数据
    public static int getNeedLen() {
        return fixStr.length() + 2 + 4 + 4;
        //return 15;
    }

    //序列化
    public byte[] toArray() {
        int len = getNeedLen();
        byte array[] = new byte[len];
        int offset = 0;
        System.arraycopy(fix, 0, array, offset, fix.length);
        offset += fix.length;

        //高位在前，低位在后
        Conv.setShortNetOrder(array, offset, version);
        offset += 2;
        Conv.setIntNetOrder(array, offset, count);
        offset += 4;
        Conv.setIntNetOrder(array, offset, fileOffset);
        offset += 4;
        return array;
    }

    public void fromArray(byte array[]) throws Exception {
        int len = getNeedLen();
        if (len != array.length) {
            throw new Exception("长度不等");
        }
        int offset = 0;
        for (int i = 0; i < fix.length; i++) {
            if (fix[i] != array[offset + i]) {
                throw new Exception("头不对");
            }
        }
        offset += fix.length;

        //高位在前，低位在后
        int version = Conv.getShortNetOrder(array, offset);
        if (this.version != version) {
            throw new Exception("版本不对");
        }
        offset += 2;

        count = Conv.getIntNetOrder(array, offset);
        offset += 4;
        fileOffset = Conv.getIntNetOrder(array, offset);
        offset += 4;
    }

    /**
     * 获取偏移数字
     */
    public static int getOffsetNum(byte[] head) {
        byte[] point = new byte[4];
        System.arraycopy(head, 11, point, 0, 4);
        int num = (int) Conv.getIntNetOrder(point, 0);
        return num;
    }

    public FileHead() {
        fix = fixStr.getBytes();
        version = 1;

    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getFileOffset() {
        return fileOffset;
    }

    public void setFileOffset(long fileOffset) {
        this.fileOffset = fileOffset;
    }


}
