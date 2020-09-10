# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨 
这个东西应该是我们撸业务最常用的组件之一了，因为之前小六六也是自己就是照着用，也没说全面的去了解一下这块，今天小六六就带大家一起来梳理梳理哈。


## JSR-303 简介
JSR-303 是 JavaEE 6 中的一项子规范，叫做 Bean Validation，官方参考实现是 Hibernate Validator。
此实现与 Hibernate ORM 没有任何关系。JSR-303 用于对 Java Bean 中的字段的值进行验证。 Spring MVC 3.x 之中也大力支持 JSR-303，可以在控制器中使用注解的方式对表单提交的数据方便地验证。
Spring 4.0 开始支持 Bean Validation 功能。
JSR-303 基本的校验规则

### 空检查
- @Null 验证对象是否为 null
- @NotNull 验证对象是否不为 null, 无法查检长度为 0 的字符串
- @NotBlank 检查约束字符串是不是 Null 还有被 Trim 的长度是否大于 0,只对字符串,且会去掉前后空格
- @NotEmpty 检查约束元素是否为 NULL 或者是 EMPTY

### 布尔检查
- @AssertTrue 验证 Boolean 对象是否为 true
- @AssertFalse 验证 Boolean 对象是否为 false

### 长度检查
@Size(min=, max=) 验证对象（Array, Collection , Map, String）长度是否在给定的范围之内
@Length(min=, max=) 验证字符串长度介于 min 和 max 之间

### 日期检查
- @Past 验证 Date 和 Calendar 对象是否在当前时间之前，验证成立的话被注释的元素一定是一个过去的日期
- @Future 验证 Date 和 Calendar 对象是否在当前时间之后 ，验证成立的话被注释的元素一定是一个将来的日期

### 正则检查
@Pattern 验证 String 对象是否符合正则表达式的规则，被注释的元素符合制定的正则表达式
	- regexp：正则表达式
	- flags：指定 Pattern.Flag 的数组，表示正则表达式的相关选项
    
### 数值检查

注意： 建议使用在 String ,Integer 类型，不建议使用在 int 类型上，因为表单值为 “” 时无法转换为 int，但可以转换为 String 为 “”，Integer 为 null

- @Min 验证 Number 和 String 对象是否大等于指定的值
- @Max 验证 Number 和 String 对象是否小等于指定的值
- @DecimalMax 被标注的值必须不大于约束中指定的最大值. 这个约束的参数是一个通过 BigDecimal定义的最大值的字符串表示 .小数 存在精度
- @DecimalMin 被标注的值必须不小于约束中指定的最小值. 这个约束的参数是一个通过 BigDecimal定义的最小值的字符串表示 .小数 存在精度
- @Digits 验证 Number 和 String 的构成是否合法
- @Digits(integer=,fraction=) 验证字符串是否是符合指定格式的数字，integer 指定整数精度，fraction 指定小数精度
- @Range(min=, max=) 被指定的元素必须在合适的范围内
- @Range(min=10000,max=50000,message=”range.bean.wage”)
- @Valid 递归的对关联对象进行校验, 如果关联对象是个集合或者数组，那么对其中的元素进行递归校验，如果是一个 map，则对其中的值部分进行校验.(是否进行递归验证)
- @CreditCardNumber 信用卡验证
- @Email 验证是否是邮件地址，如果为 null，不进行验证，算通过验证
- @ScriptAssert(lang= ,script=, alias=)
- @URL(protocol=,host=, port=,regexp=, flags=)


## 使用
至于使用的话，小六六这边就不一一的举例了，相信大家应该都会，可能平时没有那么详细的了解过，所以这次我就是好好给大家复习一下哈哈
```
@Data
public class User {
 
	/** id */
	@NotNull(message="id不能为空")
	private Long id;
	
	/** 姓名 */
	@NotBlank(message="姓名不能为空")
	private String name;
	
	/** 年龄 */
	@NotNull(message="年龄不能为空")
	@Max(message="年龄不能超过120岁", value = 120)
	@Min(message="年龄不能小于1岁", value = 1)
	private Integer age;
	
	/** 创建时间 */
	@Future
	private Date createTime;
}
 
```
然后在controller层里，使用@Valid就好了

