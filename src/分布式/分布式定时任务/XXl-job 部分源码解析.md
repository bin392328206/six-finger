# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨 
因为之前用xxl-job 用的也很多了，然后自己也把他的边缘业务代码改换成自己公司合适的用法了，比如说我们的动态定时任务，调用接口啥的，但是对应他的核心源码，一直没有机会梳理一下，固找个时间，一起梳理梳理，但是我想说的是本文争对的是原理的解读，不适合XXL-Job的初学者，我这边默认你已经对于XXl-job至少是有一定的了解，如果要去入门，先去下面的官方文档
- [xxl-job](https://gitee.com/xuxueli0323/xxl-job/tree/master/)


## 架构图
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b4d959ec11354bdf9352b7e95a25fc4b~tplv-k3u1fbpfcp-zoom-1.image)

上图是我们要进行源码分析的2.1版本的整体架构图。其分为两大块，调度中心和执行器，我们先分析调度中心，也就是xxl-job-admin这个包的代码。
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e30315a61c99436e9de12b0087d1b60e~tplv-k3u1fbpfcp-zoom-1.image)


## 主流程

XXL-JOB 他是基于SpringBoot的，所以我们的第一步当然是启动Spring的应用程序 XxlJobAdminApplication，然后他里面有个配置类XxlJobAdminConfig 实现了 InitializingBean ，Spring在初始化之后会加载这个Bean,如下图

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/272cd4536a4240ec84a637a840a01344~tplv-k3u1fbpfcp-zoom-1.image)


然后我们来看看 xxlJobScheduler.init();方法，如下图

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/82ce962dccfc463d93f335cd9e0ab89c~tplv-k3u1fbpfcp-zoom-1.image)


- 第一步国际化相关。
- 第二步监控相关。
- 第三步失败重试相关。
- 第四步启动admin端服务，接收注册请求等。
- 第五步JobScheduleHelper调度器，死循环，在xxl_job_info表里取将要执行的任务，更新下次执行时间的，调用JobTriggerPoolHelper类，来给执行器发送调度任务的

其实上面的步骤还是很多地方需要我们去一个个了解的，但是我们今天就挑些来学习，我们直接看最后一步JobScheduleHelper

### JobScheduleHelper

非常重要的一个类，我们来看看他的start()方法

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/cc99defee3c940bd96fd29d065010cab~tplv-k3u1fbpfcp-zoom-1.image)


其实很简单，就是启动了2后台线程，这2个线程是死循环的，但是这2个线程确也是很重要的。我们一个个来看

