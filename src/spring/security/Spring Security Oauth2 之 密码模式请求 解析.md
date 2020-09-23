# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   

## 絮叨 
刚刚碰到了，就把这个流程梳理一下呗


## 本文介绍的认证流程范围

本文主要对从用户发起获取token的请求（/oauth/token），到请求结束返回token中间经过的几个关键点进行说明。

## 认证会用到的相关请求
### 获取access_token请求（/oauth/token）
- 请求所需参数：client_id、client_secret、grant_type、username、password
```
http://localhost/oauth/token?client_id=demoClientId&client_secret=demoClientSecret&grant_type=password&username=demoUser&password=50575tyL86xp29O380t1
```

### 检查头肯是否有效请求（/oauth/check_token）

- 请求所需参数：token
```
http://localhost/oauth/check_token?token=f57ce129-2d4d-4bd7-1111-f31ccc69d4d1
```

### 刷新token请求（/oauth/token）
请求所需参数：grant_type、refresh_token、client_id、client_secret
其中grant_type为固定值：grant_type=refresh_token
```
http://localhost/oauth/token?grant_type=refresh_token&refresh_token=fbde81ee-f419-42b1-1234-9191f1f95be9&client_id=demoClientId&client_secret=demoClientSecret
```


### 工作原理

#### 结构总览

Spring Security所解决的问题就是安全访问控制，而安全访问控制功能其实就是对所有进入系统的请求进行拦截， 校验每个请求是否能够访问它所期望的资源。我们可以通过Filter或AOP等技术来实现，Spring Security对Web资源的保护是靠Filter实现的，所以从这个Filter来入手，逐步深入Spring Security原理。 


当初始化Spring Security时，会创建一个名为 SpringSecurityFilterChain 的Servlet过滤器，类型为 org.springframework.security.web.FilterChainProxy，它实现了javax.servlet.Filter，因此外部的请求会经过此 类，下图是Spring Security过虑器链结构图：

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2ad9fc599bcf4ba18b23c0bca5bac066~tplv-k3u1fbpfcp-zoom-1.image) 
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9df2479eb2f84ecd9f85c3ec3e31f741~tplv-k3u1fbpfcp-zoom-1.image)

FilterChainProxy是一个代理，真正起作用的是FilterChainProxy中SecurityFilterChain所包含的各个Filter，同时 这些Filter作为Bean被Spring管理，它们是Spring Security核心，各有各的职责，但他们并不直接处理用户的认 证，也不直接处理用户的授权，而是把它们交给了认证管理器（AuthenticationManager）和决策管理器 （AccessDecisionManager）进行处理，下图是FilterChainProxy相关类的UML图示。

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/fe66b3122ad0455590b178dfdbac7442~tplv-k3u1fbpfcp-zoom-1.image)

spring Security功能的实现主要是由一系列过滤器链相互配合完成。

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a5e12d3d62fa4f59a20338fe58aa60ec~tplv-k3u1fbpfcp-zoom-1.image)

下面介绍过滤器链中主要的几个过滤器及其作用


- SecurityContextPersistenceFilter 这个Filter是整个拦截过程的入口和出口（也就是第一个和最后一个拦截 器），会在请求开始时从配置好的 SecurityContextRepository 中获取 SecurityContext，然后把它设置给 SecurityContextHolder。在请求完成后将 SecurityContextHolder 持有的 SecurityContext 再保存到配置好 的 SecurityContextRepository，同时清除 securityContextHolder 所持有的 SecurityContext； 
- UsernamePasswordAuthenticationFilter 用于处理来自表单提交的认证。该表单必须提供对应的用户名和密 码，其内部还有登录成功或失败后进行处理的 AuthenticationSuccessHandler 和 AuthenticationFailureHandler，这些都可以根据需求做相关改变； 
- FilterSecurityInterceptor 是用于保护web资源的，使用AccessDecisionManager对当前用户进行授权访问，前 面已经详细介绍过了；
- ExceptionTranslationFilter 能够捕获来自 FilterChain 所有的异常，并进行处理。但是它只会处理两类异常： AuthenticationException 和 AccessDeniedException，其它的异常它会继续抛出。

### 认证流程
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/53011d1e787e41008faeb97ad395d0d7~tplv-k3u1fbpfcp-zoom-1.image)

让我们仔细分析认证过程：


