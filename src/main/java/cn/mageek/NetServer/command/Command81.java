package cn.mageek.NetServer.command;

import cn.mageek.NetServer.db.RedisClient;
import cn.mageek.NetServer.pojo.NetMsgObject;
import cn.mageek.NetServer.pojo.RcvMsgObject;
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
    public void receive(RcvMsgObject msgObject) {
        try( Jedis jedis =  RedisClient.getJedis() ) {
            Map<String,String> map = new HashMap<>();
            map.put("timestamp", String.valueOf(msgObject.getTimestamp()));
            map.put("mac",msgObject.getMac());
            jedis.hmset("device:"+msgObject.getMac(),map);
            logger.debug("转发收到并存储，{}",msgObject.getMac());
        }
    }

    @Override
    public RcvMsgObject send(NetMsgObject netMsgObject) {

        return null;
    }


}
