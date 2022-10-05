# global-log
全局日志组件

```
请求的参数、返回值打印
支持配置日志长度、忽略指定接口、请求参数及返回值数据脱敏
```

# 使用方式

1、下载代码，编译install到本地仓库

```
    mvn clean install -U -DskipTests
```
    
2、引入依赖，具体版本以pom为准 

```
<dependency>
    <groupId>com.github.andy-a-coder</groupId>
    <artifactId>global-log</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

3、yml中配置（默认不配置也可使用，可根据实际需要选择）

```
global:
  log:
    # 日志打印长度，超过则截断默认1024
    result-max-length: 1024
    # 需跳过日志打印的接口方法(格式，类名.方法名，多个逗号分隔)
    skip-methods: A.test,B.test
    # 数据脱敏配置
    desensitization:
      methods[0]:
        # 需脱敏的方法（格式，类名.方法名）
        methodName: TokenEndpoint.postAccessToken
        # 需脱敏的字段
        columns: password,email
        # 默认0（0-请求参数脱敏，1-返回值脱敏，2-all）
        type: 2
```