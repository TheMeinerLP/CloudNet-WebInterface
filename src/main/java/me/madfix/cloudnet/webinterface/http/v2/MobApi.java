package me.madfix.cloudnet.webinterface.http.v2;

import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpResponseHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpUserHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpAuthHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.JsonUtils;
import me.madfix.cloudnet.webinterface.http.v2.utils.RequestHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Collectors;

public final class MobApi extends MethodWebHandlerAdapter {

    private final Path path;
    private final WebInterface webInterface;

    /**
     * Manage the requests about the mob system of cloudnet.
     *
     * @param webInterface The main class of the project
     */
    public MobApi(WebInterface webInterface) {
        super("/cloudnet/api/v2/mob");
        CloudNet.getInstance().getWebServer().getWebServerProvider().registerHandler(this);
        this.path = Paths.get("local/servermob_config.json");
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
        if (!HttpUserHelper.hasPermission(user, "*", "cloudnet.web.module.mob.load")) {
            return HttpResponseHelper.permissionDenied(fullHttpResponse);
        }
        switch (RequestHelper.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {

            case "check": {
                resp.append("response", !CloudNet.getInstance().getConfig().getDisabledModules()
                        .contains("CloudNet-Service-MobModule"));
                return HttpResponseHelper.success(fullHttpResponse, resp);
            }
            case "config": {
                MobConfig config = JsonUtils.getGson().fromJson(Document.loadDocument(this.path)
                        .get("mobConfig"), MobConfig.class);
                resp.append("response", JsonUtils.getGson().toJson(config));
                return HttpResponseHelper.success(fullHttpResponse, resp);
            }
            case "db": {
                resp.append("response", webInterface.getMobDatabase().loadAll().values().stream()
                        .map(serverMob -> JsonUtils.getGson().toJson(serverMob)).collect(
                                Collectors.toList()));
                return HttpResponseHelper.success(fullHttpResponse, resp);

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
        String content = RequestHelper.content(httpRequest);
        switch (RequestHelper.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "save":
                if (content.isEmpty()) {
                    return HttpResponseHelper.badRequest(fullHttpResponse, new Document());
                }
                if (!HttpUserHelper.hasPermission(user, "*", "cloudnet.web.module.mob.save")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                MobConfig signLayoutConfig = JsonUtils.getGson().fromJson(content, MobConfig.class);
                Document document = Document.loadDocument(this.path);
                document.append("mobConfig", signLayoutConfig);
                document.saveAsConfig(this.path);
                CloudNet.getInstance().getNetworkManager().updateAll();
                return HttpResponseHelper.success(fullHttpResponse, new Document());

            case "delete":
                ServerMob mob = JsonUtils.getGson().fromJson(content, ServerMob.class);
                if (!HttpUserHelper.hasPermission(user, "*", "cloudnet.web.module.mob.delete")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                webInterface.getMobDatabase().remove(mob.getUniqueId());
                CloudNet.getInstance().getNetworkManager().updateAll();
                return HttpResponseHelper.success(fullHttpResponse, new Document());

            case "add":
                mob = JsonUtils.getGson().fromJson(content, ServerMob.class);
                if (!HttpUserHelper.hasPermission(user, "*", "cloudnet.web.module.mob.add")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                webInterface.getMobDatabase().add(mob);
                CloudNet.getInstance().getNetworkManager().updateAll();
                return HttpResponseHelper.success(fullHttpResponse, new Document());

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
