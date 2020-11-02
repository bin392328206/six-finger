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
- [[适合初中级Java程序员修炼手册从0搭建整个Web项目]（六）](https://juejin.im/post/6889627826451021831)

上一篇文章，就大概的讲解了一下rpc，因为我们要模仿的是dubbo,然后我就去看了下，dubbo,我擦源码太多了，然后因为我本身公司技术栈也不是dubbo,所以呢？我就去github上找写其他的模仿dubbo的项目来学习，然后我就找到了 我们Guide哥的 guide-rpc-framework,然后我就先跟着这个项目来学习dubbo吧！ 然后跟，今天我们先来聊聊dubbo的SPI先。这个的运用还是很广的。

## SPI(Service Provider Interface)

- 本质是 将接口实现类的全限定名配置在文件中，并由服务加载器读取配置文件，加载实现类。这样可以在运行时，动态为接口替换实现类。
- 在Java中SPI是被用来设计给服务提供商做插件使用的。基于策略模式 来实现动态加载的机制 。我们在程序只定义一个接口，具体的实现交个不同的服务提供者；在程序启动的时候，读取配置文件，由配置确定要调用哪一个实现；
- 通过 SPI 机制为我们的程序提供拓展功能，在dubbo中，基于 SPI，我们可以很容易的对 Dubbo 进行拓展。例如dubbo当中的protocol，LoadBalance等都是通过SPI机制扩展。

### Java SPI 
说概念的话，可能大家会比较蒙，我这边还是来看看例子吧，其实在小六六看来 这个spi机制可以类比于一个控制反转吧，反正你也不是自己去new你的实现类了，而是通过别人给你，差不多就是这个意思？我也不知道自己理解的怎么样。我们来看看spi机制呗！

- 首先，我们定义一个Car接口

```
package com.xiaoliuliu.spring.a;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/2 10:16
 */
public interface Car {
    String getBrand();
}

```

- 然后是它的2个实现类

```
package com.xiaoliuliu.six.finger.web.demo.rpc;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/2 10:17
 */
public class Benz implements Car {
    @Override
    public String getBrand() {
        System.out.println("benz car");
        return "Benz";
    }
}
```

```
package com.xiaoliuliu.six.finger.web.demo.rpc;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/2 10:16
 */
public class BM implements Car  {
    @Override
    public String getBrand() {
        System.out.println("BM car");
        return "BM";
    }
}
```

-  核心部分

> Java的Spi 其实是有固定写法的，是因为在源码里面是写死了的再在resources下创建META-INF/services 文件夹，并创建一个文件，文件名称为Car接口的全限定名package com.xiaoliuliu.spring.a.Car。内容为接口实现类的全限定类名。

```
com.xiaoliuliu.spring.a.Benz
com.xiaoliuliu.spring.a.BM
```
- 测试类

```
package com.xiaoliuliu.spring.a;

import java.util.ServiceLoader;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/2 10:17
 */
public class Test {
    public static void main(String[] args) {
        ServiceLoader<Car> serviceLoader = ServiceLoader.load(Car.class);
        for (Car car : serviceLoader) {
            System.out.println(car.getBrand());
        }
    }
}
```
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2fe26c08c5574e37a297fd8fd5befbca~tplv-k3u1fbpfcp-watermark.image)

> JAVA SPI实现了接口的定义与具体业务实现解耦，应用进程可以根据实际业务情况启用或替换具体组件。

举例：JAVA的java.sql包中就定义一个接口Driver，各个服务提供商实现该接口。当我们需要使用某个数据库时就导入相应的jar包。

缺点
- 不能按需加载。Java SPI在加载扩展点的时候，会一次性加载所有可用的扩展点，很多是不需要的，会浪费系统资源；
- 获取某个实现类的方式不够灵活，只能通过 Iterator 形式获取，不能根据某个参数来获取对应的实现类。
- 不支持AOP与依赖注入。
- JAVA SPI可能会丢失加载扩展点异常信息，导致追踪问题很困难；


### dubbo SPI

dubbo重新实现了一套功能更强的 SPI 机制, 支持了AOP与依赖注入，并且 利用缓存提高加载实现类的性能，同时 支持实现类的灵活获取，文中接下来将讲述SPI的应用与原理。

Dubbo的SPI接口都会使用@SPI注解标识，该注解的主要作用就是标记这个接口是一个SPI接口。源码如下:

```
@Documented@Retention(RetentionPolicy.RUNTIME)@Target({ElementType.TYPE})public @interface SPI {    /**
     * default extension name
     * 设置默认拓展类
     */
    String value() default "";
}
```


- 首先讲解下dubbo SPI的使用
添加 dubbo的依赖

```
    <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>dubbo</artifactId>
            <version>2.5.7</version>
        </dependency>

```

在Car 接口上 添加注解

```
/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/2 10:16
 */
@SPI
public interface Car {
    String getBrand();
}

```
配置文件的路径与文件名也暂时不变，文件内容调整如下：
```
benz=com.xiaoliuliu.spring.a.Benz
```

修改测试类

```
package com.xiaoliuliu.spring.a;

import com.alibaba.dubbo.common.extension.ExtensionLoader;

import java.util.ServiceLoader;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/2 10:17
 */
public class Test {
    public static void main(String[] args) {
        ExtensionLoader<Car> carExtensionLoader = ExtensionLoader.getExtensionLoader(Car.class);        //按需获取实现类对象
        Car car = carExtensionLoader.getExtension("benz");
        System.out.println(car.getBrand());
    }
}
```

