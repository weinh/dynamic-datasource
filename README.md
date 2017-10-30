# 动态数据源，读写分离
## spring boot集成
maven依赖
```xml
<dependency>
  <groupId>com.yongle.dshelper</groupId>
  <artifactId>dshelper-spring-boot-starter</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```
spring boot配置文件(application.yml)
```yaml
ds-helper:
  read-data-source-poll-pattern: 1
  write:
    url: jdbc:mysql://ip:port/crm_test?useUnicode=true&characterEncoding=UTF-8&useSSL=false
    username: username
    password: password
    initial-size: 10
    max-active: 50
  read:
    - url: jdbc:mysql://ip:port/crm_dev?useUnicode=true&characterEncoding=UTF-8&useSSL=false
      username: username
      password: password
      initial-size: 10
      max-active: 50
    - url: jdbc:mysql://ip:port/crm_dev?useUnicode=true&characterEncoding=UTF-8&useSSL=false
      username: username
      password: password
      initial-size: 10
      max-active: 50
```
`read-data-source-poll-pattern`：读数据源轮询模式，1循环，其他随机

`write`：写数据源

`read`：读数据源，支持配置多个，可以一写多读

连接池的具体属性请参考`DruidDataSource`配置

**注：集成spring boot的方式必须使用Druid数据源**

## spring集成
maven依赖
```xml
<dependency>
    <groupId>com.yongle.dshelper</groupId>
    <artifactId>dshelper</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
spring 配置文件（applicationContext-dao.xml）
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/tx
       http://www.springframework.org/schema/tx/spring-tx.xsd
       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd">
    <!-- druid数据库连接池 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" destroy-method="close">
        <property name="dbType" value="mysql"/>
        <!-- 配置初始化大小、最小、最大 -->
        <property name="initialSize" value="1"/>
        <property name="minIdle" value="1"/>
        <property name="maxActive" value="20"/>
        <!-- 配置获取连接等待超时的时间 -->
        <property name="maxWait" value="60000"/>
        <!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
        <property name="timeBetweenEvictionRunsMillis" value="60000"/>
        <!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->
        <property name="minEvictableIdleTimeMillis" value="300000"/>
        <property name="validationQuery" value="SELECT 'x'"/>
        <property name="testWhileIdle" value="true"/>
        <property name="testOnBorrow" value="false"/>
        <property name="testOnReturn" value="false"/>
        <!-- 打开PSCache，并且指定每个连接上PSCache的大小 -->
        <property name="poolPreparedStatements" value="false"/>
        <property name="maxPoolPreparedStatementPerConnectionSize" value="20"/>
        <!-- 配置监控统计拦截的filters -->
        <property name="filters" value="stat"/>
    </bean>
    <!--读操作配置-->
    <bean id="readDataSource1" parent="dataSource" init-method="init">
        <!-- 数据库基本信息配置 -->
        <property name="url" value="${jdbc.read.url}"/>
        <property name="username" value="${jdbc.read.username}"/>
        <property name="password" value="${jdbc.read.password}"/>
    </bean>
    <bean id="readDataSource2" parent="dataSource" init-method="init">
        <!-- 数据库基本信息配置 -->
        <property name="url" value="${jdbc.read.url}"/>
        <property name="username" value="${jdbc.read.username}"/>
        <property name="password" value="${jdbc.read.password}"/>
    </bean>
    <!--写操作配置-->
    <bean id="writeDataSource" parent="dataSource" init-method="init">
        <!-- 数据库基本信息配置 -->
        <property name="url" value="${jdbc.write.url}"/>
        <property name="username" value="${jdbc.write.username}"/>
        <property name="password" value="${jdbc.write.password}"/>
    </bean>
    <!--动态数据源-->
    <bean id="dynamicDataSource" class="com.yongle.dshelper.DynamicDataSource">
        <property name="writeDataSource" ref="writeDataSource"/>
        <property name="readDataSources">
            <list>
                <ref bean="readDataSource1"/>
                <ref bean="readDataSource2"/>
            </list>
        </property>
        <!--轮询方式-->
        <property name="readDataSourcePollPattern" value="1"/>
    </bean>
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dynamicDataSource"/>
        <property name="plugins">
            <!--这个插件顺序有讲究，分页插件必须放前面，-->
            <!--调用的时候拦截器链将最后一个调用分页插件，才能不影响读写分离-->
            <array>
                <bean class="com.github.pagehelper.PageInterceptor">
                    <property name="properties">
                        <value>
                            helperDialect=mysql
                        </value>
                    </property>
                </bean>
                <bean class="com.yongle.dshelper.DynamicPlugin"/>
            </array>
        </property>
    </bean>
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
        <property name="basePackage" value="com.yongle.goku.*.mapper"/>
    </bean>
    <!-- 事物 -->
    <bean id="transactionManager"
          class="com.yongle.dshelper.DynamicDataSourceTransactionManager">
        <property name="dataSource" ref="dynamicDataSource"/>
    </bean>
    <!--开启事务注解-->
    <tx:annotation-driven/>
    <!--获取当前aop代理-->
    <aop:aspectj-autoproxy expose-proxy="true"/>
</beans>
```
`com.yongle.dshelper.DynamicDataSource`：定义动态数据源

`writeDataSource`：写数据源

`readDataSources`：读数据源

`readDataSourcePollPattern`：读数据源轮询方式：1循环，其他随机

**这里可以根据需要选择自己使用的连接池**