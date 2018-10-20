/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.JsonUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.RequestUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.ResponseUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.UserUtil;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class UserAPI extends MethodWebHandlerAdapter {
    private final ProjectMain projectMain;

    public UserAPI(CloudNet cloudNet, ProjectMain projectMain) {
        super("/cloudnet/api/v2/userapi");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.projectMain = projectMain;
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        ResponseUtil.setHeader(fullHttpResponse,"Content-Type", "application/json; charset=utf-8");
        if (!RequestUtil.hasHeader(httpRequest, "-xcloudnet-user", "-Xcloudnet-token", "-Xmessage")) return ResponseUtil.xCloudFieldsNotFound(fullHttpResponse);
        if (!RequestUtil.checkAuth(httpRequest)) return UserUtil.failedAuthorization(fullHttpResponse);
        User user = CloudNet.getInstance().getUser(RequestUtil.getHeaderValue(httpRequest,"-xcloudnet-user"));
        switch (RequestUtil.getHeaderValue(httpRequest,"-Xmessage").toLowerCase()){
            case "users":{
                if(!UserUtil.hasPermission(user,"*","cloudnet.web.user.item.*")){
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }else {
                    List<String> users = new ArrayList<>();
                    getProjectMain().getCloud().getUsers().forEach(t -> users.add(JsonUtil.getGson().toJson(t)));
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
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        ResponseUtil.setHeader(fullHttpResponse,"Content-Type", "application/json");
        if (!RequestUtil.hasHeader(httpRequest, "-xcloudnet-user", "-Xcloudnet-token", "-xcloudnet-message")) return ResponseUtil.xCloudFieldsNotFound(fullHttpResponse);
        if (!RequestUtil.checkAuth(httpRequest)) return UserUtil.failedAuthorization(fullHttpResponse);
        User user = CloudNet.getInstance().getUser(RequestUtil.getHeaderValue(httpRequest,"-xcloudnet-user"));
        switch (RequestUtil.getHeaderValue(httpRequest,"-Xmessage").toLowerCase()){
            case "save":{
                final String jsonuser = RequestUtil.getContent(httpRequest);
                User saveduser = JsonUtil.getGson().fromJson(jsonuser,User.class);
                if(!UserUtil.hasPermission(user,"*","cloudnet.web.user.save.*","cloudnet.web.user.save."+saveduser.getName())){
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }else {
                    ArrayList<User> users = new ArrayList<>(getProjectMain().getCloud().getUsers());
                    AtomicReference<User> olduser = new AtomicReference<>();
                    users.forEach(t->{
                        if (t.getName().equals(saveduser.getName())) {
                            olduser.set(t);
                        }
                    });
                    users.remove(olduser.get());
                    getProjectMain().getCloud().getUsers().clear();
                    users.add(saveduser);
                    getProjectMain().getCloud().getUsers().addAll(users);
                    this.projectMain.getCloud().getConfig().save(getProjectMain().getCloud().getUsers());
                    Document resp = new Document();
                    this.projectMain.getTracking().updateUser();
                    return ResponseUtil.success(fullHttpResponse, true, resp);
                }
            }
            case "resetpassword":{
                final String jsonuser = RequestUtil.getContent(httpRequest);
                Document usern = Document.load(jsonuser);
                User basUser = getProjectMain().getCloud().getUser(usern.get("username").getAsString());
                if(!UserUtil.hasPermission(user,"*","cloudnet.web.user.restepassword.*","cloudnet.web.user.restepassword."+basUser.getName())){
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }else {
                    User basicUser = new User(basUser.getName(),basUser.getUniqueId(),basUser.getApiToken(),DyHash.hashString(new String(Base64.getDecoder().decode(usern.get("password").getAsString()))),basUser.getPermissions(),basUser.getMetaData());
                    ArrayList<User> users = new ArrayList<>(getProjectMain().getCloud().getUsers());
                    AtomicReference<User> olduser = new AtomicReference<>();
                    users.forEach(t->{
                        if (t.getName().equals(basicUser.getName())) {
                            olduser.set(t);
                        }
                    });
                    users.remove(olduser.get());
                    getProjectMain().getCloud().getUsers().clear();
                    users.add(basicUser);
                    getProjectMain().getCloud().getUsers().addAll(users);
                    this.projectMain.getCloud().getConfig().save(getProjectMain().getCloud().getUsers());
                    Document resp = new Document();
                    this.projectMain.getTracking().updateUserPassword();
                    return ResponseUtil.success(fullHttpResponse, true, resp);
                }
            }
            case "add":{
                if(!UserUtil.hasPermission(user,"*","cloudnet.web.user.add.*")){
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }else {
                    final String jsonuser = RequestUtil.getContent(httpRequest);

                    Document usern = Document.load(jsonuser);
                    BasicUser basicUser = new BasicUser(usern.get("username").getAsString(),new String(Base64.getDecoder().decode(usern.get("password").getAsString())),new ArrayList<>());
                    ArrayList<User> users = new ArrayList<>(getProjectMain().getCloud().getUsers());
                    AtomicBoolean exsist = new AtomicBoolean();
                    users.forEach(t->{
                        if (t.getName().equals(usern.get("username").getAsString())) {
                            exsist.set(true);
                        }
                    });
                    if(exsist.get()){
                        Document resp = new Document();
                        return ResponseUtil.success(fullHttpResponse, false, resp);
                    }
                    users.add(basicUser);
                    getProjectMain().getCloud().getUsers().clear();
                    getProjectMain().getCloud().getUsers().addAll(users);
                    this.projectMain.getCloud().getConfig().save(getProjectMain().getCloud().getUsers());
                    Document resp = new Document();
                    this.projectMain.getTracking().addUser();
                    return ResponseUtil.success(fullHttpResponse, true, resp);
                }
            }
            case "delete":{
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue")){
                    final String username1 = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    if(!UserUtil.hasPermission(user,"cloudnet.web.user.delete.*","*","cloudnet.web.user.delete."+username1)) {
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }
                    ArrayList<User> users = new ArrayList<>(getProjectMain().getCloud().getUsers());

                    AtomicReference<User> olduser = new AtomicReference<>();
                    users.forEach(t->{
                        if (t.getName().equals(username1)) {
                            olduser.set(t);
                        }
                    });
                    users.remove(olduser.get());
                    getProjectMain().getCloud().getUsers().clear();
                    getProjectMain().getCloud().getUsers().addAll(users);
                    this.projectMain.getCloud().getConfig().save(getProjectMain().getCloud().getUsers());
                    Document document = new Document();
                    this.projectMain.getTracking().deleteUser();
                    return ResponseUtil.success(fullHttpResponse,true,document);
                }else{
                    return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
                }
            }
            default:{
                return ResponseUtil.xMessageFieldNotFound(fullHttpResponse);
            }
        }
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) {
        return ResponseUtil.cross(httpRequest);
    }

    private ProjectMain getProjectMain() {
        return projectMain;
    }
}
