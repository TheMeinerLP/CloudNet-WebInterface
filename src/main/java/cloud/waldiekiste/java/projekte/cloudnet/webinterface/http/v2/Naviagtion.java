package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Naviagtion extends MethodWebHandlerAdapter {
    private final ProjectMain projectMain;

    public Naviagtion(CloudNet cloudNet, ProjectMain projectMain) {
        super("/cloudnet/api/v2/navigation");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.projectMain = projectMain;
    }

    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        fullHttpResponse.headers().set("Content-Type", "application/json");
        Document dataDocument = new Document("success", false).append("reason", new ArrayList<>()).append("response", new Document());
        if (!(httpRequest.headers().contains("-xcloudnet-user") || httpRequest.headers().contains("-xcloudnet-password") || httpRequest.headers().contains("-xcloudnet-message")))
        {
            dataDocument.append("reason", Arrays.asList("-xcloudnet-user, -xcloudnet-password or -Xmessage not found!"));
            fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
            return fullHttpResponse;
        }
        String username = httpRequest.headers().get("-xcloudnet-user");
        String userpassword = httpRequest.headers().get("-xcloudnet-password");
        if (!CloudNet.getInstance().authorizationPassword(username, userpassword))
        {
            dataDocument.append("reason", Arrays.asList("failed authorization!"));
            fullHttpResponse.setStatus(HttpResponseStatus.UNAUTHORIZED);
            fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
            return fullHttpResponse;
        }
        User user = CloudNet.getInstance().getUser(username);
        switch (httpRequest.headers().get("-Xmessage").toLowerCase()){
            case "drawer":{
                if(!user.getPermissions().contains("cloudnet.web.servergroups") && !user.getPermissions().contains("*"))
                {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }
                dataDocument.append("success", true);
                Document response = new Document();
                response.append("signs",getProjectMain().getConfigSignLayout().loadLayout().getGroupLayouts().size());
                response.append("proxygroups",getProjectMain().getCloud().getProxyGroups().size());
                response.append("servergroups",getProjectMain().getCloud().getServerGroups().size());
                response.append("playerpermissions",getProjectMain().getConfigPermission().loadAll().values().size());
                response.append("cloudpermissions",getProjectMain().getCloud().getUsers().size());
                dataDocument.append("response", response);
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                return fullHttpResponse;
            }default:{
                dataDocument.append("success", true).append("reason", Arrays.asList("No available -Xmessage command found!"));
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                return fullHttpResponse;
            }
        }
    }
    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        fullHttpResponse.headers().set("Content-Type", "application/json");
        fullHttpResponse.headers().set("Access-Control-Allow-Credentials", "true");
        fullHttpResponse.headers().set("Access-Control-Allow-Headers", "content-type, if-none-match, -Xcloudnet-token, -Xmessage, -Xvalue, -Xcloudnet-user, -Xcloudnet-password");
        fullHttpResponse.headers().set("Access-Control-Allow-Methods", "POST,GET,OPTIONS");
        fullHttpResponse.headers().set("Access-Control-Allow-Origin", "*");
        fullHttpResponse.headers().set("Access-Control-Max-Age", "3600");
        return fullHttpResponse;
    }

    public ProjectMain getProjectMain() {
        return projectMain;
    }

}
