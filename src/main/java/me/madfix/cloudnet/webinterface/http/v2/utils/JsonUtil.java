package me.madfix.cloudnet.webinterface.http.v2.utils;

import me.madfix.cloudnet.webinterface.adapter.AdvanceServerConfigJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.AutoSlotJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.CloudServerMetaJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.GroupEntityJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.MinecraftServerJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.MobPositionJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.NetworkInfoJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.PermissionEntitiyJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.PermissionGroupJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.PriorityConfigJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.ProxyConfigJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.ProxyInfoJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.ServerConfigJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.ServerFallbackJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.ServerGroupJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.ServerInfoJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.ServerInstallablePluginJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.ServerMobJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.ServerProcessMetaJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.ServiceIdJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.SignGroupLayoutsAdapter;
import me.madfix.cloudnet.webinterface.adapter.SignJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.SignLayoutAdapter;
import me.madfix.cloudnet.webinterface.adapter.TemplateJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.CloudServerJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.DynamicFallbackJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.MobConfigJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.MobItemLayoutJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.MotdJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.OfflinePlayerJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.PlayerConnectionJsonAdpater;
import me.madfix.cloudnet.webinterface.adapter.PositionJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.PriorityServiceJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.ProxyGroupJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.ProxyProcessMetaJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.ProxyServerJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.SearchingAnimationAdapter;
import me.madfix.cloudnet.webinterface.adapter.SignLayoutConfigAdapter;
import me.madfix.cloudnet.webinterface.adapter.TabListJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.UserJsonAdapter;
import me.madfix.cloudnet.webinterface.adapter.WrapperAdpater;
import me.madfix.cloudnet.webinterface.adapter.WrapperInfoAdpater;
import me.madfix.cloudnet.webinterface.adapter.WrapperMetaAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.proxylayout.AutoSlot;
import de.dytanic.cloudnet.lib.proxylayout.DynamicFallback;
import de.dytanic.cloudnet.lib.proxylayout.Motd;
import de.dytanic.cloudnet.lib.proxylayout.ProxyConfig;
import de.dytanic.cloudnet.lib.proxylayout.ServerFallback;
import de.dytanic.cloudnet.lib.proxylayout.TabList;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
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
import de.dytanic.cloudnet.lib.serverselectors.sign.Position;
import de.dytanic.cloudnet.lib.serverselectors.sign.SearchingAnimation;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignGroupLayouts;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayout;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayoutConfig;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnetcore.network.NetworkInfo;
import de.dytanic.cloudnetcore.network.components.CloudServer;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;

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