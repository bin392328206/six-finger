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
## 简介
在JDK的并发包里提供了几个非常有用的并发工具类。
- 提供并发流程控制的工具类
    - CountDownLatch
    - CyclicBarrier
    - Semaphore
- 提供了在线程间交换数据的工具类
    - Exchanger


### 等待多线程完成的CountDownLatch

CountDownLatch 允许一个或多个线程等待其他线程完成操作。

假如有这样一个需求：我们需要解析一个Excel里多个sheet的数据，此时可以考虑使用多线程，每个线程解析一个sheet里的数据，等到所有的sheet都解析完之后，程序需要提示解析完成。在这个需求中，要实现主线程等待所有线程完成sheet的解析操作，最简单的做法是使用join()方法，如下：


```
public static class JoinCountDownLatchTest {
    public static void main(String[] args) throws InterruptedException {
        Thread parser1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("parser1 finish");
            }
        });
        Thread parser2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("parser2 finish");
            }
        });
        parser1.start();
        parser2.start();
        parser1.join();
        parser2.join();
        System.out.println("all parser finish");
    }
}
```
join 用于让当前执行线程等待join线程执行结束。其实现原理是不停检查join线程是否存活，
如果 join 线程存活则让当前线程永远等待。其中，wait(0)表示永远等待下去


在JDK 1.5之后的并发包中提供的 CountDownLatch 也可以实现 join 的功能，并且比join的功能更多，示例如下：

```
public static class CountDownLatchTest {
    static CountDownLatch c = new CountDownLatch(3);

    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(1 + " -- " + System.currentTimeMillis());
                c.countDown();
                System.out.println(2 + " -- " + System.currentTimeMillis());
                c.countDown();
                try {
                    Thread.sleep(1000);
                    System.out.println(4 + " -- " + System.currentTimeMillis());
                    c.countDown();
                    Thread.sleep(1000);
                    System.out.println(5 + " -- " + System.currentTimeMillis());
                    c.countDown();
                    System.out.println(6 + " -- " + System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        c.await();
        System.out.println("3" + " -- " + System.currentTimeMillis());
    }
}
```

```
1 -- 1558966154351
2 -- 1558966154351
4 -- 1558966155352
3 -- 1558966155352
5 -- 1558966156353
6 -- 1558966156353
```

CountDownLatch 的构造函数接收一个int 类型的参数作为计数器，如果你想等待N个点完成，这里就传入N。  
当我们调用 CountDownLatch 的 countDown 方法时，N 就会 -1，CountDownLatch 的 await 方法会阻塞当前线程，直到 N 变成零。  
由于 countDown 方法可以用在任何地方，所以这里说的 N 个点，可以是 N个线程，也可以是 1个线程里的N个执行步骤。  
用在多个线程时，只需要把这个 CountDownLatch 的引用传递到线程里即可。   

> 计数器必须大于等于0，只是等于0时候，计数器就是零，调用await方法时不会阻塞当前线程。
CountDownLatch不可能重新初始化或者修改CountDownLatch对象的内部计数器的值。
一个线程调用countDown方法happen-before另外一个线程调用await方法。
### 同步屏障CyclicBarrier
CyclicBarrier的字面意思是可循环使用（Cyclic）的屏障（Barrier）。
它要做的事情是，让一组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续运行。

CyclicBarrier 默认的构造方法是 CyclicBarrier(int parties)，其参数表示 屏障拦截的线程数量，每个线程调用 await 方法告诉 CyclicBarrier 我已经到达了屏障，然后当前线程被阻塞。

```
public static class CyclicBarrierTest {
    private static CyclicBarrier c = new CyclicBarrier(2);

    public static void main(String[] args) {
        System.out.println(" 1 -- " + System.currentTimeMillis());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(" 2 -- " + System.currentTimeMillis());
                    Thread.sleep(1000);
                    System.out.println(" 3 -- " + System.currentTimeMillis());
                    c.await();
                    System.out.println(" 4 -- " + System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            System.out.println(" 5 -- " + System.currentTimeMillis());
            c.await();
            System.out.println(" 6 -- " + System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(" 7 -- " + System.currentTimeMillis());
    }
}
```

```
 1 -- 1558969130471
 5 -- 1558969130471
 2 -- 1558969130471
 3 -- 1558969131471
 4 -- 1558969131471
 6 -- 1558969131471
 7 -- 1558969131471
```
如果把 new CyclicBarrier(2) 修改成 new CyclicBarrier(3)，则主线程和子线程会永远等待，因为没有第三个线程执行await方法，即 没有第三个线程到达屏障，所以之前到达屏障的两个线程都不会继续执行。

CyclicBarrier 还提供一个更高级的构造函数CyclicBarrier(int parties，Runnable barrierAction)，用于在线程到达屏障时，优先执行barrierAction，方便处理更复杂的业务场景。

示例如下：


```
public static class CyclicBarrierTest2 {
    static CyclicBarrier c = new CyclicBarrier(2, new A());

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    c.await();
                } catch (Exception e) {
                }
                System.out.println(1);
            }
        }).start();
        try {
            c.await();
        } catch (Exception e) {
        }
        System.out.println(2);
    }

    static class A implements Runnable {
        @Override
        public void run() {
            System.out.println(3);
        }
    }
}
```

