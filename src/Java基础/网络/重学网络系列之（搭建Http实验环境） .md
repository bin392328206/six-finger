# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
## 叨絮
前面我们已经稍微的理解了些网络基础了，本来我也打算继续往理论学下去，什么7层模型，4层模型，tcp等，，这些知识是很重要的，但是如果全是去背这些理论，会有点点用，但或许学的不是那么深刻，所以呢？我这边就给打算跟着搭建一个实验环境，把里面的包呀。什么三次握手，四次挥手，让我们真实的去接触这些东西，下面是前面的章节
- [重学网络系列之（HTTP的前世今生）](https://mp.weixin.qq.com/s?__biz=MjM5OTA0MjE5Mg==&mid=2247485073&idx=1&sn=c28c74d1cef096341d600a586a148d9e&chksm=a6c0cb6e91b74278f946e1952dda61f838545d92ed6b7d4fc5c8ea34d5d20b97b3a82fa1fa35&token=357911130&lang=zh_CN#rd)
- [重学网络系列之（我的名字叫IP)](https://mp.weixin.qq.com/s?__biz=MjM5OTA0MjE5Mg==&mid=2247485084&idx=1&sn=0c164593148c75652840c6b7156b23f7&chksm=a6c0cb6391b742754280f6a00dcacf312454cda779a6fefae34187a18fed676a708bbad68792&token=357911130&lang=zh_CN#rd)
- [重学网络系列之（Ping与网关）](https://mp.weixin.qq.com/s?__biz=MjM5OTA0MjE5Mg==&mid=2247485094&idx=1&sn=40115e050bab2f8a9f89f42808bfe766&chksm=a6c0cb5991b7424f516dadd7670edb11733a816620f5a8f5b711295f1d44eb131c6680c7c082&token=357911130&lang=zh_CN#rd)


## 需要的环境列表

- Wireshark
- Chrome
- Telnet
- Openresty

### Wireshark
Wireshark是著名的网络抓包工具，能够截获在 TCP/IP 协议栈中传输的所有流量，并按协议类型、地址、端口等任意过滤，功能非常强大，是学习网络协议的必备工具。它就像是网络世界里的一台“高速摄像机”，把只在一瞬间发生的网络传输过程如实地“拍摄”下来，事后再“慢速回放”，让我们能够静下心来仔细地分析那一瞬到底发生了什么。

当然我们为啥不选择Flidder，其实我自己用Flidder还用的比较多，但是呢 对于Http的话，二者是差不多的，但是对于tcp/ip Wireshark更好点。。
#### 安装过程
[官网下载](https://www.wireshark.org/#download)
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d18fbebfae20434492ab9d76d33ef9d6~tplv-k3u1fbpfcp-watermark.image)

我们直接选择windows版本

①    下载完毕后，进行解压，双击安装执行文件，弹出安装窗口，点击【next】

②    安装路径可选择

③    然后一直下一步，下一步

### Chrome
谷歌浏览器，这个我就不说了，开发这个不会装就尴尬了

### Telnet
Telnet是一个经典的虚拟终端，基于 TCP 协议远程登录主机，我们可以使用它来模拟浏览器的行为，连接服务器后手动发送 HTTP 请求，把浏览器的干扰也彻底排除，能够从最原始的层面去研究 HTTP 协议。

这个是自带的

### OpenResty

OpenResty你可能比较陌生，它是基于 Nginx 的一个“强化包”，里面除了 Nginx 还有一大堆有用的功能模块，不仅支持 HTTP/HTTPS，还特别集成了脚本语言 Lua 简化 Nginx二次开发，方便快速地搭建动态网关，更能够当成应用容器来编写业务逻辑。选择 OpenResty 而不直接用 Nginx 的原因是它相当于 Nginx 的“超集”，功能更丰富，安装部署更方便。我也会用 Lua 编写一些服务端脚本，实现简单的 Web 服务器响应逻辑，方便实验。

安装过程

[openresty](https://juejin.cn/post/6844904174568603655#heading-13)
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e8fa9a541c3144fd9621319cf26a8f31~tplv-k3u1fbpfcp-watermark.image)


## 使用 IP 地址访问 Web 服务器

在 Chrome 浏览器的地址栏里输入“http://127.0.0.1/”，再按下回车键，等欢迎页面显示出来后 Wireshark 里就会有捕获的数据包，如下图所示。

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/808a2ecccbb74c9e89ddafd7f8af6a4e~tplv-k3u1fbpfcp-watermark.image)

在 Wireshark 里你可以看到，这次一共抓到了 11 个包（这里用了滤包功能，滤掉了 3 个包，原本是 14 个包），耗时 0.65 秒，下面我们就来一起分析一下"键入网址按下回车"后数据传输的全过程。

因为我们在地址栏里直接输入了 IP 地址“127.0.0.1”，而 Web 服务器的默认端口是80，所以浏览器就要依照 TCP 协议的规范，使用“三次握手”建立与 Web 服务器的连接。

对应到 Wireshark 里，就是最开始的三个抓包，浏览器使用的端口是 52085，服务器使用的端口是 80，经过 SYN、SYN/ACK、ACK 的三个包之后，浏览器与服务器的 TCP 连接就建立起来了。

有了可靠的 TCP 连接通道后，HTTP 协议就可以开始工作了。于是，浏览器按照 HTTP 协议规定的格式，通过 TCP 发送了一个“GET / HTTP/1.1”请求报文，也就是 Wireshark里的第四个包。

Web 服务器收到报文后在内部就要处理这个请求。同样也是依据 HTTP 协议的规定，解析报文，看看浏览器发送这个请求想要干什么。

它一看，原来是要求获取根目录下的默认文件，好吧，那我就从磁盘上把那个文件全读出来，再拼成符合 HTTP 格式的报文，发回去吧。这就是 Wireshark 里的第六个包“HTTP/1.1 200 OK”，底层走的还是 TCP 协议。

同样的，浏览器也要给服务器回复一个 TCP 的 ACK 确认，“你的响应报文收到了，多谢。”，即第七个包。

这时浏览器就收到了响应数据，但里面是什么呢？所以也要解析报文。一看，服务器给我的是个 HTML 文件，好，那我就调用排版引擎、JavaScript 引擎等等处理一下，然后在浏览器窗口里展现出了欢迎页面。

这之后还有两个来回，共四个包，重复了相同的步骤。这是浏览器自动请求了作为网站图标的“favicon.ico”文件，与我们输入的网址无关。但因为我们没有这个文件，所以服务器在硬盘上找不到，返回了一个“404 Not Found”。

### 确保Web安全的HTTPS

HTTP的缺点
- 通信使用明文（不加密），内容可能会被窃听
- 不验证通信方的身份，因此有可能遭遇伪装
所以我们需要Http加密

通信的加密
- 一种方式就是将通信加密。HTTP协议中没有加密机制，但可以通过和SSL（SecureSocketLayer，安全套接层）或TLS（TransportLayerSecurity，安全层传输协议）的组合使用，加密HTTP的通信内容。用SSL建立安全通信线路之后，就可以在这条线路上进行HTTP通信了。与SSL组合使用的HTTP被称为HTTPS（HTTPSecure，超文本传输安全协议）或HTTPoverSSL。
- 内容的加密，还有一种将参与通信的内容本身加密的方式。由于HTTP协议中没有加密机制，那么就对HTTP协议传输的内容本身加密。即把HTTP报文里所含的内容进行加密处理。在这种情况下，客户端需要对HTTP报文进行加密处理后再发送请求。

### 面试题 Https 建立安全通信的过程
我们来观察一下HTTPS的通信步骤。
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/704dc7413e1445c393bc7bba2f122296~tplv-k3u1fbpfcp-watermark.image)
- 步骤1：客户端通过发送ClientHello报文开始SSL通信。报文中包含客户端支持的SSL的指定版本、加密组件（CipherSuite）列表（所使用的加密算法及密钥长度等）。
- 步骤2：服务器可进行SSL通信时，会以ServerHello报文作为应答。和客户端一样，在报文中包含SSL版本以及加密组件。服务器的加密组件内容是从接收到的客户端加密组件内筛选出来的
- 步骤3：之后服务器发送Certificate报文。报文中包含公开密钥证书。
- 步骤4：最后服务器发送ServerHelloDone报文通知客户端，最初阶段的SSL握手协商部分结束。
- 步骤5：SSL第一次握手结束之后，客户端以ClientKeyExchange报文作为回应。报文中包含通信加密中使用的一种被称为Pre-mastersecret的随机密码串。该报文已用步骤3中的公开密钥进行加密。
- 步骤6：接着客户端继续发送ChangeCipherSpec报文。该报文会提示服务器，在此报文之后的通信会采用Pre-mastersecret密钥加密。
- 步骤7：客户端发送Finished报文。该报文包含连接至今全部报文的整体校验值。这次握手协商是否能够成功，要以服务器是否能够正确解密该报文作为判定标准。
- 步骤8：服务器同样发送ChangeCipherSpec报文。
- 步骤9：服务器同样发送Finished报文。
- 步骤10：服务器和客户端的Finished报文交换完毕之后，SSL连接就算建立完成。当然，通信会受到SSL的保护。从此处开始进行应用层协议的通信，即发送HTTP请求。
- 步骤11：应用层协议通信，即发送HTTP响应。
- 步骤12：最后由客户端断开连接。断开连接时，发送close_notify报文。上图做了一些省略，这步之后再发送TCPFIN报文来关闭与TCP的通信。

> 大家看看是不是很蒙b,小六六也是呀，但是还真有这样的公司笔试问（阅文集团）起点中文网，这个大家应该是知道的，我们来总结下https的连接过程吧（白话点） 第一就是客户端发ssl版本和一些加密组件给服务端，服务端收到返回给客户端，然后再发其他的报文，就是第一个回合就是客户端发一次，服务端发了3次，之后客户端和服务端再相互确认，最后才把安全的连接通道建立。。

## 结尾
今天其实没讲啥，但是都写了就发下吧，，感谢大家的阅读。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。 

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>微信 搜 "六脉神剑的程序人生" 回复888 有我找的许多的资料送给大家 


