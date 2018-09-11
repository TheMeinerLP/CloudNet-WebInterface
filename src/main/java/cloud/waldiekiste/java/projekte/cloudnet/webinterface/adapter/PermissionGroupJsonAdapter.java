/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionGroupJsonAdapter implements JsonDeserializer<PermissionGroup>,JsonSerializer<PermissionGroup> {
    @Override
    public PermissionGroup deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        final String name = object.get("name").getAsString();
        final String prefix = object.get("prefix").getAsString();
        final String suffix = object.get("suffix").getAsString();
        final String display = object.get("display").getAsString();
        final int tagId = object.get("tagId").getAsInt();
        final int joinPower = object.get("joinPower").getAsInt();
        final boolean defaultGroup = object.get("defaultGroup").getAsBoolean();
        Type permisisonsType = new TypeToken<HashMap<String, Boolean>>(){}.getType();
        HashMap<String, Boolean> permissions = jsonDeserializationContext.deserialize(object.get("permissions"),permisisonsType);
        Type ServerGroupPermissionsType = new TypeToken<Map<String, List<String>>>(){}.getType();
        Map<String, List<String>> serverGroupPermissions = jsonDeserializationContext.deserialize(object.get("serverGroupPermissions"),ServerGroupPermissionsType);
        Type optionsType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> options = jsonDeserializationContext.deserialize(object.get("options"),optionsType);
        Type implementGroupsType = new TypeToken<List<String>>(){}.getType();
        List<String> implementGroups = jsonDeserializationContext.deserialize(object.get("implementGroups"),implementGroupsType);
        return new PermissionGroup(name,prefix,suffix,display,tagId,joinPower,defaultGroup,permissions,serverGroupPermissions,options,implementGroups);
    }

    @Override
    public JsonElement serialize(PermissionGroup permissionGroup, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("name",permissionGroup.getName());
        object.addProperty("prefix",permissionGroup.getPrefix());
        object.addProperty("suffix",permissionGroup.getSuffix());
        object.addProperty("display",permissionGroup.getDisplay());
        object.addProperty("tagId",permissionGroup.getTagId());
        object.addProperty("joinPower",permissionGroup.getJoinPower());
        object.addProperty("defaultGroup",permissionGroup.isDefaultGroup());
        JsonArray permissions = new JsonArray();
        permissionGroup.getPermissions().forEach((k,v)->{
            JsonObject permission = new JsonObject();
            permission.addProperty("key",k);
            permission.addProperty("value",v);
            permissions.add(permission);
        });
        object.add("permissions",permissions);
        Type ServerGroupPermissionsType = new TypeToken<Map<String, List<String>>>(){}.getType();
        object.add("serverGroupPermissions",jsonSerializationContext.serialize(permissionGroup.getServerGroupPermissions(),ServerGroupPermissionsType));
        object.add("options",jsonSerializationContext.serialize(permissionGroup.getOptions(),HashMap.class));
        object.add("implementGroups",jsonSerializationContext.serialize(permissionGroup.getImplementGroups(),List.class));
        return object;
    }

}
