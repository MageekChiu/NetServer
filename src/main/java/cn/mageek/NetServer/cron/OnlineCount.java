package cn.mageek.NetServer.cron;

import cn.mageek.NetServer.res.MysqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author Mageek Chiu
 * @date 2018/3/7 0007:19:44
 */
public class OnlineCount implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(OnlineCount.class);

    public void run(){
        String SQL_QUERY = "insert into device(mac) VALUES (?)";
        try(Connection con = MysqlFactory.getConnection()){
            PreparedStatement pst = con.prepareStatement(SQL_QUERY);
            pst.setString(1, "sasdsadsas");
            pst.executeUpdate();
            logger.info("{}执行成功",this.getClass().getName());
        }catch (Exception e){
            logger.error("{} 执行 error: {}",this.getClass().getName(),e.getMessage());
            e.printStackTrace();
        }
    }
}