- 1. 用户提交用户名、密码被SecurityFilterChain中的 UsernamePasswordAuthenticationFilter 过滤器获取到， 封装为请求Authentication，通常情况下是UsernamePasswordAuthenticationToken这个实现类。
- 2. 然后过滤器将Authentication提交至认证管理器（AuthenticationManager）进行认证 
- 3. 认证成功后， AuthenticationManager 身份管理器返回一个被填充满了信息的（包括上面提到的权限信息， 身份信息，细节信息，但密码通常会被移除） Authentication 实例。 
- 4. SecurityContextHolder 安全上下文容器将第3步填充了信息的 Authentication ，通过 SecurityContextHolder.getContext().setAuthentication(…)方法，设置到其中。 可以看出AuthenticationManager接口（认证管理器）是认证相关的核心接口，也是发起认证的出发点，它 的实现类为ProviderManager。而Spring Security支持多种认证方式，因此ProviderManager维护着一个 List<AuthenticationProvider> 列表，存放多种认证方式，最终实际的认证工作是由 AuthenticationProvider完成的。咱们知道web表单的对应的AuthenticationProvider实现类为 DaoAuthenticationProvider，它的内部又维护着一个UserDetailsService负责UserDetails的获取。最终 AuthenticationProvider将UserDetails填充至Authentication。 认证核心组件的大体关系如下：
![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5277a2dce2d7482382b5195bd910dc1f~tplv-k3u1fbpfcp-zoom-1.image)
#### AuthenticationProvider
  
  通过前面的Spring Security认证流程我们得知，认证管理器（AuthenticationManager）委托 AuthenticationProvider完成认证工作。 AuthenticationProvider是一个接口，定义如下
  
```
  public interface AuthenticationProvider { Authentication authenticate(Authentication authentication) throws AuthenticationException; boolean supports(Class<?> var1); }
```
authenticate()方法定义了认证的实现过程，它的参数是一个Authentication，里面包含了登录用户所提交的用 户、密码等。而返回值也是一个Authentication，这个Authentication则是在认证成功后，将用户的权限及其他信 息重新组装后生成。

Spring Security中维护着一个 List<AuthenticationProvider> 列表，存放多种认证方式，不同的认证方式使用不 同的AuthenticationProvider。如使用用户名密码登录时，使用AuthenticationProvider1，短信登录时使用 AuthenticationProvider2等等这样的例子很多。


每个AuthenticationProvider需要实现supports（）方法来表明自己支持的认证方式，如我们使用表单方式认证， 在提交请求时Spring Security会生成UsernamePasswordAuthenticationToken，它是一个Authentication，里面 封装着用户提交的用户名、密码信息。而对应的，哪个AuthenticationProvider来处理它？

我们在DaoAuthenticationProvider的基类AbstractUserDetailsAuthenticationProvider发现以下代码：

```
public boolean supports(Class<?> authentication) { return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication); }
```
  
  也就是说当web表单提交用户名密码时，Spring Security由DaoAuthenticationProvider处理。
  
  
  最后，我们来看一下Authentication(认证信息)的结构，它是一个接口，我们之前提到的 UsernamePasswordAuthenticationToken就是它的实现之一：
  
```
  public interface Authentication extends Principal, Serializable { （1） Collection<? extends GrantedAuthority> getAuthorities(); （2） Object getCredentials(); （3） Object getDetails(); （4） Object getPrincipal(); （5） boolean isAuthenticated(); void setAuthenticated(boolean var1) throws IllegalArgumentException; }
```


- （1）Authentication是spring security包中的接口，直接继承自Principal类，而Principal是位于 java.security 包中的。它是表示着一个抽象主体身份，任何主体都有一个名称，因此包含一个getName()方法。 

- （2）getAuthorities()，权限信息列表，默认是GrantedAuthority接口的一些实现类，通常是代表权限信息的一系 列字符串。 
- （3）getCredentials()，凭证信息，用户输入的密码字符串，在认证过后通常会被移除，用于保障安全。 
- （4）getDetails()，细节信息，web应用中的实现接口通常为 WebAuthenticationDetails，它记录了访问者的ip地 址和sessionId的值。 
- （5）getPrincipal()，身份信息，大部分情况下返回的是UserDetails接口的实现类，UserDetails代表用户的详细 信息，那从Authentication中取出来的UserDetails就是当前登录用户信息，它也是框架中的常用接口之一。


### UserDetailsService
#### 认识UserDetailsService

现在咱们现在知道DaoAuthenticationProvider处理了web表单的认证逻辑，认证成功后既得到一个 Authentication(UsernamePasswordAuthenticationToken实现)，里面包含了身份信息（Principal）。这个身份 信息就是一个 Object ，大多数情况下它可以被强转为UserDetails对象。


