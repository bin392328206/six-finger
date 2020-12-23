# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
## 叨絮
上面我们讲了下zookeeper，其实只是一个铺垫，接下来，我们就要真正的开始我们的大数据的学习了。


## 大数据概念

大数据（big data）：指无法在一定时间范围内用常规软件工具进行捕捉、管理和处理的数据集合，是需要新处理模式才能具有更强的决策力、洞察发现力和流程优化能力的海量、高增长率和多样化的信息资产。

> 主要解决，海量数据的存储和海量数据的分析计算问题。


## 大数据技术生态体系

当然，目前这个生态是越来越大了，但是它的本质还是在二个方面 计算 和 存储，我这边说生态介绍一下 开源生态 和阿里云的云生态吧


### 开源生态

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5f63ebfc9e784f7690d0ed92f290e7bb~tplv-k3u1fbpfcp-watermark.image)

- Sqoop：sqoop是一款开源的工具，主要用于在Hadoop(Hive)与传统的数据库(mysql)间进行数据的传递，可以将一个关系型数据库（例如：MySQL ,Oracle 等）中的数据导进到Hadoop的HDFS中，也可以将HDFS的数据导进到关系型数据库中。

- Flume：Flume是Cloudera提供的一个高可用的，高可靠的，分布式的海量日志采集、聚合和传输的系统，Flume支持在日志系统中定制各类数据发送方，用于收集数据；同时，Flume提供对数据进行简单处理，并写到各种数据接受方（可定制）的能力。
- Kafka：Kafka是一种高吞吐量的分布式发布订阅消息系统，有如下特性：
	- 通过O(1)的磁盘数据结构提供消息的持久化，这种结构对于即使数以TB的消息存储也能够保持长时间的稳定性能。
    - 高吞吐量：即使是非常普通的硬件Kafka也可以支持每秒数百万的消息。
    - 支持通过Kafka服务器和消费机集群来分区消息。
    - 支持Hadoop并行数据加载。
- Storm：Storm为分布式实时计算提供了一组通用原语，可被用于“流处理”之中，实时处理消息并更新数据库。这是管理队列及工作者集群的另一种方式。Storm也可被用于“连续计算”（continuous computation），对数据流做连续查询，在计算时就将结果以流的形式输出给用户。
- Spark：Spark是当前最流行的开源大数据内存计算框架。可以基于Hadoop上存储的大数据进行计算。
- Oozie：Oozie是一个管理Hdoop作业（job）的工作流程调度管理系统。Oozie协调作业就是通过时间（频率）和有效数据触发当前的Oozie工作流程。
- Hbase：HBase是一个分布式的、面向列的开源数据库。HBase不同于一般的关系数据库，它是一个适合于非结构化数据存储的数据库。
- Hive：hive是基于Hadoop的一个数据仓库工具，可以将结构化的数据文件映射为一张数据库表，并提供简单的sql查询功能，可以将sql语句转换为MapReduce任务进行运行。其优点是学习成本低，可以通过类SQL语句快速实现简单的MapReduce统计，不必开发专门的MapReduce应用，十分适合数据仓库的统计分析。
- Mahout:Apache Mahout是个可扩展的机器学习和数据挖掘库，当前Mahout支持主要的4个用例：推荐挖掘：搜集用户动作并以此给用户推荐可能喜欢的事物。聚集：收集文件并进行相关文件分组。分类：从现有的分类文档中学习，寻找文档中的相似特征，并为无标签的文档进行正确的归类。频繁项集挖掘：将一组项分组，并识别哪些个别项会经常一起出现。
- ZooKeeper：Zookeeper是Google的Chubby一个开源的实现。它是一个针对大型分布式系统的可靠协调系统，提供的功能包括：配置维护、名字服务、分布式同步、组服务等。ZooKeeper的目标就是封装好复杂易出错的关键服务，将简单易用的接口和性能高效、功能稳定的系统提供给用户。

### 阿里云MaxCompute

MaxCompute（大数据计算服务）是是一种快速、完全托管的TB/PB级数据仓库解决方案。MaxCompute主要用于实时性要求不高的、批量结构化数据的存储和计算。并可提供大数据分析建模服务。其特点如下： 
- 采用分布式架构高效处理海量数据
- 基于表的数据存储
- 于SQL的数据处理
- 支持多用户协同分析数据，多种权限管理方式，具有灵活的数据访问控制策略
- 兼容Hive

