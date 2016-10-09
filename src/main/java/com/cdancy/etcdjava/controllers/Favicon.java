/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cdancy.etcdjava.controllers;

import co.cask.http.AbstractHttpHandler;
import co.cask.http.HttpResponder;
import com.cdancy.etcdjava.annotations.Controller;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 *
 * @author cdancy
 */
@Path("/favicon.ico")
@Produces("image/x-icon")
@Controller
public class Favicon extends AbstractHttpHandler{
    
    @Named("favicon:ico")
    @GET
    public void favicon(HttpRequest request, HttpResponder responder){
        responder.sendStatus(HttpResponseStatus.OK);
    }
}
