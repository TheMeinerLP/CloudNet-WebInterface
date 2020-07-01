package me.madfix.cloudnet.webinterface.http.v2;

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
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpResponseHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpUserHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpAuthHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.JsonUtils;
import me.madfix.cloudnet.webinterface.http.v2.utils.RequestHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public final class ProxyApi extends MethodWebHandlerAdapter {

    private final WebInterface webInterface;

    /**
     * Initiated the class.
     *
     * @param cloudNet     The main class of cloudnet
     * @param webInterface The main class of the project
     */
    public ProxyApi(CloudNet cloudNet, WebInterface webInterface) {
        super("/cloudnet/api/v2/proxygroup");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.webInterface = webInterface;
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
                                QueryDecoder queryDecoder,
                                PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = HttpAuthHelper.simpleCheck(httpRequest);
        User user = HttpAuthHelper.getUser(httpRequest);
        Document resp = new Document();
        switch (RequestHelper.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "groups":
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.proxys", "*")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                resp.append("response", new ArrayList<>(webInterface.getCloud().getProxyGroups().keySet()));
                return HttpResponseHelper.success(fullHttpResponse, resp);

            case "groupitems":
                resp.append("response",
                        webInterface.getCloud().getProxyGroups().keySet().stream().filter(s ->
                                HttpUserHelper.hasPermission(user, "*", "cloudnet.web.group.proxy.item.*",
                                        "cloudnet.web.proxy.group.proxy.item." + s)).map(s -> {
                            ProxyGroup group = CloudNet.getInstance().getProxyGroup(s);
                            Document document = new Document();
                            document.append("name", group.getName());
                            document.append("version", group.getProxyVersion().name());
                            document.append("status", group.getProxyConfig().isEnabled());
                            return document.convertToJson();
                        }).collect(Collectors.toList()));
                return HttpResponseHelper.success(fullHttpResponse, resp);

            case "group":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")
                        && webInterface.getCloud().getProxyGroups()
                        .containsKey(RequestHelper.headerValue(httpRequest,
                                "-Xvalue"))) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.proxy.info.*", "*",
                            "cloudnet.web.group.proxy.info." + group)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    Document data = new Document();
                    data.append(group,
                            JsonUtils.getGson().toJson(webInterface.getCloud().getProxyGroup(group)));
                    resp.append("response", data);
                    return HttpResponseHelper.success(fullHttpResponse, resp);
                } else {
                    resp.append("response",
                            CloudNet.getInstance().getProxyGroups().values().stream().filter(proxyGroup ->
                                    HttpUserHelper.hasPermission(user, "*", "cloudnet.web.group.proxy.item.*",
                                            "cloudnet.web.proxy.group.proxy.item." + proxyGroup.getName()))
                                    .map(proxyGroup ->
                                            JsonUtils.getGson().toJson(proxyGroup)).collect(
                                    Collectors.toList()));
                    return HttpResponseHelper.success(fullHttpResponse, resp);
                }

            case "screen":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")
                        && webInterface.getCloud().getProxys().containsKey(RequestHelper
                        .headerValue(httpRequest, "-Xvalue"))) {
                    final String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    ProxyServer server = webInterface.getCloud().getProxy(group);
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.screen.proxys.info.*", "*",
                            "cloudnet.web.screen.proxys.info." + server.getServiceId().getGroup())) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    if (!webInterface.getCloud().getScreenProvider().getScreens().containsKey(
                            server.getServiceId().getServerId())) {
                        server.getWrapper().enableScreen(server.getProxyInfo());
                    }
                    if (webInterface.getScreenInfos().containsKey(server.getServiceId().getServerId())) {
                        resp.append("response", webInterface
                                .getScreenInfos().get(server.getServiceId().getServerId()));
                    }
                    return HttpResponseHelper.success(fullHttpResponse, resp);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }

            case "proxys":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")
                        && webInterface.getCloud().getProxyGroups()
                        .containsKey(RequestHelper.headerValue(httpRequest,
                                "-Xvalue"))) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.proxys.info.*", "*",
                            "cloudnet.web.group.proxys.info." + group)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    resp.append("response",
                            CloudNet.getInstance().getProxys(group).stream().map(proxyServer ->
                                    JsonUtils.getGson().toJson(proxyServer.getProxyInfo().toSimple())).collect(
                                    Collectors.toList()));
                    return HttpResponseHelper.success(fullHttpResponse, resp);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }

            default: {
                return HttpResponseHelper.messageFieldNotFound(fullHttpResponse);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
                                 QueryDecoder queryDecoder,
                                 PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = HttpAuthHelper.simpleCheck(httpRequest);
        User user = HttpAuthHelper.getUser(httpRequest);
        Document document = new Document();
        switch (RequestHelper.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "command":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue") && RequestHelper.hasHeader(httpRequest,
                        "-Xcount")) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    String command = RequestHelper.headerValue(httpRequest, "-Xcount");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.screen.proxy.command.*", "*",
                            "cloudnet.web.screen.proxy.command." + command.split(" ")[0])) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    ProxyServer server = webInterface.getCloud().getProxy(group);
                    server.getWrapper().writeProxyCommand(command, server.getProxyInfo());
                    return HttpResponseHelper.success(fullHttpResponse, document);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }

            case "stopscreen":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")
                        && webInterface.getCloud().getScreenProvider().getScreens().containsKey(
                        RequestHelper.headerValue(httpRequest, "-Xvalue"))) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    ProxyServer server = webInterface.getCloud().getProxy(group);
                    server.getWrapper().disableScreen(server.getProxyInfo());
                    return HttpResponseHelper.success(fullHttpResponse, document);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }

            case "stopproxy":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.proxy.stop.*", "*",
                            "cloudnet.web.proxy.stop." + group)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    webInterface.getCloud().stopProxy(group);
                    return HttpResponseHelper.success(fullHttpResponse, document);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }

            case "stop":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")
                        && webInterface.getCloud().getProxyGroups().containsKey(
                        RequestHelper.headerValue(httpRequest, "-Xvalue"))) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.proxy.stop.*", "*",
                            "cloudnet.web.group.proxy.stop." + group)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    webInterface.getCloud().getProxys(group).forEach(
                            t -> webInterface.getCloud().stopProxy(t.getName()));

                    return HttpResponseHelper.success(fullHttpResponse, document);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }

            case "delete":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")
                        && webInterface.getCloud().getProxyGroups().containsKey(
                        RequestHelper.headerValue(httpRequest, "-Xvalue"))) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.proxy.delete.*",
                            "*", "cloudnet.web.group.proxy.delete." + group)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    ProxyGroup grp = webInterface.getCloud().getProxyGroup(group);
                    CloudNet.getInstance().getProxyGroups().remove(grp.getName());
                    Collection<String> wrappers = grp.getWrapper();
                    webInterface.getCloud().getConfig().deleteGroup(grp);
                    CloudNet.getInstance().toWrapperInstances(wrappers).forEach(Wrapper::updateWrapper);

                    return HttpResponseHelper.success(fullHttpResponse, document);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }

            case "save":
                String proxyGroupName = RequestHelper.content(httpRequest);
                if (proxyGroupName.isEmpty()) {
                    return HttpResponseHelper.badRequest(fullHttpResponse, new Document());
                }
                ProxyGroup proxygn = JsonUtils.getGson().fromJson(proxyGroupName, ProxyGroup.class);
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.proxy.save.*", "*",
                        "cloudnet.web.group.proxy.save." + proxygn.getName())) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                webInterface.getCloud().getConfig().createGroup(proxygn);
                CloudNet.getInstance().setupProxy(proxygn);
                if (!CloudNet.getInstance().getProxyGroups().containsKey(proxygn.getName())) {
                    CloudNet.getInstance().getProxyGroups().put(proxygn.getName(), proxygn);
                } else {
                    CloudNet.getInstance().getProxyGroups().replace(proxygn.getName(), proxygn);
                }
                CloudNet.getInstance().toWrapperInstances(proxygn.getWrapper())
                        .forEach(Wrapper::updateWrapper);
                CloudNet.getInstance().getNetworkManager().updateAll();

                return HttpResponseHelper.success(fullHttpResponse, document);
            case "start":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue", "-xCount")
                        && webInterface.getCloud().getProxyGroups()
                        .containsKey(RequestHelper.headerValue(httpRequest,
                                "-Xvalue"))) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    int count = Integer.parseInt(RequestHelper.headerValue(httpRequest, "-Xcount"));
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.proxy.start.*", "*",
                            "cloudnet.web.group.proxy.start." + group)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    for (int i = 0; i < count; i++) {
                        webInterface.getCloud().startProxyAsync(webInterface.getCloud().getProxyGroup(group));
                    }

                    return HttpResponseHelper.success(fullHttpResponse, document);
                } else {
                    return HttpResponseHelper.fieldNotFound(fullHttpResponse,
                            "No available -Xvalue,-Xcount command found!");
                }

            default:
                return HttpResponseHelper.messageFieldNotFound(fullHttpResponse);

        }
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
                                    QueryDecoder queryDecoder,
                                    PathProvider pathProvider, HttpRequest httpRequest) {
        return HttpResponseHelper.cross(httpRequest);
    }
}