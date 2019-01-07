/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.HttpUtil;
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
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
public class UtilsAPI extends MethodWebHandlerAdapter {
    private final ProjectMain projectMain;

    public UtilsAPI(CloudNet cloudNet, ProjectMain projectMain) {
            super("/cloudnet/api/v2/utils");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.projectMain = projectMain;
    }
    @SuppressWarnings( "deprecation" )
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder,
                                PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(),
                HttpResponseStatus.OK);
        fullHttpResponse = HttpUtil.simpleCheck(fullHttpResponse,httpRequest);
        switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase()) {
            case "version":{
                Document document = new Document();
                document.append("response",getProjectMain().getModuleConfig().getVersion());
                return ResponseUtil.success(fullHttpResponse,true,document);
            }
            case "cloudversion":{
                Document document = new Document();
                document.append("response",NetworkUtils.class.getPackage().getImplementationVersion());
                return ResponseUtil.success(fullHttpResponse,true,document);
            }
            case "badges":{
                Document document = new Document();
                Document infos = new Document();
                infos.append("proxy_groups",CloudNet.getInstance().getProxyGroups().size());
                infos.append("server_groups",CloudNet.getInstance().getServerGroups().size());
                infos.append("proxies",CloudNet.getInstance().getProxys().size());
                infos.append("servers",CloudNet.getInstance().getServers().size());
                infos.append("wrappers",CloudNet.getInstance().getWrappers().size());
                document.append("response",infos);
                return ResponseUtil.success(fullHttpResponse,true,document);
            }
            case "cloudstats":{
                Document document = new Document();
                document.append("response",CloudNet.getInstance().getDbHandlers().getStatisticManager().getStatistics());
                return ResponseUtil.success(fullHttpResponse,true,document);
            }
            default:{
                return ResponseUtil.xMessageFieldNotFound(fullHttpResponse);
            }
        }
    }
    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder,
                                    PathProvider pathProvider, HttpRequest httpRequest) {
        return ResponseUtil.cross(httpRequest);
    }
    private ProjectMain getProjectMain() {
        return projectMain;
    }
}
