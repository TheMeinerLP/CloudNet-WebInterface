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
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.listener.ScreenSessionEvent;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.permission.ConfigPermissions;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.services.ErrorService;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.services.TrackerService;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.services.UpdateService;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup.ConfigSetup;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup.UpdateChannelSetup;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.CoreModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProjectMain extends CoreModule {

    private ConfigPermissions configPermission;
    private List<String> consoleLines;
    private Map<String,List<String>> screenInfos = new HashMap<>();
    private ConfigSetup configSetup;
    private UpdateChannelSetup updateChannelSetup;
    private UpdateService updateService;
    private TrackerService tracking;
    private String Prefix = getModuleConfig().getName();

    @Override
    public void onLoad() {
        this.tracking = new TrackerService();
        this.tracking.init();
        this.tracking.onLoad();
        this.updateService = new UpdateService();
        this.consoleLines = new ArrayList<>();
        CloudNet.getLogger().getHandler().add(consoleLines::add);
        this.configSetup = new ConfigSetup();
        this.updateChannelSetup = new UpdateChannelSetup();
    }

    @Override
    public void onBootstrap() {
        versionCheck();
        try {
            this.configPermission = new ConfigPermissions();
        } catch (Exception e) {
            ErrorService.error(101);
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
        if(this.configPermission.isEnabled()){
            new CPermsApi(this);
        }
        this.tracking.onBootstrap(this);
    }
    @Override
    public void onShutdown() {
        consoleLines = null;
        screenInfos = null;
    }
    private void versionCheck(){
        if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get().contains("mdwi.downgrade")) {
            if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get().getBoolean("mdwi.downgrade")) {

                if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get().contains("mdwi.updateChannel")) {
                    this.updateChannelSetup.start(CloudNet.getLogger().getReader());
                    this.updateService.checkUpdate(this);
                }else{
                    this.updateService.checkUpdate(this);
                }
            }
        }else{
            if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get().contains("mdwi.updateChannel")) {
                this.updateChannelSetup.start(CloudNet.getLogger().getReader());
                this.updateService.checkUpdate(this);
            }else{
                this.updateService.checkUpdate(this);
            }
        }

        if(new Integer( NetworkUtils.class.getPackage().getImplementationVersion().replace(".","")) < 218){
            System.err.println("This Module is not compatible with this CloudNet Version");
            return;
        }
    }

    public UpdateService getUpdateService() {
        return updateService;
    }

    public ConfigSetup getConfigSetup() {
        return configSetup;
    }

    public TrackerService getTracking() {
        return tracking;
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
}
