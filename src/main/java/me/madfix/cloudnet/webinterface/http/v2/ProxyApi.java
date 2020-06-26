package me.madfix.cloudnet.webinterface.http.v2;

import me.madfix.cloudnet.webinterface.http.v2.utils.Http;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpUser;
import me.madfix.cloudnet.webinterface.http.v2.utils.JsonUtil;
import me.madfix.cloudnet.webinterface.ProjectMain;
import me.madfix.cloudnet.webinterface.http.v2.utils.Request;
import me.madfix.cloudnet.webinterface.http.v2.utils.Response;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public final class ProxyApi extends MethodWebHandlerAdapter {

  private final ProjectMain projectMain;

  /**
   * Initiated the class.
   * @param cloudNet The main class of cloudnet
   * @param projectMain The main class of the project
   */
  public ProxyApi(CloudNet cloudNet, ProjectMain projectMain) {
    super("/cloudnet/api/v2/proxygroup");
    cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
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
    switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "groups":
        if (!HttpUser.hasPermission(user, "cloudnet.web.group.proxys", "*")) {
          return Response.permissionDenied(fullHttpResponse);
        }
        resp.append("response", new ArrayList<>(projectMain.getCloud().getProxyGroups().keySet()));
        return Response.success(fullHttpResponse, resp);

      case "groupitems":
        resp.append("response",
            projectMain.getCloud().getProxyGroups().keySet().stream().filter(s ->
                HttpUser.hasPermission(user, "*", "cloudnet.web.group.proxy.item.*",
                    "cloudnet.web.proxy.group.proxy.item." + s)).map(s -> {
                      ProxyGroup group = CloudNet.getInstance().getProxyGroup(s);
                      Document document = new Document();
                      document.append("name", group.getName());
                      document.append("version", group.getProxyVersion().name());
                      document.append("status", group.getProxyConfig().isEnabled());
                      return document.convertToJson();
                    }).collect(Collectors.toList()));
        return Response.success(fullHttpResponse, resp);

      case "group":
        if (Request.hasHeader(httpRequest, "-Xvalue")
            && projectMain.getCloud().getProxyGroups()
                .containsKey(Request.headerValue(httpRequest,
                    "-Xvalue"))) {
          String group = Request.headerValue(httpRequest, "-Xvalue");
          if (!HttpUser.hasPermission(user, "cloudnet.web.group.proxy.info.*", "*",
              "cloudnet.web.group.proxy.info." + group)) {
            return Response.permissionDenied(fullHttpResponse);
          }
          Document data = new Document();
          data.append(group,
              JsonUtil.getGson().toJson(projectMain.getCloud().getProxyGroup(group)));
          resp.append("response", data);
          return Response.success(fullHttpResponse, resp);
        } else {
          resp.append("response",
              CloudNet.getInstance().getProxyGroups().values().stream().filter(proxyGroup ->
                  HttpUser.hasPermission(user, "*", "cloudnet.web.group.proxy.item.*",
                      "cloudnet.web.proxy.group.proxy.item." + proxyGroup.getName()))
                  .map(proxyGroup ->
                      JsonUtil.getGson().toJson(proxyGroup)).collect(
                  Collectors.toList()));
          return Response.success(fullHttpResponse, resp);
        }

      case "screen":
        if (Request.hasHeader(httpRequest, "-Xvalue")
            && projectMain.getCloud().getProxys().containsKey(Request
            .headerValue(httpRequest, "-Xvalue"))) {
          final String group = Request.headerValue(httpRequest, "-Xvalue");
          ProxyServer server = projectMain.getCloud().getProxy(group);
          if (!HttpUser.hasPermission(user, "cloudnet.web.screen.proxys.info.*", "*",
              "cloudnet.web.screen.proxys.info." + server.getServiceId().getGroup())) {
            return Response.permissionDenied(fullHttpResponse);
          }
          if (!projectMain.getCloud().getScreenProvider().getScreens().containsKey(
              server.getServiceId().getServerId())) {
            server.getWrapper().enableScreen(server.getProxyInfo());
          }
          if (projectMain.getScreenInfos().containsKey(server.getServiceId().getServerId())) {
            resp.append("response", projectMain
                .getScreenInfos().get(server.getServiceId().getServerId()));
          }
          return Response.success(fullHttpResponse, resp);
        } else {
          return Response.valueFieldNotFound(fullHttpResponse);
        }

      case "proxys":
        if (Request.hasHeader(httpRequest, "-Xvalue")
            && projectMain.getCloud().getProxyGroups()
                .containsKey(Request.headerValue(httpRequest,
                    "-Xvalue"))) {
          String group = Request.headerValue(httpRequest, "-Xvalue");
          if (!HttpUser.hasPermission(user, "cloudnet.web.group.proxys.info.*", "*",
              "cloudnet.web.group.proxys.info." + group)) {
            return Response.permissionDenied(fullHttpResponse);
          }
          resp.append("response",
              CloudNet.getInstance().getProxys(group).stream().map(proxyServer ->
                  JsonUtil.getGson().toJson(proxyServer.getProxyInfo().toSimple())).collect(
                  Collectors.toList()));
          return Response.success(fullHttpResponse, resp);
        } else {
          return Response.valueFieldNotFound(fullHttpResponse);
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
    Document document = new Document();
    switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "command":
        if (Request.hasHeader(httpRequest, "-Xvalue") && Request.hasHeader(httpRequest,
            "-Xcount")) {
          String group = Request.headerValue(httpRequest, "-Xvalue");
          String command = Request.headerValue(httpRequest, "-Xcount");
          if (!HttpUser.hasPermission(user, "cloudnet.web.screen.proxy.command.*", "*",
              "cloudnet.web.screen.proxy.command." + command.split(" ")[0])) {
            return Response.permissionDenied(fullHttpResponse);
          }
          ProxyServer server = projectMain.getCloud().getProxy(group);
          server.getWrapper().writeProxyCommand(command, server.getProxyInfo());
          return Response.success(fullHttpResponse, document);
        } else {
          return Response.valueFieldNotFound(fullHttpResponse);
        }

      case "stopscreen":
        if (Request.hasHeader(httpRequest, "-Xvalue")
            && projectMain.getCloud().getScreenProvider().getScreens().containsKey(
                Request.headerValue(httpRequest, "-Xvalue"))) {
          String group = Request.headerValue(httpRequest, "-Xvalue");
          ProxyServer server = projectMain.getCloud().getProxy(group);
          server.getWrapper().disableScreen(server.getProxyInfo());
          return Response.success(fullHttpResponse, document);
        } else {
          return Response.valueFieldNotFound(fullHttpResponse);
        }

      case "stopproxy":
        if (Request.hasHeader(httpRequest, "-Xvalue")) {
          String group = Request.headerValue(httpRequest, "-Xvalue");
          if (!HttpUser.hasPermission(user, "cloudnet.web.proxy.stop.*", "*",
              "cloudnet.web.proxy.stop." + group)) {
            return Response.permissionDenied(fullHttpResponse);
          }
          projectMain.getCloud().stopProxy(group);
          return Response.success(fullHttpResponse, document);
        } else {
          return Response.valueFieldNotFound(fullHttpResponse);
        }

      case "stop":
        if (Request.hasHeader(httpRequest, "-Xvalue")
            && projectMain.getCloud().getProxyGroups().containsKey(
                Request.headerValue(httpRequest, "-Xvalue"))) {
          String group = Request.headerValue(httpRequest, "-Xvalue");
          if (!HttpUser.hasPermission(user, "cloudnet.web.group.proxy.stop.*", "*",
              "cloudnet.web.group.proxy.stop." + group)) {
            return Response.permissionDenied(fullHttpResponse);
          }
          projectMain.getCloud().getProxys(group).forEach(
              t -> projectMain.getCloud().stopProxy(t.getName()));

          return Response.success(fullHttpResponse, document);
        } else {
          return Response.valueFieldNotFound(fullHttpResponse);
        }

      case "delete":
        if (Request.hasHeader(httpRequest, "-Xvalue")
            && projectMain.getCloud().getProxyGroups().containsKey(
                Request.headerValue(httpRequest, "-Xvalue"))) {
          String group = Request.headerValue(httpRequest, "-Xvalue");
          if (!HttpUser.hasPermission(user, "cloudnet.web.group.proxy.delete.*",
              "*", "cloudnet.web.group.proxy.delete." + group)) {
            return Response.permissionDenied(fullHttpResponse);
          }
          ProxyGroup grp = projectMain.getCloud().getProxyGroup(group);
          CloudNet.getInstance().getProxyGroups().remove(grp.getName());
          Collection<String> wrappers = grp.getWrapper();
          projectMain.getCloud().getConfig().deleteGroup(grp);
          CloudNet.getInstance().toWrapperInstances(wrappers).forEach(Wrapper::updateWrapper);

          return Response.success(fullHttpResponse, document);
        } else {
          return Response.valueFieldNotFound(fullHttpResponse);
        }

      case "save":
        String proxyGroupName = Request.content(httpRequest);
        if (proxyGroupName.isEmpty()) {
          return Response.badRequest(fullHttpResponse, new Document());
        }
        ProxyGroup proxygn = JsonUtil.getGson().fromJson(proxyGroupName, ProxyGroup.class);
        if (!HttpUser.hasPermission(user, "cloudnet.web.group.proxy.save.*", "*",
            "cloudnet.web.group.proxy.save." + proxygn.getName())) {
          return Response.permissionDenied(fullHttpResponse);
        }
        projectMain.getCloud().getConfig().createGroup(proxygn);
        CloudNet.getInstance().setupProxy(proxygn);
        if (!CloudNet.getInstance().getProxyGroups().containsKey(proxygn.getName())) {
          CloudNet.getInstance().getProxyGroups().put(proxygn.getName(), proxygn);
        } else {
          CloudNet.getInstance().getProxyGroups().replace(proxygn.getName(), proxygn);
        }
        CloudNet.getInstance().toWrapperInstances(proxygn.getWrapper())
            .forEach(Wrapper::updateWrapper);
        CloudNet.getInstance().getNetworkManager().updateAll();

        return Response.success(fullHttpResponse, document);
      case "start":
        if (Request.hasHeader(httpRequest, "-Xvalue", "-xCount")
            && projectMain.getCloud().getProxyGroups()
                .containsKey(Request.headerValue(httpRequest,
                    "-Xvalue"))) {
          String group = Request.headerValue(httpRequest, "-Xvalue");
          int count = Integer.parseInt(Request.headerValue(httpRequest, "-Xcount"));
          if (!HttpUser.hasPermission(user, "cloudnet.web.group.proxy.start.*", "*",
              "cloudnet.web.group.proxy.start." + group)) {
            return Response.permissionDenied(fullHttpResponse);
          }
          for (int i = 0; i < count; i++) {
            projectMain.getCloud().startProxyAsync(projectMain.getCloud().getProxyGroup(group));
          }

          return Response.success(fullHttpResponse, document);
        } else {
          return Response.fieldNotFound(fullHttpResponse,
              "No available -Xvalue,-Xcount command found!");
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