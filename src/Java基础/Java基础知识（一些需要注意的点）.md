# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
前面写过一篇基础面试的
- [史上最全的Java基础（针对面试）](https://juejin.im/post/5e806d23518825739728583d)

## 正确使用 equals 方法
Object的equals方法容易抛空指针异常，应使用常量或确定有值的对象来调用 equals。

举个例子：

```
null.equals("小六六")
```
这种肯定是会报NEP的，所以我们应该把不会用空的放在前面来避免空指针异常。

还有一个推荐

```
Objects.equals(null,"小六六");// false
```


我们看一下java.util.Objects#equals的源码就知道原因了。


```
public static boolean equals(Object a, Object b) {
        // 可以避免空指针异常。如果a==null的话此时a.equals(b)就不会得到执行，避免出现空指针异常。
        return (a == b) || (a != null && a.equals(b));
    }

```

几点注意的点
- 每种原始类型都有默认值一样，如int默认值为 0，boolean 的默认值为 false，null 是任何引用类型的默认值，不严格的说是所有 - Object 类型的默认值。
- 可以使用 == 或者 != 操作来比较null值，但是不能使用其他算法或者逻辑操作。在Java中null == null将返回true。
- 不能使用一个值为null的引用类型变量来调用非静态方法，否则会抛出异常


###  整型包装类值的比较

```
Integer x = 3;
Integer y = 3;
System.out.println(x == y);// true
Integer a = new Integer(3);
Integer b = new Integer(3);
System.out.println(a == b);//false
System.out.println(a.equals(b));//true
```

当使用自动装箱方式创建一个Integer对象时，当数值在-128 ~127时，会将创建的 Integer 对象缓存起来，当下次再出现该数值时，直接从缓存中取出对应的Integer对象。所以上述代码中，x和y引用的是相同的Integer对象。


### BigDecimal
#### BigDecimal 的用处
《阿里巴巴Java开发手册》中提到：浮点数之间的等值判断，基本数据类型不能用==来比较，包装数据类型不能用 equals 来判断。 具体原理和浮点数的编码方式有关，这里就不多提了，我们下面直接上实例：

```
float a = 1.0f - 0.9f;
float b = 0.9f - 0.8f;
System.out.println(a);// 0.100000024
System.out.println(b);// 0.099999964
System.out.println(a == b);// false
```

具有基本数学知识的我们很清楚的知道输出并不是我们想要的结果（精度丢失），我们如何解决这个问题呢？一种很常用的方法是：使用使用 BigDecimal 来定义浮点数的值，再进行浮点数的运算操作。

```
BigDecimal a = new BigDecimal("1.0");
BigDecimal b = new BigDecimal("0.9");
BigDecimal c = new BigDecimal("0.8");
BigDecimal x = a.subtract(b);// 0.1
BigDecimal y = b.subtract(c);// 0.1
System.out.println(x.equals(y));// true 
```

#### BigDecimal 的大小比较
a.compareTo(b) : 返回 -1 表示小于，0 表示 等于， 1表示 大于。 其实你可以把他当作是 a-b 其实是一个意思

```
BigDecimal a = new BigDecimal("1.0");
BigDecimal b = new BigDecimal("0.9");
System.out.println(a.compareTo(b));// 1

```
#### BigDecimal 保留几位小数


```
BigDecimal m = new BigDecimal("1.255433");
BigDecimal n = m.setScale(3,BigDecimal.ROUND_HALF_DOWN);
System.out.println(n);// 1.255

```

#### BigDecimal 的使用注意事项
注意：我们在使用BigDecimal时，为了防止精度丢失，推荐使用它的 BigDecimal(String) 构造方法来创建对象。《阿里巴巴Java开发手册》对这部分内容也有提到如下图所示。


![](https://user-gold-cdn.xitu.io/2020/4/29/171c4fffb2aa237a?w=1676&h=724&f=png&s=263655)

#### 总结
BigDecimal 主要用来操作（大）浮点数，BigInteger 主要用来操作大整数（超过 long 类型）。

BigDecimal 的实现利用到了 BigInteger, 所不同的是 BigDecimal 加入了小数位的概念


#### 基本数据类型与包装数据类型的使用标准
Reference:《阿里巴巴Java开发手册》
- 【强制】所有的 POJO 类属性必须使用包装数据类型。
- 【强制】RPC 方法的返回值和参数必须使用包装数据类型。
- 【推荐】所有的局部变量使用基本数据类型。
比如我们如果自定义了一个Student类,其中有一个属性是成绩score,如果用Integer而不用int定义,一次考试,学生可能没考,值是null,也可能考了,但考了0分,值是0,这两个表达的状态明显不一样.

说明 :POJO 类属性没有初值是提醒使用者在需要使用时，必须自己显式地进行赋值，任何 NPE 问题，或者入库检查，都由使用者来保证。

正例 : 数据库的查询结果可能是 null，因为自动拆箱，用基本数据类型接收有 NPE 风险。

### Arrays.asList()使用指南

Arrays.asList()在平时开发中还是比较常见的，我们可以使用它将一个数组转换为一个List集合。

```
String[] myArray = { "Apple", "Banana", "Orange" }； 
List<String> myList = Arrays.asList(myArray);
//上面两个语句等价于下面一条语句
List<String> myList = Arrays.asList("Apple","Banana", "Orange");
```
JDK 源码对于这个方法的说明：

```
/**
 *返回由指定数组支持的固定大小的列表。此方法作为基于数组和基于集合的API之间的桥梁，与           Collection.toArray()结合使用。返回的List是可序列化并实现RandomAccess接口。
 */ 
public static <T> List<T> asList(T... a) {
    return new ArrayList<>(a);
}

```
《阿里巴巴Java 开发手册》对其的描述

Arrays.asList()将数组转换为集合后,底层其实还是数组，《阿里巴巴Java 开发手册》对于这个方法有如下描述：


![](https://user-gold-cdn.xitu.io/2020/4/29/171c526563406a89?w=932&h=307&f=png&s=107098)

Arrays.asList() 方法返回的并不是 java.util.ArrayList ，而是 java.util.Arrays 的一个内部类,这个内部类并没有实现集合的修改方法或者说并没有重写这些方法。


```
List myList = Arrays.asList(1, 2, 3);
System.out.println(myList.getClass());//class java.util.Arrays$ArrayList

```

下图是java.util.Arrays$ArrayList的简易源码，我们可以看到这个类重写的方法有哪些。


### 如何正确的将数组转换为ArrayList?

- 最简便的方法(推荐)

```
List list = new ArrayList<>(Arrays.asList("a", "b", "c"))
```
- 使用 Java8 的Stream(推荐)

```
Integer [] myArray = { 1, 2, 3 };
List myList = Arrays.stream(myArray).collect(Collectors.toList());
//基本类型也可以实现转换（依赖boxed的装箱操作）
int [] myArray2 = { 1, 2, 3 };
List myList = Arrays.stream(myArray2).boxed().collect(Collectors.toList());

```

####  不要在 foreach 循环里进行元素的 remove/add 操作
如果要进行remove操作，可以调用迭代器的 remove方法而不是集合类的 remove 方法。因为如果列表在任何时间从结构上修改创建迭代器之后，以任何方式除非通过迭代器自身remove/add方法，迭代器都将抛出一个ConcurrentModificationException,这就是单线程状态下产生的 fail-fast 机制。

![](https://user-gold-cdn.xitu.io/2020/4/29/171c5456271459ee?w=1640&h=1188&f=png&s=209032)

 ### 聊聊fail-fast
 
 在用迭代器遍历一个集合对象时，如果遍历过程中对集合对象的内容进行了修改（增加、删除、修改），则会抛出Concurrent Modification Exception。

原理：迭代器在遍历时直接访问集合中的内容，并且在遍历过程中使用一个 modCount 变量。集合在被遍历期间如果内容发生变化，就会改变modCount的值。每当迭代器使用hashNext()/next()遍历下一个元素之前，都会检测modCount变量是否为expectedmodCount值，是的话就返回遍历；否则抛出异常，终止遍历。

  注意：这里异常的抛出条件是检测到 modCount！=expectedmodCount 这个条件。如果集合发生变化时修改modCount值刚好又设置为了expectedmodCount值，则异常不会抛出。因此，不能依赖于这个异常是否抛出而进行并发操作的编程，这个异常只建议用于检测并发修改的bug。

  场景：java.util包下的集合类都是快速失败的，不能在多线程下发生并发修改（迭代过程中被修改）。
  
  从上面我们可以看出，只要是涉及了改变ArrayList元素的个数的方法都会导致modCount的改变。所以我们这里可以初步判断由于expectedModCount 与modCount的改变不同步，导致两者之间不等，从而产生fail-fast机制。


那么平常我们如何去规避这种情况呢？这里有两种解决方案：

使用CopyOnWriteArrayList来替换ArrayList。
CopyOnWriteArrayList为什么能解决这个问题呢？CopyOnWrite容器即写时复制的容器。通俗的理解是当我们往一个容器添加元素的时候，不直接往当前容器添加，而是先将当前容器进行Copy，复制出一个新的容器，然后新的容器里添加元素，添加完元素之后，再将原容器的引用指向新的容器。CopyOnWriteArrayList中add/remove等写方法是需要加锁的，目的是为了避免Copy出N个副本出来，导致并发写。但是。CopyOnWriteArrayList中的读方法是没有加锁的。

我们只需要记住一句话，那就是CopyOnWriteArrayList是线程安全的，所以我们在多线程的环境下面需要去使用这个就可以了。关于CopyOnWriteArrayList更加深入的用法，会在以后的章节中去解释说明。


## 结尾
文章出自  https://snailclimb.gitee.io/  写的很不错哦
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！