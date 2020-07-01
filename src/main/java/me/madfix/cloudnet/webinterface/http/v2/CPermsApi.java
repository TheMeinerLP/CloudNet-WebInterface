package me.madfix.cloudnet.webinterface.http.v2;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutUpdateOfflinePlayer;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutUpdatePlayer;
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
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public final class CPermsApi extends MethodWebHandlerAdapter {

    private final WebInterface webInterface;
    private PermissionPool pool;

    /**
     * Initiated the class.
     *
     * @param webInterface The main class of the project
     */
    public CPermsApi(WebInterface webInterface) {
        super("/cloudnet/api/v2/cperms");
        this.webInterface = webInterface;
        webInterface.getCloud().getWebServer().getWebServerProvider().registerHandler(this);
        pool = webInterface.getCloud().getNetworkManager().getModuleProperties()
                .getObject("permissionPool",
                        PermissionPool.TYPE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
                                QueryDecoder queryDecoder,
                                PathProvider pathProvider, HttpRequest httpRequest) {
        pool = webInterface.getCloud().getNetworkManager().getModuleProperties()
                .getObject("permissionPool",
                        PermissionPool.TYPE);
        FullHttpResponse fullHttpResponse = HttpAuthHelper.simpleCheck(httpRequest);
        User user = CloudNet.getInstance()
                .getUser(RequestHelper.headerValue(httpRequest, "-xcloudnet-user"));
        Document document = new Document();
        switch (RequestHelper.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "group":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")) {
                    String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.cperms.info.group.*", "*",
                            "cloudnet.web.cperms.info.group." + group)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    } else {
                        if (!pool.isAvailable()) {
                            return HttpResponseHelper.badRequest(fullHttpResponse, document);
                        }
                        document.append("response", JsonUtils.getGson().toJson(pool.getGroups().get(group)));
                        return HttpResponseHelper.success(fullHttpResponse, document);
                    }
                } else {
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.cperms.groups", "*")) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    } else {
                        if (!pool.isAvailable()) {
                            return HttpResponseHelper.badRequest(fullHttpResponse, document);
                        }
                        document.append("response", pool.getGroups().values().stream()
                                .map(permissionGroup -> JsonUtils.getGson().toJson(permissionGroup)).collect(
                                        Collectors.toList()));
                        return HttpResponseHelper.success(fullHttpResponse, document);
                    }
                }

            case "groups":
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.cperms.info.groups.*", "*")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                } else {
                    if (!pool.isAvailable()) {
                        return HttpResponseHelper.badRequest(fullHttpResponse, document);
                    }
                    document.append("response", new ArrayList<>(pool.getGroups().keySet()));
                    return HttpResponseHelper.success(fullHttpResponse, document);
                }

            case "user":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")) {
                    String userUuid = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.cperms.info.user.*", "*",
                            "cloudnet.web.cperms.info.user." + userUuid)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    } else {
                        if (!pool.isAvailable()) {
                            return HttpResponseHelper.badRequest(fullHttpResponse, document);
                        }
                        if (!CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().getDatabase()
                                .contains(userUuid)) {
                            return HttpResponseHelper.success(fullHttpResponse, document);
                        }
                        if (userUuid.matches(
                                "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
                            document.append("response", JsonUtils.getGson().toJson(this.webInterface.getCloud()
                                    .getDbHandlers().getPlayerDatabase().getPlayer(UUID.fromString(userUuid))));
                        } else {
                            UUID id = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase()
                                    .get(userUuid);
                            document.append("response", JsonUtils.getGson().toJson(this.webInterface.getCloud()
                                    .getDbHandlers().getPlayerDatabase().getPlayer(id)));
                        }
                        return HttpResponseHelper.success(fullHttpResponse, document);
                    }
                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }
            case "check":
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.cperms.check", "*")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                if (pool.isAvailable()) {
                    return HttpResponseHelper.success(fullHttpResponse, document);
                } else {
                    return HttpResponseHelper.badRequest(fullHttpResponse, document);
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
        HttpResponseHelper.setHeader(fullHttpResponse, "Content-Type", "application/json");
        User user = CloudNet.getInstance()
                .getUser(RequestHelper.headerValue(httpRequest, "-xcloudnet-user"));
        Document document = new Document();
        switch (RequestHelper.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "group":
                String servergroup = RequestHelper.content(httpRequest);
                if (servergroup.isEmpty()) {
                    return HttpResponseHelper.badRequest(fullHttpResponse, new Document());
                }
                PermissionGroup permissionGroup = JsonUtils.getGson()
                        .fromJson(servergroup, PermissionGroup.class);
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.cperms.group.save.*", "*",
                        "cloudnet.web.cperms.group.save." + permissionGroup.getName())) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                this.webInterface.getConfigPermission().updatePermissionGroup(permissionGroup);
                NetworkUtils.addAll(pool.getGroups(), this.webInterface.getConfigPermission().loadAll());

                CloudNet.getInstance().getNetworkManager().getModuleProperties().append("permissionPool",
                        this.pool);
                CloudNet.getInstance().getNetworkManager().updateAll();
                return HttpResponseHelper.success(fullHttpResponse, document);

            case "deletegroup":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")) {
                    final String group = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.cperms.group.delete.*", "*",
                            "cloudnet.web.cperms.group.delete." + group)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    this.pool.getGroups().remove(group);
                    CloudNet.getInstance().getNetworkManager().getModuleProperties().append("permissionPool",
                            this.pool);
                    CloudNet.getInstance().getNetworkManager().updateAll();
                    return HttpResponseHelper.success(fullHttpResponse, document);

                } else {
                    return HttpResponseHelper.valueFieldNotFound(fullHttpResponse);
                }
            case "user":
                final String userString = RequestHelper.content(httpRequest);
                if (userString.isEmpty()) {
                    return HttpResponseHelper.badRequest(fullHttpResponse, new Document());
                }
                OfflinePlayer offlinePlayer = JsonUtils.getGson().fromJson(userString, OfflinePlayer.class);
                if (!HttpUserHelper.hasPermission(user, "cloudnet.web.cperms.user.save.*", "*",
                        "cloudnet.web.cperms.user.save." + offlinePlayer.getName(),
                        "cloudnet.web.cperms.user.save." + offlinePlayer.getUniqueId().toString())) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                }
                CloudNet.getInstance().getDbHandlers().getPlayerDatabase()
                        .updatePermissionEntity(offlinePlayer
                                .getUniqueId(), offlinePlayer.getPermissionEntity());

                CloudNet.getInstance().getNetworkManager()
                        .sendAllUpdate(new PacketOutUpdateOfflinePlayer(CloudNet
                                .getInstance().getDbHandlers().getPlayerDatabase()
                                .getPlayer(offlinePlayer.getUniqueId())));

                CloudPlayer onlinePlayer = CloudNet.getInstance().getNetworkManager()
                        .getOnlinePlayer(offlinePlayer.getUniqueId());
                if (onlinePlayer != null) {
                    onlinePlayer.setPermissionEntity(offlinePlayer.getPermissionEntity());
                    CloudNet.getInstance().getNetworkManager()
                            .sendAllUpdate(new PacketOutUpdatePlayer(onlinePlayer));
                }
                CloudNet.getInstance().getNetworkManager().updateAll();
                return HttpResponseHelper.success(fullHttpResponse, document);

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
