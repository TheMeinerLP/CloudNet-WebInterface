package me.madfix.cloudnet.webinterface.http.v2;

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
import me.madfix.cloudnet.webinterface.http.v2.utils.Http;
import me.madfix.cloudnet.webinterface.http.v2.utils.Request;
import me.madfix.cloudnet.webinterface.http.v2.utils.Response;

import java.util.ArrayList;

public final class AuthenticationApi extends MethodWebHandlerAdapter {

    /**
     * Initiated the class.
     */
    public AuthenticationApi() {
        super("/cloudnet/api/v2/auth");
        CloudNet.getInstance().getWebServer().getWebServerProvider().registerHandler(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
                                 QueryDecoder queryDecoder,
                                 PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
                httpRequest.getProtocolVersion(),
                HttpResponseStatus.OK);
        Response.setHeader(fullHttpResponse, "Content-Type", "application/json");
        if (!Request.hasHeader(httpRequest, "-xcloudnet-user", "-xcloudnet-password")) {
            return Response.cloudFieldNotFound(fullHttpResponse);
        }
        String username = Request.headerValue(httpRequest, "-xcloudnet-user");
        String userpassword = Request.headerValue(httpRequest, "-xcloudnet-password");
        if (!CloudNet.getInstance().authorizationPassword(username, userpassword)) {
            return Http.failedAuthorization(fullHttpResponse);
        }
        User user = CloudNet.getInstance().getUser(username);
        Document userinfos = new Document();
        userinfos.append("UUID", user.getUniqueId().toString());
        userinfos.append("token", user.getApiToken());
        userinfos.append("name", user.getName());
        userinfos.append("password", user.getHashedPassword());
        userinfos.append("permissions", new ArrayList<>(user.getPermissions()));
        Document document = new Document();
        document.append("response", userinfos);
        return Response.success(fullHttpResponse, document);
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
                                    QueryDecoder queryDecoder,
                                    PathProvider pathProvider, HttpRequest httpRequest) {
        return Response.cross(httpRequest);
    }
}
