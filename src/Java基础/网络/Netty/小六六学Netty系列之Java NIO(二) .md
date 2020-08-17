# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨 
NIO 上文把介绍，Buffer,Channel 等讲了，今天我们就来粗略的分析一个selector（本文争对Java层面，如果要分析到内核的select函数的话，小六六很懵逼）
下面是前面系列的链接
- [小六六学Netty系列之Java BIO](https://juejin.im/post/6859336784627646471)
- [小六六学Netty系列之Java NIO(一)](https://juejin.im/post/6860275544655659015)

## 基本介绍
- Java的NIO,用的非阻塞的IO方式。可以用一个客户端处理多个客户端的连接，其实这个就是我们前面讲的Uinx IO 模型中的I/O多路复用了，然后我们来讲讲我们最初的方式Selector选择器
- Selector 能够检测多个注册的通道上是否有事件发生(注意:多个Channel以事件的方式可以注册到同一个Selector)，如果有事件发生，便获取事件然后针对每个事件进行相应的处理。这样就可以只用一个单线程去管理多个通道，也就是管理多个连接和请求
- 只有在 连接/通道 真正有读写事件发生时，才会进行读写，就大大地减少了系统开销，并且不必为每个连接都创建一个线程，不用去维护多个线程，避免了多线程之间的上下文切换导致的开销

### Selector示意图和特点说明
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/aa05e20f983048fe8153db0708993f73~tplv-k3u1fbpfcp-zoom-1.image)

- Netty 的 IO 线程 NioEventLoop(小六六觉得这个蛮重要的) 聚合了 Selector(选择器，也叫多路复用器)，可以同时并发处理成百上千个客户端连接。
- 当线程从某客户端 Socket 通道进行读写数据时，若没有数据可用时，该线程可以进行其他任务。
- 线程通常将非阻塞 IO 的空闲时间用于在其他通道上执行 IO 操作，所以单独的线程可以管理多个输入和输出通道。
- 由于读写操作都是非阻塞的，这就可以充分提升 IO 线程的运行效率，避免由于频繁 I/O 阻塞导致的线程挂起。
- 一个 I/O 线程可以并发处理 N 个客户端连接和读写操作，这从根本上解决了传统同步阻塞 I/O 一连接一线程模型，架构的性能、弹性伸缩能力和可靠性都得到了极大的提升。
### Selector相关的方法

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5d61f86b67f441c1ba2e9b589b60aee3~tplv-k3u1fbpfcp-zoom-1.image)
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/420ef0760ecd451da45592a3e4b71438~tplv-k3u1fbpfcp-zoom-1.image)
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/816f0009e0f5460394f23180d39b0cb3~tplv-k3u1fbpfcp-zoom-1.image)
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a442126722354ac9a3ec734be8c02950~tplv-k3u1fbpfcp-zoom-1.image)

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ceae4a3c71d144569f9c4e5b3406e1f1~tplv-k3u1fbpfcp-zoom-1.image)

小六六也是截图的，大家如果看到这里了，应该去JDK里面找到这个类，先大致的过一下，具体的我们后面再看，有个大概映像。

**说Netty 其实也就是两大类，第一大类是网络编程，第二类就是IO操作， IO操作的话再上面2节的Buffer和Channel 我们已经了解到了，那么现在我们来看看NIO的网络编程和我们前面的BIO的编程有啥区别呗，**


### NIO 非阻塞 网络编程原理分析图
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/53afe4ad872f488abbaac80a0027d813~tplv-k3u1fbpfcp-zoom-1.image)
对上面的解析
- 当客户端连接时，会通过ServerSocketChannel 得到 SocketChannel（如果是BIO的话我们得到的是Socket,就是一个客户端连接）
- Selector 进行监听  select 方法, 返回有事件发生的通道的个数.
- 将socketChannel注册到Selector上, register(Selector sel, int ops), 一个selector上可以注册多个SocketChannel 
- 注册后返回一个 SelectionKey, 会和该Selector 关联(集合)
- 进一步得到各个 SelectionKey (有事件发生)
- 在通过 SelectionKey  反向获取 SocketChannel , 方法 channel()
- 可以通过  得到的 channel  , 完成业务处理




## NIO 网络编程案例

> 服务端代码

