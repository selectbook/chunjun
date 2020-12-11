package com.iiot.file;

import com.iiot.util.ExceptionUtil;
import io.netty.util.internal.ConcurrentSet;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * @ClassName: FileDB
 * @Description: 文件存储数据 功能： 1，支持批量数据写入 2，支持批量数据读取 3，支持设置每个文件的最大size，多出后换文件写[NO]
 * 4，某个文件的数据已被读完后，删除临时文件 5，文件具备一定的自我校验能力，如果文件损坏可以删除文件，不影响后续操作
 * 6，支持同时读、写 指标： 1，单写/读入磁盘能基本达到磁盘的最大速率的80%，一般阿里云的高速硬盘在20-40M/s之间
 * 读写速度要达到16M/s
 * <p>
 * 异常： 1，数据的读入读出不保证顺序 2，如果文件损坏则文件的所有内容丢失
 * 3，掉电时可能造成数据丢失或文件损坏，目前没有使用日志的技术保证数据完整性
 * <p>
 * 实现思路： 1，文件的结构：文件头+数据内容 +内容2+...内容n
 * 2，文件头：固定字节（0xA937F8)+组件版本（2字节） +数据个数（4）+数据偏移[整个文件]（8）[NO]
 * 3，数据内容：固定头（BSJ 3字节）+内容长度（4字节）+数据XOR校验（1）+数据（N）
 * 4，内部是一个栈的数据结构实现，数据后进先出，数据总是往文件尾插入，也从文件尾读出[NO]
 * 5，为了不让外部写入卡往，数据先写到一个队列，再开启线程写入到文件[NO]
 * 6，每一次写入数据都是一个文件，文件名是一个uuid，保证不重复就行了
 * 7，文件读取：在头部记录一个偏移，读完后修改这个偏移，下次即使关闭程序也读知道读到哪了，如果数据被读完就删除这个文件
 */
public class FileDB {
    Logger logger = Logger.getLogger(FileDB.class);
    Object lock = new Object();
    List<byte[]> internelBuf = new LinkedList<byte[]>();
    // Set<String> fileSet = new HashSet<String>();
    String path;

    static final String TAIL_STR = "TAL";
    // Thread writeThread;
    // volatile boolean isRunning = true;
    // 用一个SET保存正在写的文件，以避免正在写的时候又去读
    Set<String> fileWritingSet = new ConcurrentSet<String>();

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param path 数据保存的路径
     * @throws Exception
     */
    public FileDB(String path) throws Exception {
        this.path = path;
        // 检查路径是否存在，不存在则尝试创建这个路径
        // 如果路径不能创建，那么抛出异常
        File folder = new File(path);
        if (!folder.exists()) {
            boolean ret = folder.mkdirs();
            if (!ret) {
                // 返回错误也不确认是否失败，再次检查目录是否存在
                if (!new File(path).exists()) {
                    throw new Exception("文件夹不能被创建：" + path);
                }
            }
        }

        // FileInputStream fis = new FileInputStream(path);
        // FileOutputStream fos = new FileOutputStream(path);
        // FileChannel fci = fis.getChannel();
        // FileChannel fco = fos.getChannel();

    }

    // public void start() {
    // // 检查文件
    // // 开启内部的写线程
    // writeThread = new Thread(new InternalWriter());
    // writeThread.start();
    // }
    //
    // public void stop() {
    // // 关闭内部的写线程
    // }

