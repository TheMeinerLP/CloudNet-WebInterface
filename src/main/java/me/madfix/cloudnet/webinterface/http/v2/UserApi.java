package me.madfix.cloudnet.webinterface.http.v2;

import de.dytanic.cloudnet.lib.hash.DyHash;
import de.dytanic.cloudnet.lib.user.BasicUser;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public final class UserApi extends MethodWebHandlerAdapter {

    private final WebInterface webInterface;

    /**
     * Initiated the class.
     *
     * @param cloudNet     The main class of cloudnet
     * @param webInterface The main class of the project
     */
    public UserApi(CloudNet cloudNet, WebInterface webInterface) {
        super("/cloudnet/api/v2/userapi");
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
        if (RequestHelper.headerValue(httpRequest, "-Xmessage").equalsIgnoreCase("users")) {
            if (!HttpUserHelper.hasPermission(user, "*", "cloudnet.web.user.item.*")) {
                return HttpResponseHelper.permissionDenied(fullHttpResponse);
            } else {
                resp.append("response", CloudNet.getInstance().getUsers().stream()
                        .map(user1 -> JsonUtils.getGson().toJson(user1)).collect(Collectors.toList()));
                return HttpResponseHelper.success(fullHttpResponse, resp);
            }
        } else {
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
        String jsonuser = RequestHelper.content(httpRequest);
        switch (RequestHelper.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "save":
                if (jsonuser.length() < 1) {
                    return HttpResponseHelper.badRequest(fullHttpResponse, new Document());
                }
                User saveduser = JsonUtils.getGson().fromJson(jsonuser, User.class);
                if (!HttpUserHelper.hasPermission(user, "*", "cloudnet.web.user.save.*",
                        "cloudnet.web.user.save." + saveduser.getName())) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                } else {
                    Optional<User> oldUser = CloudNet.getInstance().getUsers().stream()
                            .filter(u -> u.getName().equals(saveduser.getName())).findAny();
                    if (oldUser.isPresent()) {
                        CloudNet.getInstance().getUsers().remove(oldUser.get());
                        CloudNet.getInstance().getUsers().add(saveduser);
                        CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
                        return HttpResponseHelper.success(fullHttpResponse, new Document());
                    } else {
                        return HttpResponseHelper.badRequest(fullHttpResponse, new Document());
                    }
                }
            case "reset":
                Document usern = Document.load(jsonuser);
                User editUser = CloudNet.getInstance().getUser(usern.get("username").getAsString());
                if (!HttpUserHelper.hasPermission(user, "*", "cloudnet.web.user.restepassword.*",
                        "cloudnet.web.user.restepassword." + editUser.getName())) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                } else {
                    User newUser = new User(editUser.getName(), editUser.getUniqueId(),
                            editUser.getApiToken(),
                            DyHash.hashString(new String(Base64.getDecoder().decode(usern.get("password")
                                    .getAsString()), StandardCharsets.UTF_8)), editUser.getPermissions(),
                            editUser.getMetaData());
                    Optional<User> oldUser = CloudNet.getInstance().getUsers().stream()
                            .filter(u -> u.getName().equals(newUser.getName())).findAny();

                    if (oldUser.isPresent()) {
                        CloudNet.getInstance().getUsers().remove(oldUser.get());
                        CloudNet.getInstance().getUsers().add(newUser);
                        this.webInterface.getCloud().getConfig().save(webInterface.getCloud().getUsers());
                        return HttpResponseHelper.success(fullHttpResponse, new Document());
                    } else {
                        return HttpResponseHelper.badRequest(fullHttpResponse, new Document());
                    }
                }
            case "add":
                if (!HttpUserHelper.hasPermission(user, "*", "cloudnet.web.user.add.*")) {
                    return HttpResponseHelper.permissionDenied(fullHttpResponse);
                } else {
                    jsonuser = RequestHelper.content(httpRequest);
                    if (jsonuser.isEmpty()) {
                        return HttpResponseHelper.badRequest(fullHttpResponse, new Document());
                    }
                    usern = Document.load(jsonuser);
                    BasicUser basicUser = new BasicUser(usern.get("username").getAsString(),
                            new String(Base64
                                    .getDecoder().decode(usern.get("password").getAsString()),
                                    StandardCharsets.UTF_8), new ArrayList<>());
                    if (CloudNet.getInstance().getUsers().stream()
                            .noneMatch(u -> u.getName().equals(basicUser.getName()))) {
                        CloudNet.getInstance().getUsers().add(basicUser);
                        CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
                        return HttpResponseHelper.success(fullHttpResponse, new Document());
                    } else {
                        return HttpResponseHelper.badRequest(fullHttpResponse, new Document());
                    }
                }
            case "delete":
                if (RequestHelper.hasHeader(httpRequest, "-Xvalue")) {
                    final String username1 = RequestHelper.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUserHelper.hasPermission(user, "cloudnet.web.user.delete.*", "*",
                            "cloudnet.web.user.delete." + username1)) {
                        return HttpResponseHelper.permissionDenied(fullHttpResponse);
                    }
                    Optional<User> oldUser = CloudNet.getInstance().getUsers().stream()
                            .filter(u -> u.getName().equals(username1)).findAny();
                    if (oldUser.isPresent()) {
                        CloudNet.getInstance().getUsers().remove(oldUser.get());
                        this.webInterface.getCloud().getConfig().save(webInterface.getCloud().getUsers());
                        return HttpResponseHelper.success(fullHttpResponse, new Document());
                    } else {
                        return HttpResponseHelper.badRequest(fullHttpResponse, new Document());
                    }
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