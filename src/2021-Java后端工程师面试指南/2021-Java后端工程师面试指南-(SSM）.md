
# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在** 

## Tips
面试指南系列，很多情况下不会去深挖细节，是小六六以被面试者的角色去回顾知识的一种方式，所以我默认大部分的东西，作为面试官的你，肯定是懂的。
> https://www.processon.com/view/link/600ed9e9637689349038b0e4

上面的是脑图地址
## 叨絮
SSM框架，这个是我们平时用的最多的，所以面试中也是经常被问到，今天我们就来看看这几个框架呗

然后下面是前面的文章汇总
 - [2021-Java后端工程师面试指南-(引言)](https://juejin.cn/post/6921868116481982477)
 - [2021-Java后端工程师面试指南-(Java基础篇）](https://juejin.cn/post/6924472756046135304)
 - [2021-Java后端工程师面试指南-(并发-多线程)](https://juejin.cn/post/6925213973205745672)
 - [2021-Java后端工程师面试指南-(JVM）](https://juejin.cn/post/6926349737394176014) 
 - [2021-Java后端工程师面试指南-(MySQL）](https://juejin.cn/post/6927105516485214216)
 - [2021-Java后端工程师面试指南-(Redis）](https://juejin.cn/post/6930816506489995272)
 - [2021-Java后端工程师面试指南-(Elasticsearch）](https://juejin.cn/post/6931558669239156743)
 - [2021-Java后端工程师面试指南-(消息队列）](https://juejin.cn/post/6932264624754524168)


## 什么是Spring
Spring 是一种轻量级开发框架，旨在提高开发人员的开发效率以及系统的可维护性。我们一般说 Spring 框架指的都是 Spring Framework，它是很多模块的集合，使用这些模块可以很方便地协助我们进行开发。这些模块是：核心容器、数据访问/集成,、Web、AOP（面向切面编程）、工具、消息和测试模块。比如：Core Container 中的 Core 组件是Spring 所有组件的核心，Beans 组件和 Context 组件是实现IOC和依赖注入的基础，AOP组件用来实现面向切面编程。

###  谈谈自己对于 Spring IoC 和 AOP 的理解
IoC（Inverse of Control:控制反转）是一种设计思想，就是 将原本在程序中手动创建对象的控制权，交由Spring框架来管理。 IoC 在其他语言中也有应用，并非 Spring 特有。 IoC 容器是 Spring 用来实现 IoC 的载体， IoC 容器实际上就是个Map（key，value）,Map 中存放的是各种对象。

将对象之间的相互依赖关系交给 IoC 容器来管理，并由 IoC 容器完成对象的注入。这样可以很大程度上简化应用的开发，把应用从复杂的依赖关系中解放出来。 IoC 容器就像是一个工厂一样，当我们需要创建一个对象的时候，只需要配置好配置文件/注解即可，完全不用考虑对象是如何被创建出来的。 在实际项目中一个 Service 类可能有几百甚至上千个类作为它的底层，假如我们需要实例化这个 Service，你可能要每次都要搞清这个 Service 所有底层类的构造函数，这可能会把人逼疯。如果利用 IoC 的话，你只需要配置好，然后在需要的地方引用就行了，这大大增加了项目的可维护性且降低了开发难度。

AOP(Aspect-Oriented Programming:面向切面编程)能够将那些与业务无关，却为业务模块所共同调用的逻辑或责任（例如事务处理、日志管理、权限控制等）封装起来，便于减少系统的重复代码，降低模块间的耦合度，并有利于未来的可拓展性和可维护性。

Spring AOP就是基于动态代理的，如果要代理的对象，实现了某个接口，那么Spring AOP会使用JDK Proxy，去创建代理对象，而对于没有实现接口的对象，就无法使用 JDK Proxy 去进行代理了，这时候Spring AOP会使用Cglib ，这时候Spring AOP会使用 Cglib 生成一个被代理对象的子类来作为代理.

一般我们可以使用 AspectJ ,Spring AOP 已经集成了AspectJ ，AspectJ 应该算的上是 Java 生态系统中最完整的 AOP 框架了。

使用 AOP 之后我们可以把一些通用功能抽象出来，在需要用到的地方直接使用即可，这样大大简化了代码量。我们需要增加新功能时也方便，这样也提高了系统扩展性。日志功能、事务管理等等场景都用到了 AOP 。
### 那你聊聊Spring AOP 和 AspectJ AOP 有什么区别？ 平时在项目中你一般用的哪个
Spring AOP 属于运行时增强，而 AspectJ 是编译时增强。 Spring AOP 基于代理(Proxying)，而 AspectJ 基于字节码操作(Bytecode Manipulation)。

Spring AOP 已经集成了 AspectJ ，AspectJ 应该算的上是 Java 生态系统中最完整的 AOP 框架了。AspectJ 相比于 Spring AOP 功能更加强大，但是 Spring AOP 相对来说更简单，

我们一般在项目中用的AspectJ AoP比较多点

### Spring 的 bean 作用域（scope）类型，Spring Bean是否是线程安全的

Spring容器中的Bean是否线程安全，容器本身并没有提供Bean的线程安全策略，因此可以说Spring容器中的Bean本身不具备线程安全的特性，但是具体还是要结合具体scope的Bean去研究。

Spring 的 bean 作用域（scope）类型

- singleton:单例，默认作用域。
- prototype:原型，每次创建一个新对象。
- request:请求，每次Http请求创建一个新对象，适用于WebApplicationContext环境下。
- session:会话，同一个会话共享一个实例，不同会话使用不用的实例。

线程安全这个问题，要从单例与原型Bean分别进行说明。

- 对于原型Bean,每次创建一个新对象，也就是线程之间并不存在Bean共享，自然是不会有线程安全的问题。
- 对于单例Bean,所有线程都共享一个单例实例Bean,因此是存在资源的竞争。
- 如果单例Bean,是一个无状态Bean，也就是线程中的操作不会对Bean的成员执行查询以外的操作，那么这个单例Bean是线程安全的。比如Spring mvc 的 Controller、Service、Dao等，这些Bean大多是无状态的，只关注于方法本身。
总结下
- 在@Controller/@Service等容器中，默认情况下，scope值是单例-singleton的，也是线程不安全的。
- 尽量不要在@Controller/@Service等容器中定义静态变量，不论是单例(singleton)还是多实例(prototype)他都是线程不安全的。
- 默认注入的Bean对象，在不设置scope的时候他也是线程不安全的。
- 一定要定义变量的话，用ThreadLocal来封装，这个是线程安全的


### 那你说说@Component 和 @Bean 的区别是什么？
- 作用对象不同: @Component 注解作用于类，而@Bean注解作用于方法。
- @Component通常是通过类路径扫描来自动侦测以及自动装配到Spring容器中（我们可以使用 @ComponentScan 注解定义要扫描的路径从中找出标识了需要装配的类自动装配到 Spring 的 bean 容器中）。@Bean 注解通常是我们在标有该注解的方法中定义产生这个 bean,@Bean告诉了Spring这是某个类的示例，当我需要用它的时候还给我。
- @Bean 注解比 Component 注解的自定义性更强，而且很多地方我们只能通过 @Bean 注解来注册bean。比如当我们引用第三方库中的类需要装配到 Spring容器时，则只能通过 @Bean来实现。


### 那你聊聊什么是 spring 装配，自动装配有哪些方式？
当 bean 在 Spring 容器中组合在一起时，它被称为装配或 bean 装配。 Spring 容器需要知道需要什么 bean 以及容器应该如何使用依赖注入来将 bean 绑定在一起，同时装配 bean。

Spring 容器能够自动装配 bean。也就是说，可以通过检查 BeanFactory 的内容让 Spring 自动解析 bean 的协作者。

自动装配的不同模式：
- no - 这是默认设置，表示没有自动装配。应使用显式 bean 引用进行装配。
- byName - 它根据 bean 的名称注入对象依赖项。它匹配并装配其属性与 XML 文件中由相同名称定义的 bean。
- byType - 它根据类型注入对象依赖项。如果属性的类型与 XML 文件中的一个 bean 名称匹配，则匹配并装配属性。
- 构造函数 - 它通过调用类的构造函数来注入依赖项。它有大量的参数。
- autodetect - 首先容器尝试通过构造函数使用 autowire 装配，如果不能，则尝试通过 byType 自动装配。

### 你知道@Autowired 注解有什么用？ 那@Qualifier呢？
- @Autowired 可以更准确地控制应该在何处以及如何进行自动装配。此注解用于在 setter 方法，构造函数，具有任意名称或多个参数的属性或方法上自动装配 bean。默认情况下，它是类型驱动的注入。
- 当您创建多个相同类型的 bean 并希望仅使用属性装配其中一个 bean 时，您可以使用@Qualifier 注解和 @Autowired 通过指定应该装配哪个确切的 bean 来消除歧义。

### 说说Spring Bean的生命周期
准确的了解Spring Bean的生命周期是非常必要的。我们通常使用ApplicationContext作为Spring容器。这里，我们讲的也是 ApplicationContext中Bean的生命周期。而实际上BeanFactory也是差不多的，只不过处理器需要手动注册。
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/216ae3bef6ef462b9e1b2a68c6bb7818~tplv-k3u1fbpfcp-watermark.image)
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/612d1dbcb1404ae39477575b608e2cea~tplv-k3u1fbpfcp-watermark.image)

其实在整个Bean的生命周期，也就是Bean初始化完成之前，我们的spring给我们提供了太多的拓展点了，可以让我们灵活的去解决我们不同的需求，下面来总结总结这些钩子函数
Bean的完整生命周期经历了各种方法调用，这些方法可以划分为以下几类：
- Bean自身的方法　　：　　这个包括了Bean本身调用的方法和通过配置文件中<bean>的init-method和destroy-method指定的方法
- Bean级生命周期接口方法　　：　　这个包括了BeanNameAware、BeanFactoryAware、InitializingBean和DiposableBean这些接口的方法
- 容器级生命周期接口方法　　：　　这个包括了InstantiationAwareBeanPostProcessor 和 BeanPostProcessor 这两个接口实现，一般称它们的实现类为“后处理器”。

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8d422a180b7d43f5a0e6fcdb3c20d915~tplv-k3u1fbpfcp-watermark.image)

### 聊聊Web容器的启动过程吧，说说它的启动方式
首先我们来聊聊Spring容器的启动方式，也就是我们整个web项目的一个启动方式，目前主流的公司一般分为2种，一种基于ssm的启动流程，一种是基于SpringBoot的启动过程，今天我就来说说ssm的一个启动流程，springboot的下次和springcloud系列一起讲

SSM的SpringBoot的启动流程
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e8a269710a724f81b5449c4d290a1d10~tplv-k3u1fbpfcp-watermark.image)

- 首先，对于一个web应用，其部署在web容器中，web容器提供其一个全局的上下文环境，这个上下文就是ServletContext，其为后面的spring IoC容器提供宿主环境；

- 然后就是我们的web.xml，在几年前的项目我想大家都有碰到过吧，在web.xml中会提供有contextLoaderListener。在web容器启动时，会触发容器初始化事件，此时 contextLoaderListener会监听到这个事件，其contextInitialized方法会被调用，在这个方法中，spring会初始 化一个启动上下文，这个上下文被称为根上下文，即WebApplicationContext，这是一个接口类，确切的说，其实际的实现类是 XmlWebApplicationContext。这个就是spring的IoC容器，其对应的Bean定义的配置由web.xml中的 context-param标签指定。在这个IoC容器初始化完毕后，spring以WebApplicationContext.ROOTWEBAPPLICATIONCONTEXTATTRIBUTE为属性Key，将其存储到ServletContext中，便于获取；

- 再 次，contextLoaderListener监听器初始化完毕后，开始初始化web.xml中配置的Servlet，这里是DispatcherServlet，这个servlet实际上是一个标准的前端控制器，用以转发、匹配、处理每个servlet请 求。DispatcherServlet上下文在初始化的时候会建立自己的IoC上下文，用以持有spring mvc相关的bean。在建立DispatcherServlet自己的IoC上下文时，会利用WebApplicationContext.ROOTWEBAPPLICATIONCONTEXTATTRIBUTE先从ServletContext中获取之前的根上下文(即WebApplicationContext)作为自己上下文的parent上下文。有了这个 parent上下文之后，再初始化自己持有的上下文。这个DispatcherServlet初始化自己上下文的工作在其initStrategies方 法中可以看到，大概的工作就是初始化处理器映射、视图解析等。这个servlet自己持有的上下文默认实现类也是 XmlWebApplicationContext。初始化完毕后，spring以与servlet的名字相关(此处不是简单的以servlet名为 Key，而是通过一些转换，具体可自行查看源码)的属性为属性Key，也将其存到ServletContext中，以便后续使用。这样每个servlet 就持有自己的上下文，即拥有自己独立的bean空间，同时各个servlet共享相同的bean，即根上下文(第2步中初始化的上下文)定义的那些 bean。

上面就是我们整个SSM的启动过程了，也是几年前大多数企业级开发的一个整个项目启动流程哦，至于SpringBoot的方式，下一篇springboot再在

### 那你聊聊Spring容器的加载过程呗（ioc的启动流程）
上面的过程是整个web容器的启动过程，它里面包含了我们spring容器的启动流程，我现在就给大家详细的讲解我们ioc启动的加载过程

 AbstractApplicationContext.java 里的 refresh() 方法，这个方法就是构建整个 IoC 容器过程的完整代码，只要把这个方法里的每一行代码都了解了，基本上了解了大部分 ioc 的原理和功能。
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c98379349c8f4f749b9fa2403c321fbe~tplv-k3u1fbpfcp-watermark.image)
- prepareRefresh() 方法：为刷新准备新的上下文环境，设置其启动日期和活动标志以及执行一些属性的初始化。主要是一些准备工作，不是很重要的方法，可以先不关注
- obtainFreshBeanFactory() 方法：该方法会解析所有 Spring 配置文件（通常我们会放在 resources 目录下），将所有 Spring 配置文件中的 bean 定义封装成 BeanDefinition，加载到 BeanFactory 中。常见的，如果解析到<context:component-scan base-package="com.joonwhee.open" /> 注解时，会扫描 base-package 指定的目录，将该目录下使用指定注解（@Controller、@Service、@Component、@Repository）的 bean 定义也同样封装成 BeanDefinition，加载到 BeanFactory 中。 上面提到的“加载到 BeanFactory 中”的内容主要指的是以下3个缓存：
    - beanDefinitionNames缓存：所有被加载到 BeanFactory 中的 bean 的 beanName 集合。
    - beanDefinitionMap缓存：所有被加载到 BeanFactory 中的 bean 的 beanName 和 BeanDefinition 映射。
    - aliasMap缓存：所有被加载到 BeanFactory 中的 bean 的 beanName 和别名映射。
- prepareBeanFactory(beanFactory) 方法：配置 beanFactory 的标准上下文特征，例如上下文的 ClassLoader、后置处理器等。这个方法会注册3个默认环境 bean：environment、systemProperties 和 systemEnvironment，注册 2 个 bean 后置处理器：ApplicationContextAwareProcessor 和 ApplicationListenerDetector。
- postProcessBeanFactory(beanFactory) 方法：允许子类对 BeanFactory 进行后续处理，默认实现为空，留给子类实现。
- invokeBeanFactoryPostProcessors(beanFactory) 方法：实例化和调用所有 BeanFactoryPostProcessor，包括其子类 BeanDefinitionRegistryPostProcessor。
- registerBeanPostProcessors(beanFactory) 方法：注册所有的 BeanPostProcessor，将所有实现了 BeanPostProcessor 接口的类加载到 BeanFactory 中。
- initMessageSource() 方法：初始化消息资源 MessageSource
- initApplicationEventMulticaster() 方法:初始化应用的事件广播器 ApplicationEventMulticaster。
- onRefresh() 方法:该方法为模板方法，提供给子类扩展实现，可以重写以添加特定于上下文的刷新工作，默认实现为空。(在springboot中这个方法可是加载tomcat容器的)
- registerListeners() 方法：注册监听器。
- finishBeanFactoryInitialization(beanFactory) 方法：该方法会实例化所有剩余的非懒加载单例 bean。除了一些内部的 bean、实现了 BeanFactoryPostProcessor 接口的 bean、实现了 BeanPostProcessor 接口的 bean，其他的非懒加载单例 bean 都会在这个方法中被实例化，并且 BeanPostProcessor 的触发也是在这个方法中。（这个方法其实是核心方法了，包含我们的bea从beandifinition变成我们的容器中的bean最核心的方法了）
- finishRefresh() 方法：完成此上下文的刷新，主要是推送上下文刷新完毕事件（ContextRefreshedEvent ）到监听器。

比较重要的是下面几个点

- obtainFreshBeanFactory 创建一个新的 BeanFactory、读取和解析 bean 定义。
- invokeBeanFactoryPostProcessors 提供给开发者对 BeanFactory 进行扩展。
- registerBeanPostProcessors 提供给开发者对 bean 进行扩展。
- finishBeanFactoryInitialization 实例化剩余的所有非懒加载单例 bean。

### SpringMVC 工作原理了解吗? 基于页面Controller的类型

- 客户端（浏览器）发送请求，直接请求到 DispatcherServlet。
- DispatcherServlet 根据请求信息调用 HandlerMapping，解析请求对应的 Handler。
- 解析到对应的 Handler（也就是我们平常说的 Controller 控制器）后，开始由 HandlerAdapter 适配器处理。
- HandlerAdapter 会根据 Handler 来调用真正的处理器来处理请求，并处理相应的业务逻辑。
- 处理器处理完业务后，会返回一个 ModelAndView 对象，Model 是返回的数据对象，View 是个逻辑上的 View。
- ViewResolver 会根据逻辑 View 查找实际的 View。
- DispaterServlet 把返回的 Model 传给 View（视图渲染）。
- 把 View 返回给请求者（浏览器）



### 聊聊Spring 框架中用到了哪些设计模式？
- 工厂设计模式 : Spring使用工厂模式通过 BeanFactory、ApplicationContext 创建 bean 对象。
- 代理设计模式 : Spring AOP 功能的实现。
- 单例设计模式 : Spring 中的 Bean 默认都是单例的。
- 模板方法模式 : Spring 中 jdbcTemplate、hibernateTemplate 等以 Template 结尾的对数据库操作的类，它们就使用到了模板模式。
- 观察者模式: Spring 事件驱动模型就是观察者模式很经典的一个应用。
- 适配器模式 :Spring AOP 的增强或通知(Advice)使用到了适配器模式、spring MVC 中也是用到了适配器模式适配Controller。



### spring事务熟悉不，一般你用哪种实现方式
- 编程式事务，在代码中硬编码。(不推荐使用)
- 声明式事务，在配置文件中配置（推荐使用）
一般在我们企业级开发的过程中，一般都是用的声明式事务，声明式事务也分为2种一种是基于xml的，一种基于注解的，一般用注解的多点


### 说说 Spring 事务中哪几种事务传播行为?
支持当前事务的情况：
- TransactionDefinition.PROPAGATION_REQUIRED： 如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务。
- TransactionDefinition.PROPAGATION_SUPPORTS： 如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务的方式继续运行。
- TransactionDefinition.PROPAGATION_MANDATORY： 如果当前存在事务，则加入该事务；如果当前没有事务，则抛出异常。（mandatory：强制性）
不支持当前事务的情况：

- TransactionDefinition.PROPAGATION_REQUIRES_NEW： 创建一个新的事务，如果当前存在事务，则把当前事务挂起。
- TransactionDefinition.PROPAGATION_NOT_SUPPORTED： 以非事务方式运行，如果当前存在事务，则把当前事务挂起。
- TransactionDefinition.PROPAGATION_NEVER： 以非事务方式运行，如果当前存在事务，则抛出异常。

### 说说BeanFactory 和 FactoryBean的区别？
- BeanFactory是个Factory，也就是IOC容器或对象工厂，在Spring中，所有的Bean都是由BeanFactory(也就是IOC容器)来进行管理的，提供了实例化对象和拿对象的功能。
- FactoryBean是个Bean，这个Bean不是简单的Bean，而是一个能生产或者修饰对象生成的工厂Bean,它的实现与设计模式中的工厂模式和修饰器模式类似。


### 聊聊Spring的循环依赖吧

- spring的循环依赖主要是指两个类相互之间通过@Autowired自动依赖注入对方，即类A包含一个类B的对象引用并需要自动注入，类B包含一个类A的对象引用也需要自动注入。
- 对于循环依赖问题，spring根据注入方式的不同，采取不同的处理策略，对于双方都是使用属性值注入或者setter方法注入，则spring可以自动解决循环依赖注入问题，应用程序可以成功启动；对于双方都是使用构造函数注入对方或者主bean对象（Spring在启动过程中，先加载的bean对象）使用构造函数注入，则spring无法解决循环依赖注入，程序报错无法启动。



首先spring在单例的情况下是默认支持循环引用的；在不做任何配置的情况下，两个bean相互依赖是能初始化成功的；spring源码中在创建bean的时候先创建这个bean的对象，创建对象完成之后通过判断容器对象的allowCircularReferences属性决定是否允许缓存这个临时对象，如果能被缓存成功则通过缓存提前暴露这个临时对象来完成循环依赖；而这个属性默认为true，所以说spring默认支持循环依赖的，但是这个属性spring提供了api让程序员来修改，所以spring也提供了关闭循环引用的功能；

### 那你说说Spring是如何解决循环引用的
首先，Spring内部维护了三个Map，也就是我们通常说的三级缓存。

笔者翻阅Spring文档倒是没有找到三级缓存的概念，可能也是本土为了方便理解的词汇。

在Spring的DefaultSingletonBeanRegistry类中，你会赫然发现类上方挂着这三个Map：

- singletonObjects 它是我们最熟悉的朋友，俗称“单例池”“容器”，缓存创建完成单例Bean的地方。
- singletonFactories 映射创建Bean的原始工厂
- earlySingletonObjects 映射Bean的早期引用，也就是说在这个Map里的Bean不是完整的，甚至还不能称之为“Bean”，只是一个Instance.
后两个Map其实是“垫脚石”级别的，只是创建Bean的时候，用来借助了一下，创建完成就清掉了。

所以笔者前文对“三级缓存”这个词有些迷惑，可能是因为注释都是以Cache of开头吧。

为什么成为后两个Map为垫脚石，假设最终放在singletonObjects的Bean是你想要的一杯“凉白开”。

那么Spring准备了两个杯子，即singletonFactories和earlySingletonObjects来回“倒腾”几番，把热水晾成“凉白开”放到singletonObjects中。

### 循环引用的不同的场景，有哪些方法可以解决循环引用

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9bff6c4cd9f2428daa5f1845ca7aba1e~tplv-k3u1fbpfcp-watermark.image)

为啥有些注入方式不能解决循环依赖问题呢？源码中看出，他们并没有用到那个earlySingletonObjects这个缓存，所以就不能解决循环依赖

### 解决Spring无法解决的循环依赖的一些方式
项目中如果出现循环依赖问题，说明是spring默认无法解决的循环依赖，要看项目的打印日志，属于哪种循环依赖。目前包含下面几种情况：
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ee49a2dedbaf4b1baaba6eb0441bf204~tplv-k3u1fbpfcp-watermark.image)

生成代理对象产生的循环依赖
这类循环依赖问题解决方法很多，主要有：

- 使用@Lazy注解，延迟加载
- 使用@DependsOn注解，指定加载先后关系
- 修改文件名称，改变循环依赖类的加载顺序

### 说说什么是Mybatis？
- Mybatis是一个半ORM（对象关系映射）框架，它内部封装了JDBC，加载驱动、创建连接、创建statement等繁杂的过程，开发者开发时只需要关注如何编写SQL语句，可以严格控制sql执行性能，灵活度高。
- 作为一个半ORM框架，MyBatis 可以使用 XML 或注解来配置和映射原生信息，将 POJO映射成数据库中的记录，避免了几乎所有的 JDBC 代码和手动设置参数以及获取结果集。

### 说说 #{}和${}的区别是什么？
- #{}是预编译处理，${}是字符串替换。
- Mybatis在处理#{}时，会将sql中的#{}替换为?号，调用PreparedStatement的set方法来赋值；
- Mybatis在处理${}时，就是把${}替换成变量的值。
- 使用#{}可以有效的防止SQL注入，提高系统安全性。

### 说说Mybatis的一级、二级缓存:
一级缓存 事务范围：缓存只能被当前事务访问。缓存的生命周期 依赖于事务的生命周期当事务结束时，缓存也就结束生命周期。 在此范围下，缓存的介质是内存。 二级缓存 进程范围：缓存被进程内的所有事务共享。这些事务有 可能是并发访问缓存，因此必须对缓存采取必要的事务隔离机制。 缓存的生命周期依赖于进程的生命周期，进程结束时， 缓存也就结束了生命周期。进程范围的缓存可能会存放大量的数据， 所以存放的介质可以是内存或硬盘。

### 聊聊Mybatis的一个整体的架构吧

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/451ceb4d4b6f4d13808de028e1d4c5a2~tplv-k3u1fbpfcp-zoom-1.image)

其实哈，我觉得mybatis框架主要需要做的事情我们是知道的，为啥呢？因为其实如果我们没有mybatis 我们也可以做数据库操作对吧，那就是jdbc 的操作呗，那其实mybatis就是在封装在jdbc之上的一个框架而已，它所需要做的就是那么多，我来总结下
- 首先就是一些基础组件 连接管理，事务管理，配置的加载，缓存的处理
- 然后是核心的功能，我们参数映射，我们的sql解析，sql执行，我们的结果映射
- 之上就是封装我们统一的crud接口就好了，对就这么多咯。

上面就是整个mybatis做的事情了，当然每一块功能处理起来也是不那么简单的。


### 对Mybatis的源码熟悉吗，找你熟悉的地方聊聊呗
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8acfb84840f040dca51d31fb98b92087~tplv-k3u1fbpfcp-zoom-1.image)

上面的图是整个mybatis的一个核心流程，其实不过是spring也好，mybatis也好，所有的框架我们都可以把他们分为2个部分，一个就是初始化的过程，就是相当于做好准备工作来接客的过程，第二个就是实际的接客过程了，所以不管是讲哪个框架的源码都是这样来的，mybatis当然也是不例外的

- 初始化过程
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("configuration.xml"));




就是我们mybatis的核心在SqlSessionFactory，首先SqlSessionFactory build出来 这个过程就会涉及到解析各种配置文件，第一个要解析的就是configuration然后他的里面有很多的标签，你比如说properties等节点，然后里面有一个mapper节点，就是可以找到我们的mapper.xml 然后又去解析里面的节点，报告各种cach,select 等等，之后把解析好之后xml通过命名空间和我们的mapper接口绑定，并生成代码对象，把他放到konwsmapper 这个map容器里面。最后就可以生成这个SqlSessionFactory


- 真正的执行过程
就是当我们的mybatis准备好之后呢？我们拿到这个sqlsession对象之后，如果是不要spring集成的话，那么接下来当然是要去获取我们的mapper对象了 sqlsession.getMapper，当然这个获取的也是代理对象拉，然后到MapperMethod，然后SqlSession 将查询方法转发给 Executor。Executor 基于 JDBC 访问数据库获取数据。Executor 通过反射将数据转换成 POJO并返回给 SqlSession。将数据返回给调用者。

## 结束
接下来我们看看SpringBoot 和SpringCloud的组件哈，东西还很多，大家一起加油
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。 

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>微信 搜 "六脉神剑的程序人生" 回复888 有我找的许多的资料送给大家 