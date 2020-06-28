package me.madfix.cloudnet.webinterface.http.v2;

import me.madfix.cloudnet.webinterface.http.v2.utils.Http;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpUser;
import me.madfix.cloudnet.webinterface.http.v2.utils.JsonUtil;
import me.madfix.cloudnet.webinterface.http.v2.utils.Request;
import me.madfix.cloudnet.webinterface.http.v2.utils.Response;
import me.madfix.cloudnet.webinterface.ProjectMain;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public final class UserApi extends MethodWebHandlerAdapter {

    private final ProjectMain projectMain;

    /**
     * Initiated the class.
     *
     * @param cloudNet    The main class of cloudnet
     * @param projectMain The main class of the project
     */
    public UserApi(CloudNet cloudNet, ProjectMain projectMain) {
        super("/cloudnet/api/v2/userapi");
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
        if (Request.headerValue(httpRequest, "-Xmessage").equalsIgnoreCase("users")) {
            if (!HttpUser.hasPermission(user, "*", "cloudnet.web.user.item.*")) {
                return Response.permissionDenied(fullHttpResponse);
            } else {
                resp.append("response", CloudNet.getInstance().getUsers().stream()
                        .map(user1 -> JsonUtil.getGson().toJson(user1)).collect(Collectors.toList()));
                return Response.success(fullHttpResponse, resp);
            }
        } else {
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
        String jsonuser = Request.content(httpRequest);
        switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "save":
                if (jsonuser.length() < 1) {
                    return Response.badRequest(fullHttpResponse, new Document());
                }
                User saveduser = JsonUtil.getGson().fromJson(jsonuser, User.class);
                if (!HttpUser.hasPermission(user, "*", "cloudnet.web.user.save.*",
                        "cloudnet.web.user.save." + saveduser.getName())) {
                    return Response.permissionDenied(fullHttpResponse);
                } else {
                    Optional<User> oldUser = CloudNet.getInstance().getUsers().stream()
                            .filter(u -> u.getName().equals(saveduser.getName())).findAny();
                    if (oldUser.isPresent()) {
                        CloudNet.getInstance().getUsers().remove(oldUser.get());
                        CloudNet.getInstance().getUsers().add(saveduser);
                        CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
                        return Response.success(fullHttpResponse, new Document());
                    } else {
                        return Response.badRequest(fullHttpResponse, new Document());
                    }
                }
            case "reset":
                Document usern = Document.load(jsonuser);
                User editUser = CloudNet.getInstance().getUser(usern.get("username").getAsString());
                if (!HttpUser.hasPermission(user, "*", "cloudnet.web.user.restepassword.*",
                        "cloudnet.web.user.restepassword." + editUser.getName())) {
                    return Response.permissionDenied(fullHttpResponse);
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
                        this.projectMain.getCloud().getConfig().save(projectMain.getCloud().getUsers());
                        return Response.success(fullHttpResponse, new Document());
                    } else {
                        return Response.badRequest(fullHttpResponse, new Document());
                    }
                }
            case "add":
                if (!HttpUser.hasPermission(user, "*", "cloudnet.web.user.add.*")) {
                    return Response.permissionDenied(fullHttpResponse);
                } else {
                    jsonuser = Request.content(httpRequest);
                    if (jsonuser.isEmpty()) {
                        return Response.badRequest(fullHttpResponse, new Document());
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
                        return Response.success(fullHttpResponse, new Document());
                    } else {
                        return Response.badRequest(fullHttpResponse, new Document());
                    }
                }
            case "delete":
                if (Request.hasHeader(httpRequest, "-Xvalue")) {
                    final String username1 = Request.headerValue(httpRequest, "-Xvalue");
                    if (!HttpUser.hasPermission(user, "cloudnet.web.user.delete.*", "*",
                            "cloudnet.web.user.delete." + username1)) {
                        return Response.permissionDenied(fullHttpResponse);
                    }
                    Optional<User> oldUser = CloudNet.getInstance().getUsers().stream()
                            .filter(u -> u.getName().equals(username1)).findAny();
                    if (oldUser.isPresent()) {
                        CloudNet.getInstance().getUsers().remove(oldUser.get());
                        this.projectMain.getCloud().getConfig().save(projectMain.getCloud().getUsers());
                        return Response.success(fullHttpResponse, new Document());
                    } else {
                        return Response.badRequest(fullHttpResponse, new Document());
                    }
                } else {
                    return Response.valueFieldNotFound(fullHttpResponse);
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