# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨
前面一节我们学习了一下eureka,我们来回顾一下，首先它是一个cs架构，分为客户端和服务端，
> 客户端 也分为 生成者和消费者，也就是服务提供方和服务消费方，具体客户端的作用如下
- 当客户端启动的时候向服务端注册当前服务
- 并和服务端维持心跳，用的是后台线程
- 拉取服务端的各个节点集合，然后定时更新服务的信息到本地，因为客户端也是会缓存服务节点信息的
- 当服务挂掉的时候，监听shutdown 然后上报自己挂掉的状态给服务端

> 服务端
- 启动后，从其他节点获取服务注册信息。
- 运行过程中，定时运行evict任务，剔除没有按时renew的服务（包括非正常停止和网络故障的服务）。
- 运行过程中，接收到的register、renew、cancel请求，都会同步至其他注册中心节点，分布式数据同步（AP）
- 运行过程中，自我保护机制。等等

- [SpringCloud原理之eureka]()

## 什么是Feign
Feign是一种声明式、模板化的HTTP客户端(仅在Application Client中使用)。声明式调用是指，就像调用本地方法一样调用远程方法，无需感知操作远程http请求。
Spring Cloud的声明式调用, 可以做到使用 HTTP请求远程服务时能就像调用本地方法一样的体验，开发者完全感知不到这是远程方法，更感知不到这是个HTTP请求。Feign的应用，让Spring Cloud微服务调用像Dubbo一样，Application Client直接通过接口方法调用Application Service，而不需要通过常规的RestTemplate构造请求再解析返回数据。它解决了让开发者调用远程接口就跟调用本地方法一样，无需关注与远程的交互细节，更无需关注分布式环境开发。

Feign是声明性Web服务客户端。它使编写Web服务客户端更加容易。要使用Feign，请创建一个接口并对其进行注释。它具有可插入注释支持，包括Feign注释和JAX-RS注释。Feign还支持可插拔编码器和解码器。Spring Cloud添加了对Spring MVC注释的支持，并支持使用HttpMessageConvertersSpring Web中默认使用的注释。当使用Feign时，Spring Cloud集成了Ribbon和Eureka以提供负载平衡的http客户端。



##  使用Feign开发时的应用部署结构
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7946fe2fc0b445b1b60620ed3f7e92f3~tplv-k3u1fbpfcp-zoom-1.image)


## Feign是如何设计的？

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7c960439c2294291b573991ec1aa67af~tplv-k3u1fbpfcp-zoom-1.image)


## 原生的Feign

虽然我们用SpringCloud全家桶比较多，但是其实呢?他只是对原生的fegin做了一些封装，所以刨根问底的话，我们还是多了解了解原生的Fegin,对于我们理解Spring Cloud feign是很有帮助的


![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5c27dcde93b346f7a9b9344f6f556c3c~tplv-k3u1fbpfcp-zoom-1.image)


### Feign使用简介
#### 基本用法

基本的使用如下所示，一个对于canonical Retrofit sample的适配。

```
interface GitHub {
 // RequestLine注解声明请求方法和请求地址,可以允许有查询参数
 @RequestLine("GET /repos/{owner}/{repo}/contributors")
 List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);
}
static class Contributor {
 String login;
 int contributions;
}
public static void main(String... args) {
 GitHub github = Feign.builder()
            .decoder(new GsonDecoder())
            .target(GitHub.class, "https://api.github.com");
 // Fetch and print a list of the contributors to this library.
 List<Contributor> contributors = github.contributors("OpenFeign", "feign");
 for (Contributor contributor : contributors) {
  System.out.println(contributor.login + " (" + contributor.contributions + ")");
 }
}
```

#### 自定义
Feign 有许多可以自定义的方面。举个简单的例子，你可以使用 Feign.builder() 来构造一个拥有你自己组件的API接口。如下:

```
interface Bank {
 @RequestLine("POST /account/{id}")
 Account getAccountInfo(@Param("id") String id);
}
...
// AccountDecoder() 是自己实现的一个Decoder
Bank bank = Feign.builder().decoder(new AccountDecoder()).target(Bank.class, https://api.examplebank.com);
```

### Feign 动态代理
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/50a7a4b351af464d85c1ac07df84130b~tplv-k3u1fbpfcp-zoom-1.image)

Feign 的默认实现是 ReflectiveFeign，通过 Feign.Builder 构建。再看代码前，先了解一下 Target 这个对象。

```
public interface Target<T> {
  // 接口的类型
  Class<T> type();

  // 代理对象的名称，默认为url,负载均衡时有用
  String name();
  // 请求的url地址，eg: https://api/v2
  String url();
}
```
其中 Target.type 是用来生成代理对象的，url 是 Client 对象发送请求的地址。

