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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cn.edu.hqu.xixing.thrift.annotation.ThriftServer;

/**
 * @Desc: Apache Thrift服务端注解处理类
 * @Author: huangzhiyuan
 * @CreateDate: 2022/4/4 10:02 上午
 * @Modify:
 */
public class ThriftServerProcessor implements BeanPostProcessor, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ThriftServerProcessor.class);


    private Map<Integer, Map<String, Object>> serviceMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.serviceMap = (Map<Integer, Map<String, Object>>) applicationContext.getBean("serviceMap");
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ThriftServer anno = bean.getClass().getAnnotation(ThriftServer.class);
        if (anno != null) {
            int port = anno.port();
            String serviceName = anno.serviceName();
            Map<String, Object> serviceContentMap = this.serviceMap.get(port);
            if (serviceContentMap == null) {
                serviceContentMap = new HashMap<>();
            }
            serviceContentMap.put(serviceName, bean);
            this.serviceMap.put(port, serviceContentMap);
        }
        return bean;
    }
}
