package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import java.lang.reflect.Type;

public class ServerProcessMetaJsonAdapter implements JsonSerializer<ServerProcessMeta> {

  @Override
  public JsonElement serialize(ServerProcessMeta serverProcessMeta, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.add("serviceId", jsonSerializationContext.serialize(serverProcessMeta.getServiceId()));
    object.addProperty("memory", serverProcessMeta.getMemory());
    object.addProperty("priorityStop", serverProcessMeta.isPriorityStop());
    object.addProperty("url", serverProcessMeta.getUrl());
    JsonArray array = new JsonArray();
    for (String s : serverProcessMeta.getProcessParameters()) {
      array.add(s);
    }
    object.add("processParameters", array);
    object.addProperty("onlineMode", serverProcessMeta.isOnlineMode());
    JsonArray downloadablePlugins = new JsonArray();
    serverProcessMeta.getDownloadablePlugins()
        .forEach(t -> downloadablePlugins.add(jsonSerializationContext.serialize(t)));
    object.add("downloadablePlugins", downloadablePlugins);
    object.add("serverConfig",
        jsonSerializationContext.serialize(serverProcessMeta.getServerConfig()));
    object.addProperty("customServerDownload", serverProcessMeta.getCustomServerDownload());
    object.addProperty("port", serverProcessMeta.getPort());
    object.add("template", jsonSerializationContext.serialize(serverProcessMeta.getTemplate()));
    return object;
  }
}
