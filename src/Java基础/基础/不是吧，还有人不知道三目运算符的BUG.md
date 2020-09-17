# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 每篇一句
这世上每个人都背负着枷锁，有的人是别人给的，有的是自己给的。

## 絮叨 

三目运算符一直是众多开发者信手拈来的一种写法，它简化了if-else的臃肿的写法，而是用一行代码替代，就感觉无形之中秀了一把。
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3f525fc3e8f4499db1d54f6f8e8c6ebc~tplv-k3u1fbpfcp-zoom-1.image)
殊不知，这么帅气的代码也暗藏着一个BUG。

## 缘由
头天晚上发布了一个功能，本以为是波澜不惊的一个需求，结果第二天kibana打出了成吨的NPE日志。这些NPE日志大多都不约而同都指向了我写的一行代码，我立马推了下我的眼镜，开始排查起来了。
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/86c7f512acab4dc5830120053abad586~tplv-k3u1fbpfcp-zoom-1.image)

问题代码
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b8f7ff9e92414fd190dbb78efc34be35~tplv-k3u1fbpfcp-zoom-1.image)

Kibana的堆栈日志定位在第899行。

resultMap.put("unAuditPurchaseOrder", switchConf == null ? 0 : switchConf.getUnAuditPurchaseOrder());

1.检查了resultMap，它在上面有实例化，不可能为空。
2.检查switchConf，但是在这里有判空，也不会报错。 那是怎么回事？？？？
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/adb8264d317749cabf48db436ce6c4e5~tplv-k3u1fbpfcp-zoom-1.image)

### 继续排查

既然肉眼看不出，那么只能找一台测试机，用一下Arthas看一下具体的情况。（线上慎用，因为可能会造成卡顿）

```
trace com.raycloud.dmj.tj.services.customer.CustomerButtonService getPurchaseConfig -n 5 '1==1' --skipJDKMethod false
```
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7e3307059a07444a85f984534b29c50d~tplv-k3u1fbpfcp-zoom-1.image)


果然，这里就发现了端倪。 这里竟然执行了intValue()！也就是说如果switchConf.getUnAuditPurchaseOrder()这个是null，那么就很明显发生了NPE。

### 看一下字节码

为了显现效果，我换一个简单的程序

```
public class Test {

    public static void main(String[] args) {

        Integer i = null;
        System.out.println(1 != 1 ? 0 : i);

    }
}

```

定位到class文件目录，执行

````
javap -c -l Test

````

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5cf6e372500340089b7c9702633f958a~tplv-k3u1fbpfcp-zoom-1.image)

然后我又改了一下程序
```
public class Test {

    public static void main(String[] args) {

        Integer i = null;
        System.out.println(1 == 1 ? 0 : i);

    }
}

```

这次运行并不会报错，看一下它的JVM指令:

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c7760bffc52f4d0f9d9ae2a90c3adff9~tplv-k3u1fbpfcp-zoom-1.image)


因为三目运算符的结果是前者的逻辑，即返回一个常量0。

### 解释

由上面的实验可以发现，JVM在解释三目运算符的时候，会对两个逻辑语句进行数据类型校验，按照前者的数据类型为准。实验中，数据类型是基本数据类型，所以，如果逻辑走到了后者，那么就会进行自动的拆箱。这个隐式的操作就是造成这个BUG的原因。

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/74ac8706b0ca4f88bfd064297ca7e035~tplv-k3u1fbpfcp-zoom-1.image)


### 解决

既然知道原因了，那么只要统一数据类型就行：

```
public class Test {

    public static void main(String[] args) {

        Integer i = null;
        System.out.println(1 != 1 ? new Integer(0) : i);

    }
}

```

然后按照惯例，我们还是看一下他的JVM指令:

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/bba8cbb7cf3946a986962a4973b387ef~tplv-k3u1fbpfcp-zoom-1.image)


## 拾遗

后来在无意之中发现，原来这个例子在《阿里巴巴开发手册》当中也有被记录

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/569d4885fdc9421b97d4600baada9b66~tplv-k3u1fbpfcp-zoom-1.image)

其实说的也是一回事情！


## 结尾

文章出自掘金 [
浮沉_fuchen]()

![](//p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8dcd57c7abce409b92e750d6ec19c3da~tplv-k3u1fbpfcp-zoom-1.image)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
