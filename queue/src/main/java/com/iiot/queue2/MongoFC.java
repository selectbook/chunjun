package com.iiot.queue2;

import com.iiot.common.bytes.Conv;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @ClassName: MongoFC
 * @Description: mongo first cached
 */
public class MongoFC {
    /**
     * 目标：
     * 1，同一个设备的数据（基本）可以一次性地拿出
     * 2，读写文件是连续的，快速写入磁盘
     * 3，内存使用量是可控的，可配的
     * 4，往1000万设备数据上靠，最终压力瓶颈应该出现在磁盘而不是这里
     * 思路：
     * 1，N个设备同写一个文件，这样可以保证目标1
     * 2，文件数为：C = 1000万/N
     * 3，仍保留一小块内存，以便集中写文件。一般磁盘以4K为一个扇区，可以为每个文件缓存8K或16K
     * 虽然操作系统会做缓存再写入，不过经过测试，数据较大时总吞吐量大，每次写的大小4048字节比100字节吞吐量快8倍
     * 使用SSD会有区别么？
     * 4，估计内存使用量：一条数据为0.2K，每30秒一条数据，最大缓存时间为1小时，数据为24K
     * 如果内存使用量为100M，那么允许4200同一个文件
     * 假设有500万车同时上线，每4200个设备同一个文件，那么有1190个文件，可以接受
     * 5，NO:配置多少个设备同一个文件，默认为1000，并估计目前的设备量是多少，默认为1000000
     * 5，配置程序允许多少个文件
     * 6，读数据时把每个文件的数据一次性读出，并按终端号整理为Map<devid,List<data>>的结构，并写入二级缓存
     * 7，基于运维的原因，把新旧分为两个目录，这样就不会出现有一些文件删除不掉的情况
     * <p>
     * 压力测试：win7,4cpu,8G ，
     */

    // 内部使用，存储文件的相关信息
    class FileInfo {
        // 有缓存，需要写，所以需要有锁
        public Object lock = new Object();
        // 写文件分开锁，因为push时不总是需要文件锁的，有大机会数据进入内存
        public Object lockFile = new Object();
        // 是否正在写，那么读的时候停一下
        // 不要在cpu的cached中缓存
        volatile public boolean isWriting = false;
        public String path;
        public File file;
        public FileOutputStream fos;
        public byte[] cached;
        // 缓存的偏移
        public int offset;

        public FileInfo(String path, File file, FileOutputStream fos, int fileCachedSizeInKB) {
            super();
            this.path = path;
            this.file = file;
            this.fos = fos;
            cached = new byte[fileCachedSizeInKB * 1024];
        }
    }

    // 读取时给出的结果，可以遍历数据
    public class MongoFcReader {
        List<FileInfo> fileArray;
        // 最后需要删除掉文件夹
        String folder = null;

        public MongoFcReader(List<FileInfo> fileArray) {
            super();
            this.fileArray = fileArray;
            if (!fileArray.isEmpty()) {
                String filePath = fileArray.get(0).path;
                // 取中间的文件夹
                int pos1 = filePath.indexOf('/', 0);
                if (pos1 < 0) {
                    pos1 = filePath.indexOf('\\', 0);
                }
                if (pos1 >= 0) {
                    int pos2 = filePath.indexOf('/', pos1 + 1);
                    if (pos2 < 0) {
                        pos2 = filePath.indexOf('\\', pos1 + 1);
                    }
                    if (pos2 >= 0) {
                        folder = filePath.substring(0, pos2);

                    }
                }
            }
        }

