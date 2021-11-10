# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**


hi,各位小伙伴，我是小六六，今天呢？我给大家分享的是能够帮助我们更好的开发Java应用程序的库，只要用上了，你的开发效率至少提升十倍，让我们来看看它们分别是哪些库吧！

一个优秀且经验丰富的Java开发人员的特点之一是对API的广泛了解，包括JDK和第三方库。
几乎每个程序员都知道要“**避免重复发明轮子**”的道理——尽可能使用那些优秀的第三方框架或库，但当真正进入开发时，我们会经常的去忘记这些优秀的库，比如我经常在一些项目代码中看到如下代码
```
if(inputString == null || inputString.length == 0){......}
```
除了字符串判断是否为空之外，还有很多字符串处理或其他数据类型判断的方法，缺少经验的程序员们往往都会想办法自己来写。这些代码当然都没有错，但我们应该尽可能去利用那些已经非常成熟的第三方库，以更标准的方式去解决这些通用的问题，并且提高开发效率。

在本文中，小六六将分享一些Java开发人员应该熟悉的最有用和最重要的库和API。但是没有并包含框架，类似于Spring这种


![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ce91eee6deb143beb07c92fa21d50d22~tplv-k3u1fbpfcp-watermark.image?)

## Java程序员20个有用的开源库

这是我收集的一些有用的第三方库，Java开发可以使用它们在应用中来完成许多有用的功能。要使用这些库，Java开发人员应该熟悉它，这就是本文的重点。如果你觉得有用，你可以研究该库并使用它。

### Hutool
Hutool是一个小而全的Java工具类库，通过静态方法封装，降低相关API的学习成本，提高工作效率，使Java拥有函数式语言般的优雅，让Java语言也可以“甜甜的”。

Hutool中的工具方法来自每个用户的精雕细琢，它涵盖了Java开发底层代码中的方方面面，它既是大型项目开发中解决小问题的利器，也是小型项目中的效率担当；

Hutool是项目中“util”包友好的替代，它节省了开发人员对项目中公用类和公用工具方法的封装时间，使开发专注于业务，同时可以最大限度的避免封装不完善带来的bug。

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d09dbd5eb3274afe90c4c6e40a2d4c48~tplv-k3u1fbpfcp-watermark.image?)

小六六推荐语，我们平时有空的时候，多看看这些util这样我们写代码的时候就相当于有料了，会方便很多。

