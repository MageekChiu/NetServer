package cn.mageek.NetServer.service;

import cn.mageek.NetServer.db.redisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

/**
 * @author Mageek Chiu
 * @date 2018/3/7 0007:20:24
 */
public class WebJobManager implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(WebJobManager.class);

    public WebJobManager(){

    }

    public void run() {
        JedisPool jedisPool =  redisClient.jedisPool;

        logger.info("WebJobManager is up now");


    }
}
