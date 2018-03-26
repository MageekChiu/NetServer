package cn.mageek.NetServer.res;

import org.apache.curator.RetryPolicy;
import org.apache.curator.RetrySleeper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
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

//    private static volatile ZooKeeper zooKeeper;

    private static volatile CuratorFramework client;

    public static void construct(Properties properties) throws Exception {
        try {
            if (client == null) {//volatile+双重检查来实现单例模式
                synchronized (ZooKeeperFactory.class) {
                    if (client == null) {
                        // zookeeper的原生api相对来说比较繁琐，比如：对节点添加监听事件，当监听触发后，我们需要再次手动添加监听，否则监听只生效一次,断线重连也需要我们手动代码来判断处理等等。
                        // Curator中采用cache来封装了对事件的监听,包括监听节点,监听子节点等
//                        zooKeeper = new ZooKeeper(properties.getProperty("zk.connectionString"), Integer.parseInt(properties.getProperty("zk.sessionTimeout")), new ZKWatcher());

                        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,10);
//                        client = CuratorFrameworkFactory.newClient(properties.getProperty("zk.connectionString"),retryPolicy);
                        client = CuratorFrameworkFactory.builder().connectString(properties.getProperty("zk.connectionString")).retryPolicy(retryPolicy).connectionTimeoutMs(Integer.parseInt(properties.getProperty("zk.sessionTimeout"))).build();
                        client.start();//开启连接
                        logger.info("zooKeeper client initialization begin ");
                        String pattern = properties.getProperty("zk.pattern");
                        NodeCache nodeCache = new NodeCache(client,pattern,false);
                        nodeCache.start(true);
                        nodeCache.getListenable().addListener(new NodeCacheListener() {
                            @Override
                            public void nodeChanged() throws Exception {
                                logger.info("节点发生变化：{}",nodeCache.getCurrentData().getData());
                            }
                        });
                        PathChildrenCache pathChildrenCache = new PathChildrenCache(client,pattern,false);
                        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                            @Override
                            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                                logger.info("子节点发生变化：{}",pathChildrenCacheEvent.getData());
                            }
                        });

                    }
                }
            }
        }catch (Exception e){
            logger.error("zooKeeper client initialize error: ",e);
        }
    }

//    public static ZooKeeper getZooKeeper(){
//        return zooKeeper;
//    }
    public static CuratorFramework getZooKeeper(){
//        client.start();
        return client;
    }

    public static void destruct(){
        //            zooKeeper.close();
        //            zooKeeper = null;
        client.close();
        client=null;

    }

//    static class ZKWatcher implements Watcher{
//
//        @Override
//        public void process(WatchedEvent watchedEvent) {
//            Event.EventType eventType = watchedEvent.getType();
//            Event.KeeperState state = watchedEvent.getState();
//            String watchPath = watchedEvent.getPath();
//            logger.info("zooKeeper client received event:{}",watchedEvent);
//            switch (state){
//                case SyncConnected:
//                    logger.info("zooKeeper client SyncConnected");
//                    break;
//                case Disconnected:
//                    logger.info("zooKeeper client Disconnected");
//                    break;
//                case Expired:
//                    logger.info("zooKeeper client Expired");
//                    break;
//                case AuthFailed:
//                    logger.info("zooKeeper client AuthFailed");
//                    break;
//                case ConnectedReadOnly:
//                    logger.info("zooKeeper client ConnectedReadOnly");
//                    break;
//                case SaslAuthenticated:
//                    logger.info("zooKeeper client SaslAuthenticated");
//                    break;
//                default:
//                    logger.info("zooKeeper client received other event");
//            }
//            switch (eventType){
//                case NodeCreated:
//                    logger.info("zooKeeper client NodeCreated");
//                    handleMsg(watchPath);
//                    break;
//                case NodeDeleted:
//                    logger.info("zooKeeper client NodeDeleted");
//                    handleMsg(watchPath);
//                    break;
//                case NodeDataChanged:
//                    logger.info("zooKeeper client NodeDataChanged");
//                    handleMsg(watchPath);
//                    break;
//                case NodeChildrenChanged:
//                    logger.info("zooKeeper client NodeChildrenChanged");
//                    handleMsg(watchPath);
//                    break;
//                default:
//            }
//        }
//
//        public  void handleMsg(String watchPath){
//            try {
//                logger.info("watchPath:{},{}",watchPath,zooKeeper.getData(watchPath,null,null));
//            } catch (KeeperException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
