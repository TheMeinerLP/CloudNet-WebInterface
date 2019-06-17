package me.madfix.projects.java.cloudnet.webinterface.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.service.ServiceId;
import java.lang.reflect.Type;
import java.util.UUID;

public class ServiceIdJsonAdapter implements JsonSerializer<ServiceId>,
    JsonDeserializer<ServiceId> {

  @Override
  public ServiceId deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    final String group = object.get("group").getAsString();
    final int id = object.get("id").getAsInt();
    final String uniqueId = object.get("uniqueId").getAsString();
    final String wrapperId = object.get("wrapperId").getAsString();
    final String serverId = object.get("serverId").getAsString();
    return new ServiceId(group, id, UUID.fromString(uniqueId), wrapperId, serverId);
  }

  @Override
  public JsonElement serialize(ServiceId serviceId, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("group", serviceId.getGroup());
    object.addProperty("id", serviceId.getId());
    object.addProperty("uniqueId", serviceId.getUniqueId().toString());
    object.addProperty("wrapperId", serviceId.getWrapperId());
    object.addProperty("serverId", serviceId.getServerId());
    return object;
  }
}
