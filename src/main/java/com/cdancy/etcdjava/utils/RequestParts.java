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

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Various parts of a `Key` request.
 * 
 * @author cdancy
 */
public class RequestParts {
    
    public final String key;
    public final String value;
    public final String dir;
    public final String path;
    public final Map<String, List<String>> params;

    /**
     * Ctor for RequestParts.
     * 
     * @param key the key of request.
     * @param value the value of the key.
     * @param dir the directory of the key.
     * @param path the path of request.
     * @param params the query parameters of request
     */
    public RequestParts(String key, @Nullable String value, @Nullable String dir, String path, @Nullable Map<String, List<String>> params) {
        this.key = key;
        this.value = value;
        this.dir = dir;
        this.path = path;
        this.params = params;
    }
}
