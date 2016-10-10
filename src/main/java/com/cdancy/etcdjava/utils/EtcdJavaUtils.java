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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.DateFormat;

/**
 * Random static methods/variables used for system-wide purpose.
 * 
 * @author cdancy
 */
public class EtcdJavaUtils {
    
    public static final Gson gson = new GsonBuilder()
             .enableComplexMapKeySerialization()
             .serializeNulls()
             .setDateFormat(DateFormat.LONG)
             .setLenient()
             .create();
    
    public static boolean isHealthy() {
        return true;
    }
    
    public static void setSystemPropertyIfNonExistent(String key, String value) {
        String previousValue = System.getProperty(key);
        if (previousValue == null) {
            System.setProperty(key, value);
        }
    }
}
