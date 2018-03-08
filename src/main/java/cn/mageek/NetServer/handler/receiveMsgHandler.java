package cn.mageek.NetServer.handler;

import cn.mageek.NetServer.command.Command;
import cn.mageek.NetServer.pojo.RcvMsgObject;
import cn.mageek.NetServer.util.Decoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 处理server接受到来自client的消息的handler，业务逻辑的核心
 * @author Mageek Chiu
 * @date 2018/3/5 0005:19:02
 */
public class receiveMsgHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(receiveMsgHandler.class);
    private static AtomicInteger clientNumber = new AtomicInteger(0);
    private Map<String,Channel> channelMap;

    public receiveMsgHandler(Map<String,Channel> channelMap){
        this.channelMap = channelMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        logger.debug("channel: {}",this);//根据hashcode，每个channel的handler是不同的对象
        String uuid = ctx.channel().id().asLongText();
        channelMap.put(uuid,ctx.channel());
        logger.info("new connection arrived: {},uuid:{}, clients living {}",ctx.channel().remoteAddress(),uuid,clientNumber.incrementAndGet());//包含ip:port


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String uuid = ctx.channel().id().asLongText();
        channelMap.remove(uuid);
        logger.info("connection closed: {},uuid:{}, clients living {}",ctx.channel().remoteAddress(),uuid,clientNumber.decrementAndGet());//包含ip:port
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf  = (ByteBuf) msg;
        try {
            logger.info("receiveMsg: {} ,from: {}",ByteBufUtil.hexDump(buf),ctx.channel().remoteAddress());
//            ctx.write(buf); // 写入缓冲区，write操作可以把把数据直接传递给OutBoundHandler
//            ctx.flush(); // 将缓冲区发送给客户端

//            logger.info("refCnt before write: {}",buf.refCnt());//1
//            ctx.writeAndFlush(buf);//上面两步合为一步
//            logger.info("refCnt after write: {}",buf.refCnt());//0

//            logger.info("refCnt before write: {}",buf.refCnt());//1
//            CompositeByteBuf compositeByteBuf =Unpooled.compositeBuffer();
//            ctx.writeAndFlush(compositeByteBuf.addComponents(true , Unpooled.copiedBuffer("get msg ",CharsetUtil.UTF_8) , buf));//必须加上true才能合并成一个ByteBuf
//            logger.info("refCnt after write: {}",buf.refCnt());//0

//            ctx.fireChannelRead(Unpooled.compositeBuffer().addComponents(true,Unpooled.copiedBuffer("get msg ",CharsetUtil.UTF_8),buf));//fire操作把数据传输给下一个InboundHandler

            // 将 buffer 解析成 对象 并转发
            RcvMsgObject msgObject = Decoder.bytesToObject(buf);
            logger.info("parsed data:\n{}",msgObject);
            Command command = (Command)Class.forName("cn.mageek.NetServer.command.Command"+msgObject.getCommand()).newInstance();//反射并创建类，类名必须写全，因为Command不止一个包下面有，会产生冲突
            command.receive(msgObject);

        }catch (Exception e){
            logger.error("parse data :{} , error :{} , from: {}", ByteBufUtil.hexDump(buf),e.getMessage(),ctx.channel().remoteAddress());
            e.printStackTrace();
        }finally {
//            buf 如果在上面被发送到另一个Handler了（用了write），这里就不能释放了，因为释放buf已经变成了另一个Handler或者自定义对象（如上面的Decoder）的责任了，这里再释放就会报错
//            ReferenceCountUtil.release(buf); // 引用计数，清除引用，便于释放内存
//            buf.release();// 和上面作用一样
        }
    }

//    the channelReadComplete() only signals that there will be no more read for the Channel in the current even-loop run. So you could for example flush now
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        logger.info("receiveMsg completed from: {}",ctx.channel().remoteAddress());
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("receiveMsg error:{} ,from: {}",cause.getMessage(),ctx.channel().remoteAddress());
        cause.printStackTrace();
        ctx.close();//这时一般就会自动关闭连接了。手动关闭的目的是避免偶尔情况下会处于未知状态
    }
}
