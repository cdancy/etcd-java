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

import static com.cdancy.etcdjava.PeerServer.members;

import co.cask.http.AbstractHttpHandler;
import co.cask.http.HttpResponder;
import com.cdancy.etcdjava.annotations.Endpoint;
import com.cdancy.etcdjava.model.members.Members;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * Serves the 'members' endpoint.
 * 
 * @author cdancy
 */
@Path("/{version}/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.WILDCARD)
@Endpoint
public class MembersEndpoint extends AbstractHttpHandler {
    
    /**
     * Lists all members in cluster.
     * 
     * @param request HttpRequest
     * @param responder HttpResponder
     */
    @Named("members:list")
    @GET
    public void list(HttpRequest request, HttpResponder responder) {
        Members members = new Members();
        members.members = members().values();
        responder.sendJson(HttpResponseStatus.OK, members);
    }
    
    /**
     * Add a member to cluster.
     * 
     * @param request HttpRequest
     * @param responder HttpResponder
     */
    @Named("members:add")
    @POST
    public void add(HttpRequest request, HttpResponder responder) {
        
    }
    
    /**
     * Delete member from cluster.
     * 
     * @param request HttpRequest
     * @param responder HttpResponder
     */
    @Named("members:delete")
    @Path("/{id}")
    @DELETE
    public void delete(HttpRequest request, HttpResponder responder, @PathParam("id") String id) {
        
    }
}
