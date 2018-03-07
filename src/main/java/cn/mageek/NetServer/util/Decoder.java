package cn.mageek.NetServer.util;

import cn.mageek.NetServer.pojo.MsgObject;
import io.netty.buffer.ByteBuf;

/**
 * 入站数据解码
 * @author Mageek Chiu
 * @date 2018/3/7 0007:18:52
 */
public class Decoder {

    /**
     * 将接收到的bit数据解析为消息对象
     * @param byteBuf
     * @return
     */
    public static MsgObject  bytesToObject(ByteBuf byteBuf){
        MsgObject msgObject = new MsgObject();
        return msgObject;
    }

}
