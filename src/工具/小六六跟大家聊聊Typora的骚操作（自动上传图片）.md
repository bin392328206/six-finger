# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**
## Typora
> 相信大家都有用到这个，小六六平时写文章也是用的这个工具，非常的好用，也可以用来写一些比较，周报啥的

但是呢有一个问题，一直困扰了我，就是比如我用 Snipaste截图了，然后直接ctrl+v粘贴到typora中。


![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c4960c55d67c49729b39644ecc62470b~tplv-k3u1fbpfcp-watermark.image)
在Typora中会有如下的效果，


![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8831d3247994484f8f618c80d1abf499~tplv-k3u1fbpfcp-watermark.image)

就是我们会把图片存到了本地，但是呢，我可能在写文章，我要复制到其他的地方，这个地方就会报错了，因为其他的地方不认得你这个路劲，不知道大家有没有这个困扰，今天小六六就跟大家一起来解决这个问题。

## 先看结果


![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/12441622cbb040bf8fa6f921c79e28a3~tplv-k3u1fbpfcp-watermark.image)
如上图，大家看看，我们这个就是最后的结果，上传之后，自动帮我上传到远端，那么我们就可以很容易的复制到其他的地方去了，非常的不错哈，下面就听小六六给大家来看看具体的操作步骤，希望对大家有帮助


## 下载Typora
- 地址
  https://www.typora.io/


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5a6c09e5b3954669a93713b378a4cc48~tplv-k3u1fbpfcp-watermark.image)
- 安装傻瓜式的安装就好了。



## 下载一个免费的图床工具
https://sm.ms/login

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/532bb58fae0541738c6cf499f770eb53~tplv-k3u1fbpfcp-watermark.image)

然后注册一个账号（不需要短信验证码哦）

注册完之后呢，先登录，然后点Dashboard,这样就能找到我们

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/fca51d3007c245ee9a1755f917177306~tplv-k3u1fbpfcp-watermark.image)

然后，点击API Token

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/573dbd12f56a4474ad702aa2964d7366~tplv-k3u1fbpfcp-watermark.image)

生成一个token

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/649bd784f6544c0dacdf4c40259b1382~tplv-k3u1fbpfcp-watermark.image)

## 设置我们的Typora
点击文件-> 选择我们的偏好设置
![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/80d0b093224643549cc24fe9141a3d21~tplv-k3u1fbpfcp-watermark.image)
选择图像，然后点击下载更新，然后选择它上面的PicGO-Core

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/96e8a2d3ca304827a64f6e410ea68f36~tplv-k3u1fbpfcp-watermark.image)

最后点打开配置文件


![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3d905ae2430e402886a717f39cdcd95d~tplv-k3u1fbpfcp-watermark.image)

然后配置上，上面的token



```js
{
  "picBed": {
    "uploader": "smms", // 代表当前的默认上传图床为 SM.MS,
    "smms": {
      "token": "这里面的token换成你上个页面的申请的token" //我们前面申请的token
    }
  },
  "picgoPlugins": {} // 为插件预留
}
```

## 最后
好了，自动上传就搞好了，大家可以去试试，我觉得这个功能很有必要，而且免费，因为我们写的东西，都是要去复制的，如果不能复制的话，那他的场景就会少很多。好了，今天就到这里，希望大家点击三联 点赞 再看 转发。

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>微信 搜 "六脉神剑的程序人生" 回复888 有我找的许多的资料送给大家 