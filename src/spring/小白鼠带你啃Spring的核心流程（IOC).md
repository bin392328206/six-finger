# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   

## 絮叨  
前面几篇文章，有的没的说了一些Spring的相关知识点，并不是说讲的不好，只是可能说只是针对某一个知识点讲的，并不是那么系统，接下来就系统的讲讲，当然也是有借鉴人家的。

## Spring 里面的几个核心概念
### IoC 和 DI
- IoC （Inversion of Control），即控制反转。这不是一种新的技术，而是 Spring 的一种设计思想。
    - 在传统的程序设计，我们直接在对象内部通过 new 来创建对象，是程序主动去创建依赖对象；而在 Spring 中有专门的一个容器来创建和管理这些对象，并将对象依赖的其他对象注入到该对象中，这个容器我们一般称为 IoC 容器。
所有的类的创建、销毁都由 Spring 来控制，也就是说控制对象生存周期的不再是引用它的对象，而是 Spring。对于某个具体的对象而言，以前是它控制其他对象，现在是所有对象都被 Spring 控制，所以这叫控制反转。

- DI（Dependency Injection），即依赖注入，由 Martin Fowler 提出。可以认为 IoC 和 DI 其实是同一个概念的不同角度描述。

    - 依赖注入是指组件之间的依赖关系由容器在运行期决定，形象的说，即由容器动态的将某个依赖关系注入到组件之中。依赖注入的目的并非为软件系统带来更多功能，而是为了提升组件重用的频率，并为系统搭建一个灵活、可扩展的平台。
    通过依赖注入机制，我们只需要通过简单的配置，而无需任何代码就可指定目标需要的资源，完成自身的业务逻辑，而不需要关心具体的资源来自何处，由谁实现。
### bean 
官方概念：在 Spring 中，构成应用程序主干并由 Spring IoC 容器管理的对象称为 bean。 bean 是一个由 Spring IoC 容器实例化，组装和管理的对象。

大白话：bean 可以认为是那些我们想注入到 Spring IoC 容器的 Java 对象实例的抽象。

我们经常会在 Service 上使用 @Service 注解，然后在要使用该 Service 的类中通过 @Autowire 注解来注入，这个 Service 就是一个 bean。在这个地方，@Service 注解相当于告诉 IoC 容器：这个类你需要帮我创建和管理；而 @Autowire 注解相当于告诉 IoC 容器：我需要依赖这个类，你需要帮我注入进来。


Java对象和SpringBean的区别
- 第一个就是SpringBean一定是一个Java对象，但是Java对象并不一定是一个SpringBean
- 第二个就是SpringBean由Java对象产生的，通过Spring一系列操作，最后变成一个SpringBean


### BeanDefinition
理解了 bean，BeanDefinition 就好理解了。BeanDefinition 是 bean 的定义，用来存储 bean 的所有属性方法定义。

### BeanFactory 和 ApplicationContext
- BeanFactory：基础类型 IoC 容器，提供完整的 IoC 服务支持。

- ApplicationContext：BeanFactory 的子接口，在 BeanFactory 的基础上构建，是相对比较高级的 IoC 容器实现。包含 BeanFactory 的所有功能，还提供了其他高级的特性，比如：事件发布、国际化信息支持、统一资源加载策略等。正常情况下，我们都是使用的 ApplicationContext。

### FactoryBean

一般情况下，我们将 bean 的创建和管理都交给 Spring IoC 容器，Spring 会利用 bean 的 class 属性指定的类来实例化 bean。

但是如果我们想自己实现 bean 的创建操作，可以实现吗？答案是可以的，FactoryBean 就可以实现这个需求。

FactoryBean 是一种特殊的 bean，它是个工厂 bean，可以自己创建 bean 实例，如果一个类实现了 FactoryBean 接口，则该类可以自己定义创建实例对象的方法，只需要实现它的 getObject() 方法即可。

FactoryBean 可能对于普通开发来说基本用不到也没去注意过，但是它其实应用的非常广，特别是在中间件中，如果你看过一些中间件的源码，一定会看到 FactoryBean 的身影。


## 核心流程

### 容器构建启动入口

容器构建启动的入口有多种多样，这边以常用的 web.xml 配置的方式来说。

首先，我们会在 web.xml 中配置 ContextLoaderListener 监听器，当 Tomcat 启动时，会触发 ContextLoaderListener 的 contextInitialized 方法，从而开始 IoC 的构建流程。

