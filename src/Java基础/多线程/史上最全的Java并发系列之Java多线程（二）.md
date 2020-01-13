# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
昨天把线程这个类的源码，稍微的讲了一下，创建线程啥的大家都懂，就没有讲了，今天我们接着讲线程，下面是前面的章节链接：  
- [🔥史上最全的Java并发系列之并发编程的挑战](https://juejin.im/post/5dfb1ca26fb9a0160b63827f)  
- [🔥史上最全的Java并发系列之Java并发机制的底层实现原理](https://juejin.im/post/5dfb3a27e51d4558181d35b0)    
- [🔥史上最全的Java并发系列之Java内存模型](https://juejin.im/post/5dfc3dadf265da339b5001dd)  
[🔥史上最全的Java并发系列之Java多线程（一）](https://juejin.im/post/5dfc9106518825126e63a711)

## 线程间通信

### volatile和synchronized关键字

volatile修饰的变量，程序访问时都需要在共享内存中去读取，对它的改变也必须更新共享内存，保证了线程对变量访问的可见性。

synchronized：对于 同步块 的实现使用了monitorenter和monitorexit指令，而 同步方法 则是依靠方法修饰符上的ACC_SYNCHRONIZED来完成的。无论采用哪种方式，其本质是对一个对象的监视器monitor进行获取，而这个获取过程是排他的，也就是同一时刻只能有一个线程获取到由synchronized所保护对象的监视器。

### 等待/通知机制——wait和notify
指一个线程A调用了对象O的wait()方法进入等待状态，而另一个线程B调用了对象O的notify()或者notifyAll()方法，线程A收到通知后从对象O的wait()方法返回，进而执行后续操作。
等待：wait()、wait(long)、wait(long, int)
通知：notify()、notifyAll()
示例：

```
    private static Object object = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                synchronized (object) {
                    System.out.println("t1 start object.wait(), time = " + System.currentTimeMillis() / 1000);
                    object.wait();
                    System.out.println("t1 after object.wait(), time = " + System.currentTimeMillis() / 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                synchronized (object) {
                    System.out.println("t2 start object.notify(), time = " + System.currentTimeMillis() / 1000);
                    object.notify();
                    System.out.println("t2 after object.notify(), time = " + System.currentTimeMillis() / 1000);
                }

                synchronized (object) {
                    System.out.println("t2  hold lock again, time = " + System.currentTimeMillis() / 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        t2.start();
    }
```

输出的结果
```
t1 start object.wait(), time = 1560138112
t2 start object.notify(), time = 1560138116
t2 after object.notify(), time = 1560138116
t2  hold lock again, time = 1560138116
t1 after object.wait(), time = 1560138116
```
以下结论
1. wait()、notify()和notifyAll()时需要先对调用对象加锁,否则会报java.lang.IllegalMonitorStateException异常。
2. 调用wait()方法后，线程状态由RUNNING变为WAITING，并将当前线程放置到对象的等待队列。
3. notify()或notifyAll()方法调用后，等待线程依旧不会从wait()返回，需要调用notify()或notifAll()的线程释放锁之后，等待线程才有机会从wait()返回。
4. notify()方法将等待队列中的一个等待线程从等待队列中移到同步队列中，而notifyAll()方法则是将等待队列中所有的线程全部移到同步队列，被移动的线程状态由WAITING变为BLOCKED。
5. 从wait()方法返回的前提是获得了调用对象的锁。

![](https://user-gold-cdn.xitu.io/2019/12/22/16f2be9a20aa7b25?w=1882&h=872&f=png&s=245841)

### 等待/通知的经典范式
包括 等待方（消费者）和 通知方（生产者）。
等待方遵循以下原则：
- 获取对象的锁。
- 如果条件不满足，那么调用对象的wait方法，被通知后任要检查条件。
- 条件不满足则执行对应的逻辑。
对应代码如下：

```
synchronized (对象) {
    while (条件不满足) {
        对象.wait();
    }
    对应的处理逻辑
}
```

通知方遵循以下原则：
- 获取对象的锁。
- 改变条件。
- 通知所有在等待在对象上的线程。

```
synchronized (对象) {
    改变条件
    对象.notifyAll();
}
```
### 管道输入/输出流
PipedWriter和PipedReader分别是字符管道输出流和字符管道输入流,是字符流中"管道流",可以实现同一个进程中两个线程之间的通信,与PipedOutputStream和PipedInputStream相比,功能类似.区别是前者传输的字符数据,而后者传输的是字节数据.

不同线程之间通信的流程大致是:PipedWriter与PipedReader流进行连接,写入线程通过将字符管道输出流写入数据,实际将数据写到了与PipedWriter连接的PipedReader流中的缓冲区,然后读取线程通过PipedReader流读取之前存储在缓冲区里面的字符数据.

例子

```
    private static PipedWriter writer;
    private static PipedReader reader;

    public static void main(String[] args) throws InterruptedException, IOException {
        writer = new PipedWriter();
        reader = new PipedReader();
        //绑定输入输出
        writer.connect(reader);
        Thread t = new Thread(() -> {
            int res;
            try {
                while ((res = reader.read()) != -1) {
                    System.out.print((char) res);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();

        int res;
        while ((res = System.in.read()) != -1) {
            System.out.println(res);
            writer.write(res);
            //按回车结束
            if (res == 10) {
                break;
            }
        }
        writer.close();
    }
```
### ThreadLocal
变量值的共享可以使用public static的形式，所有线程都使用同一个变量，如果想实现每一个线程都有自己的共享变量该如何实现呢？JDK中的ThreadLocal类正是为了解决这样的问题。

ThreadLocal类并不是用来解决多线程环境下的共享变量问题，而是用来提供线程内部的共享变量，在多线程环境下，可以保证各个线程之间的变量互相隔离、相互独立。在线程中，可以通过get()/set()方法来访问变量。ThreadLocal实例通常来说都是private static类型的，它们希望将状态与线程进行关联。这种变量在线程的生命周期内起作用，可以减少同一个线程内多个函数或者组件之间一些公共变量的传递的复杂度。

例子

```

public class ThreadLocalTest {
	static class MyThread extends Thread {
		private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
		
		@Override
		public void run() {
			super.run();
			for (int i = 0; i < 3; i++) {
				threadLocal.set(i);
				System.out.println(getName() + " threadLocal.get() = " + threadLocal.get());
			}
		}
	}
	
	public static void main(String[] args) {
		MyThread myThreadA = new MyThread();
		myThreadA.setName("ThreadA");
		
		MyThread myThreadB = new MyThread();
		myThreadB.setName("ThreadB");
		
		myThreadA.start();
		myThreadB.start();
	}
}
```

```
ThreadA threadLocal.get() = 0
ThreadB threadLocal.get() = 0
ThreadA threadLocal.get() = 1
ThreadA threadLocal.get() = 2
ThreadB threadLocal.get() = 1
ThreadB threadLocal.get() = 2
```
ThreadLocal最简单的实现方式就是ThreadLocal类内部有一个线程安全的Map，然后用线程的ID作为Map的key，实例对象作为Map的value，这样就能达到各个线程的值隔离的效果。


![](https://user-gold-cdn.xitu.io/2019/12/23/16f306062911b67d?w=1247&h=634&f=png&s=146140)

### 一个数据库连接池的简单实现
1.连接池创建

```
package com.atguigu.ct.producer.Test.BB;

import java.sql.Connection;
import java.util.LinkedList;

public class ConnectionPool {

    private LinkedList<Connection> pool = new LinkedList<>();

    public ConnectionPool(int initialSize){
        if(initialSize > 0){
            for (int i = 0; i < initialSize ; i++) {
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }

    public void releaseConnection(Connection connection){
        if(connection != null){
            synchronized (pool){
                //连接释放后需要进行通知，这样其他消费者能够感知到连接池中已归还了一个连接
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

    //在 mills内无法获取到连接，将返回 null
    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool){
            //完全超时
            if(mills <= 0){
                while (pool.isEmpty()){
                    pool.wait();
                }
                return pool.removeFirst();
            }else{
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0){
                    pool.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                }
                Connection result = null;
                if(!pool.isEmpty()){
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }
}

```
 2.由于java.sql.Connection 是一个接口，最终的实现是由数据库驱动提供方来实现的，博主只是为了娱乐，就用动态代理构造了一个Connection,改Connection的代理实现仅仅是在commit()方法调用时休眠100毫秒，如下
 
```
package com.atguigu.ct.producer.Test.BB;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

public class ConnectionDriver {

    static class ConnectionHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if(method.getName().equals("commit")){
                TimeUnit.MILLISECONDS.sleep(100);
            }
            return null;
        }
    }

    //创建一个 Connection 代理，在 commit 时休眠 100 毫秒
    public static final Connection createConnection(){
        return (Connection) Proxy.newProxyInstance(ConnectionDriver.class.getClassLoader(),
                new Class<?>[]{Connection.class}, new ConnectionHandler());
    }
}

```
3.模拟客户端ConnectionRunner获取、使用、最后释放连接的过程，当他使用时连接将会增加获取到连接的数量，反之，将会增加未获取到连接的数量，如下： 

```
package com.atguigu.ct.producer.Test.BB;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPoolTest {

    static ConnectionPool pool = new ConnectionPool(10);
    //保证所有的ConnectionRunner 能够同时开始
    static CountDownLatch start = new CountDownLatch(1);
    //main 线程将会等待所有 ConnectionRunner 结束才能继续执行
    static CountDownLatch end;

    public static void main(String[] args) throws InterruptedException {
        //线程数量，可以修改线程数量进行观察
        int threadCount = 100;
        end = new CountDownLatch(threadCount);
        int count = 20;
        AtomicInteger got = new AtomicInteger();
        AtomicInteger notGot = new AtomicInteger();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(new ConnetionRunner(count, got, notGot), "ConnetionRunnerThread");
            thread.start();
        }
        start.countDown();
        end.await();
        System.out.println("total invoke:" + (threadCount * count));
        System.out.println(" got connection : " + got);
        System.out.println(" not got connection : " + notGot);
    }


    static class ConnetionRunner implements Runnable {

        int count;
        AtomicInteger got;
        AtomicInteger notGot;

        public ConnetionRunner(int count, AtomicInteger got, AtomicInteger notGot) {
            this.count = count;
            this.got = got;
            this.notGot = notGot;
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (InterruptedException e) {
            }
            while (count > 0) {
                try {
                    //从线程池中获取连接，如果1000ms内无法获取到，将会返回null
                    //分别统计连接获取的数量got和未获取到的数量 notGot
                    Connection connection = pool.fetchConnection(1000);
                    if (connection != null) {
                        try {
                            connection.createStatement();
                            connection.commit();
                        } finally {
                            pool.releaseConnection(connection);
                            got.incrementAndGet();
                        }
                    } else {
                        notGot.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    count--;
                }
            }
            end.countDown();
        }
    }
}

```

![](https://user-gold-cdn.xitu.io/2019/12/23/16f307fcbd16e31c?w=1135&h=235&f=png&s=29836)


下面通过使用前一节中的线程池来构造一个简单的Web服务器，这个Web服务器用来处理 HTTP请求，目前只能处理简单的文本和JPG图片内容。这个Web服务器使用main线程不断地接 受客户端Socket的连接，将连接以及请求提交给线程池处理，这样使得Web服务器能够同时处 理多个客户端请求，示例如下所示。


```
public class SimpleHttpServer {
 
		private int port=8080;
	  private ServerSocketChannel serverSocketChannel = null;
	  private ExecutorService executorService;
	  private static final int POOL_MULTIPLE = 4;
 
	  public SimpleHttpServer() throws IOException {
	    executorService= Executors.newFixedThreadPool(
		    Runtime.getRuntime().availableProcessors() * POOL_MULTIPLE);
	    serverSocketChannel= ServerSocketChannel.open();
	    serverSocketChannel.socket().setReuseAddress(true);
	    serverSocketChannel.socket().bind(new InetSocketAddress(port));
	    System.out.println("ddd");
	  }
 
	  public void service() {
	    while (true) {
	      SocketChannel socketChannel=null;
	      try {
	        socketChannel = serverSocketChannel.accept();
	        executorService.execute(new Handler(socketChannel));
	      }catch (IOException e) {
	         e.printStackTrace();
	      }
	    }
	  }
 
	  public static void main(String args[])throws IOException {
	    new SimpleHttpServer().service();
	  }
	  class Handler implements Runnable{
	  private SocketChannel socketChannel;
	  public Handler(SocketChannel socketChannel){
	    this.socketChannel=socketChannel;
	  }
	  public void run(){
	    handle(socketChannel);
	  }
 
	  public void handle(SocketChannel socketChannel){
	    try {
	        Socket socket=socketChannel.socket();
	        System.out.println("ddd" +
	        socket.getInetAddress() + ":" +socket.getPort());
 
	         ByteBuffer buffer=ByteBuffer.allocate(1024);
	         socketChannel.read(buffer);
	         buffer.flip();
	         String request=decode(buffer);
	         System.out.print(request);  
 
	         StringBuffer sb=new StringBuffer("HTTP/1.1 200 OK\r\n");
	         sb.append("Content-Type:text/html\r\n\r\n");
	         socketChannel.write(encode(sb.toString()));
 
	         FileInputStream in;
	         
	         String firstLineOfRequest=request.substring(0,request.indexOf("\r\n"));
	         if(firstLineOfRequest.indexOf("login.htm")!=-1)
	            in=new FileInputStream("/Users/tokou/Documents/post.html");
	         else
	            in=new FileInputStream("/Users/tokou/Documents/post.html");
 
	         FileChannel fileChannel=in.getChannel();
	         fileChannel.transferTo(0,fileChannel.size(),socketChannel);
	         fileChannel.close();
	      }catch (Exception e) {
	         e.printStackTrace();
	      }finally {
	         try{
	           if(socketChannel!=null)socketChannel.close();
	         }catch (IOException e) {e.printStackTrace();}
	      }
	  }
	  private Charset charset=Charset.forName("GBK");
	  public String decode(ByteBuffer buffer){  
	    CharBuffer charBuffer= charset.decode(buffer);
	    return charBuffer.toString();
	  }
	  public ByteBuffer encode(String str){  
	    return charset.encode(str);
	  }
	 }
}

```
![](https://user-gold-cdn.xitu.io/2019/12/23/16f3084603d78367?w=1013&h=794&f=png&s=215210)


## 面试题 
> 2个线程交替打印A1B2C3D4...这样的模式的实现

### 方法一 LockSupport

```
public class TestLockSupport {

    static Thread t1=null,t2=null;

    public static void main(String[] args) {

        char[] aI="1234567".toCharArray();
        char[] aC="ABCDEFG".toCharArray();

        t1=new Thread(()->{
            for (char c : aI) {
                System.out.println(c);
                LockSupport.unpark(t2);
                LockSupport.park();
            }
        },"t1");


        t2=new Thread(()->{
            for (char c : aC) {
                LockSupport.park();
                System.out.println(c);
                LockSupport.unpark(t1);
            }
        },"t1");
        t1.start();
        t2.start();
    }
}

```
结果

```
1
A
2
B
3
C
4
D
5
E
6
F
7
G
```

### 方法二 用CAS自旋锁+volatitle来实现

```
 enum  ReadyToRun {T1,T2}

  //先定义T1准备运行  而且要设置volatile 线程可见
  static volatile ReadyToRun r=ReadyToRun.T1;

    public static void main(String[] args) {

        char[] aI="1234567".toCharArray();
        char[] aC="ABCDEFG".toCharArray();


        new Thread(()->{
            for (char c : aI) {
                //如果不是T1准备运行 就一直返回空，直到T1运行打印，打印完之后把准备运行的变为T2
                while (r!=ReadyToRun.T1){}
                System.out.println(c);
                r=ReadyToRun.T2;
            }

        },"t1").start();


        new Thread(()->{
            for (char c : aC) {
                //如果不是T2准备运行 就一直返回空，直到T2运行打印，打印完之后把准备运行的变为T1
                while (r!=ReadyToRun.T2){}
                System.out.println(c);
                r=ReadyToRun.T1;
            }

        },"t1").start();

    }
```
结果

```
1
A
2
B
3
C
4
D
5
E
6
F
7
G

```

### 方法三 原子类 AtomicInteger

```
public class TestLockSupport {

    
    //定义一个原子性的对象
    static AtomicInteger thredNo=new AtomicInteger(1);

    public static void main(String[] args) {


        char[] aI="1234567".toCharArray();
        char[] aC="ABCDEFG".toCharArray();


        new Thread(()->{
            for (char c : aI) {
                //如果不是1就一直返回空，直到运行打印，打印完之后把原子对象变成2
                while (thredNo.get()!=1){}
                System.out.println(c);
                thredNo.set(2);
            }

        },"t1").start();


        new Thread(()->{
            for (char c : aC) {
                //如果不是2就一直返回空，直到运行打印，打印完之后把原子对象变成1
                while (thredNo.get()!=2){}
                System.out.println(c);
                thredNo.set(1);
            }

        },"t1").start();

    }
}

```
### 方法四 也是面试官 想考你的 synchronized wait notiyfy


```
    public static void main(String[] args) {
    final  Object o=new Object();

        char[] aI="1234567".toCharArray();
        char[] aC="ABCDEFG".toCharArray();


        new Thread(()->{
            synchronized (o){
                for (char c : aI) {
                    try {
                        System.out.println(c);
                        o.wait();
                        o.notify();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                o.notify();
            }


        },"t1").start();


        new Thread(()->{
            synchronized (o){
                for (char c : aC) {
                    System.out.println(c);
                    o.notify();
                    try {
                        o.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                o.notify();
            }


        },"t1").start();

    }
```
## 结尾

多线程的基础就讲到这里了，大家看完这些应该能够知道，线程的基本概况，接下来我们看看并发的锁吧

> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
