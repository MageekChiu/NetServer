package cn.mageek.NetServer.cron;

import cn.mageek.NetServer.model.mapper.HistoryMapper;
import cn.mageek.NetServer.model.mapper.InfoMapper;
import cn.mageek.NetServer.model.pojo.History;
import cn.mageek.NetServer.model.pojo.Info;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static cn.mageek.NetServer.res.ConstPool.*;

/**
 * @author Mageek Chiu
 * @date 2018/3/7 0007:19:43
 */
public class CacheToDB implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(CacheToDB.class);


    public void run(){
//        add();
        modify();
    }

    private void add(){
        // 批量插入，但batch模式也有问题：在Insert时，在事务没有提交之前，是没有办法获取到自增的id，在某型情形下是不符合业务要求的
        try(SqlSession sqlSession = MysqlFactory.getSession(ExecutorType.BATCH);
            Jedis jedis = RedisFactory.getJedis()){
            long keyLen = jedis.llen(K_HIST);//
            int transTime = (int) Math.ceil((double)keyLen/NUMBER_PER_TIME);// 必须加double 不然达不到向上取整效果
            logger.debug("总共{}条, 每次{}条, 需要同步{}次",keyLen,NUMBER_PER_TIME,transTime);

            Transaction tx; List<Object> resultList;
            HistoryMapper historyMapper = sqlSession.getMapper(HistoryMapper.class);
            for (int i = 0 ; i < transTime; i++){
                tx = jedis.multi();// 不能放在外面
                for (long j = 0 ; j < NUMBER_PER_TIME ;j++){// 每次提交一定数量而非全部，避免溢出
                    tx.rpop(K_HIST);
                }
                resultList = tx.exec();
                resultList.forEach((data)->{
                    if (data == null) return;// == continue;foreach不是设计为可以用break以及continue来中止的操作。
                    History history = JSON.parseObject(data.toString(), History.class);//出队反序列化
                    historyMapper.insert(history);//插入数据库
                });
//            sqlSession.flushStatements();//这个不能提交
                sqlSession.commit();//提交
                sqlSession.clearCache();//防止溢出
            }
            logger.info(L_SUCCESS);//根据debug显示,提交成功后会 Resetting autocommit to true on JDBC Connection，:Closing JDBC Connection
        }catch (Exception e){
            logger.error(L_ERROR,e);
        }
    }

    private void modify(){
        try(SqlSession sqlSession = MysqlFactory.getSession(ExecutorType.BATCH);
            Jedis jedis = RedisFactory.getJedis()){

            // 遍历redis变量
            ScanParams params = new ScanParams().count(100).match(K_MAC);
            String cur = ScanParams.SCAN_POINTER_START;//其实就是0
            List<Object> resultList;
            InfoMapper infoMapper = sqlSession.getMapper(InfoMapper.class);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//
            do {
                ScanResult<String> result = jedis.scan(cur,params);
                Info info = new Info();
                result.getResult().forEach((k)->{//键名称 k，一次扫描出约100个键
                    Map<String,String> data = jedis.hgetAll(k);
                    info.setCreateTime(LocalDateTime.parse(data.get("createTime"),formatter));
//                    info.setCreateTime(LocalDateTime.parse(data.get("createTime")));
                    info.setSignal(Float.parseFloat(data.get("signal")));
                    info.setMac(String.valueOf(data.get("mac")));
                    infoMapper.updateByMac(info);
                });
                sqlSession.commit();//提交
                sqlSession.clearCache();//防止溢出
                cur = result.getStringCursor();
                logger.debug("本次同步cur:{}",cur);
            }while (!(ScanParams.SCAN_POINTER_START.equals(cur)));

            logger.info(L_SUCCESS);
        }catch (Exception e){
            logger.error(L_ERROR,e);
        }
    }
}
