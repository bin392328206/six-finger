# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
> HashMap讲完了，我们来看一下和他内部结构差不多的ConcurrentHashMap  
[🔥史上最全的Java容器集合之入门](https://juejin.im/post/5de87a92e51d4557ec02f39d)    
[🔥史上最全的Java容器集合之基础数据结构（手撕链表）](https://juejin.im/post/5de8cdb5f265da33c34e2719)   
[🔥史上最全的Java容器集合之ArrayList(源码解读)](https://juejin.im/post/5de9f222f265da33b12e9600)  
[🔥史上最全的Java容器集合之Vector和LinkedList(源码解读)](https://juejin.im/post/5deb0b26e51d4557e87fc398)  
[🔥史上最全的Java容器集合之equals 和 hashCode ](https://juejin.im/post/5decc9fa518825124a05afd8)  
[🔥史上最全的Java容器集合之HashMap（源码解读）](https://juejin.im/post/5dedb448f265da33b071716a)  

本来Map家族还有一个TreeMap,但是自己对于红黑树还不是特别了解，就先放一放吧，因为用的也不多，ConcurrentHashMap可以解决线程安全问题，至于HashTable已经用的非常少了，不能为null,也是线程安全的。


## ConcurrentHashMap
Tips:其实今天讲它肯定是大概的过一下，它既然是一个线程安全的容器，那么线程安全也要涉及到很多的知识点，比如
- 悲观锁与乐观锁
- 原子性，指令有序性和线程可见性
- 无锁算法
- 内存屏障
- Java内存模型  

这些目前等讲JVM的时候我们再一起去探讨吧，我们今天主要是过一下它，等后面把知识点串起来就会明白的。


### 跟HashTable 的区别，1.7和1.8的比较

ConcurrentHashMap是conccurrent家族中的一个类，由于它可以高效地支持并发操作，以及被广泛使用，经典的开源框架Spring的底层数据结构就是使用ConcurrentHashMap实现的。与同是线程安全的老大哥HashTable相比，它已经更胜一筹，因此它的锁更加细化，而不是像HashTable一样为几乎每个方法都添加了synchronized锁，这样的锁无疑会影响到性能。

1.7和1.8实现线程安全的思想也已经完全变了其中抛弃了原有的Segment 分段锁，而采用了 **CAS + synchronized** 来保证并发安全性。它沿用了与它同时期的HashMap版本的思想，底层依然由“数组”+链表+红黑树的方式思想，但是为了做到并发，又增加了很多辅助的类，例如TreeBin，Traverser等对象内部类。

### 继承结构

![](https://user-gold-cdn.xitu.io/2019/12/10/16eee91e5a96bee8?w=1200&h=609&f=png&s=42829)
跟HashMap就是一模一样

其实一个类是用来干嘛的再类的最开始的介绍是很详细的，但是我的渣渣英语水平，太难了，如果有能力的童鞋可以去好好看看。总结一下说了啥：
- JDK1.8底层是散列表+红黑树
- ConCurrentHashMap支持高并发的访问和更新，它是线程安全的
- 检索操作不用加锁，get方法是非阻塞的
- key和value都不允许为null
 
 ### 常量

```

	//表的最大容量
	private static final int MAXIMUM_CAPACITY = 1 << 30;


	//默认表的容量
    private static final int DEFAULT_CAPACITY = 16;

  
	//最大数组长度
    static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;


	//默认并发级别
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

   
	//负载因子
    private static final float LOAD_FACTOR = 0.75f;

	//树化阈值
    static final int TREEIFY_THRESHOLD = 8;


	//去树化阈值
    static final int UNTREEIFY_THRESHOLD = 6;


	//树化的最小容量
    static final int MIN_TREEIFY_CAPACITY = 64;


	//转移的最小值
    private static final int MIN_TRANSFER_STRIDE = 16;


	//生成sizeCtl的最小位数
    private static int RESIZE_STAMP_BITS = 16;


	//进行扩容允许的最大线程数
    private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;

 
	//sizeCtl所需要的偏移位数
    private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;

 
	//标志值
    static final int MOVED     = -1; // hash for forwarding nodes
    static final int TREEBIN   = -2; // hash for roots of trees
    static final int RESERVED  = -3; // hash for transient reservations
    static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash

 
	//cpu数量
    static final int NCPU = Runtime.getRuntime().availableProcessors();


	//序列化属性
    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("segments", Segment[].class),
        new ObjectStreamField("segmentMask", Integer.TYPE),
        new ObjectStreamField("segmentShift", Integer.TYPE)
    };

	
```
大部分的和HashMap差不多，只有少数的不同

### 成员变量

```

     //node数组，第一次插入节点时初始化 都加了内存屏障
    transient volatile Node<K,V>[] table;


     //用于扩容
    private transient volatile Node<K,V>[] nextTable;


     //计算器值，通过CAS修改值，没有竞争时使用，或者出现多线程初始化时回滚
    private transient volatile long baseCount;


     //初始化和扩容的标志，concurrent包中有很多类似用法
     // -1 初始化中； -N （N-1）个线程在扩容；table没有数据 初始化的大小 ； table有数据 下一次扩容的大小
     
     
	// 非常重要的一个属性，源码中的英文翻译，直译过来是下面的四行文字的意思
	//     sizeCtl = -1，表示有线程正在进行真正的初始化操作
	//     sizeCtl = -(1 + nThreads)，表示有nThreads个线程正在进行扩容操作
	//     sizeCtl > 0，表示接下来的真正的初始化操作中使用的容量，或者初始化/扩容完成后的threshold
	//     sizeCtl = 0，默认值，此时在真正的初始化操作中使用默认容量
    //sizeCtl = -(1 + nThreads)这个，网上好多都是用第二句的直接翻译去解释代码，这样理解是错误的
	// 默认构造的16个大小的ConcurrentHashMap，只有一个线程执行扩容时，sizeCtl = -2145714174，
	//     但是照这段英文注释的意思，sizeCtl的值应该是 -(1 + 1) = -2
	// sizeCtl在小于0时的确有记录有多少个线程正在执行扩容任务的功能，但是不是这段英文注释说的那样直接用 -(1 + nThreads)
	// 实际中使用了一种生成戳，根据生成戳算出一个基数，不同轮次的扩容操作的生成戳都是唯一的，来保证多次扩容之间不会交叉重	叠，
	//     当有n个线程正在执行扩容时，sizeCtl在值变为 (基数 + n)
	// 1.8.0_111的源码的383-384行写了个说明：A generation stamp in field sizeCtl ensures that resizings do not overlap.
    private transient volatile int sizeCtl;

    /**
     * The next table index (plus one) to split while resizing.
     */
     //transfer的table索引
    private transient volatile int transferIndex;


     //扩容或创建counterCells的自旋锁
    private transient volatile int cellsBusy;

  
     // CounterCell数组
    private transient volatile CounterCell[] counterCells;

    // views
    private transient KeySetView<K,V> keySet;
    private transient ValuesView<K,V> values;
    private transient EntrySetView<K,V> entrySet;


```
这里重点解释一下sizeCtl这个属性。可以说它是ConcurrentHashMap中出镜率很高的一个属性，因为它是一个控制标识符，在不同的地方有不同用途，而且它的取值不同，也代表不同的含义。

- 负数代表正在进行初始化或扩容操作
- -1代表正在初始化
- -N 表示有N-1个线程正在进行扩容操作
- 正数或0代表hash表还没有被初始化，这个数值表示初始化或下一次进行扩容的大小，这一点类似于扩容阈值的概念。还后面可以看到，它的值始终是当前ConcurrentHashMap容量的0.75倍，这与loadfactor是对应的。

### 构造方法

![](https://user-gold-cdn.xitu.io/2019/12/10/16eeeb2890027a7f?w=509&h=151&f=png&s=18445)
其实和HashMap是大同小异的，此时ConcurrentHashMap的构造方法逻辑和HashMap基本一致，只是多了concurrencyLevel和SizeCtl。
而且此时也没有初始化table,它是要等到第一次put的时候才初始化table,


### 成员方法

#### ConcurrentHashMap#initTable()


![](https://user-gold-cdn.xitu.io/2019/12/10/16eeebedb8a491d9?w=1628&h=701&f=png&s=141958)

再我们put方法中，首先会判断我们存放数据的table是否为null如果为null,这个时候就要初始化我们的方法了

```
private final Node<K,V>[] initTable() {
        Node<K,V>[] tab; int sc;
        while ((tab = table) == null || tab.length == 0) {
            if ((sc = sizeCtl) < 0)// sizeCtl < 0 标示有其他线程正在进行初始化操作，把线程让出cpu，对于table的厨师操作，只能有一个线程在进行
                Thread.yield(); // lost initialization race; just spin
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {    //如果把sizeCtl原子更新为-1成功，则当前线程进入初始化
            // 如果原子更新失败则说明有其它线程先一步进入初始化了，则进入下一次循环
            // 如果下一次循环时还没初始化完毕，则sizeCtl<0进入上面if的逻辑让出CPU
            // 如果下一次循环更新完毕了，则table.length!=0，退出循环
                try {<br>                    // 为什么还要判断，因为：如果走到下面的finally改变了sizeCtl值，有可能其他线程是会进入这个逻辑的
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY; // 默认大小是16
                        @SuppressWarnings("unchecked")
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                        table = tab = nt;
                        sc = n - (n >>> 2); // 0.75*n，下一次扩容阈值
                    }
                } finally {
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }
```
再这个初始化过程中，就已经有乐观锁的实现了。
可以看出table的初始化在一个cas方法中进行，当table为null或者长度为0时进入while方法。

进入之后判断sizeCtl的指，如果小于0则线程让步。由于初始状态sizeCtl是等于0的，说明前面已经有线程进入了elseif这部分，将sc的值置为-1，表示正在初始化。

如果sc大于0，则取sc，否则取默认容量16。然后计算下一次元素数量达到多少时需要resize。总结一下初始化方法
- 如果sizeCtl小于0，说明别的数组正在进行初始化，则让出执行权
- 如果sizeCtl大于0的话，则初始化一个大小为sizeCtl的数组
- 否则的话初始化一个默认大小(16)的数组
-  然后设置sizeCtl的值为数组长度的3/4

#### ConcurrentHashMap#transfer(Node<K, V>[],Node<K, V>)

![](https://user-gold-cdn.xitu.io/2019/12/10/16eeecff0fadf223?w=1383&h=381&f=png&s=113957)

![](https://user-gold-cdn.xitu.io/2019/12/10/16eeed0412503e75?w=992&h=579&f=png&s=104085)

该方法的目的是实现数组的转移，即ConcurrentHashMap的扩容逻辑。就是HashMap的resize方法

在ConcurrentHashMap中，扩容虽然和HashMap一样，将Node数组的长度变为原来的两倍，但是为了保证多线程的同步性，ConcurrentHashMap引入了nextTable属性。在扩容过程中，大致可以分为三步：
- 初始化一个空数组nextTable，长度为node数组的两倍，用作扩容后的数组的临时存储。
- 将原来的node数组复制到nextTable中。
- 将nextTable赋给原来的Node数组，并将nextTable置为null，修改sizeCtl。
ConcurrentHashMap通过遍历实现数组的复制，根据数组中Node节点的类型不同做不同处理。
    - （1）普通Node类型，表示链表中的节点，根据其下标i放入对应的nextTable中i和n+i的位置，采用头插法，链表顺序与原来相反
    - （2）ForwardingNode类型，表示已经处理过
    - （3）TreeBin类型，表示红黑树的节点，进行红黑树的复制，并考虑是否需要去树化。
    - （4）null，表示此处没有节点，加入ForwardingNode节点。根据上面的ConcurrentHashMap#helpTransfer(Node<K,V>[]， Node<K,V>)可以知道，ForwardingNode类型的节点会触发其它线程加入数组的复制过程，即并发扩容。
另外，ConcurrentHashMap复制数组时的遍历是从n到0进行遍历的，并且不会完全遍历，而是按照线程数量分成若干个小人物，每个线程每次负责复制stride(步进)个桶（[transfer-stride, transfer-1]）。任务完成后可以再次申请。stride根据cpu数量而定，最小为16。




```
private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
    int n = tab.length, stride;
    //确定stride
    if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
        stride = MIN_TRANSFER_STRIDE; // subdivide range
    if (nextTab == null) {            // initiating 
    //初始化，即使多个线程同时进入，也只不过是创建了多个Node<K,V>[]数组nt，在赋值给nextTab时后者覆盖前者，线程必然安全
        try {
            @SuppressWarnings("unchecked")
            Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
            nextTab = nt;
        } catch (Throwable ex) {      // try to cope with OOME
            sizeCtl = Integer.MAX_VALUE;
            return;
        }
        nextTable = nextTab;
        transferIndex = n;
    }
    int nextn = nextTab.length;
    //数组元素复制结束的标志位
    ForwardingNode<K,V> fwd = new ForwardingNode<K,V>(nextTab);
    //advance表示该节点是否处理成功，处理成功后继续遍历，否则该节点再次处理（CAS）
    boolean advance = true;
    //循环是否接受的标志
    boolean finishing = false; // to ensure sweep before committing nextTab
    for (int i = 0, bound = 0;;) {
        Node<K,V> f; int fh;
        while (advance) {
            int nextIndex, nextBound;
            if (--i >= bound || finishing)
                advance = false;
            else if ((nextIndex = transferIndex) <= 0) {
                i = -1;
                advance = false;
            }
            else if (U.compareAndSwapInt
                     (this, TRANSFERINDEX, nextIndex,
                      nextBound = (nextIndex > stride ?
                                   nextIndex - stride : 0))) {
                bound = nextBound;
                i = nextIndex - 1;
                advance = false;
            }
        }
        if (i < 0 || i >= n || i + n >= nextn) {
            int sc;
            if (finishing) {
            	//数组复制结束后的操作
                nextTable = null;
                table = nextTab;
                sizeCtl = (n << 1) - (n >>> 1); 原数组长度的1.75倍，即扩容后的0.75倍
                return;
            }
             //利用CAS方法更新sizeCtl，在这里面sizectl值减一，说明新加入一个线程参与到扩容操作  
            if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                    return;
                finishing = advance = true;
                i = n; // recheck before commit
            }
        }
         //如果遍历到的节点为空 则放入ForwardingNode指针  
        else if ((f = tabAt(tab, i)) == null)
            advance = casTabAt(tab, i, null, fwd);
        //如果遍历到ForwardingNode节点  说明这个点已经被处理过了 直接跳过
        else if ((fh = f.hash) == MOVED)
            advance = true; // already processed
        else {
        	//synchronized锁保证节点复制的线程安全
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                    Node<K,V> ln, hn;
                    //链表节点，头插法得到ln和hn两条链表，分别对应nextTable中下标i和n+i的元素
                    if (fh >= 0) {
                        int runBit = fh & n;
                        Node<K,V> lastRun = f;
                        for (Node<K,V> p = f.next; p != null; p = p.next) {
                            int b = p.hash & n;
                            if (b != runBit) {
                                runBit = b;
                                lastRun = p;
                            }
                        }
                        if (runBit == 0) {
                            ln = lastRun;
                            hn = null;
                        }
                        else {
                            hn = lastRun;
                            ln = null;
                        }
                        for (Node<K,V> p = f; p != lastRun; p = p.next) {
                            int ph = p.hash; K pk = p.key; V pv = p.val;
                            if ((ph & n) == 0)
                                ln = new Node<K,V>(ph, pk, pv, ln);
                            else
                                hn = new Node<K,V>(ph, pk, pv, hn);
                        }
                        setTabAt(nextTab, i, ln);
                        setTabAt(nextTab, i + n, hn);
                        setTabAt(tab, i, fwd);
                        advance = true;
                    }
                    //红黑树节点，先尾插法得到由TreeNode组成的ln和hn两条链表，分别对应nextTable中下标i和n+i的元素，然后作为参数传入TreeBin的构造方法
                    else if (f instanceof TreeBin) {
                        TreeBin<K,V> t = (TreeBin<K,V>)f;
                        TreeNode<K,V> lo = null, loTail = null;
                        TreeNode<K,V> hi = null, hiTail = null;
                        int lc = 0, hc = 0;
                        for (Node<K,V> e = t.first; e != null; e = e.next) {
                            int h = e.hash;
                            TreeNode<K,V> p = new TreeNode<K,V>
                                (h, e.key, e.val, null, null);
                            if ((h & n) == 0) {
                                if ((p.prev = loTail) == null)
                                    lo = p;
                                else
                                    loTail.next = p;
                                loTail = p;
                                ++lc;
                            }
                            else {
                                if ((p.prev = hiTail) == null)
                                    hi = p;
                                else
                                    hiTail.next = p;
                                hiTail = p;
                                ++hc;
                            }
                        }
                        ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                            (hc != 0) ? new TreeBin<K,V>(lo) : t;
                        hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                            (lc != 0) ? new TreeBin<K,V>(hi) : t;
                        setTabAt(nextTab, i, ln);
                        setTabAt(nextTab, i + n, hn);
                        setTabAt(tab, i, fwd);
                        advance = true;
                    }
                }
            }
        }
    }
}

```

![](https://user-gold-cdn.xitu.io/2019/12/10/16eeef0342a3cc11?w=1266&h=939&f=png&s=98610)

我只能说复杂的一批。扩容是最复杂的，多线程的数据复制，还有红黑树的转换。脑子不够用。大神们去探究吧，我就记记结论吧

#### put方法

单纯的put方法
```
/*
     *    单纯的额调用putVal方法，并且putVal的第三个参数设置为false
     *  当设置为false的时候表示这个value一定会设置
     *  true的时候，只有当这个key的value为空的时候才会设置
     */
    public V put(K key, V value) {
        return putVal(key, value, false);
    }
```
　再来看putVal
　
　
```
   final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) throw new NullPointerException();//K,V都不能为空，否则的话跑出异常
        int hash = spread(key.hashCode());    //取得key的hash值
        int binCount = 0;    //用来计算在这个节点总共有多少个元素，用来控制扩容或者转移为树
        for (Node<K,V>[] tab = table;;) {    //
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)    
                tab = initTable();    //第一次put的时候table没有初始化，则初始化table
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {    //通过哈希计算出一个表中的位置因为n是数组的长度，所以(n-1)&hash肯定不会出现数组越界
                if (casTabAt(tab, i, null,        //如果这个位置没有元素的话，则通过cas的方式尝试添加，注意这个时候是没有加锁的
                             new Node<K,V>(hash, key, value, null)))        //创建一个Node添加到数组中区，null表示的是下一个节点为空
                    break;                   // no lock when adding to empty bin
            }
            /*
             * 如果检测到某个节点的hash值是MOVED，则表示正在进行数组扩张的数据复制阶段，
             * 则当前线程也会参与去复制，通过允许多线程复制的功能，一次来减少数组的复制所带来的性能损失
             */
            else if ((fh = f.hash) == MOVED)    
                tab = helpTransfer(tab, f);
            else {
                /*
                 * 如果在这个位置有元素的话，就采用synchronized的方式加锁，
                 *     如果是链表的话(hash大于0)，就对这个链表的所有元素进行遍历，
                 *         如果找到了key和key的hash值都一样的节点，则把它的值替换到
                 *         如果没找到的话，则添加在链表的最后面
                 *  否则，是树的话，则调用putTreeVal方法添加到树中去
                 *  
                 *  在添加完之后，会对该节点上关联的的数目进行判断，
                 *  如果在8个以上的话，则会调用treeifyBin方法，来尝试转化为树，或者是扩容
                 */
                V oldVal = null;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {        //再次取出要存储的位置的元素，跟前面取出来的比较
                        if (fh >= 0) {                //取出来的元素的hash值大于0，当转换为树之后，hash值为-2
                            binCount = 1;            
                            for (Node<K,V> e = f;; ++binCount) {    //遍历这个链表
                                K ek;
                                if (e.hash == hash &&        //要存的元素的hash，key跟要存储的位置的节点的相同的时候，替换掉该节点的value即可
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)        //当使用putIfAbsent的时候，只有在这个key没有设置值得时候才设置
                                        e.val = value;
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {    //如果不是同样的hash，同样的key的时候，则判断该节点的下一个节点是否为空，
                                    pred.next = new Node<K,V>(hash, key,        //为空的话把这个要加入的节点设置为当前节点的下一个节点
                                                              value, null);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) {    //表示已经转化成红黑树类型了
                            Node<K,V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,    //调用putTreeVal方法，将该元素添加到树中去
                                                           value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)    //当在同一个节点的数目达到8个的时候，则扩张数组或将给节点的数据转为tree
                        treeifyBin(tab, i);    
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        addCount(1L, binCount);    //计数
        return null;
    }
```
我们来总结一下put方法
- **第一步，一进去肯定判断key value是否为null 如果为null 抛出异常**
- **第二步，当添加一对键值对的时候，首先会去判断保存这些键值对的数组是不是初始化了，如果没有就初始化数组。**
- **第三步， 通过计算hash值来确定放在数组的哪个位置如果这个位置为空则直接添加（CAS的加锁方式），如果不为空的话，则取出这个节点来**
- **第四步，如果取出来的节点的hash值是MOVED(-1)的话，则表示当前正在对这个数组进行扩容，复制到新的数组，则当前线程也去帮助复制**
- **第五步，如果这个节点，不为空，也不在扩容，则通过synchronized来加锁，进行添加操作**
- **第六步，如果是链表的话，则遍历整个链表，直到取出来的节点的key来个要放的key进行比较，如果key相等，并且key的hash值也相等的话，则说明是同一个key，则覆盖掉value，否则的话则添加到链表的末尾**
- **第七步，如果是树的话，则调用putTreeVal方法把这个元素添加到树中去**
- **第八步，最后在添加完成之后，会判断在该节点处共有多少个节点（注意是添加前的个数），如果达到8个以上了的话，则调用treeifyBin方法来尝试将处的链表转为树，或者扩容数**


#### get方法
concurrentHashMap的get操作的流程很简单，可以分为三个步骤来描述:
- 计算hash值，定位到该table索引位置，如果是首节点符合就返回。
- 如果遇到扩容的时候，会调用标志正在扩容节点ForwardingNode的find方法，查找该节点，匹配就返回。
- 以上都不符合的话，就往下遍历节点，匹配就返回，否则最后就返回null


```
public V get(Object key) {
    Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
    int h = spread(key.hashCode()); //计算两次hash
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (e = tabAt(tab, (n - 1) & h)) != null) {//读取首节点的Node元素
        if ((eh = e.hash) == h) { //如果该节点就是首节点就返回
            if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                return e.val;
        }
        //hash值为负值表示正在扩容，这个时候查的是ForwardingNode的find方法来定位到nextTable来
        //查找，查找到就返回
        else if (eh < 0)
            return (p = e.find(h, key)) != null ? p.val : null;
        while ((e = e.next) != null) {//既不是首节点也不是ForwardingNode，那就往下遍历
            if (e.hash == h &&
                ((ek = e.key) == key || (ek != null && key.equals(ek))))
                return e.val;
        }
    }
    return null;
}
```

#### size方法
对于size的计算，在扩容和addCount()方法就已经有处理了，可以注意一下Put函数，里面就有addCount()函数，早就计算好的，然后你size的时候直接给你.


```
public int size() {
    long n = sumCount();
    return ((n < 0L) ? 0 :
            (n > (long)Integer.MAX_VALUE) ? Integer.MAX_VALUE :
            (int)n);
}
final long sumCount() {
    CounterCell[] as = counterCells; CounterCell a; //变化的数量
    long sum = baseCount;
    if (as != null) {
        for (int i = 0; i < as.length; ++i) {
            if ((a = as[i]) != null)
                sum += a.value;
        }
    }
    return sum;
}
```

#### Unsafe与CAS
在ConcurrentHashMap中，随处可以看到U, 大量使用了U.compareAndSwapXXX的方法，这个方法是利用一个CAS算法实现无锁化的修改值的操作，他可以大大降低锁代理的性能消耗。这个算法的基本思想就是不断地去比较当前内存中的变量值与你指定的一个变量值是否相等，如果相等，则接受你指定的修改的值，否则拒绝你的操作。因为当前线程中的值已经不是最新的值，你的修改很可能会覆盖掉其他线程修改的结果。这一点与乐观锁，SVN的思想是比较类似的。

unsafe代码块控制了一些属性的修改工作，比如最常用的SIZECTL 。  在这一版本的concurrentHashMap中，大量应用来的CAS方法进行变量、属性的修改工作。  利用CAS进行无锁操作，可以大大提高性能。
```
private static final sun.misc.Unsafe U;
    private static final long SIZECTL;
    private static final long TRANSFERINDEX;
    private static final long BASECOUNT;
    private static final long CELLSBUSY;
    private static final long CELLVALUE;
    private static final long ABASE;
    private static final int ASHIFT;
 
    static {
        try {
            U = sun.misc.Unsafe.getUnsafe();
            Class<?> k = ConcurrentHashMap.class;
            SIZECTL = U.objectFieldOffset
                (k.getDeclaredField("sizeCtl"));
            TRANSFERINDEX = U.objectFieldOffset
                (k.getDeclaredField("transferIndex"));
            BASECOUNT = U.objectFieldOffset
                (k.getDeclaredField("baseCount"));
            CELLSBUSY = U.objectFieldOffset
                (k.getDeclaredField("cellsBusy"));
            Class<?> ck = CounterCell.class;
            CELLVALUE = U.objectFieldOffset
                (ck.getDeclaredField("value"));
            Class<?> ak = Node[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & (scale - 1)) != 0)
                throw new Error("data type scale not a power of two");
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

```

ConcurrentHashMap定义了三个原子操作，用于对指定位置的节点进行操作。正是这些原子操作保证了ConcurrentHashMap的线程安全。

```
@SuppressWarnings("unchecked")
    //获得在i位置上的Node节点
    static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int i) {
        return (Node<K,V>)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
    }
		//利用CAS算法设置i位置上的Node节点。之所以能实现并发是因为他指定了原来这个节点的值是多少
		//在CAS算法中，会比较内存中的值与你指定的这个值是否相等，如果相等才接受你的修改，否则拒绝你的修改
		//因此当前线程中的值并不是最新的值，这种修改可能会覆盖掉其他线程的修改结果  有点类似于SVN
    static final <K,V> boolean casTabAt(Node<K,V>[] tab, int i,
                                        Node<K,V> c, Node<K,V> v) {
        return U.compareAndSwapObject(tab, ((long)i << ASHIFT) + ABASE, c, v);
    }
		//利用volatile方法设置节点位置的值
    static final <K,V> void setTabAt(Node<K,V>[] tab, int i, Node<K,V> v) {
        U.putObjectVolatile(tab, ((long)i << ASHIFT) + ABASE, v);
    }

```

## 总结

### HashMap、Hashtable、ConccurentHashMap三者的区别
- HashMap线程不安全，数组+链表+红黑树
- Hashtable线程安全，锁住整个对象，数组+链表
- ConccurentHashMap线程安全，CAS+同步锁，数组+链表+红黑树
- HashMap的key，value均可为null，其他两个不行。

### 在JDK1.7和JDK1.8中的区别
在JDK1.8主要设计上的改进有以下几点:

1. 不采用segment而采用node，锁住node来实现减小锁粒度。
2. 设计了MOVED状态 当resize的中过程中 线程2还在put数据，线程2会帮助resize。
3. 使用3个CAS操作来确保node的一些操作的原子性，这种方式代替了锁。
4. sizeCtl的不同值来代表不同含义，起到了控制的作用。
采用synchronized而不是ReentrantLock  

### 版本说明
- 这里的源码是JDK8版本，不同版本可能会有所差异，但是基本原理都是一样的。
> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
