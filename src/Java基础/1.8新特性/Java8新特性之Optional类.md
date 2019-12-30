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

这个四个的主要作用 简化代码编写，提高性能等等，但是也会给维护带来麻烦，因为不懂的人去看，真心累，但是写起来是真的香，今天打算讲标题上的。今天打算讲讲这个防止空指针异常的Optional类,前面几节可以参考下面链接    
[🔥你不知道的Java内部类](https://juejin.im/post/5df0a84fe51d4557f544f7ac)     
[🔥你必须知道的Java泛型](https://juejin.im/post/5df1b667f265da3398562739)      
[🔥Java8新特性之Lambda表达式，函数式接口，方法引用和default关键字](Java8新特性之Lambda表达式，函数式接口，方法引用和default关键字)

文本力求简单讲清每个知识点，希望大家看完能有所收获

## 预介绍
思考： 调用一个方法得到了返回值却不能直接将返回值作为参数去调用别的方法。

原来解决方案： 我们首先要判断这个返回值是否为null，只有在非空的前提下才能将其作为其他方法的参数。这正是一些类似Guava的外部API试图解决的问题。一些JVM编程语言比如Scala、Ceylon等已经将对在核心API中解决了这个问题。 我们来看个例子

![](https://user-gold-cdn.xitu.io/2019/12/14/16f026f9f16ae242?w=1118&h=366&f=png&s=83496)

是不是感觉这样写的话，写法会好很多呢？接下来我们好好看看这个Optional

## Optional

![](https://user-gold-cdn.xitu.io/2019/12/14/16f03005a77689d2?w=1669&h=708&f=png&s=211392)

### Optional先来看看属性和构造方法


```
 // 1、创建出一个Optional容器，容器里边并没有装载着对象
    private static final Optional<?> EMPTY = new Optional<>();

    // 2、代表着容器中的对象
    private final T value;

    // 3、私有构造方法
    private Optional() {
        this.value = null;
    }

    // 4、得到一个Optional容器，Optional没有装载着对象
    public static<T> Optional<T> empty() {
        @SuppressWarnings("unchecked")
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }

    // 5、私有构造方法(带参数)，参数就是具体的要装载的对象，如果传进来的对象为null，抛出异常
    private Optional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    // 5.1、如果传进来的对象为null，抛出异常
    public static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }


    // 6、创建出Optional容器，并将对象(value)装载到Optional容器中。
    // 传入的value如果为null，抛出异常(调用的是Optional(T value)方法)
    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    // 创建出Optional容器，并将对象(value)装载到Optional容器中。
    // 传入的value可以为null，如果为null，返回一个没有装载对象的Optional对象
    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }
```
所以可以得出创建Optional容器有两种方式：
- 调用ofNullable()方法，传入的对象可以为null
- 调用of()方法，传入的对象不可以为null，否则抛出NullPointerException
写个方法测试一下哈哈

of
![](https://user-gold-cdn.xitu.io/2019/12/14/16f031905840b026?w=1452&h=734&f=png&s=121185)

ofNullable

![](https://user-gold-cdn.xitu.io/2019/12/14/16f031aa3fbfa2e9?w=1471&h=723&f=png&s=101729)

### Optional容器简单的方法

```
// 得到容器中的对象，如果为null就抛出异常
public T get() {
    if (value == null) {
        throw new NoSuchElementException("No value present");
    }
    return value;
}

// 判断容器中的对象是否为null
public boolean isPresent() {
    return value != null;
}

// 如果容器中的对象存在，则返回。否则返回传递进来的参数
public T orElse(T other) {
    return value != null ? value : other;
}
```

这三个方法是Optional类比较常用的方法，并且是最简单的。(因为参数不是函数式接口)

来看用法

![](https://user-gold-cdn.xitu.io/2019/12/14/16f032080e67c1d1?w=1578&h=713&f=png&s=162384)

所以这个就是我们用来再项目中校空的，用的还挺多的

### Optional容器进阶用法（主要是因为函数式编程，简化代码）

#### 首先来看看ifPresent(Consumer<? super T> consumer)方法


```
public void ifPresent(Consumer<? super T> consumer) {
    if (value != null)
        consumer.accept(value);
}

@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}
```

![](https://user-gold-cdn.xitu.io/2019/12/14/16f0333f48ae8ba5?w=1444&h=738&f=png&s=119646)

#### orElseGet和orElseThrow方法
源码
```
// 如果对象存在，则直接返回，否则返回由Supplier接口的实现用来生成默认值
public T orElseGet(Supplier<? extends T> other) {
    return value != null ? value : other.get();
}


@FunctionalInterface
public interface Supplier<T> {
    T get();
}


// 如果存在，则返回。否则抛出supplier接口创建的异常
public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    if (value != null) {
        return value;
    } else {
        throw exceptionSupplier.get();
    }
}
```

用法

```
public static void main(String[] args) {

    User user = new User();
    user.setName("六脉神剑");
    test(user);
}

public static void test(User user) {

    Optional<User> optional = Optional.ofNullable(user);

    // 如果存在user，则直接返回，否则创建出一个新的User对象
    User user1 = optional.orElseGet(() -> new User());

    // 旧写法
    if (user != null) {
        user = new User();
    }
}
```
#### filter方法
源码

```
// 如果容器中的对象存在，并且符合过滤条件，返回装载对象的Optional容器，否则返回一个空的Optional容器
public Optional<T> filter(Predicate<? super T> predicate) {
    Objects.requireNonNull(predicate);
    if (!isPresent())
        return this;
    else
        return predicate.test(value) ? this : empty();
}


// 接口
@FunctionalInterface
public interface Predicate<T> {

    boolean test(T t);
}
```

用法

```
public static void test(User user) {

    Optional<User> optional = Optional.ofNullable(user);

    // 如果容器中的对象存在，并且符合过滤条件，返回装载对象的Optional容器，否则返回一个空的Optional容器
        Optional<User> optional = Optional.ofNullable(user);
        User user1 = optional.filter((value) -> "六脉神剑".equals(value.getName())).orElse(null);
        System.out.println(user1.getName());
}
```
#### map
源码


```
// 如果容器的对象存在，则对其执行调用mapping函数得到返回值。然后创建包含mapping返回值的Optional，否则返回空Optional。
public<U> Optional<U> map(Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper);
    if (!isPresent())
        return empty();
    else {
        return Optional.ofNullable(mapper.apply(value));
    }
}


// 接口
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}

```
用法

```
public static void test(User user) {

    Optional<User> optional = Optional.ofNullable(user);

    // 如果容器的对象存在，则对其执行调用mapping函数得到返回值。然后创建包含mapping返回值的Optional，否则返回空Optional。
    optional.map(user1 -> user1.getName()).orElse("六脉神剑");
}

// 上面一句代码对应着最开始的老写法：

public String tradition(User user) {
    if (user != null) {
        return user.getName();
    }else{
        return "六脉神剑";
    }
}
```
#### flatMap
源码

```
// flatMap方法与map方法类似，区别在于apply函数的返回值不同。map方法的apply函数返回值是? extends U，而flatMap方法的apply函数返回值必须是Optional
public<U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
    Objects.requireNonNull(mapper);
    if (!isPresent())
        return empty();
    else {
        return Objects.requireNonNull(mapper.apply(value));
    }
}
```

## 总结
再来感受一下Optional的魅力


```
public static void main(String[] args) {
    User user = new User();
    user.setName("六脉神剑");
    System.out.println(test(user));
}

// 以前的代码1.0
public static String test2(User user) {
    if (user != null) {
        String name = user.getName();
        if (name != null) {
            return name.toUpperCase();
        } else {
            return null;
        }
    } else {
        return null;
    }
}

// 以前的代码2.0
public static String test3(User user) {
    if (user != null && user.getName() != null) {
        return user.getName().toUpperCase();
    } else {
        return null;
    }
}

// 现在的代码
public static String test(User user) {
    return Optional.ofNullable(user)
            .map(user1 -> user1.getName())
            .map(s -> s.toUpperCase()).orElse(null);
}
```

filter，map或flatMap一个函数，函数的参数拿到的值一定不是null。所以我们通过filter，map 和 flatMap之类的函数可以将其安全的进行变换，最后通过orElse系列，get，isPresent 和 ifPresent将其中的值提取出来。

## 结尾
好了，今天的optional就讲那么多了。大家发现没有 泛型 和函数式接口一定要熟悉，如果不熟悉来学这个确实有点难度哈，明天把Stream 这个大头讲完 1.8算是讲完了，其实不难，只要理解了，你自然会用上哈。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
