package cn.mageek.NetServer.command;

/**
 * @author Mageek Chiu
 * @date 2018/3/7 0007:19:27
 */
public interface Command {

    void receive();
    void send();

}
