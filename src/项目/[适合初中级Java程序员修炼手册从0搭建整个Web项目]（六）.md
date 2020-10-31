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
前面是已经写好的章节，下面我给大家来一一走一遍搭建流程
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（一）](https://juejin.im/post/6883284588110544904)
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（二）](https://juejin.im/post/6884027512343494669)
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（三）](https://juejin.im/post/6885224199938867213)
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（四）](https://juejin.im/post/6885994038278193165)
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（五）](https://juejin.im/post/6888926292285063176)

我来总结一下前面我们已经完成的，我们已经完成了基于Netty的Http服务器，和springmvc spring ioc,spring aop相关的功能，小六六自我感觉良好，最少也有人加我微信跟小六六一起学习了,那么我今天继续，今天我们先来简简单单的了解一下rpc呗！


## RPC简介

RPC（Remote Procedure Call Protocol）远程调用： 远程过程调用是一种常用的分布式网络通信协议,它允许运行于 一台计算机的程序调用另一台计算机的子程序，同时将网络的通信细节隐藏起来， 使得用户无须额外地为这个交互作用编程。分布式系统之间的通信大都通过RPC实现

> 基本上很多的框架的底层都是用的rpc通信，比如我们熟知的	ES RocktMq 等等 分布式的框架几乎都是，所以自己撸撸RPC框架那就很有必要了呗！

今天小六六想说的是，我们不要说一口气就吃成一个胖子，我们先来实现一个最最最简单的案例，来看看rpc他的一个大致过程，然后我们再照着我们dubbo来写个rpc框架，可能会好很多，所以今天就是给大家来个小案例，带领大家先来看看rpc框架的主流思想。

### RPC请求过程
- 注册客户端服务

- 开启rpc服务端

- 客户端以本地的方式来调用服务端

- 客户端代理找到服务端地址,连接服务端,然后将参数、方法等通过发送给服务端

- 服务端接收请求后将数据进行解码,然后根据解码的信息来执行本地程序,并将执行的结果返回给客户端

- 客户端进行解码获取返回的结果


### 代码

#### IHello

```
package com.xiaoliuliu.six.finger.web.demo.rpc.simple.test;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/30 17:46
 * 远程服务接口
 */
public interface IHello {
     String sayHello(String info);
}
```

#### HelloService

```
package com.xiaoliuliu.six.finger.web.demo.rpc.simple.test;

import lombok.Data;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/30 17:47
 * 远程服务接口实现类（Server）
 */
@Data
public class HelloService implements IHello {

    @Override
    public String sayHello(String info) {
        String result = "hello : " + info;
        System.out.println(result);
        return result;
    }
}
```

#### RpcProxyServer 也是服务的暴露过程

```
package com.xiaoliuliu.six.finger.web.demo.rpc.simple.test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/30 17:47
 * 服务器代理实现
 */
public class RpcProxyServer {
    private IHello hello = new HelloService();

    public void publisherServer(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            while (true) {
                try (Socket socket = ss.accept()) {
                    try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                        String method = ois.readUTF();
                        Object[] objs = (Object[]) ois.readObject();
                        Class<?>[] types = new Class[objs.length];
                        for (int i = 0; i < types.length; i++) {
                            types[i] = objs[i].getClass();
                        }
                        Method m = HelloService.class.getMethod(method, types);
                        Object obj = m.invoke(hello, objs);

                        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
                            oos.writeObject(obj);
                            oos.flush();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```

#### RpcProxyClient

```
package com.xiaoliuliu.six.finger.web.demo.rpc.simple.test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/31 10:24
 * RPC 客户端代理实现
 */
public class RpcProxyClient<T> {
    public static Object proxyClient(Class clazz) {

      return   Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},new InvocationHandler() {
          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

              try (Socket socket = new Socket("localhost", 8000)) {
                  try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
                      oos.writeUTF(method.getName());
                      oos.writeObject(args);
                      oos.flush();

                      try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                          return ois.readObject();
                      }
                  }
              }
          }
        });
    }

}

```


#### RpcServer 测试

```
package com.xiaoliuliu.six.finger.web.demo.rpc.simple.test;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/31 10:26
 */
public class RpcServer {

    //发布服务
    public static void main(String[] args) {
        RpcProxyServer server = new RpcProxyServer();
        server.publisherServer(8000);
    }
}

```


#### RpcClient


```
package com.xiaoliuliu.six.finger.web.demo.rpc.simple.test;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/31 10:26
 */
public class RpcClient {

    // 调用服务
    public static void main(String[] args) {
        IHello hello = (IHello) RpcProxyClient.proxyClient(IHello.class);
        String s = hello.sayHello("小六六写rpc demo呀");
        System.out.println(s);

    }
}

```


#### 结果

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/20c8c09584d240b88a6df8fa775a2fac~tplv-k3u1fbpfcp-watermark.image)

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9f85f07b231e406c90228f498b53fad4~tplv-k3u1fbpfcp-watermark.image)


## 结尾
好了，rpc的开头，我们就写完了，其实也很简单，通过代理 和网络通信来屏蔽代码的实现细节，做过调用远程服务和本地服务一样，无感知。后面的话，我们就模仿一下dubbo,来完成它的部分功能，**dubbo的人家可不是一个单纯的rpc框架，它致力于服务治理，这个就很广了，我们后面再看。**


## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！