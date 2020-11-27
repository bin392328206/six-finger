# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   

## 叨絮
今天在自己工程中使用@Async的时候，碰到了一个问题：Spring循环依赖（circular reference）问题。给大家看看小六六的代码
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9e74e6249ce2404085c634e25328273f~tplv-k3u1fbpfcp-watermark.image)
小六六在代码中用了很多的构造注入，但是这个优缺点，他没法解决循环依赖的问题，所以刚开始我还没意思到是Async引起的，还以为是构造注入的原因，然后我就一直在改，改完了之后，我特么发现我还是不能启动项目，难道我要回滚，因为此时我写了很多的代码了，我并不知道是哪一步引起的循环依赖，擦 心态爆炸，只能硬着头皮慢慢回退，最后搞了老久才发现是
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/bbb2ae7c4861494e912feb55fd46d817~tplv-k3u1fbpfcp-watermark.image)

他导致的，唉，可能还是太年轻，一个中午吃饭休息的时间都在找，然后小六六就把这个问题记录一下，为了下次不犯错了。

## 前因

或许刚说到这，有的小伙伴就会大惊失色了。Spring不是解决了循环依赖问题吗，它是支持循环依赖的呀？怎么会呢？

不可否认，在这之前我也是这么坚信的，而且每次使用得也屡试不爽。倘若你目前也和我有一样坚挺的想法，那么相信本文能让你大有收货~~。

我通过一中午的时间总结出，出现使用@Async导致循环依赖问题的必要条件：
- 已开启@EnableAsync的支持
- @Async注解所在的Bean被循环依赖了

我们在开发中一般A依赖B B依赖A，这种很正常，也没办法避免，大部分业务是没有规避的，当然你说你自己搞另外一个类去写，但是这样其实也不是很好的解决问题了，所以在crud中，肯定会碰到的。


## 背景

若你是一个有经验的程序员，那你在开发中必然碰到过这种现象：事务不生效。

