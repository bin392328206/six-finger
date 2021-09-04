# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**

## 絮叨
为啥想着写这个呢？是这样，小六六每次搭建系统的时候，都会涉及到这块的配置嘛，然后我发现我搭建了这么多次的系统，大部分的情况下，竟然是copy来完成的，然后这次搭建的过程中，又对这些配置又了点理解，所以打算给大家分享分享一些关键的点，让大家多Java 项目的日志有一些更加深入的理解吧!当然这边文章也得给大家清晰的理解logback的配置吧！尽量写的直白点！文章打算冲以下几个方面来描述
- 官网文档
- logback.xml常用配置详解
- 一个小案例加深对配置的理解
- logback.xml的参考配置(实战)
- 开发时，小六六自己的一些特殊的用法(实战）

## 参考的文档
> 开始就先给大家给贴下参考的文档吧！不过我觉得你应该不会去看，哈哈。我也是一样的，看不下去
[官方文档](<http://logback.qos.ch/documentation.html>)


## logback.xml常用配置详解
**常用节点结构图：**


![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0e2d495dd14342efbc3f3ec6c8908bee~tplv-k3u1fbpfcp-watermark.image)

相信大家对这个图应该不陌生，就是我们再spring-logback.xml里面要配置的文件就是这几个配置，下面我先来给大家讲讲细节

### 根节点 configuration
包含下面三个属性
- scan: 当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true。
- scanPeriod: 设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒。当scan为true时，此属性生效。默认的时间间隔为1分钟。
- debug: 当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。



```
<configuration scan="true" scanPeriod="60 seconds" debug="false"> 
　　  <!--其他配置省略--> 
</configuration>　
```

### **子节点** **appender**
它的作用是负责写日志的组件，它有两个必要属性name和class。name指定appender名称，class指定appender的全限定名。

下面小六六带大家来看看我们常见的几种appender吧！工作上肯定用的到的

#### **ConsoleAppender 把日志输出到控制台**

encoder：对日志进行格式化。
```
<appender name="console" class="ch.qos.logback.core.ConsoleAppender">
   <encoder>
      <pattern>${CONSOLE_LOG_PATTERN}</pattern>
   </encoder>
</appender>
```

#### **FileAppender：把日志添加到文件**
- file：被写入的文件名，可以是相对目录，也可以是绝对目录，如果上级目录不存在会自动创建，没有默认值。
- append：如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true。
- encoder：对记录事件进行格式化。
- prudent：如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false。


```
<configuration> 
　　　　　　<appender name="FILE" class="ch.qos.logback.core.FileAppender"> 
　　　　　　　　<file>testFile.log</file> 
　　　　　　　　<append>true</append> 
　　　　　　　　<encoder> 
　　　　　　　　　　<pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern> 
　　　　　　　　</encoder> 
　　　　　　</appender> 

　　　　　　<root level="DEBUG"> 
　　　　　　<appender-ref ref="FILE" /> 
　　　　　　</root> 
  </configuration>
```

#### **RollingFileAppender** **滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件** 这个小六六觉得是用的最多的一个了
- file：被写入的文件名，可以是相对目录，也可以是绝对目录，如果上级目录不存在会自动创建，没有默认值。
- append：如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true。
- rollingPolicy:当发生滚动时，决定RollingFileAppender的行为，涉及文件移动和重命名。属性class定义具体的滚动策略类

>  class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy"： 最常用的滚动策略，它根据时间来制定滚动策略，既负责滚动也负责出发滚动。


```
<appender name="BILogger_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
   <file>${log.path}/dataBi.dat</file>
   <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>${log.path}/dataBi.dat.%d{yyyyMMdd}.gz</fileNamePattern>
      <maxHistory>30</maxHistory>
   </rollingPolicy>
   <encoder>
      <charset>UTF-8</charset>
      <pattern>%date|%msg%n</pattern>
   </encoder>
</appender>
```

> class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy"： 查看当前活动文件的大小，如果超过指定大小会告知RollingFileAppender 触发当前活动文件滚动

```
<appender name="debug" class="ch.qos.logback.core.rolling.RollingFileAppender">
   <file>${log.path}/debug.log</file>
   <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
      <fileNamePattern>${log.path}/%d{yyyy-MM, aux}/debug.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
      <maxFileSize>50MB</maxFileSize>
      <maxHistory>30</maxHistory>
   </rollingPolicy>
   <encoder>
      <pattern>%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n</pattern>
   </encoder>
</appender>
```
### 子节点 logger

用来设置某一个包或具体的某一个类的日志打印级别、以及指定<appender>。

- logger仅有一个name属性，一个可选的level和一个可选的addtivity属性
- 可以包含零个或多个<appender-ref>元素，标识这个appender将会添加到这个logger。
- name: 用来指定受此loger约束的某一个包或者具体的某一个类。
- level: 用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL和OFF，还有一个特殊值INHERITED或者同义词NULL，代表强制执行上级的级别。 如果未设置此属性，那么当前loger将会继承上级的级别。
- addtivity: 是否向上级logger传递打印信息。默认是true。可以包含零个或多个appender-ref素，标识这个appender将会添加到这个logger。

```
<!--  logger name 和spring配置一致  -->
<logger name="BILogger" level="INFO" additivity="true">
   <appender-ref ref="BILogger_FILE" />
</logger>
```

像我们的bi日志，一般这种标准化，流程化的日志。

### 子节点root

它也是logger元素，但是它是根loger,是所有<loger>的上级。只有一个level属性，因为name已经被命名为"root",且已经是最上级了。
```
<root level="INFO">
   <appender-ref ref="console"/>
   <appender-ref ref="debug"/>
   <appender-ref ref="error"/>
</root>
```

好了，上面就是我们比较重要的几个元素了，还有一两个小元素的话，小六六这边就没给大家看了。

###  logback.xml配置示例


```
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">

    <!--定义日志文件的存储地址 勿在 LogBack 的配置中使用相对路径-->
    <property name="LOG_HOME" value="/home" />

    <!--控制台日志， 控制台输出 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度,%msg：日志消息，%n是换行符-->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>

    <!--文件日志， 按照每天生成日志文件 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--日志文件输出的文件名-->
            <FileNamePattern>${LOG_HOME}/TestWeb.log.%d{yyyy-MM-dd}.log</FileNamePattern>
            <!--日志文件保留天数-->
            <MaxHistory>30</MaxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
        <!--日志文件最大的大小-->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <MaxFileSize>10MB</MaxFileSize>
        </triggeringPolicy>
    </appender>

    <!-- show parameters for hibernate sql 专为 Hibernate 定制 -->
    <logger name="org.hibernate.type.descriptor.sql.BasicBinder" level="TRACE" />
    <logger name="org.hibernate.type.descriptor.sql.BasicExtractor" level="DEBUG" />
    <logger name="org.hibernate.SQL" level="DEBUG" />
    <logger name="org.hibernate.engine.QueryParameters" level="DEBUG" />
    <logger name="org.hibernate.engine.query.HQLQueryPlan" level="DEBUG" />

    <!--myibatis log configure-->
    <logger name="com.apache.ibatis" level="TRACE"/>
    <logger name="java.sql.Connection" level="DEBUG"/>
    <logger name="java.sql.Statement" level="DEBUG"/>
    <logger name="java.sql.PreparedStatement" level="DEBUG"/>

    <!-- 日志输出级别 -->
    <root level="DEBUG">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```


##  一个小案例加深对配置的理解

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">

    <!--定义日志文件的存储地址 勿在 LogBack 的配置中使用相对路径-->
    <property name="LOG_HOME" value="/home" />

    <!--控制台日志， 控制台输出 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度,%msg：日志消息，%n是换行符-->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>

    <!--文件日志， 按照每天生成日志文件 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--日志文件输出的文件名-->
            <FileNamePattern>${LOG_HOME}/TestWeb.log.%d{yyyy-MM-dd}.log</FileNamePattern>
            <!--日志文件保留天数-->
            <MaxHistory>30</MaxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
        <!--日志文件最大的大小-->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <MaxFileSize>10MB</MaxFileSize>
        </triggeringPolicy>
    </appender>

    <!-- show parameters for hibernate sql 专为 Hibernate 定制 -->
    <logger name="org.hibernate.type.descriptor.sql.BasicBinder" level="TRACE" />
    <logger name="org.hibernate.type.descriptor.sql.BasicExtractor" level="DEBUG" />
    <logger name="org.hibernate.SQL" level="DEBUG" />
    <logger name="org.hibernate.engine.QueryParameters" level="DEBUG" />
    <logger name="org.hibernate.engine.query.HQLQueryPlan" level="DEBUG" />

    <!--myibatis log configure-->
    <logger name="com.apache.ibatis" level="TRACE"/>
    <logger name="java.sql.Connection" level="DEBUG"/>
    <logger name="java.sql.Statement" level="DEBUG"/>
    <logger name="java.sql.PreparedStatement" level="DEBUG"/>

    <!-- 日志输出级别 -->
    <root level="DEBUG">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```


## 开发时，小六六自己的一些特殊的用法(实战）


- 第一个，就是在日志的时候加上traceId,这样的好处就是可以把日志收集到日志平台的时候，可以很好的查到整个流程


```
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
```

其中的原理是通过MDC实现的

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/df802d34b4d547b6aa5560184913641e~tplv-k3u1fbpfcp-watermark.image)


- 第二个就是，我们一些格式话的日志，做到数仓里面

```
<!-- BI操作日志-->
<appender name="BILogger_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
   <file>${log.path}/dataBi.dat</file>
   <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>${log.path}/dataBi.dat.%d{yyyyMMdd}.gz</fileNamePattern>
      <maxHistory>30</maxHistory>
   </rollingPolicy>
   <encoder>
      <charset>UTF-8</charset>
      <pattern>%date|%msg%n</pattern>
   </encoder>
</appender>
<!-- DI操作日志-->
<appender name="DILogger_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
   <file>${log.path}/dataDi.dat</file>
   <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>${log.path}/dataDi.dat.%d{yyyyMMdd}.gz</fileNamePattern>
      <maxHistory>30</maxHistory>
   </rollingPolicy>
   <encoder>
      <charset>UTF-8</charset>
      <pattern>%date|%msg%n</pattern>
   </encoder>
</appender>
```
类似于这种。。



## 结束
我是小六六，三天打鱼，两天晒网，今天就到这了哈！