```
public Feign build() {
    // client 有三种实现 JdkHttp/ApacheHttp/okHttp，默认是 jdk 的实现
    SynchronousMethodHandler.Factory synchronousMethodHandlerFactory =
        new SynchronousMethodHandler.Factory(client, retryer, requestInterceptors, logger,
                                             logLevel, decode404, closeAfterDecode, propagationPolicy);
    ParseHandlersByName handlersByName =
        new ParseHandlersByName(contract, options, encoder, decoder, queryMapEncoder,
                                errorDecoder, synchronousMethodHandlerFactory);
    return new ReflectiveFeign(handlersByName, invocationHandlerFactory, queryMapEncoder);
}
```

总结： 介绍一下几个主要的参数：

- Client 这个没什么可说的，有三种实现 JdkHttp/ApacheHttp/okHttp
- RequestInterceptor 请求拦截器
- Contract REST 注解解析器，默认为 Contract.Default()，即支持 Feign 的原生注解。
- InvocationHandlerFactory 生成 JDK 动态代理，实际执行是委托给了 MethodHandler。


### 生成代理对象
```
public <T> T newInstance(Target<T> target) {
    // 1. Contract 将 target.type 接口类上的方法和注解解析成 MethodMetadata，
    //    并转换成内部的MethodHandler处理方式
    Map<String, MethodHandler> nameToHandler = targetToHandlersByName.apply(target);
    Map<Method, MethodHandler> methodToHandler = new LinkedHashMap<Method, MethodHandler>();
    List<DefaultMethodHandler> defaultMethodHandlers = new LinkedList<DefaultMethodHandler>();

    for (Method method : target.type().getMethods()) {
        if (method.getDeclaringClass() == Object.class) {
            continue;
        } else if (Util.isDefault(method)) {
            DefaultMethodHandler handler = new DefaultMethodHandler(method);
            defaultMethodHandlers.add(handler);
            methodToHandler.put(method, handler);
        } else {
            methodToHandler.put(method, nameToHandler.get(Feign.configKey(target.type(), method)));
        }
    }

    // 2. 生成 target.type 的 jdk 动态代理对象
    InvocationHandler handler = factory.create(target, methodToHandler);
    T proxy = (T) Proxy.newProxyInstance(target.type().getClassLoader(),
                                         new Class<?>[]{target.type()}, handler);

    for (DefaultMethodHandler defaultMethodHandler : defaultMethodHandlers) {
        defaultMethodHandler.bindTo(proxy);
    }
    return proxy;
}
```
总结： newInstance 生成了 JDK 的动态代理，从 factory.create(target, methodToHandler) 也可以看出 InvocationHandler 实际委托给了 methodToHandler。methodToHandler 默认是 SynchronousMethodHandler.Factory 工厂类创建的。

### MethodHandler 方法执行器

ParseHandlersByName.apply 生成了每个方法的执行器 MethodHandler，其中最重要的一步就是通过 Contract 解析 MethodMetadata。

```
public Map<String, MethodHandler> apply(Target key) {
    // 1. contract 将接口类中的方法和注解解析 MethodMetadata
    List<MethodMetadata> metadata = contract.parseAndValidatateMetadata(key.type());
    Map<String, MethodHandler> result = new LinkedHashMap<String, MethodHandler>();
    for (MethodMetadata md : metadata) {
        // 2. buildTemplate 实际上将 Method 方法的参数转换成 Request
        BuildTemplateByResolvingArgs buildTemplate;
        if (!md.formParams().isEmpty() && md.template().bodyTemplate() == null) {
            // 2.1 表单
            buildTemplate = new BuildFormEncodedTemplateFromArgs(md, encoder, queryMapEncoder);
        } else if (md.bodyIndex() != null) {
            // 2.2 @Body 注解
            buildTemplate = new BuildEncodedTemplateFromArgs(md, encoder, queryMapEncoder);
        } else {
            // 2.3 其余
            buildTemplate = new BuildTemplateByResolvingArgs(md, queryMapEncoder);
        }
        // 3. 将 metadata 和 buildTemplate 封装成 MethodHandler
        result.put(md.configKey(),
                   factory.create(key, md, buildTemplate, options, decoder, errorDecoder));
    }
    return result;
}
```

总结： 这个方法由以下几步：

Contract 统一将方法解析 MethodMetadata(*)，这样就可以通过实现不同的 Contract 适配各种 REST 声明式规范。
buildTemplate 实际上将 Method 方法的参数转换成 Request。
将 metadata 和 buildTemplate 封装成 MethodHandler。


这样通过以上三步就创建了一个 Target.type 的代理对象 proxy，这个代理对象就可以像访问普通方法一样发送 Http 请求，其实和 RPC 的 Stub 模型是一样的。了解 proxy 后，其执行过程其实也就一模了然。

### Feign 调用过程

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e22cb76598d54125b419bd179cad00ba~tplv-k3u1fbpfcp-zoom-1.image)

