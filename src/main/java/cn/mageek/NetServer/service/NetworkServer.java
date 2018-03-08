package cn.mageek.NetServer.service;

import cn.mageek.NetServer.db.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.Properties;

/**
 * 管理本应用的所有服务
 * @author Mageek Chiu
 * @date 2018/3/5 0005:19:26
 */

public class NetworkServer {

    private static final Logger logger = LoggerFactory.getLogger(NetworkServer.class);

    public static void main(String[] args) throws Exception {
        try( InputStream in = ClassLoader.class.getResourceAsStream("/app.properties");
             InputStream in2 = ClassLoader.class.getResourceAsStream("/jedis.properties")){
            // 读取TCP配置
            Properties pop = new Properties();
            pop.load(in);
            String port = pop.getProperty("NetServer.port");
            logger.debug("config port:{}",port);
            // 读取redis配置初始化连接池
            Properties pop2 = new Properties();
            pop2.load(in2);
            RedisClient.construct(pop2);
            // 读取mysql配置
            // TODO


            // 分别启动3个服务
            new Thread(new ConnectionManager(port)).start();
            new Thread(new WebJobManager()).start();
            new Thread(new CronJobManager()).start();
        }catch(Exception ex) {
            logger.error("server start error: {}",ex.getMessage());
            ex.printStackTrace();
            RedisClient.destruct();
        }
    }
}
