package me.madfix.cloudnet.webinterface.http.v2;

import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import me.madfix.cloudnet.webinterface.http.v2.utils.*;

import java.util.Locale;
import java.util.stream.Collectors;

public final class WrapperApi extends MethodWebHandlerAdapter {

    /**
     * Initiated the class.
     *
     * @param cloudNet the main class of cloudnet
     */
    public WrapperApi(CloudNet cloudNet) {
        super("/cloudnet/api/v2/wrapper");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
                                QueryDecoder queryDecoder,
                                PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = HttpUtility.simpleCheck(httpRequest);
        User user = HttpUtility.getUser(httpRequest);
        switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "wrappers": {
                Document document = new Document();
                document.append("response", CloudNet.getInstance().getWrappers().keySet());
                return HttpResponseUtility.success(fullHttpResponse, document);
            }
            case "warpperinfos": {
                if (!HttpUser.hasPermission(user, "*", "cloudnet.web.wrapper.item.*")) {
                    return HttpResponseUtility.permissionDenied(fullHttpResponse);
                } else {
                    Document resp = new Document();
                    resp.append("response",
                            CloudNet.getInstance().getWrappers().values().stream()
                                    .map(wrapper -> JsonUtil.getGson().toJson(wrapper)).collect(Collectors.toList()));
                    return HttpResponseUtility.success(fullHttpResponse, resp);
                }
            }
            default: {
                return HttpResponseUtility.messageFieldNotFound(fullHttpResponse);
            }
        }
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
                                    QueryDecoder queryDecoder,
                                    PathProvider pathProvider, HttpRequest httpRequest) {
        return HttpResponseUtility.cross(httpRequest);
    }
}
