package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.proxylayout.TabList;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignLayoutAdapter implements JsonSerializer<SignLayout>, JsonDeserializer<SignLayout> {
    @Override
    public SignLayout deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String name = object.get("name").getAsString();
        List<String> signLayout = new ArrayList<>();
        object.get("signLayout").getAsJsonArray().forEach(t->signLayout.add(t.getAsString()));
        int blockId = object.get("blockId").getAsInt();
        int subId = object.get("subId").getAsInt();
        return new SignLayout(name,signLayout.toArray(new String[0]),blockId,subId);
    }

    @Override
    public JsonElement serialize(SignLayout signLayout, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("name",signLayout.getName());
        JsonArray signLayouts = new JsonArray();
        Arrays.asList(signLayout.getSignLayout()).forEach(signLayouts::add);
        object.add("signLayout",signLayouts);
        object.addProperty("blockId",signLayout.getBlockId());
        object.addProperty("subId",signLayout.getSubId());
        return object;
    }
}