DaoAuthenticationProvider中包含了一个UserDetailsService实例，它负责根据用户名提取用户信息 UserDetails(包含密码)，而后DaoAuthenticationProvider会去对比UserDetailsService提取的用户密码与用户提交 的密码是否匹配作为认证成功的关键依据，因此可以通过将自定义的 UserDetailsService 公开为spring bean来定 义自定义身份验证

```
public interface UserDetailsService { UserDetails loadUserByUsername(String username) throws UsernameNotFoundException; }
```

很多人把DaoAuthenticationProvider和UserDetailsService的职责搞混淆，其实UserDetailsService只负责从特定 的地方（通常是数据库）加载用户信息，仅此而已。而DaoAuthenticationProvider的职责更大，它完成完整的认 证流程，同时会把UserDetails填充至Authentication。


上面一直提到UserDetails是用户信息，咱们看一下它的真面目：

```
public interface UserDetails extends Serializable { Collection<? extends GrantedAuthority> getAuthorities(); String getPassword(); String getUsername(); boolean isAccountNonExpired(); boolean isAccountNonLocked(); boolean isCredentialsNonExpired(); boolean isEnabled(); }

```

它和Authentication接口很类似，比如它们都拥有username，authorities。Authentication的getCredentials()与 UserDetails中的getPassword()需要被区分对待，前者是用户提交的密码凭证，后者是用户实际存储的密码，认证 其实就是对这两者的比对。Authentication中的getAuthorities()实际是由UserDetails的getAuthorities()传递而形 成的。还记得Authentication接口中的getDetails()方法吗？其中的UserDetails用户详细信息便是经过了 AuthenticationProvider认证之后被填充的。

通过实现UserDetailsService和UserDetails，我们可以完成对用户信息获取方式以及用户信息字段的扩展。


Spring Security提供的InMemoryUserDetailsManager(内存认证)，JdbcUserDetailsManager(jdbc认证)就是 UserDetailsService的实现类，主要区别无非就是从内存还是从数据库加载用户


## 获取token的主要流程

- 用户发起获取token的请求。
- 过滤器会验证path是否是认证的请求/oauth/token，如果为false，则直接返回没有后续操作。
- 过滤器通过clientId查询生成一个Authentication对象。
- 然后会通过username和生成的Authentication对象生成一个UserDetails对象，并检查用户是否存在。
- 以上全部通过会进入地址/oauth/token，即TokenEndpoint的postAccessToken方法中。
- postAccessToken方法中会验证Scope，然后验证是否是refreshToken请求等。
- 之后调用AbstractTokenGranter中的grant方法。
- grant方法中调用AbstractUserDetailsAuthenticationProvider的authenticate方法，通过username和Authentication对象来检索用户是否存在。
- 然后通过DefaultTokenServices类从tokenStore中获取OAuth2AccessToken对象。
- 然后将OAuth2AccessToken对象包装进响应流返回。


### 代码截图梳理流程

- 解析 客户端凭证，值的格式为Basic空格 + client_id:client_secret经过Base64加密后的值
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4c6916b2e7334a798b3c2de76bec4c98~tplv-k3u1fbpfcp-zoom-1.image)

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/775f1fdb89534a87a5d9a4e0c21bec85~tplv-k3u1fbpfcp-zoom-1.image)

- ①一个比较重要的过滤器
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/cba8b3f0f54b4196b9c99793f2b90a10~tplv-k3u1fbpfcp-zoom-1.image)

- ②此处是①中的attemptAuthentication方法

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b83cc5d9163f4144ad53b2b626e7d1bd~tplv-k3u1fbpfcp-zoom-1.image)

- ③此处是②中调用的authenticate方法

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/75522b2642f24d3099b8b20ec3e624de~tplv-k3u1fbpfcp-zoom-1.image)

- ④ 此处是③中调用的AbstractUserDetailsAuthenticationProvider类的authenticate方法
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f518a4dd14454b2c83c1bfe8ca46d6d9~tplv-k3u1fbpfcp-zoom-1.image)


- ⑤ 此处是④中调用的DaoAuthenticationProvider类的retrieveUser方法
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/469750c334cb47f18c3a5dc44bb941ec~tplv-k3u1fbpfcp-zoom-1.image)

- ⑥此处为⑤中调用的ClientDetailsUserDetailsService类的loadUserByUsername方法，执行完后接着返回执行④之后的方法

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3ce9e21f10914239ba73ac30feb59b3b~tplv-k3u1fbpfcp-zoom-1.image)

- ⑦此处为④中调用的DaoAuthenticationProvider类的additionalAuthenticationChecks方法，此处执行完则主要过滤器执行完毕，后续会进入/oauth/token映射的方法。
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/37dbec4fe19948e4888e8fe81d5f2bbe~tplv-k3u1fbpfcp-zoom-1.image)


