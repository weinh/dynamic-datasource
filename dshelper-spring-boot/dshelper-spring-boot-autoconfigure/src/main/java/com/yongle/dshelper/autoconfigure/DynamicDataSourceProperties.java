package com.yongle.dshelper.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author weinh
 */
@ConfigurationProperties(prefix = DynamicDataSourceProperties.DS_HELPER_PREFIX)
public class DynamicDataSourceProperties {
    protected static final String DS_HELPER_PREFIX = "ds-helper";

    private List<DruidDataSource> read;
    private DruidDataSource write;
    /**
     * 获取读数据源方式，0：随机，1：轮询
     */
    private int readDataSourcePollPattern;

    public DruidDataSource getWrite() {
        return write;
    }

    public void setWrite(DruidDataSource write) {
        this.write = write;
    }

    public List<DruidDataSource> getRead() {
        return read;
    }

    public void setRead(List<DruidDataSource> read) {
        this.read = read;
    }

    public int getReadDataSourcePollPattern() {
        return readDataSourcePollPattern;
    }

    public void setReadDataSourcePollPattern(int readDataSourcePollPattern) {
        this.readDataSourcePollPattern = readDataSourcePollPattern;
    }
}
