package cn.mageek.NetServer.service;

import cn.mageek.NetServer.db.RedisClient;
import cn.mageek.NetServer.pojo.WebMsgObject;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * 监听来自web的指令并转发给connectionManager
 * 之所以不直接在connectionManager监听是为了扩展性，因为以后可能有来自rpc、http等其他地方的指令
 * 这样加一个中间层，不管将来指令来自哪里，只需加另一个中间层组件如rpcJobManager，而connectionManager不变，只需一直监听redis频道即可
 * @author Mageek Chiu
 * @date 2018/3/7 0007:20:24
 */
public class WebJobManager implements Runnable{

    private static final String WEB_MSG = "webMsg";
    private static final String NET_MSG = "netMsg";

    private static final Logger logger = LoggerFactory.getLogger(WebJobManager.class);

    public void run() {

        try( Jedis jedis =  RedisClient.getJedis() ) {
            final WebJobSubListener listener = new WebJobSubListener();
            logger.info("WebJobManager begin to subscribe {}", WEB_MSG);
            jedis.set("server","up");
            jedis.subscribe(listener, WEB_MSG);//策略模式的运用
//            jedis.close();//不需要关闭 因为订阅会一直使用着。而且Jedis implements Closeable，所以try with 会自动关闭
//            // subscribe 后面的代码不会执行，因为是一个阻塞操作
//            logger.info("WebJobManager is up now");
//            jedis.publish(WEB_MSG, "bar123");
//            jedis.set("server1","up1");
        }catch (Exception e){
            logger.error("web Job Listen to redis error:",e);
        }
    }

    class WebJobSubListener extends JedisPubSub {

        private Jedis jedis;

        public WebJobSubListener(){
            jedis = RedisClient.getJedis();
        }

        // 取得订阅的消息后的处理
        public void onMessage(String channel, String message) {
            logger.info("频道:{}，收到消息:{}",channel,message);
            try {
                WebMsgObject webMsgObject = JSON.parseObject(message,WebMsgObject.class);
//                NetMsgObject netMsgObject = new NetMsgObject();
                String webMsgString = JSON.toJSONString(webMsgObject);
                jedis.publish(NET_MSG,webMsgString);
            }catch (Exception e){
                logger.error("解析web消息,{} error",message,e);
                e.printStackTrace();
            }
        }

        // 初始化订阅时候的处理
        public void onSubscribe(String channel, int subscribedChannels) {
            logger.info("订阅:{}，总数:{}",channel,subscribedChannels);
            logger.info("WebJobManager is up now");
        }

        // 取消订阅时候的处理
        public void onUnsubscribe(String channel, int subscribedChannels) {
            logger.info("取消订阅:{}，总数:{}",channel,subscribedChannels);
        }

        // 初始化按表达式的方式订阅时候的处理
        public void onPSubscribe(String pattern, int subscribedChannels) {
            logger.info(pattern + "=" + subscribedChannels);
        }

        // 取消按表达式的方式订阅时候的处理
        public void onPUnsubscribe(String pattern, int subscribedChannels) {
            logger.info(pattern + "=" + subscribedChannels);
        }

        // 取得按表达式的方式订阅的消息后的处理
        public void onPMessage(String pattern, String channel, String message) {
            logger.info(pattern + "=" + channel + "=" + message);
        }

    }


}

