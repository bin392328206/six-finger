# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
## 叨絮
到目前为止，我们已经大致的了解了 zookeeper Hadoop Hive 今天我们来看看Hbase


## HBase简介

### 什么是HBase
HBASE是一个高可靠性、高性能、面向列、可伸缩的分布式存储系统，利用HBASE技术可在廉价PC Server上搭建起大规模结构化存储集群。HBASE的目标是存储并处理大型的数据，更具体来说是仅需使用普通的硬件配置，就能够处理由成千上万的行和列所组成的大型数据。HBASE是Google Bigtable的开源实现，但是也有很多不同之处。比如：Google Bigtable利用GFS作为其文件存储系统，HBASE利用Hadoop HDFS作为其文件存储系统；Google运行MAPREDUCE来处理Bigtable中的海量数据，HBASE同样利用Hadoop MapReduce来处理HBASE中的海量数据；Google Bigtable利用Chubby作为协同服务，HBASE利用Zookeeper作为对应。

### HBase中的角色

#### HMaster
功能：
- 监控RegionServer
- 处理RegionServer故障转移
- 处理元数据的变更
- 处理region的分配或移除
- 在空闲时间进行数据的负载均衡
- 通过Zookeeper发布自己的位置给客户端

#### RegionServer
功能：
-  负责存储HBase的实际数据
-  处理分配给它的Region
-  刷新缓存到HDFS
-  维护HLog
-  执行压缩
-  负责处理Region分片


##### 其他组件
- Write-Ahead logs
HBase的修改记录，当对HBase读写数据的时候，数据不是直接写进磁盘，它会在内存中保留一段时间（时间以及数据量阈值可以设定）。但把数据保存在内存中可能有更高的概率引起数据丢失，为了解决这个问题，数据会先写在一个叫做Write-Ahead logfile的文件中，然后再写入内存中。所以在系统出现故障的时候，数据可以通过这个日志文件重建。
- HFile
这是在磁盘上保存原始数据的实际的物理文件，是实际的存储文件。
- Store
HFile存储在Store中，一个Store对应HBase表中的一个列族。
- MemStore
顾名思义，就是内存存储，位于内存中，用来保存当前的数据操作，所以当数据保存在WAL中之后，RegsionServer会在内存中存储键值对。
- Region
Hbase表的分片，HBase表会根据RowKey值被切分成不同的region存储在RegionServer中，在一个RegionServer中可以有多个不同的region。


### HBase架构
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/bfa9b3afd8f844d0b938825b77e085d2~tplv-k3u1fbpfcp-watermark.image)

## HBase安装

- [HBase学习之路 （二）HBase集群安装](https://www.cnblogs.com/qingyunzong/p/8668880.html)


## HBase数据结构

### Row Key

与nosql数据库们一样,row key是用来检索记录的主键。访问HBASE table中的行，只有三种方式：
- 通过单个row key访问
- 通过row key的range（正则）
- 全表扫描Row key行键(Row key)可以是任意字符串(最大长度是64KB，实际应用中长度一般为10-100bytes)，在HBASE内部，row key保存为字节数组。存储时，数据按照Row key的字典序(byte order)排序存储。设计key时，要充分排序存储这个特性，将经常一起读取的行存储放到一起。(位置相关性)
### Columns Family
列族：HBASE表中的每个列，都归属于某个列族。列族是表的schema的一部分(而列不是)，必须在使用表之前定义。列名都以列族作为前缀。例如courses:history，courses:math都属于courses 这个列族。

### Cell

由{row key, columnFamily, version} 唯一确定的单元。cell中的数据是没有类型的，全部是字节码形式存贮。关键字：无类型、字节码

### Time Stamp
HBASE 中通过rowkey和columns确定的为一个存贮单元称为cell。每个cell都保存着同一份数据的多个版本。版本通过时间戳来索引。时间戳的类型是64位整型。时间戳可以由HBASE(在数据写入时自动)赋值，此时时间戳是精确到毫秒的当前系统时间。时间戳也可以由客户显式赋值。如果应用程序要避免数据版本冲突，就必须自己生成具有唯一性的时间戳。每个cell中，不同版本的数据按照时间倒序排序，即最新的数据排在最前面。为了避免数据存在过多版本造成的的管理(包括存贮和索引)负担，HBASE提供了两种数据版本回收方式。一是保存数据的最后n个版本，二是保存最近一段时间内的版本（比如最近七天）。用户可以针对每个列族进行设置。


## HBase原理

### 写流程
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/bf7e99f7965d4f3b9b4e54961088cc1a~tplv-k3u1fbpfcp-watermark.image)
- Client向HregionServer发送写请求；
- HregionServer将数据写到HLog（write ahead log）。为了数据的持久化和恢复；
- HregionServer将数据写到内存（MemStore）；
- 反馈Client写成功。

### 数据flush过程

- 当MemStore数据达到阈值（默认是128M，老版本是64M），将数据刷到硬盘，将内存中的数据删除，同时删除HLog中的历史数据；
- 并将数据存储到HDFS中；
- 在HLog中做标记点。
### 数据合并过程
- 当数据块达到4块，Hmaster将数据块加载到本地，进行合并；
- 当合并的数据超过256M，进行拆分，将拆分后的Region分配给不同的HregionServer管理；
- 当HregionServer宕机后，将HregionServer上的hlog拆分，然后分配给不同的HregionServer加载，修改.META；
- 注意：HLog会同步到HDFS。