因为 CyclicBarrier 设置了拦截线程的数量是 2 ，所以必须等代码中的 第一个线程 和 线程A 都执行完之后，才会继续执行 主线程，然后输出 2，所以代码执行后的输出如下：


```
3
1
2
```

### CyclicBarrier和CountDownLatch的区别
CountDownLatch的计数器只能使用一次，而CyclicBarrier的计数器可以使用reset()方法重置。
所以CyclicBarrier能处理更为复杂的业务场景。
例如，如果计算发生错误，可以重置计数器，并让线程重新执行一次。

### 控制并发线程数的Semaphore
Semaphore（信号量）是用来控制同时访问特定资源的线程数量，它通过协调各个线程，以保证合理的使用公共资源。
> 从字面上很难理解Semaphore所表达的含义，只能把它比作是控制流量的红绿灯。比如××马路要限制流量，只允许同时有一百辆车在这条路上行使，其他的都必须在路口等待，所以前一百辆车会看到绿灯，可以开进这条马路，后面的车会看到红灯，不能驶入××马路，但是如果前一百辆中有5辆车已经离开了××马路，那么后面就允许有5辆车驶入马路，这个例子里说的车就是线程，驶入马路就表示线程在执行，离开马路就表示线程执行完成，看见红灯就表示线程被阻塞，不能执行。

- 应用场景
    - Semaphore可以用于做流量控制，特别是公用资源有限的应用场景，比如数据库连接。  
    假如有一个需求，要读取几万个文件的数据，因为都是IO密集型任务，我们可以启动几十个线程并发地读取，但是如果读到内存后，还需要存储到数据库中，而数据库的连接数只有10个，这时我们必须控制只有10个线程同时获取数据库连接保存数据，否则会报错无法获取数据库连接。   
    这个时候，就可以使用Semaphore来做流量控制，代码如下：


```
public static class SemaphoreTest {
    private static final int THREAD_COUNT = 20;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
    private static Semaphore s = new Semaphore(5);

    public static void main(String[] args) {
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadPool.execute(new MyRunnable(i));
        }
        threadPool.shutdown();
    }

    private static class MyRunnable implements Runnable {

        private int index;

        MyRunnable(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            try {
                s.acquire();
                System.out.println("save data -- " + index);
                s.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
```

```
save data -- 0
save data -- 4
save data -- 3
save data -- 1
save data -- 2
save data -- 6
save data -- 5
save data -- 7
save data -- 9
save data -- 10
save data -- 8
save data -- 11
save data -- 12
save data -- 13
save data -- 14
save data -- 15
save data -- 16
save data -- 17
save data -- 18
save data -- 19
```

在代码中，虽然有20个线程在执行，但是只允许5个并发执行。Semaphore的构造方法Semaphore(int permits)接受一个整型的数字，表示可用的许可证数量。Semaphore(5)表示允许5个线程获取许可证，也就是最大并发数是5。
Semaphore的用法也很简单，首先线程使用Semaphore的acquire()方法获取一个许可证，使用完之后调用release()方法归还许可证。
还可以用tryAcquire()方法尝试获取许可证。


### 线程间交换数据的Exchanger
Exchanger（交换者）是一个用于线程间协作的工具类。  
Exchanger用于进行线程间的数据交换。它提供一个同步点，在这个同步点，两个线程可以交换彼此的数据。这两个线程通过exchange方法交换数据，如果第一个线程先执行exchange()方法，它会一直等待第二个线程也执行exchange()方法，当两个线程都到达同步点时，这两个线程就可以交换数据，将本线程生产出来的数据传递给对方。

下面来看一下Exchanger的应用场景。

Exchanger可以用于 遗传算法。
遗传算法 里需要选出两个人作为交配对象，这时候会交换两人的数据，并使用交叉规则得出2个交配结果。

```
public static class ExchangerTest {
    private static final Exchanger<String> exgr = new Exchanger<String>();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // A录入银行流水数据
                    String A = "银行流水A";
                    exgr.exchange(A);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // B录入银行流水数据
                    String B = "银行流水B";
                    String A = exgr.exchange(B);
                    System.out.println("A和B数据是否一致：" + A.equals(B)
                            + ", A录入的是：" + A + ", B录入是：" + B);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadPool.shutdown();
    }
}
```

```
A和B数据是否一致：false, A录入的是：银行流水A, B录入是：银行流水B
```
如果两个线程有一个没有执行exchange()方法，则会一直等待，如果担心有特殊情况发生，避免一直等待，可以使用exchange(V x, long timeout, TimeUnit unit)设置最大等待时长。

## 总结
本文配合一些应用场景介绍JDK中提供的几个并发工具类，大家记住这个工具类的用途，一旦有对应的业务场景，不妨试试这些工具类。

- 等待多线程完成的CountDownLatch
- 同步屏障CyclicBarrier
- 控制并发线程数的Semaphore
- 线程间交换数据的Exchanger

## 结尾
因为很多东西，全是从书上拷贝的，很枯燥，但同时看书，又是最详细的学习方法之一了，大家跟着书看博客，或许会好点吧.


> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！

