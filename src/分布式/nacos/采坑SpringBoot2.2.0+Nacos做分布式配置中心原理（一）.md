# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨  
团队准备做一个新项目，然后采用的是微服务架构和分布式系统开发，刚好开始用的时候SpringBoot 2.2.0  然后碰到一些问题记录一下，然后再一起来学习学习 Nacos做配置中心的大致原理，如果有时间 还可以看看源码，哈哈

## SpringBoot 2.2.0的问题
- spring boot 2.2.0 bug ,造成 和mybatis 3.5.2 不兼容  
因为我们的nacos的数据采用的是mysql存放的，然后mysql的版本在nacos 里面是 3.5.2 然后我们用了Nacos之后，我们就会发现他每过10秒就会报下面的错误，

Failed to bind properties under 'mybatis-plus.configuration.incomplete-result-maps[0].assistant.configuration.mapped-statements[0].paameter-map.parameter-mappings[0]' to org.apache.ibatis.mapping.ParameterMapping


真的是每过10秒就会发一次，后面查资料 查了好久才知道这个是SpringBoot的一个小bug 

官方issue: https://github.com/spring-pro...
构造器注入的问题， mybatis 私有构造器不能绑定属性， 造成其他 依赖mybatis 的框架 类型 mybatis-plus 这种问题 https://gitee.com/baomidou/my...


