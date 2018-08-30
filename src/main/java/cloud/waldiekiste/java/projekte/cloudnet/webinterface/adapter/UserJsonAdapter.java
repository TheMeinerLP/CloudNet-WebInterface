/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.hash.DyHash;
import de.dytanic.cloudnet.lib.user.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class UserJsonAdapter implements JsonSerializer<User>,JsonDeserializer<User> {
    @Override
    public User deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        final String name = object.get("name").getAsString();
        final UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        final String token = object.get("token").getAsString();
        final String password = object.get("password").getAsString();
        Collection<String> permissions = new ArrayList<>();
        object.get("permissions").getAsJsonArray().forEach(t->permissions.add(t.getAsString()));
        return new User(name,uuid,token,DyHash.hashString(password),permissions,new HashMap<>());
    }

    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("name",user.getName());
        object.addProperty("uuid",user.getUniqueId().toString());
        object.addProperty("token",user.getApiToken());
        object.addProperty("password",user.getHashedPassword());
        JsonArray array = new JsonArray();
        user.getPermissions().forEach(array::add);
        object.add("permissions",array);
        return object;
    }
}
