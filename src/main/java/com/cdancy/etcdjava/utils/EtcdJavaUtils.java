/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cdancy.etcdjava.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.DateFormat;

/**
 *
 * @author cdancy
 */
public class EtcdJavaUtils {
    
    public static final String version = "v3";
    public static final String serverVersion = "3.0.0";
    public static final String clusterVersion = "3.0.0";
    
    public static final Gson gson = new GsonBuilder()
             .enableComplexMapKeySerialization()
             .serializeNulls()
             .setDateFormat(DateFormat.LONG)
             .setLenient()
             .create();
    
    public static boolean isHealthy() {
        return true;
    }
}
