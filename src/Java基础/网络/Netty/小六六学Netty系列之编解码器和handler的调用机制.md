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
- [小六六学Netty系列之再遇Netty](https://juejin.im/post/6865910744739741703)
- [小六六学Netty系列之Netty群聊](https://juejin.im/post/6868144990603640846)

虽然说不至于学会了很多的东西，但是一遍走下来，至少对Netty 有了一点基本的认识，我相信如果你要再去使用它，或者深入它，这些基础肯定是有用的，我们继续加油。



## Google Protobuf 

### 编码和解码的基本介绍

- 编写网络应用程序时，因为数据在网络中传输的都是二进制字节码数据，在发送数据时就需要编码，接收数据时就需要解码 

- codec(编解码器) 的组成部分有两个：decoder(解码器)和 encoder(编码器)。encoder 负责把业务数据转换成字节码数据，decoder 负责把字节码数据转换成业务数据

### Netty 本身的编码解码的机制和问题分析
- Netty 自身提供了一些 codec(编解码器)
- Netty 提供的编码器
  - StringEncoder，对字符串数据进行编码
  - ObjectEncoder，对 Java 对象进行编码
- Netty 提供的解码器
  - StringDecoder, 对字符串数据进行解码
  - ObjectDecoder，对 Java 对象进行解码
- Netty 本身自带的 ObjectDecoder 和 ObjectEncoder 可以用来实现 POJO 对象或各种业务对象的编码和解码，底层使用的仍是 Java 序列化技术 , 而Java 序列化技术本身效率就不高，存在如下问题
  - 无法跨语言
  - 序列化后的体积太大，是二进制编码的 5 倍多。
  - 序列化性能太低
=> 引出 新的解决方案 [Google 的 Protobuf] 

## Java序列化

这个知识点比较重要，所以小六六重新整理了一篇文章给大家哦


## Protobuf
### Protobuf基本介绍

- Protobuf 是 Google 发布的开源项目，全称 Google Protocol Buffers，是一种轻便高效的结构化数据存储格式，可以用于结构化数据串行化，或者说序列化。它很适合做数据存储或 RPC[远程过程调用  remote procedure call ] 数据交换格式 。目前很多公司 http+json 转成 tcp+protobuf
- 参考文档 : https://developers.google.com/protocol-buffers/docs/proto   语言指南
- Protobuf 是以 message 的方式来管理数据的
支持跨平台、跨语言，即[客户端和服务器端可以是不同的语言编写的] （支持目前绝大多数语言，例如 C++、C#、Java、python 等）
- 高性能，高可靠性
- 使用 protobuf 编译器能自动生成代码，Protobuf 是将类的定义使用.proto 文件进行描述。说明，在idea 中编写 .proto 文件时，会自动提示是否下载 .ptotot 编写插件. 可以让语法高亮。
- 然后通过 protoc.exe 编译器根据.proto 自动生成.java 文件
- protobuf 使用示意图
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/222aae8a4ee64d37803b9e727936087b~tplv-k3u1fbpfcp-zoom-1.image)

## 具体使用
小六六这边就不一一举例说明了，毕竟我也是第一次接触嘛，先了解了解，后面用到了就去找案例就好了，稍微说一下

先写一个这样的执行文件
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b24783130bd0456098e43818e96534ce~tplv-k3u1fbpfcp-zoom-1.image)

然后通过它的编译器生成你需要的实体

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9f0f425b1b824f5faea6920c7c0460de~tplv-k3u1fbpfcp-zoom-1.image)


然后再Netty中使用
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9eee37b5d405438782d99c7e33ddffc4~tplv-k3u1fbpfcp-zoom-1.image)

大概就是这么个流程，其实我们只要知道我们的目的是什么了就好了，我们就是加快序列化的速度嘛



## Netty编解码器和handler的调用机制
### 基本说明

- netty的组件设计：Netty的主要组件有Channel、EventLoop、ChannelFuture、ChannelHandler、ChannelPipe等

