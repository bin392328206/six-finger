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
今天呢 我们来搭建一下Spring IOC, 其实这个东西也是老生常谈的东西了，今天先出一个基于注解的，下次补一个基于xml的，上次说的基于Servlet的MVC后面也会补，大家一起慢慢来。小六六也是跟大家一起学习。其实我们知道，不管是基于 注解，还是基于XML,他们的不同在于生成BeanDefinition，只要得到他之后呢？后面的其实就是通用的了。
好了，下面我给大家来一一走一遍搭建流程
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（一）](https://juejin.im/post/6883284588110544904)
- [[适合初中级Java程序员修炼手册从0搭建整个Web项目]（二）](https://juejin.im/post/6884027512343494669)

这边建议一边下载源码，一边来看，如果觉得有问题的话


## 大家来看看，今天完成之后的包结构
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a0241d852c224031bdc428cdebb5ac82~tplv-k3u1fbpfcp-watermark.image)

先说整体流程，所谓的IOC容器，就是控制反转之后，我们把bean存放的地方，也就是在Java中其实也就是map,所以大致的初始化流程，就是先扫描注解，和xml 生成我们Beandefinition,然后再生成Bean,然后再提供给外面使用，整体的流程就是这么简单，但是里面Spring的实现，我真的是佩服，反正我是看源码看得云里雾里的，各种抽象和封装，可能这种编码能力和思想就是我们所欠缺的。


### 看看我的演示

- 启动类
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/fe184d7614bb4521b29254207d132ed4~tplv-k3u1fbpfcp-watermark.image)

- UserController
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/59945122608743d99c289f6f45859923~tplv-k3u1fbpfcp-watermark.image)
- UserServiceImpl
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/325f2aade0024c0cae5e03e8148ee2ba~tplv-k3u1fbpfcp-watermark.image)
- 请求参数 

http://localhost:8081/user/yes

- 结果
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7fc79878b81f442d9bbaffecfec290e0~tplv-k3u1fbpfcp-watermark.image)


### BeanFactory

<kbd>BeanFactory</kbd>是IOC容器的顶层父接口，大名鼎鼎的 <kbd>ApplicationContext</kbd>就是继承它，它定义了我们最常用的获取Bean的方法。

~~~java
package com.xiaoliuliu.six.finger.web.spring.ioc.factory;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/19 10:08
 * 这个接口也是Spring ioc的核心接口呢,总的来说，Siprng ioc的实现了 我们需要实现2种，一种是基于注解的实现，一种是基于xml的实现
 */
public interface BeanFactory {

    Object getBean(String name) throws Exception;

    <T> T getBean(Class<T> requiredType) throws Exception;

}

~~~

###  ApplicationContext
<kbd>ApplicationContext</kbd> 我们非常熟悉，继承了<kbd>BeanFactory</kbd>、<kbd>MessageSource</kbd>、<kbd>ApplicationEventPublisher</kbd>等等接口，功能非常强大
~~~java

/**
 * 空接口，大家明白就好
 * 原接口需要继承ListableBeanFactory, HierarchicalBeanFactory等等，这里就简单继承BeanFactory 
 **/
public interface ApplicationContext extends BeanFactory {

}
~~~
### DefaultApplicationContext
 相信大家都知道，<kbd>ApplicationContext </kbd>实现类中最重要的就是 **refresh()** 方法，它的流程就包括了**IOC容器初始化**、**依赖注入**和**AOP**，方法中的注释已经写的很明白了。


~~~java
public class DefaultApplicationContext implements ApplicationContext {

    //配置文件路径
    private String configLocation;

