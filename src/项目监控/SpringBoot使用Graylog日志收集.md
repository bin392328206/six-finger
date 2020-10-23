# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206                          
> **种一棵树最好的时间是十年前，其次是现在**   

## 絮叨

今天公司有个项目接入了GrayLog SO，小六六写个文章记录一下，方便自己以后查阅，我发现很多东西，我们用的时候是百度的，但是过一段时间就又忘记了，又重新百度一遍，所以呢？我还不如花点时间记录一下第一次百度的过程呢？这样后面可能会映像深刻点，要找的话，也会简单很多嘛！


## Graylog介绍
Graylog是一个生产级别的日志收集系统，集成Mongo和Elasticsearch进行日志收集。其中Mongo用于存储Graylog的元数据信息和配置信息，ElasticSearch用于存储数据。

架构图如下：

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/47fdc91888b1483fb3652c9e27fe0283~tplv-k3u1fbpfcp-watermark.image)

生产环境配置图如下：

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c78355a738c3443eaadedb651b6da7be~tplv-k3u1fbpfcp-watermark.image)

## 安装Graylog

在官方文档上推荐了很多种安装的方式，这里以docker-compose的方式为例，进行安装Graylog，mongo，elasticsearch。

docker-compose.yml内容如下（这里是在官网的基础上改了一下）：

```
version: '2'
services:
  # MongoDB: https://hub.docker.com/_/mongo/
  mongodb:
    image: mongo:3
  # Elasticsearch: https://www.elastic.co/guide/en/elasticsearch/reference/6.6/docker.html
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch-oss:6.6.1
    environment:
      - http.host=0.0.0.0
      - transport.host=localhost
      - network.host=0.0.0.0
      - "ES_JAVA_OPTS=-Xms256m -Xmx256m"
    ulimits:
      memlock:
        soft: -1
        hard: -1
    mem_limit: 512m
  # Graylog: https://hub.docker.com/r/graylog/graylog/
  graylog:
    image: graylog/graylog:3.0
    environment:
      # CHANGE ME (must be at least 16 characters)!
      - GRAYLOG_PASSWORD_SECRET=somepasswordpepper
      # Password: admin
      - GRAYLOG_ROOT_PASSWORD_SHA2=8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918
      - GRAYLOG_HTTP_EXTERNAL_URI=http://127.0.0.1:9000/
    links:
      - mongodb:mongo
      - elasticsearch
    depends_on:
      - mongodb
      - elasticsearch
    ports:
      # Graylog web interface and REST API
      - 9000:9000
      # Syslog TCP
      - 1514:1514
      # Syslog UDP
      - 1514:1514/udp
      # GELF TCP
      - 12201:12201
      # GELF UDP
      - 12201:12201/udp

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2882bc4dd9e3404dbccb828efadb703e~tplv-k3u1fbpfcp-watermark.image)![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/67a7c91d49be4a6da628a8c6133078c1~tplv-k3u1fbpfcp-watermark.image)

```
其他方式可以查看官方文档，https://docs.graylog.org/en/3.0/pages/installation.html



## 配置Graylog

在浏览器访问http://ip:9000，如图：
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5d824f7ea3584b9ebcb3871b04872263~tplv-k3u1fbpfcp-watermark.image)

这里默认用户名密码都是admin，进入后如图所示。

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4dbe2cf7e27046e48cdbac65021307f9~tplv-k3u1fbpfcp-watermark.image)

选择System按钮中的input，录入一个输入源，如图

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d28cbd8838f9450b815bbddabad93fe4~tplv-k3u1fbpfcp-watermark.image)

这里以GELF UDP为例，在图中位置选择GELF UDP，选择完成后点击Launch new input，如图

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2cc88f72c52f4b5eb9b88ec32e2ee8d0~tplv-k3u1fbpfcp-watermark.image)

然后区别stream，什么意思呢？根据不同的规则区分不同的渠道

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b022e3c781d54bf397959049966b0e9f~tplv-k3u1fbpfcp-watermark.image)


## SpringBoot日志输出到Graylog

### 首先我们来增加一下traceId

因为我们一个服务可能有多个副本，那么我们怎么才能把同一个线程的链路日志全部筛选呢？所以我们在日志里面加上了traceId

### 增加LogMdcFilter
```
package cn.xbz.common.filter;
 
import org.slf4j.MDC;
 
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
 
/**
 * @title 为logback日志增加traceId
 * @author Xingbz
 * @createDate 2019-4-12
 */
@WebFilter(urlPatterns = "/*", filterName = "logMdcFilter")
public class LogMdcFilter implements Filter {
    private static final String UNIQUE_ID = "traceId";
 
    @Override
    public void init(FilterConfig filterConfig) {
    }
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        boolean bInsertMDC = insertMDC();
        try {
            chain.doFilter(request, response);
        } finally {
            if(bInsertMDC) {
                MDC.remove(UNIQUE_ID);
            }
        }
    }
 
    @Override
    public void destroy() {
    }
 
    private boolean insertMDC() {
        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString().replace("-", "");
        MDC.put(UNIQUE_ID, uniqueId);
        return true;
    }
}
```

### 配置logback日志格式
```
...
    <pattern>%d{HH:mm:ss} [%thread][%X{traceId}] %-5level %logger{36} - %msg%n</pattern>
...
```

### 异步任务补充
完成前2步之后 , 从前端发起的请求就可以输出traceId了 , 但是一些未经过前端的定时或异步任务 , 是走不了过滤器的 . 所以我们还需要添加一个类

