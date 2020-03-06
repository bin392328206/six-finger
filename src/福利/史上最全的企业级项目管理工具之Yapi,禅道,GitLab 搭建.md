# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨
刚好公司要搭建一些基础服务，想着在搭建的同时，记录一下搭建的过程，为了以后需要的话，可以简单点，所以做了一下记录


## 禅道
禅道是一个项目管理工具，反正很多公司用它来管理项目。具体怎么用大家自己去摸索，我先把部署起来再说

禅道的话 我这边用docker来部署吧 具体参考[禅道](https://hub.docker.com/r/idoop/zentao)


```
mkdir -p /data/zbox && \
docker run -d -p 80:80 -p 3306:3306 \
        -e ADMINER_USER="root" -e ADMINER_PASSWD="password" \
        -e BIND_ADDRESS="false" \
        -v /data/zbox/:/opt/zbox/ \
        --add-host smtp.exmail.qq.com:163.177.90.125 \
        --name zentao-server \
        idoop/zentao:latest
```

很简单就是 注意把禅道的数据卷给挂出来就行了，我们来访问80看看效果

![](https://user-gold-cdn.xitu.io/2020/1/8/16f82c7c3bbcfef5?w=2114&h=868&f=png&s=498343)


## Yapi
YApi 是一个可本地部署的、打通前后端及QA的、可视化的接口管理平台 https://hellosean1025.github.io/yapi  
Yapi 还是蛮好用的，用于前后端的接口联调，接口文档的管理等

这边我也是用docker-compose搭建，yapi用的mongo作数据库
- 先创建一个yapi的目录

```
mkdir yapi
```
- 编写docker-compose

cd yapi/

vim docker-compose.yml

```

version: "3"
services:
  mongo:
    image: mongo:3
    container_name: mongo
    networks:
      - yapi
    environment:
      - MONGO_INITDB_ROOT_USERNAME=yapi
      - MONGO_INITDB_ROOT_PASSWORD=yapi
      - MONGO_INITDB_DATABASE=yapi
    ports:
      - "27016:27017"
    volumes:
      - ./mongo-data:/data/db
  yapi:
    image: wyntau/ymfe-yapi
    container_name: yapi
    depends_on:
      - mongo
    ports:
      - "3000:3000"
    networks:
      - yapi
    volumes:
      - ./config.json:/app/config.json
      - ./yapi-runtime:/app/runtime
networks:
  yapi:
```
vi config.json

```
{
  "port": "3000",
  "adminAccount": "admin@example.com",
  "db": {
    "servername": "mongo",
    "DATABASE": "yapi",
    "port": 27017,
    "user": "yapi",
    "pass": "yapi",
    "authSource": "admin"
  }
}

```

- 然后启动
docker-compose up -d 

docker ps 查看运行状态

访问 10.0.51.198:3000


![](https://user-gold-cdn.xitu.io/2020/1/8/16f82efaccb93f60?w=1889&h=869&f=png&s=154020)

搭建成功，具体的用法，大家还是看下文档，我今天只是记录搭建的过程，谢谢


## Gitlab

基于 Docker 安装 GitLab


我们使用 Docker 来安装和运行 GitLab 中文版，由于新版本问题较多，这里我们使用目前相对稳定的 10.5 版本，docker-compose.yml 配置如下：

```
version: '3'
services:
  gitlab:
    image: 'twang2218/gitlab-ce-zh'
    restart: always
    hostname: '10.0.51.198'
    environment:
      TZ: 'Asia/Shanghai'
      GITLAB_OMNIBUS_CONFIG: |
        external_url 'http://10.0.51.198:9001'
        gitlab_rails['gitlab_shell_ssh_port'] = 2222
        unicorn['port'] = 8888
        nginx['listen_port'] = 9001
    ports:
      - '9001:9001'
      - '8443:443'
      - '2222:22'
    volumes:
      - ./config:/etc/gitlab
      - ./data:/var/opt/gitlab
      - ./logs:/var/log/gitlab
```

然后访问 http://10.0.51.198:9001

![](https://user-gold-cdn.xitu.io/2020/1/8/16f833bcb8ccf9fc?w=1863&h=625&f=png&s=118862)
