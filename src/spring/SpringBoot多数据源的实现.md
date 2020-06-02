# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
小六六为啥写这篇文章呢？相当于一个记录吧，刚好我们有一个业务，就是因为做报表的，但是呢，如果是实时报表查询的话，请求时间不过就超时了，那么我们的一个解决方案就是，当他们需要统计的时候我们把需要的数据用定时任务入库，然很把这个数据库提供给他们去读写，但是呢，为了教务那边看不到所以的库，所以我们就需要在一个项目中用到2个数据库了，那么就得实现多数据源了，所以有了这篇文章的一个记录。

搞了小六六好久，吐槽一下原因，现在负责的这个系统是经过很多人迭代的一个系统，然后采用的是分层结构，就Controller,Service,Dao 是一个个模块，然后相互依赖，组成一个个服务。然后呢？他的每个服务的数据库的配置放在了 api 或者admin这样的工程里面，那么如果我要改的话，我就只能在要改的项目里面加多数据源，就比如我要在admin里面加 ，就只能在admin里面，并不能在api，那么现在就有一个问题，然后我们的多数据源的解决方案就大概2种，一种是分包，另外一种就是我们去用Spring的动态配置接口，如果我们采取第二种，那么我们就要在dao里面切面去做（每次到dao的走一下切面），然后呢我dao 怎么去用admin的东西，因为是controller 依赖的dao，总不能说我在dao依赖controller吧，那不是相互依赖了，所以还有一种方案就是重写一个模块去依赖dao，但是这样也是不行的，因为影响到了api（api有自己的数据库配置，dao也有的话会重复加载，api也是依赖了dao的），导致api的配置也会有变化，所以想来想去只能采用分包的方式去拓展了，写这么多，就是想表达一个架构的设计的拓展性真的很重要，如果一个项目经过5个人的手能做到他的味道不变，那么这批人就很厉害了。我一直觉得一个产品是有生命的，代码也是，如果说一个产品的代码最后写的变成了，无法拓展，那么这个项目就会越来越难迭代，好的架构设计还是非常重要的。哈哈，说了这么多，好像偏离了主线，接下来我们来看看Spring的多数据源的配置


## 代码整合



### 包结构

