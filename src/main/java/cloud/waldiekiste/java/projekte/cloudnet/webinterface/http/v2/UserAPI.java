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
        if (!RequestUtil.hasHeader(httpRequest,"-xcloudnet-user","-xcloudnet-passwort","-xcloudnet-message")) {
            return ResponseUtil.xCloudFieldsNotFound(fullHttpResponse);
        }
        String username = RequestUtil.getHeaderValue(httpRequest,"-xcloudnet-user");
        String userpassword = RequestUtil.getHeaderValue(httpRequest,"-xcloudnet-password");
        if (!CloudNet.getInstance().authorizationPassword(username, userpassword)) {
            return UserUtil.failedAuthorization(fullHttpResponse);
        }
        User user = CloudNet.getInstance().getUser(username);
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
        if (!RequestUtil.hasHeader(httpRequest,"-xcloudnet-user","-xcloudnet-passwort","-xcloudnet-message")) {
            return ResponseUtil.xCloudFieldsNotFound(fullHttpResponse);
        }
        String username = RequestUtil.getHeaderValue(httpRequest,"-xcloudnet-user");
        String userpassword = RequestUtil.getHeaderValue(httpRequest,"-xcloudnet-password");
        if (!CloudNet.getInstance().authorizationPassword(username, userpassword)) {
            return UserUtil.failedAuthorization(fullHttpResponse);
        }
        User user = CloudNet.getInstance().getUser(username);
        switch (RequestUtil.getHeaderValue(httpRequest,"-Xmessage").toLowerCase()){
            case "save":{
                final String jsonuser = RequestUtil.getContent(httpRequest);
                System.out.println(jsonuser);
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
                    getProjectMain().getCloud().getConfig().getUsersPath().toFile().delete();
                    getProjectMain().getCloud().getConfig().save(getProjectMain().getCloud().getUsers());
                    Document resp = new Document();
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
                    User basicUser = new User(basUser.getName(),basUser.getUniqueId(),basUser.getApiToken(),DyHash.hashString(usern.get("password").getAsString()),basUser.getPermissions(),basUser.getMetaData());
                    ArrayList<User> users = new ArrayList<>(getProjectMain().getCloud().getUsers());
                    AtomicReference<User> olduser = new AtomicReference<>();
                    users.forEach(t->{
                        if (t.getName().equals(basicUser.getName())) {
                            olduser.set(t);
                        }
                    });
                    users.remove(olduser.get());
                    users.add(basicUser);
                    getProjectMain().getCloud().getUsers().clear();
                    getProjectMain().getCloud().getUsers().addAll(users);
                    getProjectMain().getCloud().getConfig().getUsersPath().toFile().delete();
                    getProjectMain().getCloud().getConfig().save(getProjectMain().getCloud().getUsers());
                    Document resp = new Document();
                    return ResponseUtil.success(fullHttpResponse, true, resp);
                }
            }
            case "add":{
                if(!UserUtil.hasPermission(user,"*","cloudnet.web.user.add.*")){
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }else {
                    final String jsonuser = RequestUtil.getContent(httpRequest);
                    System.out.println(jsonuser);
                    Document usern = Document.load(jsonuser);
                    BasicUser basicUser = new BasicUser(usern.get("username").getAsString(),usern.get("password").getAsString(),new ArrayList<>());
                    ArrayList<User> users = new ArrayList<>(getProjectMain().getCloud().getUsers());
                    AtomicBoolean exsist = new AtomicBoolean();
                    users.forEach(t->{
                        if (t.getName().equals(usern.get("username").getAsString())) {
                            exsist.set(true);
                            return;
                        }
                    });
                    if(exsist.get()){
                        Document resp = new Document();
                        return ResponseUtil.success(fullHttpResponse, false, resp);
                    }
                    users.add(basicUser);
                    getProjectMain().getCloud().getUsers().clear();
                    getProjectMain().getCloud().getUsers().addAll(users);
                    getProjectMain().getCloud().getConfig().getUsersPath().toFile().delete();
                    getProjectMain().getCloud().getConfig().save(getProjectMain().getCloud().getUsers());
                    Document resp = new Document();
                    return ResponseUtil.success(fullHttpResponse, true, resp);
                }
            }
            case "delete":{
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue") &&
                        getProjectMain().getCloud().getProxyGroups().containsKey(RequestUtil.getHeaderValue(httpRequest,"-Xvalue"))){
                    final String username1 = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    if(!UserUtil.hasPermission(user,"cloudnet.web.user.delete.*","*","cloudnet.web.user.delete."+username1)) {
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }
                    ArrayList<User> users = new ArrayList<>(getProjectMain().getCloud().getUsers());

                    AtomicReference<User> olduser = new AtomicReference<>();
                    users.forEach(t->{
                        if (t.getName().equals(username1)) {
                            olduser.set(t);
                            return;
                        }
                    });
                    users.remove(olduser.get());
                    getProjectMain().getCloud().getUsers().clear();
                    getProjectMain().getCloud().getUsers().addAll(users);
                    getProjectMain().getCloud().getConfig().getUsersPath().toFile().delete();
                    getProjectMain().getCloud().getConfig().save(getProjectMain().getCloud().getUsers());
                    Document document = new Document();
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
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        fullHttpResponse.headers().set("Content-Type", "application/json");
        fullHttpResponse.headers().set("Access-Control-Allow-Credentials", "true");
        fullHttpResponse.headers().set("Access-Control-Allow-Headers", "content-type, if-none-match, -Xcloudnet-token, -Xmessage, -Xvalue, -Xcloudnet-user, -Xcloudnet-password,-Xcount");
        fullHttpResponse.headers().set("Access-Control-Allow-Methods", "POST,GET,OPTIONS");
        fullHttpResponse.headers().set("Access-Control-Allow-Origin", "*");
        fullHttpResponse.headers().set("Access-Control-Max-Age", "3600");
        return fullHttpResponse;
    }

    private ProjectMain getProjectMain() {
        return projectMain;
    }
}
