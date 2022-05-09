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
package cn.acmsmu.xixing.thrift;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.acmsmu.xixing.thrift.processor.ThriftClientProcessor;
import cn.acmsmu.xixing.thrift.pool.TSocketPool;
import cn.acmsmu.xixing.thrift.processor.ThriftServerProcessor;
import cn.acmsmu.xixing.thrift.runner.ThriftRunner;

/**
 * @Desc: Apache Thrift自动配置类
 * @Author: huangzhiyuan
 * @CreateDate: 2022/3/29 9:09 下午
 * @Modify:
 */
@Configuration
public class ThriftAutoConfiguration {

    @Bean
    public ThriftClientProcessor createClientProcessor() {
        return new ThriftClientProcessor();
    }

    @Bean
    public ThriftServerProcessor createServerProcessor() {
        return new ThriftServerProcessor();
    }

    @Bean(name = "serviceMap")
    public Map<Integer, Map<String, Object>> creatServiceMap() {
        return new HashMap<>();
    }

    @Bean(destroyMethod = "destroy")
    public ThriftRunner createRunner() {
        return new ThriftRunner();
    }

    @Bean(name = "connPoolMap")
    public Map<String, TSocketPool> createTransportMap() {
        return new HashMap<>();
    }
}
