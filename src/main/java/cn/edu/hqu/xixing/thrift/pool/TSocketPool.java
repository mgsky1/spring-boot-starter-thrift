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
package cn.edu.hqu.xixing.thrift.pool;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.thrift.transport.TSocket;

import java.util.NoSuchElementException;

import cn.edu.hqu.xixing.thrift.config.TSocketPoolConfig;
import cn.edu.hqu.xixing.thrift.factory.TSocketPoolFactory;

/**
 * @Desc: TSocket连接池
 * @Author: huangzhiyuan
 * @CreateDate: 2022/5/8 16:46
 * @Modify:
 */
public class TSocketPool implements ObjectPool<TSocket> {

    private final GenericObjectPool<TSocket> pool;

    public TSocketPool(String host, int port, int timeout) {
        TSocketPoolFactory factory = new TSocketPoolFactory(host, port, timeout);
        pool = new GenericObjectPool<TSocket>(factory, new TSocketPoolConfig(timeout));
    }

    @Override
    public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
        pool.addObject();
    }

    @Override
    public TSocket borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
        return pool.borrowObject();
    }

    @Override
    public void clear() throws Exception, UnsupportedOperationException {
        pool.clear();
    }

    @Override
    public void close() {
        pool.close();
    }

    @Override
    public int getNumActive() {
        return pool.getNumActive();
    }

    @Override
    public int getNumIdle() {
        return pool.getNumIdle();
    }

    @Override
    public void invalidateObject(TSocket tSocket) throws Exception {
        pool.invalidateObject(tSocket);
    }

    @Override
    public void returnObject(TSocket tSocket) throws Exception {
        pool.returnObject(tSocket);
    }
}
