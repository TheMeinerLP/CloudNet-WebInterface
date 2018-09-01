/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands.CommandSetupConfig;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands.CommandVersion;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.*;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.JsonUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.listener.ScreenSessionEvent;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.permission.ConfigPermissions;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup.ConfigSetup;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup.UpdateChannelSetup;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils.UpdateData;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils.VersionType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.CoreModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProjectMain extends CoreModule implements Runnable{

    private ConfigPermissions configPermission;
    private List<String> consoleLines;
    private Map<String,List<String>> screenInfos = new HashMap<>();
    private ConfigSetup configSetup;
    private UpdateChannelSetup updateChannelSetup;

    @Override
    public void onLoad() {
        getCloud().getScheduler().runTaskRepeatAsync(this::run,0,50);
        consoleLines = new ArrayList<>();
        CloudNet.getLogger().getHandler().add(consoleLines::add);
        configSetup = new ConfigSetup();
        this.updateChannelSetup = new UpdateChannelSetup();
    }

    @Override
    public void onBootstrap() {
        /*if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get().contains("mdwi.updateChannel")) {
            this.updateChannelSetup.start(CloudNet.getLogger().getReader());
        }*/
        getCloud().getCommandManager().registerCommand(new CommandSetupConfig(this));
        getCloud().getCommandManager().registerCommand(new CommandVersion(this));
        getCloud().getEventManager().registerListener(this,new ScreenSessionEvent(this));
        new MasterAPI(getCloud(),this);
        new AuthenticationAPI(getCloud());
        new ProxyAPI(getCloud(),this);
        new UserAPI(getCloud(),this);
        new DashboardAPI(getCloud(),this);
        new ServerAPI(getCloud(),this);
        new WrapperAPI(getCloud(),this);
        new UtilsAPI(getCloud(),this);
        try {
            this.configPermission = new ConfigPermissions();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private UpdateData getSecurityUpdateData(VersionType branch, boolean dev) throws Exception {
        String url;
        if(dev){
            url = "https://update.mc-lifetime.de/CLOUDNET/WebInterface/.testing/version.php?dev=http://localhost:4200&branch="+branch.getType()+"&type=modul";
        }else{
            url = "https://update.mc-lifetime.de/CLOUDNET/WebInterface/.testing/version.php?branch="+branch.getType()+"&type=modul";
        }
        URL adress = new URL( url);
        HttpURLConnection connection = (HttpURLConnection) adress.openConnection();
        connection.setConnectTimeout(2000);
        connection.setDoOutput(false);
        connection.setDoInput(true);

        if (connection.getResponseCode() == 403) {
            System.err.println("[Updater] Der Server kann nicht auf die API zugreifen! (403)");
            throw new IOException("Der Server kann nicht auf die API zugreifen! (403)");
        }

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Fehler bei der Anfrage");
        }


        String result = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
        UpdateData data = JsonUtil.getGson().fromJson(jsonObject.get("versions").getAsJsonArray().get(0),UpdateData.class);
        return data;

    }

    @Override
    public void onShutdown() {
        consoleLines = null;
        screenInfos = null;
    }

    public ConfigSetup getConfigSetup() {
        return configSetup;
    }


    public ConfigPermissions getConfigPermission() {
        return configPermission;
    }

    public List<String> getConsoleLines() {
        return consoleLines;
    }

    public Map<String, List<String>> getScreenInfos() {
        return screenInfos;
    }

    @Override
    public void run() {
        try {
            boolean dev = false;
            Document document = CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().get();
            if (document.contains("mdwi.updateChannel")) {
                VersionType type = VersionType.valueOf(document.get("mdwi.updateChannel").getAsString());
                switch (type){
                    case RELEASE:{
                        UpdateData data = getSecurityUpdateData(type,dev);
                        UpdateData sdata = getSecurityUpdateData(VersionType.RELEASE_SECURITY,dev);
                        String versionID = getVersion().substring(getVersion().lastIndexOf("-"),getVersion().length());
                        Integer oldVersion = new Integer(versionID.replace(".",""));

                    }
                    case SNAPSHOT:{

                    }
                    case DEVELOPMENT:{

                    }
                    default:{

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
