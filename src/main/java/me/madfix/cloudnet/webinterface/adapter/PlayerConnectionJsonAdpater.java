package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.player.PlayerConnection;

import java.lang.reflect.Type;
import java.util.UUID;

public class PlayerConnectionJsonAdpater implements JsonSerializer<PlayerConnection>,
        JsonDeserializer<PlayerConnection> {

    @Override
    public PlayerConnection deserialize(JsonElement jsonElement, Type type,
                                        JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String uuid = object.get("uuid").getAsString();
        String name = object.get("name").getAsString();
        int version = object.get("version").getAsInt();
        String host = object.get("host").getAsString();
        int port = object.get("port").getAsInt();
        boolean onlineMode = object.get("onlineMode").getAsBoolean();
        boolean legacy = object.get("legacy").getAsBoolean();
        return new PlayerConnection(UUID.fromString(uuid), name, version, host, port, onlineMode,
                legacy);
    }

    @Override
    public JsonElement serialize(PlayerConnection playerConnection, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("uuid", playerConnection.getUniqueId().toString());
        object.addProperty("name", playerConnection.getName());
        object.addProperty("version", playerConnection.getVersion());
        object.addProperty("host", playerConnection.getHost());
        object.addProperty("port", playerConnection.getPort());
        object.addProperty("onlineMode", playerConnection.isOnlineMode());
        object.addProperty("legacy", playerConnection.isLegacy());
        return object;
    }
}
