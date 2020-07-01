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
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpUtility;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpUser;
import me.madfix.cloudnet.webinterface.http.v2.utils.Request;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpResponseUtility;

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
        FullHttpResponse fullHttpResponse = HttpUtility.simpleCheck(httpRequest);
        Document document = new Document();
        switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "corelog":
                document.append("response", webInterface.getConsoleLines());
                return HttpResponseUtility.success(fullHttpResponse, document);

            case "commands":
                document.append("response", webInterface.getCloud().getCommandManager().getCommands());
                return HttpResponseUtility.success(fullHttpResponse, document);

            default:
                return HttpResponseUtility.messageFieldNotFound(fullHttpResponse);

        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
                                 QueryDecoder queryDecoder,
                                 PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        FullHttpResponse fullHttpResponse = HttpUtility.simpleCheck(httpRequest);
        User user = HttpUtility.getUser(httpRequest);
        Document document = new Document();
        switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "reloadall":
                if (!HttpUser.hasPermission(user, "cloudnet.web.master.reload.all", "*",
                        "cloudnet.web.master.reload.*")) {
                    return HttpResponseUtility.permissionDenied(fullHttpResponse);
                }
                CloudNet.getInstance().reload();
                return HttpResponseUtility.success(fullHttpResponse, document);

            case "reloadconfig":
                if (!HttpUser.hasPermission(user, "cloudnet.web.master.reload.config", "*",
                        "cloudnet.web.master.reload.*")) {
                    return HttpResponseUtility.permissionDenied(fullHttpResponse);
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
                return HttpResponseUtility.success(fullHttpResponse, document);

            case "reloadwrapper":
                if (!HttpUser.hasPermission(user, "cloudnet.web.master.reload.wrapper", "*",
                        "cloudnet.web.master.reload.*")) {
                    return HttpResponseUtility.permissionDenied(fullHttpResponse);
                }
                CloudNet.getInstance().getWrappers().values().stream().filter(wrapper ->
                        wrapper.getChannel() != null).forEach(wrapper ->
                        wrapper.sendCommand("reload"));
                return HttpResponseUtility.success(fullHttpResponse, document);

            case "clearcache":
                if (!HttpUser.hasPermission(user, "cloudnet.web.master.clearcache", "*")) {
                    return HttpResponseUtility.permissionDenied(fullHttpResponse);
                }
                CloudNet.getInstance().getWrappers().values().stream().filter(wrapper ->
                        wrapper.getChannel() != null).forEach(wrapper ->
                        wrapper.sendCommand("clearcache"));
                return HttpResponseUtility.success(fullHttpResponse, document);

            case "stop":
                if (!HttpUser.hasPermission(user, "cloudnet.web.master.stop", "*")) {
                    return HttpResponseUtility.permissionDenied(fullHttpResponse);
                }
                CloudNet.getInstance().shutdown();
                return HttpResponseUtility.success(fullHttpResponse, document);

            case "command":
                if (Request.hasHeader(httpRequest, "-Xvalue")) {
                    final String command = Request.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUser.hasPermission(user, "cloudnet.web.master.command.*", "*",
                            "cloudnet.web.master.command." + command)) {
                        return HttpResponseUtility.permissionDenied(fullHttpResponse);
                    }
                    webInterface.getCloud().getCommandManager().dispatchCommand(command);
                    return HttpResponseUtility.success(fullHttpResponse, document);
                } else {
                    return HttpResponseUtility.valueFieldNotFound(fullHttpResponse);
                }

            default:
                return HttpResponseUtility.messageFieldNotFound(fullHttpResponse);

        }
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
                                    QueryDecoder queryDecoder,
                                    PathProvider pathProvider, HttpRequest httpRequest) {
        return HttpResponseUtility.cross(httpRequest);
    }
}