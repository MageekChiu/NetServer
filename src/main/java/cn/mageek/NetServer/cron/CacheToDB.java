package cn.mageek.NetServer.cron;

import cn.mageek.NetServer.model.mapper.HistoryMapper;
import cn.mageek.NetServer.model.pojo.History;
import cn.mageek.NetServer.res.MysqlFactory;
import cn.mageek.NetServer.res.RedisFactory;
import com.alibaba.fastjson.JSON;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
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
//        modify();
    }

    private void add(){
        String SQL_QUERY = "insert into "+T_HISTORY+"(mac,signal) VALUES (?)";
        try(SqlSession sqlSession = MysqlFactory.getSession(ExecutorType.BATCH);// 批量插入
            Jedis jedis = RedisFactory.getJedis()){

            long keyLen = jedis.llen(K_HIST);
            Transaction tx = jedis.multi();
            for (long i = 0 ; i < keyLen ;i++){
                tx.rpop(K_HIST);
            }
            List<Object> resultList = tx.exec();
            HistoryMapper historyMapper = sqlSession.getMapper(HistoryMapper.class);
            resultList.forEach((data)->{
                History history = JSON.parseObject(data.toString(), History.class);//出队反序列化
                historyMapper.insert(history);//插入数据库
            });
            sqlSession.flushStatements();//提交
            logger.info(L_SUCCESS);
        }catch (Exception e){
            logger.error(L_ERROR,e);
        }
    }

    private void modify(){
        String SQL_QUERY = "insert into "+T_INFO+"(mac) VALUES (?)";
        try(SqlSession sqlSession = MysqlFactory.getSession(null);
            Jedis jedis = RedisFactory.getJedis()){

            // 遍历redis变量
            ScanParams params = new ScanParams().count(100).match(K_MAC);
            String cur = ScanParams.SCAN_POINTER_START;
            do {
                ScanResult<String> result = jedis.scan(cur,params);
                result.getResult().forEach((k)->{//键名称 k

                });
                cur = result.getStringCursor();
            }while (!(ScanParams.SCAN_POINTER_START.equals(cur)));

            logger.info(L_SUCCESS);
        }catch (Exception e){
            logger.error(L_ERROR,e);
        }
    }
}
