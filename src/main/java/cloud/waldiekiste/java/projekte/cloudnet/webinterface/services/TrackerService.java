package cloud.waldiekiste.java.projekte.cloudnet.webinterface.services;

import de.dytanic.cloudnet.modules.Module;
import de.dytanic.cloudnetcore.CloudNet;
import org.piwik.java.tracking.CustomVariable;
import org.piwik.java.tracking.PiwikRequest;
import org.piwik.java.tracking.PiwikTracker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TrackerService {
    private PiwikTracker tracker;
    private String userID;

    public void init() {
        this.tracker = new PiwikTracker("https://www.analytics.madfix.me/piwik.php");
        userID = CloudNet.getInstance().getConfig().getConfig().getString("cloudnet-statistics.uuid");

    }
    public void onBootstrap(Module module, long diffrent){
        if (CloudNet.getInstance().getConfig().getConfig().getBoolean("cloudnet-statistics.enabled")) {
            String webhost = CloudNet.getInstance().getWebServer().getAddress();
            int webport = CloudNet.getInstance().getWebServer().getPort();
            String BASE_URL = "https://socket.madfix.me/";
            if (CloudNet.getInstance().getWebServer().isSsl()) {
                BASE_URL = "https://"+webhost+":"+webport;
            }else{
                BASE_URL = "http://"+webhost+":"+webport;
            }
            PiwikRequest request;
            try {
                int siteID = 4;
                request = new PiwikRequest(siteID, new URL(BASE_URL));
                request.setUserId(this.userID);
                request.setActionName("Load");
                request.setPageCustomVariable(new CustomVariable("Module Version",module.getModuleConfig().getVersion()),1);
                request.setPageCustomVariable(new CustomVariable("Java Version ", System.getProperty("java.version")),2);
                request.setPageCustomVariable(new CustomVariable("System Type ",System.getProperty("os.name")),3);
                request.setPageCustomVariable(new CustomVariable("System Bit ",System.getProperty("os.arch")),4);
                request.setPageCustomVariable(new CustomVariable("Start Time ",String.valueOf(diffrent)),5);
                request.setPluginJava(true);
                getTracker().sendRequest(request);
            } catch (MalformedURLException e) {
                System.err.println("Tracking Service: URL Error");
            } catch (IOException e) {
            } finally {
                request = null;
            }
        }

    }

    public PiwikTracker getTracker() {
        return tracker;
    }
}
