/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;

import java.lang.reflect.Type;

public class GroupEntitiyJsonAdapter implements JsonSerializer<GroupEntityData>,JsonDeserializer<GroupEntityData> {
    @Override
    public GroupEntityData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        final String group = object.get("group").getAsString();
        final long timeout = object.get("timeout").getAsLong();
        return new GroupEntityData(group,timeout);
    }

    @Override
    public JsonElement serialize(GroupEntityData groupEntityData, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("group",groupEntityData.getGroup());
        object.addProperty("timeout",groupEntityData.getTimeout());
        return object;
    }
}
