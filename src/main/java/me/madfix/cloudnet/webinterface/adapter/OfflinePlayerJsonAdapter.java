package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.UUID;

public class OfflinePlayerJsonAdapter implements JsonDeserializer<OfflinePlayer>,
        JsonSerializer<OfflinePlayer> {

    @Override
    public OfflinePlayer deserialize(JsonElement jsonElement, Type type,
                                     JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String uniqueId = object.get("uniqueId").getAsString();
        String name = object.get("name").getAsString();
        JsonObject metaData = object.get("metaData").getAsJsonObject();
        long lastLogin = object.get("lastLogin").getAsLong();
        long firstLogin = object.get("firstLogin").getAsLong();
        PlayerConnection lastPlayerConnection = jsonDeserializationContext
                .deserialize(object.get("lastPlayerConnection"), PlayerConnection.class);
        PermissionEntity permissionEntitiy = jsonDeserializationContext
                .deserialize(object.get("permissionEntity"), PermissionEntity.class);
        return new OfflinePlayer(UUID.fromString(uniqueId), name, new Document(metaData), lastLogin,
                firstLogin, lastPlayerConnection, permissionEntitiy);
    }

    @Override
    public JsonElement serialize(OfflinePlayer offlinePlayer, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("uniqueId", offlinePlayer.getUniqueId().toString());
        object.addProperty("name", offlinePlayer.getName());
        object.add("metaData", offlinePlayer.getMetaData().obj());
        object.addProperty("lastLogin", offlinePlayer.getLastLogin());
        object.addProperty("firstLogin", offlinePlayer.getFirstLogin());
        object.add("lastPlayerConnection",
                jsonSerializationContext.serialize(offlinePlayer.getLastPlayerConnection()));
        object.add("permissionEntity",
                jsonSerializationContext.serialize(offlinePlayer.getPermissionEntity()));
        return object;
    }
}
