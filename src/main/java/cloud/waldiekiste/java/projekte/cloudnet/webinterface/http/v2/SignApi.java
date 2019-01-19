package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.*;
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

import java.util.ArrayList;
import java.util.List;

public class SignApi extends MethodWebHandlerAdapter {
    private final ProjectMain projectMain;

    public SignApi(ProjectMain projectMain) {
        super("/cloudnet/api/v2/signapi");
        CloudNet.getInstance().getWebServer().getWebServerProvider().registerHandler(this);
        this.projectMain = projectMain;
    }
    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder,
                                PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(),
                HttpResponseStatus.OK);
        fullHttpResponse = HttpUtil.simpleCheck(fullHttpResponse,httpRequest);
        User user = HttpUtil.getUser(httpRequest);
        switch (RequestUtil.getHeaderValue(httpRequest,"-Xmessage").toLowerCase()){
            case "check":{
                Document resp = new Document();
                resp.append("response", CloudNet.getInstance().getConfig().getDisabledModules().contains("CloudNet-Service-SignsModule"));
                return ResponseUtil.success(fullHttpResponse,true,resp);
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

}
