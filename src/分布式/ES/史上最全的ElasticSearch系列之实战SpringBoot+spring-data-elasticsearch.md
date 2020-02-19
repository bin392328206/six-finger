# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨
昨天把简单的crud 给干完了，本来想讲讲原理的东西,但是我一想，大家都是才入门。就讲那写不好.昨天把高级客户端的crud讲完，今天我们来讲讲Spring Data 对elasticsearch的简化
下面是前面的系列文章  
- [🔥史上最全的ElasticSearch系列之入门](https://juejin.im/post/5e04613ff265da33ee17944b)
- [🔥史上最全的ElasticSearch系列之基础（一）](https://juejin.im/post/5e06cd12e51d45583e4dd495)

- [🔥史上最全的ElasticSearch系列之基础（二）](https://juejin.im/post/5e071debf265da33c90b56c7)
- [🔥史上最全的ElasticSearch系列之实战SpringBoot+ElasticSearch+HighLevelClient](https://juejin.im/post/5e0ea575e51d45414e6aaa8c)

## 项目的目录

![](https://user-gold-cdn.xitu.io/2020/1/3/16f6aa43e360699b?w=766&h=614&f=png&s=69761)


## pom文件

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>com.gf</groupId>
	<artifactId>springboot-elasticsearch</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>springboot-elasticsearch</name>
	<description>Demo project for Spring Boot</description>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.1.1.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-elasticsearch</artifactId>
		</dependency>
		<dependency>
			<groupId>io.searchbox</groupId>
			<artifactId>jest</artifactId>
			<version>5.3.3</version>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>


</project>

```

### Application启动类


```
package com.gf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootElasticsearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootElasticsearchApplication.class, args);
	}
}

```


## application.properties

```
spring.data.elasticsearch.cluster-nodes=192.168.62.145:9300
```

## Repository相当于DAO

```
package com.gf.repository;


import com.gf.entity.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface BookRepository extends ElasticsearchRepository<Book, Integer>{

    List<Book> findByBookNameLike(String bookName);

}

```

##  实体

```
package com.gf.entity;

import org.springframework.data.elasticsearch.annotations.Document;

@Document( indexName = "gf" , type = "book")
public class Book {
    private Integer id;
    private String bookName;
    private String author;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder( "{\"Book\":{" );
        sb.append( "\"id\":" )
                .append( id );
        sb.append( ",\"bookName\":\"" )
                .append( bookName ).append( '\"' );
        sb.append( ",\"author\":\"" )
                .append( author ).append( '\"' );
        sb.append( "}}" );
        return sb.toString();
    }

}

```


```
package com.gf.entity;


import io.searchbox.annotations.JestId;

public class Article {

    @JestId
    private Integer id;
    private String author;
    private String title;
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder( "{\"Article\":{" );
        sb.append( "\"id\":" )
                .append( id );
        sb.append( ",\"author\":\"" )
                .append( author ).append( '\"' );
        sb.append( ",\"title\":\"" )
                .append( title ).append( '\"' );
        sb.append( ",\"content\":\"" )
                .append( content ).append( '\"' );
        sb.append( "}}" );
        return sb.toString();
    }


}

```


## 创建索引


```
	@Test
	public void createIndex2(){
		Book book = new Book();
		book.setId(1);
		book.setBookName("西游记");
		book.setAuthor( "吴承恩" );
		bookRepository.index( book );
	}
```
![](https://user-gold-cdn.xitu.io/2020/1/3/16f6ab46225a613f?w=1312&h=384&f=png&s=68014)

![](https://user-gold-cdn.xitu.io/2020/1/3/16f6ab49cdcfb308?w=1262&h=694&f=png&s=86119)


## 查找

```
	@Test
	public void useFind() {
		List<Book> list = bookRepository.findByBookNameLike( "游" );
		for (Book book : list) {
			System.out.println(book);
		}

	}
```


![](https://user-gold-cdn.xitu.io/2020/1/3/16f6ab6300a975af?w=1501&h=760&f=png&s=100956)

## ElasticsearchRepository这个接口，只要继承了这个类就可以实现基本的增删改查

打开这个类我们发现：


```
@NoRepositoryBean
public interface ElasticsearchRepository<T, ID extends Serializable> extends ElasticsearchCrudRepository<T, ID> {
    <S extends T> S index(S var1);

    Iterable<T> search(QueryBuilder var1);

    Page<T> search(QueryBuilder var1, Pageable var2);

    Page<T> search(SearchQuery var1);

    Page<T> searchSimilar(T var1, String[] var2, Pageable var3);

    void refresh();

