package cn.mageek.NetServer.service;

import cn.mageek.NetServer.handler.receiveMsgHandler;
import cn.mageek.NetServer.handler.sendMsgHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * 监听和接受连接请求，亦即创建channel并配置消息处理的handler
 * @author Mageek Chiu
 * @date 2018/3/5 0005:19:26
 */

public class NetworkServer {

    private static final Logger logger = LoggerFactory.getLogger(NetworkServer.class);

    private final String port;

    public NetworkServer(String port) {
        this.port = port;
    }

    public void run() throws Exception {

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);//接收连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();//处理数据
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//新建一个channel
                    .option(ChannelOption.SO_BACKLOG, 128)//最大等待连接
                    .handler(new LoggingHandler(LogLevel.INFO))//netty 内部日志设置为INFO ？貌似不管用
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // out 必须放在最后一个 in 前面，也就是必须是以 in 结尾。逻辑是in 顺序执行完毕以后从 pipeline 反向查找 out
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new ReadTimeoutHandler(5));//5秒超时

                            // out 执行顺序为注册顺序的逆序
                            p.addLast(new sendMsgHandler());

                            // in 执行顺序为注册顺序
                            p.addLast(new receiveMsgHandler());

                        }
                    });

            // Start the server. 采用同步等待的方式
            ChannelFuture f = b.bind(Integer.parseInt(port)).sync();
            logger.info("NetWorkServer is up now and listens on {}", f.channel().localAddress());

            // Start the server. 采用异步回调的方式
//            ChannelFuture f = b.bind(Integer.parseInt(port));
//            f.addListener(new ChannelFutureListener() {
//                public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                    if (channelFuture.isSuccess()){
//                        logger.info("NetWorkServer is up now and listens on {}",channelFuture.channel().localAddress());
//                    }else {
//                        logger.error("NetWorkServer start error: {}",channelFuture.cause().getMessage());
//                        channelFuture.cause().printStackTrace();
//                    }
//                }
//            });


            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        try{
            InputStream in = ClassLoader.class.getResourceAsStream("/app.properties");
            Properties pop = new Properties();
            pop.load(in);
            String port = pop.getProperty("NetServer.port");
            logger.debug("config port:{}",port);
            in.close();
            new NetworkServer(port).run();
        }catch(Exception ex) {
            logger.error("read config error: {}",ex.getMessage());
            ex.printStackTrace();
        }
    }
}
