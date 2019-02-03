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
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UserJsonAdapter implements JsonSerializer<User>,JsonDeserializer<User> {
    @Override
    public User deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        final String name = object.get("name").getAsString();
        final UUID uuid = UUID.fromString(object.get("uniqueId").getAsString());
        final String token = object.get("token").getAsString();
        final String password = new String(Base64.getDecoder().decode(object.get("password").getAsString()),StandardCharsets.UTF_8);
        Collection<String> permissions = new ArrayList<>();
        if (object.get("permissions").getAsJsonArray().size() != 0) {
            object.get("permissions").getAsJsonArray().forEach(t->permissions.add(t.getAsString()));
        }
        return new User(name,uuid,token,password,permissions,new HashMap<>());
    }

    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("name",user.getName());
        object.addProperty("uniqueId",user.getUniqueId().toString());
        object.addProperty("token",user.getApiToken());
        object.addProperty("password",Base64.getEncoder().encodeToString(user.getHashedPassword().getBytes(StandardCharsets.UTF_8)));
        JsonArray array = new JsonArray();
        user.getPermissions().forEach(array::add);
        object.add("permissions",array);
        return object;
    }
}
