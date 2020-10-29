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
- [适合初中级Java程序员修炼手册从0搭建整个Web项目（四）](https://juejin.im/post/6885994038278193165)

我来总结一下前面我们已经完成的，我们已经完成了基于Netty的Http服务器，和springmvc spring ioc相关的功能，今天呢？我们尝试着来写写spring的aop

## 总的结构
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4836f545d42a41b49025b7d1a5a74010~tplv-k3u1fbpfcp-watermark.image)

上面就是写完之后的结构，我们下面来一一查看我们的过程

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3cbb417b6aaa41d3823a6cf5b19dc94c~tplv-k3u1fbpfcp-watermark.image)

其实这边小六六遇到了一些问题，但是没能解决，就先这样了吧。


# JoinPoint

通知方法的其中一个入参就是<kbd>JoinPoint</kbd>，通过它我们可以拿到当前被代理的对象、方法、参数等，甚至可以设置一个参数用于上下文传递，从接口方法就能判断出来了。
```
package com.xiaoliuliu.six.finger.web.spring.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/26 16:26
 *通知方法的其中一个入参就是<kbd>JoinPoint</kbd>，通过它我们可以拿到当前被代理的对象、方法、参数等，甚至可以设置一个参数用于上下文传递，从接口方法就能判断出来了。
 */
public interface JoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
```

它的实现类就是前言中提到的外层拦截器对象，负责执行整个拦截器链，主要逻辑是：先遍历执行完拦截器链，最后才执行被代理的方法。这其实就是责任链模式的实现。
```
package com.xiaoliuliu.six.finger.web.spring.aop.intercept;

import com.xiaoliuliu.six.finger.web.spring.aop.aspect.JoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/26 17:10
 */
public class MethodInvocation implements JoinPoint {

    /**
     *  @author: linliangkun
     *  @Date: 2020/10/26 17:12
     *  @Description: 被代理对象
     */
    private Object proxy;

    /**被代理的对象*/
    private Object target;

    /**被代理对象的class*/
    private Class<?> targetClass;


    /**被代理的方法*/
    private Method method;

    /**被代理的方法的入参*/
    private Object [] arguments;

    /**拦截器链*/
    private List<Object> interceptorsAndDynamicMethodMatchers;

    /**用户参数*/
    private Map<String, Object> userAttributes;

    /**记录当前拦截器执行的位置*/
    private int currentInterceptorIndex = -1;

    public MethodInvocation(Object proxy,
                            Object target,
                            Method method,
                            Object[] arguments,
                            Class<?> targetClass,
                            List<Object> interceptorsAndDynamicMethodMatchers) {

        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap<>();
            }
            this.userAttributes.put(key, value);
        }
        else {
            if (this.userAttributes != null) {
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }

    public Object proceed() throws Throwable{
        //拦截器执行完了，最后真正执行被代理的方法
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return this.method.invoke(this.target,this.arguments);
        }

        //获取一个拦截器
        Object interceptorOrInterceptionAdvice =
                this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        if (interceptorOrInterceptionAdvice instanceof MethodInterceptor) {
            MethodInterceptor mi = (MethodInterceptor) interceptorOrInterceptionAdvice;
            //执行通知方法
            return mi.invoke(this);
        } else {
            //跳过，调用下一个拦截器
            return proceed();
        }
    }
}

```

# MethodInterceptor
新建拦截器<kbd>MethodInterceptor</kbd>接口

```
package com.xiaoliuliu.six.finger.web.spring.aop.intercept;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/26 17:19
 */
public interface MethodInterceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;

}

```

# Advice
在实现拦截器<kbd>MethodInterceptor</kbd>的子类前，先新建<kbd>Advice</kbd>，作为不同通知方法的顶层接口。

```
package com.xiaoliuliu.six.finger.web.spring.aop.aspect;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/26 17:22
 * 在实现拦截器<kbd>MethodInterceptor</kbd>的子类前，先新建<kbd>Advice</kbd>，作为不同通知方法的顶层接口。
 */
public interface Advice {
}

```

接着写一个抽象子类来封装不同的通知类型的共同逻辑：调用通知方法前先将入参赋值，这样用户在写通知方法时才能拿到实际值。
```
package com.xiaoliuliu.six.finger.web.spring.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/26 17:23
 */
public abstract class AbstractAspectAdvice implements Advice{
    /**通知方法*/
    private Method aspectMethod;

    /**切面类*/
    private Object aspectTarget;

    public AbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    /**
     * 调用通知方法
     */
    public Object invokeAdviceMethod(JoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable {
        Class<?>[] paramTypes = this.aspectMethod.getParameterTypes();
        if (null == paramTypes || paramTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i] == JoinPoint.class) {
                    args[i] = joinPoint;
                } else if (paramTypes[i] == Throwable.class) {
                    args[i] = tx;
                } else if (paramTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget, args);
        }
    }
}

```

