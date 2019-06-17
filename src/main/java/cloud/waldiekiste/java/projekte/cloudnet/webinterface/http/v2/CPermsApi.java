package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.HttpUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.JsonUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.RequestUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.ResponseUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.UserUtil;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutUpdateOfflinePlayer;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutUpdatePlayer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public final class CPermsApi extends MethodWebHandlerAdapter {

  private final ProjectMain projectMain;
  private PermissionPool pool;

  public CPermsApi(ProjectMain projectMain) {
    super("/cloudnet/api/v2/cperms");
    this.projectMain = projectMain;
    projectMain.getCloud().getWebServer().getWebServerProvider().registerHandler(this);
    pool = projectMain.getCloud().getNetworkManager().getModuleProperties()
        .getObject("permissionPool",
            PermissionPool.TYPE);
  }

  @SuppressWarnings("deprecation")
  @Override
  public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
        httpRequest.getProtocolVersion(),
        HttpResponseStatus.OK);
    pool = projectMain.getCloud().getNetworkManager().getModuleProperties()
        .getObject("permissionPool",
            PermissionPool.TYPE);
    fullHttpResponse = HttpUtil.simpleCheck(fullHttpResponse, httpRequest);
    User user = CloudNet.getInstance()
        .getUser(RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-user"));
    Document document = new Document();
    switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "group":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue")) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          if (!UserUtil.hasPermission(user, "cloudnet.web.cperms.info.group.*", "*",
              "cloudnet.web.cperms.info.group." + group)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          } else {
            if (!pool.isAvailable()) {
              return ResponseUtil.success(fullHttpResponse, false, document);
            }
            document.append("response", JsonUtil.getGson().toJson(pool.getGroups().get(group)));
            return ResponseUtil.success(fullHttpResponse, true, document);
          }
        } else {
          if (!UserUtil.hasPermission(user, "cloudnet.web.cperms.groups", "*")) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          } else {
            if (!pool.isAvailable()) {
              return ResponseUtil.success(fullHttpResponse, false, document);
            }
            document.append("response", pool.getGroups().values().stream()
                .map(permissionGroup -> JsonUtil.getGson().toJson(permissionGroup)).collect(
                    Collectors.toList()));
            return ResponseUtil.success(fullHttpResponse, true, document);
          }
        }

      case "groups":
        if (!UserUtil.hasPermission(user, "cloudnet.web.cperms.info.groups.*", "*")) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
        } else {
          if (!pool.isAvailable()) {
            return ResponseUtil.success(fullHttpResponse, false, document);
          }
          document.append("response", new ArrayList<>(pool.getGroups().keySet()));
          return ResponseUtil.success(fullHttpResponse, true, document);
        }

      case "user":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue")) {
          String userUuid = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          if (!UserUtil.hasPermission(user, "cloudnet.web.cperms.info.user.*", "*",
              "cloudnet.web.cperms.info.user." + userUuid)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          } else {
            if (!pool.isAvailable()) {
              return ResponseUtil.success(fullHttpResponse, false, document);
            }
            if (!CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().getDatabase()
                .contains(userUuid)) {
              return ResponseUtil.success(fullHttpResponse, false, document);
            }
            if (userUuid.matches(
                "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
              document.append("response", JsonUtil.getGson().toJson(this.projectMain.getCloud()
                  .getDbHandlers().getPlayerDatabase().getPlayer(UUID.fromString(userUuid))));
            } else {
              UUID id = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase()
                  .get(userUuid);
              document.append("response", JsonUtil.getGson().toJson(this.projectMain.getCloud()
                  .getDbHandlers().getPlayerDatabase().getPlayer(id)));
            }
            return ResponseUtil.success(fullHttpResponse, true, document);
          }
        }else{
          return ResponseUtil.valueFieldNotFound(fullHttpResponse);
        }
      case "check":
        if (!UserUtil.hasPermission(user, "cloudnet.web.cperms.check", "*")) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
        }
        if (pool.isAvailable()) {
          return ResponseUtil.success(fullHttpResponse, true, document);
        } else {
          return ResponseUtil.success(fullHttpResponse, false, document);
        }

      default:
        return ResponseUtil.messageFieldNotFound(fullHttpResponse);

    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
        httpRequest.getProtocolVersion(),
        HttpResponseStatus.OK);
    ResponseUtil.setHeader(fullHttpResponse, "Content-Type", "application/json");
    if (!RequestUtil
        .hasHeader(httpRequest, "-xcloudnet-user", "-Xcloudnet-token", "-xcloudnet-message")) {
      return ResponseUtil.cloudFieldNotFound(fullHttpResponse);
    }
    if (!RequestUtil.checkAuth(httpRequest)) {
      return UserUtil.failedAuthorization(fullHttpResponse);
    }
    User user = CloudNet.getInstance()
        .getUser(RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-user"));
    Document document = new Document();
    switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "group":
        String servergroup = RequestUtil.getContent(httpRequest);
        if (servergroup.isEmpty()) {
          return ResponseUtil.success(fullHttpResponse, false, new Document());
        }
        PermissionGroup permissionGroup = JsonUtil.getGson()
            .fromJson(servergroup, PermissionGroup.class);
        if (!UserUtil.hasPermission(user, "cloudnet.web.cperms.group.save.*", "*",
            "cloudnet.web.cperms.group.save." + permissionGroup.getName())) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
        }
        this.projectMain.getConfigPermission().updatePermissionGroup(permissionGroup);
        NetworkUtils.addAll(pool.getGroups(), this.projectMain.getConfigPermission().loadAll());

        CloudNet.getInstance().getNetworkManager().getModuleProperties().append("permissionPool",
            this.pool);
        CloudNet.getInstance().getNetworkManager().updateAll();
        return ResponseUtil.success(fullHttpResponse, true, document);

      case "deletegroup":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue")) {
          final String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          if (!UserUtil.hasPermission(user, "cloudnet.web.cperms.group.delete.*", "*",
              "cloudnet.web.cperms.group.delete." + group)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          this.pool.getGroups().remove(group);
          CloudNet.getInstance().getNetworkManager().getModuleProperties().append("permissionPool",
              this.pool);
          CloudNet.getInstance().getNetworkManager().updateAll();
          return ResponseUtil.success(fullHttpResponse, true, document);

        }else{
          return ResponseUtil.valueFieldNotFound(fullHttpResponse);
        }
      case "user":
        final String userString = RequestUtil.getContent(httpRequest);
        if (userString.isEmpty()) {
          return ResponseUtil.success(fullHttpResponse, false, new Document());
        }
        OfflinePlayer offlinePlayer = JsonUtil.getGson().fromJson(userString, OfflinePlayer.class);
        if (!UserUtil.hasPermission(user, "cloudnet.web.cperms.user.save.*", "*",
            "cloudnet.web.cperms.user.save." + offlinePlayer.getName(),
            "cloudnet.web.cperms.user.save." + offlinePlayer.getUniqueId().toString())) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
        }
        CloudNet.getInstance().getDbHandlers().getPlayerDatabase()
            .updatePermissionEntity(offlinePlayer
                .getUniqueId(), offlinePlayer.getPermissionEntity());

        CloudNet.getInstance().getNetworkManager()
            .sendAllUpdate(new PacketOutUpdateOfflinePlayer(CloudNet
                .getInstance().getDbHandlers().getPlayerDatabase()
                .getPlayer(offlinePlayer.getUniqueId())));

        CloudPlayer onlinePlayer = CloudNet.getInstance().getNetworkManager()
            .getOnlinePlayer(offlinePlayer.getUniqueId());
        if (onlinePlayer != null) {
          onlinePlayer.setPermissionEntity(offlinePlayer.getPermissionEntity());
          CloudNet.getInstance().getNetworkManager()
              . sendAllUpdate(new PacketOutUpdatePlayer(onlinePlayer));
        }
        CloudNet.getInstance().getNetworkManager().updateAll();
        return ResponseUtil.success(fullHttpResponse, true, document);

      default:
        return ResponseUtil.messageFieldNotFound(fullHttpResponse);

    }

  }

  @Override
  public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    return ResponseUtil.cross(httpRequest);
  }
}
