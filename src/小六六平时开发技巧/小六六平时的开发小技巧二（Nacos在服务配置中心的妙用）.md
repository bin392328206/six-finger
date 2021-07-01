# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger

## 絮叨
小六六打算开一个系列把自己平时的一些开发经验分享出来和大家一起讨论我们平时开发过程中的一个最佳实践，就是想整理出来给大家参考一下，然后一起学习，一起进步

- [小六六平时的开发小技巧一（公共属性填充设计）](https://juejin.cn/post/6976067876394385415)

今天来聊聊我们应该怎么去设计我们系统的配置，大家也可以在文章下面留言看看你们公司的一个设计。

## Nacos做配置中心
> 这边默认大家懂一点点Nacos，就不去一一的细说这个怎么搭建，怎么写第一个hello word了，我们直接进入主题
## Maven的多环境配置
> 想必大家应该也知道这个吧，就是我们用Maven+bootstrap.yml来做多环境配置，所以这个小六六也不讲了，直接来看看我要给大家说的

## Maven+bootstrap.yml+Nacos 做多环境配置
### 第一步
首先我们搭建好nacos

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3ca50f8237d14987a817fc0f963b1407~tplv-k3u1fbpfcp-watermark.image)

### 配置我们的pom.xml里面的profiles，如下图

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/136c984038fa4478836bf95cee509b85~tplv-k3u1fbpfcp-watermark.image)

### 来看看我们bootstrap的配置文件长什么样

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f0fe09f63a0a443999f463c42940d4cd~tplv-k3u1fbpfcp-watermark.image)

我们通过@pom.nacos.namespace@}来关联不同的namespace，然后通过nacos里面的namespace来区分不同的环境

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/97920bb0e41c4221922303a5bdb840f5~tplv-k3u1fbpfcp-watermark.image)

## 结束
很简单的一个小实践，分享给大家，因为小六六之前一般是通过springboot的多环境+maven的profiles来做多环境的,或者大家如果用容器的话，k8s里面也可以做配置这个也不错。后面看到这种方式，觉得也很好，分享给大家哈。好了，就到这了，我是小六六 三天打鱼，二天晒网。



## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