# 实现多种通知类型
拦截器本质上就是各种通知方法的封装，因此继承<kbd>AbstractAspectAdvice</kbd>，实现<kbd>MethodInterceptor</kbd>。下面分别实现前置通知、后置通知和异常通知。

**前置通知**

```
package com.xiaoliuliu.six.finger.web.spring.aop.aspect;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/26 17:37
 */

import com.xiaoliuliu.six.finger.web.spring.aop.intercept.MethodInterceptor;
import com.xiaoliuliu.six.finger.web.spring.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 前置通知
 */
public class MethodBeforeAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {

    private JoinPoint joinPoint;

    public MethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method, Object[] args, Object target) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint, null, null);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.joinPoint = mi;
        //在调用下一个拦截器前先执行前置通知
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }

}
```

**后置通知**


```
package com.xiaoliuliu.six.finger.web.spring.aop.aspect;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/26 17:40
 */

import com.xiaoliuliu.six.finger.web.spring.aop.intercept.MethodInterceptor;
import com.xiaoliuliu.six.finger.web.spring.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 后置通知
 */
public class AfterReturningAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {

    private JoinPoint joinPoint;

    public AfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        //先调用下一个拦截器
        Object retVal = mi.proceed();
        //再调用后置通知
        this.joinPoint = mi;
        this.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint, retVal, null);
    }
}

```

**异常通知**

```
package com.xiaoliuliu.six.finger.web.spring.aop.aspect;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/26 17:40
 */

import com.xiaoliuliu.six.finger.web.spring.aop.intercept.MethodInterceptor;
import com.xiaoliuliu.six.finger.web.spring.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 异常通知
 */
public class AfterThrowingAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {


    private String throwingName;

    public AfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            //直接调用下一个拦截器，如果不出现异常就不调用异常通知
            return mi.proceed();
        } catch (Throwable e) {
            //异常捕捉中调用通知方法
            invokeAdviceMethod(mi, null, e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName) {
        this.throwingName = throwName;
    }
}
```

# AopProxy
新建创建代理的顶层接口<kbd>AopProxy</kbd>

```
package com.xiaoliuliu.six.finger.web.spring.aop;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/26 17:41
 */
public interface AopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}

```


Spring选择代理创建逻辑是，如果被代理的类有实现接口用原生JDK的动态代理，否则使用Cglib的动态代理。所以<kbd>AopProxy</kbd>有两个子类<kbd>JdkDynamicAopProxy</kbd>和<kbd>CglibAopProxy</kbd>来实现这两种创建逻辑。

<kbd>JdkDynamicAopProxy</kbd>是利用JDK动态代理来创建代理的，因此需实现<kbd>InvocationHandler</kbd>接口。


```
package com.xiaoliuliu.six.finger.web.spring.aop;

import com.xiaoliuliu.six.finger.web.spring.aop.intercept.MethodInvocation;
import com.xiaoliuliu.six.finger.web.spring.aop.support.AdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/26 17:42
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    private AdvisedSupport advised;

    public JdkDynamicAopProxy(AdvisedSupport config) {
        this.advised = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        //JDK的动态代理方式
        return Proxy.newProxyInstance(classLoader, this.advised.getTargetClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //获取拦截器链
        List<Object> interceptorsAndDynamicMethodMatchers =
                this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, this.advised.getTargetClass());
        //外层拦截器，用于控制拦截器链的执行
        MethodInvocation invocation = new MethodInvocation(
                proxy,
                this.advised.getTarget(),
                method,
                args,
                this.advised.getTargetClass(),
                interceptorsAndDynamicMethodMatchers
        );
        //开始连接器链的调用
        return invocation.proceed();
    }
}


```

还又我们的cglib也是经常用的

```
package com.xiaoliuliu.six.finger.web.spring.aop;

import com.xiaoliuliu.six.finger.web.spring.aop.intercept.MethodInvocation;
import com.xiaoliuliu.six.finger.web.spring.aop.support.AdvisedSupport;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/12 11:03
 * cglib的动态代理
 */
public class CglibAopProxy implements AopProxy, MethodInterceptor {
    private AdvisedSupport advised;

    public CglibAopProxy(AdvisedSupport config) {
        this.advised = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {

        //cglib的方式
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.advised.getTargetClass());
        enhancer.setCallback(this);
        return  enhancer.create();
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        //获取拦截器链
        List<Object> interceptorsAndDynamicMethodMatchers =
                this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, this.advised.getTargetClass());
        //外层拦截器，用于控制拦截器链的执行
        MethodInvocation invocation = new MethodInvocation(
                proxy,
                this.advised.getTarget(),
                method,
                args,
                this.advised.getTargetClass(),
                interceptorsAndDynamicMethodMatchers
        );
        //开始连接器链的调用
        return invocation.proceed();
    }
}

```

