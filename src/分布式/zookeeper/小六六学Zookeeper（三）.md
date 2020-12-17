# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
## 叨絮
 前面把理论都大致撸了一下，接下来，我们就看看具体怎么用他吧
 
- [小六六学Zookeeper（一）]()
- [小六六学Zookeeper（二）]()

## 下载 安装

### zookeeper单机部署部署

- 下载地址 
https://mirror.bit.edu.cn/apache/zookeeper/zookeeper-3.6.2/apache-zookeeper-3.6.2-bin.tar.gz

- 解压zookeeper 压缩包
tar -xvf apache-zookeeper-3.6.2-bin.tar.gz

- 进入解压后的zookeeper目录
cd apache-zookeeper-3.6.2-bin

- 编辑 zoo.cfg文件
cd ZOOKEEPER_HOME/conf
vim zoo.cfg 添加如下内容：
```

tickTime=2000
dataDir=/usr/local/zookeeper/data
clientPort=2181
```
保存编辑

- 启动zookeeper
cd ZOOKEEPER_HOME/bin

执行 ./zkServer.sh start

### ZOOKEEPER集群部署方式

集群部署方式 在 zoo.cfg 中添加每个集群服务器的ip配置
```
tickTime=2000
dataDir=/usr/local/zookeeper/data  // zookeeper 数据目录
clientPort=2181
initLimit=5
syncLimit=2
server.1=yourServerIP:2888:3888
server.2=yourServerIP:2888:3888
server.3=yourServerIP:2888:3888
```

dataDir=/usr/local/zookeeper/data zookeeper 目录中添加myid 文件
vi myid 输入内容对应 服务id 每个zookeeper 服务器 分别 对应输入 1 2 3

设置完成后分别启动zookeeper集群服务器
```
ZOOKEEPER_HOME/bin/zkServer.sh start

```

### 客户端命令行操作

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d53ccc7a2cef449abae17d8a8c80207f~tplv-k3u1fbpfcp-watermark.image)

#### 启动客户端
```
bin/zkCli.sh
```
至于它的其他命令，我这边就不一一去列举了，最主要


## 详解Zookeeper客户端Curator的使用

zookeeper的原生使用的话，这边就不一一讲了，而且生产上也不建议用原生的，最好是使用人家封装好的，
而Curator无疑是Zookeeper客户端中的瑞士军刀，解决了很多Zookeeper客户端非常底层的细节开发工作，包括连接重连、反复注册Watcher和NodeExistsException异常等等。


### 依赖

```
    <!--zookeeper-->
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
            <version>4.2.0</version>
            <exclusions>
                <exclusion>
                    <artifactId>zookeeper</artifactId>
                    <groupId>org.apache.zookeeper</groupId>
                </exclusion>
            </exclusions>
        </dependency> 
```

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/060887177013418ba0a924af70e935d8~tplv-k3u1fbpfcp-watermark.image)

Curator包含了几个包：
- curator-framework：对zookeeper的底层api的一些封装
- curator-client：提供一些客户端的操作，例如重试策略等
- curator-recipes：封装了一些高级特性，如：Cache事件监听、选举、分布式锁、分布式计数器、分布式Barrier等

> 这边我要说明一下的是 我们的zk版本，为啥我要把zk排除呢？就是因为不兼容的问题，所以呢，我们必须要把zk的客户端版本，和我们服务端版本一致才可以。

### Api

#### 会话创建

我们知道 zk 是cs 架构， 也就是分为服务端和客户端，我们的curator 其实就是一个客户端，那么首先要做的事情，当然就是和服务端建立连接嘛


- 使用静态工厂方法创建会话

```
CuratorFramework zkClient = CuratorFrameworkFactory.newClient(zkAddr, 5000, 3000, new ExponentialBackoffRetry(1000, 5));

```

构造函数主要有4个参数：

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/cbaab5427ff949d58f6d42548841e4b5~tplv-k3u1fbpfcp-watermark.image)

- 使用Fluent风格创建会话
```
CuratorFramework zkClient = CuratorFrameworkFactory.builder().connectString(zkAddr)
        .sessionTimeoutMs(5000)
        .connectionTimeoutMs(3000)
        .retryPolicy(new ExponentialBackoffRetry(1000, 5))
        .build();

```
#### 启动客户端

