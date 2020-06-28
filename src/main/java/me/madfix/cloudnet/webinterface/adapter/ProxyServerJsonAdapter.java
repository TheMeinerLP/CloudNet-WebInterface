package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

import java.lang.reflect.Type;

public class ProxyServerJsonAdapter implements JsonSerializer<ProxyServer> {

    @Override
    public JsonElement serialize(ProxyServer proxyServer, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.add("serviceId", jsonSerializationContext.serialize(proxyServer.getServiceId()));
        object.add("networkInfo", jsonSerializationContext.serialize(proxyServer.getNetworkInfo()));
        object.add("lastProxyInfo",
                jsonSerializationContext.serialize(proxyServer.getLastProxyInfo().toSimple()));
        object.add("processMeta", jsonSerializationContext.serialize(proxyServer.getProcessMeta()));
        return object;
    }
}
