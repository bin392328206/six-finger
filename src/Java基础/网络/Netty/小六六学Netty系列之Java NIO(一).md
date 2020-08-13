# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨 
前面也说了，学习Netty的基础，那就是Nio，昨天我们简单的过了一下BIO，这是我们Java IO的基础，在JDK1.4之前的主要的io方式，今天开始，我们就开始把NIO的一些东西了解清楚，之后才是Netty ，因为我们的Netty是基础NIO的一个框架嘛，下面是前面系列的链接
- [小六六学Netty系列之Java BIO](https://juejin.im/post/6859336784627646471)



## Java NIO 基本介绍

- Java NIO 全称 java non-blocking IO，是指 JDK 提供的新 API。从 JDK1.4 开始，Java 提供了一系列改进的输入/输出的新特性，被统称为 NIO(即 New IO)，是同步非阻塞的
- NIO 相关类都被放在 java.nio 包及子包下，并且对原 java.io 包中的很多类进行改写。
- NIO 有三大核心部分：Channel(通道)，Buffer(缓冲区), Selector(选择器) 
- NIO是 面向缓冲区 ，或者面向 块 编程的。数据读取到一个它稍后处理的缓冲区，需要时可在缓冲区中前后移动，这就增加了处理过程中的灵活性，使用它可以提供非阻塞式的高伸缩性网络
- Java NIO的非阻塞模式，使一个线程从某通道发送请求或者读取数据，但是它仅能得到目前可用的数据，如果目前没有数据可用时，就什么都不会获取，而不是保持线程阻塞，所以直至数据变的可以读取之前，该线程可以继续做其他的事情。 非阻塞写也是如此，一个线程请求写入一些数据到某通道，但不需要等待它完全写入，这个线程同时可以去做别的事情。
- 通俗理解：NIO是可以做到用一个线程来处理多个操作的。假设有10000个请求过来,根据实际情况，可以分配50或者100个线程来处理。不像之前的阻塞IO那样，非得分配10000个。
- HTTP2.0使用了多路复用的技术，做到同一个连接并发处理多个请求，而且并发请求的数量比HTTP1.1大了好几个数量级。

## NIO 和 BIO 的比较
1. BIO 以流的方式处理数据,而 NIO 以块的方式处理数据,块 I/O 的效率比流 I/O 高很多
2. BIO 是阻塞的，NIO 则是非阻塞的
3. BIO基于字节流和字符流进行操作，而 NIO 基于 Channel(通道)和 Buffer(缓冲区)进行操作，数据总是从通道读取到缓冲区中，或者从缓冲区写入到通道中。Selector(选择器)用于监听多个通道的事件（比如：连接请求，数据到达等），因此使用单个线程就可以监听多个客户端通道 

## NIO 三大核心原理示意图

一张图描述NIO 的 Selector 、 Channel 和 Buffer 的关系
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/393dde24b99e4a738c86b1d50e98c6a8~tplv-k3u1fbpfcp-zoom-1.image)

- 每个channel 都会对应一个Buffer
- Selector 对应一个线程， 一个线程对应多个channel(连接)
- 该图反应了有三个channel 注册到 该selector //程序
- 程序切换到哪个channel 是有事件决定的, Event 就是一个重要的概念（EventLoop）
- Selector 会根据不同的事件，在各个通道上切换
- Buffer 就是一个内存块 ， 底层是有一个数组
- 数据的读取写入是通过Buffer, 这个和BIO , BIO 中要么是输入流，或者是输出流, 不能双向，但是NIO的Buffer 是可以读也可以写, 需要 flip 方法切换
- channel 是双向的, 可以返回底层操作系统的情况, 比如Linux ， 底层的操作系统通道就是双向的.

下面我们就开始一一来学习NIO的三大组件吧

### 缓冲区(Buffer) 


#### 基本介绍

缓冲区（Buffer）：缓冲区本质上是一个可以读写数据的内存块，可以理解成是一个容器对象(含数组)，该对象提供了一组方法，可以更轻松地使用内存块，，缓冲区对象内置了一些机制，能够跟踪和记录缓冲区的状态变化情况。Channel 提供从文件、网络读取数据的渠道，但是读取或写入的数据都必须经由 Buffer，如图:  【后面举例说明】

### Buffer 类及其子类
- 在 NIO 中，Buffer 是一个顶层父类，它是一个抽象类, 类的层级关系图:


![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f19e5af6512a4e8897da638ada83335f~tplv-k3u1fbpfcp-zoom-1.image)

- Buffer类定义了所有的缓冲区都具有的四个属性来提供关于其所包含的数据元素的信息:
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2b2da021d8f440efa78af9a2c204630a~tplv-k3u1fbpfcp-zoom-1.image)


- Buffer类相关方法一览 就是我们所说的常用的api

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/535841b390504853b6d509b1d57a34f0~tplv-k3u1fbpfcp-zoom-1.image)

其实这些都蛮简单的，只要我们记住几个常用的，其他的 用到了我们再来找就好了

- 从前面可以看出对于 Java 中的基本数据类型(boolean除外)，都有一个 Buffer 类型与之相对应，最常用的自然是ByteBuffer 类（二进制数据），该类的主要方法如下：
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/95531e0499784bec9364a7f2f9bb60ca~tplv-k3u1fbpfcp-zoom-1.image)


## 通道(Channel)

