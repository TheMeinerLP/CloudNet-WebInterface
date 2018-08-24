package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.lib.proxylayout.ProxyConfig;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyGroupMode;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class ProxyGroupJsonAdapter implements JsonSerializer<ProxyGroup>,JsonDeserializer<ProxyGroup> {

    @Override
    public ProxyGroup deserialize(JsonElement jsongroupelemnt, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsongroup = jsongroupelemnt.getAsJsonObject();
        String name = jsongroup.get("name").getAsString();
        Collection<String> wrapper = new ArrayList<>();
        jsongroup.get("wrapper").getAsJsonArray().forEach(t->wrapper.add(t.getAsString()));
        Template template = jsonDeserializationContext.deserialize(jsongroup.get("template"),Template.class);
        ProxyVersion proxyVersion = ProxyVersion.valueOf(jsongroup.get("proxyVersion").getAsString());
        final int startPort = jsongroup.get("startPort").getAsInt();
        final int startup = jsongroup.get("startup").getAsInt();
        final int memory = jsongroup.get("memory").getAsInt();
        final ProxyConfig proxyConfig = jsonDeserializationContext.deserialize(jsongroup.get("proxyConfig"), ProxyConfig.class);
        final ProxyGroupMode proxyGroupMode = ProxyGroupMode.valueOf(jsongroup.get("proxyGroupMode").getAsString());
        final WrappedMap settings = new WrappedMap();
        return new ProxyGroup(name,wrapper,template,proxyVersion,startPort,startup,memory,proxyConfig,proxyGroupMode,settings);
    }

    @Override
    public JsonElement serialize(ProxyGroup classgroup, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsongroup = new JsonObject();
        jsongroup.addProperty("name",classgroup.getName());
        JsonArray wrapper = new JsonArray();
        classgroup.getWrapper().forEach(wrapper::add);
        jsongroup.add("wrapper",wrapper);
        jsongroup.add("template",jsonSerializationContext.serialize(classgroup.getTemplate()));
        jsongroup.addProperty("proxyVersion",classgroup.getProxyVersion().name());
        jsongroup.addProperty("startPort",classgroup.getStartPort());
        jsongroup.addProperty("startup",classgroup.getStartup());
        jsongroup.addProperty("memory",classgroup.getMemory());
        jsongroup.add("proxyConfig",jsonSerializationContext.serialize(classgroup.getProxyConfig()));
        jsongroup.addProperty("proxyGroupMode",classgroup.getProxyGroupMode().name());
        Gson gson = new Gson();
        jsongroup.addProperty("settings",gson.toJson(classgroup.getSettings()));
        return jsongroup;
    }
}
