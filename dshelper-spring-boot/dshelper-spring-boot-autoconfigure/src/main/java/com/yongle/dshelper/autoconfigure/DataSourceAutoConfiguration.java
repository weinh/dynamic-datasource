package com.yongle.dshelper.autoconfigure;

import com.yongle.dshelper.DynamicDataSource;
import com.yongle.dshelper.DynamicDataSourceTransactionManager;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author weinh
 */
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@AutoConfigureBefore(MybatisAutoConfiguration.class)
public class DataSourceAutoConfiguration {
    @Resource
    private DynamicDataSourceProperties properties;

    public List<DataSource> readDataSources() {
        List<DataSource> dataSources = new ArrayList<>(properties.getRead().size());

        dataSources.addAll(properties.getRead());
        return dataSources;
    }

    public DataSource writeDataSource() {
        return properties.getWrite();
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
