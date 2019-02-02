package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobItemLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MobItemLayoutJsonAdapter implements JsonSerializer<MobItemLayout>, JsonDeserializer<MobItemLayout> {
    @Override
    public MobItemLayout deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        int itemId = object.get("itemId").getAsInt();
        int subId = object.get("subId").getAsInt();
        String display = object.get("display").getAsString();
        List<String> lore = new ArrayList<>();
        object.get("lore").getAsJsonArray().forEach(line -> lore.add(line.getAsString()));
        return new MobItemLayout(itemId,subId,display,lore);
    }

    @Override
    public JsonElement serialize(MobItemLayout mobItemLayout, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("itemId",mobItemLayout.getItemId());
        object.addProperty("subId",mobItemLayout.getSubId());
        object.addProperty("display",mobItemLayout.getDisplay());
        JsonArray lore = new JsonArray();
        mobItemLayout.getLore().forEach(lore::add);
        object.add("lore",lore);
        return object;
    }
}
