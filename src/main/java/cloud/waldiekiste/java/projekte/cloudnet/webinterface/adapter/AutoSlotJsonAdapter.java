package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.proxylayout.AutoSlot;

import java.lang.reflect.Type;

public class AutoSlotJsonAdapter implements JsonSerializer<AutoSlot>,JsonDeserializer<AutoSlot> {
    @Override
    public AutoSlot deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        final int dynamicSlotSize = object.get("dynamicSlotSize").getAsInt();
        final boolean enabled = object.get("enabled").getAsBoolean();
        return new AutoSlot(dynamicSlotSize,enabled);
    }

    @Override
    public JsonElement serialize(AutoSlot autoSlot, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("dynamicSlotSize",autoSlot.getDynamicSlotSize());
        object.addProperty("enabled",autoSlot.isEnabled());
        return object;
    }
}
