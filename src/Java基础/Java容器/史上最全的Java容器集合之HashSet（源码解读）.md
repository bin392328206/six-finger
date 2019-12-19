# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
>今天讲Set,List,Map已经讲完了，Set有时候我们也会用，去重，但是Stream流也有去重的方法所以现在也是用的少了，我这边打算就讲一个HashSet,有机会把函数式编程Stream写一下，巩固一下 哈哈。  
[🔥史上最全的Java容器集合之入门](https://juejin.im/post/5de87a92e51d4557ec02f39d)    
[🔥史上最全的Java容器集合之基础数据结构（手撕链表）](https://juejin.im/post/5de8cdb5f265da33c34e2719)   
[🔥史上最全的Java容器集合之ArrayList(源码解读)](https://juejin.im/post/5de9f222f265da33b12e9600)  
[🔥史上最全的Java容器集合之Vector和LinkedList(源码解读)](https://juejin.im/post/5deb0b26e51d4557e87fc398)  
[🔥史上最全的Java容器集合之equals 和 hashCode ](https://juejin.im/post/5decc9fa518825124a05afd8)  
[🔥史上最全的Java容器集合之HashMap（源码解读）](https://juejin.im/post/5dedb448f265da33b071716a)    
[🔥史上最全的Java容器集合之ConcurrentHashMap（源码解读）](https://juejin.im/post/5dee17adf265da33942a7798)  
[🔥史上最全的Java容器集合之LinkedHashMap（源码解读）](https://juejin.im/post/5def59a36fb9a0162712765e)


## HashSet概述
HashSet的代码行数 300多行，是我见过最少代码的集合了，非常简单，它有以下的特点
- 没有重复元素的集合。底层基于HashMap来实现。
- 非线程安全，创建线程安全的HashMap可以使用Collections.synchronizedSet。
## HashSet源码解读

### 继承关系


![](https://user-gold-cdn.xitu.io/2019/12/11/16ef3c98379d4885?w=896&h=798&f=png&s=48663)

- 继承AbstractSet抽象类，实现Set接口
- 实现java.io.Serialization接口，支持序列化
- 实现Cloneable接口，支持对象克隆，浅复制


### 属性

```
     //使用HashMap来保存HashSet的元素
    private transient HashMap<E,Object> map;

    //HashSet只使用到key，因此使用一个静态常量来充当HashSet的value值
    private static final Object PRESENT = new Object();
```

### 构造方法


```
  //使用默认容量大小16以及加载因子0.75初始化HashMap，构造HashSet
    public HashSet() {
        map = new HashMap<>();
    }

    //初始化指定集合和默认加载因子0.75的map，构造HashSet
    public HashSet(Collection<? extends E> c) {
        map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
        addAll(c);
    }

    //使用指定容量大小和加载因子初始化map，构造HashSet
    public HashSet(int initialCapacity, float loadFactor) {
        map = new HashMap<>(initialCapacity, loadFactor);
    }

    //使用指定容量大小和默认加载因子0.75初始化map，构造HashSet
    public HashSet(int initialCapacity) {
        map = new HashMap<>(initialCapacity);
    }

    //包访问权限，构造空的LinkedHashSet
    HashSet(int initialCapacity, float loadFactor, boolean dummy) {
        map = new LinkedHashMap<>(initialCapacity, loadFactor);
```

### 方法

- size()方法：内部调用Map的size方法。

```
    public int size() {
        return map.size();
    }
```
- isEmpty()方法，也是调用map,里面是通过判断size是否为0来判断是否为null

```
 public boolean isEmpty() {
        return map.isEmpty();
    }
```

- add()方法
HashSet的add方法内部通过HashMap.put()方法来实现key的添加，在HashMap内部真正执行的是putVal()方法,putVal这边就不说了 HashMap讲过


```
    public boolean add(E e) {
        return map.put(e, PRESENT)==null;
    }
```


## 总结
HashSet 还真没啥讲了 ，就是把HashMap的key 拿来当数据存，基本上都是调用的是HashMap的方法,所以说讲的也少，总结一下吧
- HashSet的实现支持null的key，同时HashSet的内部不支持重复的key
- 不重复
- 线程不安全


## 彩蛋
因为篇幅实在是太少了，TreeSet的底层是TreeMap,红黑树，这个知识点，我先欠着哈哈，所以也不算讲了。也就意味着Java容器要完结了，至少我们把大多数常用的容器过了一遍了，就算是面试也不至于说一问三不知了。

我们知道 ArrayList LinkedList HashMap HashSet 都是线程不安全的，但是有一个类可以让他们变的线程安全，它就是 Collections

- Collections.synchronizedList
- Collections.synchronizedMap
- Collections.synchronizedSet

这个彩蛋就是我们来解密一下这个方法，挑选其中一个来讲 

### Collections.synchronizedList(List list) 方法源码


正常情况下，Collections.synchronizedList(List list) 返回的是一个 SynchronizedList 的对象，这个对象以组合的方式将对 List 的接口方法操作，委托给传入的 list 对象，并且对所有的接口方法对象加锁，得到并发安全性。

```
public static <T> List<T> synchronizedList(List<T> list) {
        return (list instanceof RandomAccess ?
                new SynchronizedRandomAccessList<>(list) :
                new SynchronizedList<>(list));
}

```

我们可以看出，他其实就是new 了一个SynchronizedList ,那这个类又是何方神圣呢，继续看


```
static class SynchronizedList<E>
        extends SynchronizedCollection<E>
        implements List<E> {
        ...
        ...
        final List<E> list;

        SynchronizedList(List<E> list) {
            super(list);
            this.list = list;
        }
        SynchronizedList(List<E> list, Object mutex) {
            super(list, mutex);
            this.list = list;
        }

        ...
        ...
        public E get(int index) {
            synchronized (mutex) {return list.get(index);}
        }
        public E set(int index, E element) {
            synchronized (mutex) {return list.set(index, element);}
        }
        public void add(int index, E element) {
            synchronized (mutex) {list.add(index, element);}
        }
        public E remove(int index) {
            synchronized (mutex) {return list.remove(index);}
        }
    ...
    ...   
 }

```

可以看到，SynchronizedList 的实现里，get, set, add 等操作都加了 mutex 对象锁，再将操作委托给最初传入的 list。
这就是以组合的方式，将非线程安全的对象，封装成线程安全对象，而实际的操作都是在原非线程安全对象上进行，只是在操作前给加了同步锁。

由于有很多业务场景下都有这种需求，所以 Java 类库中封装了这个工具类，给需要的模块使用。

但是值得一提的是，要是论Map的安全集合，还是ConcurrentHashMap效率比较高点，毕竟对象锁的粒度也是比较粗了。


### 版本说明
- 这里的源码是JDK8版本，不同版本可能会有所差异，但是基本原理都是一样的。

## 结尾

> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**神人**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
