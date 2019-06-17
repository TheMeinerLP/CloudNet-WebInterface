package me.madfix.projects.java.cloudnet.webinterface.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PermissionEntitiyJsonAdapter implements JsonSerializer<PermissionEntity>,
    JsonDeserializer<PermissionEntity> {

  @Override
  public PermissionEntity deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    final String uniqueId = object.get("uniqueId").getAsString();
    HashMap<String, Boolean> permissions = new HashMap<>();
    object.get("permissions").getAsJsonArray().forEach(t -> {
      JsonObject permission = t.getAsJsonObject();
      permissions.put(permission.get("key").getAsString(), permission.get("value").getAsBoolean());
    });
    String prefix = null;
    if (!object.get("prefix").isJsonNull()) {
      prefix = object.get("prefix").getAsString();
    }
    String suffix = null;
    if (!object.get("suffix").isJsonNull()) {
      suffix = object.get("suffix").getAsString();
    }
    List<GroupEntityData> groups = new ArrayList<>();
    object.get("groups").getAsJsonArray().forEach(t -> {
      JsonObject group = t.getAsJsonObject();
      GroupEntityData data = new GroupEntityData(group.get("key").getAsString(),
          group.get("value").getAsLong());
      groups.add(data);
    });
    return new PermissionEntity(UUID.fromString(uniqueId), permissions, prefix, suffix, groups);
  }

  @Override
  public JsonElement serialize(PermissionEntity permissionEntity, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("uniqueId", permissionEntity.getUniqueId().toString());
    JsonArray permissions = new JsonArray();
    permissionEntity.getPermissions().forEach((k, v) -> {
      JsonObject permission = new JsonObject();
      permission.addProperty("key", k);
      permission.addProperty("value", v);
      permissions.add(permission);
    });
    object.add("permissions", permissions);
    object.addProperty("prefix", permissionEntity.getPrefix());
    object.addProperty("suffix", permissionEntity.getSuffix());
    JsonArray groups = new JsonArray();
    permissionEntity.getGroups().forEach(t -> {
      JsonObject group = new JsonObject();
      group.addProperty("key", t.getGroup());
      group.addProperty("value", t.getTimeout());
      groups.add(group);
    });
    object.add("groups", groups);
    return object;
  }
}
