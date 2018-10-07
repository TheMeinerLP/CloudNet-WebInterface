/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.proxylayout.DynamicFallback;
import de.dytanic.cloudnet.lib.proxylayout.ServerFallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DynamicFallbackJsonAdapter implements JsonSerializer<DynamicFallback>,JsonDeserializer<DynamicFallback> {
    @Override
    public DynamicFallback deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        final String defaultFallback = object.get("defaultFallback").getAsString();
        List<ServerFallback> fallbacks = new ArrayList<>();
        object.get("fallbacks").getAsJsonArray().forEach(t->fallbacks.add(jsonDeserializationContext.deserialize(t,ServerFallback.class)));
        return new DynamicFallback(defaultFallback,fallbacks);
    }

    @Override
    public JsonElement serialize(DynamicFallback dynamicFallback, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("defaultFallback",dynamicFallback.getDefaultFallback());
        JsonArray fallbacks = new JsonArray();
        dynamicFallback.getFallbacks().forEach(t->fallbacks.add(jsonSerializationContext.serialize(t)));
        object.add("fallbacks",fallbacks);
        return object;
    }
}