```
 /**
	 * 校验不通过时直接抛异常
	 * @param user
	 * @return
	 */
	@PostMapping("/test1")
	public Object test1(@RequestBody @Valid User user) {
		return "操作成功！";
	}
```

## Spring Validation的3种执行校验方式

### 第一种：在Controller方法参数前加@Valid注解——校验不通过时直接抛异常

调用时会抛出一个org.springframework.web.bind.MethodArgumentNotValidException异常：

```
2019-04-21 11:35:28.600  WARN 10852 --- [nio-8080-exec-4] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for argument [0] in public java.lang.Object com.example.validation.UserController.test1(com.example.validation.User) with 3 errors: [Field error in object 'user' on field 'createTime': rejected value [Mon Dec 31 08:00:00 CST 2018]; codes [Future.user.createTime,Future.createTime,Future.java.util.Date,Future]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [user.createTime,createTime]; arguments []; default message [createTime]]; default message [需要是一个将来的时间]] [Field error in object 'user' on field 'age': rejected value [0]; codes [Min.user.age,Min.age,Min.java.lang.Integer,Min]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [user.age,age]; arguments []; default message [age],1]; default message [年龄不能小于1岁]] [Field error in object 'user' on field 'name': rejected value []; codes [NotBlank.user.name,NotBlank.name,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [user.name,name]; arguments []; default message [name]]; default message [姓名不能为空]] ]
```
其实像这种，我想大家肯定是要配合全局统一异常来处理，其实还是很好，目前我就是这么干的。推荐指数 3星


### 第二种：在Controller方法参数前加@Valid注解，参数后面定义一个BindingResult类型参数——执行时会将校验结果放进bindingResult里面，用户自行判断并处理
```
   /**
	 * 将校验结果放进BindingResult里面，用户自行判断并处理
	 * @param user
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("/test2")
	public Object test2(@RequestBody @Valid User user, BindingResult bindingResult) {
		// 参数校验
		if (bindingResult.hasErrors()) {
			String messages = bindingResult.getAllErrors()
				.stream()
				.map(ObjectError::getDefaultMessage)
				.reduce((m1, m2) -> m1 + "；" + m2)
				.orElse("参数输入有误！");
			throw new IllegalArgumentException(messages);
		}
		
		return "操作成功！";

```

把结果封装到一个BindingResult中，然后再通过自己去封装要抛出的信息，这种做法也可以，就是每个controller都要写，不那么适用，推荐指数 2星

### 第三种：用户手动调用对应API执行校验——Validation.buildDefault ValidatorFactory().getValidator().validate(xxx)

```
   /**
	 * 用户手动调用对应API执行校验
	 * @param user
	 * @return
	 */
	@PostMapping("/test3")
	public Object test3(@RequestBody User user) {
		// 参数校验
		validate(user);
		
		return "操作成功！";
	}
 
	private void validate(@Valid User user) {
		Set<ConstraintViolation<@Valid User>> validateSet = Validation.buildDefaultValidatorFactory()
				.getValidator()
				.validate(user, new Class[0]);
			if (!CollectionUtils.isEmpty(validateSet)) {
				String messages = validateSet.stream()
					.map(ConstraintViolation::getMessage)
					.reduce((m1, m2) -> m1 + "；" + m2)
					.orElse("参数输入有误！");
				throw new IllegalArgumentException(messages);
				
			}
}
```

这种其实原理也差不多，但是看起来也是一样，没有那么优雅，推荐指数 2星


## 结尾
其实这三种用法的原理都是一样的，只是使用的形式不一样了，其实看你自己把，哈哈。

参考
- [JSR](https://blog.csdn.net/qq_31142553/article/details/89430100)

![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！