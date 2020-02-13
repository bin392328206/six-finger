# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨
昨天刚好有遇到一个枚举的小问题，然后发现自己并不是那么熟悉它，然后在开发中，枚举用的特别多，所以有了今天的文章。


## 什么是枚举
Java中的枚举是一种类型，顾名思义：就是一个一个列举出来。所以它一般都是表示一个有限的集合类型，它是一种类型，在维基百科中给出的定义是：
> 在数学和计算机科学理论中，一个集的枚举是列出某些有穷序列集的所有成员的程序，或者是一种特定类型对象的计数。这两种类型经常（但不总是）重叠.。枚举是一个被命名的整型常数的集合，枚举在日常生活中很常见，例如表示星期的SUNDAY、MONDAY、TUESDAY、WEDNESDAY、THURSDAY、FRIDAY、SATURDAY就是一个枚举。

## 出现的原因

在Java5之前，其实是没有enum的，所以先来看一下Java5之前对于枚举的使用场景该怎么解决？这里我看到了一片关于在Java 1.4之前的枚举的设计方案：

```
public class Season {
    public static final int SPRING = 1;
    public static final int SUMMER = 2;
    public static final int AUTUMN = 3;
    public static final int WINTER = 4;
}
```

这种方法称作int枚举模式。可这种模式有什么问题呢?通常我们写出来的代码都会考虑它的安全性、易用性和可读性。 首先我们来考虑一下它的类型安全性。当然这种模式不是类型安全的。比如说我们设计一个函数，要求传入春夏秋冬的某个值。但是使用int类型，我们无法保证传入的值为合法。代码如下所示：

```
private String getChineseSeason(int season){
        StringBuffer result = new StringBuffer();
        switch(season){
            case Season.SPRING :
                result.append("春天");
                break;
            case Season.SUMMER :
                result.append("夏天");
                break;
            case Season.AUTUMN :
                result.append("秋天");
                break;
            case Season.WINTER :
                result.append("冬天");
                break;
            default :
                result.append("地球没有的季节");
                break;
        }
        return result.toString();
    }
```
因为我们传值的时候，可能会传其他的类型，就可能导致走default，所以这个并不能在源头上解决类型安全问题。


接下来我们来考虑一下这种模式的可读性。使用枚举的大多数场合，我都需要方便得到枚举类型的字符串表达式。如果将int枚举常量打印出来，我们所见到的就是一组数字，这是没什么太大的用处。我们可能会想到使用String常量代替int常量。虽然它为这些常量提供了可打印的字符串，但是它会导致性能问题，因为它依赖于字符串的比较操作，所以这种模式也是我们不期望的。 从类型安全性和程序可读性两方面考虑，int和String枚举模式的缺点就显露出来了。幸运的是，从Java1.5发行版本开始，就提出了另一种可以替代的解决方案，可以避免int和String枚举模式的缺点，并提供了许多额外的好处。那就是枚举类型（enum type）。


## 枚举定义

枚举类型（enum type）是指由一组固定的常量组成合法的类型。Java中由关键字enum来定义一个枚举类型。下面就是java枚举类型的定义。


```
public enum Season {
    SPRING, SUMMER, AUTUMN, WINER;
}
```
Java定义枚举类型的语句很简约。它有以下特点：
- 使用关键字enum
- 类型名称，比如这里的Season
- 一串允许的值，比如上面定义的春夏秋冬四季 
- 枚举可以单独定义在一个文件中，也可以嵌在其它Java类中
- 枚举可以实现一个或多个接口（Interface）
- 可以定义新的变量和方法


## 重写上面的枚举方式
下面是一个很规范的枚举类型

```
public enum Season {
    SPRING(1), SUMMER(2), AUTUMN(3), WINTER(4);

    private int code;
    private Season(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}
public class UseSeason {
    /**
     * 将英文的季节转换成中文季节
     * @param season
     * @return
     */
    public String getChineseSeason(Season season){
        StringBuffer result = new StringBuffer();
        switch(season){
            case SPRING :
                result.append("[中文：春天，枚举常量:" + season.name() + "，数据:" + season.getCode() + "]");
                break;
            case AUTUMN :
                result.append("[中文：秋天，枚举常量:" + season.name() + "，数据:" + season.getCode() + "]");
                break;
            case SUMMER : 
                result.append("[中文：夏天，枚举常量:" + season.name() + "，数据:" + season.getCode() + "]");
                break;
            case WINTER :
                result.append("[中文：冬天，枚举常量:" + season.name() + "，数据:" + season.getCode() + "]");
                break;
            default :
                result.append("地球没有的季节 " + season.name());
                break;
        }
        return result.toString();
    }

    public void doSomething(){
        for(Season s : Season.values()){
            System.out.println(getChineseSeason(s));//这是正常的场景
        }
        //System.out.println(getChineseSeason(5));
        //此处已经是编译不通过了，这就保证了类型安全
    }

    public static void main(String[] arg){
        UseSeason useSeason = new UseSeason();
        useSeason.doSomething();
    }
}
```
## Enum类的常用方法
|方法名称|	描述
|------|------|
values()|	以数组形式返回枚举类型的所有成员
valueOf()|	将普通字符串转换为枚举实例
compareTo()|	比较两个枚举成员在定义时的顺序
ordinal()|	获取枚举成员的索引位置

