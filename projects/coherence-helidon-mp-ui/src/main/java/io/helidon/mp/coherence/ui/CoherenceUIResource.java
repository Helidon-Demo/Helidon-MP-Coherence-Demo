/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

package io.helidon.mp.coherence.ui;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * A dynamic resource that shows a link to the static resource.
 */
@Path("/cachemap")
@RequestScoped
public class CoherenceUIResource {
    @Inject
    @ConfigProperty(name = "server.static.classpath.context", defaultValue = "")
    private String context;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getIt() {
        return "<style> body { font-family: Arial; font-size: 15pt;} </style> <html><head/><body><h1>Welcome to Helidon Coherence Demo</h1> <br> <br> <h2>Please click link to enter:  "
                + "<a href=\"" + context + "/cachemap.html\">" + context + "/cachemap.html</a> </h2>"
                + "</body></html>";
    }
}
