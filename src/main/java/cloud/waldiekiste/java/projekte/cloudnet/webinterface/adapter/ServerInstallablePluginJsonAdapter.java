/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.service.plugin.PluginResourceType;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;

import java.lang.reflect.Type;

public class ServerInstallablePluginJsonAdapter implements JsonSerializer<ServerInstallablePlugin>,JsonDeserializer<ServerInstallablePlugin> {
    @Override
    public ServerInstallablePlugin deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject object = jsonElement.getAsJsonObject();
        final String name = object.get("name").getAsString();
        final PluginResourceType pluginResourceType = PluginResourceType.valueOf(object.get("pluginResourceType").getAsString());
        final String url = object.get("url").getAsString();
        return new ServerInstallablePlugin(name,pluginResourceType,url);
    }

    @Override
    public JsonElement serialize(ServerInstallablePlugin serverInstallablePlugin, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("name",serverInstallablePlugin.getName());
        object.addProperty("pluginResourceType",serverInstallablePlugin.getPluginResourceType().name().toUpperCase());
        object.addProperty("url",serverInstallablePlugin.getUrl());
        return object;
    }
}
