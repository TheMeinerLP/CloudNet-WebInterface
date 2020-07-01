package me.madfix.cloudnet.webinterface.http.v2.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.proxylayout.*;
import de.dytanic.cloudnet.lib.server.*;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.info.SimpleProxyInfo;
import de.dytanic.cloudnet.lib.server.info.SimpleServerInfo;
import de.dytanic.cloudnet.lib.server.priority.PriorityConfig;
import de.dytanic.cloudnet.lib.server.priority.PriorityService;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobItemLayout;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobPosition;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.serverselectors.sign.*;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnetcore.network.NetworkInfo;
import de.dytanic.cloudnetcore.network.components.*;
import me.madfix.cloudnet.webinterface.adapter.*;

public final class JsonUtil {

    private static Gson instance;

    /**
     * Build all adapters to gson class.
     *
     * @return Return a gson class with all json adapters
     */
    public static synchronized Gson getGson() {
        if (instance == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(AutoSlot.class, new AutoSlotJsonAdapter());
            builder.registerTypeAdapter(DynamicFallback.class, new DynamicFallbackJsonAdapter());
            builder.registerTypeAdapter(Motd.class, new MotdJsonAdapter());
            builder.registerTypeAdapter(ProxyConfig.class, new ProxyConfigJsonAdapter());
            builder.registerTypeAdapter(ServerFallback.class, new ServerFallbackJsonAdapter());
            builder.registerTypeAdapter(ServerInstallablePlugin.class,
                    new ServerInstallablePluginJsonAdapter());
            builder.registerTypeAdapter(TabList.class, new TabListJsonAdapter());
            builder.registerTypeAdapter(Template.class, new TemplateJsonAdapter());
            builder.registerTypeAdapter(ProxyGroup.class, new ProxyGroupJsonAdapter());
            builder.registerTypeAdapter(User.class, new UserJsonAdapter());
            builder.registerTypeAdapter(PriorityConfig.class, new PriorityConfigJsonAdapter());
            builder.registerTypeAdapter(PriorityService.class, new PriorityServiceJsonAdapter());
            builder.registerTypeAdapter(AdvancedServerConfig.class, new AdvanceServerConfigJsonAdapter());
            builder.registerTypeAdapter(ServerGroup.class, new ServerGroupJsonAdapter());
            builder.registerTypeAdapter(SimpleProxyInfo.class, new ProxyInfoJsonAdapter());
            builder.registerTypeAdapter(ServiceId.class, new ServiceIdJsonAdapter());
            builder.registerTypeAdapter(SimpleServerInfo.class, new ServerInfoJsonAdapter());
            builder.registerTypeAdapter(PermissionGroup.class, new PermissionGroupJsonAdapter());
            builder.registerTypeAdapter(GroupEntityData.class, new GroupEntityJsonAdapter());
            builder.registerTypeAdapter(PermissionEntity.class, new PermissionEntitiyJsonAdapter());
            builder.registerTypeAdapter(PlayerConnection.class, new PlayerConnectionJsonAdpater());
            builder.registerTypeAdapter(OfflinePlayer.class, new OfflinePlayerJsonAdapter());
            builder.registerTypeAdapter(Wrapper.class, new WrapperAdpater());
            builder.registerTypeAdapter(WrapperMeta.class, new WrapperMetaAdapter());
            builder.registerTypeAdapter(WrapperInfo.class, new WrapperInfoAdpater());
            builder.registerTypeAdapter(ServerProcessMeta.class, new ServerProcessMetaJsonAdapter());
            builder.registerTypeAdapter(ServerConfig.class, new ServerConfigJsonAdapter());
            builder.registerTypeAdapter(ProxyServer.class, new ProxyServerJsonAdapter());
            builder.registerTypeAdapter(ProxyProcessMeta.class, new ProxyProcessMetaJsonAdapter());
            builder.registerTypeAdapter(NetworkInfo.class, new NetworkInfoJsonAdapter());
            builder.registerTypeAdapter(MinecraftServer.class, new MinecraftServerJsonAdapter());
            builder.registerTypeAdapter(CloudServerMeta.class, new CloudServerMetaJsonAdapter());
            builder.registerTypeAdapter(CloudServer.class, new CloudServerJsonAdapter());
            builder.registerTypeAdapter(SignLayout.class, new SignLayoutAdapter());
            builder.registerTypeAdapter(SearchingAnimation.class, new SearchingAnimationAdapter());
            builder.registerTypeAdapter(SignGroupLayouts.class, new SignGroupLayoutsAdapter());
            builder.registerTypeAdapter(SignLayoutConfig.class, new SignLayoutConfigAdapter());
            builder.registerTypeAdapter(Position.class, new PositionJsonAdapter());
            builder.registerTypeAdapter(Sign.class, new SignJsonAdapter());
            builder.registerTypeAdapter(MobConfig.class, new MobConfigJsonAdapter());
            builder.registerTypeAdapter(MobItemLayout.class, new MobItemLayoutJsonAdapter());
            builder.registerTypeAdapter(MobPosition.class, new MobPositionJsonAdapter());
            builder.registerTypeAdapter(ServerMob.class, new ServerMobJsonAdapter());
            instance = builder.create();
        }
        return instance;

    }
}