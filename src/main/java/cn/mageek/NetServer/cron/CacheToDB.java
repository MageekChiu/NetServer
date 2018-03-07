package cn.mageek.NetServer.cron;

import cn.mageek.NetServer.service.CronJobManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mageek Chiu
 * @date 2018/3/7 0007:19:43
 */
public class CacheToDB implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(CacheToDB.class);

    public void run(){
        logger.info("{}执行成功",this.getClass().getName());
    }
}
