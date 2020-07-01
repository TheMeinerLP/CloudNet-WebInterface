package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobPosition;

import java.lang.reflect.Type;

public class MobPositionJsonAdapter implements JsonSerializer<MobPosition>,
        JsonDeserializer<MobPosition> {

    @Override
    public MobPosition deserialize(JsonElement jsonElement, Type type,
                                   JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String group = object.get("group").getAsString();
        String world = object.get("world").getAsString();
        double x = object.get("x").getAsDouble();
        double y = object.get("y").getAsDouble();
        double z = object.get("z").getAsDouble();
        float yaw = object.get("yaw").getAsFloat();
        float pitch = object.get("pitch").getAsFloat();
        return new MobPosition(group, world, x, y, z, yaw, pitch);
    }

    @Override
    public JsonElement serialize(MobPosition position, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("group", position.getGroup());
        object.addProperty("world", position.getWorld());
        object.addProperty("x", position.getX());
        object.addProperty("y", position.getY());
        object.addProperty("z", position.getZ());
        object.addProperty("yaw", position.getYaw());
        object.addProperty("pitch", position.getPitch());
        return object;
    }
}
