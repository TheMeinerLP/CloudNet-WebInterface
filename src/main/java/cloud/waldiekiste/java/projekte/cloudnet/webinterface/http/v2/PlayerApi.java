package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.Http;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.HttpUser;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.Request;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.Response;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.player.CorePlayerExecutor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
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
    FullHttpResponse fullHttpResponse = Http.simpleCheck(httpRequest);
    User user = CloudNet.getInstance()
        .getUser(Request.headerValue(httpRequest, "-xcloudnet-user"));
    Document document = new Document();
    if ("send".equals(Request.headerValue(httpRequest, "-Xmessage")
        .toLowerCase(Locale.ENGLISH))) {
      if (Request.hasHeader(httpRequest, "-Xvalue", "-Xcount")) {
        String player = Request.headerValue(httpRequest, "-Xvalue");
        String server = Request.headerValue(httpRequest, "-Xcount");
        if (!HttpUser.hasPermission(user, "cloudnet.web.player.send", "*",
            "cloudnet.web.player.*", "cloudnet.web.player.send." + player)) {
          return Response.permissionDenied(fullHttpResponse);
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
            return Response.success(fullHttpResponse, document);
          } else {
            UUID uuid = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase()
                .get(player);
            if (uuid == null) {
              document.append("code", 404);
              return Response.badRequest(fullHttpResponse, document);
            }
            CloudPlayer cloudPlayer = CloudNet.getInstance().getNetworkManager()
                .getOnlinePlayer(uuid);
            CorePlayerExecutor.INSTANCE.sendPlayer(cloudPlayer, server);
            return Response.success(fullHttpResponse, document);
          }
        }
        return Response.success(fullHttpResponse, document);
      }
    }
    return Response.messageFieldNotFound(fullHttpResponse);
  }

  @Override
  public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    return Response.cross(httpRequest);
  }
}