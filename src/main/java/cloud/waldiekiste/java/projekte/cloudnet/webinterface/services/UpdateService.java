package cloud.waldiekiste.java.projekte.cloudnet.webinterface.services;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.JsonUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils.UpdateData;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils.VersionType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.modules.Module;
import de.dytanic.cloudnet.modules.ModuleConfig;
import de.dytanic.cloudnetcore.CloudNet;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public final class UpdateService {

  public void checkUpdate(Module module) {
    try {
      Document document = CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase()
          .get();
      if (document.contains("mdwi.updateChannel")) {
        VersionType type = VersionType.valueOf(document.get("mdwi.updateChannel").getAsString());
        ModuleConfig config = module.getModuleConfig();
        Long oldVersion = Long.valueOf(config.getVersion());
        UpdateData data = getUpdateData(type);
        if (data == null) {
          return;
        }

        long newVersion = data.getVersion();
        if (newVersion > oldVersion) {
          module.onShutdown();
          CloudNet.getInstance().getModuleManager().disableModule(module);
          File f = config.getFile();
          f.delete();
          update(data);
          CloudNet.getInstance().reload();
        } else {
          System.out.println("[Updater] No Update available!");
        }
      } else {
        System.err.println("CloudNet-Web");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void update(UpdateData data) {
    try {
      HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(data.getFilePath()))
          .openConnection();
      httpURLConnection.setRequestProperty("User-Agent",
          "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
      httpURLConnection.setUseCaches(false);
      httpURLConnection.setConnectTimeout(10000);
      httpURLConnection.connect();
      System.out.println("Downloading update...");
      InputStream inputStream = httpURLConnection.getInputStream();
      if (inputStream == null) {
        System.out.println("[Updater] No Update available!");
        return;
      }
      File f = new File("modules", data.getFileName());
      try {
        Files.copy(inputStream, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
      } catch (Exception e) {
        e.printStackTrace();
      }
      httpURLConnection.disconnect();
      System.out.println("Download complete!");
    } catch (IOException var18) {
      var18.printStackTrace();
    }
  }

  private UpdateData getUpdateData(VersionType branch) throws IOException {
    String url = String
        .format("https://api.madfix.me/v1/download.php?BRANCH=%s&ENVIRONMENT=CLOUDNET",
            branch.getType());
    URL adress;
    try {
      adress = new URL(url);
    } catch (MalformedURLException e) {
      System.err.println("Url format is Invalid");
      return null;
    }
    HttpURLConnection connection;
    try {
      connection = (HttpURLConnection) adress.openConnection();
    } catch (IOException e) {
      System.err.println("Cannot connect to URL");
      return null;
    }
    connection.setRequestProperty("User-Agent",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
    connection.setConnectTimeout(2000);
    connection.setDoOutput(false);
    connection.setDoInput(true);

    if (connection.getResponseCode() == 403) {
      System.err.println("[Updater] Der Master kann nicht auf die API zugreifen! (403)");
      return null;
    }

    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
      System.out.println("Fehler bei der Anfrage");
      return null;
    }
    String result = new BufferedReader(new InputStreamReader(connection.getInputStream()))
        .readLine();
    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
    UpdateData data = JsonUtil.getGson()
        .fromJson(jsonObject.get("versions").getAsJsonArray().get(0), UpdateData.class);
    connection.disconnect();
    return data;
  }

  public ArrayList<UpdateData> getUpdates(VersionType branch) throws Exception {
    String url = String
        .format("https://api.madfix.me/v1/download.php?BRANCH=%s&ENVIRONMENT=CLOUDNET",
            branch.getType());
    URL adress = new URL(url);
    HttpURLConnection connection = (HttpURLConnection) adress.openConnection();
    connection.setRequestProperty("User-Agent",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
    connection.setConnectTimeout(2000);
    connection.setDoOutput(false);
    connection.setDoInput(true);

    if (connection.getResponseCode() == 403) {
      System.err.println("[Updater] Der Master kann nicht auf die API zugreifen! (403)");
      return null;
    }

    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
      System.out.println("Fehler bei der Anfrage");
      return null;
    }
    String result = new BufferedReader(new InputStreamReader(connection.getInputStream()))
        .readLine();
    JsonElement jsonObject = new JsonParser().parse(result);
    ArrayList<UpdateData> datas = new ArrayList<>();
    jsonObject.getAsJsonObject().get("versions").getAsJsonArray().forEach(
        t -> datas.add(JsonUtil.getGson().fromJson(t.getAsJsonObject(), UpdateData.class)));
    connection.disconnect();
    return datas;
  }
}
