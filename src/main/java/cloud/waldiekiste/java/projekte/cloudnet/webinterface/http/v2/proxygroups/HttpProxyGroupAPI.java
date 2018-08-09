package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.proxygroups;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.dytanic.cloudnet.lib.proxylayout.*;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.utility.threading.ScheduledTask;
import de.dytanic.cloudnet.lib.utility.threading.Scheduler;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudConfig;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpProxyGroupAPI extends MethodWebHandlerAdapter {
    private final ProjectMain projectMain;

    public HttpProxyGroupAPI(CloudNet cloudNet, ProjectMain projectMain) {
        super("/cloudnet/api/v2/proxygroup");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.projectMain = projectMain;
    }

    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        fullHttpResponse.headers().set("Content-Type", "application/json");
        Document dataDocument = new Document("success", false).append("reason", new ArrayList<>()).append("response", new Document());
        if (!(httpRequest.headers().contains("-xcloudnet-user") || httpRequest.headers().contains("-xcloudnet-password") || httpRequest.headers().contains("-xcloudnet-message")))
        {
            dataDocument.append("reason", Arrays.asList("-xcloudnet-user, -xcloudnet-password or -Xmessage not found!"));
            fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
            return fullHttpResponse;
        }
        String username = httpRequest.headers().get("-xcloudnet-user");
        String userpassword = httpRequest.headers().get("-xcloudnet-password");
        if (!CloudNet.getInstance().authorizationPassword(username, userpassword))
        {
            dataDocument.append("reason", Arrays.asList("failed authorization!"));
            fullHttpResponse.setStatus(HttpResponseStatus.UNAUTHORIZED);
            fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
            return fullHttpResponse;
        }
        User user = CloudNet.getInstance().getUser(username);
        switch (httpRequest.headers().get("-Xmessage").toLowerCase()){
            case "groups":{
                if(!user.getPermissions().contains("cloudnet.web.group.proxy") && !user.getPermissions().contains("*"))
                {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }
                dataDocument.append("success", true);
                List<String> infos = new ArrayList<>();
                getProjectMain().getCloud().getProxyGroups().keySet().forEach(t->{
                    Document info = new Document();
                    info.append("version",getProjectMain().getCloud().getProxyGroups().get(t).getProxyVersion().name());
                    info.append("name",t);
                    infos.add(info.convertToJson());
                });
                dataDocument.append("response", infos);
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                return fullHttpResponse;
            }
            case "group":{
                if(!user.getPermissions().contains("cloudnet.web.group.proxy.info") && !user.getPermissions().contains("*"))
                {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }
                if(httpRequest.headers().contains("-Xvalue") && getProjectMain().getCloud().getProxyGroups().containsKey(httpRequest.headers().get("-Xvalue")))
                {
                    dataDocument.append("success", true);
                    Document response = new Document();
                    final String group = httpRequest.headers().get("-Xvalue");
                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeAdapter(AutoSlot.class,new AutoSlotJsonAdapter());
                    builder.registerTypeAdapter(DynamicFallback.class,new DynamicFallbackJsonAdapter());
                    builder.registerTypeAdapter(Motd.class,new MotdJsonAdapter());
                    builder.registerTypeAdapter(ProxyConfig.class,new ProxyConfigJsonAdapter());
                    builder.registerTypeAdapter(ServerFallback.class,new ServerFallbackJsonAdapter());
                    builder.registerTypeAdapter(ServerInstallablePlugin.class,new ServerInstallablePluginJsonAdapter());
                    builder.registerTypeAdapter(TabList.class,new TabListJsonAdapter());
                    builder.registerTypeAdapter(Template.class,new TemplateJsonAdapter());
                    builder.registerTypeAdapter(ProxyGroup.class,new ProxyGroupJsonAdapter());
                    Gson gson = builder.create();

                    response.append(group,gson.toJson(getProjectMain().getCloud().getProxyGroup(group)));
                    dataDocument.append("response", response);
                    fullHttpResponse.setStatus(HttpResponseStatus.OK);
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));

                    return fullHttpResponse;
                }else{
                    dataDocument.append("success", false).append("reason", Arrays.asList("No available -Xvlaue command found!"));
                    fullHttpResponse.setStatus(HttpResponseStatus.BAD_REQUEST);
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    return fullHttpResponse;
                }

            }
            default:{
                dataDocument.append("success", false).append("reason", Arrays.asList("No available -Xmessage command found!"));
                fullHttpResponse.setStatus(HttpResponseStatus.BAD_REQUEST);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                return fullHttpResponse;
            }
        }
    }

    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        fullHttpResponse.headers().set("Content-Type", "application/json");
        Document dataDocument = new Document("success", false).append("reason", new ArrayList<>()).append("response", new Document());
        if (!(httpRequest.headers().contains("-xcloudnet-user") || httpRequest.headers().contains("-xcloudnet-password") || httpRequest.headers().contains("-xcloudnet-message")))
        {
            dataDocument.append("reason", Arrays.asList("-xcloudnet-user, -xcloudnet-password or -Xmessage not found!"));
            fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
            return fullHttpResponse;
        }
        String username = httpRequest.headers().get("-xcloudnet-user");
        String userpassword = httpRequest.headers().get("-xcloudnet-password");
        if (!CloudNet.getInstance().authorizationPassword(username, userpassword))
        {
            dataDocument.append("reason", Arrays.asList("failed authorization!"));
            fullHttpResponse.setStatus(HttpResponseStatus.UNAUTHORIZED);
            fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
            return fullHttpResponse;
        }
        User user = CloudNet.getInstance().getUser(username);
        switch (httpRequest.headers().get("-Xmessage").toLowerCase()){
            case "stop":{

                if(httpRequest.headers().contains("-Xvalue") && getProjectMain().getCloud().getProxyGroups().containsKey(httpRequest.headers().get("-Xvalue")))
                {
                    final String group = httpRequest.headers().get("-Xvalue");
                    if(!user.getPermissions().contains("cloudnet.web.group.proxy.stop.*") && !user.getPermissions().contains("*") && !user.getPermissions().contains("cloudnet.web.group.proxy.stop."+group))
                    {
                        dataDocument.append("reason", Arrays.asList("permission denied!"));
                        fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                        fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                        return fullHttpResponse;
                    }
                    dataDocument.append("success", true);


                    /*ScheduledTask task = getProjectMain().getCloud().getScheduler().runTaskRepeatSync(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 5; i++) {
                                getProjectMain().getCloud().getProxys(group).forEach(t->t);
                            }
                        }
                    },0,20/** Minecraft Tickts**//*);
                    getProjectMain().getCloud().getScheduler().cancelTask(task.getTaskId());*/
                    getProjectMain().getCloud().getProxys(group).forEach(t->getProjectMain().getCloud().stopProxy(t.getName()));
                    fullHttpResponse.setStatus(HttpResponseStatus.OK);
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));

                    return fullHttpResponse;
                }else{
                    dataDocument.append("success", false).append("reason", Arrays.asList("No available -Xvlaue command found!"));
                    fullHttpResponse.setStatus(HttpResponseStatus.BAD_REQUEST);
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    return fullHttpResponse;
                }

            }
            case "delete":{

                if(httpRequest.headers().contains("-Xvalue") && getProjectMain().getCloud().getProxyGroups().containsKey(httpRequest.headers().get("-Xvalue")))
                {


                    final String group = httpRequest.headers().get("-Xvalue");
                    if(!user.getPermissions().contains("cloudnet.web.group.proxy.delete.*") && !user.getPermissions().contains("*") || !user.getPermissions().contains("cloudnet.web.group.proxy.delete."+group))
                    {
                        dataDocument.append("reason", Arrays.asList("permission denied!"));
                        fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                        fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                        return fullHttpResponse;
                    }
                    dataDocument.append("success", true);
                    ProxyGroup grp = getProjectMain().getCloud().getProxyGroup(group);
                    CloudNet.getInstance().getProxyGroups().remove(grp.getName());
                    Collection<String> wrps = grp.getWrapper();
                    getProjectMain().getCloud().getConfig().deleteGroup(grp);
                    Iterator var10 = CloudNet.getInstance().toWrapperInstances(wrps).iterator();

                    while(var10.hasNext()) {
                        Wrapper wrapper = (Wrapper)var10.next();
                        wrapper.updateWrapper();
                    }
                    fullHttpResponse.setStatus(HttpResponseStatus.OK);
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));

                    return fullHttpResponse;
                }else{
                    dataDocument.append("success", false).append("reason", Arrays.asList("No available -Xvlaue command found!"));
                    fullHttpResponse.setStatus(HttpResponseStatus.BAD_REQUEST);
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    return fullHttpResponse;
                }
            }
            case "save":{

                FullHttpRequest fullHttpRequest = (FullHttpRequest) httpRequest;
                ByteBuf buf = fullHttpRequest.content();
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                final String proxygroup = new String(bytes);
                if(!user.getPermissions().contains("cloudnet.web.group.proxy.save.*") && !user.getPermissions().contains("*") || !user.getPermissions().contains("cloudnet.web.group.proxy.save."+proxygroup))
                {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }

                dataDocument.append("success", true);
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(AutoSlot.class,new AutoSlotJsonAdapter());
                builder.registerTypeAdapter(DynamicFallback.class,new DynamicFallbackJsonAdapter());
                builder.registerTypeAdapter(Motd.class,new MotdJsonAdapter());
                builder.registerTypeAdapter(ProxyConfig.class,new ProxyConfigJsonAdapter());
                builder.registerTypeAdapter(ServerFallback.class,new ServerFallbackJsonAdapter());
                builder.registerTypeAdapter(ServerInstallablePlugin.class,new ServerInstallablePluginJsonAdapter());
                builder.registerTypeAdapter(TabList.class,new TabListJsonAdapter());
                builder.registerTypeAdapter(Template.class,new TemplateJsonAdapter());
                builder.registerTypeAdapter(ProxyGroup.class,new ProxyGroupJsonAdapter());
                Gson gson = builder.create();
                ProxyGroup proxygn = gson.fromJson(proxygroup,ProxyGroup.class);
                getProjectMain().getCloud().getConfig().createGroup(proxygn);
                CloudNet.getInstance().setupProxy(proxygn);
                if(!CloudNet.getInstance().getProxyGroups().containsKey(proxygn.getName())){
                    CloudNet.getInstance().getProxyGroups().put(proxygn.getName(), proxygn);
                }
                Iterator var10 = CloudNet.getInstance().toWrapperInstances(proxygn.getWrapper()).iterator();
                while(var10.hasNext()) {
                    Wrapper wrapper = (Wrapper)var10.next();
                    wrapper.updateWrapper();
                }
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                return  fullHttpResponse;
            }
            case "start":{


                if((httpRequest.headers().contains("-Xvalue") && getProjectMain().getCloud().getProxyGroups().containsKey(httpRequest.headers().get("-Xvalue")))
                        || (httpRequest.headers().contains("-Xcount") && getProjectMain().getCloud().getProxyGroups().containsKey(httpRequest.headers().get("-Xcount"))))
                {
                    final String group = httpRequest.headers().get("-Xvalue");
                    if(!user.getPermissions().contains("cloudnet.web.group.proxy.start.*") && !user.getPermissions().contains("*") || !user.getPermissions().contains("cloudnet.web.group.proxy.start."+group))
                    {
                        dataDocument.append("reason", Arrays.asList("permission denied!"));
                        fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                        fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                        return fullHttpResponse;
                    }
                    dataDocument.append("success", true);


                    final int count = Integer.valueOf(httpRequest.headers().get("-Xcount"));
                    for (int i = 0; i < count; i++) {
                        getProjectMain().getCloud().startProxyAsync(getProjectMain().getCloud().getProxyGroup(group));
                    }
                    fullHttpResponse.setStatus(HttpResponseStatus.OK);
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));

                    return fullHttpResponse;
                }else{
                    dataDocument.append("success", false).append("reason", Arrays.asList("No available -Xvalue,-Xcount command found!"));
                    fullHttpResponse.setStatus(HttpResponseStatus.BAD_REQUEST);
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    return fullHttpResponse;
                }

            }
            default:{
                dataDocument.append("success", false).append("reason", Arrays.asList("No available -Xmessage command found!"));
                fullHttpResponse.setStatus(HttpResponseStatus.BAD_REQUEST);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                return fullHttpResponse;
            }
        }
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        fullHttpResponse.headers().set("Content-Type", "application/json");
        fullHttpResponse.headers().set("Access-Control-Allow-Credentials", "true");
        fullHttpResponse.headers().set("Access-Control-Allow-Headers", "content-type, if-none-match, -Xcloudnet-token, -Xmessage, -Xvalue, -Xcloudnet-user, -Xcloudnet-password,-Xcount");
        fullHttpResponse.headers().set("Access-Control-Allow-Methods", "POST,GET,OPTIONS");
        fullHttpResponse.headers().set("Access-Control-Allow-Origin", "*");
        fullHttpResponse.headers().set("Access-Control-Max-Age", "3600");
        return fullHttpResponse;
    }

    public ProjectMain getProjectMain() {
        return projectMain;
    }
}
