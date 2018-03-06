package cn.mageek.NetServer.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 *  处理server发出给client的消息的handler
 * @author Mageek Chiu
 * @date 2018/3/6 0006:19:59
 */
public class sendMsgHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(sendMsgHandler.class);

//    @Override
//    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
//        logger.info("connected: {}",ctx.channel().remoteAddress());
//    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        CompositeByteBuf compositeByteBuf =  Unpooled.compositeBuffer();
        compositeByteBuf.addComponents(true,Unpooled.copiedBuffer("sendMsgHandlerWraper ",CharsetUtil.UTF_8),(ByteBuf)msg);
        logger.debug("sendMsg: {} to {}",compositeByteBuf.toString(CharsetUtil.UTF_8),ctx.channel().remoteAddress());
        ctx.writeAndFlush(compositeByteBuf);
        promise.setSuccess();

//        super.write(ctx, compositeByteBuf, promise);//传递给下一个OutboundHandler
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("sendMsg error:{} ,from: {}",cause.getMessage(),ctx.channel().remoteAddress());
        cause.printStackTrace();
        ctx.close();//这时一般就会自动关闭连接了。手动关闭的目的是避免偶尔情况下会处于未知状态
    }
}
