/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.smart.campus.api.resources;

import com.smartcampus.smart.campus.api.models.Sensor;
import com.smartcampus.smart.campus.api.models.Room;
import com.smartcampus.smart.campus.api.storedata.DataStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 *
 * @author Karunyan
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();
    
    private static final Logger LOGGER = Logger.getLogger(SensorResource.class.getName());

    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        LOGGER.info("Fetching all sensors from the data store");
        
        Collection<Sensor> allSensors = store.getSensors().values();

        // If no type is provided in the URL, return all sensors
        if (type == null || type.trim().isEmpty()) {
            return Response.ok(allSensors).build();
        }

        // If a type IS provided, filter the list
        List<Sensor> filteredSensors = new ArrayList<>();
        for (Sensor sensor : allSensors) {
            // Check if the sensor type matches the query (ignoring case)
            if (type.equalsIgnoreCase(sensor.getType())) {
                filteredSensors.add(sensor);
            }
        }

        return Response.ok(filteredSensors).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        LOGGER.info("Validating and adding sensors to data store");
        if (sensor == null) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(errorBody("Invalid JSON request or the body is empty")).
                    build();
        }

        if (sensor.getId() == null || sensor.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(errorBody("Sensor ID required.")).
                    build();
        }

        if (store.getSensors().containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT).
                    entity(errorBody("Sensor is alredy exit")).
                    build();
        }

        if (sensor.getRoomId() == null || !store.getRooms().containsKey(sensor.getRoomId())) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(errorBody("Invalid RoomID: The RoomID does not exist in the database")).
                    build();
        }

        store.getSensors().put(sensor.getId(), sensor);
        Room parentRoom = store.getRooms().get(sensor.getRoomId());
        parentRoom.getSensorIds().add(sensor.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Sensor created successfully");
        response.put("room", sensor);
        return Response.status(Response.Status.CREATED).entity(response).build();

    }
    
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId)
    {
        LOGGER.info("Fetching all sensors reads value from the data store");
        return new SensorReadingResource(sensorId);
    }

    private Map<String, String> errorBody(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return body;
    }

}
