package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.HttpUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.RequestUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.ResponseUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.UserUtil;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Locale;

public final class MasterApi extends MethodWebHandlerAdapter {

  private final ProjectMain projectMain;

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
    FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
        httpRequest.getProtocolVersion(),
        HttpResponseStatus.OK);
    fullHttpResponse = HttpUtil.simpleCheck(fullHttpResponse, httpRequest);
    Document document = new Document();
    switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "corelog":
        document.append("response", projectMain.getConsoleLines());
        return ResponseUtil.success(fullHttpResponse, true, document);

      case "commands":
        document.append("response", projectMain.getCloud().getCommandManager().getCommands());
        return ResponseUtil.success(fullHttpResponse, true, document);

      default:
        return ResponseUtil.xMessageFieldNotFound(fullHttpResponse);

    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
    FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
        httpRequest.getProtocolVersion(),
        HttpResponseStatus.OK);
    fullHttpResponse = HttpUtil.simpleCheck(fullHttpResponse, httpRequest);
    User user = HttpUtil.getUser(httpRequest);
    Document document = new Document();
    switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
      case "reloadall":
        if (!UserUtil.hasPermission(user, "cloudnet.web.master.reload.all", "*",
            "cloudnet.web.master.reload.*")) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
        }
        CloudNet.getInstance().reload();
        return ResponseUtil.success(fullHttpResponse, true, document);

      case "reloadconfig":
        if (!UserUtil.hasPermission(user, "cloudnet.web.master.reload.config", "*",
            "cloudnet.web.master.reload.*")) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
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
        return ResponseUtil.success(fullHttpResponse, true, document);

      case "reloadwrapper":
        if (!UserUtil.hasPermission(user, "cloudnet.web.master.reload.wrapper", "*",
            "cloudnet.web.master.reload.*")) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
        }
        CloudNet.getInstance().getWrappers().values().stream().filter(wrapper ->
            wrapper.getChannel() != null).forEach(wrapper ->
            wrapper.sendCommand("reload"));
        return ResponseUtil.success(fullHttpResponse, true, document);

      case "clearcache":
        if (!UserUtil.hasPermission(user, "cloudnet.web.master.clearcache", "*")) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
        }
        CloudNet.getInstance().getWrappers().values().stream().filter(wrapper ->
            wrapper.getChannel() != null).forEach(wrapper ->
            wrapper.sendCommand("clearcache"));
        return ResponseUtil.success(fullHttpResponse, true, document);

      case "stop":
        if (!UserUtil.hasPermission(user, "cloudnet.web.master.stop", "*")) {
          return ResponseUtil.permissionDenied(fullHttpResponse);
        }
        CloudNet.getInstance().shutdown();
        return ResponseUtil.success(fullHttpResponse, true, document);

      case "command":
        if (RequestUtil.hasHeader(httpRequest, "-Xvalue")) {
          final String command = RequestUtil.getHeaderValue(httpRequest, "-Xvalue");
          if (!UserUtil.hasPermission(user, "cloudnet.web.master.command.*", "*",
              "cloudnet.web.master.command." + command)) {
            return ResponseUtil.permissionDenied(fullHttpResponse);
          }
          projectMain.getCloud().getCommandManager().dispatchCommand(command);
          return ResponseUtil.success(fullHttpResponse, true, document);
        } else {
          return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
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