package me.madfix.cloudnet.webinterface;

import me.madfix.cloudnet.webinterface.http.v2.AuthenticationApi;
import me.madfix.cloudnet.webinterface.http.v2.CPermsApi;
import me.madfix.cloudnet.webinterface.http.v2.DashboardApi;
import me.madfix.cloudnet.webinterface.http.v2.MasterApi;
import me.madfix.cloudnet.webinterface.http.v2.MobApi;
import me.madfix.cloudnet.webinterface.http.v2.PlayerApi;
import me.madfix.cloudnet.webinterface.http.v2.ProxyApi;
import me.madfix.cloudnet.webinterface.http.v2.ServerApi;
import me.madfix.cloudnet.webinterface.http.v2.SignApi;
import me.madfix.cloudnet.webinterface.http.v2.UserApi;
import me.madfix.cloudnet.webinterface.http.v2.UtilsApi;
import me.madfix.cloudnet.webinterface.http.v2.WrapperApi;
import me.madfix.cloudnet.webinterface.mob.MobDatabase;
import me.madfix.cloudnet.webinterface.services.ConfigurationService;
import me.madfix.cloudnet.webinterface.sign.SignDatabase;
import me.madfix.cloudnet.webinterface.listener.ScreenSessionListener;
import me.madfix.cloudnet.webinterface.permission.ConfigPermissions;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.CoreModule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ProjectMain extends CoreModule {

  private ConfigurationService configurationService;

  private ConfigPermissions configPermission;
  private List<String> consoleLines;
  private Map<String, List<String>> screenInfos = new HashMap<>();
  private SignDatabase signDatabase;
  private MobDatabase mobDatabase;

  @Override
  public void onLoad() {
    this.configurationService = new ConfigurationService();
    if (!this.configurationService.loadConfigurationFile()) {
      CloudNet.getLogger().severe("No configuration file was found with the name: interface.json.");
      CloudNet.getLogger().severe("Web interface will not start!");
      CloudNet.getLogger().severe("Please create your configuration file under X and follow the instructions on the website. ");
    }
    if (this.configurationService.getOptionalInterfaceConfiguration().isPresent()) {
      this.consoleLines = new ArrayList<>();
      CloudNet.getLogger().getHandler().add(consoleLines::add);
    }
  }

  @Override
  public void onBootstrap() {
    if (this.configurationService.getOptionalInterfaceConfiguration().isPresent()) {
      try {
        this.configPermission = new ConfigPermissions();
        this.signDatabase = new SignDatabase(
            this.getCloud().getDatabaseManager().getDatabase("cloud_internal_cfg"));
        this.mobDatabase = new MobDatabase(
            this.getCloud().getDatabaseManager().getDatabase("cloud_internal_cfg"));
      } catch (Exception e) {
        e.printStackTrace();
      }
      getCloud().getEventManager().registerListener(this, new ScreenSessionListener(this));
      new MasterApi(getCloud(), this);
      new AuthenticationApi();
      new ProxyApi(getCloud(), this);
      new UserApi(getCloud(), this);
      new DashboardApi(getCloud(), this);
      new ServerApi(getCloud(), this);
      new WrapperApi(getCloud());
      new UtilsApi(getCloud(), this);
      new PlayerApi(getCloud(), this);
      new SignApi(this);
      new MobApi(this);
      if (this.configPermission.isEnabled()) {
        new CPermsApi(this);
      }
    }


  }

  /**
   * Clearing consoleLines & screenInfos for RAM "boost".
   */
  @Override
  public void onShutdown() {
    consoleLines = null;
    screenInfos = null;
  }

  /**
   * Here its getting the ConfigPermission and its returning them.
   *
   * @see ConfigPermissions
   */
  public ConfigPermissions getConfigPermission() {
    return configPermission;
  }

  /**
   * Here its getting the ConsoleLines List and its returning the list.
   */
  public List<String> getConsoleLines() {
    return consoleLines;
  }

  /**
   * Here its getting a String, a List wich contains a String and the ScreenInfo Map, its returning
   * the map.
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
