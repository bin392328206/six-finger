# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206                          
> **种一棵树最好的时间是十年前，其次是现在**   


# six-finger-web
一个Web后端框架的轮子从处理Http请求【基于Netty的请求级Web服务器】 到mvc【接口封装转发)】，再到ioc【依赖注入】，aop【切面】，再到 rpc【远程过程调用】最后到orm【数据库操作】全部自己撸一个（简易）的轮子。

[github](https://github.com/bin392328206/six-finger-web)

https://github.com/bin392328206/six-finger-web

## 为啥要写这个轮子
其实是这样的，小六六自己平时呢？有时候喜欢看看人家的源码比如Spring,但是小六六的水平可能不怎么样，每次看都看得晕头转向，然后就感觉里面的细节太难了，然后我就只能观其总体的思想，然后我就想我如果可以根据各位前辈的一些思考，自己撸一个简单的轮子出来，那我后面去理解作者的思想是不是简单点呢？于是呢 six-finger-web就面世了，它其实就是我的一个学习过程，然后我把它开源出来，希望能帮助那些对于学习源码有困难的同学。还有就是可以锻炼一下自己的编码能力，因为平时我们总是crud用的Java api都是那些，久而久之，很多框架类的api我们根本就不熟练了，所以借此机会，锻炼一下。

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


不知不觉也写到了第八篇了，差不多也有一半了吧！基本上这些东西学完，小六六打算学点大数据技术栈的东西了（其实本来想学前端的，但是吧从工作开始就没怎么写过前端，感觉能力还在初级阶段，除非真的要我去做前端，不然感觉效率估计会低很多，所以就算了吧，后面看工作吧）

今天我们来真正的来看看RPC的具体实现，上一篇的SPI 是基础哈，大家一定要好好的理解一下。有了这个今天的RPC就不难了。还是用的 Java Guide 的rpc https://gitee.com/SnailClimb/guide-rpc-framework 基本上是它的代码，我就讲解一下。

**今天是第一个版本，就是不整合Spring的，简单的实现远程过程调用**


## 来看看包结构

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7e72b1cf5ad142d2875e08a321efb475~tplv-k3u1fbpfcp-watermark.image)

我们来看上图，其实最主要的是rpc-core 因为现在是基础版 所以就几个核心组件
- config 这个就是一些配置
- loadbalance  负载均衡的操作
- proxy 代理，就是我们客户端去调用服务端接口的时候需要生成的代理对象
- registry 这边用的zk 服务的注册于发现中心
- remoting  真正的远程调用的代码，这边基础版用的是基于Socket的方式，下一个版本改成基于Netty


## 具体代码讲解

因为代码比较多，我就截图部分来讲，具体的话文章还是个辅助，如果真心想看懂，建议的话把代码拉下来看，不难的。

###  rpc-api

写过dubbo的都知道，我们会用一个放api 接口 因为我们的客户端和服务端都是会依赖它的。

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4921c87dd57c46dba7830cfb05bedb82~tplv-k3u1fbpfcp-watermark.image)
也非常的简单，就是定义一个接口，大家都懂


### rpc-server

然后就是我们的服务提供方了，大家想想，服务的提供方要干嘛呢？先把我们要做的事情想通了，后面就很简单了。
- 第一个肯定要启动一个网络通信，那么既然是服务端的话，那么我们是不是需要启动一个Server 这个是肯定的
- 第二个 我们竟然是服务端，那么我们是不是得向注册中心去注册我们的地址呢？对的，我们要注册我们自己

好了，我们来看rpc-server启动类的代码
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/834d6e29a3de4801955df735aa8ec5e2~tplv-k3u1fbpfcp-watermark.image)

然后我跟大家来跟跟这2个方法的代码

####  注册服务

ServiceProviderImpl->publishService
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/629efefab25b4e8a8ba8bfc3aa4c295a~tplv-k3u1fbpfcp-watermark.image)


ServiceProviderImpl->addService 
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/47ac0cfe1f264eb79b034e0b19a76590~tplv-k3u1fbpfcp-watermark.image)

