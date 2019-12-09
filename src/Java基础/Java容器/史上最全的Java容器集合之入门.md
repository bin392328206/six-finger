# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
> 不知不觉，自己也写了十来篇文章，在这里谢谢大家的关注，我会继续加油。 接下来讲Java的容器，至于能写多少篇，写多详细，看看吧，争取深一点。   


>零基础入门不需要阅读源码，面试前一定要回顾和阅读源码（这是面试必考的知识点）！



## 集合的由来
通常，我们的Java程序需要根据程序运行时才知道创建了多少个对象。但若非程序运行，程序开发阶段，我们根本不知道到底需要多少个数量的对象，甚至不知道它的准确类型。为了满足这些常规的编程需要，我们要求能在任何时候，任何地点创建任意数量的对象，而这些对象用什么来容纳呢？我们首先想到了数组，但是！数组只能存放同一类型的数据，而且其长度是固定的，那怎么办了？集合便应运而生了。

## 集合和数组的区别：


![](https://user-gold-cdn.xitu.io/2019/12/5/16ed438ed7ccc6f4?w=1039&h=353&f=png&s=47006)

## 集合图
![](https://user-gold-cdn.xitu.io/2019/12/5/16ed483e9411ec98?w=1218&h=597&f=png&s=25011)
其实最主要还是讲我圈的这几个

Java.util下面的包 让我们从上往下看吧，这篇就讲几个上层接口吧 具体的容器到时候一个个讲
![](https://user-gold-cdn.xitu.io/2019/12/5/16ed4b80c294d295?w=409&h=751&f=png&s=47296)


![](https://user-gold-cdn.xitu.io/2019/12/5/16ed4f4ca96fdbc3?w=1094&h=794&f=png&s=74285)

## Iterator迭代器

我们可以发现一个特点，上述所有的集合类，除了map系列的集合，即左边的集合都实现了Iterator接口

它是Java集合的顶层接口（不包括map系列的集合，Map接口是map系列集合的顶层接口）

　　Object next()：返回迭代器刚越过的元素的引用，返回值是Object，需要强制转换成自己需要的类型。

　　boolean hasNext()：判断容器内是否还有可供访问的元素。

　　void remove()：删除迭代器刚越过的元素。

所以除了map系列的集合，我么都能通过迭代器来对集合中的元素进行遍历。

注意：我们可以在源码中追溯到集合的顶层接口，比如Collection接口，可以看到它继承的是类Iterable

![](https://user-gold-cdn.xitu.io/2019/12/5/16ed51b7e37a4ec8?w=742&h=218&f=png&s=20996)
然后Iterable中有Iterator

![](https://user-gold-cdn.xitu.io/2019/12/5/16ed51c65ea5b47e?w=907&h=332&f=png&s=40674)

我们来具体聊聊 Iterator<T>

![](https://user-gold-cdn.xitu.io/2019/12/5/16ed51e5f049cc8b?w=1425&h=285&f=png&s=62585)

总共4个方法
- 判断下个迭代器是否还有下一个元素
- 返回下一个元素的值，并且把自身offset移动下一位
- 第三个方法 这个可以删除用这个迭代器集合中的元素(注意如果删除之后还是前面获得的迭代器，你会发现原来的迭代器还是没变，得重新获得删除元素之后的迭代器)
- 1.8的新方法 可以直接遍历迭代器剩下的元素，如果从最开始的话就是遍历所有的迭代器（1.8的函数式编程，写的蛮爽，后面博客会补）

**所以我想说的是所有的集合都有迭代器可以用来遍历哈 它是所有集合的最上级**

## ListIterator
为什么要讲它呢，本来没打算讲，但是想了一下，要写就写全点吧

![](https://user-gold-cdn.xitu.io/2019/12/5/16ed544de33afb76?w=1444&h=446&f=png&s=93559)
ListIterator 是 Iterator 的子接口，ListIterator 不仅可以向后迭代，也可以向前迭代。相比 Iterator，

- 它增加了以下这些方法：
- boolean hasPrevious();
- E previous();
- int nextIndex();
- int previousIndex();
- void set(E e);
- void add(E e);

其实就是 增加可以向前一个下标的操作。大家可以写个测试方法自己试试就知道了 还可以对迭代出来的元素进行替换set()方法


## Collection接口介绍
Collection的作用就是规定了一个集合有哪些基本的操作。

![](https://user-gold-cdn.xitu.io/2019/12/5/16ed55b902f0ee67?w=426&h=540&f=png&s=47528)
这里主要是插入数据，清空数据，是否包含，是否相等，集合里的数据个数和转化成熟组这几种操作。

比如：

　　int size()　获取元素个数

　　boolean isEmpty()　是否个数为零

　　boolean contains(Object element)　是否包含指定元素

　　boolean add(E element)　添加元素，成功时返回true

　　boolean remove(Object element)　删除元素，成功时返回true

　　Iterator<E> iterator()　获取迭代器
　　
　　Stream 1.8的流 (后面也比较常用)

 

还有些操作整个集合的方法，比如：

　　boolean containsAll(Collection<?> c) 　是否包含指定集合 c 的全部元素

　　boolean addAll(Collection<? extends E> c)　添加集合 c 中所有的元素到本集合中，如果集合有改变就返回 true

　　boolean removeAll(Collection<?> c)　删除本集合中和 c 集合中一致的元素，如果集合有改变就返回 true

　　boolean retainAll(Collection<?> c) 　保留本集合中 c 集合中两者共有的，如果集合有改变就返回 true

　　void clear() 　删除所有元素

还有对数组操作的方法：
　　Object[] toArray()　返回一个包含集合中所有元素的数组

　　<T> T[] toArray(T[] a)　返回一个包含集合中所有元素的数组，运行时根据集合元素的类型指定数组的类型
　　
![](https://user-gold-cdn.xitu.io/2019/12/5/16ed5613f6aa6328?w=955&h=667&f=png&s=115732)

## 结尾
> 容器的集合入门介绍算是讲完了 后面我会针对他们的具体实现深入讲讲，大家一起学习，一起进步

> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
