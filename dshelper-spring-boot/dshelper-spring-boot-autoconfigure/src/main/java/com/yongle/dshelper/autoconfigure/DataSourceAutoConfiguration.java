package com.yongle.dshelper.autoconfigure;

import com.yongle.dshelper.DynamicDataSource;
import com.yongle.dshelper.DynamicDataSourceTransactionManager;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 类 名 称：DataSourceAutoConfiguration.java
 * 功能说明：
 * 开发人员：weinh
 * 开发时间：2017年09月01日
 */
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@AutoConfigureBefore(MybatisAutoConfiguration.class)
public class DataSourceAutoConfiguration {
    @Resource
    private DynamicDataSourceProperties properties;

    public List<DataSource> readDataSources() {
        List<DataSource> dataSources = new ArrayList<>(properties.getRead().size());
        for (DataSourceProperties dataSourceProperties : properties.getRead()) {
            dataSourceProperties.setDriverClassName(properties.getDriverClassName());
            dataSourceProperties.setType(properties.getType());
            dataSources.add(dataSourceProperties.initializeDataSourceBuilder().build());
        }
        return dataSources;
    }

    public DataSource writeDataSource() {
        properties.getWrite().setDriverClassName(properties.getDriverClassName());
        properties.getWrite().setType(properties.getType());
        return properties.getWrite().initializeDataSourceBuilder().build();
    }

    @Bean(name = "dataSource")
    @Primary
    public DynamicDataSource getDynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setReadDataSources(readDataSources());
        dynamicDataSource.setWriteDataSource(writeDataSource());
        dynamicDataSource.setReadDataSourcePollPattern(properties.getReadDataSourcePollPattern());
        return dynamicDataSource;
    }

    @Bean(name = "dynamicDataSourceTransactionManager")
    public DynamicDataSourceTransactionManager getDynamicDataSourceTransactionManager() {
        DynamicDataSourceTransactionManager dynamicDataSourceTransactionManager = new DynamicDataSourceTransactionManager();
        dynamicDataSourceTransactionManager.setDataSource(getDynamicDataSource());
        return dynamicDataSourceTransactionManager;
    }
}