        // 取数据，外部需要循环读，直到返回Null为止
        public Map<String, List<byte[]>> fetch() throws IOException {
            if (fileArray.isEmpty()) {
                // 删除目录
                if (folder != null) {
                    new File(folder).delete();
                }
                return null;
            }
            Map<String, List<byte[]>> retMap = new HashMap<>();
            FileInfo fileInfo = fileArray.remove(0);

            // 先关闭写入
            fileInfo.fos.flush();
            fileInfo.fos.close();

            // 先从内存中读取，保险点上个锁
            synchronized (fileInfo.lock) {
                int offset = 0;
                byte[] cached = fileInfo.cached;
                int cachedLen = fileInfo.offset;
                while (offset < cachedLen) {
                    // 终端长度为1字节
                    byte devLen = cached[offset++];
                    if (offset + devLen >= cachedLen) {
                        logger.error("数据内容不对：offset + devLen >= readLen");
                        break;
                    }
                    String devid = new String(cached, offset, Byte.toUnsignedInt(devLen));
                    offset += devLen;
                    // 4字节长度+内容
                    long dataLen = Conv.getIntNetOrder(cached, offset);
                    offset += 4;
                    byte[] data = new byte[(int) dataLen];
                    System.arraycopy(cached, offset, data, 0, (int) dataLen);
                    offset += dataLen;

                    // 放入到map
                    List<byte[]> dataList = retMap.get(devid);
                    if (dataList == null) {
                        dataList = new LinkedList<>();
                        retMap.put(devid, dataList);
                    }
                    dataList.add(data);
                }
                if (offset != cachedLen) {
                    logger.error("offset != cachedLen");
                }
            }

            File file = new File(fileInfo.path);
            FileInputStream fis = new FileInputStream(file);
            int readLen = 0;
            byte[] fileData = null;
            try {
                long fileLen = fileInfo.file.length();
                if (fileLen == 0) {
                    // 文件为空很正常的，有一些文件没有hash到
                    return retMap;
                }

                long t1 = System.currentTimeMillis();
                fileData = new byte[(int) fileLen];
                readLen = fis.read(fileData);
                if (readLen != fileLen) {
                    logger.error("readLen != fileLen");
                    return retMap;
                }
                long t2 = System.currentTimeMillis();
                logger.debug("MongoFC读文件大小:" + fileLen + ",time:" + (t2 - t1));
            } finally {
                fis.close();
                boolean isDeleted = fileInfo.file.delete();
                if (!isDeleted) {
                    logger.warn("mongoFC file not deleted!:" + fileInfo.file.getName());
                }
            }

            int offset = 0;
            while (offset < readLen) {
                // 终端长度为1字节
                byte devLen = fileData[offset++];
                if (offset + devLen >= readLen) {
                    logger.error("数据内容不对：offset + devLen >= readLen");
                    break;
                }
                String devid = new String(fileData, offset, Byte.toUnsignedInt(devLen));
                offset += devLen;
                // 4字节长度+内容
                long dataLen = Conv.getIntNetOrder(fileData, offset);
                offset += 4;
                byte[] data = new byte[(int) dataLen];
                System.arraycopy(fileData, offset, data, 0, (int) dataLen);
                offset += dataLen;

                // 放入到map
                List<byte[]> dataList = retMap.get(devid);
                if (dataList == null) {
                    dataList = new LinkedList<>();
                    retMap.put(devid, dataList);
                }
                dataList.add(data);
            }
            if (offset != readLen) {
                logger.error("offset != readLen");
            }

            return retMap;
        }

    }

    Logger logger = Logger.getLogger(MongoFC.class);
    // 文件目录
    final String folder = "mongoFC";
    // 锁
    Object lock = new Object();
    // 记录
    long devCountPerFile;
    // 估计目前的设备量
    long maxDevCount;
    // // 在单位时间内（就是read的间隔）一个设备会上传多少条数据
    // int dataCountPerTime;
    // // 一条数据的大小
    // int dataSizeInBytes;
    // 每一个文件都有一个小缓存，减少与系统的访问交互
    int fileCachedSizeInKB;
    // 总文件数
    long fileCount;
    // 保存文件名
    // TODO fileArray在多线程中会变化，是不是需要volatile？
    volatile List<FileInfo> fileArray = new ArrayList<>();
    // 是否正在运行；当做了lastRead动作后就不能操作任何数据
    volatile boolean isRunning = true;

