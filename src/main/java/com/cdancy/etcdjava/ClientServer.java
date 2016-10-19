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

import co.cask.http.NettyHttpService;
import com.cdancy.etcdjava.annotations.Endpoint;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import io.atomix.catalyst.transport.Address;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java impl of Etcd client server.
 * 
 * @author cdancy
 */
public final class ClientServer {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientServer.class);

    public final static String DEFAULT_CLIENT_HOST = "127.0.0.1";
    public final static int DEFAULT_CLIENT_PORT = 2379;
    private volatile static Address address;
    
    private static final Reflections endpointsPackage = new Reflections("com.cdancy.etcdjava.endpoints");    
    private volatile NettyHttpService nettyHttpService;
        
    private final String name = System.getProperty("name") + "-client";

    public ClientServer() {
        address = new Address(DEFAULT_CLIENT_HOST, DEFAULT_CLIENT_PORT);
    }
    
    public ClientServer(int port) {
        address = new Address(DEFAULT_CLIENT_HOST, port);
    }
    
    public ClientServer(String host, int port) {
        address = new Address(host, port);
    }
    
    private void init() {
        nettyHttpService = NettyHttpService.builder(name())
                .setHost(address.host())
                .setPort(address.port())
                .addHttpHandlers(getControllers())
                .build();        
    }
    
    protected void start() {
        try {
            init();
            nettyHttpService.startAsync().awaitRunning(1, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            throw Throwables.propagate(e);
        }
    }
    
    protected void stop() {
        try {
            nettyHttpService.stopAsync().awaitTerminated(1, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
    * @return controllers to serve etcd endpoints.
    */
    private ImmutableList getControllers() {
        List<Object> controllers = new ArrayList<>();
        Set<Class<?>> singletons = endpointsPackage.getTypesAnnotatedWith(Endpoint.class);
        for (Class clazz : singletons) {
            try {
                logger.debug("Loading endpoint @ " + clazz.getName());
                controllers.add(clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw Throwables.propagate(e);
            }
        }
        return ImmutableList.copyOf(controllers);
    }
    
    public String name() {
        return name;
    }
    
    public static Address address() {
        return address;
    }
}
