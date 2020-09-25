## 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨 

昨天有个群友取三七互娱面试，然后被问了几个问题，然后我一看，我擦，说实话，自己不一定答得上来，所以就在这里记录一下，并且学习一下，人家可是应届生面试呢？哎


## 说下快速重传

其实这题考的是网络的知识，如果我们开发人员，一直深耕于业务的话，那么我们这方便可能就会薄弱点
### 快速重传机制

我们知道Tcp的超时重传，那我们想想超时重传的一些缺点

- 当一个报文段丢失时，会等待一定的超时周期然后才重传分组，增加了端到端的时延。
- 当一个报文段丢失时，在其等待超时的过程中，可能会出现这种情况：其后的报文段已经被接收端接收但却迟迟得不到确认，发送端会认为也丢失了，从而引起不必要的重传，既浪费资源也浪费时间。

幸运的是，由于TCP采用的是累计确认机制，即当接收端收到比期望序号大的报文段时，便会重复发送最近一次确认的报文段的确认信号，我们称之为冗余ACK（duplicate ACK）。
如图所示，报文段1成功接收并被确认ACK 2，接收端的期待序号为2，当报文段2丢失，报文段3失序到来，与接收端的期望不匹配，接收端重复发送冗余ACK 2。
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/413a656d5bf241d9836757d7bc648462~tplv-k3u1fbpfcp-zoom-1.image)

其实很好理解，就是我发完我这个包之后，发送端就会继续发送下面的包，但是接收端如果没有确定收到，那么他就会一直给发送端发送那个没有收到ACK的序号

这样，如果在超时重传定时器溢出之前，接收到连续的三个重复冗余ACK（其实是收到4个同样的ACK，第一个是正常的，后三个才是冗余的），发送端便知晓哪个报文段在传输过程中丢失了，于是重发该报文段，不需要等待超时重传定时器溢出，大大提高了效率。这便是快速重传机制。


### 为什么是3次冗余ACK

首先要明白一点，即使发送端是按序发送，由于TCP包是封装在IP包内，IP包在传输时乱序，意味着TCP包到达接收端也是乱序的，乱序的话也会造成接收端发送冗余ACK。那发送冗余ACK是由于乱序造成的还是包丢失造成的，这里便需要好好权衡一番，因为把3次冗余ACK作为判定丢失的准则其本身就是估计值。


## 自己实现一个简单IOC容器呗
我看到这题，其实一开始也很无厘头，因为Spring那么多代码，一半是IOC，我怎么可能写的出来嘛，但是仔细想想，其实IOC的东西并不是那么多（我说的是核心的思想）但是你要实现他的拓展性，健壮性还是很有困难的，我们今天就来一开来实现一个最简单的IOC


### 有道无术，术尚可求
#### 什么是IOC呢？

IOC是Inversion of Control的缩写，多数书籍翻译成“控制反转”。

1996年，Michael Mattson在一篇有关探讨面向对象框架的文章中，首先提出了IOC 这个概念。对于面向对象设计及编程的基本思想，前面我们已经讲了很多了，不再赘述，简单来说就是把复杂系统分解成相互合作的对象，这些对象类通过封装以后，内部实现对外部是透明的，从而降低了解决问题的复杂度，而且可以灵活地被重用和扩展。

IOC理论提出的观点大体是这样的：**借助于“第三方”实现具有依赖关系的对象之间的解耦。如下图：**
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f1466c246b7c45489c45fba53e5dce76~tplv-k3u1fbpfcp-zoom-1.image)


大家看到了吧，由于引进了中间位置的“第三方”，也就是IOC容器，使得A、B、C、D这4个对象没有了耦合关系，齿轮之间的传动全部依靠“第三方”了，全部对象的控制权全部上缴给“第三方”IOC容器，所以，IOC容器成了整个系统的关键核心，它起到了一种类似“粘合剂”的作用，把系统中的所有对象粘合在一起发挥作用，如果没有这个“粘合剂”，对象与对象之间会彼此失去联系，这就是有人把IOC容器比喻成“粘合剂”的由来。



我们再来做个试验：把上图中间的IOC容器拿掉，然后再来看看这套系统：

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/05ae0153b7fb4066a5df23cb823e17b3~tplv-k3u1fbpfcp-zoom-1.image)

我们现在看到的画面，就是我们要实现整个系统所需要完成的全部内容。这时候，A、B、C、D这4个对象之间已经没有了耦合关系，彼此毫无联系，这样的话，当你在实现A的时候，根本无须再去考虑B、C和D了，对象之间的依赖关系已经降低到了最低程度。所以，如果真能实现IOC容器，对于系统开发而言，这将是一件多么美好的事情，参与开发的每一成员只要实现自己的类就可以了，跟别人没有任何关系！


