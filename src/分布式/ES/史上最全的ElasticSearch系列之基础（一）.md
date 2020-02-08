# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨
昨天把ES的介绍，安装完成了，接下来就得好好讲讲ES了，这个东西，我都不知道能写多少，反正我学一点我就写一点，这个系列，估计是一直会有迭代的，就是相当于一个产品一直是再更新了，只是更新时间不确定，哈哈，但是最基础的东西，还有一些应付面试的底层原理我这边，肯定会讲到了，至于说源码，我现在一行代码也没看过，但是万一哪天有机会，我还是会写的，下面是前面的系列文章  
- [🔥史上最全的ElasticSearch系列之入门](史上最全的ElasticSearch系列之入门)



##  Elasticsearch的核心概念

###  近实时
 近实时，两个意思，从写入数据到数据可以被搜索到有一个小延迟（大概1秒）；基于es执行搜索和分析可以达到秒级。
### Cluster（集群）
集群包含多个节点，每个节点属于哪个集群是通过一个配置（集群名称，默认是elasticsearch）来决定的，对于中小型应用来说，刚开始一个集群就一个节点很正常
### Node（节点）
 集群中的一个节点，节点也有一个名称（默认是随机分配的），节点名称很重要（在执行运维管理操作的时候），默认节点会去加入一个名称为“elasticsearch”的集群，如果直接启动一堆节点，那么它们会自动组成一个elasticsearch集群，当然一个节点也可以组成一个elasticsearch集群。
### Index（索引-数据库）
 索引包含一堆有相似结构的文档数据，比如可以有一个客户索引，商品分类索引，订单索引，索引有一个名称。一个index包含很多document，一个index就代表了一类类似的或者相同的document。比如说建立一个product     index，商品索引，里面可能就存放了所有的商品数据，所有的商品document。    
 
索引(index)类似于关系型数据库里的“数据库”——它是我们存储和索引关联数据的地方。   
**提示：**   
> 事实上，我们的数据被存储和索引在分片(shards)中，索引只是一个把一个或多个分片分组在一起的逻辑空间。然而，这只是一些内部细节——我们的程序完全不用关心分片。对于我们的程序而言，文档存储在索引(index)中。剩下的细节由Elasticsearch关心既可。
我们唯一需要做的仅仅是选择一个索引名。这个名字必须是全部小写，不能以下划线开头，不能包含逗号

### Type（类型-表）
每个索引里都可以有一个或多个type，type是index中的一个逻辑数据分类，一个type下的document，都有相同的field，比如博客系统，有一个索引，可以定义用户数据type，博客数据type，评论数据type。  
商品index，里面存放了所有的商品数据，商品document   
但是商品分很多种类，每个种类的document的field可能不太一样，比如说电器商品，可能还包含一些诸如售后时间范围这样的特殊field；生鲜商品，还包含一些诸如生鲜保质期之类的特殊field
例如
- 日化商品type：product_id，product_name，product_desc，category_id，category_name
- 电器商品type：product_id，product_name，product_desc，category_id，category_name，service_period
- 生鲜商品type：product_id，product_name，product_desc，category_id，category_name，eat_period

### Document（文档-行）
文档是es中的最小数据单元，一个document可以是一条客户数据，一条商品分类数据，一条订单数据，通常用JSON数据结构表示，每个index下的type中，都可以去存储多个document。

### Field（字段-列）
Field是Elasticsearch的最小单位。一个document里面有多个field，每个field就是一个数据字段。

###  mapping（映射-约束）
数据如何存放到索引对象上，需要有一个映射配置，包括：数据类型、是否存储、是否分词等。

这样就创建了一个名为blog的Index。Type不用单独创建，在创建Mapping 时指定就可以。Mapping用来定义Document中每个字段的类型，即所使用的 analyzer、是否索引等属性，非常关键等。创建Mapping 的代码示例如下：

```
client.indices.putMapping({
    index : 'blog',
    type : 'article',
    body : {
        article: {
            properties: {
                id: {
                    type: 'string',
                    analyzer: 'ik',
                    store: 'yes',
                },
                title: {
                    type: 'string',
                    analyzer: 'ik',
                    store: 'no',
                },
                content: {
                    type: 'string',
                    analyzer: 'ik',
                    store: 'yes',
                }
            }
        }
    }
});
```
上面就是es的基本要使用的概念，既然我们把它当作一个数据库，那么我就直接把它和mysql对比一下吧

###  elasticsearch与数据库的类比
| 关系型数据库（比如Mysql） | 非关系型数据库（Elasticsearch） |
|------|------------|
|数据库Database|	索引Index
表Table|类型Type
数据行Row |文档Document
数据列Column |	字段Field
约束 Schema |	映射Mapping

### ES存入数据和搜索数据机制

