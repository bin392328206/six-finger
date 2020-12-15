# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
## 叨絮
最近开始写文章了，前段时间摸鱼阿里云的大数据技术栈，把基本上的操作会了点点了，然后接下来打算按正常的大数据技术栈一个个技术学一遍，说实话，这一遍下来，可能并不一定能学到很多东西，但是以后你用到的时候再来学习的学习速度一定会快很多，这也是我为啥要过一遍这个技术栈的原因，在阿里云上跟着他的demo做，其实很快就能知道一些操作应该怎么做，但是如果不把这些技术栈的原理过一遍的话，总的来说还是有点虚，把原理过一遍并不是说小六六久掌握了原理，但是我相信你知道了所以然，至少就不会那么迷了，哈哈，今天我们从zookeeper开始，因为这个技术栈其实在业务开发，中间件开发，都用的很多，不单单是大数据开发，所以用他来过渡，最好不过了，接下来几篇就都跟大家一起来学习zk吧！

## 概述

Zookeeper是一个开源的分布式的，为分布式应用提供协调服务的Apache项目。Zookeeper从设计模式角度来理解：是一个基于观察者模式设计的分布式服务管理框架，它负责存储和管理大家都关心的数据，然后接受观察者的注册，一旦这些数据的状态发生变化，Zookeeper就将负责通知已经在Zookeeper上注册的那些观察者做出相应的反应，从而实现集群中类似Master/Slave管理模式

> Zookeeper=文件系统+通知机制

### 特点

- Zookeeper：一个领导者（leader），多个跟随者（follower）组成的集群。
- Leader负责进行投票的发起和决议，更新系统状态
- Follower用于接收客户请求并向客户端返回结果，在选举Leader过程中参与投票
- 集群中只要有半数以上节点存活，Zookeeper集群就能正常服务。
- 全局数据一致：每个server保存一份相同的数据副本，client无论连接到哪个server，数据都是一致的。
- 更新请求顺序进行，来自同一个client的更新请求按其发送顺序依次执行。
- 数据更新原子性，一次数据更新要么成功，要么失败。
- 实时性，在一定时间范围内，client能读到最新数据。

### 数据结构

ZooKeeper数据模型的结构与Unix文件系统很类似，整体上可以看作是一棵树，每个节点称做一个ZNode。
很显然zookeeper集群自身维护了一套数据结构。这个存储结构是一个树形结构，其上的每一个节点，我们称之为"znode"，每一个znode默认能够存储1MB的数据，每个ZNode都可以通过其路径唯一标识
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/20f890e5ed674e81bc9f1fd2650e1c1b~tplv-k3u1fbpfcp-watermark.image)


#### Znode 包含哪些元素
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/56fa499d29af4578a6407cb53ab82ba0~tplv-k3u1fbpfcp-watermark.image)

- data：Znode 存储的数据信息。
- ACL：记录 Znode 的访问权限，即哪些人或哪些 IP 可以访问本节点。
- stat：包含 Znode 的各种元数据，比如事务 ID、版本号、时间戳、大小等等。
- child：当前节点的子节点引用
这里需要注意一点，Zookeeper 是为读多写少的场景所设计。Znode 并不是用来存储大规模业务数据，而是用于存储少量的状态和配置信息，每个节点的数据最大不能超过 1MB。


### Zookeeper 的事件通知
我们可以把 Watch 理解成是注册在特定 Znode 上的触发器。当这个 Znode 发生改变，也就是调用了 create，delete，setData 方法的时候，将会触发 Znode 上注册的对应事件，请求 Watch 的客户端会接收到异步通知。

具体交互过程如下：
- 客户端调用 getData 方法，watch 参数是 true。服务端接到请求，返回节点数据，并且在对应的哈希表里插入被 Watch 的 Znode 路径，以及 Watcher 列表。
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/70f6149aa30243c8bf1eaeba3638cb13~tplv-k3u1fbpfcp-watermark.image)

- 当被 Watch 的 Znode 已删除，服务端会查找哈希表，找到该 Znode 对应的所有 Watcher，异步通知客户端，并且删除哈希表中对应的 Key-Value。
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b831a045a0a64ca693529b553f32a425~tplv-k3u1fbpfcp-watermark.image)


### Zookeeper 崩溃恢复和数据同步
Zookeeper 身为分布式系统协调服务，如果自身挂了如何处理呢？为了防止单机挂掉的情况，Zookeeper 维护了一个集群。如下图：
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/919ce3da98c74369a51a9446fe87f8a9~tplv-k3u1fbpfcp-watermark.image)

其实就是Leader 挂了之后怎么办，我们zk的选举

- Zookeeper Service 集群是一主多从结构。
- 在更新数据时，首先更新到主节点（这里的节点是指服务器，不是 Znode），再同步到从节点。
- 在读取数据时，直接读取任意从节点。
- 为了保证主从节点的数据一致性，Zookeeper 采用了 ZAB 协议，这种协议非常类似于一致性算法 Paxos 和 Raft。

### 什么是 ZAB

Zookeeper Atomic Broadcast，有效解决了 Zookeeper 集群崩溃恢复，以及主从同步数据的问题。

#### ZAB 协议定义的三种节点状态

