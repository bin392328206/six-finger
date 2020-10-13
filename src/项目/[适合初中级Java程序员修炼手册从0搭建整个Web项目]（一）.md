# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206                          
> **种一棵树最好的时间是十年前，其次是现在**   


# six-finger-web
一个Web后端框架的轮子从处理Http请求【基于Netty的请求级Web服务器】 到mvc【接口封装转发)】，再到ioc【依赖注入】，aop【切面】，再到 rpc【远程过程调用】最后到orm【数据库操作】全部自己撸一个（简易）的轮子。

[github](https://github.com/bin392328206/six-finger-web)

## 为啥要写这个轮子
其实是这样的，小六六自己平时呢？有时候喜欢看看人家的源码比如Spring,但是小六六的水平可能不怎么样，每次看都看得晕头转向，然后就感觉里面的细节太难了，然后我就只能观其总体的思想，然后我就想我如果可以根据各位前辈的一些思考，自己撸一个简单的轮子出来，那我后面去理解作者的思想是不是简单点呢？于是呢 six-finger-web就面世了，它其实就是我的一个学习过程，然后我把它开源出来，希望能帮助那些对于学习源码有困难的同学。还有就是可以锻炼一下自己的编码能力，因为平时我们总是crud用的Java api都是那些，久而久之，很多框架类的api我们根本就不熟练了，所以借此机会，锻炼一下。

## 特点
- 内置由 Netty 编写 HTTP 服务器，无需额外依赖 Tomcat 之类的 web 服务（刚好小六六把Netty系列写完，顺便用下）
- 代码简单易懂（小六六自己写不出框架大佬那种高类聚，低耦合的代码），能力稍微强一点看代码就能懂，弱点的也没关系，小六六有配套的从0搭建教程。
- 支持MVC相关的注解确保和SpringMVC的用法类似
- 支持Spring IOC 和Aop相关功能
- 支持类似于Mybatis相关功能
- 支持类似于Dubbo的rpc相关功能
- 对于数据返回，只支持Json格式


## 絮叨
此教程只适合初中级水平，因为作者本身水平不高，不喜勿喷，今天是文章的第一篇，所以先写的是 由Netty 搭建一个http服务器

## 使用Netty实现HTTP服务器

Netty是一个异步事件驱动的网络应用程序框架用于快速开发可维护的高性能协议服务器和客户端。Netty经过精心设计，具有丰富的协议，如FTP，SMTP，HTTP以及各种二进制和基于文本的传统协议。

Java程序员在开发web应用的时候,我们习惯于基于servlet规范，来做后端开发，就比如我们的SpringMVC其本质也是一个servlet,至于spring Webfux，我不知道有多少公司使用了，但是目前为止2020，我们公司是没有使用的，这次呢我们就试试用Netty来实现一下，其实这个很简单，以前的我写Netty系列的时候，我已经写过了，大家可以去找找https://github.com/bin392328206/six-finger

## 首先是创建项目
因为我们这个是six-finger-web的第一篇，所以我尽量把点点滴滴做到
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8907356f7224416a8ca374dd3b4bd416~tplv-k3u1fbpfcp-watermark.webp)

首先创建一个maven项目，如果这个都不会的话，小六六建议先学习基础再来，在文章很多的地方，一些基础的小六六是默认你懂，如果有啥不懂的可以上github上找我联系方式，我如果有空会给大家解答的

- 创建pom.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.xiaoliuliu</groupId>
    <artifactId>six-finger-web</artifactId>
    <packaging>jar</packaging>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <!-- 为了代码简洁引入lombok,不需要再写setter和getter(可以不引入)-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.12</version>
            <scope>provided</scope>
        </dependency>
        <!--动态代理相关-->
        <dependency>
            <groupId>cglib</groupId>
            <artifactId>cglib</artifactId>
            <version>3.1</version>
        </dependency>

        <!-- Netty相关-->
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.1.51.Final</version>
        </dependency>

    </dependencies>
