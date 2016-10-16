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

import com.google.common.base.Throwables;
import io.atomix.AtomixReplica;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.local.LocalServerRegistry;
import io.atomix.catalyst.transport.local.LocalTransport;
import io.atomix.collections.DistributedMap;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;
import java.io.File;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Java impl of Etcd peer server.
 * 
 * @author cdancy
 */
public final class PeerServer {
    
    private volatile AtomixReplica atomixReplica;
    private String host = "0.0.0.0";
    private int port = 2380;
    private File logsDirectory = new File(System.getProperty("user.dir") + "/default.etcd");
    private static volatile DistributedMap<String, String> mapResource = null;
    
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
        atomixReplica = AtomixReplica.builder(new Address(host, port))
                .withTransport(new LocalTransport(new LocalServerRegistry()))
                .withStorage(Storage.builder()
                    .withDirectory(logsDirectory)
                    .withStorageLevel(StorageLevel.DISK)
                    .withMinorCompactionInterval(Duration.ofSeconds(30))
                    .withMajorCompactionInterval(Duration.ofMinutes(1))
                    .withMaxSegmentSize(1024 * 1024 * 8)
                    .withMaxEntriesPerSegment(1024 * 8)
                    .build())
                .build();
        atomixReplica.serializer().disableWhitelist();
    }
    
    protected void start() {
        init();
        atomixReplica.bootstrap().join();
        mapResource = atomixReplica.<String, String>getMap(name()).join();
    }
    
    protected void stop() {
        try {
            atomixReplica.shutdown().get(1, TimeUnit.MINUTES);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            Throwables.propagate(e);
        }
    }
    
    private String name() {
        return System.getProperty("name") + "-peer";
    }
    
    public static DistributedMap<String, String> sharedResource() {
        return mapResource;
    }
}
