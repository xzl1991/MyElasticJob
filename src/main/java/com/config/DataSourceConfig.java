package com.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by xlizy on 2017/6/23.
 */
@Configuration
@Order(1)
public class DataSourceConfig {
    @Value("${datasource.slave.username}")
    private String masterUserName;

    @Value("${datasource.slave.password}")
    private String masterPassword;

    @Value("${datasource.slave.url}")
    private String masterURL;
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    private DruidDataSource parentDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setInitialSize(30);
        druidDataSource.setMinIdle(30);
        druidDataSource.setMaxActive(100);
        druidDataSource.setMaxWait(6000L);
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(100);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setValidationQuery("SELECT 'X'");
        druidDataSource.setValidationQueryTimeout(60000);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000L);
        druidDataSource.setMinEvictableIdleTimeMillis(300000L);
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(180);
        druidDataSource.setLogAbandoned(true);
//        druidDataSource.setFilters("stat,wall");
        druidDataSource.setFilters("stat");
        Collection<String> init = new ArrayList();
        init.add("set names utf8mb4;");
        druidDataSource.setConnectionInitSqls(init);
        return druidDataSource;
    }
    /**
     * 允许批量操作
     **/
    @Bean(name = "WallFilter")
    public WallFilter wallFilter() throws SQLException {
        WallFilter wallFilter = new WallFilter();
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);
        wallFilter.setConfig(wallConfig);
        return wallFilter;
    }
    @Bean(name = "masterDataSource",initMethod = "init",destroyMethod = "close")
    public DruidDataSource masterDataSource() throws SQLException {
        DruidDataSource druidDataSource = parentDataSource();
        druidDataSource.setName("masterDataSource");
        druidDataSource.setUsername(masterUserName);
        druidDataSource.setPassword(masterPassword);
        druidDataSource.setUrl(masterURL);
        return druidDataSource;
    }

}
