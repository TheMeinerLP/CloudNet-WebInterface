package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobItemLayout;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MobConfigJsonAdapter implements JsonSerializer<MobConfig>,
        JsonDeserializer<MobConfig> {

    @Override
    public MobConfig deserialize(JsonElement jsonElement, Type type,
                                 JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        int inventorySize = object.get("inventorySize").getAsInt();
        int startPoint = object.get("startPoint").getAsInt();
        MobItemLayout itemLayout = jsonDeserializationContext
                .deserialize(object.get("itemLayout"), MobItemLayout.class);
        Map<Integer, MobItemLayout> defaultItemInventory = new HashMap<>();
        object.get("defaultItemInventory").getAsJsonArray().forEach(t -> {
            JsonObject item = t.getAsJsonObject();
            defaultItemInventory.put(item.get("key").getAsInt(),
                    jsonDeserializationContext.deserialize(item.get("value"), MobItemLayout.class));
        });
        return new MobConfig(inventorySize, startPoint, itemLayout, defaultItemInventory);
    }

    @Override
    public JsonElement serialize(MobConfig mobConfig, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("inventorySize", mobConfig.getInventorySize());
        object.addProperty("startPoint", mobConfig.getStartPoint());
        object.add("itemLayout", jsonSerializationContext.serialize(mobConfig.getItemLayout()));
        JsonArray defaultItemInventory = new JsonArray();
        mobConfig.getDefaultItemInventory().forEach((x, y) -> {
            JsonObject item = new JsonObject();
            item.addProperty("key", x);
            item.add("value", jsonSerializationContext.serialize(y));
            defaultItemInventory.add(item);
        });
        object.add("defaultItemInventory", defaultItemInventory);
        return object;
    }
}
