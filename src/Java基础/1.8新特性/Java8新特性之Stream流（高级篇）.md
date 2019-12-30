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
[🔥Java8新特性之Stream流（基础篇）](https://juejin.im/post/5df4a93e51882512454b37fa)  
昨天的是入门，今天跟大家讲讲点高级东西哈

## Stream的Collector

### 介绍
前面我们使用过collect(toList())，在流中生成列表。实际开发过程中，List又是我们经常用到的数据结构，但是有时候我们也希望Stream能够转换生成其他的值，比如Map或者set，甚至希望定制生成想要的数据结构。

collect也就是收集器，是Stream一种通用的、从流生成复杂值的结构。只要将它传给collect方法，也就是所谓的转换方法，其就会生成想要的数据结构。这里不得不提下，Collectors这个工具库，在该库中封装了相应的转换方法。当然，Collectors工具库仅仅封装了常用的一些情景，如果有特殊需求，那就要自定义了。

显然，List是能想到的从流中生成的最自然的数据结构， 但是有时人们还希望从流生成其他值， 比如 Map 或 Set， 或者你希望定制一个类将你想要的东西抽象出来。

**是收集器，一种通用的、从流生成复杂值的结构。只要将它传给collect 方法，所有的流就都可以使用它了**


## Collector（最常用的）
Collector是Stream的可变减少操作接口，可变减少操作包括：将元素累积到集合中，使用StringBuilder连接字符串;计算元素相关的统计信息，例如sum，min，max或average等。Collectors(类收集器)提供了许多常见的可变减少操作的实现。

首先我们来看看它长什么样

![](https://user-gold-cdn.xitu.io/2019/12/15/16f09201351af6f0?w=808&h=325&f=png&s=53139)

Collector<T, A, R>接受三个泛型参数，对可变减少操作的数据类型作相应限制：
- T：输入元素类型
- A：缩减操作的可变累积类型（通常隐藏为实现细节）
- R：可变减少操作的结果类型
- 

Collector接口声明了4个函数，这四个函数一起协调执行以将元素目累积到可变结果容器中，并且可以选择地对结果进行最终的变换

- Supplier<A> supplier(): 创建新的结果结
- BiConsumer<A, T> accumulator(): 将元素添加到结果容器
- BinaryOperator<A> combiner(): 将两个结果容器合并为一个结果容器
- Function<A, R> finisher(): 对结果容器作相应的变换


Characteristics是Collector内的一个枚举类，声明了CONCURRENT、UNORDERED、IDENTITY_FINISH等三个属性，用来约束Collector的属性。

- CONCURRENT：表示此收集器支持并发，意味着允许在多个线程中，累加器可以调用结果容器
- UNORDERED：表示收集器并不按照Stream中的元素输入顺序执行
- IDENTITY_FINISH：表示finisher实现的是识别功能，可忽略。

## Collectors 基本使用
首先我们先了解一下，Collectors可以帮我们完成的事情，例如：分组、排序（支持多字段排序）、最大值、最小值、平均值，简单的来说，以前我们在数据上面用sql 去完成的聚合相关的操作，Collectors 都可以完成

![](https://user-gold-cdn.xitu.io/2019/12/16/16f0c4b1f11f2220?w=588&h=623&f=png&s=89249)

太多了，哈哈 我们慢慢来看

### 转换成其他集合
对于前面提到了很多Stream的链式操作，但是，我们总是要将Strea生成一个集合，比如：
- 已有代码是为集合编写的， 因此需要将流转换成集合传入；
- 在集合上进行一系列链式操作后， 最终希望生成一个值；
- 写单元测试时， 需要对某个具体的集合做断言。

有些Stream可以转成集合，比如前面提到toList,生成了java.util.List 类的实例。当然了，还有还有toSet和toCollection，分别生成 Set和Collection 类的实例。这个是最常用的用法之一

#### toList
```
List<Integer> collectList = Stream.of(1, 2, 3, 4)
        .collect(Collectors.toList());
System.out.println("collectList: " + collectList);
// collectList: [1, 2, 3, 4]

```
#### toSet


```
Set<Integer> collectSet = Stream.of(1, 2, 3, 4)
        .collect(Collectors.toSet());
System.out.println("collectSet: " + collectSet);
// collectSet: [1, 2, 3, 4]

```

#### toMap
如果生成一个Map,我们需要调用toMap方法。由于Map中有Key和Value这两个值，故该方法与toSet、toList等的处理方式是不一样的。toMap最少应接受两个参数，一个用来生成key，另外一个用来生成value。toMap方法有三种变形：
```
Map<Integer, String> map = list.stream().collect(Collectors.toMap(Person::getId, Person::getName));  
 
```

### 转成值
使用collect可以将Stream转换成值。maxBy和minBy允许用户按照某个特定的顺序生成一个值。

- averagingDouble:求平均值，Stream的元素类型为double
- averagingInt:求平均值，Stream的元素类型为int
- averagingLong:求平均值，Stream的元素类型为long
- counting:Stream的元素个数
- maxBy:在指定条件下的，Stream的最大元素
- minBy:在指定条件下的，Stream的最小元素
- reducing: reduce操作
- summarizingDouble:统计Stream的数据(double)状态，其中包括count，min，max，sum和平均。
- summarizingInt:统计Stream的数据(int)状态，其中包括count，min，max，sum和平均。
- summarizingLong:统计Stream的数据(long)状态，其中包括count，min，max，sum和平均。
- summingDouble:求和，Stream的元素类型为double
- summingInt:求和，Stream的元素类型为int
- summingLong:求和，Stream的元素类型为long

举个例子（简单的大家自己去试）：



```
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("六脉神剑", "大神", "小菜鸡", "交流群：549684836");
        Integer integer = strings.stream().filter(string -> string.length() <= 6).map(String::length).sorted().limit(2)
                .distinct().collect(Collectors.maxBy(Comparator.comparing(Integer::intValue))).orElse(0);
    
        System.out.println(integer);

    }
    //3
```

### 分割数据块
collect的一个常用操作将Stream分解成两个集合。假如一个数字的Stream，我们可能希望将其分割成两个集合，一个是偶数集合，另外一个是奇数集合。我们首先想到的就是过滤操作，通过两次过滤操作，很简单的就完成了我们的需求。

但是这样操作起来有问题。首先，为了执行两次过滤操作，需要有两个流。其次，如果过滤操作复杂，每个流上都要执行这样的操作， 代码也会变得冗余。

这里我们就不得不说Collectors库中的partitioningBy方法，它接受一个流，并将其分成两部分：使用Predicate对象，指定条件并判断一个元素应该属于哪个部分，并根据布尔值返回一个Map到列表。因此对于key为true所对应的List中的元素，满足Predicate对象中指定的条件；同样，key为false所对应的List中的元素,不满足Predicate对象中指定的条件

```
    public static void main(String[] args) {

        Map<Boolean, List<Integer>> collectParti = Stream.of(1, 2, 3, 4)
                .collect(Collectors.partitioningBy(it -> it % 2 == 0));
        System.out.println("collectParti : " + collectParti);

    }
    //collectParti : {false=[1, 3], true=[2, 4]}
```

### 数据分组

数据分组是一种更自然的分割数据操作， 与将数据分成true和false两部分不同，可以使用任意值对数据分组。

调用Stream的collect方法，传入一个收集器,groupingBy接受一个分类函数，用来对数据分组，就像partitioningBy一样，接受一个
Predicate对象将数据分成true和false两部分。我们使用的分类器是一个Function对象，和map操作用到的一样。

```
Map<Boolean, List<Integer>> collectGroup= Stream.of(1, 2, 3, 4)
            .collect(Collectors.groupingBy(it -> it > 3));
System.out.println("collectGroup : " + collectGroup);// 打印结果
// collectGroup : {false=[1, 2, 3], true=[4]}

```
### 字符串
Collectors.joining 收集Stream中的值，该方法可以方便地将Stream得到一个字符串。joining函数接受三个参数，分别表示允（用以分隔元素）、前缀和后缀。

```
String strJoin = Stream.of("1", "2", "3", "4")
        .collect(Collectors.joining(",", "[", "]"));
System.out.println("strJoin: " + strJoin);
// strJoin: [1,2,3,4]
```

### 综合演练

前面，我们已经了解到Collector的强大，而且非常的使用。如果将他们组合起来，是不是更厉害呢？看前面举过的例子，在数据分组时，我们是得到的分组后的数据列表 collectGroup : {false=[1, 2, 3], true=[4]}。如果我们的要求更高点，我们不需要分组后的列表，只要得到分组后列表的个数就好了。

```
// 分割数据块
Map<Boolean, List<Integer>> collectParti = Stream.of(1, 2, 3, 4)
        .collect(Collectors.partitioningBy(it -> it % 2 == 0));

Map<Boolean, Integer> mapSize = new HashMap<>();
collectParti.entrySet()
        .forEach(entry -> mapSize.put(entry.getKey(), entry.getValue().size()));

System.out.println("mapSize : " + mapSize);
// mapSize : {false=2, true=2}

```

在partitioningBy方法中，有这么一个变形：


```
Map<Boolean, Long> partiCount = Stream.of(1, 2, 3, 4)
        .collect(Collectors.partitioningBy(it -> it.intValue() % 2 == 0,
                Collectors.counting()));
System.out.println("partiCount: " + partiCount);
// partiCount: {false=2, true=2}
```

多级分组： 嵌套使用groupingBy即可

```
List<String> views = Lists.newArrayList("wsbsq","hello word","b8fw", "word", "wall", "ad");

Map<Object, Map<Object, List<String>>> res = views.stream().collect(groupingBy(str ->str.charAt(0),groupingBy(String::length)));

System.out.println(res);

// {a={2=[ad]}, b={4=[b8fw]}, w={4=[word, wall], 5=[wsbsq]}, h={10=[hello word]}}
```

分組后取最小值

```
       List<Integer> list= Arrays.asList(1,1,2,2,3,5,7,10,11,11,12);

        Map<Integer, Optional<Integer>> collect = list.stream().collect(groupingBy(Integer::intValue, Collectors.minBy(Comparator.comparing(Integer::intValue))));
        collect.forEach((key,value)->{
            System.out.println("key"+key+"      "+"value"+value);
        });

      //key1      valueOptional[1]
      //key2      valueOptional[2]
      //key3      valueOptional[3]
      //key5      valueOptional[5]
      //key7      valueOptional[7]
      //key10      valueOptional[10]
      //key11      valueOptional[11]
      //key12      valueOptional[12]
```
## Stream中的reduce方法

### API个人理解
reduce方法有三个重载的方法，方法签名如下

#### 第一个方法
第一个方法接受一个BinaryOperator类型的lambada表达式， 常规应用方法如下
```
Optional<T> reduce(BinaryOperator<T> accumulator);
```
例子

```
List<Integer> numList = Arrays.asList(1,2,3,4,5);
int result = numList.stream().reduce((a,b) -> a + b ).get();
System.out.println(result);
```
#### 第二个方法
与第一个方法的实现的唯一区别是它首次执行时表达式第一次参数并不是stream的第一个元素，而是通过签名的第一个参数identity来指定。我们来通过这个签名对之前的求和代码进行改进
```
T reduce(T identity, BinaryOperator<T> accumulator);
```
例子

```
List<Integer> numList = Arrays.asList(1,2,3,4,5);
int result = numList.stream().reduce(0,(a,b) ->  a + b );
System.out.println(result);
```

其实这两种实现几乎差别，第一种比第一种仅仅多了一个字定义初始值罢了。 此外，因为存在stream为空的情况，所以第一种实现并不直接方法计算的结果，而是将计算结果用Optional来包装，我们可以通过它的get方法获得一个Integer类型的结果，而Integer允许null。第二种实现因为允许指定初始值，因此即使stream为空，也不会出现返回结果为null的情况，当stream为空，reduce为直接把初始值返回。
#### 第三个方法
第三种方法的用法相较前两种稍显复杂，由于前两种实现有一个缺陷，它们的计算结果必须和stream中的元素类型相同，如上面的代码示例，stream中的类型为int，那么计算结果也必须为int，这导致了灵活性的不足，甚至无法完成某些任务， 比入我们咬对一个一系列int值求和，但是求和的结果用一个int类型已经放不下，必须升级为long类型，此实第三签名就能发挥价值了，它不将执行结果与stream中元素的类型绑死。

```
<U> U reduce(U identity,
                 BiFunction<U, ? super T, U> accumulator,
                 BinaryOperator<U> combiner);
```


例子

```
List<Integer> numList = Arrays.asList(1, 2, 3, 4, 5, 6);
ArrayList<String> result = numList.stream().reduce(new ArrayList<String>(), (a, b) -> {
    a.add("element-" + Integer.toString(b));
    return a;
}, (a, b) -> null);
System.out.println(result);
//[element-1, element-2, element-3, element-4, element-5, element-6]
```

## 基于JDK 1.8的时间工具类
因为1.8的新特性我基本上都讲完了。还差一个对时间的处理，Java8中对时间的处理主要是LocalDate、LocalTime、LocalDateTime，我呢？也没打算说全部讲一遍，这个比较简单，我就给大家一个我们的工具类，拿来就能用的

```
package com.hq.eos.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    private static final String HYPHEN = "-";
    private static final String COLON = ":";

    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 时间格式 DateTimeFormatter (Java8) ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/
    enum FormatEnum {
        /**
         * 返回 DateTimeFormatter "yyyy-MM-dd HH:mm:ss" 时间格式
         */
        FORMAT_DATA_TIME(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),

        /**
         * 返回 DateTimeFormatter "yyyyMMddHHmmss"的时间格式
         */
        FORMAT_DATA_TIME_NO_SYMBOL(DateTimeFormatter.ofPattern(DATETIME_FORMAT)),

        /**
         * 返回 DateTimeFormatter "yyyy-MM-dd"的时间格式
         */
        FORMAT_DATE(DateTimeFormatter.ofPattern(DATE_FORMAT)),

        /**
         * 返回 DateTimeFormatter "HH:mm:ss"的时间格式
         */
        FORMAT_TIME(DateTimeFormatter.ofPattern(TIME_FORMAT));

        private DateTimeFormatter value;

        FormatEnum(DateTimeFormatter format) {
            this.value = format;
        }
    }
    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 时间格式 DateTimeFormatter (Java8) ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/

    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 时间格式 字符串 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/

    /**
     * 年的时间格式
     * <br/>
     * 返回 "yyyy" 字符串
     */
    public static final String YEAR_FORMAT = "yyyy";

    /**
     * 月的时间格式
     * <br/>
     * 返回 "MM" 字符串
     */
    public static final String MONTH_FORMAT = "MM";

    /**
     * 日的时间格式
     * <br/>
     * 返回 "dd" 字符串
     */
    public static final String DAY_FORMAT = "dd";

    /**
     * 时的时间格式
     * <br/>
     * 返回 "HH" 字符串
     */
    public static final String HOUR_FORMAT = "HH";

    /**
     * 分的时间格式
     * <br/>
     * 返回 "mm" 字符串
     */
    public static final String MINUTE_FORMAT = "mm";

    /**
     * 秒的时间格式
     * <br/>
     * 返回 "ss" 字符串
     */
    public static final String SECOND_FORMAT = "ss";

    /**
     * <span color='red'>年-月-日</span>的时间格式
     * <br/>
     * 返回 "yyyy-MM-dd" 字符串
     */
    public static final String DATE_FORMAT = YEAR_FORMAT + HYPHEN + MONTH_FORMAT + HYPHEN + DAY_FORMAT;

    /**
     * <span color='red'>时:分:秒</span>的时间格式
     * <br/>
     * 返回 "HH:mm:ss" 字符串
     */
    public static final String TIME_FORMAT = HOUR_FORMAT + COLON + MINUTE_FORMAT + COLON + SECOND_FORMAT;

    /**
     * <span color='red'>年-月-日 时:分:秒</span>的时间格式
     * <br/>
     * 返回 "yyyy-MM-dd HH:mm:ss" 字符串
     */
    public static final String DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;

    /**
     * <span color='red'>年月日时分秒</span>的时间格式（无符号）
     * <br/>
     * 返回 "yyyyMMddHHmmss" 字符串
     */
    public static final String DATETIME_FORMAT = YEAR_FORMAT + MONTH_FORMAT + DAY_FORMAT + HOUR_FORMAT + MINUTE_FORMAT + SECOND_FORMAT;

    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 时间格式 字符串 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/



    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 时间戳 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/

    /**
     * 获取秒级时间戳
     */
    public static Long epochSecond() {
        return localDateTime().toEpochSecond(ZoneOffset.of("+8"));
    }

    /**
     * 获取毫秒级时间戳
     */
    public static Long epochMilli() {
        return localDateTime().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 时间戳 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/


    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 当前时间相关 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/

    /**
     * 获取当前详细时间，like 2018-08-27 17:20:06
     */
    public static String dateTime() {
        return localDateTime().format(FormatEnum.FORMAT_DATA_TIME.value);
    }

    /**
     * 获取当前详细时间，like 20180827172006
     */
    public static String dateTimeNoSymbol() {
        return localDateTime().format(FormatEnum.FORMAT_DATA_TIME_NO_SYMBOL.value);
    }

    /**
     * 获取当前日期，like 2018-08-27
     */
    public static String date() {
        return localDate() + "";
    }

    /**
     * 获取当前时间，like 17:20:06
     */
    public static String time() {
        return localTime().format(FormatEnum.FORMAT_TIME.value);
    }

    /**
     * 获取当前年
     */
    public static Integer year() {
        return localDate().getYear();
    }

    /**
     * 获取当前月
     */
    public static int month() {
        return localDate().getMonthValue();
    }

    /**
     * 获取当前年中的日
     */
    public static Integer dayOfYear() {
        return localDate().getDayOfYear();
    }

    /**
     * 获取当前月中的日
     */
    public static Integer dayOfMonth() {
        return localDate().getDayOfMonth();
    }

    /**
     * 获取当前星期中的日
     */
    public static Integer dayOfWeek() {
        return localDate().getDayOfWeek().getValue();
    }

    /**
     * 获取当前小时
     */
    public static Integer hour() {
        return localTime().getHour();
    }

    /**
     * 获取当前分钟
     */
    public static Integer minute() {
        return localTime().getMinute();
    }

    /**
     * 获取当前秒
     */
    public static Integer second() {
        return localTime().getSecond();
    }

    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 当前时间相关 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/



    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 未来、历史时间相关 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/

    /**
     * 获取当前年的 前几年 的日期
     * <p>
     *
     * @param years  前几年 正整数
     * @param formatEnum 格式
     * @return 当前年的 前几年 的 对应 格式 日期
     */
    public static String minusYears(Long years, FormatEnum formatEnum) {
        return minusOrPlusYears(-years, formatEnum);
    }

    /**
     * 获取当前年的 后几年 的日期
     * <p>
     *
     * @param years  后几年 正整数
     * @param formatEnum 格式
     * @return 当前年的 后几年 的 对应 格式 日期
     */
    public static String plusYears(Long years, FormatEnum formatEnum) {
        return minusOrPlusYears(years, formatEnum);
    }

    /**
     * 获取当前月的 前几月 日期
     *
     * @param months     前几月 正整数
     * @param formatEnum 格式
     * @return 当前月的 前几月 的 对应 格式 日期
     */
    public static String minusMonths(Long months, FormatEnum formatEnum) {
        return minusOrPlusMonths(-months, formatEnum);
    }

    /**
     * 获取当前月的 后几月 的日期
     *
     * @param months     后几月 正整数
     * @param formatEnum 格式
     * @return 当前月的 后几月 的 对应 格式 日期
     */
    public static String plusMonths(Long months, FormatEnum formatEnum) {
        return minusOrPlusMonths(months, formatEnum);
    }

    /**
     * 获取当前日的 前几日 的日期
     *
     * @param days       前几日 正整数
     * @param formatEnum 格式
     * @return 当前日的 前几日 的 对应 格式 日期
     */
    public static String minusDays(Long days, FormatEnum formatEnum) {
        return minusOrPlusDays(-days, formatEnum);
    }

    /**
     * 获取当前日的 后几日 的日期
     *
     * @param days       后几日 正整数
     * @param formatEnum 格式
     * @return 当前日的 后几日 的 对应 格式 日期
     */
    public static String plusDays(Long days, FormatEnum formatEnum) {
        return minusOrPlusDays(days, formatEnum);
    }

    /**
     * 获取当前星期的 前几星期 的日期
     *
     * @param weeks      前几星期 正整数
     * @param formatEnum 格式
     * @return 当前星期的 前几星期 的 对应 格式 日期
     */
    public static String minusWeeks(Long weeks, FormatEnum formatEnum) {
        return minusOrPlusWeeks(-weeks, formatEnum);
    }

    /**
     * 获取当前星期的 后几星期 的日期
     *
     * @param weeks      后几星期 正整数
     * @param formatEnum 格式
     * @return 当前星期的 后几星期 的 对应 格式 日期
     */
    public static String plusWeeks(Long weeks, FormatEnum formatEnum) {
        return minusOrPlusWeeks(weeks, formatEnum);
    }

    /**
     * 获取当前小时的 前几小时 的日期
     *
     * @param hours      前几小时 正整数
     * @param formatEnum 格式
     * @return 当前小时的 前几小时 的 对应 格式 日期
     */
    public static String minusHours(Long hours, FormatEnum formatEnum) {
        return minusOrPlusHours(-hours, formatEnum);
    }

    /**
     * 获取当前小时的 后几小时 的日期
     *
     * @param hours      后几小时 正整数
     * @param formatEnum 格式
     * @return 当前小时的 后几小时 的 对应 格式 日期
     */
    public static String plusHours(Long hours, FormatEnum formatEnum) {
        return minusOrPlusHours(hours, formatEnum);
    }

    /**
     * 获取当前分钟的 前几分钟 的日期
     *
     * @param minutes    前几分钟 正整数
     * @param formatEnum 格式
     * @return 当前分钟的 前几分钟 的 对应 格式 日期
     */
    public static String minusMinutes(Long minutes, FormatEnum formatEnum) {
        return minusOrPlusMinutes(-minutes, formatEnum);
    }

    /**
     * 获取当前分钟的 后几分钟 的日期
     *
     * @param minutes    后几分钟 正整数
     * @param formatEnum 格式
     * @return 当前分钟的 后几分钟 的 对应 格式 日期
     */
    public static String plusMinutes(Long minutes, FormatEnum formatEnum) {
        return minusOrPlusMinutes(minutes, formatEnum);
    }

    /**
     * 获取当前秒的 前几秒 的日期
     *
     * @param seconds    前几秒 正整数
     * @param formatEnum 格式
     * @return 当前秒的 前几秒 的 对应 格式 日期
     */
    public static String minusSeconds(Long seconds, FormatEnum formatEnum) {
        return minusOrPlusSeconds(-seconds, formatEnum);
    }

    /**
     * 获取当前秒的 前几秒/后几秒 的日期
     *
     * @param seconds    后几秒 正整数
     * @param formatEnum 格式
     * @return 当前秒的 后几秒 的 对应 格式 日期
     */
    public static String plusSeconds(Long seconds, FormatEnum formatEnum) {
        return minusOrPlusSeconds(seconds, formatEnum);
    }

    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 未来、历史时间相关 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/


    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 时间转换相关 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/

    /**
     * Date类型转LocalDateTime
     * <p>
     *
     * @param date date类型时间
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Date类型转LocalDate
     * <p>
     *
     * @param date date类型时间
     * @return LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        return toLocalDateTime(date).toLocalDate();
    }

    /**
     * Date类型转LocalTime
     * <p>
     *
     * @param date date类型时间
     * @return LocalTime
     */
    public static LocalTime toLocalTime(Date date) {
        return toLocalDateTime(date).toLocalTime();
    }

    /**
     * LocalDateTime 类型转 Date
     *
     * @param localDateTime localDateTime
     * @return 转换后的Date类型日期
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDate类型转Date
     *
     * @param localDate localDate
     * @return 转换后的Date类型日期
     */
    public static Date toDate(LocalDate localDate) {
        return toDate(localDate.atStartOfDay());
    }

    /**
     * LocalTime类型转Date
     *
     * @param localTime localTime
     * @return 转换后的Date类型日期
     */
    public static Date toDate(LocalTime localTime) {
        return toDate(LocalDateTime.of(localDate(), localTime));
    }

    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 时间转换相关 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/


    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 时间间隔相关 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/

    /**
     * 获取 endDate-startDate 时间间隔天数
     * <br>创建人： leigq
     * <br>创建时间： 2018-11-07 09:55
     * <br>
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间间隔天数
     */
    public static Long daysInterval(LocalDate startDate, LocalDate endDate) {
        return endDate.toEpochDay() - startDate.toEpochDay();
    }

    /**
     * 获取 endDate-startDate 时间间隔天数
     * <br>创建人： leigq
     * <br>创建时间： 2018-11-07 09:55
     * <br>
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间间隔天数
     */
    public static Long daysInterval(String startDate, String endDate) {
        return daysInterval(LocalDateTime.parse(endDate, FormatEnum.FORMAT_DATA_TIME.value).toLocalDate(),
                LocalDateTime.parse(startDate, FormatEnum.FORMAT_DATA_TIME.value).toLocalDate());
    }

    /**
     * 获取 endDate-startDate 时间间隔天数
     * <br>创建人： leigq
     * <br>创建时间： 2018-11-07 09:55
     * <br>
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间间隔天数
     */
    public static Long daysInterval(LocalDateTime startDate, LocalDateTime endDate) {
        return daysInterval(startDate.toLocalDate(), endDate.toLocalDate());
    }

    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 时间间隔相关 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/

    /*↓↓↓只允许此类调用↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/

    /**
     * 获取 当前年 的前几年/后几年的日期
     * <p>
     *
     * @param yearsToAddOrSubtract 后几年传正整数，前几年传负数
     * @param formatEnum           格式
     * @return 当前年的前几年/后几年的对应 格式 日期
     */
    private static String minusOrPlusYears(Long yearsToAddOrSubtract, FormatEnum formatEnum) {
        return localDateTime().plusYears(yearsToAddOrSubtract).format(formatEnum.value);
    }

    /**
     * 获取 当前月 的前几月/后几月的日期
     *
     * @param monthsToAddOrSubtract 后几月传正整数，前几月传负数
     * @param formatEnum            格式
     * @return 当前月的前几月/后几月的对应 格式 日期
     */
    private static String minusOrPlusMonths(Long monthsToAddOrSubtract, FormatEnum formatEnum) {
        return localDateTime().plusMonths(monthsToAddOrSubtract).format(formatEnum.value);
    }

    /**
     * 获取 当前日 的前几日/后几日的日期
     *
     * @param daysToAddOrSubtract 后几日传正整数，前几日传负数
     * @param formatEnum          格式
     * @return 当前日的前几日/后几日的 对应 格式 日期
     */
    private static String minusOrPlusDays(Long daysToAddOrSubtract, FormatEnum formatEnum) {
        return localDateTime().plusDays(daysToAddOrSubtract).format(formatEnum.value);
    }

    /**
     * 获取当前星期的前几星期/后几星期的日期
     *
     * @param weeksToAddOrSubtract 后几星期传正整数，前几星期传负数
     * @param formatEnum           格式
     * @return 当前星期的前几星期/后几星期的 对应 格式 日期
     */
    private static String minusOrPlusWeeks(Long weeksToAddOrSubtract, FormatEnum formatEnum) {
        return localDateTime().plusWeeks(weeksToAddOrSubtract).format(formatEnum.value);
    }

    /**
     * 获取当前小时的前几小时/后几小时的日期
     *
     * @param hoursToAddOrSubtract 后几小时传正整数，前几小时传负数
     * @param formatEnum           格式
     * @return 当前小时的前几小时/后几小时的 对应 格式 日期
     */
    private static String minusOrPlusHours(Long hoursToAddOrSubtract, FormatEnum formatEnum) {
        return localDateTime().plusHours(hoursToAddOrSubtract).format(formatEnum.value);
    }

    /**
     * 获取当前分钟的前几分钟/后几分钟的日期
     *
     * @param minutesToAddOrSubtract 后几分钟传正整数，前几分钟传负数
     * @param formatEnum             格式
     * @return 当前分钟的前几分钟/后几分钟的 对应 格式 日期
     */
    private static String minusOrPlusMinutes(Long minutesToAddOrSubtract, FormatEnum formatEnum) {
        return localDateTime().plusMinutes(minutesToAddOrSubtract).format(formatEnum.value);
    }

    /**
     * 获取当前秒的前几秒/后几秒的日期
     *
     * @param secondsToAddOrSubtract 后几秒传正整数，前几秒传负数
     * @param formatEnum             格式
     * @return 当前秒的前几秒/后几秒的 对应 格式 日期
     */
    private static String minusOrPlusSeconds(Long secondsToAddOrSubtract, FormatEnum formatEnum) {
        return localDateTime().plusSeconds(secondsToAddOrSubtract).format(formatEnum.value);
    }

    /**
     * 获取 LocalDate
     */
    private static LocalDate localDate() {
        return localDateTime().toLocalDate();
    }

    /**
     * 获取 LocalTime
     */
    private static LocalTime localTime() {
        return localDateTime().toLocalTime();
    }

    /**
     * 获取 LocalDateTime
     */
    private static LocalDateTime localDateTime() {
        return LocalDateTime.now();
    }

}

```


## 结尾
Stream 的高级用法也结束了，其实一篇博客，最多带大家了解一下，要实际再项目中多思考，用了，方能熟能生巧， 感觉Map，Reduce 和 大数据的MR 很像，分而治之的思想。哈哈，扯的有点远，还在Java基础就扯到大数据了，不过，大家一起慢慢累积吧，总会讲到的
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！


