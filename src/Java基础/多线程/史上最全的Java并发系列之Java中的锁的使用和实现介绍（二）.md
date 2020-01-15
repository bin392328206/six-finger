# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
上节是锁的第一小节，其实讲的是底层的定义，怎么去定义一把锁，设置这把锁能同时有几个钥匙，这些，这节的话，我们来具体来看看官方的实现。  
- [🔥史上最全的Java并发系列之并发编程的挑战](https://juejin.im/post/5dfb1ca26fb9a0160b63827f)  
- [🔥史上最全的Java并发系列之Java并发机制的底层实现原理](https://juejin.im/post/5dfb3a27e51d4558181d35b0)    
- [🔥史上最全的Java并发系列之Java内存模型](https://juejin.im/post/5dfc3dadf265da339b5001dd)  
- [🔥史上最全的Java并发系列之Java多线程（一）](https://juejin.im/post/5dfc9106518825126e63a711) 
- [🔥史上最全的Java并发系列之Java多线程（二）](https://juejin.im/post/5dfeeed6e51d45582248e4a5)   
- [🔥史上最全的Java并发系列之Java中的锁的使用和实现介绍（一）](https://juejin.im/post/5e002416e51d45583b43939d) 

## 重入锁
重入锁ReentrantLock，顾名思义，就是支持重进入的锁，它表示该锁能够支持一个线程对 资源的重复加锁。除此之外，该锁的还支持获取锁时的公平和非公平性选择。
我们回顾下TestLock的lock方法，在 tryAcquire(int acquires)方法时没有考虑占有锁的线程再次获取锁的场景，而在调用tryAcquire(int acquires)方法时返回了false，导致该线程被阻塞。

在绝对时间上，先对锁进行获取的请求一定先被满足，那么这个锁是公平的，反之，是不公平的。
事实上，公平的锁机制往往没有非公平的效率高，但是，并不是任何场景都是以TPS作为唯一的指标，公平锁能够减少“饥饿”发生的概率，等待越久的请求越是能够得到优先满足。

下面我们来分析下ReentrantLock 的实现：

- 实现重进入
    - 重进入是指任意线程在获取到锁之后能够再次获取该锁而不会被锁所阻塞，该特性的实现需要解决以下两个问题：

        - 线程再次获取锁
        - 锁的最终释放
        
下面是ReentrantLock通过组合自定义同步器来实现锁的获取与释放，以非公平性（默认的）实现：

```
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
        if (compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0) // overflow
            throw new Error("Maximum lock count exceeded");
        setState(nextc);
        return true;
    }
    return false;
}
```
此方法通过判断 当前线程是否为获取锁的线程 来决定获取操作是否成功，如果是获取锁的线程再次请求，则将同步状态值进行增加并返回true，表示获取同步状态成功。

公平与非公平获取锁的区别
>公平性与否是针对获取锁而言的，如果一个锁是公平的，那么锁的获取顺序就应该符合请求的绝对时间顺序，也就是 FIFO。

## 读写锁
之前提到锁（如TestLock和ReentrantLock）基本都是排他锁，这些锁在同一时刻只允许一个线程进行访问，而读写锁在同一时刻可以允许多个读线程访问，但是在写线程访问时，所有的读线程和其他写线程均被阻塞。
读写锁维护了一对锁，一个 读锁 和一个 写锁，通过分离读锁和写锁，使得并发性相比一般的排他锁有了很大提升。

一般情况下，读写锁 的性能都会比 排它锁 好，因为大多数场景 读是多于写 的。在读多于写的情况下，读写锁 能够提供比 排它锁 更好的 并发性 和 吞吐量。Java并发包提供读写锁的实现是ReentrantReadWriteLock 
- 公平性选择 ：支持公平和非公平的方式获取锁，吞吐量非公平优于公平。
- 重进入 ： 读锁在获取锁之后再获取读锁，写锁在获取锁之后再获取读锁和写锁。
- 锁降级 ：遵循获取写锁、获取读锁在释放写锁的次序，写锁能够降级为读锁。

读写锁的接口与示例
ReadWriteLock仅定义了获取读锁和写锁的两个方法，即readLock()方法和writeLock()方法，而其实现类ReentrantReadWriteLock，除了接口方法之外，还提供了一些便于外界监控其内部工作状态的方法，这些方法如下：

- getReadLockCount()：返回当前读锁获取的次数
- getReadHoldCount()：返回当前线程获取读锁的次数
- isWriteLocked()：判断写锁是否被获取
- getWriteHoldCount()：返回当前写锁被获取的次数

![](https://user-gold-cdn.xitu.io/2019/12/23/16f30e76df2ac178?w=1072&h=226&f=png&s=35701)

通过读写锁保证 非线程安全的HashMap的读写是线程安全的。

```
static Map<String, Object> map = new HashMap<>();
static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
static Lock r = rwl.readLock();
static Lock w = rwl.writeLock();
/**
 * 获取一个key对应的value
 */
public static final Object get(String key) {
    r.lock();
    try {
        return map.get(key);
    } finally {
        r.unlock();
    }
}
/**
 * 设置key对应的value，并返回旧的value
 */
public static final Object put(String key, Object value) {
    w.lock();
    try {
        return map.put(key, value);
    } finally {
        w.unlock();
    }
}
/**
 * 清空所有的内容
 */
public static final void clear() {
    w.lock();
    try {
        map.clear();
    } finally {
        w.unlock();
    }
}
```
### 锁降级
锁降级指的是 写锁降级成为读锁。
如果当前线程拥有写锁，然后将其释放，最后再获取读锁，这种分段完成的过程不能称之为锁降级。锁降级是指把持住（当前拥有的）写锁，再获取到读锁，随后释放（先前拥有的）写锁的过程。


```
//当数据发生变更后，update变量（布尔类型且volatile修饰）被设置为false
public void processData() {
    readLock.lock();
    if (!update) {
        // 必须先释放读锁
        readLock.unlock();
        // 锁降级从写锁获取到开始
        writeLock.lock();
        try {
            if (!update) {
                // 准备数据的流程（略）
                update = true;
            }
            readLock.lock();
        } finally {
            writeLock.unlock();
        }
        // 锁降级完成，写锁降级为读锁
    }
    try {
        // 使用数据的流程（略）
    } finally {
        readLock.unlock();
    }
}
```
上述示例中，当数据发生变更后，布尔类型且volatile修饰update变量被设置为false，此时所有访问processData()方法的线程都能够感知到变化，但只有一个线程能够获取到写锁，其他线程会被阻塞在读锁和写锁的lock()方法上。当前线程获取写锁完成数据准备之后，再获取读锁，随后释放写锁，完成锁降级。

#### 锁降级中读锁的获取是否必要呢？
答案是必要的。主要是为了 保证数据的可见性。
如果当前线程不获取读锁而是直接释放写锁，假设此刻另一个线程（记作线程T）获取了写锁并修改了数据，那么 当前线程无法感知线程T的数据更新。
如果当前线程获取读锁，即遵循锁降级的步骤，则线程T将会被阻塞，直到当前线程使用数据并释放读锁之后，线程T才能获取写锁进行数据更新。

RentrantReadWriteLock不支持锁升级。目的也是保证数据可见性，如果读锁已被多个线程获取，其中任意线程成功获取了写锁并更新了数据，则其更新对其他获取到读锁的线程是不可见的。

## LockSupport工具
当需要阻塞或唤醒一个线程的时候，都会使用LockSupport工具类来完成相应工作。
LockSupport定义了一组的公共静态方法，这些方法提供了最基本的 线程阻塞和唤醒功能，而LockSupport也成为构建同步组件的基础工具。

LockSupport提供的 阻塞和唤醒的方法 如下：

- park()：阻塞当前线程，只有调用 unpark(Thread thread)或者被中断之后才能从park()返回。
- parkNanos(long nanos)：再park()的基础上增加了超时返回。
- parkUntil(long deadline)：阻塞线程知道 deadline 对应的时间点。
- park(Object blocker)：Java 6时增加，blocker为当前线程在等待的对象。
- parkNanos(Object blocker, long nanos)：Java 6时增加，blocker为当前线程在等待的对象。
- parkUntil(Object blocker, long deadline)：Java 6时增加，blocker为当前线程在等待的对象。
- unpark(Thread thread)：唤醒处于阻塞状态的线程 thread。
有对象参数的阻塞方法在线程dump时，会有更多的现场信息

## Condition接口
任意一个Java对象，都拥有一组监视器方法，定义在java.lang.Object），主要包括wait()、wait(long timeout)、notify()以及notifyAll()方法，这些方法与synchronized同步关键字配合，可以实现等待/通知模式。

Condition接口也提供了类似Object的监视器方法，与Lock配合可以实现 等待/通知 模式，但是这两者在使用方式以及功能特性上还是有差别的。

以下是Object的监视器方法与Condition接口的对比：


![](https://user-gold-cdn.xitu.io/2019/12/23/16f30f2b445a3a19?w=692&h=467&f=png&s=53453)

Condition的使用方式比较简单，需要注意在调用方法前获取锁，如下：


```
Lock lock = new ReentrantLock();
Condition condition = lock.newCondition();
public void conditionWait() throws InterruptedException {
    lock.lock();
    try {
        condition.await();
    } finally {
        lock.unlock();
    }
}
public void conditionSignal() throws InterruptedException {
    lock.lock();
    try {
        condition.signal();
    } finally {
        lock.unlock();
    }
}
```
Condition 接口方法介绍：
- void await() throws InterruptedException ： 当前线程进入等待状态直到被通知或中断
- void awaitUninterruptibly() ：当前线程进入等待状态直到被通知，对中断不敏感
- long awaitNanos(long var1) throws InterruptedException ：当前线程进入等待状态直到被通知、中断或超时
- boolean await(long var1, TimeUnit var3) throws InterruptedException ：当前线程进入等待状态直到被通知、中断或超时
- boolean awaitUntil(Date var1) throws InterruptedException ：当前线程进入等待状态直到被通知、中断或到某一时间
- void signal() ：唤醒Condition上一个在等待的线程
- void signalAll() ：唤醒Condition上全部在等待的线程

**获取一个Condition必须通过Lock的newCondition()方法。**


Condition的实现分析

主要包括 等待队列、等待和通知。
- 等待队列
    - 等待队列是一个FIFO的队列，在队列中的每个节点都包含了一个线程引用，该线程就是在Condition对象上等待的线程，如果一个线程调用了Condition.await()方法，那么该线程将会释放锁、构造成节点加入等待队列并进入等待状态。同步队列和等待队列中节点类型都是同步器的静态内部类AbstractQueuedSynchronizer.Node。
    - 线程调用Condition.await()，即以当前线程构造节点，并加入等待队列的尾部。

![](https://user-gold-cdn.xitu.io/2019/12/23/16f30f962ec827f7?w=662&h=214&f=png&s=43570)


## 锁的知识点
- Lock接口提供的方法lock()、unlock()等获取和释放锁的介绍
- 队列同步器的使用 以及 自定义队列同步器
- 重入锁 的使用和实现介绍
- 读写锁 的 读锁 和 写锁
- LockSupport工具实现 阻塞和唤醒线程
- Condition接口实现 等待/通知模式




## 结尾

本章介绍了Java并发包中与锁相关的API和组件，通过示例讲述了这些API和组件的使用 方式以及需要注意的地方，并在此基础上详细地剖析了队列同步器、重入锁、读写锁以及 Condition等API和组件的实现细节，只有理解这些API和组件的实现细节才能够更加准确地运 用它们


> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！

