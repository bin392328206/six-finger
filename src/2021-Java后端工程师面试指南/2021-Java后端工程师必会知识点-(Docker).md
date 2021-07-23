
上面的是脑图地址
## 叨絮
虽然公司用的都是容器，但是这个是容器组他们负责的，你一个搞业务开发的基本上就没能了解到那么全面了，所以今天小六六跟大家来学习了解了解Docker。

然后下面是前面的文章汇总
- [2021-Java后端工程师面试指南-(引言)](https://juejin.cn/post/6921868116481982477)
- [2021-Java后端工程师面试指南-(Java基础篇）](https://juejin.cn/post/6924472756046135304)
- [2021-Java后端工程师面试指南-(并发-多线程)](https://juejin.cn/post/6925213973205745672)
- [2021-Java后端工程师面试指南-(JVM）](https://juejin.cn/post/6926349737394176014)
- [2021-Java后端工程师面试指南-(MySQL）](https://juejin.cn/post/6927105516485214216)
- [2021-Java后端工程师面试指南-(Redis）](https://juejin.cn/post/6930816506489995272)
- [2021-Java后端工程师面试指南-(Elasticsearch）](https://juejin.cn/post/6931558669239156743)
- [2021-Java后端工程师面试指南-(消息队列）](https://juejin.cn/post/6932264624754524168)
- [2021-Java后端工程师面试指南-(SSM）](https://juejin.cn/post/6933006207082823688)
- [2021-Java后端工程师面试指南-(SpringBoot+SpringCloud）](https://juejin.cn/post/6933372510763548686)
- [2021-Java后端工程师面试指南-(分布式理论+Zookeeper）](https://juejin.cn/post/6934623205823315976)
- [2021-Java后端工程师面试指南-(计算机网络）](https://juejin.cn/post/6936345290085498911)
- [2021-Java后端工程师必会知识点-(操作系统)](https://juejin.cn/post/6942686874301857800)
- [2021-Java后端工程师必会知识点-(分布式RPC框架Dubbo)](https://juejin.cn/post/6948350596843831333)
- [2021-Java后端工程师必会知识点-(Lunix)](https://juejin.cn/post/6953430772338393101#comment)

> 大家好，我是小六六，目前在一线互联网公司负责年营收过百亿的支付中台项目，感谢大家的支持，今天我们来看看 Docker


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d7216cecac724e179b4b7fa2bc7ac7a2~tplv-k3u1fbpfcp-watermark.image)


## Docker简介

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ba108dab7c9048afbd2ef45010b78485~tplv-k3u1fbpfcp-watermark.image)

### 为啥是Docker
一款产品从开发到上线，从操作系统，到运行环境，再到应用配置。作为开发+运维之间的协作我们需要关心很多东西，这也是很多互联网公司都不得不面对的问题，特别是各种版本的迭代之后，不同版本环境的兼容，对运维人员都是考验,不知道大家公司是怎么样的，小六六这边就是用的Docker容器。

Docker之所以发展如此迅速，也是因为它对此给出了一个标准化的解决方案。
环境配置如此麻烦，换一台机器，就要重来一次，费力费时。很多人想到，能不能从根本上解决问题，软件可以带环境安装？也就是说，安装的时候，把原始环境一模一样地复制过来。开发人员利用 Docker 可以消除协作编码时“在我的机器上可正常工作”的问题。

### Docker的理念
Docker是基于Go语言实现的云开源项目。
Docker的主要目标是 “Build，Ship and Run Any App,Anywhere” ，也就是通过对应用组件的封装、分发、部署、运行等生命周期的管理，使用户的APP（可以是一个WEB应用或数据库应用等等）及其运行环境能够做到“一次封装，到处运行”。

Linux 容器技术的出现就解决了这样一个问题，而 Docker 就是在它的基础上发展过来的。将应用运行在 Docker 容器上面，而 Docker 容器在任何操作系统上都是一致的，这就实现了跨平台、跨服务器。只需要一次配置好环境，换到别的机子上就可以一键部署好，大大简化了操作

### 为什么要使用 Docker
作为一种新兴的虚拟化方式，Docker 跟传统的虚拟化方式相比具有众多的优势
- 更高效的利用系统资源
- 更快速的启动时间
- 一致的运行环境
- 持续交付和部署
- 更轻松的迁移
- 对比传统虚拟机总结

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/97de95558fa94bda89495af8c73c04cb~tplv-k3u1fbpfcp-zoom-1.image)



### Docker下载
- docker官网：http://www.docker.com
- docker中文网站：https://www.docker-cn.com/
- Docker Hub官网: https://hub.docker.com/

## Docker安装（centos7）
1、Docker 要求 CentOS 系统的内核版本高于 3.10 ，查看本页面的前提条件来验证你的CentOS 版本是否支持 Docker 。

通过 uname -r 命令查看你当前的内核版本

```
 $ uname -r
```

2、使用 root 权限登录 Centos。确保 yum 包更新到最新。

```
$ sudo yum update
```
3、卸载旧版本(如果安装过旧版本的话)

```
$ sudo yum remove docker  docker-common docker-selinux docker-engine
```

4、安装需要的软件包， yum-util 提供yum-config-manager功能，另外两个是devicemapper驱动依赖的

```
$ sudo yum install -y yum-utils device-mapper-persistent-data lvm2
```
5、设置yum源


```
$ sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
```
6、可以查看所有仓库中所有docker版本，并选择特定版本安装

```
$ yum list docker-ce --showduplicates | sort -r
```

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/44c8cc0ca06e41788853b74907027f43~tplv-k3u1fbpfcp-zoom-1.image)
7、安装docker

```
$ sudo yum install docker-ce  #由于repo中默认只开启stable仓库，故这里安装的是最新稳定版17.12.0
$ sudo yum install <FQPN>  # 例如：sudo yum install docker-ce-17.12.0.ce
```

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7c679eaa442747d9b6ba38264a43daaf~tplv-k3u1fbpfcp-zoom-1.image)

8、启动并加入开机启动

```
$ sudo systemctl start docker
$ sudo systemctl enable docker
```

9、验证安装是否成功(有client和service两部分表示docker安装启动都成功了)

```
$ docker version
```

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7b62060943de4d1d92d2af2b074c4af9~tplv-k3u1fbpfcp-zoom-1.image)
10、Docker 镜像加速器
对于使用 systemd 的系统，请在 /etc/docker/daemon.json 中写入如下内容（如果文件不存在请新建该文件）

```
{
  "registry-mirrors": [
    "https://registry.docker-cn.com"
  ]
}
```
之后重新启动服务。


```
$ sudo systemctl daemon-reload
$ sudo systemctl restart docker
```

## Docker镜像
我们都知道，操作系统分为内核和用户空间。对于 Linux 而言，内核启动后，会挂载 root 文件系统为其提供用户空间支持。而 Docker 镜像（Image），就相当于是一个 root 文件系统。比如官方镜像 ubuntu:16.04 就包含了完整的一套 Ubuntu 16.04 最小系统的 root 文件系统。

Docker 镜像是一个特殊的文件系统，除了提供容器运行时所需的程序、库、资源、配置等文件外，还包含了一些为运行时准备的一些配置参数（如匿名卷、环境变量、用户等）。镜像不包含任何动态数据，其内容在构建之后也

### 分层存储
因为镜像包含操作系统完整的 root 文件系统，其体积往往是庞大的，因此在 Docker 设计时，就充分利用 Union FS 的技术，将其设计为分层存储的架构。所以严格来说，镜像并非是像一个 ISO 那样的打包文件，镜像只是一个虚拟的概念，其实际体现并非由一个文件组成，而是由一组文件系统组成，或者说，由多层文件系统联合组成。

镜像构建时，会一层层构建，前一层是后一层的基础。每一层构建完就不会再发生改变，后一层上的任何改变只发生在自己这一层。比如，删除前一层文件的操作，实际不是真的删除前一层的文件，而是仅在当前层标记为该文件已删除。在最终容器运行的时候，虽然不会看到这个文件，但是实际上该文件会一直跟随镜像。因此，在构建镜像的时候，需要额外小心，每一层尽量只包含该层需要添加的东西，任何额外的东西应该在该层构建结束前清理掉。

分层存储的特征还使得镜像的复用、定制变的更为容易。甚至可以用之前构建好的镜像作为基础层，然后进一步添加新的层，以定制自己所需的内容，构建新的镜像。

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5e376b45c37a4c8d877b81d055d5e083~tplv-k3u1fbpfcp-watermark.image)

## Docker命令
### 帮助命令
- docker version
- docker info
- docker --help
### 镜像命令
- docker images(列出本地主机上的镜像)
- docker search 某个XXX镜像名字 (查找镜像)
- docker pull 某个XXX镜像名字(下载镜像)
- docker rmi 某个XXX镜像名字ID(删除某个镜像)
### 容器命令
- docker run [OPTIONS] IMAGE [COMMAND] (新建并启动容器)
- docker ps [OPTIONS] (列出当前所有正在运行的容器)
- docker start 容器ID或者容器名(启动容器)
- docker restart 容器ID或者容器名(重启容器)
- docker stop 容器ID或者容器名(停止容器)
- docker kill 容器ID或者容器名(强制停止容器)
- docker rm 容器ID(删除已停止的容器)
- docker run -d 容器名(启动守护式容器)
- docker logs -f -t --tail 容器ID(查看容器日志)
- docker exec -it 容器ID bashShell (进入正在运行的容器并以命令行交互)
- docker cp 容器ID:容器内路径 目的主机路径(从容器内拷贝文件到主机上)


### docker pull 拉取镜像

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1084166c19d04d61a94b51fda6325004~tplv-k3u1fbpfcp-watermark.image)

-  Docker Client接受docker pull命令，解析完请求以及收集完请求参数之后，发送一个HTTP请求给Docker Server，HTTP请求方法为POST，请求URL为”/images/create? “+”xxx”；
- Docker Server接受以上HTTP请求，并交给mux.Router，mux.Router通过URL以及请求方法来确定执行该请求的具体handler；
- mux.Router将请求路由分发至相应的handler，具体为PostImagesCreate；
- 在PostImageCreate这个handler之中，一个名为”pull”的job被创建，并开始执行；
- 名为”pull”的job在执行过程中，执行pullRepository操作，即从Docker Registry中下载相应的一个或者多个image；
-  名为”pull”的job将下载的image交给graphdriver；
- graphdriver负责将image进行存储，一方创建graph对象，另一方面在GraphDB中记录image之间的关系。
### docker run hello-world运行原理

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/543fab5a51d64c19b6a09fcecf02fc14~tplv-k3u1fbpfcp-watermark.image)

## Docker数据卷
### 是什么
Docker容器产生的数据，如果不通过docker commit生成新的镜像，使得数据做为镜像的一部分保存下来，
那么当容器删除后，数据自然也就没有了，为了能保存数据在docker中我们使用卷。

**一句话：有点类似我们Redis里面的rdb和aof文件，也就是我们所说的持久化用的**

### 数据卷的特点
卷的设计目的就是数据的持久化，完全独立于容器的生存周期，因此Docker不会在容器删除时删除其挂载的数据卷

特点：
- 数据卷可在容器之间共享或重用数据
- 卷中的更改可以直接生效
- 数据卷中的更改不会包含在镜像的更新中
- 数据卷的生命周期一直持续到没有容器使用它为止


### 容器内添加
- 直接命令添加
> docker run -it -v /宿主机绝对路径目录:/容器内目录 镜像名
- DockerFile添加
  可在Dockerfile中使用VOLUME指令来给镜像添加一个或多个数据卷

## Dockerfile
### 是什么
Dockerfile是用来构建Docker镜像的构建文件，是由一系列命令和参数构成的脚本。

构建三步骤
- 编写Dockerfile文件
- docker build
- docker run

### DockerFile构建过程解析

#### Dockerfile内容基础知识
- 每条保留字指令都必须为大写字母且后面要跟随至少一个参数
- 指令按照从上到下，顺序执行
- #表示注释
- 每条指令都会创建一个新的镜像层，并对镜像进行提交

#### Docker执行Dockerfile的大致流程
- docker从基础镜像运行一个容器
- 执行一条指令并对容器作出修改
- 执行类似docker commit的操作提交一个新的镜像层
- docker再基于刚提交的镜像运行一个新容器
- 执行dockerfile中的下一条指令直到所有指令都执行完成

### DockerFile体系结构
- FROM(基础镜像，当前新镜像是基于哪个镜像的)
- MAINTAINER(镜像维护者的姓名和邮箱地址)
- RUN(容器构建时需要运行的命令)
- EXPOSE(当前容器对外暴露出的端口)
- WORKDIR(指定在创建容器后，终端默认登陆的进来工作目录，一个落脚点)
- ENV(用来在构建镜像过程中设置环境变量)
- ADD(将宿主机目录下的文件拷贝进镜像且ADD命令会自动处理URL和解压tar压缩包)
- COPY(类似ADD，拷贝文件和目录到镜像中。将从构建上下文目录中 &lt;源路径&gt; 的文件/目录复制到新的一层的镜像内的 &lt;目标路径&gt; 位置)
- VOLUME(容器数据卷，用于数据保存和持久化工作)
- CMD(指定一个容器启动时要运行的命令)
- ENTRYPOINT(ENTRYPOINT 的目的和 CMD 一样，都是在指定容器启动程序及参数)
- ONBUILD(当构建一个被继承的Dockerfile时运行命令，父镜像在被子继承后父镜像的onbuild被触发)

## 本地镜像发布到阿里云

### 镜像的生成方法
- 前面的DockerFile
- 从容器创建一个新的镜像 docker commit [OPTIONS] 容器ID [REPOSITORY[:TAG]]

### 将本地镜像推送到阿里云

阿里云开发者平台
https://cr.console.aliyun.com/cn-shanghai/instances/repositories


> 将镜像推送到registry
- docker login --username=danielyoungchina registry.cn-shanghai.aliyuncs.com
- docker tag [ImageId] registry.cn-shanghai.aliyuncs.com/daniel-hub/nginx-docker:[镜像版本号]
- docker push registry.cn-shanghai.aliyuncs.com/daniel-hub/nginx-docker:[镜像版本号]
> 从Registry中拉取镜像
- docker pull registry.cn-shanghai.aliyuncs.com/daniel-hub/nginx-docker:[镜像版本号]

## 结束
小六六这边给大家整理了下Docker的基础知识，因为为啥面试要准备这个呢？我们知道，现在基本上是k8s的服务治理，服务的发布基本上是基于容器了，可能在公司用的时候我们很简单，点一下升级按钮就好了，例如下面

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/532d469c295c4f4d801088ff6e081450~tplv-k3u1fbpfcp-watermark.image)

但是呢？我们还是需要自己去了解这块东西，这样遇到问题解决起来就会快很多看。

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>微信 搜 "六脉神剑的程序人生" 回复888 有我找的许多的资料送给大家 