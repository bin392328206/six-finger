# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

![](https://user-gold-cdn.xitu.io/2019/11/27/16eacbde5ea100bd?w=640&h=428&f=jpeg&s=17889)
## 絮叨

> 从今天开始把**Redis**好好的从零开始好好的学一下，虽然再工作上也有用**Redis**   
> 但是对于我来说可能对Redis理解不深，刚好我们小组让我分享点啥，我就选择了redis   
> 希望每个读者看了我的redis系列都能有所收获  
> Tips ** 金刚凡境** 是我比较喜欢的一部动漫一样《少年歌行》里面武功的一种境界  
> 让我们再Redis的境界上 打怪升级吧



## 1 认识一下Redis
首先，肯定是去官网看看官方是怎么介绍Redis的啦。https://redis.io/topics/introduction

> Redis is an open source (BSD licensed), in-memory data structure store, used as a database, cache and message
broker. It supports data structures such as strings, hashes, lists, sets, sorted sets with range queries, bitmaps,
hyperloglogs, geospatial indexes with radius queries and streams. Redis has built-in replication, Lua scripting, LRU
eviction, transactions and different levels of on-disk persistence, and provides high availability via Redis
Sentinel and automatic partitioning with Redis Cluster. Learn more →

呜呜呜、对于英文不好的我只能用谷歌看中文文档了

> Redis是一个开放源代码（BSD许可）的内存中数据结构存储，用作数据库，缓存和消息代理。它支持数据结构，例如字符串，哈希，列表，集合，带范围查询的排序集合，位图，超日志，带有半径查询和流的地理空间索引。Redis具有内置的复制，**Lua脚本**，**LRU逐出**，**事务**和不同级别的**磁盘持久性**，并通过**Redis Sentinel**和**Redis Cluster**自动分区提供了高可用性。 了解更多→

> 一堆不认识的技术 不过我们应该能从上总结出一下几点：
- Redis 可以当数据库使用
- Redis 可以做缓存
- Redis 可以做消息代理

### 1.1 为什么用Redis
> 从上面可以知道，Redis是基于内存，常用作于缓存的一种技术，并且Redis存储的方式是以key-value的形式。
其实在开发过程中用的最多的还是把Redis当做缓存，因为传统的关系型数据库已经不能适用所有的场景了，比如秒杀的库存，首页商品的高访问量，都很容易把数据库弄死，

> 了解一下什么是缓存   
>缓存分为本地缓存和分布式缓存。以java为例，使用自带的map或者guava实现的是本地缓存，  
>使用redis或memcached之类的称为分布式缓存，在多实例的情况下，各实例共用一份缓存数据，缓存具有一致性。

区别：
- Java实现的Map是本地缓存，如果有多台实例(机器)的话，每个实例都需要各自保存一份缓存，缓存不具有一致性
- Redis实现的是分布式缓存，如果有多台实例(机器)的话，每个实例都共享一份缓存，缓存具有一致性。
- Java实现的Map不是专业做缓存的，JVM内存太大容易挂掉的。一般用做于容器来存储临时数据，缓存的数据随着JVM销毁而结束。Map所存储的数据结构，缓存过期机制等等是需要程序员自己手写的。
- Redis是专业做缓存的，可以用几十个G内存来做缓存。Redis一般用作于缓存，可以将缓存数据保存在硬盘中，Redis重启了后可以将其恢复。原生提供丰富的数据结构、缓存过期机制等等简单好用的功能。

> 所以说专业的事还是交给专业的人来做吧

## 2 Redis的下载安装
>  [菜鸟教程](https://www.runoob.com/redis/redis-install.html?ivk_sa=1023231z)   


## 3 Redis的基本操作Api
> 在这里我就不统一讲了，虽然我们工作都是用封装的，但是我觉得我们有必要花个**1， 2**小时跟着官方文档敲一遍  
> 重要哦，虽然博主已经在工作上用上了redis,但是博主自己写博客也是花时间敲了一遍的大部分的命令 偷懒了没有敲完）
![](https://user-gold-cdn.xitu.io/2019/11/28/16eb1c0e933578b5?w=916&h=350&f=png&s=14082)
> 下面是官方文档（中文版的并且每一个操作都有例子和时间复杂度分析 超级赞）：    
> **[Redis 命令](http://doc.redisfans.com/)**


## 4 Redis的基本数据结构
> 这边用基础数据结构和高级数据结构来系统的讲解一下（ps很多高级用法我也是现学的 嘻嘻）


### 4.1 String:
> String 类型再工作当中我们用的是比较多的，它的内部是通过 **SDS（Simple Dynamic String)**来存储的。  
> SDS 类似于 Java 中的 ArrayList，可以通过预分配冗余空间的方式来减少内存的频繁分配。当len小于IMB（1024*1024）时增加字符串分配空间大小为原来的2倍，当len大于等于1M时每次分配 额外多分配1M的空间。                                              

> 这是最简单的类型，就是普通的 set 和 get，做简单的 KV 缓存。但是很多时候我也会把对象或者map转成JSON 然后存String类型
> 其实这样给高手一看就知道不专业 所以呢 我们还是规范一点 合适的存储类型选择合适的Redis类型 慢慢养成好习惯

> 

>String的实际应用场景比较广泛的有:
- **缓存功能** 比如说缓存商品类目 缓存商品详情等 反正我们后端人员可以利用redis 配合其他关系形数据库 大大提高系统的读写能力 而且还能降低 数据库的压力
- **计数器** 统计pv uv 等 后端通过定时任务定时的把数据刷到我们的数据库 或者其他NoSql中去
- **存储用戶信息** 存儲我們后台系统的token等


### 4.2 Hash:
> 这个和我们Java中的Map很像呀 我們可以存一写结构化的对象之类的 并且可以查询和修改Hash里面的某个值  
> 就我来说自己用的也不多 这种结构

### 4.3 List:
>List 有序列表 和Java中的LinkedList列表很像 它是一个无环双向链表 可以从两边 插入 弹出 一个节点
>List的应用场景还是很多的
- 比如通过List 实现部分数据的分页。这样分页的性能会很高 
- 比如我们做活动时候 存储拉新人和被拉新人之间的关联 或者点赞等
- 消息队列 Redis的链表结构，可以轻松实现阻塞队列，可以使用左进右出的命令组成来完成队列的设计。比如：数据的生产者可以通过Lpush命令从左边插入数据，多个数据消费者，可以使用BRpop命令阻塞的“抢”列表尾部的数据


### 4.4 Set:
> 是无序集合，会自动去重的那种。和Java的Set差不多
> Set的应用场景
- 如果你需要对一些数据进行快速的全局去重，你当然也可以基于JVM内存里的HashSet进行去重，如果你要对不同JVM的数据去重？得基于Redis进行全局的 Set 去重。
- 还有什么 交集 并集 等 操作 取相同或者不同的数据


### 4.5 Sorted Set：
Sorted set 是排序的 Set，我们称为：有序集合，去重但可以排序，写进去的时候给一个分数，自动根据分数排序。
>Sorted Set的应用场景
- 我们一般用来做活动的时候 拉新人数的排行榜之类的



## 5 Redis的底层数据机构
**Tips**  其实我本来不想写这个的 原因是自己其实也不是那么懂的（学的云里雾里的） 但是为了给各位大佬看 我还是把别人的博客贴出来吧
> [8种底层实现的数据结构](https://www.cnblogs.com/neooelric/p/9621736.html)


## 6 Redis的几种高级用法
### 6.1 Bitmap
> 在开发中，可能会遇到这种情况：需要统计用户的某些信息，如活跃或不活跃，登录或者不登录；又如需要记录用户一年的打卡情况，打卡了是1， 没有打卡是0，如果使用普通的 key/value存储，则要记录365条记录，如果用户量很大，需要的空间也会很大，所以 Redis　提供了 Bitmap 位图这中数据结构，Bitmap 就是通过操作二进制位来进行记录，即为 0 和 1；如果要记录 365 天的打卡情况，使用 Bitmap 表示的形式大概如下：0101000111000111...........................，这样有什么好处呢？当然就是节约内存了，365 天相当于 365 bit，又 1 字节 = 8 bit , 所以相当于使用 46 个字节即可。当然，由于公司业务关系，我还没有遇到这种需求 ^ ^。

> 那么具体是怎么实现的呢 请看下面详解

> bitmap的命令

| 常用命令 | 作用 |
| :-----| :----: |
| 1、getbit key offset| 用于获取Redis中指定key对应的值，中对应offset的bit |
|2、setbit key key offset value | 用于修改指定key对应的值，中对应offset的bit|
|3、 bitcount key [start end] | 用于统计字符串被设置为1的bit数|
|4、bitop and/or/xor/not destkey key [key …]| 用于对多个key求逻辑与/逻辑或/逻辑异或/逻辑非|

>例1： 统计去年的活跃用户数   
> 我只要把用户的id当做offset 只要他在一年中访问过这个网站我就给这个offset 设置为 1 最后通过**bitcount ** 来统计年的访问量，同理 周 月 日 活跃都可以这样统计


>例2：统计三天内的活跃用户数  
>我们把时间作为key "191127:active" "191128:active" "191129:active" 用户的ID就可以作为offset，当用户访问过网站，就将对应offset的bit值设置为“1”；   
- 统计三天的活跃用户，通过bitop or 获取三天周内访问过的用户数量
- 连续三天访问的用户数量 bitop and

> bitmap的优势，以统计活跃用户为例 每个用户id占用空间为1bit，**消耗内存非常少，存储1亿用户量只需要12.5M**

### 6.2 HyperLogLog
> HyperLogLog 数据结构是 Redis 的高级数据结构  供不精确的去重计数功能，比较适合用来做**大规模数据的去重统计**，例如统计 UV；

>其实我们项目的总的uv pv也是我收集的 再没有学习这个之前我是怎么做的呢 pv 这个好说 设置一个key 用String类型的自增就好了 那uv 我是怎么做的呢 我是直接把uid 存到set中去 最后定时任务凌晨把他刷到数据库的 但是这个的缺点是如果访问量很大就会占用很多的空间 学完才知道自己的方式不是很好 哈哈

>Redis 提供了 HyperLogLog 数据结构就是用来解决这种统计问题的。HyperLogLog 提供不精确的去重计数方案，虽然不精确但是也不是非常不精确，标准误差是 **0.81%**，这样的精确度已经可以满足上面的 UV 统计需求了。

>HyperLogLog 提供了两个指令 pfadd 和 pfcount，根据字面意义很好理解，一个是增加计数，一个是获取计数。pfadd 用法和 set 集合的 sadd 是一样的，来一个用户 ID，就将用户 ID 塞进去就是。最后我们要把数据落地的时候用pfcount刷到数据库就行了 哈哈 是不是很简单 但是应该很少人用吧。

### 6.3 pub/sub：
> 功能是订阅发布功能，可以用作简单的消息队列。  
>这里我其实就是提一嘴而已 再真实的项目中很少用到 毕竟再技术选型上有更好的全的消息中间间 很少人会用到 但是面试的时候又可能说会问到 这边就在这提一下哈。

### 6.4 pipeline
> Pipeline指的是管道技术，指的是客户端允许将多个请求依次发给服务器，过程中而不需要等待请求的回复，在最后再一并读取结果即可。   
> redis客户端执行一条命令分4个过程： 发送命令－〉命令排队－〉命令执行－〉返回结果   

> 这个过程称为Round trip time(简称RTT, 往返时间)，redis的pipeline 可以让多个命令同时发送 只需要最后接收返回      
> 普通的请求模型是同步的，每次请求对应一次IO操作等待,而Pipeline 化之后所有的请求合并为一次IO，除了时延可以降低之外，还能大幅度提升系统吞吐量。

### 6.5 redis事务
> 刚刚介绍了Redis Pipeline，Pipeline能帮我们组装命令一次发送给Redis，但是Pipeline中的命令不具有原子性，所以如果我们需要自己组装一个原子性的操作，使用Pipeline是无法实现的，庆幸的是Redis提供了事务和支持Lua脚本来实现原子性操作。

> Redis提供了一个 multi 命令开启事务，exec 命令提交事务，在它们之间的命令是在一个事务内的，能保证原子性。

```
192.168.1.4:0>multi
"OK"
192.168.1.4:0>set tran1 hello
"QUEUED"
192.168.1.4:0>set tran2 world
"QUEUED"
192.168.1.4:0>exec
 1)  "OK"
 2)  "OK"
```
> 通过上面的命令可以看到使用 multi 命令开启事务之后，执行的Redis命令返回结果 QUEUED，表示命令并没有执行，而是暂时保存在Redis事务中，直到执行 exec 命令后才会执行上面的命令并且返回结果。

> Redis的事务做的很简单，没有像关系型数据库那样把事务的隔离级别划分的那么细，Redis在事务没提交之前不会执行事务中的命令，会等到事务提交的那一刻再执行事务中的所有命令。所以上面的案例中，如果在 exec 命令未执行之前另一个Redis客户端调用 get tran1 命令返回值会是null，因为事务没有提交之前事务中的命令还没有执行。

>redis执行过程中发生了运行时错误，Redis不仅不会回滚事务，还会跳过这个运行时错误，继续向下执行命令 他只是会返回告诉你哪个失败了，

>虽然事务能保证事务内的操作是原子性的，但是无法保证在事务开启到事务提交之间事务中的key没有被其他客户端修改。

>Redis提供了一个 watch 命令来帮我们解决上面描述的这个问题，在 multi 命令之前我们可以使用 watch 命令来”观察”一个或多个key，在事务提交之前Redis会确保被”观察”的key有没有被修改过，没有被修改过才会执行事务中的命令，如果存在key被修改过，那么整个事务中的命令都不会执行，有点类似于乐观锁的机制。下面是例子

```
@Test
public void testWatch() {
    JedisPool jedisPool = new JedisPool("192.168.1.4");
    // 设定 nowatch 的初始值为 雪月剑仙李寒衣
    Jedis jedis = jedisPool.getResource();
    // 使用 watch 命令watch "watchtest"
    jedis.watch("watchtest");
    // 开启事务
    jedis.set("watchtest", "雪月剑仙李寒衣");
    // 开启事务
    Transaction multi = jedis.multi();
    // 另一个jedis客户端对 watchtest进行append操作
    jedisPool.getResource().append("watchtest", "六脉神剑");
    // 事务内部对watchtest进行append操作
    multi.append("watchtest", " 孤剑仙洛青阳");
    // 提交事务
    multi.exec();
    // 打印watchtest对应的value
    System.out.println(jedis.get("watchtest"));
}
```
如上面的例子 他最后只会打印 雪月剑仙李寒衣 六脉神剑 最后的**孤剑仙洛青阳**并不会出现在控制台上

> 不得不说Redis事务是一个好功能，能帮我们实现一些原子性的操作，但是实际正常开发中很少遇到使用Redis事务的场景，因为Lua脚本同样可以帮我们实现Redis事务相关功能，并且功能要强大很多。


## 结尾
>redis的基础入门篇就讲到这了 对于redis的 **持久化** ，**高可用**， **哨兵**， **Lua** 。。。。。。等一大波技能 大家等下回一起打怪升级吧

> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 哈哈
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！

