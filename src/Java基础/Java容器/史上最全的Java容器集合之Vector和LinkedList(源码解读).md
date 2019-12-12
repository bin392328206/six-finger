# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
> 前面2篇的基础，大家还是好好学习一下，下面是链接  
[🔥史上最全的Java容器集合之入门](https://juejin.im/post/5de87a92e51d4557ec02f39d)    
[🔥史上最全的Java容器集合之基础数据结构（手撕链表）](https://juejin.im/post/5de8cdb5f265da33c34e2719)   
[🔥史上最全的Java容器集合之ArrayList(源码解读)](https://juejin.im/post/5de9f222f265da33b12e9600)  
>今天讲Vector和LinkedList(顺便提一下，如果是零基础的不建议来，有过半年工作经验的跟着我一起把这些过一遍的话，对你的帮助是非常大的)

## Vector 源码分析
其实Vector要讲的东西不多了，因为它和ArrayList的代码很像，就是再每个方法上加了锁，如下图

![](https://user-gold-cdn.xitu.io/2019/12/7/16ede32ea3f6dab9?w=990&h=629&f=png&s=35522)

![](https://user-gold-cdn.xitu.io/2019/12/7/16ede33cef3a8819?w=1664&h=748&f=png&s=245828)

因为大部分和前面差不多，我来说说不同的点吧

![](https://user-gold-cdn.xitu.io/2019/12/7/16ede398c2ae3caf?w=1521&h=482&f=png&s=138198)
看图上面的 这个是Vetor和ArrayList不同的另一个点 它的增长因子是可以自己定义的。我们来看grow方法

![](https://user-gold-cdn.xitu.io/2019/12/7/16ede3aee12025fa?w=1545&h=468&f=png&s=144921)

这段代码是扩容代码，可以看如果定义了曾长因子就每次扩容增长因子，不然就是扩容2倍

其他的增删改查，我就不说了，自己也没细看，但是底层是数组，所以大多数是相同了 只是加了一个synchronized锁


这边提一下其实ArrayList也可以变成线程安全
> 如果想要ArrayList实现同步，可以使用Collections的方法：List list=Collections.synchronizedList(new ArrayList(...));，就可以实现同步了

## LinkedList源码分析

### 概念
- LinkedList是基于链表实现的，所以先讲解一下什么是链表。链表原先是C/C++的概念，是一种线性的存储结构，意思是将要存储的数据存在一个存储单元里面，这个存储单元里面除了存放有待存储的数据以外，还存储有其下一个存储单元的地址（下一个存储单元的地址是必要的，有些存储结构还存放有其前一个存储单元的地址），每次查找数据的时候，通过某个存储单元中的下一个存储单元的地址寻找其后面的那个存储单元。
- 理解：
    - LinkedList是一个双向链表
    - 也就是说list中的每个元素，在存储自身值之外，还额外存储了其前一个和后一个元素的地址，所以 也就可以很方便地根据当前元素获取到其前后的元素
    - 链表的尾部元素的后一个节点是链表的头节点；而链表的头结点前一个节点则是则是链表的尾节点（是不是有点像贪吃蛇最后 头吃到自己尾巴的样子，脑补下）
    - 既然是一个双向链表，那么必然有一个基本的存储单元，让我们来看LinkedList的最基础的存储单元。
>  对于单项链表 自己手撕了一个，有兴趣的可以去看看   
[🔥史上最全的Java容器集合之基础数据结构（手撕链表）](https://juejin.im/post/5de8cdb5f265da33c34e2719)

### 继承结构和层次关系

![](https://user-gold-cdn.xitu.io/2019/12/7/16ede844efe9e647?w=1275&h=776&f=png&s=80393)

从这个图来对 和底层是数组结构的2个集合的区别 少实现了一个随机访问的RandomAccess接口 多实现了一个Deque接口
所以linkedList并不具备随机访问的功能，它也必须一个个去遍历，结论是： **ArrayList用for循环遍历比iterator迭代器遍历快，LinkedList用iterator迭代器遍历比for循环遍历**

还有可以看出LinkedList与ArrayList的另外不同之处，ArrayList直接继承自AbstractList，而LinkedList继承自AbstractSequentialList，然后再继承自AbstractList。另外，LinkedList还实现了Dequeu接口，双端队列。

![](https://user-gold-cdn.xitu.io/2019/12/7/16ede8a0f98158e5?w=1200&h=311&f=png&s=16450)

### Deque双端队列
> Deque 双端队列，这个之前没讲过，唉 一个个坑，自己填吧

双端队列（deque，全名double-ended queue），是一种具有队列和栈的性质的数据结构。队列是一个有序列表，可以用数组或是链表来实现。

双端队列中的元素可以从两端弹出，其限定插入和删除操作在表的两端进行。双端队列可以在队列任意一端入队和出队，

![](https://user-gold-cdn.xitu.io/2019/12/7/16edeb068f1039b1?w=973&h=146&f=png&s=29758)
遵循先入先出的原则：

- 先存入队列的数据，要先取出。
- 后存入的要后取出

![](https://user-gold-cdn.xitu.io/2019/12/7/16edeb14b34c6c0a?w=1406&h=471&f=png&s=113755)

来看看队列的方法吧
- add和offer 都表示插入 区别是一个失败会报错，一个失败返回false
- remove 和 poll 都表示删除 都是删除成功返回队首元素     当队列为空时它会抛出异常。remove失败的话会报错 poll返回null
- element peek 返回队首元素，但是不删除队首元素，element 当队列为空时它会抛出异常。peek返回null

总结一下 其实队列很简单，特别像这种简单 只有三个操作 加入 删除 查看 且全部是针对队首的操作。

接下来我们看看deque吧

![](https://user-gold-cdn.xitu.io/2019/12/7/16edebda13d41de2?w=1534&h=679&f=png&s=191851)
有点多 哈哈 其实操作也差不多，队列就是只能操作队首，双向队列就是可以操作队首和队尾

### 手撕一个简单的队列
我们知道队列它的底层可以是数组或者是链表，
我们今天就用数组来实现一个简单的队列

```
package com.atguigu.ct.producer.controller;

/**
 * 六脉神剑
 * 1.使用数组实现队列功能，使用int数组保存数据特点：先进先出，后进后出
 */

public class QueueTest1 {
    public static void main(String[] args){

        //测试队列
        System.out.println("测试队列");
        Queue queue = new Queue();
        queue.in("六脉神剑");
        queue.in("七卖神剑");
        queue.in("八面玲珑");
        System.out.println(queue.out());
        System.out.println(queue.out());


    }
}

//使用数组定义一个队列
class Queue {

    String[] a = new String[5];
    int i = 1; //数组下标

    //入队
    public void in(String m){
        a[i++] = m;
    }

    //出队
    public String out(){
        int index = 0;
        String temp = a[1];
        for(int j=1;j<i;j++){
            a[j-1] = a[j];
            index++;
        }
        i = index;
        return temp;
    }
}

```
结果

```
测试队列
六脉神剑
七卖神剑

Process finished with exit code 0
```
其实很简单 就是每次出来的时候 把所有的元素的下标往前移一位。队列深入，这边就不深入了，靠各位大佬自己了，我也是黄婆卖瓜。接下来我们讲LinkedList

### LinkedList 常量

![](https://user-gold-cdn.xitu.io/2019/12/7/16edecf88f2910ca?w=1217&h=673&f=png&s=110384)

三个常量 一个表示这个集合的大小，一个表示队列的元素的前一个元素，一个表示队列元素的后一个元素 

![](https://user-gold-cdn.xitu.io/2019/12/7/16eded10810f759a?w=730&h=366&f=png&s=34759)
来看一个Node 元素 它要存三个数据，一个是自己本身，一个是它的前驱，一个是它的后继

### 构造方法

![](https://user-gold-cdn.xitu.io/2019/12/7/16edee56cb7f2077?w=1589&h=726&f=png&s=173796)
　LinkedList 有两个构造函数，第一个是默认的空的构造函数，第二个是将已有元素的集合Collection 的实例添加到 LinkedList 中，调用的是 addAll() 方法，这个方法下面我们会介绍。

　　注意：LinkedList 是没有初始化链表大小的构造函数，因为链表不像数组，一个定义好的数组是必须要有确定的大小，然后去分配内存空间，而链表不一样，它没有确定的大小，通过指针的移动来指向下一个内存地址的分配。
### 添加元素

![](https://user-gold-cdn.xitu.io/2019/12/7/16edf094eb23117b?w=1411&h=656&f=png&s=170314)
-  addFirst(E e)
    - 添加元素到链表的头部 只需要替换链表头部的后继指针就好了
- addLast(E e)和add(E e)
    - 　将指定元素添加到链表尾
-  add(int index, E element)
    - 讲元素拆入到指定的位置，这个要先找到这个元素的前后的元素，然后再修改。
- addAll(Collection<? extends E> c)
    - 按照指定集合的​​迭代器返回的顺序，将指定集合中的所有元素追加到此列表的末尾
### 删除元素


![](https://user-gold-cdn.xitu.io/2019/12/7/16edf0e2a31e7571?w=1055&h=692&f=png&s=169451)
删除元素和添加元素一样，也是通过更改指向上一个节点和指向下一个节点的引用即可。
- remove()和removeFirst()
    - 从此列表中移除并返回第一个元素
- removeLast()
    - 从该列表中删除并返回最后一个元素
- remove(int index)
    - 删除此列表中指定位置的元素
- remove(Object o)
    - 如果存在，则从该列表中删除指定元素的第一次出现，需要注意的是，这个是删除第一个出现的，并不是删除所有的这个元素
### 修改元素
通过调用 set(int index, E element) 方法，用指定的元素替换此列表中指定位置的元素。

![](https://user-gold-cdn.xitu.io/2019/12/7/16edf11a3a6ef93b?w=1147&h=514&f=png&s=141241)
　　这里主要是通过 node(index) 方法获取指定索引位置的节点，然后修改此节点位置的元素即可。

### 查找元素

因为截图截不全我就没截图了，大家可以对着源码看，具体的实现确实也不难，前面我们手撕链表大部分也实现了
- getFirst()
    - 返回此列表中的最后一个元素
-  getLast()
    - 返回此列表中的最后一个元素
- get(int index)
    - 　返回指定索引处的元素
- indexOf(Object o)
    - 　返回此列表中指定元素第一次出现的索引，如果此列表不包含元素，则返回-1。
### 遍历集合
普通 for 循环
> 代码很简单，我们就利用 LinkedList 的 get(int index) 方法，遍历出所有的元素。
　但是需要注意的是， get(int index) 方法每次都要遍历该索引之前的所有元素，这句话这么理解：比如上面的一个 LinkedList 集合，我放入了 A,B,C,D是个元素。
 
- 总共需要四次遍历：
- 第一次遍历打印 A：只需遍历一次。
- 第二次遍历打印 B：需要先找到 A，然后再找到 B 打印。
- 第三次遍历打印 C：需要先找到 A，然后找到 B，最后找到 C 打印。
- 第四次遍历打印 D：需要先找到 A，然后找到 B，然后找到 C，最后找到 D。　　
- 这样如果集合元素很多，越查找到后面（当然此处的get方法进行了优化，查找前半部分从前面开始遍历，查找后半部分从后面开始遍历，但是需要的时间还是很多）花费的时间越多。那么如何改进呢？

迭代器
这个比较适合
迭代器的另一种形式就是使用 foreach 循环，底层实现也是使用的迭代器，这里我们就不做介绍了

## 总结
- List继承了Collection，是有序的列表，可重复。
- 实现类有ArrayList、LinkedList、Vector、Stack等
    - ArrayList是基于数组实现的，是一个数组队列。可以动态的增加容量！,线程不安全。基于数组所以查快，增删慢，因为如果要删除的话，它后面的元素就要重新改版索引。
    - LinkedList是基于链表实现的，是一个双向循环列表。可以被当做堆栈使用！，它的查慢，每次查都要遍历整个集合，但是它的增删快，特别是再头尾添加，特别的快。
    - Vector是基于数组实现的，是一个矢量队列，是线程安全的！基本差不多和ArrayList差不多，但是它是线程安全的，意味着性能没有那么好。
### 版本说明
- 这里的源码是JDK8版本，不同版本可能会有所差异，但是基本原理都是一样的。

## 结尾
>  List的三个实现，讲完了，是不是感觉也不是很难呢？博主跟着学，发现以前只是用，但是现在确实熟悉很多了。下节开始讲Map,因为Set的底层是基于Map，它放最后

> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
