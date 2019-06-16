package cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.dytanic.cloudnet.setup.Setup;
import de.dytanic.cloudnet.setup.SetupRequest;
import de.dytanic.cloudnet.setup.SetupResponseType;
import de.dytanic.cloudnetcore.CloudNet;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * This class creates the Configuration file.
 */

public final class ConfigSetup extends Setup {

  private boolean defaultDashboard = false;
  private boolean defaultConsole = false;

  public ConfigSetup() {
    setupComplete(t -> {

      String name = t.getString("NetworkName");
           /*
            at Line 32 and 33 it sets the details of the webserver (port and adress)
             */
      String webhost = CloudNet.getInstance().getWebServer().getAddress();
      int webport = CloudNet.getInstance().getWebServer().getPort();
      String url;
           /*
            Here it checks if the Webserver has a SSL-Certificate.
            */
      if (CloudNet.getInstance().getWebServer().isSsl()) {
        url = "https://" + webhost + ":" + webport;
      } else {
        url = "http://" + webhost + ":" + webport;
      }
      JsonObject jsonObject = new JsonObject();
      JsonObject server = new JsonObject();
      JsonArray servers = new JsonArray();
           /*
           Here it adds the properties CloudURL and CloudName, also it adds the server
            */
      server.addProperty("CloudURL", url);
      server.addProperty("CloudName", name);
      servers.add(server);
      jsonObject.add("Servers", servers);
      JsonObject analytics = new JsonObject();
      analytics.addProperty("enabled", t.getBoolean("analytics.enabled"));
      analytics.addProperty("ID", UUID.randomUUID().toString());
      jsonObject.add("analytics", analytics);
      JsonObject GoogleRecaptcha = new JsonObject();
      GoogleRecaptcha.addProperty("enabled", t.getBoolean("google.enabled"));
      if (t.contains("google.sitekey")) {
        GoogleRecaptcha.addProperty("SiteKey", t.getString("google.sitekey"));
      }
      jsonObject.add("GoogleRecaptcha", GoogleRecaptcha);
      JsonObject style = new JsonObject();
      style.addProperty("default", t.getString("style.default"));
      jsonObject.add("style", style);
      JsonObject settings = new JsonObject();
      settings.addProperty("branding", t.getString("settings.branding"));
      settings.addProperty("timeout", t.getInt("settings.timeout"));
      JsonObject interval = new JsonObject();
      if (defaultConsole) {
        interval.addProperty("console", 1000);
      } else {
        interval.addProperty("console", t.getInt("settings.interval.console"));
      }
      if (defaultDashboard) {
        interval.addProperty("dashboard", 1000);
      } else {
        interval.addProperty("dashboard", t.getInt("settings.interval.dashboard"));
      }
      settings.add("interval", interval);
      jsonObject.add("settings", settings);

           /*
           The user gets a message as far as the setup is completed without errors, also he gets the
           instructions to copy the config into the Webinterface.
            */
      System.out.println("Setup complete!");
      System.out.println("Copy now the config.json from 'local/mdwi/' into your '/assets/config/'" +
          " in your WebInterface!");
      File f = new File("local/mdwi");
           /*
           Here it creats a directory and the config file
            */
      f.mkdirs();
      File config = new File(f, "config.json");
      try {
        FileWriter writer = new FileWriter(config);
        writer.write(jsonObject.toString());
        writer.flush();
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    setupCancel(() -> {
            /*
            If the user cancels the setup, he gets a Message
             */
      System.out.println("The setup was cancel from user!");
    });
    request(new SetupRequest("analytics.enabled", "If you will enabled Analytics?",
        "", SetupResponseType.BOOL, c -> c.equals("yes")));
    request(new SetupRequest("NetworkName", "Please insert the network name of the cloud.",
        "", SetupResponseType.STRING, c -> true));
    request(new SetupRequest("google.enabled", "If you will enabled Google Recaptcha ?",
        "", SetupResponseType.BOOL, c -> {
      if (c.equalsIgnoreCase("yes")) {
        request(new SetupRequest("google.sitekey", "Please insert the key for Google Recaptcha.",
            "", SetupResponseType.STRING, c1 -> true));
        return true;
      } else {
        return false;
      }
    }));
    request(new SetupRequest("style.default",
        "Please insert the default theme for WebInterface(dark-theme|light-theme|mad-theme|venymc-thme)",
        "", SetupResponseType.STRING, c -> true));
    request(new SetupRequest("settings.timeout",
        "Please insert the session timeout for WebInterface Session(In Minutes)",
        "Minutes to tiny", SetupResponseType.NUMBER, c -> Integer.parseInt(c) > 2));
    request(new SetupRequest("settings.branding", "Please insert the Branding for WebInterface",
        "", SetupResponseType.STRING, c -> true));
    request(new SetupRequest("settings.interval.console",
        "Please enter the update interval in milliseconds for the console live update. Default is 1000",
        "Allowed minimum is 1000, all under is properly laggy and can crash your Browser or PC",
        SetupResponseType.NUMBER, c -> {
      if (c.isEmpty()) {
        this.defaultConsole = true;
        return true;
      } else {
        return Integer.parseInt(c) >= 1000;
      }
    }));
    request(new SetupRequest("settings.interval.dashboard",
        "Please enter the update interval in milliseconds for the dashboard live update. Default is 1000",
        "Allowed minimum is 1000, all under is properly laggy and can crash your Browser or PC",
        SetupResponseType.NUMBER, c -> {
      if (c.isEmpty()) {
        this.defaultDashboard = true;
        return true;
      } else {
        return Integer.parseInt(c) >= 1000;
      }
    }));
  }
}