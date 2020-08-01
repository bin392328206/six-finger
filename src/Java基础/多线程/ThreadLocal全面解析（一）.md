# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨  
今天无意中看到了一篇关于ThreadLocal的文章，然后就去学习了一下，但是那篇文章看完之后，小六六又觉得有点不完善的地方，所以就继续找资料学习，终于把ThreadLocal大部分的知识有了点基本的认知吧，故写文章记录一下。

## ThreadLocal基础之Java的引用



在 JDK1.2 之前，Java中的定义很传统：如果 reference 类型的数据中存储的数值代表的是另外一块内存的起始地址，就称为这块内存代表着一个引用。
Java 中的垃圾回收机制在判断是否回收某个对象的时候，都需要依据“引用”这个概念。
在不同垃圾回收算法中，对引用的判断方式有所不同：

- 引用计数法：为每个对象添加一个引用计数器，每当有一个引用指向它时，计数器就加1，当引用失效时，计数器就减1，当计数器为0时，则认为该对象可以被回收（目前在Java中已经弃用这种方式了）。
- 可达性分析算法：从一个被称为 GC Roots 的对象开始向下搜索，如果一个对象到GC Roots没有任何引用链相连时，则说明此对象不可用。
- JDK1.2 之前，一个对象只有“已被引用”和"未被引用"两种状态，这将无法描述某些特殊情况下的对象，比如，当内存充足时需要保留，而内存紧张时才需要被抛弃的一类对象。

### 四种引用类型
所以在 JDK.1.2 之后，Java 对引用的概念进行了扩充，将引用分为了：强引用（Strong Reference）、软引用（Soft Reference）、弱引用（Weak Reference）、虚引用（Phantom Reference）4 种，这 4 种引用的强度依次减弱。

### 强引用

Java中默认声明的就是强引用，比如：

```
Object obj = new Object(); //只要obj还指向Object对象，Object对象就不会被回收
obj = null;  //手动置null
```

只要强引用存在，垃圾回收器将永远不会回收被引用的对象，哪怕内存不足时，JVM也会直接抛出OutOfMemoryError，不会去回收。如果想中断强引用与对象之间的联系，可以显示的将强引用赋值为null，这样一来，JVM就可以适时的回收对象了


### 软引用

软引用是一种比强引用生命周期稍弱的一种引用类型。在JVM内存充足的情况下，软引用并不会被垃圾回收器回收，只有在JVM内存不足的情况下，才会被垃圾回收器回收。所以软引用的这种特性，一般用来实现一些内存敏感的缓存，只要内存空间足够，对象就会保持不被回收掉，比如网页缓存、图片缓存等。


```
SoftReference<String> softReference = new SoftReference<String>(new String("小六六"));
System.out.println(softReference.get());
```


### 弱引用

**弱引用的引用强度比软引用要更弱一些，无论内存是否足够，只要 JVM 开始进行垃圾回收，那些被弱引用关联的对象都会被回收。在 JDK1.2 之后，用 java.lang.ref.WeakReference 来表示弱引用。** 这个对ThreadLocal有用，大家先记住



```
WeakReference<String> weakReference = new WeakReference<String>(new String("小六六"));
System.gc();
if(weakReference.get() == null) {
    System.out.println("weakReference已经被GC回收");
}
```
输出结果：


```
weakReference已经被GC回收
```
### 虚引用（PhantomReference）
虚引用是最弱的一种引用关系，如果一个对象仅持有虚引用，那么它就和没有任何引用一样，它随时可能会被回收，在 JDK1.2 之后，用 PhantomReference 类来表示，通过查看这个类的源码，发现它只有一个构造函数和一个 get() 方法，而且它的 get() 方法仅仅是返回一个null，也就是说将永远无法通过虚引用来获取对象，虚引用必须要和 ReferenceQueue 引用队列一起使用。


