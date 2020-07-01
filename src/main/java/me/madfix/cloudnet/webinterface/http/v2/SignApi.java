package me.madfix.cloudnet.webinterface.http.v2;

import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayoutConfig;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpResponseHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpUserHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpAuthHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.JsonUtils;
import me.madfix.cloudnet.webinterface.http.v2.utils.RequestHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SignApi extends MethodWebHandlerAdapter {

    private final Path path;
    private final WebInterface webInterface;

    /**
     * Process the request about the sign system for cloudnet.
     *
     * @param webInterface The main class from the project
     */
    public SignApi(WebInterface webInterface) {
        super("/cloudnet/api/v2/sign");
        CloudNet.getInstance().getWebServer().getWebServerProvider().registerHandler(this);
        this.path = Paths.get("local/signLayout.json");
        this.webInterface = webInterface;
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
                                QueryDecoder queryDecoder,
                                PathProvider pathProvider, HttpRequest httpRequest) {
        new DefaultFullHttpResponse(
                httpRequest.getProtocolVersion(),
                HttpResponseStatus.OK);
        FullHttpResponse fullHttpResponse = HttpAuthHelper.simpleCheck(httpRequest);
        User user = HttpAuthHelper.getUser(httpRequest);

        if (!HttpUserHelper.hasPermission(user, "*", "cloudnet.web.module.sign.load")) {
            return HttpResponseHelper.permissionDenied(fullHttpResponse);
        }
        Document resp = new Document();
        switch (RequestHelper.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "check":
                resp.append("response", !CloudNet.getInstance().getConfig().getDisabledModules()
                        .contains("CloudNet-Service-SignsModule"));
                return HttpResponseHelper.success(fullHttpResponse, resp);
            case "config":
                Document document = Document.loadDocument(this.path);
                SignLayoutConfig signLayoutConfig = JsonUtils.getGson()
                        .fromJson(document.get("layout_config"), SignLayoutConfig.class);
                resp.append("response", JsonUtils.getGson().toJson(signLayoutConfig));
                return HttpResponseHelper.success(fullHttpResponse, resp);
            case "random":
                Random random = new Random();
                ArrayList<MinecraftServer> arrayList = new ArrayList<>(
                        CloudNet.getInstance().getServers().values());
                if (arrayList.size() > 0) {
                    resp.append("response",
                            JsonUtils.getGson().toJson(arrayList.get(random.nextInt(arrayList.size()))));
                    return HttpResponseHelper.success(fullHttpResponse, resp);
                } else {
                    return HttpResponseHelper.badRequest(fullHttpResponse, new Document());
                }
            case "db":
                resp.append("response",
                        webInterface.getSignDatabase().loadAll().values().stream().map(sign ->
                                JsonUtils.getGson().toJson(sign)).collect(Collectors.toList()));
                return HttpResponseHelper.success(fullHttpResponse, resp);
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
        switch (RequestHelper.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "save": {
                String content = RequestHelper.content(httpRequest);
                if (content.isEmpty()) {
                    return HttpResponseHelper.badRequest(fullHttpResponse, new Document());
                }
                if (!HttpUserHelper.hasPermission(user, "*", "cloudnet.web.module.sign.save")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                SignLayoutConfig signLayoutConfig = JsonUtils.getGson()
                        .fromJson(content, SignLayoutConfig.class);
                final Document document = Document.loadDocument(this.path);
                document.append("layout_config", signLayoutConfig);
                document.saveAsConfig(this.path);
                CloudNet.getInstance().getNetworkManager().updateAll();
                return HttpResponseHelper.success(fullHttpResponse, new Document());
            }
            case "delete": {
                String content = RequestHelper.content(httpRequest);
                if (!HttpUserHelper.hasPermission(user, "*", "cloudnet.web.module.sign.delete.*")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                UUID id = UUID.fromString(content);
                webInterface.getSignDatabase().removeSign(id);
                CloudNet.getInstance().getNetworkManager().updateAll();
                return HttpResponseHelper.success(fullHttpResponse, new Document());
            }

            case "add": {
                if (!HttpUserHelper.hasPermission(user, "*", "cloudnet.web.module.sign.add")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                String content = RequestHelper.content(httpRequest);
                Sign s = JsonUtils.getGson().fromJson(content, Sign.class);
                webInterface.getSignDatabase().appendSign(s);
                CloudNet.getInstance().getNetworkManager().updateAll();
                return HttpResponseHelper.success(fullHttpResponse, new Document());
            }
            default: {
                return HttpResponseHelper.messageFieldNotFound(fullHttpResponse);
            }
        }
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
                                    QueryDecoder queryDecoder,
                                    PathProvider pathProvider, HttpRequest httpRequest) {
        return HttpResponseHelper.cross(httpRequest);
    }
}