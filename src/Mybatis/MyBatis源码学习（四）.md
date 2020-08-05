# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
上面一节已经把一个查询语句的整体过程，大概的过了一遍，但是还有很多的细节这边没说清楚，今天就把当中的一些细节缕缕

- [MyBatis源码学习（一）](https://juejin.im/post/6854573210919239693)
- [MyBatis源码学习（二）](https://juejin.im/post/6854573214371643399)
- [MyBatis源码学习（三）](https://juejin.im/post/6854573221791383559)
## 获取 BoundSql
我们先把昨天的这个过程拿出来看看

![](https://user-gold-cdn.xitu.io/2020/6/7/1728d76df358a506?w=934&h=391&f=png&s=45622)

第一步就是 BoundSql 上一篇文章 这个我是直接跳过了，那大家猜猜这个是干嘛的呢？

在执行 SQL 之前，需要将 SQL 语句完整的解析出来。我们都知道 SQL 是配置在映射文
件中的，但由于映射文件中的 SQL 可能会包含占位符#{}，以及动态 SQL 标签，比如<if>、
<where>等。因此，我们并不能直接使用映射文件中配置的 SQL。MyBatis 会将映射文件中
的 SQL 解析成一组 SQL 片段。如果某个片段中也包含动态 SQL 相关的标签，那么，MyBatis
会对该片段再次进行分片。最终，一个 SQL 配置将会被解析成一个 SQL 片段树。形如下面
的图片：

![](https://user-gold-cdn.xitu.io/2020/6/7/1728d842a347f54e?w=739&h=657&f=png&s=153207)

我们需要对片段树进行解析，以便从每个片段对象中获取相应的内容。然后将这些内容
组合起来即可得到一个完成的 SQL 语句，这个完整的 SQL 以及其他的一些信息最终会存储
在 BoundSql 对象中。下面我们来看一下 BoundSql 类的成员变量信息，如下：

```
private final String sql;
private final List<ParameterMapping> parameterMappings;
private final Object parameterObject;
private final Map<String, Object> additionalParameters;
private final MetaObject metaParameters;
```

下面用一个表格列举各个成员变量的含义


![](https://user-gold-cdn.xitu.io/2020/6/7/1728d8a1e5d45d0a?w=927&h=430&f=png&s=104999)

以上对 BoundSql 的成员变量做了简要的说明，部分参数的用途大家现在可能不是很明
白。不过不用着急，这些变量在接下来的源码分析过程中会陆续的出现。到时候对着源码多
思考，或是写点测试代码调试一下，即可弄懂。

好了，现在准备工作已经做好。接下来，开始分析 BoundSql 的构建过程。我们源码之
旅的第一站是 MappedStatement 的 getBoundSql 方法，代码如下：


```
// -☆- MappedStatement
public BoundSql getBoundSql(Object parameterObject) {
// 调用 sqlSource 的 getBoundSql 获取 BoundSql
BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
List<ParameterMapping> parameterMappings =
boundSql.getParameterMappings();

if (parameterMappings == null || parameterMappings.isEmpty()) {
// 创建新的 BoundSql，这里的 parameterMap 是 ParameterMap 类型。
// 由<ParameterMap> 节点进行配置，该节点已经废弃，不推荐使用。
// 默认情况下，parameterMap.getParameterMappings() 返回空集合
boundSql = new BoundSql(configuration, boundSql.getSql(), 
parameterMap.getParameterMappings(), parameterObject);
 }
// 省略不重要的逻辑
return boundSql; }
```

如上，MappedStatement 的 getBoundSql 在内部调用了 SqlSource 实现类的 getBoundSql
方法。处理此处的调用，余下的逻辑都不是重要逻辑，就不啰嗦了。接下来，我们把目光转
移到 SqlSource 实现类的 getBoundSql 方法上。SqlSource 是一个接口，它有如下几个实现类：
- DynamicSqlSource
- RawSqlSource
- StaticSqlSource
- ProviderSqlSource
- VelocitySqlSource

在如上几个实现类中，我们应该选择分析哪个实现类的逻辑呢？首先我们把最后两个排
除掉，不常用。剩下的三个实现类中，仅前两个实现类会在映射文件解析的过程中被使用。
当 SQL 配置中包含${}（不是#{}）占位符，或者包含<if>、<where>等标签时，会被认为是
动态 SQL，此时使用 DynamicSqlSource 存储 SQL 片段。否则，使用 RawSqlSource 存储 SQL
配置信息。相比之下 DynamicSqlSource 存储的 SQL 片段类型较多，解析起来也更为复杂一
些。因此下面我将分析 DynamicSqlSource 的 getBoundSql 方法。弄懂这个，RawSqlSource 也
不在话下。

```
// -☆- DynamicSqlSource
public BoundSql getBoundSql(Object parameterObject) {
// 创建 DynamicContext
DynamicContext context =
new DynamicContext(configuration, parameterObject);
// 解析 SQL 片段，并将解析结果存储到 DynamicContext 中
rootSqlNode.apply(context);
SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
Class<?> parameterType = parameterObject == null ?
Object.class : parameterObject.getClass();
// 构建 StaticSqlSource，在此过程中将 sql 语句中的占位符 #{} 替换为问号 ?，
// 并为每个占位符构建相应的 ParameterMapping
SqlSource sqlSource = sqlSourceParser.parse(
context.getSql(), parameterType, context.getBindings());
// 调用 StaticSqlSource 的 getBoundSql 获取 BoundSql
BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
// 将 DynamicContext 的 ContextMap 中的内容拷贝到 BoundSql 中
for(Map.Entry<String, Object> entry : context.getBindings().entrySet()){
boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
 }
return boundSql; }
```

如上，DynamicSqlSource 的 getBoundSql 方法的代码看起来不多，但是逻辑却并不简单。
该方法由数个步骤组成，这里总结一下：
1. 创建 DynamicContext
2. 解析 SQL 片段，并将解析结果存储到 DynamicContext 中
3. 解析 SQL 语句，并构建 StaticSqlSource
4. 调用 StaticSqlSource 的 getBoundSql 获取 BoundSql
5. 将 DynamicContext 的 ContextMap 中的内容拷贝到 BoundSql 中


### DynamicContext
DynamicContext 是 SQL 语句构建的上下文，每个 SQL 片段解析完成后，都会将解析结
果存入 DynamicContext 中。待所有的 SQL 片段解析完毕后，一条完整的 SQL 语句就会出现
在 DynamicContext 对象中。下面我们来看一下 DynamicContext 类的定义。


```
public class DynamicContext {
public static final String PARAMETER_OBJECT_KEY = "_parameter";
public static final String DATABASE_ID_KEY = "_databaseId";
private final ContextMap bindings;
private final StringBuilder sqlBuilder = new StringBuilder();
public DynamicContext(
Configuration configuration, Object parameterObject) {
// 创建 ContextMap
if (parameterObject != null && !(parameterObject instanceof Map)) {
MetaObject metaObject =
configuration.newMetaObject(parameterObject);
bindings = new ContextMap(metaObject);
 } else {
bindings = new ContextMap(null);
 }
// 存放运行时参数 parameterObject 以及 databaseId
bindings.put(PARAMETER_OBJECT_KEY, parameterObject);
bindings.put(DATABASE_ID_KEY, configuration.getDatabaseId());
 } }
```
上面只贴了 DynamicContext 类的部分代码。其中 sqlBuilder 变量用于存放 SQL 片段的
解析结果，bindings 则用于存储一些额外的信息，比如运行时参数和 databaseId 等。bindings
类型为 ContextMap，

### 解析 SQL ⽚段

对于一个包含了${}占位符，或<if>、<where>等标签的 SQL，在解析的过程中，会被分解
成多个片段。每个片段都有对应的类型，每种类型的片段都有不同的解析逻辑。在源码中，
片段这个概念等价于 sql 节点，即 SqlNode。SqlNode 是一个接口，它有众多的实现类。其继
承体系如下：

![](https://user-gold-cdn.xitu.io/2020/6/7/1728d9e8d22acd17?w=923&h=215&f=png&s=91442)


上图只画出了部分的实现类，还有一小部分没画出来，不过这并不影响接下来的分析。
在众多实现类中，StaticTextSqlNode 用于存储静态文本，TextSqlNode 用于存储带有${}占位
符的文本，IfSqlNode 则用于存储<if>节点的内容。MixedSqlNode 内部维护了一个 SqlNode
集合，用于存储各种各样的 SqlNode。接下来，我将会对 MixedSqlNode、StaticTextSqlNode、
TextSqlNode、IfSqlNode、WhereSqlNode 以及 TrimSqlNode 等进行分析，其他的实现类请大
家自行分析。

```
public class MixedSqlNode implements SqlNode {
private final List<SqlNode> contents;
public MixedSqlNode(List<SqlNode> contents) {
this.contents = contents;
 }
 
 @Override
public boolean apply(DynamicContext context) {
// 遍历 SqlNode 集合
for (SqlNode sqlNode : contents) {
// 调用 salNode 对象本身的 apply 方法解析 sql
sqlNode.apply(context);
 }
return true;
 } }
```

MixedSqlNode 可以看做是 SqlNode 实现类对象的容器，凡是实现了 SqlNode 接口的类
都可以存储到 MixedSqlNode 中，包括它自己。MixedSqlNode 解析方法 apply 逻辑比较简单，
即遍历 SqlNode 集合，并调用其他 SalNode 实现类对象的 apply 方法解析 sql。那下面我们来
看看其他 SalNode 实现类的 apply 方法是怎样实现的。


```
public class StaticTextSqlNode implements SqlNode {
private final String text;
public StaticTextSqlNode(String text) {
this.text = text;
 }
@Override
public boolean apply(DynamicContext context) {
context.appendSql(text);
return true;
 } }
```

StaticTextSqlNode 用于存储静态文本，所以它不需要什么解析逻辑，直接将其存储的
SQL 片段添加到 DynamicContext 中即可。StaticTextSqlNode 的实现比较简单，看起来很轻
松。下面分析一下 TextSqlNode。

```
public class TextSqlNode implements SqlNode {
private final String text;
private final Pattern injectionFilter;
@Override
public boolean apply(DynamicContext context) {
// 创建 ${} 占位符解析器
GenericTokenParser parser = createParser(
new BindingTokenParser(context, injectionFilter));
// 解析 ${} 占位符，并将解析结果添加到 DynamicContext 中
context.appendSql(parser.parse(text));
return true;
 }
private GenericTokenParser createParser(TokenHandler handler) {
// 创建占位符解析器，GenericTokenParser 是一个通用解析器，
// 并非只能解析 ${} 占位符
return new GenericTokenParser("${", "}", handler);
 }
private static class BindingTokenParser implements TokenHandler {
private DynamicContext context;
private Pattern injectionFilter;
public BindingTokenParser(
DynamicContext context, Pattern injectionFilter) {
this.context = context;
this.injectionFilter = injectionFilter;
 }
@Override
public String handleToken(String content) {
Object parameter = context.getBindings().get("_parameter");
if (parameter == null) {
context.getBindings().put("value", null);
 }else if(SimpleTypeRegistry.isSimpleType(parameter.getClass())){
context.getBindings().put("value", parameter);
 }
// 通过 ONGL 从用户传入的参数中获取结果
Object value = OgnlCache
.getValue(content, context.getBindings());
String srtValue = (value == null ? "" : String.valueOf(value));
// 通过正则表达式检测 srtValue 有效性
checkInjection(srtValue);
return srtValue;
 }
 } }
```

如上，GenericTokenParser 是一个通用的标记解析器，用于解析形如${xxx}，#{xxx}等标
记 。GenericTokenParser 负责将标记中的内容抽取出来，并将标记内容交给相应的
TokenHandler 去处理。BindingTokenParser 负责解析标记内容，并将解析结果返回给
GenericTokenParser，用于替换${xxx}标记。举个例子说明一下吧，如下。
我们有这样一个 SQL 语句，用于从 article 表中查询某个作者所写的文章。如下：
SELECT * FROM article WHERE author = '${author}'
假设我们我们传入的 author 值为 小六六，那么该 SQL 最终会被解析成如下的结果：
SELECT * FROM article WHERE author = '小六六'

一般情况下，使用${author}接受参数都没什么问题。但是怕就怕在有人不怀好意，构建
了一些恶意的参数。当用这些恶意的参数替换${author}时就会出现灾难性问题——SQL 注

入。比如我们构建这样一个参数 author=tianxiaobo';DELETE FROM article;#，然后我们把这
个参数传给 TextSqlNode 进行解析。得到的结果如下

SELECT * FROM article WHERE author = '小六六'; DELETE FROM article;#'


看到没，由于传入的参数没有经过转义，最终导致了一条 SQL 被恶意参数拼接成了两
条 SQL。更要命的是，第二天 SQL 会把 article 表的数据清空，这个后果就很严重了（从删
库到跑路）。这就是为什么我们不应该在 SQL 语句中是用${}占位符，风险太大。
分析完 TextSqlNode 的逻辑，接下来，分析 IfSqlNode 的实现。


```
public class IfSqlNode implements SqlNode {
private final ExpressionEvaluator evaluator;
private final String test;
private final SqlNode contents;
public IfSqlNode(SqlNode contents, String test) {
this.test = test;
this.contents = contents;
this.evaluator = new ExpressionEvaluator();
 }
@Override
public boolean apply(DynamicContext context) {
// 通过 ONGL 评估 test 表达式的结果
if (evaluator.evaluateBoolean(test, context.getBindings())) {
// 若 test 表达式中的条件成立，则调用其他节点的 apply 方法进行解析
contents.apply(context);
return true;
 }
return false;
 } }
```

IfSqlNode 对应的是<iftest='xxx'>节点，<if>节点是日常开发中使用频次比较高的一个节
点。它的具体用法我想大家都很熟悉了，这里就不多啰嗦。IfSqlNode 的 apply 方法逻辑并不
复杂，首先是通过 ONGL 检测 test 表达式是否为 true，如果为 true，则调用其他节点的 apply
方法继续进行解析。需要注意的是<if>节点中也可嵌套其他的动态节点，并非只有纯文本。
因此 contents 变量遍历指向的是 MixedSqlNode，而非 StaticTextSqlNode。
关于 IfSqlNode 就说到这，接下来分析 WhereSqlNode 的实现


```
public class WhereSqlNode extends TrimSqlNode {
/** 前缀列表 */
private static List<String> prefixList = Arrays.asList(
"AND ", "OR ", "AND\n", "OR\n", "AND\r", "OR\r", "AND\t", "OR\t");
public WhereSqlNode(Configuration configuration, SqlNode contents) {
// 调用父类的构造方法
super(configuration, contents, "WHERE", prefixList, null, null);
 } }
```

 MyBatis 中，WhereSqlNode 和 SetSqlNode 都是基于 TrimSqlNode 实现的，所以上面
的代码看起来很简单。WhereSqlNode 对应于<where>节点，关于该节点的用法以及它的应用
场景，大家请自行查阅资料。我在分析源码的过程中，默认大家已经知道了该节点的用途和
应用场景。
接下来，我们把目光聚焦在 TrimSqlNode 的实现上


```
public class TrimSqlNode implements SqlNode {
private final SqlNode contents;
private final String prefix;
private final String suffix;
private final List<String> prefixesToOverride;
private final List<String> suffixesToOverride;
private final Configuration configuration;
@Override
public boolean apply(DynamicContext context) {
// 创建具有过滤功能的 DynamicContext
FilteredDynamicContext filteredDynamicContext =
new FilteredDynamicContext(context);
// 解析节点内容
boolean result = contents.apply(filteredDynamicContext);
// 过滤掉前缀和后缀
filteredDynamicContext.applyAll();
return result;
 } }
```

如上，apply 方法首选调用了其他 SqlNode 的 apply 方法解析节点内容，这步操作完成
后，FilteredDynamicContext 中会得到一条 SQL 片段字符串。接下里需要做的事情是过滤字
符串前缀后和后缀，并添加相应的前缀和后缀。这个事情由 FilteredDynamicContext 负责，
FilteredDynamicContext 是 TrimSqlNode 的私有内部类。我们去看一下它的代码


```
private class FilteredDynamicContext extends DynamicContext {
private DynamicContext delegate;
/** 构造方法会将下面两个布尔值置为 false */
private boolean prefixApplied;
private boolean suffixApplied;
private StringBuilder sqlBuffer;
public void applyAll() {
sqlBuffer = new StringBuilder(sqlBuffer.toString().trim());
String trimmedUppercaseSql =
sqlBuffer.toString().toUpperCase(Locale.ENGLISH);
if (trimmedUppercaseSql.length() > 0) {
// 引用前缀和后缀，也就是对 sql 进行过滤操作，移除掉前缀或后缀
applyPrefix(sqlBuffer, trimmedUppercaseSql);
applySuffix(sqlBuffer, trimmedUppercaseSql);
 }
// 将当前对象的 sqlBuffer 内容添加到代理类中
delegate.appendSql(sqlBuffer.toString());
}
private void applyPrefix(StringBuilder sql, String trimmedUppercaseSql){
if (!prefixApplied) {
// 设置 prefixApplied 为 true，以下逻辑仅会被执行一次
prefixApplied = true;
if (prefixesToOverride != null) {
for (String toRemove : prefixesToOverride) {
// 检测当前 sql 字符串是否包含前缀，比如 'AND ', 'AND\t'等
if (trimmedUppercaseSql.startsWith(toRemove)) {
// 移除前缀
sql.delete(0, toRemove.trim().length());
break;
 }
 }
 }
// 插入前缀，比如 WHERE
if (prefix != null) {
sql.insert(0, " ");
sql.insert(0, prefix);
 }
 }
 }
// 该方法逻辑与 applyPrefix 大同小异，大家自行分析
private void applySuffix(
StringBuilder sql, String trimmedUppercaseSql){
} }
```

在上面的代码中，我们重点关注 applyAll 和 applyPrefix 方法，其他的方法大家自行分
析。applyAll 方法的逻辑比较简单，首先从 sqlBuffer 中获取 SQL 字符串。然后调用 applyPrefix
和 applySuffix 进行过滤操作。最后将过滤后的 SQL 字符串添加到被装饰的类中。applyPrefix
方法会首先检测 SQL 字符串是不是以"AND"，"OR"，或"AND\n"，"OR\n"等前缀开头，若是
则将前缀从 sqlBuffer 中移除。然后将前缀插入到 sqlBuffer 的首部，整个逻辑就结束了。下
面写点代码简单验证一下，如下：


```
public class SqlNodeTest {
@Test
public void testWhereSqlNode() throws IOException {
String sqlFragment = "AND id = #{id}";
MixedSqlNode msn = new MixedSqlNode(
Arrays.asList(new StaticTextSqlNode(sqlFragment)));
WhereSqlNode wsn = new WhereSqlNode(new Configuration(), msn);
DynamicContext dc = new DynamicContext(
new Configuration(), new ParamMap<>());
wsn.apply(dc);
System.out.println("解析前：" + sqlFragment);
System.out.println("解析后：" + dc.getSql());
 } }
```

![](https://user-gold-cdn.xitu.io/2020/6/7/1728e64f5d870738?w=590&h=126&f=png&s=23641)


出自
- [一本小小的Mybatis源码书]()

![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！