# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨
Spring Cloud 小六六用了几个项目了，但是呢？发现自己并没有好好去理解每个组件，以前用netflix 现在用alibaba,其实说白了，就是组件不同，实现不同，但是接口还是一样的，我们就一个组件一个组件的来学习，今天我们来看看eureka吧！


## eureka功能分析

首先，eureka在springcloud中充当服务注册功能，相当于dubbo+zk里面得zk，但是比zk要简单得多，zk可以做得东西太多了，包括分布式锁，分布式队列都是基于zk里面得四种节点加watch机制通过长连接来实现得，但是eureka不一样，eureka是基于HTTPrest来实现的，就是把服务的信息放到一个ConcurrentHashMap中，然后服务启动的时候去读取这个map，来把所有服务关联起来，然后服务器之间调用的时候通过信息，进行http调用。eureka包括两部分，一部分就是服务提供者（对于eureka来说就是客户端），一部分是服务端，客户端需要每个读取每个服务的信息，然后注册到服务端，很明显了，这个服务端就是接受客户端提供的自身的一些信息。


## Eureka的一些概念：
在Eureka的服务治理中，会涉及到下面一些概念：
- 服务注册：Eureka Client会通过发送REST请求的方式向Eureka Server注册自己的服务，提供自身的元数据，比如ip地址、端口、运行状况指标的url、主页地址等信息。Eureka Server接收到注册请求后，就会把这些元数据信息存储在一个双层的Map中。
- 服务续约：在服务注册后，Eureka Client会维护一个心跳来持续通知Eureka Server，说明服务一直处于可用状态，防止被剔除。Eureka Client在默认的情况下会每隔30秒发送一次心跳来进行服务续约。
- 服务同步：Eureka Server之间会互相进行注册，构建Eureka Server集群，不同Eureka Server之间会进行服务同步，用来保证服务信息的一致性。
- 获取服务：服务消费者（Eureka Client）在启动的时候，会发送一个REST请求给Eureka Server，获取上面注册的服务清单，并且缓存在Eureka Client本地，默认缓存30秒。同时，为了性能考虑，Eureka Server也会维护一份只读的服务清单缓存，该缓存每隔30秒更新一次。
- 服务调用：服务消费者在获取到服务清单后，就可以根据清单中的服务列表信息，查找到其他服务的地址，从而进行远程调用。Eureka有Region和Zone的概念，一个Region可以包含多个Zone，在进行服务调用时，优先访问处于同一个Zone中的服务提供者。
- 服务下线：当Eureka Client需要关闭或重启时，就不希望在这个时间段内再有请求进来，所以，就需要提前先发送REST请求给Eureka Server，告诉Eureka Server自己要下线了，Eureka Server在收到请求后，就会把该服务状态置为下线（DOWN），并把该下线事件传播出去。
- 服务剔除：有时候，服务实例可能会因为网络故障等原因导致不能提供服务，而此时该实例也没有发送请求给Eureka Server来进行服务下线，所以，还需要有服务剔除的机制。Eureka Server在启动的时候会创建一个定时任务，每隔一段时间（默认60秒），从当前服务清单中把超时没有续约（默认90秒）的服务剔除。
- 自我保护：既然Eureka Server会定时剔除超时没有续约的服务，那就有可能出现一种场景，网络一段时间内发生了异常，所有的服务都没能够进行续约，Eureka Server就把所有的服务都剔除了，这样显然不太合理。所以，就有了自我保护机制，当短时间内，统计续约失败的比例，如果达到一定阈值，则会触发自我保护的机制，在该机制下，Eureka Server不会剔除任何的微服务，等到正常后，再退出自我保护机制。

从这些概念中，就可以知道大体的流程，Eureka Client向Eureka Server注册，并且维护心跳来进行续约，如果长时间不续约，就会被剔除。Eureka Server之间进行数据同步来形成集群，Eureka Client从Eureka Server获取服务列表，用来进行服务调用，Eureka Client服务重启前调用Eureka Server的接口进行下线操作。


## 源码分析：

### Eureka Client源码：

