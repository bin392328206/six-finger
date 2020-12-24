# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
## 叨絮
上面一节，大致的介绍了一下Hadoop，下面我们来看看他的各个组件

- [小六六学大数据之 Hadoop(一)]()

## HDFS概述
### HDFS产生背景
随着数据量越来越大，在一个操作系统管辖的范围内存不下了，那么就分配到更多的操作系统管理的磁盘中，但是不方便管理和维护，迫切需要一种系统来管理多台机器上的文件，这就是分布式文件管理系统。HDFS只是分布式文件管理系统中的一种。

### HDFS概念

HDFS，它是一个文件系统，用于存储文件，通过目录树来定位文件；其次，它是分布式的，由很多服务器联合起来实现其功能，集群中的服务器有各自的角色。HDFS的设计适合一次写入，多次读出的场景，且不支持文件的修改。适合用来做数据分析，并不适合用来做网盘应用

### HDFS优缺点

#### 优点
- 高容错性
	- 数据自动保存多个副本。它通过增加副本的形式，提高容错性；
    - 某一个副本丢失以后，它可以自动恢复
- 适合大数据处理
	- 数据规模：能够处理数据规模达到GB、TB、甚至PB级别的数据；
    - 文件规模：能够处理百万规模以上的文件数量，数量相当之大。
    
- 流式数据访问，它能保证数据的一致性。
- 可构建在廉价机器上，通过多副本机制，提高可靠性。


#### 缺点

- 不适合低延时数据访问，比如毫秒级的存储数据，是做不到的。
- 无法高效的对大量小文件进行存储。
	- 存储大量小文件的话，它会占用NameNode大量的内存来存储文件、目录和块信息。这样是不可取的，因为NameNode的内存总是有限的；
    - 小文件存储的寻址时间会超过读取时间，它违反了HDFS的设计目标。
- 并发写入、文件随机修改。
	- 一个文件只能有一个写，不允许多个线程同时写；
    - 仅支持数据append（追加），不支持文件的随机修改。
    
    
    
### HDFS组成架构

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9fb5b498df7b444087880236c63cdc3d~tplv-k3u1fbpfcp-watermark.image)


这种架构主要由四个部分组成，分别为HDFS Client、NameNode、DataNode和Secondary NameNode。下面我们分别介绍这四个组成部分。


- Client：就是客户端。
	- 文件切分。文件上传HDFS的时候，Client将文件切分成一个一个的Block，然后进行存储；
    - 与NameNode交互，获取文件的位置信息；
    - 与DataNode交互，读取或者写入数据；
    - Client提供一些命令来管理HDFS，比如启动或者关闭HDFS；
    - Client可以通过一些命令来访问HDFS；
    
- NameNode：就是Master，它是一个主管、管理者。
	- 管理HDFS的名称空间；
 	- 管理数据块（Block）映射信息；
 	- 配置副本策略；
  	- 处理客户端读写请求。
    
- DataNode：就是Slave。NameNode下达命令，DataNode执行实际的操作。
	- 存储实际的数据块
    - 执行数据块的读/写操作。
- Secondary NameNode：并非NameNode的热备。当NameNode挂掉的时候，它并不能马上替换NameNode并提供服务。
	- 辅助NameNode，分担其工作量；
 	- 定期合并Fsimage和Edits，并推送给NameNode；
 	- 在紧急情况下，可辅助恢复NameNode。
### HDFS文件块大小

HDFS中的文件在物理上是分块存储（block），块的大小可以通过配置参数( dfs.blocksize)来规定，默认大小在hadoop2.x版本中是128M，老版本中是64M。HDFS的块比磁盘的块大，其目的是为了最小化寻址开销。如果块设置得足够大，从磁盘传输数据的时间会明显大于定位这个块开始位置所需的时间。因而，传输一个由多个块组成的文件的时间取决于磁盘传输速率。如果寻址时间约为10ms，而传输速率为100MB/s，为了使寻址时间仅占传输时间的1%，我们要将块大小设置约为100MB。默认的块大小128MB。块的大小：10ms*100*100M/s = 100M

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/32ad8b86eab94a68b65c10857a3c31eb~tplv-k3u1fbpfcp-watermark.image)


