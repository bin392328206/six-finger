# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
>Map家族最后一讲了，前面已经讲了最常用的HashMap,讲了线程安全的ConcurrentHashMap,今年就来看看有序的LinkedHashMap   
[🔥史上最全的Java容器集合之入门](https://juejin.im/post/5de87a92e51d4557ec02f39d)    
[🔥史上最全的Java容器集合之基础数据结构（手撕链表）](https://juejin.im/post/5de8cdb5f265da33c34e2719)   
[🔥史上最全的Java容器集合之ArrayList(源码解读)](https://juejin.im/post/5de9f222f265da33b12e9600)  
[🔥史上最全的Java容器集合之Vector和LinkedList(源码解读)](https://juejin.im/post/5deb0b26e51d4557e87fc398)  
[🔥史上最全的Java容器集合之equals 和 hashCode ](https://juejin.im/post/5decc9fa518825124a05afd8)  
[🔥史上最全的Java容器集合之HashMap（源码解读）](https://juejin.im/post/5dedb448f265da33b071716a)    
[🔥史上最全的Java容器集合之ConcurrentHashMap（源码解读）](https://juejin.im/post/5dee17adf265da33942a7798)



## 概述
LinkedHashMap是HashMap的子类，它的大部分实现与HashMap相同，两者最大的区别在于，HashMap的对哈希表进行迭代时是无序的，而 LinkedHashMap对哈希表迭代是有序的，LinkedHashMap默认的规则是，迭代输出的结果保持和插入key-value pair的顺序一致（当然具体迭代规则可以修改）。LinkedHashMap除了像HashMap一样用数组、单链表和红黑树来组织数据外，还额外维护了一个 双向链表，每次向linkedHashMap插入键值对，除了将其插入到哈希表的对应位置之外，还要将其插入到双向循环链表的尾部。

下面是它的数据结构：


![](https://user-gold-cdn.xitu.io/2019/12/11/16ef3126d9329cbd?w=574&h=305&f=png&s=26236)

我们可以看出它是由 数组 + 一个单项链表+一个双向链表组成。在原HashMap的数据结构基础上加一个双向链表


## LinkedHashMap

### 继承结构

![](https://user-gold-cdn.xitu.io/2019/12/10/16eef25c786eab59?w=715&h=431&f=png&s=23266)

LinkedHashMap也就是继承HashMap,所以HashMap大都方法，它是可以直接使用的。


![](https://user-gold-cdn.xitu.io/2019/12/10/16eef278e2920d69?w=568&h=471&f=png&s=67011)
顶部的注释总结一下：
- 底层是散列表和双向链表
- 允许为null，不同步
- 插入的顺序是有序的(底层链表致使有序)
- 装载因子和初始容量对LinkedHashMap影响是很大的~
### 基本属性


```
         //双向链表的头节点
    transient LinkedHashMap.Entry<K,V> head;
    
     //双向链表的尾节点
    transient LinkedHashMap.Entry<K,V> tail;

   //排序的规则，false按插入顺序排序，true访问顺序排序 
    final boolean accessOrder;
    
    //所以说accessOrder的作用就是控制访问顺序，设置为true后每次访问一个元素，就将该元素所在的Node变成最后一个节点，
    改变该元素在LinkedHashMap中的存储顺序。


```

### 构造方法

```
//LinkedHashMap的构造方法，都是通过调用父类的构造方法来实现，大部分accessOrder默认为false
public LinkedHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
    accessOrder = false;
}
public LinkedHashMap(int initialCapacity) {
    super(initialCapacity);
    accessOrder = false;
}
public LinkedHashMap() {
    super();
    accessOrder = false;
}
public LinkedHashMap(Map<? extends K, ? extends V> m) {
    super(m);
    accessOrder = false;
}
public LinkedHashMap(int initialCapacity,
                         float loadFactor,
                         boolean accessOrder) {
    super(initialCapacity, loadFactor);
    this.accessOrder = accessOrder;
}
```
上面是LinkedHashMap的构造方法，通过传入初始化参数和代码看出，LinkedHashMap的构造方法和父类的构造方法，是一一对应的。也是通过super()关键字来调用父类的构造方法来进行初始化，唯一的不同是最后一个构造方法，提供了AccessOrder参数，用来指定LinkedHashMap的排序方式，accessOrder =false -> 插入顺序进行排序 ， accessOrder = true -> 访问顺序进行排序。



### Entry定义
这个比较重要

```
private static class Entry<K,V> extends HashMap.Entry<K,V> {
    //定义Entry类型的两个变量，或者称之为前后的两个指针    
    Entry<K,V> before, after;
    //构造方法与HashMap的没有区别，也是调用父类的Entry构造方法
    Entry(int hash, K key, V value, HashMap.Entry<K,V> next) {
            super(hash, key, value, next);
    }

    //删除
    private void remove() {
        before.after = after;
        after.before = before;
    }

    //插入节点到指定的节点之前
    private void addBefore(Entry<K,V> existingEntry) {
        after  = existingEntry;
        before = existingEntry.before;
        before.after = this;
        after.before = this;
    }

    //方法重写，HashMap中为空
    void recordAccess(HashMap<K,V> m) {
        LinkedHashMap<K,V> lm = (LinkedHashMap<K,V>)m;
        if (lm.accessOrder) {
            lm.modCount++;
            remove();
            addBefore(lm.header);
        }
    }
    //方法重写 ，HashMap中方法为空
    void recordRemoval(HashMap<K,V> m) {
        remove();
    }
}
```
到这里已经可以知道，相对比HashMap，LinkedHashMap内部不光是使用HashMap中的哈希表来存储Entry对象，还另外维护了一个LinkedHashMapEntry，这些LinkedHashMapEntry内部又保存了前驱跟后继的引用，可以确定这是个双向链表。而这个LinkedHashMapEntry提供了对象的增加删除方法都是去更改节点的前驱后继指向。


### put()方法
LinkedHashMap并没有重写父类的put()方法，说明调用put方法时实际上调用的是父类的put方法。HashMap的put这里就不多说了中有这样一个方法

![](https://user-gold-cdn.xitu.io/2019/12/11/16ef3162d201805c?w=814&h=693&f=png&s=98225)

LinkedHashMap 重写了2个方法 



```
ode<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
        LinkedHashMap.Entry<K,V> p =
            new LinkedHashMap.Entry<K,V>(hash, key, value, e);
        linkNodeLast(p);
        return p;
    }
    
        private void linkNodeLast(LinkedHashMap.Entry<K,V> p) {
        //用临时变量last记录尾节点tail
        LinkedHashMap.Entry<K,V> last = tail;
        //将尾节点设为当前插入的节点p
        tail = p;
        //如果原先尾节点为null，表示当前链表为空
        if (last == null)
            //头结点也为当前插入节点
            head = p;
        else {
            //原始链表不为空，那么将当前节点的上节点指向原始尾节点
            p.before = last;
            //原始尾节点的下一个节点指向当前插入节点
            last.after = p;
        }
    }
```

```
    //把当前节点放到双向链表的尾部
 2     void afterNodeAccess(HashMap.Node<K,V> e) { // move node to last
 3         LinkedHashMap.Entry<K,V> last;
 4         //当 accessOrder = true 并且当前节点不等于尾节点tail。这里将last节点赋值为tail节点
 5         if (accessOrder && (last = tail) != e) {
 6             //记录当前节点的上一个节点b和下一个节点a
 7             LinkedHashMap.Entry<K,V> p =
 8                     (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
 9             //释放当前节点和后一个节点的关系
10             p.after = null;
11             //如果当前节点的前一个节点为null
12             if (b == null)
13                 //头节点=当前节点的下一个节点
14                 head = a;
15             else
16                 //否则b的后节点指向a
17                 b.after = a;
18             //如果a != null
19             if (a != null)
20                 //a的前一个节点指向b
21                 a.before = b;
22             else
23                 //b设为尾节点
24                 last = b;
25             //如果尾节点为null
26             if (last == null)
27                 //头节点设为p
28                 head = p;
29             else {
30                 //否则将p放到双向链表的最后
31                 p.before = last;
32                 last.after = p;
33             }
34             //将尾节点设为p
35             tail = p;
36             //LinkedHashMap对象操作次数+1，用于快速失败校验
37             ++modCount;
38         }
39     }
```
通过重写put方法，来维护一个双向链表，使得存取有序。。
### get()方法

```
public V get(Object key) {
    Entry<K,V> e = (Entry<K,V>)getEntry(key); //调用父类的getEntry()方法
    if (e == null)
        return null;
    e.recordAccess(this);  //判断排序方式，如果accessOrder = true , 删除当前e节点
    return e.value;
}
```
　　相比于 HashMap 的 get 方法，这里多出了第 5,6行代码，当 accessOrder = true 时，即表示按照最近访问的迭代顺序，会将访问过的元素放在链表后面。


## 总结
LinkedHashMap比HashMap多了一个双向链表的维护，在数据结构而言它要复杂一些，阅读源码起来比较轻松一些，因为大多都由HashMap实现了..

阅读源码的时候我们会发现多态是无处不在的~子类用父类的方法，子类重写了父类的部分方法即可达到不一样的效果！

比如：LinkedHashMap并没有重写put方法，而put方法内部的newNode()方法重写了。LinkedHashMap调用父类的put方法，里面回调的是重写后的newNode()，从而达到目的！

LinkedHashMap可以设置两种遍历顺序：

- 访问顺序（access-ordered）

- 插入顺序（insertion-ordered）

- 默认是插入顺序的

对于访问顺序，它是LRU(最近最少使用)算法的实现，要使用它要么重写LinkedListMap的几个方法(removeEldestEntry(Map.Entry<K,V> eldest)和afterNodeInsertion(boolean evict))，要么是扩展成LRUMap来使用，不然设置为访问顺序（access-ordered）的用处不大，对于LinkedHashMap的LRU这边没有深入了，因为确实用的不多，但是大家有兴趣的可以深入研究。

LinkedHashMap遍历的是内部维护的双向链表，所以说初始容量对LinkedHashMap遍历是不受影响的


一句话。LinkedHashMap就是继承HashMap的加上双向链表的HashMap，所以讲的也不多

### 版本说明
- 这里的源码是JDK8版本，不同版本可能会有所差异，但是基本原理都是一样的。

- Map家族讲完，整个集合体系，还差一个Set。明天开始Set

> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