当创建会话成功，得到实例然后可以直接调用其start( )方法启动客户端。

```
zkClient.start();
```

#### 数据节点操作

zk一般有以下4种节点类型
- PERSISTENT：持久节点
- PERSISTENT_SEQUENTIAL：持久顺序节点
- EPHEMERAL：临时节点
- EPHEMERAL_SEQUENTIAL：临时顺序节点

##### 创建
```
// 创建一个节点，初始内容为空
// 如果没有设置节点属性，节点创建模式默认为持久化节点，内容默认为空
zkClient.create().forPath("test");

// 创建一个节点，附带初始化内容
zkClient.create().forPath("test", "contect".getBytes());

// 创建一个节点，指定创建模式（临时节点），内容为空
zkClient.create().withMode(CreateMode.EPHEMERAL).forPath("test");

// 创建一个节点，指定创建模式（临时节点），附带初始化内容
zkClient.create().withMode(CreateMode.EPHEMERAL).forPath("test", "contect".getBytes());

// 创建一个节点，指定创建模式（临时节点），附带初始化内容，并且自动递归创建父节点
zkClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("test", "contect".getBytes());

```

自动递归创建父节点非常有用，一般情况创建一个子节点必须先判断它的父节点是否存在，如果不存在直接创建会抛出NoNodeException，使用creatingParentContainersIfNeeded()能够自动递归创建所有所需的父节点。

##### 删除
```
// 删除一个节点
// 此方法只能删除叶子节点，否则会抛出异常。
zkClient.delete().forPath("test");

// 删除一个节点，并且递归删除其所有的子节点
zkClient.delete().deletingChildrenIfNeeded().forPath("test");

// 删除一个节点，强制指定版本进行删除
zkClient.delete().withVersion(1000).forPath("test");

// 删除一个节点，强制保证删除
// guaranteed()是一个保障措施，只要客户端会话有效，会在后台持续进行删除操作，直到成功。
zkClient.delete().guaranteed().forPath("test");

// 上面的多个流式接口是可以自由组合的，例如：
zkClient.delete().guaranteed().deletingChildrenIfNeeded().withVersion(1000).forPath("test");

```

##### 读取数据节点

```
// 读取一个节点的数据内容
zkClient.getData().forPath("test");

// 读取一个节点的数据内容，同时获取到该节点的stat
Stat stat = new Stat();
zkClient.getData().storingStatIn(stat).forPath("test");

//获取某个节点的所有子节点路径
zkClient.getChildren().forPath("test");

```

通过传递Stat可以获取到读取的节点状态信息（例如版本号），zookeeper内部根据版本号区别当前的更新是不是最新，所以带版本号更新可避免并行操作带来的数据不一致问题。

##### 更新数据节点

```
// 更新一个节点的数据内容
// 该接口会返回一个Stat实例
client.setData().forPath("test", "contect".getBytes());

// 更新一个节点的数据内容，强制指定版本进行更新
client.setData().withVersion(1000 ).forPath("test", "contect".getBytes());

// 检查节点是否存在
client.checkExists().forPath("test");

```

##### 事务
CuratorFramework的实例包含inTransaction( )接口方法，调用此方法开启一个ZooKeeper事务。可以复合create, setData, check, and/or delete 等操作然后调用commit()作为一个原子操作提交。例如：

```
zkClient.inTransaction().check().forPath("test")
        .and().create().withMode(CreateMode.PERSISTENT).forPath("test", "contect".getBytes())
        .and().setData().forPath("test", "newContect".getBytes())
        .and().commit();
```

##### 异步接口
上面提到的创建、删除、更新、读取等方法都是同步的，Curator提供异步接口，引入了BackgroundCallback接口用于处理异步接口调用之后服务端返回的结果信息。BackgroundCallback接口中一个重要的回调值为CuratorEvent，里面包含事件类型、响应吗和节点的详细信息

