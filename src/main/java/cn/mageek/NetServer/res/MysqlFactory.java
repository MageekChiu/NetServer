package cn.mageek.NetServer.res;

import cn.mageek.NetServer.model.mapper.HistoryMapper;
import cn.mageek.NetServer.model.mapper.InfoMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
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
    private static SqlSessionFactory sqlSessionFactory;

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

                        TransactionFactory transactionFactory = new JdbcTransactionFactory();
                        Environment environment = new Environment("development", transactionFactory, dataSource);
                        Configuration configuration = new Configuration(environment);

                        configuration.addMapper(HistoryMapper.class);
                        configuration.addMapper(InfoMapper.class);

                        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
                        // 使用配置文件
//                        String resource = "conf.xml";//调取配置文件
//                        InputStream is = MysqlFactory.class.getClassLoader().getResourceAsStream(resource);
//                        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
                        logger.info("mysql sqlSessionFactory initialized");

                    }
                }
            }
        }catch (Exception e){
            logger.error("mysql DataSource initialize error: ",e);
        }
    }

    /**
     * 直接使用mysql语句
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
//        try {
            return dataSource.getConnection();
//        } catch (SQLException e) {
//            logger.error("mysql DataSource getConnection error: {}",e);
//            e.printStackTrace();
//            return null;
//        }
    }

    /**
     * 使用mybatis操作
     * @return
     */
    public static SqlSession getSession(ExecutorType type){
        if (type!=null)
            return sqlSessionFactory.openSession(type);
        return sqlSessionFactory.openSession();
    }

    public static void destruct(){
        dataSource.close();
        logger.info("mysql DataSource closed");
    }


}
