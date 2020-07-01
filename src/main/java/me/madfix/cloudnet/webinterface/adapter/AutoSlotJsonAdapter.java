package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.proxylayout.AutoSlot;

import java.lang.reflect.Type;

/**
 * Translate the Autoslot class into json and back.
 */
public class AutoSlotJsonAdapter implements JsonSerializer<AutoSlot>, JsonDeserializer<AutoSlot> {

    /**
     * Translate the json string into JavaClass object.
     *
     * @param jsonElement                The string as JsonOpject
     * @param type                       The type of the json object
     * @param jsonDeserializationContext Other json adapter
     * @return The class with parameters of the string
     * @throws JsonParseException is the string incorrect
     */
    @Override
    public AutoSlot deserialize(JsonElement jsonElement, Type type,
                                JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        int dynamicSlotSize = object.get("dynamicSlotSize").getAsInt();
        boolean enabled = object.get("enabled").getAsBoolean();
        return new AutoSlot(dynamicSlotSize, enabled);
    }

    /**
     * Translate the java class into json object.
     *
     * @param autoSlot                 The class to translate into json
     * @param type                     The type og the class
     * @param jsonSerializationContext Other json adapters
     * @return The json object
     */
    @Override
    public JsonElement serialize(AutoSlot autoSlot, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("dynamicSlotSize", autoSlot.getDynamicSlotSize());
        object.addProperty("enabled", autoSlot.isEnabled());
        return object;
    }
}
