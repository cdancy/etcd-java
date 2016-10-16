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

package com.cdancy.etcdjava;

import com.cdancy.etcdjava.model.error.ErrorMessage;

/**
 * Errors which can be used for any request type.
 * 
 * @author cdancy
 */
public enum Errors {
    
    KEY_NOT_FOUND(100, "Key not found");
    
    private final int errorCode;
    private final String message;
    
    private Errors(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    /**
     * Convert Errors into an ErrorMessage.
     * 
     * @param cause the reason and/or cause of error.
     * @param index the index of change.
     * @return ErrorMessage representing Error.
     */
    public ErrorMessage asErrorMessage(String cause, int index) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.errorCode = this.errorCode;
        errorMessage.message = this.message;
        errorMessage.cause = cause;
        errorMessage.index = index;
        return errorMessage;
    }
}
