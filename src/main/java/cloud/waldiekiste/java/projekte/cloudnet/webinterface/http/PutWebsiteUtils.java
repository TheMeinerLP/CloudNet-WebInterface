package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import com.google.gson.JsonArray;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PutWebsiteUtils extends MethodWebHandlerAdapter {
    public PutWebsiteUtils(CloudNet cloudNet, ProjectMain projectMain) {
        super("/cloudnet/api/v2/put");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
    }

    @Override
    public FullHttpResponse put(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.UNAUTHORIZED);
        fullHttpResponse.headers().set("Content-Type", "application/json");

        Document dataDocument = new Document("success", false).append("reason", new ArrayList<>()).append("response", new Document());
        if (!httpRequest.headers().contains("-Xcloudnet-user") || (!httpRequest.headers().contains("-Xcloudnet-token") && !httpRequest.headers().contains("-Xcloudnet-password")) || !httpRequest.headers().contains("-Xmessage"))
        {
            dataDocument.append("reason", Arrays.asList("-Xcloudnet-user, -Xcloudnet-token or -Xmessage not found!"));
            fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
            return fullHttpResponse;
        }

        if (httpRequest.headers().contains("-Xcloudnet-token") ? !CloudNet.getInstance().authorization(httpRequest.headers().get("-Xcloudnet-user"), httpRequest.headers().get("-Xcloudnet-token")) : !CloudNet.getInstance().authorizationPassword(httpRequest.headers().get("-Xcloudnet-user"), httpRequest.headers().get("-Xcloudnet-password")))
        {
            dataDocument.append("reason", Arrays.asList("failed authorization!"));
            fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
            return fullHttpResponse;
        }

        User user = CloudNet.getInstance().getUser(httpRequest.headers().get("-Xcloudnet-user"));
        switch (httpRequest.headers().get("-Xmessage").toLowerCase())
        {
            case "createuser": {
                if(!user.getPermissions().contains("cloudnet.web.users.createuser") && !user.getPermissions().contains("*"))
                {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }


                dataDocument.append("success", true);
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));

                if(httpRequest.headers().contains("-Xcloudnet-user") && httpRequest.headers().contains("-Xcloudnet-password"))
                {
                    final String username = httpRequest.headers().get("-Xcloudnet-user");
                    final String password = httpRequest.headers().get("-Xcloudnet-password");
                    Document document = new Document();
                    document.loadToExistingDocument(CloudNet.getInstance().getConfig().getUsersPath());
                    document.append("users",Arrays.asList(new BasicUser(username, password, Arrays.asList(""))));
                    document.saveAsConfig(CloudNet.getInstance().getConfig().getUsersPath());
                }

                return fullHttpResponse;
            }
            case "addpermission": {
                if(!user.getPermissions().contains("cloudnet.web.user.addpermission") && !user.getPermissions().contains("*"))
                {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }


                dataDocument.append("success", true);
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));

                if(httpRequest.headers().contains("-Xcloudnet-user") && httpRequest.headers().contains("-Xvalue"))
                {
                    final String username = httpRequest.headers().get("-Xcloudnet-user");
                    final String value = httpRequest.headers().get("-Xvalue");
                    User u = CloudNet.getInstance().getUser(username);
                    Collection<String> perms = u.getPermissions();
                    perms.add(value);
                    Document document = new Document();
                    document.loadToExistingDocument(CloudNet.getInstance().getConfig().getUsersPath());
                    document.remove(username);
                    u = new BasicUser(username,u.getHashedPassword(),perms);
                    document.append("users",u);
                    document.saveAsConfig(CloudNet.getInstance().getConfig().getUsersPath());
                }

            }
            case "removepermission": {
                if(!user.getPermissions().contains("cloudnet.web.user.addpermission") && !user.getPermissions().contains("*"))
                {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }
                dataDocument.append("success", true);
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));

                if(httpRequest.headers().contains("-Xcloudnet-user") && httpRequest.headers().contains("-Xvalue"))
                {
                    final String username = httpRequest.headers().get("-Xcloudnet-user");
                    final String value = httpRequest.headers().get("-Xvalue");
                    User u = CloudNet.getInstance().getUser(username);
                    Collection<String> perms = u.getPermissions();
                    perms.remove(value);
                    Document document = new Document();
                    document.loadToExistingDocument(CloudNet.getInstance().getConfig().getUsersPath());
                    document.remove(username);
                    u = new BasicUser(username,u.getHashedPassword(),perms);
                    document.append("users",u);
                    document.saveAsConfig(CloudNet.getInstance().getConfig().getUsersPath());
                }
            }
            default:
            {
                dataDocument.append("success", true).append("reason", Arrays.asList("No available -Xmessage command found!"));
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                return fullHttpResponse;
            }
        }
    }
}
