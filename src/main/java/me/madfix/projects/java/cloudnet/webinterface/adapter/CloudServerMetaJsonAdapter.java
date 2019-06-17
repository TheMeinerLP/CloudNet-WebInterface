package me.madfix.projects.java.cloudnet.webinterface.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import java.lang.reflect.Type;
import java.util.Locale;

public class CloudServerMetaJsonAdapter implements JsonSerializer<CloudServerMeta> {

  @Override
  public JsonElement serialize(CloudServerMeta cloudServerMeta, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.add("serviceId", jsonSerializationContext.serialize(cloudServerMeta.getServiceId()));
    object.addProperty("memory", cloudServerMeta.getMemory());
    object.addProperty("priorityStop", cloudServerMeta.isPriorityStop());
    JsonArray array = new JsonArray();
    for (String s : cloudServerMeta.getProcessParameters()) {
      array.add(s);
    }
    object.add("processParameters", array);
    JsonArray downloadablePlugins = new JsonArray();
    cloudServerMeta.getPlugins()
        .forEach(t -> downloadablePlugins.add(jsonSerializationContext.serialize(t)));
    object.add("plugins", downloadablePlugins);
    object
        .add("serverConfig", jsonSerializationContext.serialize(cloudServerMeta.getServerConfig()));
    object.addProperty("port", cloudServerMeta.getPort());
    object.addProperty("templateName", cloudServerMeta.getTemplateName());
    object
        .addProperty("serverGroupType", cloudServerMeta.getServerGroupType().name()
            .toUpperCase(Locale.ENGLISH));
    object.add("template", jsonSerializationContext.serialize(cloudServerMeta.getTemplate()));
    return object;
  }
}
