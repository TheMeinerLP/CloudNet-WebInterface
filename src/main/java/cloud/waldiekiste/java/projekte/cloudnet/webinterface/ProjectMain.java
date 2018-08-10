package cloud.waldiekiste.java.projekte.cloudnet.webinterface;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.usermangment.UserAuthentication;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.ProxyAPI;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.permission.ConfigPermissions;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.sign.ConfigSignLayout;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.sign.SignDatabase;
import de.dytanic.cloudnetcore.api.CoreModule;

public class ProjectMain extends CoreModule {

    private ConfigSignLayout configSignLayout;
    private SignDatabase signDatabase;
    private ConfigPermissions configPermission;

    @Override
    public void onBootstrap() {
        new UserAuthentication(getCloud(),this);
        new ProxyAPI(getCloud(),this);

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
}
