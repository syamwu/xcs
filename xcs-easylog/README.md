# xcs日志收集系统-日志发送端组件(elasticsearch)


## 组件开发源由

应业务需求，实现将不同服务器的应用日志进行统一搜索功能，能根据请求执行链、时间区间、关键字搜索和应用名等条件进行搜索，从而为业务方提供快速定位线上应用日志和问题。


## 开源项目选择

首先搜寻到的开源架构ELK似乎就能实现需求，但后来发现graylog也是个不错的选择，graylog拥有类似logstash数据传输和格式转换功能，同时提供restful接口实现搜索功能避免和Kibana使用json搜索语法，并且和kibana一样提供有前端查询页面，另外还提供简单的用户登录授权。所以如果面对局域网下的集群，使用elasticsearch+graylog已经足以。但本项目所面对的不止局域网，还有外网的查询请求，所以除了elasticsearch+graylog组合，最后我们在各个子项目服务器（不同局域网内的应用服务）上面各安装一个elasticsearch，而这些子项目的日志将直接发送至本机的elasticsearch进行存储，而日志搜索的请求，则需要另外一个统一日志查询系统去处理，统一处理前端请求再分发至各个子项目服务，目前正在开发中...
 > * [elasticsearch官方文档](https://www.elastic.co/guide/cn/elasticsearch/guide/current/foreword_id.html)
 > * [elasticsearch搭建](https://my.oschina.net/itblog/blog/547250)
 > * [graylog搭建](https://blog.csdn.net/liukuan73/article/details/52525431)
 

## 组件功能

> * 统一日志数据格式
> * 支持不同协议传输
> * 异步传输日志数据


## 主要接口和实现类

> * 日志主输出接口：[syamwu.xchushi.easylog.XcsLogger](https://github.com/syamwu/xcs/blob/master/xcs-easylog/src/main/java/syamwu/xchushi/easylog/logback/XcsLogbackAppender.java)
> * 接口实现类：[syamwu.xchushi.easylog.elasticsearch.logger.ElasticSearchSubjectLogger](https://github.com/syamwu/xcs/blob/master/xcs-easylog/src/main/java/syamwu/xchushi/easylog/elasticsearch/logger/ElasticSearchSubjectLogger.java)
> * logback集成：[syamwu.xchushi.easylog.logback.XcsLogbackAppender](https://github.com/syamwu/xcs/blob/master/xcs-easylog/src/main/java/syamwu/xchushi/easylog/logback/XcsLogbackAppender.java)
    

## 组件使用
本组件只需要导入开发包，然后进行相应配置即可使用
### maven依赖：
```maven
<dependency>
    <groupId>syamwu.xchushi</groupId>
    <artifactId>xcs-easylog</artifactId>
    <version>1.0.0</version>
</dependency>
```

### logback集成:

**方式1：**

*classpath:logback.xml*
```xml
    <appender name="xcsloger" class="syamwu.xchushi.easylog.logback.XcsLogbackAppender">
        <xcsLogger class="syamwu.xchushi.easylog.elasticsearch.logger.ElasticSearchSubjectLogger">
            <changer class="syamwu.xchushi.easylog.elasticsearch.changer.ElasticsearchLogChanger">
                <appname>xcs-demo</appname>
            </changer>
            <observer class="syamwu.xchushi.fw.transfer.runner.CollectSenderObserverRunner">
                <sender class="syamwu.xchushi.fw.transfer.sender.HttpAndHttpsSender">
                    <serverHosts>eslog.wsy.my:7777</serverHosts>
                    <uri>yunyi_log/test</uri>
                </sender>
            </observer>
        </xcsLogger>
    </appender>
```

**方式2：**

*classpath:logback.xml*
```xml
    <appender name="xcslog" class="syamwu.xchushi.easylog.logback.XcsLogbackAppender">
        <fileName>conf/xcs.properties</fileName>
    </appender>
```
*classpath:conf/xcs.properties*

```properties
#线程池相关
#核心线程数，默认：8
executor.corePoolSize=8
#最大工作线程数，默认：20
executor.maximumPoolSize=20
#线程保留时间（毫秒），默认:10000
executor.keepAliveTime=10000

#日志数据结构相关
#index，默认：application-log
eslogger.index=sys-app-log
#type，默认：log
eslogger.type=easylog
#index区分每日，默认：true
eslogger.dateIndex=true
#应用名称，默认：application
eslogger.appname=myappname
#该结构版本号，默认：1
eslogger.docVersion=1
#传输方ip，若为空默认获取本地ip，默认：
eslogger.ipAddress=192.168.0.101

#数据收集器相关
#单次收集最大item数，默认：30
collecter.queueLoopCount=20
#单次收集字节长度（字节），默认：2097152
collecter.maxSendLength=2097152
#队列最大长度，默认：2147483647
collecter.maxQueueCount=2147483647
#字符编码，默认：UTF-8
collecter.charset=UTF-8

#传输相关
#传输协议，默认：http
sender.protocol=http
#传输字符编码，默认：UTF-8
sender.charset=UTF-8
#http请求是否进行gzip压缩，默认：false
sender.gzipEnable=false
#传输服务器host，默认：127.0.0.1
sender.serverHosts=eslog.syamwu:7202
#传输服务器uri，默认：
sender.uri=sys-app-log/easylog/_bulk
#传输超时时间（毫秒），默认：10000
sender.sendTimeOut=10000
#是否开启多host负责均衡，默认：false
sender.loadBalanc.endable=false
#负责均衡host权值比，默认：1
sender.loadBalanc.loads=1,1
#负责均衡host权值比系数，默认：1000
sender.loadBalanc.scaleBase=1000
#是否打印传输响应日志，默认：false
sender.showlog=false
#失败请求缓冲阀值，默认：10
sender.failSendCount=10
#失败请求达到阀值后的缓冲时间，避免在服务器down了频繁请求（毫秒），默认：100
sender.failSendTime=100
```
其中eslog.syamwu:7202为graylog暴露端口。若不需要使用graylog，只留elasticsearch则eslog.syamwu:7202可以为elasticsearch暴露的http端口，但需要修改syamwu.xchushi.fw.transfer.collect.StringQueueCollector该类下面的collect方法，将日志数据格式拼接为[bulk](https://www.elastic.co/guide/cn/elasticsearch/guide/current/bulk.html)格式的报文