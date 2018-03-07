package cn.mageek.NetServer.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mageek Chiu
 * @date 2018/3/7 0007:19:44
 */
public class OnlineCount implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(OnlineCount.class);

    public void run(){
        logger.info("{}执行成功",this.getClass().getName());
    }
}