MaxCompute架构
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/521857b4c5af453e8967446f719eee2c~tplv-k3u1fbpfcp-watermark.image)


MaxCompute功能

- 数据存储

适用于TB以上规模的存储及计算需求，最大可达EB级别。数据分布式存储，多副本冗余，数据存储对外仅开放表的操作接口，不提供文件系统访问接口。表数据列式存储，默认高度压缩，后续将提供兼容ORC的Ali-ORC存储格式。
支持外表，将存储在OSS对象存储、OTS表格存储的数据映射为二维表。
支持Partition、Bucket的分区、分桶存储。
底层是盘古文件系统（不是HDFS）。
使用时，存储与计算解耦，不需要仅仅为了存储而扩大不必要的计算资源。


- 数据通道
TUNNEL：提供高并发的离线数据上传下载服务。支持每天TB/PB级别的数据导入导出。适合于全量数据或历史数据的批量导入。

DataHub：针对实时数据上传的场景，具有延迟低、使用方便的特点，适用于增量数据的导入。Datahub还支持多种数据传输插件，包括Logstash、Flume、Fluentd、Sqoop等。同时支持日志服务Log Service中的日志数据的一键投递至MaxCompute，进而利用大数据开发套件进行日志分析和挖掘。

- 多种计算模型
SQL：以二维表的形式存储数据，支持多种数据类型，MaxCompute以二维表的形式存储数据，对外提供了SQL查询功能。不支持事务、索引及Update/Delete等操作，SQL语法与Oracle，MySQL等有一定差别。无法在毫秒级别返回结果。

MapReduce：支持MapReduce java编程接口（提供优化增强的MaxCompute MapReduce，也提供高度兼容Hadoop的MapReduce版本）。不暴露文件系统，输入输出都是表。通过MaxCompute客户端工具、Dataworks提交作业。

Graph：是一套面向迭代的图计算处理框架。图计算作业使用图进行建模，图由点（Vertex）和边（Edge）组成，点和边包含权值（Value）。通过迭代对图进行编辑、演化，最终求解出结果，典型应用：PageRank、单源最短距离算法 、K-均值聚类算法等。

- Spark

MaxCompute提供了Spark on MaxCompute的解决方案，在统一的计算资源和数据集权限体系之上，提供Spark计算框架，支持用户以熟悉的开发使用方式提交运行Spark作业。

- 交互式分析(Lightning)
MaxCompute产品的交互式查询服务。兼容PostgreSQL协议的JDBC/ODBC接口。支持主流BI及SQL客户端工具的连接访问，如Tableau、帆软BI、Navicat、SQL Workbench/J等。

##  Hadoop是什么

- Hadoop是一个由Apache基金会所开发的分布式系统基础架构。
- 主要解决，海量数据的存储和海量数据的分析计算问题。
- 广义上来说，HADOOP通常是指一个更广泛的概念——HADOOP生态圈。

## Hadoop的组成

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/94def613dbab4f819ff61a0802b4a5d2~tplv-k3u1fbpfcp-watermark.image)


### HDFS架构概述

- NameNode（nn）：存储文件的元数据，如文件名，文件目录结构，文件属性（生成时间、副本数、文件权限），以及每个文件的块列表和块所在的DataNode等。
- DataNode(dn)：在本地文件系统存储文件块数据，以及块数据的校验和。
- Secondary NameNode(2nn)：用来监控HDFS状态的辅助后台程序，每隔一段时间获取HDFS元数据的快照。


### YARN架构概述
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0e22495656df4cb1b6bbf187dac7b733~tplv-k3u1fbpfcp-watermark.image)

### MapReduce架构概述

MapReduce将计算过程分为两个阶段：Map和Reduce

- Map阶段并行处理输入数据
- Reduce阶段对Map结果进行汇总

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2fe4c16a0a4b4ea3b04b80c2318513cb~tplv-k3u1fbpfcp-watermark.image)


## Hadoop分布式搭建

- [史上最简单详细的Hadoop完全分布式集群搭建](https://blog.csdn.net/superman404/article/details/83591324)


## 结尾
 大致的介绍了一下Hadoop，下面我们就来一一详细的看看Hadoop的各个组件。。。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！