package cn.mageek.NetServer.handler;

import cn.mageek.NetServer.command.Command;
import cn.mageek.NetServer.res.CommandFactory;
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
//        logger.debug("business data from: {},: \n{}",ctx.channel().remoteAddress(), rcvMsgObject);
//        logger.debug("验证线程模型 thread:{}",Thread.currentThread().getName());//thread:defaultEventExecutorGroup-4-1
        // 反射加策略模式的运用，替代大量的switch case ,但是 反射效率较低。这一块请求量一旦上去估计会触发JIT
//        Command command = (Command)Class.forName("cn.mageek.NetServer.command.Command"+rcvMsgObject.getCommand()).newInstance();//反射并创建类，类名必须写全，因为Command类不止一个包下面有，会产生冲突
        // 反射的时间没法省略，但是可以利用工厂方法加原型模式来节省创建对象的时间，
        // **********************
        // 或者启动的时候就实例化这些类，然后缓存起来，用的时候直接获取，使用单例模式，这样能达到运行时时延小，启动时慢一点无所谓
        Command command = CommandFactory.getCommand(rcvMsgObject.getCommand());
        if(command==null){
            logger.error("错误的命令: {}",rcvMsgObject);
            return;
        }
        // 上面其实就是类似于 tomcat 加载 servlet 的时机选择的情况

        // 命令模式的使用，这个命令是自定义的
        RcvMsgObject rcvMsgObject1 = command.receive(rcvMsgObject);//接受消息并处理
        if (rcvMsgObject1!=null){//如果需要响应操作
            ctx.writeAndFlush(rcvMsgObject1);//从当前位置往上找outBound
//            ctx.channel().writeAndFlush(rcvMsgObject);//从最底部找outBound
        }

    }
}
