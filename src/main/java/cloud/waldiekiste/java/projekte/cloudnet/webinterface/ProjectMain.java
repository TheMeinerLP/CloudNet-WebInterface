package cloud.waldiekiste.java.projekte.cloudnet.webinterface;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands.CommandSetupConfig;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands.CommandUpdateChannel;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands.CommandVersion;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.AuthenticationAPI;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.CPermsApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.DashboardAPI;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.MasterApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.MobApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.PlayerAPI;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.ProxyApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.ServerApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.SignApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.UserAPI;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.UtilsAPI;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.WrapperAPI;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.listener.ScreenSessionListener;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.mob.MobDatabase;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.permission.ConfigPermissions;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.services.UpdateService;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup.ConfigSetup;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup.UpdateChannelSetup;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.sign.SignDatabase;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.CoreModule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the class, which is the base of the websocket-extension. At the startup, this class is
 * used by the Modulemanager.
 */
public final class ProjectMain extends CoreModule {

  /**
   * At this part, the basic Strings,Lists,Services, Setups and Maps  are listed.
   */
  private ConfigPermissions configPermission;
  private List<String> consoleLines;
  private Map<String, List<String>> screenInfos = new HashMap<>();
  private ConfigSetup configSetup;
  private UpdateChannelSetup updateChannelSetup;
  private UpdateService updateService;
  private SignDatabase signDatabase;
  private MobDatabase mobDatabase;

  /**
   * In this method, the trackingservice, the updateservice and the classes are initialised.
   *
   * @see UpdateChannelSetup
   * @see UpdateService
   * @see ConfigSetup
   */
  @Override
  public void onLoad() {
    this.updateService = new UpdateService();
    this.consoleLines = new ArrayList<>();
    CloudNet.getLogger().getHandler().add(consoleLines::add);
    this.configSetup = new ConfigSetup();
    this.updateChannelSetup = new UpdateChannelSetup();

  }

  /**
   * Iniatilising API'S and checking version and counting the time between startup and end of the
   * startup. Errorservice is still under development.
   *
   * @see ConfigPermissions
   * @see MasterApi
   * @see AuthenticationAPI
   * @see ProxyApi
   * @see UserAPI
   * @see DashboardAPI
   * @see ServerApi
   * @see WrapperAPI
   * @see UtilsAPI
   */
  @Override
  public void onBootstrap() {
    versionCheck();
    try {
      this.configPermission = new ConfigPermissions();
      this.signDatabase = new SignDatabase(
          this.getCloud().getDatabaseManager().getDatabase("cloud_internal_cfg"));
      this.mobDatabase = new MobDatabase(
          this.getCloud().getDatabaseManager().getDatabase("cloud_internal_cfg"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    getCloud().getCommandManager().registerCommand(new CommandSetupConfig(this));
    getCloud().getCommandManager().registerCommand(new CommandVersion(getVersion()));
    getCloud().getCommandManager().registerCommand(new CommandUpdateChannel(this));
    getCloud().getEventManager().registerListener(this, new ScreenSessionListener(this));
    new MasterApi(getCloud(), this);
    new AuthenticationAPI();
    new ProxyApi(getCloud(), this);
    new UserAPI(getCloud(), this);
    new DashboardAPI(getCloud(), this);
    new ServerApi(getCloud(), this);
    new WrapperAPI(getCloud());
    new UtilsAPI(getCloud(), this);
    new PlayerAPI(getCloud(), this);
    new SignApi(this);
    new MobApi(this);
    if (this.configPermission.isEnabled()) {
      new CPermsApi(this);
    }
  }

  /**
   * - Clearing consoleLines & screenInfos for RAM "boost" -
   */
  @Override
  public void onShutdown() {
    consoleLines = null;
    screenInfos = null;
  }

  /**
   * Checking Version + Checking functionality with the Cloudnet Version
   */
  private void versionCheck() {
    if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get()
        .contains("mdwi.downgrade")) {
      if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get()
          .getBoolean("mdwi.downgrade")) {
        if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get()
            .contains("mdwi.updateChannel")) {
          this.updateChannelSetup.start(CloudNet.getLogger().getReader());
          //this.updateService.checkUpdate(this);
        } else {
          //this.updateService.checkUpdate(this);
        }
      }
    } else {
      if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get()
          .contains("mdwi.updateChannel")) {
        this.updateChannelSetup.start(CloudNet.getLogger().getReader());
        this.updateService.checkUpdate(this);
      } else {
        this.updateService.checkUpdate(this);
      }
    }
    /*
     * Checking CloudNet Version and sending Error-Message if its lower than the version 2.1.8
     */
    if (Integer
        .parseInt(NetworkUtils.class.getPackage().getImplementationVersion().replace(".", ""))
        < 218) {
      System.err.println("This Module is not compatible with this CloudNet Version");
    }
  }

  /**
   * Here its getting the Updateservice and it is returning that service
   *
   * @see UpdateService
   */
  public UpdateService getUpdateService() {
    return updateService;
  }

  /**
   * Here its getting the Configsetup and it is returning that setup
   *
   * @see ConfigSetup
   */
  public ConfigSetup getConfigSetup() {
    return configSetup;
  }

  /**
   * Here its getting the ConfigPermission and its returning them
   *
   * @see ConfigPermissions
   */
  public ConfigPermissions getConfigPermission() {
    return configPermission;
  }

  /**
   * Here its getting the ConsoleLines List and its returning the list
   */
  public List<String> getConsoleLines() {
    return consoleLines;
  }

  /**
   * Here its getting a String, a List wich contains a String and the ScreenInfo Map, its returning
   * the map
   */
  public Map<String, List<String>> getScreenInfos() {
    return screenInfos;
  }

  public SignDatabase getSignDatabase() {
    return signDatabase;
  }

  public MobDatabase getMobDatabase() {
    return mobDatabase;
  }
}
