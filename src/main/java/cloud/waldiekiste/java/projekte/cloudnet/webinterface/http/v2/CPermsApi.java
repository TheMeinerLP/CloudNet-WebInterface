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
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
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
import java.util.UUID;

public class CPermsApi extends MethodWebHandlerAdapter {
    private final ProjectMain projectMain;
    private PermissionPool pool;

    public CPermsApi(ProjectMain projectMain) {
        super("/cloudnet/api/v2/cperms");
        this.projectMain = projectMain;
        projectMain.getCloud().getWebServer().getWebServerProvider().registerHandler(this);

    }
    @SuppressWarnings( "deprecation" )
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) {
        pool = this.projectMain.getCloud().getNetworkManager().getModuleProperties().getObject("permissionPool",PermissionPool.TYPE);
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        ResponseUtil.setHeader(fullHttpResponse, "Content-Type", "application/json; charset=utf-8");
        if (!RequestUtil.hasHeader(httpRequest, "-xcloudnet-user", "-Xcloudnet-token", "-xcloudnet-message"))
            return ResponseUtil.xCloudFieldsNotFound(fullHttpResponse);
        if (!RequestUtil.checkAuth(httpRequest)) return UserUtil.failedAuthorization(fullHttpResponse);
        User user = CloudNet.getInstance().getUser(RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-user"));
        switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase()) {
            case "group":{
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue")){
                    final String group = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    if(!UserUtil.hasPermission(user,"cloudnet.web.cperms.info.group.*","*","cloudnet.web.cperms.info.group."+group)) {
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }else {
                        Document document = new Document();
                        if (!pool.isAvailable()) {
                            return ResponseUtil.success(fullHttpResponse, false, document);
                        }
                        document.append("response",JsonUtil.getGson().toJson(pool.getGroups().get(group)));
                        return ResponseUtil.success(fullHttpResponse,true,document);
                    }
                }else{
                    if(!UserUtil.hasPermission(user,"cloudnet.web.cperms.groups","*")) {
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }else{
                        Document document = new Document();
                        if(!pool.isAvailable()){
                            return ResponseUtil.success(fullHttpResponse,false,document);
                        }
                        List<String> Groups = new ArrayList<>();
                        pool.getGroups().values().forEach(t->Groups.add(JsonUtil.getGson().toJson(t)));
                        document.append("response",Groups);
                        return ResponseUtil.success(fullHttpResponse,true,document);
                    }
                }
            }
            case "user":{
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue")){
                    final String userUUID = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    if(!UserUtil.hasPermission(user,"cloudnet.web.cperms.info.user.*","*","cloudnet.web.cperms.info.user."+userUUID)) {
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }else {
                        Document document = new Document();
                        if (!pool.isAvailable()) {
                            return ResponseUtil.success(fullHttpResponse, false, document);
                        }
                        document.append("response",JsonUtil.getGson().toJson(this.projectMain.getCloud().getDbHandlers().getPlayerDatabase().getPlayer(UUID.fromString(userUUID))));
                        return ResponseUtil.success(fullHttpResponse,true,document);
                    }
                }else{
                    if(!UserUtil.hasPermission(user,"cloudnet.web.cperms.user","*")) {
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }else{
                        Document document = new Document();
                        if(!pool.isAvailable()){
                            return ResponseUtil.success(fullHttpResponse,false,document);
                        }
                        List<String> offlineUsers = new ArrayList<>();
                        this.projectMain.getCloud().getDbHandlers().getPlayerDatabase().getRegisteredPlayers().values().forEach(t->offlineUsers.add(JsonUtil.getGson().toJson(t)));
                        document.append("response",offlineUsers);
                        return ResponseUtil.success(fullHttpResponse,true,document);
                    }
                }
            }
            case "check":{
                if(!UserUtil.hasPermission(user,"cloudnet.web.cperms.check","*")) {
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }
                if(pool.isAvailable()){
                    Document document = new Document();
                    return ResponseUtil.success(fullHttpResponse,true,document);
                }else{
                    Document document = new Document();
                    return ResponseUtil.success(fullHttpResponse,false,document);
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
}