可以看我前面的文章
- [在Spring事务管理下，Synchronized为啥还线程不安全？](https://juejin.im/post/5ddc7a23e51d452331202721)

就是因为动态代理的特性，有的时候很多情况下 所以我们会自己注入自己，我们才能得到我们想要的结果，我们来看看下面的例子

本文场景的背景也一样，我想调用本类的异步方法（标注有@Async注解），很显然我知道为了让于@Async生效，我把自己依赖进来，然后通过service接口来调用，代码如下：

```
@Service
public class HelloServiceImpl implements HelloService {
    @Autowired
    private HelloService helloService;
    
    @Override
    public Object hello(Integer id) {
        System.out.println("线程名称：" + Thread.currentThread().getName());
        helloService.fun1(); // 使用接口方式调用，而不是this
        return "service hello";
    }

    @Async
    @Override
    public void fun1() {
        System.out.println("线程名称：" + Thread.currentThread().getName());
    }
}

```
此种做法首先是Spring中一个典型的循环依赖场景：自己依赖自己。本以为能够像解决事务不生效问题一样依旧屡试不爽，但没想到非常的不给面子，启动即报错：

```
org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'helloServiceImpl': Bean with name 'helloServiceImpl' has been injected into other beans [helloServiceImpl] in its raw version as part of a circular reference, but has eventually been wrapped. This means that said other beans do not use the final version of the bean. This is often the result of over-eager type matching - consider using 'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:622)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:515)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:320)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:222)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:318)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199)
	...

```

这里说明一下，为什么有小伙伴跟我说：我使用@Async即使本类方法调用也从来木有遇到这个错误啊？难道它不常见？
为此经过我的一番调查，包括看一些同事、小伙伴的代码发现：并不是使用@Async没有启动报错，而是他本类调用的时候直接调用的方法，这样@Async是不生效的但小伙伴却全然不知而已。

至于@Async没生效这种问题为何没报出来？？？甚至过了很久很久都没人发现和关注？？
其实道理很简单，它和事务不生效不一样，@Async若没生效99%情况下都不会影响到业务的正常进行，因为它不会影响数据正确性，只会影响到性能（无非就是异步变同步呗，这是兼容的）。



## 方案

我们知道事务不生效和@Async不生效的根本原因都是同一个：直接调用了本类方法而非接口方法/代理对象方法。
解决这类不生效问题的方案一般我们都有两种：

- 自己注入自己，然后再调用接口方法（当然此处的一个变种是使用编程方式形如：AInterface a = applicationContext.getBean(AInterface.class);这样子手动获取也是可行的~~~本文不讨论这种比较直接简单的方式）
- 使用AopContext.currentProxy();方式


本文就讲解采取方式一自己注入自己的方案解决带来了更多问题，使用AopContext.currentProxy();方式会在紧邻的下篇博文里详解~

>注意：自己注入自己是能够完美解决事务不生效问题。如题，本文旨在讲解解决@Async的问题~~~

有的小伙伴肯定会说：让不调用本类的@Async方法不就可以了；让不产生循环依赖不就可以了；这都是解决方案啊~
其实你说的没毛病，但我我想说：理想的设计当然是不建议循环依赖的。但在真实的业务开发中循环依赖是100%避免不了的，同样本类方法的互调也同样是避免不了的~


## 自己依赖自己方案带来的问题分析

自己依赖自己这种方式是一种典型的使用循环依赖方式来解决问题，大多数情况下它是一个非常好的解决方案。
比如本例若要解决@Async本类调用问题，我们的代码会这么来写：

```
@Service
public class HelloServiceImpl implements HelloService {

    @Autowired
    private HelloService helloService;

    @Transactional
    @Override
    public Object hello(Integer id) {
        System.out.println("线程名称：" + Thread.currentThread().getName());
        // fun1(); // 这样书写@Async肯定不生效~
        helloService.fun1(); //调用接口方法
        return "service hello";
    }

    @Async
    @Override
    public void fun1() {
        System.out.println("线程名称：" + Thread.currentThread().getName());
    }
}

```

本以为像解决事务问题一样，像这样写是肯定完美解决问题的。但奈何带来了新问题，启动即报错：

```
报错信息如上~~~
```

BeanCurrentlyInCreationException这个异常类型小伙伴们应该并不陌生，在循环依赖那篇文章中（请参阅相关阅读）有讲述到：文章里有提醒小伙伴们关注报错的日志，有朝一日肯定会碰面，没想到来得这么快~

```
创建名为“helloServiceImpl”的bean时出错：名为“helloServiceImpl”的bean已作为循环引用的一部分注入到其原始版本中的其他bean[helloServiceImpl]中，
**但最终已被包装**。这意味着其他bean不使用bean的最终版本。

```

### 问题定位

本着先定位问题才能解决问题的原则，找到问题的根本原因成为了我现在最需要做的事。从报错信息的描述可以看出，根本原因是helloServiceImpl最终被包装(代理)，所以被使用的bean并不是最终的版本，所以Spring的自检机制报错了~~~

> 说明：Spring管理的Bean都是单例的，所以Spring默认需要保证所有使用此Bean的地方都指向的是同一个地址，也就是最终版本的Bean，否则可能就乱套了，Spring也提供了这样的自检机制~


上面文字叙述有点苍白，相信小伙伴们看着也是一脸懵逼、二脸继续懵逼吧。下面通过示例代码分析看看结果。

为了更好的说明问题，此处不用自己依赖自己来表述（因为名字相同容易混淆不方便说明问题），而以下面A、B两个类的形式说明：


```
@Service
public class A implements AInterface {
    @Autowired
    private BInterface b;
    @Async
    @Override
    public void funA() {
    }
}

@Service
public class B implements BInterface {
    @Autowired
    private AInterface a;
    @Override
    public void funB() {
        a.funA();
    }
}

```
如上示例代码启动时会报错：（示例代码模仿成功）


```
org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'a': Bean with name 'a' has been injected into other beans [b] in its raw version as part of a circular reference, but has eventually been wrapped. This means that said other beans do not use the final version of the bean. This is often the result of over-eager type matching - consider using 'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:622)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:515)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:320)
	...

```

下面是重点，来跟踪一下源码，定位此问题：

```
protected Object doCreateBean( ... ){
	...
	boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences && isSingletonCurrentlyInCreation(beanName));
	if (earlySingletonExposure) {
		addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
	}
	...

	// populateBean这一句特别的关键，它需要给A的属性赋值，所以此处会去实例化B~~
	// 而B我们从上可以看到它就是个普通的Bean（并不需要创建代理对象），实例化完成之后，继续给他的属性A赋值，而此时它会去拿到A的早期引用
	// 也就在此处在给B的属性a赋值的时候，会执行到上面放进去的Bean A流程中的getEarlyBeanReference()方法  从而拿到A的早期引用~~
	// 执行A的getEarlyBeanReference()方法的时候，会执行自动代理创建器，但是由于A没有标注事务，所以最终不会创建代理，so B合格属性引用会是A的**原始对象**
	// 需要注意的是：@Async的代理对象不是在getEarlyBeanReference()中创建的，是在postProcessAfterInitialization创建的代理
	// 从这我们也可以看出@Async的代理它默认并不支持你去循环引用，因为它并没有把代理对象的早期引用提供出来~~~（注意这点和自动代理创建器的区别~）

	// 结论：此处给A的依赖属性字段B赋值为了B的实例(因为B不需要创建代理，所以就是原始对象)
	// 而此处实例B里面依赖的A注入的仍旧为Bean A的普通实例对象（注意  是原始对象非代理对象）  注：此时exposedObject也依旧为原始对象
	populateBean(beanName, mbd, instanceWrapper);
	
	// 标注有@Async的Bean的代理对象在此处会被生成~~~ 参照类：AsyncAnnotationBeanPostProcessor
	// 所以此句执行完成后  exposedObject就会是个代理对象而非原始对象了
	exposedObject = initializeBean(beanName, exposedObject, mbd);
	
	...
	// 这里是报错的重点~~~
	if (earlySingletonExposure) {
		// 上面说了A被B循环依赖进去了，所以此时A是被放进了二级缓存的，所以此处earlySingletonReference 是A的原始对象的引用
		// （这也就解释了为何我说：如果A没有被循环依赖，是不会报错不会有问题的   因为若没有循环依赖earlySingletonReference =null后面就直接return了）
		Object earlySingletonReference = getSingleton(beanName, false);
		if (earlySingletonReference != null) {
			// 上面分析了exposedObject 是被@Aysnc代理过的对象， 而bean是原始对象 所以此处不相等  走else逻辑
			if (exposedObject == bean) {
				exposedObject = earlySingletonReference;
			}
			// allowRawInjectionDespiteWrapping 标注是否允许此Bean的原始类型被注入到其它Bean里面，即使自己最终会被包装（代理）
			// 默认是false表示不允许，如果改为true表示允许，就不会报错啦。这是我们后面讲的决方案的其中一个方案~~~
			// 另外dependentBeanMap记录着每个Bean它所依赖的Bean的Map~~~~
			else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
				// 我们的Bean A依赖于B，so此处值为["b"]
				String[] dependentBeans = getDependentBeans(beanName);
				Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);

				// 对所有的依赖进行一一检查~	比如此处B就会有问题
				// “b”它经过removeSingletonIfCreatedForTypeCheckOnly最终返返回false  因为alreadyCreated里面已经有它了表示B已经完全创建完成了~~~
				// 而b都完成了，所以属性a也赋值完成儿聊 但是B里面引用的a和主流程我这个A竟然不相等，那肯定就有问题(说明不是最终的)~~~
				// so最终会被加入到actualDependentBeans里面去，表示A真正的依赖~~~
				for (String dependentBean : dependentBeans) {
					if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
						actualDependentBeans.add(dependentBean);
					}
				}
	
				// 若存在这种真正的依赖，那就报错了~~~  则个异常就是上面看到的异常信息
				if (!actualDependentBeans.isEmpty()) {
					throw new BeanCurrentlyInCreationException(beanName,
							"Bean with name '" + beanName + "' has been injected into other beans [" +
							StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
							"] in its raw version as part of a circular reference, but has eventually been " +
							"wrapped. This means that said other beans do not use the final version of the " +
							"bean. This is often the result of over-eager type matching - consider using " +
							"'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.");
				}
			}
		}
	}
	...
}

```

这里知识点避开不@Aysnc注解标注的Bean的创建代理的时机。
@EnableAsync开启时它会向容器内注入AsyncAnnotationBeanPostProcessor，它是一个BeanPostProcessor，实现了postProcessAfterInitialization方法。此处我们看代码，创建代理的动作在抽象父类AbstractAdvisingBeanPostProcessor上：


```
// @since 3.2   注意：@EnableAsync在Spring3.1后出现
// 继承自ProxyProcessorSupport，所以具有动态代理相关属性~ 方便创建代理对象
public abstract class AbstractAdvisingBeanPostProcessor extends ProxyProcessorSupport implements BeanPostProcessor {

	// 这里会缓存所有被处理的Bean~~~  eligible：合适的
	private final Map<Class<?>, Boolean> eligibleBeans = new ConcurrentHashMap<>(256);

	//postProcessBeforeInitialization方法什么不做~
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}

	// 关键是这里。当Bean初始化完成后这里会执行，这里会决策看看要不要对此Bean创建代理对象再返回~~~
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		if (this.advisor == null || bean instanceof AopInfrastructureBean) {
			// Ignore AOP infrastructure such as scoped proxies.
			return bean;
		}

		// 如果此Bean已经被代理了（比如已经被事务那边给代理了~~）
		if (bean instanceof Advised) {
			Advised advised = (Advised) bean;
		
			// 此处拿的是AopUtils.getTargetClass(bean)目标对象，做最终的判断
			// isEligible()是否合适的判断方法  是本文最重要的一个方法，下文解释~
			// 此处还有个小细节：isFrozen为false也就是还没被冻结的时候，就只向里面添加一个切面接口   并不要自己再创建代理对象了  省事
			if (!advised.isFrozen() && isEligible(AopUtils.getTargetClass(bean))) {
				// Add our local Advisor to the existing proxy's Advisor chain...
				// beforeExistingAdvisors决定这该advisor最先执行还是最后执行
				// 此处的advisor为：AsyncAnnotationAdvisor  它切入Class和Method标注有@Aysnc注解的地方~~~
				if (this.beforeExistingAdvisors) {
					advised.addAdvisor(0, this.advisor);
				} else {
					advised.addAdvisor(this.advisor);
				}
				return bean;
			}
		}

		// 若不是代理对象，此处就要下手了~~~~isEligible() 这个方法特别重要
		if (isEligible(bean, beanName)) {
			// copy属性  proxyFactory.copyFrom(this); 生成一个新的ProxyFactory 
			ProxyFactory proxyFactory = prepareProxyFactory(bean, beanName);
			// 如果没有强制采用CGLIB 去探测它的接口~
			if (!proxyFactory.isProxyTargetClass()) {
				evaluateProxyInterfaces(bean.getClass(), proxyFactory);
			}
			// 添加进此切面~~ 最终为它创建一个getProxy 代理对象
			proxyFactory.addAdvisor(this.advisor);
			//customize交给子类复写（实际子类目前都没有复写~）
			customizeProxyFactory(proxyFactory);
			return proxyFactory.getProxy(getProxyClassLoader());
		}

		// No proxy needed.
		return bean;
	}
	
	// 我们发现BeanName最终其实是没有用到的~~~
	// 但是子类AbstractBeanFactoryAwareAdvisingPostProcessor是用到了的  没有做什么 可以忽略~~~
	protected boolean isEligible(Object bean, String beanName) {
		return isEligible(bean.getClass());
	}
	protected boolean isEligible(Class<?> targetClass) {
		// 首次进来eligible的值肯定为null~~~
		Boolean eligible = this.eligibleBeans.get(targetClass);
		if (eligible != null) {
			return eligible;
		}
		// 如果根本就没有配置advisor  也就不用看了~
		if (this.advisor == null) {
			return false;
		}
		
		// 最关键的就是canApply这个方法，如果AsyncAnnotationAdvisor  能切进它  那这里就是true
		// 本例中方法标注有@Aysnc注解，所以铁定是能被切入的  返回true继续上面方法体的内容
		eligible = AopUtils.canApply(this.advisor, targetClass);
		this.eligibleBeans.put(targetClass, eligible);
		return eligible;
	}
	...
}

```

经此一役，根本原理是只要能被切面AsyncAnnotationAdvisor切入（即只需要类/方法有标注@Async注解即可）的Bean最终都会生成一个代理对象（若已经是代理对象里，只需要加入该切面即可了~）赋值给上面的exposedObject作为返回最终add进Spring容器内~



针对上面的步骤，为了辅助理解，我尝试总结文字描述如下：

- context.getBean(A)开始创建A，A实例化完成后给A的依赖属性b开始赋值~
- context.getBean(B)开始创建B，B实例化完成后给B的依赖属性a开始赋值~
- 重点：此时因为A支持循环依赖，所以会执行A的getEarlyBeanReference方法得到它的早期引用。而执行getEarlyBeanReference()的时候因为@Async根本还没执行，所以最终返回的仍旧是原始对象的地址
- B完成初始化、完成属性的赋值，此时属性field持有的是Bean A原始类型的引用~
- 完成了A的属性的赋值（此时已持有B的实例的引用），继续执行初始化方法initializeBean(...)，在此处会解析@Aysnc注解，从而生成一个代理对象，所以最终exposedObject是一个代理对象（而非原始对象）最终加入到容器里~
- 尴尬场面出现了：B引用的属性A是个原始对象，而此处准备return的实例A竟然是个代理对象，也就是说B引用的并非是最终对象（不是最终放进容器里的对象）
- 执行自检程序：由于allowRawInjectionDespiteWrapping默认值是false，表示不允许上面不一致的情况发生，so最终就抛错了~


## 解决方案

通过上面分析，知道了问题的根本原因，现总结出解决上述新问题的解决方案，可分为下面三种方案：

- 把allowRawInjectionDespiteWrapping设置为true
- 使用@Lazy或者@ComponentScan(lazyInit = true)解决
- 不要让@Async的Bean参与循环依赖


### 1、把allowRawInjectionDespiteWrapping设置为true：

```
@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ((AbstractAutowireCapableBeanFactory) beanFactory).setAllowRawInjectionDespiteWrapping(true);
    }
}

```

这样配置后，容器启动将不再报错了，但是但是但是：Bean A的@Aysnc方法将不起作用了，因为Bean B里面依赖的a是个原始对象，所以它最终没法执行异步操作（即使容器内的a是个代理对象）：

这种解决方式一方面没有达到真正的目的（毕竟Bean A上的@Aysnc没有生效）。

由于它只对循环依赖内的Bean受影响，所以影响范围并不是全局，因此当找不到更好办法的时候，此种这样也不失是一个不错的方案，所以我个人对此方案的态度是不建议，也不反对。


### 2、使用@Lazy或者@ComponentScan(lazyInit = true)解决

本处以使用@Lazy为例：（强烈不建议使用@ComponentScan(lazyInit = true)作用范围太广了，容易产生误伤）

```
@Service
public class B implements BInterface {
    @Lazy
    @Autowired
    private AInterface a;

    @Override
    public void funB() {
        System.out.println("线程名称：" + Thread.currentThread().getName());
        a.funA();
    }
}

```

注意此@Lazy注解加的位置，因为a最终会是@Async的代理对象，所以在@Autowired它的地方加
另外，若不存在循环依赖而是直接引用a，是不用加@Lazy的

需要在Bean b的依赖属性上加上@Lazy即可。（因为是B希望依赖进来的是最终的代理对象进来，所以B加上即可，A上并不需要加）

最终的结果让人满意：启动正常，并且@Async异步效果也生效了，因此本方案我是推荐的


### 3、不要让@Async的Bean参与循环依赖
显然如果方案3如果能够解决它肯定是最优的方案。奈何它却是现实情况中最为难达到的方案。
因为在实际业务开发中像循环依赖、类内方法调用等情况并不能避免，除非重新设计、按规范改变代码结构，因此此种方案就见仁见智吧~



### 为何@Transactional即使循环依赖也没有问题呢？
最后回答小伙伴给我提问的这个问题：同为创建动态代理对象，同为一个注解标注在类上 / 方法上，为何@Transactional就不会出现这种启动报错呢？

虽说他俩的原理都是产生代理对象，且注解的使用方式几乎无异。so区别Spring对它哥俩的解析不同，也就是他们代理的创建的方式不同：

- @Transactional使用的是自动代理创建器AbstractAutoProxyCreator，上篇文章详细描述了，它实现了getEarlyBeanReference()方法从而很好的对循环依赖提供了支持
- @Async的代理创建使用的是AsyncAnnotationBeanPostProcessor单独的后置处理器实现的，它只在一处postProcessAfterInitialization()实现了对代理对象的创建，因此若出现它被循环依赖了，就会报错如上~~~
so，虽然从表象上看这两个注解的实现方式一样，但细咬其实现过程细节上，两者差异性还是非常明显的。了解了实现方式上的差异后，自然就不难理解为何有报错和有不报错了~

前面正确的解法
```
@Service
public class A implements AInterface{
    @Autowired
    private BInterface b;
    @Override
    public void funA() {
    }
}

@Service
public class B implements BInterface {
    @Autowired
    private AInterface a;
    @Async // 写在B的方法上  这样B最终会被创建代理对象
    @Override
    public void funB() {
        a.funA();
    }
}

```

## 结尾
碰到了就记录一下，嘻嘻。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
