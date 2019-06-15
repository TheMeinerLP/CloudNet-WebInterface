/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.server.info.SimpleProxyInfo;
import de.dytanic.cloudnet.lib.service.ServiceId;
import java.lang.reflect.Type;

public class ProxyInfoJsonAdapter implements JsonDeserializer<SimpleProxyInfo>,
    JsonSerializer<SimpleProxyInfo> {

  @Override
  public SimpleProxyInfo deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    final JsonObject object = jsonElement.getAsJsonObject();
    final ServiceId serviceId = jsonDeserializationContext
        .deserialize(object.get("serviceId"), ServiceId.class);
    final boolean online = object.get("online").getAsBoolean();
    final String hostName = object.get("hostName").getAsString();
    final int port = object.get("port").getAsInt();
    final int memory = object.get("memory").getAsInt();
    final int onlineCount = object.get("onlineCount").getAsInt();
    return new SimpleProxyInfo(serviceId, online, hostName, port, memory, onlineCount);
  }

  @Override
  public JsonElement serialize(SimpleProxyInfo simpleProxyInfo, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.add("serviceId", jsonSerializationContext.serialize(simpleProxyInfo.getServiceId()));
    object.addProperty("online", simpleProxyInfo.isOnline());
    object.addProperty("hostName", simpleProxyInfo.getHostName());
    object.addProperty("port", simpleProxyInfo.getPort());
    object.addProperty("memory", simpleProxyInfo.getMemory());
    object.addProperty("onlineCount", simpleProxyInfo.getOnlineCount());
    return object;
  }
}