    /**
     * <p>Title: </p>
     * <p>Description: </p>
     *
     * @param devCountPerFile    一个文件放多少个设备的数据
     * @param maxDevCount        估计系统最大有多少个设备
     *                           //* @param dataCountPerTime 在单位时间内（就是read的间隔）一个设备会上传多少条数据
     *                           //* @param dataSizeInBytes 一条数据的大小
     * @param fileCachedSizeInKB 小缓存的大小，一般设置4或者8比较合适
     *                           int dataCountPerTime, int dataSizeInBytes
     */
    public MongoFC(long devCountPerFile, long maxDevCount, int fileCachedSizeInKB) {

        this.devCountPerFile = devCountPerFile;
        this.maxDevCount = maxDevCount;
        this.fileCachedSizeInKB = fileCachedSizeInKB;
        // this.dataCountPerTime = dataCountPerTime;
        // 创建文件
        fileCount = (maxDevCount + devCountPerFile - 1) / devCountPerFile;
        try {
            List<FileInfo> fileArrayTmp = createFiles(fileCachedSizeInKB);
            synchronized (lock) {
                fileArray = fileArrayTmp;
            }
        } catch (IOException e) {
            logger.error("MongoFC:" + e.getLocalizedMessage());
            return;
        }
    }

    // public void shutdown() {
    // //数据没用了~~
    // }

    // 写入数据
    public void push(String devid, byte[] data) {
        // 对设备做hash ，再mod，这样决定这个设备往哪个文件中写
        int fileIndex = (int) (Math.abs(devid.hashCode()) % fileCount);
        FileInfo fileInfo = null;
        synchronized (lock) {
            if (!isRunning) {
                logger.warn("非运行状态push");
                return;
            }
            if (fileIndex >= fileArray.size()) {
                logger.warn("fileIndex-1 > fileArray.size() : " + (fileIndex - 1) + ">" + fileArray.size());
                return;
            }
            fileInfo = fileArray.get(fileIndex);
            // 做个正在写的标识
            fileInfo.isWriting = true;
        }
        try {
            writeData(fileInfo, devid, data);
        } catch (IOException e) {
            logger.error("MongoFC push:" + e.getLocalizedMessage());
            return;
        } finally {
            synchronized (fileInfo.lock) {
                fileInfo.isWriting = false;
            }
        }
    }

    // 取数据
    public MongoFcReader getReader() {
        return getReader(false);
    }

    // 最后一次读取，一般是在关闭前读取，这样就不产生新的文件
    // 当做了lastRead动作后就不能操作任何数据
    public MongoFcReader getLastReader() {
        return getReader(true);
    }

    /**
     * @return void    返回类型
     * @throws
     * @Title: getReader
     * @Description: 读出数据，一次读一个文件的数据，不同外部来指定
     * 返回的数据中，同一个设备的数据是按进来的顺序给出去的
     * isLastRead 是否为最后一次读取，是的话就不会再产生文件了
     */
    private MongoFcReader getReader(boolean isLastRead) {
        //TODO 存在优化空间，先把文件先创建好，得到一个临时的变量，后面再转换
        List<FileInfo> fileArrayTmp = null;
        try {
            fileArrayTmp = createFiles(fileCachedSizeInKB);
        } catch (IOException e) {
            logger.error("pop createFiles:" + e.getLocalizedMessage());
            return null;
        }
        // 把文件换出来
        List<FileInfo> fileArrayOut = null;
        synchronized (lock) {
            if (!isRunning) {
                return null;
            }
            fileArrayOut = fileArray;
            if (isLastRead) {
                fileArray = null;
                isRunning = false;
            } else {
                //替换创建好的文件列表
                fileArray = fileArrayTmp;
            }
        }
        if (fileArrayOut != null) {
            // 等待每个文件都已经写好了
            for (FileInfo fileInfo : fileArrayOut) {
                boolean isWriting = true;
                while (isWriting) {
                    synchronized (fileInfo.lock) {
                        isWriting = fileInfo.isWriting;
                    }

                    if (isWriting) {
                        // 正在写？那么等它
                        logger.info("getReader wait for writing end:" + fileInfo.path);
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {

                        }
                    }
                }
            }
        }

        return new MongoFcReader(fileArrayOut);

    }

