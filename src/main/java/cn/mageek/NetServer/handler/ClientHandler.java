package cn.mageek.NetServer.handler;

import cn.mageek.NetServer.db.RedisClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 管理客户端的在线状态的handler
 * @author Mageek Chiu
 * @date 2018/3/5 0005:19:02
 */
//@ChannelHandler.Sharable//必须是线程安全的
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private static final AtomicInteger clientNumber = new AtomicInteger(0);
    // 下面的 field 是线程安全的，所以多个线程修改是没有问题的。
    private Map<String,Channel> channelMap;

    public ClientHandler(Map<String,Channel> channelMap){
        this.channelMap = channelMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        logger.debug("channel: {}",this);//根据hashcode，每个channel的handler是不同的对象
        String uuid = ctx.channel().id().asLongText();
        channelMap.put(uuid,ctx.channel());
        logger.info("new connection arrived: {},uuid:{}, clients living {}",ctx.channel().remoteAddress(),uuid,clientNumber.incrementAndGet());//包含ip:port
        try( Jedis jedis =  RedisClient.getJedis() ) {
            jedis.set(ctx.channel().remoteAddress().toString(),uuid);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String uuid = ctx.channel().id().asLongText();
        channelMap.remove(uuid);
        logger.info("connection closed: {},uuid:{}, clients living {}",ctx.channel().remoteAddress(),uuid,clientNumber.decrementAndGet());//包含ip:port
        try( Jedis jedis =  RedisClient.getJedis() ) {
            jedis.del(ctx.channel().remoteAddress().toString());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.fireChannelRead(msg);//传输到下一个inBound
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("receiveMsg from: {}，error",ctx.channel().remoteAddress(),cause);//ReadTimeoutException 会出现在这里，亦即事件会传递到handler链中最后一个事件处理中
        ctx.close();//这时一般就会自动关闭连接了。手动关闭的目的是避免偶尔情况下会处于未知状态
    }
}
