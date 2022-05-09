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
package cn.acmsmu.xixing.thrift.config;


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
