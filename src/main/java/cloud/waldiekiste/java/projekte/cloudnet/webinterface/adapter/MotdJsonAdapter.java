package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.proxylayout.Motd;
import de.dytanic.cloudnet.lib.server.ProxyGroup;

import java.lang.reflect.Type;

public class MotdJsonAdapter implements JsonSerializer<Motd>,JsonDeserializer<Motd> {
    @Override
    public Motd deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        final String firstLine = object.get("firstLine").getAsString();
        final String secondLine = object.get("secondLine").getAsString();
        return new Motd(firstLine,secondLine);
    }

    @Override
    public JsonElement serialize(Motd motd, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("firstLine",motd.getFirstLine());
        object.addProperty("secondLine",motd.getSecondLine());
        return object;
    }
}
