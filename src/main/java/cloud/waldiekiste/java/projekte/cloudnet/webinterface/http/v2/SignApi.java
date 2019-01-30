package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.*;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayoutConfig;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class SignApi extends MethodWebHandlerAdapter {
    private final Path path;

    public SignApi() {
        super("/cloudnet/api/v2/sign");
        CloudNet.getInstance().getWebServer().getWebServerProvider().registerHandler(this);
        this.path = Paths.get("local/signLayout.json");
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
                resp.append("response", !CloudNet.getInstance().getConfig().getDisabledModules().contains("CloudNet-Service-SignsModule"));
                return ResponseUtil.success(fullHttpResponse,true,resp);
            }
            case "config":{
                if (!UserUtil.hasPermission(user, "*", "cloudnet.web.module.sign.load")) {
                    return ResponseUtil.success(fullHttpResponse,false,new Document());
                }
                final Document document = Document.loadDocument(this.path);
                final SignLayoutConfig signLayoutConfig = document.getObject("layout_config", new TypeToken<SignLayoutConfig>() {}.getType());
                Document resp = new Document();
                resp.append("response", JsonUtil.getGson().toJson(signLayoutConfig));
                return ResponseUtil.success(fullHttpResponse,true,resp);
            }
            case "random":{
                Random random = new Random();
                ArrayList<MinecraftServer> arrayList = new ArrayList<>(CloudNet.getInstance().getServers().values());
                if (arrayList.size() > 0) {
                    Document resp = new Document();
                    resp.append("response", JsonUtil.getGson().toJson(arrayList.get(random.nextInt(arrayList.size()))));
                    return ResponseUtil.success(fullHttpResponse,true,resp);
                }else{
                    return ResponseUtil.success(fullHttpResponse,false,new Document());
                }
            }
            default:{
                return ResponseUtil.xMessageFieldNotFound(fullHttpResponse);
            }
        }
    }
    @SuppressWarnings("deprecation")
    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder,
                                 PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(),
                HttpResponseStatus.OK);
        fullHttpResponse = HttpUtil.simpleCheck(fullHttpResponse,httpRequest);
        User user = HttpUtil.getUser(httpRequest);
        switch (RequestUtil.getHeaderValue(httpRequest,"-Xmessage").toLowerCase()){
            case "save":{
                String content = RequestUtil.getContent(httpRequest);
                if (!UserUtil.hasPermission(user, "*", "cloudnet.web.module.sign.save")) {
                    return ResponseUtil.success(fullHttpResponse,false,new Document());
                }
                SignLayoutConfig signLayoutConfig = JsonUtil.getGson().fromJson(content, SignLayoutConfig.class);
                final Document document = Document.loadDocument(this.path);
                document.append("layout_config", signLayoutConfig);
                document.saveAsConfig(this.path);
                return ResponseUtil.success(fullHttpResponse,true,new Document());
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
