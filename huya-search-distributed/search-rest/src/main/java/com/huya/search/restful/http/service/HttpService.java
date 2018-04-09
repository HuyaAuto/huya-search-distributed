package com.huya.search.restful.http.service;

import com.huya.search.restful.http.handler.HttpDispatcherHandler;
import com.huya.search.settings.Settings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;


public class HttpService {

	private static final Logger LOG = LoggerFactory.getLogger(HttpService.class);

	private final HttpDispatcherHandler sharedHandler = new HttpDispatcherHandler();

	private final int _2M = 2048 * 1000;

	private Settings settings;

	private EventLoopGroup bossLoop = null;
	private EventLoopGroup workerLoop = null;

	private Channel channel;

	private CountDownLatch latch = new CountDownLatch(1);

	private static boolean isLinux() {
		String osName = System.getProperty("os.name");
		return null != osName && osName.toLowerCase().contains("linux");
	}

	public HttpService(Settings settings) {
		this.settings = settings;
		int boss   = settings.getAsInt("server.netty.boss", 1);
		int worker = settings.getAsInt("server.netty.work", 30);
		if (isLinux()) {
			this.bossLoop = new EpollEventLoopGroup(boss);
			this.workerLoop = new EpollEventLoopGroup(worker);
		}
		else {
			this.bossLoop = new NioEventLoopGroup(boss);
			this.workerLoop = new NioEventLoopGroup(worker);
		}
	}

	public void start() {
		int port = settings.getAsInt("server.port", 28888);

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();

			if (isLinux()) {
				bootstrap.channel(EpollServerSocketChannel.class);
			}
			else {
				bootstrap.channel(NioServerSocketChannel.class);
			}
			
			bootstrap.group(bossLoop, workerLoop)
					.childOption(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.SO_REUSEADDR, true)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipe = ch.pipeline();
							pipe.addLast("decoder", new HttpRequestDecoder());
							pipe.addLast("encoder", new HttpResponseEncoder());
							pipe.addLast("aggregator", new HttpObjectAggregator(_2M));
							pipe.addLast("deflater", new HttpContentCompressor());
							pipe.addLast("dispatcher", sharedHandler);
						}
					});

			ChannelFuture future;

			future = bootstrap.bind(port).sync();

			LOG.info("Http server bound port:{} successfully", port);

			channel = future.channel();
			latch.countDown();
			channel.closeFuture().sync();
		} catch (InterruptedException e) {
			LOG.error("Starting http server on host:{} port:{} err", port, e);
		} finally {
			bossLoop.shutdownGracefully();
			workerLoop.shutdownGracefully();
		}
	}

	public void awaitStart() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		channel.close();
	}

}
