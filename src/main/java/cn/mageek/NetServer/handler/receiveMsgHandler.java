package cn.mageek.NetServer.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理接受到的消息的handler
 * @author Mageek Chiu
 * @date 2018/3/5 0005:19:02
 */
public class receiveMsgHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(receiveMsgHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf  = (ByteBuf) msg;
        try {
            logger.info("receiveMsg :{}",buf.toString(CharsetUtil.UTF_8));
//            ctx.write(buf); // 写入缓冲区
//            ctx.flush(); // 将缓冲区发送给客户端
            ctx.writeAndFlush(buf);//上面两步合为一步
        } finally {
//            buf 如果上面被发送到另一个channel了（用了write），这里就不能释放了，因为释放buf已经变成了另一个channel的责任了
            ReferenceCountUtil.release(buf); // 引用计数，清除引用，便于释放内存
//            buf.release();// 和上面作用一样
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        logger.error("receiveMsg error:{}",cause.getMessage());
    }
}
