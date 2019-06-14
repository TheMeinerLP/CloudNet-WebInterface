/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.*;
import de.dytanic.cloudnet.lib.hash.DyHash;
import de.dytanic.cloudnet.lib.user.BasicUser;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ALL")
public final class UserAPI extends MethodWebHandlerAdapter {

    private final ProjectMain projectMain;

    public UserAPI(CloudNet cloudNet, ProjectMain projectMain) {
        super("/cloudnet/api/v2/userapi");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.projectMain = projectMain;
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder,
                                PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(),
                HttpResponseStatus.OK);
        fullHttpResponse = HttpUtil.simpleCheck(fullHttpResponse,httpRequest);
        User user = HttpUtil.getUser(httpRequest);
        switch (RequestUtil.getHeaderValue(httpRequest,"-Xmessage").toLowerCase()){
            case "users":{
                if(!UserUtil.hasPermission(user,"*","cloudnet.web.user.item.*")){
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }else {
                    List<String> users = new ArrayList<>();
                    CloudNet.getInstance().getConfig().getUsers().forEach(t -> users.add(JsonUtil.getGson().toJson(t)));
                    Document resp = new Document();
                    resp.append("response", users);
                    return ResponseUtil.success(fullHttpResponse, true, resp);
                }
            }
            default:{
                return ResponseUtil.xMessageFieldNotFound(fullHttpResponse);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder,
                                 PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(),
                HttpResponseStatus.OK);
        fullHttpResponse = HttpUtil.simpleCheck(fullHttpResponse,httpRequest);
        User user = HttpUtil.getUser(httpRequest);
        switch (RequestUtil.getHeaderValue(httpRequest,"-Xmessage").toLowerCase()){
            case "save":{
                final String jsonuser = RequestUtil.getContent(httpRequest);
                if(jsonuser.isEmpty() || jsonuser == null){
                    return ResponseUtil.success(fullHttpResponse, false, new Document());
                }
                User saveduser = JsonUtil.getGson().fromJson(jsonuser,User.class);
                if(!UserUtil.hasPermission(user,"*","cloudnet.web.user.save.*",
                        "cloudnet.web.user.save." + saveduser.getName())) {
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }else {
                    ArrayList<User> users = new ArrayList<>(projectMain.getCloud().getUsers());
                    Optional<User> oldUser = users.stream().filter(u -> u.getName().equals(saveduser.getName())).findAny();
                    if (oldUser.isPresent()) {
                        users.remove(oldUser.get());
                        projectMain.getCloud().getUsers().clear();
                        users.add(saveduser);
                        projectMain.getCloud().getUsers().addAll(users);
                        this.projectMain.getCloud().getConfig().save(projectMain.getCloud().getUsers());
                        return ResponseUtil.success(fullHttpResponse, true, new Document());
                    } else {
                        return ResponseUtil.success(fullHttpResponse, false, new Document());
                    }
                }
            }
            case "reseth":{
                final String jsonuser = RequestUtil.getContent(httpRequest);
                Document usern = Document.load(jsonuser);
                User editUser = projectMain.getCloud().getUser(usern.get("username").getAsString());
                if(!UserUtil.hasPermission(user,"*","cloudnet.web.user.restepassword.*",
                        "cloudnet.web.user.restepassword."+editUser.getName())){
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                } else {
                    User newUser = new User(editUser.getName(),editUser.getUniqueId(),editUser.getApiToken(),
                            DyHash.hashString(new String(Base64.getDecoder().decode(usern.get("password").
                                    getAsString()))),editUser.getPermissions(),editUser.getMetaData());
                    ArrayList<User> users = new ArrayList<>(projectMain.getCloud().getUsers());
                    Optional<User> oldUser = users.stream().filter(u -> u.getName().equals(newUser.getName())).findAny();

                    if (oldUser.isPresent()) {
                        users.remove(oldUser.get());
                        projectMain.getCloud().getUsers().clear();
                        users.add(newUser);
                        projectMain.getCloud().getUsers().addAll(users);
                        this.projectMain.getCloud().getConfig().save(projectMain.getCloud().getUsers());
                        return ResponseUtil.success(fullHttpResponse, true, new Document());
                    } else {
                        return ResponseUtil.success(fullHttpResponse, false, new Document());
                    }
                }
            }
            case "add":{
                if(!UserUtil.hasPermission(user,"*","cloudnet.web.user.add.*")){
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }else {
                    final String jsonuser = RequestUtil.getContent(httpRequest);
                    if(jsonuser.isEmpty()){
                        return ResponseUtil.success(fullHttpResponse,false,new Document());
                    }
                    Document usern = Document.load(jsonuser);
                    BasicUser basicUser = new BasicUser(usern.get("username").getAsString(),new String(Base64.
                            getDecoder().decode(usern.get("password").getAsString())),new ArrayList<>());
                    ArrayList<User> users = new ArrayList<>(projectMain.getCloud().getUsers());
                    if (users.stream().noneMatch(u -> u.getName().equals(basicUser.getName()))) {
                        users.add(basicUser);
                        projectMain.getCloud().getUsers().clear();
                        projectMain.getCloud().getUsers().addAll(users);
                        this.projectMain.getCloud().getConfig().save(projectMain.getCloud().getUsers());
                        return ResponseUtil.success(fullHttpResponse, true, new Document());
                    } else {
                        return ResponseUtil.success(fullHttpResponse, false, new Document());
                    }
                }
            }
            case "delete":{
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue")){
                    final String username1 = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    if(!UserUtil.hasPermission(user,"cloudnet.web.user.delete.*","*",
                            "cloudnet.web.user.delete."+username1)) {
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }
                    ArrayList<User> users = new ArrayList<>(projectMain.getCloud().getUsers());

                    Optional<User> oldUser = users.stream().filter(u -> u.getName().equals(username1)).findAny();

                    if (oldUser.isPresent()) {
                        users.remove(oldUser.get());
                        projectMain.getCloud().getUsers().clear();
                        projectMain.getCloud().getUsers().addAll(users);
                        this.projectMain.getCloud().getConfig().save(projectMain.getCloud().getUsers());
                        return ResponseUtil.success(fullHttpResponse, true, new Document());
                    } else {
                        return ResponseUtil.success(fullHttpResponse, false, new Document());
                    }
                } else {
                    return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
                }
            }
            default:{
                return ResponseUtil.xMessageFieldNotFound(fullHttpResponse);
            }
        }
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder,
                                    PathProvider pathProvider, HttpRequest httpRequest) {
        return ResponseUtil.cross(httpRequest);
    }
}