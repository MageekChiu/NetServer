package cn.mageek.NetServer.pojo;

import io.netty.buffer.ByteBuf;

/**
 * 消息对象
 * @author Mageek Chiu
 * @date 2018/3/7 0007:18:56
 */
public class MsgObject {
    private String header;
    private String status;
    private String command;
    private int dataLength;
    private ByteBuf data;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public ByteBuf getData() {
        return data;
    }

    public void setData(ByteBuf data) {
        this.data = data;
    }
}