    public DefaultApplicationContext(String configLocation) {
        this.configLocation = configLocation;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refresh() throws Exception {
        //1、定位，定位配置文件

        //2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition

        //3、注册，把配置信息放到容器里面(伪IOC容器)
        //到这里为止，容器初始化完毕

        //4、把不是延时加载的类，提前初始化
    }

	@Override
    public Object getBean(String beanName) throws Exception {
        return null;
    }

	@Override
    public <T> T getBean(Class<T> requiredType) throws Exception {
        return (T) getBean(requiredType.getName());
    }
}
~~~

成员变量<kbd>configLocation</kbd>保存了我们的配置文件路径，所以这里就先把这个配置文件先新建出来。在resource目录下需要新建一个配置文件<kbd>application.properties</kbd>，并且指定扫描的包路径。
```bash
scanPackage=
```

### BeanDefinition
我们原来使用xml作为配置文件时，定义的Bean其实在IOC容器中被封装成了<kbd>BeanDefinition</kbd>，也就是Bean的配置信息，包括className、是否为单例、是否需要懒加载等等。它是一个interface，这里我们直接定义成class。

~~~java
@Data
public class BeanDefinition {

    private String beanClassName;

    private boolean lazyInit = false;

    private String factoryBeanName;

    public BeanDefinition() {}
}
~~~

###  BeanDefinitionReader

~~~java

public class BeanDefinitionReader {

    //配置文件
    private Properties config = new Properties();

    //配置文件中指定需要扫描的包名
    private final String SCAN_PACKAGE = "scanPackage";
 
     public BeanDefinitionReader(String... locations) {
 
     }
     
 	public Properties getConfig() {
         return config;
     }
 }
~~~

### BeanWrapper

当<kbd>BeanDefinition</kbd>的Bean配置信息被读取并实例化成一个实例后，这个实例封装在<kbd>BeanWrapper</kbd>中。

~~~java

public class BeanWrapper {

    /**Bean的实例化对象*/
    private Object wrappedObject;

    public BeanWrapper(Object wrappedObject) {
        this.wrappedObject = wrappedObject;
    }

    public Object getWrappedInstance() {
        return this.wrappedObject;
    }