- 结果

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/21cbc5c4a42c46b18e5101be62c18809~tplv-k3u1fbpfcp-watermark.image)



### 稍微分析一下 Dubbo的Spi 说说它对Java的拓展吧！

Dubbo对JDK SPI进行了扩展，对服务提供者配置文件中的内容进行了改造，由原来的提供者类的全限定名列表改成了KV形式的列表，这也导致了Dubbo中无法直接使用JDK ServiceLoader，所以，与之对应的，在Dubbo中有ExtensionLoader，ExtensionLoader是扩展点载入器，用于载入Dubbo中的各种可配置组件，比如：动态代理方式（ProxyFactory）、负载均衡策略（LoadBalance）、RCP协议（Protocol）、拦截器（Filter）、容器类型（Container）、集群方式（Cluster）和注册中心类型（RegistryFactory）等。

总之，Dubbo为了应对各种场景，它的所有内部组件都是通过这种SPI的方式来管理的，这也是为什么Dubbo需要将服务提供者配置文件设计成KV键值对形式，这个K就是我们在Dubbo配置文件或注解中用到的K，Dubbo直接通过服务接口（上面提到的ProxyFactory、LoadBalance、Protocol、Filter等）和配置的K从ExtensionLoader拿到服务提供的实现类。


#### 扩展功能介绍

- 方便获取扩展实现：JDK SPI仅仅通过接口类名获取所有实现，而ExtensionLoader则通过接口类名和key值获取一个实现；

- IOC依赖注入功能：Adaptive实现，就是生成一个代理类，这样就可以根据实际调用时的一些参数动态决定要调用的类了。

#### 具体实现的简单讲解

我看了下 dubbo 源码的ExtensionLoader类 大概有1000行代码 方法也挺多的，然后我就不打算拿它的来讲，我们拿上面我们参考的那个 javaguide模仿 dubbo项目的来讲解一下，下面是类的代码

```
package github.javaguide.extension;

import github.javaguide.utils.Holder;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * refer to dubbo spi: https://dubbo.apache.org/zh-cn/docs/source_code_guide/dubbo-spi.html
 */
@Slf4j
public final class ExtensionLoader<T> {

    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    private final Class<?> type;
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface.");
        }
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        // firstly get from cache, if not hit, create one
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }

    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }
        // firstly get from cache, if not hit, create one
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        // create a singleton if no instance exists
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    private T createExtension(String name) {
        // load all extension classes of type T from file and get specific one by name
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new RuntimeException("No such extension of name " + name);
        }
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException("Fail to create an instance of the extension class " + clazz);
            }
        }
        return instance;
    }

    private Map<String, Class<?>> getExtensionClasses() {
        // get the loaded extension class from the cache
        Map<String, Class<?>> classes = cachedClasses.get();
        // double check
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = new HashMap<>();
                    // load all extensions from our extensions directory
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {
            String line;
            // read every line
            while ((line = reader.readLine()) != null) {
                // get index of comment
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    // string after # is comment so we ignore it
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        final int ei = line.indexOf('=');
                        String name = line.substring(0, ei).trim();
                        String clazzName = line.substring(ei + 1).trim();
                        // our SPI use key-value pair so both of them must not be empty
                        if (name.length() > 0 && clazzName.length() > 0) {
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            extensionClasses.put(name, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }

            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

```

对比Dubbo来说 精简了很多。不过麻雀虽下，五脏俱全嘛，通过调用一个个来分析一下吧。


```
 ExtensionLoader<Car> carExtensionLoader = ExtensionLoader.getExtensionLoader(Car.class);        //按需获取实现类对象

```

相比于Java 的 Dubbo 每个接口的Spi 都是有一个ExtensionLoader 


![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d2bb9a89f7e64b3687f161a6d4d032b1~tplv-k3u1fbpfcp-watermark.image)

其实嘛，就相当于做了一个工厂的角色吧！大概就是这个意思了。但是拿到具体的实现类还是再下面的代码

```
        Car car = carExtensionLoader.getExtension("benz");
```

然后我来分析分析，这个代码

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2fc65f8ba3b6400ea5f0180ae008e6c9~tplv-k3u1fbpfcp-watermark.image)

其实就是我圈的这几个地方拉，这个不是我们写缓存的经典写法嘛，哈哈，其实就是多了一个本地缓存嘛，基本上大家都能懂吧


![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/fc7479f54a774f69b95033d2b1e4102e~tplv-k3u1fbpfcp-watermark.image)

通过扫描文件获得实现类

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0f8d22972c604f6486373f99b9e5e4fa~tplv-k3u1fbpfcp-watermark.image)

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/323d366c8cb44773ad48ceb541952372~tplv-k3u1fbpfcp-watermark.image)

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4df446aeeb224c1899bc3d3029c629db~tplv-k3u1fbpfcp-watermark.image)

再dubbo的源码里面是三个

**Dubbo默认依次扫描META-INF/dubbo/internal/、META-INF/dubbo/、META-INF/services/三个classpath目录下的配置文件。**


然后就基本上就这样了，本质上还是把接口和实现解耦。然后dubbo可能还加了IOC的相关东西，这边就没有一一分析了，有兴趣的大家自己去看看。

## 结尾
好了，rpc的第二篇，我们就分析到这了，希望对大家有所帮助。


## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！