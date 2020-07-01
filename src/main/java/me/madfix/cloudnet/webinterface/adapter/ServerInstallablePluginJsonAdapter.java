package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.service.plugin.PluginResourceType;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;

import java.lang.reflect.Type;
import java.util.Locale;

public class ServerInstallablePluginJsonAdapter implements JsonSerializer<ServerInstallablePlugin>,
        JsonDeserializer<ServerInstallablePlugin> {

    @Override
    public ServerInstallablePlugin deserialize(JsonElement jsonElement, Type type,
                                               JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String name = object.get("name").getAsString();
        PluginResourceType pluginResourceType = PluginResourceType
                .valueOf(object.get("pluginResourceType").getAsString());
        String url = object.get("url").getAsString();
        return new ServerInstallablePlugin(name, pluginResourceType, url);
    }

    @Override
    public JsonElement serialize(ServerInstallablePlugin serverInstallablePlugin, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("name", serverInstallablePlugin.getName());
        object.addProperty("pluginResourceType",
                serverInstallablePlugin.getPluginResourceType().name().toUpperCase(Locale.ENGLISH));
        object.addProperty("url", serverInstallablePlugin.getUrl());
        return object;
    }
}
