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
import com.cdancy.etcdjava.annotations.Controller;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
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
    
    private static final Reflections controllersPackage = new Reflections("com.cdancy.etcdjava.controllers");
    private static final Logger logger = LoggerFactory.getLogger(ClientServer.class);
    
    private volatile NettyHttpService nettyHttpService;
    private int port = 2379;
        
    public ClientServer() {

    }
    
    public ClientServer(int port) {
        this.port = port;
    }
    
    private void init() {
        nettyHttpService = NettyHttpService.builder(name())
                .setPort(port)
                .addHttpHandlers(getControllers())
                .build();        
    }
    
    protected void start() {
        try {
            init();
            nettyHttpService.startAsync().awaitRunning(1, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            Throwables.propagate(e);
        }
    }
    
    protected void stop() {
        try {
            nettyHttpService.stopAsync().awaitTerminated(1, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            Throwables.propagate(e);
        }
    }

    /**
    * @return controllers to serve etcd endpoints.
    */
    private ImmutableList getControllers() {
        List<Object> controllers = new ArrayList<>();
        Set<Class<?>> singletons = controllersPackage.getTypesAnnotatedWith(Controller.class);
        for (Class clazz : singletons) {
            try {
                logger.debug("Loading controller @ " + clazz.getName());
                controllers.add(clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException ie) {
                Throwables.propagate(ie);
            }
        }
        return ImmutableList.copyOf(controllers);
    }
    
    public String name() {
        return System.getProperty("name") + "-client";
    }
}
