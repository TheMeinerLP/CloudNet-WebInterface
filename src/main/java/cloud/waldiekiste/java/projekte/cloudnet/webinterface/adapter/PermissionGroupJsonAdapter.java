/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionGroupJsonAdapter implements JsonDeserializer<PermissionGroup>,
    JsonSerializer<PermissionGroup> {

  @Override
  public PermissionGroup deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    final String name = object.get("name").getAsString();
    final String color = object.get("color").getAsString();
    final String prefix = object.get("prefix").getAsString();
    final String suffix = object.get("suffix").getAsString();
    final String display = object.get("display").getAsString();
    final int tagId = object.get("tagId").getAsInt();
    final int joinPower = object.get("joinPower").getAsInt();
    final boolean defaultGroup = object.get("defaultGroup").getAsBoolean();

    HashMap<String, Boolean> permissions = new HashMap<>();
    object.get("permissions").getAsJsonArray()
        .forEach(perm -> permissions.put(perm.getAsJsonObject().get("key")
            .getAsString(), perm.getAsJsonObject().get("value").getAsBoolean()));
    JsonArray serverGroupPermissions1 = object.get("serverGroupPermissions").getAsJsonArray();
    Map<String, List<String>> serverGroupPermissions = new HashMap<>();
    serverGroupPermissions1.forEach(t -> {
      JsonObject object1 = t.getAsJsonObject();
      String servername = object1.get("key").getAsString();
      List<String> perms = new ArrayList<>();
      object1.get("value").getAsJsonArray().forEach(t1 -> perms.add(t1.getAsString()));
      serverGroupPermissions.put(servername, perms);
    });
    Map<String, Object> options = new HashMap<>();
    object.get("options").getAsJsonArray().forEach(t -> {
      JsonObject object1 = t.getAsJsonObject();
      String key = object1.get("key").getAsString();
      Object obj = JsonUtil.getGson().fromJson(object1.get("value"), Object.class);
      options.put(key, obj);
    });
    List<String> implement = new ArrayList<>();
    object.get("implementGroups").getAsJsonArray().forEach(t -> implement.add(t.getAsString()));
    return new PermissionGroup(name, color, prefix, suffix, display, tagId, joinPower, defaultGroup,
        permissions, serverGroupPermissions, options, implement);
  }

  @Override
  public JsonElement serialize(PermissionGroup permissionGroup, Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("name", permissionGroup.getName());
    object.addProperty("color", permissionGroup.getColor());
    object.addProperty("prefix", permissionGroup.getPrefix());
    object.addProperty("suffix", permissionGroup.getSuffix());
    object.addProperty("display", permissionGroup.getDisplay());
    object.addProperty("tagId", permissionGroup.getTagId());
    object.addProperty("joinPower", permissionGroup.getJoinPower());
    object.addProperty("defaultGroup", permissionGroup.isDefaultGroup());
    JsonArray permissions = new JsonArray();
    permissionGroup.getPermissions().forEach((k, v) -> {
      JsonObject permission = new JsonObject();
      permission.addProperty("key", k);
      permission.addProperty("value", v);
      permissions.add(permission);
    });
    object.add("permissions", permissions);
    JsonArray serverGroupPermissions = new JsonArray();
    permissionGroup.getServerGroupPermissions().forEach((x, y) -> {
      JsonObject serverGroup = new JsonObject();
      serverGroup.addProperty("key", x);
      JsonArray perms = new JsonArray();
      y.forEach(perms::add);
      serverGroup.add("value", perms);
      serverGroupPermissions.add(serverGroup);
    });
    object.add("serverGroupPermissions", serverGroupPermissions);
    JsonArray options = new JsonArray();
    permissionGroup.getOptions().forEach((x, y) -> {
      JsonObject option = new JsonObject();
      option.addProperty("key", x);
      option.addProperty("value", JsonUtil.getGson().toJson(y));
      options.add(option);
    });
    object.add("options", options);
    JsonArray groups = new JsonArray();
    permissionGroup.getImplementGroups().forEach(groups::add);
    object.add("implementGroups", groups);
    return object;
  }

}
