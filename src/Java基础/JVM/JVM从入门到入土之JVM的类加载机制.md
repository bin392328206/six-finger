# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
学Java的没办法都逃不过Java虚拟机的，所以这个系列是必须讲的，因为你要构建你的Java知识体系，你就肯定知道要怎么按步骤学，对于JVM的学习我也自己的一个小规划吧，从JVM介绍,然后，Java文件编程成.class文件，然后Java虚拟机怎么加载这写.class文件，加载到虚拟机之后，这些数据怎么再Java虚拟机中存储，存储之后我们知道Java是自动回收垃圾，不像C C++那样，那我们我们肯定得知道垃圾回收算法，和垃圾回收器，最后到真正的一个Java系统的JVM调优，这个就是我打算要讲的这个系列，然后我大多数类容参考周志明老师的深入理解Java虚拟机

![](https://user-gold-cdn.xitu.io/2020/1/13/16f9c792b67fe02a?w=3024&h=4032&f=png&s=4730710)


## 什么是JVM
JVM是Java Virtual Machine（Java虚拟机）的缩写，JVM是一种用于计算设备的规范，它是一个虚构出来的计算机，是通过在实际的计算机上仿真模拟各种计算机功能来实现的。Java虚拟机包括一套字节码指令集、一组寄存器、一个栈、一个垃圾回收堆和一个存储方法域。 JVM屏蔽了与具体操作系统平台相关的信息，使Java程序只需生成在Java虚拟机上运行的目标代码（字节码）,就可以在多种平台上不加修改地运行。JVM在执行字节码时，实际上最终还是把字节码解释成具体平台上的机器指令执行 ,是Java跨平台的原因。


## JDK JRE JVM
![](https://user-gold-cdn.xitu.io/2020/1/12/16f98ca7cef4dbaf?w=1001&h=661&f=png&s=76893)

这个是我从oracle官网截图的，从中可以看出JDK=JRE+一些工具，JRE里面包含了JVM(Java虚拟机)

## JVM总体概述
JVM总体上是由
- 类装载子系统（ClassLoader）
- 运行时数据区
- 执行引擎
- 内存回收
- 类文件结构

以上5个部分组成，每一个都是非常重要的，如果你要了解JVM，要学习JVM调优，那么只能是一个个去把他们啃了

## 什么是类加载机制
书上的原话：
> 虚拟机把描述类的数据从Class文件加载到内存，并对这些数据进行校验，转换 解析和初始化，最终形成可以被虚拟机直接使用的Java类型，这就是虚拟机的类加载机制

## 类加载的时机
类从被加载到虚拟机内存中开始，到卸载出内存为止，它的生命周期包括
- 加载
- 验证
- 准备
- 解析
- 初始化
- 使用
- 卸载

总共是7个阶段

![](https://user-gold-cdn.xitu.io/2020/1/13/16f9c8339ebd6e41?w=1164&h=335&f=png&s=254050)

## 理解类加载三个字
首先 类 是指的.Class文件类，那么怎么生成这个文件呢？
- Java代码编译
- 原本就是.Class 文件
- 动态代理生成

等等 还有很多

那么 加载 这2个字应该怎么理解呢 大家可以看下图

![](https://user-gold-cdn.xitu.io/2020/1/13/16f9c89292a9db68?w=1359&h=604&f=png&s=130156)

本地的.Class文件通过类加载器加载到JVM内存中的方法区里面，然后通过这个对象来访问数据区的数据
1. 通过一个类的全限定名来获取定义此类的二进制字节流
2. 将这个字节流代表的静态存储结构转化成方法区的二进制字节流
3. 再内存的方法区生成这个类的Java.lang.Class对象，作为这个类各个数据访问的入口

## 五种必须初始化的情况
Java并没用规定生命时候进行类加载的第一阶段，但是对于初始化阶段，虚拟机有严格的规范
- 遇到new 关键字的时候 
- 使用reflect包的方法的时候
- 当初始化一个类的时候发现父类还没初始化，必须先初始化父类
- 当虚拟机启动的时候，加载main方法的类
- 当使用1.7的动态语言支持的时候（这块没有接触过，有没有大佬懂的）
## 验证阶段
分为以下几种样装情况
- 文件格式的验证，验证当前字节流是否能被JVM识别
- 元数据的验证，验证它的父类，它的继承，是否是抽象类等
- 字节码验证，验证逻辑是否合理
- 符合引用的验证 验证是否能通过生成的Class对象找到对应的数据
## 准备阶段
 准备阶段是正式为类变量分配内存并设置类变量初始值的阶段，这些内存都将在方法区中分配。对于该阶段有以下几点需要注意：

1、这时候进行内存分配的仅包括类变量（static），而不包括实例变量，实例变量会在对象实例化时随着对象一块分配在Java堆中。

    2、这里所设置的初始值通常情况下是数据类型默认的零值（如0、0L、null、false等），而不是被在Java代码中被显式地赋予的值。

   假设一个类变量的定义为：

public static int value = 3；

    那么变量value在准备阶段过后的初始值为0，而不是3，因为这时候尚未开始执行任何Java方法，而把value赋值为3的putstatic指令是在程序编译后，存放于类构造器<clinit>（）方法之中的，所以把value赋值为3的动作将在初始化阶段才会执行。

下表列出了Java中所有基本数据类型以及reference类型的默认零值：

![](https://user-gold-cdn.xitu.io/2020/1/13/16f9dc78bb5752bc?w=358&h=428&f=png&s=38417)

这里还需要注意如下几点：
- 对基本数据类型来说，对于类变量（static）和全局变量，如果不显式地对其赋值而直接使用，则系统会为其赋予默认的零值，而- 对于局部变量来说，在使用前必须显式地为其赋值，否则编译时不通过。
- 对于同时被static和final修饰的常量，必须在声明的时候就为其显式地赋值，否则编译时不通过；而只被final修饰的常量则既可以在声明时显式地为其赋值，也可以在类初始化时显式地为其赋值，总之，在使用前必须为其显式地赋值，系统不会为其赋予默认零值。
- 对于引用数据类型reference来说，如数组引用、对象引用等，如果没有对其进行显式地赋值而直接使用，系统都会为其赋予默认的零值，即null。
- 如果在数组初始化时没有对数组中的各元素赋值，那么其中的元素将根据对应的数据类型而被赋予默认的零值。

如果类字段的字段属性表中存在ConstantValue属性，即同时被final和static修饰，那么在准备阶段变量value就会被初始化为ConstValue属性所指定的值。
## 解析阶段
解析阶段是虚拟机将常量池内的符号引用替换为直接引用的过程
##初始化阶段
到了初始化阶段，才真正开始执行类中定义的Java代码

## 类加载器

站在Java虚拟机的角度来讲，只存在两种不同的类加载器：

- 启动类加载器：它使用C++实现（这里仅限于Hotspot，也就是JDK1.5之后默认的虚拟机，有很多其他的虚拟机是用Java语言实现的），是虚拟机自身的一部分。
- 所有其他的类加载器：这些类加载器都由Java语言实现，独立于虚拟机之外，并且全部继承自抽象类java.lang.ClassLoader，这些类加载器需要由启动类加载器加载到内存中之后才能去加载其他的类。

站在Java开发人员的角度来看，类加载器可以大致划分为以下四类：
- 启动类加载器 （C实现）
- 扩展类加载器  (ClassLoader)
- 应用程序加载器 (ClassLoader)
-  自定义加载器 (ClassLoader)

这几种类加载器的层次关系如下图所示：

![](https://user-gold-cdn.xitu.io/2020/1/13/16f9dbd8c895cbb5?w=277&h=442&f=png&s=16625)


## 双亲委派模型
类加载器之间的这种层次关系叫做双亲委派模型。
双亲委派模型要求除了顶层的启动类加载器（Bootstrap ClassLoader）外，其余的类加载器都应当有自己的父类加载器。这里的类加载器之间的父子关系一般不是以继承关系实现的，而是用组合实现的。

## 双亲委派模型的工作过程
 由我来概况就是 八个字 **向上检查，从下加载**  
 如果一个类接受到类加载请求，他自己不会去加载这个请求，而是将这个类加载请求委派给父类加载器，这样一层一层传送，直到到达启动类加载器（Bootstrap ClassLoader）。
只有当父类加载器无法加载这个请求时，子加载器才会尝试自己去加载。

## 双亲委派模型的代码实现
双亲委派模型的代码实现集中在java.lang.ClassLoader的loadClass()方法当中。
- 首先检查类是否被加载，没有则调用父类加载器的loadClass()方法；
- 若父类加载器为空，则默认使用启动类加载器作为父加载器；
- 若父类加载失败，抛出ClassNotFoundException 异常后，再调用自己的findClass() 方法。
loadClass源代码如下：


```
protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    //1 首先检查类是否被加载
    Class c = findLoadedClass(name);
    if (c == null) {
        try {
            if (parent != null) {
             //2 没有则调用父类加载器的loadClass()方法；
                c = parent.loadClass(name, false);
            } else {
            //3 若父类加载器为空，则默认使用启动类加载器作为父加载器；
                c = findBootstrapClass0(name);
            }
        } catch (ClassNotFoundException e) {
           //4 若父类加载失败，抛出ClassNotFoundException 异常后，这个方法就是加载的核心代码
            c = findClass(name);
        }
    }
    if (resolve) {
        //5 再调用自己的findClass() 方法。
        resolveClass(c);
    }
    return c;
}

```

## 自定义类加载器

```
    class NetworkClassLoader extends ClassLoader {
 *         String host;
 *         int port;
 *
 *         public Class findClass(String name) {
 *             byte[] b = loadClassData(name);
 *             return defineClass(name, b, 0, b.length);
 *         }
 *
 *         private byte[] loadClassData(String name) {
 *             // load the class data from the connection
 *             &nbsp;.&nbsp;.&nbsp;.
 *         }
 *     }
```

这个就是官方的例子

![](https://user-gold-cdn.xitu.io/2020/1/13/16f9ebc694e854a6?w=2003&h=733&f=png&s=240274)

## 破环双亲委派

双亲委派模型很好的解决了各个类加载器加载基础类的统一性问题。即越基础的类由越上层的加载器进行加载。
若加载的基础类中需要回调用户代码，而这时顶层的类加载器无法识别这些用户代码，怎么办呢？这时就需要破坏双亲委派模型了。

java默认的线程上下文类加载器是系统类加载器(AppClassLoader).

```
// Now create the class loader to use to launch the application    
   try {    
       loader = AppClassLoader.getAppClassLoader(extcl);    
   } catch (IOException e) {    
       throw new InternalError(    
   "Could not create application class loader" );    
   }    
        
   // Also set the context class loader for the primordial thread.    
  Thread.currentThread().setContextClassLoader(loader);    
```

以上代码摘自sun.misc.Launch的无参构造函数Launch()。

使用线程上下文类加载器,可以在执行线程中,抛弃双亲委派加载链模式,使用线程上下文里的类加载器加载类.


典型的例子有,通过线程上下文来加载第三方库jndi实现,而不依赖于双亲委派.

大部分java app服务器(jboss, tomcat..)也是采用contextClassLoader来处理web服务。

![](https://user-gold-cdn.xitu.io/2020/1/13/16f9ec61a923915a?w=1255&h=91&f=png&s=42393)

## 结尾
今天把类加载机制好好讲了一下，这样大家就更加的熟悉了内的加载过程，对于Java开发是有好处的
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
