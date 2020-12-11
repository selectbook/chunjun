package com.iiot.cacheFile;

import com.alibaba.fastjson.JSON;
import com.iiot.commCommon.World;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2017/6/30.
 */
public class CacheFileUtil {

    static Logger logger = Logger.getLogger(CacheFileUtil.class);

    //标记
    public class Mark{

        public static final String TIME_FORMAT = "yyyy-MM-dd HH-mm-ss";

        String id;

        String value;

        //刚进来的时候用一次，用来标记开始块
        int startPos;

        //每次所需要写入的块位置
        int endPos;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getEndPos() {
            return endPos;
        }

        public void setEndPos(int endPos) {
            this.endPos = endPos;
        }

        public int getStartPos() {
            return startPos;
        }

        public void setStartPos(int startPos) {
            this.startPos = startPos;
        }
    }

    public List<Mark> getCacheList() {
        List<Mark> result = new ArrayList<>();
        synchronized (lock) {
            if (oldFile == null) {
                result.addAll(cacheList);
                oldFile = new FileUtil(filePathOld);
                logger.info("读取文件:" + filePathOld);
                Map<Integer, List<Integer>> cacheNum = fileUtil.getCacheNum();
                int lineSize = fileUtil.getSize();
                oldFile.setCacheNum(cacheNum);
                oldFile.setSize(lineSize);
                fileUtil.deleteOldFile();
                long nowDate = System.currentTimeMillis();
                fileUtil = new FileUtil(pathOld, fileNameOld + nowDate + suffix, capacityOld);
                filePathOld = pathOld + File.separator + fileNameOld + nowDate + suffix;
                oldLength = oldFile.getFileLength();
                parallelism.clear();
                cacheList.clear();
            }
        }
        return result;
    }

    public void setCacheList(List<Mark> cacheList) {
        this.cacheList = cacheList;
    }

    //删除文件list
    List<String> deleteList = new LinkedList<>();

    //单体锁对象
    Object lock = new Object();

    //缓存关系
    List<Mark> cacheList = new ArrayList<>();

    //对等关系
    Map<String, Mark> parallelism = new HashMap<>();

    //当前的文件操作类，用来写
    FileUtil fileUtil = null;

    //上一个文件操作类，用来读取
    private FileUtil oldFile = null;

    //上一个文件地址
    private String filePathOld = "";

    //文件后缀
    private String suffix = ".txt";

    //地址
    private String pathOld = "";

    //文件名称
    private String fileNameOld = "";

    //容器
    private int capacityOld = 0;

    //之前长度
    private long oldLength = 0;

    //读取长度
    private int readSize = 100;

    //每次读取的字节
    private byte[] cacheByte = null;

    //读取用的
    private List<Mark> readCacheList = null;

    //读取标记
    private static int readIndex = 0;

    //读取标签
    private static int readMark = 0;

    public CacheFileUtil(String path, String fileName, int capacity) {
        pathOld = path;
        fileNameOld = fileName;
        capacityOld = capacity;
        long nowDate = System.currentTimeMillis();
        filePathOld = path + File.separator + fileName + nowDate + suffix;
        fileUtil = new FileUtil(path,  fileName + nowDate + suffix, capacity);
    }

    /**
     * 删除掉
     */
    private void deleteOldFile(){
        if (oldFile != null) {
            oldFile.deleteOldFile();
            readCacheList = null;
        }
        StringBuffer deleteFile = new StringBuffer(oldFile.delFilePath);
        oldFile = null;
        //防止删除不掉的情况
        if (deleteList != null && !deleteList.isEmpty()) {
            int deleteSize = deleteList.size();
            for (int i = 0; i < deleteSize; i++) {
                String fileName = deleteList.get(i);
                boolean b = new File(fileName).delete();
                System.err.println("要删除的文件1：" + fileName);
                if (b) {
                    deleteList.remove(i);
                    i--;
                }
            }
        }
        boolean b = new File(deleteFile.toString()).delete();
        if(!b){
            deleteList.add(deleteFile.toString());
        }
    }

    /**
     * 读取内容
     * @param i 块位置
     * @param flag 是否有偏移磁头情况
     * @return
     */
    private List<byte[]> readCache(int i, boolean flag) {
        //跳
        if (flag) {
            return oldFile.getReadListBytes(i, flag);
        } else {
            return getReadCache(i, true);
        }

    }

    /**
     * 读取内容
     * @param i 对应的块
     * @param b true有偏磁头情况，反之没有
     * @return
     */
    private List<byte[]> getReadCache(int i, boolean b) {
        int size = oldFile.getSize();
        //开始位置
        int begin = i * size;
        //读取段落
        int paragraph = readSize * size;
        //结束位置
        int end = begin + paragraph;
        //选取的
        int selection = size * readSize;
        if (end > oldLength) {
            end = (int) oldLength;
            //获得选取字段
            selection = end - begin;
        }
        if(!b){
            if (cacheByte == null) {
                cacheByte = new byte[size * readSize];
            }
            if (i % readSize == 0) {
                if(readMark != 0){
                    oldFile.seek(readMark * readSize + 1);
                }
                oldFile.readLineBytes(cacheByte, 0, selection);
                readMark++;
            }
        }
        if (b) {
            int length = (i % readSize) * size;
            int endLength = length + size;
            if (endLength > cacheByte.length) {
                endLength = cacheByte.length;
            }
            byte[] countStr = Arrays.copyOfRange(cacheByte, length, endLength);
            List<byte[]> result = oldFile.getReadListByStr(countStr, i);
            return result;
        } else {
            return null;
        }
    }

