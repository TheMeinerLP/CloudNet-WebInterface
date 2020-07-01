package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;

import java.lang.reflect.Type;

/**
 * Translate the group entity into json and back.
 */
public class GroupEntityJsonAdapter implements JsonSerializer<GroupEntityData>,
        JsonDeserializer<GroupEntityData> {

    /**
     * Translate the json into java class.
     *
     * @param jsonElement                The json object with values for the java class
     * @param type                       the json type
     * @param jsonDeserializationContext Other json adapters
     * @return The java class
     * @throws JsonParseException is the json string incorrect
     */
    @Override
    public GroupEntityData deserialize(JsonElement jsonElement, Type type,
                                       JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String group = object.get("group").getAsString();
        long timeout = object.get("timeout").getAsLong();
        return new GroupEntityData(group, timeout);
    }

    /**
     * Translate the java class into json object.
     *
     * @param groupEntityData          The java class with all values
     * @param type                     the type of the class
     * @param jsonSerializationContext Other json adapters
     * @return The json object
     */
    @Override
    public JsonElement serialize(GroupEntityData groupEntityData, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("group", groupEntityData.getGroup());
        object.addProperty("timeout", groupEntityData.getTimeout());
        return object;
    }
}