</project>
```

### HttpServer

Netty 编写 HTTP 服务器主类

```
package com.xiaoliuliu.six.finger.web.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/13 11:41
 * Netty 编写 HTTP 服务器
 * 主类
 */
public class HttpServer {

    /**
     * @Des 端口 http请求的端口
     * @Author 小六六
     * @Date 2020/10/13 11:42
     * @Param
     * @Return
     */
    int port;


    /**
     * @Des 构造方法
     * @Author 小六六
     * @Date 2020/10/13 11:42
     * @Param
     * @Return
     */
    public HttpServer(int port) {
        this.port = port;
    }

    /**
     * @Des 服务的启动方法
     * @Author 小六六
     * @Date 2020/10/13 11:43
     * @Param
     * @Return
     */
    public void start() throws Exception {
        //启动引导类
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();

        bootstrap.group(boss, work)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerInitializer());

        ChannelFuture cf = bootstrap.bind(new InetSocketAddress(port)).sync();
        System.out.println(" server start up on port : " + port);
        cf.channel().closeFuture().sync();
    }
}

```

### HttpServerInitializer
```
package com.xiaoliuliu.six.finger.web.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/13 11:57
 * 用于配置 pipeline的处理链
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // http 编解码
        pipeline.addLast(new HttpServerCodec());
        // http 消息聚合器
        pipeline.addLast("httpAggregator",new HttpObjectAggregator(512*1024));
        // 请求处理器
        pipeline.addLast(new HttpRequestHandler());
    }
}
```

### HttpRequestHandler

```
package com.xiaoliuliu.six.finger.web.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/13 12:01
 * 核心处理http请求的类，包括url的匹配核心方法都是在channelRead0方法
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String FAVICON_ICO = "/favicon.ico";
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        System.out.println("获得的参数:"+req);
        if (is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.CONTINUE));
        }
        // 获取请求的uri
        String uri = req.uri();
        Map<String,String> resMap = new HashMap<>();
        resMap.put("method",req.method().name());
        resMap.put("uri",uri);
        String msg = "<html><head><title>小六六提醒你</title></head><body>你请求uri为：" + uri+"</body></html>";
        // 创建http响应
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));

        //设置头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        //把消息输出到浏览器
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

```

### ApplicationServer 测试类

```
package com.xiaoliuliu.six.finger.web.demo.server;

import com.xiaoliuliu.six.finger.web.server.HttpServer;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/13 14:26
 *  这个类 用于 搭建Netty web服务器的测试类，只适用于搭建教程的第一篇文章
 */
public class ApplicationServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = new HttpServer(8081);// 8081为启动端口
        server.start();
    }
}
```

### 测试结果

在浏览器上输入
> http://localhost:8081/xiaoliuliu

我们看看输出

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/150b010c52bc4668a30ad999e7645e6d~tplv-k3u1fbpfcp-watermark.webp)

然后我们来看看控制台

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/258e52a983b946c5a2ba7e76f82c1b23~tplv-k3u1fbpfcp-watermark.webp)

发现多了一次请求，这个是什么原因呢？

这是因为HttpRequestDecoder把请求拆分成HttpRequest和HttpContent两部分,

所以我们要过滤哪个/favicon.ico的请求，所以改改代码

```
    if("/favicon.ico".equals(uri)) {
            System.out.println("请求了 favicon.ico, 不做响应");
            return;
        }
```



## 结尾
好了，今天我们用几十行代码实现了一个简单的Http服务器，很多的细节我们一一讲解，但是我的注释基本上都写了，如果你有看不懂的地方，欢迎你来找我，我有空会给大家解答的，然后下一章就是我们要写的SpringMVC相关的代码了。

![](//p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/14aafd3e9f1c4db8b62fe1e5baaaac4b~tplv-k3u1fbpfcp-zoom-1.image)
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！