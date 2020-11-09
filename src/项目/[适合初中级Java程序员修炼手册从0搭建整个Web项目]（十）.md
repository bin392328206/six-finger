# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206                          
> **种一棵树最好的时间是十年前，其次是现在**   
​
​
# six-finger-web
一个Web后端框架的轮子从处理Http请求【基于Netty的请求级Web服务器】 到mvc【接口封装转发)】，再到ioc【依赖注入】，aop【切面】，再到 rpc【远程过程调用】最后到orm【数据库操作】全部自己撸一个（简易）的轮子。

​[github](https://github.com/bin392328206/six-finger-web)

​https://github.com/bin392328206/six-finger-web

## 为啥要写这个轮子
其实是这样的，小六六自己平时呢？有时候喜欢看看人家的源码比如Spring,但是小六六的水平可能不怎么样，每次看都看得晕头转向，然后就感觉里面的细节太难了，然后我就只能观其总体的思想，然后我就想我如果可以根据各位前辈的一些思考，自己撸一个简单的轮子出来，那我后面去理解作者的思想是不是简单点呢？于是呢 six-finger-web就面世了，它其实就是我的一个学习过程，然后我把它开源出来，希望能帮助那些对于学习源码有困难的同学。还有就是可以锻炼一下自己的编码能力，因为平时我们总是crud用的Java api都是那些，久而久之，很多框架类的api我们根本就不熟练了，所以借此机会，锻炼一下。
​
## 特点
- 内置由 Netty 编写 HTTP 服务器，无需额外依赖 Tomcat 之类的 web 服务（刚好小六六把Netty系列写完，顺便用下）
- 代码简单易懂（小六六自己写不出框架大佬那种高类聚，低耦合的代码），能力稍微强一点看代码就能懂，弱点的也没关系，小六六有配套的从0搭建教程。
- 支持MVC相关的注解确保和SpringMVC的用法类似
- 支持Spring IOC 和Aop相关功能
- 支持类似于Mybatis相关功能
- 支持类似于Dubbo的rpc相关功能
- 对于数据返回，只支持Json格式

## 絮叨
前面是已经写好的章节，下面我给大家来一一走一遍搭建流程
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（一）](https://juejin.im/post/6883284588110544904)
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（二）](https://juejin.im/post/6884027512343494669)
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（三）](https://juejin.im/post/6885224199938867213)
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（四）](https://juejin.im/post/6885994038278193165)
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（五）](https://juejin.im/post/6888926292285063176)
- [[适合初中级Java程序员修炼手册从0搭建整个Web项目]（六）](https://juejin.im/post/6889627826451021831)
- [[适合初中级Java程序员修炼手册从0搭建整个Web项目]（七）](https://juejin.im/post/6890406233061195789)
- [[适合初中级Java程序员修炼手册从0搭建整个Web项目]（八）](https://juejin.im/post/6890406233061195789)
- [[适合初中级Java程序员修炼手册从0搭建整个Web项目]（九）](https://juejin.im/post/6891836391076921352)


这个系列已经是第十篇了，感谢每一个给我点赞的人，因为你们就向明灯，指引着我一起前进，让我知道，学习的路上并不孤独，还有很多你一样的人跟你在一起学习呢？
我们前面一起学习了Netty 做Http服务 MVC的实现  IOC  AOP的实现 还有我们RPC的实现，今天我们就把Web剩下的ORM实现了，今天先实现一个简单的ORM吧！

## 来看看实现之后的结构

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/883c8ecdd10e41b19477880a21baaeea~tplv-k3u1fbpfcp-watermark.image)

这个是Mybatis的实现

其实呢 我感觉基本上所有的web框架的组件 基本上分2步走 一个是我们初始化的步骤，还有一个就是我们流程的执行过程。就这2种

其实核心就几个
- SqlSession 围绕者它的 SqlSessionFactory 等 
- Configuration 这个类 里面有MapperRegistry 
- executor 执行器等

我们从头来看吧，首先我们看pom,我们依赖了哪些依赖

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/581796ee79e14cbbb06b586d5908a3c9~tplv-k3u1fbpfcp-watermark.image)

然后我们看测试类，看源码肯定是有主流程，不然太蒙b了

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/695a9d541fd447cd96bd994c99beb6cd~tplv-k3u1fbpfcp-watermark.image)

这块是测试类，然后小六六自己画个流程图，把这几个测试类的过程所涉及的给大家画一下。

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/959458a0f675494393c8bcffa846f7f4~tplv-k3u1fbpfcp-watermark.image)

这个是主流程，具体的细节，后面小六六根据核心测试代码一行一行给大家分析，当然我这个也是很简单的版本，所以就简单分析分析。


###  SqlSessionFactoryBuilder.build
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/37d11ab620cd4540bc941f48568d24f2~tplv-k3u1fbpfcp-watermark.image)

