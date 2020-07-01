package me.madfix.cloudnet.webinterface.http.v2;

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
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpAuthHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpUserHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.RequestHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpResponseHelper;

import java.util.Locale;

public final class MasterApi extends MethodWebHandlerAdapter {

    private final WebInterface webInterface;

    /**
     * Manage request about master.
     *
     * @param cloudNet     The main class of cloudnet
     * @param webInterface The main class of the project
     */
    public MasterApi(CloudNet cloudNet, WebInterface webInterface) {
        super("/cloudnet/api/v2/master");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.webInterface = webInterface;
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
                                QueryDecoder queryDecoder,
                                PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = HttpAuthHelper.simpleCheck(httpRequest);
        Document document = new Document();
        switch (RequestHelper.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "corelog":
                document.append("response", webInterface.getConsoleLines());
                return HttpResponseHelper.success(fullHttpResponse, document);

            case "commands":
                document.append("response", webInterface.getCloud().getCommandManager().getCommands());
                return HttpResponseHelper.success(fullHttpResponse, document);

            default:
                return HttpResponseHelper.messageFieldNotFound(fullHttpResponse);

        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
                                 QueryDecoder queryDecoder,
                                 PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        FullHttpResponse fullHttpResponse = HttpAuthHelper.simpleCheck(httpRequest);
        User user = HttpAuthHelper.getUser(httpRequest);
        Document document = new Document();
        switch (RequestHelper.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "reloadall":
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.master.reload.all", "*",
                        "cloudnet.web.master.reload.*")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                CloudNet.getInstance().reload();
                return HttpResponseHelper.success(fullHttpResponse, document);

            case "reloadconfig":
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.master.reload.config", "*",
                        "cloudnet.web.master.reload.*")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
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
                return HttpResponseHelper.success(fullHttpResponse, document);

            case "reloadwrapper":
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.master.reload.wrapper", "*",
                        "cloudnet.web.master.reload.*")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                CloudNet.getInstance().getWrappers().values().stream().filter(wrapper ->
                        wrapper.getChannel() != null).forEach(wrapper ->
                        wrapper.sendCommand("reload"));
                return HttpResponseHelper.success(fullHttpResponse, document);

            case "clearcache":
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.master.clearcache", "*")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                CloudNet.getInstance().getWrappers().values().stream().filter(wrapper ->
                        wrapper.getChannel() != null).forEach(wrapper ->
                        wrapper.sendCommand("clearcache"));
                return HttpResponseHelper.success(fullHttpResponse, document);

            case "stop":
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.master.stop", "*")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                CloudNet.getInstance().shutdown();
                return HttpResponseHelper.success(fullHttpResponse, document);

            case "command":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")) {
                    final String command = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.master.command.*", "*",
                            "cloudnet.web.master.command." + command)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    webInterface.getCloud().getCommandManager().dispatchCommand(command);
                    return HttpResponseHelper.success(fullHttpResponse, document);
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
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