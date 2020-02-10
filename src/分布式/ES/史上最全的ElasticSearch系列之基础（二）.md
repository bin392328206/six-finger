# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨
昨天把简单的crud 给干完了，本来想讲讲原理的东西,但是我一想，大家都是才入门。就讲那写不好，所以我这边接着昨天的crud,把单机的搜索讲完，然后我们再去Java客户端去实践一遍先,然后我们才开始干原理的东西会好很多应该。  
下面是前面的系列文章  
- [🔥史上最全的ElasticSearch系列之入门](https://juejin.im/post/5e04613ff265da33ee17944b)
- [🔥史上最全的ElasticSearch系列之基础（一）](https://juejin.im/post/5e06cd12e51d45583e4dd495)

## 查询

### 空搜索
最基本的搜索API表单是空搜索(empty search)，它没有指定任何的查询条件，只返回集群索引中的所有文档：


```
GET /_search
```

响应内容（为了编辑简洁）类似于这样：

```
{
   "hits" : {
      "total" :       14,
      "hits" : [
        {
          "_index":   "us",
          "_type":    "tweet",
          "_id":      "7",
          "_score":   1,
          "_source": {
             "date":    "2019-09-17",
             "name":    "John Smith",
             "tweet":   "The Query DSL is really powerful and flexible",
             "user_id": 2
          }
       },
        ... 9 RESULTS REMOVED ...
      ],
      "max_score" :   1
   },
   "took" :           4,
   "_shards" : {
      "failed" :      0,
      "successful" :  10,
      "total" :       10
   },
   "timed_out" :      false
}
```

<h3>hits</h3>

响应中最重要的部分是hits，它包含了total字段来表示匹配到的文档总数，hits数组还包含了匹配到的前10条数据。
hits数组中的每个结果都包含_index、_type和文档的_id字段，被加入到_source字段中这意味着在搜索结果中我们将可以直接使用全部文档。这不像其他搜索引擎只返回文档ID，需要你单独去获取文档。

每个节点都有一个_score字段，这是相关性得分(relevance score)，它衡量了文档与查询的匹配程度。默认的，返回的结果中关联性最大的文档排在首位；这意味着，它是按照_score降序排列的。这种情况下，我们没有指定任何查询，所以所有文档的相关性是一样的，因此所有结果的_score都是取得一个中间值1
max_score指的是所有文档匹配查询中_score的最大值。


<h3>took</h3>
took告诉我们整个搜索请求花费的毫秒数。

<h3>shards</h3>
_shards节点告诉我们参与查询的分片数（total字段），有多少是成功的（successful字段），有多少的是失败的（failed字段）。通常我们不希望分片失败，不过这个有可能发生。如果我们遭受一些重大的故障导致主分片和复制分片都故障，那这个分片的数据将无法响应给搜索请求。这种情况下，Elasticsearch将报告分片failed，但仍将继续返回剩余分片上的结果。

<h3>timeout</h3>

time_out值告诉我们查询超时与否。一般的，搜索请求不会超时。如果响应速度比完整的结果更重要，你可以定义timeout参数为10或者10ms（10毫秒），或者1s（1秒）

```
GET /_search?timeout=10ms
```

### 多索引和多类别
就是一次性可以搜索，多种类型，多个索引。

- /_search
> 在所有索引的所有类型中搜索
- /gb/_search
>在索引gb的所有类型中搜索

### 分页
假如你的数据很多，你不想一次性返回所有的数据，那么es中给我们提供了分页的方法  
和SQL使用LIMIT关键字返回只有一页的结果一样，Elasticsearch接受from和size参数：
- size: 结果数，默认10
- from: 跳过开始的结果数，默认0

比如前面的搜索接口可以这样写

```
http://192.168.62.145:9200/website/_search?size=1&from=1
```
结果

```
{
    "took": 4,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 2,
        "max_score": 1,
        "hits": [
            {
                "_index": "website",
                "_type": "blog",
                "_id": "1",
                "_score": 1,
                "_source": {
                    "doc": {
                        "ada": [
                            "testing"
                        ],
                        "da": 0
                    }
                }
            }
        ]
    }
}
```

### 映射和分析

映射(mapping)机制用于进行字段类型确认，将每个字段匹配为一种确定的数据类型(string(keyword,text), number, booleans, date等)。

分析(analysis)机制用于进行全文文本(Full Text)的分词，以建立供搜索用的反向索引。'

映射说白了就是字段的类型，ES的字段类型挺多了，在这里我给大家先好好说说

#### 字段类型概述

一级分类|	二级分类|	具体类型
|-------|------|------|
核心类型|字符串类型| string,text,keyword
核心类型|整数类型|	integer,long,short,byte
核心类型|浮点类型|	double,float,half_float,scaled_float
核心类型|逻辑类型|	boolean
核心类型|日期类型|	date
核心类型|范围类型|	range
核心类型|二进制类型|	binary
复合类型|数组类型|	array
复合类型|对象类型|	object
复合类型|嵌套类型|	nested

具体来说说字符串类型
- （1）string
string类型在ElasticSearch 旧版本中使用较多，从ElasticSearch 5.x开始不再支持string，由text和keyword类型替代。
- （2）text
当一个字段是要被全文搜索的，比如Email内容、产品描述，应该使用text类型。设置text类型以后，字段内容会被分析，在生成倒排索引以前，字符串会被分析器分成一个一个词项。text类型的字段不用于排序，很少用于聚合。
- （3）keyword
keyword类型适用于索引结构化的字段，比如email地址、主机名、状态码和标签。如果字段需要进行过滤(比如查找已发布博客中status属性为published的文章)、排序、聚合。keyword类型的字段只能通过精确值搜索到。

### index
index参数控制字符串以何种方式被索引。它包含以下三个值当中的一个：
值	|解释
|-----|------|
analyzed|	首先分析这个字符串，然后索引。换言之，以全文形式索引此字段。
not_analyzed|索引这个字段，使之可以被搜索，但是索引内容和指定值一样。不分析此字段。
no|	不索引这个字段。这个字段不能为搜索到。

这个参数就可以控制搜索的时候的特性 是分词匹配 还是全部匹配 还是 不能搜索

```
{
    "tag": {
        "type":     "string",
        "index":    "not_analyzed"
    }
}
```
> 其他简单类型（long、double、date等等）也接受index参数，但相应的值只能是no和not_analyzed，它们的值不能被分析。


## 结构化查询

前面把简单的增删改查都已经做了一遍了，接下来我们要讲的就是查询，也是es中最重要的部分了
### 请求体查询
简单查询语句(lite)是一种有效的命令行adhoc查询。但是，如果你想要善用搜索，你必须使用请求体查询(request body search)API。之所以这么称呼，是因为大多数的参数以JSON格式所容纳而非查询字符串。

请求体查询(下文简称查询)，并不仅仅用来处理查询，而且还可以高亮返回结果中的片段，并且给出帮助你的用户找寻最好结果的相关数据建议。

空查询

```
GET /_search
{} 
```

结果

```
{
    "took": 10,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 2,
        "max_score": 1,
        "hits": [
            {
                "_index": "website",
                "_type": "blog",
                "_id": "bVidS28B7LO-oxNnPclm",
                "_score": 1,
                "_source": {
                    "title": "My second blog entry",
                    "text": "Still trying this out...",
                    "date": "2014/01/01"
                }
            },
            {
                "_index": "website",
                "_type": "blog",
                "_id": "1",
                "_score": 1,
                "_source": {
                    "doc": {
                        "ada": [
                            "testing"
                        ],
                        "da": 0
                    }
                }
            }
        ]
    }
}
```

你可以使用from及size参数进行分页：

```
POST /_search
{
  "from": 30,
  "size": 10
}
```

### 结构化查询 Query DSL
Query DSL又叫查询表达式，是一种非常灵活又富有表现力的查询语言，采用JSON接口的方式实现丰富的查询，并使你的查询语句更灵活、更精确、更易读且易调试

#### 查询与过滤
Elasticsearch中的数据检索分为两种情况：查询和过滤。

Query查询会对检索结果进行评分，注重的点是匹配程度，例如检索“运维咖啡吧”与文档的标题有多匹配，计算的是查询与文档的相关程度，计算完成之后会算出一个评分，记录在_score字段中，并最终按照_score字段来对所有检索到的文档进行排序

Filter过滤不会对检索结果进行评分，注重的点是是否匹配，例如检索“运维咖啡吧”是否匹配文档的标题，结果只有匹配或者不匹配，因为只是对结果进行简单的匹配，所以计算起来也非常快，并且过滤的结果会被缓存到内存中，性能要比Query查询高很多


一个最简单的DSL查询表达式如下：

```
GET /_search
{
  "query":{
    "match_all": {}
  }
}
```
/_search 查找整个ES中所有索引的内容

query 为查询关键字，类似的还有aggs为聚合关键字

match_all 匹配所有的文档，也可以写match_none不匹配任何文档

#### 全文查询
上边有用到一个match_all的全文查询关键字，match_all为查询所有记录，常用的查询关键字在ES中还有以下几个最简单的查询，下边的例子就表示查找title为My second blog entry的所有记录
- match_all
```

POST /website/_search
{
  "query":{
    "match": {
      "title":"My second blog entry"
    }
  }
}

```


- multi_match 
在多个字段上执行相同的match查询，下边的例子就表示查询host或http_referer字段中包含ops-coffee.cn的记录


```

POST /website/_search
{
  "query":{
    "multi_match": {
      "query":"My second blog entry",
      "fields":["title","text"]
    }
  }
}

```
就是查多个字段含义相同的词

- query_string

```
POST /website/_search
{
  "query":{
    "query_string": {
      "query":"(My second blog entry) OR (My fist blog entry)",
      "fields":["host"]
    }
  }
}
```
- term

```
term可以用来精确匹配，精确匹配的值可以是数字、时间、布尔值或者是设置了not_analyzed不分词的字符串
```

```
GET /ops-coffee-2019.05.14/_search
{
  "query":{
    "term": {
      "status": {
        "value": 404
      }
    }
  }
}

```
term对输入的文本不进行分析，直接精确匹配输出结果，如果要同时匹配多个值可以使用terms


```
GET /ops-coffee-2019.05.14/_search
{
  "query": {
    "terms": {
      "status":[403,404]
    }
  }
}

```
- range
range用来查询落在指定区间内的数字或者时间


```
GET /ops-coffee-2019.05.14/_search
{
  "query": {
    "range":{
      "status":{
        "gte": 400,
        "lte": 599
      }
    }
  }
}

```
以上表示搜索所有状态为400到599之间的数据，这里的操作符主要有四个gt大于，gte大于等于，lt小于，lte小于等于

当使用日期作为范围查询时，我们需要注意下日期的格式，官方支持的日期格式主要有两种

时间戳，注意是毫秒粒度

```
GET /ops-coffee-2019.05.14/_search
{
  "query": {
    "range": {
      "@timestamp": {
        "gte": 1557676800000,
        "lte": 1557680400000,
        "format":"epoch_millis"
      }
    }
  }
}
```

日期字符串

```
GET /ops-coffee-2019.05.14/_search
{
  "query": {
    "range":{
      "@timestamp":{
        "gte": "2019-05-13 18:30:00",
        "lte": "2019-05-14",
        "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd",
        "time_zone": "+08:00"
      }
    }
  }
}
```

### 组合查询

通常我们可能需要将很多个条件组合在一起查出最后的结果，这个时候就需要使用ES提供的bool来实现了

例如我们要查询host为ops-coffee.cn且http_x_forworded_for为111.18.78.128且status不为200的所有数据就可以使用下边的语句


```
GET /ops-coffee-2019.05.14/_search
{
 "query":{
    "bool": {
      "filter": [
        {"match": {
          "host": "ops-coffee.cn"
        }},
        {"match": {
          "http_x_forwarded_for": "111.18.78.128"
        }}
      ],
      "must_not": {
        "match": {
          "status": 200
        }
      }
    }
  }
}
```

主要有四个关键字来组合查询之间的关系，分别为：

must： 类似于SQL中的AND，必须包含

must_not： 类似于SQL中的NOT，必须不包含

should： 满足这些条件中的任何条件都会增加评分_score，不满足也不影响，should只会影响查询结果的_score值，并不会影响结果的内容

filter： 与must相似，但不会对结果进行相关性评分_score，大多数情况下我们对于日志的需求都无相关性的要求，所以建议查询的过程中多用filter


## 结尾
稍微讲了一下DSL，下次我们结合项目来做一下最简单的增删改查，这样就会好很多，下章节我们讲讲我们公司用的Java 客户端highlevelclient
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