### HDFS写数据流程

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c6014ed71d4c4d57ae45c7c958e02e7c~tplv-k3u1fbpfcp-watermark.image)
- 客户端通过Distributed FileSystem模块向NameNode请求上传文件，NameNode检查目标文件是否已存在，父目录是否存在。
- NameNode返回是否可以上传。
- 客户端请求第一个block上传到哪几个datanode服务器上。
- NameNode返回3个datanode节点，分别为dn1、dn2、dn3。
- 客户端通过FSDataOutputStream模块请求dn1上传数据，dn1收到请求会继续调用dn2，然后dn2调用dn3，将这个通信管道建立完成。
- dn1、dn2、dn3逐级应答客户端。
- 客户端开始往dn1上传第一个block（先从磁盘读取数据放到一个本地内存缓存），以packet为单位，dn1收到一个packet就会传给dn2，dn传给dn3；dn1每传一个packet会放入一个应答队列等待应答。
- 当一个block传输完成之后，客户端再次请求NameNode上传第二个block的服务器。（重复执行3-7步）。


### 网络拓扑概念

在本地网络中，两个节点被称为“彼此近邻”是什么意思？在海量数据处理中，其主要限制因素是节点之间数据的传输速率——带宽很稀缺。这里的想法是将两个节点间的带宽作为距离的衡量标准。

节点距离：两个节点到达最近的共同祖先的距离总和。
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c5daea537fea42d7bc72fca6edc765c6~tplv-k3u1fbpfcp-watermark.image)

### HDFS读数据流程

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b7689706f7194aafa32acdd07b9df0da~tplv-k3u1fbpfcp-watermark.image)

- 客户端通过Distributed FileSystem向NameNode请求下载文件，NameNode通过查询元数据，找到文件块所在的DataNode地址。
- 挑选一台DataNode（就近原则，然后随机）服务器，请求读取数据。
- DataNode开始传输数据给客户端（从磁盘里面读取数据输入流，以packet为单位来做校验）。
- 客户端以packet为单位接收，先在本地缓存，然后写入目标文件。



## MapReduce入门


###  MapReduce定义
Mapreduce是一个分布式运算程序的编程框架，是用户开发“基于hadoop的数据分析应用”的核心框架。Mapreduce核心功能是将用户编写的业务逻辑代码和自带默认组件整合成一个完整的分布式运算程序，并发运行在一个hadoop集群上。

### MapReduce优缺点

- 优点
  - MapReduce易于编程。它简单的实现一些接口，就可以完成一个分布式程序，这个分布式程序可以分布到大量廉价的PC机器上运行。也就是说你写一个分布式程序，跟写一个简单的串行程序是一模一样的。就是因为这个特点使得MapReduce编程变得非常流行。
  - 良好的扩展性。当你的计算资源不能得到满足的时候，你可以通过简单的增加机器来扩展它的计算能力。
  - 高容错性。MapReduce设计的初衷就是使程序能够部署在廉价的PC机器上，这就要求它具有很高的容错性。比如其中一台机器挂了，它可以把上面的计算任务转移到另外一个节点上运行，不至于这个任务运行失败，而且这个过程不需要人工参与，而完全是由Hadoop内部完成的。
  - 适合PB级以上海量数据的离线处理。这里加红字体离线处理，说明它适合离线处理而不适合在线处理。比如像毫秒级别的返回一个结果，MapReduce很难做到。

