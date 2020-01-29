# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
说实话，我不知道跟着书写是不是对的，因为确实很多东西，我们很难接触得到，学习起来，就没有那么多的耐心了，不过也写了那么多了，把这本书写完吧，之后换个方式  
- [🔥史上最全的Java并发系列之并发编程的挑战](https://juejin.im/post/5dfb1ca26fb9a0160b63827f)  
- [🔥史上最全的Java并发系列之Java并发机制的底层实现原理](https://juejin.im/post/5dfb3a27e51d4558181d35b0)    
- [🔥史上最全的Java并发系列之Java内存模型](https://juejin.im/post/5dfc3dadf265da339b5001dd)  
- [🔥史上最全的Java并发系列之Java多线程（一）](https://juejin.im/post/5dfc9106518825126e63a711) 
- [🔥史上最全的Java并发系列之Java多线程（二）](https://juejin.im/post/5dfeeed6e51d45582248e4a5)   
- [🔥史上最全的Java并发系列之Java中的锁的使用和实现介绍（一）](https://juejin.im/post/5e002416e51d45583b43939d) 
- [🔥史上最全的Java并发系列之Java中的锁的使用和实现介绍（二）](https://juejin.im/post/5e0037a651882512670ee0b5)  
- [🔥史上最全的Java并发系列之Java并发容器和框架](https://juejin.im/post/5e005f746fb9a016253c15d5)

## 简介

当程序更新一个变量时，如果多线程同时更新这个变量，可能得到期望之外的值。
比如变量 i = 1，A 线程更新 i+1，B 线程也更新i+1，经过两个线程操作之后可能 i 不等于 3，而是等于 2 。
因为 A 和 B 线程在更新变量 i 的时候拿到的 i 都是 1，这就是 线程不安全的更新操作，通常我们会使用 synchronized 来解决这个问题，synchronized 会保证多线程不会同时更新变量 i。

而 Java 从 JDK 1.5 开始提供了 java.util.concurrent.atomic 包（以下简称Atomic包），这个包中的 原子操作类 提供了一种用法简单、性能高效、线程安全地更新一个变量的方式。

因为变量的类型有很多种，所以在 Atomic 包里一共提供了 12个 类，属于以下 4 种类型的原子更新方式：

- 原子更新基本类型。
    - AtomicBoolean：原子更新布尔类型。
    - AtomicInteger：原子更新整型。
    - AtomicLong：原子更新长整型。
- 原子更新数组。
    - AtomicIntegerArray：原子更新整型数组里的元素。
    - AtomicLongArray：原子更新长整型数组里的元素。
    - AtomicReferenceArray：原子更新引用类型数组里的元素。
- 原子更新引用。
    - AtomicReference：原子更新对象引用。
    - AtomicMarkableReference：原子更新带有标记位的对象引用。
    - AtomicStampedReference：原子更新带有版本号的对象引用。
- 原子更新属性（字段）。
    - AtomicIntegerFieldUpdater：原子更新volatile修饰的整型的字段的更新器。
    - AtomicLongFieldUpdater：原子更新volatile修饰的长整型字段的更新器。
    - AtomicReferenceFieldUpdater：原子更新volatile修饰的引用类型里的字段的更新器。
    - Atomic 包里的类基本都是使用 Unsafe 实现的包装类。

### 原子更新基本类型

- AtomicBoolean：原子更新布尔类型。
- AtomicInteger：原子更新整型。
- AtomicLong：原子更新长整型。  

以上3个类提供的方法几乎一模一样，所以本节仅以 AtomicInteger 为例进行讲解。
AtomicInteger 的常用方法如下：

- int addAndGet(int delta)：以原子方式将输入的数值与实例中的值（AtomicInteger 里的 value）相加，并返回结果。
- boolean compareAndSet(int expect，int update)：如果输入的数值等于预期值，则以原子方式将该值设置为输入的值。
- int getAndIncrement()：以原子方式将当前值加1，注意，这里返回的是自增前的值。
- void lazySet(int newValue)：最终会设置成 newValue，使用 lazySet设置值后，可导致其他线程在之后的一小段时间内还是可以读到旧的值。
- int getAndSet(int newValue)：以原子方式设置为 newValue 的值，并返回旧值。

示例代码：


```
public static void main(String[] args) {
    AtomicInteger ai = new AtomicInteger(1);

    System.out.println("ai.get() = " + ai.get());

    System.out.println("ai.addAndGet(5) = " + ai.addAndGet(5));
    System.out.println("ai.get() = " + ai.get());

    System.out.println("ai.compareAndSet(ai.get(), 10) = " + ai.compareAndSet(ai.get(), 10));
    System.out.println("ai.get() = " + ai.get());

    System.out.println("ai.getAndIncrement() = " + ai.getAndIncrement());
    System.out.println("ai.get() = " + ai.get());

    ai.lazySet(8);
    System.out.println("ai.lazySet(8)");
    System.out.println("ai.get() = " + ai.get());

    System.out.println("ai.getAndSet(5) = " + ai.getAndSet(5));
    System.out.println("ai.get() = " + ai.get());
}
```

```
ai.get() = 1
ai.addAndGet(5) = 6
ai.get() = 6
ai.compareAndSet(ai.get(), 10) = true
ai.get() = 10
ai.getAndIncrement() = 10
ai.get() = 11
ai.lazySet(8)
ai.get() = 8
ai.getAndSet(5) = 8
ai.get() = 5
```
AtomicInteger 的 getAndIncrement()方法：

```
public final int getAndIncrement() {
    for (; ; ) {
        int current = get();
        int next = current + 1;
        if (compareAndSet(current, next)) {
            return current;
        }
    }
}
public final boolean compareAndSet(int expect, int update) {
    return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
}
```

- for 循环体的先取得 AtomicInteger 里存储的数值
- 对 AtomicInteger 的当前数值进行 +1 操作，
- 关键是调用 compareAndSet 方法来进行原子更新操作，该方法先检查 当前数值是否等于current ？
    - 等于意味着 AtomicInteger 的值没有被其他线程修改过，则将 AtomicInteger 的当前数值更新成 next的值。
    - 如果不等 compareAndSet 方法会返回 false，程序会进入 for 循环重新进行 compareAndSet 操作。

Atomic 包提供了 3 种基本类型的原子更新，但是 Java 的基本类型里还有 char、float 和 double 等。
那么问题来了，如何原子的更新其他的基本类型呢？
Atomic包里的类基本都是使用 Unsafe 实现的，让我们一起看一下Unsafe的源码：


```
/**
 * 如果当前数值是expected，则原子的将Java变量更新成x
 *
 * @return 如果更新成功则返回true
 */
public final native boolean compareAndSwapObject(Object o, long offset,
                                                 Object expected, Object x);

public final native boolean compareAndSwapInt(Object o, long offset,
                                              int expected, int x);

public final native boolean compareAndSwapLong(Object o, long offset,
                                               long expected, long x);
```
通过以上代码，我们发现 Unsafe 只提供了 3 种 CAS 方法：compareAndSwapObject、compareAndSwapInt 和 compareAndSwapLong，再看 AtomicBoolean 源码，发现它是先把 Boolean 转换成 整型，再使用 compareAndSwapInt 进行 CAS，所以原子更新 char、float 和 double 变量也可以用类似的思路来实现

### 原子更新数组
AtomicIntegerArray 类主要是提供原子的方式更新数组里的整型。

常用方法如下：

- int addAndGet(int i，int delta)：以原子方式将输入值与数组中索引i的元素相加。
- boolean compareAndSet(int i，int expect，int update)：如果当前值等于预期值，则以原子方式将数组位置i的元素设置成update值。

```
public static void main(String[] args) {

    int[] value = new int[]{1, 2};

    AtomicIntegerArray ai = new AtomicIntegerArray(value);

    System.out.println("ai.getAndSet(0, 3)");
    ai.getAndSet(0, 3);
    System.out.println("ai.get(0) = " + ai.get(0));
    System.out.println("value[0] = " + value[0]);

    ai.compareAndSet(1, 2, 5);
    System.out.println("ai.compareAndSet(1, 2, 5)");
    System.out.println("ai.get(1) = " + ai.get(1));
}
```

```
ai.getAndSet(0, 3)
ai.get(0) = 3
value[0] = 1
ai.compareAndSet(1,2,5)
ai.get(1) = 5
```
> 需要注意的是，数组value通过构造方法传递进去，然后AtomicIntegerArray会将当前数组复制一份，所以当AtomicIntegerArray对内部的数组元素进行 修改 时，不会影响传入的数组。
### 原子更新引用
- AtomicReference：原子更新对象引用。
- AtomicMarkableReference：原子更新带有标记位的对象引用。
- AtomicStampedReference：原子更新带有版本号的对象引用。该类将整数值与引用关联起来，可用于原子的更新数据和数据的版本号，可以解决使用 CAS 进行原子更新时可能出现的 ABA问题。

以上几个类提供的方法几乎一样，所以仅以AtomicReference为例进行介绍。

例子

```
public class AtomicReferenceTest {
    public static AtomicReference<User> atomicUserRef = new
            AtomicReference<User>();

    public static void main(String[] args) {
        User user = new User("103style", 20);
        atomicUserRef.set(user);
        System.out.println("atomicUserRef.get() = " + atomicUserRef.get().toString());

        User updateUser = new User("xiaoke", 22);
        atomicUserRef.compareAndSet(user, updateUser);

        System.out.println("atomicUserRef.compareAndSet(user, updateUser);");

        System.out.println("atomicUserRef.get() = " + atomicUserRef.get().toString());
    }

    static class User {
        private String name;
        private int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "name='" + name + ", age=" + age;
        }
    }
}
```

```
atomicUserRef.get() = name='103style, age=20
atomicUserRef.compareAndSet(user, updateUser);
atomicUserRef.get() = name='xiaoke, age=22
```


原子更新属性（字段）
- AtomicIntegerFieldUpdater：原子更新volatile修饰的整型的字段的更新器。
- AtomicLongFieldUpdater：原子更新volatile修饰的长整型字段的更新器。
- AtomicReferenceFieldUpdater：原子更新volatile修饰的引用类型里的字段的更新器。

要想原子地更新字段类需要两步：
- 因为原子更新字段类都是抽象类，每次使用的时候必须使用静态方法newUpdater()创建一个更新器，并且需要设置想要更新的类和属性。   
- 更新类的字段（属性）必须使用public volatile修饰符。  

以上3个类提供的方法几乎一样，所以仅以 AstomicIntegerFieldUpdater 为例进行讲解。


```
public class AtomicIntegerFieldUpdaterTest {

    public static void main(String[] args) {
        // 创建原子更新器,并设置需要更新的对象类和对象的属性
        AtomicIntegerFieldUpdater<User> a = AtomicIntegerFieldUpdater.
                newUpdater(User.class, "age");
        // 设置柯南的年龄是10岁
        User conan = new User("conan", 10);
        // 柯南长了一岁,但是仍然会输出旧的年龄
        System.out.println(a.getAndIncrement(conan));
        // 输出柯南现在的年龄
        System.out.println(a.get(conan));
    }

    public static class User {
        public volatile int age;
        private String name;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
}
```

```
10
11
```
## 本章小结
本章介绍了JDK中并发包里的13个原子操作类以及原子操作类的实现原理，读者需要熟 悉这些类和使用场景，在适当的场合下使用它。


## 结尾
因为很多东西，全是从书上拷贝的，很枯燥，但同时看书，又是最详细的学习方法之一了，大家跟着书看博客，或许会好点吧.


> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！