```
PhantomReference<String> phantomReference = new PhantomReference<String>(new String("小六六"), new ReferenceQueue<String>());
System.out.println(phantomReference.get());
```
运行后，发现结果总是null，引用跟没有持有差不多。

简单总结下
- 强引用	一直存活，除非GC Roots不可达	所有程序的场景，基本对象，自定义对象等。
- 软引用	内存不足时会被回收	- 一般用在对内存非常敏感的资源上，用作缓存的场景比较多，例如：网页缓存、图片缓存
- 弱引用	只能存活到下一次GC前	生命周期很短的对象，例如ThreadLocal中的Key。
- 虚引用	随时会被回收， 创建了可能很快就会被回收	业界暂无使用场景， - 可能被JVM团队内部用来跟踪JVM的垃圾回收活动


## ThreadLocal基础之Java中的值传递和地址传递

首先我们来看看代码


```
public class Test {
	 public static void main(String[] args) {
	        String str = "123";
	        System.out.println(str);
	        change(str);
	        System.out.println(str);
	    }
    public static void change(String str){
    		str = "小六六";
    }
}

```

那么你觉得会输出多少呢？至少我曾经觉得是：

```
123
小六六
```
但是，正确答案是：

```
123
123
```

这是为什么呢？我相信答错的同学大都是受到了一些”java教材“的影响–java的参数传递有两种：

- 值传递，传递值，在函数中形参发生的变化不影响实参。
- 引用传递，传递对象引用，在函数中形参发生的变化影响实参。


**然而，实际上java参数传递只有一种情况，那就是值传递。所不同的是，一般说的"引用传递"，在实际中传递的不过是引用对象的地址值 值传递传递的是真实内容的一个副本，对副本的操作不影响原内容，也就是形参怎么变化，不会影响实参对应的内容。**


在解释上述代码前，先要在补充一点知识：


```
String a = new String("小六六");
String b;
b= new String("小六六");

```

两种形式的代码所形成的的结果是完全一致的，后面一种更容易理解java中的引用与对象的具体含义。先声明一个String对象的引用，再new一个“小六六”对象，最后将这个对象赋值（等号=）给该引用。

- b：对象的引用
- “小六六”：实际对象


好了，来个例子具体解释一下值传递和地址值引用吧。

先定义一个对象：

```
 public class Person {
         private String name;
         private int age;
 
         public String getName() {
             return name;
         }
         public void setName(String name) {
             this.name = name;
        }
        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
}
```

我们写个函数测试一下：

```
 public static void PersonCrossTest(Person person){
         System.out.println("传入的person的name："+person.getName());
         person.setName("我是张小龙");
         System.out.println("方法内重新赋值后的name："+person.getName());
     }
 //测试
 public static void main(String[] args) {
         Person p=new Person();
         p.setName("我是马化腾");
        p.setAge(45);
        PersonCrossTest(p);
        System.out.println("方法执行后的name："+p.getName());
}
```

结果


```
1传入的person的name：我是马化腾
2方法内重新赋值后的name：我是张小龙
3方法执行后的name：我是张小龙
```


可以看出，person经过personCrossTest()方法的执行之后，内容发生了改变，这印证了上面所说的“引用传递”，对形参的操作，改变了实际对象的内容。

那么，到这里就结题了吗？
不是的，没那么简单，
能看得到想要的效果
是因为刚好选对了例子而已！！！

下面我们对上面的例子稍作修改，加上一行代码，


```
public static void PersonCrossTest(Person person){
        System.out.println("传入的person的name："+person.getName());
        person=new Person();//加多此行代码
        person.setName("我是张小龙");
        System.out.println("方法内重新赋值后的name："+person.getName());
    }
```


```
传入的person的name：我是马化腾
方法内重新赋值后的name：我是张小龙
方法执行后的name：我是马化腾
```

为什么这次的输出和上次的不一样了呢？
看出什么问题了吗？

