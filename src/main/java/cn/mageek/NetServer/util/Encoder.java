package cn.mageek.NetServer.util;

import cn.mageek.NetServer.pojo.MsgObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 出站数据编码
 * @author Mageek Chiu
 * @date 2018/3/7 0007:18:53
 */
public class Encoder {
    /**
     * 将消息对象解析为bit数据
     * @param  msgObject
     * @return
     */
    public static ByteBuf objectToBytes(MsgObject msgObject){
        ByteBuf byteBuf = Unpooled.buffer();
        return byteBuf;
    }
}