我们再来看看，控制反转(IOC)到底为什么要起这么个名字？我们来对比一下：

软件系统在没有引入IOC容器之前，如图1所示，对象A依赖于对象B，那么对象A在初始化或者运行到某一点的时候，自己必须主动去创建对象B或者使用已经创建的对象B。无论是创建还是使用对象B，控制权都在自己手上。

软件系统在引入IOC容器之后，这种情形就完全改变了，如图3所示，由于IOC容器的加入，对象A与对象B之间失去了直接联系，所以，当对象A运行到需要对象B的时候，IOC容器会主动创建一个对象B注入到对象A需要的地方。

通过前后的对比，我们不难看出来：对象A获得依赖对象B的过程,由主动行为变为了被动行为，控制权颠倒过来了，这就是“控制反转”这个名称的由来。


**上面那么多的理论，其实再小六六看来，其实很简单嘛，就是我现在不能自己去New 对象了，我要找一个中间人，需要什么跟他说，他会给我准备好，这就是IOC的本质，大道至简，我们就基于这几句话来实现一个简单的IOC呗**


### 组件设计
我们基于上面的话来看看，我们需要什么要组件，首先得有BeanFactory IOC容器嘛，然后得有BeanDefinition  当然还需要加载Bean的资源加载器


容器用来存放初始化好的Bean，BeanDefinition 就是Bean的基本数据结构，比如Bean的名称，Bean的属性 PropertyValue，Bean的方法，是否延迟加载，依赖关系等。资源加载器就简单了，就是一个读取XML配置文件的类，读取每个标签并解析。


### 设计接口


#### BeanFactory

首先肯定需要一个BeanFactory，就是Bean容器，容器接口至少有2个最简单的方法，一个是获取Bean，一个注册Bean

```
package com.liuliu.ioc;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/24 16:20
 * 需要一个beanFactory 定义ioc 容器的一些行为 比如根据名称获取bean， 比如注册bean，参数为bean的名称，bean的定义
 */
public interface BeanFactory {

    /**
     * 根据bean的名称从容器中获取bean对象
     *
     * @param name bean 名称
     * @return bean实例
     * @throws Exception 异常
     */
    Object getBean(String name) throws Exception;

    /**
     * 将bean注册到容器中
     *
     * @param name bean 名称
     * @param bean bean实例
     * @throws Exception 异常
     */
    void registerBeanDefinition(String name, BeanDefinition bean) throws Exception;
}

```
#### BeanDefinition 
定义完了Bean最基本的容器，还需要一个最简单 BeanDefinition 接口，我们为了方便，但因为我们这个不必考虑扩展，因此可以直接设计为类，BeanDefinition 需要哪些元素和方法呢？ 需要一个 Bean 对象，一个Class对象，一个ClassName字符串，还需要一个元素集合 PropertyValues。这些就能组成一个最基本的 BeanDefinition 类了。那么需要哪些方法呢？其实就是这些属性的get set 方法。 我们看看该类的详细：

```
package com.liuliu.ioc;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/24 16:21
 */
public class BeanDefinition {
    /**
     * bean
     */
    private Object bean;

    /**
     * bean 的 CLass 对象
     */
    private Class beanClass;

    /**
     * bean 的类全限定名称
     */
    private String ClassName;

    /**
     * 类的属性集合
     */
    private PropertyValues propertyValues = new PropertyValues();

    /**
     * 获取bean对象
     */
    public Object getBean() {
        return this.bean;
    }

    /**
     * 设置bean的对象
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }

    /**
     * 获取bean的Class对象
     */
    public Class getBeanclass() {
        return this.beanClass;
    }

    /**
     * 通过设置类名称反射生成Class对象
     */
    public void setClassname(String name) {
        this.ClassName = name;
        try {
            this.beanClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取bean的属性集合
     */
    public PropertyValues getPropertyValues() {
        return this.propertyValues;
    }

    /**
     * 设置bean的属性
     */
    public void setPropertyValues(PropertyValues pv) {
        this.propertyValues = pv;
    }

}

```


#### BeanDefinitionReader
有了基本的 BeanDefinition 数据结构，还需要一个从XML中读取并解析为 BeanDefinition 的操作类，首先我们定义一个 BeanDefinitionReader 接口，该接口只是一个标识，具体由抽象类去实现一个基本方法和定义一些基本属性，比如一个读取时需要存放的注册容器，还需要一个委托一个资源加载器 ResourceLoader， 用于加载XML文件，并且我们需要设置该构造器必须含有资源加载器，当然还有一些get set 方法。


