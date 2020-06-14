# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨  
这篇文章 我是参考子路老师的，它讲的Spring真心不错，我写文章就是写把它的知识吸收变成自己的，因为自己可能还是很菜，只能吸取别人的经验了。

## 前言
比如提到spring的自动注入作为一个java程序员肯定自信无比了解；但是笔者要说的自动注入可能会和你理解有很大出入。
首先搞明白什么是自动注入，自动注入也可以叫做自动装配（springboot也有一个自动装配但是我认为翻译的不够准确，springboot的应该叫做自动配置和这里说的自动注入是两回事，笔者不是什么大牛或者权威；所以读者如果你坚持认为springboot也叫自动装配那也无可厚非，只是在这篇文章里面所谓的自动注入就是自动装配，关于springboot的自动配置我以后更新文章再来说）；
自动注入需要相对于手动装配来说；在spring应用程序当中假设你的A类依赖了B类，需要在A类当中提供一个B类的属性，再加上setter，继而在xml当中配置、描述一下这两个类之间的依赖关系。如果做完当容器初始化过程中会实例化A，在实例化A的过程中会填充属性，由于在xml中已经配置、描述好两者的关系，故而spring会把B给A装配上；这种由程序员自己配置、描述好依赖关系的写法叫做手动装配；看个例子吧；


```
package com.luban.app;

public class A {
	B b;
	public void setB(B b) {
		this.b = b;
	}
}



package com.luban.app;

public class B {

}

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd">

		<bean id="a" class="com.luban.app.A" >
			<!-- 由程序员手动指定的依赖关系 称为手动装配-->
			<property name="b">
				<ref bean="b" />
			</property>
		</bean>

		<bean id="b"  class="com.luban.app.B">
		</bean>
</beans>
```
上面的这种情况就是手动配置，通过xml 定义好2个类的属性关系。

