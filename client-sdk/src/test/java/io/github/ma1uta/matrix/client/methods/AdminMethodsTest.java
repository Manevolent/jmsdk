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

package io.github.ma1uta.matrix.client.methods;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.ma1uta.matrix.client.model.admin.AdminResponse;
import io.github.ma1uta.matrix.client.model.admin.ConnectionInfo;
import io.github.ma1uta.matrix.client.model.admin.DeviceInfo;
import io.github.ma1uta.matrix.client.model.admin.SessionInfo;
import io.github.ma1uta.matrix.client.test.ConfigurableServlet;
import io.github.ma1uta.matrix.client.test.MockServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;

class AdminMethodsTest extends MockServer {

    @Test
    public void secured() {
        assertThrows(IllegalArgumentException.class, () -> getMatrixClient().turnServers().turnServers());
    }

    @Test
    public void whois() throws Exception {
        ConfigurableServlet.get = (req, res) -> {
            assertTrue(req.getRequestURI().startsWith("/_matrix/client/r0/admin/whois/%40peter%3Arabbit.rocks"));

            if (authenticated(req, res)) {
                try {
                    res.setContentType(MediaType.APPLICATION_JSON);
                    PrintWriter writer = res.getWriter();
                    writer.println("{\n" +
                        "  \"user_id\": \"@peter:rabbit.rocks\",\n" +
                        "  \"devices\": {\n" +
                        "    \"teapot\": {\n" +
                        "      \"sessions\": [\n" +
                        "        {\n" +
                        "          \"connections\": [\n" +
                        "            {\n" +
                        "              \"ip\": \"127.0.0.1\",\n" +
                        "              \"last_seen\": 1411996332123,\n" +
                        "              \"user_agent\": \"curl/7.31.0-DEV\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "              \"ip\": \"10.0.0.2\",\n" +
                        "              \"last_seen\": 1411996332123,\n" +
                        "              \"user_agent\": \"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                        " Chrome/37.0.2062.120 Safari/537.36\"\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}");
                } catch (IOException e) {
                    fail();
                }
            }
        };

        getMatrixClient().getDefaultParams().accessToken(ACCESS_TOKEN);
        AdminResponse res = getMatrixClient().admin().whois("@peter:rabbit.rocks").get(1000, TimeUnit.MILLISECONDS);
        assertNotNull(res);
        assertEquals("@peter:rabbit.rocks", res.getUserId());
        Map<String, DeviceInfo> devices = res.getDevices();
        assertNotNull(devices);
        assertEquals(1, devices.keySet().size());
        DeviceInfo teapot = devices.get("teapot");
        assertNotNull(teapot);
        List<SessionInfo> sessions = teapot.getSessions();
        assertNotNull(sessions);
        assertEquals(1, sessions.size());
        SessionInfo sessionInfo = sessions.get(0);
        assertNotNull(sessionInfo);
        List<ConnectionInfo> connections = sessionInfo.getConnections();
        assertNotNull(connections);
        assertEquals(2, connections.size());

        ConnectionInfo connectionInfo = connections.get(0);
        assertNotNull(connectionInfo);
        assertEquals("127.0.0.1", connectionInfo.getIp());
        assertEquals(1411996332123L, connectionInfo.getLastSeen().longValue());
        assertEquals("curl/7.31.0-DEV", connectionInfo.getUserAgent());

        connectionInfo = connections.get(1);
        assertNotNull(connectionInfo);
        assertEquals("10.0.0.2", connectionInfo.getIp());
        assertEquals(1411996332123L, connectionInfo.getLastSeen().longValue());
        assertEquals("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36",
            connectionInfo.getUserAgent());
    }
}