- Looking ：选举状态。
- Following ：Follower 节点（从节点）所处的状态。
- Leading ：Leader 节点（主节点）所处状态。
#### 最大 ZXID
最大 ZXID 也就是节点本地的最新事务编号，包含 epoch 和计数两部分。epoch 是纪元的意思，相当于 Raft 算法选主时候的 term。

#### ZAB 的崩溃恢复

假如 Zookeeper 当前的主节点挂掉了，集群会进行崩溃恢复。ZAB 的崩溃恢复分成三个阶段：
> Leader election

选举阶段，此时集群中的节点处于 Looking 状态。它们会各自向其他节点发起投票，投票当中包含自己的服务器 ID 和最新事务 ID（ZXID）。

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1f853e2d89984ad9a781a8465499e83b~tplv-k3u1fbpfcp-watermark.image)

接下来，节点会用自身的 ZXID 和从其他节点接收到的 ZXID 做比较，如果发现别人家的 ZXID 比自己大，也就是数据比自己新，那么就重新发起投票，投票给目前已知最大的 ZXID 所属节点。

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a9d191c621c14adf84edf7044863e958~tplv-k3u1fbpfcp-watermark.image)

每次投票后，服务器都会统计投票数量，判断是否有某个节点得到半数以上的投票。如果存在这样的节点，该节点将会成为准 Leader，状态变为 Leading。其他节点的状态变为 Following。

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1b1303637da946b09a4ada0ea2925ca9~tplv-k3u1fbpfcp-watermark.image)

#### Discovery

发现阶段，用于在从节点中发现最新的 ZXID 和事务日志。或许有人会问：既然 Leader 被选为主节点，已经是集群里数据最新的了，为什么还要从节点中寻找最新事务呢？

这是为了防止某些意外情况，比如因网络原因在上一阶段产生多个 Leader 的情况。

所以这一阶段，Leader 集思广益，接收所有 Follower 发来各自的最新 epoch 值。Leader 从中选出最大的 epoch，基于此值加 1，生成新的 epoch 分发给各个 Follower。

各个 Follower 收到全新的 epoch 后，返回 ACK 给 Leader，带上各自最大的 ZXID 和历史事务日志。Leader 选出最大的 ZXID，并更新自身历史日志。


#### Synchronization

同步阶段，把 Leader 刚才收集得到的最新历史事务日志，同步给集群中所有的 Follower。只有当半数 Follower 同步成功，这个准 Leader 才能成为正式的 Leader。

自此，故障恢复正式完成。

### ZAB 的数据写入
#### Broadcast


ZAB 的数据写入涉及到 Broadcast 阶段，简单来说，就是 Zookeeper 常规情况下更新数据的时候，由 Leader 广播到所有的 Follower。其过程如下：

- 客户端发出写入数据请求给任意 Follower。
- Follower 把写入数据请求转发给 Leader。
- Leader 采用二阶段提交方式，先发送 Propose 广播给 Follower。
- Follower 接到 Propose 消息，写入日志成功后，返回 ACK 消息给 Leader。
- Leader 接到半数以上ACK消息，返回成功给客户端，并且广播 Commit 请求给 Follower
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2db15bc1ca4147cf93dc6a7f443616b0~tplv-k3u1fbpfcp-watermark.image)

ZAB 协议既不是强一致性，也不是弱一致性，而是处于两者之间的单调一致性（顺序一致性）。它依靠事务 ID 和版本号，保证了数据的更新和读取是有序的。


### Zookeeper 内部原理

#### 选举机制说明

- 半数机制（Paxos 协议）：集群中半数以上机器存活，集群可用。所以 zookeeper适合装在奇数台机器上。
- Zookeeper 虽然在配置文件中并没有指定 master 和 slave。但是，zookeeper 工作时，是有一个节点为 leader，其他则为 follower，Leader 是通过内部的选举机制临时产生的
- 以一个简单的例子来说明整个选举的过程。

假设有五台服务器组成的 zookeeper 集群，它们的 id 从 1-5，同时它们都是最新启动的，
也就是没有历史数据，在存放数据量这一点上，都是一样的。假设这些服务器依序启动，来
看看会发生什么。

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/13995ac1fe3f475b990f9a8ec383fcd2~tplv-k3u1fbpfcp-watermark.image)

- 服务器 1 启动，此时只有它一台服务器启动了，它发出去的报没有任何响应，所以它的选举状态一直是 LOOKING 状态。
- 服务器 2 启动，它与最开始启动的服务器 1 进行通信，互相交换自己的选举结果，由于两者都没有历史数据，所以 id 值较大的服务器 2 胜出，但是由于没有达到超过半数以上的服务器都同意选举它(这个例子中的半数以上是 3)，所以服务器 1、2 还是继续保持LOOKING 状态。
- 服务器 3 启动，根据前面的理论分析，服务器 3 成为服务器 1、2、3 中的老大，而与上面不同的是，此时有三台服务器选举了它，所以它成为了这次选举的 leader
- 服务器 4 启动，根据前面的分析，理论上服务器 4 应该是服务器 1、2、3、4 中最大的，但是由于前面已经有半数以上的服务器选举了服务器 3，所以它只能接收当小弟的命了。
- 服务器 5 启动，同 4 一样当小弟。


## 结尾
这篇主要是介绍了一下zookeeper的概要，和他为啥能做分布式协调服务的原因
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！