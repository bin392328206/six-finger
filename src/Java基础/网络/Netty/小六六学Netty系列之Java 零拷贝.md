# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨 
NIO 上文把介绍，Buffer,Channel 等讲了，今天我们就来粗略的分析一个selector（本文争对Java层面，如果要分析到内核的select函数的话，小六六很懵逼）
下面是前面系列的链接
- [小六六学Netty系列之Java BIO](https://juejin.im/post/6859336784627646471)
- [小六六学Netty系列之Java NIO(一)](https://juejin.im/post/6860275544655659015)
- [小六六学Netty系列之Java NIO(二)](https://juejin.im/post/6860798575187197965)
- [小六六学Netty系列之unix IO模型](https://juejin.im/post/6862141145553338382)

## 什么是零拷贝?
学习三部曲，第一步是什么，哈哈，灵魂发问。

零拷贝(英语: Zero-copy) 技术是指计算机执行操作时，CPU不需要先将数据从某处内存复制到另一个特定区域。这种技术通常用于通过网络传输文件时节省CPU周期和内存带宽。



## 为什么需要零拷贝技术

如今，很多网络服务器都是基于客户端 - 服务器这一模型的。在这种模型中，客户端向服务器端请求数据或者服务；服务器端则需要响应客户端发出的请求，并为客户端提供它所需要的数据。随着网络服务的逐渐普及，video 这类应用程序发展迅速。当今的计算机系统已经具备足够的能力去处理 video 这类应用程序对客户端所造成的重负荷，但是对于服务器端来说，它应付由 video 这类应用程序引起的网络通信量就显得捉襟见肘了。而且，客户端的数量增长迅速，那么服务器端就更容易成为性能瓶颈。而对于负荷很重的服务器来说，操作系统通常都是引起性能瓶颈的罪魁祸首。举个例子来说，当数据“写”操作或者数据“发送”操作的系统调用发出时，操作系统通常都会将数据从应用程序地址空间的缓冲区拷贝到操作系统内核的缓冲区中去。操作系统这样做的好处是接口简单，但是却在很大程度上损失了系统性能，因为这种数据拷贝操作不单需要占用 CPU 时间片，同时也需要占用额外的内存带宽。

一般来说，客户端通过网络接口卡向服务器端发送请求，操作系统将这些客户端的请求传递给服务器端应用程序，服务器端应用程序会处理这些请求，请求处理完成以后，操作系统还需要将处理得到的结果通过网络适配器传递回去。





## 零拷贝的好处

- 减少甚至完全避免不必要的CPU拷贝，从而让CPU解脱出来去执行其他的任务
- 减少内存带宽的占用
- 通常零拷贝技术还能够减少用户空间和操作系统内核空间之间的上下文切换



## 零拷贝的实现
零拷贝实际的实现并没有真正的标准，取决于操作系统如何实现这一点。零拷贝完全依赖于操作系统。操作系统支持，就有；不支持，就没有。不依赖Java本身。



## 对于我们Java来说，哪些用到了零拷贝

对于Java来说，零拷贝真的是用的特别多，不然小六六也不会花那么多的精力去学习它了，哈哈，首先Java是做服务端的一个主要语言，网络这块肯定得用到它，比如说我们的tomcat,我们的中间件 redis, mq 我基本上没有看到一个框架没有用上它。哈哈

## LinuxI/O机制及零拷贝介绍

Limux I O 模型可用参考我上一篇文章，最主要的是5钟模型嘛，下文是链接

## 传统I/O
在Java中，我们可以通过InputStream从源数据中读取数据流到一个缓冲区里，然后再将它们输入到OutputStream里。我们知道，这种IO方式传输效率是比较低的。那么，当使用上面的代码时操作系统会发生什么情况：



![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f626d729194f49038df852651a4d998d~tplv-k3u1fbpfcp-zoom-1.image)


这是一个从磁盘文件读取并且通过socket写出的过程，对应的系统调用如下：
```
read(file,tmp_buf,len)
write(socket,tmp_buf,len)
```
- 程序使用read()系统调用。系统由用户态转换为内核态(第一次上线文切换)，磁盘中的数据有DMA（Direct Memory Access)的方式读取到内核缓冲区(kernel buffer)。DMA过程中CPU不需要参与数据的读写，而是DMA处理器直接将硬盘数据通过总线传输到内存中。
- 系统由内核态转换为用户态（第二次上下文切换），当程序要读取的数据已经完成写入内核缓冲区以后，程序会将数据由内核缓存区，写入用户缓存区），这个过程需要CPU参与数据的读写。
- 程序使用write()系统调用。系统由用户态切换到内核态(第三次上下文切换)，数据从用户态缓冲区写入到网络缓冲区(Socket Buffer)，这个过程需要CPU参与数据的读写。
- 系统由内核态切换到用户态（第四次上下文切换），网络缓冲区的数据通过DMA的方式传输到网卡的驱动(存储缓冲区)中(protocol engine)

可以看到，传统的I/O方式会经过4次用户态和内核态的切换(上下文切换)，两次CPU中内存中进行数据读写的过程。这种拷贝过程相对来说比较消耗资源


重新思考传统IO方式，会注意到实际上并不需要第二个和第三个数据副本。应用程序除了缓存数据并将其传输回套接字缓冲区之外什么都不做。相反，数据可以直接从读缓冲区传输到套接字缓冲区。

显然，第二次和第三次数据copy 其实在这种场景下没有什么帮助反而带来开销，这也正是零拷贝出现的背景和意义。

## 零拷贝的出现
目的：减少IO流程中不必要的拷贝

零拷贝需要OS支持，也就是需要kernel暴露api。虚拟机不能操作内核，

## Linux支持的(常见)零拷贝

### mmap内存映射
> data loaded from disk is stored in a kernel buffer by DMA copy. Then the pages of the application buffer are mapped to the kernel buffer, so that the data copy between kernel buffers and application buffers are omitted.

DMA加载磁盘数据到kernel buffer后，应用程序缓冲区(application buffers)和内核缓冲区(kernel buffer)进行映射，数据再应用缓冲区和内核缓存区的改变就能省略。


![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c9eee7e792474bd1b64e5d966af4ab9c~tplv-k3u1fbpfcp-zoom-1.image)

mmap内存映射将会经历：3次拷贝: 1次cpu copy，2次DMA copy；
以及4次上下文切换


## sendfile
linux 2.1支持的sendfile

当调用sendfile()时，DMA将磁盘数据复制到kernel buffer，然后将内核中的kernel buffer直接拷贝到socket buffer；
一旦数据全都拷贝到socket buffer，sendfile()系统调用将会return、代表数据转化的完成。
socket buffer里的数据就能在网络传输了。

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f59af1260591484f8d4049b25a86f0b0~tplv-k3u1fbpfcp-zoom-1.image)

sendfile会经历：3次拷贝，1次CPU copy 2次DMA copy；
以及2次上下文切换
## Sendfile With DMA Scatter/Gather Copy
Scatter/Gather可以看作是sendfile的增强版，批量sendfile。

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/701d44293207425eacbd15d39e2a08fd~tplv-k3u1fbpfcp-zoom-1.image)

Scatter/Gather会经历2次拷贝: 0次cpu copy，2次DMA copy

## splice
数据从磁盘读取到OS内核缓冲区后，在内核缓冲区直接可将其转成内核空间其他数据buffer，而不需要拷贝到用户空间。
如下图所示，从磁盘读取到内核buffer后，在内核空间直接与socket buffer建立pipe管道。
和sendfile()不同的是，splice()不需要硬件支持。

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5144a02c9c5f4d2eaf4b108771027070~tplv-k3u1fbpfcp-zoom-1.image)

