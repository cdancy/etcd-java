/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cdancy.etcdjava;

import com.cdancy.etcdjava.utils.EtcdJavaUtils;
import com.google.common.base.Throwables;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.local.LocalServerRegistry;
import io.atomix.catalyst.transport.local.LocalTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Java impl of Etcd peer server.
 * 
 * @author cdancy
 */
public final class PeerServer {
    
    private volatile CopycatServer copycatServer;
    private String host = "0.0.0.0";
    private int port = 2380;
    private File logsDirectory = new File(System.getProperty("user.dir") + "/default.etcd");
    
    public PeerServer() {

    }
    
    public PeerServer(int port) {
        this.port = port;
    }
    
    public PeerServer(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    private void init() {
        copycatServer = CopycatServer.builder(new Address(host, port))
                .withName(name())
                .withStateMachine(EtcdJavaStateMachine::new)
                .withTransport(new LocalTransport(new LocalServerRegistry()))
                .withStorage(Storage.builder()
                    .withDirectory(logsDirectory)
                    .withStorageLevel(StorageLevel.DISK)
                    .build())
                .build();
    }
    
    protected void start() {
        init();
        copycatServer.bootstrap().join();
    }
    
    protected void stop() {
        try {
            copycatServer.shutdown().get(1, TimeUnit.MINUTES);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            Throwables.propagate(e);
        }
    }
    
    private String name() {
        return System.getProperty("name") + "-peer";
    }
}
