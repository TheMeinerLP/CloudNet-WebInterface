package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;

import java.lang.reflect.Type;

public class MinecraftServerJsonAdapter implements JsonSerializer<MinecraftServer> {
    @Override
    public JsonElement serialize(MinecraftServer minecraftServer, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.add("serviceId",jsonSerializationContext.serialize(minecraftServer.getServiceId()));
        object.add("processMeta",jsonSerializationContext.serialize(minecraftServer.getProcessMeta()));
        object.addProperty("groupMode",minecraftServer.getGroupMode().name().toUpperCase());
        object.add("serverInfo",jsonSerializationContext.serialize(minecraftServer.getLastServerInfo()));
        return object;
    }
}