### 读流程
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5e781efc3fd947d48e1d07bf17ee8426~tplv-k3u1fbpfcp-watermark.image)

- Client先访问zookeeper，从meta表读取region的位置，然后读取meta表中的数据。meta中又存储了用户表的region信息；
- 根据namespace、表名和rowkey在meta表中找到对应的region信息；
- 找到这个region对应的regionserver；
- 查找对应的region；
- 先从MemStore找数据，如果没有，再到StoreFile上读(为了读取的效率)。


###  Hmaster的职责
- 管理用户对Table的增、删、改、查操作；
- 记录region在哪台Hregion server上；
- 在Region Split后，负责新Region的分配；
- 新机器加入时，管理HRegion Server的负载均衡，调整Region分布；
- 在HRegion Server宕机后，负责失效HRegion Server 上的Regions迁移。

###  Hregionserver的职责
- HRegion Server主要负责响应用户I/O请求，向HDFS文件系统中读写数据，是HBASE中最核心的模块。
- HRegion Server管理了很多table的分区，也就是region。

### Client职责
- HBASE Client使用HBASE的RPC机制与HMaster和RegionServer进行通信
- 管理类操作：Client与HMaster进行RPC；
- 数据读写类操作：Client与HRegionServer进行RPC。


## Phoenix(SQL On HBase)

### 简介
- Phoenix是一个HBase框架，可以通过SQL的方式来操作HBase。
- Phoenix是构建在HBase上的一个SQL层，是内嵌在HBase中的JDBC驱动，能够让用户使用标准的JDBC来操作HBase。
- Phoenix使用JAVA语言进行编写，其查询引擎会将SQL查询语句转换成一个或多个HBase Scanner，且并行执行生成标准的JDBC结果集。
- 如果需要对HBase进行复杂的操作，那么应该使用Phoenix，其会将SQL语句转换成HBase相应的API。
- Phoenix只能用在HBase上，其查询性能要远高于Hive。

### Phoenix与HBase的关系

Phoenix与HBase中的表是独立的，两者之间没有必然的关系。

Phoenix与HBase集成后会创建六张系统表：SYSTEM.CATALOG、SYSTEM.FUNCTION、SYSTEM.LOG、SYSTEM.SEQUENCE、SYSTEM.STATS，其中SYSTEM.CATALOG表用于存放Phoenix创建表时的元数据。

Phoenix创建表时会自动调用HBase客户端创建相应的表，并且在SYSTEM.CATALOG系统表中记录Phoenix创建表时的元数据，其主键的值对应HBase的RowKey，非主键的列对应HBase的Column（列族不指定时为0，且列会进行编码）

如果是通过Phoenix创建的表，那么必须通过Phoenix客户端来对表进行操作，因为通过Phoenix创建的表其非主键的列会进行编码。

### Phoenix语法

Phoenix的SQL中如果表名、字段名不使用双引号标注那么默认转换成大写。

Phoenix中的字符串使用单引号进行标注。 

创建表
```
CREATE TABLE IF NOT EXISTS us_population (
      state CHAR(2) NOT NULL,
      city VARCHAR NOT NULL,
      population BIGINT
      CONSTRAINT my_pk PRIMARY KEY (state, city)
);
```
> 主键的值对应HBase中的RowKey，列族不指定时默认是0，非主键的列对应HBase的列。

删除表
```
DROP TABLE us_population;
```

查询数据

```
SELECT * FROM us_population WHERE state = 'NA' AND population > 10000 ORDER BY population DESC;
```
> 在进行查询时，支持ORDER BY、GROUP BY、LIMIT、JOIN等操作，同时Phoenix提供了一系列的函数，其中包括COUNT()、MAX()、MIN()、SUM()等，具体的函数列表可以查看：http://phoenix.apache.org/language/functions.html不管条件中的列是否是联合主键中的，Phoenix一样可以支持。
 
 删除数据

```
DELETE FROM us_population WHERE state = 'NA';

```


Phoenix映射HBase 

只要直接通过HBase客户端创建的表，若想用Phoenix来进行操作，那么必须要进行表的映射，因为SYSTEM.CATALOG表中并没有维护Phoenix创建表的元数据。

创建表来进行表的映射

```
CREATE TABLE IF NOT EXISTS 表名(
  列名 类型 主键,
  列簇.列名,
  列簇.列名
)
```
HBase中的RowKey映射Phoenix的主键，HBase中的Column映射Phoenix的列，且使用列簇名.列名进行映射。
相当于在SYSTEM.CATALOG表中录入相关的元数据，使Phoenix能够进行操作它。


### 使用二级索引

在HBase中会自动为RowKey添加索引，因此在通过RowKey查询数据时效率会很高，但是如果要根据其他列来进行组合查询，那么查询的性能就很低下，此时可以使用Phoenix提供的二级索引，能够极大的提高查询数据的性能。

 我们其实已经知道了我们的主键 是和我们的rowkey进行映射的，所以查询性能高
 
 - 创建普通索引
> CREATE INDEX 索引名称 ON 表名(列名)
- 创建二级索引
> CREATE INDEX 索引名称 ON 表名(列名) INCLUDE(列名)



## 结尾
 Hbase，我们也大致了解了下，这个系列的目的，其实就是过一遍，并不是说多么的深入。。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。 

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>微信 搜 "六脉神剑的程序人生" 回复888 有我找的许多的资料送给大家 