- ⑧此处进入/oauth/token映射的TokenEndpoint类的postAccessToken方法

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/60c24b8e0e264e0d8470c18751d761c3~tplv-k3u1fbpfcp-zoom-1.image)

- ⑨此处为⑧中调用的AbstractTokenGranter类的grant方法

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/23a12e1d091348aaacf8e015082b2e17~tplv-k3u1fbpfcp-zoom-1.image)

- ⑩此处为⑨中调用的ResourceOwnerPasswordTokenGranter类中的getOAuth2Authentication方法

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/22e12e63e37d4589bfbebdc5c7c631bf~tplv-k3u1fbpfcp-zoom-1.image)

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/87b2c0d6e6c047229dd80dea35f2f5e5~tplv-k3u1fbpfcp-zoom-1.image)

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2d085cc524744a3abe527187548957f9~tplv-k3u1fbpfcp-zoom-1.image)

我这边是自己重现了他 
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5d468e0d697a4b4881e7cc46c00162a9~tplv-k3u1fbpfcp-zoom-1.image)

- ⑩① 此处为⑩中调用的自定义的CustomUserAuthenticationProvider类中的authenticate方法，此处校验用户密码是否正确，此处执行完则返回⑨执行后续方法。

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9adc039787ca437fa9b63bd72f38c5d9~tplv-k3u1fbpfcp-zoom-1.image)

- ⑩② 此处为⑨中调用的DefaultTokenServices中的createAccessToken方法

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1f961b130e6548f0a5b171a792a22242~tplv-k3u1fbpfcp-zoom-1.image)

- ⑩③此处为12中调用的RedisTokenStore中的getAccessToken方法等，此处执行完，则一直向上返回到⑧中执行后续方法。

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0be288d413074222ba7ba772b9dd1e9c~tplv-k3u1fbpfcp-zoom-1.image)

- ⑩④此处为⑧中获取到token后需要包装返回流操作

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/93c9f7846f6b4f2da4debd34b7a4571f~tplv-k3u1fbpfcp-zoom-1.image)


<h2 data-tool="mdnice编辑器" style="padding: 0px; font-weight: bold; color: black; font-size: 22px; line-height: 1.5em; margin-top: 2.2em; margin-bottom: 35px;"><span class="prefix" style="display: none;"></span><span class="content" style="display: inline-block; font-weight: bold; background: linear-gradient(#fff 60%, #ffb11b 40%); color: #515151; padding: 2px 13px 2px; margin-right: 3px; height: 50%;">日常求赞</span><span class="suffix"></span></h2>
<blockquote data-tool="mdnice编辑器" style="display: block; font-size: 0.9em; overflow: auto; overflow-scrolling: touch; border-left: 3px solid rgba(0, 0, 0, 0.4); color: #6a737d; padding-top: 10px; padding-bottom: 10px; padding-left: 20px; padding-right: 10px; margin-bottom: 20px; margin-top: 20px; border-left-color: #ffb11b; background: #fff5e3;">
<p style="font-size: 16px; padding-top: 8px; padding-bottom: 8px; padding: 0; margin: 0px; line-height: 26px; color: #595959;">好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是<strong style="font-weight: bold; color: black;">真粉</strong>。</p>
</blockquote>
<blockquote data-tool="mdnice编辑器" style="display: block; font-size: 0.9em; overflow: auto; overflow-scrolling: touch; border-left: 3px solid rgba(0, 0, 0, 0.4); color: #6a737d; padding-top: 10px; padding-bottom: 10px; padding-left: 20px; padding-right: 10px; margin-bottom: 20px; margin-top: 20px; border-left-color: #ffb11b; background: #fff5e3;">
<p style="font-size: 16px; padding-top: 8px; padding-bottom: 8px; padding: 0; margin: 0px; line-height: 26px; color: #595959;">创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见</p>
</blockquote>
<blockquote data-tool="mdnice编辑器" style="display: block; font-size: 0.9em; overflow: auto; overflow-scrolling: touch; border-left: 3px solid rgba(0, 0, 0, 0.4); color: #6a737d; padding-top: 10px; padding-bottom: 10px; padding-left: 20px; padding-right: 10px; margin-bottom: 20px; margin-top: 20px; border-left-color: #ffb11b; background: #fff5e3;">
<p style="font-size: 16px; padding-top: 8px; padding-bottom: 8px; padding: 0; margin: 0px; line-height: 26px; color: #595959;">六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！</p>
</blockquote>
