# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨 
为了学习Netty，我们前面铺垫了那么多，NIO Java的零拷贝，UNIX的I/O模型等等。
下面是前面系列的链接
- [小六六学Netty系列之Java BIO](https://juejin.im/post/6859336784627646471)
- [小六六学Netty系列之Java NIO(一)](https://juejin.im/post/6860275544655659015)
- [小六六学Netty系列之Java NIO(二)](https://juejin.im/post/6860798575187197965)
- [小六六学Netty系列之unix IO模型](https://juejin.im/post/6862141145553338382)
- [小六六学Netty系列之Java 零拷贝](https://juejin.im/post/6862877857258045453)
- [小六六学Netty系列之初识Netty](https://juejin.im/post/6864749738827186183)

昨天我们看了一个Netty的基本介绍，那么今天我们继续学习Netty

## 异步模型
- 异步的概念和同步相对。当一个异步过程调用发出后，调用者不能立刻得到结果。实际处理这个调用的组件在完成后，通过状态、通知和回调来通知调用者。
- Netty 中的 I/O 操作是异步的，包括 Bind、Write、Connect 等操作会简单的返回一个 ChannelFuture。
调用者并不能立刻获得结果，而是通过 Future-Listener 机制，用户可以方便的主动获取或者通过通知机制获得 IO 操作结果
- Netty 的异步模型是建立在 future 和 callback 的之上的。callback 就是回调。重点说 Future，它的核心思想是：假设一个方法 fun，计算过程可能非常耗时，等待 fun返回显然不合适。那么可以在调用 fun 的时候，立马返回一个 Future，后续可以通过 Future去监控方法 fun 的处理过程(即 ： Future-Listener 机制)

Future 说明


表示异步的执行结果, 可以通过它提供的方法来检测执行是否完成，比如检索计算等等.
ChannelFuture 是一个接口 ： public interface ChannelFuture extends Future<Void>我们可以添加监听器，当监听的事件发生时，就会通知到监听器.
  ![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/12128a1c5b764b8fab32f22dad1ed6cc~tplv-k3u1fbpfcp-zoom-1.image)

  
当 Future 对象刚刚创建时，处于非完成状态，调用者可以通过返回的 ChannelFuture 来获取操作执行的状态，注册监听函数来执行完成后的操作。
常见有如下操作

- 通过 isDone 方法来判断当前操作是否完成；
- 通过 isSuccess 方法来判断已完成的当前操作是否成功；
- 通过 getCause 方法来获取已完成的当前操作失败的原因；
- 通过 isCancelled 方法来判断已完成的当前操作是否被取消；
- 通过 addListener 方法来注册监听器，当操作已完成(isDone 方法返回完成)，将会通知指定的监听器；如果 Future 对象已完成，则通知指定的监听器

  
  ##  使用Netty实现HTTP服务  
目的：Netty 可以做Http服务开发，并且理解Handler实例和客户端及其请求的关系.

- TestHttpServer
```
  package com.liuliu.nio.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/8/18 14:57
 */
public class TestHttpServer {

    public static void main(String[] args)  throws Exception {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup(8);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup).channel(NioServerSocketChannel.class).childHandler(new TestServerInitializer());
            ChannelFuture cf = serverBootstrap.bind(8868).sync();
            cf.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }
}

```
  - TestServerInitializer
  
```
  
  package com.liuliu.nio.http;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/8/18 15:04
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        //向管道加入处理器
        //得到管道
        ChannelPipeline pipeline = ch.pipeline();

        //加入一个netty 提供的httpServerCodec codec =>[coder - decoder]
        //HttpServerCodec 说明
        //1. HttpServerCodec 是netty 提供的处理http的 编-解码器

        pipeline.addLast("MyHttpServerCodec",new HttpServerCodec());


        //2. 增加一个自定义的handler
        pipeline.addLast("MyTestHttpServerHandler", new TestHttpServerHandler());

        System.out.println("ok~~~~");

    }
}

```
  
  - TestHttpServerHandler
```
  
  package com.liuliu.nio.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/8/18 14:56
 *
 * 说明
 * 1. SimpleChannelInboundHandler 是 ChannelInboundHandlerAdapter
 * 2. HttpObject 客户端和服务器端相互通讯的数据被封装成 HttpObject
 *
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {




    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        System.out.println("对应的channel=" + ctx.channel() + " pipeline=" + ctx
                .pipeline() + " 通过pipeline获取channel" + ctx.pipeline().channel());

        System.out.println("当前ctx的handler=" + ctx.handler());

        if (msg instanceof HttpRequest){
            System.out.println("ctx 类型="+ctx.getClass());

            System.out.println("pipeline hashcode" + ctx.pipeline().hashCode() + " TestHttpServerHandler hash=" + this.hashCode());

            System.out.println("msg 类型=" + msg.getClass());
            System.out.println("客户端地址" + ctx.channel().remoteAddress());

            HttpRequest httpRequest=(HttpRequest)msg;
            URI uri = new URI(httpRequest.uri());
            if("/favicon.ico".equals(uri.getPath())) {
                System.out.println("请求了 favicon.ico, 不做响应");
                return;
            }

            //回复信息给浏览器 [http协议]
            ByteBuf content = Unpooled.copiedBuffer("hello,小六六服务器", CharsetUtil.UTF_8);
            //构造一个http的相应，即 httpresponse
            //构造一个http的相应，即 httpresponse
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            //将构建好 response返回
            ctx.writeAndFlush(response);
        }
    }
}

```
  
  - 结果
  
  ![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/debdbec5f58c43e892c3679b13356fee~tplv-k3u1fbpfcp-zoom-1.image)
  
  ![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4144090347554a7986f42e8d9f9d10b1~tplv-k3u1fbpfcp-zoom-1.image)
  
  
## Netty 核心组件一一讲解
  
 大家看上面的案例我估计都很懵逼，其实小六六也是一样的，那接下来我们就要好好的学习学习它的各个组件

### Bootstrap、ServerBootstrap
Bootstrap 意思是引导，一个 Netty 应用通常由一个 Bootstrap 开始，主要作用是配置整个 Netty 程序，串联各个组件，Netty 中 Bootstrap 类是客户端程序的启动引导类，ServerBootstrap 是服务端启动引导类

  常见的方法有
- public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup)，该方法用于服务器端，用来设置两个 EventLoop
- public B group(EventLoopGroup group) ，该方法用于客户端，用来设置一个 EventLoop
- public B channel(Class<? extends C> channelClass)，该方法用来设置一个服务器端的通道实现
- public <T> B option(ChannelOption<T> option, T value)，用来给 ServerChannel 添加配置
- public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value)，用来给接收到的通道添加配置
- public ServerBootstrap childHandler(ChannelHandler childHandler)，该方法用来设置业务处理类（自定义的 handler）
- public ChannelFuture bind(int inetPort) ，该方法用于服务器端，用来设置占用的端口号
- public ChannelFuture connect(String inetHost, int inetPort) ，该方法用于客户端，用来连接服务器端

### Future、ChannelFuture

- Netty 中所有的 IO 操作都是异步的，不能立刻得知消息是否被正确处理。但是可以过一会等它执行完成或者直接注册一个监听，具体的实现就是通过 Future 和 ChannelFutures，他们可以注册一个监听，当操作执行成功或失败时监听会自动触发注册的监听事件

- 常见的方法有
	- Channel channel()，返回当前正在进行 IO 操作的通道
	- ChannelFuture sync()，等待异步操作执行完毕
### Channel
- Netty 网络通信的组件，能够用于执行网络 I/O 操作。
- 通过Channel 可获得当前网络连接的通道的状态
- 通过Channel 可获得 网络连接的配置参数 （例如接收缓冲区大小）
- Channel 提供异步的网络 I/O 操作(如建立连接，读写，绑定端口)，异步调用意味着任何 I/O 调用都将立即返回，并且不保证在调用结束时所请求的 I/O 操作已完成
- 调用立即返回一个 ChannelFuture 实例，通过注册监听器到 ChannelFuture 上，可以 I/O 操作成功、失败或取消时回调通知调用方
- 支持关联 I/O 操作与对应的处理程序
- 不同协议、不同的阻塞类型的连接都有不同的 Channel 类型与之对应，常用的 Channel 类型:

1. NioSocketChannel，异步的客户端 TCP Socket 连接。
2. NioServerSocketChannel，异步的服务器端 TCP Socket 连接。
3. NioDatagramChannel，异步的 UDP 连接。
4. NioSctpChannel，异步的客户端 Sctp 连接。
5. NioSctpServerChannel，异步的 Sctp 服务器端连接，这些通道涵盖了 UDP 和 TCP 网络 IO 以及文件 IO。
### Selector
 Netty 基于 Selector 对象实现 I/O 多路复用，通过 Selector 一个线程可以监听多个连接的 Channel 事件。
当向一个 Selector 中注册 Channel 后，Selector 内部的机制就可以自动不断地查询(Select) 这些注册的 Channel 是否有已就绪的 I/O 事件（例如可读，可写，网络连接完成等），这样程序就可以很简单地使用一个线程高效地管理多个 Channel 


### ChannelHandler 及其实现类

- ChannelHandler 是一个接口，处理 I/O 事件或拦截 I/O 操作，并将其转发到其 ChannelPipeline(业务处理链)中的下一个处理程序。
- ChannelHandler 本身并没有提供很多方法，因为这个接口有许多的方法需要实现，方便使用期间，可以继承它的子类
- ChannelHandler 及其实现类一览图
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/03ad2c4eafd8486ea99d15315e4a891e~tplv-k3u1fbpfcp-zoom-1.image)

- ChannelInboundHandler 用于处理入站 I/O 事件。
- ChannelOutboundHandler 用于处理出站 I/O 操作。
- //适配器
- ChannelInboundHandlerAdapter 用于处理入站 I/O 事件。
- ChannelOutboundHandlerAdapter 用于处理出站 I/O 操作。
- ChannelDuplexHandler 用于处理入站和出站事件。

### ChannelHandler 及其实现类
我们经常需要自定义一个 Handler 类去继承 ChannelInboundHandlerAdapter，然后通过重写相应方法实现业务逻辑，我们接下来看看一般都需要重写哪些方法

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ea6e308373d84e0fa808101f3fc084e9~tplv-k3u1fbpfcp-zoom-1.image)
```
    //通道就绪事件    public void channelActive(ChannelHandlerContext ctx) throws Exception {        ctx.fireChannelActive();    }
    //通道读取数据事件    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {        ctx.fireChannelRead(msg);    }
    //数据读取完毕事件    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {        ctx.fireChannelReadComplete();    }
   //通道发生异常事件    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {        ctx.fireExceptionCaught(cause);    }
```

### Pipeline 和 ChannelPipeline
ChannelPipeline 是一个重点：

- ChannelPipeline 是一个 Handler 的集合，它负责处理和拦截 inbound 或者 outbound 的事件和操作，相当于一个贯穿 Netty 的链。(也可以这样理解：ChannelPipeline 是 保存 ChannelHandler 的 List，用于处理或拦截 Channel 的入站事件和出站操作)

- ChannelPipeline 实现了一种高级形式的拦截过滤器模式，使用户可以完全控制事件的处理方式，以及 Channel 中各个的 ChannelHandler 如何相互交互

- 在 Netty 中每个 Channel 都有且仅有一个 ChannelPipeline 与之对应，它们的组成关系如下
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ce392fd8808d4b4c9d4fd1c7234d5c91~tplv-k3u1fbpfcp-zoom-1.image)

- 一个 Channel 包含了一个 ChannelPipeline，而 ChannelPipeline 中又维护了一个由 ChannelHandlerContext 组成的双向链表，并且每个 ChannelHandlerContext 中又关联着一个 ChannelHandler

- 入站事件和出站事件在一个双向链表中，入站事件会从链表 head 往后传递到最后一个入站的 handler，出站事件会从链表 tail 往前传递到最前一个出站的 handler，两种类型的 handler 互不干扰

常用方法

```
ChannelPipeline addFirst(ChannelHandler... handlers)，把一个业务处理类（handler）添加到链中的第一个位置
ChannelPipeline addLast(ChannelHandler... handlers)，把一个业务处理类（handler）添加到链中的最后一个位置

```

### ChannelHandlerContext

- 保存 Channel 相关的所有上下文信息，同时关联一个 ChannelHandler 对象
- 即ChannelHandlerContext 中 包 含 一 个 具 体 的 事 件 处 理 器 ChannelHandler ， 同 时ChannelHandlerContext 中也绑定了对应的 pipeline 和 Channel 的信息，方便对 ChannelHandler进行调用.

常用方法
```
ChannelFuture close()，关闭通道
ChannelOutboundInvoker flush()，刷新
ChannelFuture writeAndFlush(Object msg) ， 将 数 据 写 到 ChannelPipeline 中 当 前
ChannelHandler 的下一个 ChannelHandler 开始处理（出站）

```

### ChannelOption


Netty 在创建 Channel 实例后,一般都需要设置 ChannelOption 参数。
ChannelOption 参数如下:

```
ChannelOption.SO_BACKLOG
对应 TCP/IP 协议 listen 函数中的 backlog 参数，用来初始化服务器可连接队列大小。服
务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接。多个客户
端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog 参数指定
了队列的大小。

ChannelOption.SO_KEEPALIVE
一直保持连接活动状态

```

### EventLoopGroup 和其实现类 NioEventLoopGroup

```
EventLoopGroup 是一组 EventLoop 的抽象，Netty 为了更好的利用多核 CPU 资源，一般会有多个 EventLoop 同时工作，每个 EventLoop 维护着一个 Selector 实例。

EventLoopGroup 提供 next 接口，可以从组里面按照一定规则获取其中一个 EventLoop来处理任务。在 Netty 服务器端编程中，我们一般都需要提供两个 EventLoopGroup，例如：BossEventLoopGroup 和 WorkerEventLoopGroup。

```

### EventLoopGroup 和其实现类 NioEventLoopGroup
通常一个服务端口即一个 ServerSocketChannel对应一个Selector 和一个EventLoop线程。BossEventLoop 负责接收客户端的连接并将 SocketChannel 交给 WorkerEventLoopGroup 来进行 IO 处理，如下图所示
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7cc8ff4596d541d5bccfb64715cd0679~tplv-k3u1fbpfcp-zoom-1.image)

BossEventLoopGroup 通常是一个单线程的 EventLoop，EventLoop 维护着一个注册了ServerSocketChannel 的 Selector 实例BossEventLoop 不断轮询 Selector 将连接事件分离出来

通常是 OP_ACCEPT 事件，然后将接收到的 SocketChannel 交给 WorkerEventLoopGroup
WorkerEventLoopGroup 会由 next 选择其中一个 EventLoop来将这个 SocketChannel 注册到其维护的 Selector 并对其后续的 IO 事件进行处理


### Unpooled 类
Netty 提供一个专门用来操作缓冲区(即Netty的数据容器)的工具类
常用方法如下所示
```
//通过给定的数据和字符编码返回一个 ByteBuf 对象（类似于 NIO 中的 ByteBuffer 但有区别）

public static ByteBuf copiedBuffer(CharSequence string, Charset charset)

```



## 结尾
反正大家就多熟悉熟悉哈


![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
