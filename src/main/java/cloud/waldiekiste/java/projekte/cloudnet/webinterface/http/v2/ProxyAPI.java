package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.JsonUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.RequestUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.ResponseUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.UserUtil;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProxyAPI extends MethodWebHandlerAdapter {
    private final ProjectMain projectMain;

    public ProxyAPI(CloudNet cloudNet, ProjectMain projectMain) {
        super("/cloudnet/api/v2/proxygroup");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.projectMain = projectMain;
    }
    @SuppressWarnings( "deprecation" )
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest){
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
            case "groupitems":{
                List<String> proxys = new ArrayList<>();
                List<String> infos = new ArrayList<>();
                getProjectMain().getCloud().getProxyGroups().keySet().forEach(infos::add);
                for (String prx : infos) {
                    if(!UserUtil.hasPermission(user,"*","cloudnet.web.group.proxy.item.*","cloudnet.web.proxy.group.proxy.item."+prx)){
                        continue;
                    }else{
                        ProxyGroup group = getProjectMain().getCloud().getProxyGroups().get(prx);
                        Document document = new Document();
                        document.append("name",group.getName());
                        document.append("version",group.getProxyVersion().name());
                        document.append("status",group.getProxyConfig().isEnabled());
                        proxys.add(document.convertToJson());
                    }
                }
                Document resp = new Document();
                resp.append("response", proxys);
                return ResponseUtil.success(fullHttpResponse,true,resp);
            }
            case "groups":{

                List<String> infos = new ArrayList<>();
                getProjectMain().getCloud().getProxyGroups().keySet().forEach(t-> infos.add(t));
                for (String proxy : infos) {
                    if(!UserUtil.hasPermission(user,"*","cloudnet.web.group.proxy.*","cloudnet.web."+proxy)){
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }
                }
                Document resp = new Document();
                resp.append("response", infos);
                return ResponseUtil.success(fullHttpResponse,true,resp);
            }
            case "status":{
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue") &&
                        getProjectMain().getCloud().getProxyGroups().containsKey(RequestUtil.getHeaderValue(httpRequest,"-Xvalue"))){
                    final String group = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    if(!UserUtil.hasPermission(user,"cloudnet.web.group.proxy.status.*","*","cloudnet.web.group.proxy.status."+group)){
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }
                    Document data = new Document();
                    data.append("status",getProjectMain().getCloud().getProxyGroup(group).getProxyConfig().isEnabled());
                    Document resp = new Document();
                    resp.append("response",data);
                    return ResponseUtil.success(fullHttpResponse,true,resp);
                }else{
                    return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
                }
            }
            case "version":{
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue") &&
                        getProjectMain().getCloud().getProxyGroups().containsKey(RequestUtil.getHeaderValue(httpRequest,"-Xvalue"))){
                    final String group = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    if(!UserUtil.hasPermission(user,"cloudnet.web.group.proxy.version.*","*","cloudnet.web.group.proxy.version."+group)){
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }
                    Document data = new Document();
                    data.append("version",getProjectMain().getCloud().getProxyGroup(group).getProxyVersion().name());
                    Document resp = new Document();
                    resp.append("response",data);
                    return ResponseUtil.success(fullHttpResponse,true,resp);
                }else{
                    return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
                }
            }
            case "group":{
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue") &&
                        getProjectMain().getCloud().getProxyGroups().containsKey(RequestUtil.getHeaderValue(httpRequest,"-Xvalue"))){
                    final String group = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    if(!UserUtil.hasPermission(user,"cloudnet.web.group.proxy.info.*","*","cloudnet.web.group.proxy.info."+group)){
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }
                    Document data = new Document();
                    data.append(group,JsonUtil.getGson().toJson(getProjectMain().getCloud().getProxyGroup(group)));
                    Document resp = new Document();
                    resp.append("response",data);
                    return ResponseUtil.success(fullHttpResponse,true,resp);
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
            case "stop":{
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue") &&
                        getProjectMain().getCloud().getProxyGroups().containsKey(RequestUtil.getHeaderValue(httpRequest,"-Xvalue"))){
                    final String group = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    if(!UserUtil.hasPermission(user,"cloudnet.web.group.proxy.stop.*","*","cloudnet.web.group.proxy.stop."+group)) {
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }
                    getProjectMain().getCloud().getProxys(group).forEach(t->getProjectMain().getCloud().stopProxy(t.getName()));
                    Document document = new Document();
                    return ResponseUtil.success(fullHttpResponse,true,document);
                }else{
                    return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
                }
            }
            case "delete":{
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue") &&
                        getProjectMain().getCloud().getProxyGroups().containsKey(RequestUtil.getHeaderValue(httpRequest,"-Xvalue"))){
                    final String group = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    if(!UserUtil.hasPermission(user,"cloudnet.web.group.proxy.delete.*","*","cloudnet.web.group.proxy.delete."+group)) {
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }
                    ProxyGroup grp = getProjectMain().getCloud().getProxyGroup(group);
                    CloudNet.getInstance().getProxyGroups().remove(grp.getName());
                    Collection<String> wrps = grp.getWrapper();
                    getProjectMain().getCloud().getConfig().deleteGroup(grp);
                    CloudNet.getInstance().toWrapperInstances(wrps).forEach(Wrapper::updateWrapper);
                    Document document = new Document();
                    return ResponseUtil.success(fullHttpResponse,true,document);
                }else{
                    return ResponseUtil.xValueFieldNotFound(fullHttpResponse);
                }
            }
            case "save":{
                final String proxygroup = RequestUtil.getContent(httpRequest);
                ProxyGroup proxygn = JsonUtil.getGson().fromJson(proxygroup,ProxyGroup.class);
                if(!UserUtil.hasPermission(user,"cloudnet.web.group.proxy.save.*","*","cloudnet.web.group.proxy.save."+proxygn.getName())) {
                    return ResponseUtil.permissionDenied(fullHttpResponse);
                }
                getProjectMain().getCloud().getConfig().createGroup(proxygn);
                CloudNet.getInstance().setupProxy(proxygn);
                if(!CloudNet.getInstance().getProxyGroups().containsKey(proxygn.getName())){
                    CloudNet.getInstance().getProxyGroups().put(proxygn.getName(), proxygn);
                }else{
                    CloudNet.getInstance().getProxyGroups().replace(proxygn.getName(),proxygn);
                }
                CloudNet.getInstance().toWrapperInstances(proxygn.getWrapper()).forEach(Wrapper::updateWrapper);
                Document document = new Document();
                return ResponseUtil.success(fullHttpResponse,true,document);
            }
            case "start":{
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue","-xCount") &&
                        getProjectMain().getCloud().getProxyGroups().containsKey(RequestUtil.getHeaderValue(httpRequest,"-Xvalue"))){
                    final String group = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    final int count = Integer.valueOf(RequestUtil.getHeaderValue(httpRequest,"-Xcount"));
                    if(!UserUtil.hasPermission(user,"cloudnet.web.group.proxy.start.*","*","cloudnet.web.group.proxy.start."+group /*,"cloudnet.web.group.proxy.start."+group+"."+count,"cloudnet.web.group.proxy.start."+count*/)) {
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }
                    for (int i = 0; i < count; i++) {
                        getProjectMain().getCloud().startProxyAsync(getProjectMain().getCloud().getProxyGroup(group));
                    }
                    Document document = new Document();
                    return ResponseUtil.success(fullHttpResponse,true,document);
                }else{
                    return ResponseUtil.xFieldNotFound(fullHttpResponse,"No available -Xvalue,-Xcount command found!");
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
