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
package cn.edu.hqu.xixing.thrift.factory;


import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TSocket;

import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Desc: 池化的TSocket工厂
 * @Author: huangzhiyuan
 * @CreateDate: 2022/5/8 16:28
 * @Modify:
 */
public class TSocketPoolFactory extends BasePooledObjectFactory<TSocket> {

    private static final Logger logger = LoggerFactory.getLogger(TSocketPoolFactory.class);

    private String host;

    private int port;

    private int timeout;

    public TSocketPoolFactory(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public TSocket create() throws Exception {
        TSocket socket = new TSocket(host, port, timeout);
        socket.open();
        logger.info("TSocket对象{}已创建", socket);
        return socket;
    }

    @Override
    public void destroyObject(PooledObject<TSocket> p) throws Exception {
        TSocket socket = p.getObject();
        if (socket.isOpen()) {
            socket.close();
        }
        p.markAbandoned();
        logger.info("TSocket对象{}已销毁", socket);
    }

    @Override
    public PooledObject<TSocket> wrap(TSocket tSocket) {
        return new DefaultPooledObject<>(tSocket);
    }


    @Override
    public boolean validateObject(PooledObject<TSocket> p) {
        try {
            Socket socket = new Socket(host, port);
            socket.close();
            return true;
        } catch (Exception e) {
            logger.error("{}:{}连接测试失败", host, port);
            return false;
        }
    }
}
