# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 

- [MyBatis源码学习（一）](https://juejin.im/post/5ed79de2518825431845aa1c)
- [MyBatis源码学习（二）](https://juejin.im/post/5edb23196fb9a047b11b59e3)

经过前面复杂的解析过程后，现在，
MyBatis 已经进入了就绪状态，等待使用者发号施令，sql执行还是有下面的几个点
1. 为 mapper 接口生成实现类
2. 根据配置信息生成 SQL，并将运行时参数设置到 SQL 中
3. 一二级缓存的实现
4. 插件机制
5. 数据库连接的获取与管理
6. 查询结果的处理，以及延迟加载等

## SQL 执⾏流程
首先呢？我还是把前面最简单的流程代码来出来，我们的源码走读也是基于那个代码的


```
    public void selectUser() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("configuration.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDao mapper = sqlSession.getMapper(UserDao.class);
        List<User> users = mapper.select();
        System.out.println(users);
 
    }
```
其实前面已经把sqlSessionFactory准备好了，那么接下来呢？我们是不是要通过他拿到sqlsession.getMapper这些


### SQL 执⾏⼊⼜口
在单独使用 MyBatis 进行数据库操作时，我们通常都会先调用 SqlSession 接口的
getMapper方法为我们的Mapper接口生成实现类。然后就可以通过Mapper进行数据库操作。
比如像下面这样：

```
SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDao mapper = sqlSession.getMapper(UserDao.class);
```

如果大家对 MyBatis 较为了解，会知道 SqlSession 是通过 JDK 动态代理的方式为接口
生成代理对象的。在调用接口方法时，相关调用会被代理逻辑拦截。在代理逻辑中可根据方
法名及方法归属接口获取到当前方法对应的 SQL 以及其他一些信息，拿到这些信息即可进
行数据库操作

### 为 Mapper 接⼜创建代理对象
我们从 DefaultSqlSession 的 getMapper 方法开始看起，如下

```
// -☆- DefaultSqlSession
public <T> T getMapper(Class<T> type) {
return configuration.<T>getMapper(type, this);
}
// -☆- Configuration
public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
return mapperRegistry.getMapper(type, sqlSession);
}
// -☆- MapperRegistry
```


```
public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
// 从 knownMappers 中获取与 type 对应的 MapperProxyFactory
final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
if (mapperProxyFactory == null) {
throw new BindingException("……");
 }
try {
// 创建代理对象
return mapperProxyFactory.newInstance(sqlSession);
 } catch (Exception e) {
throw new BindingException("……");
 } }
```

经过连续的调用，Mapper 接口代理对象的创建逻辑初现端倪。如果大家没分析过
MyBatis配置文件的解析过程，那么可能不知道knownMappers集合中的元素是何时存入的，
这 里简 单说 明一 下。MyBatis 在解析配置文件的<mappers>节点的过程中，会调用
MapperRegistry 的 addMapper 方法将 Class 到 MapperProxyFactory 对象的映射关系存入到
knownMappers。具体的代码就不分析了，大家可以阅读我之前写的文章，或者自行分析相关
的代码。



在获取到 MapperProxyFactory 对象后，即可调用工厂方法为 Mapper 接口生成代理对象
了。相关逻辑如下


```
// -☆- MapperProxyFactory
public T newInstance(SqlSession sqlSession) {
// 创建 MapperProxy 对象，MapperProxy 实现了 InvocationHandler 接口，
// 代理逻辑封装在此类中
final MapperProxy<T> mapperProxy =
new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
return newInstance(mapperProxy);
}
```


```
protected T newInstance(MapperProxy<T> mapperProxy) {
// 通过 JDK 动态代理创建代理对象
return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), 
new Class[]{mapperInterface}, mapperProxy);
}
```

上面的代码首先创建了一个 MapperProxy 对象，该对象实现了 InvocationHandler 接口。
然后将对象作为参数传给重载方法，并在重载方法中调用 JDK 动态代理接口为 Mapper 生成
代理对象。代理对象已经创建完毕，下面就可以调用接口方法进行数据库操作了。由于接口
方法会被代理逻辑拦截，所以下面我们把目光聚焦在代理逻辑上面，看看代理逻辑会做哪些
事情。

## 执⾏代理逻辑
我们不是用了动态代理，那么我们就要实现那个如下图的方法

![](https://user-gold-cdn.xitu.io/2020/6/6/1728952f52d3d3fa?w=893&h=93&f=png&s=9430)

Mapper 接口方法的代理逻辑首先会对拦截的方法进行一些检测，以决定是否执行后续
的数据库操作。对应的代码如下




```
public Object invoke(Object proxy, 
Method method, Object[] args) throws Throwable {
try {
// 如果方法是定义在 Object 类中的，则直接调用
if (Object.class.equals(method.getDeclaringClass())) {
return method.invoke(this, args);
/*
* 下面的代码最早出现在 mybatis-3.4.2 版本中，用于支持 JDK 1.8 中的
* 新特性 - 默认方法。这段代码的逻辑就不分析了，有兴趣的同学可以
* 去 Github 上看一下相关的相关的讨论（issue #709），链接如下：
* 
* https://github.com/mybatis/mybatis-3/issues/709
*/
 } else if (isDefaultMethod(method)) {
protected T newInstance(MapperProxy<T> mapperProxy) {
// 通过 JDK 动态代理创建代理对象
return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), 
new Class[]{mapperInterface}, mapperProxy);
return invokeDefaultMethod(proxy, method, args);
 }
 } catch (Throwable t) {
throw ExceptionUtil.unwrapThrowable(t);
 }
// 从缓存中获取 MapperMethod 对象，若缓存未命中，则创建 MapperMethod 对象
final MapperMethod mapperMethod = cachedMapperMethod(method);
// 调用 execute 方法执行 SQL
return mapperMethod.execute(sqlSession, args);
}
```

如上，代理逻辑会首先检测被拦截的方法是不是定义在 Object 中的，比如 equals、
hashCode 方法等。对于这类方法，直接执行即可。除此之外，MyBatis 从 3.4.2 版本开始，
对 JDK1.8 接口的默认方法提供了支持，具体就不分析了。完成相关检测后，紧接着从缓存
中获取或者创建 MapperMethod 对象，然后通过该对象中的 execute 方法执行 SQL。在分析
execute 方法之前，我们先来看一下 MapperMethod 对象的创建过程。MapperMethod 的创建
过程看似普通，但却包含了一些重要的逻辑，所以不能忽视。

### 创建 MapperMethod 对象
来分析一下 MapperMethod 的构造方法，看看它的构造方法中都包含了哪些逻
辑。如下

```
public class MapperMethod {
private final SqlCommand command;
private final MethodSignature method;
public MapperMethod(Class<?> mapperInterface,
Method method, Configuration config){
    
// 创建 SqlCommand 对象，该对象包含一些和 SQL 相关的信息
this.command = new SqlCommand(config, mapperInterface, method);
// 创建 MethodSignature 对象，由类名可知，该对象包含了被拦截方法的一些信息
this.method = new MethodSignature(config, mapperInterface, method);
 } 
    
}
```

MapperMethod 构造方法的逻辑很简单，主要是创建 SqlCommand 和 MethodSignature 对
象。这两个对象分别记录了不同的信息，这2个过程 我这边就不讲了，到此，关于 MapperMethod 的初始化逻辑就分析完了

执⾏ execute ⽅法

![](https://user-gold-cdn.xitu.io/2020/6/6/17289a3f8fc6378e?w=743&h=802&f=png&s=107725)
如上，execute 方法主要由一个 switch 语句组成，用于根据 SQL 类型执行相应的数据库
操作。该方法的逻辑清晰，不需 要太多的分析。不过在上面 代 码 中
convertArgsToSqlCommandParam 方法出现次数比较频繁，这里分析一下：


```
// -☆- MapperMethod
public Object convertArgsToSqlCommandParam(Object[] args) {
return paramNameResolver.getNamedParams(args);
}
public Object getNamedParams(Object[] args) {
final int paramCount = names.size();
if (args == null || paramCount == 0) {
return null;
 } else if (!hasParamAnnotation && paramCount == 1) {
/*
* 如果方法参数列表无 @Param 注解，且仅有一个非特别参数，则返回该
* 参数的值。比如如下方法：
* List findList(RowBounds rb, String name)
* names 如下：
* names = {1 : "0"}
* 此种情况下，返回 args[names.firstKey()]，即 args[1] -> name
*/
return args[names.firstKey()];
 } else {
final Map<String, Object> param = new ParamMap<Object>();
int i = 0;
for (Map.Entry<Integer, String> entry : names.entrySet()) {
// 添加 <参数名, 参数值> 键值对到 param 中
param.put(entry.getValue(), args[entry.getKey()]);
// genericParamName = param + index。比如 param1, param2,... paramN
final String genericParamName =
GENERIC_NAME_PREFIX + String.valueOf(i + 1);
// 检测 names 中是否包含 genericParamName，什么情况下会包含？
// 答案如下：
// 使用者显式将参数名称配置为 param1，即 @Param("param1")
if (!names.containsValue(genericParamName)) {
// 添加 <param*, value> 到 param 中
param.put(genericParamName, args[entry.getKey()]);
 }i++;
 }
 return param;
 } }
```

convertArgsToSqlCommandParam 是一个空壳方法，该方法最终调用了
ParamNameResolver 的 getNamedParams 方法。getNamedParams 方法的主要逻辑是根据条件
返回不同的结果，该方法的代码不是很难理解


### 查询语句的执⾏过程
查询语句对应的方法比较多，有如下几种：
- executeWithResultHandler
- executeForMany
- executeForMap
- executeForCursor

这些方法在内部调用了 SqlSession 中的一些 select*方法，比如 selectList、selectMap、
selectCursor 等。这些方法的返回值类型是不同的，因此对于每种返回类型，需要有专门的处
理方法。以 selectList 方法为例，该方法的返回值类型为 List。但如果我们的 Mapper 或 Dao
的接口方法返回值类型为数组，或者 Set，直接将 List 类型的结果返回给 Mapper/Dao 就不合
适了。execute*等方法只是对 select*等方法做了一层简单的封装，因此接下来我们应们应该
把目光放在这些 select*方法上。

### selectOne ⽅法分析
本节选择分析 selectOne 方法，而不是其他的方法，大家或许会觉得奇怪。前面提及了
selectList、selectMap、selectCursor 等方法，这里却分析一个未提及的方法。这样做并没什么
特别之处，主要原因是 selectOne 在内部会调用 selectList 方法。这里分析 selectOne 方法是
为了告知大家，selectOne 和 selectList 方法是有联系的，同时分析 selectOne 方法等同于分析
selectList 方法。如果你不信的话，那我们看源码吧，源码面前了无秘密。


```
// -☆- DefaultSqlSession
public <T> T selectOne(String statement, Object parameter) {
// 调用 selectList 获取结果
List<T> list = this.<T>selectList(statement, parameter);
if (list.size() == 1) {
// 返回结果
return list.get(0);
 } else if (list.size() > 1) {
// 如果查询结果大于 1 则抛出异常，这个异常也是很常见的
throw new TooManyResultsException("……");
 } else {
return null;
 } }
```


![](https://user-gold-cdn.xitu.io/2020/6/7/1728d56b1225c207?w=1405&h=433&f=png&s=67286)

这个异常我不信大家没有碰到过，哈哈，只有读过源码才知道怎么去解决这些异常，怎么说呢 以前小六六碰到一个api不会用 我第一时间就是百度，碰到异常 也是，但是现在 对于我们经常用的框架 我会点进去看源码了，感觉慢慢的有点进步了，当然不熟悉的框框我还是百度，哈哈。

如上，selectOne 方法在内部调用 selectList 了方法，并取 selectList 返回值的第 1 个元素
作为自己的返回值。如果 selectList 返回的列表元素大于 1，则抛出异常。上面代码比较易懂，
就不多说了。下面我们来看看 selectList 方法的实现。


```
// -☆- DefaultSqlSession
public <E> List<E> selectList(String statement, Object parameter) {
// 调用重载方法
return this.selectList(statement, parameter, RowBounds.DEFAULT);
}

```


```
private final Executor executor;
public <E> List<E> selectList(String statement, Object parameter, RowBounds
rowBounds) {
try {
// 获取 MappedStatement
MappedStatement ms = configuration.getMappedStatement(statement);
// 调用 Executor 实现类中的 query 方法
return executor.query(ms, wrapCollection(parameter), 
rowBounds, Executor.NO_RESULT_HANDLER);
 } catch (Exception e) {
throw ExceptionFactory.wrapException("……");
 } finally {
ErrorContext.instance().reset();
 } }
```

如上，这里要来说说 executor 变量，该变量类型为 Executor。Executor 是一个接口，它
的实现类如下：

![](https://user-gold-cdn.xitu.io/2020/6/7/1728d5c993245618?w=1069&h=261&f=png&s=109663)

Executor 有这么多的实现类，大家猜一下 executor 变量对应哪个实现类。要弄清楚这个
问题，需要大家到源头去查证。这里提示一下，大家可以跟踪一下 DefaultSqlSessionFactory
的 openSession 方法，很快就能发现 executor 变量创建的踪迹。限于篇幅原因，本文就不分
析 openSession 方法的源码了。默认情况下，executor 的类型为 CachingExecutor，该类是一
个装饰器类，用于给目标 Executor 增加二级缓存功能。那目标 Executor 是谁呢？默认情况
下是 SimpleExecutor

![](https://user-gold-cdn.xitu.io/2020/6/7/1728d5dac35263d3?w=865&h=326&f=png&s=63243)


![](https://user-gold-cdn.xitu.io/2020/6/7/1728d5e37a869c27?w=803&h=466&f=png&s=64559)


现在大家搞清楚 executor 变量的身份了，接下来继续分析 selectOne 方法的调用栈。先
来看看 CachingExecutor 的 query 方法是怎样实现的。如下：

```
// -☆- CachingExecutor
public <E> List<E> query(MappedStatement ms, Object parameterObject, 
RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
// 获取 BoundSql
BoundSql boundSql = ms.getBoundSql(parameterObject);
// 创建 CacheKey
CacheKey key = createCacheKey(ms, parameterObject, rowBounds, boundSql);
// 调用重载方法
return query(ms, parameterObject, 
rowBounds, resultHandler, key, boundSql);
}
```


上面的代码用于获取 BoundSql 对象，创建 CacheKey 对象，然后再将这两个对象传给重
载方法。BoundSql 的获取过程较为复杂，我将在下一节进行分析。CacheKey 以及接下来即
将出现的一二级缓存将会独立成章分析。
上面的方法等代码和 SimpleExecutor 父类 BaseExecutor 中的实现没什么区别，有区别的
地方在于这个方法所调用的重载方法。继续往下看


```
// -☆- CachingExecutor
public <E> List<E> query(MappedStatement ms, Object parameterObject, 
RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, 
BoundSql boundSql) throws SQLException {
// 从 MappedStatement 中获取缓存
Cache cache = ms.getCache();
// 若映射文件中未配置缓存或参照缓存，此时 cache = null
if (cache != null) {
flushCacheIfRequired(ms);
if (ms.isUseCache() && resultHandler == null) {
ensureNoOutParams(ms, boundSql);
List<E> list = (List<E>) tcm.getObject(cache, key);
if (list == null) {
// 若缓存未命中，则调用被装饰类的 query 方法
list = delegate.<E>query(ms, parameterObject, 
rowBounds, resultHandler, key, boundSql);
tcm.putObject(cache, key, list); // issue #578 and #116
 }
return list;
 } }
// 调用被装饰类的 query 方法
return delegate.<E>query(
ms, parameterObject, rowBounds, resultHandler, key, boundSql);
}
```

以上代码涉及到了二级缓存，若二级缓存为空，或未命中，则调用被装饰类的 query 方
法。下面来看一下 BaseExecutor 的中签名相同的 query 方法是如何实现的。


```
// -☆- BaseExecutor
public <E> List<E> query(MappedStatement ms, Object parameter, 
RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, 
BoundSql boundSql) throws SQLException {
if (closed) {
throw new ExecutorException("Executor was closed.");
}
if (queryStack == 0 && ms.isFlushCacheRequired()) {
clearLocalCache();
 }
List<E> list;
try {
queryStack++;

// 从一级缓存中获取缓存项
list = resultHandler == null ? (List<E>) localCache.getObject(key) : null;
if (list != null) {
// 存储过程相关处理逻辑，本文不分析存储过程，故该方法不分析了
handleLocallyCachedOutputParameters(ms,key,parameter,boundSql);
 } else {
// 一级缓存未命中，则从数据库中查询
list = queryFromDatabase(ms, parameter, 
rowBounds, resultHandler, key, boundSql);
 }
 } finally {
queryStack--; }
if (queryStack == 0) {
// 从一级缓存中延迟加载嵌套查询结果
for (DeferredLoad deferredLoad : deferredLoads) {
deferredLoad.load();
 }
deferredLoads.clear();
if (configuration.getLocalCacheScope()==LocalCacheScope.STATEMENT) {
clearLocalCache();
 }
 }
return list; }
```

上面的方法主要用于从一级缓存中查找查询结果，若缓存未命中，再向数据库进行查询。
在上面的代码中，出现了一个新的类 DeferredLoad，这个类用于延迟加载。该类的实现并不
复杂，但是具体用途让我有点疑惑。这个我目前也未完全搞清楚，就不分析了。接下来，我
们来看一下 queryFromDatabase 方法的实现



```
// -☆- BaseExecutor
private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, 
RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, 
BoundSql boundSql) throws SQLException {
List<E> list;
// 向缓存中存储一个占位符
localCache.putObject(key, EXECUTION_PLACEHOLDER);
try {
// 调用 doQuery 进行查询
list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
 } finally {
// 移除占位符
localCache.removeObject(key);
 }
// 缓存查询结果
localCache.putObject(key, list);
if (ms.getStatementType() == StatementType.CALLABLE) {
localOutputParameterCache.putObject(key, parameter);
 }
return list; }
```
上面的代码仍然不是 selectOne 方法调用栈的终点，抛开缓存操作，queryFromDatabase
最终还会调用 doQuery 进行查询。所以下面我们继续进行跟踪。


```
// -☆- SimpleExecutor
public <E> List<E> doQuery(MappedStatement ms, Object parameter, 
RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) 
throws SQLException {
Statement stmt = null;
try {
Configuration configuration = ms.getConfiguration();
// 创建 StatementHandler
StatementHandler handler = configuration.newStatementHandler(
wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
// 创建 Statement
stmt = prepareStatement(handler, ms.getStatementLog());
// 执行查询操作
return handler.<E>query(stmt, resultHandler);
 } finally {
// 关闭 Statement
closeStatement(stmt);
 } }
```


doQuery 方法中仍然有不少的逻辑，完全看不到即将要到达终点的趋势，不过这离终点
又近了一步。接下来，我们先跳过 StatementHandler 和 Statement 创建过程，这两个对象的创
建过程会在后面进行说明。这里，我们以 PreparedStatementHandler 为例，看看它的 query 方
法是怎样实现的。如下：


```
// -☆- PreparedStatementHandler
public <E> List<E> query(Statement statement, ResultHandler resultHandler) 
throws SQLException {
PreparedStatement ps = (PreparedStatement) statement;
// 执行 SQL
ps.execute();
// 处理执行结果
return resultSetHandler.<E>handleResultSets(ps);
}
```

到这里似乎看到了希望，整个调用过程总算要结束了。不过先别高兴的太早，SQL 执行
结果的处理过程也很复杂，稍后将会专门拿出一节内容进行分析。
以上就是 selectOne 方法的执行过程，尽管我已经简化了代码分析，但是整个过程看起来还是很复杂的。查询过程涉及到了很多方法调用，不把这些调用方法搞清楚，很难对
MyBatis 的查询过程有深入的理解。



## 结尾

其实我也只是跟着大佬的书在读，很多东西也是一知半解，哈哈 但是今天我们主要讲的是一个selectOne的执行过程，这边我们来总结一下，首先就是DefaultSqlSession调用selectOne，然后其实他这个方法调用的是selectList 然后通过list.size() > 1来看是否需要抛出异常，然后selectList里面其实是调用了executor.query方法，然后就得说一下Executor是怎么来的了，他是在调用openSession的时候通过config.newExecutor 生成的 默认情况下，executor 的类型为 CachingExecutor，那么接下来就是来看CachingExecutor.query 里面有几个步骤这边就不一一分析了（涉及一二级缓存），然后他接下来调用的是 query 然后就是调用BaseExecutor queryFromDatabase 这是去调用数据库的方法 然后是doQuery  往下就是类似于 JDBC的操作了以上就是一个查询的整个过程。

出自
- [一本小小的Mybatis源码书]()

![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！