package me.madfix.cloudnet.webinterface.http.v2;

import me.madfix.cloudnet.webinterface.http.v2.utils.Http;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpUser;
import me.madfix.cloudnet.webinterface.http.v2.utils.JsonUtil;
import me.madfix.cloudnet.webinterface.http.v2.utils.Request;
import me.madfix.cloudnet.webinterface.http.v2.utils.Response;
import me.madfix.cloudnet.webinterface.ProjectMain;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayoutConfig;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SignApi extends MethodWebHandlerAdapter {

  private final Path path;
  private final ProjectMain projectMain;

  /**
   * Process the request about the sign system for cloudnet.
   * @param projectMain The main class from the project
   */
  public SignApi(ProjectMain projectMain) {
    super("/cloudnet/api/v2/sign");
    CloudNet.getInstance().getWebServer().getWebServerProvider().registerHandler(this);
    this.path = Paths.get("local/signLayout.json");
    this.projectMain = projectMain;
  }

  @SuppressWarnings("deprecation")
  @Override
  public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    new DefaultFullHttpResponse(
        httpRequest.getProtocolVersion(),
        HttpResponseStatus.OK);
    FullHttpResponse fullHttpResponse = Http.simpleCheck(httpRequest);
    User user = Http.getUser(httpRequest);

    if (!HttpUser.hasPermission(user, "*", "cloudnet.web.module.sign.load")) {
      return Response.permissionDenied(fullHttpResponse);
    }
    Document resp = new Document();
    switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "check":
        resp.append("response", !CloudNet.getInstance().getConfig().getDisabledModules()
            .contains("CloudNet-Service-SignsModule"));
        return Response.success(fullHttpResponse, resp);
      case "config":
        Document document = Document.loadDocument(this.path);
        SignLayoutConfig signLayoutConfig = JsonUtil.getGson()
            .fromJson(document.get("layout_config"),SignLayoutConfig.class);
        resp.append("response", JsonUtil.getGson().toJson(signLayoutConfig));
        return Response.success(fullHttpResponse, resp);
      case "random":
        Random random = new Random();
        ArrayList<MinecraftServer> arrayList = new ArrayList<>(
            CloudNet.getInstance().getServers().values());
        if (arrayList.size() > 0) {
          resp.append("response",
              JsonUtil.getGson().toJson(arrayList.get(random.nextInt(arrayList.size()))));
          return Response.success(fullHttpResponse, resp);
        } else {
          return Response.badRequest(fullHttpResponse, new Document());
        }
      case "db":
        resp.append("response",
            projectMain.getSignDatabase().loadAll().values().stream().map(sign ->
                JsonUtil.getGson().toJson(sign)).collect(Collectors.toList()));
        return Response.success(fullHttpResponse, resp);
      default:
        return Response.messageFieldNotFound(fullHttpResponse);
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    FullHttpResponse fullHttpResponse = Http.simpleCheck(httpRequest);
    User user = Http.getUser(httpRequest);
    switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "save": {
        String content = Request.content(httpRequest);
        if (content.isEmpty()) {
          return Response.badRequest(fullHttpResponse, new Document());
        }
        if (!HttpUser.hasPermission(user, "*", "cloudnet.web.module.sign.save")) {
          return Response.permissionDenied(fullHttpResponse);
        }
        SignLayoutConfig signLayoutConfig = JsonUtil.getGson()
            .fromJson(content, SignLayoutConfig.class);
        final Document document = Document.loadDocument(this.path);
        document.append("layout_config", signLayoutConfig);
        document.saveAsConfig(this.path);
        CloudNet.getInstance().getNetworkManager().updateAll();
        return Response.success(fullHttpResponse, new Document());
      }
      case "delete": {
        String content = Request.content(httpRequest);
        if (!HttpUser.hasPermission(user, "*", "cloudnet.web.module.sign.delete.*")) {
          return Response.permissionDenied(fullHttpResponse);
        }
        UUID id = UUID.fromString(content);
        projectMain.getSignDatabase().removeSign(id);
        CloudNet.getInstance().getNetworkManager().updateAll();
        return Response.success(fullHttpResponse, new Document());
      }

      case "add": {
        if (!HttpUser.hasPermission(user, "*", "cloudnet.web.module.sign.add")) {
          return Response.permissionDenied(fullHttpResponse);
        }
        String content = Request.content(httpRequest);
        Sign s = JsonUtil.getGson().fromJson(content, Sign.class);
        projectMain.getSignDatabase().appendSign(s);
        CloudNet.getInstance().getNetworkManager().updateAll();
        return Response.success(fullHttpResponse, new Document());
      }
      default: {
        return Response.messageFieldNotFound(fullHttpResponse);
      }
    }
  }

  @Override
  public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    return Response.cross(httpRequest);
  }
}