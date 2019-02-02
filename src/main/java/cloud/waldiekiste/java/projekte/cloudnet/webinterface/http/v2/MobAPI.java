package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.*;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
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
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MobAPI extends MethodWebHandlerAdapter {
    private final Path path;
    private final ProjectMain projectMain;

    public MobAPI(ProjectMain projectMain) {
        super("/cloudnet/api/v2/mob");
        CloudNet.getInstance().getWebServer().getWebServerProvider().registerHandler(this);
        this.path = Paths.get("local/servermob_config.json");
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

        if (!UserUtil.hasPermission(user, "*", "cloudnet.web.module.mob.load")) {
            return ResponseUtil.success(fullHttpResponse,false,new Document());
        }
        switch (RequestUtil.getHeaderValue(httpRequest,"-Xmessage").toLowerCase()){

            case "check":{
                Document resp = new Document();
                resp.append("response", !CloudNet.getInstance().getConfig().getDisabledModules().contains("CloudNet-Service-MobModule"));
                return ResponseUtil.success(fullHttpResponse,true,resp);
            }
            case "config":{
                MobConfig config = Document.loadDocument(this.path).getObject("mobConfig", new TypeToken<MobConfig>() {}.getType());
                Document resp = new Document();
                resp.append("response", JsonUtil.getGson().toJson(config));
                return ResponseUtil.success(fullHttpResponse,true,resp);
            }
            case "db":{
                List<String> signs = new ArrayList<>();
                projectMain.getMobDatabase()    .loadAll().values().forEach(sign -> signs.add(JsonUtil.getGson().toJson(sign)));
                Document resp = new Document();
                resp.append("response", signs);
                return ResponseUtil.success(fullHttpResponse,true,resp);

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
                if(content.isEmpty()){
                    return ResponseUtil.success(fullHttpResponse,false,new Document());
                }
                if (!UserUtil.hasPermission(user, "*", "cloudnet.web.module.mob.save")) {
                    return ResponseUtil.success(fullHttpResponse,false,new Document());
                }
                MobConfig signLayoutConfig = JsonUtil.getGson().fromJson(content, MobConfig.class);
                final Document document = Document.loadDocument(this.path);
                document.append("mobConfig", signLayoutConfig);
                document.saveAsConfig(this.path);
                return ResponseUtil.success(fullHttpResponse,true,new Document());
            }
            case "delete":{
                String content = RequestUtil.getContent(httpRequest);
                ServerMob mob = JsonUtil.getGson().fromJson(content,ServerMob.class);
                if (!UserUtil.hasPermission(user, "*", "cloudnet.web.module.mob.delete")) {
                    return ResponseUtil.success(fullHttpResponse,false,new Document());
                }
                projectMain.getMobDatabase().remove(mob);
                return ResponseUtil.success(fullHttpResponse,true,new Document());
            }

            case "add":{
                String content = RequestUtil.getContent(httpRequest);
                ServerMob mob = JsonUtil.getGson().fromJson(content,ServerMob.class);
                if (!UserUtil.hasPermission(user, "*", "cloudnet.web.module.mob.add")) {
                    return ResponseUtil.success(fullHttpResponse,false,new Document());
                }
                projectMain.getMobDatabase().append(mob);
                CloudNet.getInstance().getNetworkManager().updateAll();
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
