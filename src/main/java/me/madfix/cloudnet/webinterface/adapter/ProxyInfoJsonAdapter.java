package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.server.info.SimpleProxyInfo;
import de.dytanic.cloudnet.lib.service.ServiceId;

import java.lang.reflect.Type;

public class ProxyInfoJsonAdapter implements JsonDeserializer<SimpleProxyInfo>,
        JsonSerializer<SimpleProxyInfo> {

    @Override
    public SimpleProxyInfo deserialize(JsonElement jsonElement, Type type,
                                       JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        ServiceId serviceId = jsonDeserializationContext
                .deserialize(object.get("serviceId"), ServiceId.class);
        boolean online = object.get("online").getAsBoolean();
        String hostName = object.get("hostName").getAsString();
        int port = object.get("port").getAsInt();
        int memory = object.get("memory").getAsInt();
        int onlineCount = object.get("onlineCount").getAsInt();
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
