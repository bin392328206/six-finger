# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在** 
## Tips
面试指南系列，很多情况下不会去深挖细节，是小六六以被面试者的角色去回顾知识的一种方式，所以我默认大部分的东西，作为面试官的你，肯定是懂的。
> https://www.processon.com/view/link/600ed9e9637689349038b0e4

上面的是脑图地址
## 叨絮
Dubbo这个框架怎么说呢？我觉得至少有一半小伙伴的公司的分布式技术栈是它，所以今天小六六给大家一起来复习复习它。能问的其实还是很多的，哈哈。

然后下面是前面的文章汇总
 - [2021-Java后端工程师面试指南-(引言)](https://juejin.cn/post/6921868116481982477)
 - [2021-Java后端工程师面试指南-(Java基础篇）](https://juejin.cn/post/6924472756046135304)
 - [2021-Java后端工程师面试指南-(并发-多线程)](https://juejin.cn/post/6925213973205745672)
 - [2021-Java后端工程师面试指南-(JVM）](https://juejin.cn/post/6926349737394176014) 
 - [2021-Java后端工程师面试指南-(MySQL）](https://juejin.cn/post/6927105516485214216)
 - [2021-Java后端工程师面试指南-(Redis）](https://juejin.cn/post/6930816506489995272)
 - [2021-Java后端工程师面试指南-(Elasticsearch）](https://juejin.cn/post/6931558669239156743)
 - [2021-Java后端工程师面试指南-(消息队列）](https://juejin.cn/post/6932264624754524168)
 - [2021-Java后端工程师面试指南-(SSM）](https://juejin.cn/post/6933006207082823688)
 - [2021-Java后端工程师面试指南-(SpringBoot+SpringCloud）](https://juejin.cn/post/6933372510763548686)
 - [2021-Java后端工程师面试指南-(分布式理论+Zookeeper）](https://juejin.cn/post/6934623205823315976)
 - [2021-Java后端工程师面试指南-(计算机网络）](https://juejin.cn/post/6936345290085498911)
 - [2021-Java后端工程师必会知识点-(操作系统)](https://juejin.cn/post/6942686874301857800)
 
 ## 你用过dubbo嘛，知道他是干嘛的嘛？
 
**Apache Dubbo 是一款高性能、轻量级的开源 Java 服务框架**

提供了六大核心能力：面向接口代理的高性能RPC调用，智能容错和负载均衡，服务自动注册和发现，高度可扩展能力，运行期流量调度，可视化的服务治理与运维。
其实Dubbo呢就是一个RPC框架。

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5d24b73cca434712a5eed386c0a21106~tplv-k3u1fbpfcp-watermark.image)

## 那你说说什么是RPC

RPC（Remote Procedure Call）—远程过程调用，它是一种通过网络从远程计算机程序上请求服务，而不需要了解底层网络技术的协议。比如两个不同的服务 A、B 部署在两台不同的机器上，那么服务 A 如果想要调用服务 B 中的某个方法该怎么办呢？使用 HTTP请求当然可以，但是可能会比较麻烦。 RPC 的出现就是为了让你调用远程方法像调用本地方法一样简单。

其实就我而言RPC的范围还是挺广的，比如举几个简单生产的情况
- 首先我觉得很少读者公司用的fegin其实也算一个简单的RPC吧？因为它也是屏蔽了底层的调用，让我们调用远程方法像调用本地方一样的方便。
- 第二个就是我们说的dubbo，那他跟fegin的一个最大的区别，我觉得就是网络传输的方式不一样，但是他也是一个RPC框架，并且性能更好，但是他目前不支持跨语言调用。
- 第三个thrift,小六六为啥提到它呢?当一个公司比较大的时候，比如说一个大的互联网公司，肯定不同的部门用的语言不一样，那么你就得上他了，我们这边就有这样的服务。。

## 小伙子可以，那你说说RPC的原理吧


![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f97b68567b8d4136ac9fe786513e4429~tplv-k3u1fbpfcp-watermark.image)

- 服务消费方（client）调用以本地调用方式调用服务；
- client stub接收到调用后负责将方法、参数等组装成能够进行网络传输的消息体；
- client stub找到服务地址，并将消息发送到服务端；
- server stub收到消息后进行解码；
- server stub根据解码结果调用本地的服务；
- 本地服务执行并将结果返回给server stub；
- server stub将返回结果打包成消息并发送至消费方；
- client stub接收到消息，并进行解码；
- 服务消费方得到最终结果。

其实还是蛮简单的，你想清楚就知道是啥了，嘿嘿。大家可以理解记忆，一定要理解

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/87089b204f174004bc4960007c67947c~tplv-k3u1fbpfcp-watermark.image)


## 我们重新聊Dubbo,你对Dubbo的各个模块熟悉呢？知道每个模块的作用不

- config 配置层：对外配置接口，以 ServiceConfig, ReferenceConfig 为中心，可以直接初始化配置类，也可以通过 spring 解析配置生成配置类
- proxy 服务代理层：服务接口透明代理，生成服务的客户端 Stub 和服务器端 Skeleton, 以 ServiceProxy 为中心，扩展接口为 ProxyFactory
- registry 注册中心层：封装服务地址的注册与发现，以服务 URL 为中心，扩展接口为 RegistryFactory, Registry, RegistryService
- cluster 路由层：封装多个提供者的路由及负载均衡，并桥接注册中心，以 Invoker 为中心，扩展接口为 Cluster, Directory, Router, LoadBalance
- monitor 监控层：RPC 调用次数和调用时间监控，以 Statistics 为中心，扩展接口为 MonitorFactory, Monitor, MonitorService
- protocol 远程调用层：封装 RPC 调用，以 Invocation, Result 为中心，扩展接口为 Protocol, Invoker, Exporter
- exchange 信息交换层：封装请求响应模式，同步转异步，以 Request, Response 为中心，扩展接口为 Exchanger, ExchangeChannel, ExchangeClient, ExchangeServer
- transport 网络传输层：抽象 mina 和 netty 为统一接口，以 Message 为中心，扩展接口为 Channel, Transporter, Client, Server, Codec
- serialize 数据序列化层：可复用的一些工具，扩展接口为 Serialization, ObjectInput, ObjectOutput, ThreadPool

## 说说Dubbo的架构吧

Dubbo 的架构图解

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c32cb4ceaa6c4f7c820a0314200dd40e~tplv-k3u1fbpfcp-watermark.image)


- Provider： 暴露服务的服务提供方
- Consumer： 调用远程服务的服务消费方
- Registry： 服务注册与发现的注册中心（一般用的zk）
- Monitor： 统计服务的调用次数和调用时间的监控中心
- Container： 服务运行容器(Spring的容器)

> 调用关系说明：


- 服务容器负责启动，加载，运行服务提供者。
- 服务提供者在启动时，向注册中心注册自己提供的服务。
- 服务消费者在启动时，向注册中心订阅自己所需的服务。
- 注册中心返回服务提供者地址列表给消费者，如果有变更，注册中心将基于长连接推送变更数据给消费者。
- 服务消费者，从提供者地址列表中，基于软负载均衡算法，选一台提供者进行调用，如果调用失败，再选另一台调用。
- 服务消费者和提供者，在内存中累计调用次数和调用时间，定时每分钟发送一次统计数据到监控中心。


## 聊聊Dubbo的架构设计方法SPI
对于一个中间件的设计原则，插件化设计，任何的功能都可以设计。

- 协议扩展
- 调用拦截扩展
- 引用监听扩展
- 暴露监听扩展
- 集群扩展
- 路由扩展
- 负载均衡扩展
- 合并结果扩展
- 注册中心扩展
- 监控中心扩展
- 扩展点加载扩展
- 动态代理扩展
- 编译器扩展
- Dubbo 配置中心扩展
- 消息派发扩展
- 线程池扩展
- 序列化扩展
- 网络传输扩展
- 信息交换扩展
- 组网扩展
- Telnet 命令扩展
- 状态检查扩展
- 容器扩展
- 缓存扩展
- 验证扩展
- 日志适配扩展

其实小六六觉得这个才是一个代码的设计原则，但是其实很多时候，我们做业务也可以这样设计，高内聚，低耦合嘛。


## 小伙子不错，那你说说Dubbo的通信协议有哪些
dubbo 支持不同的通信协议

- dubbo 协议
默认就是走 dubbo 协议，单一长连接，进行的是 NIO 异步通信，基于 hessian 作为序列化协议。使用的场景是：传输数据量小（每次请求在 100kb 以内），但是并发量很高。

为了要支持高并发场景，一般是服务提供者就几台机器，但是服务消费者有上百台，可能每天调用量达到上亿次！此时用长连接是最合适的，就是跟每个服务消费者维持一个长连接就可以，可能总共就 100 个连接。基于我们的selector模式

- rmi 协议
走 Java 二进制序列化，多个短连接，适合消费者和提供者数量差不多的情况，适用于文件的传输，一般较少用。
- hessian 协议
走 hessian 序列化协议，多个短连接，适用于提供者数量比消费者数量还多的情况，适用于文件的传输，一般较少用。

## 那你说说它的序列化协议有哪些
dubbo 支持 hession、Java 二进制序列化、json、SOAP 文本序列化多种序列化协议，probuffer。但是 hessian 是其默认的序列化协议,其中probuffer是最快的。

## 为啥probuffer最快
- 它使用 proto 编译器，自动进行序列化和反序列化，速度非常快，应该比 XML 和 JSON 快上了 20~100 倍；
- 它的数据压缩效果好，就是说它序列化后的数据量体积小。因为体积小，传输起来带宽和速度上会有优化。


## 说说Dubbo 提供的负载均衡策略
- Random LoadBalance(默认，基于权重的随机负载均衡机制)->随机,按权重设置随机概率。在一个截面上碰撞的概率高，但调用量越大分布越均匀，而且按概率使用权重后也比较均匀，有利于动态调整提供者权重。
- RoundRobin LoadBalance->(不推荐，基于权重的轮询负载均衡机制)
-  LeastActive LoadBalance->最少活跃调用数，相同活跃数的随机，活跃数指调用前后计数差。使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。
- ConsistentHash LoadBalance->一致性 Hash，相同参数的请求总是发到同一提供者。(如果你需要的不是随机负载均衡，是要一类请求都到一个节点，那就走这个一致性hash策略。),当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。

## 自己设计一个RPC框架，需要考虑些什么
哈哈，其实这个也是考虑一个大局观，问的还是很大，之前小六六写个一个简单的rpc轮子，大家可以看看
,基本上就是把duboo包里面的每个模块自己简单的实现下。
一个Web后端框架的轮子从处理Http请求【基于Netty的请求级Web服务器】 到mvc【接口封装转发)】，再到ioc【依赖注入】，aop【切面】，再到 rpc【远程过程调用】最后到orm【数据库操作】全部自己撸一个（简易）的轮子。

> https://github.com/bin392328206/six-finger-web


## 结束
其实Dubbo的东西很多，但是要深入讲，每一个点都可以去跟源码，后面有机会小六六去跟跟。其实我觉得Dubbo的官方文档写的真好，大家不管用不用dubbo的，我建议大家去读读，对于我们开发可以有很多的启发点，有机会我给大家说说。

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。 

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>微信 搜 "六脉神剑的程序人生" 回复888 有我找的许多的资料送给大家 


