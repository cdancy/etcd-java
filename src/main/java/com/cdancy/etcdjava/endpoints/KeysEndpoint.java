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

package com.cdancy.etcdjava.endpoints;

import static com.cdancy.etcdjava.utils.RequestUtils.keyFromRequestParts;
import static com.cdancy.etcdjava.utils.RequestUtils.parseRequest;
import static com.cdancy.etcdjava.PeerServer.distributedMap;

import co.cask.http.AbstractHttpHandler;
import co.cask.http.HttpResponder;
import com.cdancy.etcdjava.Errors;
import com.cdancy.etcdjava.KeyActions;
import com.cdancy.etcdjava.annotations.Endpoint;
import com.cdancy.etcdjava.model.error.ErrorMessage;
import com.cdancy.etcdjava.model.keys.Key;
import com.cdancy.etcdjava.utils.RequestParts;
import com.google.common.collect.Lists;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serves the 'keys' endpoint.
 * 
 * @author cdancy
 */
@Path("/{version}/keys")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.WILDCARD)
@Endpoint
public class KeysEndpoint extends AbstractHttpHandler {
        
    private static final Logger logger = LoggerFactory.getLogger(KeysEndpoint.class);
    private static final String basePath =  KeysEndpoint.class.getAnnotation(Path.class).value().replace("{version}", System.getProperty("version"));
    private static final Charset charset = Charset.forName("UTF-8");
    
    /**
     * Sets a key/value pair.
     * 
     * @param request HttpRequest
     * @param responder HttpResponder
     */
    @Named("keys:set")
    @Path("/**")
    @PUT
    public void setKey(HttpRequest request, HttpResponder responder) {
        RequestParts parts = parseRequest(request.getUri().replaceFirst(basePath, ""), request.getContent().toString(charset)); 
        
        Key key = keyFromRequestParts(parts);
        key.action = KeyActions.SET.toString();
        distributedMap().put(key.node.key, Lists.newArrayList(key));
        responder.sendJson(HttpResponseStatus.OK, key);
    }
    
    /**
     * Gets a value given a key.
     * 
     * @param request HttpRequest
     * @param responder HttpResponder
     */
    @Named("keys:get")
    @Path("/**")
    @GET
    public void getKey(HttpRequest request, HttpResponder responder) {
        RequestParts parts = parseRequest(request.getUri().replaceFirst(basePath, ""), null); 
        
        List<Key> keyList;
        try {
            keyList = distributedMap().get(parts.key).get(1, TimeUnit.MINUTES); 
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.info("Problems encountered GET'ing key '" + parts.key + "'", e);
            keyList = null;
        }
        
        if (keyList != null) {
            Key key = keyList.get(0);
            key.action = KeyActions.GET.toString();
            responder.sendJson(HttpResponseStatus.OK, key);
        } else {
            ErrorMessage errorMessage = Errors.KEY_NOT_FOUND.asErrorMessage(parts.key, -1);
            responder.sendJson(HttpResponseStatus.NOT_FOUND, errorMessage);
        }
    }
    
    /**
     * Deletes a key.
     * 
     * @param request HttpRequest
     * @param responder HttpResponder
     */
    @Named("keys:delete")
    @Path("/**")
    @DELETE
    public void deleteKey(HttpRequest request, HttpResponder responder) {
        RequestParts parts = parseRequest(request.getUri().replaceFirst(basePath, ""), null); 
        
        List<Key> keyList;
        try {
            keyList = distributedMap().remove(parts.key).get(1, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.info("Problems encountered DELETE'ing key '" + parts.key + "'", e);
            keyList = null;
        }
        
        if (keyList != null) {
            Key key = keyFromRequestParts(parts);
            key.action = KeyActions.DELETE.toString();
            key.prevNode = keyList.get(0).node;
            responder.sendJson(HttpResponseStatus.OK, key);
        } else {
            ErrorMessage errorMessage = Errors.KEY_NOT_FOUND.asErrorMessage(parts.key, -1);
            responder.sendJson(HttpResponseStatus.NOT_FOUND, errorMessage);
        }
    }
}
