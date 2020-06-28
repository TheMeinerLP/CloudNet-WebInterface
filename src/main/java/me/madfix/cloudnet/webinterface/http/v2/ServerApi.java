package me.madfix.cloudnet.webinterface.http.v2;

import me.madfix.cloudnet.webinterface.http.v2.utils.Http;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpUser;
import me.madfix.cloudnet.webinterface.http.v2.utils.JsonUtil;
import me.madfix.cloudnet.webinterface.http.v2.utils.Request;
import me.madfix.cloudnet.webinterface.http.v2.utils.Response;
import me.madfix.cloudnet.webinterface.ProjectMain;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public final class ServerApi extends MethodWebHandlerAdapter {

    private final ProjectMain projectMain;

    /**
     * Imitated the class.
     *
     * @param cloudNet    the main class of cloudnet
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
        FullHttpResponse fullHttpResponse = Http.simpleCheck(httpRequest);
        User user = Http.getUser(httpRequest);
        Document resp = new Document();
        switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "groups":
                if (!HttpUser.hasPermission(user, "cloudnet.web.group.servers", "*")) {
                    return Response.permissionDenied(fullHttpResponse);
                }
                resp.append("response", new ArrayList<>(CloudNet.getInstance().getServerGroups().keySet()));
                return Response.success(fullHttpResponse, resp);

            case "screen":
                if (Request.hasHeader(httpRequest, "-Xvalue")
                        && CloudNet.getInstance().getServers().containsKey(Request
                        .headerValue(httpRequest, "-Xvalue"))) {
                    String group = Request.headerValue(httpRequest, "-Xvalue");
                    MinecraftServer server = CloudNet.getInstance().getServer(group);
                    if (!HttpUser.hasPermission(user, "cloudnet.web.screen.servers.info.*", "*",
                            "cloudnet.web.screen.servers.info.group." + server.getServiceId().getGroup())) {
                        return Response.permissionDenied(fullHttpResponse);
                    }
                    if (!CloudNet.getInstance().getScreenProvider().getScreens()
                            .containsKey(server.getServiceId().getServerId())) {
                        server.getWrapper().enableScreen(server.getServerInfo());
                    }
                    if (projectMain.getScreenInfos().containsKey(server.getServiceId().getServerId())) {
                        resp.append("response", projectMain.getScreenInfos().get(
                                server.getServiceId().getServerId()));
                    }
                    return Response.success(fullHttpResponse, resp);
                } else {
                    return Response.valueFieldNotFound(fullHttpResponse);
                }

            case "servers":
                if (Request.hasHeader(httpRequest, "-Xvalue")
                        && CloudNet.getInstance().getServerGroups().containsKey(Request.headerValue(
                        httpRequest, "-Xvalue"))) {
                    String group = Request.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUser.hasPermission(user, "cloudnet.web.group.servers.info.*", "*",
                            "cloudnet.web.group.servers.info." + group)) {
                        return Response.permissionDenied(fullHttpResponse);
                    }
                    resp.append("response",
                            CloudNet.getInstance().getServers(group).stream().map(minecraftServer ->
                                    JsonUtil.getGson().toJson(minecraftServer.getLastServerInfo()))
                                    .collect(Collectors.toList()));
                    return Response.success(fullHttpResponse, resp);
                } else {
                    return Response.valueFieldNotFound(fullHttpResponse);
                }

            case "allservers":
                if (!HttpUser.hasPermission(user, "cloudnet.web.group.allservers.info.*", "*")) {
                    return Response.permissionDenied(fullHttpResponse);
                }
                resp.append("response",
                        CloudNet.getInstance().getServers().values().stream().map(minecraftServer ->
                                JsonUtil.getGson().toJson(minecraftServer.getLastServerInfo().toSimple()))
                                .collect(Collectors.toList()));
                return Response.success(fullHttpResponse, resp);

            case "group":
                if (Request.hasHeader(httpRequest, "-Xvalue")
                        && CloudNet.getInstance().getServerGroups().containsKey(
                        Request.headerValue(httpRequest, "-Xvalue"))) {
                    String group = Request.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUser.hasPermission(user, "cloudnet.web.group.server.info.*", "*",
                            "cloudnet.web.group.server.info." + group)) {
                        return Response.permissionDenied(fullHttpResponse);
                    }
                    Document data = new Document();
                    data.append(group,
                            JsonUtil.getGson().toJson(CloudNet.getInstance().getServerGroup(group)));
                    resp.append("response", data);
                    return Response.success(fullHttpResponse, resp);
                } else {
                    resp.append("response",
                            CloudNet.getInstance().getServerGroups().values().stream().filter(serverGroup ->
                                    HttpUser.hasPermission(user, "*", "cloudnet.web.group.server.item.*",
                                            "cloudnet.web.proxy.group.server.item." + serverGroup.getName()))
                                    .map(serverGroup ->
                                            JsonUtil.getGson().toJson(serverGroup)).collect(Collectors.toList()));
                    return Response.success(fullHttpResponse, resp);
                }
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
        Document document = new Document();
        switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "stop":
                if (Request.hasHeader(httpRequest, "-Xvalue")
                        && CloudNet.getInstance().getProxyGroups()
                        .containsKey(Request.headerValue(httpRequest,
                                "-Xvalue"))) {
                    String group = Request.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUser.hasPermission(user, "cloudnet.web.group.server.stop.*", "*",
                            "cloudnet.web.group.server.stop." + group)) {
                        return Response.permissionDenied(fullHttpResponse);
                    }
                    CloudNet.getInstance().getServers(group).forEach(t ->
                            CloudNet.getInstance().stopServer(t.getName()));
                    return Response.success(fullHttpResponse, document);
                } else {
                    return Response.valueFieldNotFound(fullHttpResponse);
                }

            case "command":
                if (Request.hasHeader(httpRequest, "-Xvalue")
                        && Request.hasHeader(httpRequest,
                        "-Xcount")) {
                    String group = Request.headerValue(httpRequest, "-Xvalue");
                    String command = Request.headerValue(httpRequest, "-Xcount");
                    if (!HttpUser.hasPermission(user, "cloudnet.web.screen.server.command.*", "*",
                            "cloudnet.web.screen.server.command." + command.split(" ")[0])) {
                        return Response.permissionDenied(fullHttpResponse);
                    }
                    MinecraftServer server = CloudNet.getInstance().getServer(group);
                    server.getWrapper().writeServerCommand(command, server.getServerInfo());
                    return Response.success(fullHttpResponse, document);
                } else {
                    return Response.valueFieldNotFound(fullHttpResponse);
                }

            case "stopscreen":
                if (Request.hasHeader(httpRequest, "-Xvalue")
                        && CloudNet.getInstance().getScreenProvider().getScreens().containsKey(
                        Request.headerValue(httpRequest, "-Xvalue"))) {
                    String group = Request.headerValue(httpRequest, "-Xvalue");
                    MinecraftServer server = CloudNet.getInstance().getServer(group);
                    server.getWrapper().disableScreen(server.getServerInfo());
                    return Response.success(fullHttpResponse, document);
                } else {
                    return Response.valueFieldNotFound(fullHttpResponse);
                }

            case "delete":
                if (Request.hasHeader(httpRequest, "-Xvalue")
                        && CloudNet.getInstance().getServerGroups().containsKey(
                        Request.headerValue(httpRequest, "-Xvalue"))) {
                    String group = Request.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUser.hasPermission(user, "cloudnet.web.group.server.delete.*", "*",
                            "cloudnet.web.group.server.delete." + group)) {
                        return Response.permissionDenied(fullHttpResponse);
                    }
                    CloudNet.getInstance().getServers(group).forEach(t ->
                            CloudNet.getInstance().stopServer(t.getName()));
                    ServerGroup serverGroup = CloudNet.getInstance().getServerGroup(group);
                    CloudNet.getInstance().getServerGroups().remove(serverGroup.getName());
                    Collection<String> wrappers = serverGroup.getWrapper();
                    CloudNet.getInstance().getConfig().deleteGroup(serverGroup);
                    CloudNet.getInstance().toWrapperInstances(wrappers).forEach(Wrapper::updateWrapper);
                    return Response.success(fullHttpResponse, document);
                } else {
                    return Response.valueFieldNotFound(fullHttpResponse);
                }

            case "save":
                String servergroup = Request.content(httpRequest);
                if (servergroup.isEmpty()) {
                    return Response.badRequest(fullHttpResponse, new Document());
                }
                ServerGroup serverGroup = JsonUtil.getGson().fromJson(servergroup, ServerGroup.class);
                if (!HttpUser.hasPermission(user, "cloudnet.web.group.server.save.*", "*",
                        "cloudnet.web.group.server.save." + serverGroup.getName())) {
                    return Response.permissionDenied(fullHttpResponse);
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
                return Response.success(fullHttpResponse, document);

            case "start":
                if (Request.hasHeader(httpRequest, "-Xvalue", "-Xcount")
                        && CloudNet.getInstance().getServerGroups()
                        .containsKey(Request.headerValue(httpRequest,
                                "-Xvalue"))) {
                    String group = Request.headerValue(httpRequest, "-Xvalue");
                    int count = Integer.parseInt(Request.headerValue(httpRequest, "-Xcount"));
                    if (!HttpUser.hasPermission(user, "cloudnet.web.group.server.start.*", "*",
                            "cloudnet.web.group.server.start." + group)) {
                        return Response.permissionDenied(fullHttpResponse);
                    }
                    for (int i = 0; i < count; i++) {
                        CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroup(group));
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