```
ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 300, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10000), new ThreadFactory() {
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "zkSyncThreadPool");
    }
});
zkClient.create()
        .creatingParentsIfNeeded()
        .withMode(CreateMode.EPHEMERAL)
        .inBackground((curatorFramework, curatorEvent) -> {
            System.out.println(String.format("eventType:%s,resultCode:%s",curatorEvent.getType(),curatorEvent.getResultCode()));
        }, executor)
        .forPath("test");

```

如果inBackground()方法不指定executor，那么会默认使用Curator的EventThread去进行异步处理。

#### 监听、缓存

原生Zookeeper通过注册Watcher来进行事件监听，而Watcher只能单次注册，如果需要多次使用则需要反复注册。Cache是Curator中对事件监听的包装，可以看作是在本地缓存了事件监听，从而实现反复注册监听。Curator提供了三种Watcher(Cache)来监听结点的变化。
##### PathChildrenCache
PathChildrenCache监听一个节点（Znode）下面所有子节点的变化，当一个子节点增加， 更新，删除时，PathChildrenCache可以监听到子节点的变化，同时获取子节点的数据和状态，而状态的更变将通过PathChildrenCacheListener。


```
// 监听路径子节点的变化
PathChildrenCache watcher = new PathChildrenCache(zkClient, getPath(), true);
PathChildrenCacheListener childrenCacheListener = (framework, event) -> {
    if (event.getType() != CHILD_UPDATED || !getPath().equals(event.getData().getPath())) {
        return;
    }
    initDataSource();
    System.out.println("数据源更新完毕");
    System.out.println("子节点事件类型：" + event.getType() + " | 路径：" + (null != event.getData() ? event.getData().getPath() : null));
};
watcher.getListenable().addListener(childrenCacheListener);
watcher.start();

```
##### NodeCache

NodeCache与PathChildrenCache类似，但它是监听某个节点的变化，当节点发送变化（增加，删除，内容修改），NodeCache同样可以监听到节点的变化。

```
// 监听节点变化
NodeCache cache = new NodeCache(zkClient, getPath());
NodeCacheListener nodeCacheListener = () -> {
    ChildData data = cache.getCurrentData();
    if (null != data) {
        System.out.println("节点数据：" + new String(cache.getCurrentData().getData()));
    } else {
        System.out.println("节点被删除!");
    }
};
cache.getListenable().addListener(nodeCacheListener);
cache.start();

```

##### TreeCache

TreeCache可以看成是NodeCache和PathChildrenCache的集合，监听的是整棵数的变化，包含当前节点和子节点的变化。

```
// 监听整棵树的变化
TreeCache treeCache = new TreeCache(zkClient, getPath());
TreeCacheListener treeCacheListener = (framework, event) -> {
    System.out.println("事件类型：" + event.getType() + " | 路径：" + (null != event.getData() ? event.getData().getPath() : null));
};
treeCache.getListenable().addListener(treeCacheListener);
treeCache.start();

```

#### leader选举

这里的leader选举不是指zookeeper内部的leader选举，而是指基于zookeeper实现应用层面的leader选举，比如有一个任务，在多节点环境下只允许一个节点处理，而其他节点收到相关的任务都要交给指定的那个节点执行，这里就可以基于zookeeper选出一个节点做为leader，而其他节点则是coordinator。

选举算法开始执行后， 每个节点最终会得到一个唯一的节点作为任务leader，leader负责写操作，然后通过Zab协议实现follower的同步，leader或者follower都可以处理读操作。除此之外， 选举还经常会发生在leader意外宕机的情况下，新的leader要被选举出来。

Curator有两种leader选举的策略，分别是LeaderSelector和LeaderLatch，前者是所有存活的客户端不间断的轮流做Leader。后者是一旦选举出Leader，除非有客户端挂掉重新触发选举，否则不会交出领导权。


##### LeaderSelector
LeaderSelector核心构造方法有两个
```
public LeaderSelector(CuratorFramework client, String mutexPath,LeaderSelectorListener listener)
public LeaderSelector(CuratorFramework client, String mutexPath, ThreadFactory threadFactory, Executor executor, LeaderSelectorListener listener)

```

