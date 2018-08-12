package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.dytanic.cloudnet.lib.proxylayout.*;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.priority.PriorityConfig;
import de.dytanic.cloudnet.lib.server.priority.PriorityService;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.user.User;

public class JsonUtil {
    public static Gson getGson() {
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
        builder.registerTypeAdapter(User.class,new UserJsonAdapter());
        builder.registerTypeAdapter(PriorityConfig.class,new PriorityConfigJsonAdapter());
        builder.registerTypeAdapter(PriorityService.class,new PriorityServiceJsonAdapter());
        builder.registerTypeAdapter(AdvancedServerConfig.class,new AdvanceServerConfigJsonAdapter());
        builder.registerTypeAdapter(ServerGroup.class,new ServerGroupJsonAdapter());
        return builder.create();
    }
}
