package cn.mageek.NetServer.handler;

import cn.mageek.NetServer.command.Command;
import cn.mageek.NetServer.pojo.RcvMsgObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理server接受到来自client的消息对象的handler，业务逻辑的核心
 * @author Mageek Chiu
 * @date 2018/3/10 0010:16:22
 */
public class BusinessHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(BusinessHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        RcvMsgObject rcvMsgObject = (RcvMsgObject)obj;//转换消息对象
        logger.debug("business data from: {},: \n{}",ctx.channel().remoteAddress(), rcvMsgObject);
        Command command = (Command)Class.forName("cn.mageek.NetServer.command.Command"+rcvMsgObject.getCommand()).newInstance();//反射并创建类，类名必须写全，因为Command类不止一个包下面有，会产生冲突
        RcvMsgObject rcvMsgObject1 = command.receive(rcvMsgObject);//接受消息并处理
        if (rcvMsgObject1!=null){//如果需要响应操作
            ctx.writeAndFlush(rcvMsgObject1);//从当前位置往上找outBound
//            ctx.channel().writeAndFlush(rcvMsgObject);//从最底部找outBound
        }
    }
}
