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
    private int siteID = 4;
    private String BASE_URL = "https://socket.madfix.me/";
    private PiwikRequest request;
    private String userID;

    public void init() {
        this.tracker = new PiwikTracker("https://www.analytics.madfix.me/piwik.php");
        userID = CloudNet.getInstance().getConfig().getConfig().getString("cloudnet-statistics.uuid");

    }
    public void onLoad(){
        try {
            this.request = new PiwikRequest(this.siteID, new URL(this.BASE_URL));
            this.request.setActionName("onload");
            this.request.setPluginJava(true);
            getTracker().sendRequest(this.request);
        } catch (MalformedURLException e) {
            System.err.println("Tracking Service: URL Error");
        } catch (IOException e) {
        } finally {
            this.request = null;
        }
    }
    public void deleteUser() {
        try {
            this.request = new PiwikRequest(this.siteID, new URL(this.BASE_URL));
            this.request.setUserId(this.userID);
            this.request.setEventCategory("UserAPI");
            this.request.setEventName("delete");
            this.request.setEventAction("/cloudnet/api/v2/userapi");
            this.request.setPluginJava(true);
            getTracker().sendRequest(this.request);
        } catch (MalformedURLException e) {
            System.err.println("Tracking Service: URL Error");
        } catch (IOException e) {
        } finally {
            this.request = null;
        }
    }
    public void addUser() {
        try {
            this.request = new PiwikRequest(this.siteID, new URL(this.BASE_URL));
            this.request.setUserId(this.userID);
            this.request.setEventCategory("UserAPI");
            this.request.setEventName("add");
            this.request.setEventAction("/cloudnet/api/v2/userapi");
            this.request.setPluginJava(true);
            getTracker().sendRequest(this.request);
        } catch (MalformedURLException e) {
            System.err.println("Tracking Service: URL Error");
        } catch (IOException e) {
        } finally {
            this.request = null;
        }
    }
    public void updateUserPassword() {
        try {
            this.request = new PiwikRequest(this.siteID, new URL(this.BASE_URL));
            this.request.setUserId(this.userID);
            this.request.setEventCategory("UserAPI");
            this.request.setEventName("resetpassword");
            this.request.setEventAction("/cloudnet/api/v2/userapi");
            this.request.setPluginJava(true);
            getTracker().sendRequest(this.request);
        } catch (MalformedURLException e) {
            System.err.println("Tracking Service: URL Error");
        } catch (IOException e) {
        } finally {
            this.request = null;
        }
    }
    public void updateUser() {
        try {
            this.request = new PiwikRequest(this.siteID, new URL(this.BASE_URL));
            this.request.setUserId(this.userID);
            this.request.setEventCategory("UserAPI");
            this.request.setEventName("save");
            this.request.setEventAction("/cloudnet/api/v2/userapi");
            this.request.setPluginJava(true);
            getTracker().sendRequest(this.request);
        } catch (MalformedURLException e) {
            System.err.println("Tracking Service: URL Error");
        } catch (IOException e) {
        } finally {
            this.request = null;
        }
    }
    public void getServerScreen() {
        try {
            this.request = new PiwikRequest(this.siteID, new URL(this.BASE_URL));
            this.request.setUserId(this.userID);
            this.request.setEventCategory("ServerAPI");
            this.request.setEventName("screen");
            this.request.setEventAction("/cloudnet/api/v2/servergroup");
            this.request.setPluginJava(true);
            getTracker().sendRequest(this.request);
        } catch (MalformedURLException e) {
            System.err.println("Tracking Service: URL Error");
        } catch (IOException e) {
        } finally {
            this.request = null;
        }
    }
    public void getServerGroups(int size) {
        try {
            this.request = new PiwikRequest(this.siteID, new URL(this.BASE_URL));
            this.request.setUserId(this.userID);
            this.request.setEventCategory("ServerAPI");
            this.request.setEventName("groups");
            this.request.setEventAction("/cloudnet/api/v2/servergroup");
            this.request.setEventValue(size);
            this.request.setPluginJava(true);
            getTracker().sendRequest(this.request);
        } catch (MalformedURLException e) {
            System.err.println("Tracking Service: URL Error");
        } catch (IOException e) {
        } finally {
            this.request = null;
        }
    }
    public void getUsers(int size) {
        try {
            this.request = new PiwikRequest(this.siteID, new URL(this.BASE_URL));
            this.request.setUserId(this.userID);
            this.request.setEventCategory("UserAPI");
            this.request.setEventName("users");
            this.request.setEventAction("/cloudnet/api/v2/userapi");
            this.request.setEventValue(size);
            this.request.setPluginJava(true);
            getTracker().sendRequest(this.request);
        } catch (MalformedURLException e) {
            System.err.println("Tracking Service: URL Error");
        } catch (IOException e) {
        } finally {
            this.request = null;
        }
    }
    public void getWrappers(int size) {
        try {
            this.request = new PiwikRequest(this.siteID, new URL(this.BASE_URL));
            this.request.setUserId(this.userID);
            this.request.setEventCategory("Wrapper");
            this.request.setEventName("wrappers");
            this.request.setEventAction("/cloudnet/api/v2/wrapper");
            this.request.setEventValue(size);
            this.request.setPluginJava(true);
            getTracker().sendRequest(this.request);
        } catch (MalformedURLException e) {
            System.err.println("Tracking Service: URL Error");
        } catch (IOException e) {
        } finally {
            this.request = null;
        }
    }
    public void onBootstrap(Module module, long diffrent){
        String webhost = CloudNet.getInstance().getWebServer().getAddress();
        int webport = CloudNet.getInstance().getWebServer().getPort();
        if (CloudNet.getInstance().getWebServer().isSsl()) {
            this.BASE_URL = "https://"+webhost+":"+webport;
        }else{
            this.BASE_URL = "http://"+webhost+":"+webport;
        }
        try {
            this.request = new PiwikRequest(this.siteID, new URL(this.BASE_URL));
            this.request.setUserId(this.userID);
            this.request.setActionName("onBootstrap");
            this.request.setPageCustomVariable(new CustomVariable(module.getModuleConfig().getVersion(),""),1);
            this.request.setPageCustomVariable(new CustomVariable("Java Version ", System.getProperty("java.version")),2);
            this.request.setPageCustomVariable(new CustomVariable("System Type ",System.getProperty("os.name")),3);
            this.request.setPageCustomVariable(new CustomVariable("System Bit ",System.getProperty("os.arch")),4);
            this.request.setPageCustomVariable(new CustomVariable("Start Time ",String.valueOf(diffrent)),5);
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
