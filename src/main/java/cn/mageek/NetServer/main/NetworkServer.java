package cn.mageek.NetServer.main;

import cn.mageek.NetServer.res.CommandFactory;
import cn.mageek.NetServer.res.MysqlFactory;
import cn.mageek.NetServer.res.RedisFactory;
import cn.mageek.NetServer.service.ConnectionManager;
import cn.mageek.NetServer.service.CronJobManager;
import cn.mageek.NetServer.service.WebJobManager;
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
             InputStream in2 = ClassLoader.class.getResourceAsStream("/jedis.properties");
             InputStream in3 = ClassLoader.class.getResourceAsStream("/mysql.properties")){
            // 读取TCP配置
            Properties pop = new Properties();
            pop.load(in);
            String port = pop.getProperty("NetServer.port");
            logger.debug("config port:{}",port);
            // 读取redis配置初始化连接池
            Properties pop2 = new Properties();
            pop2.load(in2);
            RedisFactory.construct(pop2);
            // 读取mysql配置并初始化
            Properties pop3 = new Properties();
            pop3.load(in3);
            MysqlFactory.construct(pop3);
            // 初始化命令对象
            CommandFactory.construct();

            // 三个线程分别启动3个服务 连接管理服务、web消息监听服务、定时任务管理服务
            new Thread(new ConnectionManager(port),"ConnectionManager").start();
            new Thread(new WebJobManager(),"WebJobManager").start();
            new Thread(new CronJobManager(),"CronJobManager").start();
        }catch(Exception ex) {
            logger.error("server start error:",ex);//log4j能直接渲染stack trace
            RedisFactory.destruct();
            MysqlFactory.destruct();
            CommandFactory.destruct();
        }
        Thread.currentThread().setName("mainJob");
    }
}
