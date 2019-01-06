/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.info.SimpleServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;

import java.lang.reflect.Type;
import java.util.List;

public class ServerInfoJsonAdapter implements JsonSerializer<ServerInfo>{
    @Override
    public JsonElement serialize(ServerInfo simpleServerInfo, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.add("serviceId",jsonSerializationContext.serialize(simpleServerInfo.getServiceId()));
        object.addProperty("host",simpleServerInfo.getHost());
        object.addProperty("port",simpleServerInfo.getPort());
        object.addProperty("maxplayers",simpleServerInfo.getMaxPlayers());
        object.addProperty("onlineCount",simpleServerInfo.getOnlineCount());
        object.addProperty("serverState",simpleServerInfo.getServerState().name().toUpperCase());
        object.add("serverConfig",jsonSerializationContext.serialize(simpleServerInfo.getServerConfig()));
        object.add("template",jsonSerializationContext.serialize(simpleServerInfo.getTemplate()));
        object.addProperty("memory",simpleServerInfo.getMemory());
        object.addProperty("motd",simpleServerInfo.getMotd());
        JsonArray players = new JsonArray();
        simpleServerInfo.getPlayers().forEach(players::add);
        object.add("players",players);
        return object;
    }
}
