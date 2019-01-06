package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.dytanic.cloudnetcore.network.components.CloudServer;

import java.lang.reflect.Type;

public class CloudServeJsonAdapter implements JsonSerializer<CloudServer> {
    @Override
    public JsonElement serialize(CloudServer cloudServer, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.add("serviceId",jsonSerializationContext.serialize(cloudServer.getServiceId()));
        object.add("cloudServerMeta",jsonSerializationContext.serialize(cloudServer.getCloudServerMeta()));
        object.add("cloudServerMeta",jsonSerializationContext.serialize(cloudServer.getWrapper()));
        object.addProperty("serverGroupType",cloudServer.getServerGroupType().name().toUpperCase());
        object.add("serverInfo",jsonSerializationContext.serialize(cloudServer.getLastServerInfo()));
        return object;
    }
}
