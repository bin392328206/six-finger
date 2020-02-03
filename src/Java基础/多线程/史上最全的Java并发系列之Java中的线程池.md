# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
不知道说啥，继续干，这本书快干完了   
- [🔥史上最全的Java并发系列之并发编程的挑战](https://juejin.im/post/5dfb1ca26fb9a0160b63827f)  
- [🔥史上最全的Java并发系列之Java并发机制的底层实现原理](https://juejin.im/post/5dfb3a27e51d4558181d35b0)    
- [🔥史上最全的Java并发系列之Java内存模型](https://juejin.im/post/5dfc3dadf265da339b5001dd)  
- [🔥史上最全的Java并发系列之Java多线程（一）](https://juejin.im/post/5dfc9106518825126e63a711) 
- [🔥史上最全的Java并发系列之Java多线程（二）](https://juejin.im/post/5dfeeed6e51d45582248e4a5)   
- [🔥史上最全的Java并发系列之Java中的锁的使用和实现介绍（一）](https://juejin.im/post/5e002416e51d45583b43939d) 
- [🔥史上最全的Java并发系列之Java中的锁的使用和实现介绍（二）](https://juejin.im/post/5e0037a651882512670ee0b5)  
- [🔥史上最全的Java并发系列之Java并发容器和框架](https://juejin.im/post/5e005f746fb9a016253c15d5)   
- [🔥史上最全的Java并发系列之Java中的13个原子操作类](https://juejin.im/post/5e006bf6518825126e63a9fb)    
- [🔥史上最全的Java并发系列之Java中的并发工具类](https://juejin.im/post/5e0076a45188251247688749)  

## 前言
> Java中的线程池是运用场景最多的并发框架，几乎所有需要异步或并发执行任务的程序都可以使用线程池。
在开发过程中，合理地使用线程池能够带来3个好处。

- 降低资源消耗。通过重复利用已创建的线程降低线程创建和销毁造成的消耗。
- 提高响应速度。当任务到达时，任务可以不需要等到线程创建就能立即执行。
- 提高线程的可管理性。线程是稀缺资源，如果无限制地创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行统- 一分配、调优和监控。但是，要做到合理利用线程池，必须对其实现原理了如指掌。

### 线程池的实现原理

当向线程池提交一个任务之后，线程池是如何处理这个任务的呢？
本文来看一下线程池的主要处理流程，处理流程图下图所示。

![](https://user-gold-cdn.xitu.io/2019/12/23/16f32acb56f37396?w=708&h=390&f=png&s=62644)
从图中可以看出，当提交一个新任务到线程池时，线程池的处理流程如下。

1. 线程池判断核心线程池里的线程是否都在执行任务。如果不是，则创建一个新的工作线程来执行任务。如果核心线程池里的线程都在执行任务，则进入下个流程。
2. 线程池判断工作队列是否已经满。如果工作队列没有满，则将新提交的任务存储在这个工作队列里。如果工作队列满了，则进入下个流程。
3. 线程池判断线程池的线程是否都处于工作状态。如果没有，则创建一个新的工作线程来执行任务。如果已经满了，则交给饱和策略来处理这个任务。

ThreadPoolExecutor 执行 execute() 方法的示意图如下：

![](https://user-gold-cdn.xitu.io/2019/12/23/16f32af6e747866f?w=694&h=608&f=png&s=93748)

ThreadPoolExecutor执行execute方法分下面4种情况：

- 如果当前运行的线程少于corePoolSize，则创建新线程来执行任务（注意，执行这一步骤需要获取全局锁）。上图1
- 如果运行的线程等于或多于corePoolSize，则将任务加入BlockingQueue。上图2
- 如果无法将任务加入BlockingQueue（队列已满），则创建新的线程来处理任务（注意，执行这一步骤需要获取全局锁）。上图3
- 如果创建新线程将使当前运行的线程超出maximumPoolSize，任务将被拒绝，并调用RejectedExecutionHandler.rejectedExecution()方法。上图4

ThreadPoolExecutor采取上述步骤的总体设计思路，是为了在执行execute()方法时，尽可能地避免获取全局锁（那将会是一个严重的可伸缩瓶颈）。在ThreadPoolExecutor完成预热之后（当前运行的线程数大于等于corePoolSize），几乎所有的execute()方法调用都是执行 上图2 ，而 上图2 不需要获取全局锁。

### 源码分析
上面的流程分析让我们很直观地了解了线程池的工作原理，让我们再通过源代码来看看是如何实现的，线程池执行任务的方法如下：

```
public void execute(Runnable command) {
    if (command == null)
        throw new NullPointerException();

    int c = ctl.get();
    if (workerCountOf(c) < corePoolSize) {
        // 如果线程数小于基本线程数，则创建线程并执行当前任务
        if (addWorker(command, true))
            return;
        c = ctl.get();
    }

    // 如线程数大于等于基本线程数或线程创建失败，则将当前任务放到工作队列中。
    if (isRunning(c) && workQueue.offer(command)) {
        int recheck = ctl.get();
        if (!isRunning(recheck) && remove(command))
            reject(command);
        else if (workerCountOf(recheck) == 0)
            addWorker(null, false);
    } else if (!addWorker(command, false))
        // 如果线程池不处于运行中或任务无法放入队列，
        //并且当前线程数量小于最大允许的线程数量,则创建一个线程执行任务.

        // 抛出RejectedExecutionException异常
        reject(command);
}
```

工作线程：线程池创建线程时，会将线程封装成工作线程Worker，Worker在执行完任务后，还会循环获取工作队列里的任务来执行。我们可以从Worker类的run()方法里看到这点

```
public void run() {
    try {
        Runnable task = firstTask;
        firstTask = null;
        while (task != null || (task = getTask()) != null) {
            runTask(task);
            task = null;
        }
    } finally {
        workerDone(this);
    }
}
```
说明线程池中的线程都是运行状态 

ThreadPoolExecutor中线程执行任务的示意图如下：

![](https://user-gold-cdn.xitu.io/2019/12/23/16f32b60ea5efd72?w=687&h=474&f=png&s=61253)

线程池中的线程执行任务分两种情况：

- 在execute()方法中创建一个线程时，会让这个线程执行当前任务。
- 这个线程执行完上图中1的任务后，会反复从BlockingQueue获取任务来执行。

## 线程池的源码（ThreadPoolExecutor）
我就讲讲Java的ThreadPoolExecutor吧，Spring的ThreadPoolTaskExecutor底层也是Java的ThreadPoolExecutor

### 继承结构

![](https://user-gold-cdn.xitu.io/2019/12/24/16f35f45d8ccbdbb?w=428&h=492&f=png&s=18343)
- Executor接口只有一个方法execute,传入线程任务参数
- ExecutorService接口继承Executor接口，并增加了submit、shutdown、invokeAll等等一系列方法。
- AbstractExecutorService抽象类实现ExecutorService接口，并且提供了一些方法的默认实现，例如submit方法、invokeAny方法、invokeAll方法。
像execute方法、线程池的关闭方法（shutdown、shutdownNow等等）就没有提供默认的实现。
- ThreadPoolExecutor 一个最下面的底层实现，我们具体就来看看它


### 基本属性

```
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;
 
    // runState is stored in the high-order bits
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;
 
    // Packing and unpacking ctl
    private static int runStateOf(int c)     { return c & ~CAPACITY; }   // 获取线程池运行状态
    private static int workerCountOf(int c)  { return c & CAPACITY; }    // 获取线程数量
    private static int ctlOf(int rs, int wc) { return rs | wc; }    

```
ctl是线程池的控制状态，是AtomicInteger类型的，里面包含两部分，workcount---线程的数量，runState---线程池的运行状态。这里限制了最大线程数是2^29-1，大约500百万个线程，这也是个问题，所以ctl也可以变成AtomicLong类型的。

线程池的五种状态：

- RUNNING - 接受新任务并且继续处理阻塞队列中的任务
- SHUTDOWN - 不接受新任务但是会继续处理阻塞队列中的任务
- STOP -  不接受新任务，不在执行阻塞队列中的任务，中断正在执行的任务
- TIDYING - 所有任务都已经完成，线程数都被回收，线程会转到TIDYING状态会继续执行钩子方法
- TERMINATED - 钩子方法执行完毕

线程之间的转换：

- RUNNING -> SHUTDOWN
    -    显式调用shutdown()方法, 或者隐式调用了finalize()方法
- (RUNNING or SHUTDOWN) -> STOP
    -    显式调用shutdownNow()方法
- SHUTDOWN -> TIDYING
    -    当线程池和任务队列都为空的时候
- STOP -> TIDYING
    -    当线程池为空的时候
- TIDYING -> TERMINATED
    -    当 terminated() hook 方法执行完成时候
### 构造函数

![](https://user-gold-cdn.xitu.io/2019/12/24/16f36a4daba686f0?w=1211&h=238&f=png&s=61269)
4个构造函数，最后都是调用一个方法

```
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler) {
}
```
参数介绍


|参数|类型|含义|
|:-:|:-:|:-:|
|corePoolSize|	int|	核心线程数
|maximumPoolSize |int|	最大线程数
|keepAliveTime	|long|	存活时间
|unit|	TimeUnit|	时间单位|
|workQueue|	BlockingQueue|	存放线程的队列|
|threadFactory|	ThreadFactory|	创建线程的工厂|
|handler|RejectedExecutionHandler|	多余的的线程处理器（拒绝策略）|

- corePoolSize：核心线程数，即最小的keep alive线程数，如果allowCoreThreadTimeOut设置为true，则该参数无效，即为0；
- maximumPoolSize：最大线程数，即定义了线程池的最大线程数（实际最大值不能超过CAPACITY）；
- keepAliveTime：空闲时间，即线程的最大空闲时间，默认情况是当线程池中的线程数超过corePoolSize时，线程的最大空闲时间，及线程数小于corePoolSize时不生效，除非allowCoreThreadTimeOut设置为true；
- workQueue：任务队列，为阻塞队列，保存需要执行的任务。
- threadFactory： 创建线程的工厂类。
- handler： 当queue满了和线程数达到最大限制，对于继续到达的任务采取的策略。默认采取AbortPolicy ，                              也就是拒绝策略。

### 拒绝策略
 1.  AbortPolicy


```

     /**
     *  默认的拒绝策略,当继续有任务到来时直接抛出异常
     */
    public static class AbortPolicy implements RejectedExecutionHandler {
      
        public AbortPolicy() { }
 
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RejectedExecutionException("Task " + r.toString() +
                                                 " rejected from " +
                                                 e.toString());
        }
    }

```
 2.  DiscardPolicy：rejectedexecution是个空方法，意味着直接抛弃该任务，不处理。
 
```
 public static class DiscardPolicy implements RejectedExecutionHandler {
       
        public DiscardPolicy() { }
 
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        }
    
```
 3.  DiscardOldestPolicy：抛弃queue中的第一个任务，再次执行该任务。
 
```
public static class DiscardOldestPolicy implements RejectedExecutionHandler {
        
        public DiscardOldestPolicy() { }
 
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
    }

```
 4. CallerRunsPolicy： 直接由执行该方法的线程继续执行该任务，除非调用了shutdown方法，这个任务才会被丢弃，否则继续执行该任务会发生阻塞。
 
```
 public static class CallerRunsPolicy implements RejectedExecutionHandler {
       
        public CallerRunsPolicy() { }
 
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                r.run();
            }
        }
    }

```


### workQueue
用于保存等待执行的任务的阻塞队列。
可以选择以下几个阻塞队列：
- rrayBlockingQueue：是一个基于数组结构的有界阻塞队列，此队列按FIFO（先进先出）原则对元素进行排序。
- LinkedBlockingQueue：一个基于链表结构的阻塞队列，此队列按FIFO排序元素，吞吐量通常要高于ArrayBlockingQueue。静态工厂- 方法Executors.newFixedThreadPool()使用了这个队列。
- SynchronousQueue：一个不存储元素的阻塞队列。每个插入操作必须等到另一个线程调用移除操作，否则插入操作一直处于阻塞状态- ，吞吐量通常要高于LinkedBlockingQueue，静态工厂方法Executors.newCachedThreadPool使用了这个队列。
- PriorityBlockingQueue：一个具有优先级的无限阻塞队列。




### execute方法

```
 public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
        /*
         *  3步操作
         *
         * 1. 如果当前运行的线程数<核心线程数,创建一个新的线程执行任务,调用addWorker方法原子性地检查
         *    运行状态和线程数,通过返回false防止不需要的时候添加线程
         * 2. 如果一个任务能够成功的入队,仍然需要双重检查,因为我们添加了一个线程(有可能这个线程在上次检查后就已经死亡了)
         *    或者进入此方法的时候调用了shutdown,所以需要重新检查线程池的状态,如果必要的话,当停止的时候要回滚入队操作,
         *    或者当线程池为空的话创建一个新的线程
         * 3. 如果不能入队,尝试着开启一个新的线程,如果开启失败,说明线程池已经是shutdown状态或饱和了,所以拒绝执行该任务
         */
        int c = ctl.get();
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            if (! isRunning(recheck) && remove(command))
                reject(command);
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        else if (!addWorker(command, false))
            reject(command);
    }
```

1. 检查当前线程池中的线程数是否<核心线程数，如果小于核心线程数，就调用addWorker方法创建一个新的线程执行任务，addworker中的第二个参数传入true，表示当前创建的是核心线程。如果当前线程数>=核心线程数或者创建线程失败的话，直接进入第二种情况。

2. 通过调用isRunning方法判断线程池是否还在运行，如果线程池状态不是running，那就直接退出execute方法，没有执行的必要了；如果线程池的状态是running，尝试着把任务加入到queue中，再次检查线程池的状态， 如果当前不是running，可能在入队后调用了shutdown方法，所以要在queue中移除该任务，默认采用拒绝策略直接抛出异常。如果当前线程数为0，可能把allowCoreThreadTimeOut设为了true，正好核心线程全部被回收，所以必须要创建一个空的线程，让它自己去queue中去取任务执行。

3. 如果当前线程数>核心线程数，并且入队失败，调用addWorker方法创建一个新的线程去执行任务，第二个参数是false，表示当前创建的线程不是核心线程。这种情况表示核心线程已满并且queue已满，如果当前线程数小于最大线程数，创建线程执行任务。如果当前线程数>=最大线程数，默认直接采取拒绝策略。

### addWorker方法

看一下addWorker是怎么具体执行的。代码有点长，慢慢看。


```
 private boolean addWorker(Runnable firstTask, boolean core) {
        retry:
        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);
 
            // Check if queue empty only if necessary.
            if (rs >= SHUTDOWN &&
                ! (rs == SHUTDOWN &&
                   firstTask == null &&
                   ! workQueue.isEmpty()))
                return false;
 
            for (;;) {
                int wc = workerCountOf(c);
                if (wc >= CAPACITY ||
                    wc >= (core ? corePoolSize : maximumPoolSize))
                    return false;
                if (compareAndIncrementWorkerCount(c))
                    break retry;
                c = ctl.get();  // Re-read ctl
                if (runStateOf(c) != rs)
                    continue retry;
                // else CAS failed due to workerCount change; retry inner loop
            }
        }
 
        boolean workerStarted = false;
        boolean workerAdded = false;
        Worker w = null;
        try {
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    // Recheck while holding lock.
                    // Back out on ThreadFactory failure or if
                    // shut down before lock acquired.
                    int rs = runStateOf(ctl.get());
 
                    if (rs < SHUTDOWN ||
                        (rs == SHUTDOWN && firstTask == null)) {
                        if (t.isAlive()) // precheck that t is startable
                            throw new IllegalThreadStateException();
                        workers.add(w);
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    t.start();
                    workerStarted = true;
                }
            }
        } finally {
            if (! workerStarted)
                addWorkerFailed(w);
        }
        return workerStarted;
    }
```
首先判断线程池的runstate，如果runstate为shutdown，那么并不能满足第二个条件，runstate != shutdown，所以这是针对runstate不是running，shutdown的情况，当runstate>shutdown时，队列为空，此时仍然有任务的话，直接返回false，线程池已关闭，并不能在继续执行任务了。

第二个自旋操作就的目的就是对线程数量自增，由于涉及到高并发，所以采用了cas来控制，判断线程的workcount>=CAPACITY，那么直接返回false，或者通过判断是否核心线程，如果是true，判断workcount>=核心线程数，如果是false，判断workcount>=最大线程数，直接返回false。如果不满足上个条件，直接使用cas把线程数自增，退出自旋操作。

worker对象。

```
  Worker(Runnable firstTask) {
            setState(-1); // inhibit interrupts until runWorker
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }
```

在当前线程不为空的情况下，加一把可重入锁reentrantlock，在加锁后，再次检查线程池的状态runstate，防止在获取到锁之前线程池已经关闭了，线程池的状态为running或者状态为shutdown并且任务为空的情况下，才能继续往下执行任务，这是充分必要条件。如果当前线程已经开启了，直接抛出异常，这是绝不允许的。

 private final HashSet<Worker> workers = new HashSet<Worker>();
 
 
 ### addWorkerFailed方法
如果添加worker失败或者开启线程失败就要调用addWorkerFailed方法移除失败的worker。


```
   private void addWorkerFailed(Worker w) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (w != null)
                workers.remove(w);
            decrementWorkerCount();
            tryTerminate();
        } finally {
            mainLock.unlock();
        }
    }

```
首先还是获取全局锁mainlock，接着对workers集合中移除worker，workers的数量自减。

### runWorker方法
这个方法是运行task的方法

```
 final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock(); // allow interrupts
        boolean completedAbruptly = true;
        try {
            while (task != null || (task = getTask()) != null) {
                w.lock();
                
                if ((runStateAtLeast(ctl.get(), STOP) ||
                     (Thread.interrupted() &&
                      runStateAtLeast(ctl.get(), STOP))) &&
                    !wt.isInterrupted())
                    wt.interrupt();
                try {
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try {
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x; throw x;
                    } catch (Error x) {
                        thrown = x; throw x;
                    } catch (Throwable x) {
                        thrown = x; throw new Error(x);
                    } finally {
                        afterExecute(task, thrown);
                    }
                } finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            processWorkerExit(w, completedAbruptly);
        }
    }

```

首先还是获取当前线程，获取当前worker对象中的任务task，把当前线程的状态由-1设为0，表示可以获取锁执行任务，接下来就是一个while循环，当task不为空或者从gettask方法取出的任务不为空的时候，加锁，底层还是使用了AQS，保证了只有一个线程执行完毕其他线程才能执行。在执行任务之前，必须进行判断，线程池的状态如果>=STOP，必须中断当前线程，如果是running或者shutdown，当前线程不能被中断，防止线程池调用了shutdownnow方法必须中断所有的线程。

在处理任务之前，会执行beforeExecute方法， 在处理任务之后，执行afterExecute方法，这两个都是钩子方法，继承了ThreadPoolExecutor可以重写此方法，嵌入自定义的逻辑。一旦在任务运行的过程中，出现异常会直接抛出，所以在实际的业务中，应该使用try..catch，把这些日常加入到日志中。

任务执行完，就把task设为空，累加当前线程完成的任务数，unlock，继续从queue中取任务执行。

### getTask方法。

```
  private Runnable getTask() {
        boolean timedOut = false; // Did the last poll() time out?

        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            // Check if queue empty only if necessary.
            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
                decrementWorkerCount();
                return null;
            }

            int wc = workerCountOf(c);

            // Are workers subject to culling?
            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

            if ((wc > maximumPoolSize || (timed && timedOut))
                && (wc > 1 || workQueue.isEmpty())) {
                if (compareAndDecrementWorkerCount(c))
                    return null;
                continue;
            }

            try {
                Runnable r = timed ?
                    workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                    workQueue.take();
                if (r != null)
                    return r;
                timedOut = true;
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }

```

这个有点复杂，其实目的就是获得一个要运行的任务，不过人家的判断还是比较多的

### shutdown方法

```
public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(SHUTDOWN);
            interruptIdleWorkers();
            onShutdown(); // hook for ScheduledThreadPoolExecutor
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
    }
```
调用了shutdown，意味着不能在继续往queue中添加任务，也不能在接受新的任务。利用cas把当前线程池的状态设为shutdown，中断所有的空闲线程，onShutdown是一个钩子方法，是专门给ScheduledThreadPoolExecutor来实现的，再次调用tryTerminate方法来尝试中止线程池，直到queue中的任务全部处理完毕才能正常关闭。

![](https://user-gold-cdn.xitu.io/2019/12/24/16f36fb8bb3d6a0f?w=737&h=688&f=png&s=23714)

提一下，默认的 存任务的队列是BlockingQueue

## 结尾
因为很多东西，全是从书上拷贝的，很枯燥，但同时看书，又是最详细的学习方法之一了，大家跟着书看博客，或许会好点吧.最后一节不写了，就是工厂方法里面的几种线程池，我们一般也是自定义线程池来操作的。


> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