![](https://user-gold-cdn.xitu.io/2019/12/28/16f4b467fdbad005?w=1877&h=890&f=png&s=295526)

我用白话来说说这个意思吧，首先把数据分词存储，每个词都是一个索引，每个索引都可以找到这个数据，通过数据存储的时候形成一个倒排索引，用来方便查询。


## 基本使用
使用它很简单，大家做开发的肯定有很多测接口的工具，随便一个就行，今天我们稍微玩玩它的增删改查，不是Java 客户端的玩法，我们先用restApi的方式来操作操作它



### 首先当然是创建一个索引
语法

```
PUT /{index}/{type}/{id}
{
  "field": "value",
  ...
}
```

例如我们的索引叫做“website”，类型叫做“blog”，我们选择的ID是“123”，那么这个索引请求就像这样：

```
PUT /website/blog/123
{
  "title": "My first blog entry",
  "text":  "Just trying this out...",
  "date":  "2014/01/01"
}
```

然后是控制台的返回

![](https://user-gold-cdn.xitu.io/2019/12/28/16f4b9abac8496eb?w=1100&h=822&f=png&s=86515)

上面的例子是自定义id,还可以id自增，只要不传第三个字段就好了


```
POST /website/blog/
{
  "title": "My second blog entry",
  "text":  "Still trying this out...",
  "date":  "2014/01/01"
}
```


![](https://user-gold-cdn.xitu.io/2019/12/28/16f4b9dafb07c506?w=1480&h=891&f=png&s=104175)

区别再与自增id的话 请求是POST。

### 检索文档
想要从Elasticsearch中获取文档，我们使用同样的_index、_type、_id，但是HTTP方法改为GET：

```
GET /website/blog/123
```
响应包含了现在熟悉的元数据节点，增加了_source字段，它包含了在创建索引时我们发送给Elasticsearch的原始文档。

```
{
  "_index" :   "website",
  "_type" :    "blog",
  "_id" :      "123",
  "_version" : 1,
  "found" :    true,
  "_source" :  {
      "title": "My first blog entry",
      "text":  "Just trying this out...",
      "date":  "2014/01/01"
  }
}
```
GET请求返回的响应内容包括{"found": true}。这意味着文档已经找到。如果我们请求一个不存在的文档，依旧会得到一个JSON，不过found值变成了false。


![](https://user-gold-cdn.xitu.io/2019/12/28/16f4ba145ee73b51?w=763&h=721&f=png&s=72701)

刚刚是获得全部文档，有的时候我们并不需要全部的字段，只需要一部分，该怎么操作呢？

通常，GET请求将返回文档的全部，存储在_source参数中。但是可能你感兴趣的字段只是title。请求个别字段可以使用_source参数。多个字段可以使用逗号分隔：

```
GET /website/blog/123?_source=title,text
```
上面少了一个date字段，结果果然没错

![](https://user-gold-cdn.xitu.io/2019/12/28/16f4ba328aaa143b?w=947&h=712&f=png&s=70947)

### 更新文档
文档在Elasticsearch中是不可变的——我们不能修改他们。如果需要更新已存在的文档，我们可以使用《索引文档》章节提到的index API 重建索引(reindex) 或者替换掉它。


```
PUT /website/blog/123
{
  "title": "My first blog entry",
  "text":  "I am starting to get the hang of this...",
  "date":  "20
  }
```

![](https://user-gold-cdn.xitu.io/2019/12/28/16f4ba5deb31db33?w=1169&h=861&f=png&s=99535)

其实所谓的更新，不过就是覆盖上面的一个文档，你看这个文档的版本也增加了1

### 删除文档
删除文档的语法模式与之前基本一致，只不过要使用DELETE方法：


```
DELETE /website/blog/123
```

如果文档被找到，Elasticsearch将返回200 OK状态码和以下响应体。注意_version数字已经增加了。

```
{
  "found" :    true,
  "_index" :   "website",
  "_type" :    "blog",
  "_id" :      "123",
  "_version" : 3
}
```
如果文档未找到，我们将得到一个404 Not Found状态码，响应体是这样的：

```
{
  "found" :    false,
  "_index" :   "website",
  "_type" :    "blog",
  "_id" :      "123",
  "_version" : 4
}
```


![](https://user-gold-cdn.xitu.io/2019/12/28/16f4baea4208fc02?w=1363&h=858&f=png&s=97171)

直接根据状态码就能知道是否删除成功

### 乐观并发控制

Elasticsearch是分布式的。当文档被创建、更新或删除，文档的新版本会被复制到集群的其它节点。Elasticsearch即是同步的又是异步的，意思是这些复制请求都是平行发送的，并无序(out of sequence)的到达目的地。这就需要一种方法确保老版本的文档永远不会覆盖新的版本。
上文我们提到index、get、delete请求时，我们指出每个文档都有一个_version号码，这个号码在文档被改变时加一。Elasticsearch使用这个_version保证所有修改都被正确排序。当一个旧版本出现在新版本之后，它会被简单的忽略。

我们利用_version的这一优点确保数据不会因为修改冲突而丢失。我们可以指定文档的version来做想要的更改。如果那个版本号不是现在的，我们的请求就失败了。

用version 来保证并发的顺序一致性

### 文档局部更新(本质还是重新新建文档的操作，只是封装了)

在《更新文档》一章，我们说了一种通过检索，修改，然后重建整文档的索引方法来更新文档。这是对的。然而，使用update API，我们可以使用一个请求来实现局部更新，例如增加数量的操作。  

我们也说过文档是不可变的——它们不能被更改，只能被替换。update    API必须遵循相同的规则。表面看来，我们似乎是局部更新了文档的位置，内部却是像我们之前说的一样简单的使用update API处理相同的检索-修改-重建索引流程，我们也减少了其他进程可能导致冲突的修改。   

最简单的update请求表单接受一个局部文档参数doc，它会合并到现有文档中——对象合并在一起，存在的标量字段被覆盖，新字段被添加。举个例子，我们可以使用以下请求为博客添加一个tags字段和一个views字段：

```

POST /website/blog/1/_update
{
   "doc" : {
      "tags" : [ "testing" ],
      "views": 0
   }
}
```

### 检索多个文档
像Elasticsearch一样，检索多个文档依旧非常快。合并多个请求可以避免每个请求单独的网络开销。如果你需要从Elasticsearch中检索多个文档，相对于一个一个的检索，更快的方式是在一个请求中使用multi-get或者mget API。

mget API参数是一个docs数组，数组的每个节点定义一个文档的_index、_type、_id元数据。如果你只想检索一个或几个确定的字段，也可以定义一个_source参数：


```
POST /_mget
{
   "docs" : [
      {
         "_index" : "website",
         "_type" :  "blog",
         "_id" :    2
      },
      {
         "_index" : "website",
         "_type" :  "pageviews",
         "_id" :    1,
         "_source": "views"
      }
   ]
}
```

结果

```
{
    "docs": [
        {
            "_index": "website",
            "_type": "blog",
            "_id": "1",
            "_version": 2,
            "_seq_no": 1,
            "_primary_term": 1,
            "found": true,
            "_source": {
                "doc": {
                    "ada": [
                        "testing"
                    ],
                    "da": 0
                }
            }
        },
        {
            "_index": "website",
            "_type": "pageviews",
            "_id": "1",
            "found": false
        }
    ]
}
```

如果你想检索的文档在同一个_index中（甚至在同一个_type中），你就可以在URL中定义一个默认的/_index或者/_index/_type。

你依旧可以在单独的请求中使用这些值：


```
POST /website/blog/_mget
{
   "docs" : [
      { "_id" : 2 },
      { "_type" : "pageviews", "_id" :   1 }
   ]
}
```

事实上，如果所有文档具有相同_index和_type，你可以通过简单的ids数组来代替完整的docs数组：


```
POST /website/blog/_mget
{
   "ids" : [ "2", "1" ]
}
```

### 更新时的批量操作

就像mget允许我们一次性检索多个文档一样，bulk API允许我们使用单一请求来实现多个文档的create、index、update或delete。这对索引类似于日志活动这样的数据流非常有用，它们可以以成百上千的数据为一个批次按序进行索引。

bulk请求体如下，它有一点不同寻常：

```
{ action: { metadata }}\n
{ request body        }\n
{ action: { metadata }}\n
{ request body        }\n
...
```

这种格式类似于用"\n"符号连接起来的一行一行的JSON文档流(stream)。两个重要的点需要注意：
- 每行必须以"\n"符号结尾，包括最后一行。这些都是作为每行有效的分离而做的标记。
- 每一行的数据不能包含未被转义的换行符，它们会干扰分析——这意味着JSON不能被美化打印。

action/metadata这一行定义了文档行为(what action)发生在哪个文档(which document)之上。

行为(action)必须是以下几种：
| 行为| 解释 |
|------|------------|
create|	当文档不存在时创建之。
index|	创建新文档或替换已有文档。
update|	局部更新文档。
delete|	删除一个文档。

说了那么多,大家可能有点懵，我们直接上代码

在索引、创建、更新或删除时必须指定文档的_index、_type、_id这些元数据(metadata)。
例如删除请求看起来像这样：


```
{ "delete": { "_index": "website", "_type": "blog", "_id": "123" }}
```
因为删除的话，就没有请求体了，就这样了

创建

```
{ "create":  { "_index": "website", "_type": "blog", "_id": "123" }}
{ "title":    "My first blog post" }
```

返回
```
{
    "took": 5,
    "errors": false,
    "items": [
        {
            "delete": {
                "_index": "website",
                "_type": "blog",
                "_id": "123",
                "_version": 1,
                "result": "not_found",
                "_shards": {
                    "total": 2,
                    "successful": 1,
                    "failed": 0
                },
                "_seq_no": 3,
                "_primary_term": 1,
                "status": 404
            }
        }
    ]
}
```



## 结尾
其实今天就是把他的结构，和最简单的crud讲一下，大家照着做一遍就好了，明日我们用Java来操作一下增删个改查，其实最主要的查询，今天也没有细讲，还有很多要讲的
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！

