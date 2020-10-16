# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206                          
> **种一棵树最好的时间是十年前，其次是现在**   


# six-finger-web
一个Web后端框架的轮子从处理Http请求【基于Netty的请求级Web服务器】 到mvc【接口封装转发)】，再到ioc【依赖注入】，aop【切面】，再到 rpc【远程过程调用】最后到orm【数据库操作】全部自己撸一个（简易）的轮子。

[github](https://github.com/bin392328206/six-finger-web)

## 为啥要写这个轮子
其实是这样的，小六六自己平时呢？有时候喜欢看看人家的源码比如Spring,但是小六六的水平可能不怎么样，每次看都看得晕头转向，然后就感觉里面的细节太难了，然后我就只能观其总体的思想，然后我就想我如果可以根据各位前辈的一些思考，自己撸一个简单的轮子出来，那我后面去理解作者的思想是不是简单点呢？于是呢 six-finger-web就面世了，它其实就是我的一个学习过程，然后我把它开源出来，希望能帮助那些对于学习源码有困难的同学。还有就是可以锻炼一下自己的编码能力，因为平时我们总是crud用的Java api都是那些，久而久之，很多框架类的api我们根本就不熟练了，所以借此机会，锻炼一下。

## 特点
- 内置由 Netty 编写 HTTP 服务器，无需额外依赖 Tomcat 之类的 web 服务（刚好小六六把Netty系列写完，顺便用下）
- 代码简单易懂（小六六自己写不出框架大佬那种高类聚，低耦合的代码），能力稍微强一点看代码就能懂，弱点的也没关系，小六六有配套的从0搭建教程。
- 支持MVC相关的注解确保和SpringMVC的用法类似
- 支持Spring IOC 和Aop相关功能
- 支持类似于Mybatis相关功能
- 支持类似于Dubbo的rpc相关功能
- 对于数据返回，只支持Json格式


## 絮叨
此教程只适合初中级水平，因为作者本身水平不高，不喜勿喷，今天是文章的第二篇，实现一个简单的SpringMVC,参考的是Guide哥的jsoncat。
好了，下面我给大家来一一走一遍搭建流程
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（一）](https://juejin.im/post/6883284588110544904)

这边建议一边下载源码，一边来看，如果觉得有问题的话

## 总结的包结构

首先我们来看看写完了MVC之后的包结构，以至于大家心里有数

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7c9740fbe41d4e6ebfc936de44cc8325~tplv-k3u1fbpfcp-watermark.image)


### pom的修改

首先第一步，我先把pom文件新增的地方给大家看看

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8ea88d3240de40918315c8c1644dee6e~tplv-k3u1fbpfcp-watermark.image)

引入他们一个是反射，因为写轮子，反射肯定用的多，还有一个就是json转换工具

### 具体MVC的实现
这边我先把整体的思想给大家捋捋，这个只是一个简单的实现，还有很多的细节没有完成

首先，我们看看我们写业务是个怎么样的样子，如下图
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3f1c084b9ee44d0580ad0de45e08d308~tplv-k3u1fbpfcp-watermark.image)

好，这个就是我们最终要做到的效果，对吧，那我们浏览器请求的时候长什么样呢？
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d9036b60f878469c91c68238c45c1efa~tplv-k3u1fbpfcp-watermark.image)

它长这样，那其实久很清晰了，SpringMVC要实现的就是不同请求走不同的方法嘛，这就是核心思想嘛。总的来说其实就三个部分
- 一个是原有的server的改造
- 一个是springmvc相关容器组件的初始化
- 一个是http请求的请求流程处理

下面，我一一来讲解

### 改造Netty服务器的请求部分

这边我解题然后把找类 方法的流程给大家，大家自己去看源码

HttpRequestHandler->channelRead0

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/872900fc92834e7b9bed6c82e6343f00~tplv-k3u1fbpfcp-watermark.image)

其实服务器这边需要改造的地方也不多，就是通过请求，来找到不同的处理器，然后再去处理我们的业务，然后根据业务的返回值，然后我们再去封装一些成功或者失败，然后这边来看看server包下面的几个类，具体的代码，大家就自己去看了，每个类基本上有注释
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0da20c9e418b4b3eabcb12978de77cd5~tplv-k3u1fbpfcp-watermark.image)

### springmvc相关组件的初始化