我们来看看这行代码
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7bd30f7ff37a490cbf28071f5852dd26~tplv-k3u1fbpfcp-watermark.image)

其实很简单，就是先读取我们的配置文件了，把我们的配置文件变成我们的对象。
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/dfef3ec6fafb45be9f60b5bf1eee6d4e~tplv-k3u1fbpfcp-watermark.image)

然后返回的是一个接口的实现，嗯，这个叫里氏替换吧。哈哈

然后我们看看实现类的构造方法
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d6119135daf64a4ab3923e91a9943456~tplv-k3u1fbpfcp-watermark.image)

它里面的loadMappersInfo,就是类似于我们现在mybatis的mapperScan，我相信大家应该都是很熟悉的拉，
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/73c16b22b5d3403a900ec778ac524975~tplv-k3u1fbpfcp-watermark.image)
就是遍历文件夹，然后看看文件夹下面的mapper,然后把mapper 变成一个mapper存起来，也把xml也解析了

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7ee2f0eeceee4f9382ed555477d09450~tplv-k3u1fbpfcp-watermark.image)

然后里面还定义了 我们接口的方法合xml的方法对应的联系就是sqlId ，所以我们在写代码的时候


###         SqlSession session = factory.openSession();


![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0af90fde8e31481fa0d2981429a293f2~tplv-k3u1fbpfcp-watermark.image)

接口的实现类是 DefaultSqlSessionFactory ，然后 new DefaultSqlSession

那么我们看看 DefaultSqlSession的构造。

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f986fae0cbaf4e5da9bd42d2f376a707~tplv-k3u1fbpfcp-watermark.image)

它new 了一个SimpleExecutor ，那么来看它的构造，然后发现它还有一个static 的方法，这个方法也是要跑的

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/bf1e604e75764065afdfb15024655b38~tplv-k3u1fbpfcp-watermark.image)

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ce90df476db14a3cb9a9e43eab5cc70b~tplv-k3u1fbpfcp-watermark.image)
发现其实就是做了数据库的连接


###         UserMapper userMapper = session.getMapper(UserMapper.class);

然后就是这行代码了，这行代码其实就是生成了我们接口mapper的代理对象，让代码对象去执行真正的sql流程。

我们来看 getMapper

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/cc4b970c0fd14c8dafa5fab0bfe1ff73~tplv-k3u1fbpfcp-watermark.image)
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ee6f5cd50cb4445d9f98ad6d89836225~tplv-k3u1fbpfcp-watermark.image)

先来解析一下这2个，因为我们前面就已经把Mapper 存到本地上下文环境了，那么现在就是去获取这个mapper的过程

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c858e0677d014091a1029ca2664385ac~tplv-k3u1fbpfcp-watermark.image)

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e306d31715f44b429aafbb8dd9966262~tplv-k3u1fbpfcp-watermark.image)

然后通过mapper 来生成我们的代理对象，这边用的是JDK动态代理
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9cfb977e92694c04864668b25c4042cf~tplv-k3u1fbpfcp-watermark.image)

###         User user = userMapper.getUser("1");

然后我们看执行过程，因为我们知道呀，它这个是生成的代理对象，所以呢？我们就要看它的invoke 方法咯

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6210390c1d75422bb1169e446190f889~tplv-k3u1fbpfcp-watermark.image)

然后大家看哈，最后执行的是            return  this.execute(method, args);
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9dcf5787fd024bebaa01988f517da6b6~tplv-k3u1fbpfcp-watermark.image)
很简单，根据接口的路径+上方法名称，找到对应的statementId，然后找到我们之前解析的xml,然后再把我们接口的参数替换掉，最好再执行sql,

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0303b374147a40d99bd9851bf9f973af~tplv-k3u1fbpfcp-watermark.image)

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4484a8dc30ba47c8b12db16ee084c129~tplv-k3u1fbpfcp-watermark.image)

这个就是sql执行的整体过程了，包含了jdbc的所以流程，其实orm框架就是要简化的就是这些流程，还有执行之后的数据封装等等，都在这个方法里面，大家有兴趣的把代码拿下来，慢慢看，我就不一一说了，整个orm的流程也就是这样的。

## 结尾
嗯，这个轮子的大致代码是完成了，但是很多细节小六六并没有去深挖，只是说自己把大部分的流程走通了，学习嘛，总是一直要学的，所以这个系列我暂时学到这，后面再看了，希望这个系列对各位有帮助就好了。感觉大家的支持。

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！