```
package com.liuliu.nio;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/8/8 11:55
 */
public class NIOServer {
    public static void main(String[] args) throws Exception {
        // 传统io    ServerSocket serverSocket = new ServerSocket(6666);
        //获取一个nio的网络socket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 获取一个selector
        Selector selector = Selector.open();
        //绑定一个端口6666, 在服务器端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //把 serverSocketChannel 注册到  selector 关心 事件为 OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("注册后的selectionkey 数量=" + selector.keys().size());
        //循环等待客户端连接
        while (true) {
            if (selector.select(1000) == 0) {
                //说明这1秒内  没有事件发生，那么我们就自己让他跳出循环
                continue;
            }
            //如果返回的>0, 就获取到相关的 selectionKey集合
            //1.如果返回的>0， 表示已经获取到关注的事件
            //2. selector.selectedKeys() 返回关注事件的集合
            //   通过 selectionKeys 反向获取通道
            //说明有事件通知了
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            //使用迭代器遍历
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                // 获取它的key
                SelectionKey key = iterator.next();
                //根据key 对应的通道发生的事件做相应处理
                if (key.isAcceptable()) {
                    //如果是 OP_ACCEPT, 有新的客户端连接]
                    //生成一个socket连接，对应BIO中的    Socket socket=ServerSocket.accept
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功 生成了一个 socketChannel " + socketChannel.hashCode());
                    //将  SocketChannel 设置为非阻塞
                    socketChannel.configureBlocking(false);
                    //将socketChannel 注册到selector, 关注事件为 OP_READ， 同时给socketChannel
                    //生成一个buffter
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    //把channel 和buffer关联
                    socketChannel.register(selector, SelectionKey.OP_READ, buffer);
                    System.out.println("客户端连接后 ，注册的selectionkey 数量=" + selector.keys().size());
                }
                if (key.isReadable()) {
                    //发送的是读事件
                    //通过key 反向获取到对应channel
                    SocketChannel channel = (SocketChannel) key.channel();
                    //获取到该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    //把数据读到buffer中去
                    channel.read(buffer);
                    //打印从客户端发出来的数据
                    System.out.println("form 客户端 " + new String(buffer.array()));
                }
                // 手动从集合中移动当前的selectionKey, 防止重复操作
                iterator.remove();
            }
        }
    }
}


```

> 客户端代码

```package com.liuliu.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/8/8 14:49
 *
 * NIO 网络编程的客户端
 */
public class NIOClient {

    public static void main(String[] args) throws Exception {
        // 得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞的操作
        socketChannel.configureBlocking(false);
        //提供服务器端的ip 和 端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);

        // 去服务端去连接
        if (!socketChannel.connect(inetSocketAddress)) {
            // 因为我们是非阻塞的，所以我们可以做其他的事情
            while (!socketChannel.finishConnect()) {
                System.out.println("因为连接需要时间，客户端不会阻塞，可以做其它工作..");
            }
        }

        //准备发送数据给服务端

        String msg="跟小六六一起学Netty";

        ByteBuffer wrap = ByteBuffer.wrap(msg.getBytes());

        //把数据给channel

        socketChannel.write(wrap);



    }
}

package com.liuliu.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/8/8 14:49
 *
 * NIO 网络编程的客户端
 */
public class NIOClient {

    public static void main(String[] args) throws Exception {
        // 得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞的操作
        socketChannel.configureBlocking(false);
        //提供服务器端的ip 和 端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);

        // 去服务端去连接
        if (!socketChannel.connect(inetSocketAddress)) {
            // 因为我们是非阻塞的，所以我们可以做其他的事情
            while (!socketChannel.finishConnect()) {
                System.out.println("因为连接需要时间，客户端不会阻塞，可以做其它工作..");
            }
        }

        //准备发送数据给服务端

        String msg="跟小六六一起学Netty";

        ByteBuffer wrap = ByteBuffer.wrap(msg.getBytes());

        //把数据给channel

        socketChannel.write(wrap);



    }
}

```

> 运行的结果

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/52c0b13784cb40c69114e4d6e014d46a~tplv-k3u1fbpfcp-zoom-1.image)

大家看后面是因为 客户端把连接关了报错了的。


### 聊聊SelectionKey


SelectionKey，表示 Selector 和网络通道的注册关系, 共四种:

- int OP_ACCEPT：有新的网络连接可以 accept，值为 16
- int OP_CONNECT：代表连接已经建立，值为 8
- int OP_READ：代表读操作，值为 1 
- int OP_WRITE：代表写操作，值为 4

源码中：

- public static final int OP_READ = 1 << 0; 
- public static final int OP_WRITE = 1 << 2;
- public static final int OP_CONNECT = 1 << 3;
- public static final int OP_ACCEPT = 1 << 4;


![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b6e2dc08ef6049e0ad941c7448213b4e~tplv-k3u1fbpfcp-zoom-1.image)


我们来看看主要的几个方法
```
public abstract class SelectionKey {
     public abstract Selector selector();//得到与之关联的 Selector 对象
 public abstract SelectableChannel channel();//得到与之关联的通道
 public final Object attachment();//得到与之关联的共享数据
 public abstract SelectionKey interestOps(int ops);//设置或改变监听事件
 public final boolean isAcceptable();//是否可以 accept
 public final boolean isReadable();//是否可以读
 public final boolean isWritable();//是否可以写
}

```

## 结尾

好了，到这里NIO的三个组件的基本已经有了一点点认识了，大家可以试着去写一个基于NIO的多人聊天系统。试试 哈哈。

![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
