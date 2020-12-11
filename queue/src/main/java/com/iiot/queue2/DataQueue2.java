package com.iiot.queue2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.iiot.common.bytes.Conv;

/**
 * 
* @ClassName: DataQueue2
* @Description: 数据队列
*
 */
public class DataQueue2 {

	// 功能：提供可以配置内存大小的、高效的队列缓存服务
	// 特点：
	// 1，非常快，最坏也能接近磁盘的读写速度，最快相当于内存
	// 2，保证数据基本不丢，除非直接掉电或程序-9被杀掉
	// 3，不保证数据的有序性和顺序确定性，出来的数据和进去的数据顺序很可能不一样

	// 定义mid=(high-low)/2
	// 改进点1：一个是保存mid以下数据,一个保存mid以上的数据，
	// 当数据超过high时直接把第二个缓存换出去，减少内存复制的成本
	// 改进点2：使用raf代替文件映射，直接写比文件映射还要快
	// 改进点3：控制内存的使用大小，之前的评估内存是不准的。数据不再是放在list中，放在两块大内存中。申请两块内存，思路同改进点1。
	// 改进点4：写文件使用最简单的方式，长度加内容，而且与内存的布局是一样的，写文件应该是非常快，读也非常快。前期为了验证可以做一个CRC校验
	// 改进点5：读文件采用一次性地读进来，一般文件是跟上次那么大的，如果没有修改配置
	// 改进点6：高低水平低的概念不太准确，使用更准确的表达，最高位是多少M，超过时写多少M到磁盘

	// 参数，对外
	static public class DataQueue2Param {
		// 名称，用于文件夹命名
		public String name;
		// 高水标，MB
		public long maxMemInMB;
		// 当数据总量超过High时，需要往文件写多少数据
		public long subMemInMB;

		public DataQueue2Param(String name, long maxMemInMB, long subMemInMB) {
			super();
			this.name = name;
			this.maxMemInMB = maxMemInMB;
			this.subMemInMB = subMemInMB;
		}

	}

	static class FileSave {
		Logger logger = Logger.getLogger(FileSave.class);
		String path;
		Set<String> readingFiles = new HashSet<>();

		public FileSave(String name) {	
			// 以名称作为目录
			path = "fs_" + name;
		
			File file = new File(path);
			if (!file.exists() || !file.isDirectory()) {
				file.mkdirs();
			}
			// 启动时把目录的文件读一次
			String fileList[] = file.list();
			if (fileList != null) {
				for (String fileName : fileList) {
					readingFiles.add(fileName);
				}
			}

		}
		public void shutdown(){
			//如果这个目录下没有文件了，说明它里面的数据全没有了，那么就需要删除目录
			File file = new File(path);
			String fileList[] = file.list();
			if(fileList == null || fileList.length == 0){
				file.delete();
			}
		}

		// 写一个文件，只要在指定文件夹下就OK
		public void writeFile(byte[] data, int bytes) throws IOException {
			String fileName = UUID.randomUUID().toString();
			File file = new File(path + "/" + fileName);
			FileOutputStream fos;

			fos = new FileOutputStream(file);
			try {
				fos.write(data, 0, bytes);
			} finally {
				fos.close();
			}
			synchronized (readingFiles) {
				readingFiles.add(fileName);
			}
		}

		// 读一个文件，只要在指定文件夹下就OK
		public byte[] readFile() throws IOException {
			File folder = new File(path);
			// String[] fileList = folder.list();
			String selectedFile = null;
			// 不同线程不要读到同一个文件/
			synchronized (readingFiles) {
				if (readingFiles.isEmpty()) {
					return null;
				}
				Iterator<String> iter = readingFiles.iterator();
				selectedFile = iter.next();
				// 取了就删除，如果后面出现异常应该不会，暂时不考虑这种情况
				readingFiles.remove(selectedFile);
			}
			byte data[] = null;

			String filePath = path + "/" + selectedFile;
			File file = new File(filePath);
			data = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			try {
				fis.read(data);
			} finally {
				fis.close();
			}
			if (!file.delete()) {
				logger.warn("file.delete fail:" + filePath);
			}

			return data;
		}
	}

	// 小数据，在内存中的表示，数据读回来之后还是需要表示出来的，不然数据不好处理
	static class SmallData {
		public byte[] data;
		// 一个数据的偏移（在内存和文件中都是一样的，指的是大段数据的偏移）
		public int preOffset;

