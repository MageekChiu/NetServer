package cn.mageek.NetServer.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理接受到的消息的handler，业务逻辑的核心
 * @author Mageek Chiu
 * @date 2018/3/5 0005:19:02
 */
public class receiveMsgHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(receiveMsgHandler.class);
    private static int clientNumber = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("new connection arrived: {}, clients living {}",ctx.channel().remoteAddress(),++clientNumber);//包含ip:port
//        logger.debug("channel: {}",this);//根据hashcode，每个channel的handler是不同的对象
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("connection expired: {}, clients living {}",ctx.channel().remoteAddress(),--clientNumber);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf  = (ByteBuf) msg;
        try {
            logger.info("receiveMsg: {}",buf.toString(CharsetUtil.UTF_8));
//            ctx.write(buf); // 写入缓冲区
//            ctx.flush(); // 将缓冲区发送给客户端

//            logger.info("refCnt before write: {}",buf.refCnt());//1
//            ctx.writeAndFlush(buf);//上面两步合为一步
//            logger.info("refCnt after write: {}",buf.refCnt());//0

            ctx.writeAndFlush(Unpooled.copiedBuffer("get msg ",CharsetUtil.UTF_8));

        } finally {
//            buf 如果在上面被发送到另一个channel了（用了write），这里就不能释放了，因为释放buf已经变成了另一个channel的责任了，这里再释放就会报错
//            ReferenceCountUtil.release(buf); // 引用计数，清除引用，便于释放内存
//            buf.release();// 和上面作用一样
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("receiveMsg error:{}",cause.getMessage());
        cause.printStackTrace();
        ctx.close();//这时一般就会自动关闭连接了。手动关闭的目的是避免偶尔情况下会处于未知状态
    }
}
