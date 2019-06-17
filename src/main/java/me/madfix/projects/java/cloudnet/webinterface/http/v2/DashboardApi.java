package me.madfix.projects.java.cloudnet.webinterface.http.v2;

import me.madfix.projects.java.cloudnet.webinterface.ProjectMain;
import me.madfix.projects.java.cloudnet.webinterface.http.v2.utils.Http;
import me.madfix.projects.java.cloudnet.webinterface.http.v2.utils.Request;
import me.madfix.projects.java.cloudnet.webinterface.http.v2.utils.Response;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import java.util.Locale;
import java.util.stream.IntStream;

public final class DashboardApi extends MethodWebHandlerAdapter {

  private final ProjectMain projectMain;

  /**
   * Initiated the class.
   * @param cloudNet the CloudNet class
   * @param projectMain the main class of the project
   */
  public DashboardApi(CloudNet cloudNet, ProjectMain projectMain) {
    super("/cloudnet/api/v2/dashboard");
    cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
    this.projectMain = projectMain;
  }

  @SuppressWarnings("deprecation")
  @Override
  public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    FullHttpResponse fullHttpResponse = Http.simpleCheck(httpRequest);
    Document document = new Document();
    switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "players":
        IntStream stream = projectMain.getCloud().getServerGroups().keySet().stream()
            .mapToInt(server -> projectMain.getCloud().getOnlineCount(server));
        document.append("response", stream.sum());
        return Response.success(fullHttpResponse,  document);
      case "servers":
        document.append("response", projectMain.getCloud().getServers().size());
        return Response.success(fullHttpResponse,  document);

      case "proxys":
        document.append("response", projectMain.getCloud().getProxys().size());
        return Response.success(fullHttpResponse,  document);
      case "groups":
        document.append("response", projectMain.getCloud().getServerGroups().size());
        return Response.success(fullHttpResponse,  document);
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