但是实际开发中手动装配的场景比较少(比如在缺少源码的情况下可能会使用这种手动装配情况)；
关于依赖注入的资料可以参考官网[Spring](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#beans-dependencies)

这一章节提到了一个非常重要的知识点，也是一个常见的spring面试题目。**spring有几种依赖注入方式？那么这个问题应该怎么回答呢？**


![](https://user-gold-cdn.xitu.io/2020/2/23/17070bd0442fe244?w=1326&h=336&f=png&s=86110)

上面这个是我谷歌翻译的Spring的文档，英语比较菜没办法，那么我们来回答一下上面的面试题吧

官网的意思是DI(依赖注入)一共有两种主要的变体（注意会考），分别是基于构造方法的依赖注入和基于setter（setXxxx(…)）的依赖注入，不管是手动装配还是自动装配都是基于这两种方式或者变体方式来的；但是这里一定要回答到主要和变体两个名词，因为有的注入方式就不是这两种，而是这两种其中一种的变体方式；比如在一个类的属性上面加@Autowired，这种方式注入属性的方式就是利用了java的反射知识，field.set(value,targetObject);关于这个我在后面的文章中对spring源码解析的时候会说明@Autowired的原理；所以@Autowired这种注入的方式是setter注入方式的一种变体
但是这里需要说明的是所谓的setter其实和属性无关，什么意思呢？一般的setter方法会对应一个属性，但是spring的基于setter的注入方式是不需要属性的，仅仅只需要一个setter方法，下面这个例子来说明这个问题


```
B.java

public class B {

}


A.java

public class A {
	public void setXxx(B b) {
		System.out.println("spring 找到符合的setter");
		System.out.println("和属性无关，甚至可以不要属性");
		System.out.println("可以直接调用，这个A里面就没有任何属性");
	}
}

xml配置文件

<bean id="a" class="com.luban.app.A" >
	<!-- 由程序员手动指定的依赖关系 称为手动装配-->
	<property name="xxx">
		<ref bean="b" />
	</property>
</bean>

<bean id="b"  class="com.luban.app.B">
</bean>
```

如上面的结构 虽然A里面注入了B，但是A里面并没有B的属性

运行上面的代码可以看到spring也会调用这个setXxx方法，如果仔细观察调用栈可以看到这个方法是在spring容器初始化的时候实例化A，完成A的注入功能时候调用过来的，下图是笔者运行结果的截图

![](https://user-gold-cdn.xitu.io/2020/2/23/17070c2c757abdec?w=1198&h=777&f=png&s=492815)


可能有的读者会认为上面的xml配置文件中已经手动装配了B给A，肯定会调用setXxx方法，其实不然，即是我使用自动装配也还是会调用的（关于什么是自动装配，下文会详细介绍），因为前文说过，注入方式与手动注入、自动注入无关。笔者改一下代码，把A的注入模型改成自动注入来看看结果


```
<!-- 程序员不指定装配的具体参数，容器自动查询后装配-->
<bean id="a" class="com.luban.app.A" autowire="byType">
	
</bean>

<bean id="b"  class="com.luban.app.B">
</bean>

```
把代码改成上面这样自动装配结果是一样的，A类当中的setXxx方法还是会调用，不需要提供任何属性，至于原理笔者后面更新到spring源码的时候再来详细说道；可能有读者会说如果使用注解呢？比如如下代码

```
A.java

@Component
public class A {
	@Autowired
	B b;
	public void setB(B b) {
		System.out.println("如果你使用注解，这个setter变得毫无意义");
		System.out.println("这个方法甚至都不会调用");
		System.out.println("因为前文说过这种方法用的是field.set");
	}
}

B.java

import org.springframework.stereotype.Component;
@Component
public class B {

}

```

**上面代码同样会注入b，但是是调用field.set去完成注入的，不是通过setter，当然再次说明一下这是setter的一种变体；上面代码直到A完成注入B后setter方法也不会调用；**

重点来了，要考。笔者看过很多资料说@Autowired也算自动装配，关于这点笔者不敢苟同，在一个属性上面加@Autowired注解应该属于手动装配，我会通过大量源码和例子来证明笔者的这个理论。
之所以会有人把@Autowired也理解为自动装配的原因是因为bytype引起的，因为spring官网有说明自动装配有四种模型分表是no、bytype、byname、constructor；参考官网资料，而现在流行着这么一种说法：@Autowired就是通过bytype来完成注入的。如果你也认为这种说法成立，那么就是默认了@Autowired是自动装配且装配模型是bytype。笔者见过很多程序员和很多资料都支持这种说法，其实严格意义上来讲这句话大错特错，甚至有误人子弟的嫌疑。因为bytype仅仅是一种自动注入模型而已，这种模型有其固有的技术手段，而@Autowired是一个注解，这个注解会被spring的后置处理器解析，和处理bytype不是同一回事。如果需要讲清楚他们的区别和证明@Autowired不是自动装配则首先要搞明白什么自动装配。笔者接下来会花一定篇幅来解释自动装配的知识，然后回过头来讲他们的区别和证明@Autowired属性手动装配

如果现在已经理解了手动装配也叫手动注入，也已经理解了注入方式(setter和构造方法)，那么接下来讨论自动注入或者叫自动装配；自动注入的出现是因为手动装配过于麻烦，比如某个类X当中依赖了10个其他类那么配置文件将会变的特别冗余和臃肿，spring的做法是可以为这个X类提供一种叫做自动装配的模型，无需程序员去手动配置X类的依赖关系。有读者会疑问，用注解不也是可以解决这个xml臃肿的问题？确实用注解可以解决，但是我们现在讨论的是自动装配的问题，就不能用注解；为什么不能用注解来讨论自动装配的问题呢？因为在不配置BeanFactoryPostProcessor和修改beanDefinition的情况下注解的类是不支持自动装配的（关于BeanFactoryPostProcessor和beanDefinition的知识笔者以后有时间更新）；这也是证明@Autowired默认不是自动装配的一个证据，那又如何证明注解类是默认不支持自动装配呢？下文我会解释一个注解类默认是不支持自动装配的。也就是说如果讨论自动装配最好是用xml形式来配置spring容器才会有意义；当然读者如果对spring比较精通其实通过修改注解类的beanDefinition来说明自动装配的问题，鉴于大部分读者对spring不精通故而笔者还是用xml来讨论吧；比如下面这个例子



```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd"
		default-autowire="byType">
		<!--default-autowire="byType"-->
		<!-- 程序员不指定装配的具体参数，容器自动查询后装配-->
		<!--当然除了这种写法还可以直接在bean标签上为特定类指定自动装配模型-->
		<bean id="a" class="com.luban.app.A">

		</bean>

		<bean id="b"  class="com.luban.app.B">
		</bean>
</beans>


A.java



public class A {
	B b;
	public void setB(B b) {
		System.out.println("在不考虑注解的情况下");
		System.out.println("如果配置了自动装配则不需要手动配置");
	}
}

B.java

public class B {

}

```
上面代码运行起来，A能注入B，但是在xml配置文件中并没有去手动维护、描述他们之间的依赖关系，而是在xml的根标签上面写了一行default-autowire=“byType”，其实关于自动注入的歧义或者被人误解的地方就是这个default-autowire="byType"引起的；那么这行代码表示什么意思呢？表示所在配置在当前xml当中的bean都以bytype这种模式自动装配(如果没有特殊指定，因为bean还可以单独配置装配模式的)；这需要注意笔者的措辞，笔者说的bytype这自动装配模式，是一种模式，这个不是笔者信口开河，因为在官网文档里面spring也是这么定义的

![](https://user-gold-cdn.xitu.io/2020/2/23/17070cee6cf9364a?w=1446&h=515&f=png&s=110167)

Autowiring mode 笔者姑且翻译为自动注入模型吧，如果有211、雅思读者可以给读者留言更合适的翻译
那为什么要锱铢在这个名词上呢？笔者觉得真的非常重要，很多初学spring的程序员都是粗学spring，忽略甚至混淆了很多关键的名词定义，导致很多观念根深蒂固后面想深入学习spring源码的时候就比较困难
这个名字叫做自动注入模型和前面提到的依赖注入方式(setter和构造方法)是两回事，**简而言之：依赖注入是一个过程，主要通过setter和构造方法以及一些变体的方式完成把对象依赖、或者填充上的这个过程叫做依赖注入**，不管手动装配还是自动装配都有这个过程；而自动装配模型是一种完成自动装配依赖的手段体现，每一种模型都使用了不同的技术去查找和填充bean；而从spring官网上面可以看到spring只提出了4中自动装配模型（严格意义上是三种、因为第一种是no，表示不使用自动装配、使用），这四个模型分别用一个整形来表示，存在spring的beanDefinition当中，任何一个类默认是no这个装配模型，也就是一个被注解的类默认的装配模型是no也就是手动装配；其中no用0来表示；bytype用2来表示；如果某个类X，假设X的bean对应的beanDefinition当中的autowireMode=2则表示这个类X的自动装配模型为bytype；如果autowireMode=1则表示为byname装配模型；需要注意的是官网上面说的四种注入模型其中并没有我们熟悉的@Autowired，这也再一次说明@Autowired不是自动装配；可能有读者会提出假设我在A类的某个属性上面加上@Autowired之后这个A类就会不会成了自动装配呢？@Autowired是不是会改变这个类A当中的autowireMode呢？我们可以写一个例子来证明一下


```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd"
	   default-autowire="byType">

		<bean id="a" class="com.luban.app.A">
		</bean>
		<bean id="b"  class="com.luban.app.B">
		</bean>
</beans>

xml配置了A 和B 都是自动装配模型为bytype讲道理要实现autowireMode=2

A.java
public class A {
	
}

B.java
public class B {
	
}



提供一个后置处理器来获取A的自动装配模型
ZiluBeanFactoryPostprocessor.java
@Component
public class ZiluBeanFactoryPostprocessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		GenericBeanDefinition a = (GenericBeanDefinition)
				beanFactory.getBeanDefinition("a");
		//打印A 的注入模型
		System.out.println("a mode="+a.getAutowireMode());

	}
}

```
讲道理由于是bytype所以应该打印出来2； 结果如图


![](https://user-gold-cdn.xitu.io/2020/2/23/17070d2826bb7b51?w=1203&h=649&f=png&s=485722)

如果笔者把注入模型改成byname则结果应该会改变


![](https://user-gold-cdn.xitu.io/2020/2/23/17070d2df3cb6976?w=1203&h=692&f=png&s=517742)

接下来笔者验证一个通过注解配置的类加上@Autowried后的注入模型的值

![](https://user-gold-cdn.xitu.io/2020/2/23/17070d354be4c094?w=1191&h=666&f=png&s=454838)

**可以看到结果为0，说明这个A类不是自动装配**，其实这已经能证明@Autowried不是自动装配了，但是还有更直接的证据证明他不是自动装配，就是通过spring源码当中处理@Autowried的代码可以看出，首先我们写一个bytype的例子看看spring如何处理的


```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd"
	   default-autowire="byType">
		<!--自动装配-->
		<bean id="a" class="com.luban.app.A">
		</bean>
		<bean id="b"  class="com.luban.app.B">
		</bean>
</beans>



A.java

public class A {

	B b;

	/**
	 * 如果是自动装配需要提供setter
	 * 或者提供构造方法
	 */
	public void setB(B b) {
		this.b = b;
	}
}

B.java
public class B {

}

```
上面代码运行起来，调试spring源码当中的org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean方法，这个方法主要就是完成属性填充的，也就是大家说的注入



![](https://user-gold-cdn.xitu.io/2020/2/23/17070d53b6fd1d38?w=1206&h=588&f=png&s=642177)
可以看上图1399行有一个判断，判断当前类的注入模式是否bynane或者bytype如果是其中一种则会进入1401行代码执行自动装配的逻辑；因为当前代码我在xml当中配置了自动注入模型为bytype所以这里一定会进入，从上图debug的结果我们可以得知确实也进入了1401行


但是如果当我们使用@Autowired注解去注入一个属性的时候spring在完成属性注入的过程中和自动注入（byname、bytype）的过程不同，spring注解跳过了那个判断，因为不成立，而是用后面的代码去完成属性注入；这也是能说明@Autowired不是自动装配的证据、更是直接打脸@Autowired是先bytype的这种说法

## 结论
以后如果再听到@Autowried是bytype请你纠正他，bytype是一种自动注入模型；@Autowried是一个注解，两个人没有关系，一点关系都没有；@Autowried讲道理算是手动装配 
我觉得只能说@Autowried是自动注入，手动装配，因为它的装配类型为0

## 结尾

Spring的东西还是很多，大家慢慢学吧
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！



