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

import co.cask.http.AbstractHttpHandler;
import co.cask.http.HttpResponder;
import com.cdancy.etcdjava.annotations.Endpoint;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * NO-OP for favicon.ico requests.
 * 
 * @author cdancy
 */
@Path("/favicon.ico")
@Produces("image/x-icon")
@Endpoint
public class FaviconEndpoint extends AbstractHttpHandler {
    
    @Named("favicon:ico")
    @GET
    public void favicon(HttpRequest request, HttpResponder responder) {
        responder.sendStatus(HttpResponseStatus.OK);
    }
}
