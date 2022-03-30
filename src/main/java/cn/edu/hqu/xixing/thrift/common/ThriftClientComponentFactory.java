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
package cn.edu.hqu.xixing.thrift.common;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.transport.TSocket;

import java.util.HashMap;
import java.util.Map;

/**
 * @Desc: Thrift客户端组件工厂
 * @Author: huangzhiyuan
 * @CreateDate: 2022/3/29 9:10 下午
 * @Modify:
 */
public class ThriftClientComponentFactory {
    private final Map<String, Class<?>> protocolMap = new HashMap<>();
    private final Map<String, Class<?>> transportMap = new HashMap<>();

    public ThriftClientComponentFactory() {
        protocolMap.put("TBinaryProtocol", TBinaryProtocol.class);
        protocolMap.put("TCompactProtocol", TCompactProtocol.class);
        protocolMap.put("TJSONProtocol", TJSONProtocol.class);
        protocolMap.put("TMultiplexedProtocol", TMultiplexedProtocol.class);
        protocolMap.put("TSimpleJSONProtocol", TSimpleJSONProtocol.class);
        protocolMap.put("TTupleProtocol", TTupleProtocol.class);

        transportMap.put("TSocket", TSocket.class);
    }

    public void registerTProtocol(String key, Class<?> value) {
        // check
        protocolMap.put(key, value);
    }

    public void registerTTransport(String key, Class<?> value) {
        // check
        transportMap.put(key, value);
    }

    public Class<?> getTProtocol(String key) {
        return protocolMap.get(key);
    }

    public Class<?> getTTransport(String key) {
        return transportMap.get(key);
    }
}
