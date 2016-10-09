/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author cdancy
 */
@Path("/v3alpha")
@Produces(MediaType.APPLICATION_JSON)
@Controller
public class Miscellaneous extends AbstractHttpHandler {
    
    @Named("miscellaneous:version")
    @Path("/version")
    @GET
    public void version(HttpRequest request, HttpResponder responder){
        Version version = new Version();
        version.etcdserver = EtcdJavaUtils.serverVersion;
        version.etcdcluster = EtcdJavaUtils.serverVersion;
        responder.sendJson(HttpResponseStatus.OK, version);
    }
    
    @Named("miscellaneous:health")
    @Path("/health")
    @GET
    public void health(HttpRequest request, HttpResponder responder){
        Health health = new Health();
        health.health = String.valueOf(EtcdJavaUtils.isHealthy());
        responder.sendJson(HttpResponseStatus.OK, health);
    }
}
