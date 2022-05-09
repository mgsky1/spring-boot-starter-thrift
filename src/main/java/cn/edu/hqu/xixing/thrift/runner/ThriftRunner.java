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
package cn.edu.hqu.xixing.thrift.runner;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;

import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import cn.edu.hqu.xixing.thrift.pool.TSocketPool;

/**
 * @Desc: Apache Thrift服务启动器
 * @Author: huangzhiyuan
 * @CreateDate: 2022/4/4 10:18 上午
 * @Modify:
 */
public class ThriftRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ThriftRunner.class);

    @Resource(name = "serviceMap")
    private Map<Integer, Map<String, Object>> serviceMap;

    @Resource(name = "connPoolMap")
    private Map<String, TSocketPool> connPoolMap;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 多线程启动服务
        for (Integer key : serviceMap.keySet()) {
            ServeThread thread = new ServeThread(key, serviceMap.get(key));
            thread.setName("thrift-server-" + key);
            thread.start();
        }
    }

    public void destroy() {
        for (String key : connPoolMap.keySet()) {
            TSocketPool pool = connPoolMap.get(key);
            pool.close();
            logger.info("连接池{}已关闭", key);
        }
    }
}

class ServeThread extends Thread {

    private int port;
    private Map<String, Object> serviceMap;

    private static final Logger logger = LoggerFactory.getLogger(ServeThread.class);

    public ServeThread(int port, Map<String, Object> serviceMap) {
        this.port = port;
        this.serviceMap = serviceMap;
    }

    @Override
    public void run() {
        try{
            ServerSocket socket = new ServerSocket(port);
            TServerSocket tServerSocket = new TServerSocket(socket);
            TBinaryProtocol.Factory factory = new TBinaryProtocol.Factory();
            TMultiplexedProcessor multiplexedProcessor = new TMultiplexedProcessor();
            for (String serviceName : serviceMap.keySet()) {
                Class<?> serviceImplClass = serviceMap.get(serviceName).getClass();
                Class<?>[] interfaces = serviceImplClass.getInterfaces();
                for (Class<?> facade : interfaces) {
                    String facadeName = facade.getName();
                    if (facadeName.contains("$Iface")) {
                        String processorName = facadeName.substring(0, facadeName.indexOf("$")) + "$Processor";
                        Class<?> processorClass = Class.forName(processorName);
                        Constructor tProcessorConstructor = processorClass.getConstructors()[0];
                        TProcessor tProcessorObj = (TProcessor) tProcessorConstructor.newInstance(serviceMap.get(serviceName));
                        multiplexedProcessor.registerProcessor(serviceName, tProcessorObj);
                        // 假设对于每一个Iface，只有一个实现类
                        break;
                    }
                }
            }
            TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(tServerSocket);
            tArgs.processor(multiplexedProcessor);
            tArgs.protocolFactory(factory);
            TThreadPoolServer tServer = new TThreadPoolServer(tArgs);
            logger.info("{}端口服务已启动...", port);
            tServer.serve();

        } catch (Exception e) {
            logger.error("Thrift服务端启动失败，端口{}：{},", port, e);
        }

    }
}