    /**
     * @throws IOException
     * @throws FileNotFoundException
     * @Title: write @Description: 外面写入数据 @param @param dataList @param @param
     * bufBytes 给出数据的总字节数，这样内部方便申请映射内存 @return void 返回类型 @throws
     */
    public void write(List<byte[]> dataList, long bufBytes) throws Exception {

        // long checkBytes = 0;
        // for (byte[] array : dataList) {
        // checkBytes += array.length;
        // }
        // if (bufBytes != checkBytes) {
        // throw new Exception("数据长度不对");
        // }

        // synchronized (lock) {
        // internelBuf.addAll(dataList);
        // }
        // 使用map写文件是足够快的，不需要使用其它的线程去写
        // Iterator<Entry<String, List<Object>>>
        // iterMap=objMap.entrySet().iterator();
        // Entry<String, List<Object>> entry = iterMap.next();
        // List<Object>dataList=entry.getValue();
        RandomAccessFile memoryMappedFile = null;
        String fileName = UUID.randomUUID().toString();
        fileWritingSet.add(fileName);
        try {
            try {

                memoryMappedFile = new RandomAccessFile(path + "/" + fileName, "rws");
            } catch (FileNotFoundException e) {
                logger.error("FileDB 创建文件失败：" + e.getLocalizedMessage());
                throw e;
            }
            try {
                // 评估需要的映射内存大小
                long memSize = FileHead.getNeedLen() + bufBytes
                        + (FileContent.getNeedLen() + TAIL_STR.length()) * dataList.size();
                MappedByteBuffer out = null;
                try {
                    out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, memSize);
                } catch (IOException e) {
                    logger.warn("FileDB 文件映射失败1" + e.getLocalizedMessage());
                    // 很可能是内存没了，可以通过gc来解决
                    System.gc();
                    try {
                        out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, memSize);
                    } catch (IOException e1) {
                        // 真的出错了
                        logger.error("FileDB 文件映射失败2" + e1.getLocalizedMessage());
                        throw e;
                    }
                }
                try {
                    // 写入头
                    FileHead head = new FileHead();
                    head.setCount(dataList.size());
                    head.setFileOffset(FileHead.getNeedLen());
                    out.put(head.toArray());
                    // 写入数据
                    Iterator<byte[]> iter = dataList.iterator();
                    while (iter.hasNext()) {
                        byte[] data = iter.next();
                        FileContent content = new FileContent();
                        content.setContentLen(data.length);
                        content.setXor((byte) 0);
                        // 写入
                        // out.put(Utils.int2byte(data.length));
                        // byte []lenByte = new byte[4];
                        // Conv.setIntNetOrder(lenByte, 0, data.length);
                        // out.put(lenByte);
                        byte contentBytes[] = content.toArray();
                        out.put(contentBytes);

                        // 数据
                        out.put(data);
                        // out.put(FileHead.fixStr.getBytes());
                        // out.put(new byte[]{0});

                        out.put(TAIL_STR.getBytes());
                    }

                } finally {
                    if (memSize != out.position()) {
                        logger.error("memSize != out.position()");
                    }
                    out.force();

                    try {
                        clean(out);
                    } catch (Exception e) {
                        logger.error("清除map失败：" + e.getLocalizedMessage());
                        // 不管这个错误，认为是成功了
                    }
                }
            } finally {
                memoryMappedFile.close();

            }
        } finally {
            // 移除这个正在写文件的标识
            fileWritingSet.remove(fileName);
        }
    }

    /**
     * @Title: read @Description: 外部读取数据 @param @param
     * count @param @return @return List<byte[]> 返回类型 @throws
     */
    public List<byte[]> read(int count, String fName) throws Exception {
        List<byte[]> ret = new ArrayList<byte[]>();
        // 校验一下这个文件是否在创建1分钟之后，否则不操作
        // java好像不能直接得到创建时间，用修改时间来代替也是可以的，因为文件只写一次，不会再次写
        // 上面用修改时间来代替创建时间是不对的！因为每次读完数据都会修改文件
        // linux上没有文件的创建时间-_- 只能换思路做
        // long modifyTime = new File(fName).lastModified();
        // if(modifyTime == 0){
        // //文件已经不存在了
        // logger.debug("FileDB read modifyTime ==0");
        // return null;
        // }
        // if(new Date().getTime() - modifyTime < 1000 * 60){
        // //logger.warn("FileDB read modifyTime < 60");
        // return null;
        // }

        // 遍历
        // 不要去从一个完整目录中找文件了，直接对比这个Uuid文件名
        // fileWritingSet数据量应该很少，甚至没有
        for (String writingFile : fileWritingSet) {
            if (fName.contains(writingFile)) {
                return ret;
            }
        }

        boolean isDel = false;
        // 创建一个随机读写文件对象
        RandomAccessFile raf = new RandomAccessFile(fName, "rws");
        // TODO 优化
        long fileLen = raf.length();

        // 文件中的数据量
        long dataCount = 0;
        // 文件头
        FileHead head = new FileHead();
        try {
            // 如果没有数据，那么不读了
            if (fileLen <= 0) {
                // 先关闭
                raf.close();
                raf = null;
                // 是否有其它程序打开这个文件？
                File tmpFile = new File(fName);
                if (tmpFile.canWrite()) {
                    boolean delRet = false;
                    try {
                        delRet = tmpFile.delete();
                    } catch (Exception e) {
                        logger.warn("读文件空，删除文件错误：" + e.getLocalizedMessage());
                    }
                    logger.warn("读文件空，删除文件：文件名：" + fName + ",len:" + fileLen + ",结果：" + delRet);
                }

                return ret;
            }
            // 打开一个文件通道
            FileChannel channel = raf.getChannel();
            // 获取文件头的长度
            // int headLen = FileHead.getNeedLen();
            // 映射文件中的某一部分数据以读写模式到内存中
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, fileLen);
            if (map == null) {
                logger.error("channel.map null:" + fName + ",len:" + fileLen);
                throw new Exception("channel.map null:" + fName + ",len:" + fileLen);
            }
            try {
                // 示例修改字节
                byte[] fileHead = new byte[15];

                map.get(fileHead);

                head.fromArray(fileHead);
                dataCount = head.getCount();
                long fileOffset = head.getFileOffset();
                // int pointNum = FileHead.getOffsetNum(fileHead);
                // map.position(pointNum+1);
                // 文件长度
                int contentLen = FileContent.getNeedLen();

                long readCount = Math.min(dataCount, count);

                // 调整读的指针
                map.position((int) fileOffset);
                for (int i = 0; i < readCount; i++) {

                    // 获取这个obj转换成对象的byte数组长度
                    byte[] contentBytes = new byte[contentLen];
                    map.get(contentBytes);
                    FileContent content = new FileContent();
                    content.fromArray(contentBytes);

                    // int objLen = FileContent.getContentLen(dataLen);\
                    long objLen = content.getContentLen();
                    // 获取长度之后将数据取出来

                    // 获取obj的转换的byte数组
                    byte[] toList = new byte[(int) objLen];
                    map.get(toList);
                    // 校验内容的结尾
                    byte[] end = new byte[3];
                    map.get(end);

                    String strEnd = new String(end);
                    if (strEnd.equals(TAIL_STR)) {
                        ret.add(toList);
                        // pointNum = pointNum + 8 + objLen;
                    }

                    dataCount--;
                    // if(map.remaining()>0){
                    // isDel = true;
                    // break;
                    // }
                }

                if (dataCount <= 0) {
                    isDel = true;
                } else {
                    // 回写文件中的count
                    head.setCount(dataCount);
                    // 最后更新offset
                    head.setFileOffset(map.position());
                    // 回到最开头
                    map.position(0);
                    // overwrite头
                    map.put(head.toArray());
                    logger.debug(
                            "queue file left:" + fName + ",dataCount=" + dataCount + ",offset=" + head.getFileOffset());
                }
            } catch (Exception e) {
                logger.error("读文件失败：文件名：" + fName + ",len:" + fileLen + ",limit:" + map.limit() + ",pos:"
                        + map.position() + ",capacity:" + map.capacity() + ",remaining:" + map.remaining() + ",except:"
                        + ExceptionUtil.getStackStr(e));
                raf.close();
                raf = null;
                clean(map);
                map = null;
                // 删除这个文件
                new File(fName).delete();

                return ret;
            } finally {
                if (map != null) {
                    clean(map);
                }
            }
        } finally {
            if (raf != null) {
                raf.close();
            }
        }

        if (isDel) {
            File f = new File(fName);
            if (f.exists()) {
                f.delete();
                logger.debug("delete queue file:" + fName);
            }
        }
        // Set<String> set = this.getFileSet();
        // set.remove(fName.substring(fName.lastIndexOf("/") + 1));
        // this.setFileSet(set);
        return ret;
    }

    /**
     * @Title: internalRead @Description: 内部读取数据 @param @param
     * count @param @return @return List<byte[]> 返回类型 @throws
     */
    private List<byte[]> internalRead() {
        List<byte[]> ret = null;
        synchronized (lock) {
            ret = internelBuf;
            internelBuf = new LinkedList<byte[]>();
        }
        return ret;
    }

    /**
     * @Title: clean @Description: 清除某个对象的内存?
     * 参考：http://langgufu.iteye.com/blog/2107023 @param @param
     * buffer @param @throws Exception @return void 返回类型 @throws
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void clean(final Object buffer) throws Exception {
        AccessController.doPrivileged(new PrivilegedAction() {
            @SuppressWarnings("restriction")
            public Object run() {
                try {
                    Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
                    getCleanerMethod.setAccessible(true);
                    @SuppressWarnings("restriction")
                    sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(buffer, new Object[0]);
                    if (cleaner != null) {
                        cleaner.clean();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

    }

    // public Set<String> getFileSet() {
    // return fileSet;
    // }
    //
    // public void setFileSet(Set<String> fileSet) {
    // this.fileSet = fileSet;
    // }

}