成员变量<kbd>AdvisedSupport</kbd>封装了创建代理所需要的一切资源，从上面的代码就可以看出它至少封装了被代理的目标实例、拦截器链等，实际上它还负责解析AOP配置和创建拦截器。

```
package com.xiaoliuliu.six.finger.web.spring.aop.support;

import com.xiaoliuliu.six.finger.web.spring.aop.aspect.AfterReturningAdviceInterceptor;
import com.xiaoliuliu.six.finger.web.spring.aop.aspect.AfterThrowingAdviceInterceptor;
import com.xiaoliuliu.six.finger.web.spring.aop.aspect.MethodBeforeAdviceInterceptor;
import com.xiaoliuliu.six.finger.web.spring.aop.config.AopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/26 17:43
 */
public class AdvisedSupport {

    /**被代理的类class*/
    private Class<?> targetClass;

    /**被代理的对象实力*/
    private Object target;

    /**被代理的方法对应的拦截器集合*/
    private Map<Method, List<Object>> methodCache;

    /**AOP外部配置*/
    private AopConfig config;

    /**切点正则表达式*/
    private Pattern pointCutClassPattern;

    public AdvisedSupport(AopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public Object getTarget() {
        return this.target;
    }

    /**
     * 获取拦截器
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {
        List<Object> cached = methodCache.get(method);
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m, cached);
        }

        return cached;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    /**
     * 解析AOP配置，创建拦截器
     */
    private void parse() {
        //编译切点表达式为正则
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        //pointCut=public .* com.lqb.demo.service..*Service..*(.*)
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));

        try {
            //保存切面的所有通知方法
            Map<String, Method> aspectMethods = new HashMap<>();
            Class aspectClass = Class.forName(this.config.getAspectClass());
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(), m);
            }

            //遍历被代理类的所有方法，为符合切点表达式的方法创建拦截器
            methodCache = new HashMap<>();
            Pattern pattern = Pattern.compile(pointCut);
            for (Method m : this.targetClass.getMethods()) {
                String methodString = m.toString();
                //为了能正确匹配这里去除函数签名尾部的throws xxxException
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pattern.matcher(methodString);
                if (matcher.matches()) {
                    //执行器链
                    List<Object> advices = new LinkedList<>();

                    //创建前置拦截器
                    if (!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        //创建一个Advivce
                        MethodBeforeAdviceInterceptor beforeAdvice = new MethodBeforeAdviceInterceptor(
                                aspectMethods.get(config.getAspectBefore()),
                                aspectClass.newInstance()
                        );
                        advices.add(beforeAdvice);
                    }
                    //创建后置拦截器
                    if (!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        AfterReturningAdviceInterceptor returningAdvice = new AfterReturningAdviceInterceptor(
                                aspectMethods.get(config.getAspectAfter()),
                                aspectClass.newInstance()
                        );
                        advices.add(returningAdvice);
                    }
                    //创建异常拦截器
                    if (!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        AfterThrowingAdviceInterceptor throwingAdvice = new AfterThrowingAdviceInterceptor(
                                aspectMethods.get(config.getAspectAfterThrow()),
                                aspectClass.newInstance()
                        );
                        throwingAdvice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }

                    //保存被代理方法和执行器链的对应关系
                    methodCache.put(m, advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * 判断一个类是否需要被代理
     */
    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
```

<kbd>AopConfig</kbd>保存了配置好切面、切点以及通知。
```
package com.xiaoliuliu.six.finger.web.spring.aop.config;

import lombok.Data;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/10/26 17:43
 */
@Data
public class AopConfig {

    //切点表达式
    private String pointCut;

    //前置通知方法
    private String aspectBefore;

    //后置通知方法
    private String aspectAfter;

    //切面类
    private String aspectClass;

    //异常通知方法
    private String aspectAfterThrow;

    //抛出的异常类型
    private String aspectAfterThrowingName;

}
```

## 测试

至于测试的话就是像前面一样，改造一下

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e5316f9164924e3190a0e4ec35b364d2~tplv-k3u1fbpfcp-watermark.image)

注入aop就好了。


### 遇到得问题


其实这边小六六有一个坑，但是自己没搞定，是这样的哈

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7266e02724d344a3a59a0e2a505a266a~tplv-k3u1fbpfcp-watermark.image)

我这个测试类的时候，我们要注入的这个UserServiceImpl 应该是代理对象，但是我们通过动态代理之后生成的代理对象一直是报错的。![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1706bd9fb1184bdf9a991d7a277ac132~tplv-k3u1fbpfcp-watermark.image)

但是他的拦截器链是成了。但是aop代理对象一直报错。
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c63121c34b9845ef83ffdbcbe0399e49~tplv-k3u1fbpfcp-watermark.image)

所以那个注入就是一直报空了，老郁闷了，不懂为啥。


## 结尾

其实spring的aop 说到底就是动态代理+我们各种前中后处理器（责任链模式的实现）。虽然没有完全做出来但是对于spring的aop还是有点帮助的，大家可以参考下。


## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！