#### scheduleThread
```

        // schedule thread
        scheduleThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    TimeUnit.MILLISECONDS.sleep(5000 - System.currentTimeMillis()%1000 );
                } catch (InterruptedException e) {
                    if (!scheduleThreadToStop) {
                        logger.error(e.getMessage(), e);
                    }
                }
                logger.info(">>>>>>>>> init xxl-job admin scheduler success.");

                // pre-read count: treadpool-size * trigger-qps (each trigger cost 50ms, qps = 1000/50 = 20)
                int preReadCount = (XxlJobAdminConfig.getAdminConfig().getTriggerPoolFastMax() + XxlJobAdminConfig.getAdminConfig().getTriggerPoolSlowMax()) * 20;

                while (!scheduleThreadToStop) {

                    // Scan Job
                    long start = System.currentTimeMillis();

                    Connection conn = null;
                    Boolean connAutoCommit = null;
                    PreparedStatement preparedStatement = null;

                    boolean preReadSuc = true;
                    try {

                        conn = XxlJobAdminConfig.getAdminConfig().getDataSource().getConnection();
                        connAutoCommit = conn.getAutoCommit();
                        conn.setAutoCommit(false);

                        preparedStatement = conn.prepareStatement(  "select * from xxl_job_lock where lock_name = 'schedule_lock' for update" );
                        preparedStatement.execute();

                        // tx start

                        // 1、pre read
                        long nowTime = System.currentTimeMillis();
                        List<XxlJobInfo> scheduleList = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().scheduleJobQuery(nowTime + PRE_READ_MS, preReadCount);
                        if (scheduleList!=null && scheduleList.size()>0) {
                            // 2、push time-ring
                            for (XxlJobInfo jobInfo: scheduleList) {

                                // time-ring jump
                                if (nowTime > jobInfo.getTriggerNextTime() + PRE_READ_MS) {
                                    // 2.1、trigger-expire > 5s：pass && make next-trigger-time
                                    logger.warn(">>>>>>>>>>> xxl-job, schedule misfire, jobId = " + jobInfo.getId());

                                    // fresh next
                                    refreshNextValidTime(jobInfo, new Date());

                                } else if (nowTime > jobInfo.getTriggerNextTime()) {
                                    // 2.2、trigger-expire < 5s：direct-trigger && make next-trigger-time

                                    // 1、trigger
                                    JobTriggerPoolHelper.trigger(jobInfo.getId(), TriggerTypeEnum.CRON, -1, null, null, null);
                                    logger.debug(">>>>>>>>>>> xxl-job, schedule push trigger : jobId = " + jobInfo.getId() );

                                    // 2、fresh next
                                    refreshNextValidTime(jobInfo, new Date());

                                    // next-trigger-time in 5s, pre-read again
                                    if (jobInfo.getTriggerStatus()==1 && nowTime + PRE_READ_MS > jobInfo.getTriggerNextTime()) {

                                        // 1、make ring second
                                        int ringSecond = (int)((jobInfo.getTriggerNextTime()/1000)%60);

                                        // 2、push time ring
                                        pushTimeRing(ringSecond, jobInfo.getId());

                                        // 3、fresh next
                                        refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));

                                    }

                                } else {
                                    // 2.3、trigger-pre-read：time-ring trigger && make next-trigger-time

                                    // 1、make ring second
                                    int ringSecond = (int)((jobInfo.getTriggerNextTime()/1000)%60);

                                    // 2、push time ring
                                    pushTimeRing(ringSecond, jobInfo.getId());

                                    // 3、fresh next
                                    refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));

                                }

                            }

                            // 3、update trigger info
                            for (XxlJobInfo jobInfo: scheduleList) {
                                XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().scheduleUpdate(jobInfo);
                            }

                        } else {
                            preReadSuc = false;
                        }

                        // tx stop


                    } catch (Exception e) {
                        if (!scheduleThreadToStop) {
                            logger.error(">>>>>>>>>>> xxl-job, JobScheduleHelper#scheduleThread error:{}", e);
                        }
                    } finally {

                        // commit
                        if (conn != null) {
                            try {
                                conn.commit();
                            } catch (SQLException e) {
                                if (!scheduleThreadToStop) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                            try {
                                conn.setAutoCommit(connAutoCommit);
                            } catch (SQLException e) {
                                if (!scheduleThreadToStop) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                if (!scheduleThreadToStop) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        }

                        // close PreparedStatement
                        if (null != preparedStatement) {
                            try {
                                preparedStatement.close();
                            } catch (SQLException e) {
                                if (!scheduleThreadToStop) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        }
                    }
                    long cost = System.currentTimeMillis()-start;


                    // Wait seconds, align second
                    if (cost < 1000) {  // scan-overtime, not wait
                        try {
                            // pre-read period: success > scan each second; fail > skip this period;
                            TimeUnit.MILLISECONDS.sleep((preReadSuc?1000:PRE_READ_MS) - System.currentTimeMillis()%1000);
                        } catch (InterruptedException e) {
                            if (!scheduleThreadToStop) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }

                }

                logger.info(">>>>>>>>>>> xxl-job, JobScheduleHelper#scheduleThread stop");
            }
        });
        scheduleThread.setDaemon(true);
        scheduleThread.setName("xxl-job, admin JobScheduleHelper#scheduleThread");
        scheduleThread.start();
```

我直接把代码拷贝了过来，然后我们来慢慢说吧

- 死循环内的代码如上图，首先利用for update语句进行获取任务的资格锁定，再去获取未来5秒内即将要执行的任务。

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5d7fec24257c4949911c7282aa4039dc~tplv-k3u1fbpfcp-zoom-1.image)

