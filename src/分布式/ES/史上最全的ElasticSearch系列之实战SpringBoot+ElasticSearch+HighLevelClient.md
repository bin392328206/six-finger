# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨
昨天把简单的crud 给干完了，本来想讲讲原理的东西,但是我一想，大家都是才入门。就讲那写不好，所以我这边接着昨天的crud,把单机的搜索讲完，今天我们先讲讲Java的客户端HighLevelClient，其实我觉得应该很少人用它，但是我们公司用，所以我就写一下，明天再把JPA的方式也写一遍 
下面是前面的系列文章  
- [🔥史上最全的ElasticSearch系列之入门](https://juejin.im/post/5e04613ff265da33ee17944b)
- [🔥史上最全的ElasticSearch系列之基础（一）](https://juejin.im/post/5e06cd12e51d45583e4dd495)

- [🔥史上最全的ElasticSearch系列之基础（二）](https://juejin.im/post/5e071debf265da33c90b56c7)


## 项目结构

![](https://user-gold-cdn.xitu.io/2020/1/3/16f6972cc465d64f?w=1088&h=587&f=png&s=88764)

## pom 文件

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>com</groupId>
	<artifactId>es</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>es</name>
	<url>http://maven.apache.org</url>

	<properties>
		<java.version>1.8</java.version>
		<elasticsearch.version>6.4.3</elasticsearch.version>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	</properties>

	<!-- Spring boot 父引用 -->
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.5.3.RELEASE</version>
	</parent>

	<dependencies>
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
		</dependency>

		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>fastjson</artifactId>
			<version>1.2.54</version>
		</dependency>

		<dependency>
			<groupId>org.elasticsearch</groupId>
			<artifactId>elasticsearch</artifactId>
		</dependency>
		<dependency>
			<groupId>org.elasticsearch.client</groupId>
			<artifactId>elasticsearch-rest-high-level-client</artifactId>
			<version>6.4.3</version>
		</dependency>

	</dependencies>
</project>

```

这里主要是设置项目的最基本的东西springboot环境 high level client 版本

## config 连接es的配置

```
package com.es.config;

import java.util.ArrayList;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfiguration {

	private static String hosts = "192.168.62.145"; // 集群地址，多个用,隔开
	private static int port = 9200; // 使用的端口号
	private static String schema = "http"; // 使用的协议
	private static ArrayList<HttpHost> hostList = null;

	private static int connectTimeOut = 1000; // 连接超时时间
	private static int socketTimeOut = 30000; // 连接超时时间
	private static int connectionRequestTimeOut = 500; // 获取连接的超时时间

	private static int maxConnectNum = 100; // 最大连接数
	private static int maxConnectPerRoute = 100; // 最大路由连接数

	private RestClientBuilder builder;

	static {
		hostList = new ArrayList<>();
		String[] hostStrs = hosts.split(",");
		for (String host : hostStrs) {
			hostList.add(new HttpHost(host, port, schema));
		}
	}

	@Bean
	public RestHighLevelClient client() {
		builder = RestClient.builder(hostList.toArray(new HttpHost[0]));
		setConnectTimeOutConfig();
		setMutiConnectConfig();
		RestHighLevelClient client = new RestHighLevelClient(builder);
		return client;
	}

	// 异步httpclient的连接延时配置
	public void setConnectTimeOutConfig() {
		builder.setRequestConfigCallback(new RequestConfigCallback() {

			@Override
			public Builder customizeRequestConfig(Builder requestConfigBuilder) {
				requestConfigBuilder.setConnectTimeout(connectTimeOut);
				requestConfigBuilder.setSocketTimeout(socketTimeOut);
				requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeOut);
				return requestConfigBuilder;
			}
		});
	}

	// 异步httpclient的连接数配置
	public void setMutiConnectConfig() {
		builder.setHttpClientConfigCallback(new HttpClientConfigCallback() {

			@Override
			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
				httpClientBuilder.setMaxConnTotal(maxConnectNum);
				httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
				return httpClientBuilder;
			}
		});
	}

}
```

## 创建一个启动类EsApp

```
package com.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EsApp {
	public static void main(String[] args) {
		SpringApplication.run(EsApp.class, args);
	}
}
```

## 创建一个bean

```
package com.es.bean;

public class Tests {
	
	private Long id;

	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Tests [id=" + id + ", name=" + name + "]";
	}
	
}

```

## 测试方法

###  创建一个索引


```

	@Autowired
	private RestHighLevelClient client;
	/**
	 * 创建索引
	 * @param
	 * @throws IOException
	 */
	@Test
	public void createIndex() throws IOException {
		CreateIndexRequest request = new CreateIndexRequest("六脉神剑");
		CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
		System.out.println("createIndex: " + JSON.toJSONString(createIndexResponse));
	}
```


![](https://user-gold-cdn.xitu.io/2020/1/3/16f69748a6586e6e?w=1480&h=449&f=png&s=77515)

![](https://user-gold-cdn.xitu.io/2020/1/3/16f6974d56cb68aa?w=475&h=345&f=png&s=25437)

### 判断索引是否存在

```
	@Test
	public void existsIndex() throws IOException {
		GetIndexRequest request = new GetIndexRequest();
		request.indices("六脉神剑");
		boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
		System.out.println("existsIndex: " + exists);
	}
```

![](https://user-gold-cdn.xitu.io/2020/1/3/16f6976adb298f60?w=284&h=59&f=png&s=4892)

### 往索引里面添加一个数据，也就是一个文档


```

	@BeforeClass
	public static void before() {
		INDEX_TEST = "index_test";
		TYPE_TEST = "type_test";
		testsList = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			tests = new Tests();
			tests.setId(Long.valueOf(i));
			tests.setName("this is the test " + i);
			testsList.add(tests);
		}
	}

	@Test
	public void add() throws IOException {
		IndexRequest indexRequest = new IndexRequest("六脉神剑", "六脉神剑", tests.getId().toString());
		indexRequest.source(JSON.toJSONString(tests), XContentType.JSON);
		IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
		System.out.println("add: " + JSON.toJSONString(indexResponse));
	}