    public Class<?> getWrappedClass() {
        return getWrappedInstance().getClass();
    }
}
~~~


### 读取配置文件

前面几个基础的类已经搭建好了，接下来就是定位和解析配置文件。这边是基于注解来生成Beandefinition。


在<kbd>DefaultApplicationContext</kbd>中，我们先完成第一步，定位和解析配置文件。
~~~java
private void refresh() throws Exception {
    //1、定位，定位配置文件
    reader = new BeanDefinitionReader(this.configLocation);

    //2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition

    //3、注册，把配置信息放到容器里面
    //到这里为止，容器初始化完毕

    //4、把不是延时加载的类，提前初始化
}
~~~


完成<kbd>BeanDefinitionReader</kbd>中的构造方法，流程分为三步走：

- 将我们传入的配置文件路径解析为文件流
- 将文件流保存为Properties，方便我们通过Key-Value的形式来读取配置文件信息
- 根据配置文件中配置好的扫描路径，开始扫描该路径下的所有class文件并保存到集合中

~~~java
/**保存了所有Bean的className*/
private List<String> registyBeanClasses = new ArrayList<>();

public BeanDefinitionReader(String... locations) {
    try(
        //1.定位，通过URL定位找到配置文件，然后转换为文件流
        InputStream is = this.getClass().getClassLoader()
                .getResourceAsStream(locations[0].replace("classpath:", ""))) {
                
        //2.加载，保存为properties
        config.load(is);
    } catch (IOException e) {
        e.printStackTrace();
    }

    //3.扫描，扫描资源文件(class)，并保存到集合中
    doScanner(config.getProperty(SCAN_PACKAGE));
}
~~~

### 扫描配置文件
<kbd>doScanner()</kbd>是递归方法，当它发现当前扫描的文件是目录时要发生递归，直到找到一个class文件，然后把它的全类名添加到集合中
~~~java
/**
  * 扫描资源文件的递归方法
  */
private void doScanner(String scanPackage) {
    URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.", "/"));
    File classPath = new File(url.getFile());
    for (File file : classPath.listFiles()) {
        if (file.isDirectory()) {
        	//如果是目录则递归调用，直到找到class
            doScanner(scanPackage + "." + file.getName());
        } else {
            if (!file.getName().endsWith(".class")) {
                continue;
            }
            String className = (scanPackage + "." + file.getName().replace(".class", ""));
            //className保存到集合
            registyBeanClasses.add(className);
        }
    }
}
~~~



# 封装成BeanDefinition
<kbd>refresh()</kbd>中接着填充下一步，将上一步扫描好的class集合封装进<kbd>BeanDefinition</kbd>
~~~java
private void refresh() throws Exception {
    //1、定位，定位配置文件
    reader = new BeanDefinitionReader(this.configLocation);

    //2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition
    List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

    //3、注册，把配置信息放到容器里面
    //到这里为止，容器初始化完毕

    //4、把不是延时加载的类，提前初始化
}
~~~

回到<kbd>BeanDefinitionReader</kbd>中填充<kbd>loadBeanDefinitions()</kbd>方法。逻辑是：扫描class集合，如果是被<kbd>@Component</kbd>注解的class则需要封装成<kbd>BeanDefinition</kbd>，表示着它将来可以被IOC进行管理。

~~~java
/**
  * 把配置文件中扫描到的所有的配置信息转换为BeanDefinition对象
  */
public List<BeanDefinition> loadBeanDefinitions() {
    List<BeanDefinition> result = new ArrayList<>();
    try {
        for (String className : registyBeanClasses) {
            Class<?> beanClass = Class.forName(className);
            //如果是一个接口，是不能实例化的，不需要封装
            if (beanClass.isInterface()) {
                continue;
            }

            Annotation[] annotations = beanClass.getAnnotations();
            if (annotations.length == 0) {
                continue;
            }

            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                //只考虑被@Component注解的class
                if (annotationType.isAnnotationPresent(Component.class)) {
                    //beanName有三种情况:
                    //1、默认是类名首字母小写
                    //2、自定义名字（这里暂不考虑）
                    //3、接口注入
                    result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

                    Class<?>[] interfaces = beanClass.getInterfaces();
                    for (Class<?> i : interfaces) {
                        //接口和实现类之间的关系也需要封装
                        result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                    }
                    break;
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return result;
}

/**
 * 相关属性封装到BeanDefinition
 */
private BeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
    BeanDefinition beanDefinition = new BeanDefinition();
    beanDefinition.setFactoryBeanName(factoryBeanName);
    beanDefinition.setBeanClassName(beanClassName);
    return beanDefinition;
}

/**
 * 将单词首字母变为小写
 */
private String toLowerFirstCase(String simpleName) {
    char [] chars = simpleName.toCharArray();
    chars[0] += 32;
    return String.valueOf(chars);
}
~~~

###  注册到容器

将<kbd>BeanDefinition</kbd>保存为以<kbd>factoryBeanName</kbd>为Key的Map

~~~java
//保存factoryBean和BeanDefinition的对应关系
private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

private void refresh() throws Exception {
    //1、定位，定位配置文件
    reader = new BeanDefinitionReader(this.configLocation);

    //2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition
    List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

    //3、注册，把配置信息放到容器里面
    //到这里为止，容器初始化完毕
    doRegisterBeanDefinition(beanDefinitions);

    //4、把不是延时加载的类，提前初始化
}

private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
    for (BeanDefinition beanDefinition : beanDefinitions) {
        if (beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
            throw new Exception("The \"" + beanDefinition.getFactoryBeanName() + "\" is exists!!");
        }
        beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
    }
}
~~~

### 非懒加载的提前初始化
这是<kbd>fresh()</kbd>的最后一步，逻辑是遍历<kbd>BeanDefinition</kbd>集合，将非懒加载的Bean提前初始化。
~~~java
public void refresh() throws Exception {

    //1、定位，定位配置文件
    reader = new BeanDefinitionReader(this.configLocation);

    //2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition
    List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

    //3、注册，把配置信息放到容器里面(伪IOC容器)
    //到这里为止，容器初始化完毕
    doRegisterBeanDefinition(beanDefinitions);

    //4、把不是延时加载的类，提前初始化
    doAutowired();
}

private void doAutowired() {
    for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {
        String beanName = beanDefinitionEntry.getKey();
        if (!beanDefinitionEntry.getValue().isLazyInit()) {
            try {
                getBean(beanName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
~~~
可见实例化的核心方法就是<kbd>getBean()</kbd>，它是<kbd>BeanFactory</kbd>中的接口方法，下面来具体实现它。

###  初始化核心方法getBean
核心逻辑也不难：
- 如果已经实例化了，则直接获取实例化后的对象返回即可。如果没有实例化则走后面的逻辑
- 拿到该Bean的<kbd>BeanDefinition </kbd>信息，通过反射实例化
- 将实例化后的对象封装到<kbd>BeanWrapper</kbd>中
- 将封装好的<kbd>BeanWrapper</kbd>保存到IOC容器（实际就是一个Map）中
- 依赖注入实例化的Bean
- 返回最终实例


~~~java
/**保存了真正实例化的对象*/
private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

@Override
public Object getBean(String beanName) throws Exception {
    //如果是单例，那么在上一次调用getBean获取该bean时已经初始化过了，拿到不为空的实例直接返回即可
    Object instance = getSingleton(beanName);
    if (instance != null) {
        return instance;
    }

    BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

    //1.调用反射初始化Bean
    instance = instantiateBean(beanName, beanDefinition);

    //2.把这个对象封装到BeanWrapper中
    BeanWrapper beanWrapper = new BeanWrapper(instance);

    //3.把BeanWrapper保存到IOC容器中去
    //注册一个类名（首字母小写，如helloService）
    this.factoryBeanInstanceCache.put(beanName, beanWrapper);
    //注册一个全类名（如com.lqb.HelloService）
    this.factoryBeanInstanceCache.put(beanDefinition.getBeanClassName(), beanWrapper);

    //4.注入
    populateBean(beanName, new BeanDefinition(), beanWrapper);

    return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
}

private Object instantiateBean(String beanName, BeanDefinition beanDefinition) {
//1、拿到要实例化的对象的类名
String className = beanDefinition.getBeanClassName();

//2、反射实例化，得到一个对象
Object instance = null;
try {
    Class<?> clazz = Class.forName(className);
    instance = clazz.newInstance();
} catch (Exception e) {
    e.printStackTrace();
}

return instance;
}
~~~

### 依赖注入

上一步中Bean只是实例化了，但是Bean中被<kbd>@Autowired</kbd>注解的变量还没有注入，如果这个时候去使用就会报空指针异常。下面是注入的逻辑：
- 拿到Bean中的所有成员变量开始遍历
- 过滤掉没有被<kbd>@Autowired</kbd>注解标注的变量
- 拿到被注解变量的类名，并从IOC容器中找到该类的实例（上一步已经初始化放在容器了）
- 将变量的实例通过反射赋值到变量中


~~~java
private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {

    Class<?> clazz = beanWrapper.getWrappedClass();

    //获得所有的成员变量
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
        //如果没有被Autowired注解的成员变量则直接跳过
        if (!field.isAnnotationPresent(Autowired.class)) {
            continue;
        }

        Autowired autowired = field.getAnnotation(Autowired.class);
        //拿到需要注入的类名
        String autowiredBeanName = autowired.value().trim();
        if ("".equals(autowiredBeanName)) {
            autowiredBeanName = field.getType().getName();
        }

        //强制访问该成员变量
        field.setAccessible(true);

        try {
            if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                continue;
            }
            //将容器中的实例注入到成员变量中
            field.set(beanWrapper.getWrappedInstance(), this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
~~~


### 改造
- 第一个改造的点，当然是MVC的地方
GetRequestHandler->handle
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f8594c8680c64ad08b49a471f5a1e732~tplv-k3u1fbpfcp-watermark.image)

什么意思呢？就是我要在Springmvc里面 拿到处理好的bean,那么我的controller 也是需要代理的，所以在这个地方加载了spring的ioc容器

这样就把srpingmvc 和spring合起来了


## 结尾
好了，今天我们把spring ioc的大致流程写了些，其实只是一个最简单的例子，有助于大家去理解spring，下次看看是补补我们的xml 还是把aop写写。


## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！