```
package cn.xbz.common.aspect;
 
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
 
/**
 * @title 为异步方法添加traceId
 * @author Xingbz
 * @createDate 2019-4-16
 */
@Aspect
@Component
public class LogMdcAspect {
    private static final String UNIQUE_ID = "traceId";
 
    @Pointcut("@annotation(org.springframework.scheduling.annotation.Async)")
    public void logPointCut() {
    }
 
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MDC.put(UNIQUE_ID, UUID.randomUUID().toString().replace("-",""));
        Object result = point.proceed();// 执行方法
        MDC.remove(UNIQUE_ID);
        return result;
    }
}
```

### 大功告成,根据id来筛选日志，方便
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c8f61ea40ba347f0a2d04cbfdf9c9a3e~tplv-k3u1fbpfcp-watermark.image)


###  还得看看我们lackback的配置文件

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <springProperty scope="context" name="springAppName" source="spring.application.name"/>

    <!-- values from application.yml -->
    <springProperty scope="context" name="graylogHost" source="logback.grayLog.host" />
    <springProperty scope="context" name="graylogPort" source="logback.grayLog.port" />
    <springProperty scope="context" name="originHost" source="logback.grayLog.originHost" />
    <springProperty scope="context" name="appName" source="logback.grayLog.appName" />
    <springProperty scope="context" name="appVersion" source="app.version" />


    <property name="log.path" value="logs"/>
    <property name="log.maxHistory" value="15"/>
    <!-- 活动文件的大小 -->
    <property name="maxFileSize" value="100MB"/>
     <!-- 控制所有归档日志文件的总大小 -->
     <property name="totalSizeCap" value="20GB"/>
    <property name="log.colorPattern"
              value="%magenta(%d{yyyy-MM-dd HH:mm:sss}) %highlight(%-5level) %boldCyan(${springAppName:-}) %yellow(%thread)  %red([%X{traceId}])  %green(%logger) %msg%n"/>
    <property name="log.pattern" value="%d{yyyy-MM-dd HH:mm:sss} %-5level ${springAppName:-} %thread  %red([%X{traceId}])  %logger %msg%n"/>

    <!--输出到控制台-->
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${log.colorPattern}</pattern>
        </encoder>
    </appender>

    <!--输出到日志平台 -->
    <appender name="GELF" class="de.siegmar.logbackgelf.GelfUdpAppender">
        <graylogHost>${graylogHost}</graylogHost>
        <graylogPort>${graylogPort}</graylogPort>
        <encoder class="de.siegmar.logbackgelf.GelfEncoder">
            <originHost>${originHost}</originHost>
            <includeRawMessage>false</includeRawMessage>
            <includeMarker>true</includeMarker>
            <includeMdcData>true</includeMdcData>
            <includeCallerData>false</includeCallerData>
            <includeRootCauseData>false</includeRootCauseData>
            <includeLevelName>true</includeLevelName>
            <shortPatternLayout class="ch.qos.logback.classic.PatternLayout">
                <pattern>%m%nopex</pattern>
            </shortPatternLayout>
            <fullPatternLayout class="ch.qos.logback.classic.PatternLayout">
                <pattern>%m%n</pattern>
            </fullPatternLayout>
            <staticField>app_name:${appName}</staticField>
            <staticField>app_version:${appVersion}</staticField>
            <staticField>os_arch:${os.arch}</staticField>
            <staticField>os_name:${os.name}</staticField>
            <staticField>os_version:${os.version}</staticField>
            <staticField>uri:%X{uri}</staticField>
            <staticField>uid:%X{uid}</staticField>
            <staticField>ip:%X{ip}</staticField>
            <staticField>traceId:%X{traceId}</staticField>
        </encoder>
    </appender>

    <!--输出到文件-->
    <appender name="file_info" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${log.path}/info/info.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <!-- 除按日志记录之外，还配置了日志文件不能超过100MB，若超过100MB，日志文件会以索引0开始，
            命名日志文件，例如log-adapterError-1992-11-06.0.log -->
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>${maxFileSize}</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <MaxHistory>${log.maxHistory}</MaxHistory>
            <totalSizeCap>${totalSizeCap}</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${log.colorPattern}</pattern>
        </encoder>
        <!-- <filter class="ch.qos.logback.classic.filter.LevelFilter">
             <level>INFO</level>
             <onMatch>ACCEPT</onMatch>
             <onMismatch>DENY</onMismatch>
         </filter>-->
    </appender>

    <appender name="file_error" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${log.path}/error/error.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <!-- 除按日志记录之外，还配置了日志文件不能超过100MB，若超过100MB，日志文件会以索引0开始，
           命名日志文件，例如log-adapterError-1992-11-06.0.log -->
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>${maxFileSize}</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <MaxHistory>${log.maxHistory}</MaxHistory>
            <totalSizeCap>${totalSizeCap}</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${log.colorPattern}</pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!--输出到 logstash的 appender-->
    <!--    <appender name="logstash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">-->
    <!--        <destination>${logstash.url}:4560</destination>-->
    <!--        <encoder charset="UTF-8" class="net.logstash.logback.encoder.LogstashEncoder"/>-->
    <!--    </appender>-->

    <root level="debug">
        <appender-ref ref="console"/>
    </root>

    <root level="info">
        <appender-ref ref="GELF" />
        <appender-ref ref="file_info"/>
        <appender-ref ref="file_error"/>
        <!--        <appender-ref ref="logstash" />-->
    </root>
</configuration>
```

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/44f824e1d514471d8addb53614f07cad~tplv-k3u1fbpfcp-watermark.image)

这个地方配置的是服务端的Graylog的地址，和自己本机的ip 采用udp发送日志，效率还行拉。


## 结尾
好了，就这么多了，其实就是记录一下简单的用法，为了以后使用的时候，好查询。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！