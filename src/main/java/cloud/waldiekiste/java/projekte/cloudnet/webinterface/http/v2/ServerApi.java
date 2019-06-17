package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.HttpUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.JsonUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.RequestUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.ResponseUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.UserUtil;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public final class ServerApi extends MethodWebHandlerAdapter {

  private final ProjectMain projectMain;

  /**
   * Imitated the class.
   * @param cloudNet the main class of cloudnet
   * @param projectMain the main class of the project
   */
  public ServerApi(CloudNet cloudNet, ProjectMain projectMain) {
    super("/cloudnet/api/v2/servergroup");
    cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
    this.projectMain = projectMain;
  }

  @SuppressWarnings("deprecation")
  @Override
  public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
        httpRequest.getProtocolVersion(),
        HttpResponseStatus.OK);
    ResponseUtil.setHeader(fullHttpResponse, "Content-Type", "application/json;charset=utf-8");
    fullHttpResponse = HttpUtil.simpleCheck(fullHttpResponse, httpRequest);
    User user = HttpUtil.getUser(httpRequest);
    Document resp = new Document();
    switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "groups":
        if (!UserUtil.hasPermission(user, "cloudnet.web.group.servers", "*")) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
        }
        resp.append("response", new ArrayList<>(CloudNet.getInstance().getServerGroups().keySet()));
        return ResponseUtil.success(fullHttpResponse, true, resp);

      case "screen":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue")
            && CloudNet.getInstance().getServers().containsKey(RequestUtil.
            getHeaderValue(httpRequest, "-Xvalue"))) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          MinecraftServer server = CloudNet.getInstance().getServer(group);
          if (!UserUtil.hasPermission(user, "cloudnet.web.screen.servers.info.*", "*",
              "cloudnet.web.screen.servers.info.group." + server.getServiceId().getGroup())) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          if (!CloudNet.getInstance().getScreenProvider().getScreens()
              .containsKey(server.getServiceId().getServerId())) {
            server.getWrapper().enableScreen(server.getServerInfo());
          }
          if (projectMain.getScreenInfos().containsKey(server.getServiceId().getServerId())) {
            resp.append("response", projectMain.getScreenInfos().get(
                server.getServiceId().getServerId()));
          }
          return ResponseUtil.success(fullHttpResponse, true, resp);
        } else {
          return ResponseUtil.valueFieldNotFound(fullHttpResponse);
        }

      case "servers":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue")
            && CloudNet.getInstance().getServerGroups().containsKey(RequestUtil.getHeaderValue(
                httpRequest, "-Xvalue"))) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          if (!UserUtil.hasPermission(user, "cloudnet.web.group.servers.info.*", "*",
              "cloudnet.web.group.servers.info." + group)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          resp.append("response",
              CloudNet.getInstance().getServers(group).stream().map(minecraftServer ->
                  JsonUtil.getGson().toJson(minecraftServer.getLastServerInfo()))
                  .collect(Collectors.toList()));
          return ResponseUtil.success(fullHttpResponse, true, resp);
        } else {
          return ResponseUtil.valueFieldNotFound(fullHttpResponse);
        }

      case "allservers":
        if (!UserUtil.hasPermission(user, "cloudnet.web.group.allservers.info.*", "*")) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
        }
        resp.append("response",
            CloudNet.getInstance().getServers().values().stream().map(minecraftServer ->
                JsonUtil.getGson().toJson(minecraftServer.getLastServerInfo().toSimple()))
                .collect(Collectors.toList()));
        return ResponseUtil.success(fullHttpResponse, true, resp);

      case "group":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue")
            && CloudNet.getInstance().getServerGroups().containsKey(
                RequestUtil.getHeaderValue(httpRequest, "-Xvalue"))) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          if (!UserUtil.hasPermission(user, "cloudnet.web.group.server.info.*", "*",
              "cloudnet.web.group.server.info." + group)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          Document data = new Document();
          data.append(group,
              JsonUtil.getGson().toJson(CloudNet.getInstance().getServerGroup(group)));
          resp.append("response", data);
          return ResponseUtil.success(fullHttpResponse, true, resp);
        } else {
          resp.append("response",
              CloudNet.getInstance().getServerGroups().values().stream().filter(serverGroup ->
                  UserUtil.hasPermission(user, "*", "cloudnet.web.group.server.item.*",
                      "cloudnet.web.proxy.group.server.item." + serverGroup.getName()))
                  .map(serverGroup ->
                      JsonUtil.getGson().toJson(serverGroup)).collect(Collectors.toList()));
          return ResponseUtil.success(fullHttpResponse, true, resp);
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
    fullHttpResponse = HttpUtil.simpleCheck(fullHttpResponse, httpRequest);
    User user = HttpUtil.getUser(httpRequest);
    Document document = new Document();
    switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "stop":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue")
            && CloudNet.getInstance().getProxyGroups()
                .containsKey(RequestUtil.getHeaderValue(httpRequest,
                    "-Xvalue"))) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          if (!UserUtil.hasPermission(user, "cloudnet.web.group.server.stop.*", "*",
              "cloudnet.web.group.server.stop." + group)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          CloudNet.getInstance().getServers(group).forEach(t ->
              CloudNet.getInstance().stopServer(t.getName()));
          return ResponseUtil.success(fullHttpResponse, true, document);
        } else {
          return ResponseUtil.valueFieldNotFound(fullHttpResponse);
        }

      case "command":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue")
            && RequestUtil.hasHeader(httpRequest,
            "-Xcount")) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          String command = RequestUtil.getHeaderValue(httpRequest, "-Xcount");
          if (!UserUtil.hasPermission(user, "cloudnet.web.screen.server.command.*", "*",
              "cloudnet.web.screen.server.command." + command.split(" ")[0])) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          MinecraftServer server = CloudNet.getInstance().getServer(group);
          server.getWrapper().writeServerCommand(command, server.getServerInfo());
          return ResponseUtil.success(fullHttpResponse, true, document);
        } else {
          return ResponseUtil.valueFieldNotFound(fullHttpResponse);
        }

      case "stopscreen":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue")
            && CloudNet.getInstance().getScreenProvider().getScreens().containsKey(
                RequestUtil.getHeaderValue(httpRequest, "-Xvalue"))) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          MinecraftServer server = CloudNet.getInstance().getServer(group);
          server.getWrapper().disableScreen(server.getServerInfo());
          return ResponseUtil.success(fullHttpResponse, true, document);
        } else {
          return ResponseUtil.valueFieldNotFound(fullHttpResponse);
        }

      case "delete":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue")
            && CloudNet.getInstance().getServerGroups().containsKey(
                RequestUtil.getHeaderValue(httpRequest, "-Xvalue"))) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          if (!UserUtil.hasPermission(user, "cloudnet.web.group.server.delete.*", "*",
              "cloudnet.web.group.server.delete." + group)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          CloudNet.getInstance().getServers(group).forEach(t ->
              CloudNet.getInstance().stopServer(t.getName()));
          ServerGroup serverGroup = CloudNet.getInstance().getServerGroup(group);
          CloudNet.getInstance().getServerGroups().remove(serverGroup.getName());
          Collection<String> wrappers = serverGroup.getWrapper();
          CloudNet.getInstance().getConfig().deleteGroup(serverGroup);
          CloudNet.getInstance().toWrapperInstances(wrappers).forEach(Wrapper::updateWrapper);
          return ResponseUtil.success(fullHttpResponse, true, document);
        } else {
          return ResponseUtil.valueFieldNotFound(fullHttpResponse);
        }

      case "save":
        String servergroup = RequestUtil.getContent(httpRequest);
        if (servergroup.isEmpty()) {
          return ResponseUtil.success(fullHttpResponse, false, new Document());
        }
        ServerGroup serverGroup = JsonUtil.getGson().fromJson(servergroup, ServerGroup.class);
        if (!UserUtil.hasPermission(user, "cloudnet.web.group.server.save.*", "*",
            "cloudnet.web.group.server.save." + serverGroup.getName())) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
        }
        CloudNet.getInstance().getConfig().deleteGroup(serverGroup);
        CloudNet.getInstance().getConfig().createGroup(serverGroup);
        if (!CloudNet.getInstance().getServerGroups().containsKey(serverGroup.getName())) {
          CloudNet.getInstance().getServerGroups().put(serverGroup.getName(), serverGroup);
        } else {
          CloudNet.getInstance().getServerGroups().replace(serverGroup.getName(), serverGroup);
        }
        CloudNet.getInstance().setupGroup(serverGroup);
        CloudNet.getInstance().toWrapperInstances(serverGroup.getWrapper())
            .forEach(Wrapper::updateWrapper);
        return ResponseUtil.success(fullHttpResponse, true, document);

      case "start":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue", "-Xcount")
            && CloudNet.getInstance().getServerGroups()
                .containsKey(RequestUtil.getHeaderValue(httpRequest,
                    "-Xvalue"))) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          int count = Integer.parseInt(RequestUtil.getHeaderValue(httpRequest, "-Xcount"));
          if (!UserUtil.hasPermission(user, "cloudnet.web.group.server.start.*", "*",
              "cloudnet.web.group.server.start." + group)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          for (int i = 0; i < count; i++) {
            CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroup(group));
          }
          return ResponseUtil.success(fullHttpResponse, true, document);
        } else {
          return ResponseUtil.fieldNotFound(fullHttpResponse,
              "No available -Xvalue,-Xcount command found!");
        }

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
