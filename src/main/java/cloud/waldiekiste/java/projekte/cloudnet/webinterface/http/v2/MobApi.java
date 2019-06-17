package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.Http;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.JsonUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.HttpUser;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.Request;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.Response;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Collectors;

public final class MobApi extends MethodWebHandlerAdapter {

  private final Path path;
  private final ProjectMain projectMain;

  /**
   * Manage the requests about the mob system of cloudnet.
   * @param projectMain The main class of the project
   */
  public MobApi(ProjectMain projectMain) {
    super("/cloudnet/api/v2/mob");
    CloudNet.getInstance().getWebServer().getWebServerProvider().registerHandler(this);
    this.path = Paths.get("local/servermob_config.json");
    this.projectMain = projectMain;
  }

  @SuppressWarnings("deprecation")
  @Override
  public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    FullHttpResponse fullHttpResponse = Http.simpleCheck(httpRequest);
    User user = Http.getUser(httpRequest);

    Document resp = new Document();
    if (!HttpUser.hasPermission(user, "*", "cloudnet.web.module.mob.load")) {
      return Response.permissionDenied(fullHttpResponse);
    }
    switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {

      case "check": {
        resp.append("response", !CloudNet.getInstance().getConfig().getDisabledModules()
            .contains("CloudNet-Service-MobModule"));
        return Response.success(fullHttpResponse, resp);
      }
      case "config": {
        MobConfig config = JsonUtil.getGson().fromJson(Document.loadDocument(this.path)
            .get("mobConfig"),MobConfig.class);
        resp.append("response", JsonUtil.getGson().toJson(config));
        return Response.success(fullHttpResponse, resp);
      }
      case "db": {
        resp.append("response", projectMain.getMobDatabase().loadAll().values().stream()
            .map(serverMob -> JsonUtil.getGson().toJson(serverMob)).collect(
                Collectors.toList()));
        return Response.success(fullHttpResponse, resp);

      }
      default: {
        return Response.messageFieldNotFound(fullHttpResponse);
      }
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    FullHttpResponse fullHttpResponse = Http.simpleCheck(httpRequest);
    User user = Http.getUser(httpRequest);
    String content = Request.content(httpRequest);
    switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "save":
        if (content.isEmpty()) {
          return Response.badRequest(fullHttpResponse, new Document());
        }
        if (!HttpUser.hasPermission(user, "*", "cloudnet.web.module.mob.save")) {
          return Response.permissionDenied(fullHttpResponse);
        }
        MobConfig signLayoutConfig = JsonUtil.getGson().fromJson(content, MobConfig.class);
        Document document = Document.loadDocument(this.path);
        document.append("mobConfig", signLayoutConfig);
        document.saveAsConfig(this.path);
        CloudNet.getInstance().getNetworkManager().updateAll();
        return Response.success(fullHttpResponse, new Document());

      case "delete":
        ServerMob mob = JsonUtil.getGson().fromJson(content, ServerMob.class);
        if (!HttpUser.hasPermission(user, "*", "cloudnet.web.module.mob.delete")) {
          return Response.permissionDenied(fullHttpResponse);
        }
        projectMain.getMobDatabase().remove(mob);
        CloudNet.getInstance().getNetworkManager().updateAll();
        return Response.success(fullHttpResponse, new Document());

      case "add":
        mob = JsonUtil.getGson().fromJson(content, ServerMob.class);
        if (!HttpUser.hasPermission(user, "*", "cloudnet.web.module.mob.add")) {
          return Response.permissionDenied(fullHttpResponse);
        }
        projectMain.getMobDatabase().append(mob);
        CloudNet.getInstance().getNetworkManager().updateAll();
        return Response.success(fullHttpResponse, new Document());

      default:
        return Response.messageFieldNotFound(fullHttpResponse);

    }
  }

  @Override
  public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    return Response.cross(httpRequest);
  }
}