- AbstractBeanDefinitionReader

```
package com.liuliu.ioc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/24 16:44
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    /**
     * 注册bean容器
     */
    private Map<String, BeanDefinition> registry;

    /**
     * 资源加载器
     */
    private ResourceLoader resourceLoader;

    /**
     * 构造器器必须有一个资源加载器， 默认插件创建一个map容器
     *
     * @param resourceLoader 资源加载器
     */
    protected AbstractBeanDefinitionReader(ResourceLoader resourceLoader) {
        this.registry = new HashMap<>();
        this.resourceLoader = resourceLoader;
    }

    /**
     * 获取容器
     */
    public Map<String, BeanDefinition> getRegistry() {
        return registry;
    }

    /**
     * 获取资源加载器
     */
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

}
```
- BeanDefinitionReader 预留接口

```
package com.liuliu.ioc;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/24 16:44
 * 读取bean定义的接口
 */
public interface BeanDefinitionReader {
}

```

-xml 读取相关的类
	- Resource
 ```
 package com.liuliu.ioc;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/24 16:46
 */

import java.io.InputStream;

/**
 * 资源定义
 *
 * @author stateis0
 */
public interface Resource {

    /**
     * 获取输入流
     */
    InputStream getInputstream() throws Exception;
}

 ```

```
package com.liuliu.ioc;

import java.net.URL;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/24 16:46
 */
public class ResourceLoader {

    /**
     * 给定一个位置， 使用累加器的资源加载URL，并创建一个“资源URL”对象，便于获取输入流
     */
    public ResourceUrl getResource(String location) {
        URL url = this.getClass().getClassLoader().getResource(location);
        return new ResourceUrl(url);
    }
}

```


```
package com.liuliu.ioc;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/24 16:46
 */

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 资源URL
 */
public class ResourceUrl implements Resource {

    /**
     * 类库URL
     */
    private final URL url;

    /**
     * 需要一个类库URL
     */
    public ResourceUrl(URL url) {
        this.url = url;
    }

    /**
     * 从URL中获取输入流
     */
    @Override
    public InputStream getInputstream() throws Exception {
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
        return urlConnection.getInputStream();

    }

}

```


#### XmlBeanDefinitionReader 

好了， AbstractBeanDefinitionReader 需要的元素已经有了， 但是，很明显该方法不能实现读取 BeanDefinition 的任务。那么我们需要一个类去继承抽象类，去实现具体的方法， 既然我们是XML 配置文件读取，那么我们就定义一个 XmlBeanDefinitionReader 继承 AbstractBeanDefinitionReader ，实现一些我们需要的方法， 比如读取XML 的readrXML， 比如将解析出来的元素注册到 registry 的 Map 中， 一些解析的细节。我们还是看代码吧。