![](https://user-gold-cdn.xitu.io/2020/4/29/171c49de81cada3d?w=495&h=408&f=png&s=18462)

### 第一步：添加依赖



```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.wangbin</groupId>
    <artifactId>springboot-moresource</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    <description>SpringBoot配置多数据源</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.4.RELEASE</version>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <druid.version>1.1.2</druid.version>
        <mysql-connector.version>5.1.21</mysql-connector.version>
        <mybatis-plus.version>2.1.8</mybatis-plus.version>
        <mybatisplus-spring-boot-starter.version>1.0.5</mybatisplus-spring-boot-starter.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql-connector.version}</version>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>com.louislivi.fastdep</groupId>
            <artifactId>fastdep-datasource</artifactId>
            <version>1.0.3</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>${druid.version}</version>
        </dependency>
        <!-- MyBatis plus增强和springboot的集成-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatisplus-spring-boot-starter</artifactId>
            <version>${mybatisplus-spring-boot-starter.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.hamcrest</groupId>
            <artifactId>hamcrest-all</artifactId>
            <version>1.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.0.0</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.10</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.28</version>
            <scope>runtime</scope>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.6.1</version>
                <configuration>
                    <!--<proc>none</proc>-->
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.20</version>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <executions>
                </executions>
            </plugin>
        </plugins>

        <resources>
            <resource>
                <directory>src/main/resources</directory>
            </resource>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
        </resources>
    </build>

</project>
```

### 修改属性文件

```
spring.datasource.one.url=jdbc:mysql:/pos?serverTimezone=UTC&useSSL=false&autoReconnect=true&tinyInt1isBit=false&useUnicode=true&characterEncoding=utf8
spring.datasource.one.username=root
spring.datasource.one.password=admin@123
spring.datasource.one.type=com.alibaba.druid.pool.DruidDataSource

spring.datasource.two.url=jdbc:mysql:/biz?serverTimezone=UTC&useSSL=false&autoReconnect=true&tinyInt1isBit=false&useUnicode=true&characterEncoding=utf8
spring.datasource.two.username=root
spring.datasource.two.password=admin@123
spring.datasource.two.type=com.alibaba.druid.pool.DruidDataSource

```
配置文件就配多个数据源就好了

### 实体类

```
package com.wangbin.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 后台管理用户表
 *
 * @author 熊能
 * @version 1.0
 * @since 2018/01/02
 */
public class User extends Model<User> {

private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * 账号
     */
    private String username;
    /**
     * 名字
     */
    private String name;
    /**
     * 密码
     */
    private String password;
    /**
     * md5密码盐
     */
    private String salt;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 备注
     */
    private String tips;
    /**
     * 状态 1:正常 2:禁用
     */
    private Integer state;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 获取 主键ID.
     *
     * @return 主键ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置 主键ID.
     *
     * @param id 主键ID.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取 账号.
     *
     * @return 账号.
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置 账号.
     *
     * @param username 账号.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取 名字.
     *
     * @return 名字.
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 名字.
     *
     * @param name 名字.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取 密码.
     *
     * @return 密码.
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置 密码.
     *
     * @param password 密码.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取 md5密码盐.
     *
     * @return md5密码盐.
     */
    public String getSalt() {
        return salt;
    }

    /**
     * 设置 md5密码盐.
     *
     * @param salt md5密码盐.
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * 获取 联系电话.
     *
     * @return 联系电话.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置 联系电话.
     *
     * @param phone 联系电话.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取 备注.
     *
     * @return 备注.
     */
    public String getTips() {
        return tips;
    }

    /**
     * 设置 备注.
     *
     * @param tips 备注.
     */
    public void setTips(String tips) {
        this.tips = tips;
    }

    /**
     * 获取 状态 1:正常 2:禁用.
     *
     * @return 状态 1:正常 2:禁用.
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置 状态 1:正常 2:禁用.
     *
     * @param state 状态 1:正常 2:禁用.
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 获取 创建时间.
     *
     * @return 创建时间.
     */
    public Date getCreatedTime() {
        return createdTime;
    }

    /**
     * 设置 创建时间.
     *
     * @param createdTime 创建时间.
     */
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * 获取 更新时间.
     *
     * @return 更新时间.
     */
    public Date getUpdatedTime() {
        return updatedTime;
    }

    /**
     * 设置 更新时间.
     *
     * @param updatedTime 更新时间.
     */
    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}

```

### config
这个是重点，配置不同包下面的数据源

DataSourceConfig

```
package com.wangbin.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/24 21:34
 */
@Configuration
public class DataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.one")
    DataSource dsOne() {
        return DruidDataSourceBuilder.create().build();
    }
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.two")
    DataSource dsTwo() {
        return DruidDataSourceBuilder.create().build();
    }
}


```

MyBatisConfigOne

```
package com.wangbin.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/24 21:35
 */
@Configuration
@MapperScan(basePackages = "com.wangbin.mybatis.mapper1",sqlSessionFactoryRef = "sqlSessionFactory1",sqlSessionTemplateRef = "sqlSessionTemplate1")
public class MyBatisConfigOne {
    @Resource(name = "dsOne")
    DataSource dsOne;

    @Bean
    SqlSessionFactory sqlSessionFactory1() {
        SqlSessionFactory sessionFactory = null;
        try {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(dsOne);
            sessionFactory = bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionFactory;
    }
    @Bean
    SqlSessionTemplate sqlSessionTemplate1() {
        return new SqlSessionTemplate(sqlSessionFactory1());
    }
}


```

MyBatisConfigTwo

```
package com.wangbin.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/24 21:36
 */
@Configuration
@MapperScan(basePackages = "com.wangbin.mybatis.mapper2",sqlSessionFactoryRef = "sqlSessionFactory2",sqlSessionTemplateRef = "sqlSessionTemplate2")
public class MyBatisConfigTwo {
    @Resource(name = "dsTwo")
    DataSource dsTwo;

    @Bean
    SqlSessionFactory sqlSessionFactory2() {
        SqlSessionFactory sessionFactory = null;
        try {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(dsTwo);
            sessionFactory = bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionFactory;
    }
    @Bean
    SqlSessionTemplate sqlSessionTemplate2() {
        return new SqlSessionTemplate(sqlSessionFactory2());
    }
}


```

### Mapper
UserMapperOne

```
package com.wangbin.mybatis.mapper1;

import com.wangbin.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/24 21:37
 */
@Mapper
public interface UserMapperOne {

    List<User> getAllUser();
}

```
UserMapperOne.xml

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.wangbin.mybatis.mapper1.UserMapperOne">
    <select id="getAllUser" resultType="com.wangbin.entity.User">
        select * from t_user;
    </select>
</mapper>

```

UserMapper

```
package com.wangbin.mybatis.mapper2;

import com.wangbin.entity.User;

import java.util.List;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/24 21:41
 */
public interface UserMapper {
    List<User> getAllUser();
}

```

UserMapper.xml

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.wangbin.mybatis.mapper2.UserMapper">
    <select id="getAllUser" resultType="com.wangbin.entity.User">
        select * from t_user;
    </select>
</mapper>

```

### 测试类
SpringTest

```
import com.wangbin.Application;
import com.wangbin.entity.User;
import com.wangbin.mybatis.mapper1.UserMapperOne;
import com.wangbin.mybatis.mapper2.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/24 20:59
 */
@SpringBootTest(classes ={Application.class})
@RunWith(SpringRunner.class)
public class SpringTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserMapperOne userMapperOne;




    @Test
    public void Test1() {
        List<User> allUser = userMapper.getAllUser();
        for (User user : allUser) {
            System.out.println(user.getName());
        }
        List<User> allUser1 = userMapperOne.getAllUser();
        for (User user : allUser1) {
            System.out.println(user.getName());
        }


    }
}

```

### 结果

![](https://user-gold-cdn.xitu.io/2020/4/29/171c4a351e7aafca?w=1701&h=332&f=png&s=28509)


## 结尾
其实蛮简单的呢? 就是分包就能实现了，自己写这篇文章，算是做个笔记吧，如果对读者有所帮助也是极好的。

![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
