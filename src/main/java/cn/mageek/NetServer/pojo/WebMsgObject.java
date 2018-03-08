package cn.mageek.NetServer.pojo;

/**
 * web传递的的消息对象
 * @author Mageek Chiu
 * @date 2018/3/8 0008:20:14
 */
public class WebMsgObject {
    private String clientId;
    private String command;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}