```
package com.liuliu.ioc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/24 16:55
 * 解析XML文件
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    /**
     * 构造器，必须包含一个资源加载器
     *
     * @param resourceLoader 资源加载器
     */
    public XmlBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    public void readerXML(String location) throws Exception {
        // 创建一个资源加载器
        ResourceLoader resourceloader = new ResourceLoader();
        // 从资源加载器中获取输入流
        InputStream inputstream = resourceloader.getResource(location).getInputstream();
        // 获取文档建造者工厂实例
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 工厂创建文档建造者
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        // 文档建造者解析流 返回文档对象
        Document doc = docBuilder.parse(inputstream);
        // 根据给定的文档对象进行解析，并注册到bean容器中
        registerBeanDefinitions(doc);
        // 关闭流
        inputstream.close();
    }

    /**
     * 根据给定的文档对象进行解析，并注册到bean容器中
     *
     * @param doc 文档对象
     */
    private void registerBeanDefinitions(Document doc) {
        // 读取文档的根元素
        Element root = doc.getDocumentElement();
        // 解析元素的根节点及根节点下的所有子节点并添加进注册容器
        parseBeanDefinitions(root);
    }

    /**
     * 解析元素的根节点及根节点下的所有子节点并添加进注册容器
     *
     * @param root XML 文件根节点
     */
    private void parseBeanDefinitions(Element root) {
        // 读取根元素的所有子元素
        NodeList nl = root.getChildNodes();
        // 遍历子元素
        for (int i = 0; i < nl.getLength(); i++) {
            // 获取根元素的给定位置的节点
            Node node = nl.item(i);
            // 类型判断
            if (node instanceof Element) {
                // 强转为父类型元素
                Element ele = (Element) node;
                // 解析给给定的节点，包括name，class，property， name， value，ref
                processBeanDefinition(ele);
            }
        }
    }

    /**
     * 解析给给定的节点，包括name，class，property， name， value，ref
     *
     * @param ele XML 解析元素
     */
    private void processBeanDefinition(Element ele) {
        // 获取给定元素的 name 属性
        String name = ele.getAttribute("name");
        // 获取给定元素的 class 属性
        String className = ele.getAttribute("class");
        // 创建一个bean定义对象
        BeanDefinition beanDefinition = new BeanDefinition();
        // 设置bean 定义对象的 全限定类名
        beanDefinition.setClassname(className);
        // 向 bean 注入配置文件中的成员变量
        addPropertyValues(ele, beanDefinition);
        // 向注册容器 添加bean名称和bean定义
        getRegistry().put(name, beanDefinition);
    }

    /**
     * 添加配置文件中的属性元素到bean定义实例中
     *
     * @param ele 元素
     * @param beandefinition bean定义 对象
     */
    private void addPropertyValues(Element ele, BeanDefinition beandefinition) {
        // 获取给定元素的 property 属性集合
        NodeList propertyNode = ele.getElementsByTagName("property");
        // 循环集合
        for (int i = 0; i < propertyNode.getLength(); i++) {
            // 获取集合中某个给定位置的节点
            Node node = propertyNode.item(i);
            // 类型判断
            if (node instanceof Element) {
                // 将节点向下强转为子元素
                Element propertyEle = (Element) node;
                // 元素对象获取 name 属性
                String name = propertyEle.getAttribute("name");
                // 元素对象获取 value 属性值
                String value = propertyEle.getAttribute("value");
                // 判断value不为空
                if (value != null && value.length() > 0) {
                    // 向给定的 “bean定义” 实例中添加该成员变量
                    beandefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, value));
                } else {
                    // 如果为空，则获取属性ref
                    String ref = propertyEle.getAttribute("ref");
                    if (ref == null || ref.length() == 0) {
                        // 如果属性ref为空，则抛出异常
                        throw new IllegalArgumentException(
                                "Configuration problem: <property> element for property '"
                                        + name + "' must specify a ref or value");
                    }
                    // 如果不为空，测创建一个 “bean的引用” 实例，构造参数为名称，实例暂时为空
                    BeanReference beanRef = new BeanReference(name);
                    // 向给定的 “bean定义” 中添加成员变量
                    beandefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, beanRef));
                }
            }
        }
    }

}

```

可以说代码注释写的非常详细，该类方法如下：

- public void readerXML(String location) 公开的解析XML的方法，给定一个位置的字符串参数即可。
- private void registerBeanDefinitions(Document doc) 给定一个文档对象，并进行解析。
- private void parseBeanDefinitions(Element root) 给定一个根元素，循环解析根元素下所有子元素。
- private void processBeanDefinition(Element ele) 给定一个子元素，并对元素进行解析，然后拿着解析出来的数据创建一个 BeanDefinition 对象。并注册到BeanDefinitionReader 的 Map 容器（该容器存放着解析时的所有Bean）中。
- private void addPropertyValues(Element ele, BeanDefinition beandefinition) 给定一个元素，一个 BeanDefinition 对象，解析元素中的 property 元素， 并注入到 BeanDefinition 实例中。
一共5步，完成了解析XML文件的所有操作。 最终的目的是将解析出来的文件放入到 BeanDefinitionReader 的 Map 容器中。



### 真正的Bean容器

好了，到这里，我们已经完成了从XML文件读取并解析的步骤，那么什么时候放进BeanFactory的容器呢？ 刚刚我们只是放进了 AbstractBeanDefinitionReader 的注册容器中。因此我们要根据BeanFactory 的设计来实现如何构建成一个真正能用的Bean呢？因为刚才的哪些Bean只是一些Bean的信息。没有我们真正业务需要的Bean。

```
package com.liuliu.ioc;

import java.util.HashMap;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/9/24 16:58
 * 一个抽象类， 实现了 bean 的方法，包含一个map，用于存储bean 的名字和bean的定义
 */
public abstract class AbstractBeanFactory implements BeanFactory {

    /**
     * 容器
     */
    private HashMap<String, BeanDefinition> map = new HashMap<>();

    /**
     * 根据bean的名称获取bean， 如果没有，则抛出异常 如果有， 则从bean定义对象获取bean实例
     */
    @Override
    public Object getBean(String name) throws Exception {
        BeanDefinition beandefinition = map.get(name);
        if (beandefinition == null) {
            throw new IllegalArgumentException("No bean named " + name + " is defined");
        }
        Object bean = beandefinition.getBean();
        if (bean == null) {
            bean = doCreate(beandefinition);
        }
        return bean;
    }

    /**
     * 注册 bean定义 的抽象方法实现，这是一个模板方法， 调用子类方法doCreate，
     */
    @Override
    public void registerBeanDefinition(String name, BeanDefinition beandefinition) throws Exception {
        Object bean = doCreate(beandefinition);
        beandefinition.setBean(bean);
        map.put(name, beandefinition);
    }

    /**
     * 减少一个bean
     */
    abstract Object doCreate(BeanDefinition beandefinition) throws Exception;
}
```