首先我们再来看看我们的启动类这边，
类 ApplicationServer
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8793b215b7a64fd886953bde9dcc9e47~tplv-k3u1fbpfcp-watermark.image)

然后我们就可以往下跟代码了，其实大家看源码也是这样，但是源码的话细节毕竟多，所以难懂点

类 DispatcherMethodMapper->loadRoutes 
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a79ce7ae3a4f43298098704228736abb~tplv-k3u1fbpfcp-watermark.image)

这个类的东西还比较多，基本上很多地方我写了注释，如果不懂的可以私下请教，然后我们大致来分析一下吧！
- 第一个肯定是加载包的扫描路径，因为我这个是基于注解的，我就在配置文件中配置扫描路径
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f2aa2ad685374169a82e9753adca7c61~tplv-k3u1fbpfcp-watermark.image)
- 之后就是遍历里面的.class文件，然后找到类上面的注解，如果有RestController 说明他这个类是用来处理请求的，然后遍历类中的方法，然后去一个个分析 PostMapping RequestMapping GetMapping 反正也就是if else 然后把这些方法，把请求的url封装到一个springmvc的容器中，等请求来的时候，就去容器中匹配，然后找到对应的方法，再填充对应的参数，就可以执行方法了，具体的呢？建议大家跟着源码来看哈

###  之后便是我们的主流程了，我们来看看主题流程怎么走的
还是在服务端那个类那跟起
HttpRequestHandler->channelRead0

我们来看这个方法中的
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f8c7039f635f4b95b4317bb4edb8dfb7~tplv-k3u1fbpfcp-watermark.image)
RequestHandlerFactory->create
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/735708320ddd4426be88036adc8436fb~tplv-k3u1fbpfcp-watermark.image)
其实就是根据不同的请求方式，找到对应的处理类

然后我们接下去看
HttpRequestHandler->channelRead0
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7f89913c5c2c4da8b34637bec4658c79~tplv-k3u1fbpfcp-watermark.image)
然后大家看，这个方法，才是我们的重头戏，也就是我们处理的核心了，我们往下跟
RequestHandler 发现是一个接口
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2e24823ce887455ba0d5feb1cbf305b2~tplv-k3u1fbpfcp-watermark.image)

假设我们是Get请求找到他的实现类

GetRequestHandler->handle 这个就是我们真正要看的逻辑所在了，我们往下看

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4944210367634308a8e817c98075c9d8~tplv-k3u1fbpfcp-watermark.image)
封装请求参数和路径参数

DispatcherMethodMapper->getMethodDetail
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5ca6d2ebe3ec47d980365f79e36db0f9~tplv-k3u1fbpfcp-watermark.image)

MethodDetail->build

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0d7d3ff3daf7423991025ca35d9c528e~tplv-k3u1fbpfcp-watermark.image)

这个就是核心填充的方法了，当填充完成之后我们会获得一个对象，这个对象就比较厉害了，他包含，当前要执行的方法 请求参数等，如下图
GetRequestHandler->handle 
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3be98454cc8c4cad83e3916648336e39~tplv-k3u1fbpfcp-watermark.image)

接着往下
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/cfd103cf2e7242338f152f2237f8b1ff~tplv-k3u1fbpfcp-watermark.image)

我们来看下，里面的核心方法
ParameterResolver->resolve 
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/28633a42b97d4a95b9a2d92649216cfe~tplv-k3u1fbpfcp-watermark.image)
根据不同的方式去填充，然后填充完成之后呢

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1e90051e53264e91b0dde4147143e21b~tplv-k3u1fbpfcp-watermark.image)

通过反射去拿到当前要执行的对象，然后再通过反射去执行对应的方法，至此mvc的所有流程走完了

## 我们看看请求的结果

要执行的RestController
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b53c80e3b956406bb442e8317c9e2368~tplv-k3u1fbpfcp-watermark.image)![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/28ec7e6aea9d4e089ff756580f14f40c~tplv-k3u1fbpfcp-watermark.image)


请求 http://localhost:8081/user/小六六写MVC

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1da511248d754126b8363382378a33de~tplv-k3u1fbpfcp-watermark.image)


## 结尾
好了，我们把MVC的小小流程写完了，代码也上传了，希望大家好好学习，一起加油，后面就要撸ioc 和aop了。

![](//p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/14aafd3e9f1c4db8b62fe1e5baaaac4b~tplv-k3u1fbpfcp-zoom-1.image)
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！