# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨
昨天把集群搭建成功了，我们今天继续干。


- [🔥史上最全的企业级容器系列之kubernetes入门和搭建(一)](https://juejin.im/post/5e12d313f265da5d422c2cf8)
- [🔥史上最全的企业级容器系列之kubernetes入门和搭建(二)](https://juejin.im/post/5e13e20b6fb9a047fd1e6d1d)



## 通过资源配置运行容器
我们知道通过 run 命令启动容器非常麻烦，Docker 提供了 Compose 为我们解决了这个问题。那 Kubernetes 是如何解决这个问题的呢？其实很简单，使用 kubectl create 命令就可以做到和 Compose 一样的效果了，该命令可以通过配置文件快速创建一个集群资源对象

### 创建 YAML 配置文件
以部署 Nginx 为例
### 部署 Deployment
创建一个名为 nginx-deployment.yml 的配置文件
v1.16.0 之前
>注意： extensions/v1beta1 不再支持部署 Deployment，并且修改了少量命令
```
# API 版本号
apiVersion: extensions/v1beta1
# 类型，如：Pod/ReplicationController/Deployment/Service/Ingress
kind: Deployment
# 元数据
metadata:
  # Kind 的名称
  name: nginx-app
spec:
  # 部署的实例数量
  replicas: 2
  template:
    metadata:
      labels:
        # 容器标签的名字，发布 Service 时，selector 需要和这里对应
        name: nginx
    spec:
      # 配置容器，数组类型，说明可以配置多个容器
      containers:
      # 容器名称
      - name: nginx
        # 容器镜像
        image: nginx
        # 暴露端口
        ports:
        # Pod 端口
        - containerPort: 80
```
v1.16.0 之后
```
# API 版本号：由 extensions/v1beta1 修改为 apps/v1
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-app
spec:
  # 增加了选择器配置
  selector:
    matchLabels:
      app: nginx
  replicas: 2
  template:
    metadata:
      labels:
        # 设置标签由 name 修改为 app
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx
        ports:
        - containerPort: 80
```

```
# 部署
kubectl create -f nginx-deployment.yml

# 删除
kubectl delete -f nginx-deployment.yml
```

发布 Service
创建一个名为 nginx-service.yml 的配置文件
v1.16.0 之前

```
# API 版本号
apiVersion: v1
# 类型，如：Pod/ReplicationController/Deployment/Service/Ingress
kind: Service
# 元数据
metadata:
  # Kind 的名称
  name: nginx-http
spec:
  # 暴露端口
  ports:
    ## Service 暴露的端口
    - port: 80
      ## Pod 上的端口，这里是将 Service 暴露的端口转发到 Pod 端口上
      targetPort: 80
  # 类型
  type: LoadBalancer
  # 标签选择器
  selector:
    # 需要和上面部署的 Deployment 标签名对应
    name: nginx
```

v1.16.0 之后

```
apiVersion: v1
kind: Service
metadata:
  name: nginx-http
spec:
  ports:
    - port: 80
      targetPort: 80
  type: LoadBalancer
  selector:
    # 标签选择器由 name 修改为 app
    app: nginx
```


```
# 部署
kubectl create -f nginx-service.yml

# 删除
kubectl delete -f nginx-service.yml
```

## 验证是否生效
查看 Pod 列表

```
kubectl get pods

# 输出如下
NAME                         READY   STATUS    RESTARTS   AGE
nginx-app-64bb598779-2pplx   1/1     Running   0          25m
nginx-app-64bb598779-824lc   1/1     Running   0          25m
```
查看 Deployment 列表

```
kubectl get deployment

# 输出如下
NAME        READY   UP-TO-DATE   AVAILABLE   AGE
nginx-app   2/2     2            2           25m
```
查看 Service 列表

```
kubectl get service

# 输出如下
NAME         TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)        AGE
kubernetes   ClusterIP      10.96.0.1      <none>        443/TCP        20h
nginx-http    LoadBalancer   10.98.49.142   <pending>     80:31631/TCP   14m
```
查看 Service 详情

```
kubectl describe service nginx-http 

# 输出如下
Name:                     nginx-http
Namespace:                default
Labels:                   <none>
Annotations:              <none>
Selector:                 name=nginx
Type:                     LoadBalancer
IP:                       10.98.49.142
Port:                     <unset>  80/TCP
TargetPort:               80/TCP
NodePort:                 <unset>  31631/TCP
Endpoints:                10.244.141.205:80,10.244.2.4:80
Session Affinity:         None
External Traffic Policy:  Cluster
Events:                   <none>
```

通过浏览器访问
http://192.168.62.128:31556/
![](https://user-gold-cdn.xitu.io/2020/1/7/16f7f18302c06d71?w=1379&h=384&f=png&s=49117)


### 集成环境部署
也可以不区分配置文件，一次性部署 Deployment 和 Service，创建一个名为 nginx.yml 的配置文件，配置内容如下：

v1.16.0 之前

```
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  name: nginx-app
spec:
  replicas: 2
  template:
    metadata:
      labels:
        name: nginx
    spec:
      containers:
      - name: nginx
        image: nginx
        ports:
        - containerPort: 80
---
apiVersion: v1
kind: Service
metadata:
  name: nginx-http
spec:
  ports:
    - port: 80
      targetPort: 80
      # 可以指定 NodePort 端口，默认范围是：30000-32767
      # nodePort: 30080
  type: LoadBalancer
  selector:
    name: nginx
```

v1.16.0 之后


```
# API 版本号：由 extensions/v1beta1 修改为 apps/v1
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-app
spec:
  # 增加了选择器配置
  selector:
    matchLabels:
      app: nginx
  replicas: 2
  template:
    metadata:
      labels:
        # 设置标签由 name 修改为 app
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx
        ports:
        - containerPort: 80
---
apiVersion: v1
kind: Service
metadata:
  name: nginx-http
spec:
  ports:
    - port: 80
      targetPort: 80
  type: LoadBalancer
  selector:
    # 标签选择器由 name 修改为 app
    app: nginx
```


```
# 部署
kubectl create -f nginx.yml

# 删除
kubectl delete -f nginx.yml
```

## 结尾
今天就是把.yml的部署发布方式实现了一遍。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
