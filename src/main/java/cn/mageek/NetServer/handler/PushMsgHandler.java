package cn.mageek.NetServer.handler;

import cn.mageek.NetServer.command.Command;
import cn.mageek.NetServer.pojo.RcvMsgObject;
import cn.mageek.NetServer.pojo.WebMsgObject;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  处理server推送消息的handler，负责根据netMsg得到待发送消息并传递给下一个handler
 * @author Mageek Chiu
 * @date 2018/3/6 0006:19:59
 */
public class PushMsgHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(PushMsgHandler.class);


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // 这种判断的方式比较浪费性能，是否有更优雅的解决方式？？？
        // 交换 PushMsgHandler 和 BusinessHandler 顺序即可，但是BusinessHandler必须处于最后？？
        // 可以再加一个InboundHandler
        if(msg instanceof WebMsgObject ){//是主动推送，需要编码
            WebMsgObject webMsgObject = (WebMsgObject)msg;;//根据消息字符串解析成消息对象
            RcvMsgObject rcvMsgObject =  ((Command)Class.forName("cn.mageek.NetServer.command.Command"+webMsgObject.getCommand()).newInstance()).send(webMsgObject);
            logger.debug("pushMsg: {} to {}",rcvMsgObject,ctx.channel().remoteAddress());
//            super.write(ctx,rcvMsgObject,promise);
            ctx.writeAndFlush(rcvMsgObject);
        }else if (msg instanceof RcvMsgObject ){
            RcvMsgObject rcvMsgObject = (RcvMsgObject)msg;//是回复，可以直接传输给下一个handler
            logger.debug("feedbackMsg: {} to {}",rcvMsgObject,ctx.channel().remoteAddress());
//            super.write(ctx,rcvMsgObject,promise);
            ctx.writeAndFlush(rcvMsgObject);
        }else{
            logger.error("error pushMsg: {} to {}",msg,ctx.channel().remoteAddress());
        }
    }
}
