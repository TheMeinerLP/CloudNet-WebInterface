package me.madfix.projects.java.cloudnet.webinterface.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import java.lang.reflect.Type;

public class WrapperAdpater implements JsonSerializer<Wrapper> {


  @Override
  public JsonElement serialize(Wrapper wrapper, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.add("wrapperInfo", jsonSerializationContext.serialize(wrapper.getWrapperInfo()));
    object.add("networkInfo", jsonSerializationContext.serialize(wrapper.getNetworkInfo()));
    object.addProperty("cpuUsage", wrapper.getCpuUsage());
    object.addProperty("usedMemory", wrapper.getUsedMemory());
    object.addProperty("usedMemoryAndWaiting", wrapper.getUsedMemoryAndWaitings());
    object.addProperty("serverId", wrapper.getServerId());
    JsonArray servers = new JsonArray();
    wrapper.getServers().values().forEach(t -> servers.add(jsonSerializationContext.serialize(t)));
    object.add("servers", servers);
    JsonArray proxys = new JsonArray();
    wrapper.getProxys().values().forEach(t -> proxys.add(jsonSerializationContext.serialize(t)));
    object.add("proxys", proxys);
    JsonArray cloudServers = new JsonArray();
    wrapper.getCloudServers().values()
        .forEach(t -> cloudServers.add(jsonSerializationContext.serialize(t)));
    object.add("cloudServers", cloudServers);
    object.addProperty("queue", wrapper.getWaitingServices().size());
    return object;
  }
}