该类实现了接口的2个基本方法，一个是getBean，一个是 registerBeanDefinition， 我们也设计了一个抽象方法供这两个方法调用，将具体逻辑创建逻辑延迟到子类。这是什么设计模式呢？模板模式。主要还是看 doCreate 方法，就是创建bean 具体方法，所以我们还是需要一个子类， 叫什么呢? AutowireBeanFactory， 自动注入Bean，这是我们这个标准Bean工厂的工作。看看代码吧？


可以看到 doCreate 方法使用了反射创建了一个对象，并且还需要对该对象进行属性注入，如果属性是 ref 类型，那么既是依赖关系，则需要调用 getBean 方法递归的去寻找那个Bean（因为最后一个Bean 的属性肯定是基本类型）。这样就完成了一次获取实例化Bean操作，并且也实现类依赖注入。

## 总结
我们通过这些代码实现了一个简单的 IOC 依赖注入的功能，也更加了解了 IOC， 以后遇到Spring初始化的问题再也不会手足无措了。直接看源码就能解决。但是这只是冰山一角，你还得实现包扫描，扫描那些Bean需要先注入到容器中，还有就是Spring 有各种前置前置处理器 后置处理器，还有他的三级缓存，我们上面只有一级缓存，等等，大牛的设计真的牛皮，骚年们 加油吧！

具体代码楼主放在了[github](https://github.com/stateIs0/simpleIoc/blob/master/src/main/java/cn/thinkinjava/myspring/factory/AutowireBeanFactory.java)上，地址：自己实现的一个简单IOC,包括依赖注入

good luck ！！！

<h2 data-tool="mdnice编辑器" style="padding: 0px; font-weight: bold; color: black; font-size: 22px; line-height: 1.5em; margin-top: 2.2em; margin-bottom: 35px;"><span class="prefix" style="display: none;"></span><span class="content" style="display: inline-block; font-weight: bold; background: linear-gradient(#fff 60%, #ffb11b 40%); color: #515151; padding: 2px 13px 2px; margin-right: 3px; height: 50%;">日常求赞</span><span class="suffix"></span></h2>
<blockquote data-tool="mdnice编辑器" style="display: block; font-size: 0.9em; overflow: auto; overflow-scrolling: touch; border-left: 3px solid rgba(0, 0, 0, 0.4); color: #6a737d; padding-top: 10px; padding-bottom: 10px; padding-left: 20px; padding-right: 10px; margin-bottom: 20px; margin-top: 20px; border-left-color: #ffb11b; background: #fff5e3;">
<p style="font-size: 16px; padding-top: 8px; padding-bottom: 8px; padding: 0; margin: 0px; line-height: 26px; color: #595959;">好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是<strong style="font-weight: bold; color: black;">真粉</strong>。</p>
</blockquote>
<blockquote data-tool="mdnice编辑器" style="display: block; font-size: 0.9em; overflow: auto; overflow-scrolling: touch; border-left: 3px solid rgba(0, 0, 0, 0.4); color: #6a737d; padding-top: 10px; padding-bottom: 10px; padding-left: 20px; padding-right: 10px; margin-bottom: 20px; margin-top: 20px; border-left-color: #ffb11b; background: #fff5e3;">
<p style="font-size: 16px; padding-top: 8px; padding-bottom: 8px; padding: 0; margin: 0px; line-height: 26px; color: #595959;">创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见</p>
</blockquote>
<blockquote data-tool="mdnice编辑器" style="display: block; font-size: 0.9em; overflow: auto; overflow-scrolling: touch; border-left: 3px solid rgba(0, 0, 0, 0.4); color: #6a737d; padding-top: 10px; padding-bottom: 10px; padding-left: 20px; padding-right: 10px; margin-bottom: 20px; margin-top: 20px; border-left-color: #ffb11b; background: #fff5e3;">
<p style="font-size: 16px; padding-top: 8px; padding-bottom: 8px; padding: 0; margin: 0px; line-height: 26px; color: #595959;">六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！</p>
</blockquote>
