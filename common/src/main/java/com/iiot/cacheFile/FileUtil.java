package com.iiot.cacheFile;

import com.iiot.util.ExceptionUtil;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/27.
 */
public class FileUtil {

    static Logger logger = Logger.getLogger(FileUtil.class);

    private RandomAccessFile raf = null;

    private int line;

    //每块对应的内容
    private Map<Integer, List<Integer>> cacheNum = new HashMap<>();

    //最后位置的标识符号
    private byte lingByte = '\n';

    public int size = 0;

    public static String delFilePath = "";

    private byte[] bytesSize;

    private Object lock = new Object();


    public long getFileLength(){
        long length = 0;
        try {
            length = raf.length();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return length;
    }

    /**
     * 初始化
     * @param path 地址
     * @param fileName 文件名
     */
    public FileUtil(String path, String fileName,int capacity) {
        size = capacity * 1024;
        bytesSize = new byte[size];
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }else {
                delAllFile(path);
            }
            String filePath = path + File.separator  + fileName;
            raf = new RandomAccessFile(filePath,"rw");
            line = 0;
        } catch (Exception e) {
            logger.info(ExceptionUtil.getStackStr(e));
        }
    }

    //删除文件
    private boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
        }
        return flag;
    }

    /**
     * 跳磁头
     * @param line
     */
    public void seek(int line){
        long pointer = getSkipByLine(line);
        try {
            raf.seek(pointer);
        } catch (IOException e) {
            logger.info(ExceptionUtil.getStackStr(e));
        }
    }

    public FileUtil(String path) {
        try {
            raf = new RandomAccessFile(path, "rw");
            delFilePath = path;
        } catch (FileNotFoundException e) {
            logger.info(ExceptionUtil.getStackStr(e));
        }
    }

    /**
     * 关闭
     */
    public void deleteOldFile() {
        synchronized (raf) {
            try {
                raf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写存在的块
     * @param bytes 内容
     * @param lineParam 对应哪个块
     * @return
     */
    public int writeOld(byte[] bytes, int lineParam) {
        synchronized (lock) {
            //跳标
            long skip = getSkipByLine(lineParam);
            //写入的字节长度
            int byteLength = bytes.length;
            //该行有哪些被写入
            List<Integer> valueList = cacheNum.get(lineParam);
            //根据每行写入的算出总值
            int length = posByList(valueList);
            //写入位置
            int strLength = length + byteLength;
            //如果比之前的大
            if (strLength > (size - 1)) {
                //另起一行写入
                lineParam = writeNew(bytes);
                return lineParam;
            }
            valueList.add(byteLength);
            cacheNum.put(lineParam, valueList);
            //跳写的位置
            skip = skip + length;
            try {
                raf.seek(skip);
                raf.write(bytes);
                //文件指针放到最后
                raf.seek(raf.length());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lineParam;
    }

    /**
     * 写新的块
     * @param bytes 内容
     * @return
     */
    public int writeNew(byte[] bytes) {
        synchronized (lock) {
            List<Integer> valueList = new LinkedList<>();
            try {
                int length = bytes.length;
                int leg = size - length;
                if(leg <= 0){
                	return 0;
                }
                //数据补充
                byte[] supplement = new byte[leg];
                //换行符
                supplement[supplement.length - 1] = lingByte;
                raf.write(bytes);
                raf.write(supplement);
                line++;
                valueList.add(length);
                cacheNum.put(line, valueList);
            } catch (Exception e) {
            	e.printStackTrace();
                logger.info(ExceptionUtil.getStackStr(e));
            }
        }
        return line;
    }


    /**
     * 读取某块的内容
     * @param i 对应的块
     * @return
     */
    private byte[] readLineBytes(int i) {
        synchronized (raf) {
            try {
                raf.seek(getSkipByLine(i + 1));
                raf.read(bytesSize);
            } catch (IOException e) {
                e.printStackTrace();
                logger.info(ExceptionUtil.getStackStr(e));
            }
        }
        return bytesSize;
    }

    /**
     * 读取一部分的内容放到内存种
     * @param bytes
     * @param start 开始位置
     * @param end 结束位置
     * @return
     */
    public byte[] readLineBytes(byte[] bytes, int start, int end) {
        synchronized (raf) {
            try {
                raf.read(bytes, start, end);
            } catch (IOException e) {
                logger.info(ExceptionUtil.getStackStr(e));
            }
        }
        return bytes;
    }

    /**
     *
     * @param paramInt
     * @param flag
     * @return
     */
    public List<byte[]> getReadListBytes(int paramInt, boolean flag) {
        List<byte[]> result = new LinkedList<>();
        byte[] content = readLineBytes(paramInt);
        List<Integer> list = cacheNum.get(paramInt + 1);
        if (list == null || list.isEmpty()) {
            result.add(content);
            return result;
        }
        int listSize = list.size();
        int length = 0;
        for (int i = 0; i < listSize; i++) {
            int contentSize = list.get(i) + length;
            byte[] subStr = Arrays.copyOfRange(content, length, contentSize);
            result.add(subStr);
            length = contentSize;
        }
        return result;
    }

    public List<byte[]> getReadListByStr(byte[] contentBytes, int paramInt) {
        List<byte[]> result = new LinkedList<>();
        List<Integer> list = cacheNum.get(paramInt + 1);
        int listSize = list.size();
        int length = 0;
        for (int i = 0; i < listSize; i++) {
            int contentSize = list.get(i) + length;
            byte[] subStr = Arrays.copyOfRange(contentBytes, length, contentSize);
            result.add(subStr);
            length = contentSize;
        }
        return result;
    }

    /**
     * 计算将要写入的位置
     * @param lineParam
     * @return
     */
    public long getSkipByLine(int lineParam) {
        return size * (long)(lineParam - 1);
    }


    private int posByList(List<Integer> list){
        int result = 0;
        if(list == null || list.isEmpty()){
            return result;
        }
        for(Integer integer : list){
            result = result + integer;
        }
        return result;
    }

    public Map<Integer, List<Integer>> getCacheNum() {
        return cacheNum;
    }

    public void setCacheNum(Map<Integer, List<Integer>> cacheNum) {
        this.cacheNum = cacheNum;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        bytesSize = new byte[size];
    }
}
