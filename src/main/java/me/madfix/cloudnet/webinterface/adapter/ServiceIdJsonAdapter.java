package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.service.ServiceId;

import java.lang.reflect.Type;
import java.util.UUID;

public class ServiceIdJsonAdapter implements JsonSerializer<ServiceId>,
        JsonDeserializer<ServiceId> {

    @Override
    public ServiceId deserialize(JsonElement jsonElement, Type type,
                                 JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String group = object.get("group").getAsString();
        int id = object.get("id").getAsInt();
        String uniqueId = object.get("uniqueId").getAsString();
        String wrapperId = object.get("wrapperId").getAsString();
        String serverId = object.get("serverId").getAsString();
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