    /**
     * 写入内容
     * @param id 主键id
     * @param bytes 内容字节
     */
    public void write(String id, byte[] bytes) {
        int line;
        Mark mark;
        Mark cacheMark;
        synchronized (lock) {
            //是否有相同的数据
            if (parallelism.containsKey(id)) {
                mark = parallelism.get(id);
                int startPos = mark.startPos;
                int endPos = mark.endPos;
                int newLine = fileUtil.writeOld(bytes, endPos);
                if (endPos != newLine) {
                    cacheList.add(null);
                    mark.setEndPos(newLine);
                    int cacheLine = startPos - 1;
                    cacheMark = cacheList.get(cacheLine);
                    cacheMark.setValue(cacheMark.getValue() + ";" + newLine);
                    cacheList.set(cacheLine, cacheMark);
                }
            //没有id相同的数据
            } else {
                mark = new Mark();
                //写入新的
                line = fileUtil.writeNew(bytes);
                mark.setEndPos(line);
                mark.setStartPos(line);
                cacheMark = new Mark();
                cacheMark.setId(id);
                cacheMark.setValue(line + "");
                cacheList.add(cacheMark);
            }
            parallelism.put(id, mark);
        }
    }

    /**
     * 读取所有的文件内容
     * @param i 对应的块
     * @param listCache 块内容所对应的每个内容长度和位置
     * @return
     */
    private List<byte[]> readAllBytesCache(int i, List<Mark> listCache) {
        //找到之前的位置信息
        Mark mark = listCache.get(i);
        //每次进来调用一下
        getReadCache(i, false);
        if(mark == null){
//            getReadCache(i,false);
            return null;
        }
        String value = mark.getValue();
        String[] valueArr = value.split(";");
        List<byte[]> list = new LinkedList<>();
        //一个id有多个行的数据(如果容器设置合理，不会出现此情况)
        if (valueArr.length > 1) {
            int pointer = i;
            for (int j = 0 ; j < valueArr.length; j ++) {
                int posInt = Integer.parseInt(valueArr[j]);
                //如果相近就替换读取行数
                if((posInt - pointer) == 1){
                    pointer = posInt;
                }
                if (j == 0) {
                    list.addAll(readCache(i, false));
                //跳磁头
                } else {
                    list.addAll(readCache(posInt - 1, true));
                }
            }
            //偏移磁头
            oldFile.seek(pointer + 1);
        } else {
            list = readCache(i, false);
        }
        return list;
    }

    /**
     * 测试数据是否准确时候用
     * @param i
     * @param id
     * @param countCheck
     * @return
     */
    private List<byte[]> readAllBytesCache(int i, String id, String countCheck) {
        Mark mark = cacheList.get(i);
        if (mark == null) {
            return null;
        }
        String value = mark.getValue();
        String[] valueArr = value.split(";");
        List<byte[]> list = new LinkedList<>();
        //一个id有多个行的数据(如果容器设置合理，不会出现此情况)
        if (valueArr.length > 1) {
            int pointer = i;
            for (int j = 0; j < valueArr.length; j++) {
                int posInt = Integer.parseInt(valueArr[j]);
                //如果相近就替换读取行数
                if ((posInt - pointer) == 1) {
                    pointer = posInt;
                }
                if (j == 0) {
                    list.addAll(readCache(i, false));
                } else {
                    list.addAll(readCache(posInt - 1, true));
                }
            }
            oldFile.seek(pointer + 1);
        } else {
            list = readCache(i, false);
        }
        if(id.equals(mark.getId())){
            System.err.println("进入id为：" + mark.getId());
            for (byte[] s : list) {
                String str = new String(s);
                if(str.equals(countCheck)){
                    System.err.println("有此内容" + str);
                }
            }
        }
        return list;
    }

    public void seek(int line){
        fileUtil.seek(line);
    }

    /**
     * 读取
     * @return
     */
    public FileResult readNext() {
        if (readCacheList == null) {
            readIndex = 0;
            readMark = 0;
            readCacheList = getCacheList();
        }
        boolean delete = false;
        FileResult result = new FileResult();
        if (readCacheList != null && !readCacheList.isEmpty()) {
            Map<String, List<byte[]>> data = new HashMap<>();
            int size = readCacheList.size();
            int length = readIndex + 10;
            //读取到末尾了
            if (length >= size) {
                length = size;
                result.setNext(false);
                delete = true;
            }
            for (int i = readIndex; i < length; i++) {
                Mark mark = readCacheList.get(i);
                List<byte[]> listData = readAllBytesCache(i, readCacheList);
                if (mark != null) {
                    data.put(mark.getId(), listData);
                }
            }
            readIndex = readIndex + 10;
            result.setData(data);
        } else {
            result.setNext(false);
            delete = true;
        }
        //读取完了就删除
        if(delete){
            deleteOldFile();
        }
        return result;
    }


    public static void main(String[] args) throws InterruptedException {
        
//        }
    }

    
}