		public SmallData(byte[] data, int preOffset) {
			super();
			this.data = data;
			this.preOffset = preOffset;
		}

	}

	static Logger logger = Logger.getLogger(DataQueue2.class);
	// lock，场景属于写多读少，没有必要使用读写锁，取数据也是写
	Object lock = new Object();
	// 文件
	FileSave fileSave = null;
	// 两块内存，一块存储low+mid以下的，一块存储mid到high的数据
	//TODO 为了保证在多线程间的正确性，把会变的变量都做volatile
	volatile byte[] memLow = null;
	volatile byte[] memHigh = null;
	volatile int lowSize = 0;
	volatile int highSize = 0;
	// 两块内存的偏移
	volatile int offsetLow = 0;
	volatile int offsetHigh = 0;
	// // 数据条数，主要用于读写文件时知道要写到哪，读到哪
	// int sizeHigh = 0;
	// int sizeLow = 0;
	// 最后一条数据的偏移，以便取数据的时候快速定位过去
	volatile int offsetLastLow = 0;
	volatile int offsetLastHigh = 0;
	
	//对象生成时间
	public long createdTime;

	public DataQueue2(DataQueue2Param param) {
		fileSave = new FileSave(param.name);
		// 两块内存一开始就是定的
		lowSize = (int) ((param.maxMemInMB - param.subMemInMB) * 1024 * 1024);
		highSize = (int) (param.subMemInMB * 1024 * 1024);
		memLow = new byte[(int) lowSize];
		memHigh = new byte[(int) highSize];
		
		createdTime = System.currentTimeMillis();
	}

	// 关闭时通知，以便保存内存中的数据
	public void shutdown() {
		synchronized (lock) {
			if (offsetHigh > 0) {
				try {
					fileSave.writeFile(memHigh, offsetHigh);
				} catch (IOException e) {
					logger.error(e.getLocalizedMessage());
				}
			}
			if (offsetLow > 0) {
				try {
					fileSave.writeFile(memLow, offsetLow);
				} catch (IOException e) {
					logger.error(e.getLocalizedMessage());
				}
			}
			//把它置为Null，算是一个哨兵
			memHigh = null;
			memLow = null;
			
		}
		fileSave.shutdown();
		
	}

	public void push(byte[] data) {
		// 计算校验
		byte xor = Conv.CheckXor(data, 0, data.length);
		// 数据存储的长度
		int dataAllLen = getDataAllLen(data.length);
		// 需要写文件么？
		byte[] mem2File = null;
		// 写文件的大小
		int fileSize = 0;
		synchronized (lock) {
			// 直接写文件了，不经过内存组装了
			// 检查应该写哪个内存？
			if (offsetLow + dataAllLen > memLow.length) {
				// 超过了
				// 是否超过了High？
				if (offsetHigh + dataAllLen > memHigh.length) {
					mem2File = memHigh;
					// 文件大小
					fileSize = offsetHigh;
					// 重置
					memHigh = new byte[highSize];
					offsetHigh = 0;
					offsetLastHigh = 0;
					// sizeHigh = 0;
				}
				if (offsetHigh + dataAllLen < 0) {
					logger.error("int rewind!!");
				}
				// 本次的数据作为一个新数据
				writeDate(memHigh, offsetHigh, data, xor, offsetLastHigh);
				// sizeHigh++;
				offsetLastHigh = offsetHigh;
				offsetHigh += dataAllLen;
			} else {
				// 使用是low
				writeDate(memLow, offsetLow, data, xor, offsetLastLow);
				offsetLastLow = offsetLow;
				offsetLow += dataAllLen;
				// sizeLow++;
			}
		}
		if (mem2File != null) {
			// 写文件，同步，让它更快的执行是比较好的办法，这里写文件异步就很难控制
			// TODO 加个时间打印

			try {
				long t1 = System.currentTimeMillis();
				fileSave.writeFile(mem2File, fileSize);
				long t2 = System.currentTimeMillis();
				// logger.info("writeFile t:" + (t2 - t1) + "ms");
			} catch (IOException e) {
				logger.error("fileSave.writeFile fail:" + e.getLocalizedMessage());
				return;
			}
		}
	}

