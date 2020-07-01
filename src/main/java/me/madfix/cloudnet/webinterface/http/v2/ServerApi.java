package me.madfix.cloudnet.webinterface.http.v2;

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

public final class ServerApi extends MethodWebHandlerAdapter {

    private final WebInterface webInterface;

    /**
     * Imitated the class.
     *
     * @param cloudNet     the main class of cloudnet
     * @param webInterface the main class of the project
     */
    public ServerApi(CloudNet cloudNet, WebInterface webInterface) {
        super("/cloudnet/api/v2/servergroup");
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
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.servers", "*")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                resp.append("response", new ArrayList<>(CloudNet.getInstance().getServerGroups().keySet()));
                return HttpResponseHelper.success(fullHttpResponse, resp);

            case "screen":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")
                        && CloudNet.getInstance().getServers().containsKey(RequestHelper
                        .headerValue(httpRequest, "-Xvalue"))) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    MinecraftServer server = CloudNet.getInstance().getServer(group);
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.screen.servers.info.*", "*",
                            "cloudnet.web.screen.servers.info.group." + server.getServiceId().getGroup())) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    if (!CloudNet.getInstance().getScreenProvider().getScreens()
                            .containsKey(server.getServiceId().getServerId())) {
                        server.getWrapper().enableScreen(server.getServerInfo());
                    }
                    if (webInterface.getScreenInfos().containsKey(server.getServiceId().getServerId())) {
                        resp.append("response", webInterface.getScreenInfos().get(
                                server.getServiceId().getServerId()));
                    }
                    return HttpResponseHelper.success(fullHttpResponse, resp);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }

            case "servers":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")
                        && CloudNet.getInstance().getServerGroups().containsKey(RequestHelper.headerValue(
                        httpRequest, "-Xvalue"))) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.servers.info.*", "*",
                            "cloudnet.web.group.servers.info." + group)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    resp.append("response",
                            CloudNet.getInstance().getServers(group).stream().map(minecraftServer ->
                                    JsonUtils.getGson().toJson(minecraftServer.getLastServerInfo()))
                                    .collect(Collectors.toList()));
                    return HttpResponseHelper.success(fullHttpResponse, resp);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }

            case "allservers":
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.allservers.info.*", "*")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                resp.append("response",
                        CloudNet.getInstance().getServers().values().stream().map(minecraftServer ->
                                JsonUtils.getGson().toJson(minecraftServer.getLastServerInfo().toSimple()))
                                .collect(Collectors.toList()));
                return HttpResponseHelper.success(fullHttpResponse, resp);

            case "group":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")
                        && CloudNet.getInstance().getServerGroups().containsKey(
                        RequestHelper.headerValue(httpRequest, "-Xvalue"))) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.server.info.*", "*",
                            "cloudnet.web.group.server.info." + group)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    Document data = new Document();
                    data.append(group,
                            JsonUtils.getGson().toJson(CloudNet.getInstance().getServerGroup(group)));
                    resp.append("response", data);
                    return HttpResponseHelper.success(fullHttpResponse, resp);
                } else {
                    resp.append("response",
                            CloudNet.getInstance().getServerGroups().values().stream().filter(serverGroup ->
                                    HttpUserHelper.hasPermission(user, "*", "cloudnet.web.group.server.item.*",
                                            "cloudnet.web.proxy.group.server.item." + serverGroup.getName()))
                                    .map(serverGroup ->
                                            JsonUtils.getGson().toJson(serverGroup)).collect(Collectors.toList()));
                    return HttpResponseHelper.success(fullHttpResponse, resp);
                }
            default:
                return HttpResponseHelper.messageFieldNotFound(fullHttpResponse);

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
            case "stop":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")
                        && CloudNet.getInstance().getProxyGroups()
                        .containsKey(RequestHelper.headerValue(httpRequest,
                                "-Xvalue"))) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.server.stop.*", "*",
                            "cloudnet.web.group.server.stop." + group)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    CloudNet.getInstance().getServers(group).forEach(t ->
                            CloudNet.getInstance().stopServer(t.getName()));
                    return HttpResponseHelper.success(fullHttpResponse, document);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }

            case "command":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")
                        && RequestHelper.hasHeader(httpRequest,
                        "-Xcount")) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    String command = RequestHelper.headerValue(httpRequest, "-Xcount");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.screen.server.command.*", "*",
                            "cloudnet.web.screen.server.command." + command.split(" ")[0])) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    MinecraftServer server = CloudNet.getInstance().getServer(group);
                    server.getWrapper().writeServerCommand(command, server.getServerInfo());
                    return HttpResponseHelper.success(fullHttpResponse, document);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }

            case "stopscreen":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")
                        && CloudNet.getInstance().getScreenProvider().getScreens().containsKey(
                        RequestHelper.headerValue(httpRequest, "-Xvalue"))) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    MinecraftServer server = CloudNet.getInstance().getServer(group);
                    server.getWrapper().disableScreen(server.getServerInfo());
                    return HttpResponseHelper.success(fullHttpResponse, document);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }

            case "delete":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")
                        && CloudNet.getInstance().getServerGroups().containsKey(
                        RequestHelper.headerValue(httpRequest, "-Xvalue"))) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.server.delete.*", "*",
                            "cloudnet.web.group.server.delete." + group)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    CloudNet.getInstance().getServers(group).forEach(t ->
                            CloudNet.getInstance().stopServer(t.getName()));
                    ServerGroup serverGroup = CloudNet.getInstance().getServerGroup(group);
                    CloudNet.getInstance().getServerGroups().remove(serverGroup.getName());
                    Collection<String> wrappers = serverGroup.getWrapper();
                    CloudNet.getInstance().getConfig().deleteGroup(serverGroup);
                    CloudNet.getInstance().toWrapperInstances(wrappers).forEach(Wrapper::updateWrapper);
                    return HttpResponseHelper.success(fullHttpResponse, document);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }

            case "save":
                String servergroup = RequestHelper.content(httpRequest);
                if (servergroup.isEmpty()) {
                    return HttpResponseHelper.badRequest(fullHttpResponse, new Document());
                }
                ServerGroup serverGroup = JsonUtils.getGson().fromJson(servergroup, ServerGroup.class);
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.server.save.*", "*",
                        "cloudnet.web.group.server.save." + serverGroup.getName())) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
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
                return HttpResponseHelper.success(fullHttpResponse, document);

            case "start":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue", "-Xcount")
                        && CloudNet.getInstance().getServerGroups()
                        .containsKey(RequestHelper.headerValue(httpRequest,
                                "-Xvalue"))) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    int count = Integer.parseInt(RequestHelper.headerValue(httpRequest, "-Xcount"));
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.group.server.start.*", "*",
                            "cloudnet.web.group.server.start." + group)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    for (int i = 0; i < count; i++) {
                        CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroup(group));
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
