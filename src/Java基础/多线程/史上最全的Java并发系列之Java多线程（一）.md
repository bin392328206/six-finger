# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
今天我们讲并发编程的基础，也就是多线程的基础，没有多线程的话，就没有并发对吧，下面是前面的章节链接：  
- [🔥史上最全的Java并发系列之并发编程的挑战](https://juejin.im/post/5dfb1ca26fb9a0160b63827f)  
- [🔥史上最全的Java并发系列之Java并发机制的底层实现原理](https://juejin.im/post/5dfb3a27e51d4558181d35b0)    
- [🔥史上最全的Java并发系列之Java内存模型](https://juejin.im/post/5dfc3dadf265da339b5001dd)  
## 线程的简介
- 什么是线程
    >线程是操作系统能够进行运算调度的最小单位。它被包含在进程之中，是进程中的实际运作单位。一条线程指的是进程中一个单一顺序的控制流，一个进程中可以并发多个线程，每条线程并行执行不同的任务。

- 为什么要使用多线程
    >目前的处理器核心越来越多，使用多线程能有更快的响应时间，并能有更好的编程模型。

- 线程优先级
    >现代操作系统基本采用时分的形式调度运行的线程，操作系统分出每一个时间片会根据线程的优先级来分配，优先级越高的最先获取执行资源。

在Java线程中，通过一个整型成员变量priority来控制优先级，优先级的范围从1~10，在线程构建的时候可以通过setPriority(int)方法来修改优先级，默认优先级是5，优先级高的线程分配时间片的数量要多于优先级低的线程。

### 线程的状态
- NEW 初始状态
- RUNNABLE 运行状态
- BLOCKED 阻塞状态
- WAITING 等待状态
- TIME_WAITING 超时等待状态
- TERMINATED 终止状态
![](https://user-gold-cdn.xitu.io/2019/12/21/16f26ce52c37e554?w=856&h=583&f=png&s=175122)

## Thead类源码解读

Java所有多线程的实现，均通过封装Thread类实现，所以深入Thread类，对深入理解java多线程很有必要

### 继承关系 

![](https://user-gold-cdn.xitu.io/2019/12/21/16f26db2f94e8861?w=646&h=455&f=png&s=15483)

![](https://user-gold-cdn.xitu.io/2019/12/21/16f26dbd734d2d77?w=1521&h=590&f=png&s=153002)

### 主要属性

```

/*
 * 线程名字，通过构造参数来指定  
 */
private volatile String name;

/*
 * 表示线程的优先级,优先级越高，越优先被执行（最大值为10，最小值为1，默认值为5）
 */
private int  priority;

/*
 * 线程是否是守护线程:当所有非守护进程结束或死亡后，程序将停止
 */   
private boolean  daemon = false;

/*
 * 将要执行的任务
 */  
private Runnable target;

/*
 * 线程组表示一个线程的集合。此外，线程组也可以包含其他线程组。线程组构成一棵树，在树中，除了初始线程组外，每个线程组都有一个父线程组。
 */ 
private ThreadGroup group;

/*
 * Thread ID
 */ 
private long tid;

/*
 * 用来生成Thread ID使用
 */ 
private static long threadSeqNumber;

/*
 * 第几个线程，在init初始化线程的时候用来赋给thread.name
 */ 
private static int threadInitNumber


/*
 * 线程从创建到最终的消亡，要经历若干个状态。 
 * 一般来说，线程包括以下这几个状态：创建(new)、就绪(runnable)、运行(running)、阻塞(blocked)、time waiting、waiting、消亡（dead）
 */
private volatile int threadStatus = 0;
```
### 构造函数

```
//传入Runnable接口实现
Thread(Runnable target)
//传入Runnable接口实现,传入线程名
Thread(Runnable target, String name) 
//设置当前线程用户组
Thread(ThreadGroup group, Runnable target)
//设置用户组，传入线程名
Thread(ThreadGroup group, Runnable target, String name)
//设置用户组，传入线程名，设置当前线程栈大小
Thread(ThreadGroup group, Runnable target, String name, long stackSize) 
```
这边提一下创建线程的几种方式，大体来说有4种吧，我这边代码就不上了，不然就写的太长了
- 第一种，便是继续Thread类  ，重写run方法，其实这个run方法也是重写Runnable的，
- 第二种，便是去实现Runnable,也是一样
- 第三种是 通过FutureTask 来创建一个线程，这个的原理是啥呢？其实一样这个类的父类继承Runnable，这个比较特殊的一点是线程执行完毕之后，可以有返回值返回。
- 第四个就是通过线程池来创建一个线程（生产环境都这么用）


线程默认名称生产规则：

```
// 当前缺省线程名："Thread-" + nextThreadNum()
public Thread(Runnable target) {
        init(null, target, "Thread-" + nextThreadNum(), 0);
    }
---
// nextThreadNum 同步方法，线程安全，不会出现重复的threadInitNumber
private static int threadInitNumber;
private static synchronized int nextThreadNum() {
    return threadInitNumber++;
}

```
线程私有化实现，好好讲讲这个初始化方法


![](https://user-gold-cdn.xitu.io/2019/12/21/16f275f5c194998e?w=1694&h=525&f=png&s=149452)

```
 
/**
     * Initializes a Thread.
     *
     * @param g the Thread group
     * @param target the object whose run() method gets called
     * @param name the name of the new Thread
     * @param stackSize the desired stack size for the new thread, or
     *        zero to indicate that this parameter is to be ignored.
     * @param acc the AccessControlContext to inherit, or
     *            AccessController.getContext() if null
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc) {
		//线程必须设置名称，否则抛出空指针异常
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
 
        this.name = name.toCharArray();
 
		//父线程
        Thread parent = currentThread();
        SecurityManager security = System.getSecurityManager();
        //如果线程组为null
        if (g == null) {
            /* Determine if it's an applet or not */
            //如果有安全管理器，从安全管理器获得线程组
            /* If there is a security manager, ask the security manager
               what to do. */
            if (security != null) {
                g = security.getThreadGroup();
            }
            //如果安全管理器也不知道线程组，那么从父线程获取线程组，也就是线程必须归属于某个线程组
            /* If the security doesn't have a strong opinion of the matter
               use the parent thread group. */
            if (g == null) {
                g = parent.getThreadGroup();
            }
        }
 
        /* checkAccess regardless of whether or not threadgroup is
           explicitly passed in. */
        g.checkAccess();
 
        /*
         * Do we have the required permissions?
         //校验是否有安全校验权利，如果重写了getContextClassLoader和setContextClassLoader
         */
        if (security != null) {
            if (isCCLOverridden(getClass())) {
                security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
            }
        }
        //线程组添加未启动线程
        g.addUnstarted();
 
        this.group = g;
	  /* 设置当前线程是否为守护线程，默认是和当前类的ThreadGroup设置相
        * 同。如果是守护线程的话，当前线程结束会随着主线程的退出而退出。
        *jvm退出的标识是，当前系统没有活跃的非守护线程。
        */
        this.daemon = parent.isDaemon();
        this.priority = parent.getPriority();
        if (security == null || isCCLOverridden(parent.getClass()))
            this.contextClassLoader = parent.getContextClassLoader();
        else
            this.contextClassLoader = parent.contextClassLoader;
        this.inheritedAccessControlContext =
                acc != null ? acc : AccessController.getContext();
        this.target = target;
        setPriority(priority);
        if (parent.inheritableThreadLocals != null)
            this.inheritableThreadLocals =
                ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
        /*设置指定的栈大小，如果未指定大小，将在jvm 初始化参数中声明：Xss参数进行指定*/
        this.stackSize = stackSize;
        
        /* Set thread ID */
        tid = nextThreadID();
    }
```
### start方法

![](https://user-gold-cdn.xitu.io/2019/12/21/16f27673c1f8f21a?w=1714&h=397&f=png&s=118180)
从源码中得出：一个线程一旦已经被start了就不能再次执行start方法。被start过的线程，线程状态已经不是0了

```
/*  导致此线程开始执行; Java Virtual Machine调用此线程的run方法。
    结果是两个线程同时运行：当前线程（从调用返回到start方法）和另一个线程（执行其run方法）。
    不止一次启动线程永远不合法。
    特别是，一旦完成执行，线程可能无法重新启动。
    @exception IllegalThreadStateException如果线程已经启动。
    @see #run（）
    @see #stop（）*/

 public synchronized void start() {
        /**
         * This method is not invoked for the main method thread or "system"
         * group threads created/set up by the VM. Any new functionality added
         * to this method in the future may have to also be added to the VM.
         *
         * A zero status value corresponds to state "NEW".
         */
        //此判断当前线程只能被启动一次，不能被重复启动
        if (threadStatus != 0)
            throw new IllegalThreadStateException();

        /* Notify the group that this thread is about to be started
         * so that it can be added to the group's list of threads
         * and the group's unstarted count can be decremented. */
        /*通知组该线程即将启动
          *这样它就可以添加到组的线程列表中
         *并且该组的未启动计数可以递减。*/
        group.add(this);

        boolean started = false;
        try {
            start0();
            started = true;
        } finally {
            try {
                // 如果线程启动失败，从线程组里面移除该线程
                if (!started) {
                    group.threadStartFailed(this);
                }
            } catch (Throwable ignore) {
                /* do nothing. If start0 threw a Throwable then
                  it will be passed up the call stack */
            }
        }
    }
```
其实，这个源码也简单，就是启动线程 然后让那个组里面待启动的线程减1，如果失败，就把他加入到失败的数组里面。
### join() 方法源码分析：
让父线程等待子线程结束之后才能继续运行。

```
/**
     * Waits at most {@code millis} milliseconds for this thread to
     * die. A timeout of {@code 0} means to wait forever.
     *
     * <p> This implementation uses a loop of {@code this.wait} calls
     * conditioned on {@code this.isAlive}. As a thread terminates the
     * {@code this.notifyAll} method is invoked. It is recommended that
     * applications not use {@code wait}, {@code notify}, or
     * {@code notifyAll} on {@code Thread} instances.
     *
     * @param  millis
     *         the time to wait in milliseconds
     *
     * @throws  IllegalArgumentException
     *          if the value of {@code millis} is negative
     *
     * @throws  InterruptedException
     *          if any thread has interrupted the current thread. The
     *          <i>interrupted status</i> of the current thread is
     *          cleared when this exception is thrown.
     */
    public final synchronized void join(long millis) throws InterruptedException {
        long base = System.currentTimeMillis();
        long now = 0;

        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (millis == 0) {
        // 如果millis == 0 线程将一直等待下去
            while (isAlive()) {
                wait(0);
            }
        } else {
            // 指定了millis ，等待指定时间以后，会break当前线程
            while (isAlive()) {
                long delay = millis - now;
                if (delay <= 0) {
                    break;
                }
                wait(delay);
                now = System.currentTimeMillis() - base;
            }
        }
    }
```

![](https://user-gold-cdn.xitu.io/2019/12/21/16f27701cd0f0e29?w=1366&h=382&f=png&s=96903)
其实这个也简单，随便说说，首先Join方法呢？有几个重载的方法，如果你不传参数，那么就表示你的超时时间是永不过期，只要调用join方法的线程不执行完成，当前线程就会一直等待，其底层实现还是Java object 的wait方法

注意点：

- join方法是通过wait方法实现的，所以会释放对象锁。

- 调用方线程状态为WAITING。

- join需要在start之后调用

### interrupt方法
线程中断在之前的版本有stop方法，但是被设置过时了。现在已经没有强制线程终止的方法了！

由于stop方法可以让一个线程A终止掉另一个线程B
- 被终止的线程B会立即释放锁，这可能会让对象处于不一致的状态。
- 线程A也不知道线程B什么时候能够被终止掉，万一线程B还处理运行计算阶段，线程A调用stop方法将线程B终止，那就很无辜了~

总而言之，Stop方法太暴力了，不安全，所以被设置过时了。

我们一般使用的是interrupt来请求终止线程~

- 要注意的是：interrupt不会真正停止一个线程，它仅仅是给这个线程发了一个信号告诉它，它应该要结束了(明白这一点非常重要！)
- 也就是说：Java设计者实际上是想线程自己来终止，通过上面的信号，就可以判断处理什么业务了。
- 具体到底中断还是继续运行，应该由被通知的线程自己处理

```
Thread t1 = new Thread( new Runnable(){
    public void run(){
        // 若未发生中断，就正常执行任务
        while(!Thread.currentThread.isInterrupted()){
            // 正常任务代码……
        }
        // 中断的处理代码……
        doSomething();
    }
} ).start();
```
再次说明：调用interrupt()并不是要真正终止掉当前线程，仅仅是设置了一个中断标志。这个中断标志可以给我们用来判断什么时候该干什么活！什么时候中断由我们自己来决定，这样就可以安全地终止线程了！


```
//除非中断自己，checkAccess都将被调用
    /**
     * Interrupts this thread.
     *
     * <p> Unless the current thread is interrupting itself, which is
     * always permitted, the {@link #checkAccess() checkAccess} method
     * of this thread is invoked, which may cause a {@link
     * SecurityException} to be thrown.
     *
	 //如果一个线程阻塞在wat方法，或者线程的join方法，再或者sleep方法上，
	 //线程的中断状态被清空设置为false，并且被interrupt的线程将收到一个中断异常。
     * <p> If this thread is blocked in an invocation of the {@link
     * Object#wait() wait()}, {@link Object#wait(long) wait(long)}, or {@link
     * Object#wait(long, int) wait(long, int)} methods of the {@link Object}
     * class, or of the {@link #join()}, {@link #join(long)}, {@link
     * #join(long, int)}, {@link #sleep(long)}, or {@link #sleep(long, int)},
     * methods of this class, then its interrupt status will be cleared and it
     * will receive an {@link InterruptedException}.
     *
	 //如果线程阻塞在IO操作，channel将被关闭，并且线程的中断状态会被设置为true，
	 //并且被interrupt的线程将收到一个ClosedByInterruptException异常。
     * <p> If this thread is blocked in an I/O operation upon an {@link
     * java.nio.channels.InterruptibleChannel InterruptibleChannel}
     * then the channel will be closed, the thread's interrupt
     * status will be set, and the thread will receive a {@link
     * java.nio.channels.ClosedByInterruptException}.
     *
	 //如果线程阻塞在selector方法，中断线程的中断状态将设置为true，并且从select操作立即返回，
	 //只有selector的wakeup方法被调用可能返回一个非0值。
     * <p> If this thread is blocked in a {@link java.nio.channels.Selector}
     * then the thread's interrupt status will be set and it will return
     * immediately from the selection operation, possibly with a non-zero
     * value, just as if the selector's {@link
     * java.nio.channels.Selector#wakeup wakeup} method were invoked.
     *//如果不是上面说的几种情况，线程的中断状态将被设置。
     * <p> If none of the previous conditions hold then this thread's interrupt
     * status will be set. </p>
     *
     * <p> Interrupting a thread that is not alive need not have any effect.
     *
     * @throws  SecurityException
     *          if the current thread cannot modify this thread
     *
     * @revised 6.0
     * @spec JSR-51
     */
    public void interrupt() {
        if (this != Thread.currentThread())
            checkAccess();
        synchronized (blockerLock) {
            Interruptible b = blocker;
            if (b != null) {
                interrupt0();           // Just to set the interrupt flag
                b.interrupt(this);
                return;
            }
        }
        interrupt0();
    }
```
interrupt线程中断还有另外两个方法(检查该线程是否被中断)：

- 静态方法interrupted()-->会清除中断标志位
- 实例方法isInterrupted()-->不会清除中断标志位
### sleep方法(long millis)

```
public static native void sleep(long millis) throws InterruptedException;

```
注意点：

- sleep是指线程被调用时，占着CPU不工作，形象地说明为“占着CPU睡觉”，此时，系统的CPU部分资源被占用，其他线程无法进入。

- sleep方法不会释放锁。

### yield()方法

```
public static native void yield();
```

注意点：

- 调用yield方法会让当前线程交出CPU权限，让拥有相同优先级的线程有获取CPU执行时间的机会
- 调用yield方法并不会让线程进入BLOCKED状态，而是让线程重回RUNNABLE状态， 但不能保证迅速转换。（自己也可以再次竞争的）
- 不会释放锁

## 结尾

第四章的第一小节讲完了，讲的是Thread,不是很深，跟着学还是可以的，好了，今天就到这里了

> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
