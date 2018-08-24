package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.RequestUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.ResponseUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.UserUtil;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.Base64;

public class MasterAPI extends MethodWebHandlerAdapter {
    private final ProjectMain projectMain;

    public MasterAPI(CloudNet cloudNet, ProjectMain projectMain) {
        super("/cloudnet/api/v2/master");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.projectMain = projectMain;
    }
    @SuppressWarnings( "deprecation" )
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        ResponseUtil.setHeader(fullHttpResponse, "Content-Type", "application/json; charset=utf-8");
        if (!RequestUtil.hasHeader(httpRequest, "-xcloudnet-user", "-xcloudnet-passwort", "-xcloudnet-message")) {
            return ResponseUtil.xCloudFieldsNotFound(fullHttpResponse);
        }
        String username = RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-user");
        String userpassword = new String(Base64.getDecoder().decode(RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-password")));
        if (!CloudNet.getInstance().authorizationPassword(username, userpassword)) {
            return UserUtil.failedAuthorization(fullHttpResponse);
        }
        //User user = CloudNet.getInstance().getUser(username);
        switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase()) {
            case "corelog":{
                Document document = new Document();
                document.append("response",getProjectMain().getConsoleLines());
                return ResponseUtil.success(fullHttpResponse,true,document);
            }
            case "commands":{
                Document document = new Document();
                document.append("response",getProjectMain().getCloud().getCommandManager().getCommands());
                return ResponseUtil.success(fullHttpResponse,true,document);
            }
            default:{
                return ResponseUtil.xMessageFieldNotFound(fullHttpResponse);
            }
        }
    }
    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        ResponseUtil.setHeader(fullHttpResponse, "Content-Type", "application/json; charset=utf-8");
        if (!RequestUtil.hasHeader(httpRequest, "-xcloudnet-user", "-xcloudnet-passwort", "-xcloudnet-message")) {
            return ResponseUtil.xCloudFieldsNotFound(fullHttpResponse);
        }
        String username = RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-user");
        String userpassword = new String(Base64.getDecoder().decode(RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-password").getBytes()));
        if (!CloudNet.getInstance().authorizationPassword(username, userpassword)) {
            return UserUtil.failedAuthorization(fullHttpResponse);
        }
        User user = CloudNet.getInstance().getUser(username);
        switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase()) {
            case "reloadall":{
                if(!UserUtil.hasPermission(user,"cloudnet.web.master.reload.all","*","cloudnet.web.master.reload.*")) {
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }
                CloudNet.getInstance().reload();
                Document document = new Document();
                return ResponseUtil.success(fullHttpResponse,true,document);
            }
            case "reloadconfig":{
                if(!UserUtil.hasPermission(user,"cloudnet.web.master.reload.config","*","cloudnet.web.master.reload.*")) {
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }
                try {
                    CloudNet.getInstance().getConfig().load();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CloudNet.getInstance().getServerGroups().clear();
                CloudNet.getInstance().getProxyGroups().clear();
                CloudNet.getInstance().getUsers().clear();
                CloudNet.getInstance().getUsers().addAll(CloudNet.getInstance().getConfig().getUsers());

                NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(), CloudNet.getInstance().getConfig().getServerGroups(), value -> {
                    System.out.println("Loading ServerGroup: " + value.getName());
                    CloudNet.getInstance().setupGroup(value);
                    return true;
                });

                NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(), CloudNet.getInstance().getConfig().getProxyGroups(), value -> {
                    System.out.println("Loading ProxyGroup: " + value.getName());
                    CloudNet.getInstance().setupProxy(value);
                    return true;
                });

                CloudNet.getInstance().getNetworkManager().reload();
                CloudNet.getInstance().getNetworkManager().updateAll();
                CloudNet.getInstance().getWrappers().values().forEach(Wrapper::updateWrapper);
                Document document = new Document();
                return ResponseUtil.success(fullHttpResponse,true,document);
            }
            case "reloadwrapper":{
                if(!UserUtil.hasPermission(user,"cloudnet.web.master.reload.wrapper","*","cloudnet.web.master.reload.*")) {
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }
                for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                    if (wrapper.getChannel() != null) wrapper.writeCommand("reload");
                }
                Document document = new Document();
                return ResponseUtil.success(fullHttpResponse,true,document);
            }
            case "clearcache":{
                if(!UserUtil.hasPermission(user,"cloudnet.web.master.clearcache","*")) {
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }
                CloudNet.getInstance().getWrappers().values().forEach(wrapper -> {
                    if (wrapper.getChannel() != null)
                    {
                        wrapper.sendCommand("clearcache");
                    }
                });
                Document document = new Document();
                return ResponseUtil.success(fullHttpResponse,true,document);
            }

            case "stop":{
                if(!UserUtil.hasPermission(user,"cloudnet.web.master.stop","*")) {
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }
                CloudNet.getInstance().shutdown();
                Document document = new Document();
                return ResponseUtil.success(fullHttpResponse,true,document);
            }
            case "command":{
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue")){
                    final String command = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    if(!UserUtil.hasPermission(user,"cloudnet.web.master.command.*","*","cloudnet.web.master.command."+command)) {
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }
                    getProjectMain().getCloud().getCommandManager().dispatchCommand(command);
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
        return ResponseUtil.cross(httpRequest);
    }

    private ProjectMain getProjectMain() {
        return projectMain;
    }
}
