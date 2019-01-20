package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.SignLayoutAdapter;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignGroupLayouts;
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
import java.util.Collection;
import java.util.Random;

public class SignApi extends MethodWebHandlerAdapter {
    private final ProjectMain projectMain;
    private final Path path;

    public SignApi(ProjectMain projectMain) {
        super("/cloudnet/api/v2/sign");
        CloudNet.getInstance().getWebServer().getWebServerProvider().registerHandler(this);
        this.projectMain = projectMain;
        this.path = Paths.get("local/signLayout.json", new String[0]);
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
            case "groups":{
                final Document document = Document.loadDocument(this.path);
                JsonArray groupLayouts = new JsonArray();
                final SignLayoutConfig signLayoutConfig = document.getObject("layout_config", new TypeToken<SignLayoutConfig>() {}.getType());
                JsonParser parser = new JsonParser();
                JsonObject group;
                for (SignGroupLayouts layout : signLayoutConfig.getGroupLayouts()) {
                    if (UserUtil.hasPermission(user, "*", "cloudnet.web.module.sign.groups.*",
                            "cloudnet.web.module.sign.groups." + layout.getName())) {
                        group = new JsonObject();
                        group.addProperty("name", layout.getName());
                        JsonArray array = new JsonArray();
                        layout.getLayouts().forEach(t -> array.add(parser.parse(JsonUtil.getGson().toJson(t))));
                        group.add("layouts", array.getAsJsonArray());
                        groupLayouts.add(group);
                    } else continue;
                }
                JsonObject object = new JsonObject();
                object.add("groupLayouts",groupLayouts);
                Document resp = new Document();
                resp.append("response", object);
                return ResponseUtil.success(fullHttpResponse,true,resp);
            }
            case "random":{
                Random random = new Random();
                ArrayList<MinecraftServer> arrayList = new ArrayList(CloudNet.getInstance().getServers().values());
                Document resp = new Document();
                resp.append("response", JsonUtil.getGson().toJson(arrayList.get(random.nextInt(arrayList.size()))));
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
