package cn.mageek.NetServer.command;

import cn.mageek.NetServer.res.RedisFactory;
import cn.mageek.NetServer.model.net.RcvMsgObject;
import cn.mageek.NetServer.model.net.WebMsgObject;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mageek Chiu
 * @date 2018/3/7 0007:19:29
 */
public class Command81 implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Command81.class);

    @Override
    public RcvMsgObject receive(RcvMsgObject msgObject) {
        try( Jedis jedis =  RedisFactory.getJedis() ) {
            Map<String,String> map = new HashMap<>();
            map.put("timestamp", String.valueOf(msgObject.getTimestamp()));
            map.put("mac",msgObject.getMac());
            jedis.hmset("device:"+msgObject.getMac(),map);
            jedis.lpush("history", JSON.toJSONString(msgObject));//序列化入队
            logger.debug("转发收到并存储，{}",msgObject.getMac());

//            测试回复消息的handler执行顺序
            return new RcvMsgObject("M:","1234567899","1","81","1",(short)2,1234567890, Unpooled.copiedBuffer("4234234", CharsetUtil.UTF_8),"2");
//            return null;
        }
    }

    @Override
    public RcvMsgObject send(WebMsgObject webMsgObject) {
        logger.debug("合成消息，{}",webMsgObject);
        return new RcvMsgObject("M:","1234567899","1","81","1",(short)2,1234567890, Unpooled.copiedBuffer("4234234", CharsetUtil.UTF_8),"2");
    }


}