Eureka Client源码：
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e903ce4789c84e1b923bd1138ae8a2b9~tplv-k3u1fbpfcp-zoom-1.image)
其实类并不多，它客户端。

先从服务注册开始梳理，Eureka Client启动的时候就去Eureka Server注册服务。通过在启动类上添加@EnableDiscoveryClient这个注解，来声明这是一个Eureka Client。所以，先看下这个注解：
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/08a1613842db4559a9088a07293db155~tplv-k3u1fbpfcp-zoom-1.image)
这个注解上方有注释，说这个注解是为了开启一个DiscoveryClient的实例。
接下来就可以搜索DiscoveryClient，
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b34574e31530474a877c9aba29ec3af2~tplv-k3u1fbpfcp-zoom-1.image)

我们就来看看这个类吧

这个类实现了EurekaClient接口，而EurekaClient又继承了LookupService接口。这两个接口都是Netflix开源包中的内容，主要定义了针对Eureka发现服务的抽象方法。所以，DiscoveryClient类主要就是发现服务的。
接下来，就详细看下DiscoveryClient类，类上面的注释，说明了这个类是用来帮助和Eureka Server互相协作的，可以进行服务注册，服务续约，服务下线，获取服务列表。需要配置一个Eureka Server的Url列表。
上面提到的这个列表，就是我们在配置文件中配置的eureka.client.service-url.defaultZone这一选项，这个地址就是Eureka Server的地址，服务注册、服务续约以及其他的操作，都是向这个地址发送请求的。
在DiscoveryClient类中可以看到有很多方法，包括register()、renew()、shutdown()、unregister()等。
既然Eureka Client需要一开始先初始化DiscoveryClient实例，那就看下DiscoveryClient的构造方法。
DiscoveryClient的构造方法还是挺长的，里面初始化了一大堆的对象，不过可以观察到在new了这么一大堆对象之后，调用了initScheduledTasks();这个方法，所以，点进initScheduledTasks()方法里面看下。

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5dbd5b558cc74dada29623f340872804~tplv-k3u1fbpfcp-zoom-1.image)

看看这个构造

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1adeb36b14d64d3f8f54fff309c0ed43~tplv-k3u1fbpfcp-zoom-1.image)

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/132554ded7da4e16a8c85b35a3ba492c~tplv-k3u1fbpfcp-zoom-1.image)


在initScheduledTasks方法中，初始化了几个任务。
一开始有个if判断，判断是否需要从Eureka Server获取数据，如果为真，则初始化一个服务获取的定时任务。
还有有个if (clientConfig.shouldRegisterWithEureka())的判断，所以，当Eureka Client配置这个为true时，就会执行这个if语句里面的逻辑。if语句中，会初始化一个Heartbeat timer和InstanceInfoReplicator。Heartbeat timer就是不断的发送请求来维持心跳的，也就是服务续约的任务。而InstanceInfoReplicator类实现了Runnable接口，所以需要看下InstanceInfoReplicator类中的run方法。

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1eb6e57d4a8e47c4b8a047e7cd36a67f~tplv-k3u1fbpfcp-zoom-1.image)


## 原理解析

### 服务提供者
1、启动后，向注册中心发起register请求，注册服务

2、在运行过程中，定时向注册中心发送renew心跳，证明“我还活着”。

3、停止服务提供者，向注册中心发起cancel请求，清空当前服务注册信息。
### 服务消费者
1、启动后，从注册中心拉取服务注册信息

2、在运行过程中，定时更新服务注册信息。

3、服务消费者发起远程调用：


### 注册中心

1、启动后，从其他节点拉取服务注册信息。

2、运行过程中，定时运行evict任务，剔除没有按时renew的服务（包括非正常停止和网络故障的服务）。

3、运行过程中，接收到的register、renew、cancel请求，都会同步至其他注册中心节点。


### 数据存储结构

既然是服务注册中心，必然要存储服务的信息，我们知道ZK是将服务信息保存在树形节点上。而下面是Eureka的数据存储结构：

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/eb8ef43622bf4e81ab0f0ce5753e7ddc~tplv-k3u1fbpfcp-zoom-1.image)

