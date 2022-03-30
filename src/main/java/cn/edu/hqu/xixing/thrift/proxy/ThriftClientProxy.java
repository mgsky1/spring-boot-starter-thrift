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
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 * @Desc: Thrift客户端动态代理
 * @Author: huangzhiyuan
 * @CreateDate: 2022/3/30 8:36 下午
 * @Modify:
 */
public class ThriftClientProxy implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ThriftClientProxy.class);

    private TServiceClient realClient;

    private TTransport transport;

    private TProtocol protocol;

    public Object bind(TServiceClient client, TTransport transport, TProtocol protocol) {
        this.realClient = client;
        this.transport = transport;
        this.protocol = protocol;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.realClient.getClass());
        enhancer.setCallback(this);
        return enhancer.create(new Class[]{TProtocol.class}, new Object[]{this.protocol});
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if (!this.transport.isOpen()) {
            // 打开Socket
            transport.open();
        }
        // 调用方法
        Object res = null;
        res = methodProxy.invoke(this.realClient, objects);
        if (this.transport.isOpen()) {
            // 关闭Socket
            transport.close();
        }
        return res;
    }
}