注意splice和sendfile的不同，sendfile是将磁盘数据加载到kernel buffer后，需要一次CPU copy,拷贝到socket buffer。
而splice是更进一步，连这个CPU copy也不需要了，直接将两个内核空间的buffer进行set up pipe。

splice会经历 2次拷贝: 0次cpu copy 2次DMA copy；
以及2次上下文切换






![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d14a60816c3041adb225f02077f03e07~tplv-k3u1fbpfcp-zoom-1.image)

## Java零拷贝机制解析
Linux提供的领拷贝技术 Java并不是全支持，支持2种(内存映射mmap、sendfile)；

### NIO提供的内存映射 MappedByteBuffer
首先要说明的是，JavaNlO中 的Channel (通道)就相当于操作系统中的内核缓冲区，有可能是读缓冲区，也有可能是网络缓冲区，而Buffer就相当于操作系统中的用户缓冲区。

底层就是调用Linux mmap()实现的。

NIO中的FileChannel.map()方法其实就是采用了操作系统中的内存映射方式，底层就是调用Linux mmap()实现的。

将内核缓冲区的内存和用户缓冲区的内存做了一个地址映射。这种方式适合读取大文件，同时也能对文件内容进行更改，但是如果其后要通过SocketChannel发送，还是需要CPU进行数据的拷贝。
使用MappedByteBuffer，小文件，效率不高；一个进程访问，效率也不高。

MappedByteBuffer只能通过调用FileChannel的map()取得，再没有其他方式。
FileChannel.map()是抽象方法，具体实现是在 FileChannelImpl.c 可自行查看JDK源码，其map0()方法就是调用了Linux内核的mmap的API。
使用 MappedByteBuffer类要注意的是：mmap的文件映射，在full gc时才会进行释放。当close时，需要手动清除内存映射文件，可以反射调用sun.misc.Cleaner方法。

### NIO提供的sendfile
- FileChannel.transferTo()方法直接将当前通道内容传输到另一个通道，没有涉及到Buffer的任何操作，NIO中 的Buffer是JVM堆或者堆外内存，但不论如何他们都是操作系统内核空间的内存
- transferTo()的实现方式就是通过系统调用sendfile() (当然这是Linux中的系统调用)
```
//使用sendfile:读取磁盘文件，并网络发送
FileChannel sourceChannel = new RandomAccessFile(source, "rw").getChannel();
SocketChannel socketChannel = SocketChannel.open(sa);
sourceChannel.transferTo(0, sourceChannel.size(), socketChannel);
```


```
MappedByteBuffer mappedByteBuffer = new RandomAccessFile(file, "r") 
                                 .getChannel() 
                                .map(FileChannel.MapMode.READ_ONLY, 0, len);
```

ZeroCopyFile实现文件复制

```
class ZeroCopyFile {

    public void copyFile(File src, File dest) {
        try (FileChannel srcChannel = new FileInputStream(src).getChannel();
             FileChannel destChannel = new FileInputStream(dest).getChannel()) {

            srcChannel.transferTo(0, srcChannel.size(), destChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
注意： Java NIO提供的FileChannel.transferTo 和 transferFrom 并不保证一定能使用零拷贝。实际上是否能使用零拷贝与操作系统相关，如果操作系统提供 sendfile 这样的零拷贝系统调用，则这两个方法会通过这样的系统调用充分利用零拷贝的优势，否则并不能通过这两个方法本身实现零拷贝。




## 参考文献

- [Java中的零拷贝](https://www.jianshu.com/p/2fd2f03b4cc3)
- [零拷贝的原理及Java实现
](https://www.jianshu.com/p/497e7640b57c)


## 结尾

好了，到目前为止，Netty的前置知识我们已经学得差不多了，明天开始可以正式Netty咯

![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
