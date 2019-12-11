# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
> 前面2篇的基础，大家还是好好学习一下，下面是链接  
[🔥史上最全的Java容器集合之入门](https://juejin.im/post/5de87a92e51d4557ec02f39d)    
[🔥史上最全的Java容器集合之基础数据结构（手撕链表）](https://juejin.im/post/5de8cdb5f265da33c34e2719)    
> 本来想直接将List这个父类下的所有之类，但是怕太长，我把我们真实开发中最最常用的ArrayList单独拿出来讲了，后面2个做一篇(顺便提一下，如果是零基础的不建议来，有过半年工作经验的跟着我一起把这些过一遍的话，对你的帮助是非常大的)

## 一、ArrayList认识

### 概念

概念：ArrayList是一个其容量能够动态增长的动态数组。但是他又和数组不一样，下面会分析对比。它继承了AbstractList，实现了List、RandomAccess, Cloneable, java.io.Serializable。

RandomAccess接口，被List实现之后，为List提供了随机访问功能，也就是通过下标获取元素对象的功能。

实现了Cloneable, java.io.Serializable意味着可以被克隆和序列化。



最主要的还是看我化圈的部分

### ArrayList的数据结构
分析一个类的时候，数据结构往往是它的灵魂所在，理解底层的数据结构其实就理解了该类的实现思路，具体的实现细节再具体分析。

　　ArrayList的数据结构是：
　　
![](https://user-gold-cdn.xitu.io/2019/12/6/16eda4c43c95c152?w=472&h=102&f=png&s=17425)
　说明：底层的数据结构就是数组，数组元素类型为Object类型，即可以存放所有类型数据。我们对ArrayList类的实例的所有的操作底层都是基于数组的。


## ArrayList源码分析


### 继承结构和层次关系
![](https://user-gold-cdn.xitu.io/2019/12/6/16ed9f672c374c32?w=1326&h=753&f=png&s=106243)

>我们看一下ArrayList的继承结构：

    - ArrayList extends AbstractList
    - AbstractList extends AbstractCollection 

> 所有类都继承Object  所以ArrayList的继承结构就是上图这样   

> 思考 为什么要先继承AbstractList，而让AbstractList先实现List<E>？而不是让ArrayList直接实现List<E>？

> 这里是有一个思想，接口中全都是抽象的方法，而抽象类中可以有抽象方法，还可以有具体的实现方法，正是利用了这一点，让AbstractList是实现接口中一些通用的方法，而具体的类，　如ArrayList就继承这个AbstractList类，拿到一些通用的方法，然后自己在实现一些自己特有的方法，这样一来，让代码更简洁，就继承结构最底层的类中通用的方法都抽取出来，先一起实现了，减少重复代码。所以一般看到一个类上面还有一个抽象类，应该就是这个作用


### RandomAccess这个接口
我点进去源码发现他是一个空的接口，为啥是个空接口呢

RandomAccess接口是一个标志接口（Marker）

ArrayList集合实现这个接口，就能支持快速随机访问


```
public static <T>
    int binarySearch(List<? extends Comparable<? super T>> list, T key) {
        if (list instanceof RandomAccess || list.size()<BINARYSEARCH_THRESHOLD)
            return Collections.indexedBinarySearch(list, key);
        else
            return Collections.iteratorBinarySearch(list, key);
    } 

```

> 通过查看源代码，发现实现RandomAccess接口的List集合采用一般的for循环遍历，而未实现这接口则采用迭代器。
ArrayList用for循环遍历比iterator迭代器遍历快，LinkedList用iterator迭代器遍历比for循环遍历快，
所以说，当我们在做项目时，应该考虑到List集合的不同子类采用不同的遍历方式，能够提高性能！
然而有人发出疑问了，那怎么判断出接收的List子类是ArrayList还是LinkedList呢？
这时就需要用instanceof来判断List集合子类是否实现RandomAccess接口！

**总结：RandomAccess接口这个空架子的存在，是为了能够更好地判断集合是否ArrayList或者LinkedList，从而能够更好选择更优的遍历方式，提高性能**

## ArrayList 类中的属性

```
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    // 版本号
    private static final long serialVersionUID = 8683452581122892189L;
    // 缺省容量
    private static final int DEFAULT_CAPACITY = 10;
    // 空对象数组
    private static final Object[] EMPTY_ELEMENTDATA = {};
    // 缺省空对象数组
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    // 元素数组  ArrayList中有扩容这么一个概念，正因为它扩容，所以它能够实现“动态”增长
    transient Object[] elementData;
    // 实际元素大小，默认为0
    private int size;
    // 最大数组容量
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
}

```

## 构造方法

![](https://user-gold-cdn.xitu.io/2019/12/6/16eda5a58ae877b6?w=1297&h=348&f=png&s=73811)

### 无参构造
```
public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }
```
此时ArrayList的size为空，但是elementData的length为1。第一次添加时，容量变成初始容量大小10（默认无参构造的容量就是10）

### int参数构造


```
   public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }
```
这个比较简单 进来判断是否大于0，如果大于0 就创建一个传进来大小的一个数组， 如果为0就是空数组

### collection参数构造函数

```
 public ArrayList(Collection<? extends E> c) {
        // 指定 collection 的元素的列表，这些元素是按照该 collection 的迭代器返回它们的顺序排
        // 这里主要做了两步：1.把集合的元素copy到elementData中。2.更新size值。
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
            // c.toArray might (incorrectly) not return Object[] (see 6260652)
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            // replace with empty array.
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }

```

把集合的元素重新放到新的集合里面，然后更新实际容量的大小


## 具体方法

### add方法

![](https://user-gold-cdn.xitu.io/2019/12/6/16eda6c737b313a6?w=1217&h=421&f=png&s=109557)


```
  /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        // 扩容操作，稍后讲解
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        // 将元素添加到数组尾部
        elementData[size++] = e;
        return true;
    }

```
第一步 扩容操作 第二步 往尾部添加一个元素 
跟着往下看

　ensureCapacityInternal(xxx);　确定内部容量的方法　　　
```
private void ensureCapacityInternal(int minCapacity) {
        if (elementData == EMPTY_ELEMENTDATA) { //看，判断初始化的elementData是不是空的数组，也就是没有长度
    //因为如果是空的话，minCapacity=size+1；其实就是等于1，空的数组没有长度就存放不了，所以就将minCapacity变成10，也就是默认大小，但是带这里，还没有真正的初始化这个elementData的大小。
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
    //确认实际的容量，上面只是将minCapacity=10，这个方法就是真正的判断elementData是否够用
        ensureExplicitCapacity(minCapacity);
    }
```

ensureExplicitCapacity(xxx)；


```
private void ensureExplicitCapacity(int minCapacity) {
        modCount++;

        // overflow-conscious code
//minCapacity如果大于了实际elementData的长度，那么就说明elementData数组的长度不够用，不够用那么就要增加elementData的length。这里有的读者就会模糊minCapacity到底是什么呢，这里给你们分析一下

/*第一种情况：由于elementData初始化时是空的数组，那么第一次add的时候，minCapacity=size+1；也就minCapacity=1，在上一个方法(确定内部容量ensureCapacityInternal)就会判断出是空的数组，就会给
　　将minCapacity=10，到这一步为止，还没有改变elementData的大小。
　第二种情况：elementData不是空的数组了，那么在add的时候，minCapacity=size+1；也就是minCapacity代表着elementData中增加之后的实际数据个数，拿着它判断elementData的length是否够用，如果length
不够用，那么肯定要扩大容量，不然增加的这个元素就会溢出。
*/


        if (minCapacity - elementData.length > 0)
    //arrayList能自动扩展大小的关键方法就在这里了
            grow(minCapacity);
    }
```

grow(xxx); arrayList核心的方法，能扩展数组大小的真正秘密。


```
private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;  //将扩充前的elementData大小给oldCapacity
        int newCapacity = oldCapacity + (oldCapacity >> 1);//newCapacity就是1.5倍的oldCapacity
        if (newCapacity - minCapacity < 0)//这句话就是适应于elementData就空数组的时候，length=0，那么oldCapacity=0，newCapacity=0，所以这个判断成立，在这里就是真正的初始化elementData的大小了，就是为10.前面的工作都是准备工作。
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)//如果newCapacity超过了最大的容量限制，就调用hugeCapacity，也就是将能给的最大值给newCapacity
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
    //新的容量大小已经确定好了，就copy数组，改变容量大小咯。
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
```
　hugeCapacity();  
　
```
//这个就是上面用到的方法，很简单，就是用来赋最大值。
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
//如果minCapacity都大于MAX_ARRAY_SIZE，那么就Integer.MAX_VALUE返回，反之将MAX_ARRAY_SIZE返回。因为maxCapacity是三倍的minCapacity，可能扩充的太大了，就用minCapacity来判断了。
//Integer.MAX_VALUE:2147483647   MAX_ARRAY_SIZE：2147483639  也就是说最大也就能给到第一个数值。还是超过了这个限制，就要溢出了。相当于arraylist给了两层防护。
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
```


总结一下扩容过程
- 1.判断是否需要扩容，如果需要，计算需要扩容的最小容量
- 2.如果确定扩容，就执行grow(int minCapacity)，minCapacity为最少需要的容量
- 3.第一次扩容是的容量大小是原来的1.5倍
- 4如果第一次 扩容后容量还是小于minCapacity,那就扩容为minCapacity
- 5.最后，如果minCapacity大于最大容量，则就扩容为最大容量


![](https://user-gold-cdn.xitu.io/2019/12/6/16eda94d3917b520?w=227&h=334&f=png&s=12324)

### add(int, E)方法


```
public void add(int index, E element) {
        // 插入数组位置检查
        rangeCheckForAdd(index);

        // 确保容量，如果需要扩容的话则自动扩容
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index); // 将index后面的元素往后移一个位置
        elementData[index] = element; // 在想要插入的位置插入元素
        size++; // 元素大小加1
    }

  // 针对插入数组的位置，进行越界检查，不通过抛出异常
  // 必须在0-最后一个元素中间的位置插入，，否则就报错
  // 因为数组是连续的空间，不存在断开的情况
  private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

```
这个也不难 前面我们数组已经实现了



### get方法

```
public E get(int index) {
        // 先进行越界检查
        rangeCheck(index);
        // 通过检查则返回结果数据，否则就抛出异常。
        return elementData(index);
    }

    // 越界检查的代码很简单，就是判断想要的索引有没有超过当前数组的最大容量
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
    
    
        @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }

```

先检查数组越界，然后你就是直接去数组那边拿数据然后返回

### set(int index, E element)


```
 // 作用：替换指定索引的元素
  public E set(int index, E element) {
        // 索引越界检查
        rangeCheck(index);

        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }

```
这个就是替换指定位置的元素

### remove(int)：通过删除指定位置上的元素


```
public E remove(int index) {
       // 索引越界检查
        rangeCheck(index);

        modCount++;
        // 得到删除位置元素值
        E oldValue = elementData(index);

        // 计算删除元素后，元素右边需要向左移动的元素个数
        int numMoved = size - index - 1;
        if (numMoved > 0)
            // 进行移动操作，图形过程大致类似于上面的add(int, E)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        // 元素大小减1，并且将最后一个置为null.
        // 置为null的原因，就是让gc起作用，所以需要显示置为null
        elementData[--size] = null; // clear to let GC do its work

        // 返回删除的元素值
        return oldValue;
    }

```

### remove(Object o)



```
public boolean remove(Object o) {
        // 如果删除元素为null,则循环找到第一个null,并进行删除
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    // 根据下标删除
                    fastRemove(index);
                    return true;
                }
        } else {
        // 否则就找到数组中和o相等的元素，返回下标进行删除
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    // 根据下标删除
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }


    private void fastRemove(int index) {
        modCount++;
        // 计算删除元素后，元素右边需要向左移动的元素个数
        int numMoved = size - index - 1;
        if (numMoved > 0)
            // 进行移动操作
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        // 元素大小减1，并且将最后一个置为null. 原因同上。
        elementData[--size] = null; // clear to let GC do its work
    }

```

## 总结

### ArrayList的优缺点
- ArrayList底层以数组实现，是一种随机访问模式，再加上它实现了RandomAccess接口，因此查找也就是get的时候非常快
- ArrayList在顺序添加一个元素的时候非常方便，只是往数组里面添加了一个元素而已
- 删除元素时，涉及到元素复制，如果要复制的元素很多，那么就会比较耗费性能
- 插入元素时，涉及到元素复制，如果要复制的元素很多，那么就会比较耗费性能

### 为什么ArrayList的elementData是用transient修饰的
- 说明：ArrayList实现了Serializable接口，这意味着ArrayList是可以被序列化的，用transient修饰elementData意味着我不希望elementData数组被序列化
- 理解：序列化ArrayList的时候，ArrayList里面的elementData未必是满的，比方说elementData有10的大小，但是我只用了其中的3个，那么是否有必要序列化整个elementData呢？显然没有这个必要，因此ArrayList中重写了writeObject方法。

```
 private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException{
        // Write out element count, and any hidden stuff
        int expectedModCount = modCount;
        s.defaultWriteObject();

        // Write out size as capacity for behavioural compatibility with clone()
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (int i=0; i<size; i++) {
            s.writeObject(elementData[i]);
        }

        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

```
- 优点：这样做既提高了序列化的效率，减少了无意义的序列化；而且还减少了序列化后文件大小。
### 版本说明
- 这里的源码是JDK8版本，不同版本可能会有所差异，但是基本原理都是一样的。

## 结尾
>  ArrayList就这么多了，大家自己可以对着博客，对着源码看，我感激它这个源码不是很难，基于数组的把可能是
> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
