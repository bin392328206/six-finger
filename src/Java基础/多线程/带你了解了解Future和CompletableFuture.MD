# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206                          
> **种一棵树最好的时间是十年前，其次是现在**   

## 絮叨
并发 多线程 异步 编程一直是我们开发人员的一个难点，因为我们基本上大部分的业务流程都可以用同步的方式处理，久而久之，其实很多可以用异步多线程优化的场景也会因为我们的不熟悉而不去使用，而是把系统优化的能力放到了缓存，数据库的 JVM的优化上了。所以呢？今天就跟大家来聊聊Future和CompletableFuture


## Future模式
Java 1.5开始，提供了Callable和Future，通过它们可以在任务执行完毕之后得到任务执行结果。
Future接口可以构建异步应用，是多线程开发中常见的设计模式。
当我们需要调用一个函数方法时。如果这个函数执行很慢,那么我们就要进行等待。但有时候,我们可能并不急着要结果。
因此,我们可以让被调用者立即返回,让他在后台慢慢处理这个请求。对于调用者来说,则可以先处理一些其他任务,在真正需要数据的场合再去尝试获取需要的数据。


![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/89130a25b5824b35aec0e3b5ad2d0710~tplv-k3u1fbpfcp-watermark.image)

大家从图中可以看出，我们其实就是并行的去做一些事情，这样我们就可以利用多核cpu的优势，来减少系统的消化的时间。

#### Callable与Runnable
java.lang.Runnable是一个接口，在它里面只声明了一个run()方法，run返回值是void，任务执行完毕后无法返回任何结果

```
public interface Runnable {
    public abstract void run();
}
```

Callable位于java.util.concurrent包下，它也是一个接口，在它里面也只声明了一个方法叫做call()，这是一个泛型接口，call()函数返回的类型就是传递进来的V类型

```
public interface Callable<V> {
    V call() throws Exception;
}
```

#### Future + Callable

Future就是对于具体的Runnable或者Callable任务的执行结果进行取消、查询是否完成、获取结果。必要时可以通过get方法获取执行结果，该方法会阻塞直到任务返回结果

```
public interface Future<V> {
    boolean cancel(boolean mayInterruptIfRunning);
    boolean isCancelled();
    boolean isDone();
    V get() throws InterruptedException, ExecutionException;
    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```

##### 示例代码

```
package com.xiaoliuliu.test.jdk;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/10 10:50
 */
public class A {
    public static void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Integer> result = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return new Random().nextInt();
            }
        });
        //shutdown调用后，不可以再submit新的task，已经submit的将继续执行。
        executor.shutdown();

        try {
            System.out.println("result:" + result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
```

```
package com.xiaoliuliu.test.jdk;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/10 10:50
 */
public class A {
    public static void main(String[] args) throws Exception {

        //第一种方式
        FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return new Random().nextInt();
            }
        });
        new Thread(task).start();
        //第二种方方式
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<Integer> task1 = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return new Random().nextInt();
            }
        });
        executor.submit(task1);

        try {
            System.out.println("result: "+task.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
    
```

#### Future 总结

- 首先呢？future 是多线程有返回结果的一种，它的使用方式，第一种就是callback,第二种就是futureTask
- 然后就是它的局限性了，Future很难直接表述多个Future 结果之间的依赖性，开发中，我们经常需要达成以下目的：将两个异步计算合并为一个（这两个异步计算之间相互独立，同时第二个又依赖于第一个的结果）等待 Future 集合中的所有任务都完成。仅等待 Future 集合中最快结束的任务完成，并返回它的结果。


#### 来看个案例

一个烧水泡茶的例子
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/17a67d6358984e19b82984807307c4a3~tplv-k3u1fbpfcp-watermark.image)

#### 案例的代码

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/797028bcbcec49f09f3fa05f5c570b57~tplv-k3u1fbpfcp-watermark.image)

- 线程T2
为啥先写T2呢？因为T2是需要先完成的，完成的结果要给到T1线程
```
package com.xiaoliuliu.test.jdk;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/10 11:14
 */
public class T2Task implements Callable<String> {

    @Override
    public String call() throws Exception {
        System.out.println("T2:洗茶壶...");
        TimeUnit.SECONDS.sleep(1);
        System.out.println("T2:洗茶杯...");
        TimeUnit.SECONDS.sleep(2);
        System.out.println("T2:拿茶叶...");
        TimeUnit.SECONDS.sleep(1);
        return "龙井";
    }
}
```
- 线程T1
T1的构造方法还得注入t2

```
package com.xiaoliuliu.test.jdk;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/10 11:15
 */
public class T1Task implements Callable<String> {

    FutureTask<String> ft2; // T1任务需要T2任务的FutureTask

    T1Task(FutureTask<String> ft2) {
        this.ft2 = ft2;
    }

    @Override
    public String call() throws Exception {
        System.out.println("T1:洗水壶...");
        TimeUnit.SECONDS.sleep(1);
        System.out.println("T1:烧开水...");
        TimeUnit.SECONDS.sleep(15);
        // 获取T2线程的茶叶
        //总结总利用Java并发包提供的Future可以很容易获得异步任务的执行结果，无论异步任务是通过线程池 ThreadPoolExecutor执行的，还是通过手工创建子线程来执行的。
        //Future可以类比为现实世界里 // 获取T2线程的茶叶
        String tf = ft2.get();
        System.out.println("T1:拿到茶叶:" + tf);
        System.out.println("T1:泡茶...");
        return "上茶:" + tf;
    }
}
```
- 测试类

