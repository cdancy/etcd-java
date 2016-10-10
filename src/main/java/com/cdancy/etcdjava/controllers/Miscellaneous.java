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

package com.cdancy.etcdjava.controllers;

import co.cask.http.AbstractHttpHandler;
import co.cask.http.HttpResponder;
import com.cdancy.etcdjava.annotations.Controller;
import com.cdancy.etcdjava.model.miscellaneous.Health;
import com.cdancy.etcdjava.model.miscellaneous.Version;
import com.cdancy.etcdjava.utils.EtcdJavaUtils;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * Handles requests not necessarily tied to an endpoint/controller.
 * 
 * @author cdancy
 */
@Produces(MediaType.APPLICATION_JSON)
@Controller
public class Miscellaneous extends AbstractHttpHandler {
    
    /**
     * Serves the 'version' endpoint.
     * 
     * @param request HttpRequest
     * @param responder HttpResponder
     */
    @Named("miscellaneous:version")
    @Path("/version")
    @GET
    public void version(HttpRequest request, HttpResponder responder) {
        Version version = new Version();
        version.etcdserver = System.getProperty("serverVersion");
        version.etcdcluster = System.getProperty("clusterVersion");
        responder.sendJson(HttpResponseStatus.OK, version);
    }
    
    /**
     * Serves the 'health' endpoint.
     * 
     * @param request HttpRequest
     * @param responder HttpResponder
     */
    @Named("miscellaneous:health")
    @Path("/health")
    @GET
    public void health(HttpRequest request, HttpResponder responder) {
        Health health = new Health();
        health.health = String.valueOf(EtcdJavaUtils.isHealthy());
        responder.sendJson(HttpResponseStatus.OK, health);
    }
    
    /**
     * Serves the 'metrics' endpoint.
     * 
     * @param request HttpRequest
     * @param responder HttpResponder
     */
    @Named("miscellaneous:metrics")
    @Path("/metrics")
    @GET
    public void metrics(HttpRequest request, HttpResponder responder) {
        responder.sendString(HttpResponseStatus.OK, "no-op");
    }
}
