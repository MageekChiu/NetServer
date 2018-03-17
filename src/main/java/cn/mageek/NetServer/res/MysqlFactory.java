package cn.mageek.NetServer.res;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Mageek Chiu
 * @date 2018/3/9 0009:18:39
 */
public class MysqlFactory {
    private static final Logger logger = LoggerFactory.getLogger(MysqlFactory.class);
//    https://github.com/brettwooldridge/HikariCP
    private static volatile HikariDataSource dataSource ;

    public static void construct(Properties properties){
        try {
            HikariConfig config = new HikariConfig();
//            config.setDataSourceClassName(properties.getProperty("jdbc.dataSourceClassName"));
            config.setJdbcUrl(properties.getProperty("mysql.jdbc.url"));
            config.setUsername(properties.getProperty("mysql.username"));
            config.setPassword(properties.getProperty("mysql.password"));
            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("jdbc.properties.maximumPoolSize")));
            config.setMinimumIdle(Integer.parseInt(properties.getProperty("jdbc.properties.minimumIdle")));
            config.setPoolName(properties.getProperty("jdbc.properties.poolName"));
            config.setConnectionTimeout(Long.parseLong(properties.getProperty("jdbc.properties.timeout")));
            config.addDataSourceProperty("cachePrepStmts", properties.getProperty("jdbc.properties.cachePrepStmts"));
            config.addDataSourceProperty("prepStmtCacheSize", properties.getProperty("jdbc.properties.prepStmtCacheSize"));
            config.addDataSourceProperty("prepStmtCacheSqlLimit", properties.getProperty("jdbc.properties.prepStmtCacheSqlLimit"));
            config.addDataSourceProperty("retryAttempts", properties.getProperty("jdbc.properties.prepStmtCacheSqlLimit"));
            config.addDataSourceProperty("autoReconnect", properties.getProperty("jdbc.properties.autoReconnect"));
            if(dataSource==null) {//volatile+双重检查来实现单例模式
                synchronized (RedisFactory.class) {
                    if (dataSource == null) {
//                        Thread.sleep(5000);// 暂停一会，等mysql起来？？，怎么自动重连呢？用autoReconnect
                        dataSource = new HikariDataSource(config);
                        logger.info("mysql DataSource initialized");
                    }
                }
            }
        }catch (Exception e){
            logger.error("mysql DataSource initialize error: ",e);
        }
    }

    public static Connection getConnection() throws SQLException {
//        try {
            return dataSource.getConnection();
//        } catch (SQLException e) {
//            logger.error("mysql DataSource getConnection error: {}",e);
//            e.printStackTrace();
//            return null;
//        }
    }

    public static void destruct(){
        dataSource.close();
        logger.info("mysql DataSource closed");
    }


}
