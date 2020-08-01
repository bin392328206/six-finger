# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
今天我们开始真正的开始读Mybatis的源码了，大家一起来围观吧！

- [MyBatis源码学习（一）](https://juejin.im/post/5ed79de2518825431845aa1c)

## Mybatis架构图

![](https://user-gold-cdn.xitu.io/2020/6/6/1728806fa0b80e99?w=709&h=390&f=png&s=281497)

说实话，小六六以前看到这个架构图的时候，感觉它就是一张图，但是我稍微看了一些源码之后，我再看这张架构图，我发现自己竟然能稍微看得懂了。虽然不一定说完全懂，但是我只能这个架构图每一个部分的意义是什么，小六六觉得学东西肯定是螺旋上升的，书读百遍，其意自现。


下面是mybatis的官网，因为小六六的英语不是那么好，所以我这边给的是中文版的，但是有能力的小伙伴我建议是看英文的，因为英文的是最准确的，当然前期可以用中文的过度一下的
- [官网](https://mybatis.org/mybatis-3/zh/index.html)


## 因为很多地方要读源码，所以下个源码
mybatis的源码编译真的超级简单，比Spring简单太多，并且很多大佬都说，如果我们要学习一个框架的源码，那么以mybatis 开始会是一个不错的选择。

![](https://user-gold-cdn.xitu.io/2020/6/6/172880edab7fe01c?w=503&h=952&f=png&s=66403)

大家可以看到mybatis的源码结构，非常容易看懂，就是一个model,然后分成各种包，每个包含不同的东西，我们学习它的源码就是学习这些包。


## 容器的加载与初始化

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

我们读源码肯定得有个主线，要是说一个包一个包去看，我的天，这个怎么看呀，所以小六六列了一主线跟着走来读它的源码
　　
　
> SqlSessionFactory是通过SqlSessionFactoryBuilder工厂类创建的，而不是直接使用构造器。容器的配置文件加载和初始化流程

### 配置⽂件解析过程分析


![](https://user-gold-cdn.xitu.io/2020/6/6/1728826679ae0c6d?w=418&h=508&f=png&s=42109)

 在源码的session包里面有这样的一个类，里面的build方法，我们来看看啊
 
 
```
  public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    try {
    // 创建配置文件解析器
      XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
      // 调用 parse 方法解析配置文件，生成 Configuration 对象
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        inputStream.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }
  
  public SqlSessionFactory build(Configuration config) {
// 创建 DefaultSqlSessionFactory
return new DefaultSqlSessionFactory(config);
}
```

里面比较重要的2步就是加载配置文件，去生成一个configuration对象。其实很简单，简单来说，就是我们自己写项目，也是要生成配置文件类的嘛


 
![](https://user-gold-cdn.xitu.io/2020/6/6/172882ae32836922?w=780&h=223&f=png&s=35005)
点进去是这个，
parseConfiguration(parser.evalNode("/configuration"));


![](https://user-gold-cdn.xitu.io/2020/6/6/172882cbcd48b91c?w=1326&h=693&f=png&s=149065)
其实就是去解析这个配置，把它变成一个对象，然后去生成SqlSessionFactory

```
private void parseConfiguration(XNode root) {
    try {
      // issue #117 read properties first
      propertiesElement(root.evalNode("properties"));
      Properties settings = settingsAsProperties(root.evalNode("settings"));
      loadCustomVfs(settings);
      loadCustomLogImpl(settings);
      typeAliasesElement(root.evalNode("typeAliases"));
      pluginElement(root.evalNode("plugins"));
      objectFactoryElement(root.evalNode("objectFactory"));
      objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
      reflectorFactoryElement(root.evalNode("reflectorFactory"));
      settingsElement(settings);
      // read it after objectFactory and objectWrapperFactory issue #631
      environmentsElement(root.evalNode("environments"));
      databaseIdProviderElement(root.evalNode("databaseIdProvider"));
      typeHandlerElement(root.evalNode("typeHandlers"));
      mapperElement(root.evalNode("mappers"));
    } catch (Exception e) {
      throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
    }
  }
```

这个呢？里面的每个方法就是解析不同的标签Node,这边小六六不一个个讲了，但是大家在心理得有一个完整的流程，它到底是怎么生成SqlSessionFactory的就可以了。具体里面的有想法的小伙伴可以自己去看，里面有以下的解析的节点
- 解析 properties点
- 解析settings节点
- 解析typeAliases节点
- 解析environments节点
- 解析typeHandlers节点


## 映射⽂件解析过程
说完了配置文件解析，接下来就来看看映射文件吧，因为他们的顺序是一致的

![](https://user-gold-cdn.xitu.io/2020/6/6/1728834ebb70569a?w=857&h=751&f=png&s=117438)

就是前面的解析配置文件里有一个mapper的配置，会找到映射文件，那么找到映射文件之后呢？我们就得去解析这个xml


映射文件 包 含 多 种 二 级 节 点 ， 比 如 <cache> ， <resultMap> ， <sql> 以 及
<select|insert|update|delete> 等。除此之外，还包含了一些三级节点，比如 <include>，<if>， <where> 等。



```
private void mapperElement(XNode parent) throws Exception {
    if (parent != null) {
      for (XNode child : parent.getChildren()) {
        if ("package".equals(child.getName())) {
          String mapperPackage = child.getStringAttribute("name");
          configuration.addMappers(mapperPackage);
        } else {
          String resource = child.getStringAttribute("resource");
          String url = child.getStringAttribute("url");
          String mapperClass = child.getStringAttribute("class");
          if (resource != null && url == null && mapperClass == null) {
            ErrorContext.instance().resource(resource);
            InputStream inputStream = Resources.getResourceAsStream(resource);
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            mapperParser.parse();
          } else if (resource == null && url != null && mapperClass == null) {
            ErrorContext.instance().resource(url);
            InputStream inputStream = Resources.getUrlAsStream(url);
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
            mapperParser.parse();
          } else if (resource == null && url == null && mapperClass != null) {
            Class<?> mapperInterface = Resources.classForName(mapperClass);
            configuration.addMapper(mapperInterface);
          } else {
            throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
          }
        }
      }
    }
  }
```
上面代码的主要逻辑是遍历 mappers 的子节点，并根据节点属性值判断通过何种方式加
载映射文件或映射信息。这里把配置在注解中的内容称为映射信息，以 XML 为载体的配置
称为映射文件。在 MyBatis 中，共有四种加载映射文件或映射信息的方式。第一种是从文件
系统中加载映射文件；第二种是通过 URL 的方式加载映射文件；第三种是通过 mapper 接口
加载映射信息，映射信息可以配置在注解中，也可以配置在映射文件中。最后一种是通过包
扫描的方式获取到某个包下的所有类，并使用第三种方式为每个类解析映射信息。


![](https://user-gold-cdn.xitu.io/2020/6/6/17288391733f62a6?w=765&h=258&f=png&s=44668)



```
// -☆- XMLMapperBuilder
public void parse() {
// 检测映射文件是否已经被解析过
if (!configuration.isResourceLoaded(resource)) {
// 解析 mapper 节点
configurationElement(parser.evalNode("/mapper"));
// 添加资源路径到“已解析资源集合”中
configuration.addLoadedResource(resource);
// 通过命名空间绑定 Mapper 接口
bindMapperForNamespace();
 }
// 处理未完成解析的节点
parsePendingResultMaps();
parsePendingCacheRefs();
parsePendingStatements();
}
```

映射文件解析入口逻辑包含三个核心操作，如下：
1. 解析 mapper 节点
2. 通过命名空间绑定 Mapper 接口
3. 处理未完成解析的节点


```
private void configurationElement(XNode context) {
try {
// 获取 mapper 命名空间
String namespace = context.getStringAttribute("namespace");
if (namespace == null || namespace.equals("")) {
throw new BuilderException("……");
 }
// 设置命名空间到 builderAssistant 中
builderAssistant.setCurrentNamespace(namespace);
// 解析 <cache-ref> 节点
cacheRefElement(context.evalNode("cache-ref"));
// 解析 <cache> 节点
cacheElement(context.evalNode("cache"));
// 已废弃配置，这里不做分析
parameterMapElement(context.evalNodes("/mapper/parameterMap"));
// 解析 <resultMap> 节点
resultMapElements(context.evalNodes("/mapper/resultMap"));
// 解析 <sql> 节点
sqlElement(context.evalNodes("/mapper/sql"));
// 解析 <select>、...、<delete> 等节点

buildStatementFromContext(
context.evalNodes("select|insert|update|delete"));
 } catch (Exception e) {
throw new BuilderException("……");
 } }
```

上面代码的执行流程清晰明了。在阅读源码时，我们按部就班的分析每个方法调用即可。其实就是一个节点去解析，就行了。这边我列几个要解析的节点
- 解析<cache>节点
 MyBatis 提供了一、二级缓存，其中一级缓存是 SqlSession 级别的，默认为开启状态。
二级缓存配置在映射文件中，使用者需要显示配置才能开启。
- 解析<cache-ref>节点
- 解析<resultMap>节点
resu濿tMa瀃 元素是 MyBatis 中最重要最强大的元素。它可以让你从 90% 的 JDBC Resu濿tSets
数据提取代码中解放出来,并在一些情形下允许你做一些 JDBC 不支持的事情。实际上，在
对复杂语句进行联合映射的时候，它很可能可以代替数千行的同等功能的代码。Resu濿tMa瀃
的设计思想是，简单的语句不需要明确的结果映射，而复杂一点的语句只需要描述它们的
关系就行
- 解析<sql>节点
- 解析 SQL 语句节点
这个里面又要解析无数的标签 什么动态sql标签啥的，我们知道整个流程就行了


## Mapper 接⼜绑定过程分析
映射文件解析完成后，并不意味着整个解析过程就结束了。此时还需要通过命名空间绑
定 mapper 接口，这样才能将映射文件中的 SQL 语句和 mapper 接口中的方法绑定在一起，
后续可直接通过调用 mapper 接口方法执行与之对应的 SQL 语句。下面我们来分析一下
mapper 接口的绑定过程。



```
private void bindMapperForNamespace() {
// 获取映射文件的命名空间
String namespace = builderAssistant.getCurrentNamespace();
if (namespace != null) {
Class<?> boundType = null;
try {
// 根据命名空间解析 mapper 类型
boundType = Resources.classForName(namespace);
 } catch (ClassNotFoundException e) {}
if (boundType != null) {
// 检测当前 mapper 类是否被绑定过
if (!configuration.hasMapper(boundType)) {
configuration.addLoadedResource("namespace:" + namespace);
// 绑定 mapper 类
configuration.addMapper(boundType);
 }
 }
 } }
// -☆- Configuration
public <T> void addMapper(Class<T> type) {
// 通过 MapperRegistry 绑定 mapper 类
mapperRegistry.addMapper(type);
}
// -☆- MapperRegistry
public <T> void addMapper(Class<T> type) {
if (type.isInterface()) {
if (hasMapper(type)) {
throw new BindingException("……");
 }

```


![](https://user-gold-cdn.xitu.io/2020/6/6/17288e67f4bff844?w=893&h=118&f=png&s=22510)

以上就是 Mapper 接口的绑定过程。这里简单总结一下：
1. 获取命名空间，并根据命名空间解析 mapper 类型
2. 将 type 和 MapperProxyFactory 实例存入 knownMappers 中
3. 解析注解中的信息
以上步骤中，第 3 步的逻辑较多。如果大家看懂了映射文件的解析过程，那么注解的解
析过程也就不难理解了，这里就不深入分析了。关于 Mapper 接口的绑定过程就先分析到这。


## 结尾

今天就把容器的初始化讲讲就先停了，最后我再总结一下吧，就是我们mybatis的核心在SqlSessionFactory，首先SqlSessionFactory build出来 这个过程就会涉及到解析各种配置文件，第一个要解析的就是configuration然后他的里面有很多的标签，你比如说properties等节点，然后里面有一个mapper节点，就是可以找到我们的mapper.xml 然后又去解析里面的节点，报告各种cach,select 等等，之后把解析好之后xml通过命名空间和我们的mapper接口绑定，并生成代码对象，把他放到konwsmapper 这个map容器里面。最后就可以生成这个SqlSessionFactory

参考
- [一本小小的Mybatis源码书]()

![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！