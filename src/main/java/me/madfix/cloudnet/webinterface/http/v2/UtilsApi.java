package me.madfix.cloudnet.webinterface.http.v2;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.http.v2.utils.Http;
import me.madfix.cloudnet.webinterface.http.v2.utils.Request;
import me.madfix.cloudnet.webinterface.http.v2.utils.Response;

import java.util.Locale;

public final class UtilsApi extends MethodWebHandlerAdapter {

    private final WebInterface webInterface;

    /**
     * Manage general request they a not categories have.
     *
     * @param cloudNet     The main class from cloudnet
     * @param webInterface The main class of the project
     */
    public UtilsApi(CloudNet cloudNet, WebInterface webInterface) {
        super("/cloudnet/api/v2/utils");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.webInterface = webInterface;
    }

    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
                                QueryDecoder queryDecoder,
                                PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = Http.simpleCheck(httpRequest);
        Document document = new Document();
        switch (Request.headerValue(httpRequest, "-Xmessage").toLowerCase(Locale.ENGLISH)) {
            case "version":
                document.append("response", webInterface.getModuleConfig().getVersion());
                return Response.success(fullHttpResponse, document);

            case "cloudversion":
                document.append("response", NetworkUtils.class.getPackage().getImplementationVersion());
                return Response.success(fullHttpResponse, document);
            case "badges":
                Document infos = new Document();
                infos.append("proxy_groups", CloudNet.getInstance().getProxyGroups().size());
                infos.append("server_groups", CloudNet.getInstance().getServerGroups().size());
                infos.append("proxies", CloudNet.getInstance().getProxys().size());
                infos.append("servers", CloudNet.getInstance().getServers().size());
                infos.append("wrappers", CloudNet.getInstance().getWrappers().values().size());
                document.append("response", infos);
                return Response.success(fullHttpResponse, document);

            case "cloudstats":
                document.append("response",
                        CloudNet.getInstance().getDbHandlers().getStatisticManager().getStatistics());
                return Response.success(fullHttpResponse, document);
            default:
                return Response.messageFieldNotFound(fullHttpResponse);

        }
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
                                    QueryDecoder queryDecoder,
                                    PathProvider pathProvider, HttpRequest httpRequest) {
        return Response.cross(httpRequest);
    }
}