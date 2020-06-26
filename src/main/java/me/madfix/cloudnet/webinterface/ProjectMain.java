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
import me.madfix.cloudnet.webinterface.sign.SignDatabase;
import me.madfix.cloudnet.webinterface.listener.ScreenSessionListener;
import me.madfix.cloudnet.webinterface.permission.ConfigPermissions;
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
  private SignDatabase signDatabase;
  private MobDatabase mobDatabase;

  /**
   * In this method, the trackingservice, the updateservice and the classes are initialised.
   *
   */
  @Override
  public void onLoad() {
    this.consoleLines = new ArrayList<>();
    CloudNet.getLogger().getHandler().add(consoleLines::add);

  }

  /**
   * Iniatilising API'S and checking version and counting the time between startup and end of the.
   * startup. Errorservice is still under development.
   *
   * @see ConfigPermissions
   * @see MasterApi
   * @see AuthenticationApi
   * @see ProxyApi
   * @see UserApi
   * @see DashboardApi
   * @see ServerApi
   * @see WrapperApi
   * @see UtilsApi
   */
  @Override
  public void onBootstrap() {
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
