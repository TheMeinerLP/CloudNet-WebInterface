package me.madfix.projects.java.cloudnet.webinterface.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.serverselectors.sign.Position;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import java.lang.reflect.Type;

public class SignJsonAdapter implements JsonSerializer<Sign>, JsonDeserializer<Sign> {

  @Override
  public Sign deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    String targetGroup = object.get("targetGroup").getAsString();
    Position position = jsonDeserializationContext
        .deserialize(object.get("position"), Position.class);
    return new Sign(targetGroup, position);
  }

  @Override
  public JsonElement serialize(Sign sign, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("uniqueId", sign.getUniqueId().toString());
    object.addProperty("targetGroup", sign.getTargetGroup());
    object.add("position", jsonSerializationContext.serialize(sign.getPosition()));
    return object;
  }
}
