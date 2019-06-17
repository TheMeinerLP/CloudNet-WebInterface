/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.AdvanceServerConfigJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.AutoSlotJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.CloudServerJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.CloudServerMetaJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.DynamicFallbackJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.GroupEntityJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.MinecraftServerJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.MobConfigJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.MobItemLayoutJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.MobPositionJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.MotdJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.NetworkInfoJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.OfflinePlayerJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.PermissionEntitiyJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.PermissionGroupJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.PlayerConnectionJsonAdpater;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.PositionJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.PriorityConfigJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.PriorityServiceJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.ProxyConfigJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.ProxyGroupJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.ProxyInfoJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.ProxyProcessMetaJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.ProxyServerJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.SearchingAnimationAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.ServerConfigJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.ServerFallbackJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.ServerGroupJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.ServerInfoJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.ServerInstallablePluginJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.ServerMobJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.ServerProcessMetaJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.ServiceIdJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.SignGroupLayoutsAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.SignJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.SignLayoutAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.SignLayoutConfigAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.TabListJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.TemplateJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.UpdateDataJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.UserJsonAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.WrapperAdpater;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.WrapperInfoAdpater;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.WrapperMetaAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils.UpdateData;
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

  public static Gson getGson() {
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
    builder.registerTypeAdapter(UpdateData.class, new UpdateDataJsonAdapter());
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
    return builder.create();
  }
}