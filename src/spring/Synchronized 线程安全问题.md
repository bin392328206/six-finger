# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客


## 在**synchronized** 锁住方法的情况下，竟然出现了脏写
> **Tips**            
> 昨天本来打算是准备着一支烟 一杯咖啡 一个**bug**写一天的,突然我们组长跟我们说线上环境报错了,  
> 还出现了"**服务器异常，请联系管理员**"                                                                          
>这特么不是一级事故吗？虽然有测试再前面扛枪。但是是我负责的直播模块，心理慌的一批(ps 报错图当时没保存了)


## 分析事故原因  
> 因为是报错(因为我做这条数据查询的时候是selectOne 所以会报出现了sql异常) 原因到是很快找到了 数据库出现了脏写如图:
![](https://user-gold-cdn.xitu.io/2019/11/26/16ea77263c8ccb32?w=1469&h=59&f=png&s=11172)

> 我负责的是直播模块 其中的一个业务是直播结束后第三方会通知我去拉取直播的回放，              
> 但是这个回放有可能一条，也有可能是多条，但是我们的业务要求是只需要保存一条直播回放所以我这会做如下操作：
![](https://user-gold-cdn.xitu.io/2019/11/26/16ea77707a400245?w=1415&h=387&f=png&s=50954)
> 我再做插入之前我会做一个校验，并且我还加了一个方法级别的锁 并且线上我们只有一个副本，           
>竟然还出现了脏写 我的**fuck**,我这是见了鬼了吧


## 解决问题的过程
> 我怀着百私不得其解的心理打算去找答案

>首先我模拟了一个并发环境：

```
    @Test
    public void TEST_TX() throws Exception {

        int N = 2;
        CountDownLatch latch = new CountDownLatch(N);
        for (int i = 0; i < N; i++) {
            Thread.sleep(100L);
            new Thread(() -> {
                try {
                    latch.await();
                    System.out.println("---> start " + Thread.currentThread().getName());
                    Thread.sleep(1000L);
                    CourseChapterLiveRecord courseChapterLiveRecord = new CourseChapterLiveRecord();
                    courseChapterLiveRecord.setCourseChapterId(9785454l);
                    courseChapterLiveRecord.setCreateTime(new Date());
                    courseChapterLiveRecord.setRecordEndTime(new Date());
                    courseChapterLiveRecord.setDuration("aaa");
                    courseChapterLiveRecord.setSiteDomain("ada");
                    courseChapterLiveRecord.setRecordId("aaaaaaaaa");
                    courseChapterLiveRecordServiceImpl.saveCourseChapterLiveRecord(courseChapterLiveRecord);
                    System.out.println("---> end " + Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            latch.countDown();
        }

    }
```
> 通过CountDownLatch 去模拟并发看看数据是否会有问题：结果测试线的数据如下：

![](https://user-gold-cdn.xitu.io/2019/11/26/16ea77f1017429d3?w=1500&h=105&f=png&s=12163)

> 我去还真出现了 而且是一部分出现脏写，一部分没有成功，我特么 **fuck** 心理一万次想说这特么我怎么找            
> 测了十来次 然后觉得肯定是有问题的 然后冷静下来 因为我打了日志 发现2个线程确实是顺序执行的(这里的截图就没有贴了)     
> 众所周知，synchronized方法能够保证所修饰的代码块、方法保证有序性、原子性、可见性。
> 那么这说明什么呢 我一想肯定**Synchronized** 它是起到它的作用的 一个线程执行完成之后，另外一个线程再来执行，
> 突然灵光一闪  是不是下一个线程再做幂等校验的时候 读到了上一次还没有提交的事务 所以造成了**脏读**，**脏写**的原因呢
然后我把再类上的 @Transactional 注解去掉

![](https://user-gold-cdn.xitu.io/2019/11/26/16ea786e4655de4e?w=972&h=188&f=png&s=21343)

> 果然后面测了几次 再也没出现上面的情况了 

> **Tips** 特别感谢**一位不愿透露姓名**的大佬的指出说我没有把标题的内容说清楚和后面的解决问题的收场的时候有点草率        

> 在这里 我再好好的说一下我标题是 在Spring事务管理下，Synchronized为啥还线程不安全？
> 其实有是自己并没有用Synchronized 锁住 Spring 的事务                                                              
> 因为我的列子上的@Transaction注解是再类上面（也就是再方法上面）Spring的声明事事务他是利用了aop的思想    
> 我虽然锁住了第一个线程 但是等到第一个线程的事务 还没提交的时候，第二个线程就去查询了 所以就会导致线程不安全问题


## 解决问题
> 方案1 很简单 那就是不开事务就行了，再这个方法上不加事务就行 因为 Synchronized  可以保证线程安全。
这个方案的意思就是说不要再同一个方法上用@Transaction 和 Synchronized 例子图就没有贴了 就像我前面的 把注解去掉就好了 （但是前提你这个方案确定是不需要事务）
 
> 方案2 再这个里面再调用一层service 让那个方法提交事务，这样的话加上Synchronized 也能保证线程安全。
方案2我贴下代码吧

```
    @Override
    public synchronized void saveCourseChapterLiveRecord(CourseChapterLiveRecord courseChapterLiveRecord) {
        saveRecord(courseChapterLiveRecord);
    }

    @Transactional
    public void saveRecord(CourseChapterLiveRecord courseChapterLiveRecord) {
        //先查数据看是否已经存了
        if (findOrder(courseChapterLiveRecord)){ return;}
        int row = this.insertSelective(courseChapterLiveRecord);
        if (row<1){
    log.info("把录播的信息插入数据库失败 参数是->{}", JSON.toJSONString(courseChapterLiveRecord));
    throw new RRException("把录播的信息插入数据库失败");
        } 
    }
```
> 其实也就是说把事务包裹在Synchronized 里面  

> **先自我批评一下**          
>在技术的道路上真的不要自己觉得是什么就是什么 上面的代码是错误的 其实我并没有测试过 就贴到文章上了 这是一个大忌 为什么很多技术文章有问题 因为很多就像我上面的一样 所以敦促自己以后做事情还是要扎扎实实

>感谢 **紫雨飞星** 读者提出我的错误 
>具体错误的原因是因为调用savRecord方法的时候使用的是this对象，其实是没有被AOP处理的，也就是这个Transactional不会生效~~~

>修改后的代码  自己注入自己  

```
    @Override
    public synchronized void saveCourseChapterLiveRecord(CourseChapterLiveRecord courseChapterLiveRecord) {
        courseChapterLiveRecordServiceImpl.saveRecord(courseChapterLiveRecord);
    }

    @Transactional
    public void saveRecord(CourseChapterLiveRecord courseChapterLiveRecord) {
        //先查数据看是否已经存了
        if (findOrder(courseChapterLiveRecord)){ return;}
        int row = this.insertSelective(courseChapterLiveRecord);
        if (row<1){
    log.info("把录播的信息插入数据库失败 参数是->{}", JSON.toJSONString(courseChapterLiveRecord));
    throw new RRException("把录播的信息插入数据库失败");
        } 
    }

```
> 利用中午的时间测了几次 确实是不会出现线程安全问题了

> 方案3 用redis 分布式锁 也是可以的 就算是多个副本也是能保证线程安全。这个后面的文章会有写到


## 结论
> 在多线程环境下，就可能会出现：方法执行完了(synchronized代码块执行完了)，事务还没提交，别的线程可以进入被synchronized修饰的方法，再读取的时候，读到的是还没提交事务的数据，这个数据不是最新的，所以就出现了这个问题。

> 参考了一位读者的结论

> Synchronized 失效关键原因：是因为**Synchronized**锁定的是当前调用方法对象,而Spring AOP 处理事务会进行生成一个代理对象，并在代理对象执行方法前的事务开启，方法执行完的事务提交，所以说，事务的开启和提交并不是在 Synchronized 锁定的范围内。出现同步锁失效的原因是:当A(线程) 执行完insertSelective()方法，会进行释放同步锁，去做提交事务，但在A(线程)还没有提交完事务之前，B(线程)进行执行findOrder() 方法，执行完毕之后和A(线程)一起提交事务, 这时候就会出现线程安全问题。



## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！

### 参考链接
- [Synchronized 同步出现失效](https://blog.csdn.net/prin_at/article/details/90671332)
- [@Transactional注解不起作用解决办法及原理分析](https://blog.csdn.net/qq_20597727/article/details/84900994)

### 特别鸣谢
> 感谢各位大佬对文章的点评 我会继续努力采坑的 来自一个小白的真实内心独白
