package cn.edu.hqu.xixing.thrift.config;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.transport.TSocket;

/**
 * @Desc: TSocket连接池配置
 * @Author: huangzhiyuan
 * @CreateDate: 2022/5/8 19:26
 * @Modify:
 */
public class TSocketPoolConfig extends GenericObjectPoolConfig<TSocket> {
    public TSocketPoolConfig(int timeout) {
        setMaxTotal(20);
        setTestOnCreate(true);
        setTestOnBorrow(true);
        setMinEvictableIdleTimeMillis(timeout);
        setTimeBetweenEvictionRunsMillis(1000L);
    }
}
