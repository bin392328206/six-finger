
# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在** 

## Tips
面试指南系列，很多情况下不会去深挖细节，是小六六以被面试者的角色去回顾知识的一种方式，所以我默认大部分的东西，作为面试官的你，肯定是懂的。
> https://www.processon.com/view/link/600ed9e9637689349038b0e4

上面的是脑图地址
## 叨絮
今天来看看Elasticsearch
然后下面是前面的文章汇总
 - [2021-Java后端工程师面试指南-(引言)](https://juejin.cn/post/6921868116481982477)
 - [2021-Java后端工程师面试指南-(Java基础篇）](https://juejin.cn/post/6924472756046135304)
 - [2021-Java后端工程师面试指南-(并发-多线程)](https://juejin.cn/post/6925213973205745672)
 - [2021-Java后端工程师面试指南-(JVM）](https://juejin.cn/post/6926349737394176014) 
 - [2021-Java后端工程师面试指南-(MySQL）](https://juejin.cn/post/6927105516485214216)
 - [2021-Java后端工程师面试指南-(Redis）](https://juejin.cn/post/6930816506489995272)
 
 Es其实用的很多，而且如果体量大点的话，基本上都需要使用到它，所以掌握它还是很有必要的，那么我们来一起看看吧
 
 
 ### 说说什么是Elasticsearch
- Elasticsearch，基于lucene.分布式的Restful实时搜索和分析引擎(实时)
- 分布式的实时文件存储,每个字段都被索引并可被搜索
- 高扩展性,可扩展至上百台服务器,处理PB级结构化或非结构化数据
- Elasticsearch用于全文检索,结构化搜索,分析/合并使用

### 聊聊Elasticsearch的特性:
- Elasticsearch没有典型意义的事务(无事务性)
- Elasticsearch是一种面向文档的数据库
- Elasticsearch没有提供授权和认证特性

### 什么是全文检索和Lucene？
> 全文检索，倒排索引

全文检索是指计算机索引程序通过扫描文章中的每一个词，对每一个词建立一个索引，指明该词在文章中出现的次数和位置，当用户查询时，检索程序就根据事先建立的索引进行查找，并将查找的结果反馈给用户的检索方式。这个过程类似于通过字典中的检索字表查字的过程。全文搜索搜索引擎数据库中的数据。

> lucene
lucene，就是一个jar包，里面包含了封装好的各种建立倒排索引，以及进行搜索的代码，包括各种算法。我们就用java开发的时候，引入lucene jar，然后基于lucene的api进行去进行开发就可以了。

### 那你聊聊Elasticsearch的核心概念，就是我们经常用的那些。

> 近实时
近实时，两个意思，从写入数据到数据可以被搜索到有一个小延迟（大概1秒）；基于es执行搜索和分析可以达到秒级。

>Cluster（集群）
集群包含多个节点，每个节点属于哪个集群是通过一个配置（集群名称，默认是elasticsearch）来决定的，对于中小型应用来说，刚开始一个集群就一个节点很正常

> Node（节点）

集群中的一个节点，节点也有一个名称（默认是随机分配的），节点名称很重要（在执行运维管理操作的时候），默认节点会去加入一个名称为“elasticsearch”的集群，如果直接启动一堆节点，那么它们会自动组成一个elasticsearch集群，当然一个节点也可以组成一个elasticsearch集群。

> Index（索引-数据库）
索引包含一堆有相似结构的文档数据，比如可以有一个客户索引，商品分类索引，订单索引，索引有一个名称。一个index包含很多document，一个index就代表了一类类似的或者相同的document。比如说建立一个product     index，商品索引，里面可能就存放了所有的商品数据，所有的商品document。


> Type（类型-表）
每个索引里都可以有一个或多个type，type是index中的一个逻辑数据分类，一个type下的document，都有相同的field，比如博客系统，有一个索引，可以定义用户数据type，博客数据type，评论数据type。

> Document（文档-行）
文档是es中的最小数据单元，一个document可以是一条客户数据，一条商品分类数据，一条订单数据，通常用JSON数据结构表示，每个index下的type中，都可以去存储多个document。

> Field（字段-列）
Field是Elasticsearch的最小单位。一个document里面有多个field，每个field就是一个数据字段。

> shard
单台机器无法存储大量数据，es可以将一个索引中的数据切分为多个shard，分布在多台服务器上存储。有了shard就可以横向扩展，存储更多数据，让搜索和分析等操作分布到多台服务器上去执行，提升吞吐量和性能。每个shard都是一个lucene index。

>replica
任何一个服务器随时可能故障或宕机，此时shard可能就会丢失，因此可以为每个shard创建多个replica副本。replica可以在shard故障时提供备用服务，保证数据不丢失，多个replica还可以提升搜索操作的吞吐量和性能。primary shard（建立索引时一次设置，不能修改，默认5个），replica shard（随时修改数量，默认1个），默认每个索引10个shard，5个primary shard，5个replica shard，最小的高可用配置，是2台服务器。

### 说说Elasticsearch乐观并发控制
Elasticsearch是分布式的。当文档被创建、更新或删除，文档的新版本会被复制到集群的其它节点。Elasticsearch即是同步的又是异步的，意思是这些复制请求都是平行发送的，并无序(out of sequence)的到达目的地。这就需要一种方法确保老版本的文档永远不会覆盖新的版本。
上文我们提到index、get、delete请求时，我们指出每个文档都有一个_version号码，这个号码在文档被改变时加一。Elasticsearch使用这个_version保证所有修改都被正确排序。当一个旧版本出现在新版本之后，它会被简单的忽略。
我们利用_version的这一优点确保数据不会因为修改冲突而丢失。我们可以指定文档的version来做想要的更改。如果那个版本号不是现在的，我们的请求就失败了。
用version 来保证并发的顺序一致性

### 聊聊text,keyword类型的区别
- text：当一个字段是要被全文搜索的，比如Email内容、产品描述，应该使用text类型。设置text类型以后，字段内容会被分析，在生成倒排索引以前，字符串会被分析器分成一个一个词项。text类型的字段不用于排序，很少用于聚合。
- keyword：keyword类型适用于索引结构化的字段，比如email地址、主机名、状态码和标签。如果字段需要进行过滤(比如查找已发布博客中status属性为published的文章)、排序、聚合。keyword类型的字段只能通过精确值搜索到。

### 那你说说查询api返回的主要包含什么东西
> hits
响应中最重要的部分是hits，它包含了total字段来表示匹配到的文档总数，hits数组还包含了匹配到的前10条数据。
hits数组中的每个结果都包含_index、_type和文档的_id字段，被加入到_source字段中这意味着在搜索结果中我们将可以直接使用全部文档。这不像其他搜索引擎只返回文档ID，需要你单独去获取文档。
每个节点都有一个_score字段，这是相关性得分(relevance score)，它衡量了文档与查询的匹配程度。默认的，返回的结果中关联性最大的文档排在首位；这意味着，它是按照_score降序排列的。这种情况下，我们没有指定任何查询，所以所有文档的相关性是一样的，因此所有结果的_score都是取得一个中间值1
max_score指的是所有文档匹配查询中_score的最大值。

> took
took告诉我们整个搜索请求花费的毫秒数。

> shards
_shards节点告诉我们参与查询的分片数（total字段），有多少是成功的（successful字段），有多少的是失败的（failed字段）。通常我们不希望分片失败，不过这个有可能发生。如果我们遭受一些重大的故障导致主分片和复制分片都故障，那这个分片的数据将无法响应给搜索请求。这种情况下，Elasticsearch将报告分片failed，但仍将继续返回剩余分片上的结果。

> timeout

time_out值告诉我们查询超时与否。一般的，搜索请求不会超时。如果响应速度比完整的结果更重要，你可以定义timeout参数为10或者10ms（10毫秒），或者1s（1秒）

### 聊聊shard&replica机制

- index包含多个shard
- 每个shard都是一个最小工作单元，承载部分数据，lucene实例，完整的建立索引和处理请求的能力
- 增减节点时，shard会自动在nodes中负载均衡
- primary shard和replica shard，每个document肯定只存在于某一个primary shard以及其对应的replica shard中，不可能存在于多个primary shard
- replica shard是primary shard的副本，负责容错，以及承担读请求负载
- primary shard的数量在创建索引的时候就固定了，replica shard的数量可以随时修改
- primary shard的默认数量是5，replica默认是1，默认有10个shard，5个primary shard，5个replica shard
- primary shard不能和自己的replica shard放在同一个节点上（否则节点宕机，primary shard和副本都丢失，起不到容错的作用），但是可以和其他primary shard的replica shard放在同一个节点上

### ES是如何实现master选举的？

前置条件：
- 只有是候选主节点（master：true）的节点才能成为主节点。
- 最小主节点数（min_master_nodes）的目的是防止脑裂。

Elasticsearch 的选主是 ZenDiscovery 模块负责的，主要包含 Ping（节点之间通过这个RPC来发现彼此）和 Unicast（单播模块包含一个主机列表以控制哪些节点需要 ping 通）这两部分；
获取主节点的核心入口为 findMaster，选择主节点成功返回对应 Master，否则返回 null。

选举流程大致描述如下：
- 第一步：确认候选主节点数达标，elasticsearch.yml 设置的值 discovery.zen.minimum_master_nodes;
- 第二步：对所有候选主节点根据nodeId字典排序，每次选举每个节点都把自己所知道节点排一次序，然后选出第一个（第0位）节点，暂且认为它是master节点。
- 第三步：如果对某个节点的投票数达到一定的值（候选主节点数n/2+1）并且该节点自己也选举自己，那这个节点就是master。否则重新选举一直到满足上述条件。

### 如何解决ES集群的脑裂问题
所谓集群脑裂，是指 Elasticsearch 集群中的节点（比如共 20 个），其中的 10 个选了一个 master，另外 10 个选了另一个 master 的情况。

当集群 master 候选数量不小于 3 个时，可以通过设置最少投票通过数量（discovery.zen.minimum_master_nodes）超过所有候选节点一半以上来解决脑裂问题；
当候选数量为两个时，只能修改为唯一的一个 master 候选，其他作为 data 节点，避免脑裂问题。


### 聊聊es的写入流程

Elasticsearch采用多Shard方式，通过配置routing规则将数据分成多个数据子集，每个数据子集提供独立的索引和搜索功能。当写入文档的时候，根据routing规则，将文档发送给特定Shard中建立索引。这样就能实现分布式了。

每个Index由多个Shard组成(默认是5个)，每个Shard有一个主节点和多个副本节点，副本个数可配。但每次写入的时候，写入请求会先根据_routing规则选择发给哪个Shard，Index Request中可以设置使用哪个Filed的值作为路由参数，如果没有设置，则使用Mapping中的配置，如果mapping中也没有配置，则使用_id作为路由参数，然后通过_routing的Hash值选择出Shard（在OperationRouting类中），最后从集群的Meta中找出出该Shard的Primary节点。

请求接着会发送给Primary Shard，在Primary Shard上执行成功后，再从Primary Shard上将请求同时发送给多个Replica Shard，请求在多个Replica Shard上执行成功并返回给Primary Shard后，写入请求执行成功，返回结果给客户端。

### 那你说说具体在 shard上的写入流程呗
在每一个Shard中，写入流程分为两部分，先写入Lucene，再写入TransLog。

写入请求到达Shard后，先写Lucene文件，创建好索引，此时索引还在内存里面，接着去写TransLog，写完TransLog后，刷新TransLog数据到磁盘上，写磁盘成功后，请求返回给用户。这里有几个关键点：


和数据库不同，数据库是先写CommitLog，然后再写内存，而Elasticsearch是先写内存，最后才写TransLog，一种可能的原因是Lucene的内存写入会有很复杂的逻辑，很容易失败，比如分词，字段长度超过限制等，比较重，为了避免TransLog中有大量无效记录，减少recover的复杂度和提高速度，所以就把写Lucene放在了最前面。

写Lucene内存后，并不是可被搜索的，需要通过Refresh把内存的对象转成完整的Segment后，然后再次reopen后才能被搜索，一般这个时间设置为1秒钟，导致写入Elasticsearch的文档，最快要1秒钟才可被从搜索到，所以Elasticsearch在搜索方面是NRT（Near Real Time）近实时的系统。

每隔一段比较长的时间，比如30分钟后，Lucene会把内存中生成的新Segment刷新到磁盘上，刷新后索引文件已经持久化了，历史的TransLog就没用了，会清空掉旧的TransLog。

Lucene缓存中的数据默认1秒之后才生成segment文件，即使是生成了segment文件，这个segment是写到页面缓存中的，并不是实时的写到磁盘，只有达到一定时间或者达到一定的量才会强制flush磁盘。如果这期间机器宕掉，内存中的数据就丢了。如果发生这种情况，内存中的数据是可以从TransLog中进行恢复的，TransLog默认是每5秒都会刷新一次磁盘。但这依然不能保证数据安全，因为仍然有可能最多丢失TransLog中5秒的数据。这里可以通过配置增加TransLog刷磁盘的频率来增加数据可靠性，最小可配置100ms，但不建议这么做，因为这会对性能有非常大的影响。一般情况下，Elasticsearch是通过副本机制来解决这一问题的。即使主分片所在节点宕机，丢失了5秒数据，依然是可以通过副本来进行恢复的。

总结一下，数据先写入内存 buffer，然后每隔 1s，将数据 refresh 到 os cache，到了 os cache 数据就能被搜索到（所以我们才说 es 从写入到能被搜索到，中间有 1s 的延迟）。每隔 5s，将数据写入 translog 文件（这样如果机器宕机，内存数据全没，最多会有 5s 的数据丢失），translog 大到一定程度，或者默认每隔 30mins，会触发 commit 操作，将缓冲区的数据都 flush 到 segment file 磁盘文件中。

### 说说es的更新流程吧

Lucene中不支持部分字段的Update，所以需要在Elasticsearch中实现该功能，具体流程如下：

- 到Update请求后，从Segment或者TransLog中读取同id的完整Doc，记录版本号为V1。
- 将版本V1的全量Doc和请求中的部分字段Doc合并为一个完整的Doc，同时更新内存中的VersionMap。获取到完整Doc后，Update请求就变成了Index请求。
- 加锁。
- 再次从versionMap中读取该id的最大版本号V2，如果versionMap中没有，则从Segment或者TransLog中读取，这里基本都会从versionMap中获取到。
- 检查版本是否冲突(V1==V2)，如果冲突，则回退到开始的“Update doc”阶段，重新执行。如果不冲突，则执行最新的Add请求。
- 在Index Doc阶段，首先将Version + 1得到V3，再将Doc加入到Lucene中去，Lucene中会先删同id下的已存在doc id，然后再增加新Doc。写入Lucene成功后，将当前V3更新到versionMap中。
- 释放锁，部分更新的流程就结束了

### 详细描述一下ES搜索的过程？
搜索被执行成一个两阶段过程，即 Query Then Fetch；
> Query阶段：
查询会广播到索引中每一个分片拷贝（主分片或者副本分片）。每个分片在本地执行搜索并构建一个匹配文档的大小为 from + size 的优先队列。PS：在搜索的时候是会查询Filesystem Cache的，但是有部分数据还在Memory Buffer，所以搜索是近实时的。
每个分片返回各自优先队列中 所有文档的 ID 和排序值 给协调节点，它合并这些值到自己的优先队列中来产生一个全局排序后的结果列表。
> Fetch阶段：
协调节点辨别出哪些文档需要被取回并向相关的分片提交多个 GET 请求。每个分片加载并 丰富 文档，如果有需要的话，接着返回文档给协调节点。一旦所有的文档都被取回了，协调节点返回结果给客户端。

### 说说es的写一致性
我们在发送任何一个增删改操作的时候，比如说put /index/type/id，都可以带上一个consistency参数，指明我们想要的写一致性是什么？
put /index/type/id?consistency=quorum

- one：要求我们这个写操作，只要有一个primary shard是active活跃可用的，就可以执行
- all：要求我们这个写操作，必须所有的primary shard和replica shard都是活跃的，才可以执行这个写操作
- quorum：默认的值，要求所有的shard中，必须是大部分的shard都是活跃的，可用的，才可以执行这个写操作

### 聊聊 elasticsearch 深度分页以及scroll 滚动搜索

> 深度分页
深度分页其实就是搜索的深浅度，比如第1页，第2页，第10页，第20页，是比较浅的；第10000页，第20000页就是很深了。
搜索得太深，就会造成性能问题，会耗费内存和占用cpu。而且es为了性能，他不支持超过一万条数据以上的分页查询。那么如何解决深度分页带来的问题，我们应该避免深度分页操作（限制分页页数），比如最多只能提供100页的展示，从第101页开始就没了，毕竟用户也不会搜的那么深，我们平时搜索淘宝或者京东也就看个10来页就顶多了。

> 滚动搜索
一次性查询1万+数据，往往会造成性能影响，因为数据量太多了。这个时候可以使用滚动搜索，也就是 scroll 。
滚动搜索可以先查询出一些数据，然后再紧接着依次往下查询。在第一次查询的时候会有一个滚动id，相当于一个锚标记 ，随后再次滚动搜索会需要上一次搜索滚动id，根据这个进行下一次的搜索请求。每次搜索都是基于一个历史的数据快照，查询数据的期间，如果有数据变更，那么和搜索是没有关系的。


### es在数据量很大的情况下如何提高性能
> filesystem

es每次走fileSystem cache查询速度是最快的
所以将每个查询的数据50% 容量
= fileSystem cache 容量。

> 数据预热
数据预热是指，每隔一段时间，将热数据
手动在后台查询一遍，将热数据刷新到fileSystem cache上

> 冷热分离
类似于MySQL的分表分库
将热数据单独建立一个索引 分配3台机器只保持热机器的索引
另外的机器保持冷数据的索引，但有一个问题，就是事先必须知道哪些是热数据 哪些是冷数据

> 不可以深度分页

跟产品经理说，你系统不允许翻那么深的页，默认翻的越深，性能就越差。

类似于 app 里的推荐商品不断下拉出来一页一页的
类似于微博中，下拉刷微博，刷出来一页一页的，你可以用 scroll api

## 结束
es可能我自己用的也比较少，就用来做一些搜索，没有用来做bi，所以呢？也不是那么深入吧，希望对大家有帮助，接下来复习下队列
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。 

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>微信 搜 "六脉神剑的程序人生" 回复888 有我找的许多的资料送给大家 