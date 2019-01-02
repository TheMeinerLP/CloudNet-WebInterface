package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;

import java.lang.reflect.Type;

public class WrapperMetaAdapter implements JsonSerializer<WrapperMeta>,JsonDeserializer<WrapperMeta> {
    private String id;
    private String hostName;
    private String user;
    @Override
    public WrapperMeta deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String id = object.get("id").getAsString();
        String hostName = object.get("hostName").getAsString();
        String user = object.get("user").getAsString();
        return new WrapperMeta(id,hostName,user);
    }

    @Override
    public JsonElement serialize(WrapperMeta wrapperMeta, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("id",wrapperMeta.getId());
        object.addProperty("hostName",wrapperMeta.getHostName());
        object.addProperty("user",wrapperMeta.getUser());
        return object;
    }
}