其实这种代码我们撸业务的。谁不是闭着眼就来，哈哈 返回的是一个JobInfo的List,如果是我们写代码会怎么样？第一个当然是判断是否为空，然后遍历去处理每一个对象，还别说xxl-job也是这样的，然后根据对象里面的属性不同做不同的业务逻辑处理，哈哈，是不是觉得开源框架和我们平时写的代码也差不多。
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/57d736b94b544f0ab11e1720954a0cda~tplv-k3u1fbpfcp-zoom-1.image)

然后根据triggerNextTime这个字段来走不同的分支，一共有3个分支，我们来看看这几个分支的逻辑

- 第一个分支当前任务的触发时间已经超时5秒以上了，不在执行，直接计算下一次触发时间。
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/157a573012bc47c48cf0094f191b50f9~tplv-k3u1fbpfcp-zoom-1.image)
-  第二个分支为触发时间已满足，利用JobTriggerPoolHelper这个类进行任务调度，之后判断下一次执行时间如果在5秒内，进行此任务数据的缓存，处理逻辑与第三个分支一样。

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a47f44caa1b844689c8b3feb52ba9177~tplv-k3u1fbpfcp-zoom-1.image)

- 我们来看看那个pushTimeRing(ringSecond, jobInfo.getId());
```
    private void pushTimeRing(int ringSecond, int jobId){
        // push async ring
        List<Integer> ringItemData = ringData.get(ringSecond);
        if (ringItemData == null) {
            ringItemData = new ArrayList<Integer>();
            ringData.put(ringSecond, ringItemData);
        }
        ringItemData.add(jobId);

        logger.debug(">>>>>>>>>>> xxl-job, schedule push time-ring : " + ringSecond + " = " + Arrays.asList(ringItemData) );
    }
```
ringData是以0到59的整数为key，以jobId集合为value的Map集合。这个集合数据的处理逻辑，就在我们第二个守护线程ringThread中。

- 我们来看看我们的第二个守护线程吧

```
  // ring thread
        ringThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // align second
                try {
                    TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis()%1000 );
                } catch (InterruptedException e) {
                    if (!ringThreadToStop) {
                        logger.error(e.getMessage(), e);
                    }
                }

                while (!ringThreadToStop) {

                    try {
                        // second data
                        List<Integer> ringItemData = new ArrayList<>();
                        int nowSecond = Calendar.getInstance().get(Calendar.SECOND);   // 避免处理耗时太长，跨过刻度，向前校验一个刻度；
                        for (int i = 0; i < 2; i++) {
                            List<Integer> tmpData = ringData.remove( (nowSecond+60-i)%60 );
                            if (tmpData != null) {
                                ringItemData.addAll(tmpData);
                            }
                        }

                        // ring trigger
                        logger.debug(">>>>>>>>>>> xxl-job, time-ring beat : " + nowSecond + " = " + Arrays.asList(ringItemData) );
                        if (ringItemData.size() > 0) {
                            // do trigger
                            for (int jobId: ringItemData) {
                                // do trigger
                                JobTriggerPoolHelper.trigger(jobId, TriggerTypeEnum.CRON, -1, null, null, null);
                            }
                            // clear
                            ringItemData.clear();
                        }
                    } catch (Exception e) {
                        if (!ringThreadToStop) {
                            logger.error(">>>>>>>>>>> xxl-job, JobScheduleHelper#ringThread error:{}", e);
                        }
                    }

                    // next second, align second
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis()%1000);
                    } catch (InterruptedException e) {
                        if (!ringThreadToStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-job, JobScheduleHelper#ringThread stop");
            }
        });
        ringThread.setDaemon(true);
        ringThread.setName("xxl-job, admin JobScheduleHelper#ringThread");
        ringThread.start();
```

根据当前秒数刻度和前一个刻度进行时间轮的任务获取，之后和上文一样，利用JobTriggerPoolHelper进行任务调度。

### 时序图
![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/fd9f448703ae4ec4af0bba39af7afd34~tplv-k3u1fbpfcp-zoom-1.image)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
