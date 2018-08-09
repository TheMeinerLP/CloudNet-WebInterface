package cloud.waldiekiste.java.projekte.cloudnet.webinterface;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.GetWebsiteUtils;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.PutWebsiteUtils;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.Auth;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.Naviagtion;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.Notification;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.User;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.proxygroups.HttpProxyGroupAPI;
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
        new Auth(getCloud(),this);
        new User(getCloud(),this);
        new Notification(getCloud(),this);
        new Naviagtion(getCloud(),this);
        new HttpProxyGroupAPI(getCloud(),this);
        new GetWebsiteUtils(getCloud(),this);
        new PutWebsiteUtils(getCloud(),this);

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
