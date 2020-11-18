# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206                          
> **种一棵树最好的时间是十年前，其次是现在**   

## 絮叨
最近看了下掘金小测 [Netty 入门与实战：仿写微信 IM 即时通讯系统]() ，然后巩固了下自己前面的学习，然后就想着做个学习记录吧，把实战的内容。

[github](https://github.com/lightningMan/flash-netty/tree/%E6%9E%84%E5%BB%BA%E5%AE%A2%E6%88%B7%E7%AB%AF%E4%B8%8E%E6%9C%8D%E5%8A%A1%E7%AB%AFpipeline) 对应的分支的话，就是下面这个
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f1008cbea4c442368089bc5bfc28c875~tplv-k3u1fbpfcp-watermark.image)，我主要是从这个分支下来总结一下学习记录吧！ 也是实战内容

## 总的结构

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/89c769d478994fdd80764d54d6c7b919~tplv-k3u1fbpfcp-watermark.image)

- attribute 这里面是用来给channal 设置值的 每个连接的值
- client 客户端
- codec 编码解码
- protocol 粘包拆包
- serialize 序列化
- server 服务端
- session 用户信息
- util 工具


然后我们分开来看 服务端和客户端


### 服务端

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/790ca77737774645876b48b935d0faf7~tplv-k3u1fbpfcp-watermark.image)

最主要是这几个hander，那么我们来看看

- Spliter 这个是用来解决粘包拆包，多包问题的 确定我们的魔数 我们的内容字节，这样我们收到的包就就不会有不完整的了。
- PacketCodecHandler  这个就是编码和解码的
- LoginRequestHandler 处理登录请求的
- AuthHandler 鉴权 意思是登录之后，并不是后面的责任链都需要去鉴权，所以就有这个鉴权的逻辑
- IMHandler 群聊和单聊相关的逻辑


以上就是我们服务端的编写，具体的代码大家自己下载去看看。


### 客户端

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7c7602c84c8c48b38edbcfc53dec89b7~tplv-k3u1fbpfcp-watermark.image)

我们可以看出，他的处理hander就比较多了，因为很多业务的hander在里面.什么加入群聊，通知群聊，退出群聊，等等逻辑都在里面了，




- [单聊一](https://juejin.im/post/6844903650741977095)
- [单聊二](https://juejin.im/post/6844903689111470094)
- [单聊三](https://juejin.im/post/6844903689581395981)



## 结尾
其实就是记录一下，下次用到的时候好找，这个文章推荐大家直接看源码撸，然后还得要有点基础这样。

![](//p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/14aafd3e9f1c4db8b62fe1e5baaaac4b~tplv-k3u1fbpfcp-zoom-1.image)
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
