# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨
昨天我们把Master节点成功搭建了，今天我们要继续把Node节点搭建成功,并且部署几个服务玩玩。

- [🔥史上最全的企业级容器系列之kubernetes入门和搭建(一)](https://juejin.im/post/5e12d313f265da5d422c2cf8)

## 使用 kubeadm 配置 slave 节点

将 slave 节点加入到集群中很简单，只需要在 slave 服务器上安装 kubeadm，kubectl，kubelet 三个工具，然后使用 kubeadm join 命令加入即可。准备工作如下：
- 修改主机名
- 配置软件源
- 安装三个工具

然后执行那个
![](https://user-gold-cdn.xitu.io/2020/1/7/16f7dbff074ddafd?w=918&h=210&f=png&s=26616)
上一节初始化Master的时候生成的

```
kubeadm join 192.168.62.159:6443 --token abcdef.0123456789abcdef   --discovery-token-ca-cert-hash sha256:7237dd082021214d77c1d99f0cdc2a1a110c33ba94c5e2df699ea3cebbab1ea4 
```

![](https://user-gold-cdn.xitu.io/2020/1/7/16f7dc10591816b5?w=1254&h=438&f=png&s=88253)

说明：

- token
    - 可以通过安装 master 时的日志查看 token 信息
    - 可以通过 kubeadm token list 命令打印出 token 信息
    - 如果 token 过期，可以使用 kubeadm token create 命令创建新的 token
- discovery-token-ca-cert-hash
    - 可以通过安装 master 时的日志查看 sha256 信息
    - 可以通过 openssl x509 -pubkey -in /etc/kubernetes/pki/ca.crt | openssl rsa -pubin -outform der 2>/dev/null | openssl dgst -sha256 -hex | sed 's/^.* //' 命令查看 sha256 信息
    
验证是否成功

回到 master 服务器

```
kubectl get nodes
```

![](https://user-gold-cdn.xitu.io/2020/1/7/16f7dc40079a8269?w=1002&h=162&f=png&s=29457)

查看 pod 状态

```
 kubectl get pod -n kube-system -o wide
```


![](https://user-gold-cdn.xitu.io/2020/1/7/16f7dda4d2f91dbc?w=1487&h=211&f=png&s=44231)

由此可以看出 coredns 尚未运行，此时我们还需要安装网络插件。

## 配置网络
容器网络是容器选择连接到其他容器、主机和外部网络的机制。容器的 runtime 提供了各种网络模式，每种模式都会产生不同的体验。例如，Docker 默认情况下可以为容器配置以下网络：

- none： 将容器添加到一个容器专门的网络堆栈中，没有对外连接。
- host： 将容器添加到主机的网络堆栈中，没有隔离。
- default bridge： 默认网络模式。每个容器可以通过 IP 地址相互连接。
- 自定义网桥： 用户定义的网桥，具有更多的灵活性、隔离性和其他便利功能。

## 什么是 CNI
CNI(Container Network Interface) 是一个标准的，通用的接口。在容器平台，Docker，Kubernetes，Mesos 容器网络解决方案 flannel，calico，weave。只要提供一个标准的接口，就能为同样满足该协议的所有容器平台提供网络功能，而 CNI 正是这样的一个标准接口协议。

## Kubernetes 中的 CNI 插件

CNI 的初衷是创建一个框架，用于在配置或销毁容器时动态配置适当的网络配置和资源。插件负责为接口配置和管理 IP 地址，并且通常提供与 IP 管理、每个容器的 IP 分配、以及多主机连接相关的功能。容器运行时会调用网络插件，从而在容器启动时分配 IP 地址并配置网络，并在删除容器时再次调用它以清理这些资源。

运行时或协调器决定了容器应该加入哪个网络以及它需要调用哪个插件。然后，插件会将接口添加到容器网络命名空间中，作为一个 veth 对的一侧。接着，它会在主机上进行更改，包括将 veth 的其他部分连接到网桥。再之后，它会通过调用单独的 IPAM（IP地址管理）插件来分配 IP 地址并设置路由。

在 Kubernetes 中，kubelet 可以在适当的时间调用它找到的插件，为通过 kubelet 启动的 pod进行自动的网络配置。

Kubernetes 中可选的 CNI 插件如下：
- Flannel
- Calico
- Canal
- Weave-  
## 什么是 Calico

Calico 为容器和虚拟机提供了安全的网络连接解决方案，并经过了大规模生产验证（在公有云和跨数千个集群节点中），可与 Kubernetes，OpenShift，Docker，Mesos，DC / OS 和 OpenStack 集成。

Calico 还提供网络安全规则的动态实施。使用 Calico 的简单策略语言，您可以实现对容器，虚拟机工作负载和裸机主机端点之间通信的细粒度控制。

## 安装网络插件 Calico
参考官方文档安装：https://docs.projectcalico.org/v3.7/getting-started/kubernetes/


```
# 在 Master 节点操作即可
kubectl apply -f https://docs.projectcalico.org/v3.11/manifests/calico.yaml

# 安装时显示如下输出
configmap/calico-config created
customresourcedefinition.apiextensions.k8s.io/felixconfigurations.crd.projectcalico.org created
customresourcedefinition.apiextensions.k8s.io/ipamblocks.crd.projectcalico.org created
customresourcedefinition.apiextensions.k8s.io/blockaffinities.crd.projectcalico.org created
customresourcedefinition.apiextensions.k8s.io/ipamhandles.crd.projectcalico.org created
customresourcedefinition.apiextensions.k8s.io/ipamconfigs.crd.projectcalico.org created
customresourcedefinition.apiextensions.k8s.io/bgppeers.crd.projectcalico.org created
customresourcedefinition.apiextensions.k8s.io/bgpconfigurations.crd.projectcalico.org created
customresourcedefinition.apiextensions.k8s.io/ippools.crd.projectcalico.org created
customresourcedefinition.apiextensions.k8s.io/hostendpoints.crd.projectcalico.org created
customresourcedefinition.apiextensions.k8s.io/clusterinformations.crd.projectcalico.org created
customresourcedefinition.apiextensions.k8s.io/globalnetworkpolicies.crd.projectcalico.org created
customresourcedefinition.apiextensions.k8s.io/globalnetworksets.crd.projectcalico.org created
customresourcedefinition.apiextensions.k8s.io/networkpolicies.crd.projectcalico.org created
customresourcedefinition.apiextensions.k8s.io/networksets.crd.projectcalico.org created
clusterrole.rbac.authorization.k8s.io/calico-kube-controllers created
clusterrolebinding.rbac.authorization.k8s.io/calico-kube-controllers created
clusterrole.rbac.authorization.k8s.io/calico-node created
clusterrolebinding.rbac.authorization.k8s.io/calico-node created
daemonset.extensions/calico-node created
serviceaccount/calico-node created
deployment.extensions/calico-kube-controllers created
serviceaccount/calico-kube-controllers created
```

## 确认安装是否成功

```
watch kubectl get pods --all-namespaces

# 需要等待所有状态为 Running，注意时间可能较久，3 - 5 分钟的样子
Every 2.0s: kubectl get pods --all-namespaces                                                                                                    kubernetes-master: Fri May 10 18:16:51 2019

NAMESPACE     NAME                                        READY   STATUS    RESTARTS   AGE
kube-system   calico-kube-controllers-8646dd497f-g2lln    1/1     Running   0          50m
kube-system   calico-node-8jrtp                           1/1     Running   0          50m
kube-system   coredns-8686dcc4fd-mhwfn                    1/1     Running   0          51m
kube-system   coredns-8686dcc4fd-xsxwk                    1/1     Running   0          51m
kube-system   etcd-kubernetes-master                      1/1     Running   0          50m
kube-system   kube-apiserver-kubernetes-master            1/1     Running   0          51m
kube-system   kube-controller-manager-kubernetes-master   1/1     Running   0          51m
kube-system   kube-proxy-p8mdw                            1/1     Running   0          51m
kube-system   kube-scheduler-kubernetes-master     
```

### 解决 ImagePullBackOff
在使用 watch kubectl get pods --all-namespaces 命令观察 Pods 状态时如果出现 ImagePullBackOff 无法 Running 的情况，请尝试使用如下步骤处理：

- Master 中删除 Nodes：kubectl delete nodes <NAME>
- Slave 中重置配置：kubeadm reset
- Slave 重启计算机：reboot
- Slave 重新加入集群：kubeadm join


## 完成搭建

![](https://user-gold-cdn.xitu.io/2020/1/7/16f7e9be1220df11?w=787&h=243&f=png&s=38492)

![](https://user-gold-cdn.xitu.io/2020/1/7/16f7e9c3eb012818?w=1147&h=402&f=png&s=55146)


### 检查组件运行状态

```
kubectl get cs

# 输出如下
NAME                 STATUS    MESSAGE             ERROR
# 调度服务，主要作用是将 POD 调度到 Node
scheduler            Healthy   ok                  
# 自动化修复服务，主要作用是 Node 宕机后自动修复 Node 回到正常的工作状态
controller-manager   Healthy   ok                  
# 服务注册与发现
etcd-0               Healthy   {"health":"true"} 
```
### 检查 Master 状态

```
kubectl cluster-info
#输出如下
Kubernetes master is running at https://192.168.62.159:6443
KubeDNS is running at https://192.168.62.159:6443/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy

To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.
```
## 运行第一个容器实例

```
# 使用 kubectl 命令创建两个监听 80 端口的 Nginx Pod（Kubernetes 运行容器的最小单元）
kubectl run nginx --image=nginx --replicas=2 --port=80

# 输出如下
kubectl run --generator=deployment/apps.v1 is DEPRECATED and will be removed in a future version. Use kubectl run --generator=run-pod/v1 or kubectl create instead.
deployment.apps/nginx created
```

查看全部 Pods 的状态

```
kubectl get pods

nginx-5578584966-f7vl5   0/1     ContainerCreating   0          55s
nginx-5578584966-p9s24   0/1     ContainerCreating   0          55s


nginx-5578584966-f7vl5   1/1     Running   0          18m
nginx-5578584966-p9s24   1/1     Running   0          18m

```

查看已部署的服务

```
kubectl get deployment

# 输出如下
NAME    READY   UP-TO-DATE   AVAILABLE   AGE
nginx   2/2     2            2           91m
```
映射服务，让用户可以访问

```
kubectl expose deployment nginx --port=80 --type=LoadBalancer

# 输出如下
service/nginx exposed
```

查看已发布的服务
```
kubectl get services
NAME         TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)        AGE
kubernetes   ClusterIP      10.96.0.1      <none>        443/TCP        21h
nginx        LoadBalancer   10.96.133.79   <pending>     80:31091/TCP   13m
```

查看服务详情

```
kubectl describe service nginx


Name:                     nginx
Namespace:                default
Labels:                   run=nginx
Annotations:              <none>
Selector:                 run=nginx
Type:                     LoadBalancer
IP:                       10.96.133.79
Port:                     <unset>  80/TCP
TargetPort:               80/TCP
NodePort:                 <unset>  31091/TCP
Endpoints:                192.168.17.1:80,192.168.8.130:80
Session Affinity:         None
External Traffic Policy:  Cluster
Events:                   <none>
```
验证是否成功
通过浏览器访问 Master 服务器


```
http://192.168.62.159:30830/
```

![](https://user-gold-cdn.xitu.io/2020/1/7/16f7ee4b50e19fb0?w=1570&h=502&f=png&s=60072)

停止服务

```
kubectl delete deployment nginx

# 输出如下
deployment.extensions "nginx" deleted
```


```
kubectl delete service nginx

# 输出如下
service "nginx" deleted
```


## 结尾
目前是已经把K8s集群搭建完成了 并且我们用它部署了nginx,先到这里把
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