### values() 方法
通过调用枚举类型实例的 values() 方法可以将枚举的所有成员以数组形式返回，也可以通过该方法获取枚举类型的成员。

下面的示例创建一个包含 3 个成员的枚举类型 Signal，然后调用 values() 方法输出这些成员。

```

public enum Signal {
    
        //定义一个枚举类型
        GREEN,YELLOW,RED;

    public static void main(String[] args)
    {
        for(int i=0;i<Signal.values().length;i++)
        {
            System.out.println("枚举成员："+Signal.values()[i]);
        }
    }

}

```
结果

```
//枚举成员：GREEN
//枚举成员：YELLOW
//枚举成员：RED
```


### valueOf方法

通过字符串获取单个枚举对象
```

public enum Signal {

        //定义一个枚举类型
        GREEN,YELLOW,RED;

        public static void main(String[] args)
        {
            Signal green = Signal.valueOf("GREEN");
            System.out.println(green);
        }

}
```
结果

```
//GREEN

```

### ordinal() 方法
通过调用枚举类型实例的 ordinal() 方法可以获取一个成员在枚举中的索引位置。下面的示例创建一个包含 3 个成员的枚举类型 Signal，然后调用 ordinal() 方法输出成员及对应索引位置。


```
public class TestEnum1
{
    enum Signal
    {
        //定义一个枚举类型
        GREEN,YELLOW,RED;
    }
    public static void main(String[] args)
    {
        for(int i=0;i<Signal.values().length;i++)
        {
            System.out.println("索引"+Signal.values()[i].ordinal()+"，值："+Signal.values()[i]);
        }
    }
}
```

结果

```
//索引0，值：GREEN
//索引1，值：YELLOW
//索引2，值：RED
```

## 枚举实现单例
> 使用枚举实现单例的方法虽然还没有广泛采用，但是单元素的枚举类型已经成为实现Singleton的最佳方法。
### 单例的饿汉式

```
package com.atguigu.ct.producer.controller;
//final 不允许被继承
public final class HungerSingleton {

private static HungerSingleton instance=new HungerSingleton();
    //私有构造函数不允许外部new
    private HungerSingleton(){
    }
        //提供一个方法给外部调用
    public static HungerSingleton getInstance(){
        return instance;
    }

}
```

### 懒汉式的单例

```
package com.atguigu.ct.producer.controller;
//final 不能被继承
public final class DoubleCheckedSingleton {
    //定义实例但是不直接初始化,volatile禁止重排序操作，避免空指针异常
    private static DoubleCheckedSingleton instance=new DoubleCheckedSingleton();

    //私有构造函数不允许外部new
    private DoubleCheckedSingleton(){
    }

        //对外提供的方法用来获取单例对象
    public static DoubleCheckedSingleton getInstance(){
        if(null==instance){
            synchronized (DoubleCheckedSingleton.class){
                if (null==instance) {
                    instance = new DoubleCheckedSingleton();
                }
            }
        }
        return instance;
    }
    
}

```

### 枚举的单例


```
package com.atguigu.ct.producer.controller;

public enum EnumSingleton {
    //定义一个单例对象
    INSTANCE;
    //获取单例对象的方法
    public  static EnumSingleton getInstance(){
        return INSTANCE;
    }


}
```

看上面三个方式，光看代码就知道单例的模式是最简单的，因为单例本身就是私有构造的，所以建议大家以后用枚举来实现单例

## 面试问题
### 枚举允许继承类吗？
> 枚举不允许继承类。Jvm在生成枚举时已经继承了Enum类，由于Java语言是单继承，不支持再继承额外的类（唯一的继承名额被Jvm用了）。

### 枚举可以用等号比较吗？
枚举可以用等号比较。Jvm会为每个枚举实例对应生成一个类对象，这个类对象是用public static final修饰的，在static代码块中初始化，是一个单例。

### 枚举可以被人家继承吗
不可以继承枚举。因为Jvm在生成枚举类时，将它声明为final。



## 结尾
枚举其实也就那么多了，定义常量的话用枚举确实是优雅很多，大家在项目中记得多使用哈
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！

