# xcs日志收集系统-日志发送端组件(elasticsearch)


**组件开发源由**

应业务需求，实现将不同服务器的应用日志进行统一搜索功能，能根据请求执行链、时间区间、关键字搜索和应用名等条件进行搜索，从而为业务方提供快速定位线上应用日志和问题。


**开源项目选择**

首先搜寻到的开源架构ELK似乎就能实现需求，但后来又发现有graylog这样的中间件也是个不错的选择，graylog拥有类似logstash数据传输和格式转换功能，同时提供restful接口实现搜索功能避免和Kibana使用json搜索语法。所以最后采用elasticsearch+graylog。而本项目则为将日志转换根据指定格式后发送给graylog的组件。
 > * [elasticsearch官方文档](https://www.elastic.co/guide/cn/elasticsearch/guide/current/foreword_id.html)
 > * [elasticsearch搭建](https://my.oschina.net/itblog/blog/547250)
 > * [graylog搭建](https://blog.csdn.net/liukuan73/article/details/52525431)
 

**组件功能**

> * 统一日志数据格式
> * 支持不同协议传输
> * 异步传输日志数据


**主要接口和实现类**

> * 日志主输出接口：syamwu.xchushi.fw.log.XcsLogger
> * 接口实现类：syamwu.xchushi.fw.log.elasticsearch.logger.ElasticSearchSubjectLogger
> * logback集成：syamwu.xchushi.fw.log.logback.XcsLogbackAppender
