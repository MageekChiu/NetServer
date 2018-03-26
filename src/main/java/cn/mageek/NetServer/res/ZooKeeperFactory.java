package cn.mageek.NetServer.res;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

/**
 * Command 工厂类
 * @author Mageek Chiu
 * @date 2018/3/13 0013:21:49
 */
public class ZooKeeperFactory {
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperFactory.class);

    private static volatile ZooKeeper zooKeeper;

    public static void construct(Properties properties) throws Exception {
        try {
            if (zooKeeper == null) {//volatile+双重检查来实现单例模式
                synchronized (ZooKeeperFactory.class) {
                    if (zooKeeper == null) {
                        zooKeeper = new ZooKeeper(properties.getProperty("zk.connectionString"), Integer.parseInt(properties.getProperty("zk.sessionTimeout")), new ZKWatcher());
                        logger.info("zooKeeper client initialization begin ");
                    }
                }
            }
        }catch (Exception e){
            logger.error("zooKeeper client initialize error: ",e);
        }
    }

    public static ZooKeeper getZooKeeper(){
        return zooKeeper;
    }

    public static void destruct(){
        zooKeeper = null;
    }

    static class ZKWatcher implements Watcher{

        @Override
        public void process(WatchedEvent watchedEvent) {
            switch (watchedEvent.getState()){
                case SyncConnected:
                    logger.info("zooKeeper client SyncConnected");
                    break;
                case Disconnected:
                    logger.info("zooKeeper client Disconnected");
                    break;
                case Expired:
                    logger.info("zooKeeper client Expired");
                    break;
                case AuthFailed:
                    logger.info("zooKeeper client AuthFailed");
                    break;
                case ConnectedReadOnly:
                    logger.info("zooKeeper client ConnectedReadOnly");
                    break;
                case SaslAuthenticated:
                    logger.info("zooKeeper client SaslAuthenticated");
                    break;
                default:
                    logger.info("zooKeeper client received other event:{}",watchedEvent);

            }
        }
    }

}
