package me.madfix.cloudnet.webinterface.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;

import java.lang.reflect.Type;

public class WrapperMetaAdapter implements JsonSerializer<WrapperMeta>,
        JsonDeserializer<WrapperMeta> {

    @Override
    public WrapperMeta deserialize(JsonElement jsonElement, Type type,
                                   JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String id = object.get("id").getAsString();
        String hostName = object.get("hostName").getAsString();
        String user = object.get("user").getAsString();
        return new WrapperMeta(id, hostName, user);
    }

    @Override
    public JsonElement serialize(WrapperMeta wrapperMeta, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("id", wrapperMeta.getId());
        object.addProperty("hostName", wrapperMeta.getHostName());
        object.addProperty("user", wrapperMeta.getUser());
        return object;
    }
}
