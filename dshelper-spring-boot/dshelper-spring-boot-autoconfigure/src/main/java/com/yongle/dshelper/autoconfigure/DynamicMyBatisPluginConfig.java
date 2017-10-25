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
 * @author weinh
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
