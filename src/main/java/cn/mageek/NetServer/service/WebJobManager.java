package cn.mageek.NetServer.service;

import cn.mageek.NetServer.db.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * @author Mageek Chiu
 * @date 2018/3/7 0007:20:24
 */
public class WebJobManager implements Runnable{

    private static final String CHANNEL = "webMsg";
    private static final Logger logger = LoggerFactory.getLogger(WebJobManager.class);

    public void run() {

        try( Jedis jedis =  RedisClient.getJedis() ) {
            final WebJobSubListener listener = new WebJobSubListener();
            logger.info("WebJobManager begin to subscribe {}",CHANNEL);
            jedis.set("server","up");
            jedis.subscribe(listener,CHANNEL);//策略模式的运用
//            jedis.close();//不需要关闭 因为订阅会一直使用着。而且Jedis implements Closeable，所以try with 会自动关闭
//            // subscribe 后面的代码不会执行，因为是一个阻塞操作
//            logger.info("WebJobManager is up now");
//            jedis.publish(CHANNEL, "bar123");
//            jedis.set("server1","up1");
        }
    }

    class WebJobSubListener extends JedisPubSub {

        // 取得订阅的消息后的处理
        public void onMessage(String channel, String message) {
            logger.info("频道:{}，收到消息:{}",channel,message);
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

