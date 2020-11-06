# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206                          
> **种一棵树最好的时间是十年前，其次是现在**   
​
​
# six-finger-web
一个Web后端框架的轮子从处理Http请求【基于Netty的请求级Web服务器】 到mvc【接口封装转发)】，再到ioc【依赖注入】，aop【切面】，再到 rpc【远程过程调用】最后到orm【数据库操作】全部自己撸一个（简易）的轮子。
​
[github](https://github.com/bin392328206/six-finger-web)
​
https://github.com/bin392328206/six-finger-web
​
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


昨天已经实现了一个rpc，但是呢我们还没把他们整合到一个容器里面，让我们来回顾一下dubbo的架构图
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/787f971cd765432f88674e9fbaa32727~tplv-k3u1fbpfcp-watermark.image)
让我们把Spring容器给它补上去把！

## 来看看总的结构

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/76a9806f411845f49b050479ee60575b~tplv-k3u1fbpfcp-watermark.image)


其实代码呢？全是人家开源的，我就是搬过来然后自己跑了一下流程，把整个流程走通，然后再给大家梳理梳理。

这个要怎么讲呢?其实也就是二大块
- 整合Spring
- 基于Netty的通信

其他的呢？都是这个的拓展吧？我会一一给大家讲下的，但是我也是讲大概的流程，因为代码不是我写的，然后里面的操作我自己也不是很懂，但是总的意思我还是可以讲清楚了。

###  基于Spring

首先我们想，我们用Spring能做什么，还不是可以代替我们之前的服务注册，服务代理这些，那么我们就基于注解来实现一下我们的服务端的服务注册。

首先我们来看看服务端的注册的实现类
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5a030390627a4074899caa0f7f7e0acc~tplv-k3u1fbpfcp-watermark.image)

其实就是多了一个注解，@RpcService。这个注解的作用是啥呢？

其实就是可以通过注解来切面来完成服务的注册，然后结合spring的拓展点就很容易就能做到了

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/df1cd3cd0924484fa245ea6e93eca627~tplv-k3u1fbpfcp-watermark.image)

在spring包下的三个拓展类就是做这样的事情的，其实呢？我大概总结一下干了啥，然后你们再根据这个去跟源码
- 自定义包扫描注解，我们知道在Spring中它是默认只扫描他们自己定义的注解变成Beandifinition的，但是给了我们拓展点
- 然后再bean的初始化过程中，通过前置通知做服务的注册，后置通知就是bean的属性注入嘛，@RpcReference就是下面这个注解了![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/bfb8b6df7507430e8c2add67bcdc510d~tplv-k3u1fbpfcp-watermark.image)

生成这个注解的代理对象，然后再注入到使用这个类的类中，这样的话，就能通过代理对象调用远程服务了

这个就是整合Spring的全部过程了，代码不多，但是真的都是核心，小六六也说的比较白话，不知道大家懂了没有。**就是我们服务端的服务的注册，和客户端代理对象的生成，放到spring的生命周期中。**


##  再一个就是基于Netty的通信方式，这个得好好讲讲东西还蛮多

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/955cea27fe8c4c81a682ce3f484873bd~tplv-k3u1fbpfcp-watermark.image)


然后我给大家捋捋顺序哈，我们知道rpc框架呢？肯定是有服务端 和客户端，对吧，然后上面我们提到得，我们已经在客户端生成了代理对象了是吧，那么我们接下来就是要调用服务端了，那么服务端首当其冲的当然是我们NettyClientTransport，


NettyClientTransport->sendRpcRequest
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/effc146a58344fd9bac61271794ebc4a~tplv-k3u1fbpfcp-watermark.image)

这个类就是首先去zk 拿到远程ip 然后去自己的客户端的Netty拿一个channel然后往服务端发送消息，然后我们知道Netty的pipeline的调用链机制是吧（不知道去补补Netyy） 然后它就会去到我们Encode 这个类了

RpcMessageDecoder->encode
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/002604dc37844d4599f4579bd6f93d27~tplv-k3u1fbpfcp-watermark.image)

然后在这里面会经过我们的系列化，我们的数据压缩，其实这就是为啥rpc比我们传统http请求快的一个原因吧，

然后这里面涉及到了2个组件，一个是序列化组件，一个是压缩组件。

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4955f7bbb8094fd48b3789e103f02529~tplv-k3u1fbpfcp-watermark.image)
具体里面我就不说了，大家自己去看吧

然后我们继续我们的流程，经过序列化，压缩之后，数据就要经过我们的网络模型了，最后到我们服务端接收了，

然后就到我们的服务端Netty的类了，也是先解压，反解码
RpcMessageDecoder![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/17cf777a59324116a292a708f3b08ede~tplv-k3u1fbpfcp-watermark.image)

之后再去![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8d1757f84125417f9449c151893a9452~tplv-k3u1fbpfcp-watermark.image)

处理完成然后就走了，这样整个过程就都清楚了


## 测试
- Server
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/59644722f8094c5696aae8537b656626~tplv-k3u1fbpfcp-watermark.image)
- Client
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/105c316b87234bccb53e52607945024d~tplv-k3u1fbpfcp-watermark.image)

## 结尾
RPC到这算是完了，但是呢？dubbo的源码有机会还是去看看吧，因为我自己公司的技术栈是Cloud，对于Dubbo其实并不熟悉，所以就只能去慢慢理解人家的东西，不过我感觉自己也学到了不少，如果大家一边跟源码，一边看我的博客，肯定没有问题，但是如果直接看可能也有些地方不清楚的，嗯。接下来就是ORM ,这样整个Web的轮子也算是完整了。


## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！

