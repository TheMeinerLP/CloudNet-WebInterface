/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands.CommandSetupConfig;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands.CommandUpdateChannel;
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
import de.dytanic.cloudnet.modules.ModuleConfig;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.CoreModule;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
        consoleLines = new ArrayList<>();
        CloudNet.getLogger().getHandler().add(consoleLines::add);
        configSetup = new ConfigSetup();
        this.updateChannelSetup = new UpdateChannelSetup();
    }

    @Override
    public void onBootstrap() {
        if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get().contains("mdwi.updateChannel")) {
            this.updateChannelSetup.start(CloudNet.getLogger().getReader());
        }else{
            run();
        }
        getCloud().getCommandManager().registerCommand(new CommandSetupConfig(this));
        getCloud().getCommandManager().registerCommand(new CommandVersion(this));
        getCloud().getCommandManager().registerCommand(new CommandUpdateChannel(this));
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
    public ArrayList<UpdateData> getUpdates(VersionType branch) throws Exception {
        String url = "https://api.mc-lifetime.de/mdwebinterface/version.php?type=modul&branch="+branch.getType();
        URL adress = new URL( url);
        HttpURLConnection connection = (HttpURLConnection) adress.openConnection();
        connection.setConnectTimeout(2000);
        connection.setDoOutput(false);
        connection.setDoInput(true);

        if (connection.getResponseCode() == 403) {
            System.err.println("[Updater] Der Master kann nicht auf die API zugreifen! (403)");
            throw new IOException("Der Server kann nicht auf die API zugreifen! (403)");
        }

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Fehler bei der Anfrage");
        }
        String result = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
        ArrayList<UpdateData> datas = new ArrayList<>();
        jsonObject.get("versions").getAsJsonArray().forEach(t->datas.add(JsonUtil.getGson().fromJson(t,UpdateData.class)));
        return datas;
    }
    private UpdateData getUpdateData(VersionType branch) throws Exception {
        String url = "https://api.mc-lifetime.de/mdwebinterface/version.php?type=modul&branch="+branch.getType();
        URL adress = new URL( url);
        HttpURLConnection connection = (HttpURLConnection) adress.openConnection();
        connection.setConnectTimeout(2000);
        connection.setDoOutput(false);
        connection.setDoInput(true);

        if (connection.getResponseCode() == 403) {
            System.err.println("[Updater] Der Master kann nicht auf die API zugreifen! (403)");
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
            Document document = CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().get();
            if (document.contains("mdwi.updateChannel")) {
                VersionType type = VersionType.valueOf(document.get("mdwi.updateChannel").getAsString());
                ModuleConfig config = getModuleConfig();
                String versionID = config.getVersion().substring(0,config.getVersion().indexOf("-"));
                Integer oldVersion = new Integer(versionID.replace(".",""));
                UpdateData data = getUpdateData(type);
                if(data == null){
                    return;
                }
                String v = data.getVersion().replace(".","");
                Integer newVersion = new Integer(v);
                if(newVersion > oldVersion){
                    onShutdown();
                    getCloud().getModuleManager().disableModule(this);
                    File f = config.getFile();
                    f.delete();
                    String urlpath = data.getPath().substring(data.getPath().indexOf("/update"),data.getPath().length());
                    String downloadpath = "https:/"+urlpath;
                    update(downloadpath,data);
                    CloudNet.getInstance().getModuleManager().disableModules();
                    CloudNet.getInstance().getModuleManager().loadModules();
                }else{
                    System.out.println("[Updater] No Update available!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void update(String path,UpdateData data) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection)(new URL(path)).openConnection();
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(1000);
            httpURLConnection.connect();
            System.out.println("Downloading update...");

            InputStream inputStream = httpURLConnection.getInputStream();
            Throwable var4 = null;

            try {
                File f = new File("modules",data.getPath().substring(data.getPath().lastIndexOf("/")+1,data.getPath().length()));
                Files.copy(inputStream, f.toPath(),new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if (inputStream != null) {
                    if (var4 != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        inputStream.close();
                    }
                }

            }

            httpURLConnection.disconnect();
            System.out.println("Download complete!");
        } catch (IOException var18) {
            var18.printStackTrace();
        }

    }
}