按照JVM内存模型可以知道，对象和数组是存储在Java堆区的，而且堆区是共享的，因此程序执行到main（）方法中的下列代码时


```
Person p=new Person();
        p.setName("我是马化腾");
        p.setAge(45);
        PersonCrossTest(p);
```
JVM会在堆内开辟一块内存，用来存储p对象的所有内容，同时在main（）方法所在线程的栈区中创建一个引用p存储堆区中p对象的真实地址，


当执行到PersonCrossTest()方法时，因为方法内有这么一行代码：

```
person=new Person();
```


JVM需要在堆内另外开辟一块内存来存储new Person()，假如地址为“xo3333”，那此时形参person指向了这个地址，假如真的是引用传递，那么由上面讲到：引用传递中形参实参指向同一个对象，形参的操作会改变实参对象的改变。

可以推出：实参也应该指向了新创建的person对象的地址，所以在执行PersonCrossTest()结束之后，最终输出的应该是后面创建的对象内容。

然而实际上，最终的输出结果却跟我们推测的不一样，最终输出的仍然是一开始创建的对象的内容。

**由此可见：引用传递，在Java中并不存在。**


但是有人会疑问：为什么第一个例子中，在方法内修改了形参的内容，会导致原始对象的内容发生改变呢？
这是因为：无论是基本类型和是引用类型，在实参传入形参时，都是值传递，也就是说传递的都是一个副本，而不是内容本身。
可以看出，方法内的形参person和实参p并无实质关联，它只是由p处copy了一份指向对象的地址，此时：
p和person都是指向同一个对象。
因此在第一个例子中，对形参p的操作，会影响到实参对应的对象内容。而在第二个例子中，当执行到new Person()之后，JVM在堆内开辟一块空间存储新对象，并且把person改成指向新对象的地址，此时：
p依旧是指向旧的对象，person指向新对象的地址。
所以此时对person的操作，实际上是对新对象的操作，于实参p中对应的对象毫无关系。

同样总结一下

- 在Java中所有的参数传递，不管基本类型还是引用类型，都是值传递，或者说是副本传递。
只是在传递过程中：
- 如果是对基本数据类型的数据进行操作，由于原始内容和副本都是存储实际值，并且是在不同的栈区，因此形参的操作，不影响原始内容。
- 如果是对引用类型的数据进行操作，分两种情况，一种是形参和实参保持指向同一个对象地址，则形参的操作，会影响实参指向的对象的内容。一种是形参被改动指向新的对象地址（如重新赋值引用），则形参的操作，不会影响实参指向的对象的内容。


## ThreadLocal基础之this关键字


### this关键字的作用

Person
```
String name;//定义当前Person名字

//参数name是目标名字
//成员变量name是自己名字
public  void sayHello(String name) {
	System.out.println(name+"，你好。我是"+name);
}

```
Demo01Person




```
public static void main(String[] args) {
	Person person = new Person();
	//设置当前person名字
	person.name = "王健林";
	//调用sayHello方法
	person.sayHello("王思聪");
}

```
运行结果：王思聪，你好。我是王健林
总结：当我们使用this.成员变量名时，就可以访问本类当中的成员变量。
由此我们可以总结出：使用this关键字可以准确的进行属性的标记

### this关键字的原理


```
String name;//定义当前Person名字

//参数name是目标名字
//成员变量name是自己名字
public  void sayHello(String name) {
	System.out.println(this);
}

```



```
public static void main(String[] args) {
	Person person = new Person();
	//设置当前person名字
	person.name = "王健林";
	//调用sayHello方法
	person.sayHello("王思聪");
	System.out.println(person);
}

```

打印结果：demo01.Person@7852e922 demo01.Person@7852e922
从结果我们可以看出，person和方法中this的地址是一样的。
由此我们可以总结出：通过谁调用的方法，谁就是this


## 结尾

今天给大家把ThreadLocal要用到的基础知识学习学习，为了下章做准备哦。

![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！