可以通过start()启动LeaderSelector，一旦启动，当节点取得领导权时会调用takeLeadership()。

举一个LeaderSelector的使用例子：

```
public class LeaderSelectorAdapter extends LeaderSelectorListenerAdapter implements Closeable {

    private String name;
    private LeaderSelector leaderSelector;

    public LeaderSelectorAdapter(CuratorFramework client, String path, String name){
        this.name = name;
        leaderSelector = new LeaderSelector(client, path, this);
        leaderSelector.autoRequeue();
    }

    public void start() {
        leaderSelector.start();
    }

    @Override
    public void close() throws IOException{
        leaderSelector.close();
    }

    @Override
    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
        final int waitSeconds = (int) (5 * Math.random()) + 1;
        System.out.println(name + " is now the leader. Waiting " + waitSeconds + " seconds...");
        try {
            TimeUnit.SECONDS.sleep(waitSeconds);
        } catch (InterruptedException e) {
            System.err.println(name + " was interrupted.");
            Thread.currentThread().interrupt();
        } finally {
            System.out.println(name + " release leadership.\n");
        }
    }
}

```

```
/**
 * 创建zk客户端
 */
private CuratorFramework createClient(){
    CuratorFramework client = CuratorFrameworkFactory.builder().connectString(zkAddr)
            .sessionTimeoutMs(50000)
            .connectionTimeoutMs(30000)
            .retryPolicy(new ExponentialBackoffRetry(10000, 5))
            .namespace("leaderSelector")
            .build();
    return client;
}
    
/**
 * 轮询创建leader
 */
private void initLeaderSelector() throws Exception{
    List<CuratorFramework> clients = new ArrayList<>();
    List<LeaderSelectorAdapter> leaders = new ArrayList<>();
    try {
        for (int i = 0; i < 10; i++) {
            CuratorFramework client = createClient();
            clients.add(client);
            LeaderSelectorAdapter selectorAdapter = new LeaderSelectorAdapter(client, "/node2", "client#" + i);
            leaders.add(selectorAdapter);
            client.start();
            selectorAdapter.start();
        }
        new BufferedReader(new InputStreamReader(System.in)).readLine();
    }finally {
        for (LeaderSelectorAdapter leader : leaders) {
            leader.close();
        }
        for (CuratorFramework client : clients) {
            client.close();
        }
    }
}

```

同时，LeaderLatch实例可以增加ConnectionStateListener来监听网络连接问题。当网络连接出现异常，leader不再认为自己还是leader。当连接重连后LeaderLatch会删除先前的ZNode然后重新创建一个，所以推荐使用ConnectionStateListener来处理网络抖动问题。

LeaderSelector必须小心连接状态的改变。如果实例成为leader, 它应该响应SUSPENDED或LOST。 当SUSPENDED状态出现时， 实例必须假定在重新连接成功之前它可能不再是leader了。 如果LOST状态出现， 实例不再是leader，takeLeadership方法返回。

推荐处理方式是当收到SUSPENDED或LOST时抛出CancelLeadershipException异常.。这会导致LeaderSelector实例中断并取消执行takeLeadership方法的异常。LeaderSelectorListenerAdapter.stateChanged提供了推荐的处理逻辑。


##### LeaderLatch

LeaderLatch有两个构造函数：

```
public LeaderLatch(CuratorFramework client, String latchPath)
public LeaderLatch(CuratorFramework client, String latchPath, String id)
```
可以通过start()启动LeaderLatch，一旦启动，LeaderLatch会和其它使用相同latchPath的其它LeaderLatch通信，最终只有一个会被选举为leader。hasLeadership()可以查看LeaderLatch实例是否为leader：

```
leaderLatch.hasLeadership( );  // 返回true说明当前实例是leader

```

LeaderLatch类似JDK的CountDownLatch,在请求成为leadership会block(阻塞)，一旦不使用LeaderLatch了，必须调用close方法。 如果它是leader，会释放leadership，其它的参与者则会继续选举出一个leader。

举一个LeaderLatch的使用例子：

