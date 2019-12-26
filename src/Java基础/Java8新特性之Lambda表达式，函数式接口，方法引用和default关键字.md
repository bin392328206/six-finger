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
- Optionl类
- default关键字

这个四个的主要作用 简化代码编写，提高性能等等，但是也会给维护带来麻烦，因为不懂的人去看，真心累，但是写起来是真的香，今天打算讲标题上的。
考虑大家可能对 内部类 和 泛型不熟悉，有需要的可以看我这2篇文章  
[🔥你不知道的Java内部类](https://juejin.im/post/5df0a84fe51d4557f544f7ac)  
[🔥你必须知道的Java泛型](https://juejin.im/post/5df1b667f265da3398562739)  


文本力求简单讲清每个知识点，希望大家看完能有所收获

## Lambda表达式简介

Lambda 表达式，也可称为闭包，允许把函数作为一个参数，使代码更简洁。

那么什么是函数式编程呢？ 
> 首先，什么是函数式编程，引用廖雪峰先生的教程里面的解释就是说：函数式编程就是一种抽象程度很高的编程范式，纯粹的函数式编程语言编写的函数没有变量，因此，任意一个函数，只要输入是确定的，输出就是确定的，这种纯函数我们称之为没有副作用。而允许使用变量的程序设计语言，由于函数内部的变量状态不确定，同样的输入，可能得到不同的输出，因此，这种函数是有副作用的。函数式编程的一个特点就是，允许把函数本身作为参数传入另一个函数，还允许返回一个函数！

## 基本语法

```
// 1. 不需要参数,返回值为 5  
() -> 5  
  
// 2. 接收一个参数(数字类型),返回其2倍的值  
x -> 2 * x  
  
// 3. 接受2个参数(数字),并返回他们的差值  
(x, y) -> x – y  
  
// 4. 接收2个int型整数,返回他们的和  
(int x, int y) -> x + y  
  
// 5. 接受一个 string 对象,并在控制台打印,不返回任何值(看起来像是返回void)  
(String s) -> System.out.print(s)
```
**这个几个基本语法 和下面的4种函数式接口是对应的。谢谢读者的提醒。本来还没把这块加上去，新手看起来有点懵逼。**

## 基础列子
创建线程

![](https://user-gold-cdn.xitu.io/2019/12/13/16efe79cac10ef69?w=768&h=353&f=png&s=48516)

再来看看遍历Map集合：

![](https://user-gold-cdn.xitu.io/2019/12/13/16efe79fb26cb5cf?w=797&h=344&f=png&s=44914)
引入lambda表达式的一个最直观的作用就是大大的简化了代码的开发，像其他一些编程语言Scala，Python等都是支持函数式的写法的。当然，不是所有的接口都可以通过这种方法来调用，只有函数式接口才行，jdk1.8里面定义了好多个函数式接口，我们也可以自己定义一个来调用，下面说一下什么是函数式接口。

## 函数式接口

- 只包含一个抽象方法的接口，称为函数式接口。
- 可以通过 Lambda 表达式来创建该接口的对象。（若 Lambda 表达式抛出一个受检异常，那么该异常需要在目标接口的抽象方
法上进行声明）。
- 可以在任意函数式接口上使用 @FunctionalInterface 注解，这样做可以检查它是否是一个函数式接口，同时 javadoc 也会包 含一条声明，说明这个接口是一个函数式接口。
- 之所以Lambda必须和函数式接口配合是因为，接口如果多个函数，则Lambda表达式无法确定实现的是哪个

来个简单的例子

这是一个自定义的函数式接口：作用是传入一个参数，返回一个参数。
```
@FunctionalInterface
public interface MyNumber<T> {
    T getValue(T t);
}
```
这是用法

```

    public static void main(String[] args) {

        //Lambda的写法
        System.out.println(toUpperString(str -> str.toUpperCase(), "abc")); //ABC
        //匿名内部类的写法
        System.out.println(toUpperString(new MyNumber<String>() {
            @Override
            public String getValue(String s) {
                return s;
            }
        }, "abc"));

    }

    public static String toUpperString(MyNumber<String> mn, String str) {
        return mn.getValue(str);
    }
```
作为参数传递 Lambda 表达式：为了将 Lambda 表达式作为参数传递，接
收Lambda 表达式的参数类型必须是与该 Lambda 表达式兼容的函数式接口
的类型。

## Java 内置四大核心函数式接口


|函数式接口	|参数类型|	返回类型|用途|
| ----|----|----|----|
|Consumer<T> 消费型接口  	|T	|void|	对类型为T的对象应用操 作，包含方法： void accept(T t)
Supplier<T> 供给型接口	|无	|T	|返回类型为T的对象，包 含方法：T get();
Function<T, R> 函数型接口	|T	|R	|对类型为T的对象应用操 作，并返回结果。结果 是R类型的对象。包含方 法：R apply(T t);
Predicate<T> 断言型接口	|T	|boolean|	确定类型为T的对象是否 满足某约束，并返回 boolean 值。包含方法 boolean test(T t);

使用场景
>之前MyNumber这种接口配合Lambda使用，可以发现必须先声明接口，很麻烦，而内置的几个接口就是解决这种问题的;而这些内置的接口也存在大量的内部实现，或者编程者自己定义的类，只要符合对应的参数类型和返回值类型的，都可以使用。例如：定义MyClass只要符合参数T返回R，则都可以使用Function<T, R> 函数型接口对应形式，包含下面的构造器引用，方法引用等等形式


如下：
```
    public static void main(String[] args) {

        //Lambda的写法
        System.out.println(toUpperString1(str -> str.toUpperCase(), "abc")); //ABC
        //匿名内部类的写法
        System.out.println(toUpperString1(new MyNumber<String>() {
            @Override
            public String getValue(String s) {
                return s;
            }
        }, "abc"));

        //使用内置的函数式接口的lambda写法
        System.out.println(toUpperString(str->str.toUpperCase(),"abc"));
    }


    //内置的函数接口
    public static String toUpperString(Function<String,String> mn, String str) {
        return mn.apply(str);
    }
    //定义的函数接口
    public static String toUpperString1(MyNumber<String> mn, String str) {
        return mn.getValue(str);
    }
```

## 方法引用与构造器引用
### 方法引用
当要传递给Lambda体的操作，已经有实现的方法了，可以使用方法引用！
（实现抽象方法的参数列表，必须与方法引用方法的参数列表保持一致！）
方法引用：使用操作符 “::” 将方法名和对象或类的名字分隔开来。 如下三种主要使用情况：
- 对象::实例方法
- 类::静态方法
- 类::实例方法

例子

```
        //例如：此时Consumer参数类型和返回值和println方法一致
        //对象::实例方法名
        //为什么这样用，因为 ConSumer这个函数接口就是传入参数，返回为void ,并且printl 方法就是这样的
        /**我们看下面的源码 和那个函数接口一样
         *     public void println(String x) {
         *         synchronized (this) {
         *             print(x);
         *             newLine();
         *         }
         *     }
         */
        
        PrintStream printStream=System.out;
        Consumer<String> con= printStream::println;
        con.accept("haha");
        
        /类::静态方法名
    Comparator<Integer> com=Integer::compare;
    Comparator<Integer> com1=(x,y)->Integer.compare(x,y);
```
### 构造器引用
格式： ClassName::new

与函数式接口相结合，自动与函数式接口中方法兼容。 可以把构造器引用赋值给定义的方法，与构造器参数 列表要与接口中抽象方法的参数列表一致

```
 Function<Integer,MyClass> fun= n->new MyClass(1);
        Function<Integer,MyClass> fun1=MyClass::new ;
        MyClass apply = fun1.apply(1);
```
如果存在多个构造器，会自动匹配对应参数或者无参的构造器,取决于apply传递的参数。

## default关键字
　在java里面，我们通常都是认为接口里面是只能有抽象方法，不能有任何方法的实现的，那么在jdk1.8里面打破了这个规定，引入了新的关键字default，通过使用default修饰方法，可以让我们在接口里面定义具体的方法实现，如下
　
```
public interface NewCharacter {
    
    public void test1();
    
    public default void test2(){
        System.out.println("我是新特性1");
    }

}
```

那这么定义一个方法的作用是什么呢？为什么不在接口的实现类里面再去实现方法呢？

其实这么定义一个方法的主要意义是定义一个默认方法，也就是说这个接口的实现类实现了这个接口之后，不用管这个default修饰的方法，也可以直接调用，如下。


```
public class NewCharacterImpl implements NewCharacter{

    @Override
    public void test1() {
        
    }
    
    public static void main(String[] args) {
        NewCharacter nca = new NewCharacterImpl();
        nca.test2();
    }

}
```
所以说这个default方法是所有的实现类都不需要去实现的就可以直接调用，那么比如说jdk的集合List里面增加了一个sort方法，那么如果定义为一个抽象方法，其所有的实现类如arrayList,LinkedList等都需要对其添加实现，那么现在用default定义一个默认的方法之后，其实现类可以直接使用这个方法了，这样不管是开发还是维护项目，都会大大简化代码量。

## 结尾
好了，今天就讲那么多了。谢谢大家的支持
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