### commons-lang3
Apache Commons Lang是Apache最著名的JAVA库 ([GitHub上的代码库](https://link.jianshu.com?t=https://github.com/apache/commons-lang))，它是对java.lang的很好扩展，包含了大量非常实用的工具类，其中用的最多的有StringUtils，DateUtils，NumberUtils等。之前提到的代码利用StringUtils可以改写为：
```
if(StringUtils.isBlank(inputString)){...}
```
除了Apache Commons Lang，还有一些其他的Apache库也是对JAVA本身的很好补充，如[Apache Commons Collection](https://link.jianshu.com?t=http://commons.apache.org/proper/commons-collections/)，[Apache Commons IO](https://link.jianshu.com?t=http://commons.apache.org/proper/commons-io/)，[Apache Commons Math](https://link.jianshu.com?t=http://commons.apache.org/proper/commons-math/)

![a58c0098a9fa58511725f044a4ea7094.jpg](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7f87e3dc8d2f4633b49676eb52505f46~tplv-k3u1fbpfcp-watermark.image?)

### 日志库
日志库非常常见，因为在每个项目中都需要它们。它们是服务器端应用最重要的东西，因为日志只放在可以看到应用程序当前运行时情况的地方。尽管JDK附带了自己的日志库，但还有更好的替代方案，例如Log4j，SLF4j和LogBack。

目前在Java项目中，用的最多的应该是例如Log4j  SLF4j和LogBack

### JSON解析库

在当今的Web服务和物联网领域，JSON已成为将信息从客户端传送到服务器的首选协议。他们已经替换XML成为在独立平台间传输信息的最佳方式。

遗憾的是，JDK没有[JSON库](#axzz5Bxv7wony "#axzz5Bxv7wony")。但是，有许多优秀的第三方库允许你解析和创建JSON消息，如Jackson和Gson，FastJson

![f365afe094270a02b2e519b4e7dbe91c.jpg](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/798eb2b6aff14b059638c4af2c97c810~tplv-k3u1fbpfcp-watermark.image?)

小六六说下自己的一些看法哈，就是你在项目中我觉得不管用哪一个库，你不应该直接引用它们的api,你应该自己封装一个你们项目的工具类，但是这些工具类，并不是让你去实现这些json转化，而是你用开源库去实现的，这样的一个好处就是，你以为要再项目中换一个库，会方便很多，也可以做统一的异常处理。

### 单元测试库

单元测试是将普通开发人员与优秀开发人员区分开来的最重要的事情。程序员经常有理由不写单元测试，但逃避写单元测试的最常见的借口是缺乏常用单元测试库的经验和知识，包括JUnit，Mockito和PowerMock。

说到单元测试！我不知道大家再项目中写不写，但是我觉得肯定很少人能把单元测试写到百分之90以上，就连spring的单元测试也不是百分之百！但是一般开发中，因为赶进度，各种原因，我们真的很少写，但是这对代码的健壮性埋下了不少的隐患！

### HTTP库
JDK对HTTP支持是比较少的，而且我平时也很少用原生jdk去使用http。虽然你可以使用java.net包中的类建立HTTP连接 ，但使用开源的第三方库（如Apache HttpClient和OkHttp3）并不容易或不能无缝结合。

HTTP库我相信大家一定会用的，一般如果用Spring的话，也可能用上fegin,结合服务发现来用，但是我觉得HTTP库，它指的是外部交互，其实内部交互的模式的话，其实很多，但是对外大部分的话是选HTTP


### Excel和PDF库

这个用的确实也多，上传下载啥的都需要实现这个的，比如阿里的  **easyexcel** 还有 POI

这个是导入导出的，还有一个pdf的工具库    [Itext官网](https://link.juejin.cn/?target=https%3A%2F%2Fitextpdf.com%2Fen "https://itextpdf.com/en") 不知道有没有小伙伴用过

iText是著名的开放源码的站点sourceforge一个项目，是用于生成PDF文档的一个java类库。通过iText不仅可以生成PDF或rtf的文档，而且可以将XML、Html文件转化为PDF文件。

![ee38b2c3323c18b1a97fb144523a44b9.jpg](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a0714c968ce440048645377a6e4f77e1~tplv-k3u1fbpfcp-watermark.image?)

### 字节码库

如果你正在编写生成代码或与字节码交互的框架，那么你需要一个字节码库。

它们允许你读取和修改应用程序生成的字节码。Java世界中一些流行的字节码库是javassist和Cglib Nodep。

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9e351d7cdb114eff8554fce86a597554~tplv-k3u1fbpfcp-watermark.image?)

Javassist（Java programming assistant）使Java字节码操作变得非常简单。它是一个用于在Java中编辑字节码的类库。ASM是另一个有用的字节码编辑库。如果你不熟悉字节码，我建议你查看[Introduction to Java Programmers](https://link.juejin.cn?target=https%3A%2F%2Fclick.linksynergy.com%2Ffs-bin%2Fclick%3Fid%3DJVFxdTr9V80%26subid%3D0%26offerid%3D323058.1%26type%3D10%26tmpid%3D14538%26RD_PARM1%3Dhttps%3A%2F%2Fwww.udemy.com%2Fintroduction-to-java-programming%2F "https://click.linksynergy.com/fs-bin/click?id=JVFxdTr9V80&subid=0&offerid=323058.1&type=10&tmpid=14538&RD_PARM1=https://www.udemy.com/introduction-to-java-programming/")以了解有关它的更多信息。

### 日期和时间库

在Java 8之前，JDK的数据和时间库有很多缺陷，因为它们不是[线程安全的](#axzz5BxtJPSi9 "#axzz5BxtJPSi9")，[不可变的](https://link.juejin.cn?target=http%3A%2F%2Fjavarevisited.blogspot.sg%2F2018%2F02%2Fjava-9-example-factory-methods-for-collections-immutable-list-set-map.html "http://javarevisited.blogspot.sg/2018/02/java-9-example-factory-methods-for-collections-immutable-list-set-map.html")，并且容易出错。许多Java开发人员依靠JodaTime来实现他们的日期和时间要求。

从JDK 8开始，没有理由使用Joda，因为你在JDK 8的[新日期和时间API中](https://link.juejin.cn?target=http%3A%2F%2Fjavarevisited.blogspot.sg%2F2015%2F03%2F20-examples-of-date-and-time-api-from-Java8.html "http://javarevisited.blogspot.sg/2015/03/20-examples-of-date-and-time-api-from-Java8.html")获得了所有功能，但如果你使用的是较旧的Java版本，那么JodaTime是一个值得学习的库。

### Email API

javax.mail和Apache Commons Email都提供了一个用于[从Java发送电子邮件](https://link.juejin.cn/?target=http%3A%2F%2Fjavarevisited.blogspot.sg%2F2014%2F08%2Fhow-to-send-email-from-java-program-example.html "http://javarevisited.blogspot.sg/2014/08/how-to-send-email-from-java-program-example.html")的API 。它建立在JavaMail API的基础之上，旨在简化它。

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/55c41c056edc40668391e7bd9ec1b002~tplv-k3u1fbpfcp-watermark.image?)

### HTML解析库
与[JSON](https://juejin.cn/post/6844903781730091015#axzz5BxtJPSi9 "#axzz5BxtJPSi9")和[XML](https://link.juejin.cn/?target=http%3A%2F%2Fwww.java67.com%2F2012%2F09%2Fdom-vs-sax-parser-in-java-xml-parsing.html "http://www.java67.com/2012/09/dom-vs-sax-parser-in-java-xml-parsing.html")类似，HMTL是我们许多人必须处理的另一种常见格式。值得庆幸的是，我们有JSoup，它极大地简化了在Java应用程序中使用HTML的过程。

你不仅可以使用[JSoup](https://link.juejin.cn/?target=http%3A%2F%2Fjavarevisited.blogspot.sg%2F2014%2F09%2Fhow-to-parse-html-file-in-java-jsoup-example.html "http://javarevisited.blogspot.sg/2014/09/how-to-parse-html-file-in-java-jsoup-example.html")解析HTML，还可以创建HTML文档


![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9613c9e54501437196632c892e9b2cb3~tplv-k3u1fbpfcp-watermark.image?)

它提供了一个非常方便的API，用于提取和操作数据，使用[DOM](https://link.juejin.cn/?target=http%3A%2F%2Fjavarevisited.blogspot.sg%2F2011%2F12%2Fdifference-between-dom-and-sax-parsers.html "http://javarevisited.blogspot.sg/2011/12/difference-between-dom-and-sax-parsers.html")，CSS和类似jquery的方法。JSoup实现了WHATWG HTML5规范，并将[HTML](https://link.juejin.cn/?target=http%3A%2F%2Fwww.java67.com%2F2018%2F02%2F5-free-html-and-css-courses-to-learn-web-development.html "http://www.java67.com/2018/02/5-free-html-and-css-courses-to-learn-web-development.html")解析到同一个DOM，就像现代浏览器一样。

### 二维码处理库ZXing

ZXing，一个支持在图像中解码和生成条形码(如二维码、PDF 417、EAN、UPC、Aztec、Data Matrix、Codabar)的库。ZXing(“zebra crossing”)是一个开源的、多格式的、用Java实现的一维/二维条码图像处理库，具有到其他语言的端口。

目前的认知告诉我们，二维码是以正方形的形式存在，以类似于二进制的方式存储数据。

在Zxing中，使用`BitMatrix`来描述一个二维码，在其内部存储一个看似`boolean`值的矩阵数组。这个类很好的抽象了二维码。
]

### P6spy数据库性能分析
通过P6Spy可以对SQL语句进行拦截，相当于一个SQL语句的记录器，这样我们可以用它来作相关的分析，比如性能分析。

p6spy我的理解就是：p6spy将应用的数据源给劫持了，应用操作数据库其实在调用p6spy的数据源，p6spy劫持到需要执行的sql或者hql之类的语句之后，他自己去调用一个realDatasource，再去操作数据库，只要劫持到那些sql之后，能干的事情就很多了。

### MDC 日志染色


MDC（Mapped Diagnostic Context，映射调试上下文）是 log4j 和 logback 提供的一种方便在多线程条件下记录日志的功能，也可以说是一种轻量级的日志跟踪工具。

既然我们知道MDC可以让我们快速的捞到可用的日志信息，那具体怎么捞呢？我们先来看这样的一个场景：很多时候，我们一个程序调用链可能会很复杂，并且在调用链的各个环节中，会对一些关键的操作做日志埋点，比如说入参出参、复杂计算后的结果等等信息，但在线上环境是很多用户使用我们功能的，比如说A程序，每个用户都在使用了A程序后，打印了A程序方法调用链内的所以日志，那我怎么就知道这一堆相同日志中，哪些是同一次请求所打印的呢？可能大家会说：可以看它的线程名啊，HTTP在同一请求中会用同一个线程。一定程度上看线程是可以的，但我们也知道，web服务器不可能无限创建线程的，它内部有个线程池，用于HTTP线程的创建、回收等管理，如果该程序使用频率是很高，那完全有可能短时间内的几次请求用的都是同一个线程，这样的话就解决不了上述所说的：“把一次请求中调用链内的所以日志找出来”的需求了。

### 代码的逆向工程 MybatisPlus 的一个功能

在使用mybatis时需要程序员自己编写sql语句，针对单表的sql语句量是很大的，mybatis官方提供了一种根据数据库表生成mybatis执行代码的工具，这个工具就是一个逆向工程。

逆向工程：针对数据库单表—->生成代码(mapper.xml、mapper.java、pojo。。)

目前这种工具很多，也可以自己去开发一个，crud不用自己写还是很香的。


![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/32ad540420864ee6b6b47e11613d1c13~tplv-k3u1fbpfcp-watermark.image?)


## 结束

嗯！其实小六六这边就是给大家分享一些常用的工具类，让大家知道有这个东西，等到了真正要去用的时候，大家再去细细学习下啊！或者是有空的时候去学习下，也是非常不错的！今天就到这了，我是小六六，三天打鱼，两天晒网！
