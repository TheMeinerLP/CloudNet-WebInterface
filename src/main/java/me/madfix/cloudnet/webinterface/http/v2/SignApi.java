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
import me.madfix.cloudnet.webinterface.http.v2.utils.*;

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
        FullHttpResponse fullHttpResponse = HttpUtility.simpleCheck(httpRequest);
        User user = HttpUtility.getUser(httpRequest);

        if (!HttpUser.hasPermission(user, "*", "cloudnet.web.module.sign.load")) {
            return HttpResponseUtility.permissionDenied(fullHttpResponse);
        }
        Document resp = new Document();
        switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "check":
                resp.append("response", !CloudNet.getInstance().getConfig().getDisabledModules()
                        .contains("CloudNet-Service-SignsModule"));
                return HttpResponseUtility.success(fullHttpResponse, resp);
            case "config":
                Document document = Document.loadDocument(this.path);
                SignLayoutConfig signLayoutConfig = JsonUtil.getGson()
                        .fromJson(document.get("layout_config"), SignLayoutConfig.class);
                resp.append("response", JsonUtil.getGson().toJson(signLayoutConfig));
                return HttpResponseUtility.success(fullHttpResponse, resp);
            case "random":
                Random random = new Random();
                ArrayList<MinecraftServer> arrayList = new ArrayList<>(
                        CloudNet.getInstance().getServers().values());
                if (arrayList.size() > 0) {
                    resp.append("response",
                            JsonUtil.getGson().toJson(arrayList.get(random.nextInt(arrayList.size()))));
                    return HttpResponseUtility.success(fullHttpResponse, resp);
                } else {
                    return HttpResponseUtility.badRequest(fullHttpResponse, new Document());
                }
            case "db":
                resp.append("response",
                        webInterface.getSignDatabase().loadAll().values().stream().map(sign ->
                                JsonUtil.getGson().toJson(sign)).collect(Collectors.toList()));
                return HttpResponseUtility.success(fullHttpResponse, resp);
            default:
                return HttpResponseUtility.messageFieldNotFound(fullHttpResponse);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
                                 QueryDecoder queryDecoder,
                                 PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = HttpUtility.simpleCheck(httpRequest);
        User user = HttpUtility.getUser(httpRequest);
        switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "save": {
                String content = Request.content(httpRequest);
                if (content.isEmpty()) {
                    return HttpResponseUtility.badRequest(fullHttpResponse, new Document());
                }
                if (!HttpUser.hasPermission(user, "*", "cloudnet.web.module.sign.save")) {
                    return HttpResponseUtility.permissionDenied(fullHttpResponse);
                }
                SignLayoutConfig signLayoutConfig = JsonUtil.getGson()
                        .fromJson(content, SignLayoutConfig.class);
                final Document document = Document.loadDocument(this.path);
                document.append("layout_config", signLayoutConfig);
                document.saveAsConfig(this.path);
                CloudNet.getInstance().getNetworkManager().updateAll();
                return HttpResponseUtility.success(fullHttpResponse, new Document());
            }
            case "delete": {
                String content = Request.content(httpRequest);
                if (!HttpUser.hasPermission(user, "*", "cloudnet.web.module.sign.delete.*")) {
                    return HttpResponseUtility.permissionDenied(fullHttpResponse);
                }
                UUID id = UUID.fromString(content);
                webInterface.getSignDatabase().removeSign(id);
                CloudNet.getInstance().getNetworkManager().updateAll();
                return HttpResponseUtility.success(fullHttpResponse, new Document());
            }

            case "add": {
                if (!HttpUser.hasPermission(user, "*", "cloudnet.web.module.sign.add")) {
                    return HttpResponseUtility.permissionDenied(fullHttpResponse);
                }
                String content = Request.content(httpRequest);
                Sign s = JsonUtil.getGson().fromJson(content, Sign.class);
                webInterface.getSignDatabase().appendSign(s);
                CloudNet.getInstance().getNetworkManager().updateAll();
                return HttpResponseUtility.success(fullHttpResponse, new Document());
            }
            default: {
                return HttpResponseUtility.messageFieldNotFound(fullHttpResponse);
            }
        }
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
                                    QueryDecoder queryDecoder,
                                    PathProvider pathProvider, HttpRequest httpRequest) {
        return HttpResponseUtility.cross(httpRequest);
    }
}