# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
一个搞Java后端的如果连一个ORM的大致轮廓都没有的话是不应该的，在这之前小六六也是一样，都是按照前人的去写，并没有尝试说去看看底层是怎么样，以前的Mybatis对于我来说，就是一个黑洞，我完全不知道他是什么，所以呢？小六六觉得这样肯定不行嘛，至少得啃一个ORM框架，然后mybatis 又是用的比较多的，所以嘛就先学习学习，慢慢来这个系列

## 总览

![](https://user-gold-cdn.xitu.io/2020/6/3/1727a47b79f03e2d?w=704&h=788&f=png&s=284111)

借用大佬的一章图，从图中可以看出，我们平时的工作，其实就是到了dao这层，我们就算完成任务了，但是mybatis的底层实际上是做了很多事情的，并且下面还有jdbc 最后才是mysql，所以说我们要学的东西其实还有很多，我今天呢？并不打算说直接讲mybatis的源码，在分析mybatis源码前，我们首先先来回忆下jdk提供的sql操作步骤和mybatis的入门例子

## 准备数据表和数据

![](https://user-gold-cdn.xitu.io/2020/6/3/1727a57dae49c511?w=853&h=352&f=png&s=37159)


```
CREATE TABLE `user` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `age` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
```

### jdbc中数据库的操作

- 引入pom

```
   <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.15</version>
        </dependency>
    </dependencies>
```
- 测试类

```
public class JdbcTest {
    @Test
    public void test() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Properties pro = new Properties();
        pro.setProperty("user","root");
        pro.setProperty("password","123456");
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC",pro);
        //Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC","root","123456");
        //Statement statement = connection.createStatement();
        PreparedStatement statement = connection.prepareStatement("select * from user");
        statement.execute();
        ResultSet resultSet = statement.getResultSet();
        //ResultSet resultSet = statement.executeQuery("select * from user");
        while (resultSet.next()){
            System.out.println(resultSet.getString("name")+":"+resultSet.getInt("age"));
        }
        resultSet.close();
        statement.close();
        connection.close();
    }
}
```

![](https://user-gold-cdn.xitu.io/2020/6/3/1727a5f5a13e24b6?w=1046&h=467&f=png&s=84320)

以上是我们在jdk下操作数据库的简单操作流程，第一步加载mysql驱动，第二步创建mysql数据库连接，第三步创建Statement，第四步执行sql语句，第五步获取执行结果，第六步关闭所有。

![](https://user-gold-cdn.xitu.io/2020/6/3/1727a64a5a288228?w=1066&h=391&f=png&s=75905)
上面几个概念大家理解一下，mybatis中也是有用到的

## mybatis入门例子
- 引入pom:

```
 <dependencies>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.2</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.46</version>
        </dependency>
    </dependencies>
```
- 创建configuration.xml


```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
              <property name="driver" value="com.mysql.jdbc.Driver"/>
              <property name="url" value="jdbc:mysql://127.0.0.1:3306/test"/>
              <property name="username" value="root"/>
              <property name="password" value="123456"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="mybatis/UserMapper.xml"/>
    </mappers>
</configuration>
```

-  创建UserMapper.xml,将文件放在我们创建的mybatis目录

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.wangbin.mybatis.dao.UserDao">
    <select id="select" resultType="com.wangbin.mybatis.entity.User">
      select * from user
    </select>
</mapper>
```
这里的namespace路径对应的是我们UserDao的全名，也就是包名+类名

- 创建UserDao


```
public interface UserDao {
    List<User> select();
}
```
- 执行操作

```
   @Test
    public void selectUser() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("configuration.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDao mapper = sqlSession.getMapper(UserDao.class);
        List<User> users = mapper.select();
        System.out.println(users);
 
    }
```


![](https://user-gold-cdn.xitu.io/2020/6/3/1727a75b8637547d?w=1861&h=228&f=png&s=66179)


## 结论

上面代码的步骤比较多，但核心步骤只有两部，分别是执行 SQL 和处理查询结果。从
开发人员的角度来说，我们也只关心这两个步骤。如果每次为了执行某个 SQL 都要写很多
额外的代码。比如打开驱动，创建数据库连接，就显得很繁琐了。当然我们可以将这些额外
的步骤封装起来，这样每次调用封装好的方法即可。这样确实可以解决代码繁琐，冗余的问
题。不过，使用 JDBC 并非仅会导致代码繁琐，冗余的问题。在上面的代码中，我们通过字
符串对 SQL 进行拼接。这样做会导致两个问题，第一是拼接 SQL 可能会导致 SQL 出错，比
如少了个逗号或者多了个单引号等。第二是将 SQL 写在代码中，如果要改动 SQL，就需要
到代码中进行更改。这样做是不合适的，因为改动 Java 代码就需要重新编译 Java 文件，然
后再打包发布。同时，将 SQL 和 Java 代码混在一起，会降低代码的可读性，不利于维护。
关于拼接 SQL 问题，是有相应的处理方法。比如可以使用 PreparedStatement，在解决拼接
SQL 问题的同时，还可解决 SQL 注入的问题。
除了上面所说的一些问题，直接使用 JDBC 访问数据库还会导致什么问题呢？这次我们
将目光转移到执行结果的处理逻辑上。从上面的代码中可以看出，我们需要手动从 ResultSet
中取出数据，然后再设置到 User 对象中。好在我们的 User 属性不多，所以这样做看起
来也没什么。假如 User 对象有几十个属性，再用上面的方式接收查询结果，会非常的麻
烦。而且可能还会因为属性太多，导致忘记设置某些属性。以上的代码还有一个问题，用户
需要自行处理受检异常，这也是导致代码繁琐的一个原因。哦，还有一个问题，差点忘了。
用户还需要手动管理数据库连接，开始要手动获取数据库连接。使用好后，又要手动关闭数
据库连接。不得不说，真麻烦。
没想到直接使用 JDBC 访问数据库会有这么多的问题。但这并不意味着完全不可以在项
目中直接 JDBC，应视情况而定。如果项目非常小，且对数据库依赖比较低。直接使用 JDBC
也很方便，不需要像 MyBatis 那样搞一堆配置了。

## 结尾

今天就把最简单的东西列了出来，这些是基础，下张开始我们就开始真正的开启Mybatis的源码旅途了。

参考
- [一本小小的Mybatis源码书]()

![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！