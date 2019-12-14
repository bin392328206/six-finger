# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
> equals和hashCode理解了，接下来就是HashMap了，这个也是平时我们工作中用的最多的容器之一  
[🔥史上最全的Java容器集合之入门](https://juejin.im/post/5de87a92e51d4557ec02f39d)    
[🔥史上最全的Java容器集合之基础数据结构（手撕链表）](https://juejin.im/post/5de8cdb5f265da33c34e2719)   
[🔥史上最全的Java容器集合之ArrayList(源码解读)](https://juejin.im/post/5de9f222f265da33b12e9600)  
[🔥史上最全的Java容器集合之Vector和LinkedList(源码解读)](https://juejin.im/post/5deb0b26e51d4557e87fc398)  
[🔥史上最全的Java容器集合之equals 和 hashCode ](https://juejin.im/post/5decc9fa518825124a05afd8)   

因为HashMap的源码确实比List要多很多，这边也不能说面面俱到，只能挑一些重点的东西讲。


## 一 什么是Map
- Map是一个接口，他是key-value的键值对，一个map不能包含重复的key，并且每一个key只能映射一个value；

- Map接口提供了三个集合视图：key的集合，value的集合，key-value的集合；

- Map内元素的顺序取决于Iterator的具体实现逻辑，获取集合内的元素实际上是获取一个迭代器，实现对其中元素的遍历；

- Map接口的具体实现中存在三种Map结构，其中HashMap和TreeMap都允许存在null值，而HashTable的key不允许为空，但是HashMap不能保证遍历元素的顺序，TreeMap能够保证遍历元素的顺序。

## 二 HashMap中的几个概念
###  什么是哈希表 
   哈希表（HashTable，散列表）是根据key-value进行访问的数据结构，他是通过把key映射到表中的一个位置来访问value，加快查找的速度，其中映射的函数叫做散列函数，存放value的数组叫做散列表，哈希表的主干是数组。


![](https://user-gold-cdn.xitu.io/2019/12/9/16ee8a7061277003?w=748&h=407&f=png&s=16858)
上面的图中就是一个值插入哈希表中的过程，那么存在的问题就是不同的值在经过hash函数之后可能会映射到相同的位置上，当插入一个元素时，发现该位置已经被占用，这时候就会产生冲突，也就是所谓的哈希冲突，因此哈希函数的设计就至关重要，一个好的哈希函数希望尽可能的保证计算方法简单，但是元素能够均匀的分布在数组中，但是数组是一块连续的且是固定长度的内存空间，不管一个哈希函数设计的多好，都无法避免得到的地址不会发生冲突，因此就需要对哈希冲突进行解决。
（1）开放定址法：当插入一个元素时，发生冲突，继续检查散列表的其他项，直到找到一个位置来放置这个元素，至于检查的顺序可以自定义；

（2）再散列法：使用多个hash函数，如果一个发生冲突，使用下一个hash函数，直到找到一个位置，这种方法增加了计算的时间；

（3）链地址法：在数组的位置使用链表，将同一个hashCode的元素放在链表中，HashMap就是使用的这种方法，数组+链表的结构。


### 关于 HashMap 源码中提到的几个重要概念
- 哈希桶（buckets）：在 HashMap 的注释里使用哈希桶来形象的表示数组中每个地址位置。注意这里并不是数组本身，数组是装哈希桶的，他可以被称为哈希表。

- 初始容量(initial capacity) : 这个很容易理解，就是哈希表中哈希桶初始的数量。如果我们没有通过构造方法修改这个容量值默认为DEFAULT_INITIAL_CAPACITY = 1<<4 即16。值得注意的是为了保证 HashMap 添加和查找的高效性，HashMap 的容量总是 2^n 的形式。

- 加载因子(load factor)：加载因子是哈希表（散列表）在其容量自动增加之前被允许获得的最大数量的度量。当哈希表中的条目数量超过负载因子和当前容量的乘积时，散列表就会被重新映射（即重建内部数据结构），重新创建的散列表容量大约是之前散列表哈系统桶数量的两倍。默认加载因子（0.75）在时间和空间成本之间提供了良好的折衷。加载因子过大会导致很容易链表过长，加载因子很小又容易导致频繁的扩容。所以不要轻易试着去改变这个默认值。

- 扩容阈值（threshold）：其实在说加载因子的时候已经提到了扩容阈值了，扩容阈值 = 哈希表容量 * 加载因子。哈希表的键值对总数 = 所有哈希桶中所有链表节点数的加和，扩容阈值比较的是是键值对的个数而不是哈希表的数组中有多少个位置被占了。

- 树化阀值(TREEIFY_THRESHOLD) ：这个参数概念是在 JDK1.8后加入的，它的含义代表一个哈希桶中的节点个数大于该值（默认为8）的时候将会被转为红黑树行存储结构。

- 非树化阀值(UNTREEIFY_THRESHOLD)： 与树化阈值相对应，表示当一个已经转化为数形存储结构的哈希桶中节点数量小于该值（默认为 6）的时候将再次改为单链表的格式存储。导致这种操作的原因可能有删除节点或者扩容。

- 最小树化容量(MIN_TREEIFY_CAPACITY): 经过上边的介绍我们只知道，当链表的节点数超过8的时候就会转化为树化存储，其实对于转化还有一个要求就是哈希表的数量超过最小树化容量的要求（默认要求是 64）,且为了避免进行扩容、树形化选择的冲突，这个值不能小于 4 * TREEIFY_THRESHOLD);在达到该有求之前优先选择扩容。扩容因为因为容量的变化可能会使单链表的长度改变。


### HashMap的类图

![](https://user-gold-cdn.xitu.io/2019/12/9/16ee8a8c47ef56be?w=1148&h=509&f=png&s=28932)
HashMap继承自AbstractMap，实现了Map接口，Map接口定义了所有Map子类必须实现的方法。AbstractMap也实现了Map接口，并且提供了两个实现Entry的内部类：SimpleEntry和SimpleImmutableEntry。


 HashMap是基于哈希表的Map接口的实现，提供所有可选的映射操作，允许使用null值和null键，存储的对象时一个键值对对象Entry<K,V>，
是基于数组+链表的结构实现，在内部维护这一个数组table，数组的每个位置保存着每个链表的表头结点，查找元素时，先通过hash函数得到key值对应的hash值，再根据hash值得到在数组中的索引位置，拿到对应的链表的表头，最后去遍历这个链表，得到对应的value值。


![](https://user-gold-cdn.xitu.io/2019/12/9/16ee8ab2a0ab636f?w=1023&h=532&f=png&s=74611)

## HashMap类中的主要方法
### 常量

```
 // 默认的初始容量是16
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;   
    // 最大容量
    static final int MAXIMUM_CAPACITY = 1 << 30; 
    // 默认的填充因子
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // 当桶(bucket)上的结点数大于这个值时会转成红黑树
    static final int TREEIFY_THRESHOLD = 8; 
    // 当桶(bucket)上的结点数小于这个值时树转链表
    static final int UNTREEIFY_THRESHOLD = 6;
    // 桶中结构转化为红黑树对应的table的最小大小
    static final int MIN_TREEIFY_CAPACITY = 64;
    // 存储元素的数组，总是2的幂次倍
    transient Node<k,v>[] table; 
    // 存放具体元素的集
    transient Set<map.entry<k,v>> entrySet;
    // 存放元素的个数，注意这个不等于数组的长度。
    transient int size;
    // 每次扩容和更改map结构的计数器
    transient int modCount;   
    // 临界值 当实际大小(容量*填充因子)超过临界值时，会进行扩容
    int threshold;
    // 加载因子
    final float loadFactor;
```

### HashMap的Node实体

```
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next; // 指向下一个节点
    Node(int hash, K key, V value, Node<K,V> next) {
        this.hash = hash; // 哈希值，存放元素到hashmap中时用来与其他元素hash值比较
        this.key = key;
        this.value = value;
        this.next = next;
    }
    public final K getKey()        { return key; }
    public final V getValue()      { return value; }
    public final String toString() { return key + "=" + value; }
    // 重写hashCode()方法
    public final int hashCode() {
        return Objects.hashCode(key) ^ Objects.hashCode(value);
    }
    public final V setValue(V newValue) {
        V oldValue = value;
        value = newValue;
        return oldValue;
    }
    // 重写 equals() 方法
    public final boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof Map.Entry) {
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;
            if (Objects.equals(key, e.getKey()) &&Objects.equals(value, e.getValue()))
                return true;
            }
        return false;
    }
}
```
其中key值定义的为final，因此在定义之后就无法进行修改，key和value就是在调用map时对应的键值对，next存储的是链表中的下一个节点，他是一个单链表，hash是对key的hashcode再次进行哈希运算之后得到的值，存储起来是为了避免重复计算。

### HashMap的构造方法

```
/**
*使用默认的容量及装载因子构造一个空的HashMap
*/
public HashMap() {
    this.loadFactor = DEFAULT_LOAD_FACTOR;
}

/**
* 根据给定的初始容量和装载因子创建一个空的HashMap
* 初始容量小于0或装载因子小于等于0将报异常 
*/
public HashMap(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
        throw new IllegalArgumentException("Illegal initial capacity: " +initialCapacity);
    if (initialCapacity > MAXIMUM_CAPACITY)//调整最大容量
        initialCapacity = MAXIMUM_CAPACITY;
    if (loadFactor <= 0 || Float.isNaN(loadFactor))
        throw new IllegalArgumentException("Illegal load factor: " +loadFactor);
    this.loadFactor = loadFactor;
    //这个方法就是把容量控制在2的倍数
        this.threshold = tableSizeFor(initialCapacity);
        
}

/**
*根据指定容量创建一个空的HashMap
*/
public HashMap(int initialCapacity) {
    //调用上面的构造方法，容量为指定的容量，装载因子是默认值
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
}
//通过传入的map创建一个HashMap，容量为默认容量（16）和(map.zise()/DEFAULT_LOAD_FACTORY)+1的较大者，装载因子为默认值
public HashMap(Map<? extends K, ? extends V> m) {
    this.loadFactor = DEFAULT_LOAD_FACTOR;
    putMapEntries(m, false);
}
```
HashMap提供了四种构造方法：

（1）使用默认的容量及装载因子构造一个空的HashMap；

（2）根据给定的初始容量和装载因子创建一个空的HashMap；

（3）根据指定容量创建一个空的HashMap；

（4）通过传入的map创建一个HashMap。

第三种构造方法会调用第二种构造方法，而第四种构造方法将会调用putMapEntries方法将元素添加到HashMap中去。

putMapEntries方法是一个final方法，不可以被修改，该方法实现了将另一个Map的所有元素加入表中，参数evict初始化时为false，其他情况为true，我们来看看这个方法吧

```
final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
    int s = m.size();
    if (s > 0) {
        if (table == null) { 
        //根据m的元素数量和当前表的加载因子，计算出阈值
        float ft = ((float)s / loadFactor) + 1.0F;
        //修正阈值的边界 不能超过MAXIMUM_CAPACITY
        int t = ((ft < (float)MAXIMUM_CAPACITY) ?(int)ft : MAXIMUM_CAPACITY);
        //如果新的阈值大于当前阈值
        if (t > threshold)
            //返回一个>=新的阈值的 满足2的n次方的阈值
            threshold = tableSizeFor(t);
        }
        //如果当前元素表不是空的，但是 m的元素数量大于阈值，说明一定要扩容。
        else if (s > threshold)
            resize();
        //遍历 m 依次将元素加入当前表中。
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            K key = e.getKey();
            V value = e.getValue();
            putVal(hash(key), key, value, false, evict);
        }
    }
}
```
从中可以看出，它这个涉及了2个操作，一个是计算新的阈值，另一个是扩容方法

如果新的阈值大于当前阈值，需要返回一个>=新的阈值的 满足2的n次方的阈值，这涉及到了tableSizeFor：

```
  static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
```
如果当前元素表不是空的，但是 m的元素数量大于阈值，说明一定要扩容。这涉及到了扩容方法resize。最复杂的方法之一


```
final Node<K,V>[] resize() {
    //oldTab 为当前表的哈希桶
    Node<K,V>[] oldTab = table;
    //当前哈希桶的容量 length
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    //当前的阈值
    int oldThr = threshold;
    //初始化新的容量和阈值为0
    int newCap, newThr = 0;
    //如果当前容量大于0
    if (oldCap > 0) {
        //如果当前容量已经到达上限
        if (oldCap >= MAXIMUM_CAPACITY) {
            //则设置阈值是2的31次方-1
            threshold = Integer.MAX_VALUE;
            //同时返回当前的哈希桶，不再扩容
            return oldTab;
        }//否则新的容量为旧的容量的两倍。 
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
            oldCap >= DEFAULT_INITIAL_CAPACITY)
            //如果旧的容量大于等于默认初始容量16
            //那么新的阈值也等于旧的阈值的两倍
            newThr = oldThr << 1; // double threshold
    }
    //如果当前表是空的，但是有阈值。代表是初始化时指定了容量、阈值的情况
    else if (oldThr > 0) 
        newCap = oldThr;//那么新表的容量就等于旧的阈值
    else {    
    //如果当前表是空的，而且也没有阈值。代表是初始化时没有任何容量/阈值参数的情况               
        newCap = DEFAULT_INITIAL_CAPACITY;//此时新表的容量为默认的容量 16
    //新的阈值为默认容量16 * 默认加载因子0.75f = 12
        newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    if (newThr == 0) {
        //如果新的阈值是0，对应的是  当前表是空的，但是有阈值的情况
        float ft = (float)newCap * loadFactor;//根据新表容量 和 加载因子 求出新的阈值
        //进行越界修复
        newThr = (newCap < MAXIMUM_CAPACITY && ft <(float)MAXIMUM_CAPACITY ? (int)ft : Integer.MAX_VALUE);
    }
    //更新阈值 
    threshold = newThr;
    @SuppressWarnings({"rawtypes","unchecked"})
    //根据新的容量 构建新的哈希桶
    Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
    //更新哈希桶引用
    table = newTab;
    //如果以前的哈希桶中有元素
    //下面开始将当前哈希桶中的所有节点转移到新的哈希桶中
    if (oldTab != null) {
        //遍历老的哈希桶
        for (int j = 0; j < oldCap; ++j) {
        //取出当前的节点 e
        Node<K,V> e;
        //如果当前桶中有元素,则将链表赋值给e
        if ((e = oldTab[j]) != null) {
            //将原哈希桶置空以便GC
            oldTab[j] = null;
            //如果当前链表中就一个元素，（没有发生哈希碰撞）
            if (e.next == null)
            //直接将这个元素放置在新的哈希桶里。
            //注意这里取下标 是用 哈希值 与 桶的长度-1 。 由于桶的长度是2的n次方，这么做其实是等于 一个模运算。但是效率更高
            newTab[e.hash & (newCap - 1)] = e;
            //如果发生过哈希碰撞 ,而且是节点数超过8个，转化成了红黑树
            else if (e instanceof TreeNode)
                 ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
            //如果发生过哈希碰撞，节点数小于8个。则要根据链表上每个节点的哈希值，依次放入新哈希桶对应下标位置。
            else {
                //因为扩容是容量翻倍，所以原链表上的每个节点，现在可能存放在原来的下标，即low位，或者扩容后的下标，即high位。high位=low位+原哈希桶容量
                //低位链表的头结点、尾节点
                Node<K,V> loHead = null, loTail = null;
                //高位链表的头节点、尾节点
                Node<K,V> hiHead = null, hiTail = null;
                Node<K,V> next;//临时节点 存放e的下一个节点
                do {
                    next = e.next;
                　　//利用位运算代替常规运算：利用哈希值与旧的容量，可以得到哈希值去模后，是大于等于oldCap还是小于oldCap，等于0代表小于oldCap，应该存放在低位，否则存放在高位
                    if ((e.hash & oldCap) == 0) {
                        //给头尾节点指针赋值
                        if (loTail == null)
                            loHead = e;
                        else
                            loTail.next = e;
                        loTail = e;
                    }//高位也是相同的逻辑
                    else {
                        if (hiTail == null)
                            hiHead = e;
                        else
                            hiTail.next = e;
                        hiTail = e;
                        }//循环直到链表结束
                    } while ((e = next) != null);
                    //将低位链表存放在原index处
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    //将高位链表存放在新index处
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    return newTab;
}
```
resize的操作主要涉及以下几步操作：
- 如果到达最大容量，那么返回当前的桶，并不再进行扩容操作，否则的话扩容为原来的两倍，返回扩容后的桶；
- 根据扩容后的桶，修改其他的成员变量的属性值；
- 根据新的容量创建新的扩建后的桶，并更新桶的引用；
- 如果原来的桶里面有元素就需要进行元素的转移；
- 在进行元素转移的时候需要考虑到元素碰撞和转红黑树操作；
- 在扩容的过程中，按次从原来的桶中取出链表头节点，并对该链表上的所有元素重新计算hash值进行分配；
- 在发生碰撞的时候，将新加入的元素添加到末尾；
- 在元素复制的时候需要同时对低位和高位进行操作。

这段是借鉴人家的，确实很复杂，各种if else ，一点点去跟，也很累，但是大家至少也要知道它是怎么扩容的，几个重要的步骤要能说出来，面试的时候会问。

### HashMap的成员方法

#### put方法

```
//向哈希表中添加元素
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}
```
- 向用户开放的put方法调用的是putVal方法：
- putVal方法需要判断是否出现哈希冲突问题：
- 其中如果哈希值相等，key也相等，则是覆盖value操作；如果不是覆盖操作，则插入一个普通链表节点；
- 遍历到尾部，追加新节点到尾部；
- 在元素添加的过程中需要随时检查是否需要进行转换成红黑树的操作；


```
final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
    //tab存放当前的哈希桶，p用作临时链表节点  
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    //如果当前哈希表是空的，代表是初始化
    if ((tab = table) == null || (n = tab.length) == 0)
    //那么直接去扩容哈希表，并且将扩容后的哈希桶长度赋值给n
    n = (tab = resize()).length;
    //如果当前index的节点是空的，表示没有发生哈希碰撞。直接构建一个新节点Node，挂载在index处即可。
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    else {//否则 发生了哈希冲突。
        Node<K,V> e; K k;
        //如果哈希值相等，key也相等，则是覆盖value操作
        if (p.hash == hash &&((k = p.key) == key || (key != null && key.equals(k))))
            e = p;//将当前节点引用赋值给e
        else if (p instance of TreeNode)
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        else {//不是覆盖操作，则插入一个普通链表节点
            //遍历链表
            for (int binCount = 0; ; ++binCount) {
                if ((e = p.next) == null) {//遍历到尾部，追加新节点到尾部
                    p.next = newNode(hash, key, value, null);
                    //如果追加节点后，链表数量>=8，则转化为红黑树
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                    treeifyBin(tab, hash);
                    break;
                }
                //如果找到了要覆盖的节点
                if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
        //如果e不是null，说明有需要覆盖的节点，
        if (e != null) { // existing mapping for key
            //则覆盖节点值，并返回原oldValue
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            //这是一个空实现的函数，用作LinkedHashMap重写使用。
            afterNodeAccess(e);
            return oldValue;
        }
    }
    //如果执行到了这里，说明插入了一个新的节点，所以会修改modCount，以及返回null。
    ++modCount;
    //更新size，并判断是否需要扩容。
    if (++size > threshold)
    resize();
    //这是一个空实现的函数，用作LinkedHashMap重写使用。
    afterNodeInsertion(evict);
    return null;
}
```

![](https://user-gold-cdn.xitu.io/2019/12/9/16ee99b63f0fc8a6?w=983&h=669&f=png&s=63626)
总结一下put过程
- 第一步当然是先计算key的hash值(有过处理的 (h = key.hashCode()) ^ (h >>> 16))
- 第二步调用putval方法，然后判断是否容器中全部为空，如果是的话，就把容器的容量扩容。
- 第三步，把最大容量和hash值求&值（i = (n - 1) & hash），判断这个数组下标是否有数据，如果没有就把它放进去。还要判断key的equals方法，看是否需要覆盖。
- 第四步，如果有，说明发生了碰撞，那么继续遍历判断链表的长度是否大于8，如果大于8，就继续把当前链表变成红黑树结构。
- 第五步，如果没有到8，那么就直接把数据存在链表的尾部
- 第六步，最后将容器的容量+1。

key.hashCode（）是Key自带的hashCode()方法，返回一个int类型的散列值。我们大家知道，32位带符号的int表值范围从-2147483648到2147483648。这样只要hash函数松散的话，一般是很难发生碰撞的，因为HashMap的初始容量只有16。但是这样的散列值我们是不能直接拿来用的。用之前需要对数组的长度取模运算。得到余数才是索引值。


#### get方法

```
public V get(Object key) {
    Node<K,V> e;
    //传入扰动后的哈希值 和 key 找到目标节点Node
    return (e = getNode(hash(key), key)) == null ? null : e.value; 
}
```

HashMap向用户分开放的get方法是调用的getNode方法来实现的


```
//传入扰动后的哈希值 和 key 找到目标节点Node
final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
    //查找过程，找到返回节点，否则返回null
    if ((tab = table) != null && (n = tab.length) > 0 && (first = tab[(n - 1) & hash]) != null) {
        if (first.hash == hash && ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        if ((e = first.next) != null) {
            if (first instanceof TreeNode)
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            do {
                if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
```

简单讲讲查询过程，还是比较简单的
- 第一步，看下整个容器是否为空。
- 第二步，如果不为空，再比较hash值的同时需要比较key的值是否相同e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k)))
- 然后返回


#### contains
　HashMap没有提供判断元素是否存在的方法，只提供了判断Key是否存在及Value是否存在的方法，分别是containsKey(Object key)、containsValue(Object value)。
containsKey(Object key)方法很简单，只是判断getNode (key)的结果是否为null，是则返回false，否返回true。

```
public boolean containsKey(Object key) {
    return getNode(hash(key), key) != null; 
}
public boolean containsValue(Object value) {
    Node<K,V>[] tab; V v;
    //遍历哈希桶上的每一个链表
    if ((tab = table) != null && size > 0) {
        for (int i = 0; i < tab.length; ++i) {
            for (Node<K,V> e = tab[i]; e != null; e = e.next) {
            //如果找到value一致的返回true
            if ((v = e.value) == value || (value != null && value.equals(v)))
                return true;
            }
        }
    }
    return false; 
}
```
判断一个value是否存在比判断key是否存在还要简单，就是遍历所有元素判断是否有相等的值。这里分为两种情况处理，value为null何不为null的情况，但内容差不多，只是判断相等的方式不同。这个判断是否存在必须遍历所有元素，是一个双重循环的过程，因此是比较耗时的操作。
#### remove方法
HashMap中“删除”相关的操作，有remove(Object key)和clear()两个方法。
其中向用户开放的remove方法调用的是removeNode方法，，removeNode (key)的返回结果应该是被移除的元素，如果不存在这个元素则返回为null。remove方法根据removeEntryKey返回的结果e是否为null返回null或e.value。


```
public V remove(Object key) {
    Node<K,V> e;
    return (e = removeNode(hash(key), key, null, false, true)) == null ? null : e.value; 
}

final Node<K,V> removeNode(int hash, Object key, Object value, boolean matchValue, boolean movable) {
    // p 是待删除节点的前置节点
    Node<K,V>[] tab; Node<K,V> p; int n, index;
    //如果哈希表不为空，则根据hash值算出的index下 有节点的话。
    if ((tab = table) != null && (n = tab.length) > 0&&(p = tab[index = (n - 1) & hash]) != null) {
        //node是待删除节点
        Node<K,V> node = null, e; K k; V v;
        //如果链表头的就是需要删除的节点
        if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))
            node = p;//将待删除节点引用赋给node
        else if ((e = p.next) != null) {//否则循环遍历 找到待删除节点，赋值给node
            if (p instanceof TreeNode)
                node = ((TreeNode<K,V>)p).getTreeNode(hash, key);
            else {
                do {
                    if (e.hash == hash && ((k = e.key) == key ||
                             (key != null && key.equals(k)))) {
                        node = e;
                        break;
                    }
                    p = e;
                } while ((e = e.next) != null);
            }
        }
        //如果有待删除节点node，  且 matchValue为false，或者值也相等
        if (node != null && (!matchValue || (v = node.value) == value ||
                                 (value != null && value.equals(v)))) {
            if (node instanceof TreeNode)
                ((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
            else if (node == p)//如果node == p，说明是链表头是待删除节点
                tab[index] = node.next;
            else//否则待删除节点在表中间
                p.next = node.next;
            ++modCount;//修改modCount
            --size;//修改size
            afterNodeRemoval(node);//LinkedHashMap回调函数
            return node;
        }
    }
    return null;
}
```

　clear()方法删除HashMap中所有的元素，这里就不用一个个删除节点了，而是直接将table数组内容都置空，这样所有的链表都已经无法访问，Java的垃圾回收机制会去处理这些链表。table数组置空后修改size为0。


```
public void clear() {
    Node<K,V>[] tab;
    modCount++;
    if ((tab = table) != null && size > 0) {
        size = 0;
        for (int i = 0; i < tab.length; ++i)
            tab[i] = null;
    }
}
```

## 总结
HashMap是我写的最长的一篇文章，但是还有很多没有写完，比如它的迭代器（Map是否有序，这个下篇得讲），它的红黑树，实在写不动了，我太难了。也是我菜，红黑树，还没好好学一下，什么左旋，右旋头晕。哈哈 以后有机会会好好补这个坑的。

**大家如果能对着源码跟着过一遍也好，至少看过源码不是，我们知道HashMap 是线程不安全的，那线程安全的Map是啥，我们知道HashMap是无序的，有序的Map又是啥。各位一起加油吧，路慢慢慢其修远。**
### 版本说明
- 这里的源码是JDK8版本，不同版本可能会有所差异，但是基本原理都是一样的。

> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
