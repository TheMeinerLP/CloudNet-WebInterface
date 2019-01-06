package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;

import java.lang.reflect.Type;

public class ProxyProcessMetaJsonAdapter implements JsonSerializer<ProxyProcessMeta> {
    @Override
    public JsonElement serialize(ProxyProcessMeta proxyProcessMeta, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.add("serviceId",jsonSerializationContext.serialize(proxyProcessMeta.getServiceId()));
        object.addProperty("memory",proxyProcessMeta.getMemory());
        object.addProperty("port",proxyProcessMeta.getPort());
        JsonArray array = new JsonArray();
        for (String s : proxyProcessMeta.getProcessParameters()) {
            array.add(s);
        }
        object.add("processParameters",array);
        object.addProperty("url",proxyProcessMeta.getUrl());
        JsonArray downloadablePlugins = new JsonArray();
        proxyProcessMeta.getDownloadablePlugins().forEach(t->downloadablePlugins.add(jsonSerializationContext.serialize(t)));
        object.add("downloadablePlugins",downloadablePlugins);
        return object;
    }
}