这个是用来找到具体实现的服务的



ZkServiceRegistry-> registerService

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b439664be86d436ba4b2d4d596566fd8~tplv-k3u1fbpfcp-watermark.image)

操作zk的话用的是CuratorFramework 这边小六六提一个点就是我们的zk的服务端和我们代码客户端的版本要一致，不然会各种报错，小六六自己搞了好久。哈哈。


####  再来看看 启动服务端的流程，这个就得懂点网络了，其实我们做业务的时候根本用不上网络，但是你只要写一些中间件基本上都要懂网络，所以网络的代码也是很重要的。

SocketRpcServer->start
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a796c4239a4648639c88084f8d520ab7~tplv-k3u1fbpfcp-watermark.image)

这个就是常规操作了，搞个线程池，然后去处理来的请求，具体我们来看里面的处理流程吧

SocketRpcRequestHandlerRunnable->run
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/434a5da84a3245b1a16b1d21763759b7~tplv-k3u1fbpfcp-watermark.image)

上面的代码也很简单吧

第一步就是拿到流，拿到流之后呢？把它转成请求对象，然后再去处理，再把结果返回给客户端，那么继续看具体的请求流程

RpcRequestHandler->handle

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/67e94efa8b374e6bb896fad4ab69374a~tplv-k3u1fbpfcp-watermark.image)
也很简单那，就2行代码，我们有了请求参数，通过请求参数我们再去找到我们服务端具体的接口的实现类，然后通过类和参数，通过方法名称和参数类型，拿到具体的方法，通过反射去执行方法，所以框架的话，反射用的是真的多。

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7ac5b366b8b445d3ab5c28931fd55286~tplv-k3u1fbpfcp-watermark.image)

到此呢？服务端的我们就分析完了，我们来看看客户端的吧


### rpc-client
客户端，我们来想想客户端要做些什么呢？
- 首先肯定是启动网络的客户端嘛
- 然后通过请求参数，生成一个代理对象，通过代理对象去调用远程服务的方法												
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d191e0466ff74af280e0a1e0bc6399ee~tplv-k3u1fbpfcp-watermark.image)

####  来看看SocketRpcClient

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d5be9925a5274125a4db73a829f02f26~tplv-k3u1fbpfcp-watermark.image)

首先 构造方法 就通过SPI生成了 我们服务发现，这个也是客户端需要做的

#### RpcClientProxy 

这个类是生成代理对象的，我们来看看它是怎么实现的
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0c59fc5d98bc4135ab99f05d7d335b3e~tplv-k3u1fbpfcp-watermark.image)

第一步，当然是JDK的动态代理了。然后我们最最最核心的来看看它的invoke 方法

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f9743e43176f4f76846e66bcd18489c9~tplv-k3u1fbpfcp-watermark.image)

前面是先构建请求对象，然后再去具体调用服务端的方法，最后拿到返回值，然后校验返回值。


SocketRpcClient-> sendRpcRequest

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a4de0c8a849a47f780e851d60c4136f3~tplv-k3u1fbpfcp-watermark.image)

这个就是也要去zk 拿到我们的ip+端口 然后再去做网络请求嘛。以上就是整个客户端的流程了，大致就是这样了


## 测试

让我们来测试测试

- api
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5a7dd496a0014f48bab5a55a1768d491~tplv-k3u1fbpfcp-watermark.image)

- server
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8d0e1b792b05463784ac933d66e585fd~tplv-k3u1fbpfcp-watermark.image)

然后我们先启动服务端
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5aa6e6f53e874a99b5bad3dc00325e44~tplv-k3u1fbpfcp-watermark.image)

然后再启动客户端

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/17c7d6fdcd9f4b1089e62920bcd83410~tplv-k3u1fbpfcp-watermark.image)

## 结尾
好了，因为代码太多了，所以我这边是截图，还是希望大家真的想了解的自己拉代码去学习，你拉代码跟着走一下，基本上rpc的整体流程你可以做到心中有数。


## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！

