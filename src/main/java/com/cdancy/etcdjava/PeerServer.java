/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cdancy.etcdjava;

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
 *
 * @author cdancy
 */
final public class PeerServer {
    
    private CopycatServer copycatServer;
    private String host = "0.0.0.0";
    private int port = 2380;
    
    public PeerServer() {
        init();     
    }
    
    public void init() {
        copycatServer = CopycatServer.builder(new Address(host, port))
                .withName("etcd-java-peer")
                .withStateMachine(EtcdJavaStateMachine::new)
                .withTransport(new LocalTransport(new LocalServerRegistry()))
                .withStorage(Storage.builder()
                    .withDirectory(new File("etcd-java-logs"))
                    .withStorageLevel(StorageLevel.DISK)
                    .build())
                .build();
    }
    
    public void start() {
        copycatServer.bootstrap().join();
    }
    
    public void stop() {
        try {
            copycatServer.shutdown().get(1, TimeUnit.MINUTES);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            Throwables.propagate(e);
        }
    }
}