```

这个beforeClass 是再测试方法之前把对象创建出来

![](https://user-gold-cdn.xitu.io/2020/1/3/16f697a570afcc7a?w=1194&h=477&f=png&s=74881)

### 查询一个文档

```
	public void get() throws IOException {
		GetRequest getRequest = new GetRequest("六脉神剑", "六脉神剑", "99");
		GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
		System.out.println("get: " + JSON.toJSONString(getResponse));
	}
```

![](https://user-gold-cdn.xitu.io/2020/1/3/16f697ce0d72d309?w=984&h=752&f=png&s=36384)

### 更新记录信息

```
	@Test
	public void update() throws IOException {
		tests.setName(tests.getName() + "updated");
		UpdateRequest request = new UpdateRequest("六脉神剑", "六脉神剑", tests.getId().toString());
		request.doc(JSON.toJSONString(tests), XContentType.JSON);
		UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
		System.out.println("update: " + JSON.toJSONString(updateResponse));
	}
```

![](https://user-gold-cdn.xitu.io/2020/1/3/16f6980e125d9e10?w=725&h=333&f=png&s=18788)

## 搜索

```
	@Test
	public void search() throws IOException {
		BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
		boolBuilder.must(QueryBuilders.matchQuery("name", "is"));
		// boolBuilder.must(QueryBuilders.matchQuery("id", tests.getId().toString()));
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(boolBuilder);
		sourceBuilder.from(0);
		sourceBuilder.size(100); // 获取记录数，默认10
		sourceBuilder.fetchSource(new String[] { "id", "name" }, new String[] {}); // 第一个是获取字段，第二个是过滤的字段，默认获取全部
		SearchRequest searchRequest = new SearchRequest("六脉神剑");
		searchRequest.types("六脉神剑");
		searchRequest.source(sourceBuilder);
		SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
		System.out.println("search: " + JSON.toJSONString(response));
		SearchHits hits = response.getHits();
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
			System.out.println("search -> " + hit.getSourceAsString());
		}
	}
```

![](https://user-gold-cdn.xitu.io/2020/1/3/16f698476303f45f?w=1077&h=735&f=png&s=34865)

![](https://user-gold-cdn.xitu.io/2020/1/3/16f6984c5e574c48?w=1474&h=337&f=png&s=43285)

### 批量操作

```
	@Test
	public void bulk() throws IOException {
		// 批量增加
		BulkRequest bulkAddRequest = new BulkRequest();
		for (int i = 0; i < testsList.size(); i++) {
			tests = testsList.get(i);
			IndexRequest indexRequest = new IndexRequest(INDEX_TEST, TYPE_TEST, tests.getId().toString());
			indexRequest.source(JSON.toJSONString(tests), XContentType.JSON);
			bulkAddRequest.add(indexRequest);
		}
		BulkResponse bulkAddResponse = client.bulk(bulkAddRequest, RequestOptions.DEFAULT);
		System.out.println("bulkAdd: " + JSON.toJSONString(bulkAddResponse));
		search(INDEX_TEST, TYPE_TEST, "this");
		
		// 批量更新
		BulkRequest bulkUpdateRequest = new BulkRequest();
		for (int i = 0; i < testsList.size(); i++) {
			tests = testsList.get(i);
			tests.setName(tests.getName() + " updated");
			UpdateRequest updateRequest = new UpdateRequest(INDEX_TEST, TYPE_TEST, tests.getId().toString());
			updateRequest.doc(JSON.toJSONString(tests), XContentType.JSON);
			bulkUpdateRequest.add(updateRequest);
		}
		BulkResponse bulkUpdateResponse = client.bulk(bulkUpdateRequest, RequestOptions.DEFAULT);
		System.out.println("bulkUpdate: " + JSON.toJSONString(bulkUpdateResponse));
		search(INDEX_TEST, TYPE_TEST, "updated");
		
		// 批量删除
		BulkRequest bulkDeleteRequest = new BulkRequest();
		for (int i = 0; i < testsList.size(); i++) {
			tests = testsList.get(i);
			DeleteRequest deleteRequest = new DeleteRequest(INDEX_TEST, TYPE_TEST, tests.getId().toString());
			bulkDeleteRequest.add(deleteRequest);
		}
		BulkResponse bulkDeleteResponse = client.bulk(bulkDeleteRequest, RequestOptions.DEFAULT);
		System.out.println("bulkDelete: " + JSON.toJSONString(bulkDeleteResponse));
		search(INDEX_TEST, TYPE_TEST, "this");
	}
```

## 结尾
今天就稍微讲了下这个的增删改查，只能说带大家入个门，后面的学习还是要靠自己了.
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
