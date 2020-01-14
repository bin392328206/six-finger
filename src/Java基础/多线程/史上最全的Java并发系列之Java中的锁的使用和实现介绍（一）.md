# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
多线程基础讲完了，接下开就是锁了，讲完这两个Java并发的基础就差不多了  
- [🔥史上最全的Java并发系列之并发编程的挑战](https://juejin.im/post/5dfb1ca26fb9a0160b63827f)  
- [🔥史上最全的Java并发系列之Java并发机制的底层实现原理](https://juejin.im/post/5dfb3a27e51d4558181d35b0)    
- [🔥史上最全的Java并发系列之Java内存模型](https://juejin.im/post/5dfc3dadf265da339b5001dd)  
- [🔥史上最全的Java并发系列之Java多线程（一）](https://juejin.im/post/5dfc9106518825126e63a711) 
- [🔥史上最全的Java并发系列之Java多线程（二）](https://juejin.im/post/5dfeeed6e51d45582248e4a5)


## 锁介绍
锁是用来控制多个线程访问共享资源的方式，一般来说，一个锁能够防止多个线程同时访问共享资源。

### Lock接口
在Java SE 5之后，并发包中新增了Lock接口（以及相关实现类）用来实现锁功能，它提供了与synchronized关键字类似的同步功能，只是在使用时需要 显式 地获取和释放锁。
虽然它缺少了（通过synchronized块或者方法所提供的）隐式获取释放锁的便捷性，但是却拥有了锁获取与释放的 可操作性、可中断的获取锁 以及 超时获取锁 等多种synchronized关键字所不具备的同步特性。

使用synchronized关键字将会 隐式 地获取锁，但是它将锁的获取和释放固化了，也就是先获取再释放。

Lock的使用：

```
Lock lock = new ReentrantLock();
lock.lock();
try {
} finally {
    lock.unlock();
}
```
注意 ：
1. 在finally块中释放锁，目的是保证在获取到锁之后，最终能够被释放。
2. 不要将获取锁的过程写在try块中，因为如果在获取锁（自定义锁的实现）时发生了异常，异常抛出的同时，也会导致锁无故释放。

Lock接口提供的synchronized关键字所不具备的主要特性：

- 尝试非阻塞性获取锁： 当前线程尝试获取锁，如果此时没有其他线程占用此锁，则成功获取到锁。
- 能被中断的获取锁： 当获取到锁的线程被中断时，中断异常会抛出并且会释放锁。
- 超时获取锁： 在指定时间内获取锁，如果超过时间还没获取，则返回。

Lock 相关的API：
![](https://user-gold-cdn.xitu.io/2019/12/23/16f309aebd642ba2?w=1625&h=350&f=png&s=65909)
- void lock();：获取锁，获取之后返回
- void lockInterruptibly() throws InterruptedException;：可中断的获取锁
- boolean tryLock();：尝试非阻塞的获取锁
- boolean tryLock(long time, TimeUnit unit) throws InterruptedException;： 超时获取锁。 超时时间结束，未获得锁，返回false.
- void unlock();：释放锁
- Condition newCondition();：获取等待通知组件，改组件和锁绑定，当前线程获取到锁才能调用wait()方法，调用之后则会释放锁。

### 队列同步器
队列同步器AbstractQueuedSynchronizer（以下简称同步器），是用来构建锁或者其他同步组件的基础框架，它使用了一个int 成员变量表示同步状态，通过内置的FIFO队列来完成资源获取线程的排队工作，并发包的作者Doug Lea期望它能够成为实现大部分同步需求的基础。

同步器的主要使用方式是继承AbstractQueuedSynchronizer，通过同步器提供的3个方法getState()、setState(int newState)和compareAndSetState(int expect,int update)来进行线程安全的状态同步。

同步器是实现锁的关键，在锁的实现中聚合同步器，利用同步器实现锁的语义。
可以这样理解二者之间的关系：

- 锁是面向使用者的，它定义了使用者与锁交互的接口，隐藏了实现细节；
- 同步器面向的是锁的实现者，它简化了锁的实现方式，屏蔽了同步状态管理、线程的排队、等待与唤醒等底层操作。


同步器的设计是基于模板方法模式的，也就是说，使用者需要继承同步器并重写指定的 方法

队列同步器的接口与示例:AbstractQueuedSynchronizer可重写的方法
- boolean tryAcquire(int arg)：独占式获取同步状态。
- boolean tryRelease(int arg)：独占式释放同步状态。
- int tryAcquireShared(int arg)：共享式获取同步状态。
- boolean tryReleaseShared(int arg)：共享释放取同步状态。
- boolean isHeldExclusively()：当前同步器是否在独占式模式下被线程占用。

实现自定义同步组件时，将会调用同步器提供 独占式获取与释放同步状态、共享式获取与释放同步状态 和 查询同步队列中的等待线程情况 三类模板方法。

独占锁的示例代码：

```
public class TestLock implements Lock {
    private TestQueuedSync sync;
    /**
     * 获取锁
     */
    @Override
    public void lock() {
        sync.acquire(1);
    }
    /**
     * 可中断的获取锁
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }
    /**
     * 尝试非阻塞式获取锁
     */
    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }
    /**
     * 尝试非阻塞式获取锁
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquire(1);
    }
    /**
     * 释放锁
     */
    @Override
    public void unlock() {
        sync.release(1);
    }
    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
    /**
     * 是否有同步队列线程
     */
    public boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }
    /**
     * 锁是否被占用
     */
    public boolean isLock() {
        return sync.isHeldExclusively();
    }
    private static class TestQueuedSync extends AbstractQueuedSynchronizer {
        /**
         * 独占式获取同步状态
         */
        @Override
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }
        /**
         * 独占式释放同步状态
         */
        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == 0) {
                throw new IllegalStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }
        /**
         * 同步状态是否被占用
         */
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }
        /**
         * 返回一个Condition，每个condition都包含了一个condition队列
         */
        Condition newCondition() {
            return new ConditionObject();
        }
    }
}
```

上述示例代码中，独占锁TestLock是一个自定义同步组件，它在同一时刻只允许一个线程占有锁。TestLock中定义了一个静态内部类TestQueuedSync继承了同步器，在tryAcquire(int acquires)方法中，如果经过compareAndSetState设置成功，则代表获取了同步状态1，而在tryRelease(int releases)方法中只是将同步状态重置为0。

用户使用TestLock时并不会直接和内部同步器的实现TestQueuedSync打交道，而是调用TestLock提供的方法，在TestLock的实现中，以获取锁的lock()方法为例，只需要在方法实现中调用同步器的模板方法acquire(int args)即可，当前线程调用该方法获取同步状态失败后会被加入到同步队列中等待，这样就大大降低了实现一个可靠自定义同步组件的门槛。

### 队列同步器的实现分析
接下来将从实现角度分析同步器是如何完成线程同步的：

同步队列 ： 一个FIFO双向队列。
当前线程获取同步状态失败时，同步器会将当前线程以及等待状态等信息构造成为一个节点Node并将其加入同步队列，同时会阻塞当前线程，当同步状态释放时，会把首节点中的线程唤醒，使其再次尝试获取同步状态。
Node 保存 获取同步状态失败的线程引用、等待状态 以及 前驱和后继节点，节点的属性类型 与 名称 以及 描述 如下:

来看下存放的Node

```
/**
 * 等待状态：
 *  CANCELLED : 1 在同步队列中等待超时或被中断，需要从队列中取消等待，在该状态将不会变化
 *  SIGNAL : -1  后继节点地线程处于等待状态，当前节点释放获取取消同步状态，后继节点地线程即开始运行
 *  CONDITION : -2  在等待队列中，
 *  PROPAGATE : -3 下一次共享式同步状态获取将会无条件地被传播下去
 *  INITAL : 0  初始状态
 */
volatile int waitStatus;
volatile Node prev;//前驱节点
volatile Node next;//后继节点
volatile Thread thread;//获取同步状态的线程
Node nextWaiter;//等待队列中的后继节点。 如果节点是共享的的，这个字段将是一个SHARED常量
```

![](https://user-gold-cdn.xitu.io/2019/12/23/16f30ad561b5f965?w=723&h=225&f=png&s=60500)
如上图所示，同步器包含了两个节点类型的引用，一个指向 头节点，而另一个指向 尾节点。
试想一下，当一个线程成功地获取了同步状态（或者锁），其他线程将无法获取到同步状态，转而被构造成为节点并加入到同步队列中，而这个加入队列的过程必须要保证线程安全，因此同步器提供了一个基于CAS的设置尾节点的方法：compareAndSetTail(Node expect,Node update)，它需要传递当前线程“认为”的尾节点和当前节点，只有设置成功后，当前节点才正式与之前的尾节点建立关联。


#### 独占式同步状态获取与释放
通过调用同步器的acquire(int arg)方法可以获取同步状态，该方法对中断不敏感，也就是由于线程获取同步状态失败后进入同步队列中，后续对线程进行中断操作时，线程不会从同步队列中移出。acquire(int arg)代码如下：

![](https://user-gold-cdn.xitu.io/2019/12/23/16f30b3ba72204dd?w=1167&h=224&f=png&s=58226)

```
public final void acquire(int arg) {
    if (!tryAcquire(arg) &&
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        selfInterrupt();
}
```
上述代码主要完成了 同步状态获取、节点构造、加入同步队列 以及 在同步队列中自旋等待。

- 首先调用自定义同步器实现的tryAcquire(int arg)方法保证线程安全的获取同步状态
如果获取同步状态失败，构造同步节点（独占式Node.EXCLUSIVE，同一时刻只能有一个线程成功获取同步状态）并通过addWaiter(Node node)方法将该节点加入到同步队列的尾部。
- 最后调用acquireQueued(Node node,int arg)方法，使得该节点以“死循环”的方式获取同步状态
如果获取不到则阻塞节点中的线程，而 被阻塞线程的唤醒 主要依靠 前驱节点的出队或 阻塞线程被中断 来实现。


我们来看下节点的构造以及加入同步队列的addWaiter(Node mode)和initializeSyncQueue()方法：
```
private Node addWaiter(Node mode) {
    Node node = new Node(mode);
    for (;;) {
        Node oldTail = tail;
        if (oldTail != null) {
            U.putObject(node, Node.PREV, oldTail);
            if (compareAndSetTail(oldTail, node)) {
                oldTail.next = node;
                return node;
            }
        } else {
            initializeSyncQueue();
        }
    }
}
private final void initializeSyncQueue() {
    Node h;
    if (U.compareAndSwapObject(this, HEAD, null, (h = new Node())))
        tail = h;
}
```
上述代码通过在“死循环”中使用compareAndSetTail(Node expect,Node update)方法来确保节点能够被线程安全添加。 如果没有尾节点的话，则构建一个新的同步队列。

接下来看下acquireQueued(final Node node, int arg)方法：

```
final boolean acquireQueued(final Node node, int arg) {
    try {
        boolean interrupted = false;
        for (;;) {
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } catch (Throwable t) {
        cancelAcquire(node);
        throw t;
    }
}
```
在acquireQueued(final Node node,int arg)方法中，当前线程在“死循环”中尝试获取同步状态，而只有前驱节点是头节点才能够尝试获取同步状态，这是为什么？原因有两个:
- 头节点是成功获取到同步状态的节点，而头节点的线程释放了同步状态之后，将会唤醒其后继节点，后继节点的线程被唤醒后需要检查自己的前驱节点是否是头节点。
- 维护同步队列的FIFO原则。

![](https://user-gold-cdn.xitu.io/2019/12/23/16f30bc4a2671d95?w=644&h=718&f=png&s=160839)

### 自定义同步组件
- 在同一时刻，只允许至多两个线程同时访问，超过两个线程的访问将被阻塞。
- 能够在同一时刻支持多个线程的访问(共享式访问)。

```
package com.atguigu.ct.producer.Test.BB;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CustomLock implements Lock  {
    private CustomSyncQueue customSyncQueue = new CustomSyncQueue(2);
    public static void main(String[] args) {
        final Lock lock = new CustomLock();
        class Worker extends Thread  {
            @Override
            public void run() {
                while (true) {
                    lock.lock();
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        System.out.println(Thread.currentThread().getName());
                        TimeUnit.SECONDS.sleep(1);
                    }
                    catch (Exception e ){
                        
                    }
                    finally {
                        lock.unlock();
                    }
                }
            }
        }
        // 启动10个线程
        for (int i = 0; i < 10; i++) {
            Worker w = new Worker();
            w.setDaemon(true);
            w.start();
        }
        // 每隔1秒换行
        for (int i = 0; i < 10; i++) {
            System.out.println();
        }
    }
    @Override
    public void lock() {
        customSyncQueue.tryAcquireShared(1);
    }
    @Override
    public void unlock() {
        customSyncQueue.tryReleaseShared(1);
    }
    public static class CustomSyncQueue extends AbstractQueuedSynchronizer {
        public CustomSyncQueue(int count) {
            if (count <= 0) {
                throw new IllegalStateException("count must >= 0");
            }
            setState(count);
        }
        @Override
        protected int tryAcquireShared(int reduceCount) {
            for (; ; ) {
                int current = getState();
                int newCount = current - reduceCount;
                if (newCount < 0 || compareAndSetState(current, newCount)) {
                    return newCount;
                }
            }
        }
        @Override
        protected boolean tryReleaseShared(int returnCount) {
            for (; ; ) {
                int current = getState();
                int newCount = current + returnCount;
                if (compareAndSetState(current, newCount)) {
                    return true;
                }
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}

```
在上述示例中，CustomLock实现了Lock接口，提供了面向使用者的接口，使用者调用lock() 方法获取锁，随后调用unlock()方法释放锁，而同一时刻只能有两个线程同时获取到锁。 TwinsLock同时包含了一个自定义同步器Sync，而该同步器面向线程访问和同步状态控制。以 共享式获取同步状态为例：同步器会先计算出获取后的同步状态，然后通过CAS确保状态的正 确设置，当tryAcquireShared(int reduceCount)方法返回值大于等于0时，当前线程才获取同步状 态，对于上层的TwinsLock而言，则表示当前线程获得了锁。

同步器作为一个桥梁，连接线程访问以及同步状态控制等底层技术与不同并发组件（比如 Lock、CountDownLatch等）的接口语义

## 结尾

简直就是枯燥，哈哈，估计很多人看到很懵逼，在这我自己也是浅尝辄止，给大家总结一下今天说的啥吧：
- 第一个就是Lock接口，他是一个锁的接口，要自定义锁，就要实现它，它是面向我们开发使用的。
- 第二个就是我们的队列同步器，他是锁的具体实现，是一个锁的底层所在，通过去继承 AbstractQueuedSynchronizer 这个类，可以自定义一个锁，比如说独占锁，或者是几个线程同时拿到锁，都是自己去实现的，虽然有点枯燥，但是这个还是很有用的。

> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
