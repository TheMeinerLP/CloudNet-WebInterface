package me.madfix.projects.java.cloudnet.webinterface.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.serverselectors.sign.Position;
import java.lang.reflect.Type;

public class PositionJsonAdapter implements JsonSerializer<Position>, JsonDeserializer<Position> {

  @Override
  public Position deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    String group = object.get("group").getAsString();
    String world = object.get("world").getAsString();
    double x = object.get("x").getAsDouble();
    double y = object.get("y").getAsDouble();
    double z = object.get("z").getAsDouble();
    return new Position(group, world, x, y, z);
  }

  @Override
  public JsonElement serialize(Position position, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("group", position.getGroup());
    object.addProperty("world", position.getWorld());
    object.addProperty("x", position.getX());
    object.addProperty("y", position.getY());
    object.addProperty("z", position.getZ());
    return object;
  }
}
