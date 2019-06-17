package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.proxylayout.Motd;
import java.lang.reflect.Type;

public class MotdJsonAdapter implements JsonSerializer<Motd>, JsonDeserializer<Motd> {

  @Override
  public Motd deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    final String firstLine = object.get("firstLine").getAsString();
    final String secondLine = object.get("secondLine").getAsString();
    return new Motd(firstLine, secondLine);
  }

  @Override
  public JsonElement serialize(Motd motd, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("firstLine", motd.getFirstLine());
    object.addProperty("secondLine", motd.getSecondLine());
    return object;
  }
}