- ChannelHandler充当了处理入站和出站数据的应用程序逻辑的容器。例如，实现ChannelInboundHandler接口（或ChannelInboundHandlerAdapter），你就可以接收入站事件和数据，这些数据会被业务逻辑处理。当要给客户端发送响应时，也可以从ChannelInboundHandler冲刷数据。业务逻辑通常写在一个或者多个ChannelInboundHandler中。ChannelOutboundHandler原理一样，只不过它是用来处理出站数据的

- ChannelPipeline提供了ChannelHandler链的容器。以客户端应用程序为例，如果事件的运动方向是从客户端到服务端的，那么我们称这些事件为出站的，即客户端发送给服务端的数据会通过pipeline中的一系列ChannelOutboundHandler，并被这些Handler处理，反之则称为入站的
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8cadcf5f9dba4a81a5cc076200cbd75d~tplv-k3u1fbpfcp-zoom-1.image)

##  再讲编码解码器

- 当Netty发送或者接受一个消息的时候，就将会发生一次数据转换。入站消息会被解码：从字节转换为另一种格式（比如java对象）；如果是出站消息，它会被编码成字节。

- Netty提供一系列实用的编解码器，他们都实现了ChannelInboundHadnler或者ChannelOutboundHandler接口。在这些类中，channelRead方法已经被重写了。以入站为例，对于每个从入站Channel读取的消息，这个方法会被调用。随后，它将调用由解码器所提供的decode()方法进行解码，并将已经解码的字节转发给ChannelPipeline中的下一个ChannelInboundHandler。

### 解码器-ByteToMessageDecoder
- 关系继承图
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/137820b56af0482d80d0b9b0414c205c~tplv-k3u1fbpfcp-zoom-1.image)

我们可以看到还是继承了ChannelInboundHandler


- 由于不可能知道远程节点是否会一次性发送一个完整的信息，tcp有可能出现粘包拆包的问题，这个类会对入站数据进行缓冲，直到它准备好被处理.

- 一个关于ByteToMessageDecoder实例分析

当服务端读取客户端传过来的数据的时候，第一步就是要解码

```

public class ToIntegerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 4) {
            out.add(in.readInt());
        }
    }
}

```
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0e298f4efbdb4a0887e61cffb04e66c4~tplv-k3u1fbpfcp-zoom-1.image)
说明：
这个例子，每次入站从ByteBuf中读取4字节，将其解码为一个int，然后将它添加到下一个List中。当没有更多元素可以被添加到该List中时，它的内容将会被发送给下一个ChannelInboundHandler。int在被添加到List中时，会被自动装箱为Integer。在调用readInt()方法前必须验证所输入的ByteBuf是否具有足够的数据

## Netty的handler链的调用机制

实例要求:  
- 使用自定义的编码器和解码器来说明Netty的handler 调用机制
- 客户端发送long -> 服务器
- 服务端发送long -> 客户端


其实小六六主要想说的还是handler的一个调用链机制，一个双向链表，然后一个个handler去处理
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3a1efd1a40f34154b1640ec9ff96b145~tplv-k3u1fbpfcp-zoom-1.image)
来看看下面这个案例，按照上面的，是从客户端发送数据到服务端，然后服务端返回数据给客户端，所以我们先写客户端

- MyClient
```

package com.xiaoliuliu.netty.hander;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/1 21:20
 */
public class MyClient {

    public static void main(String[] args) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new MyClientInitializer()); //自定义一个初始化类

            ChannelFuture channelFuture = bootstrap.connect("localhost", 7000).sync();

            channelFuture.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }

}
```
- MyClientInitializer

```

package com.xiaoliuliu.netty.hander;

import com.atguigu.netty.inboundhandlerandoutboundhandler.MyByteToLongDecoder2;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/1 21:20
 */
public class MyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        //加入一个出站的handler 对数据进行一个编码
        pipeline.addLast(new MyLongToByteEncoder());

        //这时一个入站的解码器(入站handler )
        //pipeline.addLast(new MyByteToLongDecoder());
        pipeline.addLast(new MyByteToLongDecoder2());
        //加入一个自定义的handler ， 处理业务
        pipeline.addLast(new MyClientHandler());


    }
}

```

- MyClientHandler

