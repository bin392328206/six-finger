# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
前面的章节
- [🔥JVM从入门到入土之JVM的类加载机制](https://juejin.im/post/5e1aaf626fb9a0301d11ac8e)  
- [🔥JVM从入门到入土之JVM的类文件结构](https://juejin.im/post/5e1d19b26fb9a02fee1ed155)
- [🔥JVM从入门到入土之JVM的运行时数据区](https://juejin.im/post/5e1d6e5be51d453c951da505)

Java与C++之间有一堵内存动态分配和垃圾收集的高墙 外面的人想进去，里面的人想出来


## 概述
- 那些内存需要回收？什么时候进行回收？如何进行回收？
- 程序计数器、虚拟机栈、本地方法栈这三部分随着线程而生，随着线程而灭。栈中的栈帧随着方法有序的进出。每一个栈帧中分配内- 存是在类结构确定下来就已知（JIT会在编译时进行些许优化）
- 因此程序计数器、虚拟机栈、本地方法栈这三者内存分配和回收都具备确定性，垃圾回收再这几块不需要你过多的考虑。他们会随着- 线程的回收而自动进行回收。
- Java堆和方法区（Java堆中一个逻辑部分），所以说垃圾回收也是主要考虑到这块的内存的回收和利用。

## 对象已死？
- 需要判断Java堆中有哪些对象是活着或者死去（即不可能被任何途径使用的对象）

## 引用计数算法
- 引用计数算法很优秀应用很广泛，但是它很难解决对象之间循环依赖的而导致的问题
- 引用计数算法的存在的缺陷，如上
- JavaVM不是通过引用计数来进行垃圾回收的（来判断对象是否存活）

## 根可达性分析算法（GC Roots Tracing）
Java中可以作为GC Roots对象包括以下几种：
- 虚拟机栈（栈帧中本地变量表）中引用的对象；
- 方法区中类的静态属性引用的对象；
- 方法区中常量引用的对象；
- 本地方法栈中JNI（一般只Native方法）的引用对象- 

示例如下
![](https://user-gold-cdn.xitu.io/2020/1/15/16fa757e41829d20?w=789&h=494&f=png&s=88747)

## 再谈引用
1.2之前Java中对象只有：引用和未引用两种状态

1.2之后进行了扩充：强引用（Strong Reference）、软引用（Soft Reference）、弱引用（Weak Reference）、虚引用（Phantom Reference）

- Strong Reference：只要强引用还存在，垃圾回收器就不会进行回收
- Soft Reference：一些还有用，但是非必需的对象。系统将要发生OOM时，会将这些对象列入回收范围，并进行第二次垃圾回收。如- 果回收之后内存还不够则会抛出OOM。Java中提供SoftReference来实现。
- Weak Reference：描述非必需对象。弱引用的关联的对象只能生存到下一次垃圾回收器发生之前。无论内存是否充足，都会回收掉弱- 引用关联的对象。WeakReference类
- Phantom Reference：最弱的一种引用。一个对象是否有虚引用的存在都不会对其生存时间造成影响，也无法通过虚引用来获取一个- 对象的实例。为一个对象设置、虚引用的唯一目的就是希望在该对象被垃圾回收器回收时收到一个系统通知。PhantomReference类。

## 生存还是死亡

根搜索法不可达的对象，还有两次标记的过程，进行自救。

过程：

在跟搜索算法不可达的对象，并将第一次被标记并且进行一次筛选。筛选条件是：此对象是否有必要调用finalize()方法。当对象没有覆盖finalize或方法已被虚拟机执行了，虚拟机
会认为以上两种情况没有必要执行。

如果这个对象被判定为有必要执行finalize()方法。该对象将会被F-Queue的队列中，稍后虚拟机将建立一个低优先级的Finalizer线程去执行。这里的执行指的是虚拟机会触发该
方法，但是并不承诺等待他运行结束（原因：finalizer执行很慢或死循环，导致队列中其他的对象永远在等待或内存溢出）。finalizer方法是对象逃离死亡的最后一次机会，对象只要finalizer中
拯救自己（建立自己引用）第二次标记的时候该对象就被移除回收队列。如果没有拯救，那么很快不久就被回收。但是如果对象的finalizer方法执行了，但是可能该对象还存活着。

实例代码如下：

```
    public static FinalizerEscapeGC SAVE_HOOK = null;

    public void isAlive() {
        System.out.println("yes, i am still alive!!!");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalizer method execute");
        FinalizerEscapeGC.SAVE_HOOK = this;
    }

    public static void main(String[] args) throws Exception {
        SAVE_HOOK = new FinalizerEscapeGC();
        // 对象第一次进行成功的拯救
        SAVE_HOOK = null;
        System.gc();
        // 因为finalizer方法的优先级很低，所以暂停0.5s,以等待他运行
        Thread.sleep(500);
        if (SAVE_HOOK != null) {
            SAVE_HOOK.isAlive();
        } else {
            System.out.println("no, i am dead.");
        }

        // 下面的代码和上面的代码一样。但是对象却自救失败了。
        // 对象第二次进行成功的拯救
        SAVE_HOOK = null;
        System.gc();
        // 因为finalizer方法的优先级很低，所以暂停0.5s,以等待他运行
        Thread.sleep(600);
        if (SAVE_HOOK != null) {
            SAVE_HOOK.isAlive();
        } else {
            System.out.println("no, i am dead.");
        }
    }

```
## 回收方法区

- 方法区有被认为是HotSpot虚拟机中的永久代
- 误区：Java虚拟机规范中说过，不要求虚拟机在方法区实现垃圾收集，“性价比”很低；回收堆中新生代一般可以回收70%-95%,而永久- 代的垃圾回收远远低于此
- 永久代垃圾回收主要包括：废弃常量和无用的类。
- 回收废弃常量和回收Java堆中对象十分相似。以常量池中的字面值回收为例，“abc”字符串已进入常量池，但是系统中没有任何一个- 地方
- 引用字符串“abc”,也没有其他地方引用。如果这个时候发生内存回收，如果必要的话，该字符串变量则会被回收掉。

判定一个类是否是无用类则比较复杂。
 - 该类的所有实例都已经被回收，也就是Java堆中不存在该类的任何实例
 - 该类的ClassLoader已经被回收
 - 该类对应的java.lang.Class对象没有在任何地方被引用，无法在任何地方通过反射访问该类的方法
即时是满足以上三个条件，虚拟机也仅仅是可以进行回收。不像对象一样，一定会被回收。对类的回收，Hotspot

提供了-Xnoclassgc参数进行控制，还可以使用-verbose:class及-XX:+TraceClassLoading、-XX:+TraceClassUnLoading
其中前两个参数在product版虚拟机中已经支持，最后一个需要FastDebug版的虚拟机支持。

##  垃圾收集算法

### 标记 - 清除算法

1. 首先标记处所有需要回收的对象；其次在标记完成之后统一回收掉所有被标记的对象。

2. 最基础的算法。后续的算法都是基于此，并针对其缺陷进行改进的得到的。
3. 缺点：
    - 效率（不高）标记和清除的效率都不高
    - 空间问题，标记清除后会导致大量不连续的内存碎片，而碎片过多可能会导致以后程序在运作的过程中，分配较大对象时无法找到足够的连续内存而不得不触发另一次垃圾回收动作
4. 图解
    
![](https://user-gold-cdn.xitu.io/2020/1/16/16fabe022d334c57?w=771&h=398&f=png&s=75539)

### 标记 - 复制算法(典型的用空间换时间的手法)
1. 为了解决效率问题而出现的复制算法
2. 复制算法：
    - 内容：将可用内存按容量划分成大小相等的两块。每次只是使用其中的一半，将快使用完成后将存活的对象
复制到另外一块内存上去，然后再把使用过的内存一次清理掉。
- 好处1：每次都是对整块内存进行回收，减少了内存碎片的复杂情况
- 好处2：移动时只需移动堆顶指针，按照顺序分配即可，简单高效
- 缺点：内存缩小为原来的一半，有点浪费内存空间
3.图解

![](https://user-gold-cdn.xitu.io/2020/1/16/16fabe410e567901?w=817&h=392&f=png&s=97378)

4. 现代的商业虚拟机都是都是采用这种算法来回收新生代内存。新生代中对象98%都是朝生夕死。并非严格按照1:1。而是按照8:1。

5. 1块较大的Eden和2块较小的Survivor内存；每次使用Eden和1个Survivor。（Eden:Survivor = 8:1）

6. 这样内存中整个新生代的内存容量为（80+10=90），保证了只有10%的内存容量的浪费。
但是实际发生垃圾回收时我们无法保证98%对象都是标记死亡的，如果存活的对象的占用的内存多于剩下的10%的容量，这时则需要其他内存（老年代）进行分配担保。

###  标记 - 整理算法

1. 复制算法在对象存活率较多时就需要执行较多的复制操作，效率将会降低。
2. 关键的是，如果不想浪费50%的内存，就需要有额外的内存空间进行分配担保，以应对已使用内存中的对象100%存活的情况。所以老年代一般不会选择此算法（复制算法）。
3. 根据老年代的特点：提出了标记 - 整理算法。标记过程和标记 - 清理一样。但是后续的步骤：不是对可回收的对象进行清理，
而是让所有存活的对象向一端进行移动。然后清理掉端边界以外的内存
4. 图解

![](https://user-gold-cdn.xitu.io/2020/1/16/16fabed392d5c19c?w=874&h=485&f=png&s=73213)


### 分代收集算法
- Java堆：新生代和老年代
- 新生代：适合复制算法。只需付出复制少量存活对象的成本就可以完成收集。
- 老年代：（对象的存活率较高，并且没有额外的空间对他进行内存分配）适合使用标记 - 清理或者标记 - 整理算法

### HotSpot算法实现
- 以GC Roots节点找引用链为例（作为GC Roots节点主要是全局性引用常量或类的静态属性或执行上下文栈帧中的本地变量表）
- 可达性分析对执行时间十分敏感，GC停顿以确保一致性（Stop The World）
- 准确式GC、OopMap、

## 垃圾收集器
到Java8为止

![](https://user-gold-cdn.xitu.io/2020/1/16/16fac0cb00481200?w=507&h=347&f=png&s=51648)

### Serial收集器
- 历史悠久，jdk1.3虚拟机新生代唯一选择
- 单线程收集器；进行GC工作时必须暂停其他所有的工作线程，直到他收集结束
- 使用复制算法完成

![](https://user-gold-cdn.xitu.io/2020/1/16/16fac116bc978d7d?w=741&h=126&f=png&s=33416)
- 1.3-1.7不断追求GC停顿时间的缩短，而获取更好的体验


### ParNew收集器
- ParNew收集器是多线程版的Serial，除了使用多条线程进行垃圾回收外。其余行为包括Serial可用的所有参数、收集算法、StopTheWorld、对象分配规则、回收策略和Serial垃圾器一致。
- 图解

![](https://user-gold-cdn.xitu.io/2020/1/16/16fac12d3b0ec16c?w=720&h=197&f=png&s=41461)
- 除了Serial之外，只有ParNew可以配合CMS工作
- 也是新生代的收集器

### Parallel Scavenge收集器

- 也是一个新生代的收集器
- 使用了复制算法同时又是一个并行的多线程收集器
- CMS关于垃圾收集尽量缩短用户线程停止的时间；Parallel - Scavenge目的是达到一个可控制的目标吞吐量。（所谓吞吐量CPU用于运行用户代码时间和CPU总时间的比值。
- 吞吐量 = 运行用户代码时间 / (运行用户代码时间和垃圾收集时间)）
- 停顿时间越短就越适合需要和用户交互的程序，良好的相应的速度可以提升用户体验。高吞吐量则可以最高效的利用CPU时间，尽快- 完成程序运算任务主要适合在后台运算而不是交互性的程序
- Parallel Scavenge提供两个参数。-XX:MaxGCPauseMillis(最大垃圾收集停留时间) -XX:GCTimeRatio(设置吞吐量大小)
- MaxGCPauseMillis:大于0的毫秒值。收集器将尽力保证最大垃圾收集停留时间不超过设定值。
- GCTimeRatio:大于0小于100的值。默认值99。也就是垃圾收集时间占总时间的比例。吞吐量的倒数。
- -XX:+UseAdaptiveSizePolicy:开关参数。开启后，无需手动指定新生代的大小、Eden、Survivor之间的比例等其他。又称“GC自适应- 调节”。自适应也是Parallel Scavenge和ParNew的一个重要区别。


### Serial Old收集器
- Serial Old是Serial收集器的老年代收集器。
- 单线程收集器。标记 - 清理算法。
- 主要意思在Client模式下虚拟机使用。
- Server模式下：
    - JDK1.5之前版本配合Parallel Scavenge使用
    - CMS收集器的后备选择
- 图解

![](https://user-gold-cdn.xitu.io/2020/1/16/16fac1877518704e?w=563&h=160&f=png&s=33041)


### Parallel Old收集器
- 是Parallel Scavenge的老年代版本
- 使用多线程和标记 - 清除算法
- JDK1.6后才出现的
- 图解

![](https://user-gold-cdn.xitu.io/2020/1/16/16fac1a9e1017e32?w=672&h=169&f=png&s=39763)

### CMS收集器
- 一种以获取最短回收停顿时间为目标的垃圾器
- 基于标记 - 清除算法实现的
- 实现细节：
    - 初始标记
    - 并发标记
    - 重新标记
    - 并发清除
    
其中，初始标记和重新标记还需stop the world。初始标记只是标记一下GC Roots能直接关联到的对象，速度很快。并发标记阶段也就是进行GC Roots Tracing的过程，而重新标记是为了修正
因用户程序继续运行导致标记变动的那一部分对象的标记记录。重新标记这段时间远比初始标记时间长但是远比并发标记时间短。


![](https://user-gold-cdn.xitu.io/2020/1/16/16fac1e7f54d4d3d?w=775&h=201&f=png&s=50417)

- 优点：并发收集、低停顿
- CMS收集器对CPU资源非常敏感；CMS收集器无法处理浮动垃圾,使其在标记过程失败，从而导致一次FullGC；CMS收集器是基于标记
- 清除的算法，而其算法容易导致内存碎片。

### G1收集器

- 基于标记 - 清理算法，所以对长时间运行的系统，不会产生内存碎片。
- 可以精准地控制停顿，既能让使用者明确指定在一个长度的时间M毫秒时间片段内，消耗在垃圾收集上的时间不得
超过N毫秒，这基本是Java(RTSJ)垃圾器的特征
- G1收集器基本上不牺牲吞吐量的情况下完成低停顿的内存回收；G1将Java堆（新生代和老年代）划分成多个大大小小的独立区域，
然后进行区域回收

## 理解GC日志
图片

![](https://user-gold-cdn.xitu.io/2020/1/16/16fac23cadccc3c8?w=932&h=153&f=png&s=49620)

阅读

- 最前面的数字：33.125 100.667 代表GC发生的时间；这个是从虚拟机启动来经过的秒数

- GC和Full GC说明了此次垃圾收集停顿的类型。不是用来区分新生代GC还是老年代GC。如果有Full，则表明此次GC
发生了Stop The World
- [DefNew [Tenured [Perm表示GC发生的区域
Serial收集器中的新生代名为Default New Generation所以显示[DefNew；如果是Parallel收集器新生代名称为
[ParNew 意为"Parallel New Generation"；如果是Parallel Scavenge收集器新生代名称叫做PSYoungGen。老年代和永久代同理，根据垃圾收集器
的名称来决定的
- 方括号内的“3324K --> 152K(3712K)”表示GC前的内存区域已使用容量 -> GC后内存区域已使用的容量（该内存区域总容量）；
而方括号之外的3324K -> 152K（11904K）表示GC前Java堆已使用的容量->GC后Java堆已使用的容量（Java堆总容量）
- 0.0025925 secs表示该内存区域GC所占时间，单位是秒。
## 垃圾收集器参数总结

![](https://user-gold-cdn.xitu.io/2020/1/16/16fac27b4d2b79ed?w=827&h=833&f=png&s=209354)

## 内存分配和回收策略
Java自动内存管理主要解决了两个问题：
- 给对象分配内存
- 回收分配给对象的内存

### 对象优先在Eden分配

- 大多数情况下，对象在Eden区中分配。当Eden内存不足时，虚拟机将发起一次MinorGC

- 提供了-XX:+PrintGCDetails日志参数。告诉虚拟机发生垃圾回收时打印内存回收日志，并在线程结束后输出各内存的分配情况

- Minor GC和Full GC(Stop The world)
    - 新生代GC(Minor GC)：回收频繁，且回收速度较快
    - 老年代GC(Major GC或Full GC)：经常至少伴随着一次Minor GC(并不是绝对，例如PS回收)；Major GC速度至少比Minor GC速度慢10倍以上
示例

```
/**
 * VM args：-verbose:gc -Xms20M -Xmn10M -XX:SurvivorRatio=8 -XX:+PrintGCDetails
 */
public class TestAllocation {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        byte[] allocation1, allocation2, allocation3, allocation4, allocation5;
        allocation1 = new byte[2 * _1MB];
        allocation2 = new byte[2 * _1MB];
        allocation3 = new byte[2 * _1MB];
        allocation3 = new byte[2 * _1MB];
        allocation4 = new byte[2 * _1MB];
        allocation5 = new byte[4 * _1MB];
        // 至少发生了MinorGC
    }
}
```

```
[GC (Allocation Failure) [PSYoungGen: 7651K->1016K(9216K)] 7651K->2131K(19456K), 0.0017243 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[GC (Allocation Failure) [PSYoungGen: 7402K->1000K(9216K)] 8518K->8375K(19456K), 0.0039360 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[Full GC (Ergonomics) [PSYoungGen: 1000K->0K(9216K)] [ParOldGen: 7375K->8172K(24576K)] 8375K->8172K(33792K), [Metaspace: 3455K->3455K(1056768K)], 0.0103884 secs] [Times: user=0.16 sys=0.00, real=0.01 secs] 
Heap
 PSYoungGen      total 9216K, used 4373K [0x00000007bf600000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 8192K, 53% used [0x00000007bf600000,0x00000007bfa457e0,0x00000007bfe00000)
  from space 1024K, 0% used [0x00000007bff00000,0x00000007bff00000,0x00000007c0000000)
  to   space 1024K, 0% used [0x00000007bfe00000,0x00000007bfe00000,0x00000007bff00000)
 ParOldGen       total 24576K, used 12268K [0x00000006c2800000, 0x00000006c4000000, 0x00000007bf600000)
  object space 24576K, 49% used [0x00000006c2800000,0x00000006c33fb3c8,0x00000006c4000000)
 Metaspace       used 3477K, capacity 4496K, committed 4864K, reserved 1056768K
  class space    used 375K, capacity 388K, committed 512K, reserved 1048576K

```

![](https://user-gold-cdn.xitu.io/2020/1/16/16fac5abffe754bb?w=1909&h=896&f=png&s=246543)

### 大对象直接进入老年代

- 所谓大对象：需要大量连续内存空间的Java对象。典型的如很长的字符串或数组（上面例子中byte[]就是大对象）

- 大对象对虚拟机来说是坏事，尤其是那种朝生夕死的大对象。经常内存不足而导致不得不提前进行GC

- 虚拟机提供-XX:PretenureSizeThreshold参数，使大于此值的对象在老年代分配。好处是避免了在Eden区及两个Survivor区间发生大量的复制。新生代采用复制算法进行垃圾收集。

### 长期存活的对象将进入老年代

- 每个对象定义一个年龄（Age）计数器

- 对象在Survivor区域每熬过一次MinorGC，年龄就会增加。默认年龄为15。当年龄超过一定的阈值就会进入老年代。
通过参数：-XX:MaxTenuringThreshold设置

### 动态对象年龄判断

为了更好的适应不同的程序，虚拟机并不是永远要求对象年龄达到MaxTenuringThreshold才晋升到老年代。
如果在Survivor空间的相同年龄所有对象大小的总和大于Survivor空间的一半，年龄大于或等于该年龄的对象可以直接进入老年代，无需MaxTenuringThreshold要求的对象


## 结尾
JVM的基本知识就这么多了，接下来我会给大家出一章面试题的章节，和一章根据业务实战JVM调优，大家持续关注哦
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
