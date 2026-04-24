/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.smart.campus.api.resources;

import com.smartcampus.smart.campus.api.models.Sensor;
import com.smartcampus.smart.campus.api.storedata.DataStore;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 *
 * @author Karunyan
 */
@Path("/summary")
@Produces(MediaType.APPLICATION_JSON)
public class summaryResource {
    
    private static final Logger LOGGER = Logger.getLogger(summaryResource.class.getName());
    private final DataStore store = DataStore.getInstance();
    
    @GET
    public Response getSystemSummary()
    {
        LOGGER.info("Fetching summary of the programme from the data store");
        Collection<Sensor> sensors = store.getSensors().values();
        
        int totalRooms = store.getRooms().size();
        int totalSensors = sensors.size();
        int activeSensors = 0;
        int totalValueSum = 0;
        
        for(Sensor s: sensors)
        {
            if("ACTIVE".equalsIgnoreCase(s.getStatus()))
            {
                activeSensors++;
            }
            
            totalValueSum += s.getCurrentValue();
        }
        
        double averageValue = (totalSensors > 0) ? (totalValueSum/totalSensors) : 0.0;
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRooms", totalRooms);
        summary.put("totalSensors", totalSensors);
        summary.put("activesensors", activeSensors);
        summary.put("averageValue", averageValue);
        
        return Response.status(Response.Status.OK).entity(summary).build();
    }
    
}
