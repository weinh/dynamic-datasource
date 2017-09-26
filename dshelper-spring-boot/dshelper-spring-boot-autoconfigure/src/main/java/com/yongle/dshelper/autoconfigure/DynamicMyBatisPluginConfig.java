package com.yongle.dshelper.autoconfigure;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.yongle.dshelper.DynamicPlugin;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * 类 名 称：DynamicMyBatisPluginConfig.java
 * 功能说明：
 * 开发人员：weinh
 * 开发时间：2017年09月26日
 */
@Configuration
@AutoConfigureAfter(PageHelperAutoConfiguration.class)
public class DynamicMyBatisPluginConfig {

    @Resource
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @PostConstruct
    public void addPageInterceptor() {
        DynamicPlugin interceptor = new DynamicPlugin();
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
        }
    }
}