```

package com.xiaoliuliu.netty.hander;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/1 21:22
 */
public class MyClientHandler  extends SimpleChannelInboundHandler<Long> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {

        System.out.println("服务器的ip=" + ctx.channel().remoteAddress());
        System.out.println("收到服务器消息=" + msg);

    }

    //重写channelActive 发送数据

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyClientHandler 发送数据");
        //ctx.writeAndFlush(Unpooled.copiedBuffer(""))
        ctx.writeAndFlush(123456L); //发送的是一个long

        //分析
        //1. "abcdabcdabcdabcd" 是 16个字节
        //2. 该处理器的前一个handler 是  MyLongToByteEncoder
        //3. MyLongToByteEncoder 父类  MessageToByteEncoder
        //4. 父类  MessageToByteEncoder
        /*

         public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf buf = null;
        try {
            if (acceptOutboundMessage(msg)) { //判断当前msg 是不是应该处理的类型，如果是就处理，不是就跳过encode
                @SuppressWarnings("unchecked")
                I cast = (I) msg;
                buf = allocateBuffer(ctx, cast, preferDirect);
                try {
                    encode(ctx, cast, buf);
                } finally {
                    ReferenceCountUtil.release(cast);
                }

                if (buf.isReadable()) {
                    ctx.write(buf, promise);
                } else {
                    buf.release();
                    ctx.write(Unpooled.EMPTY_BUFFER, promise);
                }
                buf = null;
            } else {
                ctx.write(msg, promise);
            }
        }
        4. 因此我们编写 Encoder 是要注意传入的数据类型和处理的数据类型一致
        */
        // ctx.writeAndFlush(Unpooled.copiedBuffer("abcdabcdabcdabcd",CharsetUtil.UTF_8));

    }
}


```

- MyLongToByteEncoder
```

package com.xiaoliuliu.netty.hander;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/1 21:18
 */
public class MyLongToByteEncoder extends MessageToByteEncoder<Long> {
    //编码方法
    @Override
    protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {

        System.out.println("MyLongToByteEncoder encode 被调用");
        System.out.println("msg=" + msg);
        out.writeLong(msg);

    }
}


```

- MyByteToLongDecoder2

```
package com.xiaoliuliu.netty.hander;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/1 21:36
 */
public class MyByteToLongDecoder2 extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        System.out.println("MyByteToLongDecoder2 被调用");
        //在 ReplayingDecoder 不需要判断数据是否足够读取，内部会进行处理判断
        out.add(in.readLong());


    }
}


```

- MyServer

```

package com.xiaoliuliu.netty.hander;

import com.atguigu.netty.inboundhandlerandoutboundhandler.MyServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/1 20:50
 */

public class MyServer {
    public static void main(String[] args) throws Exception{

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).childHandler(new MyServerInitializer()); //自定义一个初始化类


            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}

```

- MyServerInitializer

```
package com.xiaoliuliu.netty.hander;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/1 20:59
 */
public class MyServerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();//一会下断点

        //入站的handler进行解码 MyByteToLongDecoder
        pipeline.addLast(new MyByteToLongDecoder());
        //pipeline.addLast(new MyByteToLongDecoder2());
        //出站的handler进行编码
        pipeline.addLast(new MyLongToByteEncoder());
        //自定义的handler 处理业务逻辑
        pipeline.addLast(new MyServerHandler());
        System.out.println("xx");
    }


}


```

- MyServerHandler

```

package com.xiaoliuliu.netty.hander;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/1 21:19
 */
public class MyServerHandler extends SimpleChannelInboundHandler<Long> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {

        System.out.println("从客户端" + ctx.channel().remoteAddress() + " 读取到long " + msg);

        //给客户端发送一个long
        ctx.writeAndFlush(98765L);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}


```