![](https://user-gold-cdn.xitu.io/2020/6/28/172fae1a33c92e5f?w=844&h=619&f=png&s=394146)

然后解决办法
- 第一个换nacos的版本，这样替换不同的mybatis版本
- 第二个换SpringBoot的版本，结果我们就直接升级了

也可以参考冷冷大神的文章
- [不推荐使用Spring Boot 2.2.0 ，这个问题你肯定会遇到](https://segmentfault.com/a/1190000020933354)

然后这个问题就解决了。

### Nacos 

Nacos 致力于帮助您发现、配置和管理微服务。Nacos 提供了一组简单易用的特性集，帮助您快速实现动态服务发现、服务配置、服务元数据及流量管理。

Nacos 帮助您更敏捷和容易地构建、交付和管理微服务平台。 Nacos 是构建以“服务”为中心的现代应用架构 (例如微服务范式、云原生范式) 的服务基础设施。

Nacos的作用还是很多的，但是在我们的项目里面，目前我就接触到了其中的2项，一个服务的注册中心，和一个服务的配置中心，既然用到了，当然得学学人家是怎么实现的吧，哈哈，对于刚接触这些东西的小白，自己对源码和原理的理解都是再前辈的基础上，先看山是山吧 然后看山不是山，最后看山还是山，小六六自己的学习方法论哈哈。
## Nacos 配置中心原理分析

动态配置管理是 Nacos 的三大功能之一，通过动态配置服务，我们可以在所有环境中以集中和动态的方式管理所有应用程序或服务的配置信息。

动态配置中心可以实现配置更新时无需重新部署应用程序和服务即可使相应的配置信息生效，这极大了增加了系统的运维能力。

以前我们修改了一些东西，总得重启系统，现在有这个我们就可以动态的去修改配置了,不过说起实现的原理还是没有那么简单的，其中涉及到了spring +springcloud+nacos的服务端+nacos的客户端；其中每一个角色都是这个实现的必不可少的一部分。小六六会跟大家一起来学习学习，自己本身比较菜，也是刚接触，那小六六就一个个慢慢的来看看

### 先来个简单的demo

下面我将来和大家一起来了解下 Nacos 的动态配置的能力，看看 Nacos 是如何以简单、优雅、高效的方式管理配置，实现配置的动态变更的。


### 环境准备
首先我们要准备一个 Nacos 的服务端，现在有两种方式获取 Nacos 的服务端：

1.通过源码编译
2.下载 Release 包


两种方法可以获得 Nacos 的可执行程序，下面我用第一种方式通过源码编译一个可执行程序，可能有人会问为啥不直接下载 Release 包，还要自己去编译呢？首先 Release 包也是通过源码编译得到的，其次我们通过自己编译可以了解一些过程也有可能会碰到一些问题，这些都是很重要的经验，好了那我们直接源码编译吧。

首先 fork 一份 nacos 的代码到自己的 github 库，然后把代码 clone 到本地。

然后在项目的根目录下执行以下命令（假设我们已经配置好了 java 和 maven 环境）：


```
mvn -Prelease-nacos clean install -U  
```

然后在项目的 distribution 目录下我们就可以找到可执行程序了，包括两个压缩包，这两个压缩包就是nacos 的 github 官网上发布的 Release 包。

![](https://user-gold-cdn.xitu.io/2020/6/28/172fb078864f06cc?w=515&h=885&f=png&s=63413)



### 启动服务端
```
sh startup.sh -m standalone
```

![](https://user-gold-cdn.xitu.io/2020/6/28/172fb0a1dd67759c?w=1234&h=606&f=png&s=101049)

### 进去之后的样子

![](https://user-gold-cdn.xitu.io/2020/6/28/172fb0c10ee8a6f8?w=1882&h=610&f=png&s=122768)


### 新建配置
接下来我们在控制台上创建一个简单的配置项，如下图所示：

![](https://user-gold-cdn.xitu.io/2020/6/28/172fb0e3616a1f0a?w=1712&h=783&f=png&s=59546)


### 启动客户端
当服务端以及配置项都准备好之后，就可以创建客户端了，如下图所示新建一个 Nacos 的 ConfigService 来接收数据：

![](https://user-gold-cdn.xitu.io/2020/6/28/172fb147383149b8?w=1469&h=853&f=png&s=196198)



执行后将打印如下信息：


![](https://user-gold-cdn.xitu.io/2020/6/28/172fb15ac23c8b02?w=1850&h=814&f=png&s=219176)


修改配置信息


![](https://user-gold-cdn.xitu.io/2020/6/28/172fb1a713bc711f?w=882&h=674&f=png&s=34614)



执行后将打印如下信息：


![](https://user-gold-cdn.xitu.io/2020/6/28/172fb1ac50800395?w=763&h=279&f=png&s=43780)


至此一个简单的动态配置管理功能已经讲完了，删除配置和更新配置操作类似，这里不再赘述。

## 适用场景


了解了动态配置管理的效果之后，我们知道了大概的原理了，Nacos 服务端保存了配置信息，客户端连接到服务端之后，根据 dataID，group可以获取到具体的配置信息，当服务端的配置发生变更时，客户端会收到通知。当客户端拿到变更后的最新配置信息后，就可以做自己的处理了，这非常有用，所有需要使用配置的场景都可以通过 Nacos 来进行管理。

可以说 Nacos 有很多的适用场景，包括但不限于以下这些情况：

- 数据库连接信息
- 限流规则和降级开关
- 流量的动态调度


### 推还是拉


现在我们了解了 Nacos 的配置管理的功能了，但是有一个问题我们需要弄明白，那就是 Nacos 客户端是怎么实时获取到 Nacos 服务端的最新数据的。

其实客户端和服务端之间的数据交互，无外乎两种情况：

- 服务端推数据给客户端
- 客户端从服务端拉数据

那到底是推还是拉呢，从 Nacos 客户端通过 Listener 来接收最新数据的这个做法来看，感觉像是服务端推的数据，但是不能想当然，要想知道答案，最快最准确的方法就是从源码中去寻找。

### 创建 ConfigService

```
        ConfigService configService = NacosFactory.createConfigService(properties);
```


从我们的 demo 中可以知道，首先是创建了一个 ConfigService。而 ConfigService 是通过 ConfigFactory 类创建的，如下图所示：


```

    /**
     * Create Config
     *
     * @param properties init param
     * @return Config
     * @throws NacosException Exception
     */
    public static ConfigService createConfigService(Properties properties) throws NacosException {
        try {
            Class<?> driverImplClass = Class.forName("com.alibaba.nacos.client.config.NacosConfigService");
            Constructor constructor = driverImplClass.getConstructor(Properties.class);
            ConfigService vendorImpl = (ConfigService)constructor.newInstance(properties);
            return vendorImpl;
        } catch (Throwable e) {
            throw new NacosException(-400, e.getMessage());
        }
    }
```


可以看到实际是通过反射调用了 NacosConfigService 的构造方法来创建 ConfigService 的，而且是有一个 Properties 参数的构造方法。

需要注意的是，这里并没有通过单例或者缓存技术，也就是说每次调用都会重新创建一个 ConfigService的实例。

### 实例化 ConfigService


![](https://user-gold-cdn.xitu.io/2020/6/28/172fb2546870e9fd?w=1200&h=960&f=png&s=653715)


实例化时主要是初始化了两个对象，他们分别是：


- HttpAgent
- ClientWorker  


![](https://user-gold-cdn.xitu.io/2020/6/28/172fb26716c4cf44?w=677&h=78&f=png&s=14326)

###  HttpAgent

其中 agent 是通过装饰着模式实现的，ServerHttpAgent 是实际工作的类，MetricsHttpAgent 在内部也是调用了 ServerHttpAgent 的方法，另外加上了一些统计操作，所以我们只需要关心 ServerHttpAgent 的功能就可以了。

agent 实际是在 ClientWorker 中发挥能力的，下面我们来看下 ClientWorker 类。

### ClientWorker

以下是 ClientWorker 的构造方法，如下图所示：


![](https://user-gold-cdn.xitu.io/2020/6/28/172fb2776d64a68f?w=1200&h=690&f=png&s=427327)


可以看到 ClientWorker 除了将 HttpAgent 维持在自己内部，还创建了两个线程池：

第一个线程池是只拥有一个线程用来执行定时任务的 executor，executor 每隔 10ms 就会执行一次 checkConfigInfo() 方法，从方法名上可以知道是每 10 ms 检查一次配置信息。

第二个线程池是一个普通的线程池，从 ThreadFactory 的名称可以看到这个线程池是做长轮询的。**(科普一下长轮询：客户端向服务器端发送 Ajax 请求，服务器端接收到请求后保持住连接，直到有新消息才返回响应信息并关闭连接。客户端在处理请求返回信息（超时或有效数据）后再次发出请求，重新建立连接。缺点是：服务器保持连接会消耗较多的资源。)**


现在让我们来看下 executor 每 10ms 执行的方法到底是干什么的，如下图所示：


![](https://user-gold-cdn.xitu.io/2020/6/28/172fb2d71290cb3b?w=1177&h=273&f=png&s=36010)

这个和我前面用SpringBoot导致mybatis 不兼容的情况报错时间间隔是一样的 哈哈 牛逼！


![](https://user-gold-cdn.xitu.io/2020/6/28/172fb2ea703fbdf0?w=1200&h=421&f=png&s=331737)

提一句 看我们中国人自己写的代码就是舒服 注释都是中文，美滋滋。


可以看到，checkConfigInfo 方法是取出了一批任务，然后提交给 executorService 线程池去执行，执行的任务就是 LongPollingRunnable，每个任务都有一个 taskId。

现在我们来看看 LongPollingRunnable 做了什么，主要分为两部分，第一部分是检查本地的配置信息，第二部分是获取服务端的配置信息然后更新到本地。

![](https://user-gold-cdn.xitu.io/2020/6/28/172fb3250574ed91?w=1200&h=730&f=png&s=389646)
1.本地检查

通过跟踪 checkLocalConfig 方法，可以看到 Nacos 将配置信息保存在了

~/nacos/config/fixed-{address}_8848_nacos/snapshot/DEFAULT_GROUP/{dataId}

2. 服务端检查

然后通过 checkUpdateDataIds() 方法从服务端获取那些值发生了变化的 dataId 列表，

通过 getServerConfig 方法，根据 dataId 到服务端获取最新的配置信息，接着将最新的配置信息保存到 CacheData 中。

最后调用 CacheData 的 checkListenerMd5 方法，可以看到该方法在第一部分也被调用过，我们需要重点关注一下。


![](https://user-gold-cdn.xitu.io/2020/6/28/172fb37988edd766?w=1200&h=1011&f=png&s=654705)


可以看到，在该任务的最后，也就是在 finally 中又重新通过 executorService 提交了本任务。

## 添加 Listener
好了现在我们可以为 ConfigService 来添加一个 Listener 了，最终是调用了 ClientWorker 的 addTenantListeners 方法，如下图所示：


![](https://user-gold-cdn.xitu.io/2020/6/28/172fb38fce84ee87?w=990&h=246&f=png&s=30457)


该方法分为两个部分，首先根据 dataId，group 和当前的场景获取一个 CacheData 对象，然后将当前要添加的 listener 对象添加到 CacheData 中去。

也就是说 listener 最终是被这里的 CacheData 所持有了，那 listener 的回调方法 receiveConfigInfo 就应该是在 CacheData 中触发的。

我们发现 CacheData 是出现频率非常高的一个类，在 LongPollingRunnable 的任务中，几乎所有的方法都围绕着 CacheData 类，现在添加 Listener 的时候，实际上该 Listener 也被委托给了 CacheData，那我们要重点关注下 CacheData 类了。


### CacheData

![](https://user-gold-cdn.xitu.io/2020/6/28/172fb3f1a89893ea?w=1036&h=690&f=png&s=435751)


可以看到除了 dataId，group，content，taskId 这些跟配置相关的属性，还有两个比较重要的属性：listeners、md5。

listeners 是该 CacheData 所关联的所有 listener，不过不是保存的原始的 Listener 对象，而是包装后的 ManagerListenerWrap 对象，该对象除了持有 Listener 对象，还持有了一个 lastCallMd5 属性。

另外一个属性 md5 就是根据当前对象的 content 计算出来的 md5 值。

这个就很明了，怎么判断是否有变化，只要是他们的内容的md5值不一样，那么就说明他们内容是有变化的


### 触发回调

现在我们对 ConfigService 有了大致的了解了，现在剩下最后一个重要的问题还没有答案，那就是 ConfigService 的 Listener 是在什么时候触发回调方法 receiveConfigInfo 的。

现在让我们回过头来想一下，在 ClientWorker 中的定时任务中，启动了一个长轮询的任务：LongPollingRunnable，该任务多次执行了 cacheData.checkListenerMd5() 方法，那现在就让我们来看下这个方法到底做了些什么，如下图所示：


![](https://user-gold-cdn.xitu.io/2020/6/28/172fb412af2f6615?w=1088&h=294&f=png&s=129334)

到这里应该就比较清晰了，该方法会检查 CacheData 当前的 md5 与 CacheData 持有的所有 Listener 中保存的 md5 的值是否一致，如果不一致，就执行一个安全的监听器的通知方法：safeNotifyListener，通知什么呢？我们可以大胆的猜一下，应该是通知 Listener 的使用者，该 Listener 所关注的配置信息已经发生改变了。现在让我们来看一下 safeNotifyListener 方法，如下图所示：

![](https://user-gold-cdn.xitu.io/2020/6/28/172fb41d787799e7?w=1200&h=797&f=png&s=621859)

![](https://user-gold-cdn.xitu.io/2020/6/28/172fb432877c11a4?w=826&h=192&f=png&s=80205)

可以看到在 safeNotifyListener 方法中，重点关注下红框中的三行代码：获取最新的配置信息，调用 Listener 的回调方法，将最新的配置信息作为参数传入，这样 Listener 的使用者就能接收到变更后的配置信息了，最后更新 ListenerWrap 的 md5 值。和我们猜测的一样， Listener 的回调方法就是在该方法中触发的。

### Md5何时变更

那 CacheData 的 md5 值是何时发生改变的呢？我们可以回想一下，在上面的 LongPollingRunnable 所执行的任务中，在获取服务端发生变更的配置信息时，将最新的 content 数据写入了 CacheData 中，我们可以看下该方法如下：

可以看到是在长轮询的任务中，当服务端配置信息发生变更时，客户端将最新的数据获取下来之后，保存在了 CacheData 中，同时更新了该 CacheData 的 md5 值，所以当下次执行 checkListenerMd5 方法时，就会发现当前 listener 所持有的 md5 值已经和 CacheData 的 md5 值不一样了，也就意味着服务端的配置信息发生改变了，这时就需要将最新的数据通知给 Listener 的持有者。

至此配置中心的完整流程已经分析完毕了，可以发现，Nacos 并不是通过推的方式将服务端最新的配置信息发送给客户端的，而是客户端维护了一个长轮询的任务，定时去拉取发生变更的配置信息，然后将最新的数据推送给 Listener 的持有者。

## 拉的优势
客户端拉取服务端的数据与服务端推送数据给客户端相比，优势在哪呢，为什么 Nacos 不设计成主动推送数据，而是要客户端去拉取呢？如果用推的方式，服务端需要维持与客户端的长连接，这样的话需要耗费大量的资源，并且还需要考虑连接的有效性，例如需要通过心跳来维持两者之间的连接。而用拉的方式，客户端只需要通过一个无状态的 http 请求即可获取到服务端的数据。

## 总结
Nacos 服务端创建了相关的配置项后，客户端就可以进行监听了。

客户端是通过一个定时任务来检查自己监听的配置项的数据的，一旦服务端的数据发生变化时，客户端将会获取到最新的数据，并将最新的数据保存在一个 CacheData 对象中，然后会重新计算 CacheData 的 md5 属性的值，此时就会对该 CacheData 所绑定的 Listener 触发 receiveConfigInfo 回调。

客户端是通过一个定时任务来检查自己监听的配置项的数据的(每10秒检查一次) 检查 CacheData 中的md5 和每个Listener 持有的md5 是否相同，如果不同 说明服务端的配置发生了改变，这个时候我们就得去拉取新的服务端的数据并刷新（@RefreshScope 这个后面再学习），那么我们还得想 这个md5是什么时候变的呢？这个就是我们前面说的那个长连接了，这个长连接的超时时间是30s并且在这个方法的final方法中是调用自己，说明这个轮询会一直跑下去，这样每次服务端 有配置变更就可用通知的到客户端了


考虑到服务端故障的问题，客户端将最新数据获取后会保存在本地的 snapshot 文件中，以后会优先从文件中获取配置信息的值

- [原文](https://www.jianshu.com/p/38b5452c9fec)

## 结尾

今天我们就学到这里了，今天把客户端的学了一下，明天我们就来看看服务端怎么参与到这个自动配置刷新中来，再后面 我们还得结合spring springCloud的一起来分析分析。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
