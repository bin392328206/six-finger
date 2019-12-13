# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
> List集合讲完了，接下来要讲Map,因为Set底层是Map。但是讲Map之前得好好讲讲 equals和hashCode  
[🔥史上最全的Java容器集合之入门](https://juejin.im/post/5de87a92e51d4557ec02f39d)    
[🔥史上最全的Java容器集合之基础数据结构（手撕链表）](https://juejin.im/post/5de8cdb5f265da33c34e2719)   
[🔥史上最全的Java容器集合之ArrayList(源码解读)](https://juejin.im/post/5de9f222f265da33b12e9600)  
[🔥史上最全的Java容器集合之Vector和LinkedList(源码解读)](https://juejin.im/post/5deb0b26e51d4557e87fc398)   
为什么要讲这2个方法呢？因为Listd存的数据可以找的到，但是Map内部存储元素的方式的都是以键值对相关的，而元素如何存放，便与 equals 和 hashCode 这两个方法密切相关，所以分析Map之前，我们来把equals和hashCode 好好的探究一下，也为了以后更好的理解Map

## equals()方法
Object原生的equals方法：

```
   public boolean equals(Object obj) {
        return (this == obj);
    }
```
从代码中可以看出，原生的equals方法使用的是“==”来比较的。学过Java的人都应该知道，“==”比较的是内存地址，所以原生的equals方法只有在自己与自己比较的时候才会返回true，是严格的判断一个对象是否相等的方法。所以，如果类具有自己特有的“逻辑相等”概念（不同于对象相等的概念），而且超类没有覆盖equals()方法以实现期望的行为，这时我们就需要覆盖equals()方法了（通俗来说，就是在业务系统中，有时候需要的并不是一种严格意义上的相等，而是业务上的对象相等。比如：如果两个对象中的id相等，那么就认为这两个对象是相等的），这时候我们就需要对equals方法进行重写，定义新的比较方式。


覆盖equals方法时，需要遵守的通用约定覆盖equals方法时，需要遵守的通用约定
 - 自省性：对于非null的x，存在：x.equals(x)返回true
- 对称性：对于非null的x和y，存在：x.equals(y)==y.equals(x)
- 传递性：对于非null的x、y、z，存在：当x.equals(y)返回true，y.equals(z)返回true，则x.equals(z)一定为true
- 一致性：对于非null的x和y，多次调用x.equals(y)所得的结果是不变的
- 非空性：对于非null的x，存在x.equals(null)返回false


String 里面重写的equals方法
```
 public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof String) {
            String anotherString = (String)anObject;
            int n = value.length;
            if (n == anotherString.value.length) {
                char v1[] = value;
                char v2[] = anotherString.value;
                int i = 0;
                while (n-- != 0) {
                    if (v1[i] != v2[i])
                        return false;
                    i++;
                }
                return true;
            }
        }
        return false;
    }
```
 很明显，这是进行的内容比较，而已经不再是地址的比较。依次类推Math、Integer、Double等这些类都是重写了equals()方法的，从而进行的是内容的比较。当然，基本类型是进行值的比较。
## hashCode()方法

![](https://user-gold-cdn.xitu.io/2019/12/9/16ee85cbdb8b152c?w=1508&h=510&f=png&s=113134)


String中的HashCode方法

![](https://user-gold-cdn.xitu.io/2019/12/9/16ee87f87834e285?w=1613&h=866&f=png&s=159862)
他就是用31*上一个字符的数值+当前字符的大小,然后依次遍历得到String类型的hash值

在Object中 hashCode是一个Native方法。hashCode一般用于计算对象的hash值，它在类重写equals的时候一起重写，重写它的目的是为了保证equals相同的两个对象的hashCode结果一致，为什么要保证这一点呢，那就归结到java中的那几个基于Hash实现的集合上了，比如HashMap、HashSet等，这些集合需要用到对象的hash值来参与计算定位。
使用hashCode的目的就是为了散列元素，最终元素能否散列均匀和hashCode的实现息息相关，即为hash函数。

### hashCode的作用
想要弄明白hashCode的作用，就要回到我们所说的容器中来了，Java中的集合（Collection）有两类，一类是List，再有一类是Set。前者集合内的元素是有序的，元素可以重复；后者元素无序，但元素不可重复。这里就引出一个问题：要想保证元素不重复，可两个元素是否重复应该依据什么来判断呢？

 这就是Object.equals方法了。但是，如果每增加一个元素就检查一次，那么当元素很多时，后添加到集合中的元素比较的次数就非常多了。也就是说，如果集合中现在已经有1000个元素，那么第1001个元素加入集合时，它就要调用1000次equals方法。这显然会大大降低效率。
 
 于是，Java采用了哈希表的原理。哈希（Hash）实际上是个人名，由于他提出一哈希算法的概念，所以就以他的名字命名了。哈希算法也称为散列算法，是将数据依特定算法直接指定到一个地址上，初学者可以简单理解，hashCode方法实际上返回的就是对象存储的物理地址（实际可能并不是）。  
 
  这样一来，当集合要添加新的元素时，先调用这个元素的hashCode方法，就一下子能定位到它应该放置的物理位置上。如果这个位置上没有元素，它就可以直接存储在这个位置上，不用再进行任何比较了；如果这个位置上已经有元素了，就调用它的equals方法与新元素进行比较，相同的话就不存了，不相同就散列其它的地址。所以这里存在一个冲突解决的问题。这样一来实际调用equals方法的次数就大大降低了，几乎只需要一两次。
  
  
 ## 结论
 eqauls方法和hashCode方法是这样规定的
 - 如果两个对象相同，那么它们的hashCode值一定要相同；
 - 如果两个对象的hashCode相同，它们并不一定相同（这里说的对象相同指的是用eqauls方法比较）。 
 - equals()相等的两个对象，hashcode()一定相等；equals()不相等的两个对象，却并不能证明他们的hashcode()不相等。
 - 为什么要重写equals呢？因为在java的集合框架中，是通过equals来判断两个对象是否相等的
 ### 版本说明
- 这里的源码是JDK8版本，不同版本可能会有所差异，但是基本原理都是一样的。

## 结尾
> 好了 hashCode 和equals讲完了，我们就可以知道如何来保证我们存的数据不重复了，接下开大头戏HashMap开始了，我想有了这个基础，会好理解很多。

> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
