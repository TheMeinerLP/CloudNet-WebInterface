package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnetcore.network.components.*;

import java.lang.reflect.Type;

public class WrapperAdpater implements JsonSerializer<Wrapper> {


    @Override
    public JsonElement serialize(Wrapper wrapper, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.add("wrapperInfo",jsonSerializationContext.serialize(wrapper.getWrapperInfo()));
        object.add("networkInfo",jsonSerializationContext.serialize(wrapper.getNetworkInfo()));
        object.addProperty("cpuUsage",wrapper.getCpuUsage());
        object.addProperty("usedMemory",wrapper.getUsedMemory());
        object.addProperty("usedMemoryAndWaiting",wrapper.getUsedMemoryAndWaitings());
        object.addProperty("serverId",wrapper.getServerId());
        return object;
    }
}
