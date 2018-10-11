package cloud.waldiekiste.java.projekte.cloudnet.webinterface.services;

import de.dytanic.cloudnet.modules.Module;
import org.piwik.java.tracking.CustomVariable;
import org.piwik.java.tracking.PiwikRequest;
import org.piwik.java.tracking.PiwikTracker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TrackerService {
    private PiwikTracker tracker;
    private int siteID = 2;
    private String BASE_URL = "https://socket.madfix.me/";
    private PiwikRequest request;

    public void init(){
        this.tracker = new PiwikTracker("https://www.analytics.madfix.me/piwik.php");
    }
    public void onLoad(){
        try {
            this.request = new PiwikRequest(this.siteID, new URL(this.BASE_URL));
            this.request.setActionName("onLoad");
            this.request.setPluginJava(true);
            getTracker().sendRequest(this.request);
        } catch (MalformedURLException e) {
            System.err.println("Tracking Service: URL Error");
        } catch (IOException e) {
        } finally {
            this.request = null;
        }
    }
    public void onBootstrap(Module module){
        try {
            this.request = new PiwikRequest(2, new URL("https://socket.madfix.me/"));
            this.request.setActionName("onBootstrap");
            this.request.setPageCustomVariable(new CustomVariable("Module_Version",module.getModuleConfig().getVersion()),1);
            this.request.setPageCustomVariable(new CustomVariable("java.version",System.getProperty("java.version")),2);
            this.request.setPageCustomVariable(new CustomVariable("os.name",System.getProperty("os.name")),3);
            this.request.setPageCustomVariable(new CustomVariable("os.arch ",System.getProperty("os.arch")),4);
            this.request.setPluginJava(true);
            getTracker().sendRequest(this.request);
        } catch (MalformedURLException e) {
            System.err.println("Tracking Service: URL Error");
        } catch (IOException e) {
        } finally {
            this.request = null;
        }
    }

    public PiwikTracker getTracker() {
        return tracker;
    }
}