### 基本介绍
- NIO的通道类似于流，但有些区别如下：
- 通道可以同时进行读写，而流只能读或者只能写
- 通道可以实现异步读写数据
- 通道可以从缓冲读数据，也可以写数据到缓冲: 
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f183fd28de7f4194b9ae45d9385d6f0c~tplv-k3u1fbpfcp-zoom-1.image)

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/15cdf577d95d48b7a70549d624564414~tplv-k3u1fbpfcp-zoom-1.image)

- BIO 中的 stream 是单向的，例如 FileInputStream 对象只能进行读取数据的操作，而 NIO 中的通道(Channel)是双向的，可以读操作，也可以写操作。
- Channel在NIO中是一个接口public interface Channel extends Closeable{} 
常用的 Channel 类有：FileChannel、DatagramChannel、ServerSocketChannel 和 SocketChannel。【ServerSocketChanne 类似 ServerSocket , SocketChannel 类似 Socket】

- FileChannel 用于文件的数据读写，DatagramChannel 用于 UDP 的数据读写，ServerSocketChannel 和 SocketChannel 用于 TCP 的数据读写。


### FileChannel 类

FileChannel主要用来对本地文件进行 IO 操作，常见的方法有


- public int read(ByteBuffer dst) ，从通道读取数据并放到缓冲区中
- public int write(ByteBuffer src) ，把缓冲区的数据写到通道中
- public long transferFrom(ReadableByteChannel src, long position, long count)，从目标通道中复制数据到当前通道
- public long transferTo(long position, long count, WritableByteChannel target)，把数据从当前通道复制给目标通道

### 例子
#### 用FileChannel来写文件

```
package com.itheima.mq.rocketmq.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/8/2 15:37
 */
public class NIOFileChannel01  {

    public static void main(String[] args) throws Exception {
        String name="hello,小六六";

        //生成一个输出流

        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file01.txt");

        //通过 fileOutputStream 获取 对应的 FileChannel
        //这个 fileChannel 真实 类型是  FileChannelImpl

        FileChannel channel = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //把数据放到缓存区中

        byteBuffer.put(name.getBytes());


        //转换
        byteBuffer.flip();

        channel.write(byteBuffer);

        fileOutputStream.close();
    }
}

```

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e0f2abfd1a6b484e8feefac13bb07be7~tplv-k3u1fbpfcp-zoom-1.image)

总结一个和BIO的区别，其实很简单 ，就是把数据写到buffer里面 ，然后输入输出流从buffer里面拿数据

#### 应用实例2-本地文件读数据

```
package com.itheima.mq.rocketmq.nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/8/2 15:37
 */
public class NIOFileChannel02 {

    public static void main(String[] args) throws Exception {

        //创建文件流

        File file = new File("d:\\file01.txt");

        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel channel = fileInputStream.getChannel();

        ByteBuffer allocate = ByteBuffer.allocate((int) file.length());

        channel.read(allocate);

        System.out.println(new String(allocate.array()));

        fileInputStream.close();


    }

}

```


![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7ef778f0468344b2a41de43db669f722~tplv-k3u1fbpfcp-zoom-1.image)



#### 应用实例3-使用一个Buffer完成文件读取


实例要求: 

- 使用 FileChannel(通道) 和 方法  read , write，完成文件的拷贝

- 拷贝一个文本文件 1.txt  , 放在项目下即可

代码演示 
```
package com.itheima.mq.rocketmq.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/8/2 15:37
 */
public class NIOFileChannel03 {

    public static void main(String[] args) throws Exception {

        FileInputStream fileInputStream = new FileInputStream("1.txt");
        FileChannel fileChannel01 = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("2.txt");
        FileChannel fileChannel02 = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);


        //下面就是数据读写的流程了

        while (true){
            byteBuffer.clear(); //清空buffer
            int read = fileChannel01.read(byteBuffer);

            System.out.println("read =" + read);
            if(read == -1) { //表示读完
                break;
            }

            //将buffer 中的数据写入到 fileChannel02 -- 2.txt
            byteBuffer.flip();
            fileChannel02.write(byteBuffer);

            //将buffer 中的数据写入到 fileChannel02 -- 2.txt
            byteBuffer.flip();
            fileChannel02.write(byteBuffer);



        }

        //关闭相关的流
        fileInputStream.close();
        fileOutputStream.close();
    }

}


```


![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/54b12c377fd14d70807f6c6d61a13015~tplv-k3u1fbpfcp-zoom-1.image)



## 结尾

其实这节也很简单，就是大致的介绍了一下NIO，然后讲了 NIO 三大组件的 2个  一个 Buffer,一个Channel, 下次再讲讲 selector。


## 小故事

在一家幼儿园里，小朋友有上厕所的需求，小朋友都太小以至于你要问他要不要上厕所，他才会告诉你。幼儿园一共有100个小朋友，有两种方案可以解决小朋友上厕所的问题：

每个小朋友配一个老师。每个老师隔段时间询问小朋友是否要上厕所，如果要上，就领他去厕所，100个小朋友就需要100个老师来询问，并且每个小朋友上厕所的时候都需要一个老师领着他去上，这就是IO模型，一个连接对应一个线程。
所有的小朋友都配同一个老师。这个老师隔段时间询问所有的小朋友是否有人要上厕所，然后每一时刻把所有要上厕所的小朋友批量领到厕所，这就是NIO模型，所有小朋友都注册到同一个老师，对应的就是所有的连接都注册到一个线程，然后批量轮询。
这就是NIO模型解决线程资源受限的方案，实际开发过程中，我们会开多个线程，每个线程都管理着一批连接，相对于IO模型中一个线程管理一条连接，消耗的线程资源大幅减少

## 参考
[Netty是什么]()



![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
