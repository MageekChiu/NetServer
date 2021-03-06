package cn.mageek.NetServer.res;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Properties;

/**
 * @author Mageek Chiu
 * @date 2018/3/7 0007:21:37
 */
public final class RedisFactory {

    private static final Logger logger = LoggerFactory.getLogger(RedisFactory.class);

    private static volatile JedisPool jedisPool;

    public static void construct(Properties properties){
        try {
//            for (String key : properties.stringPropertyNames()) {
//                logger.debug(key + "=" + properties.getProperty(key));
//            }

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(Integer.parseInt(properties.getProperty("jedis.pool.maxTotal")));
            config.setMaxIdle(Integer.parseInt(properties.getProperty("jedis.pool.maxIdle")));
            config.setMaxWaitMillis(Integer.parseInt(properties.getProperty("jedis.pool.maxWaitMillis")));
            config.setBlockWhenExhausted(Boolean.parseBoolean(properties.getProperty("jedis.pool.blockWhenExhausted")));
            config.setTestOnBorrow(Boolean.parseBoolean(properties.getProperty("jedis.pool.testOnBorrow")));
            config.setTestOnReturn(Boolean.parseBoolean(properties.getProperty("jedis.pool.testOnReturn")));
            if(jedisPool==null){//volatile+双重检查来实现单例模式
                synchronized (RedisFactory.class){
                    if (jedisPool==null){
                        jedisPool = new JedisPool(config,properties.getProperty("redis.host"), Integer.parseInt(properties.getProperty("redis.port")),
                                Integer.parseInt(properties.getProperty("redis.timeOut")),properties.getProperty("redis.auth"), Integer.parseInt(properties.getProperty("redis.db")));
                        logger.info("redis pool initialized");
                    }
                }
            }
        }catch (Exception e){
            logger.error("redis pool initialize error: ",e);
        }
    }

    public static Jedis getJedis(){
        return jedisPool.getResource();
    }

    public static void destruct(){
        jedisPool.close();
        logger.info("redis pool closed");
    }

}
