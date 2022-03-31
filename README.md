# Spring-Boot-Starter-Thrift

## Description

一个SpringBoot Apache Thrift启动器

> ⚠️：开发中，只包含客户端，勿使用！

## Dependency

- SpringBoot 2.5
- Apache Thrift 0.16.0

## Annotation

项目是注解驱动的，尽量做到零配置

- `@ThriftClient`

```java
public @interface ThriftClient {
    // Thrift服务端主机ip
    String host();
    // Thrift服务端口
    int port();
    // 超时时间
    int timeout() default 300;
    // Thrift消息封装协议 TODO
    String protocol();
    // Thrift消息传输类型 TODO
    String transport();
    // 是否是单端口，多服务 TODO
    boolean isMultiService() default false;
    // 要调用的Thrift服务名，多服务情况下生效 TODO
    String serviceName() default "";
}
```



## Usage

- 安装

```shell
$ mvn clean install
```

- 导入依赖

```xml
 <dependency>
   <groupId>cn.edu.hqu.xixing</groupId>
   <artifactId>spring-boot-starter-thrift</artifactId>
   <version>0.1-SNAPSHOT</version>
</dependency>
```

- 在客户端上使用注解`@ThriftClient`来注册一个Thrift客户端，starter会自动注入客户端实例

现在，你可以像以前一样直接调用接口中的方法了！

```java
@Service
public class PersonServiceImpl {

    @ThriftClient(host = "127.0.0.1", port = 8989, timeout = 300, serviceName="personService")
    private PersonService.Client client;


    public String getPerson() throws Exception{
        client.getPersonByName("lucy");
       // do something
    }

}
```

