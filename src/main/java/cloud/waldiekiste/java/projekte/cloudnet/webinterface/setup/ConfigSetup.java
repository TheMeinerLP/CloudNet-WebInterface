package cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.google.gson.stream.JsonWriter;
import de.dytanic.cloudnet.setup.Setup;
import de.dytanic.cloudnet.setup.SetupRequest;
import de.dytanic.cloudnet.setup.SetupResponseType;
import de.dytanic.cloudnetcore.CloudNet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigSetup extends Setup {
    public ConfigSetup() {
        setupComplete(t->{
           String name = t.getString("NetworkName");
           String webhost = CloudNet.getInstance().getWebServer().getAddress();
           int webport = CloudNet.getInstance().getWebServer().getPort();
           String url;
           if (CloudNet.getInstance().getWebServer().isSsl()) {
               url = "https://"+webhost+":"+webport+"/";
           }else{
               url = "http://"+webhost+":"+webport+"/";
           }
           JsonObject jsonObject = new JsonObject();
           JsonObject server = new JsonObject();
           JsonArray servers = new JsonArray();
           server.addProperty("CloudURL",url);
           server.addProperty("CloudName",name);
           servers.add(server);
           jsonObject.add("Servers",servers );
           System.out.println("Setup complete!");
           System.out.println("Copy now the config.json from 'local/mdwi/' into your '/assets/config/' in your WebInterface!");
           File f = new File("local/mdwi");
           f.mkdirs();
           File config = new File(f,"config.json");
            try {
                FileWriter writer = new FileWriter(config);
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        setupCancel(()->{
            System.out.println("The setup was cancel from user!");
        });
        request(new SetupRequest("NetworkName","Please insert the network name of the cloud", "",SetupResponseType.STRING,c->true));
    }
}
