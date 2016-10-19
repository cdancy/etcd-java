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

package com.cdancy.etcdjava.utils;

import com.cdancy.etcdjava.model.keys.Key;
import com.cdancy.etcdjava.model.keys.Node;
import java.util.List;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

/**
 * Static methods/variables used for http requests.
 * 
 * @author cdancy
 */
public class RequestUtils {
    
    /**
     * Creates a bare bones `Key` object from RequestParts.
     * 
     * @param requestParts parts of `Key` request.
     * @return Key object.
     */
    public static Key keyFromRequestParts(RequestParts requestParts) {
        Key key = new Key();
        key.node = new Node();
        key.node.key = requestParts.key;
        key.node.value = requestParts.value;
        key.node.createdIndex = 4;
        key.node.modifiedIndex = 4;
        return key;
    }
    
    /**
     * Parse a RequestParts object from the parts of a request.
     * 
     * @param path path of request.
     * @param body the body of the request.
     * @return RequestParts object.
     */
    public static RequestParts parseRequest(String path, String body) {
        
        QueryStringDecoder decoder = new QueryStringDecoder(path);
        String initialPath = decoder.getPath();
        int index = initialPath.lastIndexOf("/");
        String dir = (index > 0) ? initialPath.substring(0, index) : null;
        String key = initialPath.substring(index, initialPath.length());
        
        String value;
        if (body != null) {
            decoder = new QueryStringDecoder("?" + body);
            List<String> values = decoder.getParameters().get("value");
            if (values.size() == 1) {
                value = values.get(0);
            } else {
                value = null;
            }
        } else { 
            value = null; 
        }
        return new RequestParts(key, value, dir, path, decoder.getParameters());
    }
}
