/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.smart.campus.api.resources;


import com.smartcampus.smart.campus.api.models.Room;
import com.smartcampus.smart.campus.api.storedata.DataStore;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;
/**
 *
 * @author Karunyan
 */

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    
    private static final Logger LOGGER = Logger.getLogger(RoomResource.class.getName());
    private final DataStore store = DataStore.getInstance();

    // GET /api/v1/rooms
    @GET
    public Response getAllRooms() {
        LOGGER.info("Fetching all rooms from the data store");
        return Response.ok(store.getRooms().values()).build();
    }

    // POST /api/v1/rooms
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        LOGGER.info("Validatinga and adding the rooms to data store");
        if (room == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(errorBody("Invalid JSON or empty request body")).build();
        }
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody("Room ID is required")).build();
        }
        if (store.getRooms().containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorBody("Room already exists")).build();
        }
        store.getRooms().put(room.getId(), room);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Room created successfully");
        response.put("room", room);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    // GET /api/v1/rooms/{roomId}
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = store.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody("Room not found: " + roomId)).build();
        }
        return Response.ok(room).build();
    }

    // DELETE /api/v1/rooms/{roomId}
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        LOGGER.info("Processing the action for delete");
        
        Room room = store.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody("Room not found: " + roomId)).build();
        }
        if (!room.getSensorIds().isEmpty()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorBody("Cannot delete room - it still has sensors assigned")).build();
        }
        store.getRooms().remove(roomId);
        return Response.noContent().build();
    }

    private Map<String, String> errorBody(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return body;
    }
    
    
}
