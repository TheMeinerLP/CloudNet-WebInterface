package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.HttpUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.JsonUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.RequestUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.ResponseUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.UserUtil;
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
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public final class ProxyApi extends MethodWebHandlerAdapter {

  private final ProjectMain projectMain;

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
    FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
        httpRequest.getProtocolVersion(),
        HttpResponseStatus.OK);
    fullHttpResponse = HttpUtil.simpleCheck(fullHttpResponse, httpRequest);
    User user = HttpUtil.getUser(httpRequest);
    Document resp = new Document();
    switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "groups":
        if (!UserUtil.hasPermission(user, "cloudnet.web.group.proxys", "*")) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
        }
        resp.append("response", new ArrayList<>(projectMain.getCloud().getProxyGroups().keySet()));
        return ResponseUtil.success(fullHttpResponse, true, resp);

      case "groupitems":
        resp.append("response",
            projectMain.getCloud().getProxyGroups().keySet().stream().filter(s ->
                UserUtil.hasPermission(user, "*", "cloudnet.web.group.proxy.item.*",
                    "cloudnet.web.proxy.group.proxy.item." + s)).map(s -> {
              ProxyGroup group = CloudNet.getInstance().getProxyGroup(s);
              Document document = new Document();
              document.append("name", group.getName());
              document.append("version", group.getProxyVersion().name());
              document.append("status", group.getProxyConfig().isEnabled());
              return document.convertToJson();
            }).collect(Collectors.toList()));
        return ResponseUtil.success(fullHttpResponse, true, resp);

      case "group":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue") &&
            projectMain.getCloud().getProxyGroups()
                .containsKey(RequestUtil.getHeaderValue(httpRequest,
                    "-Xvalue"))) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          if (!UserUtil.hasPermission(user, "cloudnet.web.group.proxy.info.*", "*",
              "cloudnet.web.group.proxy.info." + group)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          Document data = new Document();
          data.append(group,
              JsonUtil.getGson().toJson(projectMain.getCloud().getProxyGroup(group)));
          resp.append("response", data);
          return ResponseUtil.success(fullHttpResponse, true, resp);
        } else {
          resp.append("response",
              CloudNet.getInstance().getProxyGroups().values().stream().filter(proxyGroup ->
                  UserUtil.hasPermission(user, "*", "cloudnet.web.group.proxy.item.*",
                      "cloudnet.web.proxy.group.proxy.item." + proxyGroup.getName()))
                  .map(proxyGroup ->
                      JsonUtil.getGson().toJson(proxyGroup)).collect(
                  Collectors.toList()));
          return ResponseUtil.success(fullHttpResponse, true, resp);
        }

      case "screen":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue") &&
            projectMain.getCloud().getProxys().containsKey(RequestUtil.getHeaderValue(httpRequest,
                "-Xvalue"))) {
          final String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          ProxyServer server = projectMain.getCloud().getProxy(group);
          if (!UserUtil.hasPermission(user, "cloudnet.web.screen.proxys.info.*", "*",
              "cloudnet.web.screen.proxys.info." + server.getServiceId().getGroup())) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          if (!projectMain.getCloud().getScreenProvider().getScreens().containsKey(
              server.getServiceId().getServerId())) {
            server.getWrapper().enableScreen(server.getProxyInfo());
          }
          if (projectMain.getScreenInfos().containsKey(server.getServiceId().getServerId())) {
            resp.append("response", projectMain.
                getScreenInfos().get(server.getServiceId().getServerId()));
          }
          return ResponseUtil.success(fullHttpResponse, true, resp);
        } else {
          return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
        }

      case "proxys":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue") &&
            projectMain.getCloud().getProxyGroups()
                .containsKey(RequestUtil.getHeaderValue(httpRequest,
                    "-Xvalue"))) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          if (!UserUtil.hasPermission(user, "cloudnet.web.group.proxys.info.*", "*",
              "cloudnet.web.group.proxys.info." + group)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          resp.append("response",
              CloudNet.getInstance().getProxys(group).stream().map(proxyServer ->
                  JsonUtil.getGson().toJson(proxyServer.getProxyInfo().toSimple())).collect(
                  Collectors.toList()));
          return ResponseUtil.success(fullHttpResponse, true, resp);
        } else {
          return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
        }

      default: {
        return ResponseUtil.xMessageFieldNotFound(fullHttpResponse);
      }
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
    switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase()) {
      case "command":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue") && RequestUtil.hasHeader(httpRequest,
            "-Xcount")) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          String command = RequestUtil.getHeaderValue(httpRequest, "-Xcount");
          if (!UserUtil.hasPermission(user, "cloudnet.web.screen.proxy.command.*", "*",
              "cloudnet.web.screen.proxy.command." + command.split(" ")[0])) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          ProxyServer server = projectMain.getCloud().getProxy(group);
          server.getWrapper().writeProxyCommand(command, server.getProxyInfo());
          return ResponseUtil.success(fullHttpResponse, true, document);
        } else {
          return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
        }

      case "stopscreen":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue") &&
            projectMain.getCloud().getScreenProvider().getScreens().containsKey(
                RequestUtil.getHeaderValue(httpRequest, "-Xvalue"))) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          ProxyServer server = projectMain.getCloud().getProxy(group);
          server.getWrapper().disableScreen(server.getProxyInfo());
          return ResponseUtil.success(fullHttpResponse, true, document);
        } else {
          return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
        }

      case "stopproxy":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue")) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          if (!UserUtil.hasPermission(user, "cloudnet.web.proxy.stop.*", "*",
              "cloudnet.web.proxy.stop." + group)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          projectMain.getCloud().stopProxy(group);
          return ResponseUtil.success(fullHttpResponse, true, document);
        } else {
          return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
        }

      case "stop":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue") &&
            projectMain.getCloud().getProxyGroups().containsKey(
                RequestUtil.getHeaderValue(httpRequest, "-Xvalue"))) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          if (!UserUtil.hasPermission(user, "cloudnet.web.group.proxy.stop.*", "*",
              "cloudnet.web.group.proxy.stop." + group)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          projectMain.getCloud().getProxys(group).forEach(
              t -> projectMain.getCloud().stopProxy(t.getName()));

          return ResponseUtil.success(fullHttpResponse, true, document);
        } else {
          return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
        }

      case "delete":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue") &&
            projectMain.getCloud().getProxyGroups().containsKey(
                RequestUtil.getHeaderValue(httpRequest, "-Xvalue"))) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          if (!UserUtil.hasPermission(user, "cloudnet.web.group.proxy.delete.*",
              "*", "cloudnet.web.group.proxy.delete." + group)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          ProxyGroup grp = projectMain.getCloud().getProxyGroup(group);
          CloudNet.getInstance().getProxyGroups().remove(grp.getName());
          Collection<String> wrappers = grp.getWrapper();
          projectMain.getCloud().getConfig().deleteGroup(grp);
          CloudNet.getInstance().toWrapperInstances(wrappers).forEach(Wrapper::updateWrapper);

          return ResponseUtil.success(fullHttpResponse, true, document);
        } else {
          return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
        }

      case "save":
        String proxyGroupName = RequestUtil.getContent(httpRequest);
        if (proxyGroupName.isEmpty()) {
          return ResponseUtil.success(fullHttpResponse, false, new Document());
        }
        ProxyGroup proxygn = JsonUtil.getGson().fromJson(proxyGroupName, ProxyGroup.class);
        if (!UserUtil.hasPermission(user, "cloudnet.web.group.proxy.save.*", "*",
            "cloudnet.web.group.proxy.save." + proxygn.getName())) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
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

        return ResponseUtil.success(fullHttpResponse, true, document);
      case "start":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue", "-xCount") &&
            projectMain.getCloud().getProxyGroups()
                .containsKey(RequestUtil.getHeaderValue(httpRequest,
                    "-Xvalue"))) {
          String group = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          int count = Integer.valueOf(RequestUtil.getHeaderValue(httpRequest, "-Xcount"));
          if (!UserUtil.hasPermission(user, "cloudnet.web.group.proxy.start.*", "*",
              "cloudnet.web.group.proxy.start." + group)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          for (int i = 0; i < count; i++) {
            projectMain.getCloud().startProxyAsync(projectMain.getCloud().getProxyGroup(group));
          }

          return ResponseUtil.success(fullHttpResponse, true, document);
        } else {
          return ResponseUtil.xFieldNotFound(fullHttpResponse,
              "No available -Xvalue,-Xcount command found!");
        }

      default:
        return ResponseUtil.xMessageFieldNotFound(fullHttpResponse);

    }
  }

  @Override
  public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    return ResponseUtil.cross(httpRequest);
  }
}