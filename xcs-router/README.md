# xcs日志收集系统-日志搜索请求路由中间件


## xcs-router

路由控制器，实现RESTful风格接口进行节点配置，将http请求根据自定义规则转发至指定节点，并返回节点的响应；

该路由器用以将日志搜索请求路由至各个集群或者节点（不同集群内的es节点），并将搜索结果返回给请求方。

## 开源项目选择

xcs-router主要实现为RESTful风格的服务，同时使用了mybatis+mysql作为路由数据的存储

 > * [SpringBoot](http://spring.io/)
 > * [SpringBoot+mybatis+mysql搭建](https://blog.csdn.net/u012343297/article/details/78833744)
 

## 组件功能

> * 路由节点和应用的增删改
> * 路由搜索请求


## 启动类

syamwu.logtranslate.Main

## 主要配置


*classpath:application.yml*

```properties
spring.datasource.url: 数据库地址
spring.datasource.username: 数据库用户名
spring.datasource.password: 数据库密码

storey.key: kibana或者其他搜索工具搜索请求的json报文中的AppCode键位
```

## AppCode(应用标示)键位解析

以kibana搜索请求为例子,以下为kibana-6.1.2的discover搜索请求的实体：
```properties
{"index":["sys-app-log*"],"ignore_unavailable":true,"preference":1534128240677}
{"version":true,"size":50,"sort":[{"@timestamp":{"order":"desc","unmapped_type":"boolean"}}],"_source":{"excludes":[]},"aggs":{"2":{"date_histogram":{"field":"@timestamp","interval":"30s","time_zone":"Asia/Shanghai","min_doc_count":1}}},"stored_fields":["*"],"script_fields":{},"docvalue_fields":["@timestamp"],"query":{"bool":{"must":[{"match_all":{}},{"match_phrase":{"appname":{"query":"log-translate"}}},{"range":{"@timestamp":{"gte":1534299912466,"lte":1534300812466,"format":"epoch_millis"}}}],"filter":[],"should":[],"must_not":[]}},"highlight":{"pre_tags":["@kibana-highlighted-field@"],"post_tags":["@/kibana-highlighted-field@"],"fields":{"*":{}},"fragment_size":2147483647}}
```

而这时候需要找到AppCode(应用标示)则需要配置storey.key为: **query.bool.must.match_phrase.appname.query**
当以上例子搜索请求报文提交到xcs-router时候，找到的AppCode(应用标示)键位结果为: **log-translate**
获取结果**log-translate**后将会去数据库查找**ES_LOG_APP**表对应AppCode的所有节点信息，最后将请求报文转发至各个节点，并将各个节点的响应结果返回至请求方

