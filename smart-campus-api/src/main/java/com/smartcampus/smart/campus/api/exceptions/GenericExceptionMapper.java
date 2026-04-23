/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.smart.campus.api.exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Karunyan
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    
    
    private static final Logger LOGGER = Logger.getLogger(GenericExceptionMapper.class.getName());
    
    @Override
    public Response toResponse(Throwable exception)
    {
        
        LOGGER.log(Level.SEVERE, "API Error Caught: " + exception.getMessage(), exception);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", 500);
        errorDetails.put("error", "internal Server error");
        errorDetails.put("message", "Something went wrong on our end. Please try again later.");
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                entity(errorDetails).
                type(MediaType.APPLICATION_JSON).
                build();
        
    }
    
    
}
