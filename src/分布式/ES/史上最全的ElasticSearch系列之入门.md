# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨
上次不是才把Java的并发编程系列写完嘛，本来打算写JVM系列的，因为这个是面试必问的，但是因为公司的人员调动，然后我要多接手几个服务，其中一个服务是搜索服务，今天我写这个的原因就是好好重新学习一遍ElasticSearch,因为在接手这个之前我是有学过一遍的，但是很久了没用了，相当于忘记的差不多了，刚好很多读者说想一起学习，那我正好就打算整理一份从0开始学习的系列文章，跟大家一起学习。


## 什么是搜索？
百度：我们比如说想找寻任何的信息的时候，就会上百度去搜索一下，比如说找一部自己喜欢的电影，或者说找一本喜欢的书，或者找一条感兴趣的新闻（提到搜索的第一印象）。百度 != 搜索
- 互联网的搜索：电商网站，招聘网站，新闻网站，各种app
- IT系统的搜索：OA软件，办公自动化软件，会议管理，日程管理，项目管理。
搜索，就是在任何场景下，找寻你想要的信息，这个时候，会输入一段你要搜索的关键字，然后就期望找到这个关键字相关的有些信息


## 什么是全文检索和Lucene？
-  全文检索，倒排索引
>全文检索是指计算机索引程序通过扫描文章中的每一个词，对每一个词建立一个索引，指明该词在文章中出现的次数和位置，当用户查询时，检索程序就根据事先建立的索引进行查找，并将查找的结果反馈给用户的检索方式。这个过程类似于通过字典中的检索字表查字的过程。全文搜索搜索引擎数据库中的数据。

- lucene
> lucene，就是一个jar包，里面包含了封装好的各种建立倒排索引，以及进行搜索的代码，包括各种算法。我们就用java开发的时候，引入lucene jar，然后基于lucene的api进行去进行开发就可以了。



## 模糊的历史
多年前，一个叫做Shay Banon的刚结婚不久的失业开发者，由于妻子要去伦敦学习厨师，他便跟着也去了。在他找工作的过程中，为了给妻子构建一个食谱的搜索引擎，他开始构建一个早期版本的Lucene。
直接基于Lucene工作会比较困难，所以Shay开始抽象Lucene代码以便Java程序员可以在应用中添加搜索功能。他发布了他的第一个开源项目，叫做“Compass”。
后来Shay找到一份工作，这份工作处在高性能和内存数据网格的分布式环境中，因此高性能的、实时的、分布式的搜索引擎也是理所当然需要的。然后他决定重写Compass库使其成为一个独立的服务叫做Elasticsearch。

第一个公开版本出现在2010年2月，在那之后Elasticsearch已经成为Github上最受欢迎的项目之一，代码贡献者超过300人。一家主营Elasticsearch的公司就此成立，他们一边提供商业支持一边开发新功能，不过Elasticsearch将永远开源且对所有人可用。
Shay的妻子依旧等待着她的食谱搜索……

## Elasticsearch是什么？
- Elasticsearch，基于lucene.分布式的Restful实时搜索和分析引擎(实时)
- 分布式的实时文件存储,每个字段都被索引并可被搜索
- 高扩展性,可扩展至上百台服务器,处理PB级结构化或非结构化数据
- Elasticsearch用于全文检索,结构化搜索,分析/合并使用



## Elasticsearch的特性:
- Elasticsearch没有典型意义的事务(无事务性)
- Elasticsearch是一种面向文档的数据库
- Elasticsearch没有提供授权和认证特性
## Elasticsearch 可以做什么？
- 你有一个在线网上商城，提供用户搜索你所卖的商品功能。在这个例子中，你可以使用Elasticsearch去存储你的全部的商品目录和存货清单并且提供搜索和搜索自动完成以及搜索推荐功能。
- 你想去收集日志或者业务数据，并且去分析并从这些数据中挖掘寻找市场趋势、统计资料、摘要信息或者反常情况。在这个例子中，你可以使用Logstash(part of the Elasticsearch/Logstash/Kibana stack)去收集、聚合并且解析你的数据，然后通过Logstash将数据注入Elasticsearch。一旦数据进入Elasticsearch，你就可以运行搜索和聚集并且从中挖掘任何你感兴趣的数据。
- 你运行一个价格预警平台，它可以让那些对价格精明的客户指定一个规则，比如：“我相中了一个电子产品，并且我想在下个月任何卖家的这个电子产品的价格低于多少钱的时候提醒我”。在这个例子中，你可以抓取所有卖家的价格，把价格放入Elasticsearch并且使用Elasticsearch的反向搜索(过滤器/抽出器)功能来匹配价格变动以应对用户的查询并最终一旦发现有匹配结果时给用户弹出提示框。
- 你有分析学/商业情报的需求并且想快速审查、分析并使用图像化进行展示，并且在一个很大的数据集上查询点对点的问题(试想有百万或千万的记录)。在这个例子中，你可以使用Elasticsearch去存储你的数据然后使用Kibana(part of the Elasticsearch/Logstash/Kibana stack)去构建定制化的仪表盘。这样你就可以很直观形象的了解对你重要的数据。此外，你可以使用Elasticsearch的集成功能，靠你的数据去展现更加复杂的商业情报查询。

