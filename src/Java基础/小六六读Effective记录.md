# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨 
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/056756df4d0842f8ae82f1c6d23aada6~tplv-k3u1fbpfcp-zoom-1.image)

强烈推荐有1年以上工作经验的小伙伴看，真的可以让自己编码得到实质的提高，并且是可以马上用于工作上的，看看Java作者怎么说的 "我很希望10年前我就拥有这本书" 。



## 创建和销毁对象

### 考虑使用静态工厂方法代替构造方法

小六六看到第一点的时候，随即就优化了一下自己的代码，话不多说，来看看小六六做了啥。
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0a5e82ff98384fa882bda1b2377dd11b~tplv-k3u1fbpfcp-zoom-1.image)

本来我前面有一个返回给前端的对象R，但是他每次返回的时候，都要new,这样的话，不就会产生很多不必要的垃圾，所以我看到这本书的第一章就去优化了一下代码，用静态工厂去替代，然后，嗯真香。其实我们用的很多都是静态工厂的，比如说Optional 我相信大家都有用过吧，他的构造就是这样的。我具体来总结一下静态工厂方法的好处吧
- 静态工厂方法的第一大优势：它们有名称，写代码或者说写api 最重要的一点之一，就是要做到见名知义。如果我去学习一个新的类，如果他构造方法很多，就会很混淆，如果用静态工厂方法的话，那么我们就可以很直观的去创建它了。
- 静态工厂方法的第二大优势：不必在每次调用时都创建新的对象,这个就是可以减少一点垃圾对象的创建了，小六六碰到了很多次面试都问JVM调优，但是其实JVM调优 优化的不是就是一次性对象的创建嘛，那你代码已经够好了，那么是不是算变相调优呢？不是说不需要了解JVM，事实上JVM还是得了解的。哈哈
- 静态工厂方法第三大优势：他们可以返回原返回类型的任何子类型的对象（面向对象的原则之一）
- 静态方法的主要缺点是，类如果不含有共有的或受保护的构造器，就不能被子类化。如果是私有构造就没办法了

下面是一些静态工厂方法的惯用名称（大家可以看很多开源项目，都是遵循这个的，这个叫啥 约定优于配置）

- valueOf——不太严格地讲，该方法返回的实例与它的参数具有相同的值。这样的静态工厂方法实际上是类型转换方法。
- of—— valueOf的一 种更为简洁的替代，在EnumSet (见第32条)中使用并流行起来。
- getInstance-返回的实例是通过方法的参数来描述的，但是不能够说与参数具有同样的值。 对于Singleton来说， 该方法没有参数，并返回唯一的实例。
- newInstance——像 getInstance-样， 但newInstance 能够确保返回的每个实例都与所有其他实例不同。
- getType——像getInstance- 样， 但是在工厂方法处于不同的类中的时候使用。Type表示工厂方法所返回的对象类型。
- newType——像newinstance一样，但是在工厂方法处于不同的类中的时候使用。Type表示工厂方法所返回的对象类型。

### 遇到多个构造器参数的时候考虑用构造器
看到这条的时候，小六六不服气，哈哈 因为小六六写代码的水平很low呀
```
 Person person=new Person 
 person.setName="小六六";
```
有没有和小六六这样写代码的同款，哈哈，那么今天我们就来看看到底应不应该这样写，一般我们要生成一个对象，并且给对象赋值的话我们有几种方式可以选择
- 重叠构造器模式，啥子意思呢？就是我们不管它有多少个属性，我给它每个属性都搞上，如果你new一个对象的时候，那你就再那个位置上给个null啥的，哈哈，这个是什么鬼，肯定不行呀。
- JavaBeans模式，就是小六六上面写的代码了，哈哈，平时我们就是这样写的吧，这种方式虽然代码看来了low一点，但是感觉好像很好理解的样子，是不是，但是遗憾的是 它自身有着严重的错误，因为构造过程中JavaBean可能处于不一致状态，还有就是这种模式阻止了把类做成了一个不可变的对象了。
- 还有一种就是我们说的构建者模式了。小六六一般用Lomback的@Bulid注解，这个就很好的解决前面的问题，并且代码也足够优雅了嘛。
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2518fa5f152c4e4a9705e9e9358c98c5~tplv-k3u1fbpfcp-zoom-1.image)


### 用私有构造器或者枚举强化Singleton属性

其实这一条说的就是单例的实现了，我相信大家对单例肯定是很熟悉了，这边就不多说了


### 避免创建不必要的对象

这条的最佳实践的话，怎么说小六六其实本身不一定那么的有经验，肯定很多的时候，会创建很多没有用的对象，![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9a213fc465ee4a03b4725c38f5c4ab52~tplv-k3u1fbpfcp-zoom-1.image)

这个小六六之前肯定也这样写过，比如说我们的entity 一般用的包装类，但是dto可能是基本数据类型，然后就会有这种情况拉。
### 消除过期的引用
小六六以前写过C 但是写C的时候，还是在语法方面，还没到应用程序，所以对内存回收并不是那么的了解，所以很多时间，并不那么注重内存回收，但是就算是对于自动回收垃圾的Java, 消除过期的引用还是很有必要的呢？
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f85beec13b8f41e6ae1e4bc691e638da~tplv-k3u1fbpfcp-zoom-1.image)

