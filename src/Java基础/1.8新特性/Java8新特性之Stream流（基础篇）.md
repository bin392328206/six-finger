# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
今天 开始写Java8新特性系列，怎么说呢，主要有几个新东西
- Lambda表达式
- 函数式接口
- 方法引用
- Stream流
- Optional类
- default关键字

这个四个的主要作用 简化代码编写，提高性能等等，但是也会给维护带来麻烦，因为不懂的人去看，真心累，但是写起来是真的香，今天打算讲标题上的。今天讲讲我们这个Stream流,前面几节可以参考下面链接    
[🔥你不知道的Java内部类](https://juejin.im/post/5df0a84fe51d4557f544f7ac)     
[🔥你必须知道的Java泛型](https://juejin.im/post/5df1b667f265da3398562739)  
[🔥Java8新特性之Lambda表达式，函数式接口，方法引用和default关键字](https://juejin.im/post/5df36bdf6fb9a01608237621)   
[🔥Java8新特性之Optional类](https://juejin.im/post/5df4507f51882512523e7af9)    
大家只要好好学着，那么以后你在公司写的代码就是骚的一批，哈哈。保证学完C位出道

## 什么是 Stream？
**Stream 使用一种类似用 SQL 语句从数据库查询数据的直观方式来提供一种对 Java 集合运算和表达的高阶抽象。**  
Stream（流）是一个来自数据源的元素队列并支持聚合操作
- 元素是特定类型的对象，形成一个队列。 Java中的Stream并不会存储元素，而是按需计算。
- 数据源 流的来源。 可以是集合，数组，I/O channel， 产生器generator 等。
- 聚合操作 类似SQL语句一样的操作， 比如filter, map, reduce, find, match, sorted等。

和以前的Collection操作不同， Stream操作还有两个基础的特征：

- Pipelining: 中间操作都会返回流对象本身。 这样多个操作可以串联成一个管道， 如同流式风格（fluent style）。 这样做可以对操作进行优化， 比如延迟执行(laziness)和短路( short-circuiting)。
- 内部迭代： 以前对集合遍历都是通过Iterator或者For-Each的方式, 显式的在集合外部进行迭代， 这叫做外部迭代。 Stream提供了内部迭代的方式， 通过访问者模式(Visitor)实现。

## Stream的特点

- 无存储。Stream不是一种数据结构，它只是某种数据源的一个视图，数据源可以是一个数组，Java容器或I/O channel等。

-  为函数式编程而生。对Stream的任何修改都不会修改背后的数据源，比如对Stream执行过滤操作并不会删除被过滤的元素，而是会产生一个不包含被过滤元素的新Stream。

- 惰式执行。Stream上的操作并不会立即执行，只有等到用户真正需要结果的时候才会执行。

- 可消费性。Stream只能被“消费”一次，一旦遍历过就会失效，就像容器的迭代器那样，想要再次遍历必须重新生成。

我们举一个例子，来看一下到底Stream可以做什么事情

![](https://user-gold-cdn.xitu.io/2019/12/15/16f086cdfb87a27a?w=866&h=444&f=png&s=162606)
上面的例子中，获取一些带颜色塑料球作为数据源，首先过滤掉红色的、把它们融化成随机的三角形。再过滤器并删除小的三角形。最后计算出剩余图形的周长。

如上图，对于流的处理，主要有三种关键性操作：分别是流的创建、中间操作（intermediate operation）以及最终操作(terminal operation)。

## Stream流的创建

Stream 有关系的类图
![](https://user-gold-cdn.xitu.io/2019/12/15/16f0871100ad03d5?w=841&h=254&f=png&s=65761)

你可以发现全是接口

我们再来看集合，发现实现了这个接口 说明什么呢？说明集合可以是流的源头 还有数组，等等  
问个问题 HashMap 可以用流吗，大家去思考一下。

![](https://user-gold-cdn.xitu.io/2019/12/15/16f0871f19c2d911?w=1469&h=640&f=png&s=181096)

### 通过已有的集合来创建流
在Java 8中，除了增加了很多Stream相关的类以外，还对集合类自身做了增强，在其中增加了stream方法，可以将一个集合类转换成流。

```
List<String> strings = Arrays.asList("六脉神剑", "六脉神剑1", "Hello", "HelloWorld", "六脉神剑2");
Stream<String> stream = strings.stream();
```
以上，通过一个已有的List创建一个流。除此以外，还有一个parallelStream方法，可以为集合创建一个并行流。（多线程方式，需要考虑线程安全问题）

这种通过集合创建出一个Stream的方式也是比较常用的一种方式。
### 通过Stream创建流
可以使用Stream类提供的方法，直接返回一个由指定元素组成的流。  
> of方法
```
Stream<String> stream = Stream.of("六脉神剑", "六脉神剑1", "Hello", "HelloWorld", "六脉神剑2");
```
如以上代码，直接通过of方法，创建并返回一个Stream。

>  generator方法（用得少）

```
Stream<Double> generateA = Stream.generate(new Supplier<Double>() {
    @Override
    public Double get() {
        return java.lang.Math.random();
    }
});

Stream<Double> generateB = Stream.generate(()-> java.lang.Math.random());
Stream<Double> generateC = Stream.generate(java.lang.Math::random);

```

## Stream中间操作（流水线的中间操作）

Stream有很多中间操作，多个中间操作可以连接起来形成一个流水线，每一个中间操作就像流水线上的一个工人，每人工人都可以对流进行加工，加工后得到的结果还是一个流。

![](https://user-gold-cdn.xitu.io/2019/12/15/16f08a49a6e3b068?w=936&h=396&f=png&s=159890)

哈哈，说到流水线博主深有体会，当年读大学的时候家里穷，自己暑假去打暑假工，做的就是流水线，一个产品分成几十道工序，每个人只做其只的一道工序，重复做，我当年就是拧收音机的按键，一天拧1万个，手都破了（因为需要把按键按下去，需要一个能用上力的地方，我就用的大拇指），然后就用那种胶水胶个十几层再大拇指上，第二天继续干。所以博主理解这个流水操作，还是比较简单的哈。

以下是常用的中间操作列表:

![](https://user-gold-cdn.xitu.io/2019/12/15/16f08a825ba0fa1b?w=885&h=321&f=png&s=97248)

让我们一个个看看这些中间操作
### filter
filter 方法用于通过设置的条件过滤出元素（用于过滤得到一个新的流）。以下代码片段使用 filter 方法过滤掉空字符串：



```
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("六脉神剑", "", "大神", "小菜鸡", "交流群：549684836");
        strings.stream().filter(string -> !string.isEmpty()).forEach(System.out::println);
        //六脉神剑,大神 , 小菜鸡,交流群：549684836
    }
```

### concat
concat方法将两个Stream连接在一起，合成一个Stream。若两个输入的Stream都时排序的，则新Stream也是排序的；若输入的Stream中任何一个是并行的，则新的Stream也是并行的；若关闭新的Stream时，原两个输入的Stream都将执行关闭处理。


```
Stream.concat(Stream.of(1, 2, 3), Stream.of(4, 5))
       .forEach(integer -> System.out.print(integer + "  "));
// 1  2  3  4  5  
```
### map 
map 方法用于映射每个元素到对应的结果，以下代码片段使用 map 输出了元素对应的平方数：

```
List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
numbers.stream().map( i -> i*i).forEach(System.out::println);
//9,4,4,9,49,9,25
```

map我的好好讲讲，这个比较重要，谈谈对它的理解吧， 就是你看map里面是一个函数，什么意思呢？我们来看源码，Function 还记得啥意思吗，不记得自己去看我前面写的函数式编程，它的意思是传人一个类型，返回一个类型，就是你本来是String的流，可以转换成Student这个类的流，或者其他的形式的流。你可以把Streamf的集合的流，变成Interge这样，这就是转换，很重要哈。

![](https://user-gold-cdn.xitu.io/2019/12/15/16f08bcd11aad4f2?w=792&h=317&f=png&s=42665)

![](https://user-gold-cdn.xitu.io/2019/12/15/16f08c1e9812bc91?w=1053&h=284&f=png&s=59988)


### flatMap
latMap方法与map方法类似，都是将原Stream中的每一个元素通过转换函数转换，不同的是，该换转函数的对象是一个Stream，也不会再创建一个新的Stream，而是将原Stream的元素取代为转换的Stream。如果转换函数生产的Stream为null，应由空Stream取代。flatMap有三个对于原始类型的变种方法，分别是：flatMapToInt，flatMapToLong和flatMapToDouble。


```
Stream.of(1, 2, 3)
    .flatMap(integer -> Stream.of(integer * 10))
    .forEach(System.out::println);
    // 10，20，30
```
传给flatMap中的表达式接受了一个Integer类型的参数，通过转换函数，将原元素乘以10后，生成一个只有该元素的流，该流取代原流中的元素。

### peek
peek方法生成一个包含原Stream的所有元素的新Stream，同时会提供一个消费函数（Consumer实例），新Stream每个元素被消费的时候都会执行给定的消费函数，并且消费函数优先执行，如果不是很清楚，可以看看Consumer函数就知道了

```
Stream.of(1, 2, 3, 4, 5)
        .peek(integer -> System.out.println("accept:" + integer))
        .forEach(System.out::println);
// accept:1
//  1
//  accept:2
//  2
//  accept:3
//  3
//  accept:4
//  4
//  accept:5
//  5
```




### limit/skip

limit 返回 Stream 的前面 n 个元素；skip 则是扔掉前 n 个元素。以下代码片段使用 limit 方法保理4个元素：

```
List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
numbers.stream().limit(4).forEach(System.out::println);
//3,2,2,3
```

### sorted
sorted 方法用于对流进行排序。以下代码片段使用 sorted 方法进行排序：

```
List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
numbers.stream().sorted().forEach(System.out::println);
//2,2,3,3,3,5,7
```
### distinct
distinct主要用来去重，以下代码片段使用 distinct 对元素进行去重：

```
List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
numbers.stream().distinct().forEach(System.out::println);
//3,2,7,5
```

接下来我们通过一个例子和一张图，来演示下，当一个Stream先后通过filter、map、sort、limit处理后会发生什么。

代码如下：

```
  public static void main(String[] args) {
        List<String> strings = Arrays.asList("六脉神剑",  "大神", "小菜鸡", "交流群：549684836");
        strings.stream().filter(string -> !string.isEmpty()).forEach(System.out::println);
        //六脉神剑,大神 , 小菜鸡,交流群：549684836
        Stream<Integer> distinct = strings.stream().filter(string -> string.length() <= 6).map(String::length).sorted().limit(2)
                .distinct();

    }
```
画个图给大家理解一下，画的不好，大家见谅哈

![](https://user-gold-cdn.xitu.io/2019/12/15/16f08de6e80bb99d?w=1102&h=495&f=png&s=38992)

## Stream最终操作

Stream的中间操作得到的结果还是一个Stream，那么如何把一个Stream转换成我们需要的类型呢？比如计算出流中元素的个数、将流装换成集合等。这就需要最终操作（terminal operation）

最终操作会消耗流，产生一个最终结果。也就是说，在最终操作之后，不能再次使用流，也不能在使用任何中间操作，否则将抛出异常：
```
java.lang.IllegalStateException: stream has already been operated upon or closed
```

常用的最终操作如下图

![](https://user-gold-cdn.xitu.io/2019/12/15/16f090afbae3ff01?w=834&h=303&f=png&s=69141)

### forEach
Stream 提供了方法 'forEach' 来迭代流中的每个数据。以下代码片段使用 forEach 输出了10个随机数：

```
Random random = new Random();
random.ints().limit(10).forEach(System.out::println);
```
### count
count用来统计流中的元素个数。

```
   List<String> strings = Arrays.asList("六脉神剑", "大神", "小菜鸡", "交流群：549684836");
      
        long count = strings.stream().filter(string -> string.length() <= 6).map(String::length).sorted().limit(2)
                .distinct().count();
        System.out.println(count);
        //2
```
### collect

collect就是一个归约操作，可以接受各种做法作为参数，将流中的元素累积成一个汇总结果：


```
   List<String> strings = Arrays.asList("六脉神剑", "大神", "小菜鸡", "交流群：549684836");

        List<Integer> collect = strings.stream().filter(string -> string.length() <= 6).map(String::length).sorted().limit(2)
                .distinct().collect(Collectors.toList());
```
看上图，最终得到一个List 数组，也就是流最终的归宿。

## 结尾
Stream 的基础 差不多讲完了 ，如果大家能够跟着操作一遍，想必对于Stream 函数式编程 算是入了个门了，明天我们讲点高级用法你比如 Collector 收集器 肯定是不止我们刚刚讲的这些，还有万能的reduce,好了明天见
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！


