package com.yongle.dshelper.autoconfigure;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.sql.DataSource;
import java.util.List;

/**
 * 类 名 称：DynamicDataSourceProperties.java
 * 功能说明：
 * 开发人员：weinh
 * 开发时间：2017年09月26日
 */
@ConfigurationProperties(prefix = DynamicDataSourceProperties.DS_HELPER_PREFIX)
public class DynamicDataSourceProperties {
    protected static final String DS_HELPER_PREFIX = "ds-helper";

    private Class<? extends DataSource> type;
    private String driverClassName;
    private List<DataSourceProperties> read;
    private DataSourceProperties write;
    private int readDataSourcePollPattern;//获取读数据源方式，0：随机，1：轮询

    public DataSourceProperties getWrite() {
        return write;
    }

    public void setWrite(DataSourceProperties write) {
        this.write = write;
    }

    public List<DataSourceProperties> getRead() {
        return read;
    }

    public void setRead(List<DataSourceProperties> read) {
        this.read = read;
    }

    public Class<? extends DataSource> getType() {
        return type;
    }

    public void setType(Class<? extends DataSource> type) {
        this.type = type;
    }

    public int getReadDataSourcePollPattern() {
        return readDataSourcePollPattern;
    }

    public void setReadDataSourcePollPattern(int readDataSourcePollPattern) {
        this.readDataSourcePollPattern = readDataSourcePollPattern;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
}