怎么说呢？其实elk 就是一个技术解决方案，可以做很多的东西。目前我们只是单单先把es入门把，他的搜索功能学会，用好。

## Elasticsearch安装与配置介绍

因为博主会docker，以前自己玩的时候是用docker的，但是考虑到很多读者不会，这次我就用原生的来安装吧，会有几个坑，我到时候给大家提一下，不过我想说的是，学一个东西的第一步，就是装它，也许过程会很痛苦，但是你总得完成不是。

### 单节点安装

elasticsearch是基于java开发的，所以安装之前需要先安装版本大于等于1.8的jdk

下载地址：https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html，自己去这个地址下载，因为他会带一个认证参数AuthParam

### jdk安装: 下载---解压---配置环境变量

说明一下，博主的虚拟机是ubantu,内存给个3G吧，因为博主不是专业做教程的，可能说这个安装教程你得结合几篇博客来完成，但是网上的东西，都是东平西凑的，这是你必须经历的，但是博主尽量做的好点

![](https://user-gold-cdn.xitu.io/2019/12/27/16f4641c6c19334e?w=1093&h=316&f=png&s=43383)

这里要配置一下环境变量，然后每个人放的路径不同，配置也不同，主要是在/etc/profile里面配置的


```
vim /etc/profile

export JAVA_HOME=/usr/local/jdk1.8.0_1
export CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib
export PATH=$JAVA_HOME/bin:$PATH

source /etc/profile
```

### elasticsearch安装: 下载---解压---配置---启动

![](https://user-gold-cdn.xitu.io/2019/12/27/16f462b8a08ca2cf?w=1355&h=460&f=png&s=69855)
```
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.6.2.tar.gz

tar -zxvf elasticsearch-6.6.2.tar.gz

cd elasticsearch-6.6.2

```
下面是里面的目录结构，其实搞的多了，你会发现其实都差不多，每一个中间件都是这样的
![](https://user-gold-cdn.xitu.io/2019/12/27/16f462d8929f8ebb?w=456&h=201&f=png&s=13961)

先修改一下 sudo vi config/elasticsearch.yml（写自己虚拟机的ip 端口）
![](https://user-gold-cdn.xitu.io/2019/12/28/16f4a5e9a4218e1e?w=381&h=133&f=png&s=7236)

我们先直接启动看看，会出现什么情况吧

```
cd /bin
./elasticsearch
```
启动

![](https://user-gold-cdn.xitu.io/2019/12/27/16f4694320bcb956?w=1240&h=342&f=png&s=73474)
很明显的错误，他说我不能用root启动，我们先解决这个错误

所以我需要为elasticsearch新建一个系统运行账号

```
groupadd elasticsearch     //新建一个elasticsearch的用户组

useradd -g elasticsearch elasticsearch  //在elasticsearch用户组下面建立一个elasticsearch的用户

chown -R elasticsearch:elasticsearch elasticsearch-6.6.2/
```
然后切换到刚刚的账号启动elasticsearch

```
su elasticsearch

./elasticsearch-6.6.2/bin/elasticsearch
```

![](https://user-gold-cdn.xitu.io/2019/12/28/16f4a32f7f6c9d0a?w=1235&h=384&f=png&s=89030)

这个错误也是很明显的嘛，最大虚拟内存区域vm.max_map_count（65530）太低，至少增加到262144，然后重启

在root账号下修改配置文件
```
vi /etc/sysctl.conf

vm.max_map_count=262144 

sysctl -p
```

好了 我们继续换成账号启动elasticsearch 


![](https://user-gold-cdn.xitu.io/2019/12/28/16f4a600343e7147?w=974&h=575&f=png&s=46227)

嗯启动成功了，不错


## Ik 安装
然后反正把ES安装好了，然后就是安装一下ik分词器吧，因为es是国外开源的，对于中文的话，需要用的插件
- 第一： 下载地址：https://github.com/medcl/elasticsearch-analysis-ik/releases ，这里你需要根据你的Es的版本来下载对应版本的IK，这里我使用的是6.6.2的ES，所以就下载ik-6.6.2.zip的文件。

- 第二： 解压-->将文件复制到 es的安装目录/plugins下面即可.
- 第三步重启es就好了

## ElasticSearch head 安装 
这个是谷歌浏览器的插件，可以用来查询数据的，这个安装就是涉及到了 谷歌的插件安装，大家自己百度一下哈。

![](https://user-gold-cdn.xitu.io/2019/12/28/16f4a752d65f2146?w=1653&h=953&f=png&s=293914)


目前为止，安装就讲完了，大家先做到这一步吧，有啥不懂的可以群里问，也许不一定能成功，因为每个人配置啥的都不一定，大家多摸索吧，我第一次装，可是搞了几个小时呢？反正都这么过来的，大家加油。






## 结尾
今天就稍微介绍了一下es,然后把要安装的东西给大家做出来了，如果说大家安装有什么问题，可以群里问，如果我有空，看到了肯定会回复的。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！


