# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨  
对于消息中间件是我们后台开发人员必备的一项技能，小六六整体来说 了解和使用的有2款吧，一个rabbitmq和一个rocketmq,以前呢，只能说大致的用了用，大致的了解了, 但是小六六觉得 消息中间件 还是有必要深入学习一下的，所以呢就打算写嗯，为啥选rocketmq，因为他是Java写的，所以看起源码来就简单点，所以就学习学习。当然他还有很多的优点，后面慢慢的来学习吧





## MQ介绍
###  为什么要用MQ

消息队列是一种“先进先出”的数据结构

![](https://user-gold-cdn.xitu.io/2020/6/24/172e452786939fcf?w=806&h=182&f=png&s=42262)

其应用场景主要包含以下3个方面
- 应用解耦

系统的耦合性越高，容错性就越低。以电商应用为例，用户创建订单后，如果耦合调用库存系统、物流系统、支付系统，任何一个子系统出了故障或者因为升级等原因暂时不可用，都会造成下单操作异常，影响用户使用体验。
![](https://user-gold-cdn.xitu.io/2020/6/24/172e4532f1c44789?w=819&h=274&f=png&s=12687)
使用消息队列解耦合，系统的耦合性就会提高了。比如物流系统发生故障，需要几分钟才能来修复，在这段时间内，物流系统要处理的数据被缓存到消息队列中，用户的下单操作正常完成。当物流系统回复后，补充处理存在消息队列中的订单消息即可，终端系统感知不到物流系统发生过几分钟故障。

![](https://user-gold-cdn.xitu.io/2020/6/24/172e453e5f3bbcba?w=829&h=287&f=png&s=13761)
- 流量削峰

![](https://user-gold-cdn.xitu.io/2020/6/24/172e454266185375?w=800&h=391&f=png&s=13859)
应用系统如果遇到系统请求流量的瞬间猛增，有可能会将系统压垮。有了消息队列可以将大量请求缓存起来，分散到很长一段时间处理，这样可以大大提到系统的稳定性和用户体验。

![](https://user-gold-cdn.xitu.io/2020/6/24/172e4555a8ac185c?w=859&h=404&f=png&s=21302)
一般情况，为了保证系统的稳定性，如果系统负载超过阈值，就会阻止用户请求，这会影响用户体验，而如果使用消息队列将请求缓存起来，等待系统处理完毕后通知用户下单完毕，这样总不能下单体验要好。

<u>处于经济考量目的：</u>

业务系统正常时段的QPS如果是1000，流量最高峰是10000，为了应对流量高峰配置高性能的服务器显然不划算，这时可以使用消息队列对峰值流量削峰
- 数据分发

![](https://user-gold-cdn.xitu.io/2020/6/24/172e455dda42fe5b?w=863&h=383&f=png&s=26854)

通过消息队列可以让数据在多个系统更加之间进行流通。数据的产生方不需要关心谁来使用数据，只需要将数据发送到消息队列，数据使用方直接在消息队列中直接获取数据即可

![](https://user-gold-cdn.xitu.io/2020/6/24/172e4565bc3e805a?w=941&h=428&f=png&s=31639)

## MQ的优点和缺点

优点：解耦、削峰、数据分发
缺点包含以下几点：

* 系统可用性降低

  系统引入的外部依赖越多，系统稳定性越差。一旦MQ宕机，就会对业务造成影响。

  如何保证MQ的高可用？

* 系统复杂度提高

  MQ的加入大大增加了系统的复杂度，以前系统间是同步的远程调用，现在是通过MQ进行异步调用。

  如何保证消息没有被重复消费？怎么处理消息丢失情况？那么保证消息传递的顺序性？

* 一致性问题

  A系统处理完业务，通过MQ给B、C、D三个系统发消息数据，如果B系统、C系统处理成功，D系统处理失败。

  如何保证消息数据处理的一致性？

## 各种MQ产品的比较
常见的MQ产品包括Kafka、ActiveMQ、RabbitMQ、RocketMQ。 


![](https://user-gold-cdn.xitu.io/2020/6/24/172e457917b4e3b7?w=795&h=589&f=png&s=116825)
## 来rocketmq看看官网
其实学习一个东西，当然第一件事情就是去它的官网了，我们一起来看看吧

![](https://user-gold-cdn.xitu.io/2020/6/22/172db5f87bb557d7?w=1639&h=651&f=png&s=619078)

- Apache RocketMQ™ is a unified messaging engine, lightweight data processing platform.
- Apache RocketMQ™  是一个标准化的消息引擎，轻量级的处理平台
- unified messaging engine ：标准化消息引擎
- lightweight data processing platform：轻量级处理平台


![](https://user-gold-cdn.xitu.io/2020/6/22/172db60c49fcccf3?w=1377&h=380&f=png&s=53043)

- Low Latency
低延迟
- More than 99.6% response latency within 1 millisecond under high pressure.
在高压力下，1毫秒的延迟内可以响应超过99.6%的请求
- Finance Oriented
金融方向，面向金融(金融业务的稳定性)
- High availability with tracking and auditing features.
系统具有高可用性支撑了追踪和审计的特性
- Industry Sustainable
行业可发展性
- Trillion-level message capacity guaranteed.
保证了万亿级别的消息容量。


消息队列 RocketMQ 版是阿里云基于 Apache RocketMQ 构建的低延迟、高并发、高可用、高可靠的分布式消息中间件。消息队列 RocketMQ 版既可为分布式应用系统提供异步解耦和削峰填谷的能力，同时也具备互联网应用所需的海量消息堆积、高吞吐、可靠重试等特性。


RocketMQ是站在巨人的肩膀上（kafka）MetaQ的内核，又对其进行了优化让其更满足互联网公司的特点。它是纯Java开发，具有高吞吐量、高可用性、适合大规模分布式系统应用的特点。 RocketMQ目前在阿里集团被广泛应用于交易、充值、流计算、消息推送、日志流式处理、binglog分发等场景。



## rocketmq消息中间件功能


![](https://user-gold-cdn.xitu.io/2020/6/22/172db6ca504df725?w=1003&h=788&f=png&s=91169)
- 发布/订阅消息传递模型
- 财务级交易消息
- 各种跨语言客户端，例如Java，C / C ++，Python，Go
- 可插拔的传输协议，例如TCP，SSL，AIO
- 内置的消息跟踪功能，还支持开放式跟踪
- 多功能的大数据和流生态系统集成
- 按时间或偏移量追溯消息
- 可靠的FIFO和严格的有序消息传递在同一队列中
- 高效的推拉消费模型
- 单个队列中的百万级消息累积容量
- 多种消息传递协议，例如JMS和OpenMessaging
- 灵活的分布式横向扩展部署架构
- 快如闪电的批量消息交换系统
- 各种消息过滤器机制，例如SQL和Tag
- 用于隔离测试和云隔离群集的Docker映像
- 功能丰富的管理仪表板，用于配置，指标和监视
- 认证与授权



![](https://user-gold-cdn.xitu.io/2020/6/22/172db6dd1cff8e15?w=991&h=872&f=png&s=96676)

- rocketmq-broker：接受生产者发来的消息并存储（通过调用rocketmq-store），消费者从这里取得消息
- rocketmq-client：提供发送、接受消息的客户端API。
- rocketmq-namesrv：NameServer，类似于Zookeeper，这里保存着消息的TopicName，队列等运行时的元信息。
- rocketmq-common：通用的一些类，方法，数据结构等。
- rocketmq-remoting：基于Netty4的client/server + fastjson序列化 + 自定义二进制协议。
- rocketmq-store：消息、索引存储等。
- rocketmq-filtersrv：消息过滤器Server，需要注意的是，要实现这种过滤，需要上传代码到MQ！

## RocketMQ快速入门

### 下载RocketMQ

[下载地址](https://www.apache.org/dyn/closer.cgi?path=rocketmq/4.5.1/rocketmq-all-4.5.1-bin-release.zip)

###  环境要求

* Linux64位系统

* JDK1.8(64位)

* 源码安装需要安装Maven 3.2.x

### 安装RocketMQ
以二进制包方式安装

1. 解压安装包
2. 进入安装目录

### 目录介绍
* bin：启动脚本，包括shell脚本和CMD脚本
* conf：实例配置文件 ，包括broker配置文件、logback配置文件等
* lib：依赖jar包，包括Netty、commons-lang、FastJSON等

### 启动RocketMQ

1. 启动NameServer
```
# 1.启动NameServer
nohup sh bin/mqnamesrv &
# 2.查看启动日志
tail -f ~/logs/rocketmqlogs/namesrv.log
```
2. 启动Broker


```
# 1.启动Broker
nohup sh bin/mqbroker -n localhost:9876 &
# 2.查看启动日志
tail -f ~/logs/rocketmqlogs/broker.log 
```
 - 问题描述：

RocketMQ默认的虚拟机内存较大，启动Broker如果因为内存不足失败，需要编辑如下两个配置文件，修改JVM内存大小

```
# 编辑runbroker.sh和runserver.sh修改默认JVM大小
vi runbroker.sh
vi runserver.sh
```


```JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m -XX:MetaspaceSize=128m  -XX:MaxMetaspaceSize=320m"```

### 测试RocketMQ
 1. 发送消息
 
```
# 1.设置环境变量
export NAMESRV_ADDR=localhost:9876
# 2.使用安装包的Demo发送消息
sh bin/tools.sh org.apache.rocketmq.example.quickstart.Producer
```

2. 接收消息

```
# 1.设置环境变量
export NAMESRV_ADDR=localhost:9876
# 2.接收消息
sh bin/tools.sh org.apache.rocketmq.example.quickstart.Consumer
```
3. 关闭RocketMQ

```
# 1.关闭NameServer
sh bin/mqshutdown namesrv
# 2.关闭Broker
sh bin/mqshutdown broker
```
## 结尾


好了，第一篇就写到这里了，到目前为止，我们把rocketmq 的四大核心角色都跑了一下，知道是怎么样了的，后面就可以开始慢慢学习它的api 进而学习他们的原理
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
