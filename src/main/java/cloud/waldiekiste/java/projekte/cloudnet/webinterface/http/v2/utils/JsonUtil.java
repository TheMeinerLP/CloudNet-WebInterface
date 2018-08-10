package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.dytanic.cloudnet.lib.proxylayout.*;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;

public class JsonUtil {
    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(AutoSlot.class,new AutoSlotJsonAdapter());
        builder.registerTypeAdapter(DynamicFallback.class,new DynamicFallbackJsonAdapter());
        builder.registerTypeAdapter(Motd.class,new MotdJsonAdapter());
        builder.registerTypeAdapter(ProxyConfig.class,new ProxyConfigJsonAdapter());
        builder.registerTypeAdapter(ServerFallback.class,new ServerFallbackJsonAdapter());
        builder.registerTypeAdapter(ServerInstallablePlugin.class,new ServerInstallablePluginJsonAdapter());
        builder.registerTypeAdapter(TabList.class,new TabListJsonAdapter());
        builder.registerTypeAdapter(Template.class,new TemplateJsonAdapter());
        builder.registerTypeAdapter(ProxyGroup.class,new ProxyGroupJsonAdapter());
        JsonUtil.setGson(builder.create());
    }

    private static Gson gson = null;

    public static void setGson(Gson gson) {
        JsonUtil.gson = gson;
    }

    public static Gson getGson() {
        return gson;
    }
}