- 总结一下

	- 首先就是 我客户端发送数据出去嘛
    ![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/43a98fe3376c43ba8910e6eb99663a53~tplv-k3u1fbpfcp-zoom-1.image)
    -  第二步就是我们把数据准备好了，肯定是要把它转成encoder下
    ![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/02574481c63a4594b3b3588a4796397c~tplv-k3u1fbpfcp-zoom-1.image)
    - 第三步 当然是我们服务端接收到code 然后去decoder下
    ![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/bca2593b8eee4b39af5488dab3ee343f~tplv-k3u1fbpfcp-zoom-1.image)
    - 然后hander传递给下一个处理器，然后收到信息，然后再返回一条数据给客户端
    
    ![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e9db6bee0817412fa20e38d9914c66df~tplv-k3u1fbpfcp-zoom-1.image)
    
    - 之后肯定是把返回的数据encoder一下 再发送给socket
    ![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8d7ecb198f974891981c59a1e3b92ea6~tplv-k3u1fbpfcp-zoom-1.image)
    - 之后就是客户端收到信息，然后解码
    ![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4d11b859410545c7bb9de48037763e15~tplv-k3u1fbpfcp-zoom-1.image)
    - 整个流程如下图
    ![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/477edc15b2d6423dba930ca05707627c~tplv-k3u1fbpfcp-zoom-1.image)
    
    
## TCP 粘包和拆包 及解决方案 

### TCP 粘包和拆包基本介绍

- TCP是面向连接的，面向流的，提供高可靠性服务。收发两端（客户端和服务器端）都要有一一成对的socket，因此，发送端为了将多个发给接收端的包，更有效的发给对方，使用了优化方法（Nagle算法），将多次间隔较小且数据量小的数据，合并成一个大的数据块，然后进行封包。这样做虽然提高了效率，但是接收端就难于分辨出完整的数据包了，因为面向流的通信是无消息保护边界的

- 由于TCP无消息保护边界, 需要在接收端处理消息边界问题，也就是我们所说的粘包、拆包问题, 看一张图

- TCP粘包、拆包图解
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d52f98b956de47d9982c09064e6bc74e~tplv-k3u1fbpfcp-zoom-1.image)


假设客户端分别发送了两个数据包D1和D2给服务端，由于服务端一次读取到字节数是不确定的，故可能存在以下四种情况：
- 服务端分两次读取到了两个独立的数据包，分别是D1和D2，没有粘包和拆包
- 服务端一次接受到了两个数据包，D1和D2粘合在一起，称之为TCP粘包
- 服务端分两次读取到了数据包，第一次读取到了完整的D1包和D2包的部分内容，第二次读取到了D2包的剩余内容，这称之为TCP拆包
- 服务端分两次读取到了数据包，第一次读取到了D1包的部分内容D1_1，第二次读取到了D1包的剩余部分内容D1_2和完整的D2包。



### 粘包和半包原理

这得从底层说起。
在操作系统层面来说，我们使用了 TCP 协议。
在Netty的应用层，按照 ByteBuf 为 单位来发送数据，但是到了底层操作系统仍然是按照字节流发送数据，因此，从底层到应用层，需要进行二次拼装。
操作系统底层，是按照字节流的方式读入，到了 Netty 应用层面，需要二次拼装成 ByteBuf。
这就是粘包和半包的根源。

在Netty 层面，拼装成ByteBuf时，就是对底层缓冲的读取，这里就有问题了。
首先，上层应用层每次读取底层缓冲的数据容量是有限制的，当TCP底层缓冲数据包比较大时，将被分成多次读取，造成断包，在应用层来说，就是半包。
其次，如果上层应用层一次读到多个底层缓冲数据包，就是粘包。
    
### 如何解决呢？
基本思路是，在接收端，需要根据自定义协议来，来读取底层的数据包，重新组装我们应用层的数据包，这个过程通常在接收端称为拆包。


### 拆包的原理
拆包基本原理，简单来说：
- 接收端应用层不断从底层的TCP 缓冲区中读取数据。
-每次读取完，判断一下是否为一个完整的应用层数据包。如果是，上层应用层数据包读取完成。
- 如果不是，那就保留该数据在应用层缓冲区，然后继续从 TCP 缓冲区中读取，直到得到一个完整的应用层数据包为止。
- 至此，半包问题得以解决。
- 如果从TCP底层读到了多个应用层数据包，则将整个应用层缓冲区，拆成一个一个的独立的应用层数据包，返回给调用程序。
- 至此，粘包问题得以解决。- 


## 结尾
差不多 入门就这些吧，简单的过了下下


![](//p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/206299625d574e21b7226cb2f3cdb850~tplv-k3u1fbpfcp-zoom-1.image)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
