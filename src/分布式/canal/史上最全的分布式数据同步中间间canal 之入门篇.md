# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客

## 絮叨 
> **Redis**系列写完了，好好学习看看，各位应该可以入个门

> 这篇讲 数据同步 cannl,因为Redis中有一个知识点：如何保证缓存一致，有说到用canal做一个补偿机制，刚好自己也不会，就带大家一起学习，学习,绝对是从0开始

## 什么是canal
> canal是纯Java开发。基于数据库增量日志解析，提供增量数据订阅&消费，目前主要支持了MySQL

![](https://user-gold-cdn.xitu.io/2019/12/3/16ecade3690acd43?w=1361&h=717&f=png&s=153912)

如上图：canal 模拟 MySQL slave 的交互协议，伪装自己为 MySQL slave ，向 MySQL master 发送dump 协议

## canal 搭建

### 搭建mysql环境

- 对于自建 MySQL , 需要先开启 Binlog 写入功能，配置 binlog-format 为 ROW 模式，my.cnf 中配置如下
```
[mysqld]
log-bin=mysql-bin # 开启 binlog
binlog-format=ROW # 选择 ROW 模式
server_id=1 # 配置 MySQL replaction 需要定义，不要和 canal 的 slaveId 重复
```
- 授权 canal 链接 MySQL 账号具有作为 MySQL slave 的权限, 如果已有账户可直接 grant

```
CREATE USER canal IDENTIFIED BY 'canal';  
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';
-- GRANT ALL PRIVILEGES ON *.* TO 'canal'@'%' ;
FLUSH PRIVILEGES;
```
这个第一步还是蛮简单的，就是要自己搭建一个mysql，修改一下mysql的配置,这个配置一般是再/etc/my.cnf中，还是得要点小基础的哈，至少mysql得会搭

### 搭建canal环境

- 下载 canal, 访问 release 页面 , 选择需要的包下载, 如以 1.0.17 版本为例
```
wget https://github.com/alibaba/canal/releases/download/canal-1.0.17/canal.deployer-1.0.17.tar.gz
```
- 解压缩
```
mkdir /tmp/canal
tar zxvf canal.deployer-$version.tar.gz  -C /tmp/canal
```
> 解压完成后，进入 /tmp/canal 目录，可以看到如下结构

![](https://user-gold-cdn.xitu.io/2019/12/3/16ecaef77b149328?w=1421&h=155&f=png&s=9182)

- 配置修改

```
vi conf/example/instance.properties
```

```
#################################################
## mysql serverId , v1.0.26+ will autoGen 
canal.instance.mysql.slaveId=8

# enable gtid use true/false
canal.instance.gtidon=false

# position info 需要改成自己的数据库信息
canal.instance.master.address=10.0.98.186:3306
canal.instance.master.journal.name=
canal.instance.master.position=
canal.instance.master.timestamp=
canal.instance.master.gtid=

# rds oss binlog
canal.instance.rds.accesskey=
canal.instance.rds.secretkey=
canal.instance.rds.instanceId=

# table meta tsdb info 
canal.instance.tsdb.enable=true
#canal.instance.tsdb.url=jdbc:mysql://127.0.0.1:3306/canal_tsdb
#canal.instance.tsdb.dbUsername=canal
#canal.instance.tsdb.dbPassword=canal

#canal.instance.standby.address =
#canal.instance.standby.journal.name =
#canal.instance.standby.position = 
#canal.instance.standby.timestamp =
#canal.instance.standby.gtid=

# username/password  需要改成自己的数据库信息
canal.instance.dbUsername=root
canal.instance.dbPassword=root
canal.instance.connectionCharset=UTF-8
canal.instance.defaultDatabaseName=expert-online-school
# table regex
canal.instance.filter.regex=.*\\..*
# table black regex
canal.instance.filter.black.regex=
#################################################
                                                           
```
> 注意：
>canal.instance.connectionCharset 代表数据库的编码方式对应到 java 中的编码类型，比如 UTF-8，GBK , ISO-8859-1
>如果系统是1个 cpu，需要将 canal.instance.parser.parallel 设置为 false

- 启动

```
sh bin/startup.sh
```
到目前为止 canal的服务端我们已经搭建好了 但是到目前 我们只是把数据库的binlog 拉到canal中，我们还得把数据用otter去消费

## 写个简单的Demo 去监听mysql 数据的变动
### Jar包

```
<dependency>
    <groupId>com.alibaba.otter</groupId>
    <artifactId>canal.client</artifactId>
    <version>1.1.3</version>
</dependency>
```

### 测试代码
```
package com.hq.eos.sync.client;

import java.net.InetSocketAddress;
import java.util.List;


import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.alibaba.otter.canal.protocol.Message;



public class CanalTest {

    public static void main(String[] args) throws Exception {

        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("10.0.98.186", 11111), "expert", "root", "root");
        connector.connect();
        connector.subscribe(".*\\..*");
        connector.rollback();

        while (true) {
            Message message = connector.getWithoutAck(100);  // 获取指定数量的数据
            long batchId = message.getId();
            if (batchId == -1 || message.getEntries().isEmpty()) {
                Thread.sleep(1000);
                continue;
            }
            // System.out.println(message.getEntries());
            printEntries(message.getEntries());
            connector.ack(batchId);// 提交确认，消费成功，通知server删除数据
//            connector.rollback(batchId);// 处理失败, 回滚数据，后续重新获取数据
        }
    }

    private static void printEntries(List<Entry> entries) throws Exception {
        for (Entry entry : entries) {
            if (entry.getEntryType() != EntryType.ROWDATA) {
                continue;
            }

            RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());

            EventType eventType = rowChange.getEventType();
            System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(), eventType));

            for (RowData rowData : rowChange.getRowDatasList()) {
                switch (rowChange.getEventType()) {
                    case INSERT:
                        System.out.println("INSERT ");
                        printColumns(rowData.getAfterColumnsList());
                        break;
                    case UPDATE:
                        System.out.println("UPDATE ");
                        printColumns(rowData.getAfterColumnsList());
                        break;
                    case DELETE:
                        System.out.println("DELETE ");
                        printColumns(rowData.getBeforeColumnsList());
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private static void printColumns(List<Column> columns) {
        for(Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }
}
```

测试结果

```
================> binlog[mysql-bin.000017:240485980] , name[xxl_job,xxl_job_registry] , eventType : UPDATE
UPDATE 
id : 402    update=false
registry_group : EXECUTOR    update=false
registry_key : hq-eos-crawler    update=false
registry_value : 172.27.0.1:15674    update=false
update_time : 2019-12-03 17:54:42    update=true
================> binlog[mysql-bin.000017:240486374] , name[xxl_job,xxl_job_registry] , eventType : UPDATE
UPDATE 
id : 82    update=false
registry_group : EXECUTOR    update=false
registry_key : hq-eos-inf-config    update=false
registry_value : 172.18.0.1:15672    update=false
update_time : 2019-12-03 17:54:42    update=true
================> binlog[mysql-bin.000017:240486774] , name[xxl_job,xxl_job_registry] , eventType : UPDATE
```

注意一下

```
  CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("10.0.98.186", 11111), "expert", "root", "root");
```
这里的配置来自于 canal.properties 我把这个配置也贴出来吧

```
canal.id= 8
canal.ip=
canal.port=11111
canal.metrics.pull.port=11112
canal.zkServers=10.0.14.36:2181,10.0.14.39:2181,10.0.14.49:2181
# flush data to zk
canal.zookeeper.flush.period = 1000
canal.withoutNetty = false
# flush meta cursor/parse position to file
canal.file.data.dir = ${canal.conf.dir}
canal.file.flush.period = 1000
## memory store RingBuffer size, should be Math.pow(2,n)
canal.instance.memory.buffer.size = 16384
## memory store RingBuffer used memory unit size , default 1kb
canal.instance.memory.buffer.memunit = 1024 
## meory store gets mode used MEMSIZE or ITEMSIZE
canal.instance.memory.batch.mode = MEMSIZE

## detecing config
canal.instance.detecting.enable = false
#canal.instance.detecting.sql = insert into retl.xdual values(1,now()) on duplicate key update x=now()
canal.instance.detecting.sql = select 1
canal.instance.detecting.interval.time = 3
canal.instance.detecting.retry.threshold = 3
canal.instance.detecting.heartbeatHaEnable = false

# support maximum transaction size, more than the size of the transaction will be cut into multiple transactions delivery
canal.instance.transaction.size =  1024
# mysql fallback connected to new master should fallback times
canal.instance.fallbackIntervalInSeconds = 60

# network config
canal.instance.network.receiveBufferSize = 16384
canal.instance.network.sendBufferSize = 16384
canal.instance.network.soTimeout = 30
# binlog filter config
canal.instance.filter.druid.ddl = true
canal.instance.filter.query.dcl = false
canal.instance.filter.query.dml = false
canal.instance.filter.query.ddl = false
canal.instance.filter.table.error = false
canal.instance.filter.rows = false
canal.instance.filter.transaction.entry = false

# binlog format/image check
canal.instance.binlog.format = ROW,STATEMENT,MIXED 
canal.instance.binlog.image = FULL,MINIMAL,NOBLOB

# binlog ddl isolation
canal.instance.get.ddl.isolation = false

# parallel parser config
canal.instance.parser.parallel = true
## concurrent thread number, default 60% available processors, suggest not to exceed Runtime.getRuntime().availableProcessors()
#canal.instance.parser.parallelThreadSize = 16
## disruptor ringbuffer size, must be power of 2
canal.instance.parser.parallelBufferSize = 256

# table meta tsdb info
canal.instance.tsdb.enable=true
canal.instance.tsdb.dir=${canal.file.data.dir:../conf}/${canal.instance.destination:}
canal.instance.tsdb.url=jdbc:h2:${canal.instance.tsdb.dir}/h2;CACHE_SIZE=1000;MODE=MYSQL;
canal.instance.tsdb.dbUsername=root
canal.instance.tsdb.dbPassword=root
# rds oss binlog account
canal.instance.rds.accesskey =
canal.instance.rds.secretkey =

#################################################
#########               destinations            ############# 
#################################################
canal.destinations= expert
# conf root dir
canal.conf.dir = ../conf
# auto scan instance dir add/remove and start/stop instance
canal.auto.scan = true
canal.auto.scan.interval = 5

canal.instance.tsdb.spring.xml=classpath:spring/tsdb/h2-tsdb.xml
#canal.instance.tsdb.spring.xml=classpath:spring/tsdb/mysql-tsdb.xml

canal.instance.global.mode = spring 
canal.instance.global.lazy = false
#canal.instance.global.manager.address = 127.0.0.1:1099
#canal.instance.global.spring.xml = classpath:spring/memory-instance.xml
canal.instance.global.spring.xml = classpath:spring/file-instance.xml
#canal.instance.global.spring.xml = classpath:spring/default-instance.xml
```



这上面很多配置这边就不一一说明了 zookeeper的话是做instance 集群的时候用的

## 结尾
> canal入门写完，谢谢大家的支持，大家如果想学后面一篇文章，一定要把这个照着做一遍。   
> 因为博主也是一个开发萌新 我也是一边学一边写 我有个目标就是一周 二到三篇 希望能坚持个一年吧 希望各位大佬多提意见，让我多学习，一起进步。
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！


