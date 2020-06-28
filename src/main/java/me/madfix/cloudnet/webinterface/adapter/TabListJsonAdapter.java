package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.proxylayout.TabList;

import java.lang.reflect.Type;

public class TabListJsonAdapter implements JsonSerializer<TabList>, JsonDeserializer<TabList> {

    @Override
    public TabList deserialize(JsonElement jsonElement, Type type,
                               JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        boolean enabled = object.get("enabled").getAsBoolean();
        String header = object.get("header").getAsString();
        String footer = object.get("footer").getAsString();
        return new TabList(enabled, header, footer);
    }

    @Override
    public JsonElement serialize(TabList tabList, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("enabled", tabList.isEnabled());
        object.addProperty("header", tabList.getHeader());
        object.addProperty("footer", tabList.getFooter());
        return object;
    }
}
