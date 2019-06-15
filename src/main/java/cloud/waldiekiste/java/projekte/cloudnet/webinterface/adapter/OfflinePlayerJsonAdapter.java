/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
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
    final String uniqueId = object.get("uniqueId").getAsString();
    final String name = object.get("name").getAsString();
    final JsonObject metaData = object.get("metaData").getAsJsonObject();
    final long lastLogin = object.get("lastLogin").getAsLong();
    final long firstLogin = object.get("firstLogin").getAsLong();
    final PlayerConnection lastPlayerConnection = jsonDeserializationContext
        .deserialize(object.get("lastPlayerConnection"), PlayerConnection.class);
    final PermissionEntity permissionEntitiy = jsonDeserializationContext
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
