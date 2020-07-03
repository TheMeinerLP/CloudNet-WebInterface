package me.madfix.cloudnet.webinterface;

import com.google.gson.Gson;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.CoreModule;
import me.madfix.cloudnet.webinterface.database.MobDatabase;
import me.madfix.cloudnet.webinterface.services.ConfigurationService;
import me.madfix.cloudnet.webinterface.services.DatabaseService;


public final class WebInterface extends CoreModule {

    private ConfigurationService configurationService;
    private DatabaseService databaseService;
    private MobDatabase mobDatabase;
    private final Gson gson = new Gson();

    @Override
    public void onLoad() {
        this.configurationService = new ConfigurationService();
        if (!this.configurationService.loadConfigurationFile()) {
            CloudNet.getLogger().severe("No configuration file was found with the name: interface.json.");
            CloudNet.getLogger().severe("Web interface will not start!");
            CloudNet.getLogger().severe("Please create your configuration file under X and follow the instructions on the website. ");
        }
        if (this.configurationService.getOptionalInterfaceConfiguration().isPresent()) {
            this.databaseService = new DatabaseService(this);
        }
    }

    @Override
    public void onBootstrap() {
        if (this.configurationService.getOptionalInterfaceConfiguration().isPresent()) {
            try {
                this.mobDatabase = new MobDatabase(
                        this.getCloud().getDatabaseManager().getDatabase("cloud_internal_cfg"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onShutdown() {
    }

    public MobDatabase getMobDatabase() {
        return mobDatabase;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public Gson getGson() {
        return gson;
    }
}
