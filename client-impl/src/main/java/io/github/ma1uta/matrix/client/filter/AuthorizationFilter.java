/*
 * Copyright sablintolya@gmail.com
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

package io.github.ma1uta.matrix.client.filter;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

import io.github.ma1uta.matrix.client.ConnectionInfo;

import java.io.IOException;
import java.util.Objects;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;

/**
 * Filter to specify custom headers.
 */
@Provider
public class AuthorizationFilter implements ClientRequestFilter {

    private final ConnectionInfo connectionInfo;

    public AuthorizationFilter(ConnectionInfo connectionInfo) {
        this.connectionInfo = Objects.requireNonNull(connectionInfo, "Connection info must be specified.");
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (connectionInfo.getAccessToken() != null) {
            requestContext.getHeaders().putSingle(AUTHORIZATION, "Bearer " + connectionInfo.getAccessToken());
        }
    }
}
