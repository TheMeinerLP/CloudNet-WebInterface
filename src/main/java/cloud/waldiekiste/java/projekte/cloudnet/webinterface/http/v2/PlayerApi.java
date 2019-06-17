package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.RequestUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.ResponseUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.UserUtil;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.player.CorePlayerExecutor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Locale;
import java.util.UUID;

public final class PlayerApi extends MethodWebHandlerAdapter {

  private final ProjectMain projectMain;

  /**
   *  Process the requests for player backend.
   * @param cloudNet The main class of cloudnet
   * @param projectMain The main class of the project
   */
  public PlayerApi(CloudNet cloudNet, ProjectMain projectMain) {
    super("/cloudnet/api/v2/player");
    cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
    this.projectMain = projectMain;
  }

  @Override
  public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) {
    FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
        httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
    ResponseUtil.setHeader(fullHttpResponse, "Content-Type", "application/json; charset=utf-8");
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
    if ("send".equals(RequestUtil.getHeaderValue(httpRequest, "-Xmessage")
        .toLowerCase(Locale.ENGLISH))) {
      if (RequestUtil.hasHeader(httpRequest, "-Xvalue", "-Xcount")) {
        String player = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
        String server = RequestUtil.getHeaderValue(httpRequest, "-Xcount");
        if (!UserUtil.hasPermission(user, "cloudnet.web.player.send", "*",
            "cloudnet.web.player.*", "cloudnet.web.player.send." + player)) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
        }
        if (player.matches(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
          CloudPlayer cloudPlayer = this.projectMain.getCloud().getNetworkManager()
              .getOnlinePlayer(UUID.fromString(player));
          CorePlayerExecutor.INSTANCE.sendPlayer(cloudPlayer, server);
        } else {
          if (player.equalsIgnoreCase("*")) {
            this.projectMain.getCloud().getNetworkManager().getOnlinePlayers().values()
                .forEach(t -> CorePlayerExecutor.INSTANCE.sendPlayer(t, server));
            return ResponseUtil.success(fullHttpResponse, true, document);
          } else {
            UUID uuid = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase()
                .get(player);
            if (uuid == null) {
              document.append("code", 404);
              return ResponseUtil.success(fullHttpResponse, false, document);
            }
            CloudPlayer cloudPlayer = CloudNet.getInstance().getNetworkManager()
                .getOnlinePlayer(uuid);
            CorePlayerExecutor.INSTANCE.sendPlayer(cloudPlayer, server);
            return ResponseUtil.success(fullHttpResponse, true, document);
          }
        }
        return ResponseUtil.success(fullHttpResponse, true, document);
      }
    }
    return ResponseUtil.messageFieldNotFound(fullHttpResponse);
  }

  @Override
  public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    return ResponseUtil.cross(httpRequest);
  }
}