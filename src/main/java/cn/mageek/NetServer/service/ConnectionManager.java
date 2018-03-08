package cn.mageek.NetServer.service;

import cn.mageek.NetServer.command.Command;
import cn.mageek.NetServer.db.RedisClient;
import cn.mageek.NetServer.handler.receiveMsgHandler;
import cn.mageek.NetServer.handler.sendMsgHandler;
import cn.mageek.NetServer.pojo.NetMsgObject;
import cn.mageek.NetServer.pojo.RcvMsgObject;
import cn.mageek.NetServer.pojo.WebMsgObject;
import cn.mageek.NetServer.util.Encoder;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监听和接受连接请求，亦即创建channel并配置消息处理的handler
 * @author Mageek Chiu
 * @date 2018/3/7 0007:20:18
 */
public class ConnectionManager  implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private final String port;
    private static volatile Map<String,Channel> channelMap = new ConcurrentHashMap<>();//管理所有连接
    private static final String CHANNEL = "netMsg";


    public ConnectionManager(String port) {
        this.port = port;
    }

    public void run() {
        new Thread(new RedisSub(channelMap)).start();//开启监听redis频道

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
                            p.addLast(new ReadTimeoutHandler(300));//多少秒超时

                            // out 执行顺序为注册顺序的逆序
//                            p.addLast(new sendMsgHandler());

                            // in 执行顺序为注册顺序
                            p.addLast(new receiveMsgHandler(channelMap));

                        }
                    });

            // Start the server. 采用同步等待的方式
            ChannelFuture f = b.bind(Integer.parseInt(port)).sync();
            logger.info("ConnectionManager is up now and listens on {}", f.channel().localAddress());

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
            logger.info("ConnectionManager is down");

        } catch (InterruptedException e) {
            logger.error("ConnectionManager start error: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    class RedisSub implements Runnable {

        private Map<String,Channel> channelMap;

        public RedisSub(Map<String,Channel> channelMap){
            this.channelMap = channelMap;
        }
        @Override
        public void run() {
            try( Jedis jedis =  RedisClient.getJedis() ) {
                final ConnectionManagerSubListener listener = new ConnectionManagerSubListener(channelMap);
                logger.info("ConnectionManager begin to subscribe {}",CHANNEL);
                jedis.subscribe(listener,CHANNEL);//策略模式的运用
            }
        }
    }

    class ConnectionManagerSubListener extends JedisPubSub {
        private Map<String,Channel> channelMap;

        public ConnectionManagerSubListener(Map<String,Channel> channelMap){
            this.channelMap = channelMap;
        }

        // 取得订阅的消息后的处理
//        publish netMsg 00000000000000e0-00004720-000001a6-f5b932775e48cf4c-5cb1411f
        public void onMessage(String channel, String message) {
            logger.info("频道:{}，收到消息:{}",channel,message);
            try{
                NetMsgObject netMsgObject = (NetMsgObject) JSON.parse(message);//根据消息字符串解析成消息对象
                RcvMsgObject rcvMsgObject = ((Command)Class.forName("cn.mageek.NetServer.command.Command"+netMsgObject.getCommand()).newInstance()).send(netMsgObject);
                ByteBuf out =  Encoder.objectToBytes(rcvMsgObject);//消息对象编码为buffer
                Channel client = channelMap.get(message);
                if(client==null){
                    logger.error("{}不在线,消息：{}",netMsgObject.getClientId(),message);
                    return;
                }
                client.writeAndFlush(out).addListener(new ChannelFutureListener() {//发送消息并监听结果
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()){
                            logger.info("success",channelFuture.channel().localAddress());
                        }else {
                            logger.error("error: {}",channelFuture.cause().getMessage());
                            channelFuture.cause().printStackTrace();
                        }
                    }
                });
            }catch (Exception e){
                logger.error("解析net消息,{} error：{}",message,e.getMessage());
                e.printStackTrace();
            }



        }

        // 初始化订阅时候的处理
        public void onSubscribe(String channel, int subscribedChannels) {
            logger.info("订阅:{}，总数:{}",channel,subscribedChannels);
        }

        // 取消订阅时候的处理
        public void onUnsubscribe(String channel, int subscribedChannels) {
            logger.info("取消订阅:{}，总数:{}",channel,subscribedChannels);
        }

        // 初始化按表达式的方式订阅时候的处理
        public void onPSubscribe(String pattern, int subscribedChannels) {
            logger.info(pattern + "=" + subscribedChannels);
        }

        // 取消按表达式的方式订阅时候的处理
        public void onPUnsubscribe(String pattern, int subscribedChannels) {
            logger.info(pattern + "=" + subscribedChannels);
        }

        // 取得按表达式的方式订阅的消息后的处理
        public void onPMessage(String pattern, String channel, String message) {
            logger.info(pattern + "=" + channel + "=" + message);
        }

    }
}




