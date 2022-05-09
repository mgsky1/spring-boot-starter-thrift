/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.hqu.xixing.thrift.proxy;


import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import cn.edu.hqu.xixing.thrift.pool.TSocketPool;

/**
 * @Desc: Thrift客户端动态代理
 * @Author: huangzhiyuan
 * @CreateDate: 2022/3/30 8:36 下午
 * @Modify:
 */
public class ThriftClientProxy implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ThriftClientProxy.class);

    private TServiceClient realClient;

    private TProtocol protocol;

    private TSocketPool pool;

    private String serviceName;

    public Object bind(Class clazz, TProtocol protocol, String serviceName, TSocketPool pool) throws Exception{
        // 先绑定一个假的客户端，把框架先搭建起来
        TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(protocol, serviceName);
        Constructor<TServiceClient> constructor = clazz.getDeclaredConstructor(TProtocol.class);
        this.realClient = constructor.newInstance(multiplexedProtocol);
        this.protocol = protocol;
        this.pool = pool;
        this.serviceName = serviceName;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.realClient.getClass());
        enhancer.setCallback(this);
        return enhancer.create(new Class[]{TProtocol.class}, new Object[]{this.protocol});
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        // 真正绑定客户端
        TSocket socket = pool.borrowObject();
        TProtocol protocol = new TBinaryProtocol(socket);
        TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(protocol, serviceName);
        Constructor<TServiceClient> constructor = (Constructor<TServiceClient>) this.realClient.getClass().getDeclaredConstructor(TProtocol.class);
        this.realClient = constructor.newInstance(multiplexedProtocol);
        try {
            // 调用方法
            Object res = methodProxy.invoke(this.realClient, objects);
            return res;
        } catch (Exception e) {
            logger.error("Thrift RPC调用发生异常:{}", e);
            return null;
        } finally {
            pool.returnObject(socket);
        }
    }
}
