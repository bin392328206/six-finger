# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨 
这个系列是8月的计划，大概能写个十来篇的文章，估计是要的 ，所以说要是原创是不可能的，我不是找了很多学习资料，然后肯定会copy很多，所以说我还是得好好学习，把学习记录记录一下。还是那句话，学习是螺旋上升的，加油的。小伙伴们


## I/O模型 

### I/O 模型基本说明

> I/O 模型简单的理解：就是用什么样的通道进行数据的发送和接收，很大程度上决定了程序通信的性能

> Java共支持3种网络编程模型/IO模式：BIO、NIO、AIO

- Java BIO ： 同步并阻塞(传统阻塞型)，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销 

- Java NIO ： 同步非阻塞，服务器实现模式为一个线程处理多个请求(连接)，即客户端发送的连接请求都会注册到多路复用器上，多路复用器轮询到连接有I/O请求就进行处理 

- Java AIO(NIO.2) ： 异步非阻塞，AIO 引入异步通道的概念，采用了 Proactor 模式，简化了程序编写，有效的请求才启动线程，它的特点是先由操作系统完成后才通知服务端程序启动线程去处理，一般适用于连接数较多且连接时间较长的应用


### BIO、NIO、AIO适用场景分析

- BIO方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限于应用中，JDK1.4以前的唯一选择，但程序简单易理解。

- NIO方式适用于连接数目多且连接比较短（轻操作）的架构，比如聊天服务器，弹幕系统，服务器间通讯等。编程比较复杂，JDK1.4开始支持。

- AIO方式使用于连接数目多且连接比较长（重操作）的架构，比如相册服务器，充分调用OS参与并发操作，编程比较复杂，JDK7开始支持。

## Java BIO 基本介绍

Java BIO 就是传统的java io 编程，其相关的类和接口在 java.io 
BIO(blocking I/O) ： 

同步阻塞，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销，可以通过线程池机制改善(实现多个客户连接服务器)。 【后有应用实例】


## Java BIO 工作机制


工作原理图

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/682ade66810a45c08196378dc406d6d7~tplv-k3u1fbpfcp-zoom-1.image)


### BIO编程简单流程
 
 就是任何一个网络通信框架都是一样的，就好比tomcat对吧，我们来看看他们基本流程，并且我们自己用代码去实现一下这个流程吧，这样我们就能对整个bio有了很清晰的了解了，他的步骤分为以下几步
 
- 服务器端启动一个ServerSocket
- 客户端启动Socket对服务器进行通信，默认情况下服务器端需要对每个客户 建立一个线程与之通讯
- 客户端发出请求后, 先咨询服务器是否有线程响应，如果没有则会等待，或者被拒绝
- 如果有响应，客户端线程会等待请求结束后，在继续执行


## Java BIO 应用实例
实例说明：
使用BIO模型编写一个服务器端，监听6666端口，当有客户端连接时，就启动一个线程与之通讯。

要求使用线程池机制改善，可以连接多个客户端.
服务器端可以接收客户端发送的数据(telnet 方式即可)。


### 具体的实现代码，和演示

```
package com.itheima.mq.rocketmq;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/8/1 13:22
 */
public class BIOServer {

    public static void main(String[] args) throws Exception {

        //创建一个线程池,每连接到一个客户端，就启动一个线程和客户端进行通信
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

        @SuppressWarnings("resource")
        ServerSocket server=new ServerSocket(6666);
        System.out.println("tomcat服务器启动...");
        while(true){
            //阻塞， 等待客户端连接
            final Socket socket = server.accept();
            System.out.println("连接到一个客户端！");
            newCachedThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    //业务处理
                    handler(socket);
                }
            });
        }

    }

    /**
     * 处理
     * @param socket
     */
    public static void handler(Socket socket){
        try {
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();

            while(true){
                //读客户端数据 阻塞
                int read = inputStream.read(bytes);
                if(read != -1){
                    System.out.println(new String(bytes, 0, read));
                }else{
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                System.out.println("关闭和client的连接..");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

```
上面的代码 用的也是jdk自带的线程池，怎么说呢，其实我看到很多开源框架都用了自带的线程池，就连阿里的框架也是nacos，其实说什么自己的线程池会oom啥的，这个得自测了才行。

### 测试结果

这边小六六没有自己写客户端了，而是用的telent 

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ed97c3b384ff4dbc998e2d9f7cf341ea~tplv-k3u1fbpfcp-zoom-1.image)


### 总结一下 

- 第一个我们要 搞一个serverSocket

- ServerSocket server=new ServerSocket(6666);

- 然后通过  final Socket socket = server.accept()，获得一个socket 对象，当客户端没有发送信息给服务端的时候，此时的线程是阻塞的。之后就是相互通信了


## 结尾

我们来看看BIO这种IO模型有什么问题

- 每个请求都需要创建独立的线程，与对应的客户端进行数据 Read，业务处理，数据 Write 。
- 当并发数较大时，需要创建大量线程来处理连接，系统资源占用较大。
- 连接建立后，如果当前线程暂时没有数据可读，则线程就阻塞在 Read 操作上，造成线程资源浪费



![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！




