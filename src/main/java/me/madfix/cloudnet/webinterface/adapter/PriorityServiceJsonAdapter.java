package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.server.priority.PriorityConfig;
import de.dytanic.cloudnet.lib.server.priority.PriorityService;

import java.lang.reflect.Type;

public class PriorityServiceJsonAdapter implements JsonSerializer<PriorityService>,
        JsonDeserializer<PriorityService> {

    @Override
    public PriorityService deserialize(JsonElement jsonElement, Type type,
                                       JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        int stopTimeInSeconds = object.get("stopTimeInSeconds").getAsInt();
        PriorityConfig global = jsonDeserializationContext
                .deserialize(object.get("global"), PriorityConfig.class);
        PriorityConfig group = jsonDeserializationContext
                .deserialize(object.get("group"), PriorityConfig.class);
        return new PriorityService(stopTimeInSeconds, global, group);
    }

    @Override
    public JsonElement serialize(PriorityService priorityService, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("stopTimeInSeconds", priorityService.getStopTimeInSeconds());
        object.add("global", jsonSerializationContext.serialize(priorityService.getGlobal()));
        object.add("group", jsonSerializationContext.serialize(priorityService.getGroup()));
        return object;
    }
}
