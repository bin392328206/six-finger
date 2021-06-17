# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger

## 絮叨
小六六平时开发的时候一般用到的是idea,然后目前为止，也积累了不好好用的插件，这边就打算自己写篇文章记录一下，等下次换idea的时候，能让自己快速找回这些插件，这篇文章小六六会一直更新的，除非自己不用idea了，哈哈


##  Translation


一款翻译插件，集成在idea中，可以不用切换窗口到浏览器或其他软件中搜索，且可以根据驼峰命名规则对应的变量名或方法名，在定义方法名或属性变量时很方便

1、安装

在idea中settings-->plugins，搜索Translation回车，然后点击installed（安装），然后等待下载安装好后重启idea即可。

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e5d28e42099b4051815fbdc3afd1dc0a~tplv-k3u1fbpfcp-zoom-1.image)



## MyBatis Log Plugin
Mybatis现在是java中操作数据库的首选，在开发的时候，我们都会把Mybatis的脚本直接输出在console中，但是默认的情况下，输出的脚本不是一个可以直接执行的。

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ee2a9e7fa6c942db9224cbdefe672e6c~tplv-k3u1fbpfcp-zoom-1.image)

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b318d91f3fd24b97bc18e158a35e90ff~tplv-k3u1fbpfcp-zoom-1.image)



## Lombok

Java语言，每次写实体类的时候都需要写一大堆的setter，getter，如果bean中的属性一旦有修改、删除或增加时，需要重新生成或删除get/set等方法，给代码维护增加负担，这也是Java被诟病的一种原因。Lombok则为我们解决了这些问题，使用了lombok的注解(@Setter,@Getter,@ToString,@@RequiredArgsConstructor,@EqualsAndHashCode或@Data)之后，就不需要编写或生成get/set等方法，很大程度上减少了代码量，而且减少了代码维护的负担。

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/12d60b41463d45eb8d5e6fd8431b3f90~tplv-k3u1fbpfcp-zoom-1.image)
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/44fe766adac54726939804aaf16cb614~tplv-k3u1fbpfcp-zoom-1.image)


## POJOTOJSON

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/514ea19b1ab249709199ba1f9bac74af~tplv-k3u1fbpfcp-zoom-1.image)

这个其实怎么说呢，就比如我们做postMan测试的时候，需要JSON格式，如果自己一个个去写得花好多时间呢？然后用它就能更快的生成JSON了。


## MyBatisCodeHelper-Pro

1、mapper文件（即表对应的dao）与xml文件自由切换，方便代码评审；
2、自动代码生成功能提高开发效率，mysql数据库创建好表结构，写完 pojo（注意字段类型要统一用对象类型！）,即可生成 xml、mapper、service ；
3、mapper的命名规则比较统一，可提高代码风格一致性；


![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8ad73a799c48445cab08bd6912ff0cdb~tplv-k3u1fbpfcp-zoom-1.image)



## Codota
Codota基于数百万个Java程序和您的上下文来完成代码行，从而帮助您以更少的错误更快地进行编码
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4fb11d91d6d94b29a604c1259a33ec0d~tplv-k3u1fbpfcp-zoom-1.image)
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/18f5692caa7a4a6b8256750ebd39346a~tplv-k3u1fbpfcp-zoom-1.image)


## 阿里巴巴代码规约扫描
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/eb8998afd02f4fa185e473eb3abd9274~tplv-k3u1fbpfcp-zoom-1.image)

注意：阿里编码规约扫描，默认是开启实时监测的，此功能可能会引起idea卡顿，可以点击 关闭实时检测功能 将其关闭，在编码完成后再主动扫描文件

## Stack Overflow
不用介绍都知道的东西，程序员最喜欢去寻找答案的论坛之一，有插件哦，方便不
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/26cf6c718c4a48689c7ea68b5cae6f5e~tplv-k3u1fbpfcp-zoom-1.image)

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9e34c314e3b0494eb2449313f762075d~tplv-k3u1fbpfcp-zoom-1.image)


## Restfultookit
用来定位restful风格的controller，是不是比ctrl+shift+f，找controller要方便多了。
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a5eae47a9d8245258feda57472e5c97d~tplv-k3u1fbpfcp-zoom-1.image)

## Maven辅助神器：Maven Helper
帮忙解决依赖问题

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/42265213ddc64b289b2566a85a3640ef~tplv-k3u1fbpfcp-zoom-1.image)


## 时序图生成工具 SequenceDiagram

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f1727c8392a945d2b0dbc9c6955e4c97~tplv-k3u1fbpfcp-zoom-1.image)

##  CamelCase

驼峰式大小写切换插件。

可以通过快捷键在 CamelCase, camelCase, snake_case and SNAKE_CASE 之间快速切换。

默认快捷键：ctrl + shift + u



## 结尾

喜欢的赶快用上吧，美滋滋的哦


## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！