另一个常用的参数是 contextConfigLocation，用于指定 Spring 配置文件的路径。



```
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="2.5" xmlns="http://java.sun.com/xml/ns/javaee" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
         http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
    <display-name>open-joonwhee-service WAR</display-name>
 
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            classpath*:config/spring/appcontext-*.xml
        </param-value>
    </context-param>
 
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
</web-app>
```

![](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2020/3/8/170b876e5e618ede~tplv-t2oaga2asx-image.image)

接下来我们来看看这个方法

![](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2020/3/8/170b8f27355c4e6a~tplv-t2oaga2asx-image.image)

- 下面我们一步步分析initWebApplicationContext类到底做了什么。
    1. 确定在初始化上下文中只存在一个ContextLoader* 相关的配置，否则抛出异
![](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2020/3/8/170b8f46d6af9138~tplv-t2oaga2asx-image.image)
 
    2.打印相关日志，并且计算开始时间。
![](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2020/3/8/170b8f58e12241ab~tplv-t2oaga2asx-image.image)
    3.在本地实例变量中存储上下文，保证它在ServletContext关闭时可用。其中createWebApplicationContext方法的作用是 “实例化该加载器的根WebApplicationContext，如果指定了默认上下文类或自定义上下文类”。默认上下文即 xmlWebApplicationContext。
![](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2020/3/8/170b8f6ab470ce94~tplv-t2oaga2asx-image.image)
    4.判断应用上下文是否是属于ConfigurableWebApplicationContext实例。默认的xmlWebApplicationContext是属于ConfigurableWebApplicationContext实例的。如果应用上下文没有生效，需要确认应用上下文是否设置了父上下文。loadParentContext方法主要功能是“具有默认实现（可能被子类覆盖）的模板方法，以加载或获取将用作根WebApplicationContext的父上下文的ApplicationContext实例。” **configureAndRefreshWebApplicationContext的主要作用就是“获取web.xml中配置的contextConfigLocation路径，获取应用的访问路径，获取spring相关的XML配置文件的路径，并且刷新当前的WebApplicationContext配置相关。” 最后将该上下文对象放入servlet上下文参数中。这个一步也就是我们经常说的Spring容器的启动过程，这边我就不细说了，细说说几万字**
![](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2020/3/8/170b8f85bf375554~tplv-t2oaga2asx-image.image)
    5.获取当前线程的类加载器， 如果ContextLoader的类加载器和当前线程的类加载器一样，则应用上下文对象赋值给currentContext。
![](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2020/3/8/170b8f9837c65b5d~tplv-t2oaga2asx-image.image)

### ApplicationContext 刷新前配置
在正式进入容器的刷新前，会进行一些前置操作。
- 确认要使用的容器，通常使用的是：XmlWebApplicationContext，如果是用 Spring Boot，一般是 AnnotationConfigApplicationContext，但其实都差别不大，最终都会继承 AbstractApplicationContext，核心逻辑也都是在 AbstractApplicationContext 中实现。
- 提供一个给开发者初始化 ApplicationContext 的机会，具体的使用如下

例子：ApplicationContextInitializer 扩展使用

创建一个 ApplicationContextInitializer 接口的实现类，例如下面的 SpringApplicationContextInitializer，并在 initialize 方法中进行自己的逻辑操作，例如：添加监听器、添加 BeanFactoryPostProcessor。

```
package com.joonwhee.open.spring;
 
import com.joonwhee.open.listener.EarlyListener;
import com.joonwhee.open.processor.MyBeanFactoryPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
 
/**
 
 */
public class SpringApplicationContextInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // 自己的逻辑实现
 
        // 例子1：通过硬编码的方式添加监听器
        EarlyListener earlyListener = new EarlyListener();
        applicationContext.addApplicationListener(earlyListener);
 
        // 例子2：通过硬编码的方式添加BeanFactoryPostProcessor
        MyBeanFactoryPostProcessor myBeanFactoryPostProcessor = new MyBeanFactoryPostProcessor();
        applicationContext.addBeanFactoryPostProcessor(myBeanFactoryPostProcessor);
    }
}
```
在web.xml中，定义 contextInitializerClasses 或 globalInitializerClasses 参数，参数值为 SpringApplicationContextInitializer 的全路径。

![](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2020/3/8/170b87b6f66f0b2f~tplv-t2oaga2asx-image.image)     
### 初始化 BeanFactory、加载 Bean 定义
1、创建一个新的 BeanFactory，默认为 DefaultListableBeanFactory。