- 缺点 MapReduce不擅长做实时计算、流式计算、DAG（有向图）计算。
  - 实时计算。MapReduce无法像Mysql一样，在毫秒或者秒级内返回结果。
  - 流式计算。流式计算的输入数据是动态的，而MapReduce的输入数据集是静态的，不能动态变化。这是因为MapReduce自身的设计特点决定- 据源必须是静态的。
  - DAG（有向图）计算。多个应用程序存在依赖关系，后一个应用程序的输入为前一个的输出。在这种情况下，MapReduce并不是不能做，而是使用后，每个MapReduce作业的输出结果都会写入到磁盘，会造成大量的磁盘IO，导致性能非常的低下。
  
  
  
  ### MapReduce核心思想
  

  ![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/591d6d1a27e243f7bf5be80ff9904e1b~tplv-k3u1fbpfcp-watermark.image)
 
- 分布式的运算程序往往需要分成至少2个阶段。
- 第一个阶段的maptask并发实例，完全并行运行，互不相干。
- 第二个阶段的reduce task并发实例互不相干，但是他们的数据依赖于上一个阶段的所有maptask并发实例的输出。
- MapReduce编程模型只能包含一个map阶段和一个reduce阶段，如果用户的业务逻辑非常复杂，那就只能多个mapreduce程序，串行运行。


### MapReduce进程

一个完整的mapreduce程序在分布式运行时有三类实例进程：
- MrAppMaster：负责整个程序的过程调度及状态协调。
- MapTask：负责map阶段的整个数据处理流程。
- ReduceTask：负责reduce阶段的整个数据处理流程。

### MapReduce编程规范

用户编写的程序分成三个部分：Mapper、Reducer和Driver。

- Mapper阶段
	- 用户自定义的Mapper要继承自己的父类
	- Mapper的输入数据是KV对的形式（KV的类型可自定义）
	- Mapper中的业务逻辑写在map()方法中
	- Mapper的输出数据是KV对的形式（KV的类型可自定义）
    
- Reducer阶段
  - 用户自定义的Reducer要继承自己的父类
  - Reducer的输入数据类型对应Mapper的输出数据类型，也是KV
  - Reducer的业务逻辑写在reduce()方法中
  - Reducetask进程对每一组相同k的<k,v>组调用一次reduce()方法

- Driver阶段

整个程序需要一个Drvier来进行提交，提交的是一个描述了各种必要信息的job对象


> 其实吧我们真实开发也不会说去写mr 但是还是建议大家把最简单的wordcount做了。



## Yarn

### Yarn 概述

Yarn 是一个资源调度平台，负责为运算程序提供服务器运算资源，相当于一个分布式
的操作系统平台，而 MapReduce 等运算程序则相当于运行于操作系统之上的应用程序。


### Yarn 基本架构

YARN 主要由 ResourceManager、NodeManager、ApplicationMaster 和 Container 等组件
构成
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6c85d606218e477bbfa8cdcdb7022af4~tplv-k3u1fbpfcp-watermark.image)
### Yarn 工作机制

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ac47a79a89a142e3b7fe9a09df062d77~tplv-k3u1fbpfcp-watermark.image)

- Mr 程序提交到客户端所在的节点。
- Yarnrunner 向 Resourcemanager 申请一个 Application。 
- rm 将该应用程序的资源路径返回给 yarnrunner。 
- 该程序将运行所需资源提交到 HDFS 上。 
- 程序资源提交完毕后，申请运行 mrAppMaster。 
- RM 将用户的请求初始化成一个 task。 
- 其中一个 NodeManager 领取到 task 任务。
- 该 NodeManager 创建容器 Container，并产生 MRAppmaster。 
- Container 从 HDFS 上拷贝资源到本地。 
- MRAppmaster 向 RM 申请运行 maptask 资源。
- RM 将运行 maptask 任务分配给另外两个 NodeManager，另两个 NodeManager 分别领取任务并创建容器。
- MR 向两个接收到任务的 NodeManager 发送程序启动脚本，这两个 NodeManager分别启动 maptask，maptask 对数据分区排序。
- MrAppMaster 等待所有 maptask 运行完毕后，向 RM 申请容器，运行 reduce task。 
- reduce task 向 maptask 获取相应分区的数据。
- 程序运行完毕后，MR 会向 RM 申请注销自己。


## 结尾
走马观花，大家先了解了解哈。。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！