    // 创建文件
    private List<FileInfo> createFiles(int fileCachedSizeInKB) throws IOException {
        List<FileInfo> fileArrayTmp = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        // 不同时期的数据在不同的文件夹内
        String thisFolder = folder + "/" + formatter.format(new Date());
        // 保证文件夹存在
        File fileFolder = new File(thisFolder);
        fileFolder.mkdirs();

        for (int i = 0; i < fileCount; i++) {
            new Date();
            String onlyFileName = UUID.randomUUID().toString();
            String fileName = thisFolder + "/" + onlyFileName;
            File file = new File(fileName);
            // File file = File.createTempFile(onlyFileName, "fc", fileFolder);
            // 程序退出时保证删除
            // file.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(file);
            // fos.write(12);
            // fos.flush();
            FileInfo fileInfo = new FileInfo(fileName, file, fos, fileCachedSizeInKB);
            fileArrayTmp.add(fileInfo);
        }
        return fileArrayTmp;
    }

    // 写数据
    private void writeData(FileInfo fileInfo, String devid, byte[] data) throws IOException {
        FileOutputStream fos = fileInfo.fos;

        byte[] cachedWrite = null;
        int cachedLen = 0;
        synchronized (fileInfo.lock) {
            byte cached[] = fileInfo.cached;
            // 如果缓存的数据足够，那么写缓存，如果不够那么写文件
            if (fileInfo.offset + 1 + devid.length() + 4 + data.length < cached.length) {
                // 写内存
                // 终端号长度
                cached[fileInfo.offset++] = (byte) devid.length();
                System.arraycopy(devid.getBytes(), 0, cached, fileInfo.offset, devid.length());
                fileInfo.offset += devid.length();
                // 数据
                Conv.setIntNetOrder(cached, fileInfo.offset, data.length);
                fileInfo.offset += 4;
                System.arraycopy(data, 0, cached, fileInfo.offset, data.length);
                fileInfo.offset += data.length;
            } else {
                // 整个换出来
                cachedWrite = fileInfo.cached;
                cachedLen = fileInfo.offset;
                fileInfo.cached = new byte[fileInfo.cached.length];
                fileInfo.offset = 0;
            }
        }
        // 需要写文件
        if (cachedWrite != null) {
            // 先写本条数据
            byte[] tmp = new byte[1 + devid.length() + 4 + data.length];

            int tmpoffset = 0;
            // 终端号长度
            tmp[tmpoffset++] = (byte) devid.length();
            System.arraycopy(devid.getBytes(), 0, tmp, tmpoffset, devid.length());
            tmpoffset += devid.length();
            // 数据
            Conv.setIntNetOrder(tmp, tmpoffset, data.length);
            tmpoffset += 4;
            System.arraycopy(data, 0, tmp, tmpoffset, data.length);
            tmpoffset += data.length;

            long t1 = System.currentTimeMillis();
            synchronized (fileInfo.lockFile) {
                // 先写本条数据
                fos.write(tmp);
                // 再写大块
                fos.write(cachedWrite, 0, cachedLen);
            }
            long t2 = System.currentTimeMillis();
            if (t2 - t1 > 1000) {
                logger.info("mongoFC write size=" + (tmp.length + cachedLen) + ",t=" + (t2 - t1));
            }
        }

        // int devidLen = devid.length();
        // // 终端长度为1字节
        // fos.write(devidLen);
        // fos.write(devid.getBytes());
        // // 长度加内容
        // int len = data.length;
        // byte lenBytes[] = new byte[4];
        // Conv.setIntNetOrder(lenBytes, 0, len);
        // fos.write(lenBytes);
        // fos.write(data);
    }
}
