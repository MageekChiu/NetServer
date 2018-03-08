package cn.mageek.NetServer.command;

import cn.mageek.NetServer.pojo.RcvMsgObject;

/**
 * @author Mageek Chiu
 * @date 2018/3/7 0007:19:27
 */
public interface Command {

    void receive(RcvMsgObject msgObject);
    void send();

}
