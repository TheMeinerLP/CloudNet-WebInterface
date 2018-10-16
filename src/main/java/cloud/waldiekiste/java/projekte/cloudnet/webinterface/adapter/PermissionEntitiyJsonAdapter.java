/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PermissionEntitiyJsonAdapter implements JsonSerializer<PermissionEntity>,JsonDeserializer<PermissionEntity> {
    @Override
    public PermissionEntity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        final String uniqueId = object.get("uniqueId").getAsString();
        Type permisisonsType = new TypeToken<HashMap<String, Boolean>>(){}.getType();
        HashMap<String, Boolean> permissions = jsonDeserializationContext.deserialize(object.get("permissions"),permisisonsType);
        final String prefix = object.get("prefix").getAsString();
        final String suffix = object.get("suffix").getAsString();
        Type implementGroupsType = new TypeToken<Collection<GroupEntityData>>(){}.getType();
        List<GroupEntityData> groups = jsonDeserializationContext.deserialize(object.get("groups"),implementGroupsType);
        return new PermissionEntity(UUID.fromString(uniqueId),permissions,prefix,suffix,groups);
    }

    @Override
    public JsonElement serialize(PermissionEntity permissionEntity, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("uniqueId",permissionEntity.getUniqueId().toString());
        JsonArray permissions = new JsonArray();
        permissionEntity.getPermissions().forEach((k,v)->{
            JsonObject permission = new JsonObject();
            permission.addProperty("key",k);
            permission.addProperty("value",v);
            permissions.add(permission);
        });
        object.add("permissions",permissions);
        object.addProperty("prefix",permissionEntity.getPrefix());
        object.addProperty("suffix",permissionEntity.getSuffix());
        Type groups = new TypeToken<Collection<GroupEntityData>>(){}.getType();
        object.add("groups",jsonSerializationContext.serialize(permissionEntity.getGroups(),groups));
        return object;
    }
}