```
/**
 * 多节点下每个节点轮询当leader
 */
private void initLeaderLatch() throws Exception {
    List<CuratorFramework> clients = new ArrayList<>();
    List<LeaderLatch> latches = new ArrayList<>();
    try {
        for (int i = 0; i < 10; i++){
            CuratorFramework client = createClient();
            clients.add(client);
            LeaderLatch latch = new LeaderLatch(client, "/node", "client#" + i);
            latch.addListener(new LeaderLatchListener() {
                @Override
                public void isLeader() {
                    System.out.println("i am leader");
                }

                @Override
                public void notLeader() {
                    System.out.println("i am not leader");
                }
            });
            latches.add(latch);
            client.start();
            latch.start();
        }
        Thread.sleep(10000);
        LeaderLatch currentLeader = null;
        for (LeaderLatch latch : latches) {
            if (latch.hasLeadership()) {
                currentLeader = latch;
            }
        }
        System.out.println("current leader is " + currentLeader.getId());
        currentLeader.close();
        Thread.sleep(10000);
        for (LeaderLatch latch : latches) {
            if (latch.hasLeadership()) {
                currentLeader = latch;
            }
        }
        System.out.println("current leader is " + currentLeader.getId());
    }finally {
        for (LeaderLatch latch : latches) {
            if (latch.getState() != null && latch.getState() != LeaderLatch.State.CLOSED){
                latch.close();
            }
        }
        for (CuratorFramework client : clients) {
            if(client != null){
                client.close();
            }
        }
    }
}

```
#### 分布式锁

##### 可重入锁

Curator实现的可重入锁跟jdk的ReentrantLock类似，即可重入，意味着同一个客户端在拥有锁的同时，可以多次获取，不会被阻塞，由类InterProcessMutex来实现。

```
//实例化锁
InterProcessMutex lock = new InterProcessMutex(zkClient, path);
try {
    lock.acquire();
    /**
     * TODO 业务逻辑
     */
}finally {
    lock.release();
}

```

##### 不可重入锁
这个锁和上面的InterProcessMutex相比，就是少了Reentrant的功能，也就意味着它不能在同一个线程中重入。这个类是InterProcessSemaphoreMutex,使用方法和InterProcessMutex类似。

```
InterProcessSemaphoreMutex lock = new InterProcessSemaphoreMutex(zkClient, path);
try {
    lock.acquire(3, TimeUnit.SECONDS);
    /**
     * TODO 业务逻辑
     */
}finally {
    lock.release();
}

```

##### 可重入读写锁

Curator实现的可重入锁类似jdk的ReentrantReadWriteLock。一个读写锁管理一对相关的锁。一个负责读操作，另外一个负责写操作。读操作在写锁没被使用时可同时由多个进程使用，而写锁在使用时不允许读(阻塞)。

此锁是可重入的。一个拥有写锁的线程可重入读锁，但是读锁却不能进入写锁。这也意味着写锁可以降级成读锁， 比如请求写锁 —>请求读锁—>释放读锁 ---->释放写锁。从读锁升级成写锁是不行的。

可重入读写锁主要由两个类实现：InterProcessReadWriteLock、InterProcessMutex。使用时首先创建一个InterProcessReadWriteLock实例，然后再根据你的需求得到读锁或者写锁，读写锁的类型是InterProcessMutex。

```
// 实例化锁
InterProcessReadWriteLock lock = new InterProcessReadWriteLock(zkClient, path);
// 获取读锁
InterProcessMutex readLock = lock.readLock();
// 获取写锁
InterProcessMutex writeLock = lock.writeLock();
try {
    // 只能先得到写锁再得到读锁，不能反过来
    if(!writeLock.acquire(3, TimeUnit.SECONDS)){
        throw new IllegalStateException("acquire writeLock err");
    }
    if(!readLock.acquire(3, TimeUnit.SECONDS)){
        throw new IllegalStateException("acquire readLock err");
    }
    /**
     * TODO 业务逻辑
     */
}finally {
    readLock.release();
    writeLock.release();
}

```

## 结尾
主要介绍了zookeeper的安装 和他的具体使用，包括用Java Curator框架的crud 分布式协调服务，分布式锁等等

- [zk](https://blog.csdn.net/lveex/article/details/111193697)
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！