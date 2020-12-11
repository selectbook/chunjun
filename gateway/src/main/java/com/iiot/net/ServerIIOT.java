package com.iiot.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Locale;

import com.iiot.common.ptest.NumSave;
import org.apache.log4j.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 
 *
 *
 */
public class ServerIIOT {

	private static Logger logger = Logger.getLogger(ServerIIOT.class);

	// 单例
	ServerIIOT() {

	}

	static private ServerIIOT instIIOT = new ServerIIOT();

	public static ServerIIOT getInst() {
		return instIIOT;
	}

	// ServerBootstrap是netty的功能主类
	private static ServerBootstrap bootstrap = null;
	private static EventLoopGroup boss_group = null;
	private static EventLoopGroup worker_group = null;
	private static String st_ip = null;
	private static int st_port = 0;
	private static ChannelFuture channelFuture=null;
	

	private static boolean init(String ip, int port, int thread_count_boss, int thread_count_worker,
			boolean isKeepAlive, Class<? extends ChannelHandler>... clazz) throws Exception {
		if (clazz.length == 0) {
			logger.fatal("Server IIOT 未指定ChannelHandler");
			return false;
		}
		bootstrap = new ServerBootstrap();
		// boss_group = new NioEventLoopGroup(thread_count_boss);
		// worker_group = new NioEventLoopGroup(thread_count_worker);
		logger.info("Server IIOT 处理线程数：" + thread_count_boss + "/" + thread_count_worker);

		String os = System.getProperty("os.name");
		boolean isLinux = false;
		if (!os.toLowerCase(Locale.ENGLISH).contains("linux")) {
		} else {
			isLinux = true;
		}
		logger.warn("Server IIOT 运行在" + os + "下");

		if (isLinux) {
			boss_group = new EpollEventLoopGroup(thread_count_boss);
			worker_group = new EpollEventLoopGroup(thread_count_worker);
			bootstrap.channel(EpollServerSocketChannel.class);

			// 重用端口
			bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
		} else {
			boss_group = new NioEventLoopGroup(thread_count_boss);
			worker_group = new NioEventLoopGroup(thread_count_worker);
			bootstrap.channel(NioServerSocketChannel.class);
		}

		// 重用地址
		bootstrap.option(ChannelOption.SO_REUSEADDR, true);

		// 指定BOSS，WORKER
		bootstrap.group(boss_group, worker_group);
		// channel，这里把文件操作、网络操作等抽象为channel（通道）
		// 一般有channel和childchannel，或handle和childhandle，或option和childOption
		// 没有child的是指boss的，有child的是worker的
		bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		// TODO 执行到这里面的时机是？
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				for (Class<? extends ChannelHandler> c : clazz) {
					ChannelHandler newInstance = c.newInstance();
					ch.pipeline().addLast(newInstance);
				}
			}
		});
		
		// 设定SOCKET参数
		bootstrap.option(ChannelOption.SO_BACKLOG, 10000);

		// 连接还没有上来，可以先设定参数,设备可能没有实现KA，不使用
		if (isKeepAlive) {
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		}

		// 准备本地绑定
		InetAddress inet_addr = null;
		try {
			inet_addr = InetAddress.getByName(ip);
			InetSocketAddress sock_addr = new InetSocketAddress(inet_addr, port);
			channelFuture = bootstrap.bind(sock_addr).sync();
		} catch (Exception e) {
			if (e instanceof InterruptedException) {
				logger.fatal("Server IIOT net bind err:" + ip + ":" + port);
			} else {
				logger.fatal("Server IIOT ip err:" + ip);
			}
			// 关闭
			boss_group.shutdownGracefully();
			worker_group.shutdownGracefully();
			boss_group = null;
			worker_group = null;
			// 释放netty
			bootstrap = null;

			return false;
		}
		
		return true;
	}

	public  boolean start(String ip, int port, Class<? extends ChannelHandler>... clazz) throws Exception {
		this.st_ip = ip;
		this.st_port = port;
		int cpu = Runtime.getRuntime().availableProcessors();
		return init(ip, port, cpu, cpu, false, clazz);
	}

	public void start(int port, boolean isKeepAlive, Class<? extends ChannelHandler>... clazz) throws Exception {
		int cpu = Runtime.getRuntime().availableProcessors();
		init("0.0.0.0", port, cpu, cpu, isKeepAlive, clazz);
	}

	public void start(int port, Class<? extends ChannelHandler>... clazz) throws Exception {
		start("0.0.0.0", port, clazz);
	}

	public void stop() {
		boss_group.shutdownGracefully();
		worker_group.shutdownGracefully();
		boss_group = null;
		worker_group = null;
		// 释放netty
		bootstrap = null;
	}

	public static void send(ChannelHandlerContext ctx, InetSocketAddress inetSocketAddress, byte[] data) {
		ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(data), inetSocketAddress));
	}
	
	static public void send(ChannelHandlerContext ctx, byte data[]) {
		NumSave.add("downTcp", 1);
		// 发送有点复杂，必须使用如下的方式;
		final ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer(data.length);
		buf.writeBytes(data);
		final Channel channel = ctx.channel();

		// 在EventLoop中不能对做await的操作，不然netty会出异常或死循环
		// 如果数据发送过快，数据就会大量在内存，造成内存不足。(真实数据可能没有那么多，在YY)
		if (channel.eventLoop().inEventLoop()) {
			channel.writeAndFlush(buf);
		} else {
			channel.eventLoop().execute(new Runnable() {
				public void run() {
					channel.writeAndFlush(buf);
				}
			});
		}
		
	}
	
	public void send(byte data[]) {
		if(channelFuture ==null){
			logger.info("Server IIOT channelFuture is null");
			return;
		}
		final Channel channel = channelFuture.channel();
		if (channel == null) {
			logger.info("Server IIOT down channel is null");
			return;
		}
		InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
		if (inetSocketAddress != null) {
			logger.info("客户端地址" + inetSocketAddress.getAddress().getHostAddress() + " 端口: " + inetSocketAddress.getPort());
		}
		final ByteBuf buf = channel.alloc().buffer(data.length);
		// 这里的buf不需要release，netty会照顾好它
		buf.writeBytes(data);
		channel.writeAndFlush(buf);
	}
	

}
