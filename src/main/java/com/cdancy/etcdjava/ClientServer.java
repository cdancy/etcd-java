/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author cdancy
 */
final public class ClientServer {
    
    private final static Reflections controllersPackage = new Reflections("com.cdancy.etcdjava.controllers");
    private static final Logger logger = LoggerFactory.getLogger(ClientServer.class);
    
    private NettyHttpService nettyHttpService;
    private int port = 2379;
        
    public ClientServer() {
        init();
    }
    
    public void init() {
        nettyHttpService = NettyHttpService.builder("etcd-java-client")
                .setPort(port)
                .addHttpHandlers(getControllers())
                .build();        
    }
    
    public void start() {
        try {
            nettyHttpService.startAsync().awaitRunning(1, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            Throwables.propagate(e);
        }
    }
    
    public void stop() {
        try {
            nettyHttpService.stopAsync().awaitTerminated(1, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            Throwables.propagate(e);
        }
    }

    /**
    * @return controllers to serve etcd endpoints
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
}
