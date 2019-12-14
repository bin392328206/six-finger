# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨 
> canal的入门篇，我已经带大家搭建好了canal 并用Java客服端 去订阅canal 从mysql那边拿到的binlog日志

>其实我们生产中的作用也是差不多这么玩的，只是说完善一点、，这篇带大家说说canal的一些原理。 
因为这篇是进阶，所以建议大家呢一定要按我前面的把第一个demo做出来，再看第二篇  
[🔥史上最全的分布式数据同步中间间canal之入门篇](https://juejin.im/post/5de6187b51882512727f0454)


## canal的工作原理：

![](https://user-gold-cdn.xitu.io/2019/12/4/16eceb71342cf03b?w=894&h=414&f=jpeg&s=23778)

原理相对比较简单：
- canal模拟mysql slave的交互协议，伪装自己为mysql slave，向mysql master发送dump协议
- mysql master收到dump请求，开始推送binary log给slave(也就是canal)
- canal解析binary log对象(原始为byte流)


## canal的架构
![](https://user-gold-cdn.xitu.io/2019/12/4/16eceb89218b2921?w=880&h=382&f=jpeg&s=28773)
说明：

 - server代表一个canal运行实例，对应于一个jvm
 - instance对应于一个数据队列  （1个server对应1..n个instance)
 
instance模块：

- eventParser (数据源接入，模拟slave协议和master进行交互，协议解析)
- eventSink (Parser和Store链接器，进行数据过滤，加工，分发的工作)
- eventStore (数据存储)
- metaManager (增量订阅&消费信息管理器)


## EventParser设计


![](https://user-gold-cdn.xitu.io/2019/12/4/16eced670534b062?w=1113&h=639&f=jpeg&s=44947)

- 第一步 Connection获取上一次解析成功的位置  (如果第一次启动，则获取初始指定的位置或者是当前数据库的binlog位点)
- 第二步 nnection建立链接，发送BINLOG_DUMP指令
- 第三步 Mysql开始推送Binaly Log
- 第四步 接收到的Binaly Log的通过Binlog parser进行协议解析，补充一些特定信息。
- 第五步 传递给EventSink模块进行数据存储，是一个阻塞操作，直到存储成功
- 第六步 存储成功后，定时记录Binaly Log位置


## EventSink设计

![](https://user-gold-cdn.xitu.io/2019/12/4/16ecedb105a1c0c6?w=965&h=358&f=jpeg&s=26570)

- 数据过滤：支持通配符的过滤模式，表名，字段内容等
- 数据路由/分发：解决1:n (1个parser对应多个store的模式)
- 数据归并：解决n:1 (多个parser对应1个store)
- 数据加工：在进入store之前进行额外的处理，比如join


## EventStore设计

![](https://user-gold-cdn.xitu.io/2019/12/4/16ecf1be273871a3?w=576&h=529&f=jpeg&s=17461)

- 1.  目前仅实现了Memory内存模式，后续计划增加本地file存储，mixed混合模式
- 2.  借鉴了Disruptor的RingBuffer的实现思路

定义了3个cursor

- Put :  Sink模块进行数据存储的最后一次写入位置
- Get :  数据订阅获取的最后一次提取位置
- Ack :  数据消费成功的最后一次消费位置

## 增量订阅/消费设计(还是要第一节的基础，不然很难看懂)

![](https://user-gold-cdn.xitu.io/2019/12/4/16ecf1d94a868557?w=589&h=826&f=jpeg&s=46986)
具体的协议格式，可参见：CanalProtocol.proto

get/ack/rollback协议介绍：

> Message getWithoutAck(int batchSize)，允许指定batchSize，一次可以获取多条，每次返回的对象为Message，包含的内容为：

- a. batch id 唯一标识
- b. entries 具体的数据对象，对应的数据对象格式：EntryProtocol.proto
- void rollback(long batchId)，顾命思议，回滚上次的get请求，重新获取数据。基于get获取的batchId进行提交，避免误操作
- void ack(long batchId)，顾命思议，确认已经消费成功，通知server删除数据。基于get获取的batchId进行提交，避免误操作
- 
> canal的get/ack/rollback协议和常规的jms协议有所不同，允许get/ack异步处理，比如可以连续调用get多次，后续异步按顺序提交ack/rollback，项目中称之为流式api. 

## 数据对象格式：EntryProtocol.proto

```
Entry  
    Header  
        logfileName [binlog文件名]  
        logfileOffset [binlog position]  
        executeTime [发生的变更]  
        schemaName   
        tableName  
        eventType [insert/update/delete类型]  
    entryType   [事务头BEGIN/事务尾END/数据ROWDATA]  
    storeValue  [byte数据,可展开，对应的类型为RowChange]  
      
RowChange  
    isDdl       [是否是ddl变更操作，比如create table/drop table]  
    sql     [具体的ddl sql]  
    rowDatas    [具体insert/update/delete的变更数据，可为多条，1个binlog event事件可对应多条变更，比如批处理]  
        beforeColumns [Column类型的数组]  
        afterColumns [Column类型的数组]  
          
Column   
    index         
    sqlType     [jdbc type]  
    name        [column name]  
    isKey       [是否为主键]  
    updated     [是否发生过变更]  
    isNull      [值是否为null]  
    value       [具体的内容，注意为文本]  
```
可以提供数据库变更前和变更后的字段内容，针对binlog中没有的name,isKey等信息进行补全
可以提供ddl的变更语句

## 其实canal还可以直接用mq去订阅 这样就不用再写一个Java客户端了 
具体参考：[配置Canal投递消息到RocketMQ](https://github.com/alibaba/canal/wiki/Canal-Kafka-RocketMQ-QuickStart)


## 喜欢的一句话
昨天看书听到了一句非常喜欢发话送给大家：这个是岳麓书院的一幅对联，大家有机会可以去看看
> **是非审之于己，毁誉听之于人，得失安之于数，成败归之于零**,

是是非非由自己的内心来判断，诋毁还是赞誉随别人去说，得到的与失去的都只是天定的。


## 结尾
> canal系列完结了，其实讲的不是很深，但是基本上能自己用了，如果要深入还得靠大家自己，因为我自己也还只是了解层面，感谢大家的支持，下期打算做Java基础吧 ，感觉Java基础，要讲的东西好多呀。正好大家明年面试 哈哈 

> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