    Class<T> getEntityClass();
}
```
关键字

关键字	|使用示例	|等同于的ES查询
|-----|------|------|
And|	findByNameAndPrice	|{“bool” : {“must” : [ {“field” : {“name” : “?”}}, {“field” : {“price” : “?”}} ]}}
Or|	findByNameOrPrice|	{“bool” : {“should” : [ {“field” : {“name” : “?”}}, {“field” : {“price” : “?”}} ]}}
Is|	findByName	|{“bool” : {“must” : {“field” : {“name” : “?”}}}}
Not|	findByNameNot|	{“bool” : {“must_not” : {“field” : {“name” : “?”}}}}
Between	|findByPriceBetween|	{“bool” : {“must” : {“range” : {“price” : {“from” : ?,”to” : ?,”include_lower” : true,”include_upper” : true}}}}}
LessThanEqual|	findByPriceLessThan|	{“bool” : {“must” : {“range” : {“price” : {“from” : null,”to” : ?,”include_lower” : true,”include_upper” : true}}}}}
GreaterThanEqual |findByPriceGreaterThan|	{“bool” : {“must” : {“range” : {“price” : {“from” : ?,”to” : null,”include_lower” : true,”include_upper” : true}}}}}
Before|	findByPriceBefore|	{“bool” : {“must” : {“range” : {“price” : {“from” : null,”to” : ?,”include_lower” : true,”include_upper” : true}}}}}
After|	findByPriceAfter|	{“bool” : {“must” : {“range” : {“price” : {“from” : ?,”to” : null,”include_lower” : true,”include_upper” : true}}}}}
Like|	findByNameLike|	{“bool” : {“must” : {“field” : {“name” : {“query” : “? *”,”analyze_wildcard” : true}}}}}
StartingWith|	findByNameStartingWith|	{“bool” : {“must” : {“field” : {“name” : {“query” : “? *”,”analyze_wildcard” : true}}}}}
EndingWith|	findByNameEndingWith|	{“bool” : {“must” : {“field” : {“name” : {“query” : “*?”,”analyze_wildcard” : true}}}}}
Contains/Containing	|findByNameContaining|	{“bool” : {“must” : {“field” : {“name” : {“query” : “?”,”analyze_wildcard” : true}}}}}
In|	findByNameIn(Collectionnames)|	{“bool” : {“must” : {“bool” : {“should” : [ {“field” : {“name” : “?”}}, {“field” : {“name” : “?”}} ]}}}}
NotIn|	findByNameNotIn(Collectionnames)|	{“bool” : {“must_not” : {“bool” : {“should” : {“field” : {“name” : “?”}}}}}}
True|	findByAvailableTrue	|{“bool” : {“must” : {“field” : {“available” : true}}}}
False|	findByAvailableFalse|	{“bool” : {“must” : {“field” : {“available” : false}}}}
OrderBy	|findByAvailableTrueOrderByNameDesc	|{“sort” : [{ “name” : {“order” : “desc”} }],”bool” : {“must” : {“field” : {“available” : true}}}}

## 再来一个分页的例子


### QuestionElasticsearchRepository

```
package com.gf.repository;

import com.gf.entity.FQuestionElasticssearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface QuestionElasticsearchRepository extends ElasticsearchRepository<FQuestionElasticssearch, Long> {

    Page<FQuestionElasticssearch> findByCatory(String catory, Pageable pageable);

    @Query("{ \"bool\":{ \"must\":[ { \"multi_match\": { \"query\": \"?0\", \"type\": \"most_fields\", \"fields\": [ \"title\", \"content\" ] } }, { \"match\": { \"catory\": \"?1\" } } ] } } ")
    Page<FQuestionElasticssearch> searchByKeyWordsAndCatory(String keyword, String catory, Pageable pageable);

}

```

### 实体 FQuestionElasticssearch

```
package com.gf.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "pomit", type = "issue", createIndex = false)
public class FQuestionElasticssearch {
    @Id
    private Long id;
    private String title;
    private String catory;
    private String content;

    public FQuestionElasticssearch() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCatory(String catory) {
        this.catory = catory;
    }

    public String getCatory() {
        return catory;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
```
### Service

```
package com.gf.service;

import com.gf.entity.FQuestionElasticssearch;
import com.gf.repository.QuestionElasticsearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class QuestionElasticsearchService {
    @Autowired
    QuestionElasticsearchRepository questionElasticsearchRepository;

    public Page<FQuestionElasticssearch> pageByOpenAndCatory(Integer page, Integer size, String catory,
                                                             String keyWord) {
        Pageable pageable = PageRequest.of(page, size);
        if (StringUtils.isEmpty(keyWord)) {
            return questionElasticsearchRepository.findByCatory(catory, pageable);

        } else {
            return questionElasticsearchRepository.searchByKeyWordsAndCatory(keyWord, catory, pageable);
        }
    }
}

```

### Controller

```
package com.gf.controller;

import com.gf.entity.FQuestionElasticssearch;
import com.gf.service.QuestionElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/elsearch")
public class ElasticsearchRest {

    @Autowired
    QuestionElasticsearchService questionElasticsearchService;

    @RequestMapping(value = "/test", method = { RequestMethod.GET })
    public List<FQuestionElasticssearch> test(@RequestParam(value = "value", required = false) String value) {
        return questionElasticsearchService.pageByOpenAndCatory(0, 10, "Spring专题", value).getContent();
    }
}
```

结果

![](https://user-gold-cdn.xitu.io/2020/1/3/16f6add0fa001ebe?w=711&h=254&f=png&s=12007)

这个里面我具体来说说

```

    @Query("{ \"bool\":{ \"must\":[ { \"multi_match\": { \"query\": \"?0\", \"type\": \"most_fields\", \"fields\": [ \"title\", \"content\" ] } }, { \"match\": { \"catory\": \"?1\" } } ] } } ")
    Page<FQuestionElasticssearch> searchByKeyWordsAndCatory(String keyword, String catory, Pageable pageable);
```
这个的意思吧，其实这个和所有的springData JPA是一样的，它是为了拼成这样的条件。里面的分页有是有了的

![](https://user-gold-cdn.xitu.io/2020/1/3/16f6adde71dadebb?w=891&h=599&f=png&s=22091)


## 结尾
今天就稍微讲了下这个springDataElasticsearch的增删改查，反正如果是初学的话，就先做到这，要用的时候再去深入吧
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
