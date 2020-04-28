/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.mp.coherence.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Helper functions to handle different JAX-RS calls. Examples:
 *
 * Get a single entry for a given key:
 * curl -X GET http://ipaddress:8082/get/{key}
 *
 * Get all entries, key value pairs from cache:
 * curl -X GET http://ipaddress:8082/getall
 *
 * Add new entry for key value pair:
 * curl -X POST -H 'Content-Type: application/json' http://ipaddress:8082/getall/coherence/add -d '{"key": "value", "value":"value"}'
 *
 * Delete entry for a given key:
 * curl -X DELETE https://ipaddress:8082/coherence/delete/k2
 *
 * Delete all entries in cache:
 * curl -X DELETE https://ipaddress:8082/coherence/deleteall
 *
 * Message are returned as a JSON object.
 */


@Path("/coherence")
@RequestScoped
public class CoherenceResource {

    // private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

    /**
     * The Coherence provider.
     */
    private final CoherenceProvider coherenceProvider;

    // the coherence cache
    private final CoherenceCache coherenceCache = new CoherenceCache("demo");

    /**
     * Using constructor injection to get a configuration property.
     * By default this gets the value from META-INF/microprofile-config
     *
     * @param coherenceConfig the configured Coherence cluster
     */
    @Inject
    public CoherenceResource(CoherenceProvider coherenceConfig) {
        this.coherenceProvider = coherenceConfig;
    }

    /**
     * Return a wordly greeting message.
     *
     * @return {@link JsonObject}
     */
    @SuppressWarnings("checkstyle:designforextension")
    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getDefaultMessage() {
        return createResponse("World");
    }


    /**
     * Function 1 - get single K-V
     * Return a single K-V with input key
     *    the full path should be /coherence/get/{key}
     * @param name the name to greet
     * @return {@link JsonObject}
     */
    @SuppressWarnings("checkstyle:designforextension")
    @Path("/get/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getOneEntry(@PathParam("name") String name) {
        return this.coherenceCache.getoneKV(name);
    }


    /**
     * Function 2 - get all K-Vs
     * Return all K-Vs from cache
     *    the full path should be /coherence/getall
     * @return {@link JsonObject}
     */
    @SuppressWarnings("checkstyle:designforextension")
    @Path("/getall")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getAllEntry() {
    	return this.coherenceCache.getallKV();
    }

    /**
     * Function 3 - set single K-V
     * Set a single K-V
     *
     * @param newGreeting the new greeting message
     * @return {"result":"success"}
     */
    @SuppressWarnings("checkstyle:designforextension")
    // need to change path param
    @Path("/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // need to check how to consume post json
    public JsonObject addOneEntry(OneEntry oneentry) {
        return this.coherenceCache.putoneKV(oneentry.key,oneentry.value);
    }

    /**
     * Function 4 - delete single K-V
     * Delete a single K-V with input key
     *    the full path should be /coherence/delete/{key}
     * @param name the name to greet
     * @return {"result","success"}
     */
    @SuppressWarnings("checkstyle:designforextension")
    @Path("/delete/{name}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject deleteOneEntry(@PathParam("name") String name) {
        return this.coherenceCache.deleteoneKV(name);
    }

    /**
     * Function 5 - delete ALL K-V - equals to clear cache
     * Delete all K-Vs
     *    the full path should be /coherence/delete/{key}
     * @param name the name to greet
     * @return {"result","success"}
     */
    @SuppressWarnings("checkstyle:designforextension")
    @Path("/deleteall")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject deleteAllEntry() {
        return this.coherenceCache.deleteallKV();
    }

    // helper function to return default response
    private JsonObject createResponse(String message) {
    	return Json.createObjectBuilder()
    		.add("result", message).build();
    }
}