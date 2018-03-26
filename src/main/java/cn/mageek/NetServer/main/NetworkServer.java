package cn.mageek.NetServer.main;

import cn.mageek.NetServer.res.CommandFactory;
import cn.mageek.NetServer.res.MysqlFactory;
import cn.mageek.NetServer.res.RedisFactory;
import cn.mageek.NetServer.res.ZooKeeperFactory;
import cn.mageek.NetServer.service.ConnectionManager;
import cn.mageek.NetServer.service.CronJobManager;
import cn.mageek.NetServer.service.WebJobManager;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * 管理本应用的所有服务
 * @author Mageek Chiu
 * @date 2018/3/5 0005:19:26
 */

public class NetworkServer {

    private static final Logger logger = LoggerFactory.getLogger(NetworkServer.class);

    public static void main(String[] args) throws Exception {
        Thread.currentThread().setName("NetworkServer");
        Thread connectionManager,webJobManager,cronJobManager;
        try( InputStream in = ClassLoader.class.getResourceAsStream("/app.properties");
             InputStream in2 = ClassLoader.class.getResourceAsStream("/jedis.properties");
             InputStream in3 = ClassLoader.class.getResourceAsStream("/mysql.properties");
             InputStream in4 = ClassLoader.class.getResourceAsStream("/zk.properties")){
            // 读取服务器配置
            Properties pop = new Properties();
            pop.load(in);
            String port = pop.getProperty("NetServer.port");
            String serverId = pop.getProperty("NetServer.id");
            logger.debug("config port:{}",port);
            // 读取redis配置初始化连接池
            Properties pop2 = new Properties();
            pop2.load(in2);
            RedisFactory.construct(pop2);
            // 读取mysql配置并初始化
            Properties pop3 = new Properties();
            pop3.load(in3);
            MysqlFactory.construct(pop3);
            // 初始化ZooKeeper连接
            Properties pop4 = new Properties();
            pop4.load(in4);
            ZooKeeperFactory.construct(pop4);
            // 初始化命令对象
            CommandFactory.construct();

            CountDownLatch countDownLatch = new CountDownLatch(4);
            // 三个线程分别启动3个服务 连接管理服务、web消息监听服务、定时任务管理服务
            connectionManager = new Thread(new ConnectionManager(port,countDownLatch),"ConnectionManager");
            webJobManager = new Thread(new WebJobManager(countDownLatch),"WebJobManager");
            cronJobManager = new Thread(new CronJobManager(countDownLatch),"CronJobManager");
            connectionManager.start();webJobManager.start();cronJobManager.start();
            countDownLatch.await();//等待其他几个线程完全启动，然后才能对外提供服务
            logger.info("Network Server is fully up now");

            ZooKeeperFactory.getZooKeeper().create("/node_"+serverId,"up".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        }catch(Exception ex) {
            logger.error("server start error:",ex);//log4j能直接渲染stack trace
            RedisFactory.destruct();
            MysqlFactory.destruct();
            CommandFactory.destruct();
        }
    }
}
