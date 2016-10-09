/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cdancy.etcdjava.controllers;

import co.cask.http.AbstractHttpHandler;
import com.cdancy.etcdjava.annotations.Controller;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author cdancy
 */
@Path("/v3alpha/maintenance")
@Produces(MediaType.APPLICATION_JSON)
@Controller
public class Maintenance extends AbstractHttpHandler{
    
}
