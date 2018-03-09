package cn.mageek.NetServer.db;

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
public class MysqlClient {
    private static final Logger logger = LoggerFactory.getLogger(MysqlClient.class);
//    https://github.com/brettwooldridge/HikariCP
    private static HikariDataSource dataSource ;

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
            config.addDataSourceProperty("cachePrepStmts", properties.getProperty("jdbc.properties.cachePrepStmts"));
            config.addDataSourceProperty("prepStmtCacheSize", properties.getProperty("jdbc.properties.prepStmtCacheSize"));
            config.addDataSourceProperty("prepStmtCacheSqlLimit", properties.getProperty("jdbc.properties.prepStmtCacheSqlLimit"));
            dataSource = new HikariDataSource(config);
            logger.info("mysql DataSource initialized");
        }catch (Exception e){
            logger.error("mysql DataSource initialize error: {}",e);
            e.printStackTrace();
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