2、根据 web.xml 中 contextConfigLocation 配置的路径，读取 Spring 配置文件，并封装成 Resource。

3、根据 Resource 加载 XML 配置文件，并解析成 Document 对象 。

4、从根节点开始，遍历解析 Document 中的节点。

4.1、对于默认命名空间的节点：先将 bean 节点内容解析封装成 BeanDefinition，然后将 beanName、BeanDefinition 放到 BeanFactory 的缓存中，用于后续创建 bean 实例时使用。

默认命名空间：http://www.springframework.org/schema/beans，可能存在的节点如下：

![](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2020/3/8/170b913e40213a0e~tplv-t2oaga2asx-image.image)

### 触发 BeanFactoryPostProcessor

实例化和调用所有 BeanFactoryPostProcessor，包括其子类 BeanDefinitionRegistryPostProcessor。

BeanFactoryPostProcessor 接口是 Spring 初始化 BeanFactory 时对外暴露的扩展点，Spring IoC 容器允许 BeanFactoryPostProcessor 在容器实例化任何 bean 之前读取 bean 的定义，并可以修改它。

BeanDefinitionRegistryPostProcessor 继承自 BeanFactoryPostProcessor，比 BeanFactoryPostProcessor 具有更高的优先级，主要用来在常规的 BeanFactoryPostProcessor 激活之前注册一些 bean 定义。特别是，你可以通过 BeanDefinitionRegistryPostProcessor 来注册一些常规的 BeanFactoryPostProcessor，因为此时所有常规的 BeanFactoryPostProcessor 都还没开始被处理。 

注：这边的 “常规 BeanFactoryPostProcessor” 主要用来跟 BeanDefinitionRegistryPostProcessor 区分。

例子：BeanFactoryPostProcessor 扩展使用
1. 创建一个 BeanFactoryPostProcessor 接口的实现类，例如下面的 MyBeanFactoryPostProcessor，并在 postProcessBeanFactory 方法中进行自己的逻辑操作。例如：扫描某个包路径，将该包路径下使用了某个注解的类全部注册到 Spring 中。

2. 将该实现类注册到 Spring 容器中，例如使用 @Component 注解

```

public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
     //看到沒有 我们此时可以拿到 beanFactory，你都拿到了 那你不是可以为所欲为了
        System.out.println("六脉神剑");
    }
}

```
另外，Mybatis 中的 MapperScannerConfigurer 是一个典型的 BeanDefinitionRegistryPostProcessor 的扩展使用，有兴趣的可以看看这个类的源码。 你想一下 Mapper这个类 是Spring的注解嘛，并不是 他是通过实现这个钩子来完成把自己的Bean 注入到Spring IOC容器中去的


### 注册 BeanPostProcessor

注册所有的 BeanPostProcessor，将所有实现了 BeanPostProcessor 接口的类加载到 BeanFactory 中。

BeanPostProcessor 接口是 Spring 初始化 bean 时对外暴露的扩展点，Spring IoC 容器允许 BeanPostProcessor 在容器初始化 bean 的前后，添加自己的逻辑处理。在这边只是注册到 BeanFactory 中，具体调用是在 bean 初始化的时候。

```
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
      //自己的李逻辑
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
      //自己的逻辑
        return bean;
    }
}
```

### 实例化所有剩余的非懒加载单例 bean

1、遍历所有被加载到缓存中的 beanName，触发所有剩余的非懒加载单例 bean 的实例化。

2、首先通过 beanName 尝试从缓存中获取，如果存在则跳过实例化过程；否则，进行 bean 的实例化。

3、根据 BeanDefinition，使用构造函数创建 bean 实例。

4、根据 BeanDefinition，进行 bean 实例属性填充。

5、执行 bean 实例的初始化。

5.1、触发 Aware 方法。

5.2、触发 BeanPostProcessor 的 postProcessBeforeInitialization 方法。

5.3、如果 bean 实现了 InitializingBean 接口，则触发 afterPropertiesSet() 方法。

5.4、如果 bean 设置了 init-method 属性，则触发 init-method 指定的方法。

5.5、触发 BeanPostProcessor 的 postProcessAfterInitialization 方法。

6、将创建好的 bean 实例放到缓存中，用于之后使用。（如果是循环依赖的话就更复杂）

7、完成上下文的刷新 完成Spring容器上下文的创建


## 结尾
这个是Spring IOC的一个大致过程吧，具体的内容我们后面再一一讲解。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！

