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
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutUpdateOfflinePlayer;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutUpdatePlayer;
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
        pool = projectMain.getCloud().getNetworkManager().getModuleProperties().getObject("permissionPool",PermissionPool.TYPE);
    }
    @SuppressWarnings( "deprecation" )
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        pool = projectMain.getCloud().getNetworkManager().getModuleProperties().getObject("permissionPool",PermissionPool.TYPE);

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
            case "groups":{
                if(!UserUtil.hasPermission(user,"cloudnet.web.cperms.info.groups*","*")) {
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }else {
                    Document document = new Document();
                    if (!pool.isAvailable()) {
                        return ResponseUtil.success(fullHttpResponse, false, document);
                    }
                    List<String> groups = new ArrayList<>(pool.getGroups().keySet());
                    document.append("response",groups);
                    return ResponseUtil.success(fullHttpResponse,true,document);
                }
            }
            case "user":{
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue")){
                    final String userUUID = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    if(!UserUtil.hasPermission(user,"cloudnet.web.cperms.info.user.*","*",
                            "cloudnet.web.cperms.info.user."+userUUID)) {
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }else {
                        Document document = new Document();
                        if (!pool.isAvailable()) {
                            return ResponseUtil.success(fullHttpResponse, false, document);
                        }
                        if(!CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().getDatabase().contains(userUUID)){
                            return ResponseUtil.success(fullHttpResponse,false,document);
                        }
                        if (userUUID.matches(
                                "/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i"))
                        {
                            document.append("response",JsonUtil.getGson().toJson(this.projectMain.getCloud().
                                    getDbHandlers().getPlayerDatabase().getPlayer(UUID.fromString(userUUID))));
                        }else{
                            UUID id = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(userUUID);
                            document.append("response",JsonUtil.getGson().toJson(this.projectMain.getCloud().
                                    getDbHandlers().getPlayerDatabase().getPlayer(id)));
                        }
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

    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        ResponseUtil.setHeader(fullHttpResponse,"Content-Type", "application/json");
        if (!RequestUtil.hasHeader(httpRequest, "-xcloudnet-user", "-Xcloudnet-token", "-xcloudnet-message")) return ResponseUtil.xCloudFieldsNotFound(fullHttpResponse);
        if (!RequestUtil.checkAuth(httpRequest)) return UserUtil.failedAuthorization(fullHttpResponse);
        User user = CloudNet.getInstance().getUser(RequestUtil.getHeaderValue(httpRequest,"-xcloudnet-user"));
        switch (RequestUtil.getHeaderValue(httpRequest,"-Xmessage").toLowerCase()){
            case "group":{
                final String servergroup = RequestUtil.getContent(httpRequest);
                PermissionGroup permissionGroup = JsonUtil.getGson().fromJson(servergroup,PermissionGroup.class);
                if(!UserUtil.hasPermission(user,"cloudnet.web.cperms.group.save.*","*","cloudnet.web.cperms.group.save."+permissionGroup.getName())) {
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }
                this.projectMain.getConfigPermission().updatePermissionGroup(permissionGroup);
                NetworkUtils.addAll(pool.getGroups(), this.projectMain.getConfigPermission().loadAll0());

                CloudNet.getInstance().getNetworkManager().getModuleProperties().append("permissionPool", this.pool);
                Document document = new Document();
                return ResponseUtil.success(fullHttpResponse,true,document);
            }
            case "user":{
                final String userString = RequestUtil.getContent(httpRequest);
                OfflinePlayer permissionGroup = JsonUtil.getGson().fromJson(userString, OfflinePlayer.class);
                if(!UserUtil.hasPermission(user,"cloudnet.web.cperms.user.save.*","*","cloudnet.web.cperms.user.save."+permissionGroup.getName(),"cloudnet.web.cperms.user.save."+permissionGroup.getUniqueId().toString())) {
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }
                CloudNet.getInstance().getDbHandlers().getPlayerDatabase().updatePermissionEntity(permissionGroup.getUniqueId(),permissionGroup.getPermissionEntity());

                CloudNet.getInstance().getNetworkManager().sendAllUpdate(new PacketOutUpdateOfflinePlayer(CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getPlayer(permissionGroup.getUniqueId())));

                CloudPlayer onlinePlayer = CloudNet.getInstance().getNetworkManager().getOnlinePlayer(permissionGroup.getUniqueId());
                if(onlinePlayer != null){
                    onlinePlayer.setPermissionEntity(permissionGroup.getPermissionEntity());
                    CloudNet.getInstance().getNetworkManager().sendAllUpdate(new PacketOutUpdatePlayer(onlinePlayer));
                }
                Document document = new Document();
                return ResponseUtil.success(fullHttpResponse,true,document);
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