Eureka的数据存储分了两层：数据存储层和缓存层。Eureka Client在拉取服务信息时，先从缓存层获取（相当于Redis），如果获取不到，先把数据存储层的数据加载到缓存中（相当于Mysql），再从缓存中获取。值得注意的是，数据存储层的数据结构是服务信息，而缓存中保存的是经过处理加工过的、可以直接传输到Eureka Client的数据结构。

> Eureka这样的数据结构设计是把内部的数据存储结构与对外的数据结构隔离开了，就像是我们平时在进行接口设计一样，对外输出的数据结构和数据库中的数据结构往往都是不一样的。

#### 数据存储层

这里为什么说是存储层而不是持久层？因为rigistry本质上是一个双层的ConcurrentHashMap，存储在内存中的。

第一层的key是spring.application.name，value是第二层ConcurrentHashMap；

第二层ConcurrentHashMap的key是服务的InstanceId，value是Lease对象；

Lease对象包含了服务详情和服务治理相关的属性。

#### 二级缓存层

Eureka实现了二级缓存来保存即将要对外传输的服务信息，数据结构完全相同。

一级缓存：ConcurrentHashMap<Key,Value> readOnlyCacheMap，本质上是HashMap，无过期时间，保存服务信息的对外输出数据结构。

二级缓存：Loading<Key,Value> readWriteCacheMap，本质上是guava的缓存，包含失效机制，保存服务信息的对外输出数据结构。

既然是缓存，那必然要有更新机制，来保证数据的一致性。下面是缓存的更新机制：

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/319bab5bf9d34ce3ad4a8bb40ae0e7a5~tplv-k3u1fbpfcp-zoom-1.image)
更新机制包含删除和加载两个部分，上图黑色箭头表示删除缓存的动作，绿色表示加载或触发加载的动作。

### 服务注册机制

服务提供者、服务消费者、以及服务注册中心自己，启动后都会向注册中心注册服务（如果配置了注册）。下图是介绍如何完成服务注册的：

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ba40b26aebdb417284ea556f1ca1d6f6~tplv-k3u1fbpfcp-zoom-1.image)

注册中心服务接收到register请求后：

1、保存服务信息，将服务信息保存到registry中；

2、更新队列，将此事件添加到更新队列中，供Eureka Client增量同步服务信息使用。

3、清空二级缓存，即readWriteCacheMap，用于保证数据的一致性。

4、更新阈值，供剔除服务使用。

5、同步服务信息，将此事件同步至其他的Eureka Server节点


### 服务续约机制
服务注册后，要定时（默认30S，可自己配置）向注册中心发送续约请求，告诉注册中心“我还活着”。

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6e57eb62989946a4abdf379603a7b505~tplv-k3u1fbpfcp-zoom-1.image)

注册中心收到续约请求后：

1、更新服务对象的最近续约时间，即Lease对象的lastUpdateTimestamp;

2、同步服务信息，将此事件同步至其他的Eureka Server节点。


### 服务注销机制
服务正常停止之前会向注册中心发送注销请求，告诉注册中心“我要下线了”。

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a78c39fb5eb846068119c11f13f1217e~tplv-k3u1fbpfcp-zoom-1.image)

注册中心服务接收到cancel请求后：

1、删除服务信息，将服务信息从registry中删除；

2、更新队列，将此事件添加到更新队列中，供Eureka Client增量同步服务信息使用。

3、清空二级缓存，即readWriteCacheMap，用于保证数据的一致性。

4、更新阈值，供剔除服务使用。

5、同步服务信息，将此事件同步至其他的Eureka Server节点。

#### 自我保护
Eureka自我保护机制是为了防止误杀服务而提供的一个机制。Eureka的自我保护机制“谦虚”的认为如果大量服务都续约失败，则认为是自己出问题了（如自己断网了），也就不剔除了；反之，则是Eureka Client的问题，需要进行剔除。而自我保护阈值是区分Eureka Client还是Eureka Server出问题的临界值：如果超出阈值就表示大量服务可用，少量服务不可用，则判定是Eureka Client出了问题。如果未超出阈值就表示大量服务不可用，则判定是Eureka Server出了问题。


## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
