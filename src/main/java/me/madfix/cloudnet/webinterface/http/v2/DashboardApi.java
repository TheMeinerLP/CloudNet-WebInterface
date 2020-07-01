package me.madfix.cloudnet.webinterface.http.v2;

import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpUtility;
import me.madfix.cloudnet.webinterface.http.v2.utils.Request;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpResponseUtility;

import java.util.Locale;
import java.util.stream.IntStream;

public final class DashboardApi extends MethodWebHandlerAdapter {

    private final WebInterface webInterface;

    /**
     * Initiated the class.
     *
     * @param cloudNet     the CloudNet class
     * @param webInterface the main class of the project
     */
    public DashboardApi(CloudNet cloudNet, WebInterface webInterface) {
        super("/cloudnet/api/v2/dashboard");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.webInterface = webInterface;
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
                                QueryDecoder queryDecoder,
                                PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = HttpUtility.simpleCheck(httpRequest);
        Document document = new Document();
        switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "players":
                IntStream stream = webInterface.getCloud().getServerGroups().keySet().stream()
                        .mapToInt(server -> webInterface.getCloud().getOnlineCount(server));
                document.append("response", stream.sum());
                return HttpResponseUtility.success(fullHttpResponse, document);
            case "servers":
                document.append("response", webInterface.getCloud().getServers().size());
                return HttpResponseUtility.success(fullHttpResponse, document);

            case "proxys":
                document.append("response", webInterface.getCloud().getProxys().size());
                return HttpResponseUtility.success(fullHttpResponse, document);
            case "groups":
                document.append("response", webInterface.getCloud().getServerGroups().size());
                return HttpResponseUtility.success(fullHttpResponse, document);
            default:
                return HttpResponseUtility.messageFieldNotFound(fullHttpResponse);
        }
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
                                    QueryDecoder queryDecoder,
                                    PathProvider pathProvider, HttpRequest httpRequest) {
        return HttpResponseUtility.cross(httpRequest);
    }
}