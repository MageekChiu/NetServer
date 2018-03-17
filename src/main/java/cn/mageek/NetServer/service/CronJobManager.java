package cn.mageek.NetServer.service;

import cn.mageek.NetServer.cron.CacheToDB;
import cn.mageek.NetServer.cron.OnlineCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Mageek Chiu
 * @date 2018/3/7 0007:20:24
 */
public class CronJobManager implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CronJobManager.class);
    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    public void run() {
        scheduledExecutorService.scheduleAtFixedRate(new OnlineCount(),5,5, TimeUnit.SECONDS);
//        scheduledExecutorService.scheduleAtFixedRate(new OnlineCount(),5,3, TimeUnit.SECONDS);

        scheduledExecutorService.scheduleAtFixedRate(new CacheToDB(),5,24, TimeUnit.HOURS);

        logger.info("CronJobManager is up now");
    }

}