#### FeignInvocationHandler#invoke
```
private final Map<Method, MethodHandler> dispatch;
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    ...
    // 每个Method方法对应一个MethodHandler
    return dispatch.get(method).invoke(args);
}
```
总结： 和上面的结论一样，实际的执行逻辑实际上是委托给了 MethodHandler。

#### SynchronousMethodHandler#invoke

```
// 发起 http 请求，并根据 retryer 进行重试
public Object invoke(Object[] argv) throws Throwable {
    // template 将 argv 参数构建成 Request
    RequestTemplate template = buildTemplateFromArgs.create(argv);
    Options options = findOptions(argv);
    Retryer retryer = this.retryer.clone();

    // 调用client.execute(request, options)
    while (true) {
        try {
            return executeAndDecode(template, options);
        } catch (RetryableException e) {
            try {
                // 重试机制
                retryer.continueOrPropagate(e);
            } catch (RetryableException th) {
                ...
            }
            continue;
        }
    }
}
```

总结： invoke 主要进行请求失败的重试机制，至于具体执行过程委托给了 executeAndDecode 方法。


```
// 一是编码生成Request；二是http请求；三是解码生成Response
Object executeAndDecode(RequestTemplate template, Options options) throws Throwable {
    // 1. 调用拦截器 RequestInterceptor，并根据 template 生成 Request
    Request request = targetRequest(template);
    // 2. http 请求
    Response response = client.execute(request, options);
	// 3. response 解码
    if (Response.class == metadata.returnType()) {
        byte[] bodyData = Util.toByteArray(response.body().asInputStream());
        return response.toBuilder().body(bodyData).build();
    }
    ...
}

Request targetRequest(RequestTemplate template) {
    // 执行拦截器
    for (RequestInterceptor interceptor : requestInterceptors) {
        interceptor.apply(template);
    }
    // 生成 Request
    return target.apply(template);
```

**这个是原生feign的调用过程，总的来说分为2部 一个是 客户端的封装，一个调用方法的封装**



## Spring Cloud Feign 的原理解析

我们前面看了原生的feign之后呢？对于Spring Cloud的Feign的话理解起来就很简单了，我们知道Spring cloud 是基于SpringBoot SpringBoot 又是基于Spring,那么Spring就是一个胶水框架，它就是把各个组件把它封装起来，所以呢，这样就简单很多了嘛


小六六在这边就不一一的给大家演示SpringCloud 是如何使用Feign的了，小六六默认大家都懂，哈哈，那么就直接说原理吧

### 工作原理
我们来想想平时我们使用feign的时候，会是一个怎么样的流程

- 添加了 Spring Cloud OpenFeign 的依赖
- 在 SpringBoot 启动类上添加了注解 @EnableFeignCleints
- 按照 Feign 的规则定义接口 DemoService， 添加@FeignClient 注解
- 在需要使用 Feign 接口 DemoService 的地方， 直接利用@Autowire 进行注入
- 使用接口完成对服务端的调用

那我们基于这些步骤来分析分析，本文并不会说非常深入去看每一行的源码
- SpringBoot 应用启动时， 由针对 @EnableFeignClient 这一注解的处理逻辑触发程序扫描 classPath中所有被@FeignClient 注解的类， 这里以 XiaoLiuLiuService 为例， 将这些类解析为 BeanDefinition 注册到 Spring 容器中
- Sping 容器在为某些用的 Feign 接口的 Bean 注入 XiaoLiuLiuService 时， Spring 会尝试从容器中查找 XiaoLiuLiuService 的实现类
- 由于我们从来没有编写过 XiaoLiuLiuService 的实现类， 上面步骤获取到的 XiaoLiuLiuService 的实现类必然是 feign 框架通过扩展 spring 的 Bean 处理逻辑， 为 XiaoLiuLiuService 创建一个动态接口代理对象， 这里我们将其称为 XiaoLiuLiuServiceProxy 注册到spring 容器中。
- Spring 最终在使用到 XiaoLiuLiuService 的 Bean 中注入了 XiaoLiuLiuServiceProxy 这一实例。
- 当业务请求真实发生时， 对于 XiaoLiuLiuService 的调用被统一转发到了由 Feign 框架实现的 InvocationHandler 中， InvocationHandler 负责将接口中的入参转换为 HTTP 的形式， 发到服务端， 最后再解析 HTTP 响应， 将结果转换为 Java 对象， 予以返回。

**所以我们基于原生的feign来分析分析，其实就是多了2步，前面的原生feign会帮助我们生成代理对象，这个是我们调用方法的主体，也是这个代理对象才有能力去请求http请求，那么spring就想办法，把这一类的对象放到spring的上下文中，那么我们下次调用的时候，这个对象当然就有了http请求的能力了。**


