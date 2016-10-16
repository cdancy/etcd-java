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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This Java source file was auto generated by running 'gradle buildInit --type java-library'
 * by 'cdancy' at '10/6/16 4:49 PM' with Gradle 2.14.1
 *
 * @author cdancy, @date 10/6/16 4:49 PM
 */
public class EtcdJava {
        
    private static final Logger logger = LoggerFactory.getLogger(EtcdJava.class);
    
    private final ClientServer clientServer;
    private final PeerServer peerServer;
    
    /**
     * Initializes system properties, client, and peer server.
     */
    public EtcdJava() {
        initSystemProperties();
        clientServer = new ClientServer();
        peerServer = new PeerServer();
    }
    
    private void initSystemProperties() {
        EtcdJavaUtils.setSystemPropertyIfAbsent("name", "etcd-java");
        EtcdJavaUtils.setSystemPropertyIfAbsent("version", "v2");
        EtcdJavaUtils.setSystemPropertyIfAbsent("serverVersion", "2.0.0");
        EtcdJavaUtils.setSystemPropertyIfAbsent("clusterVersion", "2.0.0");
    }
    
    /**
     * Start instance of etcd-java.
     */
    public synchronized void start() {
        logger.info("Starting etcd-java ...");
        peerServer.start();
        clientServer.start();
        logger.info("Started etcd-java ...");
    }
    
    /**
     * Stop instance of etcd-java.
     */
    public synchronized void stop() {
        logger.info("Stopping etcd-java ...");
        try {
            clientServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            peerServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Stopping etcd-java ...");
    }
    
    /**
     * Temp main method used for testing.
     * 
     * @param args no-op.
     */
    public static void main(String [] args) {
        
        EtcdJava server = new EtcdJava();
        server.start();
        
        try {
            Thread.sleep(200000);            
        } catch (Exception e) {
            Throwables.propagate(e);
        }
        
        server.stop();
    }
}

