package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.proxylayout.ServerFallback;

import java.lang.reflect.Type;

public class ServerFallbackJsonAdapter implements JsonSerializer<ServerFallback>,JsonDeserializer<ServerFallback> {
    @Override
    public ServerFallback deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String group = object.get("group").getAsString();
        String permission = object.get("permission").getAsString();
        return new ServerFallback(group,permission);
    }

    @Override
    public JsonElement serialize(ServerFallback serverFallback, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("group",serverFallback.getGroup());
        if (serverFallback.getPermission() == null) {
            object.addProperty("permission","NULL");
        }else{
            object.addProperty("permission",serverFallback.getPermission());
        }

        return object;
    }
}
