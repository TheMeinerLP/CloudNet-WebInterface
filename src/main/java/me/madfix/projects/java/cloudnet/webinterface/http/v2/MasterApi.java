package me.madfix.projects.java.cloudnet.webinterface.http.v2;

import me.madfix.projects.java.cloudnet.webinterface.ProjectMain;
import me.madfix.projects.java.cloudnet.webinterface.http.v2.utils.Http;
import me.madfix.projects.java.cloudnet.webinterface.http.v2.utils.HttpUser;
import me.madfix.projects.java.cloudnet.webinterface.http.v2.utils.Request;
import me.madfix.projects.java.cloudnet.webinterface.http.v2.utils.Response;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import java.util.Locale;

public final class MasterApi extends MethodWebHandlerAdapter {

  private final ProjectMain projectMain;

  /**
   * Manage request about master.
   * @param cloudNet The main class of cloudnet
   * @param projectMain The main class of the project
   */
  public MasterApi(CloudNet cloudNet, ProjectMain projectMain) {
    super("/cloudnet/api/v2/master");
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
      case "corelog":
        document.append("response", projectMain.getConsoleLines());
        return Response.success(fullHttpResponse, document);

      case "commands":
        document.append("response", projectMain.getCloud().getCommandManager().getCommands());
        return Response.success(fullHttpResponse, document);

      default:
        return Response.messageFieldNotFound(fullHttpResponse);

    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
    FullHttpResponse fullHttpResponse = Http.simpleCheck(httpRequest);
    User user = Http.getUser(httpRequest);
    Document document = new Document();
    switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "reloadall":
        if (!HttpUser.hasPermission(user, "cloudnet.web.master.reload.all", "*",
            "cloudnet.web.master.reload.*")) {
          return Response.permissionDenied(fullHttpResponse);
        }
        CloudNet.getInstance().reload();
        return Response.success(fullHttpResponse, document);

      case "reloadconfig":
        if (!HttpUser.hasPermission(user, "cloudnet.web.master.reload.config", "*",
            "cloudnet.web.master.reload.*")) {
          return Response.permissionDenied(fullHttpResponse);
        }
        try {
          CloudNet.getInstance().getConfig().load();
        } catch (Exception e) {
          e.printStackTrace();
        }
        CloudNet.getInstance().getServerGroups().clear();
        CloudNet.getInstance().getProxyGroups().clear();
        CloudNet.getInstance().getUsers().clear();
        CloudNet.getInstance().getUsers().addAll(CloudNet.getInstance().getConfig().getUsers());

        NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(),
            CloudNet.getInstance().getConfig().getServerGroups(), value -> {
              System.out.println("Loading ServerGroup: " + value.getName());
              CloudNet.getInstance().setupGroup(value);
              return true;
            });

        NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(),
            CloudNet.getInstance().getConfig().getProxyGroups(), value -> {
              System.out.println("Loading ProxyGroup: " + value.getName());
              CloudNet.getInstance().setupProxy(value);
              return true;
            });

        CloudNet.getInstance().getNetworkManager().reload();
        CloudNet.getInstance().getNetworkManager().updateAll();
        CloudNet.getInstance().getWrappers().values().forEach(Wrapper::updateWrapper);
        return Response.success(fullHttpResponse, document);

      case "reloadwrapper":
        if (!HttpUser.hasPermission(user, "cloudnet.web.master.reload.wrapper", "*",
            "cloudnet.web.master.reload.*")) {
          return Response.permissionDenied(fullHttpResponse);
        }
        CloudNet.getInstance().getWrappers().values().stream().filter(wrapper ->
            wrapper.getChannel() != null).forEach(wrapper ->
            wrapper.sendCommand("reload"));
        return Response.success(fullHttpResponse, document);

      case "clearcache":
        if (!HttpUser.hasPermission(user, "cloudnet.web.master.clearcache", "*")) {
          return Response.permissionDenied(fullHttpResponse);
        }
        CloudNet.getInstance().getWrappers().values().stream().filter(wrapper ->
            wrapper.getChannel() != null).forEach(wrapper ->
            wrapper.sendCommand("clearcache"));
        return Response.success(fullHttpResponse, document);

      case "stop":
        if (!HttpUser.hasPermission(user, "cloudnet.web.master.stop", "*")) {
          return Response.permissionDenied(fullHttpResponse);
        }
        CloudNet.getInstance().shutdown();
        return Response.success(fullHttpResponse, document);

      case "command":
        if (Request.hasHeader(httpRequest, "-Xvalue")) {
          final String command = Request.headerValue(httpRequest, "-Xvalue");
          if (!HttpUser.hasPermission(user, "cloudnet.web.master.command.*", "*",
              "cloudnet.web.master.command." + command)) {
            return Response.permissionDenied(fullHttpResponse);
          }
          projectMain.getCloud().getCommandManager().dispatchCommand(command);
          return Response.success(fullHttpResponse, document);
        } else {
          return Response.valueFieldNotFound(fullHttpResponse);
        }

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