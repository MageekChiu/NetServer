package cn.mageek.NetServer.service;

import cn.mageek.NetServer.res.RedisFactory;
import cn.mageek.NetServer.handler.*;
import cn.mageek.NetServer.pojo.WebMsgObject;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监听和接受连接请求，亦即创建channel并配置消息处理的handler
 * @author Mageek Chiu
 * @date 2018/3/7 0007:20:18
 */
public class ConnectionManager  implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private static final String NET_MSG = "netMsg";
    private final String port;
    private static final Map<String,Channel> channelMap = new ConcurrentHashMap<>();//管理所有连接


    public ConnectionManager(String port) {
        this.port = port;
    }

    public void run() {
//        new Thread(new RedisSub(channelMap)).start();//开启监听redis频道
        new Thread(new RedisSub()).start();//新开线程，开启监听redis频道

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);//接收连接
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);//处理连接的I/O事件
        EventExecutorGroup businessGroup = new DefaultEventExecutorGroup(8);//处理耗时业务逻辑，我实际上为了统一起见把全部业务逻辑都放这里面了
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//新建一个channel
                    .option(ChannelOption.SO_BACKLOG, 128)//最大等待连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // out 必须放在最后一个 in 前面，也就是必须是以 in 结尾。逻辑是in 顺序执行完毕以后从 pipeline 反向查找 out
                            ChannelPipeline p = ch.pipeline();
                            // out 执行顺序为注册顺序的逆序
                            // in 执行顺序为注册顺序
                            p.addLast("ReadTimeoutHandler",new ReadTimeoutHandler(600));// in // 多少秒超时
                            p.addLast("SendMsgHandler",new SendMsgHandler());// out //发送消息编码
                            p.addLast("ClientHandler",new ClientHandler(channelMap));// in //连接管理
                            p.addLast("RcvMsgHandler",new RcvMsgHandler());// in //解码消息
                            p.addLast(businessGroup,"BusinessHandler",new BusinessHandler());// in //解析业务数据
                            p.addLast(businessGroup,"PushMsgHandler",new PushMsgHandler());// out //合成推送消息
//                            p.addLast("OtherHandler",new OtherHandler());// in  // 纯粹是为了占位，把PushMsgHandler防止BusinessHandler下面。注释掉也没事，in 结尾 是扯淡，看源码，netty会自己找第一个

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
            logger.error("ConnectionManager start error: ", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            businessGroup.shutdownGracefully();
        }

    }

    class RedisSub implements Runnable {

//        private Map<String,Channel> channelMap;

//        public RedisSub(Map<String,Channel> channelMap){
//            this.channelMap = channelMap;
//        }
        @Override
        public void run() {
            try( Jedis jedis =  RedisFactory.getJedis() ) {
//                final JedisPubSub listener = new ConnectionManagerSubListener(channelMap);
                final JedisPubSub listener = new ConnectionManagerSubListener();
                logger.info("ConnectionManager begin to subscribe {}", NET_MSG);
                jedis.subscribe(listener, NET_MSG);//策略模式的运用
            }
        }
    }

    class ConnectionManagerSubListener extends JedisPubSub {
//        private Map<String,Channel> channelMap;

//        public ConnectionManagerSubListener(Map<String,Channel> channelMap){
//            this.channelMap = channelMap;
//        }

        // 取得订阅的消息后的处理
        public void onMessage(String channel, String message) {
//         运行   F:\workspace\java\NetServer>node src\main\java\cn\mageek\NetServer\helper\tcpclients.js
//         后立即运行  publish netMsg 00000000000000e0-00004720-000001a6-f5b932775e48cf4c-5cb1411f 得到日志
//         [INFO ] 2018-03-10 14:55:51,766 cn.mageek.NetServer.handler.ClientHandler.channelActive(ClientHandler.java:36):new connection arrived: /127.0.0.1:8340,uuid:00000000000000e0-00003808-0000021c-afede79a9c5bbb5e-aff2fa90, clients living 33
//         [INFO ] 2018-03-10 14:55:51,767 cn.mageek.NetServer.service.ConnectionManager$ConnectionManagerSubListener.onMessage(ConnectionManager.java:133):频道:netMsg，收到消息:00000000000000e0-00004720-000001a6-f5b932775e48cf4c-5cb1411f,客户端数量:33
//         [INFO ] 2018-03-10 14:55:51,780 cn.mageek.NetServer.handler.ClientHandler.channelActive(ClientHandler.java:36):new connection arrived: /127.0.0.1:8347,uuid:00000000000000e0-00003808-00000223-89c90f9a9c5bbb5f-f77566c1, clients living 34

            logger.info("频道:{}，收到消息:{},客户端数量:{}",channel,message,channelMap.size());
            try{
                WebMsgObject webMsgObject = JSON.parseObject(message,WebMsgObject.class);//根据消息字符串解析成消息对象
                Channel client = channelMap.get(webMsgObject.getClientId());//内部类可以直接访问外部类的变量
                if(client==null){
                    logger.error("{}不在线,消息：{}",webMsgObject.getClientId(),message);
                }else{
                    client.writeAndFlush(webMsgObject);
                }
            }catch (Exception e){
                logger.error("解析net消息,{} error: ",message,e);
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




