package cn.mageek.NetServer.command;

import cn.mageek.NetServer.pojo.RcvMsgObject;
import cn.mageek.NetServer.pojo.WebMsgObject;

/**
 * 抽象策略类
 * @author Mageek Chiu
 * @date 2018/3/7 0007:19:27
 */
public interface Command {

    // 处理收到来自客户端的消息，返回需要响应的消息，若无则返回null
    RcvMsgObject receive(RcvMsgObject msgObject);
    // 根据参数返回发送给客户端的消息
    RcvMsgObject send(WebMsgObject webMsgObject);

}
