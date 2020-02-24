# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨
其实写这个系列的原因的因为公司人员调动，上个团队中有一个人专门搞k8s,然后我们调出来的人（新团队，新项目），然后没有人会，然后我们领导说必须要人会，然后我前面因为学了点点，没办法就要我接手，行呗，想着反正接手了，做一个系列吧，也是自己学习的过程，因为时间关系Docker我就先不讲了，后面我有空了补上。Docker是基础必须要会,我学Rancher的目的是在它之上搭建K8s,因为原生的K8s,搭建起来比较困难。


## Rancher是什么
Rancher是一个开源的企业级容器管理平台。通过Rancher，企业再也不必自己使用一系列的开源软件去从头搭建容器服务平台。Rancher提供了在生产环境中使用的管理Docker和Kubernetes的全栈化容器部署与管理平台。

## 为什么需要Rancher
在原来, 如果我们需要做一个分布式集群我们需要学习一全套的框架并编码实现如 服务发现, 负载均衡等逻辑, 给开发者造成很大的负担, 不过好在现在有Docker以及他周边的一些技术能在上层解决这些问题, 而应用该怎么开发就怎么开发.

当你选择使用Docker技术栈的时候, 会发现在生产环境中不光光是 docker run就能解决的. 还需要考虑比如docker之间的组网, 缩扩容等问题, 于是你去学习kubernetes, 发现好像有点复杂啊, 有没有更傻瓜化一点的? 那就是rancher了.


## Rancher搭建
首先rancher需要安装了docker的linux环境，我的系统版本为

```
cat /etc/redhat-release
```

![](https://user-gold-cdn.xitu.io/2020/1/6/16f786c02dcf6870?w=495&h=62&f=png&s=7556)
然后Docker 环境

![](https://user-gold-cdn.xitu.io/2020/1/6/16f786cf6c5bf86a?w=680&h=413&f=png&s=45922)

执行命令

```
docker run -d --restart=always -p 8080:8080 rancher/server
```

命令行参数解释：

- docker run 的 -d 参数标示在后台运行
- –restart=always 容器如果异常停止自动重启
- -p 8082:8080 把 Rancher 服务器的UI 对外服务（容器内）的端口 8080（命令中第二个8080）  ，绑定到到宿主机的8082（命令中第一个8080） 端口。
- 后面跟的镜像名 rancher/server ，Docker 会首先检查本地有没有这个镜像，如果没有，Docker 会去 Docker Hub 将这个镜像下载下来，并且启动。


启动容器并指定端口，如果没有rancher/server镜像会自动下载。执行完成后查看镜像与容器运行情况：


![](https://user-gold-cdn.xitu.io/2020/1/6/16f788db90f11a2f?w=1344&h=532&f=png&s=79378)
以上步骤完成后，查看本机的ip地址，我这里用10.0.98.219，在浏览器输入 http://10.0.98.219:8082/ ，登录到rancher官网，为安全起见，设置管理账户:

![](https://user-gold-cdn.xitu.io/2020/1/6/16f7899daed8ebc3?w=1717&h=767&f=png&s=47763)


在右下角有系统语言设置，选择中文


![](https://user-gold-cdn.xitu.io/2020/1/6/16f789fdf834e97b?w=982&h=66&f=png&s=9462)


![](https://user-gold-cdn.xitu.io/2020/1/6/16f78a26e3ecd673?w=1151&h=535&f=png&s=56463)


![](https://user-gold-cdn.xitu.io/2020/1/6/16f78a4935fcfc52?w=1523&h=742&f=png&s=93322)
把上面的复制到你的liunx里面就行了，这样就可以安装一个主机

![](https://user-gold-cdn.xitu.io/2020/1/6/16f78a88a175c50a?w=1798&h=979&f=png&s=159217)

### 搭建一个Mysql

![](https://user-gold-cdn.xitu.io/2020/1/6/16f78b6beb24073e?w=2140&h=842&f=png&s=384656)
应用商店 搜索Mysql，然后点查看详情

![](https://user-gold-cdn.xitu.io/2020/1/6/16f78b76cdf79e84?w=1885&h=971&f=png&s=129420)
配置基本信息，然后点启动

![](https://user-gold-cdn.xitu.io/2020/1/6/16f78b7cbaf1df74?w=1905&h=395&f=png&s=53147)


![](https://user-gold-cdn.xitu.io/2020/1/6/16f78b7fb12d0b9f?w=1334&h=212&f=png&s=29358)
连接成功，是不是特别简单，


[Rancher中文文档](https://www.rancher.cn/ )  
[Racher 搭建 k8s](https://blog.51cto.com/13941177/2165668)
## 结尾
这个还是很简单的，但是真正的生产环境不可能这么简单的，有人说我们做开发的确实没必要说去懂那么多运维，但是你想要拓展一下自己的知识面，你就得学，下一章，我们先用原生的来搭建一个k8s，如果不走一遍原生的k8s的话，我估计还是很蒙。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
