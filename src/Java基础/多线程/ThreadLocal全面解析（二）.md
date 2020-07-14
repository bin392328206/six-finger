# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨  
昨天把ThreadLocal的基本要用的知识点讲了下，比如说 Java的引用  this 关键字 还有Java中的值传递和地址传递 
如果没有看的话建议先看下
- [Java中的值传递和地址传递](https://juejin.im/post/5f058d8ae51d4534695c2237)


## 前世今生
前面的文章，我们学习了有关锁的使用，锁的机制是保证同一时刻只能有一个线程访问临界区的资源，也就是通过控制资源的手段来保证线程安全，这固然是一种有效的手段，但程序的运行效率也因此大大降低。那么，有没有更好的方式呢？答案是有的，既然锁是严格控制资源的方式来保证线程安全，那我们可以反其道而行之，增加更多资源，保证每个线程都能得到所需对象，各自为营，互不影响，从而达到线程安全的目的，而ThreadLocal便是采用这样的思路。

### ThreadLocal实例

ThreadLocal翻译成中文的话大概可以说是：线程局部变量，也就是只有当前线程能够访问。它的设计作用是为每一个使用该变量的线程都提供一个变量值的副本，每个线程都是改变自己的副本并且不会和其他线程的副本冲突，这样一来，从线程的角度来看，就好像每个线程都拥有了该变量。



```
public class ThreadLocalDemo {

    static ThreadLocal<Integer> local = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    public static class MyRunnable implements Runnable{

        @Override
        public void run() {
            for (int i = 0;i<3;i++){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int value = local.get();
                System.out.println(Thread.currentThread().getName() + ":" + value);
                local.set(value + 1);
            }
        }
    }

    public static void main(String[] args) {
        MyRunnable runnable = new MyRunnable();
        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        t1.start();
        t2.start();
    }
}
```

上面的代码不难理解，首先是定义了一个名为 local 的ThreadLocal变量，并初识变量的值为0，然后是定义了一个实现Runnable接口的内部类，在其run方法中对local 的值做读取和加1的操作，最后是main方法中开启两个线程来运行内部类实例。

以上就是代码的大概逻辑，运行main函数后，程序的输出结果如下：


```
Thread-0:0
Thread-1:0
Thread-1:1
Thread-0:1
Thread-1:2
Thread-0:2
```

从结果可以看出，虽然两个线程都共用一个Runnable实例，但两个线程中所展示的ThreadLocal的数据值并不会相互影响，也就是说这种情况下的local 变量保存的数据相当于是线程安全的，只能被当前线程访问。


## ThreadLocal实现原理

那么ThreadLocal内部是怎么保证对象是线程私有的呢？毫无疑问，答案需要从源码中查找。回顾前面的代码，可以发现其中调用了ThreadLocal的两个方法set 和 get，我们就从这两个方法入手。

先看 set() 的源码：


![](https://user-gold-cdn.xitu.io/2020/7/9/17332878398b144c?w=604&h=281&f=png&s=15271)


- 获取当前线程的成员变量map
- map非空，则重新将ThreadLocal和新的value副本放入到map中。
- map空，则对线程的成员变量ThreadLocalMap进行初始化创建，并将ThreadLocal和value副本放入map中。



不过，与常见的Map实现类，如HashMap之类的不同的是，ThreadLocalMap中的Entry是继承于WeakReference类的，保持了对 “键” 的弱引用和对 “值” 的强引用，这是类的源码：

![](https://user-gold-cdn.xitu.io/2020/7/9/1733288b650b724f?w=877&h=783&f=png&s=50268)

从源码中中可以看出，Entry构造函数中的参数 k 就是ThreadLocal实例，调用super(k) 表明对 k 是弱引用，使用弱引用的原因在于，当没有强引用指向 ThreadLocal 实例时，它可被回收，从而避免内存泄露，那么为何需要防止内存泄露呢？原因下面会说到。

接着说set方法的逻辑，当调用set方法时，其实是将数据写入threadLocals这个Map对象中，这个Map的key为ThreadLocal当前对象，value就是我们存入的值。而threadLocals本身能保存多个ThreadLocal对象，相当于一个ThreadLocal集合。


接着看 get() 的源码：

![](https://user-gold-cdn.xitu.io/2020/7/9/1733289a4ef81ca2?w=713&h=406&f=png&s=20039)


 
1. 获取当前线程的ThreadLocalMap对象threadLocals
2. 从map中获取线程存储的K-V Entry节点。
3. 从Entry节点获取存储的Value副本值返回。
4. map为空的话返回初始值null，即线程变量副本为null，在使用时需要注意判断NullPointerException。



看到这，我们大概就能明白为什么ThreadLocal能实现线程私有的原理了，其实就是每个线程都维护着一个ThreadLocal的容器，这个容器就是ThreadLocalMap，可以保存多个ThreadLocal对象。而调用ThreadLocal的set或get方法其实就是对当前线程的ThreadLocal变量操作，与其他线程是分开的，所以才能保证线程私有，也就不存在线程安全的问题了。


然而，该方案虽然能保证线程私有，但却会占用大量的内存，因为每个线程都维护着一个Map，当访问某个ThreadLocal变量后，线程会在自己的Map内维护该ThreadLocal变量与具体实现的映射，如果这些映射一直存在，就表明ThreadLocal 存在引用的情况，那么系统GC就无法回收这些变量，可能会造成内存泄露。

针对这种情况，上面所说的ThreadLocalMap中Entry的弱引用就起作用了。这个也是我昨天为啥要做那么的工作来说其他的知识点，比如弱引用，值传递等


## ThreadLocal和弱引用的那些事

ThreadLocalMap本质上也是个Map,其中Key是我们的ThreadLocal这个对象，Value就是我们在ThreadLocal中保存的值。也就是说我们的ThreadLocal保存和取对象都是通过Thread中的ThreadLocalMap来操作的，而key就是本身。在ThreadLocalMap中Entry有如下定义:


![](https://user-gold-cdn.xitu.io/2020/7/9/1733295ec347b1ba?w=672&h=330&f=png&s=15647)

可以看见Entry是WeakReference的子类，而这个弱引用所关联的对象正是我们的ThreadLocal这个对象。那我就要问个问题了

```
"threadlocal的key是弱引用，那么在threadlocal.get()的时候,发生GC之后，key是否是null？"
```

这个问题晃眼一看，弱引用嘛，还有垃圾回收那肯定是为null，这其实是不对的，因为题目说的是在做threadlocal.get()操作，证明其实还是有强引用存在的。所以key并不为null。如果我们的强引用不存在的话，那么Key就会被回收，也就是会出现我们value没被回收，key被回收，导致value永远存在，出现内存泄漏。这也是ThreadLocal经常会被很多书籍提醒到需要remove()的原因。

![](https://user-gold-cdn.xitu.io/2020/7/9/173329738e239768?w=725&h=512&f=png&s=31407)
你也许会问看到很多源码的ThreadLocal并没有写remove依然再用得很好呢？那其实是因为很多源码经常是作为静态变量存在的生命周期和Class是一样的，而remove需要再那些方法或者对象里面使用ThreadLocal，因为方法栈或者对象的销毁从而强引用丢失，导致内存泄漏。

还有本身源代码也帮我们做了点优化，出现内存泄漏的可能性比较少，我们直接看代码

![](https://user-gold-cdn.xitu.io/2020/7/9/173329bb1515f866?w=590&h=243&f=png&s=12986)

这个set方法，我们直接跟进去看看

![](https://user-gold-cdn.xitu.io/2020/7/9/173329c1af63657e?w=942&h=685&f=png&s=46757)

这是java8种ThreadLocal.set()方法，for循环是遍历整个Entry数组，红色框的地方是碰到了(null,value)的处理逻辑，也就是碰到了内存泄漏后会将原来的Entry替换掉避免内存泄漏。


## 线程复用时Threadlocal需要注意的点

Threadlocal为每个使用该变量的线程提供独立的变量副本。

使用的情况：对每一个线程都必须持有一个类的实例，而且这个类是可变的(不可变的就是线程安全的，全部线程使用一个就可以了)，例如hibernate对session的处理。


问题场景：用户登录时，token保存在ThreadLocal里，但是经常偶现 token失效（在tonken在有效时间里）

产生原因：使用线程池或有复用线程时，复用同一个线程时，每次请求结束后ThreadLoca的值l没有清空，导致第二次使用时ThreadLocal的token还是上次遗留一下的token，以致tonken失效。

 

tomcat默认使用线程池，所以一个线程的生命周期不能对等于一个请求的生命周期，线程池中的线程是可以被复用的。

解决方案：

1、保证每次都用新的值覆盖线程变量；

2、保证在每个请求结束后清空线程变量。





## 结尾

ThreadLoal 相关的知识，这边就分析完毕了，具体运用其实在shiro 等安全框架中，当前请求获取当前用户对象中可以经常看得到的

![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！