package cn.mageek.NetServer.db;


import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Mageek Chiu
 * @date 2018/3/7 0007:21:37
 */
public class redisClient {
    public static JedisPool jedisPool;

    public static void  construct(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(jedisPoolConfig,"localhost",6379);
    }

}
