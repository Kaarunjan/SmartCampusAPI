/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.smart.campus.api.resources;

import com.smartcampus.smart.campus.api.models.Sensor;
import com.smartcampus.smart.campus.api.models.Room;
import com.smartcampus.smart.campus.api.models.SensorReading;
import com.smartcampus.smart.campus.api.storedata.DataStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Karunyan
 */

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final DataStore store = DataStore.getInstance();

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSensorReading(SensorReading sensorReading) {
        if (sensorReading == null) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(errorBody("Invalid JSON request or the body is empty")).
                    build();
        }

        Sensor parentSensor = store.getSensors().get(this.sensorId);
        if (parentSensor == null) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(errorBody("Sensor not found")).
                    build();
        }
        
        if (sensorReading.getId() == null || sensorReading.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(errorBody("Event ID (UUID) required")).
                    build();
        }

        if (sensorReading.getTimestamp() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(errorBody("Time Stamp required")).
                    build();
        }

        if (sensorReading.getValue() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(errorBody("Value Should Not be 0")).
                    build();
        }
        
        parentSensor.setCurrentValue(sensorReading.getValue());

        List<SensorReading> readings = store.getSensorReadings().getOrDefault(this.sensorId, new ArrayList<>());
        readings.add(sensorReading);
        store.getSensorReadings().put(this.sensorId, readings);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Sensor Reading added successfully");
        response.put("room", sensorReading);
        return Response.status(Response.Status.CREATED).entity(response).build();

    }

    @GET
    public Response getResponseHistory() 
    {
        List<SensorReading> readings = store.getSensorReadings()
                .getOrDefault(this.sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }

    private Map<String, String> errorBody(String message) 
    {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return body;
    }

}
