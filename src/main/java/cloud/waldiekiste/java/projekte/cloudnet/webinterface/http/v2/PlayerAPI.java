package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.RequestUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.ResponseUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.UserUtil;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.PlayerCommandExecution;
import de.dytanic.cloudnet.lib.player.PlayerExecutor;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandler;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.player.CorePlayerExecutor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.UUID;

public class PlayerAPI extends MethodWebHandlerAdapter {
    private final ProjectMain projectMain;

    public PlayerAPI(CloudNet cloudNet, ProjectMain projectMain) {
        super("/cloudnet/api/v2/player");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.projectMain = projectMain;
    }

    /**
     *
     */
    @SuppressWarnings( "deprecation" )
    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        ResponseUtil.setHeader(fullHttpResponse, "Content-Type", "application/json; charset=utf-8");
        if (!RequestUtil.hasHeader(httpRequest, "-xcloudnet-user", "-Xcloudnet-token", "-xcloudnet-message")) return ResponseUtil.xCloudFieldsNotFound(fullHttpResponse);
        if (!RequestUtil.checkAuth(httpRequest)) return UserUtil.failedAuthorization(fullHttpResponse);
        switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase()) {

            default:{
                return ResponseUtil.xMessageFieldNotFound(fullHttpResponse);
            }
        }
    }

    /**
     *
     */
    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        ResponseUtil.setHeader(fullHttpResponse, "Content-Type", "application/json; charset=utf-8");
        if (!RequestUtil.hasHeader(httpRequest, "-xcloudnet-user", "-Xcloudnet-token", "-xcloudnet-message")) return ResponseUtil.xCloudFieldsNotFound(fullHttpResponse);
        if (!RequestUtil.checkAuth(httpRequest)) return UserUtil.failedAuthorization(fullHttpResponse);
        User user = CloudNet.getInstance().getUser(RequestUtil.getHeaderValue(httpRequest,"-xcloudnet-user"));
        switch (RequestUtil.getHeaderValue(httpRequest, "-Xmessage").toLowerCase()) {
            case "send": {
                if(RequestUtil.hasHeader(httpRequest,"-Xvalue","-Xcount")){
                    final String player = RequestUtil.getHeaderValue(httpRequest,"-Xvalue");
                    final String Server = RequestUtil.getHeaderValue(httpRequest,"-Xcount");
                    if (!UserUtil.hasPermission(user, "cloudnet.web.player.send", "*",
                            "cloudnet.web.player.*","cloudnet.web.player.send."+player)) {
                        return ResponseUtil.permissionDenied(fullHttpResponse);
                    }
                    if(player.matches("/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i\n")){
                        CloudPlayer cloudPlayer = this.projectMain.getCloud().getNetworkManager().getOnlinePlayer(UUID.fromString(player));
                        CorePlayerExecutor.INSTANCE.sendPlayer(cloudPlayer,Server);
                    }else{
                        if(player.equalsIgnoreCase("*")){
                            this.projectMain.getCloud().getNetworkManager().getOnlinePlayers().values().forEach(t->{
                                CorePlayerExecutor.INSTANCE.sendPlayer(t,Server);
                            });
                            Document document = new Document();
                            return ResponseUtil.success(fullHttpResponse,true,document);
                        }else{
                            CloudPlayer cloudPlayer = this.projectMain.getCloud().getNetworkManager().getPlayer(player);
                            CorePlayerExecutor.INSTANCE.sendPlayer(cloudPlayer,Server);

                            Document document = new Document();
                            return ResponseUtil.success(fullHttpResponse,true,document);
                        }
                    }
                    Document document = new Document();
                    return ResponseUtil.success(fullHttpResponse,true,document);
                }
            }
            default:{
                return ResponseUtil.xMessageFieldNotFound(fullHttpResponse);
            }
        }
    }

    /**
     * Fix the issue for CORS Error
     */
    @SuppressWarnings( "deprecation" )
    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder,
                                    PathProvider pathProvider, HttpRequest httpRequest) {
        return ResponseUtil.cross(httpRequest);
    }
}