小六六以前写代码一般不会注意这些，但是很多零时工（就是零时来放一下的对象） 我们用完之后还是给人家结算一下工资，不然人家会赖着你家不走哦。就是Object=null（发工资）

## 对所有对象都适用的方法
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f1f0b28a051a48aa8f5176c996dba40b~tplv-k3u1fbpfcp-zoom-1.image)

哈哈 这个真的有空，如果你用HashMap 然后对象做key的话，你忘记重新的它的hashCode 那你的代码估计很容易频繁的GC 或者OOM

### 覆盖equals时请遵循通用约定
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/954922c0bb544b11b088d8b0cc9c5f4b~tplv-k3u1fbpfcp-zoom-1.image)- 

### 覆盖equals的时候总要覆盖hashCode

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ac47c6ae4be249819b2c88b10ef870b9~tplv-k3u1fbpfcp-zoom-1.image)

### 要始终覆盖toString

这条只是建议了，并没有强制了，因为我们实际在编码的过程中，一般会用到JSON,那么当我们打日志 或者是抛出异常信息的话，我们就可以用这个JSON来代替toString了

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/65c5786c029441d5a365c3787757e57c~tplv-k3u1fbpfcp-zoom-1.image)


## 类和接口

###  使类和成员的可访问性最小化

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ccf698719a0d42f08cd354fe89b2f6f0~tplv-k3u1fbpfcp-zoom-1.image)

### 在共有类中使用访问方法而非共有域
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/dc86868f11134f89b1285567d1c0a21a~tplv-k3u1fbpfcp-zoom-1.image)

### 接口优于抽象类
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/167ca0b5d8684ab892044e22c3f84f5e~tplv-k3u1fbpfcp-zoom-1.image)

### 优先考虑静态成员类

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1164679c6a074dfc86eb8e4d3aa5d521~tplv-k3u1fbpfcp-zoom-1.image)


## 泛型 （编译之前的约束）

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b91f43996a204532bc6e101e877d4369~tplv-k3u1fbpfcp-zoom-1.image)

### 请不要在新代码中使用原生态类型

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e39a1535423b47a0934ea787581105f7~tplv-k3u1fbpfcp-zoom-1.image)
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c88bb73b5d424d41b469ee84c8aa786f~tplv-k3u1fbpfcp-zoom-1.image)


## 方法
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a06d1ae5112240f692346aafe15e4ee0~tplv-k3u1fbpfcp-zoom-1.image)

###  检查参数的有效性
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5825b4ec74f1405eae67ecaff3f79101~tplv-k3u1fbpfcp-zoom-1.image)

### 慎用可变参数
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a3b317dd70314881a6eb8893e3d7592d~tplv-k3u1fbpfcp-zoom-1.image)


### 返回零长度的数组或者集合，而不是Null

这条就很实用了，小六六自己也经常写这样的代码，比如我们抽取一些方法的时候，假设这个方法是去查询一个实体的时候，我们经常就会说直接把dao查出来的实体，直接当作方法的返回，这种做法是很不好的，我们应该再这个地方做一下非空校验，就算判断一下 返回一个没有对象的List，也比返回一个Null好，总的来说一条，就是你要永远认为调用你api的人的水平有限，或者说不可靠，如果它不做校验，那么是不是就会有问题呢？
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c2aec44bee7645e19ce4e09ce47cbfe7~tplv-k3u1fbpfcp-zoom-1.image)
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3aa204c0a280466db1a1442ab4148b11~tplv-k3u1fbpfcp-zoom-1.image)


## 通用程序设计
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/805de7020f3b4c4a94b5f144eaef0be8~tplv-k3u1fbpfcp-zoom-1.image)

### 将局部变量的作用域最小化

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/da0756cc2d9244c18f31614a76c47c4a~tplv-k3u1fbpfcp-zoom-1.image)

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a9b2c9314e9d4984889ceca0dfbf6b57~tplv-k3u1fbpfcp-zoom-1.image)


除了下面几种情况，其他的情况我建议还是用for-each
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ec9bea18d2b344eea55de145ab9c5457~tplv-k3u1fbpfcp-zoom-1.image)

### 如果需要精确的答案，请避免使用float和double

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/74b468ca97fe4638a7ad72cf1ebb517d~tplv-k3u1fbpfcp-zoom-1.image)

### 基于类型优先于装箱基本类型

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/40f8a08fa4484833a8b23e35d845046c~tplv-k3u1fbpfcp-zoom-1.image)


### 如果有其他类型更合适，则尽量避免使用字符串

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e64a6a90a30c4c98980518b9cfe85ba6~tplv-k3u1fbpfcp-zoom-1.image)

### 通过接口引用对象

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/41e92118946d4c589088e35664094bae~tplv-k3u1fbpfcp-zoom-1.image)

### 接口优先于反射机制

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/57374fc079d14248ab8c07b29777cc7c~tplv-k3u1fbpfcp-zoom-1.image)

## 异常
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/79150fff467c4f20a3c08ab23e279ac0~tplv-k3u1fbpfcp-zoom-1.image)

## 结尾

好了，这篇就结束了，希望给大家一点点帮助。

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
