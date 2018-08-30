/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.server.info.SimpleServerInfo;
import de.dytanic.cloudnet.lib.service.ServiceId;

import java.lang.reflect.Type;

public class ServerInfoJsonAdapter implements JsonSerializer<SimpleServerInfo>,JsonDeserializer<SimpleServerInfo> {
    @Override
    public SimpleServerInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject object = jsonElement.getAsJsonObject();
        final ServiceId serviceId = jsonDeserializationContext.deserialize(object.get("serviceId"), ServiceId.class);
        final String hostName = object.get("hostName").getAsString();
        final int port = object.get("port").getAsInt();
        final int maxplayers = object.get("maxplayers").getAsInt();
        final int onlineCount = object.get("onlineCount").getAsInt();
        return new SimpleServerInfo(serviceId,hostName,port,onlineCount,maxplayers);
    }

    @Override
    public JsonElement serialize(SimpleServerInfo simpleServerInfo, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.add("serviceId",jsonSerializationContext.serialize(simpleServerInfo.getServiceId()));
        object.addProperty("hostName",simpleServerInfo.getHostAddress());
        object.addProperty("port",simpleServerInfo.getPort());
        object.addProperty("maxplayers",simpleServerInfo.getMaxPlayers());
        object.addProperty("onlineCount",simpleServerInfo.getOnlineCount());
        return object;
    }
}
