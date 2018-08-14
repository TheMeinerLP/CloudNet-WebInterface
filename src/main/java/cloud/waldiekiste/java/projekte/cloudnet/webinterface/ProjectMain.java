package cloud.waldiekiste.java.projekte.cloudnet.webinterface;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.*;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.usermangment.UserAuthentication;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.permission.ConfigPermissions;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.sign.ConfigSignLayout;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.sign.SignDatabase;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.CoreModule;

import java.util.ArrayList;
import java.util.List;


public class ProjectMain extends CoreModule {

    private ConfigSignLayout configSignLayout;
    private SignDatabase signDatabase;
    private ConfigPermissions configPermission;
    private List<String> consoleLines;

    @Override
    public void onLoad() {
        consoleLines = new ArrayList<>();
        CloudNet.getLogger().getHandler().add(consoleLines::add);
    }

    @Override
    public void onBootstrap() {
        new MasterAPI(getCloud(),this);
        new UserAuthentication(getCloud());
        new ProxyAPI(getCloud(),this);
        new UserAPI(getCloud(),this);
        new DashboardAPI(getCloud(),this);
        new ServerGroupAPI(getCloud(),this);
        new WrapperAPI(getCloud(),this);
        this.configSignLayout = new ConfigSignLayout();
        this.configSignLayout.loadLayout();
        this.signDatabase = new SignDatabase(getCloud().getDatabaseManager().getDatabase("cloud_internal_cfg"));
        try {
            this.configPermission = new ConfigPermissions();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShutdown() {
        consoleLines = null;
    }

    public ConfigSignLayout getConfigSignLayout() {
        return configSignLayout;
    }

    public SignDatabase getSignDatabase() {
        return signDatabase;
    }

    public ConfigPermissions getConfigPermission() {
        return configPermission;
    }

    public List<String> getConsoleLines() {
        return consoleLines;
    }
}
