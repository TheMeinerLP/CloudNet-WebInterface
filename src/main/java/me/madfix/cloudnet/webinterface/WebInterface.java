package me.madfix.cloudnet.webinterface;

import com.google.gson.Gson;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.CoreModule;
import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import me.madfix.cloudnet.webinterface.logging.WebInterfaceLogger;
import me.madfix.cloudnet.webinterface.services.CloudNetService;
import me.madfix.cloudnet.webinterface.services.ConfigurationService;
import me.madfix.cloudnet.webinterface.services.DatabaseService;


public final class WebInterface extends CoreModule {

    private ConfigurationService configurationService;
    private DatabaseService databaseService;
    private CloudNetService cloudNetService;

    private final Gson gson = new Gson();
    private WebInterfaceLogger logger;



    @Override
    public void onLoad() {
        Sentry.init("https://08a4da2c621c4b8f9f16f345d829825b@o419044.ingest.sentry.io/5327070");
        Sentry.getContext().setUser(new UserBuilder()
                .setIpAddress(CloudNet.getInstance().getConfig().getConfig().getString("server.hostaddress"))
                .setId(CloudNet.getInstance().getConfig().getConfig().getString("cloudnet-statistics.uuid"))
                .build());
        this.logger = new WebInterfaceLogger();
        this.configurationService = new ConfigurationService(this);
        if (!this.configurationService.loadConfigurationFile()) {
            this.logger.severe("[100] No configuration file was found with the name: interface.json.");
            this.logger.severe("[100] Web interface will not start!");
            this.logger.severe("[100] Please create your configuration file under X and follow the instructions on the website. ");
        }
        if (this.configurationService.getOptionalInterfaceConfiguration().isPresent()) {
            this.databaseService = new DatabaseService(this);
        }
    }

    @Override
    public void onBootstrap() {
        if (this.configurationService.getOptionalInterfaceConfiguration().isPresent()) {
            this.cloudNetService = new CloudNetService(this);
        }


    }

    @Override
    public void onShutdown() {
    }

    public WebInterfaceLogger getLogger() {
        return logger;
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

    public CloudNetService getCloudNetService() {
        return cloudNetService;
    }
}
