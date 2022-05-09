# Spring-Boot-Starter-Thrift

## Description

一个SpringBoot Apache Thrift启动器，主要场景在于Java与其他语言的跨语言调用上，比如java调用Python编写的AI模型


## Dependency

- SpringBoot 2.5
- Apache Thrift 0.16.0

## Features

- ⭐️ 注解驱动，零配置，引入starter后，即完成SpringBoot与Thrift整合

- ⭐️ 支持跨语言调用

- ⭐️ 默认是使用单端口，多服务模式（其实单端口单服务是特殊的单端口，多服务）

> 注意：现阶段使用的配置如下，如果涉及跨语言调用，尽量保持一致，以避免莫名其妙的错误
> 
> - Protocol：TBinaryProtocol、TMultiplexedProtocol
> - Transport：TSocket
> - 服务器类型：TThreadPoolServer


## Annotation

- `@ThriftClient`

```java
public @interface ThriftClient {
    // Thrift服务端主机ip
    String host();
    // Thrift服务端口
    int port();
    // 超时时间
    int timeout() default 300;
    // 要调用的Thrift服务名
    String serviceName() default "";
}
```

- `@ThriftServer`

```java
public @interface ThriftServer {
    // Thrift服务端口号
    int port();
    // 服务名称
    String serviceName();
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

- 服务端使用

在Thrift服务实现类上使用`@ThriftServer`即可，项目在启动时会自动启动Thrift服务器

```java
@Service
@ThriftServer(port = 8989, serviceName = "personService")
public class PersonServiceThriftImpl implements PersonService.Iface{
    @Override
    public Person getPersonByName(String name) throws TException {
        // 业务逻辑
        return new person();
    }

    @Override
    public void savePerson(Person person) throws TException {
        // 业务逻辑
    }
}
```

- 客户端使用

在Thrift客户端上使用`@ThriftClient`即可，项目在启动时会自动注入Thrift客户端

```java
@Service
public class PersonServiceImpl {

    @ThriftClient(host = "127.0.0.1", port = 8989, timeout = 300, serviceName = "personService")
    private PersonService.Client client;


    public String getPerson() throws Exception{
        Person person = client.getPersonByName("martin");
        return person.toString();
    }

}
```

- 跨语言调用

这里列举一个使用Python作为客户端调用Java服务端的例子，其实按照常规操作就可以了

```python
if __name__ == '__main__':
    # 定义Transport传输协议
    transport = TSocket.TSocket('localhost', 8989)

    transport = TTransport.TBufferedTransport(transport)

    # 定义protocol
    protocol = TBinaryProtocol.TBinaryProtocol(transport)

    tprotocol = TMultiplexedProtocol.TMultiplexedProtocol(protocol, "personService")


    # 创建客户端
    client = PersonService.Client(tprotocol)

    transport.open()

    # 调用
    person = client.getPersonByName("martin")
    print(person.name)
    print(person.age)
    print(person.married)

    person_new = ttypes.Person('martin', 18, False)
    client.savePerson(person_new)
```

## Technology

- Spring Bean后置处理器
- cglib动态代理
- Java Reflection、Multi-Thread

