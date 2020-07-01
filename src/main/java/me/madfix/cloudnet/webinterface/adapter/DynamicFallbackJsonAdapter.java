package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.proxylayout.DynamicFallback;
import de.dytanic.cloudnet.lib.proxylayout.ServerFallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Translate the DynamicFallback into json and back.
 */
public class DynamicFallbackJsonAdapter implements JsonSerializer<DynamicFallback>,
        JsonDeserializer<DynamicFallback> {

    /**
     * Translate the json object into java class.
     *
     * @param jsonElement                the json object
     * @param type                       the type of the json object
     * @param jsonDeserializationContext Other json adapters
     * @return The Java class
     * @throws JsonParseException is the json object incorrect
     */
    @Override
    public DynamicFallback deserialize(JsonElement jsonElement, Type type,
                                       JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String defaultFallback = object.get("defaultFallback").getAsString();
        List<ServerFallback> fallbacks = new ArrayList<>();
        object.get("fallbacks").getAsJsonArray().forEach(
                t -> fallbacks.add(jsonDeserializationContext.deserialize(t, ServerFallback.class)));
        return new DynamicFallback(defaultFallback, fallbacks);
    }

    /**
     * Translate the java class into json object.
     *
     * @param dynamicFallback          the java class with values
     * @param type                     the type of the java class
     * @param jsonSerializationContext other json adapters
     * @return the json object
     */
    @Override
    public JsonElement serialize(DynamicFallback dynamicFallback, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("defaultFallback", dynamicFallback.getDefaultFallback());
        JsonArray fallbacks = new JsonArray();
        dynamicFallback.getFallbacks()
                .forEach(t -> fallbacks.add(jsonSerializationContext.serialize(t)));
        object.add("fallbacks", fallbacks);
        return object;
    }
}