	// 取数据，count最多取的个数
	public List<byte[]> pop(int count) {
		// 尽量满足外部一次取数据的需求，即使数据在文件中也取给它
		List<byte[]> retList = new LinkedList<byte[]>();
		synchronized (lock) {
			// 先取high的数据
			while (offsetHigh != 0 && retList.size() < count) {
				SmallData smallData = readData(memHigh, offsetLastHigh);
				// 从数据中取出上一条数据的偏移
				offsetLastHigh = smallData.preOffset;
				// if(offsetLastHigh == 0){
				// logger.error("offsetLastHigh == 0");
				// }
				offsetHigh = offsetHigh - getDataAllLen(smallData.data.length);
				// if(offsetHigh < 0){
				// logger.error("offsetHigh < 0!!");
				// }
				// sizeHigh--;
				retList.add(smallData.data);
			}
			// 数据量不够，再取low的数据
			while (offsetLow != 0 && retList.size() < count) {
				SmallData smallData = readData(memLow, offsetLastLow);
				offsetLastLow = smallData.preOffset;
				offsetLow = offsetLow - getDataAllLen(smallData.data.length);
				// sizeLow--;
				retList.add(smallData.data);
			}
		}
		// 数据量不够，尝试读文件
		while (retList.size() < count) {
			byte[] fileData = null;
			try {
				fileData = fileSave.readFile();
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage());
				break;
			}
			if (fileData == null || fileData.length == 0) {
				// 没有文件数据了
				break;
			}
			// 直接从这些数据中取，取完了有多的放回到内存，不够的话就继续从文件中读取
			int fileDateOffset = 0;
			while (true) {
				SmallData smallData = readData(fileData, fileDateOffset);
				if (smallData == null) {
					// 没有数据可读了
					break;
				}
				fileDateOffset += getDataAllLen(smallData.data.length);
				if (retList.size() >= count) {
					// 数据足够了，可以退出了，退出前把多余的数据放回到内存
					push(smallData.data);
				} else {
					retList.add(smallData.data);
				}
			}
		}
		// 还是不够那就是没有数据了
		return retList;
	}

	// 返回内存的缓存大小
	public long getQueueBytesInMem() {
		synchronized (lock) {
			return (long) offsetHigh + (long) offsetLow;
		}
	}

	// 写数据到给定的block中
	private void writeDate(byte[] block, int offset, byte[] data, byte xor, int offsetLast) {
		// 数据格式：数据长度4+数据内容N+内容校验1+上一条数据的offset，方便
		int dataLen = data.length;
		if (offset + dataLen >= block.length) {
			logger.error("offset + dataLen >= block.length");
			return;
		}
		try {
			Conv.setIntNetOrder(block, offset, dataLen);
		} catch (Exception e) {
			logger.error("offset + dataLen >= block.length");
			return;
		}
		offset += 4;
		System.arraycopy(data, 0, block, offset, dataLen);
		offset += dataLen;
		block[offset++] = xor;
		Conv.setIntNetOrder(block, offset, offsetLast);
		offset += 4;
	}

	// 读一条数据，无数据则为null
	// offset必须为最后一条数据的offset
	private SmallData readData(byte[] block, int offset) {
		if (block.length > 0 && offset + 1 > block.length) {
			return null;
		}
		if (offset + 1 >= block.length) {
			return null;
		}
		long dataLen = Conv.getIntNetOrder(block, offset);
		offset += 4;
		// 这里存在优化空间，如果数据指向block，再增加一个offset+长度，不需要复制内存
		// 按offset修改会造成接口变更和外部处理改动很大，还是算了
		byte data[] = new byte[(int) dataLen];
		System.arraycopy(block, offset, data, 0, (int) dataLen);
		offset += dataLen;
		byte xor = block[offset++];
		// 校验数据
		if (xor != Conv.CheckXor(data, 0, data.length)) {
			logger.error("Conv.CheckXor fail!");
			return null;
		}

		int offsetLast = (int) Conv.getIntNetOrder(block, offset);
		offset += 4;
		return new SmallData(data, offsetLast);
	}

	// 从一个数据中得到上一条数据的偏移，block是全的block
	private int getPreOffsetFromData(byte[] block) {
		int offset = 0;
		long dataLen = Conv.getIntNetOrder(block, offset);
		offset += 4;
		offset += dataLen + 1;
		int offsetLast = (int) Conv.getIntNetOrder(block, offset);
		offset += 4;
		return offsetLast;
	}

	// 数据长度
	private int getDataAllLen(int dataLen) {
		// 数据格式：数据长度4+数据内容N+内容校验1+上一条数据的offset，方便
		return 4 + dataLen + 1 + 4;
	}
}
