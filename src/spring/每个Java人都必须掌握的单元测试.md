# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨
最近再维护一个前人写的系统，然后就发现了一个很大的问题，就是单元测试没有写，真的是心态崩溃，很多时候自己都不敢去改代码，因为怕一改代码导致测试不到位，改多了bug,优化都不敢优化，只能说出现bug了，才不得不改他的时候去改改。对于一个好的产品，六六觉得代码的规范，可测试性，可拓展性，容错性真的很重要。到后期的优化，重构全靠一期 二期的基础，这些才是一个产品能长久不衰的本质，但是目前大部分产品就是先活下来，然后再考虑这个问题，确实也是，因为你活不下来，设计那么好，又有啥用呢？但是作为我们开发人员，可以自己多注意这些规范，说实话包括自己，以前碰到单元测试，我也不喜欢写，因为也许这个代码一期是你开发的，后面都不一定是你去接手了，不过小六六得改掉这个习惯，以后自己的接口，尽量写单元测试，算是立个flag吧。既然我用postMan，我为啥不用mockMvc,我想缺点可能是每次的启动时间有点长，如果项目很赶，可以放下，闲下来了，肯定得补的





## 使用junit测试业务层代码

![](https://user-gold-cdn.xitu.io/2020/4/10/17162f161de444a7?w=1128&h=519&f=png&s=52535)
上面是一个分布式头条练习项目，我们可以看到他是一个标准的分布式项目目录结构，
然后我们找到我们要测试的service 然后 我们右键点击他

![](https://user-gold-cdn.xitu.io/2020/4/10/17162f2b57292656?w=1114&h=821&f=png&s=87286)

然后你就可以直接创建你的测试类了

![](https://user-gold-cdn.xitu.io/2020/4/10/17162f333f5c49e9?w=817&h=606&f=png&s=40179)

记得填好你下面的2个注解，你就可以愉快的做单元测试了。
![](https://user-gold-cdn.xitu.io/2020/4/10/17162f380371c637?w=1064&h=385&f=png&s=36062)


## 使用mockmvc测试控制层代码

### 为何使用MockMvc
MockMvc实现了对Http请求的模拟，能够直接使用网络的形式，转换到Controller的调用，这样可以
使得测试速度快、不依赖网络环境，而且提供了一套验证的工具，这样可以使得请求的验证统一而
且很方便。
### 测试逻辑
- MockMvcBuilder构造MockMvc的构造器
- mockMvc调用perform，执行一个RequestBuilder请求，调用controller的业务处理逻辑
- perform返回ResultActions，返回操作结果，通过ResultActions，提供了统一的验证方式
- 使用StatusResultMatchers对请求结果进行验证
- 使用ContentResultMatchers对请求返回的内容进行验证

### 具体代码

```
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunitApplication.class)
@WebAppConfiguration
public class JunitTestControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @Before
    public void setUp()throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void helloJunit() throws Exception{

        /**
         * 1、mockMvc.perform执行一个请求。
         * 2、MockMvcRequestBuilders.get("XXX")构造一个请求。
         * 3、ResultActions.param添加请求传值
         * 4、ResultActions.accept(MediaType.TEXT_HTML_VALUE))设置返回类型
         * 5、ResultActions.andExpect添加执行完成后的断言。
         * 6、ResultActions.andDo添加一个结果处理器，表示要对结果做点什么事情
         *   比如此处使用MockMvcResultHandlers.print()输出整个响应结果信息。
         * 5、ResultActions.andReturn表示执行完成后返回相应的结果。
         */

        MvcResult mvcResult= mvc.perform(MockMvcRequestBuilders.get("/junit/helloJunit")
            // 参数传递
                .param("name","lvgang")
                .accept(MediaType.TEXT_HTML_VALUE))
                // .andExpect(MockMvcResultMatchers.status().isOk())             //等同于Assert.assertEquals(200,status);
                // .andExpect(MockMvcResultMatchers.content().string("hello lvgang"))    //等同于 Assert.assertEquals("hello lvgang",content);
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        int status=mvcResult.getResponse().getStatus();                 //得到返回代码
        String content=mvcResult.getResponse().getContentAsString();    //得到返回结果

        Assert.assertEquals(200,status);                        //断言，判断返回代码是否正确
        Assert.assertEquals("你好：Frank",content);            //断言，判断返回的值是否正确
    }
}

```

方法解析

- **perform：**执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
- **get:**声明发送一个get请求的方法。MockHttpServletRequestBuilder get(String urlTemplate, **Object…
- urlVariables)：**根据uri模板和uri变量值得到一个GET请求方式的。另外提供了其他的请求的方法，如：post、put、delete等。
- **param：**添加request的参数，如上面发送请求的时候带上了了pcode = - root的参数。假如使用需要发送json数据格式的时将不能使用这种方式，可见后面被@ResponseBody注解参数的解决方法
- **andExpect：**添加ResultMatcher验证规则，验证控制器执行完成后结果是否正确（对返回的数据进行的判断）；
- **andDo：**添加ResultHandler结果处理器，比如调试时打印结果到控制台（对返回的数据进行的判断）；
- **andReturn：**最后返回相应的MvcResult；然后进行自定义验证/进行下一步的异步处理（对返回的数据进行的判断）。


### MockMvcRequestBuilders常用API：

- MockHttpServletRequestBuilder get(String urlTemplate, Object... - urlVariables)：根据uri模板和uri变量值得到一个GET请求方式的MockHttpServletRequestBuilder；如get(/user/{id}, 1L)；

- MockHttpServletRequestBuilder post(String urlTemplate, Object... urlVariables)：同get类似，但是是POST方法；

- MockHttpServletRequestBuilder put(String urlTemplate, Object... urlVariables)：同get类似，但是是PUT方法；

- MockHttpServletRequestBuilder delete(String urlTemplate, Object... urlVariables) ：同get类似，但是是DELETE方法；

- MockHttpServletRequestBuilder options(String urlTemplate, Object... urlVariables)：同get类似，但是是OPTIONS方法；

- MockHttpServletRequestBuilder request(HttpMethod httpMethod, String urlTemplate, Object... urlVariables)： - 提供自己的Http请求方法及uri模板和uri变量，如上API都是委托给这个API；

- MockMultipartHttpServletRequestBuilder fileUpload(String urlTemplate, Object... - urlVariables)：提供文件上传方式的请求，得到MockMultipartHttpServletRequestBuilder；

- RequestBuilder asyncDispatch(final MvcResult - mvcResult)：创建一个从启动异步处理的请求的MvcResult进行异步分派的RequestBuilder；

### MockHttpServletRequestBuilder常用API：

- MockHttpServletRequestBuilder header(String name, Object... values)/MockHttpServletRequestBuilder - headers(HttpHeaders httpHeaders)：添加头信息；

- MockHttpServletRequestBuilder contentType(MediaType mediaType)：指定请求的contentType头信息；

- MockHttpServletRequestBuilder accept(MediaType... mediaTypes)/MockHttpServletRequestBuilder accept(String... - mediaTypes)：指定请求的Accept头信息；

- MockHttpServletRequestBuilder content(byte[] content)/MockHttpServletRequestBuilder content(String - content)：指定请求Body体内容；

- MockHttpServletRequestBuilder cookie(Cookie... cookies)：指定请求的Cookie；

- MockHttpServletRequestBuilder locale(Locale locale)：指定请求的Locale；

- MockHttpServletRequestBuilder characterEncoding(String encoding)：指定请求字符编码；

- MockHttpServletRequestBuilder requestAttr(String name, Object value) ：设置请求属性数据；

- MockHttpServletRequestBuilder sessionAttr(String name, Object value)/MockHttpServletRequestBuilder - sessionAttrs(Map<string, object=""> sessionAttributes)：设置请求session属性数据；

- MockHttpServletRequestBuilder flashAttr(String name, Object value)/MockHttpServletRequestBuilder - flashAttrs(Map<string, object=""> flashAttributes)：指定请求的flash信息，比如重定向后的属性信息；

- MockHttpServletRequestBuilder session(MockHttpSession session) ：指定请求的Session；

- MockHttpServletRequestBuilder principal(Principal principal) ：指定请求的Principal；

- MockHttpServletRequestBuilder contextPath(String contextPath) - ：指定请求的上下文路径，必须以“/”开头，且不能以“/”结尾；

- MockHttpServletRequestBuilder pathInfo(String pathInfo) ：请求的路径信息，必须以“/”开头；
 
- MockHttpServletRequestBuilder secure(boolean secure)：请求是否使用安全通道；

- MockHttpServletRequestBuilder with(RequestPostProcessor - postProcessor)：请求的后处理器，用于自定义一些请求处理的扩展点；

### MvcResult


- MvcResult为执行完控制器后得到的整个结果，并不仅仅是返回值，其包含了测试时需要的所有信息。

- MockHttpServletRequest getRequest()：得到执行的请求；
 
- MockHttpServletResponse getResponse()：得到执行后的响应；

- Object getHandler()：得到执行的处理器，一般就是控制器；

- HandlerInterceptor[] getInterceptors()：得到对处理器进行拦截的拦截器；

- ModelAndView getModelAndView()：得到执行后的ModelAndView；

- Exception getResolvedException()：得到HandlerExceptionResolver解析后的异常；

- FlashMap getFlashMap()：得到FlashMap；

- Object getAsyncResult()/Object getAsyncResult(long timeout)：得到异步执行的结果；

## 结尾

虽然说现在的业务千变万化，但是如果有时间还是多写写单元测试吧。更多的实战还是要碰到了才会更了解。


## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！


上面提到了 分布式头条项目 回复 888 获取 我敢说超过了大部分仔目前的技术范围维度。
![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)