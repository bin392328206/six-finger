# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
昨天讲了类加载机制，其实那个应该算是第二步，第一步还是我们的.Class文件的结构，但是直接讲这个未免太枯燥，所以我就写讲了类加载机制，再讲文件结构
- [🔥JVM从入门到入土之JVM的类加载机制](https://juejin.im/post/5e1aaf626fb9a0301d11ac8e)  

我们知道我们写完的Java程序经过javac xxx.java编译后生成了xxx.class文件，可是你是否想过xxx.class文件到底是什么？这个文件中到底包含了什么内容？那么现在我们就一起通过解析一个.class文件来深入的学习一下类文件结构，通过这次的学习，我想你会对class文件了如指掌。


## Class类文件结构

在解析一个class文件之前，我们需要先学习一下Class类文件的结构，这个类文件结构相当于一个总纲，我们马上就会对照着这个类文件结构解析真正的class文件。

- Class文件是一组以8个字节为基础单位的二进制流（可能是磁盘文件，也可能是类加载器直接生成的），各个数据项目严格按照顺序- 紧凑地排列，中间没有任何分隔符；
- Class文件格式采用一种类似于C语言结构体的伪结构来存储数据，其中只有两种数据类型：无符号数和表；
- 无符号数属于基本的数据类型，以u1、u2、u4和u8来分别代表1个字节、2个字节、4个字节和8个字节的无符号数，可以用来描述数字- 、索引引用、数量值或者按照UTF-8编码构成字符串值；
- 表是由多个无符号数获取其他表作为数据项构成的复合数据类型，习惯以“_info”结尾；
- 无论是无符号数还是表，当需要描述同一个类型但数量不定的多个数据时，经常会使用一个前置的容量计数器加若干个连续的数据项- 的形式，这时称这一系列连续的某一类型的数据未某一类型的集合。

类文件结构图：

![](https://user-gold-cdn.xitu.io/2020/1/14/16fa2e433fdd1775?w=922&h=409&f=png&s=197613)

## 类文件分析

```
package temp;
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello,World");
    }
}
```

我们通过16进制编辑器打开编译后的HelloWorld.class文件，其十六进制的文件内容如下：


![](https://user-gold-cdn.xitu.io/2020/1/14/16fa2e49951bd107?w=1150&h=828&f=png&s=213548)

###  魔数和版本
- Class文件的头4个字节，唯一作用是确定文件是否为一个可被虚拟机接受的Class文件，固定为“0xCAFEBABE”。
- 第5和第6个字节是次版本号，第7和第8个字节是主版本号（0x0034为52，对应JDK版本1.8）；Java的版本号是从45开始的，JDK1.1之后的每一个JDK大版本发布主版本号向上加1，高版本的JDK能向下兼容低版本的JDK。

对应到class文件中就是：


![](https://user-gold-cdn.xitu.io/2020/1/14/16fa2e537a97b75d?w=562&h=89&f=png&s=19753)
图中1就是魔数，第二个就是版本

### 常量池

紧接着主版本号的就是常量池，常量池可以理解为class文件的资源仓库，它是class文件结构中与其它项目关联最多的数据类型，也是占用class文件空间最大的数据项目之一，也是class文件中第一个出现的表类型数据项目。

由于常量池中常量的数量不是固定的，所以常量池入口需要放置一项u2类型的数据，代表常量池中的容量计数。不过，这里需要注意的是，这个容器计数是从1开始的而不是从0开始，也就是说，常量池中常量的个数是这个容器计数-1。将0空出来的目的是满足后面某些指向常量池的索引值的数据在特定情况下需要表达“不引用任何一个常量池项目”的含义。class文件中只有常量池的容量计数是从1开始的，对于其它集合类型，比如接口索引集合、字段表集合、方法表集合等的容量计数都是从0开始的。

常量池中主要存放两大类常量：字面量和符号引用。字面量比较接近Java语言的常量概念，如文本字符串、声明为final的常量等。而符号引用则属于编译原理方面的概念，它包括三方面的内容：
- 类和接口的全限定名（Fully Qualified Name）；
- 字段的名称和描述符（Descriptor）；
- 方法的名称和描述符；

Java代码在进行javac编译的时候并不像C和C++那样有连接这一步，而是在虚拟机加载class文件的时候进行动态连接。也就是说，在class文件中不会保存各个方法、字段的最终内存布局信息，因此这些字段、方法的符号引用不经过运行期转换的话无法得到真正的内存入口地址，虚拟机也就无法使用。当虚拟机运行时，需要从常量池获得对应的符号引用，再在类创建时或运行时解析、翻译到具体的内存地址中。

常量池中的每一项都是一个表，在JDK1.7之前有11中结构不同的表结构，在JDK1.7中为了更好的支持动态语言调用，又增加了3种（CONSTANT_MethodHandle_info、CONSTANT_MethodType_info和CONSTANT_InvokeDynamic_info）。不过这里不会介绍这三种表数据结构。

这14个表的开始第一个字节是一个u1类型的tag，用来标识是哪一种常量类型。这14种常量类型所代表的含义如下：

![](https://user-gold-cdn.xitu.io/2020/1/14/16fa2d9fdccfd701?w=907&h=468&f=png&s=210182)

由class文件结构图可知：

![](https://user-gold-cdn.xitu.io/2020/1/14/16fa2da39d9eb6f4?w=545&h=69&f=png&s=14285)

常量池的开头两个字节0x0022是常量池的容量计数，这里是34，也就是说，这个常量池中有33个常量项。
我们可以看一下这33个常量：


![](https://user-gold-cdn.xitu.io/2020/1/14/16fa2e59e3263ebf?w=587&h=52&f=png&s=6421)

蓝色部分的内容就是33个常量，我们可以发现图片右边用UTF-8编码后已经把常量翻译成了英文字母。可以看到这部分的内容非常多。因为常量池中的常量比较多，每一中常量还有自己的结构，导致常量池的结构非常复杂，这里只解析第一个常量作为示例：

看看这个例子的第一项，容量计数后面的第一个字节标识这个常量的类型，是0x0A，即10，查表可知是类方法的符号引用，这个常量表的结构如下：


![](https://user-gold-cdn.xitu.io/2020/1/14/16fa2e607ba8342f?w=954&h=620&f=png&s=424624)

按照这个结构，可以知道name_index是6（0x0006），descriptor_index是20（0x0014）。这都是一个索引，指向常量池中的其他常量，其中name描述了这个方法的名称，descriptor描述了这个方法的访问标志（比如public、private等）、参数类型和返回类型。（这里因为手工解析常量池确实是一件很坑爹的工作，而且后面会介绍自动解析的工具，所以这里就不去管name和descriptor的内容了）

我们可以看到手工解析常量池是一件非常痛苦的事情，这里还只是一个特别简单的例子生成的class文件，我们可以自己想想如果是自己写的一个程序编译为class文件后，它的常量池会非常大，所以Java已经为我们提供了一个解析常量池的工具javap，我们可以通过javap -verbose class文件名，就可以自动帮我们解析了，下面是这个程序的解析结果：

```
警告: 二进制文件HelloWorld包含temp.HelloWorld
Classfile /I:/work/out/production/work/temp/HelloWorld.class
  Last modified 2018-8-3; size 543 bytes
  MD5 checksum 5eeb0ca06c253d3206781e81895bd4a4
  Compiled from "HelloWorld.java"
public class temp.HelloWorld
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #6.#20         // java/lang/Object."<init>":()V
   #2 = Fieldref           #21.#22        // java/lang/System.out:Ljava/io/PrintStream;
   #3 = String             #23            // Hello,World
   #4 = Methodref          #24.#25        // java/io/PrintStream.println:(Ljava/lang/String;)V
   #5 = Class              #26            // temp/HelloWorld
   #6 = Class              #27            // java/lang/Object
   #7 = Utf8               <init>
   #8 = Utf8               ()V
   #9 = Utf8               Code
  #10 = Utf8               LineNumberTable
  #11 = Utf8               LocalVariableTable
  #12 = Utf8               this
  #13 = Utf8               Ltemp/HelloWorld;
  #14 = Utf8               main
  #15 = Utf8               ([Ljava/lang/String;)V
  #16 = Utf8               args
  #17 = Utf8               [Ljava/lang/String;
  #18 = Utf8               SourceFile
  #19 = Utf8               HelloWorld.java
  #20 = NameAndType        #7:#8          // "<init>":()V
  #21 = Class              #28            // java/lang/System
  #22 = NameAndType        #29:#30        // out:Ljava/io/PrintStream;
  #23 = Utf8               Hello,World
  #24 = Class              #31            // java/io/PrintStream
  #25 = NameAndType        #32:#33        // println:(Ljava/lang/String;)V
  #26 = Utf8               temp/HelloWorld
  #27 = Utf8               java/lang/Object
  #28 = Utf8               java/lang/System
  #29 = Utf8               out
  #30 = Utf8               Ljava/io/PrintStream;
  #31 = Utf8               java/io/PrintStream
  #32 = Utf8               println
  #33 = Utf8               (Ljava/lang/String;)V
{
  public temp.HelloWorld();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 2: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Ltemp/HelloWorld;

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #3                  // String Hello,World
         5: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return
      LineNumberTable:
        line 4: 0
        line 5: 8
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       9     0  args   [Ljava/lang/String;
}
SourceFile: "HelloWorld.java"
```

## 访问标志
常量池结束后紧接着的两个字节代表访问标志，用来标识一些类或接口的访问信息，包括：这个Class是类还是接口；是否定义为public；是否定义为abstract；如果是类的话，是否被声明为final等。具体的标志位以及含义如下表：

![](https://user-gold-cdn.xitu.io/2020/1/14/16fa2e1920101145?w=998&h=373&f=png&s=101963)


![](https://user-gold-cdn.xitu.io/2020/1/14/16fa2e6931c5ad2a?w=561&h=67&f=png&s=9116)

由于access_flags是两个字节大小，一共有十六个标志位可以使用，当前仅仅定义了8个，没有用到的标志位都是0。对于一个类来说，可能会有多个访问标志，这时就可以对照上表中的标志值取或运算的值。拿上面那个例子来说，它的访问标志值是0x0021，查表可知，这是ACC_PUBLIC和ACC_SUPER值取或运算的结果。所以HelloWorld这个类的访问标志就是ACC_PUBLIC和ACC_SUPER，这一点我们可以在javap得到的结果中验证：

![](https://user-gold-cdn.xitu.io/2020/1/14/16fa2e6f0dff1f3f?w=963&h=204&f=png&s=96016)

## 类索引、父类索引与接口索引集合

在访问标志access_flags后接下来就是类索引（this_class）和父类索引（super_class），这两个数据都是u2类型的，而接下来的接口索引集合是一个u2类型的集合，class文件由这三个数据项来确定类的继承关系。由于Java中是单继承，所以父类索引只有一个；但Java类可以实现多个接口，所以接口索引是一个集合。

类索引用来确定这个类的全限定名，这个全限定名就是说一个类的类名包含所有的包名，然后使用”/”代替”.”。比如Object的全限定名是java.lang.Object。父类索引确定这个类的父类的全限定名，除了Object之外，所有的类都有父类，所以除了Object之外所有类的父类索引都不为0.接口索引集合存储了implements语句后面按照从左到右的顺序的接口。

类索引和父类索引都是一个索引，这个索引指向常量池中的CONSTANT_Class_info类型的常量。然后再CONSTANT_Class_info常量中的索引就可以找到常量池中类型为CONSTANT_Utf8_info的常量，而这个常量保存着类的全限定名。

## 字段表集合

字段表集合，顾名思义就是Java类中的字段，字段又分为类字段（静态属性）和实例字段（对象属性），那么，在Class文件中是如何保存这些字段的呢？我们可以想一想保存一个字段需要保存它的哪些信息呢？

答案是：字段的作用域（public、private和protected修饰符）、是实例变量还是类变量（static修饰符）、可变性（final修饰符）、并发可见性（volatile修饰符）、是否可被序列化（transient修饰符）、字段的数据类型（基本类型、对象、数组）以及字段名称。

## 方法表集合

在字段表集合中介绍了字段的描述符和方法的描述符，对于理解方法表有很大帮助。class文件存储格式中对方法的描述和对字段的描述几乎相同，方法表的结构也和字段表相同，这里就不再列出。不过，方法表的访问标志和字段的不同，列出如下：

## 属性表集合
属性表在前面出现了多次，在Class文件、字段表和方法表都可以携带自己的属性表集合，来描述某些场景专有的信息。
与Class文件中其他的数据项目要求严格的顺序、长度和内容不同，属性表集合的限制比较少，不要求严格的顺序，只要不与已有的属性名重复，任何人实现的编译器都可以向属性表中写入自定义的属性信息，Java虚拟机会在运行时忽略掉那些不认识的信息。为了能正确解析class文件，《Java虚拟机规范（第二版）》中预定义了9项虚拟机应当识别的属性。现在，属性已经达到了21项。具体信息如下表，这里仅对常见的属性做介绍：


## 结尾
其实真心不想写这篇的，因为自己也没有静下心来，认真的一个个自己去实际，只是说把书上的东西搬过来，这个坑以后补吧，可能对字节码的东西还是刚接触，等有了最基本的概率再去啃它，太难了
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
