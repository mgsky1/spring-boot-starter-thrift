package cn.edu.hqu.xixing.thrift.annotation;

/**
 * @Desc: Thrift服务端注解
 * @Author: huangzhiyuan
 * @CreateDate: 2022/3/21 8:15 下午
 * @Modify:
 */
public @interface ThriftServer {
    // Thrift服务端口号
    int port();
    // 服务名称
    String serviceName();
}
