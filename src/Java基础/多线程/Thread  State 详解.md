# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
多线程的系列前面已经写了很多了，有兴趣的可以去我的仓库康康，今天呢？我也是看到人家写的不错，然后自己随便抄袭下，哈哈，其实就是把看到的知识记录一下下。自己写一遍的话，可能记忆深刻一点。



## 线程状态转换图

![](https://user-gold-cdn.xitu.io/2020/5/6/171ea47b8287af73?w=981&h=602&f=png&s=49232)
- NEW 初始状态
- RUNNABLE 运行状态
- BLOCKED 阻塞状态
- WAITING 等待状态
- TIME_WAITING 超时等待状态
- TERMINATED 终止状态

> 注意：
调用obj.wait()的线程需要先获取obj的monitor，wait()会释放obj的monitor并进入等待态。所以wait()/notify()都要与synchronized联用。

### 阻塞与等待的区别

阻塞：当一个线程试图获取对象锁（非java.util.concurrent库中的锁，即synchronized），而该锁被其他线程持有，则该线程进入阻塞状态。它的特点是使用简单，由JVM调度器来决定唤醒自己，而不需要由另一个线程来显式唤醒自己，不响应中断。 

阻塞 一个线程因为等待临界区的锁被阻塞产生的状态

等待：当一个线程等待另一个线程通知调度器一个条件时，该线程进入等待状态。它的特点是需要等待另一个线程显式地唤醒自己，实现灵活，语义更丰富，可响应中断。例如调用：Object.wait()、Thread.join()以及等待Lock或Condition。
等待 一个线程进入了锁，但是需要等待其他线程执行某些操作

需要强调的是虽然synchronized和JUC里的Lock都实现锁的功能，但线程进入的状态是不一样的。synchronized会让线程进入阻塞态，而JUC里的Lock是用LockSupport.park()/unpark()来实现阻塞/唤醒的，会让线程进入等待态。但话又说回来，虽然等锁时进入的状态不一样，但被唤醒后又都进入runnable态，从行为效果来看又是一样的。
一个线程进入了锁，但是需要等待其他线程执行某些操作

## 主要操作
### start()
新启一个线程执行其run()方法，一个线程只能start一次。主要是通过调用native start0()来实现。

```
public synchronized void start() {
　　　　　//判断是否首次启动
        if (threadStatus != 0)
            throw new IllegalThreadStateException();

        group.add(this);

        boolean started = false;
        try {
　　　　　　　//启动线程
            start0();
            started = true;
        } finally {
            try {
                if (!started) {
                    group.threadStartFailed(this);
                }
            } catch (Throwable ignore) {
                /* do nothing. If start0 threw a Throwable then
                  it will be passed up the call stack */
            }
        }
    }

    private native void start0();
```

### run()
run()方法是不需要用户来调用的，当通过start方法启动一个线程之后，当该线程获得了CPU执行时间，便进入run方法体去执行具体的任务。注意，继承Thread类必须重写run方法，在run方法中定义具体要执行的任务。

### sleep()
sleep方法有两个重载版本

```
sleep(long millis)     //参数为毫秒

sleep(long millis,int nanoseconds)    //第一参数为毫秒，第二个参数为纳秒
```
sleep相当于让线程睡眠，交出CPU，让CPU去执行其他的任务。

但是有一点要非常注意，sleep方法不会释放锁，也就是说如果当前线程持有对某个对象的锁，则即使调用sleep方法，其他线程也无法访问这个对象。

### yield()


调用yield方法会让当前线程交出CPU权限，让CPU去执行其他的线程。它跟sleep方法类似，同样不会释放锁。但是yield不能控制具体的交出CPU的时间，另外，yield方法只能让拥有相同优先级的线程有获取CPU执行时间的机会。

注意，调用yield方法并不会让线程进入阻塞状态，而是让线程重回就绪状态，它只需要等待重新获取CPU执行时间，这一点是和sleep方法不一样的。


### join()
join方法有三个重载版本

```
1 join()
2 join(long millis)     //参数为毫秒
3 join(long millis,int nanoseconds)    //第一参数为毫秒，第二个参数为纳秒
```
join()实际是利用了wait()，只不过它不用等待notify()/notifyAll()，且不受其影响。它结束的条件是：1）等待时间到；2）目标线程已经run完（通过isAlive()来判断）。


```
public final synchronized void join(long millis) throws InterruptedException {
    long base = System.currentTimeMillis();
    long now = 0;

    if (millis < 0) {
        throw new IllegalArgumentException("timeout value is negative");
    }
    
    //0则需要一直等到目标线程run完
    if (millis == 0) {
        while (isAlive()) {
            wait(0);
        }
    } else {
        //如果目标线程未run完且阻塞时间未到，那么调用线程会一直等待。
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

### interrupt()

此操作会将线程的中断标志位置位，至于线程作何动作那要看线程了。

- 如果线程sleep()、wait()、join()等处于阻塞状态，那么线程会定时检查中断状态位如果发现中断状态位为true，则会在这些阻塞方法调用处抛出InterruptedException异常，并且在抛出异常后立即将线程的中断状态位清除，即重- 新设置为false。抛出异常是为了线程从阻塞状态醒过来，并在结束线程前让程序员有足够的时间来处理中断请求。
- 如果线程正在运行、争用synchronized、lock()等，那么是不可中断的，他们会忽略。

可以通过以下三种方式来判断中断：

- isInterrupted()

    此方法只会读取线程的中断标志位，并不会重置。

- interrupted()

    此方法读取线程的中断标志位，并会重置。

- throw InterruptException

    抛出该异常的同时，会重置中断标志位。
    
    
## 结尾

小六六觉得，最主要的是理解一下 阻塞 和等待 这2个状态的区别，等待和超时等待很好理解，就是说你们的线程需要等待其他线程操作的时间是确定的。

文章出自 [Thread详解](https://www.cnblogs.com/waterystone/p/4920007.html)

![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！