# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206                          
> **种一棵树最好的时间是十年前，其次是现在**   


# six-finger-web
一个Web后端框架的轮子从处理Http请求【基于Netty的请求级Web服务器】 到mvc【接口封装转发)】，再到ioc【依赖注入】，aop【切面】，再到 rpc【远程过程调用】最后到orm【数据库操作】全部自己撸一个（简易）的轮子。

[github](https://github.com/bin392328206/six-finger-web)

## 为啥要写这个轮子
其实是这样的，小六六自己平时呢？有时候喜欢看看人家的源码比如Spring,但是小六六的水平可能不怎么样，每次看都看得晕头转向，然后就感觉里面的细节太难了，然后我就只能观其总体的思想，然后我就想我如果可以根据各位前辈的一些思考，自己撸一个简单的轮子出来，那我后面去理解作者的思想是不是简单点呢？于是呢 six-finger-web就面世了，它其实就是我的一个学习过程，然后我把它开源出来，希望能帮助那些对于学习源码有困难的同学。还有就是可以锻炼一下自己的编码能力，因为平时我们总是crud用的Java api都是那些，久而久之，很多框架类的api我们根本就不熟练了，所以借此机会，锻炼一下。

## 特点
- 内置由 Netty 编写 HTTP 服务器，无需额外依赖 Tomcat 之类的 web 服务（刚好小六六把Netty系列写完，顺便用下）
- 代码简单易懂（小六六自己写不出框架大佬那种高类聚，低耦合的代码），能力稍微强一点看代码就能懂，弱点的也没关系，小六六有配套的从0搭建教程。
- 支持MVC相关的注解确保和SpringMVC的用法类似
- 支持Spring IOC 和Aop相关功能
- 支持类似于Mybatis相关功能
- 支持类似于Dubbo的rpc相关功能
- 对于数据返回，只支持Json格式


## 絮叨
前面是已经写好的章节，下面我给大家来一一走一遍搭建流程
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（一）](https://juejin.im/post/6883284588110544904)
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（二）](https://juejin.im/post/6884027512343494669)
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（三）](https://juejin.im/post/6885224199938867213)

上次我们完成了基于Spring注解方式的ioc的注入，今天我们来看看简单的xml的实现，来学习一下xml的流程,只是最简单的了解一下xml流程，并没有多深入的去写里面的细节！


### 首先我们来看看包结构，来看看新增的东西

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6e7b0e6ebcf34ed9b9b42a74de1913da~tplv-k3u1fbpfcp-watermark.image)

其实这节，就很简单了，就是增加几个类去解析xml文件，我也是用的人家的包，dom4j这个专门解析xml的。所以说其实也没有多少东西是我们写的，我们就是理解他的流程就好了，然后可以更好的去理解我们spring的源码，

## 来看看效果，和测试类

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/20f9969086cc4e43810bc7309bcd84fd~tplv-k3u1fbpfcp-watermark.image)

其实很简单，我就是在请求里面加入了这类，然后打印出来这个类，就好了
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/cdb51baaf92b426f939f06fbdfddf268~tplv-k3u1fbpfcp-watermark.image)

测试的后台打印

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b612ba5f6fa64486ad1335a474dd4006~tplv-k3u1fbpfcp-watermark.image)

## 来看看改造的过程。

### DefaultApplicationContext
DefaultApplicationContext->refresh()
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a58d5db1467e418cb4fc9d5824f2e10c~tplv-k3u1fbpfcp-watermark.image)
其实就是这个地方，加载了xml的bean

### XmlBeanDefinitionReader

```
package com.xiaoliuliu.six.finger.web.spring.ioc.beans.support;

import com.xiaoliuliu.six.finger.web.spring.ioc.beans.BeanDefinition;
import com.xiaoliuliu.six.finger.web.spring.ioc.exception.BeanDefinitionStoreException;
import com.xiaoliuliu.six.finger.web.spring.ioc.io.Resource;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/21 15:11
 * 解析xml文件 把它变成数据结构
 */
public class XmlBeanDefinitionReader {
    public static final String ID_ATTRIBUTE = "id";

    public static final String CLASS_ATTRIBUTE = "class";


    public List<BeanDefinition> loadBeanDefinitions(Resource resource) {
        InputStream is = null;
        List<BeanDefinition> result = new ArrayList<>();
        try {
            is = resource.getInputStream();
            SAXReader reader = new SAXReader();
            Document doc = reader.read(is);

            Element root = doc.getRootElement(); //<beans>
            Iterator<Element> iter = root.elementIterator();
            while (iter.hasNext()) {
                Element ele = (Element) iter.next();
                String id = ele.attributeValue(ID_ATTRIBUTE);
                String beanClassName = ele.attributeValue(CLASS_ATTRIBUTE);
                BeanDefinition bd = new BeanDefinition(beanClassName, id);
                result.add(bd);
            }
            return result;
        } catch (Exception e) {
            throw new BeanDefinitionStoreException("IOException parsing XML document from " + resource.getDescription(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}

```

### Resource

```
package com.xiaoliuliu.six.finger.web.spring.ioc.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/21 15:18
 */
public interface Resource {
    public InputStream getInputStream() throws IOException;
    public String getDescription();
}


```


### ClassPathResource

```
package com.xiaoliuliu.six.finger.web.spring.ioc.io;

import com.xiaoliuliu.six.finger.web.spring.ioc.util.ClassUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/21 15:32
 */
public class ClassPathResource implements Resource {

    private String path;
    private ClassLoader classLoader;

    public ClassPathResource(String path) {
        this(path, (ClassLoader) null);
    }
    public ClassPathResource(String path, ClassLoader classLoader) {
        this.path = path;
        this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = this.classLoader.getResourceAsStream(this.path);

        if (is == null) {
            throw new FileNotFoundException(path + " cannot be opened");
        }
        return is;

    }
    @Override
    public String getDescription(){
        return this.path;
    }

}

```

## 结尾
好了，总的来说，这篇文章其实也没什么，其实就是把xml变成beandifinition 这个是核心，不管是基于注解的，还是基于xml最终的形态是相同的。下章我们就来看看aop吧！fighting吧！


## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！