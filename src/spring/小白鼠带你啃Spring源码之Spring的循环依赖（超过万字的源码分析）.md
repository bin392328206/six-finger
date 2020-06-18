# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨  
同样这篇文章也是直接copy的，因为子路老师已经写得很好了。我这边相当于重新学习一遍，加深印象

## 众所周知spring在默认单例的情况下是支持循环引用的


### Appconfig.java类的代码


```
@Configurable
@ComponentScan("com.shadow")
public class Appconfig {
}

```
### X.java类的代码

```
package com.shadow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class X {

	@Autowired
	Y y;

	public X(){
		System.out.println("X create");
	}
}

```
### Y.java的代码

```
package com.shadow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Y {
	@Autowired
	X x;
	
	public Y(){
		System.out.println("Y create");
	}
}

```

这两个类非常简单，就是相互引用了对方，也就是我们常常的说的循环依赖，spring是允许这样的循环依赖(前提是单例的情况下的,非构造方法注入的情况下)

### 运行这段代码的结果下图

![](https://user-gold-cdn.xitu.io/2020/2/23/170712f837a3e8a9?w=1808&h=845&f=gif&s=1378554)
上面代码从容器中能正常获取到Xbean，说明循环依赖成功。但是spring的循环依赖其实是可以关闭的，spring提供了api来关闭循环依赖的功能。当然你也可以修改spring源码来关闭这个功能，这里笔者为了提高逼格，就修改一下spring的源码来关闭这个功能，老话说要想高明就得装逼。

### 下图是我修改spring源码运行的结果
我在AnnotationConfigApplicationContext的构造方法中加了一行setAllowCircularReferences(false);结果代码异常，循环依赖失败
![](https://user-gold-cdn.xitu.io/2020/2/23/170713121d6baafb?w=1808&h=845&f=gif&s=2519733)


那么为什么setAllowCircularReferences(false);会关闭循环依赖呢？首要明白spring的循环依赖是怎么做到的呢？spring源码当中是如何处理循环依赖的？ 分析一下所谓的循环依赖其实无非就是属性注入，或者就是大家常常说的自动注入， 故而搞明白循环依赖就需要去研究spring自动注入的源码；spring的属性注入属于spring bean的生命周期一部分；怎么理解spring bean的生命周期呢？注意笔者这里并不打算对bean的生命周期大书特书，只是需要读者理解生命周期的概念，细节以后在计较；
要理解bean的生命周期首先记住两个概念
- spring bean——受spring容器管理的对象，可能经过了完整的spring bean生命周期（为什么是可能？难道还有bean是没有经过bean生命周期的？答案是有的，具体我们后面文章分析），最终存在spring容器当中；一个bean一定是个对象
- 对象——任何符合java语法规则实例化出来的对象，但是一个对象并不一定是spring bean；

其实我也很认可作者的这2个概念，学习Spring如果能把这个2个抽象的概念具体化，对于我们真的有很大的帮助，而不是以一个半成品的Spring Bean去理解

### Bean生命周期
所谓的bean的生命周期就是磁盘上的类通过spring扫描，然后实例化，跟着初始化，继而放到容器当中的过程


![](https://user-gold-cdn.xitu.io/2020/2/23/170713599dd1b49c?w=1055&h=3086&f=png&s=294743)

上图就是spring容器初始化bean的大概过程(至于详细的过程，后面文章再来介绍)；
文字总结一下：
1. 实例化一个ApplicationContext的对象；
2. 调用bean工厂后置处理器完成扫描；
3. 循环解析扫描出来的类信息；
4. 实例化一个BeanDefinition对象来存储解析出来的信息；
5. 把实例化好的beanDefinition对象put到beanDefinitionMap当中缓存起来，以便后面实例化bean；
6. 再次调用bean工厂后置处理器；
7. 当然spring还会干很多事情，比如国际化，比如注册BeanPostProcessor等等，如果我们只关心如何实例化一个bean的话那么这一步就是spring调用finishBeanFactoryInitialization方法来实例化单例的bean，实例化之前spring要做验证，需要遍历所有扫描出来的类，依次判断这个bean是否Lazy，是否prototype，是否abstract等等；
8. 如果验证完成spring在实例化一个bean之前需要推断构造方法，因为spring实例化对象是通过构造方法反射，故而需要知道用哪个构造方法；
9. 推断完构造方法之后spring调用构造方法反射实例化一个对象；注意我这里说的是对象、对象、对象；这个时候对象已经实例化出来了，但是并不是一个完整的bean，最简单的体现是这个时候实例化出来的对象属性是没有注入，所以不是一个完整的bean；
10. spring处理合并后的beanDefinition(合并？是spring当中非常重要的一块内容，后面的文章我会分析)；
11. 判断是否支持循环依赖，如果支持则提前把一个工厂存入singletonFactories——map；
12. 判断是否需要完成属性注入
13. 如果需要完成属性注入，则开始注入属性
14. 判断bean的类型回调Aware接口
15. 调用生命周期回调方法
16. 如果需要代理则完成代理
17. put到单例池——bean完成——存在spring容器当中

## 用一个例子来证明上面的步骤

### Z.java的源码

```
package com.shadow.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Z implements ApplicationContextAware {
	@Autowired
	X x;//注入X

    //构造方法
	public Z(){
		System.out.println("Z create");
	}

    //生命周期初始化回调方法
	@PostConstruct
	public void zinit(){
		System.out.println("call z lifecycle init callback");
	}

	//ApplicationContextAware 回调方法
	@Override
	public void setApplicationContext(ApplicationContext ac) {
		System.out.println("call aware callback");
	}
}

```
来看看Z的生命周期，注意下图当中的字幕，会和上面的17个步骤一一对应

### 第一步到第六步，
接下来我们通过各种图片分析一下springbean的生命周期，读者只需要看图搞明白流程，至于图中涉及的源码，分析完流程之后再来解释；
![](https://user-gold-cdn.xitu.io/2020/2/23/1707143cfb8c3e53?w=1842&h=895&f=gif&s=4742700)
上面的gif对应了前面的6步，大家好好看看


在研究其他步骤之前，首先了解spring大概在什么时候实例化bean的

![](https://user-gold-cdn.xitu.io/2020/2/23/1707146c967dd7d5?w=1808&h=845&f=gif&s=2701404)

上图可以知道spring在AbstractApplicationContext#finishBeanFactoryInitialization方法中完成了bean的实例化。这点需要记住

### 第7步
然后通过图片来说明一下，第七步



![](https://user-gold-cdn.xitu.io/2020/2/23/170714a42c295f3a?w=1212&h=591&f=png&s=481617)

接下来spring需要推断构造方法，然后通过推断出来的构造方法反射实例化对象，也就是上面说的第8步和第9步
当然他有可能推断不出来构造方法——关于这块知识我后面更新文章来说明

### 第八步 第九步

![](https://user-gold-cdn.xitu.io/2020/2/23/170714bec4bea4f7?w=1214&h=593&f=png&s=376901)

上图说明spring是通过createBeanInstance(beanName, mbd, args);完成了推断构造方法和实例化的事情那么接下来便要执行第10步处理合并后的beanDefinition对象，这一块内容特别多，读者可以先不必要理解，后面文章会解释；

### 第十步

![](https://user-gold-cdn.xitu.io/2020/2/23/170714ceb51924c5?w=1842&h=895&f=gif&s=859363)

仔细看上图，其实这个时候虽然Z被实例化出来了，但是并没有完成属性的注入；其中的X属性为null，而且里面的Aware接口的方法也没有调用，再就是@PostConstruct方法也没有调用，再一次说明他不是一个完整的bean，这里我们只能说z是个对象；
继而applyMergedBeanDefinitionPostProcessors方法就是用来处理合并后的beanDefinition对象；
### 第十一步
跟着第11步，判断是否支持循环依赖，如果支持则提前暴露一个工厂对象，注意是工厂对象

![](https://user-gold-cdn.xitu.io/2020/2/23/170714e8b2c64bd5?w=1842&h=895&f=gif&s=1258966)
### 第十二步
第12步，spring会判断是否需要完成属性注入（spring默认是需要的，但是程序员可以扩展spring，根据情况是否需要完成属性注入）；如果需要则spring完成13步——属性注入，也就是所谓的自动注入

![](https://user-gold-cdn.xitu.io/2020/2/23/170715142643486d?w=1842&h=895&f=gif&s=3293227)

### 第十四 十五 十六步
第14、15、16步

![](https://user-gold-cdn.xitu.io/2020/2/23/17071522bb700e6c?w=1842&h=895&f=gif&s=1379202)


## 下面笔者介绍一下spring创建一个bean的流程，通过源码结合在idea当中debug截图来说明

>1、main方法，初始化spring容器，在初始化容器之后默认的单例bean已经实例化完成了，也就是说spring的默认单例bean创建流程、和所谓自动注入的功能都在容器初始化的过程中。故而我们需要研究这个容器初始化过程、在哪里体现了自动注入；
进入AnnotationConfigApplicationContext类的构造方法

![](https://user-gold-cdn.xitu.io/2020/2/23/1707155990a4e820?w=1706&h=935&f=png&s=736900)
>2、在构造方法中调用了refresh方法，查看refresh方法


![](https://user-gold-cdn.xitu.io/2020/2/23/170715b9b9ce5d13?w=1210&h=671&f=png&s=507738)

>3、refresh方法当中调用finishBeanFactoryInitialization方法来实例化所有扫描出来的类

![](https://user-gold-cdn.xitu.io/2020/2/23/170715bf88befaa1?w=1174&h=532&f=png&s=364868)
> 4、finishBeanFactoryInitialization方法当中调用preInstantiateSingletons初始化扫描出来的类

![](https://user-gold-cdn.xitu.io/2020/2/23/170715c6ae24ff1d?w=1163&h=709&f=png&s=616534)
>5、preInstantiateSingletons方法经过一系列判断之后会调用getBean方法去实例化扫描出来的类


![](https://user-gold-cdn.xitu.io/2020/2/23/170715d4879cde40?w=1160&h=611&f=png&s=457560)
>6、getBean方法就是个空壳方法，调用了doGetBean方法


![](https://user-gold-cdn.xitu.io/2020/2/23/170715eb0506b0e5?w=1713&h=744&f=png&s=705017)

> doGetBean方法内容有点多，这个方法非常重要，不仅仅针对循环依赖，
甚至整个spring bean生命周期中这个方法也有着举足轻重的地位，读者可以认真看看笔者的分析。需要说明的是我为了更好的说清楚这个方法，我把代码放到文章里面进行分析；但是删除了一些无用的代码；比如日志的记录这些无关紧要的代码。下面重点说这个doGetBean方法

```
protected <T> T doGetBean(final String name, @Nullable final Class<T> requiredType,
      @Nullable final Object[] args, boolean typeCheckOnly) throws BeansException {
    
    //这个方法非常重要，但是和笔者今天要分析的循环依赖没什么很大的关系
    //读者可以简单的认为就是对beanName做一个校验特殊字符串的功能
    //我会在下次更新博客的时候重点讨论这个方法
    //transformedBeanName(name)这里的name就是bean的名字
   final String beanName = transformedBeanName(name);
   //定义了一个对象，用来存将来返回出来的bean
   Object bean;
   // Eagerly check singleton cache for manu  ally registered singletons.
   /**
    * 注意这是第一次调用getSingleton方法，下面spring还会调用一次
    * 但是两次调用的不是同一个方法；属于方法重载
    * 第一次 getSingleton(beanName) 也是循环依赖最重要的方法
    * 关于getSingleton(beanName)具体代码分析可以参考笔者后面的分析
    * 这里给出这个方法的总结
    * 首先spring会去单例池去根据名字获取这个bean，单例池就是一个map
    * 如果对象被创建了则直接从map中拿出来并且返回
    * 但是问题来了，为什么spring在创建一个bean的时候会去获取一次呢？
    * 因为作为代码的书写者肯定知道这个bean这个时候没有创建？为什么需要get一次呢？
    * 关于这个问题其实原因比较复杂，需要读者对spring源码设计比较精通
    * 笔者不准备来针对这个原因大书特书，稍微解释一下吧
    * 我们可以分析doGetBean这个方法，顾名思义其实是用来获取bean的
    * 为什么创建bean会调用这个doGetBean方法呢？难道是因为spring作者疏忽，获取乱起名字
    * 显然不是，spring的源码以其书写优秀、命名优秀而著名，那么怎么解释这个方法呢？
    * 其实很简单doGetBean这个方法不仅仅在创建bean的时候会被调用，在getBean的时候也会调用
    * 他是创建bean和getBean通用的方法。但是这只是解释了这个方法的名字意义
    * 并么有解释这个方法为什么会在创建bean的时候调用
    * 笔者前面已经说过原因很复杂，和本文有关的就是因为循环引用
    * 由于循环引用需要在创建bean的过程中去获取被引用的那个类
    * 而被引用的这个类如果没有创建，则会调用createBean来创建这个bean
    * 在创建这个被引用的bean的过程中会判断这个bean的对象有没有实例化
    * bean的对象？什么意思呢？
    * 为了方便阅读，请读者一定记住两个概念；什么是bean，什么是对象
    * 笔者以为一个对象和bean是有区别的
    * 对象：只要类被实例化就可以称为对象
    * bean：首先得是一个对象，然后这个对象需要经历一系列的bean生命周期
    * 最后把这个对象put到单例池才能算一个bean
    * 读者千万注意，笔者下文中如果写到bean和写到对象不是随意写的
    * 是和这里的解释有关系的；重点一定要注意；一定；一定；
    * 简而言之就是spring先new一个对象，继而对这个对象进行生命周期回调
    * 接着对这个对象进行属性填充，也是大家说的自动注入
    * 然后在进行AOP判断等等；这一些操作简称----spring生命周期
    * 所以一个bean是一个经历了spring周期的对象，和一个对象有区别
    * 再回到前面说的循环引用，首先spring扫描到一个需要被实例化的类A
    * 于是spring就去创建A；A=new A-a;new A的过程会调用getBean("a"))；
    * 所谓的getBean方法--核心也就是笔者现在写注释的这个getSingleton(beanName)
    * 这个时候get出来肯定为空？为什么是空呢？我写这么多注释就是为了解释这个问题?
    * 可能有的读者会认为getBean就是去容器中获取，所以肯定为空，其实不然，接着往下看
    * 如果getA等于空；spring就会实例化A；也就是上面的new A
    * 但是在实例化A的时候会再次调用一下 
    * getSingleton(String beanName, ObjectFactory<?> singletonFactory)
    * 笔者上面说过现在写的注释给getSingleton(beanName)
    * 也即是第一次调用getSingleton(beanName)
    * 实例化一共会调用两次getSingleton方法；但是是重载
    * 第二次调用getSingleton方法的时候spring会在一个set集合当中记录一下这个类正在被创建
    * 这个一定要记住，在调用完成第一次getSingleton完成之后
    * spring判读这个类没有创建，然后调用第二次getSingleton
    * 在第二次getSingleton里面记录了一下自己已经开始实例化这个类
    * 这是循环依赖做的最牛逼的地方，两次getSingleton的调用
    * 也是回答面试时候关于循环依赖必须要回答道的地方；
    * 需要说明的spring实例化一个对象底层用的是反射；
    * spring实例化一个对象的过程非常复杂，需要推断构造方法等等；
    * 这里笔者先不讨论这个过程，以后有机会更新一下
    * 读者可以理解spring直接通过new关键字来实例化一个对象
    * 但是这个时候对象a仅仅是一个对象，还不是一个完整的bean
    * 接着让这个对象去完成spring的bean的生命周期
    * 过程中spring会判断容器是否允许循环引用，判断循环引用的代码笔者下面会分析
    * 前面说过spring默认是支持循环引用的，笔者后面解析这个判断的源码也是spring默认支持循环引用的证据
    * 如果允许循环依赖，spring会把这个对象(还不是bean)临时存起来，放到一个map当中
    * 注意这个map和单例池是两个map，在spring源码中单例池的map叫做 singletonObjects
    * 而这个存放临时对象的map叫做singletonFactories
    * 当然spring还有一个存放临时对象的map叫做earlySingletonObjects
    * 所以一共是三个map，有些博客或者书籍人也叫做三级缓存
    * 为什么需要三个map呢？先来了解这三个map到底都缓存了什么
    * 第一个map singletonObjects 存放的单例的bean
    * 第二个map singletonFactories 存放的临时对象(没有完整springBean生命周期的对象)
    * 第三个map earlySingletonObjects 存放的临时对象(没有完整springBean生命周期的对象)
    * 笔者为了让大家不懵逼这里吧第二个和第三个map功能写成了一模一样
    * 其实第二个和第三个map会有不一样的地方，但这里不方便展开讲，下文会分析
    * 读者姑且认为这两个map是一样的
    * 第一个map主要为了直接缓存创建好的bean；方便程序员去getBean；很好理解
    * 第二个和第三个主要为了循环引用；为什么为了方便循环引用，接着往下看
    * 把对象a缓存到第二个map之后，会接着完善生命周期；
    * 当然spring bean的生命周期很有很多步骤；本文先不详细讨论；
    * 后面的博客笔者再更新；
    * 当进行到对象a的属性填充的这一周期的时候，发觉a依赖了一个B类
    * 所以spring就会去判断这个B类到底有没有bean在容器当中
    * 这里的判断就是从第一个map即单例池当中去拿一个bean
    * 后面我会通过源码来证明是从第一个map中拿一个bean的
    * 假设没有，那么spring会先去调用createBean创建这个bean
    * 于是又回到和创建A一样的流程，在创建B的时候同样也会去getBean("B")；
    * getBean核心也就是笔者现在写注释的这个getSingleton(beanName)方法
    * 下面我重申一下：因为是重点
    * 这个时候get出来肯定为空？为什么是空呢？我写这么多注释就是为了解释这个问题?
    * 可能有的读者会认为getBean就是去容器中获取；
    * 所以肯定为空，其实不然，接着往下看；
    * 第一次调用完getSingleton完成之后会调用第二次getSingleton
   * 第二次调用getSingleton同样会在set集合当中去记录B正在被创建
   * 请笔者记住这个时候 set集合至少有两个记录了 A和B；
   * 如果为空就 b=new B()；创建一个b对象；
   * 再次说明一下关于实例化一个对象，spring做的很复杂，下次讨论
   * 创建完B的对象之后，接着完善B的生命周期
   * 同样也会判断是否允许循环依赖，如果允许则把对象b存到第二个map当中；
   * 提醒一下笔者这个时候第二个map当中至少有两个对象了，a和b
   * 接着继续生命周期；当进行到b对象的属性填充的时候发觉b需要依赖A
   * 于是就去容器看看A有没有创建，说白了就是从第一个map当中去找a
   * 有人会说不上A在前面创建了a嘛？注意那只是个对象，不是bean;
   * 还不在第一个map当中 对所以b判定A没有创建，于是就是去创建A；
   * 那么又再次回到了原点了，创建A的过程中；首先调用getBean("a")
   * 上文说到getBean("a")的核心就是 getSingleton(beanName)
   * 上文也说了get出来a==null；但是这次却不等于空了
   * 这次能拿出一个a对象；注意是对象不是bean
   * 为什么两次不同？原因在于getSingleton(beanName)的源码
   * getSingleton(beanName)首先从第一个map当中获取bean
   * 这里就是获取a；但是获取不到；然后判断a是不是等于空
   * 如果等于空则在判断a是不是正在创建？什么叫做正在创建？
   * 就是判断a那个set集合当中有没有记录A；
   * 如果这个集合当中包含了A则直接把a对象从map当中get出来并且返回
   * 所以这一次就不等于空了，于是B就可以自动注入这个a对象了
   * 这个时候a还只是对象，a这个对象里面依赖的B还没有注入
   * 当b对象注入完成a之后，把B的周期走完，存到容器当中
   * 存完之后继续返回，返回到a注入b哪里？
   * 因为b的创建时因为a需要注入b；于是去get b
   * 当b创建完成一个bean之后，返回b(b已经是一个bean了)
   * 需要说明的b是一个bean意味着b已经注入完成了a；这点上面已经说明了
   * 由于返回了一个b，故而a也能注入b了；
   * 接着a对象继续完成生命周期，当走完之后a也在容器中了
   * 至此循环依赖搞定
   * 需要说明一下上文提到的正在创建这种说法并没有官方支持
   * 是笔者自己的认为；各位读者可以自行给他取名字
   * 笔者是因为存放那些记录的set集合的名字叫做singletonsCurrentlyInCreation
   * 顾名思义，当前正在创建的单例对象。。。。。
   * 还有上文提到的对象和bean的概念；也没有官方支持
   * 也是笔者为了让读者更好的理解spring源码而提出的个人概念
   * 但是如果你觉得这种方法确实能让你更好的理解spring源码
   * 那么请姑且相信笔者对spring源码的理解，假设10个人相信就会有100个人相信
   * 继而会有更多人相信，就会成为官方说法，哈哈。
   * 以上是循环依赖的整个过程，其中getSingleton(beanName)
   * 这个方法的存在至关重要
   * 最后说明一下getSingleton(beanName)的源码分析，下文会分析
   **/
    Object sharedInstance = getSingleton(beanName);
    
    
  /**
   * 如果sharedInstance不等于空直接返回
   * 当然这里没有直接返回而是调用了getObjectForBeanInstance
   * 关于这方法以后解释，读者可以认为这里可以理解为
   * bean =sharedInstance; 然后方法最下面会返回bean
   * 什么时候不等于空？
   * 再容器初始化完成之后
   * 程序员直接调用getbean的时候不等于空
   * 什么时候等于空？
   * 上文已经解释过了，创建对象的时候调用就会等于空
   */  
   if (sharedInstance != null && args == null) {
      bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
   }

   else {
      /**
        * 判断这个类是不是在创建过程中
        * 上文说了，一个类是否在创建的过程中是第二次调用getSingleton中决定的
        * 这里还没有执行到，如果就在创建过程中则出异常
        * 
        **/
      //prototypesCurrentlyInCreation 需要联系 getSingleton方法
      if (isPrototypeCurrentlyInCreation(beanName)) {
         throw new BeanCurrentlyInCreationException(beanName);
      }else{
          /**
           * 需要说明的笔者删了很多和本文无用的代码
           * 意思就是源码中执行到这个if的时候有很多其他代码
           * 但是都是一些判断，很本文需要讨论的问题关联不大
           * 这个if就是判断当前需要实例化的类是不是单例的
           * spring默认都是单例的，故而一般都成立的
           * 接下来便是调用第二次 getSingleton
           * 第二次会把当前正在创建的类记录到set集合
           * 然后反射创建这个实例，并且走完生命周期
           * 第二次调用getSingleton的源码分析会在下文
           **/
         if (mbd.isSingleton()) {
            sharedInstance = getSingleton(beanName, () -> {
               try {
                  //完成了目标对象的创建
                  //如果需要代理，还完成了代理
                  return createBean(beanName, mbd, args);
               }
               catch (BeansException ex) {
                  // Explicitly remove instance from singleton cache: It might have been put there
                  // eagerly by the creation process, to allow for circular reference resolution.
                  // Also remove any beans that received a temporary reference to the bean.
                  destroySingleton(beanName);
                  throw ex;
               }
            });
            bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
         }
   return (T) bean;
}

```
### 第一次调用getSingleton的源码分析 Object sharedInstance =getSingleton(beanName);

```
//空壳方法
public Object getSingleton(String beanName) {
    //重点，一定要记住这里传的是一个true，面试会考
   return getSingleton(beanName, true);
}



/**
上面说的true对应这里的第二个参数boolean allowEarlyReference
顾名思义 叫做允许循环引用，而spring在内部调用这个方法的时候传的true
这也能说明spring默认是支持循环引用的，这也是需要讲过面试官的
但是你不能只讲这一点，后面我会总结，这里先记着这个true
这个allowEarlyReference也是支持spring默认支持循环引用的其中一个原因
**/
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    /**
     首先spring会去第一个map当中去获取一个bean；说白了就是从容器中获取
     说明我们如果在容器初始化后调用getBean其实就从map中去获取一个bean
     假设是初始化A的时候那么这个时候肯定等于空，前文分析过这个map的意义
     **/
    Object singletonObject = this.singletonObjects.get(beanName);
       /**
       我们这里的场景是初始化对象A第一次调用这个方法
       这段代码非常重要，首先从容器中拿，如果拿不到，再判断这个对象是不是在set集合
       这里的set集合前文已经解释过了，就是判断a是不是正在创建
       假设现在a不在创建过程，那么直接返回一个空，第一次getSingleton返回
       **/
   if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
      synchronized (this.singletonObjects) {
         singletonObject = this.earlySingletonObjects.get(beanName);
         if (singletonObject == null && allowEarlyReference) {
            ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
            if (singletonFactory != null) {
               singletonObject = singletonFactory.getObject();
               this.earlySingletonObjects.put(beanName, singletonObject);
               this.singletonFactories.remove(beanName);
            }
         }
      }
   }
   return singletonObject;
}

```
### 第二次调用getSingleton sharedInstance = getSingleton(beanName, () ->代码我做了删减，删了一些本本文无关的代码

```
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
   synchronized (this.singletonObjects) {
    
       //首先也是从第一个map即容器中获取
       //再次证明如果我们在容器初始化后调用getBean其实就是从map当中获取一个bean
       //我们这里的场景是初始化对象A第一次调用这个方法
       //那么肯定为空
      Object singletonObject = this.singletonObjects.get(beanName);
      if (singletonObject == null) {
          /**注意这行代码，就是A的名字添加到set集合当中
          也就是笔者说的标识A正在创建过程当中
          这个方法比较简单我就不单独分析了，直接在这里给出
          singletonsCurrentlyInCreation.add就是放到set集合当中
          protected void beforeSingletonCreation(String beanName) {
               if (!this.inCreationCheckExclusions.contains(beanName)
                && !this.singletonsCurrentlyInCreation.add(beanName)) {
                  throw new BeanCurrentlyInCreationException(beanName);
           }
        }
        **/
        
         beforeSingletonCreation(beanName);
         boolean newSingleton = false;
         try {
             //这里便是创建一个bean的入口了
             //spring会首先实例化一个对象，然后走生命周期
             //走生命周期的时候前面说过会判断是否允许循环依赖
             //如果允许则会把创建出来的这个对象放到第二个map当中
             //然后接着走生命周期当他走到属性填充的时候
             //会去get一下B，因为需要填充B，也就是大家认为的自动注入
             //这些代码下文分析，如果走完了生命周期
            singletonObject = singletonFactory.getObject();
            newSingleton = true;
         }
      }
      return singletonObject;
   }
}

```

>如果允许则会把创建出来的这个对象放到第二个map当中
AbstractAutowireCapableBeanFactory#doCreateBean()方法部分代码
由于这个方法内容过去多，我删减了一些无用代码 上面说的 singletonObject =
singletonFactory.getObject();
会开始创建bean调用AbstractAutowireCapableBeanFactory#doCreateBean() 在创建bean；
下面分析这个方法

```
protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)
      throws BeanCreationException {

   // Instantiate the bean.
   BeanWrapper instanceWrapper = null;
   if (mbd.isSingleton()) {
       //如果你bean指定需要通过factoryMethod来创建则会在这里被创建
       //如果读者不知道上面factoryMethod那你就忽略这行代码
       //你可以认为你的A是一个普通类，不会再这里创建
      instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
   }
   if (instanceWrapper == null) {
       //这里就通过反射创建一个对象，注意是对象不是bean
       //这个createBeanInstance的方法过于复杂，本文不做分析
       //以后如果有更新再来分析这个代码
       //读者可以理解这里就是new了一个A对象
      instanceWrapper = createBeanInstance(beanName, mbd, args);
   }
   //得到new出来的A，为什么需要得到呢？因为Anew出来之后存到一个对象的属性当中
   final Object bean = instanceWrapper.getWrappedInstance();
   //重点:面试会考
   //这里就是判断是不是支持循环引用和是否单例以及bean是否在创建过程中
   //判断循环引用的是&& this.allowCircularReferences
   //allowCircularReferences在spring源码当中默认就是true
   // private boolean allowCircularReferences = true; 这是spring源码中的定义
   //并且这个属性上面spring写了一行非常重要的注释
   // Whether to automatically try to resolve circular references between beans
   // 读者自行翻译，这是支持spring默认循环引用最核心的证据
   //读者一定要讲给面试官，关于怎么讲，我后面会总结
   boolean earlySingletonExposure = (mbd.isSingleton() 
       && this.allowCircularReferences &&
         isSingletonCurrentlyInCreation(beanName));
   //如果是单例，并且正在创建，并且是没有关闭循环引用则执行
   //所以spring原形是不支持循环引用的这是证据，但是其实可以解决
   //怎么解决原形的循环依赖，笔者下次更新吧      
   if (earlySingletonExposure) {
       //这里就是这个创建出来的A 对象a 放到第二个map当中
       //注意这里addSingletonFactory就是往map当中put
       //需要说明的是他的value并不是一个a对象
       //而是一段表达式，但是包含了这个对象的
       //所以上文说的第二个map和第三个map的有点不同
       //第三个map是直接放的a对象(下文会讲到第三个map的)，
       //第二个放的是一个表达式包含了a对象
       //为什么需要放一个表达式？下文分析吧
      addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
   }

   // Initialize the bean instance.
   Object exposedObject = bean;
   try {
       //填充属性，也就是所谓的自动注入
       //这个代码我同一张图来说明
      populateBean(beanName, mbd, instanceWrapper);
      exposedObject = initializeBean(beanName, exposedObject, mbd);
   }
   return exposedObject;
   }
```
###  populateBean(beanName, mbd, instanceWrapper)截图图说明
当A执行到属性填充的时候会调用AutowiredAnnotationBeanPostProcessor的postProcessProperties方法来完成填充或者叫做自动注入b
下图有很多文字注释，可以放大图上的注释


![](https://user-gold-cdn.xitu.io/2020/2/23/1707166a6ec092ea?w=1899&h=974&f=png&s=1399670)
> 填充B的时候先从容器中获取B，这个时候b没有创建则等于空，然后看B是不是正在创建，这个时候B只是执行了第一次getSingleton故而不在第二个map当中，所以返回空，返回空之后会执行创建B的流程；执行第二遍调用getSingleton的时候会把b标识正在创建的过程中，也就是添加到那个set集合当中；下图做说明


![](https://user-gold-cdn.xitu.io/2020/2/23/170716864086b5e1?w=1874&h=940&f=png&s=1026829)

创建B的流程和创建A差不多，把B放到set集合，标识B正在创建，继而实例化b对象，然后执行生命周期流程，把创建的这个b对象放到第二个map当中，这个时候map当中已经有了a，b两个对象。
然后执行b对象的属性填充或者叫自动注入时候发觉需要依赖a，于是重复上面的getbean步骤，调用getSingleton方法；只不过现在a对象已经可以获取了故而把获取出来的a对象、临时对象注入给b对象，然后走完b的生命周期流程后返回b所表示bean，跟着把这个b所表示的bean注入给a对象，最后走完a对象的其他生命周期流程；循环依赖流程全部走完；但是好像没有说到第三个map，第三个map到底充当了什么角色呢？
这个知识点非常的重要，关于这个知识不少书籍和博客都说错了，这也是写这篇文章的意义；笔者说每次读spring源码都不一样的收获，这次最大的收获便是这里了；我们先来看一下代码；
场景是这样的，spring创建A，记住第一次创建A，过程中发觉需要依赖B，于是创建B，创建B的时候发觉需要依赖A，于是再一次创建–第二次创建A，下面代码就是基于第二次创建的A来分析；第二次创建A的时候依然会调用getSingleton，先获取一下a


```
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    //先从第一个map获取a这个bean，也就是单例池获取
   Object singletonObject = this.singletonObjects.get(beanName);
   if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
      synchronized (this.singletonObjects) {
          //然后从第三个map当中获取a这个对象
         singletonObject = this.earlySingletonObjects.get(beanName);
         //如果第三个map获取不到a对象，再看是否允许了循环引用
         //而这里的allowEarlyReference是true
         //为什么是true，上文说了这个方法是spring自己调用的，他默认传了true
         if (singletonObject == null && allowEarlyReference) {
             //然后从第二个map中获取一个表达式
             //这里要非常注意第二个map当中存的不是一个单纯的对象
             //前面说了第二个map当中存的是一个表达式，你可以理解为存了一个工厂
             //或者理解存了一个方法，方法里面有个参数就是这个对象
             //安装spring的命名来分析应该理解为一个工厂singletonFactory
             //一个能够生成a对象的工厂
             //那么他为什么需要这么一个工厂
             //这里我先大概说一下，是为了通过工厂来改变这个对象
             //至于为什么要改变对象，下文我会分析
             //当然大部分情况下是不需要改变这个对象的
             //读者先可以考虑不需要改变这个对象，
             //那么这个map里面存的工厂就生产就是这个原对象，那么和第三个map功能一样
            ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
            if (singletonFactory != null) {
                //调用表达式，说白了就是调用工厂的方法，然后改变对象
                //我们假设对象不需要改变的情况那么返回了原对象就是a
                //需要改变的情况我们下文再分享
               singletonObject = singletonFactory.getObject();
               //然后把这个对象放到第三个map当中
               this.earlySingletonObjects.put(beanName, singletonObject);
               //把这个对象、或者表达式、或者工厂从第二个map中移除
               this.singletonFactories.remove(beanName);
               //重点:面试会考---为什么要放到第三个？为什么要移除第二个？
               首先我们通过分析做一个总结:
                   spring首先从第一个map中拿a这个bean
                   拿不到，从第三个map当中拿a这个对象
                   拿不到，从第二个map拿a这个对象或者工厂
                   拿到之后放到第三个map，移除第二个map里面的表达式、或者工厂
               如果对象需要改变，当改变完成之后就把他放到第三个里面
               这里的情况是b需要a而进行的步骤，试想一下以后如果还有C需要依赖a
               就不需要重复第二个map的工作了，也就是改变对象的工作了。
               因为改变完成之后的a对象已经在第三个map中了。不知道读者能不能懂笔者的意思
               如果对象不需要改变道理是一样的，也同样在第三个map取就是了；
               至于为什么需要移除第二个map里面的工厂、或者表达式就更好理解了
               他已经对a做完了改变，改变之后的对象已经在第三个map了，为了方便gc啊
               下面对为什么需要改变对象做分析
               
            }
         }
      }
   }
   return singletonObject;
}

```

>为什么需要改变对象？那个表达式、或者说工厂主要干什么事呢？ 那个工厂、或者表达式主要是调用了下面这个方法


```
//这个方法内容比较少，但是很复杂，因为是对后置处理器的调用
//关于后置处理器笔者其实要说话很多很多
//现在市面上可见的资料或者书籍对后置处理器的说法笔者一般都不苟同
//我在B站上传过一个4个小时的视频，其中对spring后置处理器做了详细的分析
//也提出了一些自己的理解和主流理解不同的地方，有兴趣同学可以去看看
//其实简单说--这个方法作用主要是为了来处理aop的；
//当然还有其他功能，但是一般的读者最熟悉的就是aop
//这里我说明一下，aop的原理或者流程有很多书籍说到过
//但是笔者今天亲测了，现在市面可见的资料和书籍对aop的说法都不全
//很多资料提到aop是在spring bean的生命周期里面填充属性之后的代理周期完成的
//而这个代理周期甚至是在执行生命周期回调方法之后的一个周期
//那么问题来了？什么叫spring生命周期回调方法周期呢？
// 首先spring bean生命周期和spring生命周期回调方法周期是两个概念
//spring生命周期回调方法是spring bean生命周期的一部分、或者说一个周期
//简单理解就是spring bean的生命的某个过程会去执行spring的生命周期的回调方法
//比如你在某个bean的方法上面写一个加@PostConstruct的方法（一般称bean初始化方法）
//那么这个方法会在spring实例化一个对象之后，填充属性之后执行这个加注解的方法
//我这里叫做spring 生命周期回调方法的生命周期，不是我胡说，有官方文档可以参考的
//在执行完spring生命周期回调方法的生命周期之后才会执行代理生命周期
//在代理这个生命周期当中如果有需要会完成aop的功能
//以上是现在主流的说法，也是一般书籍或者“某些大师”的说法
//但是在循环引用的时候就不一样了，循环引用的情况下这个周期这里就完成了aop的代理
//这个周期严格意义上是在填充属性之前（填充属性也是一个生命周期阶段）
//填充属性的周期甚至在生命周期回调方法之前，更在代理这个周期之前了
//简单来说主流说法代理的生命周期比如在第8个周期或者第八步吧
//但是笔者这里得出的结论，如果一个bean是循环引用的则代理的周期可能在第3步就完成了
//那么为什么需要在第三步就完成呢？
//试想一下A、B两个类，现在对A类做aop处理，也就是需要对A代理
不考虑循环引用 spring 先实例化A，然后走生命周期确实在第8个周期完成的代理
关于这个结论可以去看b站我讲的spring aop源码分析
但是如果是循环依赖就会有问题
比如spring 实例化A 然后发现需要注入B这个时候A还没有走到8步
还没有完成代理，发觉需要注入B，便去创建B，创建B的时候
发觉需要注入A，于是创建A，创建的过程中通过getSingleton
得到了a对象，注意是对象，一个没有完成代理的对象
然后把这个a注入给B？这样做合适吗？注入的a根本没有aop功能；显然不合适
因为b中注入的a需要是一个代理对象
而这个时候a存在第二个map中；不是一个代理对象；
于是我在第二个map中就不能单纯的存一个对象，需要存一个工厂
这个工厂在特殊的时候需要对a对象做改变，比如这里说的代理（需要aop功能的情况）
这也是三个map存在的必要性，不知道读者能不能get到点



protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
   Object exposedObject = bean;
   if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
      for (BeanPostProcessor bp : getBeanPostProcessors()) {
         if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
            SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
            exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
         }
      }
   }
   return exposedObject;
}

```

## 结论
总结关于循环引用，如何回答面试:
首先spring在单例的情况下是默认支持循环引用的(当然原形也有办法，今天先不讨论)；在不做任何配置的情况下，两个bean相互依赖是能初始化成功的；spring源码中在创建bean的时候先创建这个bean的对象，创建对象完成之后通过判断容器对象的allowCircularReferences属性决定是否允许缓存这个临时对象，如果能被缓存成功则通过缓存提前暴露这个临时对象来完成循环依赖；而这个属性默认为true，所以说spring默认支持循环依赖的，但是这个属性spring提供了api让程序员来修改，所以spring也提供了关闭循环引用的功能；再就是spring完成这个临时对象的生命周期的过程中当执行到注入属性或者自动装配的周期时候会通过getSingleton方法去得到需要注入的b对象；而b对象这个时候肯定不存在故而会创建b对象创建b对象成功后继续b对象的生命周期，当执行到b对象的自动注入周期时候会要求注入a对象；调用getSingleton；从map缓存中得到a的临时对象（因为这个时候a在set集合中；这里可以展开讲），而且获取的时候也会判断是否允许循环引用，但是判断的这个值是通过参数传进来的，也就是spring内部调用的，spring源码当中写死了为true，故而如果需要扩展spring、或者对spring二次开发的的时候程序员可以自定义这个值来实现自己的功能；不管放到缓存还是从缓存中取出这个临时都需要判断；而这两次判断spring源码当中都是默认为true；这里也能再次说明spring默认是支持循环引用的；

然后面试中可以在说说两次调用getSingleton的意义，正在创建的那个set集合有什么用；最后在说说你在看spring循环引用的时候得出的aop实例化过程的新发现；就比较完美了

点击这里有笔者讲的本文视频
https://www.bilibili.com/video/av71093907/?redirectFrom=h5


## 结尾

我copy这篇文章的原因，我是真觉得讲得好，对Spring源码的理解肯定是到了一定的深度，对于我这种初涉源码的小白都能理解，所以想着分享给大家。然后我可是跟着debug了一遍哈哈。建议所有的读者不要看了就算了，可以看一遍，自己再debug一遍，以后有空再画个流程图，基本上就转换成自己的东西了，一开始都是copy嘛。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
