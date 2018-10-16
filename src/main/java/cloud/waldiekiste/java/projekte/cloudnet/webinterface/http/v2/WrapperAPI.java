/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.RequestUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.ResponseUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.UserUtil;
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

public class WrapperAPI extends MethodWebHandlerAdapter {
    private final ProjectMain projectMain;

    public WrapperAPI(CloudNet cloudNet, ProjectMain projectMain) {
        super("/cloudnet/api/v2/wrapper");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.projectMain = projectMain;
    }
    @SuppressWarnings( "deprecation" )
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder,
                                PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(),
                HttpResponseStatus.OK);
        ResponseUtil.setHeader(fullHttpResponse, "Content-Type", "application/json; charset=utf-8");
        if (!RequestUtil.hasHeader(httpRequest, "-xcloudnet-user", "-Xcloudnet-token", "-xcloudnet-message"))
            return ResponseUtil.xCloudFieldsNotFound(fullHttpResponse);
        if (!RequestUtil.checkAuth(httpRequest)) return UserUtil.failedAuthorization(fullHttpResponse);
        switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase()) {
            case "wrappers":{
                Document document = new Document();
                this.projectMain.getTracking().getWrappers(CloudNet.getInstance().getWrappers().size());
                document.append("response",CloudNet.getInstance().getWrappers().keySet());
                return ResponseUtil.success(fullHttpResponse,true,document);
            }
            default:{
                return ResponseUtil.xMessageFieldNotFound(fullHttpResponse);
            }
        }
    }
    @SuppressWarnings( "deprecation" )
    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder,
                                    PathProvider pathProvider, HttpRequest httpRequest) {
        return ResponseUtil.cross(httpRequest);
    }
}
