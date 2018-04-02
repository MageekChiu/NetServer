package cn.mageek.NetServer.cron;

import cn.mageek.NetServer.res.MysqlFactory;
import cn.mageek.NetServer.res.RedisFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static cn.mageek.NetServer.util.ConstPool.*;

/**
 * @author Mageek Chiu
 * @date 2018/3/7 0007:19:43
 */
public class CacheToDB implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(CacheToDB.class);


    public void run(){
        add();
        modify();
    }

    private void add(){
        String SQL_QUERY = "insert into "+T_HISTORY+"(mac,signal) VALUES (?)";
        try(Connection con = MysqlFactory.getConnection();
            Jedis jedis = RedisFactory.getJedis()){
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);

            long keyLen = jedis.llen(K_HIST);
            Transaction tx = jedis.multi();
            for (long i = 0 ; i < keyLen ;i++){
                tx.rpop(K_HIST);
            }
            List<Object> resultList = tx.exec();

            resultList.forEach((data)->{

            });

            pst.setString(1, "sasdsadsas");
            pst.executeUpdate();
            logger.info(L_SUCCESS);
        }catch (Exception e){
            logger.error(T_ERROR,e);
        }
    }

    private void modify(){
        String SQL_QUERY = "insert into device(mac) VALUES (?)";
        try(Connection con = MysqlFactory.getConnection();
            Jedis jedis = RedisFactory.getJedis()){
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);

            // 遍历redis变量
            ScanParams params = new ScanParams().count(100).match(K_MAC);
            String cur = ScanParams.SCAN_POINTER_START;
            do {
                ScanResult<String> result = jedis.scan(cur,params);
                result.getResult().forEach((k)->{//键名称 k

                });
                cur = result.getStringCursor();
            }while (!(ScanParams.SCAN_POINTER_START.equals(cur)));


            pst.setString(1, "sasdsadsas");
            pst.executeUpdate();
            logger.info(L_SUCCESS);
        }catch (Exception e){
            logger.error(T_ERROR,e);
        }
    }
}
