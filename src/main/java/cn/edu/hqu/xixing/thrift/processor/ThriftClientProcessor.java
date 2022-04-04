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
package cn.edu.hqu.xixing.thrift.processor;

import cn.edu.hqu.xixing.thrift.annotation.ThriftClient;
import cn.edu.hqu.xixing.thrift.proxy.ThriftClientProxy;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

/**
 * @Desc: Thrift客户端注解处理器
 * @Author: huangzhiyuan
 * @CreateDate: 2022/3/29 9:13 下午
 * @Modify:
 */
public class ThriftClientProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static final Logger logger = LoggerFactory.getLogger(ThriftClientProcessor.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        List<Field> fieldList = scanTargetClass(bean.getClass());
        for (Field field : fieldList) {
            Object target = applicationContext.getBean(beanName);
            // 将ThriftClient对象注入回去
            ReflectionUtils.makeAccessible(field);
            try {
                TServiceClient client = createClient(field.getType(), field.getAnnotation(ThriftClient.class));
                field.set(target, client);
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
            }
        }
        return bean;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
    *获取哪些类中哪些成员变量带有目标注解
    *@param: [clazz]
    *@return: java.util.List<java.lang.reflect.Field>
    *@throws:
    *@author: huangzhiyuan
    *@date: 2022/3/30 2:20 下午
    */
    private List<Field> scanTargetClass(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            ThriftClient hasAnno = field.getAnnotation(ThriftClient.class);
            if (hasAnno != null) {
                fieldList.add(field);
            }
        }
        return fieldList;
    }

    // 根据注解生成ThriftClient对象
    private TServiceClient createClient(Class clazz, ThriftClient anno) throws Exception{
        TTransport transport = new TSocket(anno.host(), anno.port(), anno.timeout());
        TProtocol protocol = new TBinaryProtocol(transport);
        ThriftClientProxy clientProxy = new ThriftClientProxy();
        return (TServiceClient) clientProxy.bind(clazz, transport, protocol, anno.serviceName());
    }

}