```
package com.xiaoliuliu.test.jdk;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/10 10:50
 */
public class A {
    public static void main(String[] args) throws Exception {

        FutureTask<String> ft2 = new FutureTask<>(new T2Task());

        FutureTask<String> ft1 = new FutureTask<>(new T1Task(ft2));
        // 线程T1执行任务ft1
        Thread T1 = new Thread(ft1);
        T1.start();

        Thread T2 = new Thread(ft2); T2.start();

        System.out.println(ft1.get());
    }
}

```

#### 结果

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/bd70daadfff34a018da963261b90ebd4~tplv-k3u1fbpfcp-watermark.image)

这个就是并行的玩法。


## CompletableFuture
首先，CompletableFuture类实现了CompletionStage和Future接口，因此你可以像Future那样使用它。
莫急，下面通过例子来一步一步解释CompletableFuture的使用。

### 创建CompletableFuture对象

说明：Async结尾的方法都是可以异步执行的，如果指定了线程池，会在指定的线程池中执行，如果没有指定，默认会在ForkJoinPool.commonPool()中执行。下面很多方法都是类似的，不再做特别说明。
四个静态方法用来为一段异步执行的代码创建CompletableFuture对象，方法的参数类型都是函数式接口，所以可以使用lambda表达式实现异步任务
runAsync方法：它以Runnabel函数式接口类型为参数，所以CompletableFuture的计算结果为空。
supplyAsync方法以Supplier<U>函数式接口类型为参数，CompletableFuture的计算结果类型为U。
```
public static CompletableFuture<Void> runAsync(Runnable runnable)
public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)
```
### 变换结果
```
public <U> CompletionStage<U> thenApply(Function<? super T,? extends U> fn);
public <U> CompletionStage<U> thenApplyAsync(Function<? super T,? extends U> fn);
public <U> CompletionStage<U> thenApplyAsync(Function<? super T,? extends U> fn,Executor executor);
```
这些方法的输入是上一个阶段计算后的结果，返回值是经过转化后结果

```
package com.xiaoliuliu.test.jdk;

import java.util.concurrent.CompletableFuture;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/10 11:40
 */
public class TestCompleteFuture {
    public static void main(String[] args){
        String result = CompletableFuture.supplyAsync(()->{return "Hello ";}).thenApplyAsync(v -> v + "world").join();
        System.out.println(result);
    }
}
```

其实上面的代码，本身就是同步的代码，也没必要写成异步的。

### 消费结果
```
public CompletionStage<Void> thenAccept(Consumer<? super T> action);
public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action);
public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action,Executor executor);
```

```
public class TestCompleteFuture {
    public static void main(String[] args){
        CompletableFuture.supplyAsync(()->{return "Hello ";}).thenAccept(v -> { System.out.println("consumer: " + v);});
    }
}
```

### 结合两个CompletionStage的结果，进行转化后返回

```
public <U,V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn);
public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn);
public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn,Executor executor)
```
需要上一阶段的返回值，并且other代表的CompletionStage也要返回值之后，把这两个返回值，进行转换后返回指定类型的值。
说明：同样，也存在对两个CompletionStage结果进行消耗的一组方法，例如thenAcceptBoth，这里不再进行示例。


```
package com.xiaoliuliu.test.jdk;

import java.util.concurrent.CompletableFuture;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/10 11:40
 */
public class TestCompleteFuture {
    public static void main(String[] args){


        String res = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "word";
        }), (s1, s2) -> {
            return s1 + " " + s2;
        }).join();
        System.out.println(res);
    }
}
```

### 案例

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e1f22f4001884ee3bda83d9c845d9877~tplv-k3u1fbpfcp-watermark.image)

还是上面的案例，我们把它分为3步，三个线程，我们称为分治过程。

```
package com.xiaoliuliu.test.jdk;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/11/10 10:50
 */
public class A {
    public static void main(String[] args) throws Exception {

        //任务1：洗水壶->烧开水
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            try {
                System.out.println("T1:洗水壶...");
                Thread.sleep(1000);
                System.out.println("T1:烧开水...");
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //任务2：洗茶壶->洗茶杯->拿茶叶
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("T2:洗茶壶...");
                Thread.sleep(1000);
                System.out.println("T2:洗茶杯...");
                Thread.sleep(2000);
                System.out.println("T2:拿茶叶...");
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "龙井";
        });
        //任务3：任务1和任务2完成后执行：泡茶
        CompletableFuture<String> f3 = f1.thenCombine(f2, (__, tf) -> {
            System.out.println("T1:拿到茶叶:" + tf);
            System.out.println("T1:泡茶...");
            return "上茶:" + tf;
        });
        //等待任务3执行结果
        System.out.println(f3.join());

    }
}

```

一些基本的用法，就到这里了。


## 结尾
今天的一个多线程的小知识就到这里了，希望对大家有帮助。

![](//p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/14aafd3e9f1c4db8b62fe1e5baaaac4b~tplv-k3u1fbpfcp-zoom-1.image)
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！